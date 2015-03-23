<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.OrderPublicationItem" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.OrderPublicationItemList" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.OrderPublicationListResult" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Arrays" %>

<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");
    final String PROCESS_PARAM = "submit";
    final String SEARCH_CONDITION = "search-condition";
    final String OFFSET = "offset";
    final String PARAMS_TO_REMOVE[] = {PROCESS_PARAM};

    try {
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        URI currentUri = UriUtils
                .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
%>
<script type="text/javascript">
    var req;
    var isIE;

    function init(orderId) {
        completeField = document.getElementById("order"+orderId);
    }

    function deleteOrder(orderId) {
        init(orderId);
        var url = "pages/delete-order-publication.jsp?order_id="+orderId;
        req = initRequest();
        req.open("get", url, "true");
        req.onreadystatechange = callback;
        req.send();
    }

    function initRequest() {
        if (window.XMLHttpRequest) {
            if (navigator.userAgent.indexOf('MSIE') != -1) {
                isIE = true;
            }
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            isIE = true;
            return new ActiveXObject("Microsoft.XMLHTTP");
        }
    }
    function callback() {
        if (req.readyState == 4) {
            if (req.status == 200) {
                completeField.innerHTML = req.responseText;
            }
        }
    }
</script>
<a class="command-link" href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(UriUtils.putParam(currentUri, "page", "show-publications").toString()))%>">Поиск в каталоге библиотеки</a> |
<a class="command-link" href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(UriUtils.putParam(currentUri, "page", "show-publications-advanced").toString()))%>">Расширенный поиск</a>
<div class="output-text">Список заказов на получение литературы:</div>
<%
    ClientRoomController port=clientAuthToken.getPort();
    OrderPublicationListResult orderListResult=port.getOrderPublicationList(clientAuthToken.getContractId());
    OrderPublicationItemList orList = orderListResult.orderPublicationList;
%>

<table width="100%">
    <tr>
        <td>
            <div class="output-text">Дата</div>
        </td>
        <td>
            <div class="output-text">Автор</div>
        </td>
        <td>
            <div class="output-text">Заглавие</div>
        </td>
        <td>
            <div class="output-text">Продолжение заглавия</div>
        </td>
        <td>
            <div class="output-text">Дата издания</div>
        </td>
        <td>
            <div class="output-text">Издатель</div>
        </td>
        <td>
            <div class="output-text">Статус</div>
        </td>
        <td>
            <div class="output-text">Действия</div>
        </td>
    </tr>
    <%
        for (OrderPublicationItem orderPub : orList.getC()) {
    %>

    <tr>
        <td>
            <%=orderPub.getOrderDate()%>
        </td>
        <td>
            <%=orderPub.getPublication().getAuthor()%>
        </td>
        <td>
            <%=orderPub.getPublication().getTitle()%>
        </td>
        <td>
            <%=orderPub.getPublication().getTitle2()%>
        </td>
        <td>
            <%=orderPub.getPublication().getPublicationDate()%>
        </td>
        <td>
            <%=orderPub.getPublication().getPublisher()%>
        </td>
        <td>
            <%=orderPub.getOrderStatus()%>
        </td>
        <td>
            <% if ((orderPub.getOrderStatus() == null) || (orderPub.getOrderStatus().isEmpty())) { %>
            <div id="order<%=orderPub.getOrderId().toString()%>">
                <a href="#" onclick="deleteOrder(<%=orderPub.getOrderId()%>);">Отменить</a>
            </div>
            <% } %>
        </td>
    </tr>
    <%
            }
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        }
    %>
</table>
