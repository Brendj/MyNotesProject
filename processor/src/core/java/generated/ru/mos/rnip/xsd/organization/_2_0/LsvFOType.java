
package generated.ru.mos.rnip.xsd.organization._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LsvFOType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LsvFOType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="nameFO" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="255"/>
 *             &lt;pattern value="\S+[\S\s]*\S+"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="accountNumberFO" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="20"/>
 *             &lt;pattern value="\S+[\S\s]*\S+"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LsvFOType")
public class LsvFOType {

    @XmlAttribute(name = "nameFO", required = true)
    protected String nameFO;
    @XmlAttribute(name = "accountNumberFO", required = true)
    protected String accountNumberFO;

    /**
     * Gets the value of the nameFO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFO() {
        return nameFO;
    }

    /**
     * Sets the value of the nameFO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFO(String value) {
        this.nameFO = value;
    }

    /**
     * Gets the value of the accountNumberFO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumberFO() {
        return accountNumberFO;
    }

    /**
     * Sets the value of the accountNumberFO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNumberFO(String value) {
        this.accountNumberFO = value;
    }

}
