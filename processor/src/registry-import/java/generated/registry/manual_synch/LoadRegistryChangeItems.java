
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loadRegistryChangeItems complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loadRegistryChangeItems">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="revisionDate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="actionFilter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nameFilter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadRegistryChangeItems", propOrder = {
    "idOfOrg",
    "revisionDate",
    "actionFilter",
    "nameFilter"
})
public class LoadRegistryChangeItems {

    protected long idOfOrg;
    protected long revisionDate;
    protected int actionFilter;
    protected String nameFilter;

    /**
     * Gets the value of the idOfOrg property.
     * 
     */
    public long getIdOfOrg() {
        return idOfOrg;
    }

    /**
     * Sets the value of the idOfOrg property.
     * 
     */
    public void setIdOfOrg(long value) {
        this.idOfOrg = value;
    }

    /**
     * Gets the value of the revisionDate property.
     * 
     */
    public long getRevisionDate() {
        return revisionDate;
    }

    /**
     * Sets the value of the revisionDate property.
     * 
     */
    public void setRevisionDate(long value) {
        this.revisionDate = value;
    }

    /**
     * Gets the value of the actionFilter property.
     * 
     */
    public int getActionFilter() {
        return actionFilter;
    }

    /**
     * Sets the value of the actionFilter property.
     * 
     */
    public void setActionFilter(int value) {
        this.actionFilter = value;
    }

    /**
     * Gets the value of the nameFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFilter() {
        return nameFilter;
    }

    /**
     * Sets the value of the nameFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFilter(String value) {
        this.nameFilter = value;
    }

}
