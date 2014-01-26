<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.ComplexInfoExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubFeedingResult" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/WebContent/css/flick/jquery-ui-1.10.3.custom.min.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/plan.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/complexTable.css"/>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-1.10.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/tools.js"></script>
    <script>
        $(function () {
            $("#complexForm").preventDoubleSubmission();
            $("button").button();
        });
    </script>
</head>
<body>
<div class="bodyDiv">
<%
    @SuppressWarnings("unchecked")
    SubFeedingResult sf = (SubFeedingResult) request.getAttribute("subscriptionFeeding");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    String action = sf.getIdOfSubscriptionFeeding() == null ? "activate" : "edit";
%>
    <div class="header">
        <span class="contract">
            <%=ContractIdFormat.format(client.getContractId())%></span>
        <span class="contract" style="padding-left: 20px;"><%=client.getFullName()%></span>
        <span style="float: right;">
            <button onclick="location.href = '${pageContext.request.contextPath}/office/logout'" name="logout">Выход
            </button>
        </span>
    </div>
    <div id="content">
        <div id="infoHeader">
<%
    if (sf.getIdOfSubscriptionFeeding() == null) {
%>
            <h1>Активировать подписку абонементного питания?</h1>
            <h2>Для продолжения необходимо заполнить циклограмму.</h2>
<%
    } else {
%>
            <h1>Редактирование циклограммы питания</h1>
<%  } %>
            <c:if test="${not empty requestScope.subFeedingError}">
                <div class="errorMessage">${requestScope.subFeedingError}</div>
            </c:if>
            <c:if test="${not empty requestScope.subFeedingSuccess}">
                <div class="successMessage">${requestScope.subFeedingSuccess}</div>
            </c:if>
        </div>
        <div id="cycleDiagram">
            <form method="post" enctype="application/x-www-form-urlencoded" id="complexForm"
                  action="${pageContext.request.contextPath}/office/<%=action%>">
                <div class="newTable">
                    <div class="tableHeader">
                        <div class="tableCell" style="text-align: center;">Комплекс</div>
                        <div class="tableCell">ПН</div>
                        <div class="tableCell">ВТ</div>
                        <div class="tableCell">СР</div>
                        <div class="tableCell">ЧТ</div>
                        <div class="tableCell">ПТ</div>
                        <div class="tableCell">СБ</div>
                    </div>
            <%
                @SuppressWarnings("unchecked")
                List<ComplexInfoExt> complexes = (List<ComplexInfoExt>) request.getAttribute("complexes");
                if (complexes == null) {
                    complexes = Collections.emptyList();
                }
                @SuppressWarnings("unchecked")
                Map<Integer, List<String>> activeComplexes = (Map<Integer, List<String>>) request.getAttribute("activeComplexes");
                for (ComplexInfoExt complex : complexes) {
            %>
                    <div class="tableRow">
                        <div class="tableCell complexName">
                            <%=complex.getComplexName() + " - " + CurrencyStringUtils
                                    .copecksToRubles(complex.getCurrentPrice()) + " руб"%>
                        </div>
                <%
                    for (int i = 1; i <= 6; i++) {
                        String key = complex.getIdOfComplex() + "_" + i;
                        boolean checked = activeComplexes != null && activeComplexes.get(i)
                                .contains(String.valueOf(complex.getIdOfComplex()));
                %>
                        <div class="tableCell">
                            <input type="checkbox" name="complex_option_<%=key%>" value="<%=key%>" title=""
                                    <%=checked ? "checked" : ""%> />
                        </div>
                <%
                    }
                %>
                    </div>
            <%
                }
            %>
                    <div class="tableFooter">
            <%
                if (sf.getIdOfSubscriptionFeeding() == null) {
            %>
                        <button type="submit" name="activate">Активировать</button>
                        <div style="font-size: 0.8em;">Нажимая на данную кнопку, Вы согласны с условиями предоставления
                            услуги.
                        </div>
            <%  } else {

            %>
                        <button type="button" onclick="location.href = '${pageContext.request.contextPath}/office/view'" name="back">
                            Вернуться к подписке
                        </button>
                        <button type="submit" name="edit">Сохранить изменения</button>
            <%  } %>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>