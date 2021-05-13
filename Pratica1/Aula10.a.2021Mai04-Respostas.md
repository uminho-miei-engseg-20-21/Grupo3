# Aula TP 10 - 4/Maio/2021 - Resolução

## Pergunta P1.1

Os programas apresentados, independetemente da linguagem, tem como objetivo criar e preencher um array e para isso aloca parte da memoria estática para armazenar 10 inteiros. Ao utilizador é pedido que indique o número de inteiros que pretende guardar. Assim, se for introduzido um número inteiro igual ou menor que 10 o programa comporta-se como esperado, guardando os valores no array. Se em seguida o valor inserido for referente a uma das 10 posições do array {0,1,2,3,4,5,6,7,8,9} então é retornado o valor guardado nessa posição e o programa termina com sucesso a sua execução. 

Apresenta-se abaixo os resultados obtidos quando o programa tem sucesso na  execução, ou seja são cumpridos os pressupostos antes apresentados (Valor a guardar: int <= 10 e valor a recuperar 0<= int <10):

```
> python3 LOverflow3.py

Quantos valores quer guardar no array? 10
Que valor deseja recuperar? 1
[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
O valor e  9


> java LOverflow3.java

Quantos valores quer guardar no array?
10
Que valor deseja recuperar?
1
O valor é 9


> ./overflow3

Quantos valores quer guardar no array? 10
Que valor deseja recuperar? 1
O valor é 9

```


Mas, tendo em conta que foi apenas alocado espaço para 10 inteiros, no caso do user indicar um número superior o programa vai ter uma reação diferente de acordo com a liguagem de programação utilizada.

De modo a analisar os diferentes comportamentos de acordo com a linguagem apresenta-se o output obtido em cada uma delas quando se tenta guardar 11 inteiros.

```
> python3 LOverflow3.py
Quantos valores quer guardar no array? 11
Traceback (most recent call last):
  File "LOverflow3.py", line 5, in <module>
    vals[i] = count-i
IndexError: list assignment index out of range


> java LOverflow3.java
Quantos valores quer guardar no array?
11
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 10
        at LOverflow3.main(LOverflow3.java:15)


> ./overflow3
Quantos valores quer guardar no array? 11
Que valor deseja recuperar? 2
O valor é 9
*** stack smashing detected ***: terminated
Aborted
```


Para a implementação em Java, a execução do programa pará com o lançamento da exeption *Index 10 out of bounds for length 10*. O script python também interrompe a execução com o lançamento de uma exepção: *IndexError: list assignment index out of range*. 

Por outro lado no programa em linguagem C++, conseguimos aceder aos valores que se encontram em posições do array que não deviam ser possiveis obter abortando depois a execução com um *stack smashing*. No caso de se utilizar um valor de elevadas dimensões o programa dá *segmentation fault*.

Com a resolução deste exercicío e das experiências anteriores concluímos que os programas em Java e Python não são vulneráveis a problemas de Buffer overflow. Isto deve-se ao facto de serem linguagens *Memory Safe*, ou seja não permitem o acesso a regiões de memória não alocadas para o efeito. Em opsição, o linguagem C++ não realiza qualquer controlo de acesso a mémoria.


## Pergunta P1.2

* RootExploit.c

Neste programa, o utilizador é solicitado a inserir uma palavra passe. A vulnerabilidade existente está relacionada com a não validação do tamanho do input, associado a password inserida, lido pela função *gets*.

Uma vez que temos acesso ao código, facilmente se obteve o endereço do *buff* e da variável *pass*, e se percebeu que a *pass* se encontra 4 posições depois do buff. Assim, qualquer password inserida com pelo menos 5 valores, apesar de errada, vai permitir preencher o *buff* e activar a variável *pass* que atribui as permissões de admin.

Abaixo apresenta-se o processo executado.

```
gcc -o RootExploit RootExploit.c -fno-stack-protector

./RootExploit

 Endereço da variável pass: 0x7ffce7e5fdfc

 Endereço da variável buff: 0x7ffce7e5fdf8

 Insira a password de root:
ollla

 Password errada

 Foram-lhe atribuidas permissões de root/admin
```


* 0-simple.c

Neste programa a vulnerabilidade existente ocorre pelas mesmas razões que a anterior, devido a não validação do tamanho de input. 
Aqui a variavel *buffer* é um array de 64 bytes. Consultando o endereço de cada variável conseguimos identificar que as mesmas distam 76 posições. Quando este valor é ultrapassado a variável de controlo é alterada deixando de estar a zero e obtendo-se a mensagem "YOU WIN"


```
> ./0-simple

 Endereço da variável control: 0x7ffe1ac6058c

 Endereço da variável buffer: 0x7ffe1ac60540
You win this game if you can change variable control'
ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
YOU WIN!!!

```


## Pergunta P1.3

O programa começa por alocar um buffer de 100 bytes. Em seguida o utilizador deve escolher o tamanho do input  (nº de carateres) e uma frase.
Depois de realizar alguns testes e analisar o código com maior promenor percebe-se que o mesmo se encontra vulneravel por
não realizar uma validação do input, permitindo que o valor inserido para o tamanho seja maior que o disponivel no buffer e ainda que a frase inserida seja de dimensão inferior ao tamanho so buffer. 

Assim, quando todos os carateres pertencentes à frase passam pelo While para serem impressos, o ciclo continua a iterar até atingir o número de carateres dado pelo user e apresentando o conteúdo das posições de memória que se seguem.


```
> ./ReadOverflow

Insira numero de caracteres: 600
Insira frase: OLA
ECO: |OLA.............................@P$U..............WM.9...VM.9...-c$U..ȟP-v...b$U...........a$X...L.9.......|............4-v... 6T-v...hN.9...........a$U..b$U..rH..a$U..`N.9...................r.kmL C{M................................hN.9...xN.9...QT-v....................a$U..`N.9....................a$U..XN.9...................a.9...........a.9...a.9...a.9...a.9...bb.9...pb.9...b.9...b.9...b.9...b.9...h.9...h.9...h.9...h.9....i.9....i.9....i.9...Xi.9...^o.9...no.9...o.9...........!.........9................|
```

## Pergunta P1.4


## Pergunta P1.5


## Pergunta P1.6