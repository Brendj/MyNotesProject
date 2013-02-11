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

    <h:outputText escape="true" value="Роль" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.userEditPage.idOfRole}" styleClass="input-text">
        <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
        <f:selectItems value="#{mainPage.userEditPage.userRoleEnumTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Имя роли" styleClass="output-text required-field" rendered="#{mainPage.userEditPage.isDefault}"/>
    <h:inputText value="#{mainPage.userEditPage.roleName}" maxlength="128" styleClass="input-text" rendered="#{mainPage.userEditPage.isDefault}"/>

    <h:outputText escape="true" value="Контрагент" styleClass="output-text required-field" rendered="#{mainPage.userEditPage.isSupplier}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.userEditPage.isSupplier}">
        <h:inputText value="#{mainPage.userEditPage.contragentItem.contragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0"
                                         target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2"
                                         target="#{mainPage.classTypes}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Права пользователя" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.items}" var="item" rendered="#{mainPage.userEditPage.isDefault}">
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