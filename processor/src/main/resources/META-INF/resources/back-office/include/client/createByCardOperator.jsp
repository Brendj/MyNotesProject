<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script type="text/javascript">
    function onstartloading(){
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button").attr('disabled', '');
    }
</script>

<%-- Панель создания клиента --%>
<h:panelGrid styleClass="borderless-grid" columns="2" id="clientCreatePanelByCardOperator"
             binding="#{mainPage.clientRegistrationByCardOperatorPage.pageComponent}">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientRegistrationByCardOperatorPage.org.shortName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientRegistrationByCardOperatorPage.person.surname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientRegistrationByCardOperatorPage.person.firstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientRegistrationByCardOperatorPage.person.secondName}" maxlength="128" styleClass="input-text" />

    <h:outputText escape="true" value="Группа" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientRegistrationByCardOperatorPage.clientGroupName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientGroupSelectPage}" reRender="modalClientGroupSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" disabled="#{mainPage.clientRegistrationByCardOperatorPage.org.idOfOrg == null}">
            <f:param name="idOfOrg" value="#{mainPage.clientRegistrationByCardOperatorPage.org.idOfOrg}" />
            <f:setPropertyActionListener value="#{mainPage.clientRegistrationByCardOperatorPage.org.idOfOrg}" target="#{mainPage.clientGroupSelectPage.idOfOrg}" />
            <f:setPropertyActionListener value="#{null}" target="#{mainPage.clientGroupSelectPage.filter}" />
        </a4j:commandButton>
    </h:panelGroup>

    <h:outputText escape="true" value="Пол" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientRegistrationByCardOperatorPage.gender}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientRegistrationByCardOperatorPage.clientGenderMenu.items}" />
    </h:selectOneMenu>

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать нового клиента" action="#{mainPage.createClientByCardOperator}"
                       reRender="mainMenu, workspaceForm" styleClass="command-button" />
</h:panelGrid>
<a4j:status id="createClientCardOperatorStatus" onstart="onstartloading()" onstop="onstoploading()">
    <f:facet name="start">
        <h:panelGrid columns="2">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </h:panelGrid>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <a4j:commandButton action="#{mainPage.showCardRegistrationAndIssuePageWithContractId}" styleClass="command-button" id="registerClientByCardOperatorLink"
                   value="Выдать карту" reRender="mainMenu, workspaceForm" rendered="#{mainPage.clientRegistrationByCardOperatorPage.allowRegisterCard}">
        <f:setPropertyActionListener value="#{mainPage.clientRegistrationByCardOperatorPage.newContractId}" target="#{mainPage.contractIdCardOperator}" />
    </a4j:commandButton>
</h:panelGrid>