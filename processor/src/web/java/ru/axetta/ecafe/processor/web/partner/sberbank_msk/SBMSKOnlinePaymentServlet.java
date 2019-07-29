/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.paystd.StdOnlinePaymentServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class SBMSKOnlinePaymentServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(SBMSKOnlinePaymentServlet.class);
    private String remoteAddressMasks = getSberbankRemoteAddressMasks();
    public static final String ONLINE_PS_ERROR = "paymentError";

    private String getSberbankRemoteAddressMasks() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.sbpayment.remoteAddress", ".*");
    }

    private Long idOfSberbankAsContragent = getIdOfSberbankAsContragent();

    private Long getIdOfSberbankAsContragent() {
        Long idOfContragentLongVal;
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        String idOfContragent = properties.getProperty("ecafe.processor.sbpayment.idOfContragent", "-1");
        try {
            idOfContragentLongVal = Long.parseLong(idOfContragent);
        } catch (Exception e) {
            idOfContragentLongVal = -1L;
        }
        return idOfContragentLongVal;
    }

    public Logger getLogger() {
        return logger;
    }

    private SBMSKOnlinePaymentRequestParser createParser() {
        return new SBMSKOnlinePaymentRequestParser();
    }

    private String getAuthenticatedRemoteAddressMasks() {
        return this.remoteAddressMasks;
    }

    private long getDefaultIdOfContragent() {
        return this.idOfSberbankAsContragent;
    }

    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            SBMSKOnlinePaymentRequestParser requestParser = createParser();
            requestParser.setRequestParams(httpRequest);
            try {
                if (!authenticate(httpRequest, requestParser)) {
                    throw new Exception("Remote address doesn't match.");
                }
                if (!requestParser.checkRequestSignature(httpRequest)) {
                    throw new Exception("Request signature wasn't verified.");
                }
            } catch (Exception e) {
                logger.error("Failed to authenticate request", e);
                logger.error("Request string", httpRequest.getAttribute("javamelody.request"));

                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INTERNAL_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            }
            OnlinePaymentProcessor.PayRequest payRequest = null;
            OnlinePaymentProcessor.PayResponse response = null;
            long contragentId = getDefaultIdOfContragent();
            try {
                payRequest = requestParser.parsePayRequest(contragentId, httpRequest);
            } catch (InvalidPayIdException e) {
                logger.error("Failed to parse request", e);
                logger.error("Request string: " + requestParser.getQueryString(httpRequest));
                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INVALID_PAY_ID_VALUE_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            } catch (InvalidDateException e){
                logger.error("Failed to parse request", e);
                logger.error("Request string: " + requestParser.getQueryString(httpRequest));
                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INVALID_DATE_VALUE_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            } catch (InvalidPaymentSumException e){
                logger.error("Failed to parse request", e);
                logger.error("Request string: " + requestParser.getQueryString(httpRequest));
                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INVALID_PAYMENT_SUM_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            }
            logger.info(String.format("New request: %s", payRequest.toString()));
            try {
                response = runtimeContext.getOnlinePaymentProcessor().processPayRequest(payRequest);
                addSBMSKInfoToResponse(response);
            } catch (Exception e) {
                logger.error("Failed to process request", e);
                logger.error("Request string: " + requestParser.getQueryString(httpRequest));
                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INTERNAL_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            }
            logger.info(String.format("Request (%s) processed: %s", requestParser == null ? "null" : requestParser.toString(),
                    response.toString()));
            try {
                requestParser.serializeResponse(response, httpResponse);
                httpRequest.setAttribute(StdOnlinePaymentServlet.ATTR_PAY_RESPONSE, response);
            } catch (Exception e) {
                logger.error("Failed to serialize response", e);
                requestParser.serializeResponseIfException(httpResponse, SBMSKPaymentsCodes.INTERNAL_ERROR);
                httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
                return;
            }
        } catch (Exception e) {
            logger.error("Failed", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpRequest.setAttribute(ONLINE_PS_ERROR, e.getMessage());
        }
    }

    private void addSBMSKInfoToResponse(OnlinePaymentProcessor.PayResponse response) {
        Contragent contragent = DAOReadExternalsService.getInstance().findContragentByClient(response.getClientId());
        response.setInn(getValueNullSafe(contragent.getInn()));
        response.setNazn(getValueNullSafe(contragent.getContragentName()));
        response.setBic(getValueNullSafe(contragent.getBic()));
        response.setRasch(getValueNullSafe(contragent.getAccount()));
    }

    private String getValueNullSafe(String value) {
        return value == null ? "" : value.trim();
    }

    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        doPost(httpRequest, httpResponse);
    }

    private boolean authenticate(HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser)
            throws Exception {
        String remoteAddress = httpRequest.getRemoteAddr();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.format("remoteAddress: %s", remoteAddress));
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String remoteAddressMasks = getAuthenticatedRemoteAddressMasks();
        if (remoteAddress.matches(remoteAddressMasks)) {
            return true;
        }
        getLogger().error(String.format("Authentication failed for: %s", remoteAddress));
        return false;
    }
}
