#ifndef graph_h
#define graph_h


void DFS(int *matrix, short* checked, short *res, int n, int idx, int *count) {
    *(checked + idx) = 1;
    
    for (short i = 0; i < n; i ++) {
        if (*(matrix + idx * n + i) && !*(checked + i))
            DFS(matrix, checked, res, n, i, count);
    }
    
    if (*count < n) {
        *(res + *count) = idx;
        *count += 1;
    }
    else
        printf("%d ", idx);
}


void invert(int *matrix, int n) {
    int buf;
    
    for (int i = 0; i < n; i++)
        for (int j = i; j < n; j++) {
            buf = *(matrix + i*n +j);
            *(matrix + i*n +j) = *(matrix + j*n +i);
            *(matrix + j*n +i) = buf;
        }
}


void kosaraju(int *matrix, short* checked, int n) {
    short *res = (short*)malloc(sizeof(short)*n);
    int count = 0;
    
//    DFS with inverted matrix
    for (short i = 0; i < n; i++)
        if (!*(checked + i))
            DFS(matrix, checked, res, n, i, &count);
            
    
//    clen up the check list
    for (short i = 0; i < n; i++)
        *(checked + i) = 0;
    
//    invert the matrix to its original state
    invert(matrix, n);
    
    for (short i = n-1; i > -1; i--)
        if (!*(checked + *(res + i))) {
            DFS(matrix, checked, res, n, *(res + i), &count);
            puts("");
        }
    
    free(res);
}

#endif /* graph_h */
