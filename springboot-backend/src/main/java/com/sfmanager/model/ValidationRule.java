package com.sfmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRule {
    private String Id;
    private String ValidationName;
    private Boolean Active;
    private String Description;
    private String ErrorMessage;
}
