## Desafio: implementar uma página com um formulário de busca
- Requisitos:
	 - Ao digitar no mínimo 4 caracteres as sugestões de busca devem aparecer
	 - Se as sugestões para completarem o termo digitado não existirem, então nenhuma sugestão deve ser exibida
	 - O backend deve retornar no máximo 20 sugestões (Apenas 10 são exibidas, e as demais o usuário precisa scrollar)
	 - as sugestões devem manter em negrito a parte do termo que corresponde ao que já foi digitado
	 - ao passar o mouse por alguma sugestão ou tocar, o elemento deve ser destacado
	 - o usuário pode continuar digitando, e as sugestões vão mudando dinamicamente
	 - as sugestões devem aparecer conforme a velocidade de digitação do usuário
	 - ao clicar na sugestão, o campo de busca deve ser atualizado com o texto da sugestão

## 1ª Decisão: Implementação do back-end em Java com Spring Boot

Escolhi utilizar Java como linguagem principal do back-end por dois motivos principais:

1. É a linguagem que utilizo no meu dia a dia de trabalho, o que me dá mais fluidez para estruturar o código, testar rapidamente e me sentir mais confortável.
    
2. Java é uma linguagem concisa para aplicações de back-end, especialmente quando associada ao ecossistema do Spring.
    

Optei por usar Spring Boot por sua capacidade de:

- Gerenciar dependências;
    
- Reduzir a necessidade de configuração manual;
    
- Integrar facilmente com ferramentas como GraphQL (via Spring for GraphQL);
    
- Ser compatível com Docker.
    
### Implementação do MVP - Backend

**1º Passo: Armazenamento das sugestões**

- Optei por utilizar a API para entregar os dados de um arquivo JSON, uma vez que essa opção me pouparia tempo e garantiria, de maneira efetiva, que o código pudesse ser rodado em qualquer máquina (funciona bem com Docker).
    
- Para popular o JSON, fui até o Google Trends e fiz uma consulta sobre o que estava sendo mais procurado no Brasil nos últimos dias. Descobri que o Mundial de Clubes estava em alta. A partir disso, pedi que uma inteligência artificial populasse o JSON com sugestões em torno desse tema.
    
- A princípio, coloquei apenas 100 sugestões. Após os testes, posso aumentar esse número para garantir a efetividade das sugestões.
    

**2º Passo: Estruturação básica da API**

- Comecei criando a estrutura básica do meu schema GraphQL, definindo como o front-end poderia consultar dados do back-end.
    
- Eu ainda não conhecia bem a linguagem de consulta, mas foi simples entender como implementar nesse caso: basicamente, era só fazer a busca de uma string. Criei uma query chamada `suggestions`, que recebe uma palavra-chave e devolve uma lista de strings (as sugestões).
    
- Depois disso, criei a classe da API que responde à query do GraphQL (resolver). Para isso, eu precisava:
    
    - Ler os dados do arquivo passado como parâmetro;
        
    - Retornar no máximo 20 sugestões que contenham o termo digitado;
        
    - Garantir que, para ter uma resposta do GraphQL, no mínimo 4 caracteres sejam digitados.
        

Tecnologias e bibliotecas utilizadas:

- Biblioteca Jackson, para ler o JSON;
    
- Algumas classes nativas do Java, para ler e manipular os dados.
    

**3º Passo: Primeiro teste do MVP**

- Utilizei o Postman para checar se a API estava funcionando como deveria. Fiz a busca abaixo e recebi apenas um resultado, o que me fez perceber que o tamanho do JSON estava menor do que o ideal. Mas, até então, tudo estava funcionando corretamente.
```
{

  "query": "{ suggestions(term: \"fute\") }"

}

 Resposta: {"data":{"suggestions":["Santos Futebol Clube"]}}
```

## Implementação do MVP - Frontend

Nunca tinha trabalhado com React antes, o primeiro passo foi entender como iniciar um projeto e como organizar os arquivos básicos. Durante esse processo, descobri o **Vite**, uma ferramenta de build que facilita bastante o setup inicial do React.

Com o Vite instalado, criei a estrutura inicial do projeto e configurei o Apollo Client, que seria responsável por fazer a ponte entre o front-end e o GraphQL do back-end.

**1º Passo: Estrutura inicial do projeto React**

- Criei a `main.jsx`, que é o ponto de entrada da aplicação, e nela configurei o ApolloProvider com a URL do back-end.
    
