<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmCreatePage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCreatePage"--%>
<h:panelGrid id="visitorDogmCreateGrid" binding="#{visitorDogmCreatePage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.surname}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.firstName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.secondName}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Должность" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.position}" styleClass="input-text" maxlength="128" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmCreatePage.visitorDogm.passportDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер паспорта" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.passportNumber}" styleClass="input-text"
                      validatorMessage="Cерия и номер паспорта должен быть числом."/>

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmCreatePage.visitorDogm.driverLicenceDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.driverLicenceNumber}" styleClass="input-text"/>

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <rich:calendar value="#{visitorDogmCreatePage.visitorDogm.warTicketDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                        inputClass="input-text" showWeeksBar="false" />
         <h:outputLabel escape="true" value="Серия и номер военного билета" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCreatePage.visitorDogm.warTicketNumber}" styleClass="input-text"/>

     </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{visitorDogmCreatePage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>