# Aula TP - 09/Mar/2021 - Resolução

## Pergunta P1.1
> Não, apenas com este comando não é possível garantir que estamos localizados nos EUA. Isto deve-se ao facto de que o protocolo TOR escolhe, aleatóriamente, um endpoint que é muito provavelmente diferente para cada comunicação efetuada. Assim sendo, a probabilidade de que o endpoint escolhido seja nos EUA sempre é baixa.
>
> No entanto, é possível, modificando os ficheiros de configuração do software TOR, escolher sempre o mesmo _exit node_, de modo a garantir que a nossa localização aparece consistentemente como sendo nos EUA.

## Pergunta P1.2

> 1. O circuito para o site escolhido (http://zqktlwi4fecvo6ri.onion/wiki/index.php/Main_Page, ou The Hidden Wiki) é o seguinte: Hungria, Rússia, e Reino Unido. Depois disso, aparecem três Relays, até que chegamos ao site pretendido.
> 
> 2. Os primeiros três saltos (especificamente, os saltos na Hungria, Rússia e Reino Unido) são os saltos que protegem a nossa identidade e anonimidade na rede TOR. As três Relays pelas quais passa o tráfego antes de chegar ao website pretendido protegem a anonimidade do website ao qual nos estamos a tentar ligar.
>
> 3. O _Rendez-vous Point_ é o nodo no Reino Unido que referimos, pois é o último que faz parte do nosso circuito antes de começar a parte do circuito de Relays.
