package com.sicredi.desafio.services;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.constants.Endpoints;
import com.sicredi.desafio.models.request.LoginRequest;
import com.sicredi.desafio.models.response.LoginResponse;
import com.sicredi.desafio.models.response.ProductsResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthService {

    public Response login(LoginRequest request) {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .body(request)
                .when()
                .post(Endpoints.AUTH_LOGIN);
    }

    public LoginResponse loginAndExtract(String username, String password) {
        return login(LoginRequest.builder()
                .username(username)
                .password(password)
                .build())
                .then()
                .spec(ApiConfig.responseSpec(200))
                .extract()
                .as(LoginResponse.class);
    }

    public Response getAuthProducts(String token) {
        return given()
                .spec(ApiConfig.authenticatedRequestSpec(token))
                .when()
                .get(Endpoints.AUTH_PRODUCTS);
    }

    public Response getAuthProductsWithoutToken() {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .when()
                .get(Endpoints.AUTH_PRODUCTS);
    }

    public ProductsResponse getAuthProductsAndExtract(String token) {
        return getAuthProducts(token)
                .then()
                .spec(ApiConfig.responseSpec(200))
                .extract()
                .as(ProductsResponse.class);
    }
}
