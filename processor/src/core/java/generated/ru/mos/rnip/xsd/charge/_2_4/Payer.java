
package generated.ru.mos.rnip.xsd.charge._2_4;

import generated.ru.mos.rnip.xsd.common._2_1.PayerType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerType">
 *       &lt;attribute name="payerName" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="160"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="additionalPayerIdentifier">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerIdentifierType">
 *             &lt;pattern value="(1((0[1-9])|(1[0-5])|(2[12456789])|(3[0])|(99))[0-9a-zA-Zа-яА-Я]{19})|(200\d{14}[A-Z0-9]{2}\d{3})|(300\d{14}[A-Z0-9]{2}\d{3}|3[0]{7}\d{9}[A-Z0-9]{2}\d{3})|(4[0]{9}\d{12})"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Payer")
public class Payer
    extends PayerType
{

    @XmlAttribute(name = "payerName", required = true)
    protected String payerName;
    @XmlAttribute(name = "additionalPayerIdentifier")
    protected String additionalPayerIdentifier;

    /**
     * Gets the value of the payerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayerName() {
        return payerName;
    }

    /**
     * Sets the value of the payerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayerName(String value) {
        this.payerName = value;
    }

    /**
     * Gets the value of the additionalPayerIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalPayerIdentifier() {
        return additionalPayerIdentifier;
    }

    /**
     * Sets the value of the additionalPayerIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalPayerIdentifier(String value) {
        this.additionalPayerIdentifier = value;
    }

}
