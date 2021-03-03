# Aula TP - 02/Mar/2021 - Resolução

## Exercícios

### 1\. Números aleatórios/pseudoaleatórios

#### Pergunta P1.1
> Os comandos que executamos, cujo *target* era o ficheiro */dev/random*, especialmente o comando que obtem 1024 bytes, demora bastante tempo a executar. Após alguma pesquisa, ficamos a perceber que isto acontece devido ao facto de que o gerador pseudoaleatório */dev/random* utiliza fontes de entropia vindas do sistema, como por exemplo, atividade da rede, movimentos do rato do utilizador, entradas do teclado, atividade do sistema de ficheiros, etc. Quando não existe atividade suficiente no sistema para gerar números suficientemente aleatórios, dizemos que o sistema não tem entropia suficiente para gerar os mesmos. O nível de entropia disponível no sistema pode ser consultado com o seguinte comando:
>
> - `cat /proc/sys/kernel/random/entropy_avail`
> 
> A entropia é apenas uma medida de "quanta aleatoriedade" existe atualmente disponível do sistema, e, portanto, pode também ser vista como uma medida da segurança dos números aleatórios gerados.
>
> O último comando, `head -c 1024 /dev/urandom | openssl enc -base64`, executa sempre em tempo útil. Isto deve-se pois funciona de um modo diferente dos anteriores: enquanto que o */dev/random* apenas utiliza a entropia disponível do sistema para gerar números aleatórios, o */dev/urandom* apenas utiliza a entropia do sistema para inicializar um algoritmo de geração de números pseudo-aleatórios. Após essa inicialização, o algoritmo é utilizado para gerar novos números aleatórios, sem ser necessário esperar que o sistema tenha entropia suficiente para gerar os mesmos.

#### Pergunta P1.2
> Após instalar o referido software (e inicializar o serviço), foi possível verificar que o nível de entropia do sistema imediatamente aumentou substancialmente (de ~40 para ~2400). Ao executar os comandos, verificamos também que nenhum dos dois bloqueou ao executar. Enquanto que isto é esperado do segundo comando (pois utiliza o */dev/urandom*), o primeiro comando executou rapidamente devido ao facto de que o software *haveged* utiliza um algoritmo gerador de números aleatórios pseudo-aleatório que substitui o método utilizado, por defeito, pelo */dev/random*. Assim, após inicializado, este algoritmo é utilizado para gerar os números pseudo-aleatórios, em vez de esperar por entropia suficiente vinda do sistema.

#### Pergunta P2.1
> A. Para dividir o segredo em 8 partes, com um quorum de 5 necessário para reconstruir o segredo, executamos os seguintes comandos:
> 
> - `openssl genrsa -aes128 -out private-key.pem 1024`, para gerar a chave privada que assinará as várias componentes do segredo;
> - `python3 createSharedSecret-app.py 8 5 1 private-key.pem > componentes.txt`, para dividir o segredo e guardar as várias componentes no ficheiro `componentes.txt`.
> 

> B. A diferença entre o script *recoverSecretFromComponents-app.py* e o script *recoverSecretFromAllComponents-app.py* é que o segundo script requer a unanimidade de todos os membros pelos quais o segredo foi distribuido, ao contrário do primeiro, que apenas requer um quorum. Isto pode ser necessário, por exemplo, se for necessário alterar o próprio segredo: nós prevemos que, nestas situações, seja necessária a concordância de todos os membros do grupo pelo qual as várias componentes do segredo estão divididas.
