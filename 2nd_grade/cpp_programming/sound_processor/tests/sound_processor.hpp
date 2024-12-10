#ifndef sound_processor_hpp
#define sound_processor_hpp

#include <stdio.h>


#include <iostream>
#include <vector>
#include <memory>
#include "input_parser.hpp"
#include "wav_processing.hpp"

class SoundProcessor {
private:
    const InputParser& parser_;
    
public:
    SoundProcessor(const InputParser& parser);
    bool run();
};

#endif /* sound_processor_hpp */
