package com.shop.authorization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_data")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User login is mandatory!")
    @Size(min = 3, max = 64, message = "User's login must contains from 3 to 64 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$", message = "Invalid user's login format!")
    @Column(name = "login", length = 64, unique = true, nullable = false)
    private String login;

    @NotNull(message = "User's password is mandatory!")
    @Size(max = 1024, message = "User's password can't be longer than 1024 characters!")
    @Column(name = "password", length = 1024, nullable = false)
    private String password;

    @NotNull(message = "User's email is mandatory!")
    @Size(min = 3, max = 128, message = "User's email mist contains from 3 to 128 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9\\.\\-\\_]+[@][a-zA-Z0-9]+[.][a-zA-Z]{2,9}$", message = "Invalid user's email format!")
    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @OneToOne(targetEntity = UserRefreshToken.class)
    private UserRefreshToken refreshToken;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Role> roles;

}
