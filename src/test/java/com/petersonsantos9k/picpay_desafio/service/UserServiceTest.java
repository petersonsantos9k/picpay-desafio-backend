package com.petersonsantos9k.picpay_desafio.service;

import com.petersonsantos9k.picpay_desafio.config.SecurityConfig;
import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import com.petersonsantos9k.picpay_desafio.repository.UserRepository;
import com.petersonsantos9k.picpay_desafio.service.mapper.UserMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({
    UserService.class,
    UserMapperImpl.class,
    SecurityConfig.class
})
@DisplayName("UserServiceTest")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Nested
    class FindById {
        @Test
        @DisplayName("Should find user by ID successfully")
        void ShouldFindUserByIdSuccessfully() {
            User user = new User(
                "Name1",
                "12345678901",
                "name1@gmail.com",
                "password",
                UserType.COMMON);
            User objSave = userRepository.save(user);

            var resultMethodFindById = userService.findById(objSave.getId());
            assertThat(resultMethodFindById)
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(objSave);
        }

        @Test
        @DisplayName("Should find user by ID Failed")
        void ShouldFindUserByIdFailed() {

            var uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            assertThatThrownBy(() -> userService.findUserById(uuid))
                .hasMessage("User not found with id");
        }

        @Test
        @DisplayName("Should find user by ID Null")
        void shouldFindUserByIdNull(){
            assertThatThrownBy(() -> userService.findUserById(null))
                .hasMessage("ID cannot be null");
        }
    }

    @Nested
    class Create{

    }

    @Nested
    class Deposit{}
}

