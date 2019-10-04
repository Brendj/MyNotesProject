
package ru.axetta.ecafe.processor.core.partner.atol.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "email",
    "sno",
    "inn",
    "payment_address"
})
public class Company {

    @JsonProperty("email")
    private String email;
    @JsonProperty("sno")
    private Company.Sno sno;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("inn")
    private String inn;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payment_address")
    private String paymentAddress;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("sno")
    public Company.Sno getSno() {
        return sno;
    }

    @JsonProperty("sno")
    public void setSno(Company.Sno sno) {
        this.sno = sno;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("inn")
    public String getInn() {
        return inn;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("inn")
    public void setInn(String inn) {
        this.inn = inn;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payment_address")
    public String getPaymentAddress() {
        return paymentAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payment_address")
    public void setPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Sno {

        OSN("osn"),
        USN_INCOME("usn_income"),
        USN_INCOME_OUTCOME("usn_income_outcome"),
        ENVD("envd"),
        ESN("esn"),
        PATENT("patent");
        private final String value;
        private final static Map<String, Company.Sno> CONSTANTS = new HashMap<String, Company.Sno>();

        static {
            for (Company.Sno c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Sno(String value) {
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
        public static Company.Sno fromValue(String value) {
            Company.Sno constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
