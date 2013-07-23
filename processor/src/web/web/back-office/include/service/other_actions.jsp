<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="otherActionsPage" type="ru.axetta.ecafe.processor.web.ui.service.OtherActionsPage"--%>

<%--Тест лога --%>
<h:panelGrid id="otherActionsGrid" binding="#{otherActionsPage.pageComponent}" styleClass="borderless-grid">
    <a4j:commandButton value="Экспортировать данные для BI" action="#{otherActionsPage.rubBIExport}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Запустить генерацию ключевых показателей" action="#{otherActionsPage.runProjectStateGenerator}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Запустить синхронизацию с Реестрами" action="#{otherActionsPage.runImportRegisterClients}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Запустить пересчет льготных правил" action="#{otherActionsPage.runBenefitsRecalculation}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Перестроить таблицу опекунов" action="#{otherActionsPage.runClientGuardSANRebuild}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>