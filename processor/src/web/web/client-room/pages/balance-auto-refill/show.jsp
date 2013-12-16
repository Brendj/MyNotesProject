<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription" %>
<%@ page import="ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.balance-auto-refill.show_jsp");
    final String COMMAND_PARAM = "command";
    URI currentUri;
    try {
        currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new ServletException(ex.getMessage());
    }
    RuntimeContext runtimeContext = RuntimeContext.getInstance();
    RegularPaymentSubscriptionService cs = RuntimeContext.getAppContext()
            .getBean(RegularPaymentSubscriptionService.class);
    List<BankSubscription> list = cs.findClientBankSubscriptions(ClientAuthToken.loadFrom(session).getContractId());
    if (list.size() > 0) {
%>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="5">
            <div class="output-text">Список всех подписок:</div>
        </td>
    </tr>
    <tr class="subheader-tr">
        <td>
            <div class="output-text">№</div>
        </td>
        <td>
            <div class="output-text">Номер карты</div>
        </td>
        <td>
            <div class="output-text">Активная</div>
        </td>
        <td>
            <div class="output-text">Дата подключения</div>
        </td>
        <td>
            <div class="output-text">Действует до:</div>
        </td>
        <td>
            <div class="output-text">Дата отключения</div>
        </td>
        <td>
            <div class="output-text">Последний удачный платеж</div>
        </td>
        <td>
            <div class="output-text">Последний неудачный платеж</div>
        </td>
        <td></td>
    </tr>

<%
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(runtimeContext.getDefaultLocalTimeZone(null));
        DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        tf.setTimeZone(runtimeContext.getDefaultLocalTimeZone(null));
        for (int i = 0; i < list.size(); i++) {
            BankSubscription bs = list.get(i);
            String params = "&command=edit&bs=" + bs.getIdOfSubscription();
            String bsUri = currentUri.toString() + params;
            String activationDate = df.format(bs.getActivationDate());
            String validToDate = df.format(bs.getValidToDate());
            String deactivationDate = bs.getDeactivationDate() == null ? "" : df.format(bs.getDeactivationDate());
            String lastUnsPayment = bs.getLastUnsuccessfulPaymentDate() == null ? "" : tf.format(bs.getLastUnsuccessfulPaymentDate());
            String lastSucPayment = bs.getLastSuccessfulPaymentDate() == null ? "" : tf.format(bs.getLastSuccessfulPaymentDate());
%>
    <tr>
        <td>
            <div class="output-text"><%=i + 1%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=StringUtils.defaultString(bs.getMaskedCardNumber())%>
            </div>
        </td>
        <td>
            <div class="output-text">
                <span class=<%=bs.isActive() ? "green" : "red"%>><%=bs.isActive() ? "Да" : "Нет"%></span>
            </div>
        </td>
        <td>
            <div class="output-text"><%=activationDate%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=validToDate%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=deactivationDate%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=lastSucPayment%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=lastUnsPayment%>
            </div>
        </td>
        <td>
            <div class="output-text"><a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(bsUri))%>">Просмотр</a>
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

<div class="output-text" style="margin-top: 30px;">Для добавления новой подписки на автопополнение баланса нажмите
    здесь:
</div>
<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <input type="hidden" name="<%=COMMAND_PARAM%>" value="create" />
    <input class="command-button" type="submit" name="create" value="Добавить подписку" />
</form>