package com.petersonsantos9k.picpay_desafio.client.payment;

import com.petersonsantos9k.picpay_desafio.client.Payment;
import com.petersonsantos9k.picpay_desafio.dto.client.TransactionAuthorizationResponse;
import com.petersonsantos9k.picpay_desafio.exception.ClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component("paymentClient")
public class PaymentClient implements Payment {

    private final RestClient restClient;
    private final PaymentClientProperties properties;
    public PaymentClient(
        @Qualifier("paymentRestClient") RestClient restClient,
        PaymentClientProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public Optional<TransactionAuthorizationResponse> authorizeTransaction(){
        int maxRetries = properties.getMaxRetries() != null ? Math.max(0, properties.getMaxRetries()) : 0;
        RestClientException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try{
                return Optional.ofNullable(
                    restClient.get()
                        .uri(properties.getAuthorizeTransactionPath())
                        .exchange((request, response)->
                            response.bodyTo(TransactionAuthorizationResponse.class)
                        )
                );
            } catch (RestClientException e) {
                lastException = e;
            }
        }

        throw new ClientException(
            "Error occurred while authorizing transaction after " + (maxRetries + 1) + " attempt(s): "
                + lastException.getMessage(),
            lastException
        );
    }


}
