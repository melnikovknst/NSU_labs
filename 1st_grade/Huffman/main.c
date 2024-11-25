#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <wchar.h>
#include <locale.h>
#include <math.h>


#include "tree.h"
#include "queue.h"


//  reversing string str
char *reverse(char *str, int len) {
    for (int i = 0; i < len - 1 - i; i++) {
        char tmp = str[i];
        str[i] = str[len-i-1];
        str[len-i-1] = tmp;
    }
    
    return str;
}


NODE **buildHuffmanTree(NODE **tree, int *count, QUEUE *queue) {
    while (queue->prev)
        queue = queue->prev;
    
    long prior = queue->priority + queue->next->priority;
    
    tree = addToTree(tree, count, WEOF, prior, queue->node, queue->next->node);
    
    queue = pop(queue);
    queue = pop(queue);
    
    queue = addToQueue(queue, tree[*count-1], NULL, NULL);
    
    if (!queue->prev & !queue->next) {
        free(queue);
        return tree;
    }
    
    return buildHuffmanTree(tree, count, queue);
}


//  codding and printing Huffman tree
void codeTree(FILE *out, NODE *node, unsigned char *buf, short *counter) {
    if (*counter == 8) {
        fputc(*buf, out);
        *buf = 0;
        *counter = 0;
    }
    
    if (!node->right & ! node->left) {
        *buf |= (1 << (7 - (*counter)++));
        for (int i = 15; i > -1; i--) {
            if (*counter == 8) {
                fputc(*buf, out);
                *buf = 0;
                *counter = 0;
            }
            
            if (node->symbol & (1 << i))
                *buf |= (1 << (7 - (*counter)++));
            else
                *buf &= ~(1 << (7 - (*counter)++));

        }
    }
    else {
        *buf &= ~(1 << (7 - (*counter)++));
        if (node->left)
            codeTree(out, node->left, buf, counter);
        
        if (node->right)
            codeTree(out, node->right, buf, counter);
    }
}


//  returns code of symbol chr
char *code(char ** codes, NODE **tree, wchar_t chr, int i) {
    while (chr != tree[i]->symbol) {
        i++;
        return code(codes, tree, chr, i);
    }
    return codes[i];
}


//  codding and printing text
void codeText(FILE *in, FILE *out, unsigned char buf, short counter, char ** codes, NODE **tree) {
    rewind(in);
    wchar_t chr;
    
    while ((chr = fgetwc(in)) != WEOF) {
        char *chr_code = code(codes, tree, chr, 0);
        int i = 0;
        
        while(chr_code[i] && chr_code) {
            if (counter == 8) {
                fputc(buf, out);
                buf = 0;
                counter = 0;
            }
            if (chr_code[i] == '1')
                buf |= (1 << (7 - (counter)++));
            else
                buf &= ~(1 << (7 - (counter)++));
            
            i++;
        }
    }
    if (counter == 8) {
        fputc(buf, out);
        buf = 0;
        counter = 0;
    }
}



void encode(FILE *in, FILE *out) {
    NODE **tree = NULL;
    int count = 0;
    wchar_t chr;
    
    while ((chr = fgetwc(in)) != WEOF)
        if (!contains(tree, count, chr))
            tree = addToTree(tree, &count, chr, 1, NULL, NULL);
    
    int len = count; // alphabet len
    QUEUE *queue = NULL;
    
    for (int i = 0; i< count; i++)
        queue = addToQueue(queue, tree[i], NULL, NULL);
    
    tree = buildHuffmanTree(tree, &count, queue);
    NODE *root = tree[count-1];
    int zeroes = count + 3; // insignificant zeros
    

//    building codes table
    char **codes = (char**)malloc(sizeof(char*) * len);
    for (int i = 0; i < len; i++) {
        wchar_t chr = tree[i]->symbol;
        int codeLen = 0;
        char *code = NULL;
        search(root, &code, chr, &codeLen);
        codes[i] = reverse(code, codeLen);
        zeroes += tree[i]->count * codeLen;
    }
    
    zeroes = ((ceil)((float)zeroes/8))*8 - zeroes;

    unsigned char buf = 0;
    short counter = 0;
    
//      information about insignificant zeros
    for (int i = 2; i > -1; i--) {
        if (zeroes & (1 << i))
            buf |= (1 << (7 - (counter)++));
        else
            buf &= ~(1 << (7 - (counter)++));
    }
    
//      print insignificant zeros
    while (zeroes) {
        if (counter == 8) {
            fputc(buf, out);
            buf = 0;
            counter = 0;
        }
        buf &= ~(1 << (7 - (counter)++));
        zeroes--;
    }
    
//    print alphabet len
    for (int i = 15; i > -1; i--) {
        if (counter == 8) {
            fputc(buf, out);
            buf = 0;
            counter = 0;
        }
        if (len & (1 << i))
            buf |= (1 << (7 - (counter)++));
        else
            buf &= ~(1 << (7 - (counter)++));
    }
    
    codeTree(out, tree[count-1], &buf, &counter);
    codeText(in, out, buf, counter, codes, tree);
    
    free(tree);
}


void decodeText(FILE *in, FILE *out,NODE *node, int *buf, short *buf_cur) {
    if (node->left == NULL & node->right == NULL) {
        fputwc(node->symbol, out);
        return;
    }
    if (*buf_cur < 0) {
        *buf = fgetc(in);
        *buf_cur = 7;
    }
    if (*buf & (1 << (*buf_cur)--))
        decodeText(in, out, node->right, buf, buf_cur);
    else
        decodeText(in, out, node->left, buf, buf_cur);
}


void decode(FILE *in, FILE *out) {
    int buf = 0;
    short buf_cur = 7;
    short zeroes = 0;
    buf = fgetc(in);
    
    for (int i = 0; i < 3; i++) {
        if (buf & (1 << (buf_cur--)))
            zeroes |= (1 << (2 - i));
    }
//    skip insignificant zeros
    while (zeroes) {
        zeroes--;
        buf_cur--;
        if (buf_cur < 0) {
            buf = fgetc(in);
            buf_cur = 7;
        }
    }

    int alphabet = 0; //   alphabet len
    for (int i = 15; i > -1; i--) {
        if (buf & (1 << (buf_cur--)))
            alphabet |= (1 << i);
        
        if (buf_cur < 0) {
            buf = fgetc(in);
            buf_cur = 7;
        }
    }
    
    int leafs = 0;
    NODE *root = addNode(WEOF, NULL, NULL);
    buf_cur--;
    readTree(in, &buf, &buf_cur, &leafs, alphabet, root);
    
    while (buf != EOF) {
        decodeText(in, out, root, &buf, &buf_cur);
        if (buf_cur < 0) {
            buf = fgetc(in);
            buf_cur = 7;
        }
    }
    del(root);
}


int main(int argc, char *argv[]) {
    setlocale(LC_ALL, "");
    FILE *in = fopen(argv[1], "r");
    FILE *out = fopen(argv[3], "w");
    char *operation = argv[2];
    
    if (strcmp(operation, "c") == 0)
        encode(in, out);
    else
        decode(in, out);

    fclose(in);
    fclose(out);
    return 0;
}
