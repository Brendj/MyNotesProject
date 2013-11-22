<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCreatePage"--%>
<h:panelGrid id="employeeCreateGrid" binding="#{employeeCreatePage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.surname}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.firstName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.secondName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <rich:calendar value="#{employeeCreatePage.employee.passportDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер паспорта" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.passportNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <rich:calendar value="#{employeeCreatePage.employee.driverLicenceDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.driverLicenceNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <rich:calendar value="#{employeeCreatePage.employee.warTicketDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серийный номер военного билета" styleClass="output-text" />
         <h:inputText value="#{employeeCreatePage.employee.warTicketNumber}" styleClass="input-text" />

     </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{employeeCreatePage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>