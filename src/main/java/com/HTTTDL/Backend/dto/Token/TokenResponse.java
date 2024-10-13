package com.HTTTDL.Backend.dto.Token;

import lombok.Builder;

@Builder
public record TokenResponse(
        String access_token,
        String refresh_token
) {
}
