<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.*" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    final  Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.subfeeding.pages.plain_jsp");
    @SuppressWarnings("unchecked")
    //SubFeedingResult sf = (SubFeedingResult) request.getAttribute("subscriptionFeeding");
    SubscriptionFeedingExt sf = (SubscriptionFeedingExt) request.getAttribute("subscriptionFeeding");
    CycleDiagramExt currentCycleDiagram = (CycleDiagramExt) request.getAttribute("currentCycleDiagram");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    final Long subBalance0 = client.getSubBalance0();
    final Long subBalance1 = client.getSubBalance1();
    String action = sf == null ? "demo" : sf.getDateActivate() == null ? "create" : "edit";
    String activateDate = (String) request.getAttribute("dateActivate");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <jsp:include page="include/header.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/scripts/tools.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/plan.css"/>
    <script>
        var subBalance0 = <%=subBalance0%>;
        var subBalance1 = <%=subBalance1%>;
        var total = 0;
        var minActivateDate = new Date('<%=activateDate%>'.replace(/(\d+)\.(\d+)\.(\d+)/, '$2/$1/$3'));
        function formatSum(sum){
            return (sum/100).toFixed(2).replace(".",",")+" руб";
        }
        function updateTotalValue() {
            $('#totalweek').text(formatSum(total));$('#totalmonth').text(formatSum(4 * total));
            //$('#totalweek1').text(formatSum(subBalance1 - total));//$('#totalmonth1').text(formatSum(subBalance1 - 4 * total));
        }
        function addUp(choice){
            var num = parseInt(choice.value);
            total += choice.checked?num:-num;
            updateTotalValue();
        }
        $(function () {
            $("#complexForm").preventDoubleSubmission();
            $('input:text').button().addClass('ui-textfield');
            $("button").button();
            var $cbs = $('.simpleTable input[type="checkbox"]');
            $cbs.each(function() {
                total += this.checked?parseInt(this.value):0;
            });
            updateTotalValue();
            $('#disableButton').button().css({
                'background': 'rgb(152, 152, 152)',
                'color': 'white'
            }).click(function (e) {
                        e.preventDefault();
                        return false;
                    });
            var activateDate = $("#activateDate").datepicker();
            activateDate.datepicker("option", "minDate", minActivateDate);
        });
    </script>
    <style type="text/css">
        fieldset legend {text-align: left; }
        fieldset legend div { margin: 0.3em 0.5em; }
    </style>
