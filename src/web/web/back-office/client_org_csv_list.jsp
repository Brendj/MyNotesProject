<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=client_org.csv");
%><f:view>
    <h:outputText escape="false" value="Номер учреждения;Название учреждения;Количество учащихся;Количество учащихся (balance > 0);Количество учащихся (balance == 0);Количество учащихся (balance < 0);Сумма денег;Сумма положительных балансов;Сумма отрицательных балансов"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{mainPage.clientReportPage.clientReport.clientItems}" var="item">
        <h:outputText escape="false" value="#{item.idOfOrg};#{item.officialName};#{item.clientCount};#{item.clientWithPositiveBalanceCount};#{item.clientWithNullBalanceCount};#{item.clientWithNegativeBalanceCount};#{item.balanceSum};#{item.posBalanceSum};#{item.negBalanceSum}" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>