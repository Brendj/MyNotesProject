/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.bk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.DuplicatePaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.IRequestOperation;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.PaymentResponse;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.NotFoundPaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.AsynchronousPaymentRequest;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.AsynchronousPaymentResponse;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCard;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCardService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 13:17
 */

@Service("bk_regularPaymentSubscriptionService")
public class BKRegularPaymentSubscriptionService extends ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService {
    private static Logger logger = LoggerFactory.getLogger(BKRegularPaymentSubscriptionService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;


    public RegularPayment findPayment(Long actionId){
        return (RegularPayment) em.createQuery("from RegularPayment p left join fetch p.client where p.idOfPayment = :idOfPayment ")
                .setParameter("idOfPayment", actionId)
                .getSingleResult();
    }

    private PaymentResponse sendSubscriptionRequest(Long subscriptionId, IRequestOperation operation) {
        MfrRequest mfrRequest = operation.createRequest(subscriptionId);
        Map<String, String> params = operation.getRequestParams(mfrRequest);
        PaymentResponse paymentResponse = sendRequest(mfrRequest.getRequestUrl(), params);
        operation.processResponse(mfrRequest.getIdOfRequest(), paymentResponse);
        return paymentResponse;
    }

    @Override
    public boolean deactivateSubscription(Long subscriptionId) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus(MfrRequest.SUBSCRIPTION_DEACTIVATED);
        paymentResponse.setDateTime(new Date());
        return getSubscriptionDeleteRequest().postProcessResponse(null, subscriptionId, paymentResponse);
    }

    public AsynchronousPaymentRequest prepareForRequest( Map<String, String> params) {
        AsynchronousPaymentRequest.Requestex.Cards cards = new AsynchronousPaymentRequest.Requestex.Cards();
        cards.setAction(BigInteger.valueOf(0));
        cards.setAmount(BigInteger.valueOf(Long.valueOf(
                (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.autopayment.bk.amount"))));
        cards.setIdaction(Long.valueOf(params.get("regular_payment_id")));
        cards.setContractid(Long.valueOf(params.get("contractId")));
        AsynchronousPaymentRequest.Requestex requestex = new AsynchronousPaymentRequest.Requestex();
        requestex.getCards().add(cards);
        AsynchronousPaymentRequest request = new AsynchronousPaymentRequest();
        request.setRequestex(requestex);

        return request;
    }

    @Override
    protected PaymentResponse sendRequest(String uri, Map<String, String> params) {
        AsynchronousPaymentResponse asynchronousPaymentResponse;
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            AsynchronousPaymentRequest asynchronousPaymentRequest = prepareForRequest(params);

            SchoolCardService schoolCardService  =  new SchoolCardService();
            SchoolCard sc = schoolCardService.getSchoolCardPort();
            asynchronousPaymentResponse = sc.asynchronousPayment(asynchronousPaymentRequest);
            if(asynchronousPaymentResponse != null && asynchronousPaymentResponse.getResponseex() != null){
                if(asynchronousPaymentResponse.getResponseex().getCards() != null ){
                    //todo статус отправлен
                    for(AsynchronousPaymentResponse.Responseex.Cards card : asynchronousPaymentResponse.getResponseex().getCards()){
                        int statusCode = card.getErrorcode().intValue();
                        paymentResponse.setStatusCode(statusCode);
                        if (statusCode == 0) {
                            updateRegularPayment(card.getIdaction(), "ReceivedBYBK", null, null);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            logger.info("Response from BK: {}", paymentResponse.toString());
        }
        return paymentResponse;
    }

    @Transactional
    public void updateRegularPayment(long idofpayment, String status, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        RegularPayment payment = em.find(RegularPayment.class, idofpayment);
        if (payment != null) {
            if ((!payment.isSuccess())) {
                payment.setStatus(status);

                if (realAmount !=null){
                    payment.setPaymentAmount(realAmount);
                }
                //payment.setAuthCode(ip);
                em.persist(payment);
            } else {
                throw new DuplicatePaymentException();
            }
        } else {
            throw new NotFoundPaymentException();
        }
    }

    @Transactional
    public void finalizeRegularPayment (long idofpayment, String status, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        RegularPayment payment = em.find(RegularPayment.class, idofpayment);
        if (payment != null) {
            if ((!payment.isSuccess())) {
                payment.setStatus(status);
                payment.setSuccess(true);

                if (realAmount !=null){
                    payment.setPaymentAmount(realAmount);
                }
                //payment.setAuthCode(ip);
                em.persist(payment);
            } else {
                throw new DuplicatePaymentException();
            }
        } else {
            throw new NotFoundPaymentException();
        }
    }
}
