FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src

# Roda os testes, gera o relatório HTML e sempre encerra com 0
# para que o nginx suba automaticamente via depends_on.
CMD ["sh", "-c", "mvn test; mvn allure:report -q; exit 0"]
