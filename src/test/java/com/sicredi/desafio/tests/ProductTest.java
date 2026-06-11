package com.sicredi.desafio.tests;

import com.sicredi.desafio.config.ApiConfig;
import com.sicredi.desafio.models.request.ProductRequest;
import com.sicredi.desafio.models.response.Product;
import com.sicredi.desafio.models.response.ProductsResponse;
import com.sicredi.desafio.services.ProductService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Produtos")
class ProductTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    // --- GET /products ---

    @Test
    @Tag("smoke")
    @DisplayName("GET /products deve retornar HTTP 200 com lista de produtos")
    @Description("Verifica que o endpoint retorna status 200 com lista não vazia de produtos")
    @Story("Listar Produtos")
    void shouldReturnAllProductsWithSuccess() {
        productService.getProducts()
                .then()
                .spec(ApiConfig.responseSpec(200))
                .body("products", not(empty()))
                .body("total", greaterThan(0));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /products deve retornar metadados de paginação")
    @Description("Verifica que a resposta contém total, skip e limit")
    @Story("Listar Produtos")
    void shouldReturnPaginationMetadata() {
        ProductsResponse response = productService.getProductsAndExtract();

        assertThat("Total deve ser maior que zero", response.getTotal(), greaterThan(0));
        assertThat("Skip deve ser zero por padrão", response.getSkip(), equalTo(0));
        assertThat("Limit deve ser maior que zero", response.getLimit(), greaterThan(0));
        assertThat("Lista de produtos não deve ser vazia", response.getProducts(), not(empty()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET /products deve retornar produtos com campos obrigatórios preenchidos")
    @Description("Verifica que cada produto possui id, title, price e category")
    @Story("Listar Produtos")
    void shouldReturnProductsWithRequiredFields() {
        ProductsResponse response = productService.getProductsAndExtract();
        Product firstProduct = response.getProducts().get(0);

        assertThat("Id do produto deve ser positivo", firstProduct.getId(), greaterThan(0));
        assertThat("Title não deve ser nulo", firstProduct.getTitle(), not(emptyOrNullString()));
        assertThat("Price deve ser maior que zero", firstProduct.getPrice(), greaterThan(0.0));
        assertThat("Category não deve ser nula", firstProduct.getCategory(), not(emptyOrNullString()));
    }

    // --- GET /products/{id} ---

    @Test
    @Tag("smoke")
    @DisplayName("GET /products/{id} com id válido deve retornar o produto correto")
    @Description("Verifica que buscar produto por id retorna HTTP 200 com os dados do produto")
    @Story("Buscar Produto por ID")
    void shouldReturnProductByValidId() {
        int productId = 1;

        Product product = productService.getProductByIdAndExtract(productId);

        assertThat("Id deve corresponder ao solicitado", product.getId(), equalTo(productId));
        assertThat("Title não deve ser nulo", product.getTitle(), not(emptyOrNullString()));
        assertThat("Price deve ser positivo", product.getPrice(), greaterThan(0.0));
    }

    @Test
    @Tag("negative")
    @DisplayName("GET /products/{id} com id inexistente deve retornar HTTP 404")
    @Description("Verifica que buscar produto com id inválido retorna 404 com mensagem de erro")
    @Story("Buscar Produto por ID")
    void shouldReturn404ForNonExistentProductId() {
        int nonExistentId = 99999;

        productService.getProductById(nonExistentId)
                .then()
                .statusCode(404)
                .body("message", containsString(String.valueOf(nonExistentId)));
    }

    // --- POST /products/add ---

    @Test
    @Tag("smoke")
    @DisplayName("POST /products/add deve criar produto e retornar HTTP 201")
    @Description("Verifica que um novo produto é criado com os dados enviados e retorna id atribuído")
    @Story("Criar Produto")
    void shouldCreateProductWithSuccess() {
        ProductRequest request = ProductRequest.builder()
                .title("Produto Teste Sicredi")
                .description("Produto criado para validação do desafio QE")
                .price(99.99)
                .discountPercentage(10.0)
                .rating(4.5)
                .stock(50)
                .brand("Sicredi Brand")
                .category("test-category")
                .thumbnail("https://via.placeholder.com/150")
                .build();

        Product createdProduct = productService.addProductAndExtract(request);

        assertThat("Id do produto criado deve ser positivo", createdProduct.getId(), greaterThan(0));
        assertThat("Title deve coincidir com o enviado", createdProduct.getTitle(), equalTo(request.getTitle()));
        assertThat("Price deve coincidir com o enviado", createdProduct.getPrice(), equalTo(request.getPrice()));
        assertThat("Brand deve coincidir com o enviado", createdProduct.getBrand(), equalTo(request.getBrand()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("POST /products/add deve retornar produto com id gerado automaticamente")
    @Description("Verifica que o id retornado na criação é um número positivo (não persistido na API)")
    @Story("Criar Produto")
    void shouldReturnAutoGeneratedIdOnProductCreation() {
        ProductRequest request = ProductRequest.builder()
                .title("Produto com ID Auto")
                .price(149.90)
                .build();

        Product createdProduct = productService.addProductAndExtract(request);

        assertThat("Id gerado deve ser um inteiro positivo", createdProduct.getId(), greaterThan(0));
    }
}
