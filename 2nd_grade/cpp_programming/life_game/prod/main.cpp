#include <iostream>
#include <fstream>
#include <regex>

#include "game.hpp"


int main(int argc, char* argv[]) {
    Universe universe;
    Game game(universe);

    return game.start(argc, argv);
}

