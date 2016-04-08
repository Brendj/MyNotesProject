
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.charge;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.charge package. 
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

    private final static QName _Charge_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/Charge", "Charge");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.charge
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ChargeType.MainSupplierBillIDList }
     * 
     */
    public ChargeType.MainSupplierBillIDList createChargeTypeMainSupplierBillIDList() {
        return new ChargeType.MainSupplierBillIDList();
    }

    /**
     * Create an instance of {@link ChargeType }
     * 
     */
    public ChargeType createChargeType() {
        return new ChargeType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/Charge", name = "Charge")
    public JAXBElement<ChargeType> createCharge(ChargeType value) {
        return new JAXBElement<ChargeType>(_Charge_QNAME, ChargeType.class, null, value);
    }

}
