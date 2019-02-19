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
<h:panelGrid id="otherActionsGrid" binding="#{otherActionsPage.pageComponent}" styleClass="borderless-grid borderless-grid-align-top" columns="2">
    <h:panelGrid styleClass="borderless-grid">
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

    <a4j:commandButton value="Импорт платежей из RNIP" action="#{otherActionsPage.runImportRNIPPayment}"
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
    <a4j:commandButton value="Тест Scud Service" action="#{otherActionsPage.runScudTest}" id="runScudTest"
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

    <a4j:commandButton value="Загрузка файла ЕСЗ с мигрантами" action="#{otherActionsPage.loadESZMigrantsFile()}" id="loadESZMigrantsFile"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />

    <a4j:commandButton value="Обработка мигрантов" action="#{otherActionsPage.loadESZMigrants()}" id="loadESZMigrants"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Сгенерировать регулярные предзаказы" action="#{otherActionsPage.createRegularPreorders()}" id="createRegularPreorders"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Отправить отчеты PreorderRequestsNewReport поставщикам" action="#{otherActionsPage.sendGoodRequestsNewReports()}" id="sendGoodRequestsReport"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Запустить сервис обработки ЗЛП" action="#{otherActionsPage.runApplicationForFoodProcessingService()}" id="runApplicationForFoodProcessingService"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Запустить сверку льгот ДТСЗН с реестрами 2.0" action="#{otherActionsPage.runDTSZNDiscountsReviseService()}" id="runDTSZNDiscountsReviseService"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Отравка статусов заявление ЛП в АИС Контингент" action="#{otherActionsPage.sendToAISContingent()}" id="sendToAISContingent"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Запустить сверку льгот ДТСЗН с реестрами 2.0(этап 2)" action="#{otherActionsPage.runDTSZNDiscountsReviseServicePart2()}" id="runDTSZNDiscountsReviseServicePart2"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <a4j:commandButton value="Запустить обработку ЗЛП по результатам сверки" action="#{otherActionsPage.runUpdateApplicationsForFoodTask()}" id="runUpdateApplicationsForFoodTask"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <rich:panel>
        <a4j:commandButton value="Получить льготы по гуиду" action="#{otherActionsPage.runUpdateDiscounts()}"
                           title="Источник данных указывается в настройках"
                           styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" /><br/>
        <h:outputText value="GUID:"/>
        <h:inputText value="#{otherActionsPage.guidForDiscountsUpdate}" size="50"/>
    </rich:panel>
    <rich:separator align="center" height = "8" width = "75%" />
    <a4j:commandButton value="Итого за день" action="#{otherActionsPage.runEventNotificationServiceForDaily}" id="runEventNotificationServiceForDaily"
                           styleClass="command-button" />
    <a4j:commandButton value="Итого за неделю" action="#{otherActionsPage.runEventNotificationServiceForWeekly}" id="runEventNotificationServiceForWeekly"
                                                                             styleClass="command-button" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid-align-top" id="specialDatesFileLoaderPanel" >
        <rich:panel>
            <h:outputText escape="true" value="Загрузить производственный календарь для всех ОО" styleClass="output-text" /> <br/>
            <h:commandLink action="#{otherActionsPage.downloadSampleFile}" id="downloadSampleSpecialDates" value="Скачать текущий календарь" styleClass="command-link" />
            <rich:fileUpload id="specialDatesFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
                             addButtonClassDisabled="upload-command-button-diasbled" cleanButtonClass="upload-command-button"
                             cleanButtonClassDisabled="upload-command-button-diasbled" stopButtonClass="upload-command-button"
                             stopButtonClassDisabled="upload-command-button-diasbled" uploadButtonClass="upload-command-button"
                             uploadButtonClassDisabled="upload-command-button-diasbled" fileEntryClass="output-text"
                             fileEntryClassDisabled="output-text" fileEntryControlClass="output-text"
                             fileEntryControlClassDisabled="output-text" sizeErrorLabel="Недопустимый размер"
                             stopControlLabel="Остановить" stopEntryControlLabel="Остановить" addControlLabel="Добавить файл"
                             clearControlLabel="Очистить" clearAllControlLabel="Очистить все" doneLabel="Готово"
                             cancelEntryControlLabel="Отменить" transferErrorLabel="Ошибка передачи"
                             uploadControlLabel="Загрузка файла" progressLabel="Загрузка" listHeight="70px"
                             fileUploadListener="#{otherActionsPage.specialDatesLoadFileListener}">
                <f:facet name="label">
                    <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
                </f:facet>
                <a4j:support event="onuploadcomplete" reRender="specialDatesFileLoaderPanel" />
                <a4j:support event="onclear" reRender="specialDatesFileLoaderPanel" />
            </rich:fileUpload>
        </rich:panel>
    </h:panelGrid>

    <rich:panel rendered="#{otherActionsPage.isSpb()}">
        <a4j:commandButton value="Преобразовать номера карт" action="#{otherActionsPage.runUpdateSpbCardUids}" id="updateSpbCardUids"
                           title="Запуск скрипта по преобразовыванию уидов карт"
                           styleClass="command-button" onclick="disableButtons(true);" oncomplete="disableButtons(false)" /><br/>
        <h:outputText value="Список ид. организаций:"/>
        <h:inputText value="#{otherActionsPage.orgsForSpbCardsUidUpdate}" size="50"/>
    </rich:panel>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>