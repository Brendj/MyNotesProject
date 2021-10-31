<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeViewPage"--%>
<%--@elvariable id="employeeEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeEditPage"--%>
<%--@elvariable id="employeeCardEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardEditPage"--%>
<%--@elvariable id="employeeCardViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardViewPage"--%>
<%--@elvariable id="employeeCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardGroupPage"--%>
<h:panelGrid id="employeeViewGrid" binding="#{employeeViewPage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.surname}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.firstName}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.secondName}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Должность" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.position}" styleClass="input-text" maxlength="128" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.passportDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер паспорта" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.passportNumber}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.driverLicenceDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.driverLicenceNumber}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.warTicketDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер военного билета" styleClass="output-text" />
         <h:inputText value="#{employeeViewPage.employee.warTicketNumber}" readonly="true" styleClass="input-text" />

     </h:panelGrid>
    <rich:panel headerClass="workspace-panel-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Карты (#{employeeViewPage.employee.countCardItems})" />
        </f:facet>
        <rich:dataTable id="employeeCardTable" value="#{employeeViewPage.employee.cardItems}" var="card"
                        rows="8"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер карты" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeCardViewPage.show}" styleClass="command-link">
                    <h:outputText escape="true" value="#{card.cardNo}" converter="cardNoConverter" styleClass="output-text" />
                    <f:setPropertyActionListener value="#{card}" target="#{employeeCardGroupPage.currentCard}"/>
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата создания" />
                </f:facet>
                <h:outputText escape="true" value="#{card.createDate}" converter="timeConverter" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Редактировать" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeCardEditPage.show}" styleClass="command-link">
                    <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{card}" target="#{employeeCardGroupPage.currentCard}"/>
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="employeeCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    </rich:panel>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Редактировать" action="#{employeeEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>