
package ru.msk.schemas.uec.common.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.msk.schemas.uec.common.v1 package. 
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

    private final static QName _Fault_QNAME = new QName("http://schemas.msk.ru/uec/common/v1", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.msk.schemas.uec.common.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SimpleReferenceType }
     * 
     */
    public SimpleReferenceType createSimpleReferenceType() {
        return new SimpleReferenceType();
    }

    /**
     * Create an instance of {@link FaultType }
     * 
     */
    public FaultType createFaultType() {
        return new FaultType();
    }

    /**
     * Create an instance of {@link AdditionalDataType }
     * 
     */
    public AdditionalDataType createAdditionalDataType() {
        return new AdditionalDataType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link SimpleReferenceType.Items }
     * 
     */
    public SimpleReferenceType.Items createSimpleReferenceTypeItems() {
        return new SimpleReferenceType.Items();
    }

    /**
     * Create an instance of {@link ErrorListType }
     * 
     */
    public ErrorListType createErrorListType() {
        return new ErrorListType();
    }

    /**
     * Create an instance of {@link SimpleReferenceType.Items.Item }
     * 
     */
    public SimpleReferenceType.Items.Item createSimpleReferenceTypeItemsItem() {
        return new SimpleReferenceType.Items.Item();
    }

    /**
     * Create an instance of {@link AdditionalDataType.AdditionalData }
     * 
     */
    public AdditionalDataType.AdditionalData createAdditionalDataTypeAdditionalData() {
        return new AdditionalDataType.AdditionalData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/common/v1", name = "fault")
    public JAXBElement<FaultType> createFault(FaultType value) {
        return new JAXBElement<FaultType>(_Fault_QNAME, FaultType.class, null, value);
    }

}
