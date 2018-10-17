
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.AttachmentContentList;
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
 *         &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.2}SenderProvidedRequestData"/>
 *         &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}AttachmentContentList" minOccurs="0"/>
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
    "senderProvidedRequestData",
    "attachmentContentList",
    "callerInformationSystemSignature"
})
@XmlRootElement(name = "SendRequestRequest")
public class SendRequestRequest {

    @XmlElement(name = "SenderProvidedRequestData", required = true)
    protected SenderProvidedRequestData senderProvidedRequestData;
    @XmlElement(name = "AttachmentContentList", namespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2")
    protected AttachmentContentList attachmentContentList;
    @XmlElement(name = "CallerInformationSystemSignature")
    protected XMLDSigSignatureType callerInformationSystemSignature;

    /**
     * �������������� ����� ������� + ��������� ������, ����������� ������������.
     * 
     * @return
     *     possible object is
     *     {@link SenderProvidedRequestData }
     *     
     */
    public SenderProvidedRequestData getSenderProvidedRequestData() {
        return senderProvidedRequestData;
    }

    /**
     * Sets the value of the senderProvidedRequestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SenderProvidedRequestData }
     *     
     */
    public void setSenderProvidedRequestData(SenderProvidedRequestData value) {
        this.senderProvidedRequestData = value;
    }

    /**
     * ��������� ����� - ����������. ���������� �������� ��-��� ��-��, ����� �� �������� ������ MTOM.
     * 
     * @return
     *     possible object is
     *     {@link AttachmentContentList }
     *     
     */
    public AttachmentContentList getAttachmentContentList() {
        return attachmentContentList;
    }

    /**
     * Sets the value of the attachmentContentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttachmentContentList }
     *     
     */
    public void setAttachmentContentList(AttachmentContentList value) {
        this.attachmentContentList = value;
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

}
