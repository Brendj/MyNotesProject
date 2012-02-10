/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;

import javax.jws.WebMethod;
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
    @WebMethod (operationName = "getSummary")
    ClientSummaryResult getSummary(Long contractId);
    @WebMethod (operationName = "getSummaryBySan")
    ClientSummaryResult getSummary(String san);
    @WebMethod (operationName = "getPurchaseList")
    PurchaseListResult getPurchaseList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getPurchaseListBySan")
    PurchaseListResult getPurchaseList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getPaymentList")
    PaymentListResult getPaymentList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getPaymentListBySan")
    PaymentListResult getPaymentList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getMenuList")
    MenuListResult getMenuList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getMenuListBySan")
    MenuListResult getMenuList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getCardList")
    CardListResult getCardList(Long contractId);
    @WebMethod (operationName = "getCardListBySan")
    CardListResult getCardList(String san);
    @WebMethod (operationName = "getEnterEventList")
    EnterEventListResult getEnterEventList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getEnterEventListBySan")
    EnterEventListResult getEnterEventList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getClientsByGuardSan")
    ClientsData getClientsByGuardSan(String san);
    @WebMethod (operationName = "attachGuardSanBySan")
    AttachGuardSanResult attachGuardSan(String san, String guardSan);
    @WebMethod (operationName = "attachGuardSan")
    AttachGuardSanResult attachGuardSan(Long contractId, String guardSan);
    @WebMethod (operationName = "detachGuardSanBySan")
    DetachGuardSanResult detachGuardSan(String san, String guardSan);
    @WebMethod (operationName = "detachGuardSan")
    DetachGuardSanResult detachGuardSan(Long contractId, String guardSan);
    PaymentResult balanceRequest(String pid, Long clientId, Long opId, Long termId, int paymentSystem);
    PaymentResult commitPaymentRequest(String pid, Long clientId, Long sum, String time, Long opId, Long termId, int paymentSystem);
}
