#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


#include "graph.h"


int main(void) {
    short buf_finish;
    short buf_start;
    short n;
    int m;
    
    scanf("%hi %d", &n, &m);
    
    short *checked = (short*)malloc(sizeof(short)*n);
    int* path_matrix = (int*)malloc(sizeof(int)*n*n);
    for (short i = 0; i < n; i++) {
        *(checked + i) = 0;
        for (short j = 0; j < n; j++)
            *(path_matrix + i*n + j) = 0;
    }

//    filling inverted matrix
    for (int i = 0; i < m; i++) {
        scanf("%hi %hi", &buf_start, &buf_finish);
        *(path_matrix + buf_finish * n + buf_start) = 1;
    }
    
    kosaraju(path_matrix, checked, n);
 
    free(checked);
    free(path_matrix);

    return 0;
}
