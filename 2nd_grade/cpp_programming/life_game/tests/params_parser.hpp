#ifndef params_parser_hpp
#define params_parser_hpp

#include <iostream>

//  A tool for getting game parameters from command line arguments.
class Parser {
private:
    std::string output_file;
    std::string input_file;
    int iterations;
    bool mode;  //  True - offline mode/ False - online mode
    void get_help();
   
public:
    Parser();
    std::string get_output_file() const;
    std::string get_input_file() const;
    int get_iterations() const;
    bool is_offline_mode() const;
    bool parse(int argc, char* argv[]);
};

#endif /* params_parser_hpp */
