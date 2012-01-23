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
    Data getSummary(Long contractId);
    @WebMethod (operationName = "getSummaryBySan")
    Data getSummary(String san);
    @WebMethod (operationName = "getPurchaseList")
    Data getPurchaseList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getPurchaseListBySan")
    Data getPurchaseList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getPaymentList")
    Data getPaymentList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getPaymentListBySan")
    Data getPaymentList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getMenuList")
    Data getMenuList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getMenuListBySan")
    Data getMenuList(String san, Date startDate, Date endDate);
    @WebMethod (operationName = "getCardList")
    Data getCardList(Long contractId);
    @WebMethod (operationName = "getCardListBySan")
    Data getCardList(String san);
    @WebMethod (operationName = "getEnterEventList")
    Data getEnterEventList(Long contractId, Date startDate, Date endDate);
    @WebMethod (operationName = "getEnterEventListBySan")
    Data getEnterEventList(String san, Date startDate, Date endDate);
    @WebMethod
    Data getClientsByGuardSan(String san);
}
