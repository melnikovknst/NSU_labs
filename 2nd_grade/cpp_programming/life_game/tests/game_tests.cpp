#include <gtest/gtest.h>

#include "params_parser.hpp"
#include "game.hpp"


TEST(ParamsParserTest, ParseFlags)  {
    const char* argv[] = {"life", "-f", "glider.txt", "-i", "32", "-m", "offline", "-o", "out.txt"};
    Parser parser;
    
    EXPECT_TRUE(parser.parse(9, const_cast<char**>(argv)));
    EXPECT_EQ(parser.get_input_file(), "glider.txt");
    EXPECT_EQ(parser.get_iterations(), 32);
    EXPECT_TRUE(parser.is_offline_mode());
    EXPECT_EQ(parser.get_output_file(), "out.txt");
}


TEST(FStreamOperatorTest, IOStream) {
    Game game;
    const char* argv[] = {"life", "-f", "glider.txt", "-i", "32", "-m", "offline", "-o", "out.txt"};
    Parser parser;
    parser.parse(9, const_cast<char**>(argv));
    
    EXPECT_TRUE(game.game_prep(parser));
    EXPECT_EQ(game.get_field()[0].size(), 10);
    EXPECT_EQ(game.get_rule(), "B3/S23");
    EXPECT_EQ(game.get_universe_name(), "Glider");
    
    Game game2;
    const char* argv2[] = {"life", "-f", "out.txt", "-m", "online"};
    Parser parser2;
    parser2.parse(5, const_cast<char**>(argv2));
    
    EXPECT_TRUE(game2.game_prep(parser));
    EXPECT_EQ(game2.get_field()[0].size(), 10);
    EXPECT_EQ(game2.get_rule(), "B3/S23");
    EXPECT_EQ(game2.get_universe_name(), "Glider");
}


TEST(GameTest, CycledField) {
    Game game;
    const char* argv[] = {"life", "-f", "cycled.txt", "-m", "online"};
    Parser parser;
    parser.parse(5, const_cast<char**>(argv));
    
    EXPECT_TRUE(game.game_prep(parser));
    std::vector<std::vector<Cell>> start_field = game.get_field();
    game.run_iterations(2);
    for (int i = 0; i < game.get_field()[0].size(); i ++)
        for (int j = 0; j < game.get_field()[0].size(); j++)
            EXPECT_EQ(start_field[i][j].is_alive(), game.get_field()[i][j].is_alive());
}


TEST(GameTest, StaticField) {
    Game game;
    const char* argv[] = {"life", "-f", "static.txt", "-m", "online"};
    Parser parser;
    parser.parse(5, const_cast<char**>(argv));
    EXPECT_TRUE(game.game_prep(parser));
    std::vector<std::vector<Cell>> start_field = game.get_field();
    
    for (int k = 1; k < 3; k++) {
        game.run_iterations(k);
        for (int i = 0; i < game.get_field()[0].size(); i ++)
            for (int j = 0; j < game.get_field()[0].size(); j++)
                EXPECT_EQ(start_field[i][j].is_alive(), game.get_field()[i][j].is_alive());
    }
}


TEST(GameTest, EmptyField) {
    Game game;
    const char* argv[] = {"life", "-f", "empty_field.txt", "-m", "online"};
    Parser parser;
    parser.parse(5, const_cast<char**>(argv));
    EXPECT_TRUE(game.game_prep(parser));

    game.run_iterations(12);
    for (int i = 0; i < game.get_field()[0].size(); i ++)
        for (int j = 0; j < game.get_field()[0].size(); j++)
            EXPECT_FALSE(game.get_field()[i][j].is_alive());
}


TEST(GameTest, IsValidRule) {
    Game game;
    EXPECT_TRUE(game.set_rules("3/S23"));
    EXPECT_TRUE(game.set_rules("36/S128"));
    EXPECT_FALSE(game.set_rules("/3S23"));
    EXPECT_FALSE(game.set_rules("9/S23"));
    EXPECT_FALSE(game.set_rules("3/S"));
}


TEST(GameTest, CountAliveNeighbors) {
    Game game;
    const char* argv[] = {"life", "-f", "glider.txt",  "-m", "online"};
    Parser parser;
    parser.parse(5, const_cast<char**>(argv));
    
    EXPECT_TRUE(game.game_prep(parser));

    EXPECT_EQ(game.count_alive_neighbors(0, 2), 1);
    EXPECT_EQ(game.count_alive_neighbors(1, 0), 1);
    EXPECT_EQ(game.count_alive_neighbors(1, 2), 3);
    EXPECT_EQ(game.count_alive_neighbors(2, 1), 3);
    EXPECT_EQ(game.count_alive_neighbors(2, 2), 2);
}


TEST(GameTest, BorderNeighbors) {
    Game game;
    const char* argv[] = {"life", "-f", "border_glider.txt",  "-m", "online"};
    Parser parser;
    parser.parse(5, const_cast<char**>(argv));
    
    EXPECT_TRUE(game.game_prep(parser));

    EXPECT_EQ(game.count_alive_neighbors(0, 2), 4);
    EXPECT_EQ(game.count_alive_neighbors(1, 0), 4);
    EXPECT_EQ(game.count_alive_neighbors(1, 2), 4);
    EXPECT_EQ(game.count_alive_neighbors(2, 1), 4);
    EXPECT_EQ(game.count_alive_neighbors(2, 2), 4);
}


