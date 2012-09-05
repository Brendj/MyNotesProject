
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for clientGroupListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clientGroupListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clientGroupList" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ClientGroupList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientGroupListResult", propOrder = {
    "clientGroupList"
})
public class ClientGroupListResult {

    protected ClientGroupList clientGroupList;

    /**
     * Gets the value of the clientGroupList property.
     * 
     * @return
     *     possible object is
     *     {@link ClientGroupList }
     *     
     */
    public ClientGroupList getClientGroupList() {
        return clientGroupList;
    }

    /**
     * Sets the value of the clientGroupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientGroupList }
     *     
     */
    public void setClientGroupList(ClientGroupList value) {
        this.clientGroupList = value;
    }

}
