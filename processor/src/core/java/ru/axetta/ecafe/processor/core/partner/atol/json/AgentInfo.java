
package ru.axetta.ecafe.processor.core.partner.atol.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "type",
    "paying_agent",
    "receive_payments_operator",
    "money_transfer_operator"
})
public class AgentInfo {

    @JsonProperty("type")
    private AgentInfo.Type type;
    @JsonProperty("paying_agent")
    private PayingAgent payingAgent;
    @JsonProperty("receive_payments_operator")
    private ReceivePaymentsOperator receivePaymentsOperator;
    @JsonProperty("money_transfer_operator")
    private MoneyTransferOperator moneyTransferOperator;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("type")
    public AgentInfo.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(AgentInfo.Type type) {
        this.type = type;
    }

    @JsonProperty("paying_agent")
    public PayingAgent getPayingAgent() {
        return payingAgent;
    }

    @JsonProperty("paying_agent")
    public void setPayingAgent(PayingAgent payingAgent) {
        this.payingAgent = payingAgent;
    }

    @JsonProperty("receive_payments_operator")
    public ReceivePaymentsOperator getReceivePaymentsOperator() {
        return receivePaymentsOperator;
    }

    @JsonProperty("receive_payments_operator")
    public void setReceivePaymentsOperator(ReceivePaymentsOperator receivePaymentsOperator) {
        this.receivePaymentsOperator = receivePaymentsOperator;
    }

    @JsonProperty("money_transfer_operator")
    public MoneyTransferOperator getMoneyTransferOperator() {
        return moneyTransferOperator;
    }

    @JsonProperty("money_transfer_operator")
    public void setMoneyTransferOperator(MoneyTransferOperator moneyTransferOperator) {
        this.moneyTransferOperator = moneyTransferOperator;
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

        BANK_PAYING_AGENT("bank_paying_agent"),
        BANK_PAYING_SUBAGENT("bank_paying_subagent"),
        PAYING_AGENT("paying_agent"),
        PAYING_SUBAGENT("paying_subagent"),
        ATTORNEY("attorney"),
        COMMISSION_AGENT("commission_agent"),
        ANOTHER("another");
        private final String value;
        private final static Map<String, AgentInfo.Type> CONSTANTS = new HashMap<String, AgentInfo.Type>();

        static {
            for (AgentInfo.Type c: values()) {
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
        public static AgentInfo.Type fromValue(String value) {
            AgentInfo.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