</head>
<body>
<div class="bodyDiv">
    <div class="header">
        <span class="contract">
            <%=ContractIdFormat.format(client.getContractId())%></span>
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
        <form method="post" enctype="application/x-www-form-urlencoded" id="complexForm"
              action="${pageContext.request.contextPath}/sub-feeding/<%=action%>">
        <div id="infoHeader">
            <h1>Активировать подписку абонементного питания?</h1>
            <h2>Для продолжения необходимо заполнить циклограмму.</h2>
            <c:if test="${requestScope.complexes==null}">
                <label for="activateDate" style="padding-right: 10px;">Дата активации подписки:</label>
                <input type="text" name="activateDate" value="<%=StringEscapeUtils.escapeHtml(activateDate)%>"
                       id="activateDate" maxlength="10" required/>
            </c:if>
            <c:if test="${requestScope.complexes!=null}">
                <label for="activateDate" style="padding-right: 10px;">Дата активации циклограммы:</label>
                <input type="text" name="activateDate"
                       value="<%=StringEscapeUtils.escapeHtml(activateDate)%>"
                       id="activateDate" maxlength="10" required/>
            </c:if>
            <c:if test="${not empty requestScope.subFeedingError}">
                <div class="messageDiv errorMessage">${requestScope.subFeedingError}</div>
            </c:if>
            <c:if test="${not empty requestScope.subFeedingSuccess}">
                <div class="messageDiv successMessage">${requestScope.subFeedingSuccess}</div>
            </c:if>

        </div>
        <div id="cycleDiagram">
                <div class="simpleTable">
                    <div class="simpleRow simpleTableHeader">
                        <div class="simpleCell wideCell">Комплекс</div>
                        <div class="simpleCell">ПН</div>
                        <div class="simpleCell">ВТ</div>
                        <div class="simpleCell">СР</div>
                        <div class="simpleCell">ЧТ</div>
                        <div class="simpleCell">ПТ</div>
                        <div class="simpleCell">СБ</div>
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
                    <div class="simpleRow">
                        <div class="simpleCell complexName">
                            <%=complex.getComplexName() + " - " + CurrencyStringUtils
                                    .copecksToRubles(complex.getCurrentPrice()) + " руб"%>
                        </div>
                <%
                    for (int i = 1; i <= 6; i++) {
                        String key = complex.getIdOfComplex() + "_" + i;
                        boolean checked = activeComplexes != null && activeComplexes.get(i)
                                .contains(String.valueOf(complex.getIdOfComplex()));
                %>
                        <div class="simpleCell">
                            <label><input type="checkbox"
                                          name="complex_option_<%=key%>" value="<%=complex.getCurrentPrice()%>"
                                          title="" <%=checked ? "checked" : ""%>
                                          onchange="addUp(this);"/></label>
                        </div>
                <%
                    }
                %>
                    </div>
            <%
                }
            %>
                    <div class="simpleTableFooter">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all"><div>Расчетная информация</div></legend>
                            <div class="simpleTable" style="margin: 0">
                                <%--<div class="simpleRow simpleTableHeader">
                                    <div class="simpleCell"></div>
                                    <div class="simpleCell" style="text-align: right;">Сумма</div>
                                    <div class="simpleCell wideCell" style="text-align: right;">Баланс за вычетом суммы</div>
                                </div>--%>
                                <div class="simpleRow">
                                    <div class="simpleCell wideCell" style="text-align: left;">Расчетная стоимость 1-ой недели:</div>
                                    <div class="simpleCell" id="totalweek" style="text-align: right;"></div>
                                    <%--<div class="simpleCell" id="totalweek1" style="text-align: right;"></div>--%>
                                </div>
                                <div class="simpleRow" style="border: 0">
                                    <div class="simpleCell" style="text-align: left;">Расчетная стоимость 4-х недель:</div>
                                    <div class="simpleCell" id="totalmonth" style="text-align: right;"></div>
                                    <%--<div class="simpleCell" id="totalmonth1" style="text-align: right;"></div>--%>
                                </div>
                            </div>
                        </fieldset>

                        <c:if test="${requestScope.subscriptionFeeding==null}">
                            <button id="disableButton">Активировать</button>
                            <div style="font-size: 0.8em;">
                                Нажимая на кнопку "Активировать", Вы соглашаетесь с условиями предоставления услуги.
                            </div>
                        </c:if>
                        <c:if test="${requestScope.subscriptionFeeding!=null}">
                            <c:if test="${requestScope.subscriptionFeeding.dateCreateService!=null}">
                                <c:if test="${requestScope.subscriptionFeeding.dateCreateService!=null && requestScope.subscriptionFeeding.dateActivate==null}">
                                    <button type="submit" name="activate">Активировать</button>
                                    <div style="font-size: 0.8em;">
                                        Нажимая на кнопку "Активировать", Вы соглашаетесь с условиями предоставления услуги.
                                    </div>
                                </c:if>
                                <c:if test="${requestScope.subscriptionFeeding.dateCreateService!=null && requestScope.subscriptionFeeding.dateActivate!=null}">
                                    <button type="submit" name="edit">Сохранить изменения</button>
                                </c:if>
                            </c:if>
                            <%--<c:if test="${!requestScope.subscriptionFeeding.suspended}">--%>
                                <%--<button type="submit" name="edit">Сохранить изменения</button>--%>
                            <%--</c:if>--%>
                        </c:if>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>