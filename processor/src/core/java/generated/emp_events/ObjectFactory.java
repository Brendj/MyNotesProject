
package generated.emp_events;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.emp_events package. 
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

    private final static QName _SendSubscriptionStreamEventsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "sendSubscriptionStreamEventsResponse");
    private final static QName _CreateCitizenProfileRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "createCitizenProfileRequest");
    private final static QName _SelectCitizenPaymentsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectCitizenPaymentsRequest");
    private final static QName _GetSubscriptionStreamCategoryResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getSubscriptionStreamCategoryResponse");
    private final static QName _SelectSubscriptionStreamCategoriesResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectSubscriptionStreamCategoriesResponse");
    private final static QName _SetCitizenProfileResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "setCitizenProfileResponse");
    private final static QName _DeleteCitizenProfileOptionsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "deleteCitizenProfileOptionsRequest");
    private final static QName _DropCitizenSubscriptionSettingsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "dropCitizenSubscriptionSettingsResponse");
    private final static QName _SelectSubscriptionStreamsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectSubscriptionStreamsResponse");
    private final static QName _SelectCitizenSubscriptionsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectCitizenSubscriptionsResponse");
    private final static QName _SendSubscriptionStreamEventsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "sendSubscriptionStreamEventsRequest");
    private final static QName _GetStreamDictValueResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getStreamDictValueResponse");
    private final static QName _GetStreamDictValueRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getStreamDictValueRequest");
    private final static QName _GetSubscriptionStreamCategoryRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getSubscriptionStreamCategoryRequest");
    private final static QName _GetSubscriptionStreamResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getSubscriptionStreamResponse");
    private final static QName _SelectCitizenSubscriptionsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectCitizenSubscriptionsRequest");
    private final static QName _DeleteCitizenProfileOptionsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "deleteCitizenProfileOptionsResponse");
    private final static QName _SelectSubscriptionStreamCategoriesRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectSubscriptionStreamCategoriesRequest");
    private final static QName _SetCitizenProfileRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "setCitizenProfileRequest");
    private final static QName _SelectStreamTariffsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectStreamTariffsRequest");
    private final static QName _DeactivateCitizenSubscriptionResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "deactivateCitizenSubscriptionResponse");
    private final static QName _CreateCitizenProfileResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "createCitizenProfileResponse");
    private final static QName _SetUpCitizenSubscriptionSettingsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "setUpCitizenSubscriptionSettingsRequest");
    private final static QName _GetSubscriptionStreamRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getSubscriptionStreamRequest");
    private final static QName _DeactivateCitizenSubscriptionRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "deactivateCitizenSubscriptionRequest");
    private final static QName _DropCitizenSubscriptionSettingsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "dropCitizenSubscriptionSettingsRequest");
    private final static QName _SelectStreamTariffsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectStreamTariffsResponse");
    private final static QName _SelectSubscriptionStreamsRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectSubscriptionStreamsRequest");
    private final static QName _ActivateCitizenSubscriptionRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "activateCitizenSubscriptionRequest");
    private final static QName _SetUpCitizenSubscriptionSettingsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "setUpCitizenSubscriptionSettingsResponse");
    private final static QName _GetCitizenProfileResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getCitizenProfileResponse");
    private final static QName _SelectCitizenPaymentsResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "selectCitizenPaymentsResponse");
    private final static QName _ActivateCitizenSubscriptionResponse_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "activateCitizenSubscriptionResponse");
    private final static QName _GetCitizenProfileRequest_QNAME = new QName("urn://subscription.api.emp.altarix.ru", "getCitizenProfileRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.emp_events
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventMessageType }
     * 
     */
    public EventMessageType createEventMessageType() {
        return new EventMessageType();
    }

    /**
     * Create an instance of {@link EventFilterType }
     * 
     */
    public EventFilterType createEventFilterType() {
        return new EventFilterType();
    }

    /**
     * Create an instance of {@link EventFilterType.OptionGroup }
     * 
     */
    public EventFilterType.OptionGroup createEventFilterTypeOptionGroup() {
        return new EventFilterType.OptionGroup();
    }

    /**
     * Create an instance of {@link EventFilterType.Options }
     * 
     */
    public EventFilterType.Options createEventFilterTypeOptions() {
        return new EventFilterType.Options();
    }

    /**
     * Create an instance of {@link EventFilterType.Persons }
     * 
     */
    public EventFilterType.Persons createEventFilterTypePersons() {
        return new EventFilterType.Persons();
    }

    /**
     * Create an instance of {@link CitizenPaymentType }
     * 
     */
    public CitizenPaymentType createCitizenPaymentType() {
        return new CitizenPaymentType();
    }

    /**
     * Create an instance of {@link StreamTariffType }
     * 
     */
    public StreamTariffType createStreamTariffType() {
        return new StreamTariffType();
    }

    /**
     * Create an instance of {@link CitizenProfileBaseType }
     * 
     */
    public CitizenProfileBaseType createCitizenProfileBaseType() {
        return new CitizenProfileBaseType();
    }

    /**
     * Create an instance of {@link StreamType }
     * 
     */
    public StreamType createStreamType() {
        return new StreamType();
    }

    /**
     * Create an instance of {@link SubscriptionType }
     * 
     */
    public SubscriptionType createSubscriptionType() {
        return new SubscriptionType();
    }

    /**
     * Create an instance of {@link EventType }
     * 
     */
    public EventType createEventType() {
        return new EventType();
    }

    /**
     * Create an instance of {@link DeleteCitizenProfileOptionsRequestType }
     * 
     */
    public DeleteCitizenProfileOptionsRequestType createDeleteCitizenProfileOptionsRequestType() {
        return new DeleteCitizenProfileOptionsRequestType();
    }

    /**
     * Create an instance of {@link SendSubscriptionStreamEventsRequestType }
     * 
     */
    public SendSubscriptionStreamEventsRequestType createSendSubscriptionStreamEventsRequestType() {
        return new SendSubscriptionStreamEventsRequestType();
    }

    /**
     * Create an instance of {@link SelectCitizenSubscriptionsResponseType }
     * 
     */
    public SelectCitizenSubscriptionsResponseType createSelectCitizenSubscriptionsResponseType() {
        return new SelectCitizenSubscriptionsResponseType();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamsResponseType }
     * 
     */
    public SelectSubscriptionStreamsResponseType createSelectSubscriptionStreamsResponseType() {
        return new SelectSubscriptionStreamsResponseType();
    }

    /**
     * Create an instance of {@link CreateCitizenProfileRequestType }
     * 
     */
    public CreateCitizenProfileRequestType createCreateCitizenProfileRequestType() {
        return new CreateCitizenProfileRequestType();
    }

    /**
     * Create an instance of {@link CreateCitizenProfileRequestType.Options }
     * 
     */
    public CreateCitizenProfileRequestType.Options createCreateCitizenProfileRequestTypeOptions() {
        return new CreateCitizenProfileRequestType.Options();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamCategoriesResponseType }
     * 
     */
    public SelectSubscriptionStreamCategoriesResponseType createSelectSubscriptionStreamCategoriesResponseType() {
        return new SelectSubscriptionStreamCategoriesResponseType();
    }

    /**
     * Create an instance of {@link CreateCitizenProfileResponseType }
     * 
     */
    public CreateCitizenProfileResponseType createCreateCitizenProfileResponseType() {
        return new CreateCitizenProfileResponseType();
    }

    /**
     * Create an instance of {@link SetUpCitizenSubscriptionSettingsRequestType }
     * 
     */
    public SetUpCitizenSubscriptionSettingsRequestType createSetUpCitizenSubscriptionSettingsRequestType() {
        return new SetUpCitizenSubscriptionSettingsRequestType();
    }

    /**
     * Create an instance of {@link GetStreamDictValueResponseType }
     * 
     */
    public GetStreamDictValueResponseType createGetStreamDictValueResponseType() {
        return new GetStreamDictValueResponseType();
    }

    /**
     * Create an instance of {@link DropCitizenSubscriptionSettingsRequestType }
     * 
     */
    public DropCitizenSubscriptionSettingsRequestType createDropCitizenSubscriptionSettingsRequestType() {
        return new DropCitizenSubscriptionSettingsRequestType();
    }

    /**
     * Create an instance of {@link SelectStreamTariffsResponseType }
     * 
     */
    public SelectStreamTariffsResponseType createSelectStreamTariffsResponseType() {
        return new SelectStreamTariffsResponseType();
    }

    /**
     * Create an instance of {@link DeactivateCitizenSubscriptionRequestType }
     * 
     */
    public DeactivateCitizenSubscriptionRequestType createDeactivateCitizenSubscriptionRequestType() {
        return new DeactivateCitizenSubscriptionRequestType();
    }

    /**
     * Create an instance of {@link ActivateCitizenSubscriptionRequestType }
     * 
     */
    public ActivateCitizenSubscriptionRequestType createActivateCitizenSubscriptionRequestType() {
        return new ActivateCitizenSubscriptionRequestType();
    }

    /**
     * Create an instance of {@link SelectCitizenPaymentsResponseType }
     * 
     */
    public SelectCitizenPaymentsResponseType createSelectCitizenPaymentsResponseType() {
        return new SelectCitizenPaymentsResponseType();
    }

    /**
     * Create an instance of {@link GetCitizenProfileResponseType }
     * 
     */
    public GetCitizenProfileResponseType createGetCitizenProfileResponseType() {
        return new GetCitizenProfileResponseType();
    }

    /**
     * Create an instance of {@link SetUpCitizenSubscriptionSettingsResponseType }
     * 
     */
    public SetUpCitizenSubscriptionSettingsResponseType createSetUpCitizenSubscriptionSettingsResponseType() {
        return new SetUpCitizenSubscriptionSettingsResponseType();
    }

    /**
     * Create an instance of {@link GetCitizenProfileRequestType }
     * 
     */
    public GetCitizenProfileRequestType createGetCitizenProfileRequestType() {
        return new GetCitizenProfileRequestType();
    }

    /**
     * Create an instance of {@link ActivateCitizenSubscriptionResponseType }
     * 
     */
    public ActivateCitizenSubscriptionResponseType createActivateCitizenSubscriptionResponseType() {
        return new ActivateCitizenSubscriptionResponseType();
    }

    /**
     * Create an instance of {@link GetSubscriptionStreamRequestType }
     * 
     */
    public GetSubscriptionStreamRequestType createGetSubscriptionStreamRequestType() {
        return new GetSubscriptionStreamRequestType();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamsRequestType }
     * 
     */
    public SelectSubscriptionStreamsRequestType createSelectSubscriptionStreamsRequestType() {
        return new SelectSubscriptionStreamsRequestType();
    }

    /**
     * Create an instance of {@link SelectCitizenSubscriptionsRequestType }
     * 
     */
    public SelectCitizenSubscriptionsRequestType createSelectCitizenSubscriptionsRequestType() {
        return new SelectCitizenSubscriptionsRequestType();
    }

    /**
     * Create an instance of {@link DeleteCitizenProfileOptionsResponseType }
     * 
     */
    public DeleteCitizenProfileOptionsResponseType createDeleteCitizenProfileOptionsResponseType() {
        return new DeleteCitizenProfileOptionsResponseType();
    }

    /**
     * Create an instance of {@link GetSubscriptionStreamResponseType }
     * 
     */
    public GetSubscriptionStreamResponseType createGetSubscriptionStreamResponseType() {
        return new GetSubscriptionStreamResponseType();
    }

    /**
     * Create an instance of {@link GetSubscriptionStreamCategoryRequestType }
     * 
     */
    public GetSubscriptionStreamCategoryRequestType createGetSubscriptionStreamCategoryRequestType() {
        return new GetSubscriptionStreamCategoryRequestType();
    }

    /**
     * Create an instance of {@link GetStreamDictValueRequestType }
     * 
     */
    public GetStreamDictValueRequestType createGetStreamDictValueRequestType() {
        return new GetStreamDictValueRequestType();
    }

    /**
     * Create an instance of {@link DeactivateCitizenSubscriptionResponseType }
     * 
     */
    public DeactivateCitizenSubscriptionResponseType createDeactivateCitizenSubscriptionResponseType() {
        return new DeactivateCitizenSubscriptionResponseType();
    }

    /**
     * Create an instance of {@link SetCitizenProfileRequestType }
     * 
     */
    public SetCitizenProfileRequestType createSetCitizenProfileRequestType() {
        return new SetCitizenProfileRequestType();
    }

    /**
     * Create an instance of {@link SelectStreamTariffsRequestType }
     * 
     */
    public SelectStreamTariffsRequestType createSelectStreamTariffsRequestType() {
        return new SelectStreamTariffsRequestType();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamCategoriesRequestType }
     * 
     */
    public SelectSubscriptionStreamCategoriesRequestType createSelectSubscriptionStreamCategoriesRequestType() {
        return new SelectSubscriptionStreamCategoriesRequestType();
    }

    /**
     * Create an instance of {@link GetSubscriptionStreamCategoryResponseType }
     * 
     */
    public GetSubscriptionStreamCategoryResponseType createGetSubscriptionStreamCategoryResponseType() {
        return new GetSubscriptionStreamCategoryResponseType();
    }

    /**
     * Create an instance of {@link SelectCitizenPaymentsRequestType }
     * 
     */
    public SelectCitizenPaymentsRequestType createSelectCitizenPaymentsRequestType() {
        return new SelectCitizenPaymentsRequestType();
    }

    /**
     * Create an instance of {@link SendSubscriptionStreamEventsResponseType }
     * 
     */
    public SendSubscriptionStreamEventsResponseType createSendSubscriptionStreamEventsResponseType() {
        return new SendSubscriptionStreamEventsResponseType();
    }

    /**
     * Create an instance of {@link DropCitizenSubscriptionSettingsResponseType }
     * 
     */
    public DropCitizenSubscriptionSettingsResponseType createDropCitizenSubscriptionSettingsResponseType() {
        return new DropCitizenSubscriptionSettingsResponseType();
    }

    /**
     * Create an instance of {@link SetCitizenProfileResponseType }
     * 
     */
    public SetCitizenProfileResponseType createSetCitizenProfileResponseType() {
        return new SetCitizenProfileResponseType();
    }

    /**
     * Create an instance of {@link StreamSettingType }
     * 
     */
    public StreamSettingType createStreamSettingType() {
        return new StreamSettingType();
    }

    /**
     * Create an instance of {@link BaseResponseType }
     * 
     */
    public BaseResponseType createBaseResponseType() {
        return new BaseResponseType();
    }

    /**
     * Create an instance of {@link EventMessageParameterType }
     * 
     */
    public EventMessageParameterType createEventMessageParameterType() {
        return new EventMessageParameterType();
    }

    /**
     * Create an instance of {@link StreamTariffRuleType }
     * 
     */
    public StreamTariffRuleType createStreamTariffRuleType() {
        return new StreamTariffRuleType();
    }

    /**
     * Create an instance of {@link CitizenProfileOptionType }
     * 
     */
    public CitizenProfileOptionType createCitizenProfileOptionType() {
        return new CitizenProfileOptionType();
    }

    /**
     * Create an instance of {@link EventMessageObjectType }
     * 
     */
    public EventMessageObjectType createEventMessageObjectType() {
        return new EventMessageObjectType();
    }

    /**
     * Create an instance of {@link CitizenProfileType }
     * 
     */
    public CitizenProfileType createCitizenProfileType() {
        return new CitizenProfileType();
    }

    /**
     * Create an instance of {@link CitizenProfileOptionBaseType }
     * 
     */
    public CitizenProfileOptionBaseType createCitizenProfileOptionBaseType() {
        return new CitizenProfileOptionBaseType();
    }

    /**
     * Create an instance of {@link CitizenPaymentResourceType }
     * 
     */
    public CitizenPaymentResourceType createCitizenPaymentResourceType() {
        return new CitizenPaymentResourceType();
    }

    /**
     * Create an instance of {@link CitizenPaymentStatusType }
     * 
     */
    public CitizenPaymentStatusType createCitizenPaymentStatusType() {
        return new CitizenPaymentStatusType();
    }

    /**
     * Create an instance of {@link StreamCategoryType }
     * 
     */
    public StreamCategoryType createStreamCategoryType() {
        return new StreamCategoryType();
    }

    /**
     * Create an instance of {@link BaseRequestType }
     * 
     */
    public BaseRequestType createBaseRequestType() {
        return new BaseRequestType();
    }

    /**
     * Create an instance of {@link EventMessageType.Parameters }
     * 
     */
    public EventMessageType.Parameters createEventMessageTypeParameters() {
        return new EventMessageType.Parameters();
    }

    /**
     * Create an instance of {@link EventMessageType.Objects }
     * 
     */
    public EventMessageType.Objects createEventMessageTypeObjects() {
        return new EventMessageType.Objects();
    }

    /**
     * Create an instance of {@link EventFilterType.Groups }
     * 
     */
    public EventFilterType.Groups createEventFilterTypeGroups() {
        return new EventFilterType.Groups();
    }

    /**
     * Create an instance of {@link EventFilterType.OptionGroup.Option }
     * 
     */
    public EventFilterType.OptionGroup.Option createEventFilterTypeOptionGroupOption() {
        return new EventFilterType.OptionGroup.Option();
    }

    /**
     * Create an instance of {@link EventFilterType.Options.Option }
     * 
     */
    public EventFilterType.Options.Option createEventFilterTypeOptionsOption() {
        return new EventFilterType.Options.Option();
    }

    /**
     * Create an instance of {@link EventFilterType.Persons.Person }
     * 
     */
    public EventFilterType.Persons.Person createEventFilterTypePersonsPerson() {
        return new EventFilterType.Persons.Person();
    }

    /**
     * Create an instance of {@link CitizenPaymentType.Resources }
     * 
     */
    public CitizenPaymentType.Resources createCitizenPaymentTypeResources() {
        return new CitizenPaymentType.Resources();
    }

    /**
     * Create an instance of {@link StreamTariffType.Rules }
     * 
     */
    public StreamTariffType.Rules createStreamTariffTypeRules() {
        return new StreamTariffType.Rules();
    }

    /**
     * Create an instance of {@link CitizenProfileBaseType.Options }
     * 
     */
    public CitizenProfileBaseType.Options createCitizenProfileBaseTypeOptions() {
        return new CitizenProfileBaseType.Options();
    }

    /**
     * Create an instance of {@link StreamType.Settings }
     * 
     */
    public StreamType.Settings createStreamTypeSettings() {
        return new StreamType.Settings();
    }

    /**
     * Create an instance of {@link StreamType.Options }
     * 
     */
    public StreamType.Options createStreamTypeOptions() {
        return new StreamType.Options();
    }

    /**
     * Create an instance of {@link SubscriptionType.Settings }
     * 
     */
    public SubscriptionType.Settings createSubscriptionTypeSettings() {
        return new SubscriptionType.Settings();
    }

    /**
     * Create an instance of {@link SubscriptionType.Options }
     * 
     */
    public SubscriptionType.Options createSubscriptionTypeOptions() {
        return new SubscriptionType.Options();
    }

    /**
     * Create an instance of {@link EventType.Filters }
     * 
     */
    public EventType.Filters createEventTypeFilters() {
        return new EventType.Filters();
    }

    /**
     * Create an instance of {@link DeleteCitizenProfileOptionsRequestType.Options }
     * 
     */
    public DeleteCitizenProfileOptionsRequestType.Options createDeleteCitizenProfileOptionsRequestTypeOptions() {
        return new DeleteCitizenProfileOptionsRequestType.Options();
    }

    /**
     * Create an instance of {@link SendSubscriptionStreamEventsRequestType.Events }
     * 
     */
    public SendSubscriptionStreamEventsRequestType.Events createSendSubscriptionStreamEventsRequestTypeEvents() {
        return new SendSubscriptionStreamEventsRequestType.Events();
    }

    /**
     * Create an instance of {@link SelectCitizenSubscriptionsResponseType.Return }
     * 
     */
    public SelectCitizenSubscriptionsResponseType.Return createSelectCitizenSubscriptionsResponseTypeReturn() {
        return new SelectCitizenSubscriptionsResponseType.Return();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamsResponseType.Return }
     * 
     */
    public SelectSubscriptionStreamsResponseType.Return createSelectSubscriptionStreamsResponseTypeReturn() {
        return new SelectSubscriptionStreamsResponseType.Return();
    }

    /**
     * Create an instance of {@link CreateCitizenProfileRequestType.Options.Option }
     * 
     */
    public CreateCitizenProfileRequestType.Options.Option createCreateCitizenProfileRequestTypeOptionsOption() {
        return new CreateCitizenProfileRequestType.Options.Option();
    }

    /**
     * Create an instance of {@link SelectSubscriptionStreamCategoriesResponseType.Return }
     * 
     */
    public SelectSubscriptionStreamCategoriesResponseType.Return createSelectSubscriptionStreamCategoriesResponseTypeReturn() {
        return new SelectSubscriptionStreamCategoriesResponseType.Return();
    }

    /**
     * Create an instance of {@link CreateCitizenProfileResponseType.Return }
     * 
     */
    public CreateCitizenProfileResponseType.Return createCreateCitizenProfileResponseTypeReturn() {
        return new CreateCitizenProfileResponseType.Return();
    }

    /**
     * Create an instance of {@link SetUpCitizenSubscriptionSettingsRequestType.Settings }
     * 
     */
    public SetUpCitizenSubscriptionSettingsRequestType.Settings createSetUpCitizenSubscriptionSettingsRequestTypeSettings() {
        return new SetUpCitizenSubscriptionSettingsRequestType.Settings();
    }

    /**
     * Create an instance of {@link GetStreamDictValueResponseType.Return }
     * 
     */
    public GetStreamDictValueResponseType.Return createGetStreamDictValueResponseTypeReturn() {
        return new GetStreamDictValueResponseType.Return();
    }

    /**
     * Create an instance of {@link DropCitizenSubscriptionSettingsRequestType.Settings }
     * 
     */
    public DropCitizenSubscriptionSettingsRequestType.Settings createDropCitizenSubscriptionSettingsRequestTypeSettings() {
        return new DropCitizenSubscriptionSettingsRequestType.Settings();
    }

    /**
     * Create an instance of {@link SelectStreamTariffsResponseType.Return }
     * 
     */
    public SelectStreamTariffsResponseType.Return createSelectStreamTariffsResponseTypeReturn() {
        return new SelectStreamTariffsResponseType.Return();
    }

    /**
     * Create an instance of {@link DeactivateCitizenSubscriptionRequestType.Options }
     * 
     */
    public DeactivateCitizenSubscriptionRequestType.Options createDeactivateCitizenSubscriptionRequestTypeOptions() {
        return new DeactivateCitizenSubscriptionRequestType.Options();
    }

    /**
     * Create an instance of {@link ActivateCitizenSubscriptionRequestType.Settings }
     * 
     */
    public ActivateCitizenSubscriptionRequestType.Settings createActivateCitizenSubscriptionRequestTypeSettings() {
        return new ActivateCitizenSubscriptionRequestType.Settings();
    }

    /**
     * Create an instance of {@link ActivateCitizenSubscriptionRequestType.Options }
     * 
     */
    public ActivateCitizenSubscriptionRequestType.Options createActivateCitizenSubscriptionRequestTypeOptions() {
        return new ActivateCitizenSubscriptionRequestType.Options();
    }

    /**
     * Create an instance of {@link SelectCitizenPaymentsResponseType.Return }
     * 
     */
    public SelectCitizenPaymentsResponseType.Return createSelectCitizenPaymentsResponseTypeReturn() {
        return new SelectCitizenPaymentsResponseType.Return();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendSubscriptionStreamEventsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "sendSubscriptionStreamEventsResponse")
    public JAXBElement<SendSubscriptionStreamEventsResponseType> createSendSubscriptionStreamEventsResponse(SendSubscriptionStreamEventsResponseType value) {
        return new JAXBElement<SendSubscriptionStreamEventsResponseType>(_SendSubscriptionStreamEventsResponse_QNAME, SendSubscriptionStreamEventsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCitizenProfileRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "createCitizenProfileRequest")
    public JAXBElement<CreateCitizenProfileRequestType> createCreateCitizenProfileRequest(CreateCitizenProfileRequestType value) {
        return new JAXBElement<CreateCitizenProfileRequestType>(_CreateCitizenProfileRequest_QNAME, CreateCitizenProfileRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCitizenPaymentsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectCitizenPaymentsRequest")
    public JAXBElement<SelectCitizenPaymentsRequestType> createSelectCitizenPaymentsRequest(SelectCitizenPaymentsRequestType value) {
        return new JAXBElement<SelectCitizenPaymentsRequestType>(_SelectCitizenPaymentsRequest_QNAME, SelectCitizenPaymentsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptionStreamCategoryResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getSubscriptionStreamCategoryResponse")
    public JAXBElement<GetSubscriptionStreamCategoryResponseType> createGetSubscriptionStreamCategoryResponse(GetSubscriptionStreamCategoryResponseType value) {
        return new JAXBElement<GetSubscriptionStreamCategoryResponseType>(_GetSubscriptionStreamCategoryResponse_QNAME, GetSubscriptionStreamCategoryResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectSubscriptionStreamCategoriesResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectSubscriptionStreamCategoriesResponse")
    public JAXBElement<SelectSubscriptionStreamCategoriesResponseType> createSelectSubscriptionStreamCategoriesResponse(SelectSubscriptionStreamCategoriesResponseType value) {
        return new JAXBElement<SelectSubscriptionStreamCategoriesResponseType>(_SelectSubscriptionStreamCategoriesResponse_QNAME, SelectSubscriptionStreamCategoriesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCitizenProfileResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "setCitizenProfileResponse")
    public JAXBElement<SetCitizenProfileResponseType> createSetCitizenProfileResponse(SetCitizenProfileResponseType value) {
        return new JAXBElement<SetCitizenProfileResponseType>(_SetCitizenProfileResponse_QNAME, SetCitizenProfileResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCitizenProfileOptionsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "deleteCitizenProfileOptionsRequest")
    public JAXBElement<DeleteCitizenProfileOptionsRequestType> createDeleteCitizenProfileOptionsRequest(DeleteCitizenProfileOptionsRequestType value) {
        return new JAXBElement<DeleteCitizenProfileOptionsRequestType>(_DeleteCitizenProfileOptionsRequest_QNAME, DeleteCitizenProfileOptionsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DropCitizenSubscriptionSettingsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "dropCitizenSubscriptionSettingsResponse")
    public JAXBElement<DropCitizenSubscriptionSettingsResponseType> createDropCitizenSubscriptionSettingsResponse(DropCitizenSubscriptionSettingsResponseType value) {
        return new JAXBElement<DropCitizenSubscriptionSettingsResponseType>(_DropCitizenSubscriptionSettingsResponse_QNAME, DropCitizenSubscriptionSettingsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectSubscriptionStreamsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectSubscriptionStreamsResponse")
    public JAXBElement<SelectSubscriptionStreamsResponseType> createSelectSubscriptionStreamsResponse(SelectSubscriptionStreamsResponseType value) {
        return new JAXBElement<SelectSubscriptionStreamsResponseType>(_SelectSubscriptionStreamsResponse_QNAME, SelectSubscriptionStreamsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCitizenSubscriptionsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectCitizenSubscriptionsResponse")
    public JAXBElement<SelectCitizenSubscriptionsResponseType> createSelectCitizenSubscriptionsResponse(SelectCitizenSubscriptionsResponseType value) {
        return new JAXBElement<SelectCitizenSubscriptionsResponseType>(_SelectCitizenSubscriptionsResponse_QNAME, SelectCitizenSubscriptionsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendSubscriptionStreamEventsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "sendSubscriptionStreamEventsRequest")
    public JAXBElement<SendSubscriptionStreamEventsRequestType> createSendSubscriptionStreamEventsRequest(SendSubscriptionStreamEventsRequestType value) {
        return new JAXBElement<SendSubscriptionStreamEventsRequestType>(_SendSubscriptionStreamEventsRequest_QNAME, SendSubscriptionStreamEventsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStreamDictValueResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getStreamDictValueResponse")
    public JAXBElement<GetStreamDictValueResponseType> createGetStreamDictValueResponse(GetStreamDictValueResponseType value) {
        return new JAXBElement<GetStreamDictValueResponseType>(_GetStreamDictValueResponse_QNAME, GetStreamDictValueResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStreamDictValueRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getStreamDictValueRequest")
    public JAXBElement<GetStreamDictValueRequestType> createGetStreamDictValueRequest(GetStreamDictValueRequestType value) {
        return new JAXBElement<GetStreamDictValueRequestType>(_GetStreamDictValueRequest_QNAME, GetStreamDictValueRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptionStreamCategoryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getSubscriptionStreamCategoryRequest")
    public JAXBElement<GetSubscriptionStreamCategoryRequestType> createGetSubscriptionStreamCategoryRequest(GetSubscriptionStreamCategoryRequestType value) {
        return new JAXBElement<GetSubscriptionStreamCategoryRequestType>(_GetSubscriptionStreamCategoryRequest_QNAME, GetSubscriptionStreamCategoryRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptionStreamResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getSubscriptionStreamResponse")
    public JAXBElement<GetSubscriptionStreamResponseType> createGetSubscriptionStreamResponse(GetSubscriptionStreamResponseType value) {
        return new JAXBElement<GetSubscriptionStreamResponseType>(_GetSubscriptionStreamResponse_QNAME, GetSubscriptionStreamResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCitizenSubscriptionsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectCitizenSubscriptionsRequest")
    public JAXBElement<SelectCitizenSubscriptionsRequestType> createSelectCitizenSubscriptionsRequest(SelectCitizenSubscriptionsRequestType value) {
        return new JAXBElement<SelectCitizenSubscriptionsRequestType>(_SelectCitizenSubscriptionsRequest_QNAME, SelectCitizenSubscriptionsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCitizenProfileOptionsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "deleteCitizenProfileOptionsResponse")
    public JAXBElement<DeleteCitizenProfileOptionsResponseType> createDeleteCitizenProfileOptionsResponse(DeleteCitizenProfileOptionsResponseType value) {
        return new JAXBElement<DeleteCitizenProfileOptionsResponseType>(_DeleteCitizenProfileOptionsResponse_QNAME, DeleteCitizenProfileOptionsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectSubscriptionStreamCategoriesRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectSubscriptionStreamCategoriesRequest")
    public JAXBElement<SelectSubscriptionStreamCategoriesRequestType> createSelectSubscriptionStreamCategoriesRequest(SelectSubscriptionStreamCategoriesRequestType value) {
        return new JAXBElement<SelectSubscriptionStreamCategoriesRequestType>(_SelectSubscriptionStreamCategoriesRequest_QNAME, SelectSubscriptionStreamCategoriesRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCitizenProfileRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "setCitizenProfileRequest")
    public JAXBElement<SetCitizenProfileRequestType> createSetCitizenProfileRequest(SetCitizenProfileRequestType value) {
        return new JAXBElement<SetCitizenProfileRequestType>(_SetCitizenProfileRequest_QNAME, SetCitizenProfileRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectStreamTariffsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectStreamTariffsRequest")
    public JAXBElement<SelectStreamTariffsRequestType> createSelectStreamTariffsRequest(SelectStreamTariffsRequestType value) {
        return new JAXBElement<SelectStreamTariffsRequestType>(_SelectStreamTariffsRequest_QNAME, SelectStreamTariffsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeactivateCitizenSubscriptionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "deactivateCitizenSubscriptionResponse")
    public JAXBElement<DeactivateCitizenSubscriptionResponseType> createDeactivateCitizenSubscriptionResponse(DeactivateCitizenSubscriptionResponseType value) {
        return new JAXBElement<DeactivateCitizenSubscriptionResponseType>(_DeactivateCitizenSubscriptionResponse_QNAME, DeactivateCitizenSubscriptionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCitizenProfileResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "createCitizenProfileResponse")
    public JAXBElement<CreateCitizenProfileResponseType> createCreateCitizenProfileResponse(CreateCitizenProfileResponseType value) {
        return new JAXBElement<CreateCitizenProfileResponseType>(_CreateCitizenProfileResponse_QNAME, CreateCitizenProfileResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUpCitizenSubscriptionSettingsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "setUpCitizenSubscriptionSettingsRequest")
    public JAXBElement<SetUpCitizenSubscriptionSettingsRequestType> createSetUpCitizenSubscriptionSettingsRequest(SetUpCitizenSubscriptionSettingsRequestType value) {
        return new JAXBElement<SetUpCitizenSubscriptionSettingsRequestType>(_SetUpCitizenSubscriptionSettingsRequest_QNAME, SetUpCitizenSubscriptionSettingsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptionStreamRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getSubscriptionStreamRequest")
    public JAXBElement<GetSubscriptionStreamRequestType> createGetSubscriptionStreamRequest(GetSubscriptionStreamRequestType value) {
        return new JAXBElement<GetSubscriptionStreamRequestType>(_GetSubscriptionStreamRequest_QNAME, GetSubscriptionStreamRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeactivateCitizenSubscriptionRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "deactivateCitizenSubscriptionRequest")
    public JAXBElement<DeactivateCitizenSubscriptionRequestType> createDeactivateCitizenSubscriptionRequest(DeactivateCitizenSubscriptionRequestType value) {
        return new JAXBElement<DeactivateCitizenSubscriptionRequestType>(_DeactivateCitizenSubscriptionRequest_QNAME, DeactivateCitizenSubscriptionRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DropCitizenSubscriptionSettingsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "dropCitizenSubscriptionSettingsRequest")
    public JAXBElement<DropCitizenSubscriptionSettingsRequestType> createDropCitizenSubscriptionSettingsRequest(DropCitizenSubscriptionSettingsRequestType value) {
        return new JAXBElement<DropCitizenSubscriptionSettingsRequestType>(_DropCitizenSubscriptionSettingsRequest_QNAME, DropCitizenSubscriptionSettingsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectStreamTariffsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectStreamTariffsResponse")
    public JAXBElement<SelectStreamTariffsResponseType> createSelectStreamTariffsResponse(SelectStreamTariffsResponseType value) {
        return new JAXBElement<SelectStreamTariffsResponseType>(_SelectStreamTariffsResponse_QNAME, SelectStreamTariffsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectSubscriptionStreamsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectSubscriptionStreamsRequest")
    public JAXBElement<SelectSubscriptionStreamsRequestType> createSelectSubscriptionStreamsRequest(SelectSubscriptionStreamsRequestType value) {
        return new JAXBElement<SelectSubscriptionStreamsRequestType>(_SelectSubscriptionStreamsRequest_QNAME, SelectSubscriptionStreamsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActivateCitizenSubscriptionRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "activateCitizenSubscriptionRequest")
    public JAXBElement<ActivateCitizenSubscriptionRequestType> createActivateCitizenSubscriptionRequest(ActivateCitizenSubscriptionRequestType value) {
        return new JAXBElement<ActivateCitizenSubscriptionRequestType>(_ActivateCitizenSubscriptionRequest_QNAME, ActivateCitizenSubscriptionRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUpCitizenSubscriptionSettingsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "setUpCitizenSubscriptionSettingsResponse")
    public JAXBElement<SetUpCitizenSubscriptionSettingsResponseType> createSetUpCitizenSubscriptionSettingsResponse(SetUpCitizenSubscriptionSettingsResponseType value) {
        return new JAXBElement<SetUpCitizenSubscriptionSettingsResponseType>(_SetUpCitizenSubscriptionSettingsResponse_QNAME, SetUpCitizenSubscriptionSettingsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCitizenProfileResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getCitizenProfileResponse")
    public JAXBElement<GetCitizenProfileResponseType> createGetCitizenProfileResponse(GetCitizenProfileResponseType value) {
        return new JAXBElement<GetCitizenProfileResponseType>(_GetCitizenProfileResponse_QNAME, GetCitizenProfileResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCitizenPaymentsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "selectCitizenPaymentsResponse")
    public JAXBElement<SelectCitizenPaymentsResponseType> createSelectCitizenPaymentsResponse(SelectCitizenPaymentsResponseType value) {
        return new JAXBElement<SelectCitizenPaymentsResponseType>(_SelectCitizenPaymentsResponse_QNAME, SelectCitizenPaymentsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActivateCitizenSubscriptionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "activateCitizenSubscriptionResponse")
    public JAXBElement<ActivateCitizenSubscriptionResponseType> createActivateCitizenSubscriptionResponse(ActivateCitizenSubscriptionResponseType value) {
        return new JAXBElement<ActivateCitizenSubscriptionResponseType>(_ActivateCitizenSubscriptionResponse_QNAME, ActivateCitizenSubscriptionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCitizenProfileRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://subscription.api.emp.altarix.ru", name = "getCitizenProfileRequest")
    public JAXBElement<GetCitizenProfileRequestType> createGetCitizenProfileRequest(GetCitizenProfileRequestType value) {
        return new JAXBElement<GetCitizenProfileRequestType>(_GetCitizenProfileRequest_QNAME, GetCitizenProfileRequestType.class, null, value);
    }

}
