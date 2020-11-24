/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import org.springframework.context.annotation.DependsOn;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientPaymentsDao;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.httpclient.HttpStatus;
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
 * Time: 11:15
 */

@Service(IRequestOperation.REGULAR_PAYMENT)
@DependsOn("runtimeContext")
public class RegularPaymentRequest implements IRequestOperation {

    private static Logger logger = LoggerFactory.getLogger(RegularPaymentRequest.class);


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
        params.put("clientId", "" + request.getClient().getIdOfClient());
        params.put("contractId", "" + request.getClient().getContractId());
        params.put("paymentAmount", "" + request.getBankSubscription().getPaymentAmount());
        params.put("regular_payment_id", "" +  request.getRegularPayment().getIdOfPayment());
        params.put("bankSubscription_id", "" +  request.getBankSubscription().getIdOfSubscription());
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
    public boolean postProcessResponse(Long mfrRequestId, Long subscriptionId, PaymentResponse paymentResponse) {
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
            updateLastClientPayment(mfrRequest);
            return true;
        }
        return false;
    }

    //Ищет последний, за 5 минут, запрос на платеж от клиента и ставит ему отметку "Автоплатеж с банковской карты"
    @Transactional
    public static void updateLastClientPayment(MfrRequest mfrRequest) {
        logger.warn("Запущена модификация mfrRequest: " + mfrRequest.getIdOfRequest());
        try {
            ClientPaymentsDao clientPaymentsDao = RuntimeContext.getAppContext().getBean(ClientPaymentsDao.class);
            Long clientPayment = clientPaymentsDao.findAllIn5Minutes(mfrRequest.getClient().getIdOfClient());
            if (clientPayment == null){
                //logger.warn("Не найден платеж клиента : " + (mfrRequest.getClient().getIdOfClient()) ); //убрать после 15,01,15
                return;
            }
            clientPaymentsDao.updatePaymentMethod(clientPayment,
                    ClientPayment.AUTO_PAYMENT_METHOD);
        } catch (Exception e) {
            logger.error("Проблема при модификации платежа: "+ mfrRequest.getIdOfRequest(), e);
        }

        logger.warn("Успешно модифицирован ClientPayment: " + mfrRequest.getIdOfRequest());

    }
}
