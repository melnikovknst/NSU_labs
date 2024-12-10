#ifndef wav_processing_hpp
#define wav_processing_hpp

#include <stdio.h>
#include <string>
#include <vector>
#include <fstream>
#include <iostream>
#include <stdexcept>

#include "exceptions.hpp"

class WAVFile {
private:
    std::string filepath_;
    std::vector<short> samples_;
    int sample_rate_;
    bool valid_format_;

    bool read_header(std::ifstream& file);
    bool write_header(std::ofstream& file);
    
public:
    WAVFile(const std::string& filepath);
    WAVFile(const std::string& filepath, const std::vector<short>& samples, int sample_rate = 44100);

    bool read();
    bool write();

    const std::vector<short>& get_samples() const;
    int get_sample_rate() const;
};


#endif /* wav_processing_hpp */
