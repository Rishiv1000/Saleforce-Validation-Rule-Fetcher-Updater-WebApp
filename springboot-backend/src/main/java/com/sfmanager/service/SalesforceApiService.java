package com.sfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sfmanager.model.SalesforceSession;
import com.sfmanager.model.ValidationRule;
import com.sfmanager.request.BulkToggleRequest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesforceApiService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rest;

    public SalesforceApiService() {
        // Use Apache HttpClient to support PATCH method
        HttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);
        this.rest = new RestTemplate(factory);
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private String url(String instanceUrl, String path) {
        return instanceUrl + "/services/data/v59.0/tooling" + path;
    }

    public List<ValidationRule> getValidationRules(SalesforceSession sf) throws Exception {
        String soql = "SELECT+Id,ValidationName,Active,Description,ErrorMessage" +
                      "+FROM+ValidationRule+WHERE+EntityDefinitionId='Account'";
        JsonNode body = rest.exchange(
                url(sf.getInstanceUrl(), "/query/?q=" + soql),
                HttpMethod.GET,
                new HttpEntity<>(headers(sf.getAccessToken())),
                JsonNode.class
        ).getBody();

        List<ValidationRule> rules = new ArrayList<>();
        JsonNode records = body.get("records");
        if (records != null && records.isArray()) {
            for (JsonNode r : records) rules.add(mapper.treeToValue(r, ValidationRule.class));
        }
        return rules;
    }

    private JsonNode getRuleMetadata(SalesforceSession sf, String id) throws Exception {
        return rest.exchange(
                url(sf.getInstanceUrl(), "/sobjects/ValidationRule/" + id),
                HttpMethod.GET,
                new HttpEntity<>(headers(sf.getAccessToken())),
                JsonNode.class
        ).getBody();
    }

    public void toggleRule(SalesforceSession sf, String id, boolean active) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new RuntimeException("Rule ID is null or empty");
        }

        JsonNode existing = getRuleMetadata(sf, id);
        JsonNode metaNode = existing.get("Metadata");
        if (metaNode == null) {
            throw new RuntimeException("Metadata not found for rule: " + id);
        }

        ObjectNode meta = (ObjectNode) mapper.readTree(metaNode.toString());
        meta.put("active", active);

        ObjectNode body = mapper.createObjectNode();
        body.set("Metadata", meta);

        rest.exchange(
                url(sf.getInstanceUrl(), "/sobjects/ValidationRule/" + id),
                HttpMethod.PATCH,
                new HttpEntity<>(body.toString(), headers(sf.getAccessToken())),
                Void.class
        );
    }

    public void bulkToggle(SalesforceSession sf, List<BulkToggleRequest.RuleToggle> rules) throws Exception {
        for (BulkToggleRequest.RuleToggle r : rules) {
            toggleRule(sf, r.getId(), r.isActive());
        }
    }
}
