package com.sicredi.desafio.tests;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.models.response.User;
import com.sicredi.desafio.models.response.UsersResponse;
import com.sicredi.desafio.services.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Usuários")
class UserTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /users deve retornar HTTP 200 com lista de usuários")
    @Description("Verifica que o endpoint retorna status 200 com lista não vazia de usuários")
    @Story("Listar usuários")
    void shouldReturnUsersListWithSuccess() {
        userService.getUsers()
                .then()
                .spec(ApiConfig.responseSpec(200))
                .body("users", not(empty()))
                .body("total", greaterThan(0));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /users deve retornar usuários com credenciais de acesso")
    @Description("Verifica que cada usuário contém username e password necessários para autenticação")
    @Story("Listar usuários")
    void shouldReturnUsersWithLoginCredentials() {
        UsersResponse response = userService.getUsersAndExtract();

        assertThat("Lista de usuários não deve ser vazia", response.getUsers(), not(empty()));

        User firstUser = response.getUsers().get(0);
        assertThat("Username não deve ser nulo ou vazio", firstUser.getUsername(), not(emptyOrNullString()));
        assertThat("Password não deve ser nulo ou vazio", firstUser.getPassword(), not(emptyOrNullString()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /users deve retornar metadados de paginação")
    @Description("Verifica que a resposta contém os campos total, skip e limit")
    @Story("Listar usuários")
    void shouldReturnPaginationMetadata() {
        UsersResponse response = userService.getUsersAndExtract();

        assertThat("Total deve ser maior que zero", response.getTotal(), greaterThan(0));
        assertThat("Skip deve ser zero por padrão", response.getSkip(), equalTo(0));
        assertThat("Limit deve ser maior que zero", response.getLimit(), greaterThan(0));
    }
}
