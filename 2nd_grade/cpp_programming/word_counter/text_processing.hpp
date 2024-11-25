#ifndef text_processing_hpp
#define text_processing_hpp


#include <iostream>
#include <stdio.h>
#include <fstream>
#include <regex>
#include <list>
#include <map>


class WordStat {
private:
    std::list<std::string> readed_strings;
    std::map<std::string, int> words;
    std::string input_file_name;
    int word_counter = 0;
    void read_from_txt();
    void word_processing();
    
public:
    WordStat(std::string input_file_name);
    void text_processing();
    const std::map<std::string, int>& get_words() const;
    int get_word_counter() const;
};

#endif /* text_processing_hpp */
