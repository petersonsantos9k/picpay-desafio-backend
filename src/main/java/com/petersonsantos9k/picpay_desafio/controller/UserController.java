package com.petersonsantos9k.picpay_desafio.controller;

import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.request.UserDepositRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.response.UserResponseDTO;
import com.petersonsantos9k.picpay_desafio.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String message = "";

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO dto){
        return ResponseEntity.ok().body(
            userService.create(dto)
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable @NotNull UUID id){
        return ResponseEntity.ok().body(
            userService.findById(id)
        );
    }

    @PostMapping("/deposit")
    public ResponseEntity<UserResponseDTO> deposit(@RequestBody @Valid UserDepositRequestDTO dto){
        return ResponseEntity.ok().body(
            userService.deposit(dto)
        );
    }

}
