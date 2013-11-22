/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-325 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.14 at 07:18:21 PM MSK 
//


package generated.rnip.roskazna.xsd.responsetemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.rnip.roskazna.xsd.doacknowledgmentresponse.DoAcknowledgmentResponseType;
import generated.rnip.roskazna.xsd.errinfo.ErrInfo;
import generated.rnip.roskazna.xsd.exportcatsrvsresponse.ExportCatalogResponse;
import generated.rnip.roskazna.xsd.exportincomesresponse.ExportIncomesResponse;
import generated.rnip.roskazna.xsd.exportpaymentsresponse.ExportPaymentsResponse;
import generated.rnip.roskazna.xsd.exportquittanceresponse.ExportQuittanceResponse;
import generated.rnip.roskazna.xsd.pgu_chargesresponse.ExportChargesResponse;
import generated.rnip.roskazna.xsd.postblock.PostBlock;
import generated.rnip.roskazna.xsd.ticket.Ticket;


/**
 * <p>Java class for ResponseTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PostBlock" type="{http://roskazna.ru/xsd/PostBlock}PostBlock"/>
 *         &lt;element name="RequestProcessResult" type="{http://roskazna.ru/xsd/ErrInfo}ErrInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseTemplate", propOrder = {
    "postBlock",
    "requestProcessResult"
})
@XmlSeeAlso({
    Ticket.class,
    ExportCatalogResponse.class,
    ExportChargesResponse.class,
    ExportPaymentsResponse.class,
    ExportIncomesResponse.class,
    ExportQuittanceResponse.class,
    DoAcknowledgmentResponseType.class
})
public class ResponseTemplate {

    @XmlElement(name = "PostBlock", required = true)
    protected PostBlock postBlock;
    @XmlElement(name = "RequestProcessResult")
    protected ErrInfo requestProcessResult;

    /**
     * Gets the value of the postBlock property.
     * 
     * @return
     *     possible object is
     *     {@link PostBlock }
     *     
     */
    public PostBlock getPostBlock() {
        return postBlock;
    }

    /**
     * Sets the value of the postBlock property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostBlock }
     *     
     */
    public void setPostBlock(PostBlock value) {
        this.postBlock = value;
    }

    /**
     * Gets the value of the requestProcessResult property.
     * 
     * @return
     *     possible object is
     *     {@link ErrInfo }
     *     
     */
    public ErrInfo getRequestProcessResult() {
        return requestProcessResult;
    }

    /**
     * Sets the value of the requestProcessResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrInfo }
     *     
     */
    public void setRequestProcessResult(ErrInfo value) {
        this.requestProcessResult = value;
    }

}
