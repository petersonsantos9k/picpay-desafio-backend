package com.petersonsantos9k.picpay_desafio.config;

import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;


    @Override
    public void run(String... args) throws Exception {
        seedUsers();
    }


    private void seedUsers(){

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

        for(UserCreateRequestDTO req : users){
            userService.create(req);
        }

    }




}
