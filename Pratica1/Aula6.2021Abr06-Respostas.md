# Aula TP - 09/Mar/2021 - Resolução

## Pergunta P1.1

De modo a cumprir com os requisitos desta questão, a função _createGenesisBlock_ foi alterada para a configuração apresentada abaixo.


```
 createGenesisBlock(){
        return new Block(0, new Date().toISOString().substr(0, 10), "Bloco inicial da koreCoin", "0");
    }
``` 

## Pergunta P1.2

Recorreu-se a função _addBlock_ para adicionar quatro novos blocos, cujo o conteúdo das transações pode ser visto abaixo.

```
koreCoin.addBlock(new Block (4, "15/04/2021", {amount1: 100}));
koreCoin.addBlock(new Block (5, "16/04/2021", {amount1: 10}));
koreCoin.addBlock(new Block (6, "17/04/2021", {amount1: 50}));
koreCoin.addBlock(new Block (7, "18/04/2021", {amount1: 5}));
```


> A resposta às perguntas 1.1 e 1.2 foram adicionadas ao código fornecido e podem ser consultadas em [main.experiencia1.1.js](Aula6/main.experiencia1.1.js)


## Pergunta P2.1


Utilizou-se o comando _time_ para obter os resultados listados em seguida.


Tempo obtido quando a dificuldade é 2:

>real    0m0.493s

>user    0m0.445s

>sys     0m0.032s

Tempo obtido quando a dificuldade é 3:

> real    0m0.871s

> user    0m0.906s

> sys     0m0.045s


Tempo obtido quando a dificuldade é 4:
> real    0m2.572s

> user    0m2.638s

> sys     0m0.184s

Tempo obtido quando a dificuldade é 5:

> real    0m24.622s

> user    0m25.721s

> sys     0m1.086s


A partir dos tempos obtidos, facilmente se verifica que a medida que o grau de dificuldade aumenta, o tempo gasto para minerar os blocos aumenta significativamente.  

Isto acontece devido a relação entre o grau de dificuldade e o esforço necessário para a resolução do puzzle da _proof of work_ que permite assegurar que o user gasta um determinado poder computacional (relacionado com o nível de dificuldade selecionado) para criar o novo bloco. 

Esta estratégia encontra-se implementada assegurando que quanto maior for o grau de dificuldade, maior é o número de zeros acrescentado na parte inicial do hash. E por consequência, quanto maior o número de zeros, mais elevado é o tempo e o esforço computacional gastos para gerar o bloco. 

## Pergunta P2.2

1. Ao analisar o código referente a experiência 2.2, identificamos o algoritmo de _proof of work_ como sendo o seguinte:


```
def proof_of_work(last_proof):

  incrementor = last_proof + 1

  while not (incrementor % 9 == 0 and incrementor % last_proof == 0):
    incrementor += 1

  return incrementor
```

Este algoritmo apenas incrementa a variável _incrementor_ desde o valor de _last_proof_ (recebido como parâmetro) até que o mesmo seja divisível por 9 e pelo proof number do bloco anterior. 

2. 

Este algoritmo não deve ser utilizado para minerar os blocos uma vez que depende da prova anterior para realizar os cálculos da prova atual. Além disto, não permite controlar o nível de dificuldade aplicada e os cálculos que incorpora exigem pouco poder computacional para criar o novo bloco.