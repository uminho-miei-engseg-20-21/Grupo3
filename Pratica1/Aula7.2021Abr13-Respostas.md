# Aula TP - 06/Abr/2021 - Resolução

## Pergunta P1.1.1

A primeira fraqueza da lista "The CWE Top 25" é referente a vulnerabilidades que podem ocorrer devido a uma má validação
de inputs em geração de páginas web. Este tipo de vulnerabilidades são normalmente chamadas de *Cross-site scripting (XSS)* e podem
ocorrer quando dados não confiáveis podem ser entregues a uma aplicação web (normalmente introduzidos no HTTP request) e a mesma gera dinamicamente uma página web utilizando estes dados. Caso a aplicação web não previna que os dados não contenham certos comandos que possam ser executado por um web browser, 
por exemplo código JavaScript ou tags e atributos HTML, é possível que uma vítima que aceda a uma página desta aplicação web (que foi gerada usando
dados não confiáveis e não validados) possa estar inadvertidamente a executar código malicioso na sua máquina. Existem três tipos de ataques:
  - ***Reflected XSS*** - O código malicioso vem diretamente do servidor, normalmente numa resposta a um HTTP request. Tipicamente acontece quando um atacante consegue que uma vítima envie dados perigosos (código malicioso) a uma aplicação web vulnerável que são depois "
refletidos" para a vítima e executados pelo browser. O modo mais comum de os atacantes conseguirem fazer isto é enviando, de alguma forma, um URL para a vítima onde o código malicioso seja um parâmetro no mesmo.
Por exemplo, a aplicação web que gera o website https://insecure-website.com/ está vulnerável a ataques deste tipo. Esta aplicação simplesmente
recebe dados na forma de um HTTP request e imediatamente envia estes dados para o utilizador. Assim, ao aceder ao URL 
https://insecure-website.com/query?query=Some+Query será enviado ao utilizador ```<p>Query: Some Query</p>```. Como a aplicação não valida os dados recebidos, um atacante poderá construir o seguinte URL: [https://insecure-website.com/query?query=<script>/\*Código Malicioso\*/</script>], quando uma vítima visitar este url irá ser enviada a seguinte resposta do servidor: ```<p>Query: <script>/* Código Malicioso */</script></p>```. O browser da
vítima, ao tentar interpretar a resposta do servidor, irá executar o script com código malicioso criado pelo atacante.
 - ***Stored XSS*** - Acontece quando uma aplicação web recebe dados de fontes inseguras e depois os inclui em respostas HTTP de maneira
não segura. Por exemplo, uma aplicação web vulnerável a este tipo de ataque poderá ser um web site que contenha uma secção com comentários. Quando é feito o pedido da página, o servidor consulta na sua base de dados os comentários existentes e estes são enviados ao utilizador da seguinte
forma: ```<p>Hello, this is my comment!</p> <p>Hello, this is my comment 2!</p>```. Um atacante poderá então introduzir um comentário que contenha código
malicioso, como por exemplo : ```<script>/* Código Malicioso */</script>```. Quando uma vítima tentar aceder à página web novamente irá ser enviado pelo servidor
web o seguinte: ```<p>Hello, this is my comment!</p> <p>Hello, this is my comment 2!</p> <p><script>/* Código Malicioso */</script></p>```. O web browser
da vítima ao tentar ler a resposta do servidor irá executar o código malicioso introduzido pelo atacante.
- ***DOM-based XSS*** - Este tipo de ataque acontece quando uma aplicação web contém código JavaScript que corre do lado do cliente e usa dados
de uma fonte não segura de uma forma não segura. Por exemplo, vamos supor que uma aplicação web utiliza, no lado do cliente, o código apresentado abaixo, que
lê o valor de um campo de input e escreve esse mesmo valor num elemento HTML:
    ```javascript
    var search = document.getElementById('search').value;
    var results = document.getElementById('results');
    results.innerHTML = 'You searched for: ' + search;
    ```
    Se um atacante puder controlar o valor que escreve no campo de input,facilmente poderá utilizar um valor malicioso. Por exemplo, pode inserir:
    ```<img src=1 onerror='/* Código Malicioso */'> ```
    Isto irá fazer com que o resultado do elemento ```results``` seja: ```You searched for: <img src=1 onerror='/* Código Malicioso */'>```.
    Ao ler isto, o browser irá executar o código malicioso.

