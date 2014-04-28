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
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    @SuppressWarnings("unchecked")
    SubscriptionFeedingExt sf = (SubscriptionFeedingExt) request.getAttribute("subscriptionFeeding");
    Date activationDate = (Date) request.getAttribute("activationDate");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance1());
    String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getSubBalance0());
    boolean wasSuspended = sf != null && sf.getSuspended() != null && sf.getSuspended();
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    PaymentListResult payments = (PaymentListResult) request.getAttribute("payments");
    PurchaseListResult purchases = (PurchaseListResult) request.getAttribute("purchases");
    TransferSubBalanceListResult transfers = (TransferSubBalanceListResult) request.getAttribute("transfers");
    boolean purchasesExist =
            purchases != null && purchases.purchaseList != null && !purchases.purchaseList.getP().isEmpty();
    boolean paymentsExist = payments != null && payments.paymentList != null && !payments.paymentList.getP().isEmpty();
    boolean transferExist =
            transfers != null && transfers.transferSubBalanceListExt != null && !transfers.transferSubBalanceListExt
                    .getT().isEmpty();%>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <jsp:include page="include/header.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/view.css" />
    <script>
        $(function () {
            $('button').button();
            $('input:text').button().addClass('ui-textfield');
            $('#pauseButton').button().css({
                'background': 'rgba(240, 26, 26, 0.55)',
                'color': 'white'
            });
            $('#reopenButton').button().css({
                'background': 'rgb(104, 153, 104)',
                'color': 'white'
            });
            $('#disableButton').button().css({
                'background': 'rgb(152, 152, 152)',
                'color': 'white'
            }).click(function (e) {
                        e.preventDefault();
                        return false;
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
<div class="header">
    <span class="contract"><%=ContractIdFormat.format(client.getContractId())%></span>
    <span class="contract" style="padding-left: 20px;"><%=client.getFullName()%></span>
    <span style="float: right;">
        <button onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/logout'" name="logout">
            Выход
        </button>
    </span>
</div>
<div id="content">
<div id="infoHeader">
<div id="infoTable">
    <div class="colRow">
        <div class="leftcol">Баланс основного счета:</div>
        <div class="rightcol"><%=subBalance0%> руб.</div>
    </div>
    <div class="colRow">
        <div class="leftcol">Баланс субсчета АП:</div>
        <div class="rightcol"><%=subBalance1%> руб.</div>
    </div>
    <c:if test="${requestScope.subscriptionFeeding!=null}">
        <div class="colRow">
            <div class="leftcol">Дата создания услуги АП:</div>
            <div class="rightcol"><%=df.format(sf.getDateCreateService())%>
            </div>
        </div>
        <c:if test="${requestScope.subscriptionFeeding.dateActivate==null}">
            <div class="colRow">
                <div class="leftcol">Состояние услуги:</div>
                <div class="rightcol">Нет подключеий</div>
            </div>
        </c:if>
        <c:if test="${requestScope.subscriptionFeeding.dateActivate!=null}">
            <div class="colRow">
                <div class="leftcol">Дата начала подписки на услугу АП:</div>
                <div class="rightcol"><%=df.format(sf.getDateActivate())%>
                </div>
            </div>
            <div class="colRow">
                <div class="leftcol">Дата отключения услуги:</div>
                <div class="rightcol"><%=sf.getDateDeactivate() == null ? "услуга бессрочная"
                        : df.format(sf.getDateDeactivate())%>
                </div>
            </div>
            <div class="colRow">
                <div class="leftcol">Состояние услуги:</div>
                <div class="rightcol">
                    <%String status = "услуга активна";
                        if (wasSuspended && sf.getLastDatePause() != null) {
                            boolean reallySuspended = sf.getLastDatePause().after(new Date());
                            status =
                                    "услуга " + (reallySuspended ? "приостанавливается" : "приостановлена") + " с " + df
                                            .format(sf.getLastDatePause());
                        }
                        out.print(status);%>
                </div>
            </div>
        </c:if>
    </c:if>
    <c:if test="${requestScope.subscriptionFeeding==null}">
        <div class="colRow">
            <div class="leftcol">Состояние услуги:</div>
            <div class="rightcol errorMessage">услуга не активна</div>
        </div>
    </c:if>

    <c:if test="${not empty requestScope.subFeedingError}">
        <div class="messageDiv errorMessage">${requestScope.subFeedingError}</div>
    </c:if>
    <c:if test="${not empty requestScope.subFeedingSuccess}">
        <div class="messageDiv successMessage">${requestScope.subFeedingSuccess}</div>
    </c:if>
    <c:if test="${empty requestScope.subFeedingSuccess && empty requestScope.subFeedingError && requestScope.subscriptionFeeding==null}">
        <div class="messageDiv errorMessage">
            Для активации необходимо обратиться в образовательную организацию и подключить услугу.
        </div>
    </c:if>
</div>
<div id="manageButtons">
<c:choose>
<c:when test="${requestScope.subscriptionFeeding==null}">
    <button id="disableButton">
        Активировать подписку
    </button>
    <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/plan'">
        Просмотр циклограммы
    </button>
    <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
        Перевод средств
    </button>
</c:when>
<c:otherwise>
<c:choose>
<%-- Подписки нет но услуга создана --%>
<c:when test="${requestScope.subscriptionFeeding.dateActivate==null}">
    <c:choose>
        <%-- У клиента не достаточно средств --%>
        <c:when test="${requestScope.client.subBalance1==null || requestScope.client.subBalance1<=0}">
            <div class="errorMessage">Недостаточно средств на счете, пополните баланс перводом</div>
            <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
                Перевод средств
            </button>
        </c:when>
        <%-- У клиента не активных циклограм --%>
        <c:when test="${requestScope.currentCycleDiagram==null}">
            <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/plan'">
                Активировать циклограмму
            </button>
            <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
                Перевод средств
            </button>
        </c:when>
    </c:choose>
</c:when>
<%-- Подпика приостановлена --%>
<c:when test="${requestScope.subscriptionFeeding.lastDatePause!=null}">
    <% if (sf.getLastDatePause().before(new Date())) { %>
    <form method="post" enctype="application/x-www-form-urlencoded"
          action="${pageContext.request.contextPath}/sub-feeding/reopen">
        <script>
            $(function () {
                var minEndReopenDate = new Date('<%=df.format(activationDate)%>'.replace(/(\d+)\.(\d+)\.(\d+)/,
                        '$2/$1/$3'));
                var endReopenDate = $("#endReopenDate").datepicker();
                endReopenDate.datepicker("option", "minDate", minEndReopenDate);
            })
        </script>
        <button type="submit" id="reopenButton">
            Возобновить подписку
        </button>
        <label for="endReopenDate"><span> с даты </span></label>
        <input type="text" name="endReopenDate"
               value="<%=StringEscapeUtils.escapeHtml(df.format(activationDate))%>" id="endReopenDate"
               maxlength="10" required>
    </form>
    <p>
        <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/plan'">
            Просмотр циклограммы
        </button>
        <button type="button"
                onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
            Перевод средств
        </button>
    </p>
    <%-- Подписка ожидается приостановки --%>
    <% } else {%>
    <button type="submit" id="cancelButton"
            onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/cancel'">
        Отменить приостановку подписки
    </button>
    <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/plan'">
        Просмотр циклограммы
    </button>
    <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
        Перевод средств
    </button>
    <% } %>
</c:when>
<c:otherwise>
    <script>
        $(function () {
            var minEndPauseDate = new Date('<%=df.format(activationDate)%>'.replace(/(\d+)\.(\d+)\.(\d+)/,
                    '$2/$1/$3'));
            var endPauseDate = $("#endPauseDate").datepicker();
            endPauseDate.datepicker("option", "minDate", minEndPauseDate);
        })
    </script>
    <form method="post" enctype="application/x-www-form-urlencoded"
          action="${pageContext.request.contextPath}/sub-feeding/suspend">
        <button type="submit" id="pauseButton">
            Приостановить подписку
        </button>
        <label for="endPauseDate"><span> с даты </span></label>
        <input type="text" name="endPauseDate"
               value="<%=StringEscapeUtils.escapeHtml(df.format(activationDate))%>" id="endPauseDate"
               maxlength="10" required>
    </form>
    <p>
        <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/plan'">
            Просмотр циклограммы
        </button>
        <button type="button"
                onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
            Перевод средств
        </button>
    </p>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>
</div>
</div>
<div id="history">
    <div style="font-weight: bold;">История операций</div>
    <div style="margin-top: 20px;">
        <form method="post" enctype="application/x-www-form-urlencoded"
              action="${pageContext.request.contextPath}/sub-feeding/view">
            <span style="padding-right: 10px;">Начальная дата:</span>
            <input type="text" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                   id="datepickerBegin" maxlength="10" required />
            <span style="padding: 10px;">Конечная дата:</span>
            <input type="text" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>" id="datepickerEnd"
                   maxlength="10" required />
            <button type="submit">Показать</button>
        </form>
    </div>
    <div id="purchases">
        <div style="font-weight: bold;">Покупки</div>
        <div style="line-height: 3em;">
            <span><%=!purchasesExist ? " За данный период по субсчету АП покупок не было." : ""%></span>
        </div>
        <%if (purchasesExist) {%>
        <div class="simpleTable purchaseTable">
            <div class="simpleTableHeader purchaseRow">
                <div class="simpleCell purchaseHeaderCell">Дата</div>
                <div class="simpleCell purchaseHeaderCell">Сумма покупки</div>
                <div class="simpleCell purchaseHeaderCell">Торговая скидка</div>
                <div class="simpleCell purchaseHeaderCell">Наличными</div>
                <div class="simpleCell purchaseHeaderCell">По карте</div>
                <div class="simpleCell purchaseHeaderCell wideCell">Состав</div>
            </div>
            <%for (PurchaseExt purchase : purchases.purchaseList.getP()) {
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
                String consistence = viewInfo.toString();%>
            <div class="simpleRow purchaseRow">
                <div class="purchaseCell simpleCell"><%=date%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=sum%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=tradeDiscount%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=sumByCash%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=sumByCard%>
                </div>
                <div class="purchaseCell simpleCell complexName"><%=consistence%>
                </div>
            </div>
            <%}%>
        </div>
        <%}%>
    </div>
    <div id="payments">
        <div style="font-weight: bold;">Платежи</div>
        <div style="line-height: 3em;">
            <span><%=!paymentsExist ? " За данный период по субсчету АП платежей не было." : ""%></span>
        </div>
        <%if (paymentsExist) {%>
        <div class="simpleTable purchaseTable">
            <div class="simpleTableHeader purchaseRow">
                <div class="simpleCell purchaseHeaderCell">Дата</div>
                <div class="simpleCell purchaseHeaderCell">Сумма</div>
                <div class="simpleCell purchaseHeaderCell wideCell">Информация о платеже</div>
            </div>
            <%for (Payment payment : payments.paymentList.getP()) {
                String date = tf.format(payment.getTime().toGregorianCalendar().getTime());
                String sum = CurrencyStringUtils.copecksToRubles(payment.getSum());%>
            <div class="simpleRow purchaseRow">
                <div class="purchaseCell simpleCell"><%=date%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=sum%>
                </div>
                <div class="purchaseCell simpleCell complexName"><%=payment.getOrigin()%>
                </div>
            </div>
            <%}%>
        </div>
        <%}%>
    </div>

    <div id="transfers">
        <div style="font-weight: bold;">Переводы</div>
        <div style="line-height: 3em;">
            <span><%=!transferExist ? " За данный период по субсчету АП переводов не было." : ""%></span>
        </div>
        <%if (transferExist) {%>
        <div class="simpleTable purchaseTable">
            <div class="simpleTableHeader purchaseRow">
                <div class="simpleCell purchaseHeaderCell wideCell">Номер счета списания</div>
                <div class="simpleCell purchaseHeaderCell wideCell">Номер счета пополнения</div>
                <div class="simpleCell purchaseHeaderCell">Дата</div>
                <div class="simpleCell purchaseHeaderCell">Сумма</div>
            </div>
            <%for (TransferSubBalanceExt transferSubBalanceExt : transfers.transferSubBalanceListExt.getT()) {
                String date = tf.format(transferSubBalanceExt.getCreateTime());
                String sum = CurrencyStringUtils.copecksToRubles(transferSubBalanceExt.getTransferSum());%>
            <div class="simpleRow purchaseRow">
                <div class="purchaseCell simpleCell"><%=transferSubBalanceExt.getBalanceBenefactor()%>
                </div>
                <div class="purchaseCell simpleCell"><%=transferSubBalanceExt.getBalanceBeneficiary()%>
                </div>
                <div class="purchaseCell simpleCell"><%=date%>
                </div>
                <div class="purchaseCell simpleCell sum"><%=sum%>
                </div>
            </div>
            <%}%>
        </div>
        <%}%>
    </div>

</div>
</div>
</div>
</body>
</html>