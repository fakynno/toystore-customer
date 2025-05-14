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

## Pré-requisitos

Antes de executar o projeto, verifique se você tem os seguintes pré-requisitos instalados:

- **Docker**: Para rodar o container do banco de dados e da aplicação.
- **Docker Compose**: Para orquestrar os containers.
- **Maven**: Para compilar e executar a aplicação (se você preferir rodar sem Docker).

## Rodando o Projeto com Docker

### 1. **Baixe os Containers**

Este projeto já está configurado para rodar com Docker. Ele utiliza dois containers: um para o banco de dados PostgreSQL e outro para a aplicação.

### 2. **Requisitos para Rodar o Docker**

Certifique-se de ter o **Docker** e **Docker Compose** instalados na sua máquina. Caso ainda não tenha, [baixe e instale o Docker](https://www.docker.com/get-started).

### 3. **Subir o Banco de Dados PostgreSQL e a Aplicação**

Para rodar o projeto, execute os seguintes passos:

1. Clone o repositório do projeto:

2. Certifique-se de que o **Docker** está rodando.

3. Suba os containers do projeto utilizando o **Docker Compose**:

    ```bash
    docker-compose up --build
    ```

Isso irá subir dois containers:

- O container do banco de dados PostgreSQL, configurado no arquivo `docker-compose.yml`.
- O container da aplicação, utilizando a imagem `evaldofires/toystore-customer-app:1.0.1`.

### 4. **Acessando a Aplicação**

Após o comando acima, o projeto estará disponível em:

- **API da aplicação**: `http://localhost:8080/customer/`
- **Swagger UI**: `http://localhost:8080/customer/swagger-ui/index.html#/` (A documentação interativa da API).

### 5. **Acessando o Banco de Dados PostgreSQL**

Se você precisar acessar o banco de dados diretamente, o PostgreSQL estará disponível na porta `5432` do seu Docker:

- Host: `localhost`
- Porta: `5432`
- Usuário: `postgres`
- Senha: `0`
- Banco de dados: `toystore-customer`

## Rodando o Projeto sem Docker

Se você preferir rodar o projeto sem Docker, siga os passos abaixo:

1. **Compilar o projeto**:

    ```bash
    mvn clean install
    ```

2. **Rodar a aplicação**:

    ```bash
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8080`.

## Imagem Docker

Se você quiser rodar a imagem Docker diretamente, pode usar a imagem hospedada no Docker Hub. Para isso, execute:

```bash
docker pull evaldofires/toystore-customer-app:1.0.1
docker run -p 8080:8080 evaldofires/toystore-customer-app:1.0.1
