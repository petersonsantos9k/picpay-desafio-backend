package com.petersonsantos9k.picpay_desafio.config;

import com.petersonsantos9k.picpay_desafio.dto.request.TransferRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.request.UserDepositRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.response.UserResponseDTO;
import com.petersonsantos9k.picpay_desafio.exception.TransferException;
import com.petersonsantos9k.picpay_desafio.exception.UserException;
import com.petersonsantos9k.picpay_desafio.service.TransferService;
import com.petersonsantos9k.picpay_desafio.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final TransferService transferService;

    @Override
    public void run(String... args) throws Exception {
        seedTransfers(seedUsers());
    }


    @Transactional
    private List<UserResponseDTO> seedUsers(){

        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();

        List<UserCreateRequestDTO> users = List.of(
            new UserCreateRequestDTO(
                "John Smith", "11111111111",
                "john@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Mary Johnson", "22222222222",
                "mary@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Robert Brown", "33333333333",
                "robert@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Patricia Davis", "44444444444",
                "patricia@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Michael Wilson", "55555555555",
                "michael@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Jennifer Garcia", "66666666666",
                "jennifer@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "William Miller", "77777777777",
                "william@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Elizabeth Taylor", "88888888888",
                "elizabeth@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "David Anderson", "99999999999",
                "david@gmail.com", "Password123"
            ),
            new UserCreateRequestDTO(
                "Sarah Thomas", "12345678901",
                "sarah@gmail.com", "Password123"
            )
        );

        users.forEach(user ->{

            var createdUser = userService.create(user);

            var depositDTO = new UserDepositRequestDTO(
                createdUser.id(),
                new BigDecimal("1000.00")
            );

            var userResponseDTO = userService.deposit(depositDTO);

            userResponseDTOS.add(userResponseDTO);
        });

        return userResponseDTOS;
    }


    @Transactional
    private void seedTransfers(List<UserResponseDTO> users){
        for (int i = 0; i < users.size(); i++) {

            var payer = users.get(i);
            var payee = users.get((i + 1) % users.size());

            var transferDTO = new TransferRequestDTO(
                new BigDecimal("100.00"),
                payer.id(),
                payee.id()
            );

            try{
                transferService.transfer(transferDTO);
            } catch (TransferException exception){
                log.warn(
                    "Transfer not authorized. Payer: {}, Payee: {}. Reason: {}",
                    payer.id(),
                    payee.id(),
                    exception.getMessage()
                );
            }

        }
    }

}
