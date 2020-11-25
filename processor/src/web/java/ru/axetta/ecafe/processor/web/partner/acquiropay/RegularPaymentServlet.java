/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.PaymentResponse;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 18:56
 * Сервлет принимает callback'и от процессинга Банка Москвы на операции,
 * связанные с автопополнением баланса клиента с банковской карты.
 */

@WebServlet(
        name = "RegularPaymentServlet",
        description = "RegularPaymentServlet",
        urlPatterns = {"/regpay-acquiropay"}
)
public class RegularPaymentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegularPaymentServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCallback(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCallback(req, resp);
    }

    private void processCallback(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Starting process Acquiropay callback from {}", req.getRemoteAddr());
        Long mfrRequestId;
        int error = 0;
        RegularPaymentSubscriptionService service = RuntimeContext.getInstance().getRegularPaymentSubscriptionService();
        try {
            PaymentResponse callback = new PaymentResponse();
            service.fillFromRequest(callback, req);
            logger.info("Callback request body: {}", callback.toString());
            try {
                mfrRequestId = Long.valueOf(callback.getCf());
            } catch (NumberFormatException ex) {
                logger.error("Failed to read request.");
                error = HttpServletResponse.SC_BAD_REQUEST;
                return;
            }
            MfrRequest mfrRequest = service.findMfrRequest(mfrRequestId);
            if (mfrRequest == null) {
                logger.error("Request with ID = {} not found!", mfrRequestId);
                error = HttpServletResponse.SC_BAD_REQUEST;
                return;
            }
            if (!service.checkResponseSign(callback)) {
                logger.error("Sign is not valid! Request ID = {}", mfrRequestId);
                error = HttpServletResponse.SC_BAD_REQUEST;
                return;
            }
            logger.info("Callback request <status> value: {}", callback.getStatus());
            if (mfrRequest.getRequestType() == MfrRequest.REQUEST_TYPE_ACTIVATION) {
                service.processSubscriptionActivated(mfrRequest.getIdOfRequest(), callback);
            } else if (mfrRequest.getRequestType() == MfrRequest.REQUEST_TYPE_PAYMENT) {
                service.processRegularPayment(mfrRequest.getIdOfRequest(), callback);
            }
        } catch (Exception ex) {
            logger.error("Error in RegularPaymentServlet", ex);
            error = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } finally {
            processResponse(resp, error);
            logger.info("Acquiropay callback processing is over.");
        }
    }

    private void processResponse(HttpServletResponse response, int errorCode) {
        try {
            if (errorCode != 0) {
                response.sendError(errorCode);
            }
        } catch (IOException e) {
            logger.error("Error in process response: ", e);
        }
    }
}
