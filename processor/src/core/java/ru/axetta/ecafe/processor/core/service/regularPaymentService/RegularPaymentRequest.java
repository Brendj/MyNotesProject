/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

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
 * Time: 11:15
 */

@Service("regularPaymentRequest")
public class RegularPaymentRequest implements IRequestOperation {

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
        request.setRequestType(MfrRequest.REQUEST_TYPE_PAYMENT);
        request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getPaymentUrl());
        request.setRequestTime(new Date());
        request.setClient(bs.getClient());
        request.setSan(bs.getClient().getSan());
        request.setBankSubscription(bs);
        em.persist(request);
        RegularPayment payment = new RegularPayment();
        payment.setBankSubscription(bs);
        payment.setMfrRequest(request);
        payment.setPaymentAmount(bs.getPaymentAmount());
        payment.setClient(bs.getClient());
        payment.setClientBalance(bs.getClient().getBalance());
        payment.setThresholdAmount(bs.getThresholdAmount());
        payment.setPaymentDate(new Date());
        request.addRegularPayment(payment);
        em.persist(payment);
        return request;
    }

    @Override
    public Map<String, String> getRequestParams(MfrRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        RegularPayment payment = request.getRegularPayment();
        // код операции
        params.put("opcode", "rebill");
        // сумма платежа
        params.put("amount", CurrencyStringUtils.copecksToRubles(payment.getPaymentAmount(), 0));
        // ID запроса ИС ПП
        params.put("cf", request.getIdOfRequest().toString());
        String paymentId = request.getBankSubscription().getPaymentId();
        // ID подписки в системе МФР
        params.put("payment_id", paymentId);
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + payment_id + [amount]  + secret_word)
        String token = CryptoUtils.MD5(merchantId + paymentId + params.get("amount") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processResponse(Long mfrRequestId, PaymentResponse paymentResponse) {
        MfrRequest request = em.find(MfrRequest.class, mfrRequestId);
        if (paymentResponse.getStatusCode() == HttpStatus.SC_OK) {
            request.setSuccess(true);
        } else {
            request.setErrorDescription(String.valueOf(paymentResponse.getStatusCode()));
        }
        request.setResponseStatus(paymentResponse.getStatus());
        if (MfrRequest.ERROR.equalsIgnoreCase(paymentResponse.getStatus())) {
            request.setErrorDescription(paymentResponse.getErrorDescription());
            RegularPayment rp = request.getRegularPayment();
            rp.setStatus(MfrRequest.ERROR);
            // Сохраняем дату неуспешного платежа по подписке
            // и увеличиваем счетчик неуспешных платежей подряд.
            BankSubscription bs = request.getBankSubscription();
            bs.setLastUnsuccessfulPaymentDate(rp.getPaymentDate());
            bs.setUnsuccessfulPaymentsCount(bs.getUnsuccessfulPaymentsCount() + 1);
            bs.setLastPaymentStatus(MfrRequest.ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean postProcessResponse(Long mfrRequestId, PaymentResponse paymentResponse) {
        MfrRequest mfrRequest = em.find(MfrRequest.class, mfrRequestId);
        mfrRequest.setResponseStatus(paymentResponse.getStatus());
        RegularPayment rp = mfrRequest.getRegularPayment();
        BankSubscription bs = rp.getBankSubscription();
        if (MfrRequest.PAYMENT_SUCCESSFUL.equalsIgnoreCase(paymentResponse.getStatus())) {
            rp.setSuccess(true);
            rp.setStatus(MfrRequest.PAYMENT_SUCCESSFUL);
            rp.setPaymentDate(paymentResponse.getDateTime());
            rp.setAuthCode(paymentResponse.getAuthCode());
            rp.setRrn(Long.valueOf(paymentResponse.getRrn()));
            // Сохраняем дату последнего успешного платежа по подписке
            // и сбрасываем счетчик неуспешных платежей подряд.
            bs.setLastSuccessfulPaymentDate(rp.getPaymentDate());
            bs.setUnsuccessfulPaymentsCount(0);
            bs.setLastPaymentStatus(MfrRequest.PAYMENT_SUCCESSFUL);
            return true;
        }
        return false;
    }
}
