#include <iostream>

#include "input_parser.hpp"
#include "sound_processor.hpp"

int main(int argc, char * argv[]) {
    InputParser parser(argc, argv);

    if (!parser.parse())
        return 1;

    if (parser.help_required())
        return 0;

    SoundProcessor processor(parser);
    
    if (!processor.run()) {
        std::cerr << "Error: Processing failed." << std::endl;
        return 1;
    }
    
    return 0;
}
