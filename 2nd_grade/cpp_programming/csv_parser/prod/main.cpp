#include <iostream>
#include <fstream>

#include "tuple_print_op.hpp"
#include "csv_parser.hpp"

int main(int argc, char * argv[]) {
    if (argc != 3 || ((!atoi(argv[2])) & strcmp(argv[2], "0")) || atoi(argv[2]) < 0) {
        std::cerr << "Invalid arguments. Use './csv_parser *filename*.csv n'; n - skip first lines count" << argv[1] << std::endl;
        return 1;
    }
    
    std::ifstream file(argv[1]);

    if (!file.is_open()) {
        std::cerr << "Could not open file: " << argv[1] << std::endl;
        return 1;
    }

    try {
        CSVParser<int, std::string, float> parser(file, atoi(argv[2]));

        for (const auto& record : parser)
            std::cout << record << std::endl;
        
    } catch (const CSVParseException& e) {
        std::cerr << "Parsing error in line " << e.row()
                  << ", col " << e.col() << ": " << e.what() << std::endl;
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }


    return 0;
}
