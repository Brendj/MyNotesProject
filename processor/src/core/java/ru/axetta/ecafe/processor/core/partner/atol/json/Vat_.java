
package ru.axetta.ecafe.processor.core.partner.atol.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "type",
    "sum"
})
public class Vat_ {

    @JsonProperty("type")
    private Vat_.Type type;
    @JsonProperty("sum")
    private Object sum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("type")
    public Vat_.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Vat_.Type type) {
        this.type = type;
    }

    @JsonProperty("sum")
    public Object getSum() {
        return sum;
    }

    @JsonProperty("sum")
    public void setSum(Object sum) {
        this.sum = sum;
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
        VAT_0("vat0"),
        VAT_10("vat10"),
        VAT_18("vat18"),
        VAT_110("vat110"),
        VAT_118("vat118"),
        VAT_20("vat20"),
        VAT_120("vat120");
        private final String value;
        private final static Map<String, Vat_.Type> CONSTANTS = new HashMap<String, Vat_.Type>();

        static {
            for (Vat_.Type c: values()) {
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
        public static Vat_.Type fromValue(String value) {
            Vat_.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
