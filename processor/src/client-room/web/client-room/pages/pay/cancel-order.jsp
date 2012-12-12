<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.IdResult" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.pay.cancel-order_jsp");
    final String ID_OF_CLIENT_PAYMENT_ORDER_PARAM = "order-id";

    ClientRoomControllerWSService service = new ClientRoomControllerWSService();
    ClientRoomController port
            = service.getClientRoomControllerWSPort();
    ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");

    try {
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

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
                        ClientPaymentOrder.ORDER_STATUS_CANCELLED);
            } catch (Exception e) {
                logger.error("Failed to change orderStatus", e);
            }
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>
<jsp:include page="create-order.jsp" />