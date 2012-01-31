
package ru.msk.schemas.uec.transactionservice.v1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.msk.schemas.uec.transaction.v1.TransactionDescriptionType;


/**
 * Тип для описания тарификационного расчета
 * 
 * <p>Java class for BillType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionDescriptionType" minOccurs="0"/>
 *         &lt;element name="calcDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="customerDescription" type="{http://schemas.msk.ru/uec/TransactionService/v1}CustomerDescriptionType" minOccurs="0"/>
 *         &lt;element name="serviceProvider" type="{http://schemas.msk.ru/uec/TransactionService/v1}ServiceProviderType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillType", propOrder = {
    "transactionDescription",
    "calcDate",
    "value",
    "customerDescription",
    "serviceProvider"
})
public class BillType {

    protected TransactionDescriptionType transactionDescription;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar calcDate;
    @XmlElement(required = true)
    protected BigDecimal value;
    protected CustomerDescriptionType customerDescription;
    @XmlElement(required = true)
    protected ServiceProviderType serviceProvider;

    /**
     * Gets the value of the transactionDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionDescriptionType }
     *     
     */
    public TransactionDescriptionType getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * Sets the value of the transactionDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionDescriptionType }
     *     
     */
    public void setTransactionDescription(TransactionDescriptionType value) {
        this.transactionDescription = value;
    }

    /**
     * Gets the value of the calcDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCalcDate() {
        return calcDate;
    }

    /**
     * Sets the value of the calcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCalcDate(XMLGregorianCalendar value) {
        this.calcDate = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the customerDescription property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerDescriptionType }
     *     
     */
    public CustomerDescriptionType getCustomerDescription() {
        return customerDescription;
    }

    /**
     * Sets the value of the customerDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerDescriptionType }
     *     
     */
    public void setCustomerDescription(CustomerDescriptionType value) {
        this.customerDescription = value;
    }

    /**
     * Gets the value of the serviceProvider property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProviderType }
     *     
     */
    public ServiceProviderType getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Sets the value of the serviceProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProviderType }
     *     
     */
    public void setServiceProvider(ServiceProviderType value) {
        this.serviceProvider = value;
    }

}
