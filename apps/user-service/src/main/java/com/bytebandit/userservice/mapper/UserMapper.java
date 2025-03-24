package com.bytebandit.userservice.mapper;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserRegistrationResponse(UserEntity userEntity);
}
