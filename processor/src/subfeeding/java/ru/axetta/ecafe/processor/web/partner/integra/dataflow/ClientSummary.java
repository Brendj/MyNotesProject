
package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ClientSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClientSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="DateOfContract" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="StateOfContract" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Balance" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="OverdraftLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientSummary")
public class ClientSummary {

    @XmlAttribute(name = "DateOfContract")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfContract;
    @XmlAttribute(name = "StateOfContract")
    protected String stateOfContract;
    @XmlAttribute(name = "Balance")
    protected Long balance;
    @XmlAttribute(name = "OverdraftLimit")
    protected Long overdraftLimit;

    /**
     * Gets the value of the dateOfContract property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfContract() {
        return dateOfContract;
    }

    /**
     * Sets the value of the dateOfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfContract(XMLGregorianCalendar value) {
        this.dateOfContract = value;
    }

    /**
     * Gets the value of the stateOfContract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateOfContract() {
        return stateOfContract;
    }

    /**
     * Sets the value of the stateOfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateOfContract(String value) {
        this.stateOfContract = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBalance(Long value) {
        this.balance = value;
    }

    /**
     * Gets the value of the overdraftLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Sets the value of the overdraftLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOverdraftLimit(Long value) {
        this.overdraftLimit = value;
    }

}
