package com.petersonsantos9k.picpay_desafio.service.mapper;

import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateRequestDTO dto);
    UserResponseDTO toResponse(User dto);


}
