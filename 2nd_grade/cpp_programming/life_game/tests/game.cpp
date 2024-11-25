#include <regex>

#include "game.hpp"

// for tests
std::vector<std::vector<Cell>>& Game::get_field() { return game_field; }
const std::string& Game::get_universe_name() const { return universe_name; }
std::string Game::get_rule() { return "B" + birth_rule + "/S" + survival_rule; }
//

Cell::Cell() : state(false){}
bool Cell::is_alive() const {return state;}
void Cell::set_state(bool state) {(*this).state = state;}


Game::Game() : field_size(0), current_iteration(0) {}
Game::~Game() = default;

int Game::start(int argc, char* argv[]) {
    Parser parser;
    if (!parser.parse(argc, argv))
        return 1;
    
    if (!(*this).game_prep(parser)) {
        std::cerr << "Error: Failed to start the game with the specified parameters." << std::endl;
        return 1;
    }

    if (parser.is_offline_mode()) {
        (*this).run_iterations(parser.get_iterations());
        std::ofstream out_file(parser.get_output_file());

        if (out_file.is_open()) {
            out_file << (*this);
            std::cout << "Offline mode completed: output saved to " << parser.get_output_file() << std::endl;
        }
        else {
            std::cerr << "Error: Could not open file " << parser.get_output_file() << std::endl;
            return 1;
        }
    }
    else
        (*this).run_online();
    
    return 0;
}

void Game::generate_random_universe() {
    for (std::size_t x = 0; x < field_size; x++)
        for (std::size_t y = 0; y < field_size; y++)
            game_field[x][y].set_state(rand() % 2 == 0);
}


bool Game::set_rules(const std::string& rule) {
    size_t slash_pos = rule.find('/');
    
    if (slash_pos == std::string::npos)
        return false;

    if (slash_pos == 0 || rule[slash_pos + 1] != 'S')
        return false;

    for (size_t i = 0; i < slash_pos; i++) {
        if (rule[i] < '0' || rule[i] > '8')
            return false;
        else if (birth_rule.find(rule[i]) == std::string::npos)
            birth_rule += rule[i];
    }
    

    if (slash_pos + 2 >= rule.size())
        return false;

    for (size_t i = slash_pos + 2; i < rule.size(); ++i) {
        if (rule[i] < '0' || rule[i] > '8')
            return false;
        else if (survival_rule.find(rule[i]) == std::string::npos)
            survival_rule += rule[i];
    }

    return true;
}


bool Game::game_prep(const Parser& parser) {
    if (parser.get_input_file() == "") {
        std::cout << "Warning: No input file provided. Generating random universe." << std::endl;
        generate_random_universe();
    }
    else {
        std::ifstream in_file(parser.get_input_file());
        if (!in_file.is_open()) {
            std::cerr << "Error: Failed to open file " << parser.get_input_file() << std::endl;
            return false;
        }
        
        in_file >> *this;
        if (!in_file) {
            std::cerr << "Error: Failed to load universe from input file." << std::endl;
            return false;
        }
    }

    if (parser.is_offline_mode() && parser.get_output_file() == "") {
        std::cerr << "Error: No output file specified in offline mode." << std::endl;
        return false;
    }

    if ((parser.get_iterations() <= 0) & parser.is_offline_mode()) {
        std::cerr << "Error: Incorrect number of iterations." << std::endl;
        return false;
    }

    return true;
}


int Game::count_alive_neighbors(int x, int y) const {
    int alive_neighbors = 0;
    for (int i = -1; i <= 1; i++)
        for (int j = -1; j <= 1; j++)
            if (!(i == 0 && j == 0)) {
                int nx = (x + i + field_size) % field_size;
                int ny = (y + j + field_size) % field_size;
                if (game_field[nx][ny].is_alive())
                    alive_neighbors++;
            }
        
    return alive_neighbors;
}


void Game::run_iteration() {
    std::vector<std::vector<bool>> state_matrix(field_size, std::vector<bool>(field_size, false));
    
    for (int x = 0; x < field_size; x++)
        for (int y = 0; y < field_size; y++) {
            int alive_neighbors = count_alive_neighbors(x, y);
            
            if (game_field[x][y].is_alive()) {
                state_matrix[x][y] = (survival_rule.find(std::to_string(alive_neighbors)) != std::string::npos);
            }
            else {
                state_matrix[x][y] = (birth_rule.find(std::to_string(alive_neighbors)) != std::string::npos);
            }
        }

    for (int x = 0; x < field_size; x++)
        for (int y = 0; y < field_size; y++)
            game_field[x][y].set_state(state_matrix[x][y]);

    current_iteration++;
}


