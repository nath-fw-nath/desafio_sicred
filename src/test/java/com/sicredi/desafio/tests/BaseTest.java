package com.sicredi.desafio.tests;

import com.sicredi.desafio.models.response.LoginResponse;
import com.sicredi.desafio.models.response.User;
import com.sicredi.desafio.models.response.UsersResponse;
import com.sicredi.desafio.services.AuthService;
import com.sicredi.desafio.services.UserService;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    protected static AuthService authService;
    protected static UserService userService;
    protected static String authToken;
    protected static User validUser;

    @BeforeAll
    static void setup() {
        authService = new AuthService();
        userService = new UserService();

        UsersResponse usersResponse = userService.getUsersAndExtract();
        validUser = usersResponse.getUsers().get(0);

        LoginResponse loginResponse = authService.loginAndExtract(
                validUser.getUsername(),
                validUser.getPassword()
        );
        authToken = loginResponse.getAccessToken();
    }
}
