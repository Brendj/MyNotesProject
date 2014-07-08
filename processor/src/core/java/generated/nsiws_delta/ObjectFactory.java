
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.nsiws_delta;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.nsiws_delta package. 
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

    private final static QName _ReceiveNSIDeltaRequest_QNAME = new QName("http://rstyle.com/nsi/delta/service", "receiveNSIDeltaRequest");
    private final static QName _ReceiveNSIDeltaResponse_QNAME = new QName("http://rstyle.com/nsi/delta/service", "receiveNSIDeltaResponse");
    private final static QName _Container_QNAME = new QName("http://rstyle.com/nsi/delta", "container");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.nsiws_delta
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContainerDelta }
     * 
     */
    public ContainerDelta createContainerDelta() {
        return new ContainerDelta();
    }

    /**
     * Create an instance of {@link ReceiveNSIDeltaRequestType }
     * 
     */
    public ReceiveNSIDeltaRequestType createReceiveNSIDeltaRequestType() {
        return new ReceiveNSIDeltaRequestType();
    }

    /**
     * Create an instance of {@link DeltaType.Part }
     * 
     */
    public DeltaType.Part createDeltaTypePart() {
        return new DeltaType.Part();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link DeltaType }
     * 
     */
    public DeltaType createDeltaType() {
        return new DeltaType();
    }

    /**
     * Create an instance of {@link ReceiveNSIDeltaResponseType }
     * 
     */
    public ReceiveNSIDeltaResponseType createReceiveNSIDeltaResponseType() {
        return new ReceiveNSIDeltaResponseType();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Create an instance of {@link GroupValue }
     * 
     */
    public GroupValue createGroupValue() {
        return new GroupValue();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Attribute.Value }
     * 
     */
    public Attribute.Value createAttributeValue() {
        return new Attribute.Value();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveNSIDeltaRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/delta/service", name = "receiveNSIDeltaRequest")
    public JAXBElement<ReceiveNSIDeltaRequestType> createReceiveNSIDeltaRequest(ReceiveNSIDeltaRequestType value) {
        return new JAXBElement<ReceiveNSIDeltaRequestType>(_ReceiveNSIDeltaRequest_QNAME, ReceiveNSIDeltaRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveNSIDeltaResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/delta/service", name = "receiveNSIDeltaResponse")
    public JAXBElement<ReceiveNSIDeltaResponseType> createReceiveNSIDeltaResponse(ReceiveNSIDeltaResponseType value) {
        return new JAXBElement<ReceiveNSIDeltaResponseType>(_ReceiveNSIDeltaResponse_QNAME, ReceiveNSIDeltaResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContainerDelta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/delta", name = "container")
    public JAXBElement<ContainerDelta> createContainer(ContainerDelta value) {
        return new JAXBElement<ContainerDelta>(_Container_QNAME, ContainerDelta.class, null, value);
    }

}
