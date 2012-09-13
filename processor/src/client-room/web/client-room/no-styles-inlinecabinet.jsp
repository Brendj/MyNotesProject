<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
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
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%--<%@ page import="RuntimeContext" %>--%>


<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.client-room.web.client-room.inlinecabinet_jsp");
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
    final String RECOVER_PARAM = "recover";
    final String PASSWORD_PARAM = "password";
    Boolean recoverPassword = Boolean.FALSE;

    if (StringUtils.isEmpty(request.getCharacterEncoding())) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            logger.error("Can\'t assign character set to request", e);
            throw new ServletException(e);
        }
    }

    if (null == ClientAuthToken.loadFrom(session)) {
        /*  */
        String pageName = request.getParameter(PAGE_PARAM);
        if(pageName == null || pageName.equalsIgnoreCase("")){
        %><jsp:include page="pages/login.jsp" /><%
        } else {
            if (StringUtils.equals(PASSWORD_PARAM, pageName)) {
                %>
                <jsp:include page="pages/password.jsp" />
                <%
            }
            if (StringUtils.equals(RECOVER_PARAM, pageName)) {
                %><jsp:include page="pages/recover.jsp" /><%
            }
        }

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
   String hidePagesAttr=(String)request.getAttribute("hidePages");
    if (hidePagesAttr==null) hidePagesAttr="";
   ClientRoomController port= clientAuthToken.getPort() ;
   // hidePagesAttr+=","+RuntimeContext.getInstance().getPropertiesValue(RuntimeContext.PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM, "");
    hidePagesAttr+=","+port.getHiddenPages().getHiddenPages();

    String[] pageNames = {
            SHOW_ORDERS_AND_PAYMENTS_PAGE, SHOW_MENU_PAGE, null, SHOW_JOURNAL,  SHOW_LIBRARY, PAY_BANK_INFO, PREPARE_PAY_PAGE,
            SHOW_CARDS_PAGE,CHANGE_PERSONAL_INFO_PAGE, CHANGE_PASSWORD_PAGE, LOGOUT_PAGE};
    String[] labels = {
            "Покупки и платежи", "Узнать меню",  "Дневник", "Посещение школы", "Данные книговыдачи библиотеки", "Оплата в банке", "Оплата он-лайн",
            "Мои карты", "Личные данные", "Изменить пароль", "Выход"};
%>
<table width="100%">
    <tr>
        <% 
            for (int i = 0; i != pageNames.length; ++i) {
                if (pageNames[i]==null || hidePagesAttr.indexOf(pageNames[i])!=-1) continue;
                if (!(clientAuthToken.isSsoAuth() && (StringUtils.equals(LOGOUT_PAGE, pageNames[i]) || StringUtils
                        .equals(CHANGE_PASSWORD_PAGE, pageNames[i])))) {
                    boolean activePage = null != pageNames[i] && StringUtils.equals(pageNames[i], pageName);
        %>
        <td class="tab <%=activePage ? "active-tab" : "inactive-tab"%> ">
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
<%--<jsp:include page="pages/show-diary.jsp" />--%>
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