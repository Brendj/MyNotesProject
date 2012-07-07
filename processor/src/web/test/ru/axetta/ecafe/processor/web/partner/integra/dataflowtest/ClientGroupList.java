
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClientGroupList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClientGroupList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="G" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ClientGroupItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientGroupList", propOrder = {
    "g"
})
public class ClientGroupList {

    @XmlElement(name = "G")
    protected List<ClientGroupItem> g;

    /**
     * Gets the value of the g property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the g property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getG().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientGroupItem }
     * 
     * 
     */
    public List<ClientGroupItem> getG() {
        if (g == null) {
            g = new ArrayList<ClientGroupItem>();
        }
        return this.g;
    }

}
