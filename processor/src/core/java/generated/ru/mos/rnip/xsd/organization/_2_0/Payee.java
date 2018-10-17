
package generated.ru.mos.rnip.xsd.organization._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.OrgAccount;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.0.1}OrganizationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}OrgAccount"/>
 *       &lt;/sequence>
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

    @XmlElement(name = "OrgAccount", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1", required = true)
    protected OrgAccount orgAccount;

    /**
     * ��������� ����� �����������
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

}
