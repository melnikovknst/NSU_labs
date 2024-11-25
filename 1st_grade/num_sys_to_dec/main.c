#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>


int isCorrect (char *num, int b1) {
    int count = 0;
    
    if ((int)strlen(num) == 13) {
        int dot = 0;
        
        for (int i = 0; i < (int)strlen(num); i++)
            if (num[i] =='.')
                dot = 1;
        
        if (!dot)
            return 0;
    }
    
    for (int i = 0; i < (int)strlen(num); i++) {
        int digit = 0;
        
        if (num[i] == '.' && (i == 0 || i == (int)strlen(num) - 1))
            return 0;
        else if (num[i] == '.')
            count++;
        
        if ((!(tolower(num[i]) >= 'a' && tolower(num[i]) <= 'f') && !(tolower(num[i]) >= '0' && tolower(num[i]) <= '9')) && num[i] != '.')
            return 0;
        
        if (tolower(num[i])  >= 'a')
            digit = tolower(num[i]) - 'a' + 10;
        else
            digit = tolower(num[i]) - '0';
        
        if (digit >= b1)
            return 0;
    }
    
    if (count > 1)
        return 0;
    
    return 1;
}


int dotPosition(char *num) {
    for (int i = 0; i < (int)strlen(num); i++)
        if (num[i] == '.')
            return i;
    
    return (int)strlen(num);
}


long double toDec(char *num, int b1) {
    int dot = dotPosition(num);
    int len = (int)strlen(num);
    long double decNum = 0;
    char digit;
    
    for (int i = dot - 1; i >= 0; i--) {
        digit = tolower(num[i]);

        if (digit >= 'a' && digit <= 'f')
            decNum += (digit - 'a' + 10) * pow(b1, dot - i - 1);
        else
            decNum += (digit - '0') * pow(b1, dot - i - 1);
    }
    
    if (dot != len)
        for (int i = dot + 1; i < len; i++) {
            digit = tolower(num[i]);
            
            if (digit >= 'a' && digit <= 'f')
                decNum += (digit - 'a' + 10) * pow(b1, -(i - dot));
            else
                decNum += (digit - '0') * pow(b1, -(i - dot));
        }
    
    return decNum;
}


void reverse(char *resultInt) {
    for (int i = (int)strlen(resultInt) - 1; i >= 0; i--)
        printf("%c", resultInt[i]);
}


void convert(long double decNum, int b2) {
    long long int integer = (long long int)decNum;
    int len = 0;
    int count = 0;
    char resultInt[50] = {"\0"};
    long double fractional = decNum - integer;
    
    if (!integer)
        printf("0");
    
    while (integer) {
        if (integer % b2 > 9)
            resultInt[len++] = integer % b2 - 10 + 'a';
        else
            resultInt[len++] = integer % b2 + '0';
        
        integer /= b2;
    }
    
    reverse(resultInt);
    
    if (fractional != 0)
        printf(".");
    
    while (fractional && count < 12) {
        if ((int)(fractional * b2) > 9)
            printf("%c", ((int)(fractional * b2)) - 10 + 'a');
        else
            printf("%d", (int)(fractional * b2));
        
        fractional = (fractional * b2) - (int)(fractional * b2);
        count++;
    }
    
    printf("\n");
}


int main(void) {
    int b1, b2;
    char num[16];

    scanf("%d %d", &b1, &b2);
    scanf("%s", num);
    
    if (b1 < 2 || b1 > 16 || b2 < 2 || b2 > 16 || !isCorrect(num, b1)) {
        printf("bad input\n");
        
        return 0;
    }
    
    convert(toDec(num, b1), b2);
    
    return 0;
}
