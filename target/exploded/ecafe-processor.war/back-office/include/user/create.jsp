<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers()) 
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания пользователя --%>
<h:panelGrid id="userCreateGrid" binding="#{mainPage.userCreatePage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.userName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userCreatePage.plainPassword}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userCreatePage.plainPasswordConfirmation}" maxlength="64"
                   styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Права пользователя" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.items}" var="item">
        <rich:column>
            <h:selectBooleanCheckbox value="#{item.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.functionName}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать нового пользователя" action="#{mainPage.createUser}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>