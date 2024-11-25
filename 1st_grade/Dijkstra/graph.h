#ifndef graph_h
#define graph_h


//      returns index if unchecked vertex with min path
int min_index(long *array, short *checked, int n) {
    long min = LONG_MAX;
    int min_index = -1;
    
    for (int i = 0; i < n; i++)
        if (array[i] != -1 && array[i] < min && !checked[i]) {
            min = array[i];
            min_index = i;
        }
    
    return min_index;
}


void dijkstra(int **path_matrix, long *pathes, short *checked,
              int s, int f, int n, short *overflow) {
    int cur = min_index(pathes, checked, n);
    
    if (cur == -1)
        return;
    
    for (int i = 0; i < n; i++)
        if (path_matrix[cur][i] != -1) {
            if (pathes[f-1] > INT_MAX && i == f-1 && 
                (path_matrix[cur][i] + pathes[cur]) > INT_MAX)
                *overflow = 1;
            if ((path_matrix[cur][i]+pathes[cur]) < pathes[i] || pathes[i]==-1)
                pathes[i] = (long long)path_matrix[cur][i] + pathes[cur];
        }
    
    checked[cur] = 1;
    dijkstra(path_matrix, pathes, checked, s, f, n, overflow);
}


//      prints the shortest path from f to s
void shortest_path(int **path_matrix, long *pathes,
                   short *checked, int s, int f, int n) {
    long cur_weight = LONG_MAX;
    int cur = -1;
    checked[f-1] = 1;
    
    for (int i = 0; i < n; i++) {
        if ((pathes[i] + path_matrix[f-1][i]) < cur_weight && !checked[i]
            && path_matrix[f-1][i] != -1) {
            cur = i;
            cur_weight = pathes[i] + path_matrix[f-1][i];
        }
    }
    
    if (cur != -1)
        printf(" %d", cur + 1);
    if (cur == s - 1)
        return;
    
    shortest_path(path_matrix, pathes, checked, s, cur+1, n);
}

#endif /* graph_h */
