
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
public class AtolJsonPayment {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    private AtolJsonPayment.Type type;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sum")
    private Object sum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public AtolJsonPayment.Type getType() {
        return type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(AtolJsonPayment.Type type) {
        this.type = type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sum")
    public Object getSum() {
        return sum;
    }

    /**
     * 
     * (Required)
     * 
     */
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

        _0(0),
        _1(1),
        _2(2),
        _3(3),
        _4(4),
        _5(5),
        _6(6),
        _7(7),
        _8(8),
        _9(9);
        private final Integer value;
        private final static Map<Integer, AtolJsonPayment.Type> CONSTANTS = new HashMap<Integer, AtolJsonPayment.Type>();

        static {
            for (AtolJsonPayment.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(Integer value) {
            this.value = value;
        }

        @JsonValue
        public Integer value() {
            return this.value;
        }

        @JsonCreator
        public static AtolJsonPayment.Type fromValue(Float value) {
            AtolJsonPayment.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}
