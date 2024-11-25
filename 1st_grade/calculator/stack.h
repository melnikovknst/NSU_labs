#include <stdio.h>
#include <stdlib.h>


#define STACK struct stack


#ifndef stack_h
#define stack_h


typedef struct stack {
    int value;
    STACK* next;
} stack;


int empty(STACK* elem) {
    return !(elem);
}


int pop(STACK** elem) {
    STACK* del = *elem;
    int value = del->value;
    
    if (empty(del))
        return  0;
    
    *elem = (*elem)->next;
    
    free(del);
    return value;
}


void push(STACK** elem, int value) {
    STACK* new = (STACK*)malloc(sizeof(STACK));
    new->value = value;
    new->next = *elem;
    
    *elem = new;
}


void destroy(STACK** elem) {
    while (*elem != NULL)
        pop(elem);
    
    free(elem);
}


#endif /* stack_h */
