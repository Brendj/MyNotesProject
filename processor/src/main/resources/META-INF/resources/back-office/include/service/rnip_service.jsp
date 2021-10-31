<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="RNIPLaunchService" type="ru.axetta.ecafe.processor.controllers.RNIPLaunchService"--%>
<h:panelGrid columns="4">
    <h:outputLabel value="Период: "/>
    <h:inputText value="#{mainPage.serviceRNIPPage.startDate}" />
    <h:outputLabel value=" - "/>
    <h:inputText value="#{mainPage.serviceRNIPPage.endDate}" />

    <a4j:commandButton value="Run" action="#{mainPage.serviceRNIPPage.run()}" status="serviceRNIPStatus"/>
    <a4j:status id="serviceRNIPStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
</h:panelGrid>
<h:panelGrid columns="1">
<rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
               warnClass="warn-messages" />
</h:panelGrid>