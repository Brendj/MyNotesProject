/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.12.14
 * Time: 12:20
 */

public class RegularPaymentEasyCheck {

    private static final Logger logger = LoggerFactory.getLogger(RegularPaymentEasyCheck.class);

    private static final int RC_BAD_REQUEST = 400;
    private static final int RC_INTERNAL_SERVER_ERROR = 500;

    private static final String RC_CLIENT_NOT_FOUND_DESC = "Клиент с Л/с № %s не найден.";
    private static final String RC_INTERNAL_SERVER_ERROR_DESC = "Внутренняя ошибка сервера.";
    private static final String RC_SUBSCRIPTION_NOT_FOUND_DESC = "Подписка с id = %s не найдена.";
    private static final String RC_SUBSCRIPTION_CLIENT_NOT_VALID = "Подписка коиента не действительна.";
    private static final String RC_INVALID_PARAMETERS_DESC = "Request has invalid parameters.";

    @Autowired
    @Qualifier("regularPaymentSubscriptionService")
    private RegularPaymentSubscriptionService rpService;

    public RequestResultEasyCheck regularPaymentEasyCheckReadSubscriptionList(Long contractId, Session session) {
        RequestResultEasyCheck requestResult = new RequestResultEasyCheck();
        List<Long> subscriptionList;
        try {
            if (contractId != null) {
                subscriptionList = findSubscriptionsIdWithoutActivationDate(contractId, session);
            } else {
                subscriptionList = new ArrayList<Long>();
            }
            requestResult.setSubscriptionListEasyCheck(new SubscriptionListEasyCheck());
            requestResult.getSubscriptionListEasyCheck().setIdList(subscriptionList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    public RequestResultEasyCheck regularPaymentEasyCheckCreateSubscription(Long contractId, Long lowerLimitAmount,
            Long paymentAmount, Session session, Transaction transaction, RuntimeContext runtimeContext) {
        RequestResultEasyCheck requestResultEasyCheck = new RequestResultEasyCheck();
        try {
            Client c = DAOUtils.findClientByContractId(session, contractId);
            int period = 12;

            MfrRequest request = new MfrRequest();
            request.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
            request.setRequestType(MfrRequest.REQUEST_TYPE_ACTIVATION);
            request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getLinkingUrl());
            Client client = DAOUtils.findClientByContractId(session, contractId);
            request.setRequestTime(new Date());
            request.setClient(client);
            request.setSan(client.getSan());
            BankSubscription bs = new BankSubscription();
            bs.setPaymentAmount(paymentAmount);
            bs.setThresholdAmount(lowerLimitAmount);
            bs.setMonthsCount(period);
            bs.setClient(client);
            bs.setSan(client.getSan());
            bs.setPaySystem(request.getPaySystem());
            bs.setActive(true);
            bs.setValidToDate(CalendarUtils.addMonth(new Date(), period));
            session.persist(bs);
            request.setBankSubscription(bs);
            session.persist(request);
            requestResultEasyCheck.setErrorCode(0);
            transaction.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage());
            requestResultEasyCheck.setErrorCode(RC_BAD_REQUEST);
            requestResultEasyCheck.setErrorDesc(String.format(RC_CLIENT_NOT_FOUND_DESC, contractId));
        } finally {
            HibernateUtils.close(session, logger);
        }
        return requestResultEasyCheck;
    }

    public RequestResultEasyCheck regularPaymentEasyCheckEditSubscription(Long regularPaymentSubscriptionID,
            Long contractId, long lowerLimitAmount, long paymentAmount, Session session) {
        RequestResultEasyCheck requestResultEasyCheck = new RequestResultEasyCheck();
        try {
            BankSubscription bs = findBankSubscription(regularPaymentSubscriptionID, session);
            if (bs == null) {
                requestResultEasyCheck.setErrorCode(RC_BAD_REQUEST);
                requestResultEasyCheck
                        .setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, regularPaymentSubscriptionID));
                return requestResultEasyCheck;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, requestResultEasyCheck)) {
                return requestResultEasyCheck;
            }
            updateBankSubscriptionWithoutPeriod(regularPaymentSubscriptionID, paymentAmount, lowerLimitAmount, session);
            requestResultEasyCheck.setErrorCode(0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            requestResultEasyCheck.setErrorCode(RC_BAD_REQUEST);
            requestResultEasyCheck.setErrorDesc(String.format(RC_CLIENT_NOT_FOUND_DESC, contractId));
        }
        return requestResultEasyCheck;
    }

    private boolean checkSubscriptionContractId(BankSubscription bs, Long contractId,
            RequestResultEasyCheck requestResult) {
        if (contractId != null) {
            Client c = DAOReadonlyService.getInstance().getClientByContractId(contractId);
            if (c == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_CLIENT_NOT_FOUND_DESC, contractId));
                return false;
            }
            if (c.getIdOfClient().longValue() != bs.getClient().getIdOfClient().longValue()) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(RC_SUBSCRIPTION_CLIENT_NOT_VALID);
                return false;
            }
        }
        return true;
    }

    public List<Long> findSubscriptionsIdWithoutActivationDate(Long contractId, Session session) throws Exception {
        Criteria criteria = session.createCriteria(BankSubscription.class);
        criteria.setProjection(Projections.property("idOfSubscription")).createAlias("client", "c");
        criteria.add(Restrictions.eq("c.contractId", contractId));
        criteria.addOrder(Order.asc("idOfSubscription"));
        return (List<Long>) criteria.list();
    }

    public void updateBankSubscriptionWithoutPeriod(Long bsId, Long paymentAmount, Long thresholdAmount,
            Session session) {
        BankSubscription bs = (BankSubscription) session.load(BankSubscription.class, bsId);
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setActive(true);
        bs.setValidToDate(CalendarUtils.addMonth(new Date(), 12));
    }

    public BankSubscription findBankSubscription(Long id, Session session) {
        return (BankSubscription) session.load(BankSubscription.class, id);
    }

    public RequestResultEasyCheck regularPaymentEasyCheckEdit(List<Long> ids, Long contractId, Long lowerLimitAmount,
            Long paymentAmount, Session session, Transaction transaction) {
        RequestResultEasyCheck requestResultEasyCheck = null;

        try {
            for (Long id : ids) {
                requestResultEasyCheck = regularPaymentEasyCheckEditSubscription(id, contractId, lowerLimitAmount,
                        paymentAmount, session);
            }
            transaction.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
        } finally {
            HibernateUtils.close(session, logger);
        }

        return requestResultEasyCheck;
    }
}
