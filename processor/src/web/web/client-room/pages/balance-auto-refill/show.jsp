<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%--<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>--%>
<%--<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>--%>
<%--<%@ page import="java.net.URI" %>--%>
<%--<%@ page import="org.slf4j.Logger" %>--%>
<%--<%@ page import="org.slf4j.LoggerFactory" %>--%>
<%--<%@ page import="ru.axetta.ecafe.processor.core.persistence.clientBalanceRefill.BankSubscription" %>--%>
<%--<%@ page import="java.util.List" %>--%>
<%--<%@ page import="ru.axetta.ecafe.processor.core.service.clientBalanceRefill.ClientBalanceRefillService" %>--%>
<%--<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>--%>
<%--<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>--%>
<%--<%@ page import="org.apache.commons.lang.StringUtils" %>--%>
<%--<%@ page import="java.text.DateFormat" %>--%>
<%--<%@ page import="java.text.SimpleDateFormat" %>--%>
<%--<%@ page import="java.util.TimeZone" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CryptoUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="java.util.Random" %>

<%--<%--%>
    <%--final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.balance-auto-refill.show_jsp");--%>
    <%--final String COMMAND_PARAM = "command";--%>
    <%--URI currentUri = null;--%>
    <%--try {--%>
        <%--currentUri = ServletUtils.getHostRelativeUriWithQuery(request);--%>
    <%--} catch (Exception ex) {--%>
        <%--logger.error(ex.getMessage());--%>
        <%--throw new ServletException(ex.getMessage());--%>
    <%--}--%>
    <%--ClientBalanceRefillService cs = (ClientBalanceRefillService) RuntimeContext.getAppContext()--%>
            <%--.getBean("clientBalanceRefillService");--%>
    <%--List<BankSubscription> list = cs.findClientBankSubscriptions(ClientAuthToken.loadFrom(session).getContractId());--%>
    <%--if (list.size() > 0) {--%>
<%--%>--%>

<%--<table class="infotable">--%>
    <%--<tr class="header-tr">--%>
        <%--<td colspan="5">--%>
            <%--<div class="output-text">Список всех подписок:</div>--%>
        <%--</td>--%>
    <%--</tr>--%>
    <%--<tr class="subheader-tr">--%>
        <%--<td>--%>
            <%--<div class="output-text">№</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">Номер карты</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">Активная</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">Дата подключения</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">Действует до:</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">Дата отключения</div>--%>
        <%--</td>--%>
        <%--<td></td>--%>
    <%--</tr>--%>
<%--<%--%>
        <%--for (int i = 0; i < list.size(); i++) {--%>
            <%--BankSubscription bs = list.get(i);--%>
            <%--String params = "&command=edit&bs=" + bs.getIdOfSubscription();--%>
            <%--String bsUri = currentUri.toString() + params;--%>
            <%--DateFormat df = new SimpleDateFormat("dd.MM.yyyy");--%>
            <%--df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));--%>
            <%--String activationDate = df.format(bs.getActivationDate());--%>
            <%--String validToDate = df.format(bs.getValidToDate());--%>
            <%--String deactivationDate = bs.getDeactivationDate() == null ? "" : df.format(bs.getDeactivationDate());--%>
<%--%>--%>
    <%--<tr>--%>
        <%--<td>--%>
            <%--<div class="output-text"><%=i + 1%>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text"><%=StringUtils.defaultString(bs.getMaskedCardNumber())%>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text">--%>
                <%--<span class=<%=bs.isActive() ? "green" : "red"%>><%=bs.isActive() ? "Да" : "Нет"%></span>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text"><%=activationDate%>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text"><%=validToDate%>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text"><%=deactivationDate%>--%>
            <%--</div>--%>
        <%--</td>--%>
        <%--<td>--%>
            <%--<div class="output-text"><a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(bsUri))%>">Просмотр</a>--%>
            <%--</div>--%>
        <%--</td>--%>
    <%--</tr>--%>
<%--<%--%>
        <%--}--%>
<%--%>--%>
<%--</table>--%>
<%--<%--%>
    <%--}--%>
<%--%>--%>

<%--<div class="output-text" style="margin-top: 30px;">Чтобы добавить новую подписку на автопополнение баланса нажмите здесь:</div>--%>
<%--<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"--%>
      <%--enctype="application/x-www-form-urlencoded" class="borderless-form">--%>
    <%--<input type="hidden" name="<%=COMMAND_PARAM%>" value="create" />--%>
    <%--<input class="command-button" type="submit" name="create" value="Добавить подписку" />--%>
<%--</form>--%>

<%
    Random r = new Random();
    int cf = r.nextInt();
    while (cf < 0) {
        cf = r.nextInt();
    }
    int cf2 = r.nextInt();
    while (cf2 < 0) {
        cf2 = r.nextInt();
    }
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

%>
<form action="https://secure.acquiropay.com" method="post" enctype="application/x-www-form-urlencoded"
      class="borderless-form">
    <input type="hidden" name="product_id" value="3814" />
    <input type="hidden" name="product_name" value="Пополнение баланса" />
    <input type="hidden" name="amount" value="*" />
    <input type="hidden" name="cf" value="<%=cf%>" />
    <input type="hidden" name="cf2" value="<%=cf2%>" />
    <input type="hidden" name="cf3" value="<%=clientAuthToken.getContractId()%>" />
    <input type="hidden" name="token" value="<%=CryptoUtils
                .MD5(516 + "3814" + '*' + String.valueOf(cf) + String.valueOf(cf2) +
                        String.valueOf(clientAuthToken.getContractId()) + "YunW2hD8Zs4").toLowerCase()%>" />
    <input type="submit" value="Отправить запрос" />
</form>
<%-- Скрипт автосабмита формы --%>
<%--<script type="text/javascript">--%>
<%--window.onload = function () {--%>
<%--document.forms[0].submit();--%>
<%--}--%>
<%--</script>--%>