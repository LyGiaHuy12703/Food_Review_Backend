package com.HTTTDL.Backend.ultis;

import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.model.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Component
public class Helper {
    public SignedJWT verifyToken(String token, String secretKey) throws ParseException, JOSEException {
        var verifier = new MACVerifier(secretKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        var verify = signedJWT.verify(verifier);

        var expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(!verify) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Unauthenticated", "auth-e-01");
        }
        if(!expireTime.after(new Date())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Token expired", "auth-e-02");
        }
        return signedJWT;

    }
    //build scope for user
    public String buildScopeUser(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");//phân cách bằng dấu cách
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(stringJoiner::add);
        }

        return stringJoiner.toString();
    }

    //generate token cho user
    public String generateTokenUser(
            User user,
            int expireDay,
            String secretKey,
            @Nullable Date expireTime
    ) {
        Date expirationTimeVar = expireTime == null ? new Date(
                Instant.now().plus(expireDay, ChronoUnit.SECONDS).toEpochMilli()
        ) : expireTime;
        JWSHeader jwtHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new  JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("telemedicine.com")
                .issueTime(new Date())
                .expirationTime(expirationTimeVar)
                .claim("scope",buildScopeUser(user))
                .claim("name",user.getName())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwtHeader, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
