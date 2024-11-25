#include "text_processing.hpp"
#include "csv_writer.hpp"

using namespace std;

int main(int argc, const char * argv[]) {
    WordStat word_stat(argv[1]);
    CsvWriter writer(argv[2]);
    
    word_stat.text_processing();
    writer.write_to_csv(word_stat);
    
    return 0;
}
