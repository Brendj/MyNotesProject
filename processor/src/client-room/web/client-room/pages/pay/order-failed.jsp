<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.IdResult" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.pay.order-failed_jsp");
    final String STAGE_PARAM = "stage";
    final String ID_OF_CLIENT_PAYMENT_ORDER_PARAM = "order-id";
    try {

        URI currentUri;
        try {
            currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
        } catch (Exception e) {
            logger.error("Error during currentUri building", e);
            throw new ServletException(e);
        }

        URI backURI;
        try {
            backURI = UriUtils.removeParam(currentUri, ID_OF_CLIENT_PAYMENT_ORDER_PARAM);
            backURI = UriUtils.removeParam(backURI, STAGE_PARAM);
        } catch (Exception e) {
            logger.error("Error during backUri building", e);
            throw new ServletException(e);
        }

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

        ClientRoomController port=clientAuthToken.getPort();

        boolean haveDataToProcess = StringUtils.isNotEmpty(request.getParameter(ID_OF_CLIENT_PAYMENT_ORDER_PARAM));
        boolean dataToProcessVerified = false;

        Long idOfClientPaymentOrder = null;
        if (haveDataToProcess) {
            try {
                idOfClientPaymentOrder = Long.valueOf(request.getParameter(ID_OF_CLIENT_PAYMENT_ORDER_PARAM));
                dataToProcessVerified = true;
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to read data", e);
                }
            }
        }

        if (haveDataToProcess && dataToProcessVerified) {
            Long idOfClient = null;

            IdResult idOfClientResult=port.getIdOfClient(clientAuthToken.getContractId());
            idOfClient=idOfClientResult.getId();

            //ClientPaymentOrderProcessor clientPaymentOrderProcessor = runtimeContext.getClientPaymentOrderProcessor();
            try {
               /* clientPaymentOrderProcessor.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder,
                        ClientPaymentOrder.ORDER_STATUS_FAILED);*/

                port.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder,
                        ClientPaymentOrder.ORDER_STATUS_FAILED);
            } catch (Exception e) {
                logger.error("Failed to change orderStatus", e);
            }
        }
%>
<div class="output-text">Платежной системе не удалось обработать запрос на перечисление средств.</div>
<a class="output-text" href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(backURI.toString()))%>">Вернуться к
    странице создания
    запроса на оплату</a>
<%
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>
