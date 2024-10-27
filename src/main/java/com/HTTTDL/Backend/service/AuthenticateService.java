package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.Auth.*;
import com.HTTTDL.Backend.dto.Token.RefreshTokenRequest;
import com.HTTTDL.Backend.dto.Token.TokenResponse;
import com.HTTTDL.Backend.enums.Role;
import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.mapper.UserMapper;
import com.HTTTDL.Backend.model.Token;
import com.HTTTDL.Backend.model.User;
import com.HTTTDL.Backend.repository.TokenRepository;
import com.HTTTDL.Backend.repository.UserRepository;
import com.HTTTDL.Backend.ultis.Helper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticateService {
    Helper helper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    @NonFinal
    @Value("${jwt.accessToken}")
    protected String ACCESS_TOKEN_SECRET;
    @NonFinal
    @Value("${jwt.refreshToken}")
    protected String REFRESH_TOKEN_SECRET;
    @NonFinal
    @Value("${jwt.expiryTime}")
    protected int TOKEN_EXPIRY_TIME;
    @NonFinal
    @Value("${jwt.expiryTimeRefreshToken}")
    protected int TOKEN_REFRESH_EXPIRY_TIME;

    public void register(RegisterRequest request) {
        boolean existedUser = userRepository.existsByEmail(request.getEmail());
        if(existedUser){
            throw new AppException(HttpStatus.BAD_REQUEST, "Email has existed", "auth-e-01");
        }
    }
    public AuthResponse verifyRegister(RegisterRequest request) {
        // Find user if not existed
        boolean existedUser = userRepository.existsByEmail(request.getEmail());
        if(existedUser){
            throw new AppException(HttpStatus.BAD_REQUEST, "Email has existed", "auth-e-01");
        }
        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);

        // Roles for normal user
        Role role = Role.USER;
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        user.setRole(role);
        userRepository.save(user);

        // Generate a pair of token
        String accessToken = helper.generateTokenUser(user,TOKEN_EXPIRY_TIME,ACCESS_TOKEN_SECRET,null);
        String refreshToken = helper.generateTokenUser(user,TOKEN_REFRESH_EXPIRY_TIME,REFRESH_TOKEN_SECRET,null);
        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        return AuthResponse.builder()
                .token(tokenResponse)
                .build();
    }
    private SignedJWT verifyTokenPrivate(String token, boolean isRefersh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(ACCESS_TOKEN_SECRET.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        //lấy thời gian hết hạn
        Date expityTime = (isRefersh)
                ?new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(TOKEN_REFRESH_EXPIRY_TIME, ChronoUnit.SECONDS).toEpochMilli())
                :signedJWT.getJWTClaimsSet().getExpirationTime();

        //trả về true or false token hết hạn
        boolean verified = signedJWT.verify(verifier);
        if(!(verified && expityTime.after(new Date()))){
            throw new AppException(HttpStatus.BAD_REQUEST, "Unauthenticated", "auth-e-03");
        }

        if(tokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(HttpStatus.BAD_REQUEST, "Unauthenticated", "auth-e-03");

        return signedJWT;
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyTokenPrivate(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }
    public AuthResponse signinUser(
            AuthRequest request
    ) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user == null) {
            throw new  AppException(HttpStatus.BAD_REQUEST, "Login fail");
        }
        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isMatch) {
            throw new  AppException(HttpStatus.BAD_REQUEST, "Login fail");
        }
        //generate token
        String accessToken = helper.generateTokenUser(user,TOKEN_EXPIRY_TIME,ACCESS_TOKEN_SECRET,null);
        String refreshToken = helper.generateTokenUser(user,TOKEN_REFRESH_EXPIRY_TIME,REFRESH_TOKEN_SECRET,null);

        Token token = tokenRepository.findByUserId(user.getId());
        if(token == null) {
            tokenRepository.save(Token.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .build());
        }else {
            token.setRefreshToken(refreshToken);
            tokenRepository.save(token);
        }
        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        AuthResponse userResponse = AuthResponse.builder()
                .email(user.getEmail())
                .token(tokenResponse)
                .role(user.getRole().toString())
                .build();
        return userResponse;
    }
    public TokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        String token = request.refresh_token();
        SignedJWT signedJWT = helper.verifyToken(token,REFRESH_TOKEN_SECRET);
        //kiểm tra token trong db
        Token refreshTokenEntity = tokenRepository.findByRefreshToken(token);
        if(refreshTokenEntity == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Unauthenticated", "auth-e-01");
        }
        User user = userRepository.findById(refreshTokenEntity.getUser().getId()).orElse(null);
        if(user == null) {
            throw new AppException(HttpStatus.NOT_FOUND, "User not found", "auth-e-02");
        }
        Date expireTimeOfRefreshToken = signedJWT.getJWTClaimsSet().getExpirationTime();
        String accessToken = helper.generateTokenUser(user,TOKEN_EXPIRY_TIME,ACCESS_TOKEN_SECRET,null);
        String refreshToken = helper.generateTokenUser(user,TOKEN_REFRESH_EXPIRY_TIME,REFRESH_TOKEN_SECRET,expireTimeOfRefreshToken);

        //cập nhật token trong db
        refreshTokenEntity.setRefreshToken(refreshToken);
        tokenRepository.save(refreshTokenEntity);
        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        return tokenResponse;
    }
    @Transactional
    public String logout() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var username = auth.getName();
        //xóa token trong db
        User user = userRepository.findByEmail(username).orElse(null);

        if(user == null) {
            throw new  AppException(HttpStatus.NOT_FOUND, "User not found", "auth-e-02");
        }
        tokenRepository.deleteTokenByUserId(user.getId());
        return "Logout successfully";
    }
}
