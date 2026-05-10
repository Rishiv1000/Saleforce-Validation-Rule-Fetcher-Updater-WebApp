package com.sfmanager.controller;

import com.sfmanager.config.SalesforceConfig;
import com.sfmanager.model.SalesforceSession;
import com.sfmanager.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;
    private final SalesforceConfig config;

    @GetMapping("/auth/login")
    public void login(@RequestParam(defaultValue = "production") String env,
                      HttpSession session, HttpServletResponse response) throws Exception {

        String loginUrl = "sandbox".equals(env)
                ? "https://test.salesforce.com"
                : "https://login.salesforce.com";

        session.setAttribute("loginUrl", loginUrl);

        // No PKCE — plain OAuth2 Authorization Code flow
        String authUrl = oAuthService.buildAuthorizationUrl(loginUrl, null);
        response.sendRedirect(authUrl);
    }

    @GetMapping({"/auth/callback", "/oauth2/callback"})
    public void callback(@RequestParam(required = false) String code,
                         @RequestParam(required = false) String error,
                         @RequestParam(name = "error_description", required = false) String errorDescription,
                         HttpSession session, HttpServletResponse response) throws Exception {

        String frontend = config.getFrontendUrl();

        if (error != null) {
            response.sendRedirect(frontend + "?error=" +
                URLEncoder.encode(errorDescription != null ? errorDescription : error, StandardCharsets.UTF_8));
            return;
        }

        if (code == null) {
            response.sendRedirect(frontend + "?error=No+authorization+code+received");
            return;
        }

        String loginUrl = (String) session.getAttribute("loginUrl");
        if (loginUrl == null) loginUrl = "https://login.salesforce.com";

        try {
            SalesforceSession sf = oAuthService.exchangeCode(code, null, loginUrl);

            session.setAttribute("accessToken",  sf.getAccessToken());
            session.setAttribute("instanceUrl",  sf.getInstanceUrl());
            session.setAttribute("refreshToken", sf.getRefreshToken());
            session.setAttribute("username",     sf.getUsername());
            session.setAttribute("displayName",  sf.getDisplayName());
            session.setAttribute("orgId",        sf.getOrgId());

            response.sendRedirect(frontend);
        } catch (Exception e) {
            response.sendRedirect(frontend + "?error=" +
                URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/auth/user")
    public ResponseEntity<Map<String, Object>> getUser(HttpSession session) {
        String token = (String) session.getAttribute("accessToken");
        if (token != null) {
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username",      session.getAttribute("username"),
                "displayName",   session.getAttribute("displayName"),
                "orgId",         session.getAttribute("orgId")
            ));
        }
        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true));
    }
}
