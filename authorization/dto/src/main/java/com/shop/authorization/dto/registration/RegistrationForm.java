package com.shop.authorization.dto.registration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {

    @NotNull(message = "User's login is mandatory!")
    @Size(min = 3, max = 64, message = "User's login must contains from 3 to 64 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$", message = "Invalid user's login format!")
    private String login;

    @NotNull(message = "User's email is mandatory!")
    @Size(min = 3, max = 128, message = "User's email must contain from 3 to 128 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9\\.\\-\\_]+[@][a-zA-Z0-9]+[.][a-zA-Z]{2,9}$", message = "Invalid user's email format!")
    private String email;

    @NotNull(message = "User's password is mandatory!")
    @Size(min = 4, max = 32, message = "User's password must contains from 4 to 32 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9#@$%^&*!_-~]{4,32}$", message = "Invalid user's password format!")
    private String password;

    @NotNull(message = "User's repeated password is mandatory!")
    @Size(min = 4, max = 32, message = "User's repeated password must contains from 4 to 32 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9#@$%^&*!_-~]{4,32}$", message = "Invalid user's repeated password format!")
    private String repeatPassword;

}
