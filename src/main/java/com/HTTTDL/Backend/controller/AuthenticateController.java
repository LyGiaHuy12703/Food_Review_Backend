package com.HTTTDL.Backend.controller;

import com.HTTTDL.Backend.core.ResponseSuccess;
import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.Auth.AuthRequest;
import com.HTTTDL.Backend.dto.Auth.AuthResponse;
import com.HTTTDL.Backend.dto.Auth.RefreshRequest;
import com.HTTTDL.Backend.dto.Auth.RegisterRequest;
import com.HTTTDL.Backend.dto.Token.RefreshTokenRequest;
import com.HTTTDL.Backend.dto.Token.TokenResponse;
import com.HTTTDL.Backend.service.AuthenticateService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    @Autowired
    private AuthenticateService authenticateService;

    //Đăng ký cho user
    @PostMapping("/register")
    ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid RegisterRequest request) {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code("auth-s-01")
                .message(authenticateService.register(request))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    //đăng nhập cho user
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        var result = authenticateService.loginUser(request);

        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .data(result)
                .code("auth-s-02")
                .message("Login successful")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    @PostMapping("/refreshToken")
    public ResponseSuccess refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        TokenResponse result = authenticateService.refreshToken(request);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Refresh Token Success")
                .metadata(result)
                .build();
    }
    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout() {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code("auth-s-03")
                .message(authenticateService.logout())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
