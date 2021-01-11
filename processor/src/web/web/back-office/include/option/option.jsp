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
        <h:panelGrid styleClass="borderless-grid" columns="1">
            <h:panelGroup styleClass="borderless-div">
                <h:outputText escape="true" value="Удалять записи меню в базе" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.cleanMenu}" styleClass="output-text">
                    <a4j:support event="onchange" reRender="menuDaysForDeletion,srcOrgMenuDaysForDeletion" />
                </h:selectBooleanCheckbox>
            </h:panelGroup>
            <h:panelGrid styleClass="mleft20px" columns="2">
                <h:outputText escape="true" value="Хранить дней от текущей даты " styleClass="output-text" />
                <h:inputText value="#{optionPage.menuDaysForDeletion}" id="menuDaysForDeletion" styleClass="input-text"
                             size="3" disabled="#{not optionPage.cleanMenu}" />
                <h:outputText escape="true" value="для организаций-поставщиков" styleClass="output-text" />
                <h:inputText value="#{optionPage.srcOrgMenuDaysForDeletion}" id="srcOrgMenuDaysForDeletion"
                             styleClass="input-text" size="3" disabled="#{not optionPage.cleanMenu}" />
            </h:panelGrid>
        </h:panelGrid>
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

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Модель оплаты СМС оповещений:" styleClass="output-text" />
            <h:selectOneRadio value="#{optionPage.smsPaymentType}" styleClass="input-text">
                <f:selectItem itemValue="1" itemLabel="по абонентской плате" />
                <f:selectItem itemValue="2" itemLabel="поштучно" />
            </h:selectOneRadio>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Размер абонентской платы по умолчанию" styleClass="output-text" />
            <h:inputText value="#{optionPage.smsDefaultSubscriptionFee}" styleClass="input-text"
                         converter="copeckSumConverter" size="5" />
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Включить абонементное питание" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.enableSubscriptionFeeding}" styleClass="output-text">
                <a4j:support event="onclick" ajaxSingle="true" reRender="enableSubBalanceOperationControl"/>
            </h:selectBooleanCheckbox>
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Включить работу по субсчетам" styleClass="output-text" />
            <h:selectBooleanCheckbox id="enableSubBalanceOperationControl" styleClass="output-text"
                                     value="#{optionPage.enableSubBalanceOperation}"/>
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Оставшийся период действия временной карты клиента (при нулевом значении проверка не проводится):" styleClass="output-text" />
            <rich:inputNumberSpinner value="#{optionPage.tempCardValidDays}" minValue="0" maxValue="60"/>
        </h:panelGrid>

        <h:panelGrid title="Оповещение об изменении в заявках" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Включить оповещение об изменении в заявках" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.enableNotificationGoodRequestChange}" styleClass="output-text">
                <a4j:support event="onclick"
                      reRender="hideMissedColumnsNotificationGoodRequestChange,maxNumDaysNotificationGoodRequestChange"
                      ajaxSingle="true" />
                         </h:selectBooleanCheckbox>
            <h:outputText escape="true" value="Скрывать даты с пустыми значениями"
                          styleClass="output-text mleft20px" />
            <h:selectBooleanCheckbox id="hideMissedColumnsNotificationGoodRequestChange"
                                                  value="#{optionPage.hideMissedColumnsNotificationGoodRequestChange}"
                                                  disabled="#{!optionPage.enableNotificationGoodRequestChange}"
                                                  styleClass="output-text" />
            <h:outputText escape="true" value="Количество дней выборки (от 7 до 31)"
                          styleClass="output-text mleft20px" />
            <rich:inputNumberSpinner id="maxNumDaysNotificationGoodRequestChange"
                                                  value="#{optionPage.maxNumDaysNotificationGoodRequestChange}"
                                                  disabled="#{!optionPage.enableNotificationGoodRequestChange}"
                                                  minValue="7" maxValue="31"/>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Сохранять агрегированные данные по синхронизациям" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.reportOn}" styleClass="output-text">
                <a4j:support event="onclick" ajaxSingle="true"/>
            </h:selectBooleanCheckbox>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Запрет редактирования поля e-mail клиента" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.disableEmailEdit}" styleClass="output-text" />
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Дата выдачи карты" styleClass="output-text"/>
            <rich:calendar value="#{optionPage.validRegistryDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
            </rich:calendar>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2" bgcolor="#CCCCCC">
            <h:outputText escape="true" value="Сверка 2.0 источник данных:" styleClass="output-text" />
            <h:selectOneRadio value="#{optionPage.reviseSourceType}" styleClass="input-text">
                <f:selectItem itemValue="1" itemLabel="НСИ" />
                <f:selectItem itemValue="2" itemLabel="мастер база" />
            </h:selectOneRadio>
            <h:outputText escape="true" value="Сверка 2.0 дельта (в часах)" styleClass="output-text" />
            <h:inputText value="#{optionPage.reviseDelta}" styleClass="input-text" size="5" />
            <h:outputText escape="true" value="Лимит записей в запросе к мастер базе" styleClass="output-text" />
            <h:inputText value="#{optionPage.reviseLimit}" styleClass="input-text" size="6" />
            <h:outputText escape="true" value="Обработано до: " styleClass="output-text" />
            <h:inputText value="#{optionPage.reviseLastDate}" styleClass="input-text" readonly="true" />
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" id="synSettingExpressionsGrid" columns="2" bgcolor="#FFF3C2">
            <h:outputText escape="true" value="Выражение для полной синхронизации" styleClass="output-text" />
            <h:inputText value="#{optionPage.fullSyncExpressions}" styleClass="input-text"  />
            <h:outputText escape="true" value="Выражение для синхронизации настроек ОО" styleClass="output-text" />
            <h:inputText value="#{optionPage.orgSettingSyncExpressions}" styleClass="input-text"  />
            <h:outputText escape="true" value="Выражение для синхронизации данных по клиентам ОО" styleClass="output-text" />
            <h:inputText value="#{optionPage.clientDataSyncExpressions}" styleClass="input-text"  />
            <h:outputText escape="true" value="Выражение для синхронизации меню" styleClass="output-text" />
            <h:inputText value="#{optionPage.menuSyncExpressions}" styleClass="input-text"  />
            <h:outputText escape="true" value="Выражение для синхронизации фотографий" styleClass="output-text" />
            <h:inputText value="#{optionPage.photoSyncExpressions}" styleClass="input-text"  />
            <h:outputText escape="true" value="Выражение для синхронизации библиотеки" styleClass="output-text" />
            <h:inputText value="#{optionPage.libSyncExpressions}" styleClass="input-text"  />
            <rich:toolTip styleClass="tooltip" layout="block">
                <span style="white-space: nowrap">
                    Выражение для автораспределения расписания представляет собой перечисление временных промежутков, когда необходимо и когда запрещено проводить синхронизацию АРМ с сервером Процессинга.
                    <ul>
                        <li>Промежутки задаются в формате HH:mm-HH:mm, разделителем является точка с запятой (;).</li>
                        <li>Если задать несколько промежутков, то сервис автораспределения сгенерирует время для каждого. Из-за чего количество промежутков == количество сеансов.</li>
                        <li>Время, в которое запрещено проводить синхронизацию, помечается восклицательным знаком в начале (!).</li>
                        <li>Выражение должно состоять только из разрешенных промежутков, либо только из запрещенных.</li>
                        <li>Если выражение не задано, то используется выражение по умолчанию.</li>
                        <li>Сервис автораспределения имеет ограниченное количество попыток сгенерировать время для сеанса. По истечению количества попыток сервис генерирует время используя выражение по умолчанию.</li>
                    </ul>
                </span>
            </rich:toolTip>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2" bgcolor="#CCCCCC">
            <h:outputText escape="true" value="CRON-выражение для сервиса блокировки неиспользуемых карт" styleClass="output-text" />
            <h:inputText value="#{optionPage.cardAutoBlockCron}" styleClass="input-text"  />
            <h:outputText escape="true" value="Сколько дней отсутствия активности по карте до блокировки" styleClass="output-text" />
            <h:inputText value="#{optionPage.cardAutoBlockDays}" styleClass="input-text"  />
            <h:outputText escape="true" value="Сервер для запуска операции блокировки" styleClass="output-text" />
            <h:inputText value="#{optionPage.cardAutoBlockNode}" styleClass="input-text"  />
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
                                    reRender="workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />

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
                    <h:outputText escape="true" value="Дата последней выгрузки" styleClass="output-text" />
                    <h:outputText value="#{optionPage.lastBIDataUpdate}" styleClass="output-text" />
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
                    <h:outputText escape="true" value="URL сервиса РНИП v1.15" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsURL}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="URL сервиса РНИП v1.16" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsURL_v116}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="URL сервиса РНИП v2.1" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsURL_v20}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="URL сервиса РНИП v2.2" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsURL_v22}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="Обращаться к сервису РНИП версии" styleClass="output-text" />
                    <h:selectOneMenu value="#{optionPage.RNIPPaymentsWorkingVersion}" styleClass="input-text">
                        <f:selectItems value="#{optionPage.RNIPWorkingVersions}" />
                    </h:selectOneMenu>
                    <h:outputText escape="true" value="Имя сервера с которого проводить импорт платежей"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.rnipProcessorInstance}" styleClass="input-text" size="40" />

                    <h:outputText escape="true" value="Ограничение по дате платежа в днях" styleClass="output-text" />
                    <h:panelGrid styleClass="borderless-grid" columns="2">
                        <h:inputText value="#{optionPage.daysRestrictionPaymentDateImport}" styleClass="input-text" size="10" />
                        <h:outputText escape="true" value="- не импортировать платежи РНИП, старше {N} дней" styleClass="output-text" />
                    </h:panelGrid>
                    <h:outputText escape="true" value="Мнемоника отправителя Sender/Code" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPSenderCode}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="Мнемоника отправителя Sender/Name" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPSenderName}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="Адрес сервиса TSA" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPTSAServer}" styleClass="input-text" size="100" />
                    <h:outputText escape="true" value="Использовать подпись формата XadES-T"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.useXadesT}" styleClass="output-text" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                                      value="Импорт данных из АИС Реестры" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Состояние сверки контингента: #{optionPage.isSverkaEnabledString()}"
                                  styleClass="output-text" />
                    <a4j:commandButton value="Включить" action="#{optionPage.turnOnSverka()}" rendered="#{!optionPage.isSverkaEnabled()}"
                                       reRender="workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
                    <a4j:commandButton value="Выключить" action="#{optionPage.turnOffSverka()}" rendered="#{optionPage.isSverkaEnabled()}"
                                       reRender="workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
                    <h:outputText escape="true" value="Версия НСИ" styleClass="output-text" />
                    <h:selectOneMenu value="#{optionPage.nsiVersion}" styleClass="input-text">
                        <f:selectItems value="#{optionPage.nsiVersions}" />
                    </h:selectOneMenu>
                    <h:outputText escape="true" value="Производить автоматическую сверку клиентов"
                                                       styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.syncRegisterClients}" styleClass="output-text" />
                    <h:outputText escape="true" value="URL сервиса" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterURL}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="WSDL сервиса" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterWSDL}" styleClass="input-text" size="40" />
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
                    <h:outputText escape="true" value="Хранить историю сверки не более (дней)" styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRegisterDaysTimeout}" styleClass="input-text" size="5" />
                    <h:outputText escape="true" value="Маска IP-адреса для доступа к локальным методам" styleClass="output-text" />
                    <h:inputText value="#{optionPage.frontControllerRequestIpMask}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Вести логгирование запросов в" styleClass="output-text" />
                    <h:inputText value="#{optionPage.synchLoggingFolder}" styleClass="input-text" size="100" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Параметры синхронизации" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Максимальное количество параллельных сессий полной синхронизации"
                                                       styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncLimits}" styleClass="input-text" size="3" />
                    <h:outputText escape="true" value="Максимальное количество параллельных сессий синхронизации всех типов"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncLimitFilter}" styleClass="input-text" size="3" />
                    <h:outputText escape="true" value="Количество одновременно обрабатываемых синхронизаций"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.simultaneousSyncThreads}" styleClass="input-text" size="3" />
                    <h:outputText escape="true" value="Таймаут ожидания одновременно обрабатываемых синхронизаций, минуты"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.simultaneousSyncTimeout}" styleClass="input-text" size="3" />
                    <h:outputText escape="true" value="Периоды запрета полной синхронизации"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.syncRestrictFullSyncPeriods}" styleClass="input-text" size="25" />
                    <h:outputText escape="true" value="Тайм-аут"
                                                       styleClass="output-text" />
                    <h:inputText value="#{optionPage.retryAfter}" styleClass="input-text" size="5" />
                    <h:outputText escape="true" value="Производить очистку журналов синхронизации"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.synchCleanup}" styleClass="output-text" />
                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Сервис информирования" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Включить логирование пакетов"
                                  styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{optionPage.logInfoService}" styleClass="output-text" />
                    <h:outputText escape="true" value="Список методов"
                                  styleClass="output-text" />
                    <h:inputText value="#{optionPage.methodsInfoService}" styleClass="input-text" size="100" />

                </h:panelGrid>
            </rich:panel>

            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header" value="Редактирование срока действия карт" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Срок продления карты (кол-во лет)"
                                  styleClass="output-text" />
                    <h:inputText id="periodOfExtensionCards" label="Срок продления карты" value="#{optionPage.periodOfExtensionCards}" styleClass="input-text" size="2" converter="javax.faces.Integer">
                        <f:validateLongRange minimum="0" maximum="99"/>
                    </h:inputText>
                </h:panelGrid>
            </rich:panel>
        </h:panelGrid>
    </rich:tab>

    <rich:tab label="Криптопровайдер">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                                      value="Криптоконтейнер ключей взаимодействия с РНИП" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Наименование контейнера" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsStore}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Алиас" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsAlias}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
                    <h:inputText value="#{optionPage.RNIPPaymentsPassword}" styleClass="input-text" size="40" />
                </h:panelGrid>
            </rich:panel>
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Взаимодействие с МТС банк" /></f:facet>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Путь к файлу сертификата" styleClass="output-text" />
                    <h:inputText value="#{optionPage.regularPaymentCertPath}" styleClass="input-text" size="40" />
                    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
                    <h:inputText value="#{optionPage.regularPaymentCertPassword}" styleClass="input-text" size="40" />
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
            <h:outputText escape="true" value="Разрешенные тэги при отображении мониторинга" styleClass="output-text" />
            <h:inputText value="#{optionPage.monitoringAllowedTags}" styleClass="output-text" />
            <h:outputText escape="true" value="Очищать отчеты в репозитории по дате создания" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.cleanupRepositoryReports}" styleClass="output-text" />
            <h:outputText escape="true" value="Выполнять повторную отправку недоставленных СМС" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.smsResending}" styleClass="output-text" />
            <h:outputText escape="true" value="Отладочный режим отправки СМС (СМС считается не отправленным)" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.smsFailureTestingMode}" styleClass="output-text" />
            <h:outputText escape="true" value="Включить PUSH-уведомления для создаваемых клиентов" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.notifyByPushNewClients}" styleClass="output-text" />
            <h:outputText escape="true" value="Включить email-уведомления для создаваемых клиентов" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.notifyByEmailNewClients}" styleClass="output-text" />
            <h:outputText escape="true" value="Включить флаги \"Оповещать о пополнениях\" и \"Оповещать о проходах\"" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.enableNotificationsOnBalancesAndEE}" styleClass="output-text" />
            <h:outputText escape="true" value="Включить флаг \"Служебные оповещения\" при создании связки \"Опекун-Ребенок\"" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.enableNotificationsSpecial}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>
    <rich:tab label="Личный кабинет">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Запретить пользователю включать/отключать СМС-информирование" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.disableSMSNotifyEditInClientRoom}" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить использование услуги автопополнения баланса"
                                       styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{optionPage.enableBalanceAutoRefill}" styleClass="output-text">
                <a4j:support event="onchange" reRender="thresholdValues,autoRefillValues" />
            </h:selectBooleanCheckbox>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Список выражений по фильтраций комплексов АП*" styleClass="output-text" />
            <h:inputText value="#{optionPage.arrayOfFilterText}" styleClass="output-text" />
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid" columns="1">
            <h:outputText escape="true" value="Настройки автопополнения баланса:" styleClass="output-text" />
            <h:panelGrid style="margin-left: 15px;" columns="2">
                <h:outputText escape="true" value="Пороговое значение баланса*" styleClass="output-text" />
                <h:inputText value="#{optionPage.thresholdValues}" styleClass="input-text" size="70"
                                              converter="rublesStringConverter" id="thresholdValues"
                                              disabled="#{not optionPage.enableBalanceAutoRefill}" />
                <h:outputText escape="true" value="Размер пополнения*" styleClass="output-text" />
                <h:inputText value="#{optionPage.autoRefillValues}" styleClass="input-text" size="70"
                                              converter="rublesStringConverter" id="autoRefillValues"
                                              disabled="#{not optionPage.enableBalanceAutoRefill}" />
                <h:outputText escape="true" style="font-size: 8pt;"
                                               value="* - при вводе списка значений разделителем является ';'" />
            </h:panelGrid>
        </h:panelGrid>
    </rich:tab>
    <rich:tab label="Тонкий клиент">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Редактировать заявки с (дни)" styleClass="output-text" />
            <h:inputText value="#{optionPage.thinClientMinClaimsEditableDays}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>
    <rich:tab label="Считыватель для веб-интерфейса">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Считыватель" styleClass="output-text" />
            <h:inputText value="#{optionPage.readerForWebInterfaceString}" styleClass="output-text" />
        </h:panelGrid>
    </rich:tab>

</rich:tabPanel>

<h:panelGroup style="margin-top: 10px">

    <a4j:commandButton value="Сохранить" action="#{optionPage.save}"
                            reRender="workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
    <a4j:commandButton value="Отмена" action="#{optionPage.cancel}"
                            reRender="workspaceTogglePanel, optionPanelGrid" styleClass="command-button" />
</h:panelGroup>
<rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>