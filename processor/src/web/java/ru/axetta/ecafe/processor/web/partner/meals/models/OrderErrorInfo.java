package ru.axetta.ecafe.processor.web.partner.meals.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Информация об ошибке при попытке создания заказа
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderErrorInfo")
public class OrderErrorInfo {
    private Long code = null;
    private String description = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetail details = new ErrorDetail();
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
        this.description = information;
        return this;
    }



    /**
     * Информация возвращаемая в ответе
     * @return information
     **/
    public String getInformation() {
        return description;
    }
    public void setInformation(String information) {
        this.description = information;
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

    public ErrorDetail getDetails() {
        return details;
    }

    public void setDetails(ErrorDetail details) {
        this.details = details;
    }
}
