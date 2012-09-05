
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for clientSmsListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clientSmsListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clientSmsList" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ClientSmsList" minOccurs="0"/>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientSmsListResult", propOrder = {
    "clientSmsList",
    "resultCode",
    "description"
})
public class ClientSmsListResult {

    protected ClientSmsList clientSmsList;
    protected Long resultCode;
    protected String description;

    /**
     * Gets the value of the clientSmsList property.
     * 
     * @return
     *     possible object is
     *     {@link ClientSmsList }
     *     
     */
    public ClientSmsList getClientSmsList() {
        return clientSmsList;
    }

    /**
     * Sets the value of the clientSmsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientSmsList }
     *     
     */
    public void setClientSmsList(ClientSmsList value) {
        this.clientSmsList = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResultCode(Long value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
