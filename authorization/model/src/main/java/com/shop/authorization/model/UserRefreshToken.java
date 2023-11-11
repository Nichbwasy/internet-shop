package com.shop.authorization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_refresh_token")
public class UserRefreshToken {

    public UserRefreshToken(UserData user, String token) {
        this.user = user;
        this.token = token;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is mandatory!")
    @JoinColumn(columnDefinition = "user_id", referencedColumnName = "id")
    @OneToOne(targetEntity = UserData.class, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserData user;

    @NotNull(message = "Token is mandatory!")
    @Size(max = 1024, message = "Token can't be longer than 1024 characters!")
    @Column(name = "token", length = 1024, nullable = false)
    private String token;

}
