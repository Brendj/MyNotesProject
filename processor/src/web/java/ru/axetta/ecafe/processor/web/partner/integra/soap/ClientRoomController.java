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

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.12.11
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */

@WebService
public interface ClientRoomController {

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
    @WebMethod (operationName = "getPaymentList")
    PaymentListResult getPaymentList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);
    @WebMethod (operationName = "getPaymentListBySan")
    PaymentListResult getPaymentList(@WebParam(name="san") String san, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate") Date endDate);

     @WebMethod(operationName="getClientSmsList")
    ClientSmsListResult getClientSmsList(@WebParam(name="contractId") Long contractId, @WebParam(name="startDate") Date startDate, @WebParam(name="endDate")Date endDate);

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
    public ClientSummaryExt[] getSummaryByGuardSan(@WebParam(name="guardSan") String guardSan);
    @WebMethod
    public Result enableNotificationBySMS(@WebParam(name="contractId") Long contractId, @WebParam(name="state") boolean state);
    @WebMethod
    public Result enableNotificationByEmail(@WebParam(name="contractId") Long contractId, @WebParam(name="state") boolean state);
    @WebMethod
    public Result changeMobilePhone(@WebParam(name="contractId") Long contractId, @WebParam(name="mobilePhone") String mobilePhone);
    @WebMethod
    public Result changeEmail(@WebParam(name="contractId") Long contractId, @WebParam(name="email") String email);
    @WebMethod
    public Result changeExpenditureLimit(@WebParam(name="contractId") Long contractId, @WebParam(name="limit") long limit);
    @WebMethod
    public Result changePassword(@WebParam(name="contractId") Long contractId, @WebParam(name="base64passwordHash")  String base64passwordHash);
    @WebMethod
    public CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId, @WebParam(name="state") int state);

    @WebMethod
    public Result authorizeClient(@WebParam(name="contractId") Long contractId, @WebParam(name="token") String token);

    @WebMethod
    public ActivateLinkingTokenResult activateLinkingToken(@WebParam(name = "linkingToken") String linkingToken);
    @WebMethod
    public Result sendLinkingTokenByContractId(@WebParam(name="contractId") Long contractId);
    @WebMethod
    public Result sendLinkingTokenByMobile(@WebParam(name="mobilePhone") String mobilePhone);
    @WebMethod
    public GenerateLinkingTokenResult generateLinkingToken(@WebParam(name="contractId") Long contractId);

    @WebMethod
    public SendResult sendPasswordRecoverURLFromEmail(@WebParam(name="contractId") Long contractId,@WebParam(name="request")RequestWebParam request);
    @WebMethod
    public CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name="request")RequestWebParam request);
     @WebMethod
    public IdResult getIdOfClient(@WebParam(name="contractId")Long contractId);
     @WebMethod
    public IdResult getIdOfContragent(@WebParam(name="contragentName")String contragentName);
      @WebMethod
    public IdResult createPaymentOrder(@WebParam(name="idOfClient")Long idOfClient,
              @WebParam(name="idOfContragent")Long idOfContragent,
              @WebParam(name="paymentMethod")int paymentMethod,
              @WebParam(name="copecksAmount")Long copecksAmount,
              @WebParam(name="contragentSum")Long contragentSum);

    @WebMethod
    public Result changePaymentOrderStatus(@WebParam(name="idOfClient")Long idOfClient,
            @WebParam(name="idOfClientPaymentOrder")Long idOfClientPaymentOrder,
             @WebParam(name="orderStatus") int orderStatus);
     @WebMethod
    public RBKMoneyConfigResult getRBKMoneyConfig();

    @WebMethod
    public ChronopayConfigResult getChronopayConfig();

     @WebMethod
    public BanksData getBanks();

    @WebMethod (operationName = "changePersonalInfo")
    public Result changePersonalInfo(@WebParam(name="contractId") Long contractId,@WebParam(name="limit") Long limit,
           @WebParam(name="address") String address,@WebParam(name="phone") String phone,@WebParam(name="mobilePhone") String mobilePhone,
           @WebParam(name="email") String email,@WebParam(name="smsNotificationState") boolean smsNotificationState);


    @WebMethod(operationName="getHiddenPages")
    public HiddenPagesResult getHiddenPages();

    @WebMethod(operationName="getComplexList")
    public ComplexListResult getComplexList(@WebParam(name = "contractId") Long contractId,
            final Date startDate, final Date endDate);

}
