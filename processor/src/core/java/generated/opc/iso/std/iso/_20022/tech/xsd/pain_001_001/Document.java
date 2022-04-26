
package generated.opc.iso.std.iso._20022.tech.xsd.pain_001_001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CstmrCdtTrfInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}CustomerCreditTransferInitiationV03"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentItem", propOrder = {
    "cstmrCdtTrfInitn"
})
public class Document {

    @XmlElement(name = "CstmrCdtTrfInitn", required = true)
    protected CustomerCreditTransferInitiationV03 cstmrCdtTrfInitn;

    /**
     * Gets the value of the cstmrCdtTrfInitn property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerCreditTransferInitiationV03 }
     *     
     */
    public CustomerCreditTransferInitiationV03 getCstmrCdtTrfInitn() {
        return cstmrCdtTrfInitn;
    }

    /**
     * Sets the value of the cstmrCdtTrfInitn property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerCreditTransferInitiationV03 }
     *     
     */
    public void setCstmrCdtTrfInitn(CustomerCreditTransferInitiationV03 value) {
        this.cstmrCdtTrfInitn = value;
    }

}
