
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message;

import generated.smev.gisgmp.client.org.w3._2000._09.xmldsig_.SignatureType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.chargecreationresponse.ChargeCreationResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment.DoAcknowledgmentResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse.ExportIncomesResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ExportCatalogResponse;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_chargesresponse.ExportChargesResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.ticket.TicketType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ResponseMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/MessageData}ResponseMessageData"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="rqId" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="senderIdentifier" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="6"/>
 *             &lt;maxLength value="32"/>
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
@XmlType(name = "ResponseMessageType", propOrder = {
    "responseMessageData",
    "signature"
})
public class ResponseMessageType {

    @XmlElementRef(name = "ResponseMessageData", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", type = JAXBElement.class)
    protected JAXBElement<?> responseMessageData;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rqId;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlAttribute(required = true)
    protected String senderIdentifier;

    /**
     * ������ ������
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportQuittanceResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportChargesResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportPaymentsResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportCatalogResponse }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportIncomesResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link TicketType }{@code >}
     *     
     */
    public JAXBElement<?> getResponseMessageData() {
        return responseMessageData;
    }

    /**
     * Sets the value of the responseMessageData property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportQuittanceResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportChargesResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportPaymentsResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportCatalogResponse }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportIncomesResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentResponseType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link TicketType }{@code >}
     *     
     */
    public void setResponseMessageData(JAXBElement<?> value) {
        this.responseMessageData = ((JAXBElement<?> ) value);
    }

    /**
     * ��
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
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

    /**
     * Gets the value of the rqId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRqId() {
        return rqId;
    }

    /**
     * Sets the value of the rqId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRqId(String value) {
        this.rqId = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the senderIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    /**
     * Sets the value of the senderIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderIdentifier(String value) {
        this.senderIdentifier = value;
    }

}
