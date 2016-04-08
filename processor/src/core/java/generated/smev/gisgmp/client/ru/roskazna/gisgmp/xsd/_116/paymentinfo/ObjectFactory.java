
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.paymentinfo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.paymentinfo package. 
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

    private final static QName _FinalPayment_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/PaymentInfo", "FinalPayment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.paymentinfo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PaymentType.Payer }
     * 
     */
    public PaymentType.Payer createPaymentTypePayer() {
        return new PaymentType.Payer();
    }

    /**
     * Create an instance of {@link PaymentType }
     * 
     */
    public PaymentType createPaymentType() {
        return new PaymentType();
    }

    /**
     * Create an instance of {@link PaymentType.Payee }
     * 
     */
    public PaymentType.Payee createPaymentTypePayee() {
        return new PaymentType.Payee();
    }

    /**
     * Create an instance of {@link PaymentIdentificationDataType }
     * 
     */
    public PaymentIdentificationDataType createPaymentIdentificationDataType() {
        return new PaymentIdentificationDataType();
    }

    /**
     * Create an instance of {@link PaymentType.ChangeStatus }
     * 
     */
    public PaymentType.ChangeStatus createPaymentTypeChangeStatus() {
        return new PaymentType.ChangeStatus();
    }

    /**
     * Create an instance of {@link PaymentType.PartialPayt }
     * 
     */
    public PaymentType.PartialPayt createPaymentTypePartialPayt() {
        return new PaymentType.PartialPayt();
    }

    /**
     * Create an instance of {@link AccDoc }
     * 
     */
    public AccDoc createAccDoc() {
        return new AccDoc();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link PaymentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/PaymentInfo", name = "FinalPayment")
    public JAXBElement<PaymentType> createFinalPayment(PaymentType value) {
        return new JAXBElement<PaymentType>(_FinalPayment_QNAME, PaymentType.class, null, value);
    }

}
