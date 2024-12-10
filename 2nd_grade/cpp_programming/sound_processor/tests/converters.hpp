#ifndef converters_hpp
#define converters_hpp

#include <stdio.h>
#include <algorithm>
#include <vector>
#include <string>
#include <memory>
#include <iostream>
#include <cmath>

#include "wav_processing.hpp"


#define SAMPLE_RATE 44100

class Converter {
public:
    virtual void apply(std::vector<short>& samples) = 0;
    virtual ~Converter() = default;
};


class MuteConverter : public Converter {
private:
    size_t start_time_;
    size_t end_time_;
    
public:
    MuteConverter(int start_time, int end_time);
    void apply(std::vector<short>& samples) override;
};


class MixConverter : public Converter {
private:
    int insert_position_;
    std::string additional_stream_;
    std::vector<short> mix_samples_;
    
public:
    MixConverter(const std::string& additional_stream, int insert_position);
    void apply(std::vector<short>& samples) override;
};


class LowPassFilterConverter : public Converter {
private:
    size_t start_time_;
    size_t end_time_;
    short cutoff_frequency_;

public:
    LowPassFilterConverter(int cutoff_frequency, size_t start_time, size_t end_time);
    void apply(std::vector<short>& samples) override;
};


class ConverterFactory {
public:
    virtual std::unique_ptr<Converter> create_converter(const std::vector<std::string>& args) const = 0;
        virtual ~ConverterFactory() = default;;
};


class MuteConverterFactory : public ConverterFactory {
public:
    std::unique_ptr<Converter> create_converter(const std::vector<std::string>& args) const override;
};


class MixConverterFactory : public ConverterFactory {
public:
    std::unique_ptr<Converter> create_converter(const std::vector<std::string>& args) const override;
};


class LowPassFilterConverterFactory : public ConverterFactory {
public:
    std::unique_ptr<Converter> create_converter(const std::vector<std::string>& args) const override;
};


#endif /* converters_hpp */
