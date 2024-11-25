#ifndef tree_h
#define tree_h
#define TREE struct tree
#define MAX_LEN 128


#include <stdio.h>
#include <stdlib.h>
#include <string.h>


struct tree {
    char* val;
    TREE* left;
    TREE* right;
};


TREE* init(char* val) {
    TREE* unit = (TREE*)malloc(sizeof(TREE));
    unit->val = val;
    unit->right = NULL;
    unit->left = NULL;
    
    return unit;
}


TREE* read_file(FILE *sourse) {
//    read data from file
    char* buffer = (char*)malloc(MAX_LEN * sizeof(char));
    fgets(buffer, 128, sourse);
    if (buffer[strlen(buffer) - 1] == '\n')
        buffer[strlen(buffer) - 1] = '\0';
    int is_leaf = buffer[0] - '0';
    char* tmp = strtok(buffer, "01");
    char* val = (char*)malloc(MAX_LEN * sizeof(char));
    strcpy(val, tmp);
    
    TREE* new_unit = init(val);
    
    if (!is_leaf)
        new_unit->left = read_file(sourse);
    
    if (!is_leaf)
        new_unit->right = read_file(sourse);
    
    free(buffer);
    return new_unit;
}


void rewrite_file(TREE* unit,  FILE* file) {
    if (unit->left)
        fputs("0", file);
    else
        fputs("1", file);
    
    fputs(unit->val, file);
    fputs("\n", file);
    
    if (unit->left)
        rewrite_file(unit->left, file);
    if (unit->right)
        rewrite_file(unit->right, file);
}


void del(TREE* unit) {
    if (unit->left)
        del(unit->left);
    
    if (unit->right)
        del(unit->right);
    
    free(unit->val);
    free(unit);
}


#endif /* tree_h */
