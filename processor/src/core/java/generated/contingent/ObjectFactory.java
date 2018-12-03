
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.contingent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.contingent.ws.ispp package. 
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

    private final static QName _SetBenefits_QNAME = new QName("urn:contingent.mos.ru:ws:ispp", "setBenefits");
    private final static QName _SetBenefitsResponse1_QNAME = new QName("urn:contingent.mos.ru:ws:ispp", "setBenefitsResponse");
    private final static QName _ServiceHeader_QNAME = new QName("urn:contingent.mos.ru:ws:ispp", "ServiceHeader");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.contingent.ws.ispp
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetBenefitsResponse.NotFound }
     * 
     */
    public SetBenefitsResponse.NotFound createSetBenefitsResponseNotFound() {
        return new SetBenefitsResponse.NotFound();
    }

    /**
     * Create an instance of {@link SetBenefits }
     * 
     */
    public SetBenefits createSetBenefits() {
        return new SetBenefits();
    }

    /**
     * Create an instance of {@link SetBenefitsRequest }
     * 
     */
    public SetBenefitsRequest createSetBenefitsRequest() {
        return new SetBenefitsRequest();
    }

    /**
     * Create an instance of {@link Child }
     * 
     */
    public Child createChild() {
        return new Child();
    }

    /**
     * Create an instance of {@link SetBenefitsResponse.Success }
     * 
     */
    public SetBenefitsResponse.Success createSetBenefitsResponseSuccess() {
        return new SetBenefitsResponse.Success();
    }

    /**
     * Create an instance of {@link SetBenefitsRequest.Children }
     * 
     */
    public SetBenefitsRequest.Children createSetBenefitsRequestChildren() {
        return new SetBenefitsRequest.Children();
    }

    /**
     * Create an instance of {@link SetBenefitsResponse1 }
     * 
     */
    public SetBenefitsResponse1 createSetBenefitsResponse1() {
        return new SetBenefitsResponse1();
    }

    /**
     * Create an instance of {@link IsppHeaders }
     * 
     */
    public IsppHeaders createIsppHeaders() {
        return new IsppHeaders();
    }

    /**
     * Create an instance of {@link SetBenefitsResponse }
     * 
     */
    public SetBenefitsResponse createSetBenefitsResponse() {
        return new SetBenefitsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetBenefits }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:contingent.mos.ru:ws:ispp", name = "setBenefits")
    public JAXBElement<SetBenefits> createSetBenefits(SetBenefits value) {
        return new JAXBElement<SetBenefits>(_SetBenefits_QNAME, SetBenefits.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetBenefitsResponse1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:contingent.mos.ru:ws:ispp", name = "setBenefitsResponse")
    public JAXBElement<SetBenefitsResponse1> createSetBenefitsResponse1(SetBenefitsResponse1 value) {
        return new JAXBElement<SetBenefitsResponse1>(_SetBenefitsResponse1_QNAME, SetBenefitsResponse1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsppHeaders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:contingent.mos.ru:ws:ispp", name = "ServiceHeader")
    public JAXBElement<IsppHeaders> createServiceHeader(IsppHeaders value) {
        return new JAXBElement<IsppHeaders>(_ServiceHeader_QNAME, IsppHeaders.class, null, value);
    }

}
