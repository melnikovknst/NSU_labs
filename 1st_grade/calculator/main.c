#include <stdio.h>
#include <string.h>
#include "stack.h"
#include "string.h"


char* postfix_notation(char*);
void calculate(char*);
int order(char);


int main(void) {
    char* input = get_input();
    char* postfix = postfix_notation(input);
    
    if ((is_digit(input[0]) != 1 && input[0] != '(') || !postfix) {
        printf("syntax error\n");
        
        return 0;
    }
        
    calculate(postfix);
    
    return 0;
}


char* postfix_notation(char* str) {
    char* postfix = (char*)malloc(sizeof(char));
    STACK* op_stack = NULL;     // stack for operators
    int len = (int)strlen(str);
    int operators = 0;
    int brackets = 0;
    
    for (int i = 0; i < len; i++) {
        char chr = str[i];
        
        if (is_digit(chr) == 1)
            concatenate(&chr, &postfix);
        else if (is_digit(chr) == 0) {
            if (chr != '(')
                concatenate(" ", &postfix);
            
            if (empty(op_stack)) {
                push(&op_stack, (int)chr);
                
                if (chr != '(')
                    operators++;
                else
                    brackets++;
            }
            else if (chr == '(') {
                push(&op_stack, (int)chr);
                brackets++;
            }
            else if (chr == ')' && brackets <= 0)
                return NULL;
            else if (chr == ')') {
                operators++;
                brackets--;
                
                if (str[i - 1] == '(')
                    return NULL;
                
                while (op_stack && (char)(op_stack->value) != '(') {
                    char op = (char)pop(&op_stack);
                    concatenate(&op, &postfix);
                    concatenate(" ", &postfix);
                }
                if (op_stack && (char)(op_stack->value) == '(')
                    pop(&op_stack);
            }
            else if (order(chr) > order(op_stack->value)) {
                push(&op_stack, (int)chr);
                operators++;
            }
            else {
                while (op_stack && order(op_stack->value) >= order(chr)) {
                    char op = (char)pop(&op_stack);
                    
                    if (op != '(') {
                        concatenate(&op, &postfix);
                        concatenate(" ", &postfix);
                    }
                }                
                push(&op_stack, (int)chr);
                operators++;
            }
        }
        else
            return NULL;
    }
    
    if (operators * 2 >= (int)strlen(str))
        return NULL;
    
    while (op_stack) {
        char op = pop(&op_stack);
        concatenate(" ", &postfix);
        concatenate(&op, &postfix);
    }
    
    return postfix;
}


int order(char op) {
    char* opers = "((-+/*)";
    for (int i = 0; i < (int)strlen(opers); i++)
        if (opers[i] == op)
            return (int)(i/2);
    
    return -1;
}


void calculate(char* postfix) {
    char *elem = strtok(postfix, " ");
    STACK* calc_stack = NULL;
    
    while (elem != NULL) {
        if ((int)strlen(elem) == 1 && is_digit(elem[0]) == 0){
            if (empty(calc_stack) || calc_stack->next == NULL) {
                printf("syntax error\n");
                return;
            }
            else if (elem[0] == '/' && calc_stack->value == 0) {
                printf("division by zero\n");
                return;
            }
            else {
                int a = calc_stack->next->value;
                int b = calc_stack->value;
                pop(&calc_stack);
                
                switch (elem[0]) {
                    case '-':
                        calc_stack->value = a - b;
                        break;
                    case '+':
                        calc_stack->value = a + b;
                        break;
                    case '*':
                        calc_stack->value = a * b;
                        break;
                    case '/':
                        calc_stack->value = a / b;
                        break;
                    default:
                        break;
                }
            }
                
        }
        else if ((int)strlen(elem) != 0)
            push(&calc_stack, atoi(elem));
        
        elem = strtok(NULL, " ");
    }
    
    if (calc_stack && !calc_stack->next)
        printf("%d\n", calc_stack->value);
    else
        printf("syntax error\n");
}
