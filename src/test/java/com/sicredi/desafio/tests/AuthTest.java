package com.sicredi.desafio.tests;

import com.sicredi.desafio.models.request.LoginRequest;
import com.sicredi.desafio.models.response.LoginResponse;
import com.sicredi.desafio.models.response.ProductsResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Autenticação")
class AuthTest extends BaseTest {

    // --- POST /auth/login ---

    @Test
    @Tag("smoke")
    @DisplayName("POST /auth/login com credenciais válidas deve retornar token JWT")
    @Description("Verifica que o login com usuário válido retorna HTTP 200 com token e refreshToken")
    @Story("Login")
    void shouldReturnTokenOnValidLogin() {
        LoginResponse response = authService.loginAndExtract(
                validUser.getUsername(),
                validUser.getPassword()
        );

        assertThat("Token não deve ser nulo", response.getAccessToken(), not(emptyOrNullString()));
        assertThat("RefreshToken não deve ser nulo", response.getRefreshToken(), not(emptyOrNullString()));
        assertThat("Username na resposta deve coincidir", response.getUsername(), equalTo(validUser.getUsername()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("POST /auth/login deve retornar dados do usuário autenticado")
    @Description("Verifica que a resposta de login contém id, email, firstName e lastName")
    @Story("Login")
    void shouldReturnUserDataOnValidLogin() {
        LoginResponse response = authService.loginAndExtract(
                validUser.getUsername(),
                validUser.getPassword()
        );

        assertThat("Id do usuário deve ser positivo", response.getId(), greaterThan(0));
        assertThat("Email não deve ser nulo", response.getEmail(), not(emptyOrNullString()));
        assertThat("FirstName não deve ser nulo", response.getFirstName(), not(emptyOrNullString()));
        assertThat("LastName não deve ser nulo", response.getLastName(), not(emptyOrNullString()));
    }

    @Test
    @Tag("negative")
    @DisplayName("POST /auth/login com credenciais inválidas deve retornar HTTP 400")
    @Description("Verifica que login com senha incorreta retorna erro 400 com mensagem de credenciais inválidas")
    @Story("Login")
    void shouldReturn400OnInvalidCredentials() {
        authService.login(LoginRequest.builder()
                .username("usuario_invalido")
                .password("senha_invalida")
                .build())
                .then()
                .statusCode(400)
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @Tag("negative")
    @DisplayName("POST /auth/login com body vazio deve retornar HTTP 400")
    @Description("Verifica que login sem credenciais retorna erro 400")
    @Story("Login")
    void shouldReturn400OnEmptyCredentials() {
        authService.login(LoginRequest.builder()
                .username("")
                .password("")
                .build())
                .then()
                .statusCode(400);
    }

    // --- GET /auth/products ---

    @Test
    @Tag("smoke")
    @DisplayName("GET /auth/products com token válido deve retornar lista de produtos")
    @Description("Verifica que o endpoint autenticado retorna HTTP 200 com lista de produtos")
    @Story("Produtos Autenticados")
    void shouldReturnProductsWithValidToken() {
        ProductsResponse response = authService.getAuthProductsAndExtract(authToken);

        assertThat("Lista de produtos não deve ser vazia", response.getProducts(), not(empty()));
        assertThat("Total deve ser maior que zero", response.getTotal(), greaterThan(0));
    }

    @Test
    @Tag("negative")
    @DisplayName("GET /auth/products sem token deve retornar HTTP 401 ou 403")
    @Description("Verifica que o acesso sem Authorization header é negado com erro de autenticação")
    @Story("Produtos Autenticados")
    void shouldReturn401Or403WithoutToken() {
        int status = authService.getAuthProductsWithoutToken()
                .then()
                .extract()
                .statusCode();

        assertThat("Status deve ser 401 ou 403", status, anyOf(equalTo(401), equalTo(403)));
    }

    @Test
    @Tag("negative")
    @DisplayName("GET /auth/products com token inválido deve retornar HTTP 401")
    @Description("Verifica que token inválido resulta em HTTP 401 com mensagem de token expirado/inválido")
    @Story("Produtos Autenticados")
    void shouldReturn401WithInvalidToken() {
        authService.getAuthProducts("token_invalido_123")
                .then()
                .statusCode(401)
                .body("message", not(emptyOrNullString()));
    }
}
