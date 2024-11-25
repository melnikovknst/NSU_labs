#include <stdio.h>
#include <stdlib.h>


void swap(int *, int*);
void sort(int *, int, int);


int main(void) {
    int N;
    scanf("%d", &N);
    int array[N];

    for (int i = 0; i < N; i++)
        scanf("%d", &array[i]);

    sort(array, 0, N - 1);
    
    //  printing answer
    for (int k = 0; k < N; k++)
        printf("%d ", array[k]);

    return 0;
}


void swap(int *num1, int *num2) {
    int tmp = *num1;
    
    *num1 = *num2;
    *num2 = tmp;
}


void sort(int *array, int start, int end) {
    int reference = array[end];
    int lft = start;
    int rght = end;

    if (start <= end) {
        while (lft <= rght) {
            //  crossover to the left
            while (array[lft] < reference)
                lft++;
            
            //  crossover to the right
            while (array[rght] > reference)
                rght--;
            
            //  swap elements that are out of place
            if (lft <= rght)
                swap(&array[lft++], &array[rght--]);
        }
        
        sort(array, start, rght);
        sort(array, lft, end);
    }
}
