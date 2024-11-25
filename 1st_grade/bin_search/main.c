#include <stdio.h>
#include "tree.h"


int main(void) {
    int buf;
    int n;
    scanf("%d", &n);
    
    if (n == 0) {
        printf("%d", n);
        return 0;
    }
    
    scanf("%d", &buf);
    TREE* root = init(buf);
    
    for (int i = 1; i < n; i++) {
        scanf("%d", &buf);
        root = add(root, buf);
    }

    printf("//\nTree height: %d\n", height(root));
    puts("-----");
    ordered_print(root);
    
//    Searching element
    int elem;
    puts("\nEnter element for searching");
    scanf("%d", &elem);
    search(elem, root);
    
    del(root);
    
    return 0;
}