void Game::display() const {
    std::cout << "Universe: " << universe_name << ", Rule: B" << birth_rule << "/S"
    << survival_rule << ", Iteration: " << current_iteration << std::endl;
    for (int x = 0; x < field_size; ++x) {
        for (int y = 0; y < field_size; ++y)
            std::cout << (game_field[x][y].is_alive() ? "■" : "□");
        std::cout << '\n';
    }
}


void Game::run_online() {
    display();
    std::string command;
    while (true) {
        std::cout << "Enter command (dump, tick, exit, help): ";
        std::getline(std::cin, command);
        if (!execute_command(command))
            break;
    }
}


bool Game::execute_command(const std::string& command) {
    std::istringstream iss(command);
    std::string cmd;
    iss >> cmd;

    if (cmd == "tick" || cmd == "t") {
        int iterations = 1;
        iss >> iterations;
        run_iterations(iterations);
        display();
    }

    else if (cmd == "dump") {
        std::string filename;
        if (iss >> filename) {
            std::ofstream out_file(filename);
            if (out_file.is_open()) {
                out_file << *this;
                std::cout << "Universe dumped to " << filename << std::endl;
            }
            else
                std::cerr << "Error: Unable to open file " << filename << std::endl;
        }
        else
            std::cout << "No filename provided for dump command. Use help." << std::endl;
    }

    else if (cmd == "exit")
        return false;

    else if (cmd == "help")
        std::cout << "Available commands:\n"
                  << "  tick <n=1> - Perform n iterations (default is 1)\n"
                  << "  dump <filename> - Save the current state to a file\n"
                  << "  exit - End the game\n"
                  << "  help - Show this help message\n";

    else
        std::cout << "Unknown command. Use help." << std::endl;

    return true;
}


void Game::run_iterations(int n) {
    for (int i = 0; i < n; ++i)
        run_iteration();
}


std::ostream& operator<<(std::ostream& os, const Game& game) {
    os << "#Life 1.06\n";
    os << "#N " << game.universe_name << "\n";
    os << "#R B" << game.birth_rule << "/S" << game.survival_rule << "\n";
    os << "#S " << game.field_size << "\n";

    for (size_t y = 0; y < game.game_field.size(); ++y)
        for (size_t x = 0; x < game.game_field[y].size(); ++x)
            if (game.game_field[y][x].is_alive())
                os << x << " " << y << "\n";

    return os;
}


std::istream& operator>>(std::istream& is, Game& game) {
    std::string str;
    game.universe_name.clear();
    game.birth_rule.clear();
    game.survival_rule.clear();
    bool checked_format = false;
    std::regex format_mask("#Life 1.06");
    std::regex name_mask("#N +");
    std::regex rule_mask("#R +B");
    std::regex size_mask("#S +");
    std::smatch format;
    std::smatch name;
    std::smatch rule;
    std::smatch size;
    
    while (std::getline(is, str)) {
        if (str.empty()) continue;
    
        if (!checked_format) {
            if (regex_search(str, format, format_mask)) {
                checked_format = true;
                continue;
            }
        }
        
        if (game.universe_name.empty()) {
            if (regex_search(str, name, name_mask)) {
                game.universe_name = name.suffix();
                continue;
            }
        }
        
        if (game.birth_rule.empty())
            if (regex_search(str, rule, rule_mask)) {
                if (!game.set_rules(rule.suffix())) {
                    is.setstate(std::ios::failbit);
                    return is;
                }
                continue;
            }
        
        
        if (game.field_size == 0)
            if (regex_search(str, size, size_mask)) {
                int f_size = atoi(size.suffix().str().c_str());
                if (f_size > 0) {
                    game.field_size = f_size;
                    game.game_field.resize(f_size * sizeof(std::vector<Cell>(f_size)));
                    for (int i = 0; i < f_size; i++)
                        game.game_field[i] = std::vector<Cell>(f_size);
                }
                else {
                    is.setstate(std::ios::failbit);
                    return is;
                }
                continue;
            }
        
        std::istringstream coord_stream(str);
        int x, y;
        
        if (!(coord_stream >> x >> y)) {
            is.setstate(std::ios::failbit);
            return is;
        }
        
        if (x >= 0 && y >= 0 && x < game.field_size && y < game.field_size)
            game.game_field[x][y].set_state(true);
        else {
            is.setstate(std::ios::failbit);
            return is;
        }
    }

    if (game.universe_name.empty()) {
        std::cout << "Warning: Unnamed university." << std::endl;
        game.universe_name = "Unnamed";
    }

    if (game.birth_rule.empty()) {
        std::cout << "Warning: No birth/survival rules. Will be used default life rules." << std::endl;
        game.birth_rule = "3";
        game.birth_rule = "23";
    }

    is.clear();
    return is;
}
