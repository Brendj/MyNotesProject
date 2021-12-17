/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 12.11.13
 * Time: 11:02
 */

@Service(IRequestOperation.SUBSCRIPTION_REG)
public class SubscriptionRegRequest implements IRequestOperation {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionRegRequest.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    private RuntimeContext runtimeContext;

    @PostConstruct
    protected void init() {
        runtimeContext = RuntimeContext.getInstance();
    }

    @Override
    public MfrRequest createRequest(Long subscriptionId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MfrRequest createRequestForSubscriptionReg(Long contractId, Long paymentAmount, Long thresholdAmount,
            int period, Date validityDate, String mobile) {
        MfrRequest request = new MfrRequest();
        request.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        request.setRequestType(MfrRequest.REQUEST_TYPE_ACTIVATION);
        request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getLinkingUrl());
        Client client = DAOUtils.findClientByContractId(em.unwrap(Session.class), contractId);
        request.setRequestTime(new Date());
        request.setClient(client);
        request.setSan(client.getSan());
        BankSubscription bs = new BankSubscription();
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setMonthsCount(period);
        bs.setClient(client);
        bs.setSan(client.getSan());
        bs.setPaySystem(request.getPaySystem());
        bs.setValidToDate((validityDate == null && period > 0) ? CalendarUtils.addMonth(new Date(), period) : validityDate);
        bs.setMobile(mobile);
        em.persist(bs);
        request.setBankSubscription(bs);
        em.persist(request);
        return request;
    }

    @Override
    public Map<String, String> getRequestParams(MfrRequest mfrRequest) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", runtimeContext.getAcquiropaySystemConfig().getLinkingUrl());
        Contragent c = DAOReadonlyService.getInstance().findContragentByClient(mfrRequest.getClient().getContractId());
        String productId = c.getMfrId();
        if (StringUtils.isEmpty(productId)) {
            logger.error("Contragent '{}' with idOfContragent = {} doesn't have specified MFR_ID.",
                    c.getContragentName(), c.getIdOfContragent());
        }
        // ID поставщика питания №1 в системе МФР
        params.put("product_id", productId);
        String account = ContractIdFormat.format(mfrRequest.getClient().getContractId());
        params.put("product_name", "Подписка на оплату Школьного питания л/с " + account);
        params.put("amount", "*");
        // ID запроса ИС ПП
        params.put("cf", mfrRequest.getIdOfRequest().toString());
        // Уникальный идентификатор подписки ИС ПП
        params.put("cf2", mfrRequest.getBankSubscription().getIdOfSubscription().toString());
        // Номер лицевого счета учащегося
        params.put("cf3", account);
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + product_id + [amount] + cf +cf2 + cf3 + secret_word)
        String token = CryptoUtils
                .MD5(merchantId + productId + params.get("amount") + params.get("cf") + params.get("cf2") +
                        params.get("cf3") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processResponse(Long mfrRequestId, PaymentResponse paymentResponse) {
        MfrRequest request = em.getReference(MfrRequest.class, mfrRequestId);
        if (paymentResponse.getStatusCode() == HttpStatus.SC_OK) {
            request.setSuccess(true);
        } else {
            request.setErrorDescription(String.valueOf(paymentResponse.getStatusCode()));
        }
        request.setResponseStatus(paymentResponse.getStatus());
        if (MfrRequest.ERROR.equalsIgnoreCase(paymentResponse.getStatus())) {
            request.setErrorDescription(paymentResponse.getErrorDescription());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean postProcessResponse(Long mfrRequestId, Long subscriptionId, PaymentResponse paymentResponse) {
        MfrRequest mfrRequest = em.find(MfrRequest.class, mfrRequestId);
        mfrRequest.setResponseStatus(paymentResponse.getStatus());
        if (MfrRequest.SUBSCRIPTION_ACTIVATED.equalsIgnoreCase(paymentResponse.getStatus())) {
            BankSubscription bs = mfrRequest.getBankSubscription();
            bs.setStatus(MfrRequest.SUBSCRIPTION_ACTIVATED);
            bs.setActive(true);
            bs.setPaymentId(paymentResponse.getPaymentId());
            bs.setActivationDate(paymentResponse.getDateTime());
            bs.setMaskedCardNumber(paymentResponse.getPanMask());
            bs.setCardHolder(paymentResponse.getCardHolder());
            Integer expYear = StringUtils.isEmpty(paymentResponse.getExpYear()) ? null
                    : Integer.valueOf(paymentResponse.getExpYear());
            bs.setExpYear(expYear);
            Integer expMonth = StringUtils.isEmpty(paymentResponse.getExpMonth()) ? null
                    : Integer.valueOf(paymentResponse.getExpMonth());
            bs.setExpMonth(expMonth);
            /*Date validToDate = CalendarUtils.addMonth(paymentResponse.getDateTime(), bs.getMonthsCount());
            if (expMonth != null && expYear != null) {
                Date cardValidityDate = CalendarUtils.getDateOfLastDay(expYear, expMonth);
                bs.setValidToDate(cardValidityDate.before(validToDate) ? cardValidityDate : validToDate);
            } else {
                bs.setValidToDate(validToDate);
            }*/
            return true;
        } else if (MfrRequest.ERROR.equalsIgnoreCase(paymentResponse.getStatus())) {
            mfrRequest.setErrorDescription(paymentResponse.getErrorDescription());
        }
        return false;
    }
}
