<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script language="javascript">
    function disableButtons(value) {
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:generateGuardiansByOrgs").disabled=value;
    }
</script>

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

    <rich:panel>
        <a4j:commandButton value="Генерировать представителей" action="#{otherActionsPage.runGenerateGuardians}" id="generateGuardiansByOrgs"
                         title="Принудительное создание представителя клиентам из не предопределенных групп."
                         styleClass="command-button" onclick="disableButtons(true);" oncomplete="disableButtons(false)" /><br/>
        <h:outputText value="Список ид. организаций:"/>
        <h:inputText value="#{otherActionsPage.orgsForGenerateGuardians}" size="50"/>
    </rich:panel>

    <rich:panel>
        <a4j:commandButton value="Выгрузка данных по продажам" action="#{otherActionsPage.runGenerateSummaryDownloadFile}"
                           title="Файл с выгрузкой за выбранную дату будет создан в папке, заданной в конфигурации"
                           styleClass="command-button" /><br/>
        <h:outputText value="Дата:"/>
        <rich:calendar value="#{otherActionsPage.summaryDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" >
            <a4j:support event="onchanged" />
        </rich:calendar>
    </rich:panel>

    <a4j:commandButton value="Тест Meal Service" action="#{otherActionsPage.runMealTest}" id="runMealTest"
                       styleClass="command-button" />
    <a4j:commandButton value="Загрузка файла НСИ по контингенту" action="#{otherActionsPage.loadNSIFile}" id="loadNSIFile"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Загрузка файла НСИ по сотрудникам" action="#{otherActionsPage.loadNSIEmployeeFile}" id="loadNSIEmployeeFile"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <rich:panel>
        <a4j:commandButton value="Выгрузка транзакций для Фин Оператора" action="#{otherActionsPage.runGenerateSummaryFinOperatorFile}"
                       title="Файл с выгрузкой за выбранную дату будет создан в папке, заданной в конфигурации"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" /><br/>
        <h:outputText value="Дата:"/>
        <rich:calendar value="#{otherActionsPage.summaryFinOperatorDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" >
            <a4j:support event="onchanged" />
        </rich:calendar>
    </rich:panel>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>