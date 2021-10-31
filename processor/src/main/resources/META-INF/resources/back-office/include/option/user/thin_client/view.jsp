<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<%--@elvariable id="thinClientUserViewPage" type="ru.axetta.ecafe.processor.web.ui.option.user.ThinClientUserViewPage"--%>
<h:panelGrid id="thinClientUserViewPage" binding="#{thinClientUserViewPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid columns="2" rendered="#{not empty thinClientUserViewPage.idOfClient}">
        <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
        <h:inputText readonly="true" value="#{thinClientUserViewPage.username}" styleClass="input-text" />
        <h:outputText escape="true" value="Клиент" styleClass="output-text" />
        <h:inputText readonly="true" value="#{thinClientUserViewPage.person.fullName}" styleClass="input-text"/>
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:inputText readonly="true" value="#{thinClientUserViewPage.org.officialName}" styleClass="input-text"/>
        <h:outputText escape="true" value="Роль пользователя" styleClass="output-text" />
        <h:inputText readonly="true" value="#{thinClientUserViewPage.roleName}" styleClass="input-text"/>
    </h:panelGrid>
    <h:panelGrid rendered="#{empty thinClientUserViewPage.idOfClient}">
        <h:outputText value="Необходимо выбрать клиента на странице " styleClass="output-text"/>
        <a4j:commandLink value="Списка пользователей" styleClass="output-text">
            <a4j:support event="onclick" action="#{thinClientUserListPage.show}" reRender="mainMenu, workspaceForm"/>
        </a4j:commandLink>
    </h:panelGrid>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" rendered="#{not empty thinClientUserViewPage.idOfClient}">
    <a4j:commandButton value="Редактировать" action="#{thinClientUserEditPage.show}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" >
        <f:setPropertyActionListener value="#{thinClientUserViewPage.idOfClient}" target="#{thinClientUserEditPage.idOfClient}" />
        <f:setPropertyActionListener value="0" target="#{thinClientUserEditPage.callFromMenu}" />
    </a4j:commandButton>
</h:panelGrid>
