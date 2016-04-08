
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message;

import generated.smev.gisgmp.client.org.w3._2000._09.xmldsig_.SignatureType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.chargecreationrequest.ChargeCreationRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment.DoAcknowledgmentRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ImportCatalogRequest;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.packagestatusrequest.PackageStatusRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_importrequest.ImportRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.selfadministration.ImportCertificateRequestType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/MessageData}RequestMessageData"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="senderIdentifier" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="6"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="senderRole">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="10"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="callBackURL" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestMessageType", propOrder = {
    "requestMessageData",
    "signature"
})
@XmlRootElement(name = "RequestMessage")
public class RequestMessageType {

    @XmlElementRef(name = "RequestMessageData", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", type = JAXBElement.class)
    protected JAXBElement<?> requestMessageData;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlAttribute(required = true)
    protected String senderIdentifier;
    @XmlAttribute
    protected String senderRole;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String callBackURL;

    /**
     * Gets the value of the requestMessageData property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCertificateRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DataRequest }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link PackageStatusRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCatalogRequest }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentRequestType }{@code >}
     *     
     */
    public JAXBElement<?> getRequestMessageData() {
        return requestMessageData;
    }

    /**
     * Sets the value of the requestMessageData property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCertificateRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DataRequest }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link PackageStatusRequestType }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCatalogRequest }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentRequestType }{@code >}
     *     
     */
    public void setRequestMessageData(JAXBElement<?> value) {
        this.requestMessageData = ((JAXBElement<?> ) value);
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

    /**
     * Gets the value of the senderRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderRole() {
        return senderRole;
    }

    /**
     * Sets the value of the senderRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderRole(String value) {
        this.senderRole = value;
    }

    /**
     * Gets the value of the callBackURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallBackURL() {
        return callBackURL;
    }

    /**
     * Sets the value of the callBackURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallBackURL(String value) {
        this.callBackURL = value;
    }

}
