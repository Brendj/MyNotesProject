
package ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import ru.msk.schemas.uec.common.v1.SimpleReferenceType;
import ru.msk.schemas.uec.transaction.v1.ErrorListType;
import ru.msk.schemas.uec.transaction.v1.TransactionListType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.msk.schemas.uec.transactionservice.v1 package. 
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

    private final static QName _GetSimpleReferenceResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getSimpleReferenceResponse");
    private final static QName _GetBillsRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getBillsRequest");
    private final static QName _GetBillsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getBillsResponse");
    private final static QName _GetPersonTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getPersonTransactionsResponse");
    private final static QName _StoreTariffsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeTariffsResponse");
    private final static QName _StoreNotificationSubscribersRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeNotificationSubscribersRequest");
    private final static QName _StoreNotificationSubscribersResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeNotificationSubscribersResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.msk.schemas.uec.transactionservice.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetPersonTransactionsRequest }
     * 
     */
    public GetPersonTransactionsRequest createGetPersonTransactionsRequest() {
        return new GetPersonTransactionsRequest();
    }

    /**
     * Create an instance of {@link ServiceProviderType }
     * 
     */
    public ServiceProviderType createServiceProviderType() {
        return new ServiceProviderType();
    }

    /**
     * Create an instance of {@link TariffByPeriodType }
     * 
     */
    public TariffByPeriodType createTariffByPeriodType() {
        return new TariffByPeriodType();
    }

    /**
     * Create an instance of {@link BillListType }
     * 
     */
    public BillListType createBillListType() {
        return new BillListType();
    }

    /**
     * Create an instance of {@link BillType }
     * 
     */
    public BillType createBillType() {
        return new BillType();
    }

    /**
     * Create an instance of {@link TariffType.TariffBindings }
     * 
     */
    public TariffType.TariffBindings createTariffTypeTariffBindings() {
        return new TariffType.TariffBindings();
    }

    /**
     * Create an instance of {@link SystemType }
     * 
     */
    public SystemType createSystemType() {
        return new SystemType();
    }

    /**
     * Create an instance of {@link ServiceCustomerType }
     * 
     */
    public ServiceCustomerType createServiceCustomerType() {
        return new ServiceCustomerType();
    }

    /**
     * Create an instance of {@link GetBillsRequestType.TransactionIdDescription }
     * 
     */
    public GetBillsRequestType.TransactionIdDescription createGetBillsRequestTypeTransactionIdDescription() {
        return new GetBillsRequestType.TransactionIdDescription();
    }

    /**
     * Create an instance of {@link TariffByFixedValueType }
     * 
     */
    public TariffByFixedValueType createTariffByFixedValueType() {
        return new TariffByFixedValueType();
    }

    /**
     * Create an instance of {@link TariffBindingsMetaDataItemType }
     * 
     */
    public TariffBindingsMetaDataItemType createTariffBindingsMetaDataItemType() {
        return new TariffBindingsMetaDataItemType();
    }

    /**
     * Create an instance of {@link TariffByVolumeType }
     * 
     */
    public TariffByVolumeType createTariffByVolumeType() {
        return new TariffByVolumeType();
    }

    /**
     * Create an instance of {@link TariffBindingsItemType.MetaData }
     * 
     */
    public TariffBindingsItemType.MetaData createTariffBindingsItemTypeMetaData() {
        return new TariffBindingsItemType.MetaData();
    }

    /**
     * Create an instance of {@link TariffByVolumeType.TimeTarificationInterval }
     * 
     */
    public TariffByVolumeType.TimeTarificationInterval createTariffByVolumeTypeTimeTarificationInterval() {
        return new TariffByVolumeType.TimeTarificationInterval();
    }

    /**
     * Create an instance of {@link TariffListType }
     * 
     */
    public TariffListType createTariffListType() {
        return new TariffListType();
    }

    /**
     * Create an instance of {@link SubscribtionListType }
     * 
     */
    public SubscribtionListType createSubscribtionListType() {
        return new SubscribtionListType();
    }

    /**
     * Create an instance of {@link SubscriptionType }
     * 
     */
    public SubscriptionType createSubscriptionType() {
        return new SubscriptionType();
    }

    /**
     * Create an instance of {@link GetBillsRequestType }
     * 
     */
    public GetBillsRequestType createGetBillsRequestType() {
        return new GetBillsRequestType();
    }

    /**
     * Create an instance of {@link GetBillsResponseType }
     * 
     */
    public GetBillsResponseType createGetBillsResponseType() {
        return new GetBillsResponseType();
    }

    /**
     * Create an instance of {@link GetSimpleReferenceRequest }
     * 
     */
    public GetSimpleReferenceRequest createGetSimpleReferenceRequest() {
        return new GetSimpleReferenceRequest();
    }

    /**
     * Create an instance of {@link TariffType }
     * 
     */
    public TariffType createTariffType() {
        return new TariffType();
    }

    /**
     * Create an instance of {@link TariffBindingsItemType }
     * 
     */
    public TariffBindingsItemType createTariffBindingsItemType() {
        return new TariffBindingsItemType();
    }

    /**
     * Create an instance of {@link TariffType.TariffByVolumes }
     * 
     */
    public TariffType.TariffByVolumes createTariffTypeTariffByVolumes() {
        return new TariffType.TariffByVolumes();
    }

    /**
     * Create an instance of {@link CustomerDescriptionType }
     * 
     */
    public CustomerDescriptionType createCustomerDescriptionType() {
        return new CustomerDescriptionType();
    }

    /**
     * Create an instance of {@link TariffType.TariffByPeriods }
     * 
     */
    public TariffType.TariffByPeriods createTariffTypeTariffByPeriods() {
        return new TariffType.TariffByPeriods();
    }

    /**
     * Create an instance of {@link StoreTariffsRequest }
     * 
     */
    public StoreTariffsRequest createStoreTariffsRequest() {
        return new StoreTariffsRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "getSimpleReferenceResponse")
    public JAXBElement<SimpleReferenceType> createGetSimpleReferenceResponse(SimpleReferenceType value) {
        return new JAXBElement<SimpleReferenceType>(_GetSimpleReferenceResponse_QNAME, SimpleReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "getBillsRequest")
    public JAXBElement<GetBillsRequestType> createGetBillsRequest(GetBillsRequestType value) {
        return new JAXBElement<GetBillsRequestType>(_GetBillsRequest_QNAME, GetBillsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "getBillsResponse")
    public JAXBElement<GetBillsResponseType> createGetBillsResponse(GetBillsResponseType value) {
        return new JAXBElement<GetBillsResponseType>(_GetBillsResponse_QNAME, GetBillsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "getPersonTransactionsResponse")
    public JAXBElement<TransactionListType> createGetPersonTransactionsResponse(TransactionListType value) {
        return new JAXBElement<TransactionListType>(_GetPersonTransactionsResponse_QNAME, TransactionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "storeTariffsResponse")
    public JAXBElement<ErrorListType> createStoreTariffsResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreTariffsResponse_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscribtionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "storeNotificationSubscribersRequest")
    public JAXBElement<SubscribtionListType> createStoreNotificationSubscribersRequest(SubscribtionListType value) {
        return new JAXBElement<SubscribtionListType>(_StoreNotificationSubscribersRequest_QNAME, SubscribtionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "storeNotificationSubscribersResponse")
    public JAXBElement<ErrorListType> createStoreNotificationSubscribersResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreNotificationSubscribersResponse_QNAME, ErrorListType.class, null, value);
    }

}
