
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.util.List;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ClientRoomController", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ClientRoomController {


    /**
     * 
     * @param mobilePhone
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "changeMobilePhone", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeMobilePhone")
    @ResponseWrapper(localName = "changeMobilePhoneResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeMobilePhoneResponse")
    public Result changeMobilePhone(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "mobilePhone", targetNamespace = "")
        String mobilePhone);

    /**
     * 
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClientSummaryResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getSummary", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummary")
    @ResponseWrapper(localName = "getSummaryResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryResponse")
    public ClientSummaryResult getSummary(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId);

    /**
     * 
     * @param san
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.CardListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCardListBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCardListBySan")
    @ResponseWrapper(localName = "getCardListBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCardListBySanResponse")
    public CardListResult getCardListBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san);

    /**
     * 
     * @param startDate
     * @param endDate
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.PaymentListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPaymentList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPaymentList")
    @ResponseWrapper(localName = "getPaymentListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPaymentListResponse")
    public PaymentListResult getPaymentList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.CardListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCardList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCardList")
    @ResponseWrapper(localName = "getCardListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCardListResponse")
    public CardListResult getCardList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId);

    /**
     * 
     * @param email
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "changeEmail", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeEmail")
    @ResponseWrapper(localName = "changeEmailResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeEmailResponse")
    public Result changeEmail(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "email", targetNamespace = "")
        String email);

    /**
     * 
     * @param state
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "enableNotificationBySMS", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnableNotificationBySMS")
    @ResponseWrapper(localName = "enableNotificationBySMSResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnableNotificationBySMSResponse")
    public Result enableNotificationBySMS(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "state", targetNamespace = "")
        boolean state);

    /**
     * 
     * @param state
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "enableNotificationByEmail", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnableNotificationByEmail")
    @ResponseWrapper(localName = "enableNotificationByEmailResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnableNotificationByEmailResponse")
    public Result enableNotificationByEmail(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "state", targetNamespace = "")
        boolean state);

    /**
     * 
     * @param startDate
     * @param san
     * @param endDate
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.PaymentListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPaymentListBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPaymentListBySan")
    @ResponseWrapper(localName = "getPaymentListBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPaymentListBySanResponse")
    public PaymentListResult getPaymentListBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param idOfClientGroup
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClassStudentListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getStudentListByIdOfClientGroup", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetStudentListByIdOfClientGroup")
    @ResponseWrapper(localName = "getStudentListByIdOfClientGroupResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetStudentListByIdOfClientGroupResponse")
    public ClassStudentListResult getStudentListByIdOfClientGroup(
        @WebParam(name = "idOfClientGroup", targetNamespace = "")
        Long idOfClientGroup);

    /**
     * 
     * @param idOfOrg
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClientGroupListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getGroupListByOrg", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetGroupListByOrg")
    @ResponseWrapper(localName = "getGroupListByOrgResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetGroupListByOrgResponse")
    public ClientGroupListResult getGroupListByOrg(
        @WebParam(name = "idOfOrg", targetNamespace = "")
        Long idOfOrg);

    /**
     * 
     * @param startDate
     * @param endDate
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnterEventListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getEnterEventList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetEnterEventList")
    @ResponseWrapper(localName = "getEnterEventListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetEnterEventListResponse")
    public EnterEventListResult getEnterEventList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param san
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClientSummaryResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getSummaryBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryBySan")
    @ResponseWrapper(localName = "getSummaryBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryBySanResponse")
    public ClientSummaryResult getSummaryBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san);

    /**
     * 
     * @param startDate
     * @param san
     * @param endDate
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.MenuListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMenuListBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuListBySan")
    @ResponseWrapper(localName = "getMenuListBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuListBySanResponse")
    public MenuListResult getMenuListBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param cardId
     * @return
     *     returns java.lang.Long
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getContractIdByCardNo", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetContractIdByCardNo")
    @ResponseWrapper(localName = "getContractIdByCardNoResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetContractIdByCardNoResponse")
    public Long getContractIdByCardNo(
        @WebParam(name = "cardId", targetNamespace = "")
        String cardId);

    /**
     * 
     * @param id
     * @param idType
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClientSummaryResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getSummaryByTypedId", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryByTypedId")
    @ResponseWrapper(localName = "getSummaryByTypedIdResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryByTypedIdResponse")
    public ClientSummaryResult getSummaryByTypedId(
        @WebParam(name = "id", targetNamespace = "")
        String id,
        @WebParam(name = "idType", targetNamespace = "")
        int idType);

    /**
     *
     * @param guardMobile
     * @return
     *     returns java.util.List<ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ClientSummaryExt>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getSummaryByGuardMobile", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryByGuardMobile")
    @ResponseWrapper(localName = "getSummaryByGuardMobileResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetSummaryByGuardMobileResponse")
    public List<ClientSummaryExt> getSummaryByGuardMobile(
            @WebParam(name = "guardMobile", targetNamespace = "")
            String guardMobile);

    /**
     * 
     * @param startDate
     * @param endDate
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.MenuListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMenuList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuList")
    @ResponseWrapper(localName = "getMenuListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuListResponse")
    public MenuListResult getMenuList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param startDate
     * @param san
     * @param endDate
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.PurchaseListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPurchaseListBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPurchaseListBySan")
    @ResponseWrapper(localName = "getPurchaseListBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPurchaseListBySanResponse")
    public PurchaseListResult getPurchaseListBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param token
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "authorizeClient", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.AuthorizeClient")
    @ResponseWrapper(localName = "authorizeClientResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.AuthorizeClientResponse")
    public Result authorizeClient(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "token", targetNamespace = "")
        String token);

    /**
     * 
     * @param state
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.CirculationListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCirculationList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCirculationList")
    @ResponseWrapper(localName = "getCirculationListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetCirculationListResponse")
    public CirculationListResult getCirculationList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "state", targetNamespace = "")
        int state);

    /**
     * 
     * @param limit
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.Result
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "changeExpenditureLimit", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeExpenditureLimit")
    @ResponseWrapper(localName = "changeExpenditureLimitResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.ChangeExpenditureLimitResponse")
    public Result changeExpenditureLimit(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "limit", targetNamespace = "")
        long limit);

    /**
     * 
     * @param startDate
     * @param endDate
     * @param contractId
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.PurchaseListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPurchaseList", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPurchaseList")
    @ResponseWrapper(localName = "getPurchaseListResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetPurchaseListResponse")
    public PurchaseListResult getPurchaseList(
        @WebParam(name = "contractId", targetNamespace = "")
        Long contractId,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param orgId
     * @param startDate
     * @param endDate
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.MenuListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMenuListByOrg", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuListByOrg")
    @ResponseWrapper(localName = "getMenuListByOrgResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetMenuListByOrgResponse")
    public MenuListResult getMenuListByOrg(
        @WebParam(name = "orgId", targetNamespace = "")
        Long orgId,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

    /**
     * 
     * @param startDate
     * @param san
     * @param endDate
     * @return
     *     returns ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.EnterEventListResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getEnterEventListBySan", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetEnterEventListBySan")
    @ResponseWrapper(localName = "getEnterEventListBySanResponse", targetNamespace = "http://soap.integra.partner.web.processor.ecafe.axetta.ru/", className = "ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.GetEnterEventListBySanResponse")
    public EnterEventListResult getEnterEventListBySan(
        @WebParam(name = "san", targetNamespace = "")
        String san,
        @WebParam(name = "startDate", targetNamespace = "")
        XMLGregorianCalendar startDate,
        @WebParam(name = "endDate", targetNamespace = "")
        XMLGregorianCalendar endDate);

}
