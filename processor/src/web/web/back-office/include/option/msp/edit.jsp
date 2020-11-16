<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCategory())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования категории --%>
<%--@elvariable id="codeMSPEditPage" type="ru.axetta.ecafe.processor.web.ui.option.msp.CodeMSPEditPage"--%>
<h:panelGrid id="codeMSPEditPanel" binding="#{codeMSPEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Код МСП" styleClass="output-text required-field" />
    <h:inputText value="#{codeMSPEditPage.code}" styleClass="input-text" maxlength="32" />
    <h:outputText escape="true" value="Описание кода МСП" styleClass="output-text" />
    <h:inputTextarea value="#{codeMSPEditPage.description}" styleClass="input-text" rows="5" cols="64" />
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{codeMSPEditPage.save()}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{codeMSPEditPage.reload()}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>