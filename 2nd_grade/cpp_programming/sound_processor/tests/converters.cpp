#include "converters.hpp"


MuteConverter::MuteConverter(int start_time, int end_time) : start_time_(start_time), end_time_(end_time)
{
    if (start_time < 0 || end_time < 0)
        throw std::invalid_argument("Start time and end time must be non-negative.");
    if (start_time >= end_time)
        throw std::invalid_argument("Start time must be less than end time.");
}


MixConverter::MixConverter(const std::string& additional_stream, int insert_position)
: additional_stream_(additional_stream), insert_position_(insert_position) {
    if (insert_position < 0)
        throw std::invalid_argument("Insert position must be non-negative.");

    WAVFile wav_file(additional_stream);
    if (!wav_file.read())
        throw std::runtime_error("Error: Could not read WAV file: " + additional_stream);

    mix_samples_ = wav_file.get_samples();

    if (mix_samples_.empty())
        throw std::runtime_error("Error: WAV file contains no samples: " + additional_stream);
}


LowPassFilterConverter::LowPassFilterConverter(int cutoff_frequency, size_t start_time, size_t end_time)
    : cutoff_frequency_(cutoff_frequency), start_time_(start_time), end_time_(end_time) {}



void MuteConverter::apply(std::vector<short>& samples) {
    int start_sample = static_cast<int>(start_time_ * SAMPLE_RATE);
    int end_sample = static_cast<int>(end_time_ * SAMPLE_RATE);

    for (size_t i = start_sample; i < end_sample && i < samples.size(); ++i)
        samples[i] = 0;
}


void MixConverter::apply(std::vector<short>& samples) {
    int sample_position = insert_position_ * SAMPLE_RATE;

    if (mix_samples_.empty())
        throw std::runtime_error("Error: The mix samples are empty.");

    if (sample_position < 0 || sample_position >= samples.size())
        throw std::runtime_error("Error: Invalid insert position for mix command.");

    for (size_t i = 0; i < mix_samples_.size() && (i + sample_position) < samples.size(); ++i)
        samples[i + sample_position] = (samples[sample_position + i] + mix_samples_[i]) / 2;
}


void LowPassFilterConverter::apply(std::vector<short>& samples) {
    if (samples.empty() || cutoff_frequency_ <= 0 || SAMPLE_RATE <= 0) {
        return;
    }

    // Расчёт коэффициента фильтра
    float rc = 1.0f / (2.0f * M_PI * cutoff_frequency_); // Постоянная времени RC-фильтра
    float dt = 1.0f / SAMPLE_RATE;                     // Период дискретизации
    float alpha = dt / (rc + dt);                       // Коэффициент фильтрации

    // Определяем диапазон семплов, на которых применяется фильтр
    size_t start_sample = start_time_ * SAMPLE_RATE;
    size_t end_sample = end_time_ * SAMPLE_RATE;

    if (start_sample >= samples.size()) {
        return; // Время начала за пределами аудио
    }

    end_sample = std::min(end_sample, samples.size()); // Корректируем конечное время, если оно выходит за пределы

    // Применение фильтра
    float previous_output = 0.0f;
    for (size_t i = start_sample; i < end_sample; ++i) {
        float input = static_cast<float>(samples[i]);
        float filtered = alpha * input + (1.0f - alpha) * previous_output; // Формула фильтра
        samples[i] = static_cast<short>(filtered); // Преобразование обратно в short
        previous_output = filtered; // Запоминаем выход для следующего шага
    }
}


std::unique_ptr<Converter> MuteConverterFactory::create_converter(const std::vector<std::string>& args) const {
    int start = std::stoi(args[0]);
    int end = std::stoi(args[1]);
    return std::make_unique<MuteConverter>(start, end);
}


std::unique_ptr<Converter> MixConverterFactory::create_converter(const std::vector<std::string>& args) const {
    if (args.size() < 2)
        throw std::invalid_argument("MixConverter requires at least 2 arguments: file_name and insert_position.");

    std::string additional_stream = args[0];
    int insert_position;
    try {
        insert_position = std::stoi(args[1]);
    }
    catch (const std::exception& e) {
        throw std::invalid_argument("Invalid insert position for mix command: " + std::string(e.what()));
    }

    return std::make_unique<MixConverter>(additional_stream, insert_position);
}


std::unique_ptr<Converter> LowPassFilterConverterFactory::create_converter(
    const std::vector<std::string>& args) const {
    if (args.size() < 3) {
        throw std::invalid_argument("LowPassFilterConverter requires arguments: cutoff_frequency, start_time, end_time");
    }

    size_t start_time = std::stoul(args[0]);
    size_t end_time = std::stoul(args[1]);
    float cutoff_frequency = std::stof(args[2]);

    return std::make_unique<LowPassFilterConverter>(cutoff_frequency, start_time, end_time);
}
