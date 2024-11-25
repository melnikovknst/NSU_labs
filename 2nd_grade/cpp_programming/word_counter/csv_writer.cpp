#include "csv_writer.hpp"

#include <map>

using namespace std;


CsvWriter::CsvWriter(string output_file) {
    output_file_name = output_file;
}


void CsvWriter::write_to_csv(const WordStat& stat) {
    const map<std::string, int> words  = stat.get_words();
    int counter = stat.get_word_counter();
//---    Sorting map by value in descending order
    multimap<int, const string*> sorted_words;
    for (auto &elem : words) {
        sorted_words.insert({-1 * elem.second, &(elem.first)});
    }
//---
    
    ofstream output;
    output.open(output_file_name, ios::out);
    
    output << "Word,Repetitions,%\n";
    for (auto &elem : sorted_words) {
        output << *(elem.second) << "," << -1 * elem.first << ","
        << (float)(-1 * elem.first) / (float)counter * 100 << "\n";
    }
    
    output.close();
}
