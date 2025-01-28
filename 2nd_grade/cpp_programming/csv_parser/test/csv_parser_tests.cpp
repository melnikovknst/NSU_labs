#include <gtest/gtest.h>
#include <fstream>

#include "tuple_print_op.hpp"
#include "csv_parser.hpp"


TEST(CSVParserTest, CorrectnessTest) {
    std::ifstream file("correct.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    int counter = 0;
    
    for (const auto& record : parser) {
        ASSERT_NO_THROW(std::cout << record << std::endl;);
        counter++;
    }
    
    ASSERT_EQ(counter, 10);
}


TEST(CSVParserTest, SkipLinesTest) {
    std::ifstream file("correct.csv");
    CSVParser<int, std::string, float> parser(file, 2);
    int counter = 0;
    
    for (const auto& record : parser){
        ASSERT_NO_THROW(std::cout << record << std::endl;);
        counter++;
    }
    
    ASSERT_EQ(counter, 8);
}


TEST(CSVParserTest, EscapedDataTest) {
    std::ifstream file("correct_3.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    int counter = 0;
    
    for (const auto& record : parser) {
        ASSERT_NO_THROW(std::cout << record << std::endl;);
        counter++;
    }
    
    ASSERT_EQ(counter, 10);
}


TEST(CSVParserTest, UserSeparatorsTest) {
    std::ifstream file("correct_2.csv");
    CSVParser<int, std::string, float> parser(file, 0, ';', '|', '\'');
    int counter = 0;
    
    for (const auto& record : parser){
        ASSERT_NO_THROW(std::cout << record << std::endl;);
        counter++;
    }
    
    ASSERT_EQ(counter, 10);
}


TEST(CSVParserTest, InvalidDTypeTest) {
    std::ifstream file("incorrect.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    
    for (const auto& record : parser)
        ASSERT_THROW(std::cout << record << std::endl;, CSVParseException);
}


TEST(CSVParserTest, InvalidFileTest) {
    std::ifstream file("hgvjs.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    
    for (const auto& record : parser)
        ASSERT_THROW(std::cout << record << std::endl;, CSVParseException);
}


TEST(CSVParserTest, EmptyFieldTest) {
    std::ifstream file("empty.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    
    for (const auto& record : parser)
        ASSERT_THROW(std::cout << record << std::endl;, CSVParseException);
}


TEST(CSVIteratorTest, BeginTest) {
    std::ifstream file("incorrect.csv");
    CSVParser<int, std::string, float> parser(file, 0);
    
    ASSERT_EQ(parser.begin().parser_->get_row(), 0);
}
