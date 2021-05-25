# Aula TP - 11/05/2021 - Resolução
## Pergunta 1.1
Neste programa não é feita corretemente a validação de input levando a que o mesmo possua vulnerabilidades.
Uma delas é que podemos ler qualquer ficheiro a que o utilizador tem acesso, basta dar como argumento ao 
programa o seguinte: ". && cat /etc/passwd".
Além disto podemos executar qualquer programa, utilizando como argumento: ". && ./programa-malicioso".
Como uma destas vulnerabilidades permite executar/ler qualquer programa/ficheiro, se tivesse permissões setuid root
iria ser possivel executar/ler qualquer programa/ficheiro ou realizar qualquer operação como root, por exemplo
ler o ficheiro de passwords em /etc/shadow. No entanto existem alguns sistemas operativos que se conseguem defender contra este tipo de ataques, por exemplo isto não iria funciona num sistema operativo Kali Linux.

## Pergunta 1.2
A resolução encontra-se na pasta Aula11
