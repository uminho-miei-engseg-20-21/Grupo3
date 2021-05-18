#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

void vulneravel (char *matriz, size_t x, size_t y, char valor) {
        int i, j;
        matriz = (char *) malloc(x * y);

        for (i = 0; i < x; i++) {
                for (j = 0; j < y; j++) {
                        printf("%d %d\n", i, j);
                        matriz[i * y + j] = valor;
                }
        }
}

int main() {
        char* matriz;
        vulneravel(matriz, SIZE_MAX, SIZE_MAX, '1');
}