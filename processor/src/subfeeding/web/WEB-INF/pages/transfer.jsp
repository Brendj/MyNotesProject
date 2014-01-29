<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryExt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/WebContent/css/flick/jquery-ui-1.10.3.custom.min.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/transfer.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/tables.css" />
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-1.10.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery.ui.datepicker-ru.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/tools.js"></script>
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
            $('input:text').button().css({
                'font': 'inherit',
                'color': 'inherit',
                'text-align': 'left',
                'outline': 'none',
                'cursor': 'text'
            });
        });
    </script>
</head>
<body>
<%
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String subBalance1 = CurrencyStringUtils.copecksToRubles(client.getSubBalance1());
    String subBalance0 = CurrencyStringUtils.copecksToRubles(client.getSubBalance0());
%>
<div class="bodyDiv">
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
            <form method="post" enctype="application/x-www-form-urlencoded"
                  action="${pageContext.request.contextPath}/office/transfer" id="transferForm">
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
                        <div class="rightcol">
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
                        <button type="button"
                                onclick="location.href = '${pageContext.request.contextPath}/office/view'">Вернуться назад
                        </button>
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
    </div>
</div>
</body>
</html>