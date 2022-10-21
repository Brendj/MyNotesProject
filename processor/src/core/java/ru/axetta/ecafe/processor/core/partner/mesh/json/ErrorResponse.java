package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "error_code",
        "error_description",
        "details"
})
public class ErrorResponse {
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_description")
    private String errorDescription;
    @JsonProperty("details")
    private ErrorDetails details;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("error_code")
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty("error_code")
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @JsonProperty("details")
    public ErrorDetails getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(ErrorDetails details) {
        this.details = details;
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
