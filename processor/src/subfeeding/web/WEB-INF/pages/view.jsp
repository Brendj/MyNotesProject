<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/flick/jquery-ui-1.10.3.custom.min.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/common.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/view.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/complexTable.css"/>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-1.10.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery.ui.datepicker-ru.js"></script>
    <script>
        $(function () {
            $('button').button();
            $('input:text').button().css({
                'font': 'inherit',
                'color': 'inherit',
                'text-align': 'left',
                'outline': 'none',
                'cursor': 'text'
            });
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
<div class="bodyDiv">
<%
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    @SuppressWarnings("unchecked")
    SubFeedingResult sf = (SubFeedingResult) request.getAttribute("subscriptionFeeding");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance1());
    String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getSubBalance0());
    boolean wasSuspended = sf.getSuspended() != null && sf.getSuspended();
%>
<div class="header">
    <span class="contract"><%=ContractIdFormat.format(client.getContractId())%></span>
    <span class="contract" style="padding-left: 20px;"><%=client.getFullName()%></span>
    <span style="float: right;">
        <button onclick="location.href = '${pageContext.request.contextPath}/office/logout'" name="logout">Выход
            </button>
    </span>
</div>
<div id="content">
    <div id="infoHeader">
        <div class="colRow">
            <div class="leftcol">Баланс основного счета:</div>
            <div class="rightcol"><%=subBalance0%> руб.</div>
        </div>
        <div class="colRow">
            <div class="leftcol">Баланс субсчета АП:</div>
            <div class="rightcol"><%=subBalance1%> руб.</div>
        </div>
        <div class="colRow">
            <div class="leftcol">Дата активации услуги:</div>
            <div class="rightcol"><%=tf.format(sf.getDateActivate())%></div>
        </div>
        <div class="colRow">
            <div class="leftcol">Дата отключения услуги:</div>
            <div class="rightcol"><%=sf.getDateDeactivate() == null ? "услуга бессрочная"
                    : df.format(sf.getDateDeactivate())%>
            </div>
        </div>
        <%
            if (wasSuspended) {
                String suspendDate = df.format(sf.getLastDatePause());
        %>
        <div class="colRow">
            <div class="leftcol">Услуга приостанавливается</div>
            <div class="rightcol">c <%=suspendDate%></div>
        </div>
        <%
            }
        %>
        <c:if test="${not empty requestScope.subFeedingError}">
            <div class="messageDiv errorMessage">${requestScope.subFeedingError}</div>
        </c:if>
        <c:if test="${not empty requestScope.subFeedingSuccess}">
            <div class="messageDiv successMessage">${requestScope.subFeedingSuccess}</div>
        </c:if>
        <div class="colRow">
            <%
                if (!wasSuspended) {
            %>
            <div class="leftcol">
                <button type="submit" onclick="location.href = '${pageContext.request.contextPath}/office/suspend'">
                    Приостановить услугу
                </button>
            </div>
            <%
            } else {
            %>
            <div class="leftcol">
                <button type="submit" onclick="location.href = '${pageContext.request.contextPath}/office/reopen'">
                    Возобновить услугу
                </button>
            </div>
            <%
                }
            %>
             <div class="rightcol">
                 <button type="button" onclick="location.href = '${pageContext.request.contextPath}/office/plan'">
                     Просмотр циклограммы
                 </button>
             </div>
        </div>
    </div>
    <%
        String startDate = (String) request.getAttribute("startDate");
        String endDate = (String) request.getAttribute("endDate");
        PaymentListResult payments = (PaymentListResult) request.getAttribute("payments");
        PurchaseListResult purchases = (PurchaseListResult) request.getAttribute("purchases");
        boolean purchasesExist = purchases != null && purchases.purchaseList != null && !purchases.purchaseList.getP().isEmpty();
        boolean paymentsExist = payments != null && payments.paymentList != null && !payments.paymentList.getP().isEmpty();
    %>
    <div id="history">
        <div style="font-weight: bold; margin: 20px 0;">История операций</div>
        <div>
            <form method="post" enctype="application/x-www-form-urlencoded"
                  action="${pageContext.request.contextPath}/office/view">
                <span style="padding-right: 10px;">Начальная дата:</span>
                <input type="text" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                       id="datepickerBegin" maxlength="10" required />
                <span style="padding: 10px;">Конечная дата:</span>
                <input type="text" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>" id="datepickerEnd"
                       maxlength="10" required />
                <button type="submit">Показать</button>
            </form>
        </div>
    </div>
</div>


<div class="purchases">
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
</div>

<div class="payments">
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
</div>
</div>
</body>
</html>