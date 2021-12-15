<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=currentpositions.csv");
%><f:view>
    <h:outputText escape="false" value="Идентификатор;Дебитор;Кредитор;Сумма"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{mainPage.currentPositionsReportPage.currentPositionList}" var="item">
        <h:outputText escape="false" value="#{item.idOfPosition};#{item.contragentDebtorName};#{item.contragentCreditorName};#{item.summa}" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>