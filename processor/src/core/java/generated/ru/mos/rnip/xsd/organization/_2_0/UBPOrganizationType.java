
package generated.ru.mos.rnip.xsd.organization._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBPOrganizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBPOrganizationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.0.1}OrganizationType">
 *       &lt;attribute name="codeUBP" use="required" type="{http://rnip.mos.ru/xsd/Organization/2.0.1}kodUBPType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBPOrganizationType")
public class UBPOrganizationType
    extends OrganizationType
{

    @XmlAttribute(name = "codeUBP", required = true)
    protected String codeUBP;

    /**
     * Gets the value of the codeUBP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeUBP() {
        return codeUBP;
    }

    /**
     * Sets the value of the codeUBP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeUBP(String value) {
        this.codeUBP = value;
    }

}
