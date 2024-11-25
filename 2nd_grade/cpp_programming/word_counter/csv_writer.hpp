#ifndef csv_writer_hpp
#define csv_writer_hpp


#include <stdio.h>
#include <iostream>
#include <fstream>
#include <map>

#include "text_processing.hpp"


class CsvWriter {
private:
    std::string output_file_name;
    
public:
    CsvWriter(std::string output_file);
    void write_to_csv(const WordStat& stat);
};

#endif /* csv_writer_hpp */

