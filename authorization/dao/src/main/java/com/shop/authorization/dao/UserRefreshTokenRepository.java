package com.shop.authorization.dao;

import com.shop.authorization.model.UserData;
import com.shop.authorization.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    Boolean existsByUser(UserData userData);
    Boolean existsByUserId(Long id);
    UserRefreshToken getByUserId(Long id);

}
