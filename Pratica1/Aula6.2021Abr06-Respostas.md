# Aula TP - 09/Mar/2021 - Resolução

## Pergunta P1.1

De modo a cumprir com os requisitos desta questão, a função _createGenesisBlock_ foi alterada encontrando-se com a configuração apresentada abaixo.


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


> A resposta às perguntas 1.1 e 1.2 foram adicionada ao código fornecido e podem ser consultads em [main.experiencia1.1.js](Aula6/main.experiencia1.1.js)


## Pergunta P2.1


Utilizou-se o comando _time_ para obter os resultados listados em seguida.


Tempo obtido quando a dificuldade é 2:

>real    0m0.493s

>user    0m0.445s

>sys     0m0.032s

Tempo obtido quando a dificuldade é 3:



Tempo obtido quando a dificuldade é 4:


Tempo obtido quando a dificuldade é 5:



A partir dos tempos obtidos, facilmente se verifica que a medida que o grau de dificuldade aumente, o tempo gasto para minerar o clockchain aumenta de forma bastante significativa.  

Isto acontece devido ao aumento do grau de dificuldade referente a resolução do puzzle associado a _proof of work_ de modo a permitir encontrar o mineiro responsavel a publicar o bloco.