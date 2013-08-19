<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeEditPage"--%>
<h:panelGrid id="employeeEditGrid" binding="#{employeeEditPage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.surname}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.firstName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.secondName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <rich:calendar value="#{employeeEditPage.employee.passportDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер паспорта" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.passportNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <rich:calendar value="#{employeeEditPage.employee.driverLicenceDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.driverLicenceNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <rich:calendar value="#{employeeEditPage.employee.warTicketDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер военного билета" styleClass="output-text" />
         <h:inputText value="#{employeeEditPage.employee.warTicketNumber}" styleClass="input-text" />

     </h:panelGrid>

    <rich:panel headerClass="workspace-panel-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Карты (#{employeeEditPage.employee.countCardItems})" />
        </f:facet>
        <rich:dataTable id="clientCardTable" value="#{employeeEditPage.employee.cardItems}" var="card"
                        rows="8"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер карты" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceForm" styleClass="command-link">
                    <h:outputText escape="true" value="#{card.cardNo}" converter="cardNoConverter"
                                  styleClass="output-text" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Статус" />
                </f:facet>
                <h:outputText escape="true" value="#{card.cardStation}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата создания" />
                </f:facet>
                <h:outputText escape="true" value="#{card.createDate}" converter="timeConverter" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Последний день действия" />
                </f:facet>
                <h:outputText escape="true" value="#{card.validDate}" converter="timeConverter" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Редактировать" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceForm" styleClass="command-link">
                    <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="clientCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Добавить карту" action="#{employeeEditPage.addCard}"
                               reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
        </h:panelGrid>

    </rich:panel>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{employeeEditPage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
        <a4j:commandButton value="Востановить" action="#{employeeEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>