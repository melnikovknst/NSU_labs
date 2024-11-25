#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


#include "graph.h"


int main(void) {
    FILE* file = fopen("in.txt", "r");
    short buf_finish;
    short buf_start;
    short n;
    int m;
    
    if (!fscanf(file, "%hi", &n)) {
        fclose(file);
        return 0;
    }
    if (fscanf(file, "%d",&m) != 1) {
        fclose(file);
        printf("bad number of lines\n");
        return 0;
    }

    if (n < 0 || n > 2000) {
        printf("bad number of vertices\n");
        fclose(file);
        return 0;
    }
    if (m < 0 || m > (n*(n-1)/2)) {
        printf("bad number of edges\n");
        fclose(file);
        return 0;
    }
    
    short *checked = (short*)malloc(sizeof(short)*n);
    short *res = (short*)malloc(sizeof(short)*n);
    char* path_matrix = (char*)malloc(sizeof(char)*n*n);
    for (short i = 0; i < n; i++) {
        *(checked + i) = 0;
        *(res + i) = 0;
        for (short j = 0; j < n; j++) {
            *(path_matrix + i * n + j) = (char)0;
        }
    }

    for (int i = 0; i < m; i++) {
        if(fscanf(file, "%hi %hi", &buf_start, &buf_finish) != 2) {
            fclose(file);
            printf("bad number of lines\n");
            free(checked);
            free(res);
            free(path_matrix);
            return 0;
        }
        
        if (buf_start < 1 || buf_start > n || buf_finish < 1 || buf_finish > n) {
            printf("bad vertex");
            fclose(file);
            free(checked);
            free(res);
            free(path_matrix);
            return 0;
        }

        *(path_matrix + (buf_start-1) * n + buf_finish-1) = 1;
    }
    
    int cycled = 0;
    int count = n;
    for (short i = 0; i < n; i++) {
        if (!*(checked + i)) {
            sort(path_matrix, checked, res, n, i, i, &count, &cycled);
            if (cycled) {
                printf("impossible to sort");
                fclose(file);
                free(checked);
                free(res);
                free(path_matrix);
                return  0;
            }
        }
    }

    for (short i = 0; i < n; i++) {
        printf("%hi ", *(res + i));
    }

    fclose(file);
    free(checked);
    free(res);
    
    free(path_matrix);

    return 0;
}
