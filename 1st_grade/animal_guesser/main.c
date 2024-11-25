#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "str.h"
#include "tree.h"
#include "game.h"


int main(void) {
    FILE* sourse = fopen("source.txt", "r");
    printf("Загадайте любое животное и отвечайте на вопросы д/н (да/нет)\n\n");
    
//    create bin tree from data in file
    TREE* root = read_file(sourse);
    fclose(sourse);
    
//    start game process
    game(root, root);
       
    del(root);
    return 0;
}
