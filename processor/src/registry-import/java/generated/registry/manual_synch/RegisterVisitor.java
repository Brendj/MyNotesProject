
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registerVisitor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registerVisitor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="orgId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="visitor" type="{http://ru.axetta.ecafe}visitorItem" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerVisitor", propOrder = {
    "orgId",
    "visitor"
})
public class RegisterVisitor {

    protected Long orgId;
    protected VisitorItem visitor;

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
     * Gets the value of the visitor property.
     * 
     * @return
     *     possible object is
     *     {@link VisitorItem }
     *     
     */
    public VisitorItem getVisitor() {
        return visitor;
    }

    /**
     * Sets the value of the visitor property.
     * 
     * @param value
     *     allowed object is
     *     {@link VisitorItem }
     *     
     */
    public void setVisitor(VisitorItem value) {
        this.visitor = value;
    }

}
