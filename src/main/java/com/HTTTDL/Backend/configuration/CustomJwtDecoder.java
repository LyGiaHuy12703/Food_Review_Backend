package com.HTTTDL.Backend.configuration;

import com.HTTTDL.Backend.dto.Auth.IntrospectRequest;
import com.HTTTDL.Backend.service.AuthenticateService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.accessToken}")
    private String signerKey;

    @Autowired
    private AuthenticateService authenticateService;

    private NimbusJwtDecoder nimbusJwtDecoder=null;

    @Override
    public Jwt decode(String token) throws JwtException {
        //verify token đã hết hạn hay logout rồi
        try {
             var response = authenticateService.introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}