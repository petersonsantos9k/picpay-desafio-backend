# PicPay Simplificado — API de Transferências

API REST para cadastro de usuários, depósito em carteira e transferência de dinheiro entre usuários comuns e lojistas.

## Tecnologias

Java 21 · Spring Boot 4.1.2 · Spring Data JPA · H2 · MapStruct · Lombok

## Como rodar

```bash
git clone git@github.com:PetersonSantos9K/picpay-desafio-backend.git
cd picpay-desafio-backend
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Funcionalidades

- Cadastro de usuários comuns e lojistas
- Depósito em carteira
- Consulta de usuário por ID
- Transferência com validação de saldo
- Autorização externa antes da transferência
- Notificação externa após a transferência