- Em seguida, criei o arquivo `App.jsx`, com uma estrutura bem básica: apenas um título, um campo de input e uma lista para exibir sugestões.
    
- Neste primeiro momento, não incluí botão de busca nem nenhuma estilização. O foco era validar a comunicação com o back-end e fazer o autocomplete funcionar.
    

**2º Passo: Dificuldades iniciais**

- A princípio, o autocomplete não funcionava. Passei algum tempo tentando entender o motivo.
    
- Após algumas pesquisas, percebi que o problema estava na comunicação entre o front e o back-end: eu estava chamando o servidor incorretamente e não havia configurado o CORS (Cross-Origin Resource Sharing) no back-end.
    
- Para resolver isso, adicionei uma configuração de CORS no Spring Boot, permitindo chamadas HTTP vindas do `localhost:5173`, que é onde o Vite roda por padrão durante o desenvolvimento.

**3º Passo: Corrigindo a query no front-end**

- Mesmo após resolver o problema do CORS, o autocomplete ainda não funcionava como esperado.
    
- Depois de várias tentativas, descobri que a query GraphQL estava mal formatada no front faltavam aspas em algumas partes, o que quebrava a requisição sem dar um erro claro.
    
- Além disso, percebi que a lista de sugestões estava sendo preenchida com strings vazias, o que fazia com que aparecesse apenas um bullet point sem texto na tela.
    

**4º Passo: Resultado final**

- Após corrigir a formatação da query e filtrar as strings vazias na resposta, o MVP passou a funcionar corretamente.
    
- Agora, ao digitar no input, o usuário recebe sugestões com base nas palavras digitadas, consultadas dinamicamente via GraphQL no back-end.
    
## Uso do Docker para deixar tudo pronto pro `docker compose up`

Bom, como o MVP já tava funcionando bem, fui pra etapa de configurar o Docker. Essa foi, de longe, a parte mais simples de todo o projeto.

- Criei o `Dockerfile` para o backend em Spring Boot;
    
- Criei o `Dockerfile` para o frontend em React;
    
- Montei o `docker-compose.yml` com os dois serviços, garantindo que o backend subisse primeiro e que as portas estivessem expostas corretamente.
    

Com isso, bastava rodar `docker compose up` e todo o sistema já estava funcionando.
## Reestruturação da geração de sugestões e melhoria do JSON

Essa foi, sem dúvida, a parte mais trabalhosa e dificil de todo o projeto. Passei várias horas reestruturando a lógica de geração das frases, ajustando o JSON e quebrando a cabeça com erros inesperados. Abaixo explico as etapas principais desse processo.

### 1º Passo: Crescimento do JSON e primeiros problemas

Comecei aumentando o tamanho do meu JSON para cerca de 3000 sugestões, acreditando que isso daria conta do recado. Mas percebi rápido que esse formato estático não funcionava bem. Se o usuário buscasse algo que não estivesse no JSON, nada era retornado.

Isso fez com que eu mudasse completamente a estratégia — em vez de depender de frases prontas, comecei a montar frases dinamicamente com base em categorias.

### 2º Passo: Sugestões que não seguiam o termo digitado

Mesmo com o JSON maior, outro problema surgiu: ao digitar "jogo", as sugestões nem sempre começavam com esse termo. Isso acontecia porque o sistema ainda era engessado e tratava tudo de forma genérica. Eu resolvi isso parcialmente, mas percebi que ainda faltava inteligência na estrutura das frases — elas não pareciam naturais e, às vezes, nem faziam sentido.

### 3ºPasso: Reestruturação completa do JSON e da lógica da API

A partir daí, decidi fazer uma reformulação completa. Separei o JSON em **categorias específicas**: `sujeitos`, `ações`, `contextos`, `tempos`, `conectivos` e `modelosDeFrase`. Isso me deu liberdade para montar frases novas, sem depender de combinações pré-existentes.

Essa parte evidenciou alguns pontos fracos da minha API:

- A estrutura era muito básica, baseada só em leitura + montagem de frases com `replace()`;
    
- A cada nova lógica que eu tentava implementar, o programa quebrava;
    
- Tinha frases repetidas, frases sem sentido e erros como chaves não preenchidas (`{acao}` etc.);
    
- Quando o usuário dava um espaço, as sugestões sumiam, porque a lógica não lidava bem com strings parciais ou vazias.
    
