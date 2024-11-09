package com.HTTTDL.Backend.configuration;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

//config trong https://docs.spring.io/spring-security/reference/servlet/architecture.html
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @NonFinal
    @Value("${jwt.accessToken}")
    protected String ACCESS_TOKEN_SECRET;
    //ngoài những endpoint public bất cứ ai cũng có thể truy cập còn lại phải cung cấp token
    private final String[] PUBLIC_ENDPOINTS = {
            "auth/register",
            "auth/login",
            "auth/introspect",
            "auth/refreshToken",
            "auth/register/verify/**"
    } ;

    CustomJwtDecoder customJwtDecoder;

    //cấu hình spring security những endpoint nào cần bảo vệ và k cần
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //cấu hình những đường dẫn guest có thể đi vào
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "auth/verify/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "geo/**").permitAll()
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 ->
                //kiểm tra jwt của hệ thống
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        //chống tấn công nhưng trường hợp này có thể tắt csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(Customizer.withDefaults());
//        httpSecurity.cors(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    //cấu hình cho front end gọi
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://127.0.0.1:5500");
        corsConfiguration.addAllowedOrigin("http://localhost:3000");  // Cho phép frontend truy cập từ localhost:3000
        corsConfiguration.addAllowedMethod("*");  // Cho phép tất cả các phương thức: GET, POST, PUT, DELETE, etc.
        corsConfiguration.addAllowedHeader("*");  // Cho phép tất cả các header
        corsConfiguration.setAllowCredentials(true);  // Cho phép gửi thông tin xác thực như cookies, authorization headers

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);  // Áp dụng cho tất cả các endpoint

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }


    @Bean
    JwtAuthenticationConverter jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return converter;
    }
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(ACCESS_TOKEN_SECRET.getBytes(), "HS512");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512).build();

        return token -> {
            Jwt jwt = jwtDecoder.decode(token);
            var expiresAt = jwt.getExpiresAt();
            if(expiresAt != null && Instant.now().isAfter(expiresAt)) {
                throw new AuthenticationServiceException("Token has expired");
            }
            return jwt;
        };
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
