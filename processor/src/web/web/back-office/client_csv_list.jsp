<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=clients.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.clientListPage.items}" var="item">
        <h:outputText escape="false" value="#{item.contractId}" converter="contractIdConverter" />
        <h:outputText escape="false" value=";#{item.contractState};#{item.contractPerson.surname};#{item.contractPerson.firstName};#{item.contractPerson.secondName};#{item.contractPerson.idDocument};#{item.person.surname};#{item.person.firstName};#{item.person.secondName};#{item.person.idDocument};#{item.address};#{item.phone};#{item.mobile};#{item.email};#{item.payForSMS};#{item.notifyViaSMS};#{item.notifyViaEmail};#{item.notifyViaPUSH}" />
        <h:outputText escape="false" value=";" />
        <h:outputText escape="false" value="#{item.updateTime}" converter="timeConverter" />
        <h:outputText escape="false" value=";#{item.clientGroupName};"/>
        <h:outputText escape="false" value=";#{item.org.shortName}"/>
        <h:outputText escape="false" value=";" />
        <h:outputText escape="false" value="#{item.balance}" converter="copeckSumConverter"/>
        <h:outputText escape="false" value=";"/>
        <h:outputText escape="false" value="#{item.limit}" converter="copeckSumConverter" />
        <h:outputText escape="false" value=";"/>
        <h:outputText escape="false" value="#{item.expenditureLimit}" converter="copeckSumConverter" />
        <h:outputText escape="false" value=";#{item.discountMode}"/>
        <h:outputText escape="false" value=";#{item.guid}"/>
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>