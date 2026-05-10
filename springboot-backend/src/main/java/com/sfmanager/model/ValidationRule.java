package com.sfmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRule {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("ValidationName")
    private String validationName;

    @JsonProperty("Active")
    private Boolean active;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("ErrorMessage")
    private String errorMessage;
}
