<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования пользователя --%>
<h:panelGrid id="userEditGrid" binding="#{mainPage.userEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.userName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.userEditPage.changePassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="userEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль" rendered="#{mainPage.userEditPage.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userEditPage.plainPassword}" maxlength="64"
                   rendered="#{mainPage.userEditPage.changePassword}"
                   readonly="#{!mainPage.userEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" rendered="#{mainPage.userEditPage.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret id="userEditorPasswordConfirmation" value="#{mainPage.userEditPage.plainPasswordConfirmation}"
                   maxlength="64" rendered="#{mainPage.userEditPage.changePassword}"
                   readonly="#{!mainPage.userEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.email}" maxlength="128" styleClass="input-text"/>
    <h:outputText escape="true" value="Права пользователя" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.items}" var="item">
        <rich:column>
            <h:selectBooleanCheckbox value="#{item.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateUser}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showUserEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>