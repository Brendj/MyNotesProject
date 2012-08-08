
package ru.axetta.ecafe.processor.dashboard;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.axetta.ecafe.processor.dashboard package. 
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

    private final static QName _GetInfoForDashboard_QNAME = new QName("http://dashboard.processor.ecafe.axetta.ru/", "getInfoForDashboard");
    private final static QName _GetInfoForDashboardResponse_QNAME = new QName("http://dashboard.processor.ecafe.axetta.ru/", "getInfoForDashboardResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.axetta.ecafe.processor.dashboard
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DashboardResponse }
     * 
     */
    public DashboardResponse createDashboardResponse() {
        return new DashboardResponse();
    }

    /**
     * Create an instance of {@link EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry }
     * 
     */
    public EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry createEduInstItemInfoLastOperationTimePerPaymentSystemEntry() {
        return new EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry();
    }

    /**
     * Create an instance of {@link EduInstItemInfo }
     * 
     */
    public EduInstItemInfo createEduInstItemInfo() {
        return new EduInstItemInfo();
    }

    /**
     * Create an instance of {@link GetInfoForDashboard }
     * 
     */
    public GetInfoForDashboard createGetInfoForDashboard() {
        return new GetInfoForDashboard();
    }

    /**
     * Create an instance of {@link EduInstItemInfo.LastOperationTimePerPaymentSystem }
     * 
     */
    public EduInstItemInfo.LastOperationTimePerPaymentSystem createEduInstItemInfoLastOperationTimePerPaymentSystem() {
        return new EduInstItemInfo.LastOperationTimePerPaymentSystem();
    }

    /**
     * Create an instance of {@link GetInfoForDashboardResponse }
     * 
     */
    public GetInfoForDashboardResponse createGetInfoForDashboardResponse() {
        return new GetInfoForDashboardResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInfoForDashboard }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dashboard.processor.ecafe.axetta.ru/", name = "getInfoForDashboard")
    public JAXBElement<GetInfoForDashboard> createGetInfoForDashboard(GetInfoForDashboard value) {
        return new JAXBElement<GetInfoForDashboard>(_GetInfoForDashboard_QNAME, GetInfoForDashboard.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInfoForDashboardResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dashboard.processor.ecafe.axetta.ru/", name = "getInfoForDashboardResponse")
    public JAXBElement<GetInfoForDashboardResponse> createGetInfoForDashboardResponse(GetInfoForDashboardResponse value) {
        return new JAXBElement<GetInfoForDashboardResponse>(_GetInfoForDashboardResponse_QNAME, GetInfoForDashboardResponse.class, null, value);
    }

}
