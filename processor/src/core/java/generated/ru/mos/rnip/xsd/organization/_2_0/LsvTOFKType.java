
package generated.ru.mos.rnip.xsd.organization._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LsvTOFKType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LsvTOFKType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="nameTOFK" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="255"/>
 *             &lt;pattern value="\S+[\S\s]*\S+"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="codeTOFK" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\d{4}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="accountNumberTOFK" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[\d\w]{11}"/>
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
@XmlType(name = "LsvTOFKType")
public class LsvTOFKType {

    @XmlAttribute(name = "nameTOFK", required = true)
    protected String nameTOFK;
    @XmlAttribute(name = "codeTOFK", required = true)
    protected String codeTOFK;
    @XmlAttribute(name = "accountNumberTOFK", required = true)
    protected String accountNumberTOFK;

    /**
     * Gets the value of the nameTOFK property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameTOFK() {
        return nameTOFK;
    }

    /**
     * Sets the value of the nameTOFK property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameTOFK(String value) {
        this.nameTOFK = value;
    }

    /**
     * Gets the value of the codeTOFK property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeTOFK() {
        return codeTOFK;
    }

    /**
     * Sets the value of the codeTOFK property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeTOFK(String value) {
        this.codeTOFK = value;
    }

    /**
     * Gets the value of the accountNumberTOFK property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumberTOFK() {
        return accountNumberTOFK;
    }

    /**
     * Sets the value of the accountNumberTOFK property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNumberTOFK(String value) {
        this.accountNumberTOFK = value;
    }

}
