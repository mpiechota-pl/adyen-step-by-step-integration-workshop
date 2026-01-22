package com.adyen.workshop.controllers;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.Amount;
import com.adyen.model.checkout.CreateCheckoutSessionRequest;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import com.adyen.util.HMACValidator;
import com.adyen.workshop.configurations.ApplicationConfiguration;
import com.adyen.workshop.controllers.dto.PaymentSessionRequest;

/**
 * Your backend controller.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationConfiguration applicationConfiguration;

    private final Client adyenClient;

    private final HMACValidator hmacValidator = new HMACValidator();

    public ApiController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.adyenClient = new Client(
                applicationConfiguration.getAdyenApiKey(),
                Environment.TEST
        );
    }

    @PostMapping("/api/sessions")
    public ResponseEntity<String> payments(@RequestBody PaymentSessionRequest frontendRequest) throws IOException {
        PaymentsApi paymentsApi = new PaymentsApi(adyenClient);

        CreateCheckoutSessionRequest sessionsRequest = new CreateCheckoutSessionRequest();
        sessionsRequest.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());
        sessionsRequest.setAmount(new Amount().currency(frontendRequest.getCurrency()).value(frontendRequest.getAmount()));
        sessionsRequest.setCountryCode(frontendRequest.getCountryCode());
        sessionsRequest.setReturnUrl("http://localhost:8080/");
        sessionsRequest.setReference(frontendRequest.getReference());

        CreateCheckoutSessionResponse response;
        try {
            response = paymentsApi.sessions(sessionsRequest);
            return ResponseEntity.ok(response.toJson());
        } catch (ApiException e) {
            throw new IOException("API error: " + e.getError().getMessage());
        } catch (IOException e) {
            throw new IOException("IO error: " + e.getMessage());
        }
    }

    @PostMapping("/api/notification")
    public ResponseEntity<String> notifications(@RequestBody NotificationRequest notificationRequest) {
        log.info("Received notification: " + notificationRequest);
        boolean[] isError = {false};
        notificationRequest.getNotificationItems().forEach(item -> {
            log.info("Notification item: " + item);
            try {
                hmacValidator.validateHMAC(item, applicationConfiguration.getAdyenHmacKey());
            } catch (IllegalArgumentException e) {
                log.error("HMAC validation failed: " + e.getMessage());
                isError[0] = true;
            } catch (SignatureException e) {
                log.error("Signature exception during HMAC validation: " + e.getMessage());
                isError[0] = true;
            }
            if (!isError[0]) {
                if ("AUTHORISATION".equals(item.getEventCode())
                        && Boolean.TRUE.equals(item.isSuccess())) {
                    log.info("Payment authorised for reference: " + item.getPspReference());
                } else {
                    log.info("Unhandled notification event code: " + item.getEventCode());
                }
            }
        });

        if (isError[0]) {
            return ResponseEntity.ok("[not-accepted]");
        }
        return ResponseEntity.ok("[accepted]");
    }

}
