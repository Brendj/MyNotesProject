<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ComplexInfo" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/subfeeding/css/complexTable.css"/>
</head>
<body>
<div class="textDiv" style="position: relative; text-align: right; padding-right: 50px;">
    <form method="post" action="${pageContext.request.contextPath}/sub-feeding/logout">
        <input type="submit" name="logout" value="Выход" />
    </form>
</div>
<%
    @SuppressWarnings("unchecked")
    SubscriptionFeeding sf = (SubscriptionFeeding) request.getAttribute("subscriptionFeeding");
    String action = sf == null ? "activate" : "edit";
%>
<c:if test="${empty requestScope.subscriptionFeeding}">
    <div class="textDiv">Активировать подписку абонементного питания? Нажимая на данную кнопку Вы согласны с условиями
        предоставления услуги.
    </div>
</c:if>
<c:if test="${not empty requestScope.subFeedingError}">
    <div class="textDiv" style="color: red">${requestScope.subFeedingError}</div>
</c:if>
<c:if test="${not empty requestScope.subFeedingSuccess}">
    <div class="textDiv" style="color: green">${requestScope.subFeedingSuccess}</div>
</c:if>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="${pageContext.request.contextPath}/sub-feeding/<%=action%>">
    <table class="customTable">
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
            @SuppressWarnings("unchecked")
            Map<Integer, List<String>> activeComplexes = (Map<Integer, List<String>>) request.getAttribute("activeComplexes");
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
                    boolean checked = activeComplexes != null && activeComplexes.get(i)
                            .contains(String.valueOf(complex.getIdOfComplex()));
            %>
            <td>
                <input type="checkbox" name="complex_option_<%=key%>" value="<%=key%>" title=""
                        <%=checked ? "checked" : ""%> />
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
<%
    if (sf == null) {
%>
            <td align="center" colspan="8">
                <input type="submit" class="reopenButton" name="activate" value="Активировать" />
            </td>
<%
    } else {
%>
            <td align="right" colspan="3">
                <input type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/view'"
                       class="reopenButton" name="back" value="Вернуться к подписке" />
            </td>
            <td align="left" colspan="5">
                <input type="submit" class="reopenButton" name="edit" value="Сохранить изменения" />
            </td>
<%
    }
%>
        </tr>
    </table>
</form>
</body>
</html>