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
import com.HTTTDL.Backend.service.EmailService;
import com.HTTTDL.Backend.ultis.CodeUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    @Autowired
    private AuthenticateService authenticateService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CodeUtil codeUtil;

    //Đăng ký cho user
    @PostMapping("/register")
    ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid RegisterRequest request) {
        authenticateService.register(request);
        String verificationCode = UUID.randomUUID().toString();
        codeUtil.save(verificationCode, request, 1);
        emailService.sendEmailToVerifyRegister(request.getEmail(), verificationCode);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code("auth-s-01")
                .message("Request register successfully, check your email")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    @GetMapping("/register/verify/{verificationCode}")
    public RedirectView verifyRegister(@PathVariable String verificationCode) {
        RegisterRequest request = (RegisterRequest) codeUtil.get(verificationCode);
        AuthResponse authResponse = authenticateService.verifyRegister(request);
        codeUtil.remove(verificationCode);
        emailService.sendEmailToWelcome(request.getEmail());
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/")
                .queryParam("accessToken",  authResponse.getToken().access_token())
                .queryParam("refreshToken", authResponse.getToken().refresh_token())
                .toUriString();
        return new RedirectView(redirectUrl);
    }
    //đăng nhập cho user
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        var result = authenticateService.signinUser(request);

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
