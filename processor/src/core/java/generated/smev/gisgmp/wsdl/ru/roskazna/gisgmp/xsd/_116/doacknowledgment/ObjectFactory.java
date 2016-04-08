
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.doacknowledgment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.doacknowledgment package. 
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

    private final static QName _PaymentSystemIdentifier_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/DoAcknowledgment", "PaymentSystemIdentifier");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.doacknowledgment
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DoAcknowledgmentResponseType.Quittances }
     * 
     */
    public DoAcknowledgmentResponseType.Quittances createDoAcknowledgmentResponseTypeQuittances() {
        return new DoAcknowledgmentResponseType.Quittances();
    }

    /**
     * Create an instance of {@link DoAcknowledgmentRequestType }
     * 
     */
    public DoAcknowledgmentRequestType createDoAcknowledgmentRequestType() {
        return new DoAcknowledgmentRequestType();
    }

    /**
     * Create an instance of {@link DoAcknowledgmentRequestType.Payments }
     * 
     */
    public DoAcknowledgmentRequestType.Payments createDoAcknowledgmentRequestTypePayments() {
        return new DoAcknowledgmentRequestType.Payments();
    }

    /**
     * Create an instance of {@link DoAcknowledgmentResponseType.PaymentsNotFound }
     * 
     */
    public DoAcknowledgmentResponseType.PaymentsNotFound createDoAcknowledgmentResponseTypePaymentsNotFound() {
        return new DoAcknowledgmentResponseType.PaymentsNotFound();
    }

    /**
     * Create an instance of {@link DoAcknowledgmentResponseType.Quittances.Quittance }
     * 
     */
    public DoAcknowledgmentResponseType.Quittances.Quittance createDoAcknowledgmentResponseTypeQuittancesQuittance() {
        return new DoAcknowledgmentResponseType.Quittances.Quittance();
    }

    /**
     * Create an instance of {@link DoAcknowledgmentResponseType }
     * 
     */
    public DoAcknowledgmentResponseType createDoAcknowledgmentResponseType() {
        return new DoAcknowledgmentResponseType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/DoAcknowledgment", name = "PaymentSystemIdentifier")
    public JAXBElement<String> createPaymentSystemIdentifier(String value) {
        return new JAXBElement<String>(_PaymentSystemIdentifier_QNAME, String.class, null, value);
    }

}
