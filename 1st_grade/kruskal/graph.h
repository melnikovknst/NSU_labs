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


int kruskal(EDGE *edges, int *comm_idx, int *checked,
             int m, short *res, int count) {
    long min_len = LONG_MAX;
    EDGE *min = NULL;
    int idx = 0;
    
    for (int i = 0; i < m; i++) {
        
        if (!checked[i] && edges[i].len < min_len &&
            comm_idx[edges[i].start] != comm_idx[edges[i].finish]) {
            min_len = edges[i].len;
            min = &edges[i];
            idx = i;
        }
    }
    
    if (!min)
        return count;
    
    res[count++] = idx;
    checked[idx] = 1;
    comm_idx[min->finish] = comm_idx[min->start];
    
    return kruskal(edges, comm_idx, checked, m, res, count);
}

#endif /* graph_h */
