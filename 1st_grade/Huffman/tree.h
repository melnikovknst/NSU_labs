#ifndef tree_h
#define tree_h


typedef struct NODE {
    wchar_t symbol;
    long count;
    struct NODE* left;
    struct NODE* right;
} NODE;


NODE **addToTree(NODE **tree, int *idx, wchar_t chr, long count, NODE* left, NODE *right) {
    if (!tree) {
        tree = (NODE**)malloc(sizeof(NODE*));
        tree[0] = (NODE*)malloc(sizeof(NODE));
    }
    else {
        tree = realloc(tree, sizeof(NODE*) * (*idx + 1));
        tree[*idx] = (NODE*)malloc(sizeof(NODE));
    }
    (tree[*idx])->symbol = chr;
    (tree[*idx])->count = count;
    (tree[*idx])->left = left;
    (tree[*idx])->right = right;
    
    (*idx)++;
    return tree;
}


int contains(NODE **tree, int count, wchar_t chr) {
    if (tree)
        for (int i = 0; i < count; i++)
            if ((tree[i])->symbol == chr) {
                (tree[i])->count += 1;
                return 1;
            }
    return 0;
}


int search(NODE *tree, char **code, wchar_t chr, int *codeLen) {
    if (tree->symbol == chr)
        return 1;
    if (!tree->left & !tree->right)
        return -1;
    if (tree->left)
        if(search(tree->left, code, chr, codeLen) == 1) {
            *code = realloc(*code, sizeof(char) * ((*codeLen)+1));
            (*code)[*codeLen] = '0';
            (*codeLen)++;
            return 1;
        }
    if (tree->right)
        if(search(tree->right, code, chr, codeLen) == 1) {
            *code = realloc(*code, sizeof(char) * ((*codeLen)+1));
            (*code)[*codeLen] = '1';
            (*codeLen)++;
            return 1;
        }
    return 0;
}


NODE *addNode(wchar_t symbol, NODE* left, NODE* right) {
    NODE *node = (NODE*)malloc(sizeof(NODE));
    
    node->symbol = symbol;
    node->left = left;
    node->right = right;
    
    return node;
}


void readTree(FILE *in, int *buf, short *buf_cur, int *leafs, int alphabet, NODE *node) {
    if (*leafs == alphabet)
        return;
    
    if (node->left != NULL & node->right != NULL)
        return;
    
    if (*buf_cur < 0) {
        *buf = fgetc(in);
        *buf_cur = 7;
    }
    
    if (*buf & (1 << *buf_cur)) {
        (*leafs)++;
        (*buf_cur)--;
        wchar_t symbol = 0;

        for (int i = 15; i > -1; i--) {
            if (*buf_cur < 0) {
                *buf = fgetc(in);
                *buf_cur = 7;
            }
            if (*buf & (1 << (*buf_cur)--))
                symbol |= (1 << i);
        }

        if (!node->left)
            node->left = addNode(symbol, NULL, NULL);
        else
            node->right = addNode(symbol, NULL, NULL);
        
        readTree(in, buf, buf_cur, leafs, alphabet, node);
    }
    else {
        (*buf_cur)--;
        
        if (*buf_cur < 0) {
            *buf = fgetc(in);
            *buf_cur = 7;
        }
        
        if (!node->left) {
            node->left = addNode(WEOF, NULL, NULL);
            readTree(in, buf, buf_cur, leafs, alphabet, node->left);
        }
        else if (!node->right) {
            node->right = addNode(WEOF, NULL, NULL);
            readTree(in, buf, buf_cur, leafs, alphabet, node->right);
        }
        readTree(in, buf, buf_cur, leafs, alphabet, node);
    }
    return;
}


void del(NODE *node) {
    if (node->left)
        del(node->left);
    
    if (node->right)
        del(node->right);
    
    free(node);    
}


#endif
