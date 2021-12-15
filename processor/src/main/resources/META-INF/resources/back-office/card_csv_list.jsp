<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=cards.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.cardListPage.items}" var="item">
        <h:outputText escape="false" value="#{item.client.contractId}" converter="contractIdConverter" /><h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.cardNo}" converter="cardNoConverter" /><h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.cardPrintedNo}" converter="cardPrintedNoConverter" /><h:outputText escape="false" value=";#{item.cardType};#{item.state};#{item.lifeState};" /><h:outputText escape="false" value="#{item.createTime}" converter="timeConverter" /><h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.validTime}" converter="timeConverter" /><h:outputText escape="false" value=";" /><h:outputText escape="false" value="#{item.issueTime}" converter="timeConverter" /><h:outputText escape="false" value=";#{item.lockReason};" /><h:outputText escape="false" value="#{item.updateTime}" converter="timeConverter" /><h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>