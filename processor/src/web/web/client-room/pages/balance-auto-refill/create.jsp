<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest" %>
<%@ page import="ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Map" %>

<%
    final Logger logger = LoggerFactory
        .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.balance-auto-refill.create_jsp");
    String stage = request.getParameter("stage");
    URI currentUri;
    try {
        currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new ServletException(ex.getMessage());
    }
    if (StringUtils.isEmpty(stage)) {
%>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <input type="hidden" name="command" value="create" />
    <input type="hidden" name="stage" value="process"/>
    <table class="borderless-grid">
        <tr>
            <td>
                <div class="output-text">Сумма пополнения:</div>
            </td>
            <td>
                <select size="1" class="input-text" name="refillAmount" id="refillAmount" required>
                <%
                    String values = RuntimeContext.getInstance()
                            .getOptionValueString(Option.OPTION_AUTOREFILL_VALUES);
                    String[] list = StringUtils.split(values, ";");
                    for (String value : list) {
                %>
                <option value="<%=value%>"><%=CurrencyStringUtils.copecksToRubles(Long.parseLong(value))%>
                </option>
                <%}%>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Пороговое значение баланса:</div>
            </td>
            <td>
                <select size="1" class="input-text" name="thresholdAmount" id="thresholdAmount" required>
                    <%
                        values = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_THRESHOLD_VALUES);
                        list = StringUtils.split(values, ";");
                        for (String value : list) {
                    %>
                    <option value="<%=value%>"><%=CurrencyStringUtils.copecksToRubles(Long.parseLong(value))%>
                    </option>
                    <%}%>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Срок действия (в месяцах):</div>
            </td>
            <td>
                <select size="1" class="input-text" name="activePeriod" id="activePeriod" required>
                    <%for (int i = 1; i <= 12; i++) {%>
                    <option value="<%=i%>"><%=i%></option>
                    <%}%>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <input class="command-button" type="submit" name="create" value="Подключить подписку" />
            </td>
        </tr>
    </table>
</form>

<table class="borderless-grid">
    <tr>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-grid">
                <input type="hidden" name="command" value="show" />
                <input class="command-button" type="submit" value="Вернуться" />
            </form>
        </td>
    </tr>
</table>

<%
    } else if (stage.equals("process")) {
        ClientAuthToken ct = ClientAuthToken.loadFrom(session);
        Long refillAmount;
        Long thresholdAmount;
        int period;
        try {
            refillAmount = Long.valueOf(request.getParameter("refillAmount"));
            thresholdAmount = Long.valueOf(request.getParameter("thresholdAmount"));
            period = Integer.parseInt(request.getParameter("activePeriod"));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ServletException(ex.getMessage());
        }
        RegularPaymentSubscriptionService cs = RuntimeContext.getInstance().getRegularPaymentSubscriptionService();
        MfrRequest mfrRequest = cs.createRequestForSubscriptionReg(ct.getContractId(), refillAmount, thresholdAmount, period);
        Map<String, String> params = cs.getParamsForRegRequest(mfrRequest);
%>

<form action="<%=params.get("action")%>" method="post" enctype="application/x-www-form-urlencoded"
      class="borderless-form">
    <input type="hidden" name="product_id" value="<%=params.get("product_id")%>" />
    <input type="hidden" name="product_name" value="<%=params.get("product_name")%>" />
    <input type="hidden" name="amount" value="<%=params.get("amount")%>" />
    <input type="hidden" name="cf" value="<%=params.get("cf")%>" />
    <input type="hidden" name="cf2" value="<%=params.get("cf2")%>" />
    <input type="hidden" name="cf3" value="<%=params.get("cf3")%>" />
    <input type="hidden" name="token" value="<%=params.get("token")%>" />
</form>
<%--Скрипт автосабмита формы --%>
<script type="text/javascript">
    window.onload = function () {
        document.forms[0].submit();
    }
</script>

<%
    }
%>