
package ru.axetta.ecafe.processor.core.partner.atol.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "name",
    "price",
    "quantity",
    "sum",
    "measurement_unit",
    "payment_method",
    "payment_object",
    "nomenclature_code",
    "vat",
    "agent_info",
    "supplier_info",
    "user_data",
    "excise",
    "country_code",
    "declaration_number"
})
public class AtolJsonItem {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    private String name;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    private Double price;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("quantity")
    private Integer quantity;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sum")
    private Object sum;
    @JsonProperty("measurement_unit")
    private String measurementUnit;
    @JsonProperty("payment_method")
    private AtolJsonItem.PaymentMethod paymentMethod;
    @JsonProperty("payment_object")
    private AtolJsonItem.PaymentObject paymentObject;
    @JsonProperty("nomenclature_code")
    private String nomenclatureCode;
    @JsonProperty("vat")
    private Vat vat;
    @JsonProperty("agent_info")
    private AgentInfo_ agentInfo;
    @JsonProperty("supplier_info")
    private SupplierInfo_ supplierInfo;
    @JsonProperty("user_data")
    private String userData;
    @JsonProperty("excise")
    private Float excise;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("declaration_number")
    private String declarationNumber;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("quantity")
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("quantity")
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    @JsonProperty("measurement_unit")
    public String getMeasurementUnit() {
        return measurementUnit;
    }

    @JsonProperty("measurement_unit")
    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @JsonProperty("payment_method")
    public AtolJsonItem.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @JsonProperty("payment_method")
    public void setPaymentMethod(AtolJsonItem.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @JsonProperty("payment_object")
    public AtolJsonItem.PaymentObject getPaymentObject() {
        return paymentObject;
    }

    @JsonProperty("payment_object")
    public void setPaymentObject(AtolJsonItem.PaymentObject paymentObject) {
        this.paymentObject = paymentObject;
    }

    @JsonProperty("nomenclature_code")
    public String getNomenclatureCode() {
        return nomenclatureCode;
    }

    @JsonProperty("nomenclature_code")
    public void setNomenclatureCode(String nomenclatureCode) {
        this.nomenclatureCode = nomenclatureCode;
    }

    @JsonProperty("vat")
    public Vat getVat() {
        return vat;
    }

    @JsonProperty("vat")
    public void setVat(Vat vat) {
        this.vat = vat;
    }

    @JsonProperty("agent_info")
    public AgentInfo_ getAgentInfo() {
        return agentInfo;
    }

    @JsonProperty("agent_info")
    public void setAgentInfo(AgentInfo_ agentInfo) {
        this.agentInfo = agentInfo;
    }

    @JsonProperty("supplier_info")
    public SupplierInfo_ getSupplierInfo() {
        return supplierInfo;
    }

    @JsonProperty("supplier_info")
    public void setSupplierInfo(SupplierInfo_ supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    @JsonProperty("user_data")
    public String getUserData() {
        return userData;
    }

    @JsonProperty("user_data")
    public void setUserData(String userData) {
        this.userData = userData;
    }

    @JsonProperty("excise")
    public Float getExcise() {
        return excise;
    }

    @JsonProperty("excise")
    public void setExcise(Float excise) {
        this.excise = excise;
    }

    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("country_code")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("declaration_number")
    public String getDeclarationNumber() {
        return declarationNumber;
    }

    @JsonProperty("declaration_number")
    public void setDeclarationNumber(String declarationNumber) {
        this.declarationNumber = declarationNumber;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum PaymentMethod {

        FULL_PREPAYMENT("full_prepayment"),
        PREPAYMENT("prepayment"),
        ADVANCE("advance"),
        FULL_PAYMENT("full_payment"),
        PARTIAL_PAYMENT("partial_payment"),
        CREDIT("credit"),
        CREDIT_PAYMENT("credit_payment");
        private final String value;
        private final static Map<String, AtolJsonItem.PaymentMethod> CONSTANTS = new HashMap<String, AtolJsonItem.PaymentMethod>();

        static {
            for (AtolJsonItem.PaymentMethod c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private PaymentMethod(String value) {
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
        public static AtolJsonItem.PaymentMethod fromValue(String value) {
            AtolJsonItem.PaymentMethod constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum PaymentObject {

        COMMODITY("commodity"),
        EXCISE("excise"),
        JOB("job"),
        SERVICE("service"),
        GAMBLING_BET("gambling_bet"),
        GAMBLING_PRIZE("gambling_prize"),
        LOTTERY("lottery"),
        LOTTERY_PRIZE("lottery_prize"),
        INTELLECTUAL_ACTIVITY("intellectual_activity"),
        PAYMENT("payment"),
        AGENT_COMMISSION("agent_commission"),
        COMPOSITE("composite"),
        ANOTHER("another"),
        PROPERTY_RIGHT("property_right"),
        NON_OPERATING_GAIN("non-operating_gain"),
        INSURANCE_PREMIUM("insurance_premium"),
        SALES_TAX("sales_tax"),
        RESORT_FEE("resort_fee");
        private final String value;
        private final static Map<String, AtolJsonItem.PaymentObject> CONSTANTS = new HashMap<String, AtolJsonItem.PaymentObject>();

        static {
            for (AtolJsonItem.PaymentObject c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private PaymentObject(String value) {
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
        public static AtolJsonItem.PaymentObject fromValue(String value) {
            AtolJsonItem.PaymentObject constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
