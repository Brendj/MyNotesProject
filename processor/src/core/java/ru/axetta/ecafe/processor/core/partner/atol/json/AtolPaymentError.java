/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol.json;

/**
 * Created by nuc on 03.09.2019.
 */

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "error_id",
        "code",
        "text",
        "type"
})
public class AtolPaymentError {
    @JsonProperty("error_id")
    private String errorId;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("text")
    private String text;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("error_id")
    public String getErrorId() {
        return errorId;
    }

    @JsonProperty("error_id")
    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("errorId", errorId).append("code", code).append("text", text).append("type", type).append("additionalProperties", additionalProperties).toString();
    }
}
