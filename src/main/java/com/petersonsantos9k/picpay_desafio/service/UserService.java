package com.petersonsantos9k.picpay_desafio.service;

import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.request.UserDepositRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.response.UserResponseDTO;
import com.petersonsantos9k.picpay_desafio.exception.UserException;
import com.petersonsantos9k.picpay_desafio.repository.UserRepository;
import com.petersonsantos9k.picpay_desafio.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO findById(UUID id){
        User obj = getUserById(id);
        return userMapper.toResponse(obj);
    }

    public UserResponseDTO create(UserCreateRequestDTO dto){
        if(userRepository.existsByTaxIdOrEmail(dto.taxId(), dto.email())){
            throw new UserException("User already exists with the provided tax ID or email");
        }

        UserType userType = dto.taxId().length() == 11 ? UserType.COMMON : UserType.MERCHANT;

        User obj = userMapper.toEntity(dto);
        obj.setUserType(userType);

        String encodedPassword = passwordEncoder.encode(obj.getPassword());
        obj.setPassword(encodedPassword);


        obj = userRepository.save(obj);
        log.info("User created successfully. User ID: {}", obj.getId());
        return userMapper.toResponse(obj);
    }

    public UserResponseDTO deposit(UserDepositRequestDTO dto){

        if(dto.userId() == null || dto.amount() == null){
            throw new UserException("Invalid user ID or deposit amount");
        }

        User user = getUserById(dto.userId());
        user.deposit(dto.amount());
        userRepository.save(user);
        log.info("Deposit completed successfully. User ID: {}", user.getId());
        return userMapper.toResponse(user);
    }


    protected User findUserById(UUID id){
        return getUserById(id);
    }

    protected void updateUser(User user){
        userRepository.save(user);
    }

    private User getUserById(UUID id){

        if(id == null){
            throw new UserException("ID cannot be null");
        }

        return userRepository.findById(id)
            .orElseThrow(() -> new UserException("User not found with id"));
    }
}
