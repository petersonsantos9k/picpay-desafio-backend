package com.petersonsantos9k.picpay_desafio.service;

import com.petersonsantos9k.picpay_desafio.client.Payment;
import com.petersonsantos9k.picpay_desafio.config.SecurityConfig;
import com.petersonsantos9k.picpay_desafio.domain.Transfer;
import com.petersonsantos9k.picpay_desafio.domain.User;
import com.petersonsantos9k.picpay_desafio.domain.UserType;
import com.petersonsantos9k.picpay_desafio.dto.client.TransactionAuthorizationResponse;
import com.petersonsantos9k.picpay_desafio.dto.request.TransferRequestDTO;
import com.petersonsantos9k.picpay_desafio.exception.TransferException;
import com.petersonsantos9k.picpay_desafio.repository.TransferRepository;
import com.petersonsantos9k.picpay_desafio.repository.UserRepository;
import com.petersonsantos9k.picpay_desafio.service.mapper.UserMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({
    TransferService.class,
    TransferPersistenceService.class,
    UserService.class,
    UserMapperImpl.class,
    SecurityConfig.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("TransferService integration tests")
class TransferServiceIT {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransferRepository transferRepository;

    @MockitoBean(name = "paymentClient")
    private Payment paymentClient;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void cleanDatabase() {
        transferRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should transfer funds and persist the updated balances and transfer")
    void shouldTransferFundsSuccessfully() {
        User payer = createUser("Payer", "12345678901", "payer@example.com", UserType.COMMON,
            new BigDecimal("100.00"));
        User payee = createUser("Payee", "12345678902", "payee@example.com", UserType.MERCHANT,
            BigDecimal.ZERO);
        BigDecimal amount = new BigDecimal("35.50");
        authorizeTransfer();

        transferService.transfer(new TransferRequestDTO(amount, payer.getId(), payee.getId()));

        User updatedPayer = userRepository.findById(payer.getId()).orElseThrow();
        User updatedPayee = userRepository.findById(payee.getId()).orElseThrow();
        Transfer savedTransfer = transferRepository.findAll().getFirst();

        assertThat(updatedPayer.getBalance()).isEqualByComparingTo("64.50");
        assertThat(updatedPayee.getBalance()).isEqualByComparingTo("35.50");
        assertThat(savedTransfer.getSender().getId()).isEqualTo(payer.getId());
        assertThat(savedTransfer.getReceiver().getId()).isEqualTo(payee.getId());
        assertThat(savedTransfer.getAmount()).isEqualByComparingTo(amount);
        assertThat(savedTransfer.getCreatedAt()).isNotNull();
        verify(emailService).sendTransferEmails();
    }

    @Test
    @DisplayName("Should not change balances or persist a transfer when authorization is denied")
    void shouldNotTransferFundsWhenAuthorizationIsDenied() {
        User payer = createUser("Payer", "22345678901", "payer2@example.com", UserType.COMMON,
            new BigDecimal("100.00"));
        User payee = createUser("Payee", "22345678902", "payee2@example.com", UserType.COMMON,
            BigDecimal.ZERO);
        when(paymentClient.authorizeTransaction()).thenReturn(Optional.of(
            new TransactionAuthorizationResponse("success", new TransactionAuthorizationResponse.Data(false))
        ));

        assertThatThrownBy(() -> transferService.transfer(
            new TransferRequestDTO(new BigDecimal("35.50"), payer.getId(), payee.getId())
        ))
            .isInstanceOf(TransferException.class)
            .hasMessage("Transaction authorization failed");

        assertThat(userRepository.findById(payer.getId()).orElseThrow().getBalance())
            .isEqualByComparingTo("100.00");
        assertThat(userRepository.findById(payee.getId()).orElseThrow().getBalance())
            .isEqualByComparingTo("0.00");
        assertThat(transferRepository.findAll()).isEmpty();
    }

    private User createUser(
        String name,
        String taxId,
        String email,
        UserType userType,
        BigDecimal balance
    ) {
        User user = new User(name, taxId, email, "password", userType);
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            user.deposit(balance);
        }
        return userRepository.saveAndFlush(user);
    }

    private void authorizeTransfer() {
        when(paymentClient.authorizeTransaction()).thenReturn(Optional.of(
            new TransactionAuthorizationResponse("success", new TransactionAuthorizationResponse.Data(true))
        ));
    }
}
