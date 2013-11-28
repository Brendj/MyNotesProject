
package ru.axetta.ecafe.processor.web.bo.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for banksList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="banksList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Banks" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}BankItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "banksList", propOrder = {
    "banks"
})
public class BanksList {

    @XmlElement(name = "Banks")
    protected List<BankItem> banks;

    /**
     * Gets the value of the banks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the banks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBanks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BankItem }
     * 
     * 
     */
    public List<BankItem> getBanks() {
        if (banks == null) {
            banks = new ArrayList<BankItem>();
        }
        return this.banks;
    }

}
