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

#### Pergunta P4.1
> A Entidade de Certificação EquiSign utiliza o algoritmo RSA (com tamanho de chave de 2048 bits) e o algoritmo SHA-256 para integridade das mensagens.
> 
> Enquanto que os algoritmos utilizados não são atualmente considerados inseguros, para garantir a máxima segurança, recomendamos a sua atualização, atualizando o tamanho de chave para 4096 bits, bem como substituindo o algoritmo SHA-256 pelo algoritmo SHA-3, para a integridade das mensagens.
>
> A saída do comando `openssl x509 -in Equisign.crt -text -noout` é a seguinte:
> 
>     Certificate:
>         Data:
>             Version: 3 (0x2)
>             Serial Number:
>                 11:21:2e:8f:80:0c:93:9c:93:61:5f:84:01:1d:3c:2e:93:44
>             Signature Algorithm: sha256WithRSAEncryption
>             Issuer: C = FR, O = KEYNECTIS, OU = ICS, OU = 0002 478217318, CN = KEYNECTIS ICS QUALIFIED CA
>             Validity
>                 Not Before: Apr  3 14:44:48 2020 GMT
>                 Not After : Apr  3 14:44:48 2023 GMT
>             Subject: C = FR, O = Equisign, organizationIdentifier = NTRFR-799320031, OU = 0002 799320031, CN = Equisign
>             Subject Public Key Info:
>                 Public Key Algorithm: rsaEncryption
>                     RSA Public-Key: (2048 bit)
>                     Modulus:
>                         00:c3:16:1f:6f:d1:5a:ab:e9:95:43:7d:e7:86:59:
>                         39:f1:59:6e:f3:69:b5:66:6b:f1:ce:d3:4f:2e:43:
>                         96:5c:8f:b6:ee:86:53:d3:64:39:f4:4f:c3:b3:cf:
>                         75:15:9c:8b:9d:61:75:ba:b8:cd:c5:83:0e:0a:f0:
>                         ef:73:51:c1:19:c7:b8:d5:f3:2e:0d:d9:9d:71:56:
>                         35:b1:3a:df:48:b2:ee:84:9c:50:89:d7:0b:b3:2a:
>                         2b:2d:34:37:bb:b0:05:e9:60:92:92:a1:fb:e5:7a:
>                         c4:31:65:58:e3:46:f3:6a:1d:01:25:e6:da:56:fc:
>                         4b:85:45:9b:c6:48:ea:20:e2:b4:0d:c5:8a:01:3a:
>                         96:70:07:b7:ca:58:b0:6d:ce:ab:e7:62:4d:2b:6d:
>                         f8:b7:ed:c6:57:d8:a5:07:1a:28:c1:b3:98:47:8b:
>                         56:d5:75:6c:36:12:fb:ec:26:19:26:d1:d9:29:59:
>                         b6:fc:63:bb:8b:b1:a6:4b:ad:be:b9:87:c8:c0:af:
>                         91:d3:fe:78:07:30:04:19:79:79:f7:4e:25:c4:dc:
>                         b2:84:4d:55:e3:8e:8c:c5:56:9a:2c:d6:55:37:df:
>                         6f:f1:14:d4:26:76:9d:5e:93:6d:0c:65:cb:2b:98:
>                         ff:28:44:fe:60:da:7e:d1:26:3b:9b:b0:46:db:83:
>                         e3:13
>                     Exponent: 65537 (0x10001)
>             X509v3 extensions:
>                 X509v3 Key Usage: critical
>                     Digital Signature
>                 X509v3 Extended Key Usage:
>                     1.3.6.1.4.1.311.10.3.12, 1.2.840.113583.1.1.5
>                 X509v3 Certificate Policies:
>                     Policy: 1.3.6.1.4.1.22234.2.9.3.21
>                     CPS: http://www.opentrust.com/PC/
> 
>                 X509v3 Basic Constraints: critical
>                     CA:FALSE
>                 X509v3 CRL Distribution Points:
> 
>                     Full Name:
>                     URI:http://trustcenter-crl.certificat2.com/Keynectis/KEYNECTIS_ICS_QUALIFIED_CA.crl
> 
>                 Authority Information Access:
>                     OCSP - URI:http://ocsp-id.dsf.docusign.net/ics_qualified_ca
>                     CA Issuers - URI:http://crt.dsf.docusign.net/keynectisicsqualifiedca.p7c
> 
>                 qcStatements:
>                     0..0......F..0J.....F..0@0>.8https://pds.dsf.docusign.net/keynectisicsqualifiedca.pdf..EN0......F..0......F...0...+.......0.......I..
>                 1.2.840.113583.1.1.9.1:
>                     0%.... http://tss.dsf.docusign.net/seal
>                 X509v3 Subject Key Identifier:
>                     E2:96:79:74:D8:C7:A8:39:B0:47:BF:51:4F:42:0F:74:55:2F:A7:7F
>                 X509v3 Authority Key Identifier:
>                     keyid:54:97:45:C1:EA:00:C5:45:A8:CD:DB:82:F8:7D:CB:F5:90:41:A0:78
> 
>         Signature Algorithm: sha256WithRSAEncryption
>             86:06:3d:07:4f:e6:50:8f:60:62:af:93:a3:4c:bc:c4:e8:6a:
>             c0:9a:33:1e:5a:b7:6a:ae:f2:43:94:4d:10:15:01:e9:d4:39:
>             84:b6:0c:13:ce:72:7c:92:f1:6d:a6:9e:aa:9c:f1:b3:de:55:
>             75:3a:7b:44:0f:73:c6:fa:88:f7:cd:16:90:3b:a0:66:c4:71:
>             3c:4c:9f:50:34:64:fd:a1:93:f4:f2:b6:12:5b:e6:bd:a2:2a:
>             60:c1:94:ca:27:fb:3f:df:2e:2a:a2:d2:a7:88:36:e4:1b:17:
>             8b:39:56:88:66:92:fb:a4:41:03:d3:8d:60:c8:f9:76:9a:c2:
>             8c:ae:29:67:66:91:56:79:f5:a8:5a:24:49:5d:ed:57:ec:f5:
>             af:f9:ea:db:12:44:93:58:fa:d8:8d:ec:f7:ae:7b:6c:ff:9d:
>             09:57:c5:68:6e:2f:f3:4e:bc:b7:1d:20:ff:5c:00:ea:ef:62:
>             25:25:12:cb:cb:0c:89:8c:c1:67:07:1b:5c:5f:cb:59:7a:f9:
>             28:33:9b:58:2a:c2:5f:15:2f:72:ad:c5:7e:7a:76:c0:07:73:
>             64:99:08:62:c7:1d:f3:e5:34:d0:b6:b5:02:f4:a5:69:0c:ee:
>             04:9b:d9:3a:90:3b:93:fa:30:bd:be:04:e9:24:ab:51:3a:62:
>             e7:c2:9e:31
