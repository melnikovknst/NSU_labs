#include <stdio.h>
#include <stdlib.h>


#ifndef string_h
#define string_h


char* get_input(void) {
    char* input = (char*)malloc(0);
    int input_len = 1;
    int chr;
    
    while ((chr = getchar()) != '\n') {
        input = realloc(input, input_len++ * sizeof(char));
        input[input_len - 2] = chr;
    }
    
    return input;
}


//  units str1 and str2
void concatenate(char* str, char** postfix) {
    int len = (int)strlen(*postfix);
    *postfix = realloc(*postfix, (len + 2) * sizeof(char));
    strncat(*postfix, str, 1);
}


//  returns 1 if smbl is digit, 0 if it`s an operator and -1 if syntax error
int is_digit(char smbl) {
    if ('0' <= smbl && smbl <= '9')
        return 1;
    else if ('(' <= smbl && smbl <= '/' && smbl != '.' && smbl != ',')
        return 0;
    
    return -1;
}


#endif /* string_h */
