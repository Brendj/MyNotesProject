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
@XmlType(name = "OrderErrorInfo")
public class OrderErrorInfo {
    private Long code = null;
    private String information = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long currentBalanceLimit = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long currentBalance = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String buffetOpenTime = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String buffetCloseTime = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long orderNumber = null;
    public OrderErrorInfo code(Long code) {
        this.code = code;
        return this;
    }



    /**
     * Код ошибки, возвращаемый в ответе
     * @return code
     **/
    public Long getCode() {
        return code;
    }
    public void setCode(Long code) {
        this.code = code;
    }
    public OrderErrorInfo information(String information) {
        this.information = information;
        return this;
    }



    /**
     * Информация возвращаемая в ответе
     * @return information
     **/
    public String getInformation() {
        return information;
    }
    public void setInformation(String information) {
        this.information = information;
    }
    public OrderErrorInfo currentBalanceLimit(Long currentBalanceLimit) {
        this.currentBalanceLimit = currentBalanceLimit;
        return this;
    }



    /**
     * Лимит дневных трат. Атрибут передаётся в случае ошибки, связанной с превышением дневного лимита
     * @return currentBalanceLimit
     **/
    public Long getCurrentBalanceLimit() {
        return currentBalanceLimit;
    }
    public void setCurrentBalanceLimit(Long currentBalanceLimit) {
        this.currentBalanceLimit = currentBalanceLimit;
    }
    public OrderErrorInfo currentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
        return this;
    }



    /**
     * Остаток денежных средств. Атрибут передаётся в случае ошибки, связанной с нехваткой средств на балансе при попытке заказа
     * @return currentBalance
     **/
    public Long getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderErrorInfo orderErrorInfo = (OrderErrorInfo) o;
        return Objects.equals(this.code, orderErrorInfo.code) &&
                Objects.equals(this.information, orderErrorInfo.information) &&
                Objects.equals(this.currentBalanceLimit, orderErrorInfo.currentBalanceLimit) &&
                Objects.equals(this.currentBalance, orderErrorInfo.currentBalance);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(code, information, currentBalanceLimit, currentBalance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrderErrorInfo {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    information: ").append(toIndentedString(information)).append("\n");
        sb.append("    currentBalanceLimit: ").append(toIndentedString(currentBalanceLimit)).append("\n");
        sb.append("    currentBalance: ").append(toIndentedString(currentBalance)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public String getBuffetOpenTime() {
        return buffetOpenTime;
    }

    public void setBuffetOpenTime(String buffetOpenTime) {
        this.buffetOpenTime = buffetOpenTime;
    }

    public String getBuffetCloseTime() {
        return buffetCloseTime;
    }

    public void setBuffetCloseTime(String buffetCloseTime) {
        this.buffetCloseTime = buffetCloseTime;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
}
