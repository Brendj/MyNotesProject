<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.TransferSubBalanceListResult" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.TransferSubBalanceExt" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance1());
    String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getSubBalance0());
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    TransferSubBalanceListResult transfers = (TransferSubBalanceListResult) request.getAttribute("transfers");
    boolean transferExist = transfers != null && transfers.transferSubBalanceListExt != null && !transfers.transferSubBalanceListExt.getT().isEmpty();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <jsp:include page="include/header.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/transfer.css" />
    <script src="${pageContext.request.contextPath}/resources/scripts/tools.js"></script>
    <script>
        $(function () {
            $('#transferForm').on('submit', function (e) {
                var $form = $(this);
                if ($form.data('submitted') === true) {
                    // Previously submitted - don't submit again
                    e.preventDefault();
                } else {
                    var error = validateTransferForm();
                    if (error === false) {
                        // Mark it so that the next submit can be ignored
                        $form.data('submitted', true);
                    } else {
                        e.preventDefault();
                    }
                }
            });
            $("button").button();
            $('input:text').button().addClass('ui-textfield');
            $('#transferMenu').menu().addClass('ui-textfield');
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
        <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/logout'" name="logout">
            Выход
        </button>
        </span>
    </div>
    <div id="content">
        <div id="infoHeader">
            <form method="post" enctype="application/x-www-form-urlencoded"
                  action="${pageContext.request.contextPath}/sub-feeding/transfer" id="transferForm">
                <input type="hidden" name="stage" value="createTransfer"/>
                <div id="transferTable">
                    <div class="colRow">
                        <div class="leftcol">Баланс основного счета:</div>
                        <div class="rightcol"><%=subBalance0%> руб.</div>
                    </div>
                    <div class="colRow">
                        <div class="leftcol">Баланс субсчета АП:</div>
                        <div class="rightcol"><%=subBalance1%> руб.</div>
                    </div>
                    <div class="colRow">
                        <div class="leftcol">Направление перевода:</div>
                        <div class="rightcol" style="padding-left: 12px;">
                            <select name="transferDirection" id="transferMenu" size="1" required>
                                <option value="toSub">с основного счета на субсчет АП</option>
                                <option value="fromSub">с субсчета АП на основной счет</option>
                            </select>
                        </div>
                    </div>
                    <div class="colRow">
                        <div class="leftcol">Размер перевода:</div>
                        <div class="rightcol">
                            <input type="text" name="transferAmount" size="15" maxlength="10" id="transferAmountField" required />
                        </div>
                    </div>
                    <div class="messageDiv">
                        <button type="submit">Совершить перевод</button>
                    </div>
                    <div class="messageDiv errorMessage" id="errorDiv">
                        <c:if test="${not empty requestScope.subFeedingError}">
                            ${requestScope.subFeedingError}
                        </c:if>
                    </div>
                    <c:if test="${not empty requestScope.subFeedingSuccess}">
                        <div class="messageDiv successMessage">${requestScope.subFeedingSuccess}</div>
                    </c:if>
                </div>
            </form>
        </div>
        <div id="history">
            <div style="font-weight: bold;">История операций</div>
            <div style="margin-top: 20px;">
                <form method="post" enctype="application/x-www-form-urlencoded"
                      action="${pageContext.request.contextPath}/sub-feeding/transfer">
                    <span style="padding-right: 10px;">Начальная дата:</span>
                    <input type="text" name="startDate" value="<%=StringEscapeUtils.escapeHtml(startDate)%>"
                           id="datepickerBegin" maxlength="10" required />
                    <span style="padding: 10px;">Конечная дата:</span>
                    <input type="text" name="endDate" value="<%=StringEscapeUtils.escapeHtml(endDate)%>" id="datepickerEnd"
                           maxlength="10" required />
                    <button type="submit">Показать</button>
                </form>
            </div>
            <div id="transfers">
                <div style="font-weight: bold;">Переводы</div>
                <div style="line-height: 3em;">
                    <span><%=!transferExist ? " За данный период по субсчету АП переводов не было." : ""%></span>
                </div>
                <%
                    if (transferExist) {
                %>
                <div class="simpleTable purchaseTable">
                    <div class="simpleTableHeader purchaseRow">
                        <div class="simpleCell purchaseHeaderCell wideCell">Номер счета списания</div>
                        <div class="simpleCell purchaseHeaderCell wideCell">Номер счета пополнения</div>
                        <div class="simpleCell purchaseHeaderCell">Дата</div>
                        <div class="simpleCell purchaseHeaderCell">Сумма</div>
                    </div>
                    <%
                        for (TransferSubBalanceExt transferSubBalanceExt : transfers.transferSubBalanceListExt.getT()) {
                            String date = tf.format(transferSubBalanceExt.getCreateTime());
                            String sum = CurrencyStringUtils.copecksToRubles(transferSubBalanceExt.getTransferSum());
                    %>
                    <div class="simpleRow purchaseRow">
                        <div class="purchaseCell simpleCell"><%=transferSubBalanceExt.getBalanceBenefactor()%></div>
                        <div class="purchaseCell simpleCell"><%=transferSubBalanceExt.getBalanceBeneficiary()%></div>
                        <div class="purchaseCell simpleCell"><%=date%></div>
                        <div class="purchaseCell simpleCell sum"><%=sum%></div>
                    </div>
                    <%
                        }
                    %>
                </div>
                <%
                    }
                %>
            </div>
        </div>
    </div>
</div>
</body>
</html>