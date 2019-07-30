
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.AckTargetMessage;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.XMLDSigSignatureType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}AckTargetMessage"/>
 *         &lt;element name="CallerInformationSystemSignature" type="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}XMLDSigSignatureType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ackTargetMessage",
    "sender",
    "callerInformationSystemSignature"
})
@XmlRootElement(name = "AckRequest")
public class AckRequest {

    @XmlElement(name = "AckTargetMessage", namespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2", required = true)
    protected AckTargetMessage ackTargetMessage;
    @XmlElement(name = "Sender", required = true)
    protected AckRequest.Sender sender;
    @XmlElement(name = "CallerInformationSystemSignature")
    protected XMLDSigSignatureType callerInformationSystemSignature;

    public AckRequest.Sender getSender() {
        return sender;
    }

    public void setSender(AckRequest.Sender value) {
        this.sender = value;
    }

    /**
     * Gets the value of the ackTargetMessage property.
     * 
     * @return
     *     possible object is
     *     {@link AckTargetMessage }
     *     
     */
    public AckTargetMessage getAckTargetMessage() {
        return ackTargetMessage;
    }

    /**
     * Sets the value of the ackTargetMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AckTargetMessage }
     *     
     */
    public void setAckTargetMessage(AckTargetMessage value) {
        this.ackTargetMessage = value;
    }

    /**
     * Gets the value of the callerInformationSystemSignature property.
     * 
     * @return
     *     possible object is
     *     {@link XMLDSigSignatureType }
     *     
     */
    public XMLDSigSignatureType getCallerInformationSystemSignature() {
        return callerInformationSystemSignature;
    }

    /**
     * Sets the value of the callerInformationSystemSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLDSigSignatureType }
     *     
     */
    public void setCallerInformationSystemSignature(XMLDSigSignatureType value) {
        this.callerInformationSystemSignature = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "mnemonic"
    })
    public static class Sender {

        @XmlElement(name = "Mnemonic", required = true)
        protected String mnemonic;

        /**
         * Gets the value of the mnemonic property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMnemonic() {
            return mnemonic;
        }

        /**
         * Sets the value of the mnemonic property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMnemonic(String value) {
            this.mnemonic = value;
        }

    }

}
