/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.Arrays;
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
@DependsOn("runtimeContext")
public class SMSSubscriptionFeeService {

    private static Logger logger = LoggerFactory.getLogger(SMSSubscriptionFeeService.class);

    public static final int SMS_PAYMENT_BY_SUBSCRIPTION_FEE = 1;
    public static final int SMS_PAYMENT_BY_THE_PIECE = 2;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    private RuntimeContext runtimeContext;

    private SMSSubscriptionFeeService getSelfProxy() {
        return RuntimeContext.getAppContext().getBean(SMSSubscriptionFeeService.class);
    }

    @Autowired
    private EventNotificationService enService;
    @Autowired
    private FinancialOpsManager fOpsManager;

    private Long defaultSubFee;
    private int paymentType;

    @PostConstruct
    protected void init() {
        if (RuntimeContext.isOrgRoomRunning()) {
            return;
        }
        defaultSubFee = runtimeContext.getOptionValueLong(Option.OPTION_SMS_DEFAULT_SUBSCRIPTION_FEE);
        paymentType = runtimeContext.getOptionValueInt(Option.OPTION_SMS_PAYMENT_TYPE);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void notifyClientsAboutSMSSubscriptionFee() throws Exception {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        if (paymentType != SMS_PAYMENT_BY_SUBSCRIPTION_FEE) {
            return;
        }
        try {
            SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                    SecurityJournalProcess.EventType.SMS_SUBSCRIPTION_FEE, new Date());
            process.saveWithSuccess(true);
            Date date = new Date();
            DateFormat df = CalendarUtils.getDateFormatLocal();
            String withdrawDate = df.format(CalendarUtils.getFirstDayOfNextMonth(date));
            String currentDate = df.format(date);
            List<Long> ids = findClientsIdWithNotificationViaSMS();
            for (Long id : ids) {
                Client client = em.find(Client.class, id);
                Long smsSubFee = client.getOrg().getSubscriptionPrice() == 0 ? defaultSubFee
                        : client.getOrg().getSubscriptionPrice();
                if (client.getBalance() - smsSubFee < 0) {
                    String[] values = {
                            "contractId", client.getContractId().toString(), "withdrawDate", withdrawDate,
                            "smsSubscriptionFee", CurrencyStringUtils.copecksToRubles(smsSubFee), "date", currentDate};
                    enService.sendSMS(client, null, EventNotificationService.NOTIFICATION_SMS_SUBSCRIPTION_FEE, values, date);
                }
            }
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                    SecurityJournalProcess.EventType.SMS_SUBSCRIPTION_FEE, new Date());
            processEnd.saveWithSuccess(true);
        } catch (Exception e) {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                    SecurityJournalProcess.EventType.SMS_SUBSCRIPTION_FEE, new Date());
            processEnd.saveWithSuccess(false);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    public void smsSubscriptionFeeWithdraw() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        if (paymentType != SMS_PAYMENT_BY_SUBSCRIPTION_FEE) {
            return;
        }
        logger.info("SMS subscription fee withdraw service work begin.");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String withdrawDate = CalendarUtils.dateToString(new Date());
        List<Long> ids = findClientsIdWithoutWithdraw(year, month, 10000);
        for (Long id : ids) {
            try {
                getSelfProxy().processOneClient(id, withdrawDate, year, month);
            } catch (Exception ex) {
                logger.error("Unable to withdraw SMS subscription fee for client with id = {}. Cause: {}", id,
                        ex.getMessage());
            }
        }
        logger.info("SMS subscription fee withdraw service work end.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processOneClient(Long id, String withdrawDate, int year, int month) throws Exception {
        Client client = em.find(Client.class, id);
        Long smsSubFee =
                client.getOrg().getSubscriptionPrice() == 0 ? defaultSubFee : client.getOrg().getSubscriptionPrice();
        String[] values = {
                "contractId", client.getContractId().toString(), "date", withdrawDate, "smsSubscriptionFee",
                CurrencyStringUtils.copecksToRubles(smsSubFee), "withdrawDate", withdrawDate};
        if (client.getBalance() - smsSubFee < 0) {
            enService.sendSMS(client, null, EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS, values,
                    false, new Date());
            client.setNotifyViaSMS(false);
        } else {
            Session session = em.unwrap(Session.class);
            // Снимаем абон. плату за смс-сервис
            SubscriptionFee sf = fOpsManager.createSubscriptionFeeCharge(session, client, smsSubFee, year, month,
                    SubscriptionFee.TYPE_SMS_SERVICE);
            logger.info("Withdraw from client with contract_id = {}", client.getContractId());
            // Уведомляем клиента об активации услуги.
            boolean sendResult = enService
                    .sendSMS(client, null, EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS, values, false, new Date());
            if (sendResult) {
                ClientSms clientSms = SMSService.getCreatedClientSms();
                clientSms.setTransaction(sf.getTransaction());
                session.merge(clientSms);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> findClientsIdWithoutWithdraw(int year, int month, int rowCount) {
        Query query = em.createQuery("select distinct c.idOfClient \n" +
                "from Client c where c.notifyViaSMS = :notify and (c.idOfClientGroup not in (:cg) or c.idOfClientGroup is null) \n"
                + "and c not in (select c2 from AccountTransaction at join at.client c2 join at.subscriptionFeesInternal sf \n"
                + "where sf.subscriptionYear = :year and sf.periodNo = :month and sf.subscriptionType = :type)")
                .setParameter("notify", true)
                .setParameter("cg", Arrays.asList(
                        ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                        ClientGroup.Predefined.CLIENT_DELETED.getValue()))
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("type", SubscriptionFee.TYPE_SMS_SERVICE);
        if (rowCount > 0) {
            query.setMaxResults(rowCount);
        }
        return (List<Long>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<Long> findClientsIdWithNotificationViaSMS() {
        Query query = em.createQuery("select distinct c.idOfClient from Client c where c.notifyViaSMS = :notify and "
                + " (c.idOfClientGroup not in (:cg) or c.idOfClientGroup is null)")
                .setParameter("notify", true)
                .setParameter("cg", Arrays.asList(
                        ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                        ClientGroup.Predefined.CLIENT_DELETED.getValue()));
        return (List<Long>) query.getResultList();
    }
}
