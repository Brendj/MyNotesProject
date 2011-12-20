<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Arrays" %>


<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.inlinecabinet_jsp");
    final String PARAMS_TO_REMOVE[] = {
            "submit", "start-date", "end-date", "org-id", "order-id", "stage", "password", "login", "contractId"};
    final String PAGE_PARAM = "page";
    final String LOGOUT_PAGE = "logout";
    final String SHOW_CARDS_PAGE = "show-cards";
    final String CHANGE_PERSONAL_INFO_PAGE = "change-personal-info";
    final String CHANGE_PASSWORD_PAGE = "change-password";
    final String SHOW_ORDERS_AND_PAYMENTS_PAGE = "show-orders-and-payments";
    final String SHOW_ORDER_DETAILS_PAGE = "show-order-details";
    final String SHOW_MENU_PAGE = "show-menu";
    final String SHOW_DIARY_PAGE = "show-diary";
    final String PREPARE_PAY_PAGE = "pay";
    final String PAY_BANK_INFO = "paybank";
    final String SHOW_JOURNAL = "show-journal";
    final String SHOW_LIBRARY = "show-library";

    if (StringUtils.isEmpty(request.getCharacterEncoding())) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            logger.error("Can\'t assign character set to request", e);
            throw new ServletException(e);
        }
    }

    if (null == ClientAuthToken.loadFrom(session)) {
%>
<jsp:include page="pages/login.jsp" />
<%
    }
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
    if (null != clientAuthToken) {
        String pageName = request.getParameter(PAGE_PARAM);
        if (pageName == null) {
            pageName = CHANGE_PERSONAL_INFO_PAGE;
        }
        if (StringUtils.equals(pageName, LOGOUT_PAGE)) {
            session.invalidate();
%>
<jsp:include page="pages/login.jsp" />
<%
} else {
    URI currentUri;
    try {
        currentUri = UriUtils
                .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
    } catch (Exception e) {
        logger.error("Error during currentUri building", e);
        throw new ServletException(e);
    }
    String[] pageNames = {
            SHOW_ORDERS_AND_PAYMENTS_PAGE, SHOW_MENU_PAGE, null, SHOW_CARDS_PAGE, SHOW_JOURNAL, SHOW_LIBRARY, PAY_BANK_INFO, PREPARE_PAY_PAGE,
            CHANGE_PERSONAL_INFO_PAGE, CHANGE_PASSWORD_PAGE, LOGOUT_PAGE};
    String[] labels = {
            "Покупки и платежи", "Узнать меню", "Дневник", "Мои карты", "Журнал событий системы доступа", "Данные книговыдачи библиотеки", "Оплата в банке", "Оплата он-лайн",
            "Личные данные", "Изменить пароль", "Выход"};
%>
<table width="100%">
    <tr>
        <%
            for (int i = 0; i != pageNames.length; ++i) {
                if (!(clientAuthToken.isSsoAuth() && (StringUtils.equals(LOGOUT_PAGE, pageNames[i]) || StringUtils
                        .equals(CHANGE_PASSWORD_PAGE, pageNames[i])))) {
                    boolean activePage = null != pageNames[i] && StringUtils.equals(pageNames[i], pageName);
        %>
        <td <%=activePage ? " class=\"active-tab\"" : ""%>>
            <%if (activePage) {%>
            <div class="active-command-link"><%=StringEscapeUtils.escapeHtml(labels[i])%>
            </div>
            <%} else if (null == pageNames[i]) {%>
            <div class="disabled-command-link"><%=labels[i]%>
            </div>
            <%
            } else {
                try {
            %>
            <a class="command-link"
               href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(UriUtils.putParam(currentUri, PAGE_PARAM, pageNames[i]).toString()))%>">
                <%=StringEscapeUtils.escapeHtml(labels[i])%>
            </a>
            <%
                    } catch (Exception e) {
                        logger.error("Failed to build URI", e);
                        throw new ServletException(e);
                    }
                }
            %>
        </td>
        <%
                }
            }
        %>
    </tr>
</table>
<jsp:include page="pages/show-balance-warn.jsp" />
<%if (StringUtils.equals(pageName, CHANGE_PERSONAL_INFO_PAGE)) {%>
<jsp:include page="pages/change-personal-info.jsp" />
<%} else if (StringUtils.equals(pageName, CHANGE_PASSWORD_PAGE)) {%>
<jsp:include page="pages/change-password.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_ORDERS_AND_PAYMENTS_PAGE)) {%>
<jsp:include page="pages/show-orders-and-payments.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_CARDS_PAGE)) {%>
<jsp:include page="pages/show-cards.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_ORDER_DETAILS_PAGE)) {%>
<jsp:include page="pages/show-order-details.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_MENU_PAGE)) {%>
<jsp:include page="pages/show-menu.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_DIARY_PAGE)) {%>
<jsp:include page="pages/show-diary.jsp" />
<%} else if (StringUtils.equals(pageName, PAY_BANK_INFO)) {%>
<jsp:include page="pages/paybank.jsp" />
<%} else if (StringUtils.equals(pageName, PREPARE_PAY_PAGE)) {%>
<jsp:include page="pages/pay.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_JOURNAL)) {%>
<jsp:include page="pages/show-journal.jsp" />
<%} else if (StringUtils.equals(pageName, SHOW_LIBRARY)) {%>
<jsp:include page="pages/show-library.jsp" />
<%
} else {
    if (StringUtils.isNotEmpty(pageName) && logger.isWarnEnabled()) {
        logger.warn(String.format("Can't find page with name \"%s\"", pageName));
    }
%>
<jsp:include page="pages/change-personal-info.jsp" />
<%
            }
        }
    }
%>