package com.petersonsantos9k.picpay_desafio.repository;

import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@DisplayName("UserRepositoryTest")
class UserRepositoryTest {

    @Autowired UserRepository repository;

    @Test
    @DisplayName("should return true when exists by taxId or email")
    public void shouldReturnTrueWhenExistsByTaxIdOrEmail() {
        User user = new User("Name1", "12345678901", "name1@gmail.com", "password", UserType.COMMON);
        repository.save(user);
        var exists = repository.existsByTaxIdOrEmail(user.getTaxId(), user.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false when not exists by taxId or email")
    public void shouldReturnFalseWhenNotExistsByTaxIdOrEmail() {
        var exists = repository.existsByTaxIdOrEmail("12345678901", "name  @gmail.com");
        assertThat(exists).isFalse();
    }
}
