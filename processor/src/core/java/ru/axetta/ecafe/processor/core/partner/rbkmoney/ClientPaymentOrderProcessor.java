/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.12.2009
 * Time: 17:02:56
 * To change this template use File | Settings | File Templates.
 */
public interface ClientPaymentOrderProcessor {

    Long createPaymentOrder(Long idOfClient, Long idOfContragent, int paymentMethod, Long sum, Long contragentSum)
            throws Exception;

    void changePaymentOrderStatus(Long idOfClient, Long idOfClientPaymentOrder, int orderStatus) throws Exception;

    void changePaymentOrderStatus(Long idOfContragent, Long idOfClientPaymentOrder, int orderStatus, Long contragentSum,
            String idOfPayment,String addIdOfPayment) throws Exception;
    void changePaymentOrderStatus(Long idOfContragent, Long idOfClientPaymentOrder, int orderStatus, Long contragentSum,
            String idOfPayment) throws Exception;
}
