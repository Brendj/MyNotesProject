
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

        _0(0.0D),
        _1(1.0D),
        _2(2.0D),
        _3(3.0D),
        _4(4.0D),
        _5(5.0D),
        _6(6.0D),
        _7(7.0D),
        _8(8.0D),
        _9(9.0D);
        private final Double value;
        private final static Map<Double, AtolJsonPayment.Type> CONSTANTS = new HashMap<Double, AtolJsonPayment.Type>();

        static {
            for (AtolJsonPayment.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(Double value) {
            this.value = value;
        }

        @JsonValue
        public Double value() {
            return this.value;
        }

        @JsonCreator
        public static AtolJsonPayment.Type fromValue(Double value) {
            AtolJsonPayment.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}
