#ifndef input_parser_hpp
#define input_parser_hpp

#include <stdio.h>
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <memory> 

#include "exceptions.hpp"
#include "converters.hpp"

struct Command {
    std::string name;
    std::vector<std::string> args;
};

class InputParser {
private:
    int argc_;
    char** argv_;
    bool help_showed;
    std::string config_file_;
    std::string output_file_;
    std::vector<std::string> input_files_;

    std::vector<std::unique_ptr<Converter>> commands_list_;

    bool parse_config_file();
    void process_command(const Command& cmd);
    
public:
    InputParser(int argc, char* argv[]);

    bool help_required() const;
    const std::string& get_config_file_path() const;
    const std::string& get_output_file_path() const;
    const std::vector<std::string>& get_input_files() const;

    bool parse();

    const std::vector<std::unique_ptr<Converter>>& get_commands_list() const;
    static std::string get_help_message();
};
#endif /* input_parser_hpp */
