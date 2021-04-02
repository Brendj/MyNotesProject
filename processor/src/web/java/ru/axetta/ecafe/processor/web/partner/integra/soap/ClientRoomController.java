/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents.DataAllEvents;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.org.OrgSummaryResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummaryResult;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.12.11
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */

@WebService
public interface ClientRoomController {

    @WebMethod(operationName = "getActiveMenuQuestions")
    QuestionaryResultList getActiveMenuQuestions(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "currentDate")
          final Date currentDate);

    @WebMethod(operationName = "setAnswerFromQuestion")
    Result setAnswerFromQuestion(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "IdOfAnswer") Long idOfAnswer);

    @WebMethod(operationName = "getGroupListByOrg")
    ClientGroupListResult getGroupListByOrg(@WebParam(name = "idOfOrg") Long idOfOrg);

    @WebMethod(operationName = "getStudentListByIdOfClientGroup")
    ClassStudentListResult getStudentListByIdOfClientGroup(@WebParam(name = "idOfClientGroup") Long idOfClientGroup);

    @WebMethod(operationName = "getSummary")
    ClientSummaryResult getSummary(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getSummaryBySan") ClientSummaryResult getSummary(@WebParam(name = "san") String san);

    @WebMethod(operationName = "getSummaryByTypedId")
    ClientSummaryResult getSummaryByTypedId(@WebParam(name = "id") String id, @WebParam(name = "idType") int idType);

    @WebMethod(operationName = "getPhotoURL")
    PhotoURLResult getPhotoURL(@WebParam(name = "contractId") Long contractId, @WebParam(name = "GUID") String guid,
            @WebParam(name = "size") int size, @WebParam(name = "isNew") boolean isNew);

    @WebMethod(operationName = "uploadPhoto")
    PhotoURLResult uploadPhoto(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianContractId") Long guardianContractId, @WebParam(name = "image") Image photo, @WebParam(name = "size") int size);

    @WebMethod(operationName = "deleteNewPhoto")
    Result deleteNewPhoto(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getPurchaseList")
    PurchaseListResult getPurchaseList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate, @WebParam(name = "mode") Short mode);

    @WebMethod(operationName = "getPurchaseListBySan")
    PurchaseListResult getPurchaseList(@WebParam(name = "san") String san, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate,@WebParam(name = "mode") Short mode);

    @WebMethod(operationName = "getPurchaseSubscriptionFeedingListBySan")
    PurchaseListResult getPurchaseSubscriptionFeedingList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,@WebParam(name = "mode") Short mode);

    @WebMethod(operationName = "getPurchaseListWithDetails")
    PurchaseListWithDetailsResult getPurchaseListWithDetails(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate, @WebParam(name = "mode") Short mode);

    @WebMethod(operationName = "getPaymentList")
    PaymentListResult getPaymentList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getPaymentListBySan")
    PaymentListResult getPaymentList(@WebParam(name = "san") String san, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getPaymenteSubscriptionFeedingListBySan")
    PaymentListResult getPaymentSubscriptionFeedingList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getClientSmsList")
    ClientSmsListResult getClientSmsList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getListOfProducts")
    ListOfProductsResult getListOfProducts(@WebParam(name = "orgId") Long orgId);

    @WebMethod(operationName = "getListOfGoods") ListOfGoodsResult getListOfGoods(@WebParam(name = "orgId") Long orgId);

    @WebMethod(operationName = "getDishProhibitionsList")
    ProhibitionsListResult getDishProhibitionsList(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "setProhibitionOnProduct")
    IdResult setProhibitionOnProduct(@WebParam(name = "orgId") Long orgId,
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "idOfProduct") Long idOfProduct,
          @WebParam(name = "isDeleted") Boolean isDeleted);

    @WebMethod(operationName = "setProhibitionOnProductGroup")
    IdResult setProhibitionOnProductGroup(@WebParam(name = "orgId") Long orgId,
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "idOfProductGroup") Long idOfProductGroup,
          @WebParam(name = "isDeleted") Boolean isDeleted);

    @WebMethod(operationName = "setProhibitionOnGood")
    IdResult setProhibitionOnGood(@WebParam(name = "orgId") Long orgId, @WebParam(name = "contractId") Long contractId,
          @WebParam(name = "idOfGood") Long idOfGood, @WebParam(name = "isDeleted") Boolean isDeleted);

    @WebMethod(operationName = "setProhibitionOnGoodGroup")
    IdResult setProhibitionOnGoodGroup(@WebParam(name = "orgId") Long orgId,
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "idOfGoodGroup") Long idOfGoodGroup,
          @WebParam(name = "isDeleted") Boolean isDeleted);

    @WebMethod(operationName = "excludeGoodFromProhibition")
    IdResult excludeGoodFromProhibition(@WebParam(name = "orgId") Long orgId,
          @WebParam(name = "idOfProhibition") Long idOfProhibition, @WebParam(name = "idOfGood") Long idOfGood);

    @WebMethod(operationName = "excludeGoodGroupFromProhibition")
    IdResult excludeGoodGroupFromProhibition(@WebParam(name = "orgId") Long orgId,
          @WebParam(name = "idOfProhibition") Long idOfProhibition,
          @WebParam(name = "idOfGoodGroup") Long idOfGoodGroup);

    @WebMethod(operationName = "getListOfComplaintBookEntriesByOrg")
    ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByOrg(@WebParam(name = "orgId") Long orgId);

    @WebMethod(operationName = "getListOfComplaintBookEntriesByClient")
    ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByClient(
          @WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "openComplaint") IdResult openComplaint(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "orderOrgId") Long orderOrgId,
          @WebParam(name = "idOfOrderDetail") List<Long> orderDetailIdList,
          @WebParam(name = "causeNumber") List<Integer> causeNumberList,
          @WebParam(name = "description") String description);

    @WebMethod(operationName = "changeComplaintStatusToConsideration")
    Result changeComplaintStatusToConsideration(@WebParam(name = "complaintId") Long complaintId);

    @WebMethod(operationName = "changeComplaintStatusToInvestigation")
    Result changeComplaintStatusToInvestigation(@WebParam(name = "complaintId") Long complaintId);

    @WebMethod(operationName = "giveConclusionOnComplaint")
    Result giveConclusionOnComplaint(@WebParam(name = "complaintId") Long complaintId,
          @WebParam(name = "conclusion") String conclusion);

