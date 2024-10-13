package com.HTTTDL.Backend.dto.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    Long id;
    String username;
    String roles;
}
