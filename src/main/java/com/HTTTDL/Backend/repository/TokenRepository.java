package com.HTTTDL.Backend.repository;

import com.HTTTDL.Backend.model.Token;
import com.HTTTDL.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    Token findByToken(String token);
    Token findByUserId(Long userId);
    Token findByRefreshToken(String refreshToken);
    void deleteTokenByUserId(Long id);
}
