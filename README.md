# Toystore Customer - Projeto de Microserviço

Este projeto é um microserviço de gerenciamento de clientes para a Toystore. Ele foi desenvolvido com **Spring Boot** e conta com a utilização de **Docker** para facilitar o ambiente de desenvolvimento e execução.

## Funcionalidades

- Cadastro de clientes
- Acesso à documentação via Swagger
- Banco de dados PostgreSQL

## Tecnologias

- Java 21 (Amazon Corretto)
- Spring Boot
- Docker
- PostgreSQL
- Swagger

## Rodando o Projeto com Docker

docker run -d --name toystore-customer-app -p 8080:8080 --network toystorerede -e SPRING_PROFILES_ACTIVE=docker toystore-customer-app

###  **Acessando a Aplicação**

Após o comando acima, o projeto estará disponível em:

- **API da aplicação**: `http://localhost:8080/customer/`
- **Swagger UI**: `http://localhost:8080/customer/swagger-ui/index.html#/` (A documentação interativa da API).




## Imagem Docker

Se você quiser rodar a imagem Docker diretamente, pode usar a imagem hospedada no Docker Hub. Para isso, execute:

```bash
docker pull evaldofires/toystore-customer-app:1.0.2
docker run -p 8080:8080 evaldofires/toystore-customer-app:1.0.2
