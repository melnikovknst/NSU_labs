#include "sound_processor.hpp"


SoundProcessor::SoundProcessor(const InputParser& parser) : parser_(parser) {}

bool SoundProcessor::run() {
    try {
        if (parser_.get_input_files().empty())
            throw SoundProcessorError("No input files provided");

        WAVFile inputFile(parser_.get_input_files().front());
        if (!inputFile.read())
            throw FileReadError("Could not read the input WAV file");

        std::vector<short> samples = inputFile.get_samples();

        for (const auto& converter : parser_.get_commands_list())
            converter->apply(samples);

        WAVFile outputFile(parser_.get_output_file_path(), samples, inputFile.get_sample_rate());

        if (!outputFile.write())
            throw FileReadError("Could not write to the output WAV file");

        std::cout << "Processing complete. Output saved to " << parser_.get_output_file_path() << std::endl;
        return true;
    }
    catch (const SoundProcessorError& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return false;
    }
}
