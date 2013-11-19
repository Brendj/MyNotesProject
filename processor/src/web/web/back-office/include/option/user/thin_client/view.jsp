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
<h:panelGrid id="thinClientUserViewPage" binding="#{thinClientUserViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserViewPage.username}" styleClass="input-text" />
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserViewPage.person.fullName}" styleClass="input-text"/>
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserViewPage.org.officialName}" styleClass="input-text"/>
    <h:outputText escape="true" value="Роль пользователя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{thinClientUserViewPage.roleName}" styleClass="input-text"/>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{thinClientUserEditPage.show}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" >
        <f:setPropertyActionListener value="#{thinClientUserViewPage.idOfClient}" target="#{thinClientUserEditPage.idOfClient}" />
    </a4j:commandButton>
</h:panelGrid>
