
package generated.ru.mos.rnip.xsd.common._2_0;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for PayerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="payerIdentifier" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayerType")
@XmlSeeAlso({
        generated.ru.mos.rnip.xsd.charge._2_0.ChargeTemplateType.Payer.class,
        generated.ru.mos.rnip.xsd.charge._2_0.ChargeType.Payer.class
})
public class PayerType {

    @XmlAttribute(name = "payerIdentifier", required = true)
    protected String payerIdentifier;

    /**
     * Gets the value of the payerIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayerIdentifier() {
        return payerIdentifier;
    }

    /**
     * Sets the value of the payerIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayerIdentifier(String value) {
        this.payerIdentifier = value;
    }

}
