package com.HTTTDL.Backend.dto.Auth;

import com.HTTTDL.Backend.dto.Token.TokenResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String email;
    TokenResponse token;
    String role;
}
