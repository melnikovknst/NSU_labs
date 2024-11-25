#include <stdio.h>
#include <string.h>


int search(char *, int);
void replace(char *, int);
void sort(char*, int);
void swap(char*, int, int);
int correct(char*);


int main(void) {
    char p[100];
    int n;
    fgets(p, sizeof(p), stdin);
    p[(int)strlen(p) - 1] = '\0';
    scanf("%d", &n);
    
    if (!correct(p)) {
        printf("bad input\n");
        return 0;
    }
    
    while (n) {
        if (search(p, n) == -1)
            return 0;
        
        replace(p, search(p, n--));
    
        printf("%s\n", p);
    }
    
    return 0;
}


// checking the correctness of the input
int correct(char *p) {
    for (int i = 0; i < (int)strlen(p); i++) {
        if (p[i] > '9' || p[i] < '0')
            return 0;
        
        for (int k = i + 1; k < (int)strlen(p); k++)
            if (p[i] == p[k])
                return 0;
    }
    
    return 1;
}


// searching for an element to replace
int search(char *p, int n) {
    int len = (int)strlen(p);
    
    for (int i = len - 2; i >= 0; i--)
        if (p[i] < p[i + 1])
            return i;
        
    return -1;
}


// replaces the necessary elements
void replace(char *p, int i) {
    int min = i + 1;
    
    for (int k = i + 1; k < (int)strlen(p); k++)
        if (p[k] < p[min] && p[k] > p[i])
            min = k;

    swap(p, min, i);
    
    sort(p, i);
}


// swap elements with indexes num1 and num2
void swap(char *p, int num1, int num2) {
    int temp;
    
    temp = p[num1];
    p[num1] = p[num2];
    p[num2] = temp;
}


// sort in ascending order
void sort(char *p, int i) {
    int disordered = 1;
    
    while (disordered) {
        disordered = 0;
        
        for (int k = i + 1; k < (int)strlen(p) - 1; k++)
            if (p[k] > p[k + 1]) {
                swap(p, k, k + 1);
                disordered ++;
            }
    }
}
