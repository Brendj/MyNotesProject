<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

Автоматически устанавливается 23-59 для второй даты
<%--@elvariable id="RNIPLaunchService" type="ru.axetta.ecafe.processor.controllers.RNIPLaunchService"--%>
<h:panelGrid columns="4">
    <h:outputLabel value="Период: "/>
    <rich:calendar value="#{mainPage.serviceRNIPPage.startDate}" />
    <h:outputLabel value=" - "/>
    <rich:calendar value="#{mainPage.serviceRNIPPage.endDate}" />

    <a4j:commandButton value="Run" action="#{mainPage.serviceRNIPPage.run()}"/>
</h:panelGrid>
