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
<%@ page import="ru.axetta.ecafe.processor.web.subfeeding.SubscriptionFeeding" %>
<%@ page import="ru.axetta.ecafe.processor.web.subfeeding.CycleDiagram" %>
<%
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    DateFormat tf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    tf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    final  Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.subfeeding.pages.plain_jsp");
    @SuppressWarnings("unchecked")
    SubscriptionFeeding subscriptionFeeding = (SubscriptionFeeding) request.getAttribute("subscriptionFeeding");
    ClientSummaryExt client = (ClientSummaryExt) request.getAttribute("client");
    final Long subBalance0 = client.getSubBalance0();
    final Long subBalance1 = client.getSubBalance1();
    String action = doAction(subscriptionFeeding);
    String activateDate = (String) request.getAttribute("dateActivate");
    //String currentActivateDate = (String) request.getAttribute("currentActivateDate");
    @SuppressWarnings("unchecked")
    List<CycleDiagram> cycleDiagrams = (List<CycleDiagram>) request.getAttribute("cycleDiagrams");
    @SuppressWarnings("unchecked")
    List<ComplexInfoExt> complexes = (List<ComplexInfoExt>) request.getAttribute("complexes");
    if (complexes == null) {
        complexes = Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <jsp:include page="include/header.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/scripts/tools.js"></script>
    <script src="${pageContext.request.contextPath}/resources/scripts/jquery.bxslider.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/plan.css"/>
    <script>
        var subBalance0 = <%=subBalance0%>;
        var subBalance1 = <%=subBalance1%>;
        var totals = {};
        var slider;
        var minActivateDate = new Date('<%=activateDate%>'.replace(/(\d+)\.(\d+)\.(\d+)/, '$2/$1/$3'));
        function formatSum(sum){
            return (sum/100).toFixed(2).replace(".",",")+" руб";
        }
        function updateTotalValue(cycleDiagramid, total) {
            $("."+cycleDiagramid+' .totalweek').text(formatSum(total));
            $("."+cycleDiagramid+' .totalmonth').text(formatSum(4 * total));
        }
        function addUp(choice, cycleDiagramid){
            //console.log("before: "+totals[cycleDiagramid]);
            var num = parseInt(choice.value);
            //console.log("num: "+num);
            totals[cycleDiagramid] += choice.checked?num:-num;
            updateTotalValue(cycleDiagramid, totals[cycleDiagramid]);
            //var $header = $('.' + cycleDiagramid + ' .active-period');
            //var index = $header.html().indexOf("*");
            //console.log("index: "+index);
            //if(index<=0){
            //    $header.css({"color":"red"}).append("*");
            //}
            //$('input:hidden.'+cycleDiagramid).val(1); // Ставим метку что объект изменен
            //console.log("after: "+totals[cycleDiagramid]);
        }
        $(function () {
            $("#complexForm").preventDoubleSubmission();
            $('input:text').button().addClass('ui-textfield');
            $("button").button();
            $('#disableButton').button().css({
                'background': 'rgb(152, 152, 152)',
                'color': 'white'
            }).click(function (e) {
                        e.preventDefault();
                        return false;
                    });
            slider = $('.bxslider').bxSlider({
                pager: false,
                infiniteLoop: false,
                hideControlOnEnd: true,
                nextSelector: '.slider-next',
                prevSelector: '.slider-prev',
                nextText: 'Следующий',
                prevText: 'Предыдущий'
            });
            $('.slider-next').button().click(function(e){
                $(".messageDiv").hide();
                slider.goToNextSlide();
                if(slider.getCurrentSlide()==slider.getSlideCount()-1){
                    $('.slider-next').hide();
                }
                $('.slider-prev').show();
                e.preventDefault();
                return false;
            });

            $('.slider-prev').button().click(function(e){
                $(".messageDiv").hide();
                slider.goToPrevSlide();
                if(slider.getCurrentSlide()==0){
                    $('.slider-prev').hide();
                }
                $('.slider-next').show();
                e.preventDefault();
                return false;
            }).hide();
            var activateDate = $("#activateDate").datepicker();
            activateDate.datepicker("option", "minDate", minActivateDate);
            // прячем сообщения об ошибках и увидомлений
            $(".successMessage").show().delay(5000).fadeOut();
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
            <%
                if(cycleDiagrams.size()<2){
            %>
            <div class="infoHeader">
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
            <div class="cycleDiagram">
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
                        Map<Integer, List<String>> activeComplexes1 = (Map<Integer, List<String>>) request.getAttribute("activeComplexes");
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
                                boolean checked = activeComplexes1 != null && activeComplexes1.get(i)
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
                                <div class="simpleRow">
                                    <div class="simpleCell wideCell" style="text-align: left;">Расчетная стоимость 1-ой недели:</div>
                                    <div class="simpleCell" id="totalweek" style="text-align: right;"></div>
                                </div>
                                <div class="simpleRow" style="border: 0">
                                    <div class="simpleCell" style="text-align: left;">Расчетная стоимость 4-х недель:</div>
                                    <div class="simpleCell" id="totalmonth" style="text-align: right;"></div>
                                </div>
                            </div>
                        </fieldset>

                        <c:if test="${requestScope.subscriptionFeeding==null}">
                            <button id="disableButton">Активировать</button>
                            <div style="font-size: 0.8em;">
                                Для активации циклограммы необходимо обратиться в образовательную организацию и подключить услугу.
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
                        </c:if>
                    </div>
                </div>
            </div>
            <%
                } else {
            %>
            <div class="cycleDiagram">
            <div class="infoHeader">
                <h1>Активировать подписку абонементного питания?</h1>
                <h2>Для продолжения необходимо заполнить циклограмму.</h2>
                <c:if test="${requestScope.complexes==null}">
                    <label for="activateDate"
                           style="padding-right: 10px;">Дата активации подписки:</label>
                    <input type="text" name="activateDate" id="activateDate"
                           value="<%=StringEscapeUtils.escapeHtml(activateDate)%>"
                           class="activateDate" maxlength="10" required/>
                </c:if>
                <c:if test="${requestScope.complexes!=null}">
                    <label for="activateDate"
                           style="padding-right: 10px;">Дата активации циклограммы:</label>
                    <input type="text" name="activateDate" id="activateDate"
                           value="<%=StringEscapeUtils.escapeHtml(activateDate)%>"
                           class="activateDate" maxlength="10" required/>
                </c:if>
                <c:if test="${not empty requestScope.subFeedingError}">
                    <div class="messageDiv errorMessage">${requestScope.subFeedingError}</div>
                </c:if>
                <c:if test="${not empty requestScope.subFeedingSuccess}">
                    <div class="messageDiv successMessage">${requestScope.subFeedingSuccess}</div>
                </c:if>
            </div>
            <ul class="bxslider">
                <%
                    for (CycleDiagram cycleDiagram: cycleDiagrams){
                %>
                <li class="<%=cycleDiagram.getGlobalId()%>">
                        <div style="text-align: center;">
                            <div class="active-period"  style="margin-top: -15px;">
                                <table style="width: 100%;">
                                    <tr style="border-collapse:collapse;border-spacing:0;">
                                        <td rowspan="2" style="text-align: left; width: 140px; padding: 0">
                                            <div class="slider-prev" style="float: left;"></div>
                                        </td>
                                        <td rowspan="2" style="text-align: left; padding: 0">
                                            <b>Циклограмм_<%=cycleDiagram.getGlobalId()%></b>
                                        </td>
                                        <td style="text-align: left; padding: 0">
                                            <b>Дата начала действия циклограммы:</b>
                                        </td>
                                        <td style="text-align: left; padding: 0">
                                            <b><%=cycleDiagram.getStartDate()%></b>
                                        </td>
                                        <td rowspan="2" style="text-align: left; width: 140px; padding: 0">
                                            <div class="slider-next" style="float: right; margin-right: 0;"></div>
                                        </td>
                                    </tr>
                                    <tr style="border-collapse:collapse;border-spacing:0;">
                                        <td style="text-align: left; padding: 0">
                                            <b>Дата окончания действия циклограммы:</b>
                                        </td>
                                        <td style="text-align: left; padding: 0">
                                            <b><%=cycleDiagram.getEndDate()%></b>
                                        </td>
                                    </tr>
                                </table>
                            </div>

                        </div>
                        <div class="simpleTable" style="clear: both">
                            <div class="simpleRow simpleTableHeader">
                                <div class="simpleCell wideCell">Комплекс</div>
                                <div class="simpleCell" style="width: 80px">ПН</div>
                                <div class="simpleCell" style="width: 80px">ВТ</div>
                                <div class="simpleCell" style="width: 80px">СР</div>
                                <div class="simpleCell" style="width: 80px">ЧТ</div>
                                <div class="simpleCell" style="width: 80px">ПТ</div>
                                <div class="simpleCell" style="width: 80px">СБ</div>
                            </div>
                            <%
    // TODO: необходимо исбавиться от передачи элементов коллекции так как они не сериализуются
                                @SuppressWarnings("unchecked")
                                Map<Integer, List<String>> activeComplexes = cycleDiagram.splitPlanComplexes();
                                for (ComplexInfoExt complex : complexes) {
                            %>
                            <div class="simpleRow">
                                <div class="simpleCell complexName">
                                    <%=complex.getIdOfComplex()%>:<%=complex.getComplexName() + " &mdash; " + CurrencyStringUtils.copecksToRubles(complex.getCurrentPrice()) + " руб"%>
                                </div>
                                <%
                                    for (int i = 1; i <= 6; i++) {
                                        String key = complex.getIdOfComplex() + "_" + i + "_"+cycleDiagram.getGlobalId();
                                        boolean checked = containsComplex(activeComplexes, complex, i);
                                        if(checked) {
                                            cycleDiagram.addTotalSum(complex.getCurrentPrice());
                                        }
                                %>
                                <div class="simpleCell" style="width: 80px">
                                    <label><input type="checkbox"
                                                  name="complex_option_<%=key%>" value="<%=complex.getCurrentPrice()%>"
                                                  title="" <%=checked ? "checked" : ""%>
                                                  onchange="addUp(this, <%=cycleDiagram.getGlobalId()%>);"/></label>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                            <%
                                }
                            %>
                            <script>
                                totals[<%=cycleDiagram.getGlobalId()%>]=<%=cycleDiagram.getTotalSum()%>;
                            </script>
                            <div class="simpleTableFooter">
                                <fieldset class="ui-widget ui-widget-content">
                                    <legend class="ui-widget-header ui-corner-all"><div>Расчетная информация</div></legend>
                                    <div class="simpleTable" style="margin: 0">
                                        <div class="simpleRow">
                                            <div class="simpleCell wideCell" style="text-align: left;">Расчетная стоимость 1-ой недели:</div>
                                            <div class="simpleCell totalweek" style="text-align: right;">
                                                <%=cycleDiagram.getWeekPrices()%> руб
                                            </div>
                                        </div>
                                        <div class="simpleRow" style="border: 0">
                                            <div class="simpleCell" style="text-align: left;">Расчетная стоимость 4-х недель:</div>
                                            <div class="simpleCell totalmonth" style="text-align: right;">
                                                <%=cycleDiagram.getMonthPrices()%> руб
                                            </div>
                                        </div>
                                    </div>
                                </fieldset>

                                <c:if test="${requestScope.subscriptionFeeding==null}">
                                    <button id="disableButton">Активировать</button>
                                    <div style="font-size: 0.8em;">
                                        Для активации циклограммы необходимо обратиться в образовательную организацию и подключить услугу.
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
                                            <button type="submit" class="edit_btn" name="edit_<%=cycleDiagram.getGlobalId()%>">
                                                Сохранить изменения
                                            </button>
                                        </c:if>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>

                </li>
                <%
                    }
                %>
            </ul>
            </div>
            <%
                }
            %>

        </form>
    </div>
</div>
</body>
</html>
<%!
    private boolean containsComplex(Map<Integer, List<String>> activeComplexes1, ComplexInfoExt complex, int i) {
        return activeComplexes1 != null && activeComplexes1.get(i)
                .contains(String.valueOf(complex.getIdOfComplex()));
    }

    private String doAction(SubscriptionFeeding subscriptionFeeding) {
        return subscriptionFeeding == null || subscriptionFeeding.getDateActivate() == null ? "create" : "edit";
    }
%>