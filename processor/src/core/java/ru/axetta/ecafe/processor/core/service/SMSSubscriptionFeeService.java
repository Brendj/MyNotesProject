/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SubscriptionFee;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 25.09.13
 * Time: 15:44
 * Класс для работы с абонентской платой за SMS-сервис.
 */

@Service("smsSubscriptionFeeService")
public class SMSSubscriptionFeeService {

    private static Logger logger = LoggerFactory.getLogger(SMSSubscriptionFeeService.class);

    public static final int SMS_PAYMENT_BY_SUBSCRIPTION_FEE = 1;
    public static final int SMS_PAYMENT_BY_THE_PIECE = 2;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    private RuntimeContext runtimeContext;

    @Autowired
    private EventNotificationService enService;

    @Transactional
    public void notifyClientsAboutSMSSubscriptionFee() throws Exception {
        int paymentType = runtimeContext.getOptionValueInt(Option.OPTION_SMS_PAYMENT_TYPE);
        if (paymentType != SMS_PAYMENT_BY_SUBSCRIPTION_FEE) {
            return;
        }
        String withdrawDate = CalendarUtils.dateToString(CalendarUtils.getFirstDayOfNextMonth(new Date()));
        String currentDate = CalendarUtils.dateToString(new Date());
        List<Client> clients = findClientsWithNotificationViaSMS();
        for (Client client : clients) {
            Long smsFee = client.getOrg().getSubscriptionPrice();
            if (client.getBalance() - smsFee < 0) {
                String[] values = {
                        "contractId", client.getContractId().toString(), "withdrawDate", withdrawDate,
                        "smsSubscriptionFee", CurrencyStringUtils.copecksToRubles(smsFee), "date", currentDate};
                enService.sendSMS(client, EventNotificationService.NOTIFICATION_SMS_SUBSCRIPTION_FEE, values, false);
            }
        }
    }

    @Transactional
    public void smsSubscriptionFeeWithdraw() {
        int paymentType = runtimeContext.getOptionValueInt(Option.OPTION_SMS_PAYMENT_TYPE);
        if (paymentType != SMS_PAYMENT_BY_SUBSCRIPTION_FEE) {
            return;
        }
        String withdrawDate = CalendarUtils.dateToString(new Date());
        List<Client> clients = findClientsWithoutWithdraw();
        for (Client client : clients) {
            try {
                processOneClient(client, withdrawDate);
            } catch (Exception ex) {
                logger.error("Unable to withdraw SMS subscription fee for client with contract_id = {}.",
                        client.getContractId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    private void processOneClient(Client client, String withdrawDate) throws Exception {
        Long smsSubFee = client.getOrg().getSubscriptionPrice();
        String[] values = {
                "contractId", client.getContractId().toString(), "date", withdrawDate, "smsSubscriptionFee",
                CurrencyStringUtils.copecksToRubles(smsSubFee), "withdrawDate", withdrawDate};
        if (client.getBalance() - smsSubFee < 0) {
            client.setNotifyViaSMS(false);
            enService.sendSMS(client, EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS, values,
                    false);
        } else {
            // Снимаем абон. плату за смс-сервис
            SubscriptionFee sf = RuntimeContext.getFinancialOpsManager()
                    .createSubscriptionFeeCharge((Session) em.getDelegate(), client, smsSubFee,
                            Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                            SubscriptionFee.TYPE_SMS_SERVICE);
            // Уведомляем клиента об активации услуги.
            boolean sendResult = enService
                    .sendSMS(client, EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS, values, false);
            if (sendResult) {
                ClientSms clientSms = SMSService.getCreatedClientSms();
                clientSms.setTransaction(sf.getTransaction());
                em.merge(clientSms);
            }
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    private List<Client> findClientsWithNotificationViaSMS() {
        TypedQuery<Client> query = em
                .createQuery("select distinct c from Client c where c.notifyViaSMS = :param", Client.class)
                .setParameter("param", true);
        return query.getResultList();
    }

    // Возвращает клиентов, у которых не списывалась абон. плата за текущий месяц.
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    private List<Client> findClientsWithoutWithdraw() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        TypedQuery<Client> query = em.createQuery(
                "select distinct c from Client c where c.notifyViaSMS = :param and c not in "
                        + "(select c2 from AccountTransaction at join at.client c2 join at.subscriptionFeesInternal sf " +
                        "where sf.subscriptionYear = :year and sf.periodNo = :month and sf.subscriptionType = :type)",
                Client.class).setParameter("param", true).setParameter("year", year).setParameter("month", month)
                .setParameter("type", SubscriptionFee.TYPE_SMS_SERVICE);
        return query.getResultList();
    }
}
