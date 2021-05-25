# Aula TP - 11/05/2021 - Resolução
## Pergunta 1.1
Neste programa, validação de *input* não é feita corretemente, levando a que o mesmo possua vulnerabilidades.
Uma dessas vulnerabilidades consiste no facto de o utilizador poder ler qualquer ficheiro a que tenha acesso, bastando apenas passar ao programa o seguinte argumento: ```. && cat /etc/passwd```.
Além disto, o utilizador pode executar qualquer programa, utilizando como argumento: ``. && ./programa-malicioso``.

Como uma destas vulnerabilidades permite executar/ler qualquer programa/ficheiro, se o utilizador tivesse permissões *setuid root*
poderia executar/ler qualquer programa/ficheiro ou realizar qualquer operação como *root*. Por exemplo,
ler o ficheiro de *passwords* em ``/etc/shadow``. No entanto, existem alguns sistemas operativos que se conseguem defender contra este tipo de ataques, como por exemplo o sistema operativo ***Kali Linux***. Neste sistema, estes ataques não iriam funcionar.

## Pergunta 1.2
A resolução desta questão encontra-se na pasta [Aula11](Aula11).
