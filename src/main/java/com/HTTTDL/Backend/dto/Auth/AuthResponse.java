package com.HTTTDL.Backend.dto.Auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String username;
    String accessToken;
    String refreshToken;
    String role;
}
