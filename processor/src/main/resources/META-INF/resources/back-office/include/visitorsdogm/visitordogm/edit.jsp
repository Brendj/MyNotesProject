<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmEditPage"--%>
<h:panelGrid id="visitorDogmEditGrid" binding="#{visitorDogmEditPage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.surname}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.firstName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.secondName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Должность" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.position}" styleClass="input-text" maxlength="128" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmEditPage.visitorDogm.passportDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер паспорта" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.passportNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmEditPage.visitorDogm.driverLicenceDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.driverLicenceNumber}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmEditPage.visitorDogm.warTicketDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер военного билета" styleClass="output-text" />
         <h:inputText value="#{visitorDogmEditPage.visitorDogm.warTicketNumber}" styleClass="input-text" />

     </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{visitorDogmEditPage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
        <a4j:commandButton value="Востановить" action="#{visitorDogmEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>