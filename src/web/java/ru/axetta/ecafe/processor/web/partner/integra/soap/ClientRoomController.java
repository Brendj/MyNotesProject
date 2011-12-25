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
    @WebMethod
    ClientSummaryExt getSummary(Long contractId);
    @WebMethod
    PurchaseListExt getPurchaseList(Long contractId, Date startDate, Date endDate);
    @WebMethod
    PaymentList getPaymentList(Long contractId, Date startDate, Date endDate);
    @WebMethod
    MenuListExt getMenuList(Long contractId, Date startDate, Date endDate);
    @WebMethod
    CardList getCardList(Long contractId);
    @WebMethod
    EnterEventList getEnterEventList(Long contractId, Date startDate, Date endDate);
}
