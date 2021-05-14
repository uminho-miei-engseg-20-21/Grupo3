#include <stdio.h>
#include <string.h>
#include <stdlib.h>


//Encontram-se aqui as alterações realizadas no código utilizando programação defensiva 
//com o objetivo de mitigar as suas vulnerabilidades


int main(int argc, char **argv) {
    
    // 1) Validação dos argumentos do programa. Deve receber 2
    if (argc == 2){
    
       // 2) Alocar memoria sufciente para a variável dummy
        char *dummy = (char *) malloc (sizeof(char) * sizeof(argv[1]));
        char *readonly = (char *) malloc (sizeof(char) * 10);
        
      // 3) substitui-se a utilização da função *strcpy* pela função *strncpy*
        strncpy(readonly, "laranjas", sizeof("laranjas"));
        strncpy(dummy, argv[1], sizeof(argv[1]));
        printf("%s\n", readonly);
    }
    else{
        printf ("Número inválido de argumentos \n");
    }
}
