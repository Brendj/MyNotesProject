<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=free_complexes.csv");
%><f:view>
    <h:outputText escape="false" value="Организация;Название;Цена за ед;Скидка на ед;Кол-во;Сумма без скидки;Сумма скидки;Итоговая сумма;Время первой продажи;Время последней продажи"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{mainPage.freeComplexReportPage.complexReport.complexItems}" var="item">
        <h:outputText escape="false" value="#{item.officialName};#{item.menuDetailName};#{item.rPrice};#{item.discount};#{item.qty};#{item.sumPrice};#{item.sumPriceDiscount};#{item.total}" />
        <h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.firstTimeSale}" converter="timeConverter" />
        <h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.lastTimeSale}" converter="timeConverter" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>