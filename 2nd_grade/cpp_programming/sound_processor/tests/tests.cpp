#include <gtest/gtest.h>

#include "input_parser.hpp"
#include "sound_processor.hpp"
#include "exceptions.hpp"


TEST(ParserTest, ParseArguments) {
    int argc = 6;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("-c"),
        const_cast<char*>("cfg1.txt"),
        const_cast<char*>("out.wav"),
        const_cast<char*>("severe_tire_damage.wav"),
        const_cast<char*>("funkorama.wav")
    };
    InputParser parser(argc, argv);
    
    std::vector<std::string> input_files;
    input_files.push_back(argv[4]);
    input_files.push_back(argv[5]);
    
    ASSERT_TRUE(parser.parse());
    EXPECT_EQ(parser.get_config_file(), "cfg1.txt");
    EXPECT_EQ(parser.get_output_file(), "out.wav");
    EXPECT_EQ(parser.get_input_files(), input_files);
    
}


TEST(ParserTest, HelpTest) {
    int argc = 2;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("-h")
    };
    InputParser parser(argc, argv);
    
    ASSERT_TRUE(parser.parse());
    ASSERT_TRUE(parser.help_required());
}


TEST(ParserTest, InvalidArg) {
    int argc = 2;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("iof")
    };
    InputParser parser(argc, (argv));

    ASSERT_FALSE(parser.parse());
}


TEST(ParserTest, MissingArgs) {
    int argc = 3;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("-c"),
        const_cast<char*>("out.wav")
    };
    InputParser parser(argc, (argv));

    ASSERT_FALSE(parser.parse());
}


TEST(ParserTest, ParseConfig) {
    int argc = 6;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("-c"),
        const_cast<char*>("cfg1.txt"),
        const_cast<char*>("out.wav"),
        const_cast<char*>("severe_tire_damage.wav"),
        const_cast<char*>("funkorama.wav")
    };
    InputParser parser(argc, (argv));

    ASSERT_TRUE(parser.parse());
    ASSERT_EQ(parser.get_commands_list().size(), 4);
}


TEST(SoundProcessorTest, EmptyConfig) {
    int argc = 5;
    char* argv[] = {
        const_cast<char*>("sound_processor"),
        const_cast<char*>("-c"),
        const_cast<char*>("empty_cfg.txt"),
        const_cast<char*>("out.wav"),
        const_cast<char*>("severe_tire_damage.wav")
    };
    InputParser parser(argc, argv);
    
    ASSERT_TRUE(parser.parse());
    ASSERT_EQ(parser.get_commands_list().size(), 0);

    SoundProcessor processor(parser);
    ASSERT_NO_THROW(processor.run());
}
