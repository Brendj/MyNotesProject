
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata;

import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceCatalogType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.chargecreationrequest.ChargeCreationRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.chargecreationresponse.ChargeCreationResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment.DoAcknowledgmentRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment.DoAcknowledgmentResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse.ExportIncomesResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.packagestatusrequest.PackageStatusRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_chargesresponse.ExportChargesResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_importrequest.ImportRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.selfadministration.ImportCertificateRequestType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.ticket.TicketType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.gisgmp.xsd._116.messagedata package. 
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

    private final static QName _PackageStatusRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "PackageStatusRequest");
    private final static QName _ResponseMessageData_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ResponseMessageData");
    private final static QName _ExportChargesResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportChargesResponse");
    private final static QName _ExportPaymentsResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportPaymentsResponse");
    private final static QName _DoAcknowledgmentResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "DoAcknowledgmentResponse");
    private final static QName _RequestMessageData_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "RequestMessageData");
    private final static QName _ExportQuittanceResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportQuittanceResponse");
    private final static QName _DoAcknowledgmentRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "DoAcknowledgmentRequest");
    private final static QName _ImportRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ImportRequest");
    private final static QName _ImportCertificateRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ImportCertificateRequest");
    private final static QName _ExportRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportRequest");
    private final static QName _ExportIncomesResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportIncomesResponse");
    private final static QName _ChargeCreationRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ChargeCreationRequest");
    private final static QName _ExportCatalogResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ExportCatalogResponse");
    private final static QName _ChargeCreationResponse_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ChargeCreationResponse");
    private final static QName _ImportCatalogRequest_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "ImportCatalogRequest");
    private final static QName _Ticket_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "Ticket");
    private final static QName _ImportCatalogRequestChanges_QNAME = new QName("http://roskazna.ru/gisgmp/xsd/116/MessageData", "Changes");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.gisgmp.xsd._116.messagedata
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExportCatalogResponse.Catalog }
     * 
     */
    public ExportCatalogResponse.Catalog createExportCatalogResponseCatalog() {
        return new ExportCatalogResponse.Catalog();
    }

    /**
     * Create an instance of {@link ImportCatalogRequest }
     * 
     */
    public ImportCatalogRequest createImportCatalogRequest() {
        return new ImportCatalogRequest();
    }

    /**
     * Create an instance of {@link ExportCatalogResponse }
     * 
     */
    public ExportCatalogResponse createExportCatalogResponse() {
        return new ExportCatalogResponse();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link PackageStatusRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "PackageStatusRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<PackageStatusRequestType> createPackageStatusRequest(PackageStatusRequestType value) {
        return new JAXBElement<PackageStatusRequestType>(_PackageStatusRequest_QNAME, PackageStatusRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ResponseMessageData")
    public JAXBElement<Object> createResponseMessageData(Object value) {
        return new JAXBElement<Object>(_ResponseMessageData_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportChargesResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportChargesResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ExportChargesResponseType> createExportChargesResponse(ExportChargesResponseType value) {
        return new JAXBElement<ExportChargesResponseType>(_ExportChargesResponse_QNAME, ExportChargesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportPaymentsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportPaymentsResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ExportPaymentsResponseType> createExportPaymentsResponse(ExportPaymentsResponseType value) {
        return new JAXBElement<ExportPaymentsResponseType>(_ExportPaymentsResponse_QNAME, ExportPaymentsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "DoAcknowledgmentResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<DoAcknowledgmentResponseType> createDoAcknowledgmentResponse(DoAcknowledgmentResponseType value) {
        return new JAXBElement<DoAcknowledgmentResponseType>(_DoAcknowledgmentResponse_QNAME, DoAcknowledgmentResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "RequestMessageData")
    public JAXBElement<Object> createRequestMessageData(Object value) {
        return new JAXBElement<Object>(_RequestMessageData_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportQuittanceResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportQuittanceResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ExportQuittanceResponseType> createExportQuittanceResponse(ExportQuittanceResponseType value) {
        return new JAXBElement<ExportQuittanceResponseType>(_ExportQuittanceResponse_QNAME, ExportQuittanceResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link DoAcknowledgmentRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "DoAcknowledgmentRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<DoAcknowledgmentRequestType> createDoAcknowledgmentRequest(DoAcknowledgmentRequestType value) {
        return new JAXBElement<DoAcknowledgmentRequestType>(_DoAcknowledgmentRequest_QNAME, DoAcknowledgmentRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ImportRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<ImportRequestType> createImportRequest(ImportRequestType value) {
        return new JAXBElement<ImportRequestType>(_ImportRequest_QNAME, ImportRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCertificateRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ImportCertificateRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<ImportCertificateRequestType> createImportCertificateRequest(ImportCertificateRequestType value) {
        return new JAXBElement<ImportCertificateRequestType>(_ImportCertificateRequest_QNAME, ImportCertificateRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link DataRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<DataRequest> createExportRequest(DataRequest value) {
        return new JAXBElement<DataRequest>(_ExportRequest_QNAME, DataRequest.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportIncomesResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportIncomesResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ExportIncomesResponseType> createExportIncomesResponse(ExportIncomesResponseType value) {
        return new JAXBElement<ExportIncomesResponseType>(_ExportIncomesResponse_QNAME, ExportIncomesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ChargeCreationRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<ChargeCreationRequestType> createChargeCreationRequest(ChargeCreationRequestType value) {
        return new JAXBElement<ChargeCreationRequestType>(_ChargeCreationRequest_QNAME, ChargeCreationRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ExportCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ExportCatalogResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ExportCatalogResponse> createExportCatalogResponse(ExportCatalogResponse value) {
        return new JAXBElement<ExportCatalogResponse>(_ExportCatalogResponse_QNAME, ExportCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ChargeCreationResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ChargeCreationResponse", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<ChargeCreationResponseType> createChargeCreationResponse(ChargeCreationResponseType value) {
        return new JAXBElement<ChargeCreationResponseType>(_ChargeCreationResponse_QNAME, ChargeCreationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ImportCatalogRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "ImportCatalogRequest", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "RequestMessageData")
    public JAXBElement<ImportCatalogRequest> createImportCatalogRequest(ImportCatalogRequest value) {
        return new JAXBElement<ImportCatalogRequest>(_ImportCatalogRequest_QNAME, ImportCatalogRequest.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link TicketType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "Ticket", substitutionHeadNamespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", substitutionHeadName = "ResponseMessageData")
    public JAXBElement<TicketType> createTicket(TicketType value) {
        return new JAXBElement<TicketType>(_Ticket_QNAME, TicketType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ServiceCatalogType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", name = "Changes", scope = ImportCatalogRequest.class)
    public JAXBElement<ServiceCatalogType> createImportCatalogRequestChanges(ServiceCatalogType value) {
        return new JAXBElement<ServiceCatalogType>(_ImportCatalogRequestChanges_QNAME, ServiceCatalogType.class, ImportCatalogRequest.class, value);
    }

}
