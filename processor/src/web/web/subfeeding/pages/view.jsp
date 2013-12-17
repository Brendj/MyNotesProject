<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.*" %>
<%@ page import="ru.axetta.ecafe.processor.web.subfeeding.OrderDetailViewInfo" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/subfeeding/css/complexTable.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/subfeeding/css/flick/jquery-ui-1.10.3.custom.min.css"/>
    <script src="${pageContext.request.contextPath}/subfeeding/js/jquery-1.10.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/subfeeding/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/subfeeding/js/jquery.ui.datepicker-ru.js"></script>
    <script>
        $(function () {
            var datepickerBegin = $("#datepickerBegin").datepicker({
                onSelect: function (selected) {
                    $("#datepickerEnd").datepicker("option", "minDate", selected);
                }
            });
            var datepickerEnd = $("#datepickerEnd").datepicker();
            datepickerEnd.datepicker("option", "minDate", datepickerBegin.datepicker("getDate"));
        });
    </script>
</head>
<body>
<div class="textDiv" style="position: relative; text-align: right; padding-right: 50px;">
    <form method="post" action="${pageContext.request.contextPath}/sub-feeding/logout">
        <input type="submit" name="logout" value="Выход" />
    </form>
</div>
<%
    TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(timeZone);
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(timeZone);
    @SuppressWarnings("unchecked")
    SubscriptionFeeding sf = (SubscriptionFeeding) request.getAttribute("subscriptionFeeding");
    Client client = (Client) request.getAttribute("client");
    String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance(1));
    String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getBalance() - client.getSubBalance(1));
    boolean wasSuspended = sf.getWasSuspended() != null && sf.getWasSuspended();
%>
<div class="textDiv"><%=client.getPerson().getFullName()%></div>
<div class="textDiv">Номер контракта: <%=ContractIdFormat.format(client.getContractId())%></div>
<div class="textDiv">Текущий баланс основного счета: <%=subBalance0%> руб.</div>
<div class="textDiv">Баланс субсчета АП: <%=subBalance1%> руб.</div>
<div class="textDiv">Дата подключения услуги: <%=tf.format(sf.getDateActivateService())%></div>
<div class="textDiv">Дата отключения услуги: <%=sf.getDateDeactivateService() == null ? "услуга бессрочная"
        : df.format(sf.getDateDeactivateService())%>
</div>
<%
    if (wasSuspended) {
        String suspendDate = df.format(sf.getLastDatePauseService());
        String status = "Услуга приостановлена. Заявки сформированы до " + suspendDate + ".";
%>
<div class="textDiv"><%=status%></div>
<%
    }
%>
<c:if test="${not empty requestScope.subFeedingError}">
    <div class="textDiv" style="color: red">${requestScope.subFeedingError}</div>
</c:if>
<c:if test="${not empty requestScope.subFeedingSuccess}">
    <div class="textDiv" style="color: green">${requestScope.subFeedingSuccess}</div>
</c:if>

<table class="customTable">
    <tr>
<%
    if (!wasSuspended) {
%>
        <td align="center" colspan="2">
            <form method="post" action="${pageContext.request.contextPath}/sub-feeding/suspend">
                <input type="submit" name="deactivate" class="deactivateButton" value="Приостановить услугу" />
            </form>
        </td>
<%
    } else {
%>
        <td align="center" colspan="2">
            <form method="post" action="${pageContext.request.contextPath}/sub-feeding/reopen">
                <div>
                    <input type="submit" class="reopenButton" name="reopen" value="Возобновить услугу" />
                </div>
            </form>
        </td>
<%
    }
%>
        <td align="left" colspan="6">
            <form method="post" action="${pageContext.request.contextPath}/sub-feeding/plan">
                <div>
                    <input type="submit" class="reopenButton" name="plan" value="Просмотр плана питания" />
                </div>
            </form>
        </td>
    </tr>
</table>
<%
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    PaymentListResult payments = (PaymentListResult) request.getAttribute("payments");
    PurchaseListResult purchases = (PurchaseListResult) request.getAttribute("purchases");
    boolean purchasesExist = purchases != null && purchases.purchaseList != null && !purchases.purchaseList.getP().isEmpty();
    boolean paymentsExist = payments != null && payments.paymentList != null && !payments.paymentList.getP().isEmpty();
%>

