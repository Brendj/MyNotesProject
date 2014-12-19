
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.bankofkazan package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AsynchronousPaymentResponse_QNAME = new QName("http://bankofkazan.ru/", "AsynchronousPaymentResponse");
    private final static QName _AsynchronousPaymentRequest_QNAME = new QName("http://bankofkazan.ru/", "AsynchronousPaymentRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.bankofkazan
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ru.axetta.ecafe.processor.web.partner.kzn_bankkazani.ru.bankofkazan.Requestex }
     * 
     */
    public Requestex createRequestex() {
        return new Requestex();
    }

    /**
     * Create an instance of {@link ru.axetta.ecafe.processor.web.partner.kzn_bankkazani.ru.bankofkazan.Responseex }
     * 
     */
    public Responseex createResponseex() {
        return new Responseex();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentRequest }
     * 
     */
    public AsynchronousPaymentRequest createAsynchronousPaymentRequest() {
        return new AsynchronousPaymentRequest();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentRequest.Requestex }
     * 
     */
    public AsynchronousPaymentRequest.Requestex createAsynchronousPaymentRequestRequestex() {
        return new AsynchronousPaymentRequest.Requestex();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentResponse }
     * 
     */
    public AsynchronousPaymentResponse createAsynchronousPaymentResponse() {
        return new AsynchronousPaymentResponse();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentResponse.Responseex }
     * 
     */
    public AsynchronousPaymentResponse.Responseex createAsynchronousPaymentResponseResponseex() {
        return new AsynchronousPaymentResponse.Responseex();
    }

    /**
     * Create an instance of {@link ru.axetta.ecafe.processor.web.partner.kzn_bankkazani.ru.bankofkazan.Requestex.Cards }
     * 
     */
    public Requestex.Cards createRequestexCards() {
        return new Requestex.Cards();
    }

    /**
     * Create an instance of {@link ru.axetta.ecafe.processor.web.partner.kzn_bankkazani.ru.bankofkazan.Responseex.Cards }
     * 
     */
    public Responseex.Cards createResponseexCards() {
        return new Responseex.Cards();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentRequest.Requestex.Cards }
     * 
     */
    public AsynchronousPaymentRequest.Requestex.Cards createAsynchronousPaymentRequestRequestexCards() {
        return new AsynchronousPaymentRequest.Requestex.Cards();
    }

    /**
     * Create an instance of {@link AsynchronousPaymentResponse.Responseex.Cards }
     * 
     */
    public AsynchronousPaymentResponse.Responseex.Cards createAsynchronousPaymentResponseResponseexCards() {
        return new AsynchronousPaymentResponse.Responseex.Cards();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link AsynchronousPaymentResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://bankofkazan.ru/", name = "AsynchronousPaymentResponse")
    public JAXBElement<AsynchronousPaymentResponse> createAsynchronousPaymentResponse(AsynchronousPaymentResponse value) {
        return new JAXBElement<AsynchronousPaymentResponse>(_AsynchronousPaymentResponse_QNAME, AsynchronousPaymentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link AsynchronousPaymentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bankofkazan.ru/", name = "AsynchronousPaymentRequest")
    public JAXBElement<AsynchronousPaymentRequest> createAsynchronousPaymentRequest(AsynchronousPaymentRequest value) {
        return new JAXBElement<AsynchronousPaymentRequest>(_AsynchronousPaymentRequest_QNAME, AsynchronousPaymentRequest.class, null, value);
    }

}
