
package generated.registry.manual_synch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registerClients complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registerClients">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="orgId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="clientDescList" type="{http://ru.axetta.ecafe}clientDesc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="checkFullNameUniqueness" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerClients", propOrder = {
    "orgId",
    "clientDescList",
    "checkFullNameUniqueness"
})
public class RegisterClients {

    protected Long orgId;
    protected List<ClientDesc> clientDescList;
    protected boolean checkFullNameUniqueness;

    /**
     * Gets the value of the orgId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrgId(Long value) {
        this.orgId = value;
    }

    /**
     * Gets the value of the clientDescList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clientDescList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClientDescList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientDesc }
     * 
     * 
     */
    public List<ClientDesc> getClientDescList() {
        if (clientDescList == null) {
            clientDescList = new ArrayList<ClientDesc>();
        }
        return this.clientDescList;
    }

    /**
     * Gets the value of the checkFullNameUniqueness property.
     * 
     */
    public boolean isCheckFullNameUniqueness() {
        return checkFullNameUniqueness;
    }

    /**
     * Sets the value of the checkFullNameUniqueness property.
     * 
     */
    public void setCheckFullNameUniqueness(boolean value) {
        this.checkFullNameUniqueness = value;
    }

}
