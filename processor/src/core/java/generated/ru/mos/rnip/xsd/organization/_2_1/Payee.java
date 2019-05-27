
package generated.ru.mos.rnip.xsd.organization._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.OrgAccount;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.1.0}OrganizationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.0}OrgAccount"/>
 *       &lt;/sequence>
 *       &lt;attribute name="subsystemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgAccount"
})
@XmlRootElement(name = "Payee")
public class Payee
    extends OrganizationType
{

    @XmlElement(name = "OrgAccount", namespace = "http://rnip.mos.ru/xsd/Common/2.1.0", required = true)
    protected OrgAccount orgAccount;
    @XmlAttribute(name = "subsystemId")
    protected String subsystemId;

    /**
     * Реквизиты счета организации
     * 
     * @return
     *     possible object is
     *     {@link OrgAccount }
     *     
     */
    public OrgAccount getOrgAccount() {
        return orgAccount;
    }

    /**
     * Sets the value of the orgAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrgAccount }
     *     
     */
    public void setOrgAccount(OrgAccount value) {
        this.orgAccount = value;
    }

    /**
     * Gets the value of the subsystemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubsystemId() {
        return subsystemId;
    }

    /**
     * Sets the value of the subsystemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubsystemId(String value) {
        this.subsystemId = value;
    }

}
