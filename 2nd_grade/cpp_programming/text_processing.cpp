#include "text_processing.hpp"


using namespace std;


WordStat::WordStat(std::string input_file) {
    input_file_name = input_file;
}


void WordStat::text_processing() {
    read_from_txt();
    word_processing();
}


void WordStat::read_from_txt() {
    ifstream input;
    input.open(input_file_name, ios::in);
    string str;
    
    while (getline(input, str))
        readed_strings.push_back(str);
    
    input.close();
}


void WordStat::word_processing() {
    regex word_mask("[^\\W_]+");
    smatch word;
    
    for(string &str : readed_strings) {
        while(regex_search(str, word, word_mask)) {
            words[word[0]]++;
            str = word.suffix();
            word_counter++;
        }
    }
}


const std::map<std::string, int>& WordStat::get_words() const{
    return words;
}


int WordStat::get_word_counter() const {
    return word_counter;
}