<div class="textDiv" style="font-weight: bold; margin-top: 50px;">История операций</div>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="${pageContext.request.contextPath}/sub-feeding/view">
    <table class="customTable">
        <tr class="paymentEvenLine">
            <td>Начальная дата</td>
            <td>
                <div class="textDiv">
                    <input type="text" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                           id="datepickerBegin" maxlength="10" required />
                </div>
            </td>
        </tr>
        <tr class="paymentEvenLine">
            <td>Конечная дата</td>
            <td>
                <div class="textDiv">
                    <input type="text" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>"
                           id="datepickerEnd" maxlength="10" required />
                </div>
            </td>
        </tr>
        <tr class="paymentUnevenLine">
            <td colspan="2" align="center">
                <div class="output-text">(формат даты - ДД.ММ.ГГГГ)</div>
            </td>
        </tr>
        <tr class="paymentUnevenLine">
            <td colspan="2" align="center">
                <input type="submit" class="textDiv" name="payments" value="Показать"/>
            </td>
        </tr>
    </table>
</form>

<div class="textDiv" style="margin-top: 30px;">
    <span style="font-weight: bold;">Покупки:</span>
    <span><%=!purchasesExist ? " за данный период по субсчету АП покупок не было." : ""%></span>
</div>
<%
    if (purchasesExist) {
%>
<table class="output-text customTable">
    <tr>
        <th>Дата</th>
        <th>Сумма покупки</th>
        <th>Торговая скидка</th>
        <th>Наличными</th>
        <th>По карте</th>
        <th>
            <div style="width: 200px;">Состав</div>
        </th>
    </tr>
<%
        int i = 1;
        for (PurchaseExt purchase : purchases.purchaseList.getP()) {
            boolean even = i % 2 == 0;
            String date = tf.format(purchase.getTime().toGregorianCalendar().getTime());
            String sum = CurrencyStringUtils.copecksToRubles(purchase.getSum());
            String tradeDiscount = CurrencyStringUtils.copecksToRubles(purchase.getTrdDiscount());
            String sumByCash = CurrencyStringUtils.copecksToRubles(purchase.getByCash());
            String sumByCard = CurrencyStringUtils.copecksToRubles(purchase.getByCard());
            OrderDetailViewInfo viewInfo = new OrderDetailViewInfo();
            for (PurchaseElementExt pe : purchase.getE()) {
                int type = pe.getType();
                if (type == 1) {
                    viewInfo.createComplexViewInfo(pe.getMenuType(), pe.getName(), pe.getSum(), true);
                } else if (type == 2) {
                    int complexMenuType = pe.getMenuType() - 100;
                    OrderDetailViewInfo.ComplexViewInfo cvi = viewInfo.complexesByType.get(complexMenuType);
                    if (cvi == null) {
                        cvi = viewInfo.createComplexViewInfo(complexMenuType, "", 0L, false);
                    }
                    cvi.addComplexDetail(pe.getName());
                } else if (type == 0) {
                    viewInfo.createSeparateDish(pe.getName(), pe.getSum(), pe.getAmount());
                }
            }
            String consistence = viewInfo.toString();
%>
    <tr style="vertical-align: top;" class="<%=even ? "paymentEvenLine" : "paymentUnevenLine"%>">
        <td><%=date%></td>
        <td align="right"><%=sum%></td>
        <td align="right"><%=tradeDiscount%></td>
        <td align="right"><%=sumByCash%></td>
        <td align="right"><%=sumByCard%></td>
        <td><%=consistence%></td>
    </tr>
<%
            i++;
        }
%>
</table>
<%
    }
%>

<div class="textDiv" style="margin-top: 20px;">
    <span style="font-weight: bold">Платежи:</span>
    <span><%=!paymentsExist ? " за данный период по субсчету АП платежей не было." : ""%></span>
</div>
<%
    if (paymentsExist) {
%>
<table class="output-text customTable">
    <tr>
        <th>Дата</th>
        <th>Сумма</th>
        <th>
            <div style="width: 200px;">Информация о платеже</div>
        </th>
    </tr>
    <%
        int i = 1;
        for (Payment payment : payments.paymentList.getP()) {
            boolean even = i % 2 == 0;
            String date = tf.format(payment.getTime().toGregorianCalendar().getTime());
            String sum = CurrencyStringUtils.copecksToRubles(payment.getSum());
    %>
    <tr style="vertical-align: top;" class="<%=even ? "paymentEvenLine" : "paymentUnevenLine"%>">
        <td><%=date%></td>
        <td align="right"><%=sum%></td>
        <td><%=payment.getOrigin()%></td>
    </tr>
    <%
            i++;
        }
    %>
</table>
<%
    }
%>
</body>
</html>