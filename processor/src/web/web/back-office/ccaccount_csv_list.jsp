<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=accounts.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.ccAccountListPage.items}" var="item">
        <h:outputText escape="false" value="#{item.contragent.idOfContragent};#{item.contragent.contragentName};#{item.idOfAccount};#{item.client.idOfClient};" /><h:outputText escape="false" value="#{item.client.contractId}" converter="contractIdConverter" /><h:outputText escape="false" value=";#{item.client.person.surname};#{item.client.person.firstName};#{item.client.person.secondName};#{item.client.contractPerson.surname};#{item.client.contractPerson.firstName};#{item.client.contractPerson.secondName}#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>