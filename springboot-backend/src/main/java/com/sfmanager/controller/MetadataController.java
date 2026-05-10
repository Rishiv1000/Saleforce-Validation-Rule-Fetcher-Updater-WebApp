package com.sfmanager.controller;

import com.sfmanager.model.SalesforceSession;
import com.sfmanager.model.ValidationRule;
import com.sfmanager.request.BulkToggleRequest;
import com.sfmanager.request.ToggleRequest;
import com.sfmanager.service.SalesforceApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetadataController {

    private final SalesforceApiService sfService;

    private SalesforceSession fromSession(HttpSession session) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) return null;
        SalesforceSession sf = new SalesforceSession();
        sf.setAccessToken(token);
        sf.setInstanceUrl((String) session.getAttribute("instanceUrl"));
        return sf;
    }

    @GetMapping("/validation-rules")
    public ResponseEntity<?> getValidationRules(HttpSession session) {
        SalesforceSession sf = fromSession(session);
        if (sf == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated."));
        try {
            List<ValidationRule> rules = sfService.getValidationRules(sf);
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/validation-rules/bulk-toggle")
    public ResponseEntity<?> bulkToggle(@RequestBody BulkToggleRequest request, HttpSession session) {
        SalesforceSession sf = fromSession(session);
        if (sf == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated."));
        try {
            sfService.bulkToggle(sf, request.getRules());
            return ResponseEntity.ok(Map.of("success", true, "updated", request.getRules().size()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/validation-rules/{id}/toggle")
    public ResponseEntity<?> toggleRule(@PathVariable String id,
                                        @RequestBody ToggleRequest request,
                                        HttpSession session) {
        SalesforceSession sf = fromSession(session);
        if (sf == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated."));
        try {
            sfService.toggleRule(sf, id, request.isActive());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
