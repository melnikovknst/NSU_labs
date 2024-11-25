#ifndef game_hpp
#define game_hpp

#include <fstream>
#include <iostream>
#include <vector>
#include <sstream>

#include "params_parser.hpp"

class Cell {
private:
    bool state;

public:
    Cell();
    void set_state(bool state);
    bool is_alive() const;
};

class Game {
private:
    int field_size;
    int current_iteration;

    std::vector<std::vector<Cell>> game_field;
    std::string universe_name;
    
    std::string birth_rule;
    std::string survival_rule;


    friend std::ostream& operator<<(std::ostream& os, const Game& game);
    friend std::istream& operator>>(std::istream& is, Game& game);

public:
    Game();
    ~Game();

    int start(int argc, char* argv[]);
    bool game_prep(const Parser& parser);
    bool set_rules(const std::string& rule);
    bool execute_command(const std::string& command);

    void run_online();
    void run_iterations(int n);
    void display() const;
    void generate_random_universe();
    void run_iteration();
    int count_alive_neighbors(int x, int y) const;
    
    // for tests
    std::vector<std::vector<Cell>>& get_field();
    const std::string& get_universe_name() const;
    std::string get_rule();
};

#endif /* game_hpp */
