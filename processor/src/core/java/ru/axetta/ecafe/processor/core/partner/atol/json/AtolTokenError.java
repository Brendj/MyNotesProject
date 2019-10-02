/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol.json;

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
public class AtolTokenError {

    @JsonProperty("error_id")
    private String errorId;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("text")
    private String text;
    @JsonProperty("type")
    private AtolTokenError.Type type;
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
    public AtolTokenError.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(AtolTokenError.Type type) {
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

    public enum Type {

        NONE("none"),
        UNKNOWN("unknown"),
        SYSTEM("system"),
        DRIVER("driver"),
        TIMEOUT("timeout");
        private final String value;
        private final static Map<String, AtolTokenError.Type> CONSTANTS = new HashMap<String, AtolTokenError.Type>();

        static {
            for (AtolTokenError.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AtolTokenError.Type fromValue(String value) {
            AtolTokenError.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}