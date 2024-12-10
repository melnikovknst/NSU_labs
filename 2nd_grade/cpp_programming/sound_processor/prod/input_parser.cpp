#include "input_parser.hpp"


InputParser::InputParser(int argc, char* argv[]) : argc_(argc), argv_(argv), help_showed(false) {}

bool InputParser::help_required() const { return help_showed; }
const std::string& InputParser::get_config_file_path() const { return config_file_; }
const std::string& InputParser::get_output_file_path() const { return output_file_; }
const std::vector<std::string>& InputParser::get_input_files() const { return input_files_; }



const std::vector<std::unique_ptr<Converter>>& InputParser::get_commands_list() const { return commands_list_; }

bool InputParser::parse() {
    if (argc_ < 2) {
        std::cerr << "Not enough arguments entered. Use -h flag for help." << std::endl;
        return false;
    }

    if (strcmp(argv_[1], "-h") == 0) {
        std::cout << InputParser::get_help_message() << std::endl;
        help_showed = true;
        return true;
    }
    else if (strcmp(argv_[1], "-c") == 0) {
        if (argc_ < 5) {
            std::cerr << "Not enough for -c flag. Use -h flag for help." << std::endl;
            return false;
        }

        config_file_ = argv_[2];
        output_file_ = argv_[3];

        for (int i = 4; i < argc_; i++)
            input_files_.push_back(argv_[i]);
        
        try {
            return parse_config_file();
        }
        catch (const ConfigParseError& e){
            std::cerr << "Configuration Error: " << e.what() << std::endl;
            return false;
        }
    }
    else {
        std::cerr << "Unknown arguments, use -h flag for help." << std::endl;
        return false;
    }
}


bool InputParser::parse_config_file() {
    std::ifstream config(config_file_);
    if (!config.is_open())
        throw FileReadError("Could not open the configuration file: " + config_file_);

    std::string line;
    while (std::getline(config, line)) {
        if (line.empty() || line[0] == '#')
            continue;

        std::istringstream iss(line);
        
        Command cmd;
        iss >> cmd.name;
        
        std::string arg;
        while (iss >> arg) {
            if (arg[0] == '$')
                arg = input_files_[std::stoi(arg.substr(1)) - 1];
            cmd.args.push_back(arg);
        }

        process_command(cmd);
    }
    config.close();
    return true;
}


void InputParser::process_command(const Command& cmd) {
    try {
        if (cmd.name == "mute" && cmd.args.size() == 2) {
            MuteConverterFactory factory;
            auto converter = factory.create_converter(cmd.args);
            commands_list_.push_back(std::move(converter));
        }
        else if (cmd.name == "mix" && cmd.args.size() >= 2) {
            MixConverterFactory factory;
            auto converter = factory.create_converter(cmd.args);
            commands_list_.push_back(std::move(converter));
        }
        else if (cmd.name == "LowPassFilter" && cmd.args.size() == 3) {
            LowPassFilterConverterFactory factory;
            auto converter = factory.create_converter(cmd.args);
            commands_list_.push_back(std::move(converter));
        }
        else
            throw std::invalid_argument("Unknown or malformed command: " + cmd.name);
    }
    catch (const std::exception& e) {
        std::cerr << "Error processing command: " << e.what() << std::endl;
    }
}


std::string InputParser::get_help_message() {
    std::ostringstream oss;
//    Usage
    oss << "Usage: ./sound_processor [-h] [-c config.txt output.wav input1.wav [input2.wav …]]\n";
    oss << "Flags:\n";
    oss << "  -h                  Show this help message and exit\n";
    oss << "  -c config.txt       Specify the configuration file, output file, and input WAV files\n\n";
//    Convertors description
    oss << "Converters:\n";
//    mute
    oss << "  mute <start> <end> - Mutes audio from start to end time (in s).\n";
    oss << "      Parameters:\n";
    oss << "        start - Start time in seconds.\n";
    oss << "        end   - End time in seconds.\n";
//    mix
    oss << "  mix <insert_position> <samples...> - Mixes additional samples at the specified position.\n";
    oss << "      Parameters:\n";
    oss << "        insert_position - Position in seconds where mixing starts.\n";
    oss << "        samples         - Additional samples to mix at insert_position.\n";
    //    low Pass Filter
    oss << "  lowPassFilter <start> <end> <cutoff frequency> - applies a simple low-pass filter to an audio signal. A low-pass filter removes high-frequency components while allowing low-frequency components to pass through, effectively smoothing out rapid changes in the signal.\n";
    oss << "      Parameters:\n";
    oss << "        start - Start time in seconds.\n";
    oss << "        end   - End time in seconds.\n";
    oss << "        cutoff frequency   - Defines the maximum frequency allowed to pass in Hz.\n";

    return oss.str();
}