/*    @WebMethod(operationName = "getMenuList") MenuListResult getMenuList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);*/

    @WebMethod(operationName = "getMenuList")
    MenuListResult getMenuFirstDay(@WebParam(name = "contractId") Long contractId, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getMenuListWithComplexes")
    MenuListWithComplexesResult getMenuListWithComplexes(@WebParam(name = "contractId") Long contractId, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getMenuListBySan")
    MenuListResult getMenuList(@WebParam(name = "san") String san, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getMenuListByOrg")
    MenuListResult getMenuListByOrg(@WebParam(name = "orgId") Long orgId, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getCardList")
    CardListResult getCardList(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getCardListBySan") CardListResult getCardList(@WebParam(name = "san") String san);

    @WebMethod(operationName = "getEnterEventList")
    DataAllEvents getEnterEventList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getNEnterEventList")
    EnterEventListResult getNEnterEventList(@WebParam(name = "orgId") long orgId,@WebParam(name = "minDate") Date minDate,
            @WebParam(name = "maxDate") Date maxDate, @WebParam(name = "N") int n);

    @WebMethod(operationName = "getEnterEventWithRepList")
    EnterEventWithRepListResult getEnterEventWithRepList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getEnterEventListBySan")
    EnterEventListResult getEnterEventList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getEnterEventListByGuardian")
    EnterEventListResult getEnterEventListByGuardian(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod Long getContractIdByCardNo(@WebParam(name = "cardId") String cardId, @WebParam(name = "mode") int mode);

    @WebMethod
    ClientSummaryExtListResult getSummaryByGuardMobile(@WebParam(name = "guardMobile") String guardMobile);

    @WebMethod
    ClientRepresentativesResult getClientRepresentatives(@WebParam(name = "contractId") String contractId);

    @WebMethod Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state);

    Result enableNotificationByPUSH(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state);

    @WebMethod Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state);

    @WebMethod Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "mobilePhone") String mobilePhone, @WebParam(name = "dateConfirm") Date dateConfirm);

    @WebMethod
    Result changeEmail(@WebParam(name = "contractId") Long contractId, @WebParam(name = "email") String email);

    @WebMethod
    Result changeExpenditureLimit(@WebParam(name = "contractId") Long contractId, @WebParam(name = "roleRepresentative") Long roleRepresentative,  @WebParam(name = "limit") long limit);

    @WebMethod
    Result changeThresholdBalanceNotify(@WebParam(name = "contractId") Long contractId, @WebParam(name = "threshold") long threshold);

    @WebMethod Result changePassword(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "base64passwordHash") String base64passwordHash);

    @WebMethod PublicationListResult getPublicationListSimple(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "searchCondition") String searchCondition, @WebParam(name="limit") int limit,
            @WebParam(name="offset") int offset);

    @WebMethod PublicationListResult getPublicationListAdvanced(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "author") String author, @WebParam(name = "title") String title,
            @WebParam(name = "title2") String title2, @WebParam(name = "publicationDate") String publicationDate,
            @WebParam(name = "publisher") String publisher, @WebParam(name = "isbn") String isbn,
            @WebParam(name="limit") int limit, @WebParam(name="offset") int offset);

    @WebMethod OrderPublicationResult orderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "publicationId") Long publicationId, @WebParam(name = "orgHolderId") Long orgHolderId);

    @WebMethod OrderPublicationListResult getOrderPublicationList(@WebParam(name = "contractId") Long contractId);

    @WebMethod OrderPublicationDeleteResult deleteOrderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "orderId") Long orderId);

    @WebMethod CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") int state);

    @WebMethod Result clearMobileByContractId(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "phone") String phone);

    @WebMethod Result setGuardianshipDisabled(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardMobile") String guardMobile,
            @WebParam(name = "value") Boolean value,
            @WebParam(name = "roleRepresentativePrincipal") Integer roleRepresentativePrincipal);

    @WebMethod EnterEventStatusListResult getEnterEventStatusListByGUID(@WebParam(name = "guid") List<String> guids);

    @WebMethod ClassRegisterEventListByGUIDResult putClassRegisterEventListByGUID(@WebParam(name = "registerEventList")
          ClassRegisterEventListByGUID registerEventList);

    @WebMethod
    Result authorizeClient(@WebParam(name = "contractId") Long contractId, @WebParam(name = "token") String token);

    @WebMethod ActivateLinkingTokenResult activateLinkingToken(@WebParam(name = "linkingToken") String linkingToken);

    @WebMethod Result sendLinkingTokenByContractId(@WebParam(name = "contractId") Long contractId);

    @WebMethod Result sendLinkingTokenByMobile(@WebParam(name = "mobilePhone") String mobilePhone);

    @WebMethod GenerateLinkingTokenResult generateLinkingToken(@WebParam(name = "contractId") Long contractId);

    @WebMethod SendResult sendPasswordRecoverURLFromEmail(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "request") RequestWebParam request);

    @WebMethod SendResult sendPasswordRecoverBySms(@WebParam(name = "contractId") Long contractId);

    @WebMethod CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name = "request") RequestWebParam request);

    @WebMethod IdResult getIdOfClient(@WebParam(name = "contractId") Long contractId);

    @WebMethod IdResult getIdOfContragent(@WebParam(name = "contragentName") String contragentName);

    @WebMethod IdResult createPaymentOrder(@WebParam(name = "idOfClient") Long idOfClient,
          @WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "paymentMethod") int paymentMethod,
          @WebParam(name = "copecksAmount") Long copecksAmount, @WebParam(name = "contragentSum") Long contragentSum);

    @WebMethod Result changePaymentOrderStatus(@WebParam(name = "idOfClient") Long idOfClient,
          @WebParam(name = "idOfClientPaymentOrder") Long idOfClientPaymentOrder,
          @WebParam(name = "orderStatus") int orderStatus);

    @WebMethod RBKMoneyConfigResult getRBKMoneyConfig();

    @WebMethod ChronopayConfigResult getChronopayConfig();

    @WebMethod BanksData getBanks();

    @WebMethod(operationName = "changePersonalInfo")
    Result changePersonalInfo(@WebParam(name = "contractId") Long contractId, @WebParam(name = "limit") Long limit,
          @WebParam(name = "address") String address, @WebParam(name = "phone") String phone,
          @WebParam(name = "mobilePhone") String mobilePhone, @WebParam(name = "email") String email,
          @WebParam(name = "smsNotificationState") boolean smsNotificationState);


    @WebMethod(operationName = "getHiddenPages") HiddenPagesResult getHiddenPages();

    @WebMethod(operationName = "getComplexList")
    ComplexListResult getComplexList(@WebParam(name = "contractId") Long contractId, final Date startDate,
          final Date endDate);

    @WebMethod(operationName = "getNotificationSettings")
    ClientNotificationSettingsResult getClientNotificationSettings(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getNotificationSettingsByClientGuardian")
    ClientNotificationSettingsResult getClientGuardianNotificationSettings(@WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile);

    @WebMethod(operationName = "setNotificationSettings")
    ClientNotificationChangeResult setClientNotificationSettings(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "notificationType") List<Long> notificationTypes);

    @WebMethod(operationName = "setNotificationSettingsByClientGuardian")
    ClientNotificationChangeResult setClientGuardianNotificationSettings(@WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "notificationType") List<Long> notificationTypes);

    @WebMethod(operationName = "getNotificationTypes") ClientNotificationSettingsResult getClientNotificationTypes();

    @WebMethod(operationName = "getStudentsByCanNotConfirmPayment")
    ClientConfirmPaymentData getStudentsByCanNotConfirmPayment(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getClientStats")
    ClientStatsResult getClientStats(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") int type);

    @WebMethod(operationName = "transferBalance")
    Result transferBalance(@WebParam(name = "contractId") Long contractId, @WebParam(name = "fromSub") Integer fromSub,
          @WebParam(name = "toSub") Integer toSub, @WebParam(name = "amount") Long amount);

    @WebMethod(operationName = "transferBalanceBySan")
    Result transferBalance(@WebParam(name = "san") String san, @WebParam(name = "fromSub") Integer fromSub,
          @WebParam(name = "toSub") Integer toSub, @WebParam(name = "amount") Long amount);

    @WebMethod(operationName = "findComplexesWithSubFeeding")
    ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "findComplexesWithSubFeedingBySan")
    ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "getTransferSubBalanceList")
    TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getTransferSubBalanceListBySan")
    TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getSubscriptionFeedingSetting")
    SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getSubscriptionFeedingSettingBySan")
    SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "san") String san);

