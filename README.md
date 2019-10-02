# Distribuição de participação dos lucros

O projeto tem como objetivo calcular a participação dos lucros de cada funcionário de uma empresa.
O cáculo é feito seguindo alguns critérios como tempo de casa, área de atuação na empresa e faixa salarial.

### Pré requisitos

Para rodar essa aplicação, você necessitar ter instalado [Java 8](https://www.java.com/pt_BR/) ou superior.
Caso necessite fazer o build do código fonte, será necessário também ter o [mvn](https://maven.apache.org/download.cgi) instalado.

### Para fazer build do código fonte

rode o camando abaixo dentro do diretório do projeto.

```

mvn package

```

### Para executar o programa

Para executar o programa basta rodar o comando abaixo. A aplicação rodará **localmente** e estará disponível
na porta **8080**.

```

java -jar distribuicaopl-0.0.1-SNAPSHOT.jar


```

### Rodar testes unitários

Os testes unitários podem ser executados tanto dentro de uma IDE quanto usando 
o mvn:

```

mvn test

```

### Documentação API SWAGGER

Você pode encontrar [aqui](https://app.swaggerhub.com/apis/Junger/distribuicao-lucros/1.0.0#/) 
a documentação da api. Lá você consegue ver exemplos das requisições e de seus responses.

### Usando a aplicação

1. A aplicação precisa que os funcionários estejam cadastrados, para isso realize o cadastro

POST - /rest/v1/funcionario
````json

[
    {
        "matricula": "0009968",
        "nome": "Victor Wilson",
        "area": "Diretoria",
        "cargo": "Diretor Financeiro",
        "salario_bruto": 12696.20,
        "data_de_admissao": "2012-01-05"
    },
    {
        "matricula": "0004468",
        "nome": "Flossie Wilson",
        "area": "Contabilidade",
        "cargo": "Auxiliar de Contabilidade",
        "salario_bruto": 1396.52,
        "data_de_admissao": "2015-01-05"
    }
]

````

 2 . Calcule a participação que cada funcionário receberá
 dado um valor máximo a ser distribuído
 
 POST - /rest/v1/funcionario/participacao
 
 ````json

{
	"valor_distribuicao": 4257411.58
}

````

3 . Caso precise, você pode apagar todos os funcionários e recomeçar seus testes cadastrando novos funcionários.

DELETE - /rest/v1/funcionario

### Collection do Postman

vou deixar disponibilizado uma collection com as requisições REST usadas na aplicação

````
distribuicao_pl.postman_collection.json
````



