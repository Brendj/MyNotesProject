/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 19.05.2010
 * Time: 12:12:00
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTextUtils {

    private PaymentTextUtils() {

    }

    public static String buildTransferInfo(Session session, String contragentName, Integer paymentMethod, String addPaymentMethod, String addIdOfPayment) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != contragentName) {
            stringBuilder.append(contragentName);
        }
        if (paymentMethod != ClientPayment.SYNC_PAYMENT_METHOD) {
            appendIfNotEmpty(stringBuilder, " - ");
            stringBuilder.append(ClientPayment.PAYMENT_METHOD_NAMES[paymentMethod]);
        }
        if (StringUtils.isNotEmpty(addPaymentMethod)) {
            appendIfNotEmpty(stringBuilder, " - ");
            stringBuilder.append(addPaymentMethod);
            if (StringUtils.isNotEmpty(addIdOfPayment)) {
                stringBuilder.append(" - номер платежного документа ").append(addIdOfPayment);
            }
        }
        return stringBuilder.toString();
    }

    public static String buildTransferInfo(Session session, ClientPayment clientPayment) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Contragent contragent = (Contragent)session.merge(clientPayment.getContragent());
        if (null != contragent) {
            stringBuilder.append(contragent.getContragentName());
        }
        int paymentMethod = clientPayment.getPaymentMethod();
        if (paymentMethod != ClientPayment.SYNC_PAYMENT_METHOD) {
            appendIfNotEmpty(stringBuilder, " - ");
            stringBuilder.append(ClientPayment.PAYMENT_METHOD_NAMES[paymentMethod]);
        }
        String addPaymentMethod = clientPayment.getAddPaymentMethod();
        if (StringUtils.isNotEmpty(addPaymentMethod)) {
            appendIfNotEmpty(stringBuilder, " - ");
            stringBuilder.append(addPaymentMethod);
            String addIdOfPayment = clientPayment.getAddIdOfPayment();
            if (StringUtils.isNotEmpty(addIdOfPayment)) {
                stringBuilder.append(" - номер платежного документа ").append(addIdOfPayment);
            }
        }
        return stringBuilder.toString();
    }

    private static void appendIfNotEmpty(StringBuilder stringBuilder, String suffix) {
        if (stringBuilder.length() > 0) {
            stringBuilder.append(suffix);
        }
    }

}
