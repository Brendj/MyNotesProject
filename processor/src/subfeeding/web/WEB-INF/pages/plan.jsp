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
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
    final  Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.subfeeding.pages.plain_jsp");
    @SuppressWarnings("unchecked")
    SubFeedingResult sf = (SubFeedingResult) request.getAttribute("subscriptionFeeding");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    final Long subBalance0 = client.getSubBalance0();
    final Long subBalance1 = client.getSubBalance1();
    String action = sf.getIdOfSubscriptionFeeding() == null ? "activate" : "edit";
    String dateActivate = (String) request.getAttribute("dateActivate");
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
        var minDateActivate = new Date('<%=dateActivate%>'.replace(/(\d+)\.(\d+)\.(\d+)/, '$2/$1/$3'));
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
            var dateActivate = $("#dateActivate").datepicker(<%=(sf.getIdOfSubscriptionFeeding() != null?"disable":"")%>);
            dateActivate.datepicker("option", "minDate", minDateActivate);
            var $cbs = $('.simpleTable input[type="checkbox"]');
            $cbs.each(function() {
                total += this.checked?parseInt(this.value):0;
            });
            updateTotalValue();
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
             <c:if test="${requestScope.subscriptionFeeding.idOfSubscriptionFeeding!=null}">
                 <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/view'">Вернуться
                 </button>
             </c:if>
            <button onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/logout'" name="logout">Выход
            </button>
        </span>
    </div>
    <div id="content">
        <form method="post" enctype="application/x-www-form-urlencoded" id="complexForm"
              action="${pageContext.request.contextPath}/sub-feeding/<%=action%>">
        <div id="infoHeader">

            <c:if test="${requestScope.subscriptionFeeding.idOfSubscriptionFeeding==null}">
                <h1>Активировать подписку абонементного питания?</h1>
                <h2>Для продолжения необходимо заполнить циклограмму.</h2>
                <label for="dateActivate" style="padding-right: 10px;">Дата начала подписки на услугу АП:</label>
                <input type="text" name="dateActivate" value="<%=StringEscapeUtils.escapeHtml(dateActivate)%>"
                       id="dateActivate" maxlength="10" required/>
            </c:if>
            <c:if test="${requestScope.subscriptionFeeding.idOfSubscriptionFeeding!=null}">
                <h1>Редактирование циклограммы питания</h1>
                <label for="dateActivate" style="padding-right: 10px;">Дата начала подписки на услугу АП:</label>
                <input type="text" name="dateActivate" value="<%=StringEscapeUtils.escapeHtml(dateActivate)%>"
                       id="dateActivate" maxlength="10" style="opacity: 1" disabled="disabled" required />
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
                            <input type="checkbox" name="complex_option_<%=key%>" value="<%=complex.getCurrentPrice()%>" title=""
                                    <%=checked ? "checked" : ""%> onchange="addUp(this);"/>
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

                        <c:if test="${requestScope.subscriptionFeeding.idOfSubscriptionFeeding==null}">
                            <button type="button" onclick="location.href = '${pageContext.request.contextPath}/sub-feeding/transfer'">
                                Перевод средств
                            </button>
                            <button type="submit" name="activate">Активировать</button>
                            <div style="font-size: 0.8em;">
                                Нажимая на кнопку "Активировать", Вы соглашаетесь с условиями предоставления услуги.
                            </div>
                        </c:if>
                        <c:if test="${requestScope.subscriptionFeeding.idOfSubscriptionFeeding!=null}">
                            <c:if test="${!requestScope.subscriptionFeeding.suspended}">
                                <button type="submit" name="edit">Сохранить изменения</button>
                            </c:if>
                        </c:if>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>