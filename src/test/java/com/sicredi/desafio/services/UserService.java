package com.sicredi.desafio.services;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.constants.Endpoints;
import com.sicredi.desafio.models.response.UsersResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserService {

    public Response getUsers() {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .when()
                .get(Endpoints.USERS);
    }

    public UsersResponse getUsersAndExtract() {
        return getUsers()
                .then()
                .spec(ApiConfig.responseSpec(200))
                .extract()
                .as(UsersResponse.class);
    }
}
