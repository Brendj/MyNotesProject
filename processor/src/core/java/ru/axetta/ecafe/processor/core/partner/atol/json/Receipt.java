
package ru.axetta.ecafe.processor.core.partner.atol.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "client",
    "company",
    "agent_info",
    "supplier_info",
    "items",
    "payments",
    "vats",
    "total",
    "additional_check_props",
    "cashier",
    "additional_user_props"
})
public class Receipt {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("client")
    private AtolJsonClient client;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("company")
    private Company company;
    @JsonProperty("agent_info")
    private AgentInfo agentInfo;
    @JsonProperty("supplier_info")
    private SupplierInfo supplierInfo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    private List<AtolJsonItem> items = null;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payments")
    private List<AtolJsonPayment> payments = null;
    @JsonProperty("vats")
    private List<Vat_> vats = null;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    private Double total;
    @JsonProperty("additional_check_props")
    private String additionalCheckProps;
    @JsonProperty("cashier")
    private String cashier;
    @JsonProperty("additional_user_props")
    private AdditionalUserProps additionalUserProps;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("client")
    public AtolJsonClient getClient() {
        return client;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("client")
    public void setClient(AtolJsonClient client) {
        this.client = client;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("company")
    public Company getCompany() {
        return company;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("company")
    public void setCompany(Company company) {
        this.company = company;
    }

    @JsonProperty("agent_info")
    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    @JsonProperty("agent_info")
    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    @JsonProperty("supplier_info")
    public SupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    @JsonProperty("supplier_info")
    public void setSupplierInfo(SupplierInfo supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    public List<AtolJsonItem> getItems() {
        return items;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    public void setItems(List<AtolJsonItem> items) {
        this.items = items;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payments")
    public List<AtolJsonPayment> getPayments() {
        return payments;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("payments")
    public void setPayments(List<AtolJsonPayment> payments) {
        this.payments = payments;
    }

    @JsonProperty("vats")
    public List<Vat_> getVats() {
        return vats;
    }

    @JsonProperty("vats")
    public void setVats(List<Vat_> vats) {
        this.vats = vats;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    public Double getTotal() {
        return total;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    public void setTotal(Double total) {
        this.total = total;
    }

    @JsonProperty("additional_check_props")
    public String getAdditionalCheckProps() {
        return additionalCheckProps;
    }

    @JsonProperty("additional_check_props")
    public void setAdditionalCheckProps(String additionalCheckProps) {
        this.additionalCheckProps = additionalCheckProps;
    }

    @JsonProperty("cashier")
    public String getCashier() {
        return cashier;
    }

    @JsonProperty("cashier")
    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    @JsonProperty("additional_user_props")
    public AdditionalUserProps getAdditionalUserProps() {
        return additionalUserProps;
    }

    @JsonProperty("additional_user_props")
    public void setAdditionalUserProps(AdditionalUserProps additionalUserProps) {
        this.additionalUserProps = additionalUserProps;
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
