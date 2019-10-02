/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;

import org.apache.commons.httpclient.HttpStatus;
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
 * Time: 11:42
 */

@Service(IRequestOperation.SUBSCRIPTION_DELETE)
public class SubscriptionDeleteRequest implements IRequestOperation {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    private RuntimeContext runtimeContext;

    @PostConstruct
    protected void init() {
        runtimeContext = RuntimeContext.getInstance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MfrRequest createRequest(Long subscriptionId) {
        BankSubscription bs = em.find(BankSubscription.class, subscriptionId);
        MfrRequest request = new MfrRequest();
        request.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        request.setRequestType(MfrRequest.REQUEST_TYPE_DEACTIVATION);
        request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getPaymentUrl());
        request.setRequestTime(new Date());
        request.setClient(bs.getClient());
        request.setSan(bs.getClient().getSan());
        request.setBankSubscription(bs);
        em.persist(request);
        return request;
    }

    @Override
    public Map<String, String> getRequestParams(MfrRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        // код операции
        params.put("opcode", "stop_rebilling");
        // ID подписки в системе МФР
        params.put("payment_id", request.getBankSubscription().getPaymentId());
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + payment_id + secret_word)
        String token = CryptoUtils.MD5(merchantId + params.get("payment_id") + secretWord).toLowerCase();
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
        BankSubscription bs = em.find(BankSubscription.class, subscriptionId);
        if (paymentResponse.getStatusCode() == HttpStatus.SC_OK) {
            bs.setActive(false);
            bs.setDeactivationDate(paymentResponse.getDateTime());
            bs.setStatus(MfrRequest.SUBSCRIPTION_DEACTIVATED);
            return true;
        } else {
            return false;
        }
    }
}
