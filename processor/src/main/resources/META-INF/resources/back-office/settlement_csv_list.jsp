<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=settlements.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.settlementListPage.items}" var="item">
        <h:outputText escape="false" value="#{item.idOfSettlement};" />
        <h:outputText escape="false" value="#{item.contragentPayer.idOfContragent};#{item.contragentPayer.contragentName};" />
        <h:outputText escape="false" value="#{item.contragentReceiver.idOfContragent};#{item.contragentReceiver.contragentName};" />
        <h:outputText escape="false" value="#{item.createdDate}" converter="timeConverter" /><h:outputText escape="false" value=";" />
        <h:outputText escape="false" value="#{item.paymentDate}" converter="timeConverter" /><h:outputText escape="false" value=";" />
        <h:outputText escape="false" value="#{item.paymentDoc};" />
        <h:outputText escape="false" value="#{item.summa};" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>