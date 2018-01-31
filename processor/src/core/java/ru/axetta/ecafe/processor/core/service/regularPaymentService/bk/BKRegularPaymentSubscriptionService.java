/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.bk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.BankSubscriptionDao;
import ru.axetta.ecafe.processor.core.persistence.dao.regularpayments.RegularPaymentsRepository;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.DuplicatePaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.IRequestOperation;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.PaymentResponse;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentRequest;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.NotFoundPaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.AsynchronousPaymentRequest;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.AsynchronousPaymentResponse;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCard;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCardService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URL;
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

    @Autowired
    private RegularPaymentsRepository regularPaymentsRepository;

    @Autowired
    private BankSubscriptionDao bankSubscriptionDao;

    @Autowired
    private RegularPaymentRequest regularPaymentRequest;

    public void sendRequestOnNotifyAction(long contractId){
        //Если у чела баланс меньше тресхолда сенд реквест

        logger.warn("sendRequestOnNotifyAction enter contractId: " + contractId);
        Long subscriptionId = bankSubscriptionDao.findSubscription(contractId);
        logger.warn("Found subscription subscriptionId: " + subscriptionId);
        if(subscriptionId != null){
            sendSubscriptionRequest(subscriptionId, regularPaymentRequest);
            logger.warn("Sending request from sendRequestOnNotifyAction contractId: " + contractId + " | subscriptionId:" +subscriptionId);

        }
    }

    @Override
    protected boolean notifyAboutExpiredSubscription() {
        return false;
    }

    private PaymentResponse sendSubscriptionRequest(Long subscriptionId, IRequestOperation operation) {
        MfrRequest mfrRequest = operation.createRequest(subscriptionId);
        Map<String, String> params = operation.getRequestParams(mfrRequest);
        PaymentResponse paymentResponse = sendRequest(mfrRequest.getRequestUrl(), params);
        //operation.processResponse(mfrRequest.getIdOfRequest(), paymentResponse);
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
        cards.setAmount(BigInteger.valueOf(Long.valueOf(params.get("paymentAmount"))));
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
        logger.warn(
                "Request to BK init:" + params.get("contractId") + ", actionId: " + params.get("regular_payment_id"));
        AsynchronousPaymentResponse asynchronousPaymentResponse;
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            AsynchronousPaymentRequest asynchronousPaymentRequest = prepareForRequest(params);
            logger.info("Request to BK:" +  params.get("contractId") + ", actionId: " + params.get("regular_payment_id"));
            String wsdlLocationString = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.autopayment.bk.wsdllocation");
            SchoolCardService schoolCardService;
            if(wsdlLocationString == null) {
                schoolCardService  =  new SchoolCardService();
            }else{
                URL wsdlLocationURL = new URL(wsdlLocationString);
                schoolCardService =  new SchoolCardService(wsdlLocationURL);
            }
            SchoolCard sc = schoolCardService.getSchoolCardPort();
            Date date = new Date();
            asynchronousPaymentResponse = sc.asynchronousPayment(asynchronousPaymentRequest);
            if(asynchronousPaymentResponse != null && asynchronousPaymentResponse.getResponseex() != null){
                if(asynchronousPaymentResponse.getResponseex().getCards() != null ){
                    //todo статус отправлен
                    for(AsynchronousPaymentResponse.Responseex.Cards card : asynchronousPaymentResponse.getResponseex().getCards()){
                        int statusCode = card.getErrorcode().intValue();
                        paymentResponse.setStatusCode(statusCode);
                        if (statusCode == 0) {
                            regularPaymentsRepository.updateRegularPayment(card.getIdaction(), MfrRequest.PAYMENT_RECEIVED_BY_BK, null,null);
                            bankSubscriptionDao.updateLastSuccessfulPayment(
                                    Long.valueOf(params.get("bankSubscription_id")), date);
                        }else{
                            regularPaymentsRepository.updateRegularPayment(card.getIdaction(), "KO" + statusCode, null, null);
                            bankSubscriptionDao.updateLastUnsuccessfulPayment(Long.valueOf(params.get("bankSubscription_id")), date);
                        }
                        logger.info("Request to BK statusCode:" +  card.getErrorcode());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            logger.info("Response from BK: {}", paymentResponse.toString());
        }
        return paymentResponse;
    }


    public void finalizeRegularPayment(Long idaction, String paymentSuccessful, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        RegularPayment regularPayment = regularPaymentsRepository
                .finalizeRegularPayment(idaction, paymentSuccessful, realAmount, ip);
        bankSubscriptionDao.updateLastSuccessfulPayment(regularPayment.getBankSubscription().getIdOfSubscription(),
                new Date());

    }

    public RegularPayment findPayment(Long idaction) {
        return  regularPaymentsRepository.findPayment(idaction);
    }

    public void updateRegularPayment(Long idaction, String paymentSuccessful, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        regularPaymentsRepository.updateRegularPayment(idaction,paymentSuccessful,realAmount,ip);
    }
}
