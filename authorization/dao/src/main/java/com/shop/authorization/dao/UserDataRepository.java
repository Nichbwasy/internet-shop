package com.shop.authorization.dao;

import com.shop.authorization.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);

}
