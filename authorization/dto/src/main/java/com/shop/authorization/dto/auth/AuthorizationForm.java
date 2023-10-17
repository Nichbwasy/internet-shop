package com.shop.authorization.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationForm {

    @NotNull(message = "Login or email is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$|^[a-zA-Z0-9\\.\\-\\_]+[@][a-zA-Z0-9]+[.][a-zA-Z]{2,9}$",
            message = "Invalid login or email format!")
    private String loginOrEmail;

    @NotNull(message = "Password is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9#@$%^&*!_-~]{4,32}$", message = "Invalid password format!")
    private String password;

}
