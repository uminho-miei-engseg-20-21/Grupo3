# Aula TP 10 - Parte B - 4/Maio/2021 - Resolução

## Pergunta P1.1
1. Analisando o programa *overflow.c*, a vulnerabilidade que existe na função ```vulneravel()``` é o facto de as variáveis ``x`` e ``y`` terem sido definidas com o tipo ``size_t`` e o seu tamanho nao estar a ser validado. Este tipo de dados é um tipo *unsigned*, sendo que o seu tamanho varia de acordo com o tipo de dados concreto (um inteiro, um *char*, um *long*, entre outros).
O valor máximo permitido para alocação de memória na função ``malloc`` é 16711568 *bytes*. Como não há validação do tamanho destas variáveis, pode ocorrer *overflow* pois caso seja definida uma matriz cujo tamanho ultrapasse esse valor, irá ocorrer um erro de *segmentation fault*. 

2. De modo a demonstrar a vulnerabilidade descrita na pergunta anterior, definimos a função ``main`` da seguinte forma:

```C
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
```
Esta função está a chamar a função ``vulneravel`` com os argumentos ``x`` e ``y`` tomando o valor de ``SIZE_MAX`` (que é fornecido pela biblioteca ``<stdint.h>``).

3. Ao executar o programa, obtemos um erro de ***segmentation fault***, pois o espaço de memória alocada para armazenar a matriz criada pela função ``vulneravel()``, ultrapassa o valor permitido.

## Pergunta P1.2
1. Se a variável ``tamanho`` tomar o valor *0*, então a variável ``tamanho real`` tomará o valor *-1*. Uma vez que a função ``malloc`` recebe um argumento do tipo ``size_t``, que é *unsigned*, ao ser executada com um argumento **-1** (que é um inteiro negativo e, portanto, *signed*), este será convertido para um número *unsigned*. Neste caso, o -1 será convertido para ``-1 + MAX_SIZE_T + 1``, o que resulta em ``MAX_SIZE_T``. Este número ultrapassa o limite permitido e, portanto, o espaço de memória não pode ser alocado, pelo que a função ``malloc`` retornará um endereço nulo. Por sua vez, a função ``memcpy`` irá retornar *segmentation fault*, pois a variável ``destino`` não tem espaço alocado em memória.

2. De modo a demonstrar a vulnerabilidade descrita na pergunta anterior, definimos a função ``main`` da seguinte forma:
```C
int main() {
        char *origem;
        vulneravel(origem, 0);
}
```
Esta função está a chamar a função ``vulneravel`` com os argumento ``tamanho`` a tomar o valor 0.

3. Ao executar, retorna um erro de *segmentation fault*, que ocorre pelas razões apresentadas na alínea 1.

4. Para mitigar a vulnerabilidade que existe no programa e seguindo as práticas de programação defensiva, basta garantir que a variável ``tamanho``, passada como argumento à função ``vulneravel``, seja maior que 0. Assim sendo, definimos a variável global ``MIN_SIZE``, que indica o valor mínimo que a variável ``tamanho`` deve ter.

```C
const int MIN_SIZE = 0;
const int MAX_SIZE = 2048;

void vulneravel (char *origem, size_t tamanho) {
        size_t tamanho_real;
        char *destino;
        if (tamanho > MIN_SIZE && tamanho < MAX_SIZE) {
                tamanho_real = tamanho - 1; // Não copiar \0 de origem para destino
                destino = malloc(tamanho_real);
                memcpy(destino, origem, tamanho_real);
        }
}

int main() {
    char *origem = "Hello World";
    vulneravel(origem, 0);
}
```



