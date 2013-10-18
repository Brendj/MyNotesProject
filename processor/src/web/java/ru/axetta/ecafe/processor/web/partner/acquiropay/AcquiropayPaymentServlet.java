/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay;

//import ru.axetta.ecafe.processor.core.RuntimeContext;
//import ru.axetta.ecafe.processor.core.persistence.clientBalanceRefill.MfrRequest;
//import ru.axetta.ecafe.processor.core.service.clientBalanceRefill.ClientBalanceRefillService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 18:56
 * Сервлет принимает callback'и от процессинга Банка Москвы на операции,
 * связанные с автопополнением баланса клиента с банковской карты.
 */

public class AcquiropayPaymentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AcquiropayPaymentServlet.class);

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
        //Long mfrRequestId;
        //int error = 0;
        //ClientBalanceRefillService service = RuntimeContext.getAppContext().getBean(ClientBalanceRefillService.class);
        //try {
        //    try {
        //        mfrRequestId = Long.valueOf(req.getParameter("cf"));
        //    } catch (NumberFormatException ex) {
        //        logger.error("Failed to read request parameter cf");
        //        error = HttpServletResponse.SC_BAD_REQUEST;
        //        return;
        //    }
        //    MfrRequest mfrRequest = service.findMfrRequest(mfrRequestId);
        //    if (mfrRequest == null) {
        //        logger.error("Request with ID = {} not found!", mfrRequestId);
        //        error = HttpServletResponse.SC_BAD_REQUEST;
        //        return;
        //    }
        //    if (!checkSign(req)) {
        //        logger.error("Sign is not valid! Request ID = {}", mfrRequestId);
        //        error = HttpServletResponse.SC_BAD_REQUEST;
        //        return;
        //    }
        //    logger.info("Callback request <status> value: {}", StringUtils.trim(req.getParameter("status")));
        //    if (mfrRequest.getRequestType() == MfrRequest.REQUEST_TYPE_ACTIVATION) {
        //        service.processSubscriptionActivated(mfrRequest, req);
        //    } else if (mfrRequest.getRequestType() == MfrRequest.REQUEST_TYPE_WRITE_OFF) {
        //        service.processPaymentCallback(mfrRequest, req);
        //    }
        //} catch (Exception ex) {
        //    logger.error(ex.getMessage());
        //    error = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        //} finally {
        //    processResponse(resp, error);
        //    logger.info("Acquiropay callback processing is over.");
        //}
        checkSign(req);
    }

    private void processResponse(HttpServletResponse response, int errorCode) {
        try {
            if (errorCode != 0) {
                response.sendError(errorCode);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean checkSign(HttpServletRequest req) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("payment_id", StringUtils.trim(req.getParameter("payment_id")));
        fields.put("status", StringUtils.trim(req.getParameter("status")));
        fields.put("cf", StringUtils.trimToEmpty(req.getParameter("cf")));
        fields.put("cf2", StringUtils.trimToEmpty(req.getParameter("cf2")));
        fields.put("cf3", StringUtils.trimToEmpty(req.getParameter("cf3")));
        String validSign = StringUtils.trim(req.getParameter("sign"));
        for(Map.Entry<String, String> e : fields.entrySet()) {
            logger.info(e.getKey() + ": {}", e.getValue());
        }
        return true;
        //return ClientBalanceRefillService.checkSign(fields, validSign);
    }
}
