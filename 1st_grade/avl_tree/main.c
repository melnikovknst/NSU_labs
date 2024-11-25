#include <stdio.h>
#include "tree.h"


int main(void) {
    int height = 0;
    int buf;
    int n;
    scanf("%d", &n);
    
    if (n != 0) {
        scanf("%d", &buf);
        TREE* root = init(buf);
        
        for (int i = 1; i < n; i++) {
            scanf("%d", &buf);
            root = add(root, buf);
        }
        
        height = root->height;
        del(root);
    }
    
    printf("%d\n", height);
    return 0;
}
