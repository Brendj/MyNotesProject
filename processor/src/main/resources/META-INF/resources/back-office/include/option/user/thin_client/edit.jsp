<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<%--@elvariable id="thinClientUserEditPage" type="ru.axetta.ecafe.processor.web.ui.option.user.ThinClientUserEditPage"--%>
<h:panelGrid id="thinClientUserEditPage" binding="#{thinClientUserEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{thinClientUserEditPage.username}" styleClass="input-text" />
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{thinClientUserEditPage.person.fullName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserEditPage.org.officialName}" styleClass="input-text"/>
    <h:outputText escape="true" value="Роль пользователя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserEditPage.roleName}" styleClass="input-text"/>
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{thinClientUserEditPage.changePassword}" styleClass="output-text" >
        <a4j:support event="onclick" actionListener="#{thinClientUserEditPage.doChangePasswordShow}" reRender="thinClientUserEditPage"/>
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль" styleClass="output-text" rendered="#{thinClientUserEditPage.changePassword}" />
    <h:inputSecret value="#{thinClientUserEditPage.password}" styleClass="input-text" rendered="#{thinClientUserEditPage.changePassword}"/>
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text" rendered="#{thinClientUserEditPage.changePassword}" />
    <h:inputSecret value="#{thinClientUserEditPage.passwordRepeat}" styleClass="input-text" rendered="#{thinClientUserEditPage.changePassword}" />
</h:panelGrid>
<%--<h:panelGrid id="thinClientUserEditPage" binding="#{thinClientUserEditPage.pageComponent}" styleClass="borderless-grid"
             columns="1" rendered="#{!thinClientUserViewPage.validForModify}">
    <h:outputText value="Необходимо выбрать клиента на странице " styleClass="output-text"/>
    <a4j:commandLink value="Списка пользователей" styleClass="output-text">
        <a4j:support event="onclick" action="#{thinClientUserListPage.show}" reRender="mainMenu, workspaceForm"/>
    </a4j:commandLink>
</h:panelGrid>--%>
<h:panelGrid styleClass="borderless-grid">
    <h:panelGrid id="messages">
        <h:outputText escape="true" value="#{thinClientUserEditPage.infoMessages}" rendered="#{not empty thinClientUserEditPage.infoMessages}" styleClass="info-messages" />
        <h:outputText escape="true" value="#{thinClientUserEditPage.errorMessages}" rendered="#{not empty thinClientUserEditPage.errorMessages}" styleClass="error-messages" />
    </h:panelGrid>

    <a4j:commandButton value="#{thinClientUserEditPage.submitButtonCaption}" action="#{thinClientUserEditPage.doSave}"
                       reRender="mainMenu, workspaceTogglePanel, messages" styleClass="command-button" />
</h:panelGrid>