Como podemos ver, este tipo de fraquezas podem ocorrer em qualquer tipo de linguagem que permita construir uma aplicação web. No entanto, o código
malicioso que é executado, é maioritariamente escrito em JavaScript. As tecnologias onde esta fraqueza é mais prevalente são as tecnologias web.
Devido à natureza da fraqueza, a probabilidade de a mesma ser explorada é alta e, como o atacante pode executar o código que pretender na máquina
da vítima, existem várias consequências. As mais comuns são: 
- o furto de cookies ou a manipulação das mesmas - É roubada a identidade de um utilizador e podem ser feitos pedidos ao servidor
que os interpretará como vindos de um utilizador válido, podendo comprometer informação confidencial e também o controlo de acesso.
- execução de código malicioso - Como explicado acima, é possível a execução de código arbitrário criado por um atacante. Isto pode implicar
desde um simples redirecionamento para outro website à instalação de outros programas maliciosos como *Trojan horses*. Pode ser alterada a interface da página
web ou pode ser feita uma leitura de ficheiros do utilizador. Desta forma é comprometida a confidencialidade, a integridade e disponibilidade de uma aplicação web.

A segunda fraqueza da lista corresponde à escrita fora do limite do buffer. Isto acontece quando, na fase de implementação, são escritos dados 
antes ou depois dos limites de um determinado buffer, o que pode resultar na corrupção de dados, a uma paragem da aplicação ou à execução de código não autorizado, comprometendo assim a integridade e a disponibilidade do software. Sendo esta fraqueza relativa a escritas em memória, as linguagens mais vulneráveis a este tipo de fraquezas são C, C++ e Assembly.

## Pergunta P1.1.2
A fraqueza a ser analisada pelo nosso grupo será a fraqueza número 5 do ranking. Esta corresponde à restrição imprópria de operações dentro dos limites de um
buffer de memória. As linguagens onde este tipo de fraqueza é mais prevalente são C, C++ e Assembly. Nestas linguagens é possível o acesso direto a endereços de memória que não são endereços válidos para o buffer de memória que está a ser referenciado. Assim, podem ocorrer operações de escrita e 
leitura em localizações de memória que podem estar associadas a outras variavéis, estruturas de dados ou dados internos do programa. Desta forma um 
atacante poderá executar código arbitário, alterar a estrutura de controlo de um programa, ler informação confidencial ou causar a paragem de um programa
comprometendo assim a integridade, confidencialidade e disponibilidade do mesmo.
Podemos exemplificar esta fraqueza recorrendo ao seguinte exemplo em C:
```C
int main (){
char *names[] = {"tomas","tobias","tumias","naomias"};
int index = AskUserForIndex(); // Asks the user for any number
printf("Selected name: %s\n", names[index-1])
}
``` 
Aqui, o programa pede ao utilizador um índice de um array de nomes ("names") quen não é validado se o índice pertencer ao array. 
Um atacante poderá fornecer um índice maior do que o tamanho do array resultando numa leitura fora dos limites do buffer de memória.
No CVE-2005-1513 é observada uma vulnerabilidade que contém uma fraqueza deste tipo. Uma variável da aplicação ```qmail``` pode dar overflow emsistemas de 64 bits com acesso a uma grande quantidade de memória virtual. Desta forma, atacantes remotos podem realizar um ataque de negação de serviço e possivelmente executar código arbitrário utilizando um pedido SMTP de tamanho grande.

## Pergunta P1.2.1
Estima-se que qualquer pacote de software tenha em média 5 a 50 bugs por cada 1.000 SLOC *(Source Lines Of Code)*. No website fornecido no enunciado
é estimado que o Linux 3.1 tenha 15 milhões de SLOC , o Facebook 62 milhões de SLOC e o software de automóveis 100 milhões de SLOC, o que
corresponde a uma estimativa de 75000 a 750000 de bugs no Linux 3.1, 310000 a 3100000 de bugs no Facebook e 500000 a 5000000 de bugs no software de
automóveis.

## Pergunta P1.2.2

(???)

## Pergunta P1.3

## Pergunta P1.4
Uma vulnerabilidade de dia-zero, ao contrário de uma vulnerabilidade normal, é uma vulnerabilidade que não é conhecida pelo público em geral (incluindo o vendedor do software vulnerável). Até esta vulnerabilidade ser detectada e mitigada, atacantes podem explorá-la realizando ataques designados por
ataques dia-zero. Estes são normalmente devastadores pois permitem atacar sistemas mesmo que estes sejam administrados por equipas de segurança competentes.
