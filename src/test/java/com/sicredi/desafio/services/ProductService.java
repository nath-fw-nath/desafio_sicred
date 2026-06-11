package com.sicredi.desafio.services;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.constants.Endpoints;
import com.sicredi.desafio.models.request.ProductRequest;
import com.sicredi.desafio.models.response.Product;
import com.sicredi.desafio.models.response.ProductsResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductService {

    public Response getProducts() {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .when()
                .get(Endpoints.PRODUCTS);
    }

    public ProductsResponse getProductsAndExtract() {
        return getProducts()
                .then()
                .spec(ApiConfig.responseSpec(200))
                .extract()
                .as(ProductsResponse.class);
    }

    public Response getProductById(int id) {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .pathParam("id", id)
                .when()
                .get(Endpoints.PRODUCTS_BY_ID);
    }

    public Product getProductByIdAndExtract(int id) {
        return getProductById(id)
                .then()
                .spec(ApiConfig.responseSpec(200))
                .extract()
                .as(Product.class);
    }

    public Response addProduct(ProductRequest request) {
        return given()
                .spec(ApiConfig.baseRequestSpec())
                .body(request)
                .when()
                .post(Endpoints.PRODUCTS_ADD);
    }

    public Product addProductAndExtract(ProductRequest request) {
        return addProduct(request)
                .then()
                .spec(ApiConfig.responseSpec(201))
                .extract()
                .as(Product.class);
    }
}
