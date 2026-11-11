package com.petersonsantos9k.picpay_desafio.service;

import com.petersonsantos9k.picpay_desafio.config.SecurityConfig;
import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import com.petersonsantos9k.picpay_desafio.dto.request.UserCreateRequestDTO;
import com.petersonsantos9k.picpay_desafio.dto.request.UserDepositRequestDTO;
import com.petersonsantos9k.picpay_desafio.exception.UserException;
import com.petersonsantos9k.picpay_desafio.repository.UserRepository;
import com.petersonsantos9k.picpay_desafio.service.mapper.UserMapper;
import com.petersonsantos9k.picpay_desafio.service.mapper.UserMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
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
class UserServiceIT {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("Find user by ID")
    class FindById {
        @Test
        @DisplayName("Should return the user when the ID exists")
        void ShouldFindUserByIdSuccessfully() {
            User user = new User(
                "Test",
                "12345678901",
                "test@gmail.com",
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
        @DisplayName("Should throw an exception when the user does not exist")
        void shouldThrowExceptionWhenUserDoesNotExist() {
            var uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            assertThatThrownBy(() -> userService.findById(uuid))
                .isInstanceOf(UserException.class)
                .hasMessage("User not found with id");
        }

        @Test
        @DisplayName("Should throw an exception when the ID is null")
        void shouldThrowExceptionWhenUserIdIsNull(){
            assertThatThrownBy(() -> userService.findById(null))
                .isInstanceOf(UserException.class)
                .hasMessage("ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Create user")
    class Create{

        @Test
        @DisplayName("Should create a user successfully")
        void shouldCreateUserSuccessfully(){

            var createUserDTO = new UserCreateRequestDTO(
                "Test",
                "12345678912",
                "test@gmail.com",
                "passwordTeste123"
            );

            userService.create(createUserDTO);
            var savedUser = userRepository.findUserByTaxId(createUserDTO.taxId())
                .orElseThrow();

            assertThat(savedUser.getUserType())
                .isEqualTo(UserType.COMMON);

            assertThat(savedUser.getPassword())
                .isNotEqualTo(createUserDTO.password());

            assertThat(passwordEncoder.matches(
                createUserDTO.password(),
                savedUser.getPassword()
            )).isTrue();
        }

        @Test
        @DisplayName("Should throw an exception when creating a user with an existing email")
        void shouldThrowExceptionWhenCreatingUserWithExistingEmail(){

            String username = "Test";
            String cpf = "00045678901";
            String email = "test@gmail.com";
            String password = "password12345";

            User user = new User(
                username, cpf,
                email, password,
                UserType.COMMON
            );
            userRepository.save(user);

            var createUserDTO = new UserCreateRequestDTO(
                username, "11122233344",
                email, password
            );
            assertThatThrownBy(() -> userService.create(createUserDTO))
                .isInstanceOf(UserException.class)
                .hasMessage("User already exists with the provided tax ID or email");
        }

        @Test
        @DisplayName("Should throw an exception when creating a user with an existing tax ID")
        void shouldThrowExceptionWhenCreatingUserWithExistingTaxId() {

            String username = "Test";
            String cpf = "00045678901";
            String email = "test@gmail.com";
            String password = "password12345";

            User user = new User(
                username, cpf,
                email, password,
                UserType.COMMON
            );
            userRepository.save(user);

            var createUserDTO = new UserCreateRequestDTO(
                username, cpf,
                "test2@gmail.com", password
            );
            assertThatThrownBy(() -> userService.create(createUserDTO))
                .isInstanceOf(UserException.class)
                .hasMessage("User already exists with the provided tax ID or email");
        }


    }

    @Nested
    @DisplayName("Deposit amount")
    class Deposit{

        @Test
        @DisplayName("Should deposit an amount to the user's balance successfullyy")
        void shouldDepositAmountToUserSuccessfully(){

            var user = new User(
                "Test",
                "12345678901",
                "test@gmail.com",
                "password",
                UserType.COMMON);
            User objSave = userRepository.save(user);


            var depositRequestDTO = new UserDepositRequestDTO(
                objSave.getId(),
                new BigDecimal(100)
            );

            var responseDTO = userService.deposit(depositRequestDTO);

            objSave = userRepository.findById(objSave.getId())
                .orElseThrow();

            assertThat(responseDTO.balance())
                .isEqualTo(objSave.getBalance());

            assertThat(responseDTO.balance())
                .isEqualTo(depositRequestDTO.amount());
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-10", "-100"})
        @DisplayName("Should throw an exception when the deposit amount is zero or negative")
        void shouldThrowExceptionWhenDepositAmountIsNotPositive(String amount){

            var user = new User(
                "Test",
                "12345678901",
                "test@gmail.com",
                "password",
                UserType.COMMON);
            User objSave = userRepository.save(user);

            var depositRequestDTO = new UserDepositRequestDTO(
                objSave.getId(),
                new BigDecimal(amount)
            );

            assertThatThrownBy(() -> userService.deposit(depositRequestDTO))
                .isInstanceOf(UserException.class)
                .hasMessage("Amount must be positive");
        }
    }
}

