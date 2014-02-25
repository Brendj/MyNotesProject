
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for refreshRegistryChangeItemsInternal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="refreshRegistryChangeItemsInternal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "refreshRegistryChangeItemsInternal", propOrder = {
    "idOfOrg"
})
public class RefreshRegistryChangeItemsInternal {

    protected long idOfOrg;

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

}
