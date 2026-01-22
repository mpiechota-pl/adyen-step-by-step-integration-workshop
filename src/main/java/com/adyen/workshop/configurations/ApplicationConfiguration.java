package com.adyen.workshop.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Value("${server.port}")                    // This will be prefilled from application.properties (e.g. port 8080)
    private int serverPort;

    @Value("${apiKey:#{null}}") // This will be prefilled from application.properties
    private String adyenApiKey;

    @Value("${merchantAccount:#{null}}") // This will be prefilled from application.properties
    private String adyenMerchantAccount;

    @Value("${clientKey:#{null}}") // This will be prefilled from application.properties
    private String adyenClientKey;

    @Value("${hmac:#{null}}") // This will be prefilled from application.properties
    private String adyenHmacKey;

    public String getAdyenApiKey() {
        return adyenApiKey;
    }

    public void setAdyenApiKey(String adyenApiKey) {
        this.adyenApiKey = adyenApiKey;
    }

    public String getAdyenMerchantAccount() {
        return adyenMerchantAccount;
    }

    public void setAdyenMerchantAccount(String adyenMerchantAccount) {
        this.adyenMerchantAccount = adyenMerchantAccount;
    }

    public String getAdyenClientKey() {
        return adyenClientKey;
    }

    public void setAdyenClientKey(String adyenClientKey) {
        this.adyenClientKey = adyenClientKey;
    }

    public String getAdyenHmacKey() {
        return adyenHmacKey;
    }

    public void setAdyenHmacKey(String adyenHmacKey) {
        this.adyenHmacKey = adyenHmacKey;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}