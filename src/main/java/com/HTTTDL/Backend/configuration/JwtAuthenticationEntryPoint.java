package com.HTTTDL.Backend.configuration;


import com.HTTTDL.Backend.core.ResponseError;
import com.HTTTDL.Backend.exception.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ErrorEnum err = ErrorEnum.UNAUTHENTICATED;
        if(authException.getMessage().contains("Jwt expired")) {
            err = ErrorEnum.TOKEN_EXPIRE;
        }

        //set response status và contentType
        response.setStatus(err.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);


        ResponseError resErr = ResponseError.builder()
                .message(err.getMessage())
                .error(err.getStatus())
                .build();
        //map object -> String
        ObjectMapper mapper = new ObjectMapper();

        //Write string vào response
        response.getWriter().write(mapper.writeValueAsString(resErr));

        //buộc response trả về client
        response.flushBuffer();
    }
}
