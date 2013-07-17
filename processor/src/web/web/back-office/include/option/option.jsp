<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="optionPage" type="ru.axetta.ecafe.processor.web.ui.option.OptionPage"--%>

<h:panelGrid id="optionPanelGrid" binding="#{optionPage.pageComponent}" styleClass="borderless-grid">
<rich:tabPanel>
    <rich:tab label="Общие">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Использовать схему с оператором" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.withOperator}" styleClass="output-text" />
            <h:outputText escape="true" value="Отпралять СМС-уведомление о событиях входа-выхода"
                          styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.notifyBySMSAboutEnterEvent}" styleClass="output-text" />
        </h:panelGrid>
        <h:panelGroup styleClass="borderless-grid">
            <h:outputText escape="true" value="Удалять записи меню в базе" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.cleanMenu}" styleClass="output-text" />
            <h:outputText escape="true" value="Хранить дней от текущей даты " styleClass="output-text" />
            <h:inputText value="#{optionPage.menuDaysForDeletion}" styleClass="input-text" size="3" />
        </h:panelGroup>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Лимит овердрафта по-умолчанию для новых клиентов"
                          styleClass="output-text" />
            <h:inputText value="#{optionPage.defaultOverdraftLimit}" styleClass="input-text" size="5" />
            <h:outputText escape="true" value="Лимит дневных трат по-умолчанию для новых клиентов"
                          styleClass="output-text" />
            <h:inputText value="#{optionPage.defaultExpenditureLimit}" styleClass="input-text" size="5" />
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Отправлять СМС оповещение о покупке"
                          styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.sendSMSPaymentNotification}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>
    <rich:tab label="Платежные системы">
        <h:outputText escape="true" value="Выводить в личный кабинет" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="4">

            <h:outputText escape="true" value="Chronopay" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.chronopaySection}" styleClass="output-text" />
            <h:outputText escape="true" value="Размер комиссии" styleClass="output-text" />
            <h:inputText value="#{optionPage.chronopayRate}" styleClass="input-text" size="10" />

            <h:outputText escape="true" value="RBKMoney" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.rbkSection}" styleClass="output-text" />
            <h:outputText escape="true" value="Размер комиссии" styleClass="output-text" />
            <h:inputText value="#{optionPage.rbkRate}" styleClass="input-text" size="10" />

        </h:panelGrid>

        <rich:dataTable id="bankListTable" width="700" var="bank" value="#{bankListPage.entityList}" rows="20"
                        rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="№" styleClass="output-text" escape="true" />
                </f:facet>
                <h:outputText styleClass="output-text" value="#{row+1}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Наименование" styleClass="output-text" escape="true" />
                </f:facet>
                <h:inputText value="#{bank.name}" styleClass="input-text" size="30" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="URL логотипа" styleClass="output-text" escape="true" />
                </f:facet>
                <h:inputText id="logourl" value="#{bank.logoUrl}" styleClass="input-text" size="20" />
            </rich:column>
            <rich:column headerClass="column-header">
                <a4j:commandButton value=">" title="Превью изображения" reRender="logourl, logoimage"
                                   styleClass="command-button" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Логотип" styleClass="output-text" escape="true" />
                </f:facet>
                <h:graphicImage id="logoimage" style="width: 128px;" value="#{bank.logoUrl}" url="#{bank.logoUrl}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="URL на адреса" styleClass="output-text" escape="true" />
                </f:facet>
                <h:inputText value="#{bank.terminalsUrl}" styleClass="input-text" size="20" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Комиссия" styleClass="output-text" escape="true" />
                </f:facet>
                <h:inputText value="#{bank.rate}" styleClass="input-text" size="20">
                    <f:convertNumber pattern="#0.00" />
                </h:inputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Минимальная сумма платежа" styleClass="output-text" escape="true" />
                </f:facet>
                <h:inputText value="#{bank.minRate}" styleClass="input-text" size="20">
                    <f:convertNumber pattern="#0.00" locale="en_GB" />
                </h:inputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Тип зачисления" styleClass="output-text" escape="true" />
                </f:facet>
                <h:selectOneMenu value="#{bank.enrollmentType}" styleClass="input-text" style="width: 100px;">
                    <f:selectItem itemLabel="Онлайн" itemValue="онлайн" />
                    <f:selectItem itemLabel="Оффлайн" itemValue="оффлайн" />
                </h:selectOneMenu>
            </rich:column>
            <rich:column style="text-align:center">
                <f:facet name="header">
                    <h:outputText value="Удалить" escape="true" />
                </f:facet>
                <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                                 oncomplete="#{rich:component('removedBankItemDeletePanel')}.show()">
                    <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{bank}" target="#{bankDeletePage.currentEntity}" />
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="bankListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>

        <a4j:commandButton value="Добавить банк" action="#{bankListPage.addBank}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />

    </rich:tab>

    <rich:tab label="Импорт/Экспорт">
        <h:panelGrid styleClass="borderless-grid" columns="1">
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Выгрузка в ИС НФП" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Журналировать транзакции в БД" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.journalTransactions}" styleClass="output-text" />
                    <h:outputText escape="true" value="Отправлять транзакции в ИС НФП" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.sendJournalTransactionsToNFP}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" value="URL-адрес сервиса приема ИС НФП" styleClass="output-text" />
                    <h:inputText value="#{optionPage.nfpServiceAddress}" styleClass="input-text" size="40" />
                </h:panelGrid>
            </rich:panel>
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Выгрузка данных для BI" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Экспортировать данные в BI" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.exportBIData}" styleClass="output-text" />
                    <h:outputText escape="true" value="Расположение файлов для BI" styleClass="output-text" />
                    <h:inputText value="#{optionPage.exportBIDataDirectory}" styleClass="input-text" size="100" />
                </h:panelGrid>
            </rich:panel>
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Импорт стоп-листов из ИС УОС МСР" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Импортировать данные стоп-листов" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.importMSRData}" styleClass="output-text" />
                    <h:outputText escape="true" value="Удаленный узел для импортирования стоп-листов"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.importMSRURL}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="Логин для импортирования стоп-листов" styleClass="output-text" />
                    <h:inputText value="#{optionPage.importMSRLogin}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Пароль для импортирования стоп-листов"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.importMSRPassword}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Журналировать импортирование стоп-листов"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.importMSRLogging}" styleClass="output-text" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Импорт из РНИП" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Импортировать платежи из РНИП"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.importRNIPPayments}" styleClass="output-text" />
                    <h:outputText escape="true" value="URL сервиса РНИП"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsURL}" styleClass="input-text" size="100" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Импорт данных из АИС Реестры" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Производить автоматическую синхронизацию клиентов"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.syncRegisterClients}" styleClass="output-text" />
                    <h:outputText escape="true" value="URL сервиса" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterURL}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Является тестовым сервисом"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.syncRegisterIsTestingService}" styleClass="output-text" />
                    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterUser}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterPassword}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Организация" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterCompany}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Вести лог синхронизации" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.syncRegisterLogging}" styleClass="output-text" />
                    <h:outputText escape="true" value="Количество попыток" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterMaxAttempts}" styleClass="input-text" size="5" />
                    <h:outputText escape="true" value="Емайл для оповещений" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterSupportEmail}" styleClass="input-text" size="100" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Параметры синхронизации" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Максимальное количество параллельных сессий синхронизации"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncLimits}" styleClass="input-text" size="3" />
                    <h:outputText escape="true" value="Тайм-аут"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.retryAfter}" styleClass="input-text" size="5" />
                </h:panelGrid>
            </rich:panel>

        </h:panelGrid>
    </rich:tab>

    <rich:tab label="Криптопровайдер">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Испорт из РНИП" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Наименование контейнера" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsStore}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Алиас" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsAlias}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsPassword}" styleClass="input-text" size="40" />
                </h:panelGrid>
            </rich:panel>
        </h:panelGrid>
    </rich:tab>

    <rich:tab label="Глобальные">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="URL адрес сервера для внешних ссылок" styleClass="output-text" />
            <h:inputText value="#{optionPage.externalURL}" styleClass="output-text" />
            <h:outputText escape="true" value="Производить перерасчет льгот клиентов" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.recalculateBenefits}" styleClass="output-text" />
            <h:outputText escape="true" value="Производить расчет для отображения графиков статуса проекта"
                          styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.exportProjectStateData}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>
    <rich:tab label="Личный кабинет">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Запретить пользователю включать/отключать СМС-информирование" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.disableSMSNotifyEditInClientRoom}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>
</rich:tabPanel>

<h:panelGroup style="margin-top: 10px">

    <a4j:commandButton value="Сохранить" action="#{optionPage.save}"
                       reRender="mainMenu, workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
    <a4j:commandButton value="Отмена" action="#{optionPage.cancel}"
                       reRender="mainMenu, workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
</h:panelGroup>
<rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>