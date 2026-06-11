package com.sicredi.desafio.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersResponse {
    private List<User> users;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
