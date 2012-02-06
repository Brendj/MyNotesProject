
package generated.nfp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import generated.nfp.x3.SimpleReferenceType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.nfp package. 
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

    private final static QName _StoreTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTransactionsResponse");
    private final static QName _GetSimpleReferenceResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getSimpleReferenceResponse");
    private final static QName _GetBillsRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getBillsRequest");
    private final static QName _GetBillsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getBillsResponse");
    private final static QName _GetPersonTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "getPersonTransactionsResponse");
    private final static QName _StoreTagsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTagsResponse");
    private final static QName _GetTransactionsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "getTransactionsResponse");
    private final static QName _TransactionDescription_QNAME = new QName("http://schemas.msk.ru/uec/transaction/v1", "transactionDescription");
    private final static QName _StoreTariffsResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeTariffsResponse");
    private final static QName _StoreNotificationSubscribersRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeNotificationSubscribersRequest");
    private final static QName _StoreTransactionsRequest_QNAME = new QName("http://schemas.msk.ru/uec/TransactionSynchronization/v1", "storeTransactionsRequest");
    private final static QName _Requester_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "requester");
    private final static QName _StoreNotificationSubscribersResponse_QNAME = new QName("http://schemas.msk.ru/uec/TransactionService/v1", "storeNotificationSubscribersResponse");
    private final static QName _HolderInfoDescriptionTypeResidenceAddress_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "residenceAddress");
    private final static QName _HolderInfoDescriptionTypeSchoolInfo_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "schoolInfo");
    private final static QName _HolderInfoDescriptionTypeUniversityInfo_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "universityInfo");
    private final static QName _HolderInfoDescriptionTypeAuthorityPerson_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "authorityPerson");
    private final static QName _HolderInfoDescriptionTypeOms_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "oms");
    private final static QName _NameTypeMiddleName_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "middleName");
    private final static QName _DocumentTypeSeries_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "series");
    private final static QName _ContactsTypeEmail_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "email");
    private final static QName _ContactsTypeMobilePhone_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "mobilePhone");
    private final static QName _AddressTypeRegionName_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "regionName");
    private final static QName _AddressTypeCorp_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "corp");
    private final static QName _AddressTypeRegionCode_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "regionCode");
    private final static QName _AddressTypeDistrictCode_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "districtCode");
    private final static QName _AddressTypeBuilding_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "building");
    private final static QName _AddressTypeIndex_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "index");
    private final static QName _AddressTypeDistrictName_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "districtName");
    private final static QName _AddressTypeFlat_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "flat");
    private final static QName _PersonInfoTypeBirthRegionCode_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "birthRegionCode");
    private final static QName _PersonInfoTypeBirthRegionName_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "birthRegionName");
    private final static QName _PersonInfoTypeBirthDistrictName_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "birthDistrictName");
    private final static QName _PersonInfoTypeCitizenship_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "citizenship");
    private final static QName _PersonInfoTypePhoto_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "photo");
    private final static QName _PersonInfoTypeBirthDistrictCode_QNAME = new QName("http://schemas.msk.ru/uec/identification/v1", "birthDistrictCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.nfp
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SchoolInfoType }
     * 
     */
    public SchoolInfoType createSchoolInfoType() {
        return new SchoolInfoType();
    }

    /**
     * Create an instance of {@link TariffByPeriodType }
     * 
     */
    public TariffByPeriodType createTariffByPeriodType() {
        return new TariffByPeriodType();
    }

    /**
     * Create an instance of {@link TariffBindingsItemType }
     * 
     */
    public TariffBindingsItemType createTariffBindingsItemType() {
        return new TariffBindingsItemType();
    }

    /**
     * Create an instance of {@link AccountingDescriptionItemType.FinancialDescription }
     * 
     */
    public AccountingDescriptionItemType.FinancialDescription createAccountingDescriptionItemTypeFinancialDescription() {
        return new AccountingDescriptionItemType.FinancialDescription();
    }

    /**
     * Create an instance of {@link TariffByFixedValueType }
     * 
     */
    public TariffByFixedValueType createTariffByFixedValueType() {
        return new TariffByFixedValueType();
    }

    /**
     * Create an instance of {@link CustomerDescriptionType }
     * 
     */
    public CustomerDescriptionType createCustomerDescriptionType() {
        return new CustomerDescriptionType();
    }

    /**
     * Create an instance of {@link TransactionStatusDescriptionType }
     * 
     */
    public TransactionStatusDescriptionType createTransactionStatusDescriptionType() {
        return new TransactionStatusDescriptionType();
    }

    /**
     * Create an instance of {@link SystemType }
     * 
     */
    public SystemType createSystemType() {
        return new SystemType();
    }

    /**
     * Create an instance of {@link OtherStatusesType.OtherStatus }
     * 
     */
    public OtherStatusesType.OtherStatus createOtherStatusesTypeOtherStatus() {
        return new OtherStatusesType.OtherStatus();
    }

    /**
     * Create an instance of {@link TariffType.TariffBindings }
     * 
     */
    public TariffType.TariffBindings createTariffTypeTariffBindings() {
        return new TariffType.TariffBindings();
    }

    /**
     * Create an instance of {@link StoreTagsRequest.RemoveTag }
     * 
     */
    public StoreTagsRequest.RemoveTag createStoreTagsRequestRemoveTag() {
        return new StoreTagsRequest.RemoveTag();
    }

    /**
     * Create an instance of {@link SubscribtionListType }
     * 
     */
    public SubscribtionListType createSubscribtionListType() {
        return new SubscribtionListType();
    }

    /**
     * Create an instance of {@link GetSimpleReferenceRequest }
     * 
     */
    public GetSimpleReferenceRequest createGetSimpleReferenceRequest() {
        return new GetSimpleReferenceRequest();
    }

    /**
     * Create an instance of {@link TransactionTypeDescriptionType }
     * 
     */
    public TransactionTypeDescriptionType createTransactionTypeDescriptionType() {
        return new TransactionTypeDescriptionType();
    }

    /**
     * Create an instance of {@link MacType }
     * 
     */
    public MacType createMacType() {
        return new MacType();
    }

    /**
     * Create an instance of {@link StoreTariffsRequest }
     * 
     */
    public StoreTariffsRequest createStoreTariffsRequest() {
        return new StoreTariffsRequest();
    }

    /**
     * Create an instance of {@link TariffType }
     * 
     */
    public TariffType createTariffType() {
        return new TariffType();
    }

    /**
     * Create an instance of {@link ExternalAuthenticateRequestType }
     * 
     */
    public ExternalAuthenticateRequestType createExternalAuthenticateRequestType() {
        return new ExternalAuthenticateRequestType();
    }

    /**
     * Create an instance of {@link UniversityInfoType }
     * 
     */
    public UniversityInfoType createUniversityInfoType() {
        return new UniversityInfoType();
    }

    /**
     * Create an instance of {@link GetPersonTransactionsRequest }
     * 
     */
    public GetPersonTransactionsRequest createGetPersonTransactionsRequest() {
        return new GetPersonTransactionsRequest();
    }

    /**
     * Create an instance of {@link TagType }
     * 
     */
    public TagType createTagType() {
        return new TagType();
    }

    /**
     * Create an instance of {@link PaymentAdditionalInfoType.PaymentInstruction }
     * 
     */
    public PaymentAdditionalInfoType.PaymentInstruction createPaymentAdditionalInfoTypePaymentInstruction() {
        return new PaymentAdditionalInfoType.PaymentInstruction();
    }

    /**
     * Create an instance of {@link TariffType.TariffByVolumes }
     * 
     */
    public TariffType.TariffByVolumes createTariffTypeTariffByVolumes() {
        return new TariffType.TariffByVolumes();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes createGetTransactionsRequestTransactionTypesExcludeTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes();
    }

    /**
     * Create an instance of {@link TransactionIdDescriptionType }
     * 
     */
    public TransactionIdDescriptionType createTransactionIdDescriptionType() {
        return new TransactionIdDescriptionType();
    }

    /**
     * Create an instance of {@link AuthorityPersonType }
     * 
     */
    public AuthorityPersonType createAuthorityPersonType() {
        return new AuthorityPersonType();
    }

    /**
     * Create an instance of {@link SAMMACDescriptionType }
     * 
     */
    public SAMMACDescriptionType createSAMMACDescriptionType() {
        return new SAMMACDescriptionType();
    }

    /**
     * Create an instance of {@link ClientCategoryType }
     * 
     */
    public ClientCategoryType createClientCategoryType() {
        return new ClientCategoryType();
    }

    /**
     * Create an instance of {@link TransactionListType }
     * 
     */
    public TransactionListType createTransactionListType() {
        return new TransactionListType();
    }

    /**
     * Create an instance of {@link FinancialDescriptionItemType }
     * 
     */
    public FinancialDescriptionItemType createFinancialDescriptionItemType() {
        return new FinancialDescriptionItemType();
    }

    /**
     * Create an instance of {@link MacRequestType }
     * 
     */
    public MacRequestType createMacRequestType() {
        return new MacRequestType();
    }

    /**
     * Create an instance of {@link ApplicationStatusType }
     * 
     */
    public ApplicationStatusType createApplicationStatusType() {
        return new ApplicationStatusType();
    }

    /**
     * Create an instance of {@link StoreTagsRequest.AddTag }
     * 
     */
    public StoreTagsRequest.AddTag createStoreTagsRequestAddTag() {
        return new StoreTagsRequest.AddTag();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link BankType }
     * 
     */
    public BankType createBankType() {
        return new BankType();
    }

    /**
     * Create an instance of {@link GetBillsRequestType.TransactionIdDescription }
     * 
     */
    public GetBillsRequestType.TransactionIdDescription createGetBillsRequestTypeTransactionIdDescription() {
        return new GetBillsRequestType.TransactionIdDescription();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType }
     * 
     */
    public TransactionDescriptionType createTransactionDescriptionType() {
        return new TransactionDescriptionType();
    }

    /**
     * Create an instance of {@link RelationDescriptionType.PreviousTransaction }
     * 
     */
    public RelationDescriptionType.PreviousTransaction createRelationDescriptionTypePreviousTransaction() {
        return new RelationDescriptionType.PreviousTransaction();
    }

    /**
     * Create an instance of {@link TariffByVolumeType.TimeTarificationInterval }
     * 
     */
    public TariffByVolumeType.TimeTarificationInterval createTariffByVolumeTypeTimeTarificationInterval() {
        return new TariffByVolumeType.TimeTarificationInterval();
    }

    /**
     * Create an instance of {@link RelationDescriptionType }
     * 
     */
    public RelationDescriptionType createRelationDescriptionType() {
        return new RelationDescriptionType();
    }

    /**
     * Create an instance of {@link AuthentificationDataType }
     * 
     */
    public AuthentificationDataType createAuthentificationDataType() {
        return new AuthentificationDataType();
    }

    /**
     * Create an instance of {@link FinancialDescriptionItemType.PaymentInfo }
     * 
     */
    public FinancialDescriptionItemType.PaymentInfo createFinancialDescriptionItemTypePaymentInfo() {
        return new FinancialDescriptionItemType.PaymentInfo();
    }

    /**
     * Create an instance of {@link OtherStatusesType }
     * 
     */
    public OtherStatusesType createOtherStatusesType() {
        return new OtherStatusesType();
    }

    /**
     * Create an instance of {@link TariffBindingsMetaDataItemType }
     * 
     */
    public TariffBindingsMetaDataItemType createTariffBindingsMetaDataItemType() {
        return new TariffBindingsMetaDataItemType();
    }

    /**
     * Create an instance of {@link GetBillsResponseType }
     * 
     */
    public GetBillsResponseType createGetBillsResponseType() {
        return new GetBillsResponseType();
    }

    /**
     * Create an instance of {@link HolderInfoDescriptionType }
     * 
     */
    public HolderInfoDescriptionType createHolderInfoDescriptionType() {
        return new HolderInfoDescriptionType();
    }

    /**
     * Create an instance of {@link ContactsType }
     * 
     */
    public ContactsType createContactsType() {
        return new ContactsType();
    }

    /**
     * Create an instance of {@link StoreTagsRequest }
     * 
     */
    public StoreTagsRequest createStoreTagsRequest() {
        return new StoreTagsRequest();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType.MacDescription }
     * 
     */
    public TransactionDescriptionType.MacDescription createTransactionDescriptionTypeMacDescription() {
        return new TransactionDescriptionType.MacDescription();
    }

    /**
     * Create an instance of {@link MacResponseType }
     * 
     */
    public MacResponseType createMacResponseType() {
        return new MacResponseType();
    }

    /**
     * Create an instance of {@link DocumentType }
     * 
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.Tags }
     * 
     */
    public GetTransactionsRequest.Tags createGetTransactionsRequestTags() {
        return new GetTransactionsRequest.Tags();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link HolderIdDescriptionType }
     * 
     */
    public HolderIdDescriptionType createHolderIdDescriptionType() {
        return new HolderIdDescriptionType();
    }

    /**
     * Create an instance of {@link AdditionalInfoType }
     * 
     */
    public AdditionalInfoType createAdditionalInfoType() {
        return new AdditionalInfoType();
    }

    /**
     * Create an instance of {@link CardIdType }
     * 
     */
    public CardIdType createCardIdType() {
        return new CardIdType();
    }

    /**
     * Create an instance of {@link SubscriptionType }
     * 
     */
    public SubscriptionType createSubscriptionType() {
        return new SubscriptionType();
    }

    /**
     * Create an instance of {@link OMSType }
     * 
     */
    public OMSType createOMSType() {
        return new OMSType();
    }

    /**
     * Create an instance of {@link LegalIdDescriptionType }
     * 
     */
    public LegalIdDescriptionType createLegalIdDescriptionType() {
        return new LegalIdDescriptionType();
    }

    /**
     * Create an instance of {@link ServiceCustomerType }
     * 
     */
    public ServiceCustomerType createServiceCustomerType() {
        return new ServiceCustomerType();
    }

    /**
     * Create an instance of {@link AccountingDescriptionItemType }
     * 
     */
    public AccountingDescriptionItemType createAccountingDescriptionItemType() {
        return new AccountingDescriptionItemType();
    }

    /**
     * Create an instance of {@link UECPointIdentificationType }
     * 
     */
    public UECPointIdentificationType createUECPointIdentificationType() {
        return new UECPointIdentificationType();
    }

    /**
     * Create an instance of {@link PersonInfoType }
     * 
     */
    public PersonInfoType createPersonInfoType() {
        return new PersonInfoType();
    }

    /**
     * Create an instance of {@link BillType }
     * 
     */
    public BillType createBillType() {
        return new BillType();
    }

    /**
     * Create an instance of {@link DeviceDescriptionType }
     * 
     */
    public DeviceDescriptionType createDeviceDescriptionType() {
        return new DeviceDescriptionType();
    }

    /**
     * Create an instance of {@link TariffListType }
     * 
     */
    public TariffListType createTariffListType() {
        return new TariffListType();
    }

    /**
     * Create an instance of {@link HolderInfoDescriptionType.PersonIds }
     * 
     */
    public HolderInfoDescriptionType.PersonIds createHolderInfoDescriptionTypePersonIds() {
        return new HolderInfoDescriptionType.PersonIds();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes createGetTransactionsRequestTransactionTypesIncludeTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes();
    }

    /**
     * Create an instance of {@link TransactionDescriptionType.AccountingDescription }
     * 
     */
    public TransactionDescriptionType.AccountingDescription createTransactionDescriptionTypeAccountingDescription() {
        return new TransactionDescriptionType.AccountingDescription();
    }

    /**
     * Create an instance of {@link TariffType.TariffByPeriods }
     * 
     */
    public TariffType.TariffByPeriods createTariffTypeTariffByPeriods() {
        return new TariffType.TariffByPeriods();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest.TransactionTypes }
     * 
     */
    public GetTransactionsRequest.TransactionTypes createGetTransactionsRequestTransactionTypes() {
        return new GetTransactionsRequest.TransactionTypes();
    }

    /**
     * Create an instance of {@link GetBillsRequestType }
     * 
     */
    public GetBillsRequestType createGetBillsRequestType() {
        return new GetBillsRequestType();
    }

    /**
     * Create an instance of {@link ErrorListType }
     * 
     */
    public ErrorListType createErrorListType() {
        return new ErrorListType();
    }

    /**
     * Create an instance of {@link PaymentAdditionalInfoType }
     * 
     */
    public PaymentAdditionalInfoType createPaymentAdditionalInfoType() {
        return new PaymentAdditionalInfoType();
    }

    /**
     * Create an instance of {@link GetTransactionsRequest }
     * 
     */
    public GetTransactionsRequest createGetTransactionsRequest() {
        return new GetTransactionsRequest();
    }

    /**
     * Create an instance of {@link PensionFundType }
     * 
     */
    public PensionFundType createPensionFundType() {
        return new PensionFundType();
    }

    /**
     * Create an instance of {@link TariffBindingsItemType.MetaData }
     * 
     */
    public TariffBindingsItemType.MetaData createTariffBindingsItemTypeMetaData() {
        return new TariffBindingsItemType.MetaData();
    }

    /**
     * Create an instance of {@link TransactionSourceDescriptionType }
     * 
     */
    public TransactionSourceDescriptionType createTransactionSourceDescriptionType() {
        return new TransactionSourceDescriptionType();
    }

    /**
     * Create an instance of {@link AuthorityDocumentType }
     * 
     */
    public AuthorityDocumentType createAuthorityDocumentType() {
        return new AuthorityDocumentType();
    }

    /**
     * Create an instance of {@link ServiceProviderType }
     * 
     */
    public ServiceProviderType createServiceProviderType() {
        return new ServiceProviderType();
    }

    /**
     * Create an instance of {@link BillListType }
     * 
     */
    public BillListType createBillListType() {
        return new BillListType();
    }

    /**
     * Create an instance of {@link TariffByVolumeType }
     * 
     */
    public TariffByVolumeType createTariffByVolumeType() {
        return new TariffByVolumeType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTransactionsResponse")
    public JAXBElement<ErrorListType> createStoreTransactionsResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreTransactionsResponse_QNAME, ErrorListType.class, null, value);
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
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTagsResponse")
    public JAXBElement<ErrorListType> createStoreTagsResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreTagsResponse_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "getTransactionsResponse")
    public JAXBElement<TransactionListType> createGetTransactionsResponse(TransactionListType value) {
        return new JAXBElement<TransactionListType>(_GetTransactionsResponse_QNAME, TransactionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionDescriptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/transaction/v1", name = "transactionDescription")
    public JAXBElement<TransactionDescriptionType> createTransactionDescription(TransactionDescriptionType value) {
        return new JAXBElement<TransactionDescriptionType>(_TransactionDescription_QNAME, TransactionDescriptionType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", name = "storeTransactionsRequest")
    public JAXBElement<TransactionListType> createStoreTransactionsRequest(TransactionListType value) {
        return new JAXBElement<TransactionListType>(_StoreTransactionsRequest_QNAME, TransactionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UECPointIdentificationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "requester")
    public JAXBElement<UECPointIdentificationType> createRequester(UECPointIdentificationType value) {
        return new JAXBElement<UECPointIdentificationType>(_Requester_QNAME, UECPointIdentificationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", name = "storeNotificationSubscribersResponse")
    public JAXBElement<ErrorListType> createStoreNotificationSubscribersResponse(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_StoreNotificationSubscribersResponse_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "residenceAddress", scope = HolderInfoDescriptionType.class)
    public JAXBElement<AddressType> createHolderInfoDescriptionTypeResidenceAddress(AddressType value) {
        return new JAXBElement<AddressType>(_HolderInfoDescriptionTypeResidenceAddress_QNAME, AddressType.class, HolderInfoDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SchoolInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "schoolInfo", scope = HolderInfoDescriptionType.class)
    public JAXBElement<SchoolInfoType> createHolderInfoDescriptionTypeSchoolInfo(SchoolInfoType value) {
        return new JAXBElement<SchoolInfoType>(_HolderInfoDescriptionTypeSchoolInfo_QNAME, SchoolInfoType.class, HolderInfoDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniversityInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "universityInfo", scope = HolderInfoDescriptionType.class)
    public JAXBElement<UniversityInfoType> createHolderInfoDescriptionTypeUniversityInfo(UniversityInfoType value) {
        return new JAXBElement<UniversityInfoType>(_HolderInfoDescriptionTypeUniversityInfo_QNAME, UniversityInfoType.class, HolderInfoDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorityPersonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "authorityPerson", scope = HolderInfoDescriptionType.class)
    public JAXBElement<AuthorityPersonType> createHolderInfoDescriptionTypeAuthorityPerson(AuthorityPersonType value) {
        return new JAXBElement<AuthorityPersonType>(_HolderInfoDescriptionTypeAuthorityPerson_QNAME, AuthorityPersonType.class, HolderInfoDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OMSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "oms", scope = HolderInfoDescriptionType.class)
    public JAXBElement<OMSType> createHolderInfoDescriptionTypeOms(OMSType value) {
        return new JAXBElement<OMSType>(_HolderInfoDescriptionTypeOms_QNAME, OMSType.class, HolderInfoDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "middleName", scope = NameType.class)
    public JAXBElement<String> createNameTypeMiddleName(String value) {
        return new JAXBElement<String>(_NameTypeMiddleName_QNAME, String.class, NameType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "series", scope = DocumentType.class)
    public JAXBElement<String> createDocumentTypeSeries(String value) {
        return new JAXBElement<String>(_DocumentTypeSeries_QNAME, String.class, DocumentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "email", scope = ContactsType.class)
    public JAXBElement<String> createContactsTypeEmail(String value) {
        return new JAXBElement<String>(_ContactsTypeEmail_QNAME, String.class, ContactsType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "mobilePhone", scope = ContactsType.class)
    public JAXBElement<String> createContactsTypeMobilePhone(String value) {
        return new JAXBElement<String>(_ContactsTypeMobilePhone_QNAME, String.class, ContactsType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "regionName", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeRegionName(String value) {
        return new JAXBElement<String>(_AddressTypeRegionName_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "corp", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeCorp(String value) {
        return new JAXBElement<String>(_AddressTypeCorp_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "regionCode", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeRegionCode(String value) {
        return new JAXBElement<String>(_AddressTypeRegionCode_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "districtCode", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeDistrictCode(String value) {
        return new JAXBElement<String>(_AddressTypeDistrictCode_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "building", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeBuilding(String value) {
        return new JAXBElement<String>(_AddressTypeBuilding_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "index", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeIndex(String value) {
        return new JAXBElement<String>(_AddressTypeIndex_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "districtName", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeDistrictName(String value) {
        return new JAXBElement<String>(_AddressTypeDistrictName_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "flat", scope = AddressType.class)
    public JAXBElement<String> createAddressTypeFlat(String value) {
        return new JAXBElement<String>(_AddressTypeFlat_QNAME, String.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "middleName", scope = AuthorityPersonType.class)
    public JAXBElement<String> createAuthorityPersonTypeMiddleName(String value) {
        return new JAXBElement<String>(_NameTypeMiddleName_QNAME, String.class, AuthorityPersonType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "birthRegionCode", scope = PersonInfoType.class)
    public JAXBElement<String> createPersonInfoTypeBirthRegionCode(String value) {
        return new JAXBElement<String>(_PersonInfoTypeBirthRegionCode_QNAME, String.class, PersonInfoType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "birthRegionName", scope = PersonInfoType.class)
    public JAXBElement<String> createPersonInfoTypeBirthRegionName(String value) {
        return new JAXBElement<String>(_PersonInfoTypeBirthRegionName_QNAME, String.class, PersonInfoType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "birthDistrictName", scope = PersonInfoType.class)
    public JAXBElement<String> createPersonInfoTypeBirthDistrictName(String value) {
        return new JAXBElement<String>(_PersonInfoTypeBirthDistrictName_QNAME, String.class, PersonInfoType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "citizenship", scope = PersonInfoType.class)
    public JAXBElement<String> createPersonInfoTypeCitizenship(String value) {
        return new JAXBElement<String>(_PersonInfoTypeCitizenship_QNAME, String.class, PersonInfoType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "photo", scope = PersonInfoType.class)
    public JAXBElement<byte[]> createPersonInfoTypePhoto(byte[] value) {
        return new JAXBElement<byte[]>(_PersonInfoTypePhoto_QNAME, byte[].class, PersonInfoType.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.msk.ru/uec/identification/v1", name = "birthDistrictCode", scope = PersonInfoType.class)
    public JAXBElement<String> createPersonInfoTypeBirthDistrictCode(String value) {
        return new JAXBElement<String>(_PersonInfoTypeBirthDistrictCode_QNAME, String.class, PersonInfoType.class, value);
    }

}
