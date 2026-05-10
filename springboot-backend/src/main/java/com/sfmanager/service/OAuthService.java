package com.sfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sfmanager.config.SalesforceConfig;
import com.sfmanager.model.SalesforceSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final SalesforceConfig config;

    public String generateCodeVerifier() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String generateCodeChallenge(String verifier) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(verifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public String buildAuthorizationUrl(String loginUrl, String challenge) {
        return loginUrl + "/services/oauth2/authorize"
                + "?response_type=code"
                + "&client_id="     + config.getClientId()
                + "&redirect_uri="  + config.getRedirectUri()
                + "&scope=api+web+refresh_token"
                + "&code_challenge=" + challenge
                + "&code_challenge_method=S256";
    }

    public SalesforceSession exchangeCode(String code, String verifier, String loginUrl) throws Exception {
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",    "authorization_code");
        params.add("code",          code);
        params.add("client_id",     config.getClientId());
        params.add("client_secret", config.getClientSecret());
        params.add("redirect_uri",  config.getRedirectUri());
        if (verifier != null) params.add("code_verifier", verifier);

        JsonNode body = rest.exchange(
                loginUrl + "/services/oauth2/token",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                JsonNode.class
        ).getBody();

        SalesforceSession sf = new SalesforceSession();
        sf.setAccessToken(body.get("access_token").asText());
        sf.setInstanceUrl(body.get("instance_url").asText());
        if (body.has("refresh_token")) sf.setRefreshToken(body.get("refresh_token").asText());

        try {
            HttpHeaders idHeaders = new HttpHeaders();
            idHeaders.setBearerAuth(sf.getAccessToken());
            JsonNode id = rest.exchange(
                    body.get("id").asText(), HttpMethod.GET,
                    new HttpEntity<>(idHeaders), JsonNode.class
            ).getBody();
            sf.setUsername(id.get("username").asText());
            sf.setDisplayName(id.get("display_name").asText());
            if (id.has("organization_id")) sf.setOrgId(id.get("organization_id").asText());
        } catch (Exception e) {
            sf.setUsername("Salesforce User");
        }

        return sf;
    }
}
