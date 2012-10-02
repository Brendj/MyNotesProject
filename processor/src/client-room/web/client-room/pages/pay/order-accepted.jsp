<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%--<%@ page import="ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder" %>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--<%@ page import="org.hibernate.Criteria" %>--%>
<%--<%@ page import="org.hibernate.Session" %>--%>
<%--<%@ page import="org.hibernate.criterion.Restrictions" %>--%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.IdResult" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.pay.order-accepted_jsp");
    final String STAGE_PARAM = "stage";
    final String ID_OF_CLIENT_PAYMENT_ORDER_PARAM = "order-id";
    //RuntimeContext runtimeContext = null;


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


            try {

                port.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder,
                        ClientPaymentOrder.ORDER_STATUS_ACCEPTED);
            } catch (Exception e) {
                logger.error("Failed to change orderStatus", e);
            }
        }
%>
<div class="output-text">
    Ваш запрос на перечисление средств принят.
    Начисление на Вашу карту будет призведено сразу после получения уведомления от платежной системы.
    Это может занять несколько минут.
</div>
<a class="output-text" href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(backURI.toString()))%>">Вернуться к
    странице запроса на
    оплату</a>
<%
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>
