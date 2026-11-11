package com.petersonsantos9k.picpay_desafio.repository;

import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@DisplayName("User repository tests")
class UserRepositoryIT {

    @Autowired UserRepository repository;

    @Test
    @DisplayName("Should return true when a user exists with the given tax ID or email")
    public void shouldReturnTrueWhenExistsByTaxIdOrEmail() {
        User user = new User("test1", "12345678901", "test@gmail.com", "password", UserType.COMMON);
        repository.save(user);
        var exists = repository.existsByTaxIdOrEmail(user.getTaxId(), user.getEmail());
        assertThat(exists).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "12345678901, nonexistent@gmail.com, true",
        "99999999999, test@gmail.com, true",
        "12345678901, test@gmail.com, true",
        "99999999999, nonexistent@gmail.com, false"
    })
    @DisplayName("Should correctly check whether a user exists by tax ID or email")
    void shouldCheckIfUserExistsByTaxIdOrEmail(
        String taxId,
        String email,
        boolean expected
    ) {
        User user = new User(
            "test1",
            "12345678901",
            "test@gmail.com",
            "password",
            UserType.COMMON
        );

        repository.save(user);

        var exists = repository.existsByTaxIdOrEmail(taxId, email);

        assertThat(exists).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return a user when the tax ID exists")
    void shouldReturnUserWhenTaxIdExists() {
        User user = new User(
            "test1",
            "12345678901",
            "test@gmail.com",
            "password",
            UserType.COMMON
        );

        repository.save(user);

        var result = repository.findUserByTaxId(user.getTaxId());

        assertThat(result)
            .isPresent()
            .get()
            .extracting(User::getTaxId)
            .isEqualTo(user.getTaxId());
    }

    @Test
    @DisplayName("Should return empty when the tax ID does not exist")
    void shouldReturnEmptyWhenTaxIdDoesNotExist() {
        var result = repository.findUserByTaxId("99999999999");
        assertThat(result).isEmpty();
    }
}
