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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticateService {
    Helper helper;
    UserRepository userRepository;
    UserMapper userMapper;
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

    public String register(RegisterRequest request) {
        User findUser = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (findUser != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User existed", "auth-e-01");
        }
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();

        roles.add(Role.USER.toString());

        user.setRoles(roles);

        userRepository.save(user);
        return "Register successful";
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
    public AuthResponse loginUser(AuthRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if(user == null){
            throw new AppException(HttpStatus.BAD_REQUEST, "User not found", "auth-e-01");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw new AppException(HttpStatus.BAD_REQUEST, "UnAuthenticated", "auth-e-04");
        }
        var accessToken = helper.generateTokenUser(user, TOKEN_EXPIRY_TIME, ACCESS_TOKEN_SECRET, null);
        var refreshToken = helper.generateTokenUser(user, TOKEN_REFRESH_EXPIRY_TIME, REFRESH_TOKEN_SECRET, null);

        Token token = tokenRepository.findByUserId(user.getId());
        if(token == null){
            tokenRepository.save(Token.builder()
                    .user(user)
                    .token(refreshToken)
                    .refreshToken(refreshToken)
                    .createAt(new Date())
                    .build()
            );
        }else{
            token.setToken(accessToken);
            token.setRefreshToken(refreshToken);
            tokenRepository.save(token);
        }
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(helper.buildScopeUser(user))
                .build();
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
    public String logout() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var email = auth.getName();
        //xóa token trong db
        User user = userRepository.findByUsername(email).orElse(null);

        if(user == null) {
            throw new  AppException(HttpStatus.NOT_FOUND, "User not found", "auth-e-02");
        }
        tokenRepository.deleteTokenByUser(user);
        return "Logout successfully";
    }
}
