#include "params_parser.hpp"


Parser::Parser() : iterations(0), mode(false) {}
bool Parser::is_offline_mode() const { return mode; }
int Parser::get_iterations() const { return iterations; }
std::string Parser::get_input_file() const { return input_file; }
std::string Parser::get_output_file() const { return output_file; }


bool Parser::parse(int argc, char* argv[]) {
    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-f") == 0 || strcmp(argv[i], "--file") == 0) {
            if (i + 1 < argc)
                input_file = std::string(argv[++i]);
            else {
                std::cerr << "Error: No input filename provided. Use -h or --help for usage." << std::endl;
                return false;
            }
        }
        else if (strcmp(argv[i], "-o") == 0 || strcmp(argv[i], "--output") == 0) {
            if (i + 1 < argc)
                output_file = std::string(argv[++i]);
            else {
                std::cerr << "Error: No output filename provided. Use -h or --help for usage." << std::endl;
                return false;
            }
        }
        else if (strcmp(argv[i], "-i") == 0 || strcmp(argv[i], "--iterations") == 0) {
            if (i + 1 < argc) {
                try {
                    iterations = std::atoi(argv[++i]);
                }
                catch (const std::invalid_argument& e) {
                    std::cerr << "Error: Invalid value for iterations. Use -h or --help for usage." << std::endl;
                    return false;
                }
            }
            else {
                std::cerr << "Error: No value for iterations provided. Use -h or --help for usage." << std::endl;
                return false;
            }
        }
        else if (strcmp(argv[i], "-m") == 0 || strcmp(argv[i], "--mode") == 0) {
            if (i + 1 < argc) {
                std::string input_mode = std::string(argv[++i]);
                if (input_mode == "offline")
                    mode = true;
                else if (input_mode == "online")
                    mode = false;
                else {
                    std::cerr << "Error: Invalid mode. Use 'offline' or 'online'. Use -h or --help for usage." << std::endl;
                    return false;
                }
            }
            else {
                std::cerr << "Error: No mode specified. Use -h or --help for usage." << std::endl;
                return false;
            }
        }
        else if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            get_help();
            return false;
        }
        else {
            std::cerr << "Error: Unknown option entered. Use -h or --help for usage." << std::endl;
            return false;
        }
    }

    
//    ??????
//    if (!mode && iterations == 0)
//        iterations = 1;

    return true;
}


void Parser::get_help() {
    std::cout << "-f  |  --file        FILENAME    Input file with universe inside.\n"
                 "-o  |  --output      FILENAME    Output file for saving universe.\n"
                 "-i  |  --iterations  NUMBER      Number of iterations to run.\n"
                 "-m  |  --mode        MODE        Mode of the game (online or offline).\n"
                 "-h  |  --help                    Displays this help menu and exit program." << std::endl;
}
