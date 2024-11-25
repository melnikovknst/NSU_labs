#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


#include "graph.h"


#define VERTEX_MAX 5000
#define EDGES_MAX n*(n-1)/2


int main(void) {
    FILE* file = fopen("in.txt", "r");
    short overflow = 0;
    short buf_finish;
    short buf_start;
    long buf_len;
    short n;
    short s;
    short f;
    int m;
    
    fscanf(file, "%hi %hi %hi %d", &n, &s, &f, &m);
//    check the correctness of input
    if (n < 0 || n > VERTEX_MAX) {
        printf("bad number of vertices\n");
        return 0;
    }
    if (m < 0 || m > EDGES_MAX) {
        printf("bad number of edges\n");
        return 0;
    }
    if (s < 1 || s > n || f < 1 || f > n) {
        printf("bad vertex");
        return 0;
    }
    
    long *pathes = malloc(sizeof(long)*n);
    short *checked = malloc(sizeof(short)*n);
    int** path_matrix = malloc(sizeof(int*)*n);
    for (int i = 0; i < n; i++) {
        pathes[i] = -1;
        checked[i] = 0;
        path_matrix[i] = (int*)malloc(sizeof(int)*n);
        for (int j = 0; j < n; j++)
            path_matrix[i][j] = -1;
    }
    pathes[s-1] = 0;

    for (int i = 0; i < m; i++) {
        if(fscanf(file, "%hi %hi %li", &buf_start, &buf_finish, &buf_len) != 3) {
            fclose(file);
            printf("bad number of lines\n");
            return 0;
        }
        
//        check the correctness of input
        if (buf_start < 1 || buf_start > n ||
            buf_finish < 1 || buf_finish > n) {
            printf("bad vertex");
            return 0;
        }
        if (buf_len < 1 || buf_len > INT_MAX) {
            printf("bad length");
            return 0;
        }
//        filling matrix
        path_matrix[buf_start-1][buf_finish-1] = (int)buf_len;
        path_matrix[buf_finish-1][buf_start-1] = (int)buf_len;
    }
    
    dijkstra(path_matrix, pathes, checked, s, f, n, &overflow);
    
//    print distance from s to every vertex
    for (int i = 0; i < n; i++) {
        if (pathes[i] == -1)
            printf("oo ");
        else if (pathes[i] > INT_MAX)
            printf("INT_MAX+ ");
        else
            printf("%li ", pathes[i]);
        
    }
    puts("");

//    information about the vertices of the shortest path from s to f
    if (pathes[f-1] == -1)
        printf("no path\n");
    else if (overflow)
        printf("overflow\n");
    else {
        for (int i = 0; i < n; i++)
            checked[i] = 0;
        printf("%d", f);
        if (s != f)
            shortest_path(path_matrix, pathes, checked, s, f, n);
    }
    puts("");
    
    fclose(file);
    free(pathes);
    free(checked);
    for (int i = 0; i < n; i++)
        free(path_matrix[i]);
    free(path_matrix);
    
    return 0;
}
