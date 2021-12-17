<?xml version="1.0" encoding="windows-1251"?>
<PAYMENTS>
<%@ page contentType="text/xml; charset=windows-1251" language="java" pageEncoding="windows-1251" %>
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
    response.setContentType("text/xml");
    response.setHeader("Content-disposition", "attachment;filename*=Reconciliation_Missing.xml");
%>
<f:view>
    <a4j:repeat value="#{reconciliationPage.differencesList}" var="item" rowKeyVar="row">
        <h:outputText escape="false" value="#{item.registryItem.toXmlString()}#{mainPage.endOfLine}"
                      rendered="#{item.type == 1}"/>
    </a4j:repeat>
</f:view>
</PAYMENTS>