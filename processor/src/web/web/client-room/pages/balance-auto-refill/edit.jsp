<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment" %>
<%@ page import="ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>

<%
    final Logger logger = LoggerFactory
        .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.balance-auto-refill.edit_jsp");
    URI currentUri;
    try {
        currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
        currentUri = UriUtils.removeParams(currentUri, Arrays.asList("command", "bs"));
    } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new ServletException(ex.getMessage());
    }
    RuntimeContext runtimeContext = RuntimeContext.getInstance();
    Long bsId = Long.valueOf(StringUtils.trim(request.getParameter("bs")));
    RegularPaymentSubscriptionService rpService =  RuntimeContext.getInstance().getRegularPaymentSubscriptionService();
    String stage = request.getParameter("stage");
    if ("process".equals(stage)) {
        Long refillAmount;
        Long thresholdAmount;
        int period;
        try {
            refillAmount = Long.valueOf(request.getParameter("refillAmount"));
            thresholdAmount = Long.valueOf(request.getParameter("thresholdAmount"));
            period = Integer.parseInt(request.getParameter("activePeriod"));
            rpService.updateBankSubscription(bsId, refillAmount, thresholdAmount, period);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ServletException(ex.getMessage());
        }
    }
    BankSubscription bs = rpService.findBankSubscription(bsId);
%>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <input type="hidden" name="command" value="edit" />
    <input type="hidden" name="stage" value="process" />
    <input type="hidden" name="bs" value="<%=bs.getIdOfSubscription()%>"/>
    <table class="borderless-grid">
        <tr>
            <td>
                <div class="output-text">Сумма пополнения:</div>
            </td>
            <td>
                <select size="1" class="input-text" name="refillAmount" id="refillAmount"
                        <%=bs.isActive() ? "" : "disabled"%> required>
                    <%
                        String values = runtimeContext.getOptionValueString(Option.OPTION_AUTOREFILL_VALUES);
                        List<String> list = Arrays.asList(StringUtils.split(values, ";"));
                        String paymentAmount = bs.getPaymentAmount().toString();
                        if (!list.contains(paymentAmount)) {
                            list = new ArrayList<String>(list);
                            list.add(paymentAmount);
                        }
                        for (String value : list) {
                    %>
                    <option value="<%=value%>" <%=(paymentAmount.equals(value)) ? "selected"
                            : ""%>><%=CurrencyStringUtils.copecksToRubles(Long.parseLong(value))%>
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
                <select size="1" class="input-text" name="thresholdAmount" id="thresholdAmount"
                        <%=bs.isActive() ? "" : "disabled"%> required>
                    <%
                        values = runtimeContext.getOptionValueString(Option.OPTION_THRESHOLD_VALUES);
                        list = Arrays.asList(StringUtils.split(values, ";"));
                        String thresholdAmount = bs.getThresholdAmount().toString();
                        if (!list.contains(thresholdAmount)) {
                            list = new ArrayList<String>(list);
                            list.add(thresholdAmount);
                        }
                        for (String value : list) {
                    %>
                    <option value="<%=value%>" <%=(thresholdAmount.equals(value)) ? "selected"
                            : ""%>><%=CurrencyStringUtils.copecksToRubles(Long.parseLong(value))%>
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
                <select size="1" class="input-text" name="activePeriod" id="activePeriod"
                        <%=bs.isActive() ? "" : "disabled"%> required>
                    <%for (int i = 1; i <= 12; i++) {%>
                    <option value="<%=i%>" <%=(i == bs.getMonthsCount()) ? "selected" : ""%>><%=i%>
                    </option>
                    <%}%>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <input class="command-button" type="submit" name="edit" value="Изменить параметры подписки"
                        <%=bs.isActive() ? "" : "disabled"%> />
            </td>
        </tr>
    </table>
</form>

<table class="borderless-grid">
    <tr>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-grid">
                <input type="hidden" name="command" value="deactivate" />
                <input type="hidden" name="bs" value="<%=bs.getIdOfSubscription()%>">
                <input class="command-button" type="submit" value="Отключить подписку"
                        <%=bs.isActive() ? "" : "disabled"%> />
            </form>
        </td>
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
    List<RegularPayment> paymentList = rpService.getSubscriptionPayments(bsId, null, null, false);
    if (!paymentList.isEmpty()) {
        int i = 1;
%>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="5">
            <div class="output-text">Список всех платежей по подписке:</div>
        </td>
    </tr>
    <tr class="subheader-tr">
        <td>
            <div class="output-text">№</div>
        </td>
        <td>
            <div class="output-text">Дата</div>
        </td>
        <td>
            <div class="output-text">Баланс до пополнения</div>
        </td>
        <td>
            <div class="output-text">Нижний порог баланса</div>
        </td>
        <td>
            <div class="output-text">Сумма пополнения</div>
        </td>
        <td>
            <div class="output-text">RRN транзакции</div>
        </td>
        <td>
            <div class="output-text">Платеж успешный</div>
        </td>
    </tr>
<%
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        df.setTimeZone(runtimeContext.getDefaultLocalTimeZone(null));
        for (RegularPayment rp : paymentList) {
            String paymentDate = rp.getPaymentDate() == null ? "" : df.format(rp.getPaymentDate());
%>
    <tr>
        <td>
            <div class="output-text"><%=i++%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=paymentDate%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=CurrencyStringUtils.copecksToRubles(rp.getClientBalance())%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=CurrencyStringUtils.copecksToRubles(rp.getThresholdAmount())%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=CurrencyStringUtils.copecksToRubles(rp.getPaymentAmount())%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=rp.getRrn()%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=rp.isSuccess() ? "Да" : "Нет"%>
            </div>
        </td>
    </tr>
    <%
        }
    %>
</table>

<%
    }
%>



