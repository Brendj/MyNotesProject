
package generated.ru.mos.rnip.xsd.payment._2_4;

import generated.ru.mos.rnip.xsd.charge._2_4.Payer;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.rnip.xsd.payment._2_4 package.
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.rnip.xsd.payment._2_4
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PaymentType }
     * 
     */
    public PaymentType createPaymentType() {
        return new PaymentType();
    }

    /**
     * Create an instance of {@link Payer }
     * 
     */
    public Payer createPaymentTypePayer() {
        return new Payer();
    }

    /**
     * Create an instance of {@link generated.ru.mos.rnip.xsd.payment._2_1.PaymentType.PartialPayt }
     * 
     */
    public generated.ru.mos.rnip.xsd.payment._2_1.PaymentType.PartialPayt createPaymentTypePartialPayt() {
        return new generated.ru.mos.rnip.xsd.payment._2_1.PaymentType.PartialPayt();
    }

}
