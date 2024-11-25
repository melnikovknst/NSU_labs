#ifndef str_h
#define str_h


char* get_input(void) {
    char* input = (char*)malloc(0);
    int input_len = 0;
    int chr;
    
    while ((chr = getchar()) != '\n') {
        input = realloc(input, 1 + input_len++ * sizeof(char));
        input[input_len - 1] = chr;
    }
    
    return input;
}


#endif /* str_h */
