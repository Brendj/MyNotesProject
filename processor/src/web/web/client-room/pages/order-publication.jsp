<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.OrderPublicationResult" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");
    final String PUBLICATION_ID = "publication_id";
    final String ORG_HOLDER_ID = "org_holder_id";

    try {
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        Long publicationId = 0L;
        Long orderId = 0L;
        Long orgHolderId = 0L;
        logger.debug(request.getParameter(PUBLICATION_ID));
        logger.debug(request.getParameter(ORG_HOLDER_ID));
        publicationId = Long.parseLong(StringUtils.defaultString(request.getParameter(PUBLICATION_ID)));
        orgHolderId = Long.parseLong(StringUtils.defaultString(request.getParameter(ORG_HOLDER_ID)));
        ClientRoomController port = clientAuthToken.getPort();
        OrderPublicationResult orderResult=port.orderPublication(clientAuthToken.getContractId(), publicationId, orgHolderId);
        orderId = orderResult.id;
        long resultCode = orderResult.resultCode;
        if (resultCode == 0L) {
%>
<div id="order<%=orderId%>">Книга забронирована. <a href="#" onclick="deleteOrder(<%=orderId%>);">Отменить заказ</a>
</div>
<%
    }
    else {
%>
Не удалось забронировать книгу
<%
    }
        }
    catch (Exception e) {
%>
Не удалось забронировать книгу
<%
        if (logger.isDebugEnabled()) {
            logger.debug("Failed to read data", e);
        }
    }
%>
