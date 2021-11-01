
package generated.emp_events;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SubscriptionPortType", targetNamespace = "urn://emp.altarix.ru/subscriptions")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SubscriptionPortType {


    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.GetCitizenProfileResponseType
     */
    @WebMethod(action = "urn:#getCitizenProfile")
    @WebResult(name = "getCitizenProfileResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public GetCitizenProfileResponseType getCitizenProfile(
        @WebParam(name = "getCitizenProfileRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        GetCitizenProfileRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SetCitizenProfileResponseType
     */
    @WebMethod(action = "urn:#setCitizenProfile")
    @WebResult(name = "setCitizenProfileResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SetCitizenProfileResponseType setCitizenProfile(
        @WebParam(name = "setCitizenProfileRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SetCitizenProfileRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.GetSubscriptionStreamCategoryResponseType
     */
    @WebMethod(action = "urn:#getSubscriptionStreamCategory")
    @WebResult(name = "getSubscriptionStreamCategoryResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public GetSubscriptionStreamCategoryResponseType getSubscriptionStreamCategory(
        @WebParam(name = "getSubscriptionStreamCategoryRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        GetSubscriptionStreamCategoryRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SelectSubscriptionStreamCategoriesResponseType
     */
    @WebMethod(action = "urn:#selectSubscriptionStreamCategories")
    @WebResult(name = "selectSubscriptionStreamCategoriesResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SelectSubscriptionStreamCategoriesResponseType selectSubscriptionStreamCategories(
        @WebParam(name = "selectSubscriptionStreamCategoriesRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SelectSubscriptionStreamCategoriesRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.GetSubscriptionStreamResponseType
     */
    @WebMethod(action = "urn:#getSubscriptionStream")
    @WebResult(name = "getSubscriptionStreamResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public GetSubscriptionStreamResponseType getSubscriptionStream(
        @WebParam(name = "getSubscriptionStreamRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        GetSubscriptionStreamRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SelectSubscriptionStreamsResponseType
     */
    @WebMethod(action = "urn:#selectSubscriptionStreams")
    @WebResult(name = "selectSubscriptionStreamsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SelectSubscriptionStreamsResponseType selectSubscriptionStreams(
        @WebParam(name = "selectSubscriptionStreamsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SelectSubscriptionStreamsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.ActivateCitizenSubscriptionResponseType
     */
    @WebMethod(action = "urn:#activateCitizenSubscription")
    @WebResult(name = "activateCitizenSubscriptionResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public ActivateCitizenSubscriptionResponseType activateCitizenSubscription(
        @WebParam(name = "activateCitizenSubscriptionRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        ActivateCitizenSubscriptionRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.DeactivateCitizenSubscriptionResponseType
     */
    @WebMethod(action = "urn:#deactivateCitizenSubscription")
    @WebResult(name = "deactivateCitizenSubscriptionResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public DeactivateCitizenSubscriptionResponseType deactivateCitizenSubscription(
        @WebParam(name = "deactivateCitizenSubscriptionRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        DeactivateCitizenSubscriptionRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SetUpCitizenSubscriptionSettingsResponseType
     */
    @WebMethod(action = "urn:#setUpCitizenSubscriptionSettings")
    @WebResult(name = "setUpCitizenSubscriptionSettingsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SetUpCitizenSubscriptionSettingsResponseType setUpCitizenSubscriptionSettings(
        @WebParam(name = "setUpCitizenSubscriptionSettingsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SetUpCitizenSubscriptionSettingsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.DropCitizenSubscriptionSettingsResponseType
     */
    @WebMethod(action = "urn:#dropCitizenSubscriptionSettings")
    @WebResult(name = "dropCitizenSubscriptionSettingsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public DropCitizenSubscriptionSettingsResponseType dropCitizenSubscriptionSettings(
        @WebParam(name = "dropCitizenSubscriptionSettingsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        DropCitizenSubscriptionSettingsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SelectCitizenSubscriptionsResponseType
     */
    @WebMethod(action = "urn:#selectCitizenSubscriptions")
    @WebResult(name = "selectCitizenSubscriptionsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SelectCitizenSubscriptionsResponseType selectCitizenSubscriptions(
        @WebParam(name = "selectCitizenSubscriptionsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SelectCitizenSubscriptionsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SendSubscriptionStreamEventsResponseType
     */
    @WebMethod(action = "urn:#sendSubscriptionStreamEvents")
    @WebResult(name = "sendSubscriptionStreamEventsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SendSubscriptionStreamEventsResponseType sendSubscriptionStreamEvents(
        @WebParam(name = "sendSubscriptionStreamEventsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SendSubscriptionStreamEventsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.DeleteCitizenProfileOptionsResponseType
     */
    @WebMethod(action = "urn:#deleteCitizenProfileOptions")
    @WebResult(name = "deleteCitizenProfileOptionsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public DeleteCitizenProfileOptionsResponseType deleteCitizenProfileOptions(
        @WebParam(name = "deleteCitizenProfileOptionsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        DeleteCitizenProfileOptionsRequestType parameter);

    /**
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.CreateCitizenProfileResponseType
     */
    @WebMethod(action = "urn:#createCitizenProfile")
    @WebResult(name = "createCitizenProfileResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public CreateCitizenProfileResponseType createCitizenProfile(
        @WebParam(name = "createCitizenProfileRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        CreateCitizenProfileRequestType parameter);

    /**
     * ����� ��������� ������ ������� ��� ������ ���������������� ������� � ��������
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SelectStreamTariffsResponseType
     */
    @WebMethod(action = "urn:#selectStreamTariffs")
    @WebResult(name = "selectStreamTariffsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SelectStreamTariffsResponseType selectStreamTariffs(
        @WebParam(name = "selectStreamTariffsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SelectStreamTariffsRequestType parameter);

    /**
     * ��������� ������ ����� ���������������� ������� ��������
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.SelectCitizenPaymentsResponseType
     */
    @WebMethod(action = "urn:#selectCitizenPayments")
    @WebResult(name = "selectCitizenPaymentsResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public SelectCitizenPaymentsResponseType selectCitizenPayments(
        @WebParam(name = "selectCitizenPaymentsRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        SelectCitizenPaymentsRequestType parameter);

    /**
     * ��������� �������� ����������� �������������� ������� ��� ���������� �������
     * 
     * @param parameter
     * @return
     *     returns generated.emp_events.GetStreamDictValueResponseType
     */
    @WebMethod(action = "urn:#getStreamDictValue")
    @WebResult(name = "getStreamDictValueResponse", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
    public GetStreamDictValueResponseType getStreamDictValue(
        @WebParam(name = "getStreamDictValueRequest", targetNamespace = "urn://subscription.api.emp.altarix.ru", partName = "parameter")
        GetStreamDictValueRequestType parameter);

}
