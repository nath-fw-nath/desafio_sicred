package com.sicredi.desafio.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsResponse {
    private List<Product> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
