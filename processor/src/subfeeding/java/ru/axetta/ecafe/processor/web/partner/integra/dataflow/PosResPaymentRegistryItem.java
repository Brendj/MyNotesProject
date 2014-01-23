package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.10.13
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PosResPaymentRegistryItem", propOrder = {
        "idOfOrder", "result", "error"
})
public class PosResPaymentRegistryItem {

    @XmlAttribute(name = "IdOfOrder")
    protected Long idOfOrder;
    @XmlAttribute(name = "Result")
    protected Integer result;
    @XmlAttribute(name = "Error")
    protected String error;

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
