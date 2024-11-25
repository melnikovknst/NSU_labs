#ifndef graph_h
#define graph_h


#define EDGE struct edge


typedef struct edge {
    short start;
    short finish;
    long len;
} edge;


void add(EDGE *edges, int i, short start, short finish, long len) {
    if (start < finish) {
        edges[i].start = start;
        edges[i].finish = finish;
    }
    else {
        edges[i].start = finish;
        edges[i].finish = start;
    }
    edges[i].len = len;
}


int prim(EDGE *edges, int *checked, int m, short *res, int count) {
    long min_len = LONG_MAX;
    EDGE *min = NULL;
    int idx = 0;
    
    for (int i = 0; i < m; i++) {
        if (checked[edges[i].start-1] != checked[edges[i].finish-1] && edges[i].len < min_len) {
            min_len = edges[i].len;
            min = &edges[i];
            idx = i;
        }
    }
    
    if (!min)
        return count;
    
    res[count++] = idx;
    checked[edges[idx].start-1] = 1;
    checked[edges[idx].finish-1] = 1;
    
    return prim(edges, checked, m, res, count);
}

#endif /* graph_h */
