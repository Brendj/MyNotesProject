<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
        taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
        taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
        taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
        taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%
    response.setHeader("Content-disposition", "inline;filename=totalservices.csv");
%><f:view>
    <h:outputText escape="false" value="Организация;Число учащихся;Число льготников;;Зафиксирован проход;;Получили льготное питание;;Получили комплексное питание;;Получили питание в буфете;;Получили питание (льготное + платное)"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <%--@elvariable id="totalServicesReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TotalServicesReportPage"--%>
    <a4j:repeat value="#{totalServicesReportPage.totalReport.items}" var="item">
        <h:outputText escape="false" value="#{item.shortName}" />
        <h:outputText escape="false" value=";#{item.totalClientsCount}" />
        <h:outputText escape="false" value=";#{item.planBenefitClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_planBenefitClientsCount}" />
        <h:outputText escape="false" value=";#{item.currentClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_currentClientsCount}" />
        <h:outputText escape="false" value=";#{item.realBenefitClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_realBenefitClientsCount}" />
        <h:outputText escape="false" value=";#{item.realPaidClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_realPaidClientsCount}" />
        <h:outputText escape="false" value=";#{item.realSnackPaidClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_realSnackPaidClientsCount}" />
        <h:outputText escape="false" value=";#{item.uniqueClientsCount}" />
        <h:outputText escape="false" value=";#{item.per_uniqueClientsCount}" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>
