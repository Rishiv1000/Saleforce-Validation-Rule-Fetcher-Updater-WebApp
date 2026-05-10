package com.sfmanager.request;

import lombok.Data;
import java.util.List;

@Data
public class BulkToggleRequest {
    private List<RuleToggle> rules;

    @Data
    public static class RuleToggle {
        private String id;
        private boolean active;
    }
}
