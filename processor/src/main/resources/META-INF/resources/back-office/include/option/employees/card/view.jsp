<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeCardEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardEditPage"--%>
<%--@elvariable id="employeeCardViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardViewPage"--%>
<h:panelGrid id="employeeListGrid" binding="#{employeeCardViewPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Инженер" styleClass="output-text" />
        <h:inputText value="#{employeeCardViewPage.card.visitorItem.shortFullName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />

        <h:outputLabel escape="true" value="Номер карты" styleClass="output-text" />
        <h:inputText value="#{employeeCardViewPage.card.cardNo}" styleClass="input-text" readonly="true"/>

        <h:outputLabel escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
        <h:inputText value="#{employeeCardViewPage.card.cardPrintedNo}" styleClass="input-text" readonly="true"/>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Редактировать" action="#{employeeCardEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>