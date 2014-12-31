/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.notify;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.DuplicatePaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.bk.BKRegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.NotFoundPaymentException;
import ru.axetta.ecafe.processor.web.partner.autopayments.AutoPaymentResultRequest;
import ru.axetta.ecafe.processor.web.partner.autopayments.AutoPaymentResultResponse;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ResultConst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.List;

/**
 * User: shamil
 * Date: 20.11.14
 * Time: 17:11
 */
@WebService
public class NotifyControllerWS extends HttpServlet implements NotifyController {

    private static final Logger logger = LoggerFactory.getLogger(NotifyControllerWS.class);

    @Override
    public NotifyResult notify(@WebParam(name = "accountN") long accountNumber,
            @WebParam(name = "eventCode") int eventCode) {
        logger.warn("NotifyWS notify: " + accountNumber + " | " + eventCode);
        NotifyResult result = new NotifyResult();
        result.resultCode = ResultConst.CODE_OK;
        result.description = ResultConst.DESCR_OK;

        rpService.senRequestOnNotifyAction(accountNumber);

        return result;
    }


    @Resource
    javax.xml.ws.WebServiceContext wsContext;

    @Autowired
    @Qualifier("bk_regularPaymentSubscriptionService")
    private BKRegularPaymentSubscriptionService rpService;


    public List<AutoPaymentResultResponse> AsynchronousPaymentResponse(
            @WebParam(name = "opers") List<AutoPaymentResultRequest> autoPaymentResultRequestList) {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        logger.info("Starting process BK callback from {}", req.getRemoteAddr());
        List<AutoPaymentResultResponse> result = new ArrayList<AutoPaymentResultResponse>();

        try {
            for (AutoPaymentResultRequest autoPaymentResultRequest : autoPaymentResultRequestList) {
                logger.warn("NotifyWS AsynchronousPaymentResponse: " + autoPaymentResultRequest.getErrorCode() + " | "
                        + autoPaymentResultRequest.getIdaction() + " | " + autoPaymentResultRequest.getRealAmount());
                result.add(handleResultRequest(autoPaymentResultRequest, req.getRemoteAddr()));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        logger.info("Stop process BK callback from {}", req.getRemoteAddr());

        return result;
    }


    private AutoPaymentResultResponse handleResultRequest(AutoPaymentResultRequest autoPaymentResultRequest,
            String ip) {
        AutoPaymentResultResponse result = new AutoPaymentResultResponse(autoPaymentResultRequest);
        RegularPayment regularPayment;
        if (rpService == null) {
            rpService = (BKRegularPaymentSubscriptionService) RuntimeContext.getAppContext()
                    .getBean("bk_regularPaymentSubscriptionService");
        }
        if (autoPaymentResultRequest.getIdaction() != null) {
            if (autoPaymentResultRequest.getErrorCode() == 0) {
                try {
                    RegularPayment payment = rpService.findPayment(autoPaymentResultRequest.getIdaction());
                    if (payment == null) {
                        throw new NotFoundPaymentException();
                    } else if (payment.isSuccess()) {
                        throw new DuplicatePaymentException();
                    }
                    OnlinePaymentProcessor.PayResponse payResponse = sendRequestToPayment(
                            payment.getClient().getIdOfClient(), payment.getClient().getContractId(),
                            "" + autoPaymentResultRequest.getIdaction(), autoPaymentResultRequest.getRealAmount());

                    if (payResponse.getResultCode() != 0) {
                        result.setErrorCode(payResponse.getResultCode());
                        result.setErrorDesc(payResponse.getResultDescription());
                    } else {
                        rpService.finalizeRegularPayment(autoPaymentResultRequest.getIdaction(),
                                MfrRequest.PAYMENT_SUCCESSFUL, autoPaymentResultRequest.getRealAmount(), ip);
                        result.setErrorCode(0);
                        result.setErrorDesc("Ok.");
                    }
                } catch (DuplicatePaymentException e) {
                    logger.warn(e.getMessage());
                    result.setErrorCode(160);
                    result.setErrorDesc(e.getMessage());
                } catch (NotFoundPaymentException e) {
                    logger.warn(e.getMessage());
                    result.setErrorCode(9999);
                    result.setErrorDesc(e.getMessage());
                }
                System.out.print("d");
            } else {
                //todo handle exception
                result.setErrorCode(9998);
                try {
                    rpService
                            .updateRegularPayment(autoPaymentResultRequest.getIdaction(), MfrRequest.PAYMENT_SUCCESSFUL,
                                    autoPaymentResultRequest.getRealAmount(), ip);
                } catch (DuplicatePaymentException e) {
                    logger.warn(e.getMessage());
                    result.setErrorCode(160);
                    result.setErrorDesc(e.getMessage());
                } catch (NotFoundPaymentException e) {
                    logger.warn(e.getMessage());
                    result.setErrorCode(9999);
                    result.setErrorDesc(e.getMessage());
                }
            }
        } else {
            //todo empty actionID
            result.setErrorCode(9997);
        }
        return result;
    }

    /*
    * Отправка запроса на оплату внутри ИСПП
    * */
    private OnlinePaymentProcessor.PayResponse sendRequestToPayment(Long clientId, Long contractId, String paymentId,
            long realAmount) throws DuplicatePaymentException {
        Long contragentId = Long.valueOf(
                (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.autopayment.bk.contragentId"));

        OnlinePaymentProcessor.PayRequest payRequest = null;
        try {
            payRequest = new OnlinePaymentProcessor.PayRequest(1, false, contragentId, null,
                    ClientPayment.AUTO_PAYMENT_METHOD, contractId, paymentId, null, realAmount, false);
        } catch (Exception e) {
            e.printStackTrace(); //todo надо обработать ошибку
        }

        OnlinePaymentProcessor.PayResponse payResponse = RuntimeContext.getInstance().getOnlinePaymentProcessor()
                .processPayRequest(payRequest);
        if (payResponse.getResultCode() == 140) {
            throw new DuplicatePaymentException();
        }
        return payResponse;
    }
}
