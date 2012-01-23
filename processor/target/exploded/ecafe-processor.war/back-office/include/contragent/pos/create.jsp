<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditPos())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания точки продажи --%>
<h:panelGrid id="posCreatePanel" binding="#{mainPage.posCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.posCreatePage.contragent.contragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value=""
                                             target="#{mainPage.classTypes}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.posCreatePage.name}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{mainPage.posCreatePage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:inputText value="#{mainPage.posCreatePage.state}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Флаги" styleClass="output-text" />
    <h:inputText value="#{mainPage.posCreatePage.flags}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText value="#{mainPage.posCreatePage.publicKey}" maxlength="1024" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать точку продажи" action="#{mainPage.createPos}"
                       reRender="posCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>