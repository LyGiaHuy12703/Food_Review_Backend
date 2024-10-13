package com.HTTTDL.Backend.mapper;

import com.HTTTDL.Backend.dto.Auth.RegisterRequest;
import com.HTTTDL.Backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password",ignore = true)
    User toUser(RegisterRequest request);

//    UserResponse userToUserResponse(User user);
//
//    @Mapping(target = "role",ignore = true)
//    @Mapping(target = "token",ignore = true)
//    AuthResponse userToAuthResponse(User user);
}
