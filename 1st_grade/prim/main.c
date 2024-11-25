#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


#include "graph.h"


#define VERTEX_MAX 5000
#define EDGES_MAX n*(n-1)/2


int main(void) {
    FILE* file = fopen("in.txt", "r");
    short buf_finish;
    short buf_start;
    long buf_len;
    short n;
    int m;
    
    fscanf(file, "%hi %d", &n, &m);
//    check the correctness of input
    if (n < 0 || n > VERTEX_MAX) {
        printf("bad number of vertices\n");
        return 0;
    }
    if (m < 0 || m > EDGES_MAX) {
        printf("bad number of edges\n");
        return 0;
    }
    
    short *result = (short*)malloc(sizeof(short)*(n-1));
    for (int i = 0; i < n - 1; i++)
        result[i] = 0;
    
    int *checked = (int*)malloc(sizeof(int)*n);
    checked[0] = 1;
    for (int i = 1; i < n; i++)
        checked[i] = 0;

    EDGE *edges = (EDGE*)malloc(sizeof(EDGE)*n);
    for (int i = 0; i < m; i++) {
            
        if(fscanf(file, "%hi %hi %li", &buf_start, &buf_finish, &buf_len) != 3) {
            fclose(file);
            printf("bad number of lines\n");
            return 0;
        }
        
//        check the correctness of input
        if (buf_start < 1 || buf_start > n ||
            buf_finish < 1 || buf_finish > n) {
            printf("bad vertex\n");
            return 0;
        }
        if (buf_len < 1 || buf_len > INT_MAX) {
            printf("bad length\n");
            return 0;
        }
//        adding a new edge
        add(edges, i, buf_start, buf_finish, buf_len);
    }
    
    if (prim(edges, checked, m, result, 0) < n-1 || n == 0)
        printf("no spanning tree\n");
    else {
        for (int i = 0; i < n - 1; i++) {
            EDGE *cur = &edges[result[i]];
            printf("%d %d\n", cur->start, cur->finish);
        }
    }
    
    fclose(file);
    free(result);
    free(edges);
    
    return 0;
}
