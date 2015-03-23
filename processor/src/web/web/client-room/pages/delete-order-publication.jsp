<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.OrderPublicationDeleteResult" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");
    final String ORDER_ID = "order_id";

    try {
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        Long orderId = 0L;
        orderId = Long.parseLong(StringUtils.defaultString(request.getParameter(ORDER_ID)));
        ClientRoomController port = clientAuthToken.getPort();
        OrderPublicationDeleteResult orderResult=port.deleteOrderPublication(clientAuthToken.getContractId(), orderId);
        orderId = orderResult.resultCode;
        if (orderId == 0L) {
%>
Заказ отменен
<%
    }
    else {
%>
Не удалось отменить заказ
<%
    }
}
catch (Exception e) {
%>
Не удалось отменить заказ
<%
        if (logger.isDebugEnabled()) {
            logger.debug("Failed to read data", e);
        }
    }
%>