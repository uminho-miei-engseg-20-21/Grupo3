# Aula TP - 09/Mar/2021 - Resolução

## Pergunta P1.1
> A resposta está fornecida na pasta [Pergunta1](./Aula3/Pergunta1/).

## Pergunta P2.1
> 1. Executamos o *SSL Server Test* com os websites da [Universidade de Harvard](https://harvard.edu/) e da [Universidade do Cabo Ocidental](https://www.uwc.ac.za). Os resultados da execução do *SSL Server Test* nestes websites foram guardados na pasta [Pergunta2](./Aula3/Pergunta2/).
> 
> 2. O website com o pior rating que avaliamos é o website da Universidade de Harvard. Temos as seguintes observações a fazer sobre o mesmo:
> > 1. O website utiliza o algoritmo RSA com tamanho de chave de 2048 bits, e utiliza o algoritmo SHA-256 como algoritmo de MAC;
> > 2. Para segurança máxima, o website deveria utilizar um algoritmo baseado em curvas elípticas, ou, em alternativa, aumentar o tamanho da chave para, pelo menos, 3072 bits, e deveria também utilizar o SHA-512 ou SHA3-512 como algoritmo de MAC;
> > 3. A necessidade de suportar dispositivos antigos compromete, em alguns aspetos, a sua segurança, pois faz com que seja necessário o website suportar protocolos antigos;
> > 4. Ao contrário do website da Universidade do Cabo Ocidental, a não implementação de HSTS e a falta de suporte para o TLSv1.3 são alguns dos motivos pelos quais o rating do website da Universidade de Harvard é menor do que o rating do website da Universidade de Cabo Ocidental;
> > 5. De qualquer modo, o rating deste website continua a ser bom (A), pelo que podemos dizer que tem boa segurança.
>
> 3. O _DNS CAA_ é um *standard* que permite aos websites especificarem, através de registos de DNS, uma lista de autoridades de certificação que estão autorizadas a emitir certificados para eles próprios. Deste modo impedem que um *browser* aceite um certificado criado por uma entidade de certificação maliciosa ou comprometida. Nenhum dos websites que avaliámos tem suporte a *DNS CAA*.

## Pergunta P3.1
> 
