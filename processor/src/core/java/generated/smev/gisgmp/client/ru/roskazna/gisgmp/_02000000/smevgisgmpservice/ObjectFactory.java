
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp._02000000.smevgisgmpservice;

import generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.BaseMessageType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp._02000000.smevgisgmpservice package. 
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

    private final static QName _GISGMPTransferMsg_QNAME = new QName("http://roskazna.ru/gisgmp/02000000/SmevGISGMPService/", "GISGMPTransferMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp._02000000.smevgisgmpservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link BaseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/02000000/SmevGISGMPService/", name = "GISGMPTransferMsg")
    public JAXBElement<BaseMessageType> createGISGMPTransferMsg(BaseMessageType value) {
        return new JAXBElement<BaseMessageType>(_GISGMPTransferMsg_QNAME, BaseMessageType.class, null, value);
    }

}
