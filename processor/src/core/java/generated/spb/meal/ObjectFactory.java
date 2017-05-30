
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package generated.spb.meal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.mealwebservice2 package. 
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

    public final static QName _Password_QNAME = new QName("http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", "password");
    public final static QName _PushZipFileRequest_QNAME = new QName("http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", "pushZipFileRequest");
    public final static QName _Login_QNAME = new QName("http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", "login");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.mealwebservice2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransactionType }
     * 
     */
    public TransactionType createTransactionType() {
        return new TransactionType();
    }

    /**
     * Create an instance of {@link MealData.IdentityInfo }
     * 
     */
    public MealData.IdentityInfo createMealDataIdentityInfo() {
        return new MealData.IdentityInfo();
    }

    /**
     * Create an instance of {@link PushResponse }
     * 
     */
    public PushResponse createPushResponse() {
        return new PushResponse();
    }

    /**
     * Create an instance of {@link MealData }
     * 
     */
    public MealData createMealData() {
        return new MealData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", name = "password")
    public JAXBElement<String> createPassword(String value) {
        return new JAXBElement<String>(_Password_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", name = "pushZipFileRequest")
    public JAXBElement<String> createPushZipFileRequest(String value) {
        return new JAXBElement<String>(_PushZipFileRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://svc.edu.n3demo.ru/service/webservice/meal/wsdl", name = "login")
    public JAXBElement<String> createLogin(String value) {
        return new JAXBElement<String>(_Login_QNAME, String.class, null, value);
    }

}
