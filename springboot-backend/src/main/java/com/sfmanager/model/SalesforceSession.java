package com.sfmanager.model;

import lombok.Data;

@Data
public class SalesforceSession {
    private String accessToken;
    private String instanceUrl;
    private String refreshToken;
    private String username;
    private String displayName;
    private String orgId;
    private String codeVerifier;
    private String loginUrl;
}
