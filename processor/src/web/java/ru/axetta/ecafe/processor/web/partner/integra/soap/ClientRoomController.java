/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
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
    QuestionaryResultList getActiveMenuQuestions(@WebParam(name="contractId") Long contractId, @WebParam(name = "currentDate") final Date currentDate);

    @WebMethod(operationName = "setAnswerFromQuestion")
    Result setAnswerFromQuestion(@WebParam(name="contractId") Long contractId, @WebParam(name="IdOfAnswer") Long idOfAnswer);

    @WebMethod (operationName = "getGroupListByOrg")
    ClientGroupListResult getGroupListByOrg(@WebParam(name="idOfOrg") Long idOfOrg);

    @WebMethod (operationName = "getStudentListByIdOfClientGroup")
    ClassStudentListResult getStudentListByIdOfClientGroup(@WebParam(name="idOfClientGroup") Long idOfClientGroup);

    @WebMethod (operationName = "getSummary")
    ClientSummaryResult getSummary(@WebParam(name="contractId") Long contractId);
    @WebMethod (operationName = "getSummaryBySan")
    ClientSummaryResult getSummary(@WebParam(name="san") String san);
    @WebMethod (operationName = "getSummaryByTypedId")
    ClientSummaryResult getSummaryByTypedId(@WebParam(name="id")String id, @WebParam(name="idType")int idType);

    @WebMethod (operationName = "getPurchaseList")
    PurchaseListResult getPurchaseList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPurchaseListBySan")
    PurchaseListResult getPurchaseList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPurchaseSubscriptionFeedingListBySan")
    PurchaseListResult getPurchaseSubscriptionFeedingList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPaymentList")
    PaymentListResult getPaymentList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPaymentListBySan")
    PaymentListResult getPaymentList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPaymenteSubscriptionFeedingListBySan")
    PaymentListResult getPaymentSubscriptionFeedingList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);

    @WebMethod(operationName="getClientSmsList")
    ClientSmsListResult getClientSmsList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate")Date endDate);

    @WebMethod(operationName = "getListOfProducts")
    ListOfProductsResult getListOfProducts(@WebParam(name="orgId") Long orgId);
    @WebMethod(operationName = "getListOfGoods")
    ListOfGoodsResult getListOfGoods(@WebParam(name="orgId") Long orgId);

    @WebMethod(operationName = "getDishProhibitionsList")
    ProhibitionsListResult getDishProhibitionsList(@WebParam(name="contractId") Long contractId);
    @WebMethod(operationName = "setProhibitionOnProduct")
    IdResult setProhibitionOnProduct(@WebParam(name="orgId") Long orgId, @WebParam(name="contractId") Long contractId, @WebParam(name="idOfProduct") Long idOfProduct, @WebParam(name="isDeleted") Boolean isDeleted);
    @WebMethod(operationName = "setProhibitionOnProductGroup")
    IdResult setProhibitionOnProductGroup(@WebParam(name="orgId") Long orgId, @WebParam(name="contractId") Long contractId, @WebParam(name="idOfProductGroup") Long idOfProductGroup, @WebParam(name="isDeleted") Boolean isDeleted);
    @WebMethod(operationName = "setProhibitionOnGood")
    IdResult setProhibitionOnGood(@WebParam(name="orgId") Long orgId, @WebParam(name="contractId") Long contractId, @WebParam(name="idOfGood") Long idOfGood, @WebParam(name="isDeleted") Boolean isDeleted);
    @WebMethod(operationName = "setProhibitionOnGoodGroup")
    IdResult setProhibitionOnGoodGroup(@WebParam(name="orgId") Long orgId, @WebParam(name="contractId") Long contractId, @WebParam(name="idOfGoodGroup") Long idOfGoodGroup, @WebParam(name="isDeleted") Boolean isDeleted);
    @WebMethod(operationName = "excludeGoodFromProhibition")
    IdResult excludeGoodFromProhibition(@WebParam(name="orgId") Long orgId, @WebParam(name="idOfProhibition") Long idOfProhibition, @WebParam(name="idOfGood") Long idOfGood);
    @WebMethod(operationName = "excludeGoodGroupFromProhibition")
    IdResult excludeGoodGroupFromProhibition(@WebParam(name="orgId") Long orgId, @WebParam(name="idOfProhibition") Long idOfProhibition, @WebParam(name="idOfGoodGroup") Long idOfGoodGroup);

    @WebMethod(operationName = "getListOfComplaintBookEntriesByOrg")
    ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByOrg(@WebParam(name="orgId") Long orgId);
    @WebMethod(operationName = "getListOfComplaintBookEntriesByClient")
    ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByClient(@WebParam(name="contractId") Long contractId);
    @WebMethod(operationName = "openComplaint")
    IdResult openComplaint(@WebParam(name="contractId") Long contractId, @WebParam(name="orderOrgId") Long orderOrgId, @WebParam(name="idOfOrderDetail") List<Long> orderDetailIdList, @WebParam(name = "causeNumber") List<Integer> causeNumberList, @WebParam(name="description") String description);
    @WebMethod(operationName = "changeComplaintStatusToConsideration")
    Result changeComplaintStatusToConsideration(@WebParam(name = "complaintId") Long complaintId);
    @WebMethod(operationName = "changeComplaintStatusToInvestigation")
    Result changeComplaintStatusToInvestigation(@WebParam(name = "complaintId") Long complaintId);
    @WebMethod(operationName = "giveConclusionOnComplaint")
    Result giveConclusionOnComplaint(@WebParam(name = "complaintId") Long complaintId, @WebParam(name = "conclusion") String conclusion);

    @WebMethod (operationName = "getMenuList")
    MenuListResult getMenuList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getMenuListBySan")
    MenuListResult getMenuList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getMenuListByOrg")
    MenuListResult getMenuListByOrg(@WebParam(name="orgId") Long orgId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getCardList")
    CardListResult getCardList(@WebParam(name="contractId") Long contractId);
    @WebMethod (operationName = "getCardListBySan")
    CardListResult getCardList(@WebParam(name="san") String san);
    @WebMethod (operationName = "getEnterEventList")
    EnterEventListResult getEnterEventList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getEnterEventListBySan")
    EnterEventListResult getEnterEventList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getEnterEventListByGuardian")
    EnterEventListResult getEnterEventListByGuardian(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getClientsByGuardSan")
    ClientsData getClientsByGuardSan(@WebParam(name="san") String san);
    @WebMethod (operationName = "attachGuardSanBySan")
    AttachGuardSanResult attachGuardSan(@WebParam(name="san") String san, @WebParam(name="guardSan") String guardSan);
    @WebMethod (operationName = "attachGuardSan")
    AttachGuardSanResult attachGuardSan(@WebParam(name="contractId") Long contractId, @WebParam(name="guardSan") String guardSan);
    @WebMethod (operationName = "detachGuardSanBySan")
    DetachGuardSanResult detachGuardSan(@WebParam(name="san") String san, @WebParam(name="guardSan") String guardSan);
    @WebMethod (operationName = "detachGuardSan")
    DetachGuardSanResult detachGuardSan(@WebParam(name="contractId") Long contractId, @WebParam(name="guardSan") String guardSan);
    @WebMethod
    Long getContractIdByCardNo(@WebParam(name="cardId") String cardId);
    @WebMethod
    ClientSummaryExt[] getSummaryByGuardSan(@WebParam(name="guardSan") String guardSan);
    @WebMethod
    Result enableNotificationBySMS(@WebParam(name="contractId") Long contractId, @WebParam(name="state") boolean state);
    @WebMethod
    Result enableNotificationByEmail(@WebParam(name="contractId") Long contractId, @WebParam(name="state") boolean state);
    @WebMethod
    Result changeMobilePhone(@WebParam(name="contractId") Long contractId, @WebParam(name="mobilePhone") String mobilePhone);
    @WebMethod
    Result changeEmail(@WebParam(name="contractId") Long contractId, @WebParam(name="email") String email);
    @WebMethod
    Result changeExpenditureLimit(@WebParam(name="contractId") Long contractId, @WebParam(name="limit") long limit);
    @WebMethod
    Result changePassword(@WebParam(name="contractId") Long contractId, @WebParam(name="base64passwordHash")  String base64passwordHash);
    @WebMethod
    CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId, @WebParam(name="state") int state);

    @WebMethod
    Result authorizeClient(@WebParam(name="contractId") Long contractId, @WebParam(name="token") String token);

    @WebMethod
    ActivateLinkingTokenResult activateLinkingToken(@WebParam(name = "linkingToken") String linkingToken);
    @WebMethod
    Result sendLinkingTokenByContractId(@WebParam(name="contractId") Long contractId);
    @WebMethod
    Result sendLinkingTokenByMobile(@WebParam(name="mobilePhone") String mobilePhone);
    @WebMethod
    GenerateLinkingTokenResult generateLinkingToken(@WebParam(name="contractId") Long contractId);

    @WebMethod
    SendResult sendPasswordRecoverURLFromEmail(@WebParam(name="contractId") Long contractId,@WebParam(name="request")RequestWebParam request);
    @WebMethod
    CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name="request")RequestWebParam request);
    @WebMethod
    IdResult getIdOfClient(@WebParam(name="contractId")Long contractId);
    @WebMethod
    IdResult getIdOfContragent(@WebParam(name="contragentName")String contragentName);
    @WebMethod
    IdResult createPaymentOrder(@WebParam(name="idOfClient")Long idOfClient,
              @WebParam(name="idOfContragent")Long idOfContragent,
              @WebParam(name="paymentMethod")int paymentMethod,
              @WebParam(name="copecksAmount")Long copecksAmount,
              @WebParam(name="contragentSum")Long contragentSum);

    @WebMethod
    Result changePaymentOrderStatus(@WebParam(name="idOfClient")Long idOfClient,
            @WebParam(name="idOfClientPaymentOrder")Long idOfClientPaymentOrder,
             @WebParam(name="orderStatus") int orderStatus);
    @WebMethod
    RBKMoneyConfigResult getRBKMoneyConfig();

    @WebMethod
    ChronopayConfigResult getChronopayConfig();

    @WebMethod
    BanksData getBanks();

    @WebMethod (operationName = "changePersonalInfo")
    Result changePersonalInfo(@WebParam(name="contractId") Long contractId,@WebParam(name="limit") Long limit,
           @WebParam(name="address") String address,@WebParam(name="phone") String phone,@WebParam(name="mobilePhone") String mobilePhone,
           @WebParam(name="email") String email,@WebParam(name="smsNotificationState") boolean smsNotificationState);


    @WebMethod(operationName="getHiddenPages")
    HiddenPagesResult getHiddenPages();

    @WebMethod(operationName="getComplexList")
    ComplexListResult getComplexList(@WebParam(name = "contractId") Long contractId,
            final Date startDate, final Date endDate);

    @WebMethod(operationName="getNotificationSettings")
    ClientNotificationSettingsResult getClientNotificationSettings(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName="setNotificationSettings")
    ClientNotificationChangeResult setClientNotificationSettings (@WebParam(name = "contractId") Long contractId,
                                                                         @WebParam(name = "notificationType") List<Long> notificationTypes);

    @WebMethod(operationName="getNotificationTypes")
    ClientNotificationSettingsResult getClientNotificationTypes ();

    @WebMethod(operationName="getStudentsByCanNotConfirmPayment")
    ClientConfirmPaymentData getStudentsByCanNotConfirmPayment(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName="getClientStats")
    ClientStatsResult getClientStats(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "type") int type);

    @WebMethod(operationName = "transferBalance")
    Result transferBalance(@WebParam(name = "contractId") Long contractId, @WebParam(name = "fromSub") Integer fromSub,
            @WebParam(name = "toSub") Integer toSub, @WebParam(name = "amount") Long amount);

    @WebMethod(operationName = "transferBalanceBySan")
    Result transferBalance(@WebParam(name = "san") String san, @WebParam(name = "fromSub") Integer fromSub,
            @WebParam(name = "toSub") Integer toSub, @WebParam(name = "amount") Long amount);

    @WebMethod(operationName = "createSubscriptionFeeding")
    Result createSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cycleDiagram") CycleDiagramIn cycleDiagramIn,
            @WebParam(name = "dateActivateService") Date dateActivateService,
            @WebParam(name = "dateCreateService") Date dateCreateService);

    @WebMethod(operationName = "findSubscriptionFeeding")
    SubFeedingResult findSubscriptionFeeding(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "suspendSubscriptionFeeding")
    Result suspendSubscriptionFeeding(@WebParam(name = "contractId") Long contractId, @WebParam(name = "reasonWasSuspended") String reasonWasSuspended);

    @WebMethod(operationName = "reopenSubscriptionFeeding")
    Result reopenSubscriptionFeeding(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "editSubscriptionFeedingPlan")
    CycleDiagramOut editSubscriptionFeedingPlan(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cycleDiagram") CycleDiagramIn cycleDiagramIn);

    @WebMethod(operationName = "findClientCycleDiagram")
    CycleDiagramOut findClientCycleDiagram(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "findComplexesWithSubFeeding")
    ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "contractId") Long contractId);
    //Result attachGuardianByClient(@WebParam(name = "childrenContractId") Long childrenContractId,
    //        @WebParam(name = "childrenGuardianId") Long GuardianId);

    @WebMethod(operationName = "createSubscriptionFeedingBySan")
    Result createSubscriptionFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "cycleDiagram") CycleDiagramIn cycleDiagramIn,
            @WebParam(name = "dateActivateService") Date dateActivateService,
            @WebParam(name = "dateCreateService") Date dateCreateService);

    @WebMethod(operationName = "findSubscriptionFeedingBySan")
    SubFeedingResult findSubscriptionFeeding(@WebParam(name = "san") String san);

    @WebMethod(operationName = "suspendSubscriptionFeedingBySan")
    Result suspendSubscriptionFeeding(@WebParam(name = "san") String san, @WebParam(name = "reasonWasSuspended") String reasonWasSuspended);

    @WebMethod(operationName = "reopenSubscriptionFeedingBySan")
    Result reopenSubscriptionFeeding(@WebParam(name = "san") String san);

    @WebMethod(operationName = "editSubscriptionFeedingPlanBySan")
    CycleDiagramOut editSubscriptionFeedingPlan(@WebParam(name = "san") String san,
            @WebParam(name = "cycleDiagram") CycleDiagramIn cycleDiagramIn);

    @WebMethod(operationName = "findClientCycleDiagramBySan")
    CycleDiagramOut findClientCycleDiagram(@WebParam(name = "san") String san);

    @WebMethod(operationName = "findComplexesWithSubFeedingBySan")
    ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "san") String san);

    @WebMethod(operationName = "getTransferSubBalanceList")
    TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getTransferSubBalanceListBySan")
    TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "san") String san, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "getSubscriptionFeedingSetting")
    SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "contractId") Long contractId);

    @WebMethod(operationName = "getSubscriptionFeedingSettingBySan")
    SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "san") String san);


}
