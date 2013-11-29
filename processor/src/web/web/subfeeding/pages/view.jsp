<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ComplexInfo" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TimeZone" %>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/subfeeding/complexTable.css"/>
</head>
<body>
<%
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    @SuppressWarnings("unchecked")
    SubscriptionFeeding sf = (SubscriptionFeeding) request.getAttribute("subscriptionFeeding");
    if (sf == null) {
%>
<div class="textDiv">Активировать подписку абонементного питания? Нажимая на данную кнопку Вы согласны с условиями
    предоставления услуги.
</div>
<%
    } else {
        Client client = (Client) request.getAttribute("client");
        String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance(1));
        String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getBalance() - client.getSubBalance(1));
%>
<div class="textDiv">Текущий баланс основного счета: <%=subBalance0%> руб.</div>
<div class="textDiv">Баланс субсчета АП: <%=subBalance1%> руб.</div>
<%
        if (sf.getWasSuspended() != null && sf.getWasSuspended()) {
            String suspendDate = df.format(sf.getLastDatePauseService());
%>
<div class="textDiv">Услуга приостановлена с <%=suspendDate%></div>
<%
        }
    }
%>
<c:if test="${not empty requestScope.subFeedingError}">
    <div class="textDiv" style="color: red">${requestScope.subFeedingError}</div>
</c:if>
<c:if test="${not empty requestScope.subFeedingSuccess}">
    <div class="textDiv" style="color: green">${requestScope.subFeedingSuccess}</div>
</c:if>
<%
    if (sf == null) {
%>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="${pageContext.request.contextPath}/sub-feeding/activate">
    <table>
        <tr>
            <th class="complexNameHeader">День недели</th>
            <th rowspan="2">ПН</th>
            <th rowspan="2">ВТ</th>
            <th rowspan="2">СР</th>
            <th rowspan="2">ЧТ</th>
            <th rowspan="2">ПТ</th>
            <th rowspan="2">СБ</th>
            <th rowspan="2">ВС</th>
        </tr>
        <tr>
            <th class="dayNameHeader">Комплекс</th>
        </tr>
        <%
            @SuppressWarnings("unchecked")
            List<ComplexInfo> complexes = (List<ComplexInfo>) request.getAttribute("complexes");
            int j = 1;
            for (ComplexInfo complex : complexes) {
                boolean even = j % 2 == 0;
        %>
        <tr class="<%=even ? "evenLine" : "unevenLine"%>">
            <td class="complexName"><%=complex.getComplexName() + " - " + CurrencyStringUtils
                    .copecksToRubles(complex.getCurrentPrice()) + " руб"%>
            </td>
            <%
                for (int i = 1; i <= 7; i++) {
                    String key = complex.getIdOfComplex() + "_" + i;
            %>
            <td>
                <input type="checkbox" name="complex_option_<%=key%>" value="<%=key%>" title="" />
            </td>
            <%
                }
            %>
        </tr>
        <%
                j++;
            }
        %>
        <tr>
            <td align="center" colspan="8"><input type="submit" name="activate" value="Активировать" /></td>
        </tr>
    </table>
</form>
<%
    }  else {
%>

<table>
<%
        if (sf.getWasSuspended() == null || !sf.getWasSuspended()) {
%>
    <tr>
        <td align="center" colspan="8">
            <form method="post" action="${pageContext.request.contextPath}/sub-feeding/suspend">
                <div style="background-color: red; width: 160px;">
                    <input type="submit" name="deactivate" value="Приостановить услугу" />
                </div>
            </form>
        </td>
    </tr>
<%
        } else {
%>
    <tr>
        <td align="center" colspan="8">
            <form method="post" action="${pageContext.request.contextPath}/sub-feeding/reopen">
                <div>
                    <input type="submit" name="reopen" value="Возобновить услугу" />
                </div>
            </form>
        </td>
    </tr>
<%
        }
%>
</table>
<%
        String startDate = (String) request.getAttribute("startDate");
        String endDate = (String) request.getAttribute("endDate");
        PaymentListResult payments = (PaymentListResult) request.getAttribute("payments");
        PurchaseListResult purchases = (PurchaseListResult) request.getAttribute("purchases");
        boolean purchasesExist = purchases != null && purchases.purchaseList != null && !purchases.purchaseList.getP().isEmpty();
        boolean paymentsExist = payments != null && payments.paymentList != null && !payments.paymentList.getP().isEmpty();
%>

<div class="textDiv" style="font-weight: bold; margin-top: 50px;">Покупки и платежи</div>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="${pageContext.request.contextPath}/sub-feeding/view">
    <table>
        <tr class="paymentEvenLine">
            <td>Начальная дата</td>
            <td>
                <input type="text" class="textDiv" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                       size="12" maxlength="10" />
            </td>
        </tr>
        <tr class="paymentEvenLine">
            <td>Конечная дата</td>
            <td>
                <input type="text" class="textDiv" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>"
                       size="12" maxlength="10" />
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
    <span><%=!purchasesExist ? " по данному субсчету АП не было покупок" : ""%></span>
</div>
<%
        if (purchasesExist) {
%>
<table>
    <tr>
        <th>
            <div class="output-text">Дата</div>
        </th>
        <th>
            <div class="output-text">Сумма покупки</div>
        </th>
        <th>
            <div class="output-text">Торговая скидка</div>
        </th>
        <th>
            <div class="output-text">Наличными</div>
        </th>
        <th>
            <div class="output-text">По карте</div>
        </th>
        <th>
            <div class="output-text">Состав</div>
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
                StringBuilder consistenceBuilder = new StringBuilder();
                for (PurchaseElementExt pe : purchase.getE()) {
                    consistenceBuilder.append(pe.getName()).append(" - ")
                            .append(CurrencyStringUtils.copecksToRubles(pe.getSum())).append("<br/>");
                }
                String consistence = consistenceBuilder.length() > 0 ? consistenceBuilder.toString()
                        .substring(0, consistenceBuilder.length() - 5) : "";
%>
    <tr style="vertical-align: top;" class="<%=even ? "paymentEvenLine" : "paymentUnevenLine"%>">
        <td>
            <div class="output-text"><%=date%>
            </div>
        </td>
        <td align="right">
            <div class="output-text"><%=sum%>
            </div>
        </td>
        <td align="right">
            <div class="output-text"><%=tradeDiscount%>
            </div>
        </td>
        <td align="right">
            <div class="output-text"><%=sumByCash%>
            </div>
        </td>
        <td align="right">
            <div class="output-text"><%=sumByCard%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=consistence%>
            </div>
        </td>
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
    <span><%=!paymentsExist ? " по данному субсчету АП не было платежей" : ""%></span>
</div>
<%
        if (paymentsExist) {
%>
<table>
    <tr>
        <th>
            <div class="output-text">Дата</div>
        </th>
        <th>
            <div class="output-text">Сумма</div>
        </th>
        <th>
            <div class="output-text">Информация о платеже</div>
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
        <td>
            <div class="output-text"><%=date%>
            </div>
        </td>
        <td align="right">
            <div class="output-text"><%=sum%>
            </div>
        </td>
        <td>
            <div class="output-text"><%=payment.getOrigin()%>
            </div>
        </td>
    </tr>
    <%
                i++;
            }
    %>
</table>
<%
        }
    }
%>
</body>
</html>