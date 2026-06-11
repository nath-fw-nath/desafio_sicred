package com.sicredi.desafio.services;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.constants.Endpoints;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TestService {

    public Response getStatus() {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .when()
                .get(Endpoints.TEST);
    }
}
