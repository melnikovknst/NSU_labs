#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#define MAX_LEN 16


int compare(char*, char*, int);
int shift(char*, char);


int main(void) {
    char* text = (char*)malloc(0);
    char sample[MAX_LEN + 2] = {'\0'};
    fgets(sample, sizeof(sample), stdin);
    sample[(int)strlen(sample) - 1] = '\0';
    int start = (int)strlen(sample) - 1;
    int text_len = 1;
    int chr;
    
    // get text
    while ((chr = getchar()) != EOF) {
        text = realloc(text, text_len++ * sizeof(char));
        text[text_len - 2] = chr;
    }
    
    text_len--;
    
    // comparison loop
    while (start < text_len)
        start += shift(sample, text[compare(text, sample, start)]);
    
    free(text);
    return 0;
}


// compares a sample with a substring of text
int compare(char* text, char* sample, int start) {
    int sample_len = (int)strlen(sample);
    
    for (int i = 0; i < sample_len; i++) {
        printf("%d ", start - i + 1);
        if (sample[sample_len - i - 1] != text[start - i])
            return start;
    }
    
    return start - sample_len;
}


// returns the beginning of the next substring of text to compare
int shift(char* sample, char symbol) {
    int sample_len = (int)strlen(sample);
    
    for (int i = sample_len - 2; i >= 0; i--)
        if (sample[i] == symbol)
            return sample_len - i - 1;
    
    return sample_len;
}
