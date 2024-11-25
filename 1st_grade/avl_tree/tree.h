#ifndef tree_h
#define tree_h


#define TREE struct tree
#define MAX_LEN 128


#include <stdio.h>
#include <stdlib.h>


struct tree {
    int val;
    int height;
    TREE* left;
    TREE* right;
};


TREE* init(int val) {
    TREE* unit = (TREE*)malloc(sizeof(TREE));
    unit->val = val;
    unit->height = 1;
    unit->right = NULL;
    unit->left = NULL;
    
    return unit;
}


int get_hght(TREE* unit) {
    if (unit)
        return unit->height;
    
    return 0;
}



//         define height of unit
void def_hght(TREE* unit) {
    unit->height = get_hght(unit->left);
    
    if (unit->height < get_hght(unit->right))
        unit->height = get_hght(unit->right);
    unit->height++;
}


TREE* l_rotation(TREE* unit) {
    TREE* tmp = unit->right;
    unit->right = tmp->left;
    tmp->left = unit;
    def_hght(unit);
    def_hght(tmp);
    
    return tmp;
}


TREE* r_rotation(TREE* unit) {
    TREE* tmp = unit->left;
    unit->left = tmp->right;
    tmp->right = unit;
    def_hght(unit);
    def_hght(tmp);
    
    return tmp;
}


TREE* balance(TREE* unit) {
    int right = get_hght(unit->right);
    int left = get_hght(unit->left);
    TREE* root = unit;
    
    if (abs(left - right) < 2)
        def_hght(unit);
    
    else if (right - left == 2) {
        if (get_hght(unit->right->right) >= get_hght(unit->right->left))
//            small left rotation
            root = l_rotation(unit);
        else {
//            big left rotation
            unit->right = r_rotation(unit->right);
            root = l_rotation(unit);
        }
    }
    
    else {
        if (get_hght(unit->left->left) >= get_hght(unit->left->right))
//            small right rotation
            root = r_rotation(unit);
        else {
//            big right rotation
            unit->left = l_rotation(unit->left);
            root = r_rotation(unit);
        }
    }
      
    return root;
}


TREE* add(TREE* unit, int val) {
    if (val < unit->val) {
        if (unit->left)
            add(unit->left, val);
        else
            unit->left = init(val);
    }
    else {
        if (unit->right)
            add(unit->right, val);
        else
            unit->right = init(val);
    }
    
    unit = balance(unit);
    return unit;
}


void del(TREE* unit) {
    if (unit->left)
        del(unit->left);
    
    if (unit->right)
        del(unit->right);
    
    free(unit);
}

#endif /* tree_h */