### 4º Passo: Organização das responsabilidades

Depois de muito testar, vi que eu estava tentando resolver tudo num lugar só. E isso me travava. A solução foi dividir a lógica em partes menores e bem definidas:

- Uma classe para ler e carregar o JSON;
    
- Outra para armazenar as diferentes categorias;
    
- Outra responsável por montar as frases dinamicamente;
    
- Um serviço que organiza os dados e gera as frases com base no que o usuário digitou.
    

Essa divisão de responsabilidades deixou tudo mais fácil de manter e evoluir. A cada novo ajuste, eu mexia em um ponto específico — e não quebrava o sistema inteiro.
### 5º Passo: Geração dinâmica e inteligente das frases

Com a base bem estruturada, reescrevi o serviço de sugestões com foco em 3 pontos:

- Relevância: se o usuário digita “corin”, o sistema entende que está buscando por “Corinthians” e usa esse termo como base para montar as frases.
    
- Aleatoriedade controlada: os modelos de frase são preenchidos com termos aleatórios das categorias, mas mantendo coerência (evitando frases sem sentido como “marcar gol no vestiário”).


## Melhorias e incrementação do MVP – Frontend

Depois de garantir que tudo funcionava bem no básico, comecei a focar em melhorar o frontend. O design inicial não teve muito planejamento estético, o foco era mais em fazer funcionar.

Essa parte aconteceu antes da grande melhoria no JSON.
### 1º Passo: Layout parecido com o exemplo dado

Usei como referência um exemplo de tela e montei uma estrutura parecida:

- Uma caixa de busca no centro da tela;
    
- Um botão azul para buscar;
    
- Uma lista de sugestões logo abaixo.
    

### 2º Passo: Ajuste de sugestões e destaque do termo digitado

Foi aí que começaram a aparecer uns problemas mais visíveis.

- A primeira versão mostrava 4 sugestões visíveis por vez, e o restante só aparecia se o usuário rolasse a lista (scroll). Isso prejudicava a usabilidade.
    
- Além disso, o termo digitado não era destacado nas sugestões, o que dificultava a leitura rápida.
    

Durante esses testes, percebi que o backend estava retornando frases demais. Ao digitar algo como "final", várias sugestões apareciam, inclusive algumas que nem começavam com "final". Isso era reflexo do modelo antigo, baseado em frases estáticas.

Pra resolver:

- No backend, limitei o número de sugestões retornadas para no máximo 20;
    
- No frontend, ajustei o CSS para definir uma altura fixa nas sugestões e exibir um número controlado de itens;
    
- Também modifiquei a renderização das sugestões para que o termo buscado aparecesse em negrito dentro da frase.
    

---

### 3º Passo: Ajuste dinâmico da altura das sugestões

No começo, defini a altura da área de sugestões de forma fixa, o que gerava outros problemas:

- Às vezes só apareciam 4 sugestões;
    
- Outras vezes 7;
    
- Em alguns casos, o conteúdo ficava cortado ou passava da borda.
    

A solução foi tornar esse espaço dinâmico, ou seja, a altura da caixa de sugestões agora se adapta de acordo com a quantidade de frases retornadas. Isso deixou a experiência bem mais fluida e sem cortes visuais.

## Bugs
Ao tentar deixar a busca mais completa, acabei criando uma limitação e sigo buscando (mesmo após a entrega do desafio/teste) entender o por que dela. Ao estruturar as sugestões, criei um bug que o auto complete apenas funciona quando a busca é iniciada a partir de um sujeito do json (nomes de jogadores como neymar, lionel messi ou times como corinthians, palmeiras, os que estão presentes nos sujeitos do JSON)

Já alterei todo tipo de coisa para eliminar essa limitação/bug, e ainda não descobri como. Infelizmente, percebi que o meu código funciona bem quando a pesquisa se inicia a partir de um sujeito definido, mas não tão bem quando ele foge disso.

a alternatiiva que encontrei foi adicionar outros sujeitos ao JSON até então, mas sigo buscando uma alternativa melhor

## Conclusão

Para concluir o projeto, realizei algumas estilizações no front-end para melhorar a aparência e usabilidade da interface.

Caso tivesse mais tempo, teria aprimorado o back-end para implementar uma busca semântica mais eficiente e também refinaria o design do front-end, deixando a interface mais atrativa e responsiva.
