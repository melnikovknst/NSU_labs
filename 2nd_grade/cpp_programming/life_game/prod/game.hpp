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


class Universe {
private:
    int field_size;
    std::vector<std::vector<Cell>> game_field;
    std::string universe_name;
    
    std::string birth_rule;
    std::string survival_rule;
public:
    Universe();
    void generate_random_universe();
    bool set_rules(const std::string& rule);
    int& get_field_size();
    std::string& get_survival_rule();
    std::string& get_birth_rule();
    std::string& get_name();
    std::vector<std::vector<Cell>>& get_field();
};


class Game {
private:
    int current_iteration;
    Universe& universe;
    friend std::ostream& operator<<(std::ostream& os, const Game& game);
    friend std::istream& operator>>(std::istream& is, Game& game);

public:
    Game(Universe& univ);
    ~Game();
    bool execute_command(const std::string& command);
    int count_alive_neighbors(int x, int y) const;
    bool game_prep(const Parser& parser);
    int start(int argc, char* argv[]);
    void run_iterations(int n);
    void run_iteration();
    void display() const;
    
    void run_online();
};

#endif /* game_hpp */
