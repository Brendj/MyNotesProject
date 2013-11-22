<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.balance-auto-refill.deactivate_jsp");
    String stage = request.getParameter("stage");
    URI currentUri;
    try {
        currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new ServletException(ex.getMessage());
    }
    Long bsId = Long.valueOf(request.getParameter("bs"));
    if (StringUtils.isEmpty(stage)) {
%>

<table>
    <tr>
        <td colspan="2"><span class="output-text">Вы действительно хотите отключить данную подписку?</span></td>
    </tr>
    <tr>
        <td align="right">
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form">
                <input type="hidden" name="command" value="deactivate" />
                <input type="hidden" name="stage" value="process" />
                <input type="hidden" name="bs" value="<%=bsId%>" />
                <input class="command-button" type="submit" value="Да" />
            </form>
        </td>
        <td align="left">
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form">
                <input type="hidden" name="command" value="edit" />
                <input type="hidden" name="bs" value="<%=bsId%>" />
                <input class="command-button" type="submit" value="Нет" />
            </form>
        </td>
    </tr>
</table>

<%
    } else if (stage.equals("process")) {
        RegularPaymentSubscriptionService cs = RuntimeContext.getAppContext()
                .getBean(RegularPaymentSubscriptionService.class);
        boolean result = false;
        try {
            result = cs.deactivateSubscription(bsId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        if (result) {
%>

<div class="output-text">Подписка отключена успешно.</div>

<%
        } else {
%>

<div class="output-text"><span class="red">Произошла ошибка. Попробуйте отключить подписку позже.</span></div>

<%
        }
%>

<div class="output-text"><a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>">Вернуться
    к списку подписок</a></div>

<%
    }
%>