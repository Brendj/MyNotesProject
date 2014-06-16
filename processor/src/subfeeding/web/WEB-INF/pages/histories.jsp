<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<%@ page import="ru.axetta.ecafe.processor.web.subfeeding.SubscriptionFeeding" %>
<%@ page import="java.util.List" %>
<%DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    String historyType = (String) request.getAttribute("historyType");
    PaymentListResult payments = (PaymentListResult) request.getAttribute("payments");
    PurchaseListResult purchases = (PurchaseListResult) request.getAttribute("purchases");
    TransferSubBalanceListResult transfers = (TransferSubBalanceListResult) request.getAttribute("transfers");
    //List<SubscriptionFeeding> subfeedings = (List<SubscriptionFeeding>) request.getAttribute("subfeedings");
    boolean purchasesExist = isExist(request, "purchasesExist");
    boolean paymentsExist = isExist(request, "paymentsExist");
    boolean transfersExist = isExist(request, "transfersExist");
    boolean subfeedingExist = isExist(request, "subfeedingExist");
    boolean clientdiagramExist =isExist(request, "clientdiagramExist");

%>
<!DOCTYPE html>
<head>
    <title>Абонементное питание</title>
    <jsp:include page="include/header.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/view.css" />
    <script>
        $(function () {
            $('button').button();
            $("#menu-radio").buttonset();
            $("input[type=radio]").click(function(e){
                $("#historyForm").submit();
            });
            $('input:text').button().addClass('ui-textfield');
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
            <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/view'">
                Вернуться
            </button>
            <button onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/logout'" name="logout">
                Выход
            </button>
        </span>
    </div>
    <div id="content">
        <div id="history">
            <div style="font-weight: bold;">История операций</div>
            <div style="margin-top: 20px;">
                <form id="historyForm" method="post" enctype="application/x-www-form-urlencoded"
                      action="${pageContext.request.contextPath}/sub-feeding/histories">
                    <span style="padding-right: 10px;">Начальная дата:</span>
                    <input type="text" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                           id="datepickerBegin" maxlength="10" required />
                    <span style="padding: 10px;">Конечная дата:</span>
                    <input type="text" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>" id="datepickerEnd"
                           maxlength="10" required />
                    <button type="submit">Показать</button>
                    <div id="menu-radio" style="margin-top: 20px">
                        <input type="radio" name="historyType" id="purchaseid" value="purchase" <%=historyType.equals("purchase") ? "checked" : ""%>>
                        <label for="purchaseid">Покупки</label>
                        <input type="radio" name="historyType" id="paymentid" value="payment" <%=historyType.equals("payment") ? "checked" : ""%>>
                        <label for="paymentid">Платежи</label>
                        <input type="radio" name="historyType" id="transferid" value="transfer" <%=historyType.equals("transfer") ? "checked" : ""%>>
                        <label for="transferid">Переводы</label>
                        <input type="radio" name="historyType" id="subfeedingid" value="subfeeding" <%=historyType.equals("subfeeding") ? "checked" : ""%>>
                        <label for="subfeedingid">Активация</label>
                        <input type="radio" name="historyType" id="clientdiagramid" value="clientdiagram" <%=historyType.equals("clientdiagram") ? "checked" : ""%>>
                        <label for="clientdiagramid">Циклограмма</label>
                    </div>
                </form>
            </div>
            <c:choose>
                <c:when test="${requestScope.historyType=='purchase'}">
                    <div id="purchase">
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
                </c:when>
                <c:when test="${requestScope.historyType=='payment'}">
                    <div id="payment">
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
                </c:when>
                <c:when test="${requestScope.historyType=='transfer'}">
                    <div id="transfer">
                        <div style="line-height: 3em;">
                            <span><%=!transfersExist ? " За данный период по субсчету АП переводов не было." : ""%></span>
                        </div>
                        <%if (transfersExist) {%>
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
                </c:when>
                <c:when test="${requestScope.historyType=='subfeeding'}">
                    <div id="subfeeding">
                        <div style="line-height: 3em;">
                            <span><%=!subfeedingExist ? "За период не зарегестрированно ни одной подписки." : ""%></span>
                        </div>
                        <%if (subfeedingExist) {%>
                        <div class="simpleTable purchaseTable">
                            <div class="simpleTableHeader purchaseRow">
                                <div class="simpleCell purchaseHeaderCell wideCell">Дата и время внесения изменений</div>
                                <div class="simpleCell purchaseHeaderCell wideCell">Место внесения изменений</div>
                                <div class="simpleCell purchaseHeaderCell">Действия пользователя</div>
                                <div class="simpleCell purchaseHeaderCell">Дата вступления изменений в силу</div>
                                <div class="simpleCell purchaseHeaderCell">Состояние услуги</div>
                            </div>

                            <c:forEach items="${requestScope.subfeedings}" var="subfeeding">
                                <div class="simpleRow purchaseRow">
                                    <div class="simpleCell purchaseHeaderCell wideCell">
                                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm"
                                                        value="${subfeeding.updateDate}" />
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell wideCell"></div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        <c:out value="${subfeeding.subscriptionAction}"/>
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm"
                                                        value="${subfeeding.subscriptionActionDate}" />
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        <c:out value="${subfeeding.subscriptionState}"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <%}%>
                    </div>
                </c:when>
                <c:when test="${requestScope.historyType=='clientdiagram'}">
                    <div id="clientdiagram">
                        <div style="line-height: 3em;">
                            <span><%=!clientdiagramExist ? "За период не зарегестрированно ни одной Циклограммы" : ""%></span>
                        </div>
                        <%if (clientdiagramExist) {%>
                        <div class="simpleTable purchaseTable">
                            <div class="simpleTableHeader purchaseRow">
                                <div class="simpleCell purchaseHeaderCell wideCell">Номер циклограммы</div>
                                <div class="simpleCell purchaseHeaderCell wideCell">Дата и время внесения изменений</div>
                                <div class="simpleCell purchaseHeaderCell">Место внесения изменений</div>
                                <div class="simpleCell purchaseHeaderCell">Дата вступления изменений в силу</div>
                                <div class="simpleCell purchaseHeaderCell">Состояние циклограммы</div>
                            </div>
                            <c:forEach items="${requestScope.clientdiagrams}" var="clientdiagram">
                                <div class="simpleRow purchaseRow">
                                    <div class="simpleCell purchaseHeaderCell wideCell">
                                         ?
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell wideCell">
                                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm"
                                                        value="${clientdiagram.updateDate}" />
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        ?
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm"
                                                        value="${clientdiagram.dateActivationDiagram}" />
                                    </div>
                                    <div class="simpleCell purchaseHeaderCell">
                                        <c:out value="${clientdiagram.stateDiagram}"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <%}%>
                    </div>
                </c:when>
            </c:choose>

        </div>
    </div>
</div>
</body>
</html><%!
    private boolean isExist(HttpServletRequest request, String attribute) {
        return request.getAttribute(attribute)==null?false:(Boolean) request.getAttribute(attribute);
    }
%>