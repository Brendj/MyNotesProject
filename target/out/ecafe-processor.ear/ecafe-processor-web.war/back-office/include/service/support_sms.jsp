<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:panel id="supportSmsPanel" headerClass="workspace-panel-header">
    <f:facet name="header">
        <h:outputText escape="true" value="Сервис > Отправка SMS" />
    </f:facet>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="" />
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</rich:panel>