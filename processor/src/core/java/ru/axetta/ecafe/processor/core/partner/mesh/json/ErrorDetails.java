package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "exception",
        "validation_errors"
})
public class ErrorDetails {
    @JsonProperty("exception")
    private String exception;
    @JsonProperty("validation_errors")
    private List<ValidationError> validationErrors = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("exception")
    public String getException() {
        return exception;
    }

    @JsonProperty("exception")
    public void setException(String exception) {
        this.exception = exception;
    }

    @JsonProperty("validation_errors")
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @JsonProperty("validation_errors")
    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
