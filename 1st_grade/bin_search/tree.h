#ifndef tree_h
#define tree_h


#define TREE struct tree
#define MAX_LEN 128


#include <stdio.h>
#include <stdlib.h>
#include <math.h>


struct tree {
    int val;
    TREE* left;
    TREE* right;
};


TREE* init(int val) {
    TREE* unit = (TREE*)malloc(sizeof(TREE));
    unit->val = val;
    unit->right = NULL;
    unit->left = NULL;
    
    return unit;
}


int height(TREE* unit) {
    if (!unit)
        return 0;
    
    return fmax(height(unit->left), height(unit->right)) + 1;
}


TREE* lft_rotation(TREE* unit) {
    TREE* tmp = unit->right;
    unit->right = tmp->left;
    tmp->left = unit;
    
    return tmp;
}


TREE* rght_rotation(TREE* unit) {
    TREE* tmp = unit->left;
    unit->left = tmp->right;
    tmp->right = unit;
    
    return tmp;
}


TREE* balance(TREE* unit) {
    int right = height(unit->right);
    int left = height(unit->left);
    
    if (right - left == 2) {
        if (height(unit->right->right) >= height(unit->right->left))
//            small left rotation
            unit = lft_rotation(unit);
        else {
//            big left rotation
            unit->right = rght_rotation(unit->right);
            unit = lft_rotation(unit);
        }
    }
    
    else if (left - right == 2){
        if (height(unit->left->left) >= height(unit->left->right))
//            small right rotation
            unit = rght_rotation(unit);
        else {
//            big right rotation
            unit->left = lft_rotation(unit->left);
            unit = rght_rotation(unit);
        }
    }
      
    return unit;
}


TREE* add(TREE* unit, int val) {
    if (val < unit->val) {
        if (unit->left)
            unit->left = add(unit->left, val);
        else
            unit->left = init(val);
    }
    else {
        if (unit->right)
            unit->right = add(unit->right, val);
        else
            unit->right = init(val);
    }
    
    unit = balance(unit);
    return unit;
}


void ordered_print(TREE* unit) {
    if (unit->left)
        ordered_print(unit->left);
    
    printf("%d ", unit->val);
    
    if (unit->right)
        ordered_print(unit->right);
}


void search(int elem, TREE* unit) {
    if (unit->val == elem)
        printf("Element %d found, height: %d\n", elem, height(unit));

    else if (elem < unit->val && unit->left)
        search(elem, unit->left);
    
    else if (elem > unit->val && unit->right)
        search(elem, unit->right);
    
    else
        printf("Element %d not found\n", elem);
    
}


void del(TREE* unit) {
    if (unit->left)
        del(unit->left);
    
    if (unit->right)
        del(unit->right);
    
    free(unit);
}

#endif /* tree_h */
