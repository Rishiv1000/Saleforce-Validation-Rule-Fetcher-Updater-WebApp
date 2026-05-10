package com.sfmanager.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SalesforceConfig {

    @Value("${sf.client-id}")
    private String clientId;

    @Value("${sf.client-secret}")
    private String clientSecret;

    @Value("${sf.redirect-uri}")
    private String redirectUri;

    @Value("${sf.login-url:https://login.salesforce.com}")
    private String loginUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;
}
