<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<h:panelGrid id="userViewPage" binding="#{mainPage.userViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.userName}" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.phone}" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.email}" styleClass="input-text"/>
    <h:outputText escape="true" value="Дата последних изменений" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.updateTime}" styleClass="input-text"
                 converter="timeConverter" />
    <h:outputText escape="true" value="Права пользователя" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.items}" var="item">
        <rich:column>
            <h:outputText escape="true" value="#{item.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showUserEditPage}"
                       reRender="workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
