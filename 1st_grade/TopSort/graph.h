#include <stdio.h>
#include <stdlib.h>


void sort(char* matrix, short* checked, short *res, int n, int idx, int start, int *count, int *cycled) {
    *(checked + idx) = 1;
    
    for(short i = 0; i < n; i ++) {
        if (*(matrix + idx*n + i) && i == start) {
            *cycled = 1;
            return;
        }
        else if (*(matrix + idx*n + i) && !*(checked + i))
            sort(matrix, checked, res, n, i, start, count, cycled);
    }
    
    *count -= 1;
    *(res + *count) = idx + 1;
}
