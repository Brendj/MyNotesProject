
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.MessageTypeSelector;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.XMLDSigSignatureType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;choice>
 *           &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}MessageTypeSelector"/>
 *           &lt;element name="OriginalMessageID">
 *             &lt;complexType>
 *               &lt;simpleContent>
 *                 &lt;extension base="&lt;urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2>UUID">
 *                   &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *                 &lt;/extension>
 *               &lt;/simpleContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="Sender">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Mnemonic" type="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}string-50"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "messageTypeSelector",
    "originalMessageID",
    "sender",
    "callerInformationSystemSignature"
})
@XmlRootElement(name = "GetResponseRequest")
public class GetResponseRequest {

    @XmlElement(name = "MessageTypeSelector", namespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2")
    protected MessageTypeSelector messageTypeSelector;
    @XmlElement(name = "OriginalMessageID")
    protected GetResponseRequest.OriginalMessageID originalMessageID;
    @XmlElement(name = "Sender", required = true)
    protected GetResponseRequest.Sender sender;
    @XmlElement(name = "CallerInformationSystemSignature")
    protected XMLDSigSignatureType callerInformationSystemSignature;

    /**
     * 
     * 								См. описание
     * 								{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}MessageTypeSelector
     * 							
     * 
     * @return
     *     possible object is
     *     {@link MessageTypeSelector }
     *     
     */
    public MessageTypeSelector getMessageTypeSelector() {
        return messageTypeSelector;
    }

    /**
     * Sets the value of the messageTypeSelector property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTypeSelector }
     *     
     */
    public void setMessageTypeSelector(MessageTypeSelector value) {
        this.messageTypeSelector = value;
    }

    /**
     * Gets the value of the originalMessageID property.
     * 
     * @return
     *     possible object is
     *     {@link GetResponseRequest.OriginalMessageID }
     *     
     */
    public GetResponseRequest.OriginalMessageID getOriginalMessageID() {
        return originalMessageID;
    }

    /**
     * Sets the value of the originalMessageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResponseRequest.OriginalMessageID }
     *     
     */
    public void setOriginalMessageID(GetResponseRequest.OriginalMessageID value) {
        this.originalMessageID = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link GetResponseRequest.Sender }
     *     
     */
    public GetResponseRequest.Sender getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResponseRequest.Sender }
     *     
     */
    public void setSender(GetResponseRequest.Sender value) {
        this.sender = value;
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


    /**
     * 
     * 									Идентификатор, присвоенный сообщению отправителем.
     * 								
     * 
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2>UUID">
     *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class OriginalMessageID {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "Id")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * 
         * 				Строковое представление UUID.
         * 				В СМЭВ UUID используются в качестве идентификаторов сообщений.
         * 				Идентификаторы присваиваются сообщеням отправителями.
         * 			
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

    }


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
     *         &lt;element name="Mnemonic" type="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}string-50"/>
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