/*   // @WebMethod(operationName = "getSubscriptionFeedingHistoryList")
    SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);*/

   // @WebMethod(operationName = "getSubscriptionFeedingHistoryListBySan")
/*    SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);*/

    @WebMethod(operationName = "getSubscriptionFeedingJournal")
    SubscriptionFeedingJournalResult getSubscriptionFeedingJournal(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getCurrentSubscriptionFeeding")
    SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "getCurrentSubscriptionFeedingBySan")
    SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type);

    /*@WebMethod(operationName = "activateSubscriptionFeeding")
    Result activateSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram);*/

    /*@WebMethod(operationName = "activateSubscriptionFeedingBySan")
    Result activateSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram);*/

    @WebMethod(operationName = "activateCurrentSubscriptionFeeding")
    Result activateCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "dateActivateSubscription") Date dateActivateSubscription);

    @WebMethod(operationName = "suspendSubscriptionFeeding")
    Result suspendSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "suspendDate") Date suspendDate);

    @WebMethod(operationName = "suspendSubscriptionFeedingBySan")
    Result suspendSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "suspendDate") Date suspendDate);

    @WebMethod(operationName = "reopenSubscriptionFeeding")
    Result reopenSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "activateDate") Date activateDate);

    @WebMethod(operationName = "reopenSubscriptionFeedingBySan")
    Result reopenSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "activateDate") Date activateDate);

    @WebMethod(operationName = "cancelSubscriptionFeeding")
    Result cancelSubscriptionFeeding(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "cancelSubscriptionFeedingBySan")
    Result cancelSubscriptionFeeding(@WebParam(name = "san") String san);

    @WebMethod(operationName = "getCycleDiagramList")
    CycleDiagramList getCycleDiagramList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "getCycleDiagramListBySan")
    CycleDiagramList getCycleDiagramList(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "getCycleDiagramHistoryList")
    CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "getCycleDiagramHistoryListBySan")
    CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") Integer type);

    @WebMethod(operationName = "putCycleDiagram")
    Result putCycleDiagram(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram,
          @WebParam(name = "type") Integer type, @WebParam(name = "startWeekPosition") Integer startWeekPosition);

    @WebMethod(operationName = "putCycleDiagramBySan")
    Result putCycleDiagram(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram,
          @WebParam(name = "type") Integer type, @WebParam(name = "startWeekPosition") Integer startWeekPosition);

    @WebMethod(operationName = "getMenuListWithProhibitions")
    MenuListWithProhibitionsResult getMenuListWithProhibitions(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "addProhibition")
    ProhibitionsResult addProhibition(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "filterText") String filterText, @WebParam(name = "filterType") Integer filterType);

    @WebMethod(operationName = "removeProhibition")
    ProhibitionsResult removeProhibition(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "prohibitionId") Long prohibitionId);

    @WebMethod(operationName = "getOrgSummary")
    OrgSummaryResult getOrgSummary(@WebParam(name = "orgId") Long orgId);


    @WebMethod(operationName = "getVisitorsSummary")
    VisitorsSummaryResult getVisitorsSummary ();

    @WebMethod(operationName = "getVisitorsSummaryByDate")
    public VisitorsSummaryResult getVisitorsSummaryByDate(@WebParam(name = "dateTime")Long datetime);

    @WebMethod ClientGuidResult getClientGuidByContractId(@WebParam(name = "contractId") Long contractId);

    @WebMethod ClientContractIdResult getContractIdByGUID(@WebParam(name = "GUID") String guid);

    @WebMethod
    Result addGuardian(@WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "mobile") String mobile, @WebParam(name = "gender") Integer gender,
            @WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "creatorMobile") String creatorMobile,
            @WebParam(name = "passportNumber") String passportNumber,
            @WebParam(name = "passportSeries") String passportSeries,
            @WebParam(name = "typeCard") Integer typeCard,
            @WebParam(name = "roleRepresentative") Integer roleRepresentative,
            @WebParam(name = "roleRepresentativePrincipal") Integer roleRepresentativePrincipal,
            @WebParam(name = "degree") Long relation);

    /*@WebMethod Result changeGuardian(@WebParam(name = "contractId") Long contractId, @WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "gender") Integer gender, @WebParam(name = "contracts") ListOfContracts contracts);*/

    @WebMethod Result removeGuardian(@WebParam(name = "guardianContractId") Long contractId,
            @WebParam(name = "childContractId") Long childContractId);

    @WebMethod MuseumEnterInfo getMuseumEnterInfo(@WebParam(name = "cardId") String cardId);

    @WebMethod CultureEnterInfo getCultureEnterInfo(@WebParam(name = "cardId") String cardId);

    @WebMethod Result enterMuseum(@WebParam(name = "guid") String guid, @WebParam(name = "museumCode") String museumCode,
            @WebParam(name = "museumName") String museumName, @WebParam(name = "accessTime") Date accessTime,
            @WebParam(name = "ticketStatus") Integer ticketStatus);

    @WebMethod Result enterCulture(@XmlElement(required=true)@WebParam(name = "guid") String guid,
            @XmlElement(required=true)@WebParam(name = "orgCode") String orgCode,
            @XmlElement(required=true)@WebParam(name = "CultureName") String CultureName,
            @XmlElement(required=true)@WebParam(name = "CultureShortName") String CultureShortName,
            @XmlElement(required=true)@WebParam(name = "CultureAddress") String CultureAddress,
            @XmlElement(required=true)@WebParam(name = "accessTime") Date accessTime,
            @XmlElement(required=true)@WebParam(name = "eventsStatus") Long eventsStatus);

    @WebMethod ClientSummaryBaseListResult getSummaryByGuardMobileMin(@WebParam(name = "guardMobile") String guardMobile);

    @WebMethod ClientSummaryBaseListResult getSummaryByChildMobileMin(@WebParam(name = "childMobile") String guardMobile);

    @WebMethod ClientSummaryBaseListResult getSummaryByStaffMobileMin(@WebParam(name = "staffMobile") String guardMobile);

    @WebMethod TransactionInfoListResult getOrderTransactions();

    @WebMethod(operationName = "getGuardiansFromDate")
    GuardianInfoListResult getGuardiansFromDate(@WebParam(name="dateTime")Long dateTime);

    @WebMethod(operationName = "getPreorderClientSummary")
    PreorderClientSummaryResult getPreorderClientSummary(@WebParam(name="contractId") Long contractId, @WebParam(name="guardianMobile") String guardianMobile);

    @WebMethod(operationName = "getPreorderClientSummaryOnDate")
    PreorderClientSummaryResult getPreorderClientSummaryOnDate(@WebParam(name="contractId") Long contractId, @WebParam(name="guardianMobile") String guardianMobile,
            @WebParam(name="date") Date date);

    @WebMethod(operationName = "setInformedSpecialMenuForClient")
    Result setInformedSpecialMenuForClient(@WebParam(name="contractId") Long contractId);

    @WebMethod(operationName = "setInformedSpecialMenu")
    Result setInformedSpecialMenu(@WebParam(name="contractId") Long contractId, @WebParam(name="guardianMobile") String guardianMobile);

    @WebMethod(operationName = "setPreorderAllowed")
    Result setPreorderAllowed(@WebParam(name="contractId") Long contractId, @WebParam(name="guardianMobile") String guardianMobile,
            @WebParam(name="childMobile") String childMobile, @WebParam(name="value") Boolean value);

    @WebMethod(operationName = "setSpecialMenu")
    Result setSpecialMenu(@WebParam(name = "contractId") Long contractId, @WebParam(name = "value") Boolean value);

    @WebMethod(operationName = "getTypeClients")
    ClientGroupResult getTypeClients(@WebParam(name="mobile") String mobile);

    @WebMethod(operationName = "getPreorderComplexes")
    PreorderComplexesResult getPreorderComplexes(@WebParam(name = "contractId") Long contractId, @WebParam(name = "date") Date date);

    @WebMethod(operationName = "getPreorderAllComplexes")
    PreorderAllComplexesResult getPreorderAllComplexes(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getPeopleQuantityByOrg")
    public PeopleQuantityInOrgResult getPeopleQuantityByOrg(@WebParam(name = "organizationUid") String ogrn);

    @WebMethod()
    Result putPreorderComplex(@WebParam(name = "preorders") PreorderParam preorders, @WebParam(name="guardianMobile") String guardianMobile);

    @WebMethod(operationName = "addRegistrationCard")
    AddRegistrationCardResult addRegistrationCard(@WebParam(name = "regid") String regid, @WebParam(name = "suid") String suid,
            @WebParam(name = "organizationSuid") String organizationSuid, @WebParam(name = "cardId") String cardId,
            @WebParam(name = "validdate") Date validdate, @WebParam(name = "firstName") String firstName,
            @WebParam(name = "surname") String surname, @WebParam(name = "secondName") String secondName,
            @WebParam(name = "birthDate") Date birthDate, @WebParam(name = "grade") String grade,
            @WebParam(name = "codeBenefit") String codeBenefit, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate, @WebParam(name = "lsnum") String lsnum);

    @WebMethod(operationName = "setMultiCardModeForClient")
    Result setMultiCardModeForClient(@WebParam(name = "contractId") String contractId, @WebParam(name = "value") String value);

    @WebMethod(operationName = "getClientCardInfo")
    CardInfo getClientCardInfo(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "addRequestForCashOut")
    CashOutResult addRequestForCashOut(@WebParam(name = "contractId") Long contractId, @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "sum") Long sum, @WebParam(name = "guardianDataForCashOut") GuardianDataForCashOut guardianDataForCashOut);

    @WebMethod(operationName = "getRequestForCashOutList")
    RequestForCashOutList getRequestForCashOutList(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "removeRequestForCashOut")
    Result removeRequestForCashOut(@WebParam(name = "contractId") Long contractId, @WebParam(name = "idOfRequest") Long idOfRequest);
    @WebMethod(operationName = "checkApplicationForFood")
    CheckApplicationForFoodResult checkApplicationForFood(@WebParam(name = "clientGuid") String clientGuid,
            @WebParam(name = "meshGuid") String meshGuid);

    @WebMethod(operationName = "registerApplicationForFood")
    Result registerApplicationForFood(@WebParam(name = "clientGuid") String clientGuid, @WebParam(name = "categoryDiscount") Long categoryDiscount,
            @WebParam(name = "otherDiscount") Boolean otherDiscount, @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "guardianName") String guardianName, @WebParam(name = "guardianSurname") String guardianSurname,
            @WebParam(name = "guardianSecondName") String guardianSecondName, @WebParam(name = "serviceNumber") String serviceNumber);

    @WebMethod(operationName = "updateStatusOfApplicationForFood")
    Result updateStatusOfApplicationForFood(@WebParam(name = "state") Integer state, @WebParam(name = "declineReason") Integer declineReason,
            @WebParam(name = "serviceNumber") String serviceNumber);

    @WebMethod(operationName = "getContragentForClient")
    ContragentData getContragentForClient(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getETPDiscounts")
    ETPDiscountsResult getETPDiscounts();

    @WebMethod(operationName = "blockActiveCardByCardNoAndContractId")
    Result blockActiveCardByCardNoAndContractId(@WebParam(name = "contractId") Long contractId, @WebParam(name = "cardNo") Long cardNo);

    @WebMethod(operationName = "extendValidDateOfСard")
    Result extendValidDateOfCard(@WebParam(name = "contractId") Long contractId, @WebParam(name = "UID") Long cardNo);
}
