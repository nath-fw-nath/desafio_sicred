package com.sicredi.desafio.tests;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.services.TestService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@Feature("Status da Aplicação")
class TestEndpointTest {

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestService();
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /test deve retornar status 200 com status ok e método GET")
    @Description("Verifica que o endpoint de status retorna HTTP 200 com body {status: ok, method: GET}")
    @Story("Verificar saúde da aplicação")
    void shouldReturnApplicationStatusOk() {
        testService.getStatus()
                .then()
                .spec(ApiConfig.responseSpec(200))
                .body("status", equalTo("ok"))
                .body("method", equalTo("GET"));
    }
}
