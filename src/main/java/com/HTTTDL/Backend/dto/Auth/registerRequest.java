package com.HTTTDL.Backend.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    String id;
    @Size(min = 5, message = "NAME MUST BE MORE THAN 5 CHARACTERS")
    String name;
    String username;
    @Size(min = 5, message = "PASSWORD MUST BE MORE THAN 5 CHARACTERS")
    String password;
}