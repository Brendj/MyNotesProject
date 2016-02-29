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

    <a4j:commandButton value="Иморт платежей из RNIP" action="#{otherActionsPage.runImportRNIPPayment}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Очистить Репозиторий" action="#{otherActionsPage.runRepositoryReportsCleanup}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Очистить журналы синхронизации" action="#{otherActionsPage.runSynchCleanup}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

    <a4j:commandButton value="Привязать клиентов к ЕМП" action="#{otherActionsPage.runBindEMPClients}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Запустить загрузку обновления из ЕМП" action="#{otherActionsPage.runReceiveEMPUpdates}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Отправить пробное событие на ЕМП" action="#{otherActionsPage.runSendEMPEvent}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Обновить статистику ЕМП" action="#{otherActionsPage.runRecalculateEMPStatistics}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Исправить записи из Реестров" action="#{otherActionsPage.repairNSI}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Повторная отправка не доставленных СМС" action="#{otherActionsPage.runTest}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Запустить пересчет показателей СМС" action="#{otherActionsPage.runSmsDeliveryRecalculation}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />


    <a4j:commandButton value="Запустить Автоплатеж" action="#{otherActionsPage.runRegularPayments}"
                        styleClass="command-button" />

    <rich:panel>
        <h:commandButton value="Модифицировать пароли" action="#{otherActionsPage.runPasswordReplacer}"
                         title="Заменяет все пароли == input на номер договора"
                            styleClass="command-button" />
        <h:inputText value="#{otherActionsPage.passwordForSearch}"/>
        <h:commandButton value="Скачать" action="#{otherActionsPage.download}"
                         rendered="#{otherActionsPage.downloadable}"
                         styleClass="command-button" />
    </rich:panel>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>