package ru.axetta.ecafe.processor.web.partner.meals.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Информация об ошибке при попытке создания заказа
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorDetail")
public class ErrorDetail {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long balanceLimit = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long balance = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String buffetOpenAt = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String buffetCloseAt = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long foodboxOrderId = null;


    /**
     * Лимит дневных трат. Атрибут передаётся в случае ошибки, связанной с превышением дневного лимита
     * @return currentBalanceLimit
     **/
    public Long getBalanceLimit() {
        return balanceLimit;
    }
    public void setBalanceLimit(Long balanceLimit) {
        this.balanceLimit = balanceLimit;
    }
    public ErrorDetail currentBalance(Long currentBalance) {
        this.balance = currentBalance;
        return this;
    }

    /**
     * Остаток денежных средств. Атрибут передаётся в случае ошибки, связанной с нехваткой средств на балансе при попытке заказа
     * @return currentBalance
     **/
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public String getBuffetOpenAt() {
        return buffetOpenAt;
    }

    public void setBuffetOpenAt(String buffetOpenAt) {
        this.buffetOpenAt = buffetOpenAt;
    }

    public String getBuffetCloseAt() {
        return buffetCloseAt;
    }

    public void setBuffetCloseAt(String buffetCloseAt) {
        this.buffetCloseAt = buffetCloseAt;
    }

    public Long getFoodboxOrderId() {
        return foodboxOrderId;
    }

    public void setFoodboxOrderId(Long foodboxOrderId) {
        this.foodboxOrderId = foodboxOrderId;
    }
}
