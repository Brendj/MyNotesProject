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
    <h:outputText escape="false" value="Организация;Число учащихся;Получают льготное питание;;Зафиксирован проход;;Получили льготное питание;;Получили платное питание;;Получили питание (льготное + платное)"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{totalServicesReportPage.totalReport.items}" var="item">
        <h:outputText escape="false" value="#{item.shortName}" />
        <h:outputText escape="false" value=";#{item.data['totalClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['planBenefitClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_planBenefitClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['currentClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_currentClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['realBenefitClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_realBenefitClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['realPayedClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_realPayedClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['realSnackPayedClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_realSnackPayedClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['uniqueClientsCount']}" />
        <h:outputText escape="false" value=";#{item.data['per_uniqueClientsCount']}" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>
