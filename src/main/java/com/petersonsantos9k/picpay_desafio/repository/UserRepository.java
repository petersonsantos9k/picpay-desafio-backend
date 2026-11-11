package com.petersonsantos9k.picpay_desafio.repository;

import com.petersonsantos9k.picpay_desafio.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByTaxIdOrEmail(String taxId, String email);

    Optional<User> findUserByTaxId(String taxId);
}
