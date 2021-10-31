<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="removeOrderGrid" binding="#{mainPage.orderRemovePage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Идентификатор организации (IdOfOrg)" styleClass="output-text" />
        <h:inputText value="#{mainPage.orderRemovePage.idOfOrg}" styleClass="input-text" required="true" />
        <h:outputText escape="true" value="Идентификатор покупки (IdOfOrder)" styleClass="output-text" />
        <h:inputText value="#{mainPage.orderRemovePage.idOfOrder}" styleClass="input-text" required="true" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Провести удаление" action="#{mainPage.removeOrder}" styleClass="command-button" />
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>