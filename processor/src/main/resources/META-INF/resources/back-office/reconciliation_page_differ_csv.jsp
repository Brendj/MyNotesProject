<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%--@elvariable id="reconciliationPage" type="ru.axetta.ecafe.processor.web.ui.contragent.ReconciliationPage"--%>
<%
    response.setHeader("Content-disposition", "inline;filename=Reconciliation_Difference.csv");
%>
<f:view>
    <a4j:repeat value="#{reconciliationPage.differencesList}" var="item" rowKeyVar="row">
        <h:outputText escape="false" value="#{item}#{mainPage.endOfLine}"/>
    </a4j:repeat>
</f:view>