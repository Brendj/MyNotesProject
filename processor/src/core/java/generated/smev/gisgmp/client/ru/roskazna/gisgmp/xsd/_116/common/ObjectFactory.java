
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.common package. 
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

    private final static QName _AdditionalData_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "AdditionalData");
    private final static QName _TransKind_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "TransKind");
    private final static QName _SimpleParameter_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "SimpleParameter");
    private final static QName _ComplexParameter_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "ComplexParameter");
    private final static QName _PayerIdentification_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "PayerIdentification");
    private final static QName _PayerIdentifier_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Common", "PayerIdentifier");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Money }
     * 
     */
    public Money createMoney() {
        return new Money();
    }

    /**
     * Create an instance of {@link ChangeStatus }
     * 
     */
    public ChangeStatus createChangeStatus() {
        return new ChangeStatus();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link ComplexParameterType }
     * 
     */
    public ComplexParameterType createComplexParameterType() {
        return new ComplexParameterType();
    }

    /**
     * Create an instance of {@link SimpleParameterType }
     * 
     */
    public SimpleParameterType createSimpleParameterType() {
        return new SimpleParameterType();
    }

    /**
     * Create an instance of {@link AdditionalDataType }
     * 
     */
    public AdditionalDataType createAdditionalDataType() {
        return new AdditionalDataType();
    }

    /**
     * Create an instance of {@link ContactsType }
     * 
     */
    public ContactsType createContactsType() {
        return new ContactsType();
    }

    /**
     * Create an instance of {@link ParamValueType }
     * 
     */
    public ParamValueType createParamValueType() {
        return new ParamValueType();
    }

    /**
     * Create an instance of {@link PayerIdentificationType }
     * 
     */
    public PayerIdentificationType createPayerIdentificationType() {
        return new PayerIdentificationType();
    }

    /**
     * Create an instance of {@link ContactInfoType }
     * 
     */
    public ContactInfoType createContactInfoType() {
        return new ContactInfoType();
    }

    /**
     * Create an instance of {@link CommissionType }
     * 
     */
    public CommissionType createCommissionType() {
        return new CommissionType();
    }

    /**
     * Create an instance of {@link CommissionsType }
     * 
     */
    public CommissionsType createCommissionsType() {
        return new CommissionsType();
    }

    /**
     * Create an instance of {@link AddressType.AddressField }
     * 
     */
    public AddressType.AddressField createAddressTypeAddressField() {
        return new AddressType.AddressField();
    }

    /**
     * Create an instance of {@link FieldType }
     * 
     */
    public FieldType createFieldType() {
        return new FieldType();
    }

    /**
     * Create an instance of {@link AddressesType }
     * 
     */
    public AddressesType createAddressesType() {
        return new AddressesType();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdditionalDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "AdditionalData")
    public JAXBElement<AdditionalDataType> createAdditionalData(AdditionalDataType value) {
        return new JAXBElement<AdditionalDataType>(_AdditionalData_QNAME, AdditionalDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "TransKind")
    public JAXBElement<String> createTransKind(String value) {
        return new JAXBElement<String>(_TransKind_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "SimpleParameter")
    public JAXBElement<SimpleParameterType> createSimpleParameter(SimpleParameterType value) {
        return new JAXBElement<SimpleParameterType>(_SimpleParameter_QNAME, SimpleParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComplexParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "ComplexParameter")
    public JAXBElement<ComplexParameterType> createComplexParameter(ComplexParameterType value) {
        return new JAXBElement<ComplexParameterType>(_ComplexParameter_QNAME, ComplexParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PayerIdentificationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "PayerIdentification")
    public JAXBElement<PayerIdentificationType> createPayerIdentification(PayerIdentificationType value) {
        return new JAXBElement<PayerIdentificationType>(_PayerIdentification_QNAME, PayerIdentificationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", name = "PayerIdentifier")
    public JAXBElement<String> createPayerIdentifier(String value) {
        return new JAXBElement<String>(_PayerIdentifier_QNAME, String.class, null, value);
    }

}
