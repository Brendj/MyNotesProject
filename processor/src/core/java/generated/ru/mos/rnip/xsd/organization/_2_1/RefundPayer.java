
package generated.ru.mos.rnip.xsd.organization._2_1;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.1.1}OrganizationType">
 *       &lt;attribute name="codeUBP" use="required" type="{http://rnip.mos.ru/xsd/Organization/2.1.1}kodUBPType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "RefundPayer")
public class RefundPayer
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
