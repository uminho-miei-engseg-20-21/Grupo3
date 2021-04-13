# Aula TP - 09/Mar/2021 - Resolução

## Pergunta P1.1
O regulamento europeu RGPD deve ser levado em linha de conta na primeira fase do modelo *Microsoft Security Development Lifecycle*, a fase de requisitos, pois é nesta fase que se definem os requisitos mínimos de segurança que o software em questão deve satisfazer. Esta fase de requisitos é análoga à fase correspondente no modelo em cascata, que deve ter por base a legislação em vigor, como é o caso do RGPD.

## Pergunta P1.3
No nosso projeto de desenvolvimento, que é relativo à implementação de novas funcionalidades relativas ao uso do Cartão de Cidadão e da Chave Móvel Digital na _DSS Web App_ da Comissão Europeia, apenas identificamos, na nossa equipa, o cargo de _Software Developer_, tal como descrito no documento _NIST SP 800-64_. Com uma equipa pequena como a nossa, de quatro elementos, bem como o tempo limitado de desenvolvimento deste projeto, e também com os conhecimentos que possuimos, não conseguimos identificar, na nossa equipa, um membro com as características necessárias para qualquer um dos cargos identificados na _NIST SP 800-64_.

## Pergunta P2.1
As práticas de segurança que escolhemos para avaliar no nosso projeto foram o _Threat Assessment_, os _Security Requirements_, e o _Security Testing_. Os resultados desta avaliação encontram-se em [SAMM.xlsx](Aula5/SAMM.xlsx).

Tento em conta as quatro áreas em que se divide o SAMM, as duas que achamos mais adequadas ao nosso projeto foram as áreas de _Construction_ e _Verification_, pois as rentantes, de um modo geral, necessitam muito mais de um ambiente empresarial do que estas duas. De entre as três práticas de segurança que se enquandram em cada uma destas áreas, as três que foram escolhidas são as mais adequadas (e as mais exequíveis) ao nosso projeto, dada a dimensão do mesmo, e dada a dimensão, bem como os recursos (técnicos e humanos) da equipa de desenvolvimento.

## Pergunta P2.2
Resultando da análise do _score_ obtido pelo preenchimento da ficha relativa à pergunta anterior, e relativamente às práticas de _Threat Assessment_ e _Security Testing_, queremos atingir o nível 1 de maturidade. No entanto, relativamente à prática de _Security Requirements_, como já atingimos o nível 1, queremos avançar para o nível 2.

Caso estes níveis sejam atingidos com sucesso, iremos efetuar uma nova avaliação destas práticas de modo a perceber se será exequível, com o tempo e os recursos de que disponibilizamos, um aumento destes objetivos (para avançar para os níveis seguintes de maturidade).

## Pergunta P2.3

Para atingir os níveis descritos na pergunta anterior, queremos, relativamente ao _Threat Assessment_:
1. Documentar os piores casos possíveis que poderão ser executados contra o nosso _software_;
2. Documentar os tipos de agentes invasores e atacantes, e documentar as suas potenciais motivações para abusar do nosso _software_;

Relativamente ao _Security Testing_, queremos:
1. Documentar, dentro do nosso projeto, os casos de teste que queremos executar, relativos aos nossos requisitos de segurança;
2. Informar devidamente os _stakeholders_ dos resultados destas operações, e também das considerações de segurança relativas à criação do projeto.

Relativamente ao segundo nível dos _Security Requirements_, queremos:
1. Colocar os _stakeholders_ a rever, periodicamente, a maioria dos controlos de acesso ao projeto;
2. Ouvir o feedback vindo de outras atividades de segurança, e implementar esse feedback no desenvolvimento do projeto.
