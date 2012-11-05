<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Главное меню --%>
<a4j:form id="mainMenuForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
<rich:panelMenu id="mainMenu" binding="#{mainPage.mainMenu}" styleClass="main-menu" expandSingle="true"
                disabledGroupClass="main-menu-disabled-group" disabledItemClass="main-menu-disabled-item"
                groupClass="main-menu-group" itemClass="main-menu-item" hoveredGroupClass="main-menu-hovered-group"
                hoveredItemClass="main-menu-hovered-item" topGroupClass="main-menu-top-group"
                topItemClass="main-menu-top-item" iconCollapsedGroup="triangle" iconExpandedGroup="triangleDown"
                iconItem="none" mode="ajax">

<rich:panelMenuGroup id="orgGroupMenu" label="Организации" binding="#{mainPage.orgGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewOrgs}">
    <a4j:support event="onclick" action="#{mainPage.showOrgGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showOrgListMenuItem" binding="#{mainPage.orgListPage.mainMenuComponent}" label="Список"
                        action="#{mainPage.showOrgListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedOrgGroupMenu" label="#{mainPage.selectedOrgGroupPage.shortName}"
                         binding="#{mainPage.selectedOrgGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedOrgGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewOrgMenuItem" binding="#{mainPage.orgViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{mainPage.showOrgViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editOrgMenuItem" binding="#{mainPage.orgEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{mainPage.showOrgEditPage}"
                            rendered="#{mainPage.eligibleToEditOrgs}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="viewOrgMenuExchange" binding="#{mainPage.menuExchangePage.mainMenuComponent}"
                            action="#{mainPage.showMenuExchangePage}" label="Просмотр мастер-меню"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="viewOrgMenuView" binding="#{mainPage.menuViewPage.mainMenuComponent}"
                            action="#{mainPage.showMenuViewPage}" label="Просмотр меню" reRender="workspaceForm" />


        <rich:panelMenuItem id="generateOrgBalanceReportMenuItem"
                            binding="#{mainPage.orgBalanceReportPage.mainMenuComponent}" label="Отчет по балансу"
                            action="#{mainPage.showOrgBalanceReportPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="generateOrgOrderReportMenuItem"
                            binding="#{mainPage.orgOrderReportPage.mainMenuComponent}" label="Отчет по покупкам"
                            action="#{mainPage.showOrgOrderReportPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createOrgMenuItem" binding="#{mainPage.orgCreatePage.mainMenuComponent}" label="Регистрация"
                        rendered="#{mainPage.eligibleToEditOrgs}"
                        action="#{mainPage.showOrgCreatePage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="distributionRulesMenuItem" binding="#{distributionRulesPage.mainMenuComponent}" label="Правила распространения"
                        action="#{distributionRulesPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="contragentGroupMenu" label="Контрагенты"
                     binding="#{mainPage.contragentGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewContragents}">
    <a4j:support event="onclick" action="#{mainPage.showContragentGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showContragentListMenuItem" binding="#{mainPage.contragentListPage.mainMenuComponent}"
                        label="Список" action="#{mainPage.showContragentListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedContragentGroupMenu" label="#{mainPage.selectedContragentGroupPage.contragentName}"
                         binding="#{mainPage.selectedContragentGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedContragentGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewContragentMenuItem" binding="#{mainPage.contragentViewPage.mainMenuComponent}"
                            label="Просмотр" action="#{mainPage.showContragentViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editContragentMenuItem" binding="#{mainPage.contragentEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{mainPage.showContragentEditPage}"
                            rendered="#{mainPage.eligibleToEditContragents}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="generateContragentClientPaymentReportMenuItem"
                            binding="#{mainPage.contragentClientPaymentReportPage.mainMenuComponent}"
                            label="Отчет по платежам клиентов"
                            action="#{mainPage.showContragentClientPaymentReportPage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createContragentMenuItem" binding="#{mainPage.contragentCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{mainPage.showContragentCreatePage}"
                        rendered="#{mainPage.eligibleToEditContragents}"
                        reRender="workspaceForm" />

    <rich:panelMenuGroup id="ccAccountGroupMenu" binding="#{mainPage.ccAccountGroupPage.mainMenuComponent}"
                         label="Счета клиентов">
        <a4j:support event="onclick" action="#{mainPage.showCCAccountGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="ccAccountListMenuItem" label="Список"
                            binding="#{mainPage.ccAccountListPage.mainMenuComponent}"
                            action="#{mainPage.showCCAccountListPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="ccAccountCreateMenuItem" binding="#{mainPage.CCAccountCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showCCAccountCreatePage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="ccAccountFileLoadMenuItem" binding="#{mainPage.CCAccountFileLoadPage.mainMenuComponent}"
                            label="Загрузка из файла" action="#{mainPage.showCCAccountFileLoadPage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="posGroupMenu" binding="#{mainPage.posGroupPage.mainMenuComponent}"
                         label="Cправочник точек продаж" rendered="#{mainPage.eligibleToViewPos}">
        <a4j:support event="onclick" action="#{mainPage.showPosGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="posListMenuItem" label="Список" binding="#{mainPage.posListPage.mainMenuComponent}"
                            action="#{mainPage.showPosListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedPosGroupMenu" label="#{mainPage.selectedPosGroupPage.name}"
                             binding="#{mainPage.selectedPosGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedPosGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editPosMenuItem" binding="#{mainPage.posEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showPosEditPage}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="posCreateMenuItem" binding="#{mainPage.posCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showPosCreatePage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="settlementGroupMenu" binding="#{mainPage.settlementGroupPage.mainMenuComponent}"
                         label="Платежи между контрагентами" rendered="#{mainPage.eligibleToViewPayment}">
        <a4j:support event="onclick" action="#{mainPage.showSettlementGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="settlementListMenuItem" label="Список"
                            binding="#{mainPage.settlementListPage.mainMenuComponent}"
                            action="#{mainPage.showSettlementListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedSettlementGroupMenu" label="#{mainPage.selectedSettlementGroupPage.name}"
                             binding="#{mainPage.selectedSettlementGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedSettlementGroupPage}"
                         reRender="workspaceForm" />

            <rich:panelMenuItem id="editSettlementMenuItem" binding="#{mainPage.settlementEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showSettlementEditPage}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="settlementCreateMenuItem" binding="#{mainPage.settlementCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showSettlementCreatePage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="addPaymentGroupMenu" binding="#{mainPage.addPaymentGroupPage.mainMenuComponent}"
                         label="Начисление платы" rendered="#{mainPage.eligibleToViewPayment}">
        <a4j:support event="onclick" action="#{mainPage.showAddPaymentGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="addPaymentListMenuItem" label="Список"
                            binding="#{mainPage.addPaymentListPage.mainMenuComponent}"
                            action="#{mainPage.showAddPaymentListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedAddPaymentGroupMenu" label="#{mainPage.selectedAddPaymentGroupPage.name}"
                             binding="#{mainPage.selectedAddPaymentGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedAddPaymentGroupPage}"
                         reRender="workspaceForm" />

            <rich:panelMenuItem id="editAddPaymentMenuItem" binding="#{mainPage.addPaymentEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showAddPaymentEditPage}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="addPaymentCreateMenuItem" binding="#{mainPage.addPaymentCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showAddPaymentCreatePage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>


    <rich:panelMenuGroup id="contractGroupMenu" label="Контракты"
                         binding="#{contractListPage.groupPage.mainMenuComponent}">
        <a4j:support event="onclick" action="#{contractListPage.groupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="contractListPageMenuItem" label="Список" binding="#{contractListPage.mainMenuComponent}"
                            action="#{contractListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedContractGroupMenu" label="#{contractEditPage.selectedEntityGroupPage.title}"
                             binding="#{contractEditPage.selectedEntityGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{contractEditPage.selectedEntityGroupPage.show}"
                         reRender="workspaceForm" />


            <rich:panelMenuItem id="viewContractMenuItem" binding="#{contractViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{contractViewPage.show}" reRender="workspaceForm" />
            <rich:panelMenuItem id="editContractMenuItem" binding="#{contractEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{contractEditPage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createContractMenuItem" binding="#{contractCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{contractCreatePage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="caOpsGroupMenu" binding="#{mainPage.caOpsGroupPage.mainMenuComponent}" label="Операции">
        <a4j:support event="onclick" action="#{mainPage.showContragentOpsGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="caReconcileMenuItem" label="Сверка реестров"
                            binding="#{reconciliationPage.mainMenuComponent}" action="#{reconciliationPage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

</rich:panelMenuGroup>


<rich:panelMenuGroup id="clientGroupMenu" binding="#{mainPage.clientGroupPage.mainMenuComponent}" label="Клиенты"
                     rendered="#{mainPage.eligibleToViewClients}">
    <a4j:support event="onclick" action="#{mainPage.showClientGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showClientListMenuItem" binding="#{mainPage.clientListPage.mainMenuComponent}"
                        label="Список" action="#{mainPage.showClientListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedClientGroupMenu" binding="#{mainPage.selectedClientGroupPage.mainMenuComponent}"
                         label="#{mainPage.selectedClientGroupPage.shortName}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedClientGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewClientMenuItem" binding="#{mainPage.clientViewPage.mainMenuComponent}"
                            label="Просмотр" action="#{mainPage.showClientViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editClientMenuItem" binding="#{mainPage.clientEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{mainPage.showClientEditPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewClientOperationsMenuItem"
                            binding="#{mainPage.clientOperationListPage.mainMenuComponent}" label="Операции"
                            action="#{mainPage.showClientOperationListPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createClientMenuItem" binding="#{mainPage.clientCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{mainPage.showClientCreatePage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="loadClientFromFileMenuItem" binding="#{mainPage.clientFileLoadPage.mainMenuComponent}"
                        label="Загрузить из файла" action="#{mainPage.showClientFileLoadPage}"
                        reRender="workspaceForm" />

    <rich:panelMenuItem id="loadClientUpdateFromFileMenuItem"
                        binding="#{mainPage.clientUpdateFileLoadPage.mainMenuComponent}" label="Обновить из файла"
                        action="#{mainPage.showClientUpdateFileLoadPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="contractBuildMenu" label="Подготовка договора"
                        binding="#{mainPage.contractBuildPage.mainMenuComponent}"
                        action="#{mainPage.showContractBuildPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="clientLimitBatchEditMenu" label="Изменить лимит овердрафта"
                        binding="#{mainPage.clientLimitBatchEditPage.mainMenuComponent}"
                        action="#{mainPage.showClientLimitBatchEditPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="clientSmsListMenu" label="SMS" binding="#{mainPage.clientSmsListPage.mainMenuComponent}"
                        action="#{mainPage.showClientSmsListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="clientOpsGroupMenu" binding="#{mainPage.clientOpsGroupPage.mainMenuComponent}" label="Операции">
        <a4j:support event="onclick" action="#{mainPage.showClientOpsGroupPage}" reRender="workspaceForm" />

        <%--@elvariable id="clientBalanceTransferPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientBalanceTransferPage"--%>
        <rich:panelMenuItem id="clientBalanceTransferMenuItem" label="Перевод между счетами"
                            binding="#{clientBalanceTransferPage.mainMenuComponent}" action="#{clientBalanceTransferPage.show}"
                            reRender="workspaceForm" />

        <%--@elvariable id="clientRefundPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientRefundPage"--%>
        <rich:panelMenuItem id="clientRefundMenuItem" label="Возврат средств"
                            binding="#{clientRefundPage.mainMenuComponent}" action="#{clientRefundPage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

</rich:panelMenuGroup>

<rich:panelMenuGroup id="cardGroupMenu" binding="#{mainPage.cardGroupPage.mainMenuComponent}" label="Карты"
                     rendered="#{mainPage.eligibleToViewCards}">
    <a4j:support event="onclick" action="#{mainPage.showCardGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showCardListMenuItem" binding="#{mainPage.cardListPage.mainMenuComponent}" label="Список"
                        action="#{mainPage.showCardListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedCardGroupMenu" binding="#{mainPage.selectedCardGroupPage.mainMenuComponent}"
                         label="#{mainPage.selectedCardGroupPage.shortName}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedCardGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewCardMenuItem" binding="#{mainPage.cardViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{mainPage.showCardViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editCardMenuItem" binding="#{mainPage.cardEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{mainPage.showCardEditPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewCardOperationsMenuItem"
                            binding="#{mainPage.cardOperationListPage.mainMenuComponent}" label="Операции"
                            action="#{mainPage.showCardOperationListPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createCardMenuItem" binding="#{mainPage.cardCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{mainPage.showCardCreatePage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="loadCardFromFileMenuItem" binding="#{mainPage.cardFileLoadPage.mainMenuComponent}"
                        label="Загрузить из файла" action="#{mainPage.showCardFileLoadPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="cardExpireBatchEditMenu" label="Изменить дату валидности"
                        binding="#{mainPage.cardExpireBatchEditPage.mainMenuComponent}"
                        action="#{mainPage.showCardExpireBatchEditPage}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="serviceGroupMenu" binding="#{mainPage.serviceGroupPage.mainMenuComponent}" label="Сервис">
    <a4j:support event="onclick" action="#{mainPage.showServiceGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="removeOrderMenuItem" binding="#{mainPage.orderRemovePage.mainMenuComponent}"
                        label="Удаление покупки" action="#{mainPage.showOrderRemovePage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}" />

    <rich:panelMenuItem id="logTestMenuItem" binding="#{mainPage.testLogPage.mainMenuComponent}" label="Тест лога"
                        action="#{mainPage.showTestLogPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}" />

    <rich:panelMenuItem id="buildSignKeysMenuItem" binding="#{mainPage.buildSignKeysPage.mainMenuComponent}"
                        label="Генерация ключей ЭЦП" action="#{mainPage.showBuildSignKeysPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}" />

    <rich:panelMenuItem id="supportEmailMenuItem" binding="#{mainPage.supportEmailPage.mainMenuComponent}"
                        label="Отправка письма" action="#{mainPage.showSupportEmailPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceSupport}" />
    <rich:panelMenuItem id="supportSMSMenuItem" binding="#{supportSMSPage.mainMenuComponent}" label="Отправка SMS"
                        action="#{supportSMSPage.show}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceSupport}" />
    <rich:panelMenuItem id="showJournal" label="Очередь выгрузки транзакций"
                        binding="#{journalViewPage.mainMenuComponent}" action="#{journalViewPage.show}"
                        rendered="#{mainPage.eligibleToServiceAdmin}"
                        reRender="workspaceForm" />

    <rich:panelMenuGroup id="nsiGroup" binding="#{mainPage.nsiGroupPage.mainMenuComponent}" label="Реестры"
                         rendered="#{mainPage.eligibleToServiceAdmin}" >
        <a4j:support event="onclick" action="#{mainPage.showNSIGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="nsiOrgCatalogFind" binding="#{orgCatalogFindPage.mainMenuComponent}"
                            label="Поиск организаций" action="#{orgCatalogFindPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="nsiPupilCatalogFind" binding="#{pupilCatalogFindPage.mainMenuComponent}"
                            label="Поиск учащихся" action="#{pupilCatalogFindPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="uosGroup" binding="#{mainPage.uosGroupPage.mainMenuComponent}" label="УОС"
                         rendered="#{mainPage.eligibleToServiceAdmin}" >
        <a4j:support event="onclick" action="#{mainPage.showUOSGroupPage}" reRender="workspaceForm" />

<%--@elvariable id="uosSendBlockPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.UosSendBlockPage"--%>
<%--@elvariable id="uosStopListPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.UosStopListPage"--%>
        <rich:panelMenuItem id="uosSendBlock" binding="#{uosSendBlockPage.mainMenuComponent}"
                            label="Запрос блокировки" action="#{uosSendBlockPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="uosStopList" binding="#{uosStopListPage.mainMenuComponent}"
                            label="Получение стоп-листов" action="#{uosStopListPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>
    <%--<rich:panelMenuItem label="Отправка SMS" action="#{mainPage.showSupportSmsSender}"--%>
    <%--reRender="mainMenu, workspaceForm" />
    --%>
</rich:panelMenuGroup>


<rich:panelMenuGroup id="monitoringGroupMenu" binding="#{mainPage.monitoringGroupPage.mainMenuComponent}"
                     label="Мониторинг" rendered="#{mainPage.eligibleToMonitor}">
    <a4j:support event="onclick" action="#{mainPage.showMonitoringGroupPage}" reRender="workspaceForm" />
    <%--@elvariable id="syncMonitorPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.SyncMonitorPage"--%>
    <rich:panelMenuItem id="syncMonitorItem" binding="#{syncMonitorPage.mainMenuComponent}"
                        label="Мониторинг синхронизации" action="#{syncMonitorPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="dashboardMenuItem" binding="#{dashboardPage.mainMenuComponent}"
                        label="Мониторинг активности" action="#{dashboardPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="syncReportMenuItem" binding="#{mainPage.syncReportPage.mainMenuComponent}"
                        label="Отчет по синхронизации" action="#{mainPage.showSyncReportPage}"
                        reRender="workspaceForm" />

    <rich:panelMenuItem id="statusSyncReportMenuItem" binding="#{mainPage.statusSyncReportPage.mainMenuComponent}"
                        label="Статус синхронизации" action="#{mainPage.showStatusSyncReportPage}"
                        reRender="workspaceForm" />
</rich:panelMenuGroup>


<rich:panelMenuGroup id="reportOnlineGroupMenu" binding="#{mainPage.reportOnlineGroupPage.mainMenuComponent}"
                     label="Онлайн отчеты" rendered="#{mainPage.eligibleToWorkOnlineReport}">
    <a4j:support event="onclick" action="#{mainPage.showReportOnlineGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="complexGroupMenu" binding="#{mainPage.complexGroupPage.mainMenuComponent}"
                         label="Отчет по комплексам">
        <a4j:support event="onclick" action="#{mainPage.showComplexGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="allComplexReportMenuItem" binding="#{mainPage.allComplexReportPage.mainMenuComponent}"
                            label="Все комплексы" action="#{mainPage.showAllComplexReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="freeComplexReportMenuItem" binding="#{mainPage.freeComplexReportPage.mainMenuComponent}"
                            label="Бесплатные комплексы" action="#{mainPage.showFreeComplexReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="payComplexReportMenuItem" binding="#{mainPage.payComplexReportPage.mainMenuComponent}"
                            label="Платные комплексы" action="#{mainPage.showPayComplexReportPage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="discountsGroupMenu" binding="#{mainPage.discountGroupPage.mainMenuComponent}"
                         label="Отчет по льготам">
        <a4j:support event="onclick" action="#{mainPage.showDiscountGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="allOrgsReportMenuItem"
                            binding="#{mainPage.allOrgsDiscountsReportPage.mainMenuComponent}"
                            label="Отчет по всем организациям" action="#{mainPage.showAllOrgsDiscountReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="orgDiscountsReportMenuItem"
                            binding="#{mainPage.orgDiscountsReportPage.mainMenuComponent}" label="Отчет по организации"
                            action="#{mainPage.showOrgDiscountsReportPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="salesReportMenuItem" binding="#{mainPage.salesReportPage.mainMenuComponent}"
                        label="Отчет по продажам" action="#{mainPage.showSalesReportPage}" reRender="workspaceForm" />

    <!-- внесено изменние -->
    <rich:panelMenuItem id="enterEventReportMenuItem" binding="#{mainPage.enterEventReportPage.mainMenuComponent}"
                        label="Отчет по турникетам" action="#{mainPage.showEnterEventReportPage}"
                        reRender="workspaceForm" />
    <!-- внесено изменние -->

    <rich:panelMenuItem id="clientReportMenuItem" binding="#{mainPage.clientReportPage.mainMenuComponent}"
                        label="Отчет по учащимся" action="#{mainPage.showClientReportPage}" reRender="workspaceForm" />
    <rich:panelMenuItem id="currentPositionReportMenuItem"
                        binding="#{mainPage.currentPositionsReportPage.mainMenuComponent}"
                        label="Просмотр текущих позиций" action="#{mainPage.showCurrentPositionsReportPage}"
                        reRender="workspaceForm" />
    <%--@elvariable id="commonStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CommonStatsPage"--%>
    <rich:panelMenuItem id="commonStatsMenuItem"
                        binding="#{commonStatsPage.mainMenuComponent}"
                        label="Общая статистика" action="#{commonStatsPage.show}"
                        reRender="workspaceForm" />
    <%--@elvariable id="payStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PayStatsPage"--%>
    <rich:panelMenuItem id="payStatsMenuItem"
                        binding="#{payStatsPage.mainMenuComponent}"
                        label="Отчет по агентам" action="#{payStatsPage.show}"
                        reRender="workspaceForm" />

</rich:panelMenuGroup>

<%--@elvariable id="reportRepositoryListPage" type="ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryListPage"--%>
<rich:panelMenuGroup id="reportRepositoryGroupMenu" binding="#{reportRepositoryListPage.groupPage.mainMenuComponent}"
                     label="Репозиторий отчетов" rendered="#{mainPage.eligibleToWorkOnlineReport}">
    <a4j:support event="onclick" action="#{reportRepositoryListPage.groupPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="reportRepositoryListMenuItem"
                        binding="#{reportRepositoryListPage.mainMenuComponent}" label="Просмотр"
                        action="#{reportRepositoryListPage.show}" reRender="workspaceForm" />
</rich:panelMenuGroup>

<rich:panelMenuGroup id="eventNotificationGroupMenu" binding="#{mainPage.eventNotificationGroupPage.mainMenuComponent}"
                     label="Уведомления о событиях" rendered="#{mainPage.eligibleToViewReports}">
    <a4j:support event="onclick" action="#{mainPage.showEventNotificationGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="eventNotificationRuleListMenuItem"
                        binding="#{mainPage.eventNotificationListPage.mainMenuComponent}" label="Список правил"
                        action="#{mainPage.showEventNotificationListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedEventNotificationGroupMenu" rendered="false"
                         binding="#{mainPage.selectedEventNotificationGroupPage.mainMenuComponent}"
                         label="#{mainPage.selectedEventNotificationGroupPage.title}">
        <a4j:support event="onclick" action="#{mainPage.showSelectedEventNotificationGroupPage}"
                     reRender="workspaceForm" />

        <rich:panelMenuItem id="eventNotificationRuleViewMenuItem"
                            binding="#{mainPage.eventNotificationViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{mainPage.showEventNotificationViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="eventNotificationRuleEditMenuItem"
                            binding="#{mainPage.eventNotificationEditPage.mainMenuComponent}" label="Редактирование"
                            action="#{mainPage.showEventNotificationEditPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="eventNotificationRuleCreateMenuItem"
                        binding="#{mainPage.eventNotificationCreatePage.mainMenuComponent}" label="Добавить уведомление"
                        action="#{mainPage.showEventNotificationCreatePage}" reRender="workspaceForm" />

</rich:panelMenuGroup>
<rich:panelMenuGroup id="optionGroupMenu" label="Настройки" binding="#{mainPage.optionGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToEditOptions}">
<a4j:support event="onclick" action="#{mainPage.showOptionGroupPage}" reRender="workspaceForm" />

<rich:panelMenuGroup id="optionsGroupMenu" label="Настройки" binding="#{mainPage.optionsGroupPage.mainMenuComponent}"
                     rendered="true">
    <rich:panelMenuItem id="showOptionMenuItem" binding="#{optionPage.mainMenuComponent}" label="Настройки"
                        action="#{optionPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showMessageConfigureMenuItem" binding="#{messageConfigurePage.mainMenuComponent}"
                        label="Шаблоны уведомлений" action="#{messageConfigurePage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showConfigurationMenuItem" binding="#{mainPage.configurationPage.mainMenuComponent}"
                        label="Конфигурация" action="#{mainPage.showConfigurationPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showLicInfoMenuItem" binding="#{licInfoPage.mainMenuComponent}" label="Лицензии"
                        action="#{licInfoPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>


<rich:panelMenuGroup id="userGroupMenu" label="Пользователи" binding="#{mainPage.userGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewUsers}">
    <a4j:support event="onclick" action="#{mainPage.showUserGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showUserListMenuItem" binding="#{mainPage.userListPage.mainMenuComponent}" label="Список"
                        action="#{mainPage.showUserListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedUserGroupMenu" label="#{mainPage.selectedUserGroupPage.userName}"
                         binding="#{mainPage.selectedUserGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedUserGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewUserMenuItem" binding="#{mainPage.userViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{mainPage.showUserViewPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editUserMenuItem" binding="#{mainPage.userEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{mainPage.showUserEditPage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createUserMenuItem" binding="#{mainPage.userCreatePage.mainMenuComponent}" label="Создание"
                        action="#{mainPage.showUserCreatePage}" reRender="workspaceForm" />

</rich:panelMenuGroup>


<rich:panelMenuGroup id="categoryOrgGroupMenu" label="Категории организаций"
                     binding="#{mainPage.categoryOrgGroupPage.mainMenuComponent}" rendered="true">

    <a4j:support event="onclick" action="#{mainPage.showCategoryOrgGroupPage}" reRender="workspaceForm" />


    <rich:panelMenuItem id="showCategoryOrgsListMenuItem" binding="#{categoryOrgListPage.mainMenuComponent}"
                        label="Список" action="#{categoryOrgListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedCategoryOrgsGroupMenu" label="#{categoryOrgEditPage.entityName}" rendered="false">
        <rich:panelMenuItem id="editCategoryOrgsMenuItem" binding="#{categoryOrgEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{categoryOrgEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="showCategoryOrgsCreateMenuItem" binding="#{categoryOrgCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{categoryOrgCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="categoryGroupMenu" binding="#{mainPage.categoryGroupPage.mainMenuComponent}"
                     label="Категории клиентов" rendered="#{mainPage.eligibleToViewCategory}">
    <a4j:support event="onclick" action="#{mainPage.showCategoryGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="categoryListMenuItem" label="Список" binding="#{categoryDiscountListPage.mainMenuComponent}"
                        action="#{categoryDiscountListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedCategoryGroupMenu" label="#{categoryDiscountEditPage.entityName}" rendered="false">
        <rich:panelMenuItem id="editCategoryMenuItem" binding="#{categoryDiscountEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{categoryDiscountEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="categoryCreateMenuItem" binding="#{categoryDiscountCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{categoryDiscountCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="ruleGroupMenu" binding="#{mainPage.ruleGroupPage.mainMenuComponent}"
                     label="Правила соц. скидок" rendered="#{mainPage.eligibleToViewRule}">
    <a4j:support event="onclick" action="#{mainPage.showRuleGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="ruleListMenuItem" label="Список" binding="#{ruleListPage.mainMenuComponent}"
                        action="#{ruleListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedRuleGroupMenu" label="#{ruleEditPage.entityName}" rendered="false">

        <rich:panelMenuItem id="editRuleMenuItem" binding="#{ruleEditPage.mainMenuComponent}" label="Редактирование"
                            action="#{ruleEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="ruleCreateMenuItem" binding="#{ruleCreatePage.mainMenuComponent}" label="Регистрация"
                        action="#{ruleCreatePage.show}" reRender="workspaceForm" />
</rich:panelMenuGroup>

<rich:panelMenuGroup id="reportGroupMenu" binding="#{mainPage.reportGroupPage.mainMenuComponent}" label="Отчеты">
    <a4j:support event="onclick" action="#{mainPage.showReportGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showReportTemplateManagerMenuItem"
                        binding="#{mainPage.reportTemplateManagerPage.mainMenuComponent}" label="Шаблоны отчетов"
                        action="#{mainPage.showReportTemplateManagerPage}" reRender="workspaceForm" />


    <rich:panelMenuGroup id="reportJobGroupMenu" binding="#{mainPage.reportJobGroupPage.mainMenuComponent}"
                         label="Расписание отчетов" rendered="#{mainPage.eligibleToViewReports}">
        <a4j:support event="onclick" action="#{mainPage.showReportJobGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="reportJobListMenuItem" binding="#{mainPage.reportJobListPage.mainMenuComponent}"
                            label="Список задач" action="#{mainPage.showReportJobListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedReportJobGroupMenu"
                             binding="#{mainPage.selectedReportJobGroupPage.mainMenuComponent}"
                             label="#{mainPage.selectedReportJobGroupPage.title}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedReportJobGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="reportJobViewMenuItem" binding="#{mainPage.reportJobViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{mainPage.showReportJobViewPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="reportJobEditMenuItem" binding="#{mainPage.reportJobEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showReportJobEditPage}"
                                reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <rich:panelMenuItem id="reportJobCreateMenuItem" binding="#{mainPage.reportJobCreatePage.mainMenuComponent}"
                            label="Добавить задачу" action="#{mainPage.showReportJobCreatePage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>


    <rich:panelMenuGroup id="reportRuleGroupMenu" binding="#{mainPage.reportRuleGroupPage.mainMenuComponent}"
                         label="Обработка отчетов" rendered="#{mainPage.eligibleToViewReports}">
        <a4j:support event="onclick" action="#{mainPage.showReportRuleGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="reportRuleListMenuItem" binding="#{mainPage.reportRuleListPage.mainMenuComponent}"
                            label="Список правил" action="#{mainPage.showReportRuleListPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedReportRuleGroupMenu"
                             binding="#{mainPage.selectedReportRuleGroupPage.mainMenuComponent}"
                             label="#{mainPage.selectedReportRuleGroupPage.title}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedReportRuleGroupPage}"
                         reRender="workspaceForm" />

            <rich:panelMenuItem id="reportRuleViewMenuItem" binding="#{mainPage.reportRuleViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{mainPage.showReportRuleViewPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="reportRuleEditMenuItem" binding="#{mainPage.reportRuleEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showReportRuleEditPage}"
                                reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <rich:panelMenuItem id="reportRuleCreateMenuItem" binding="#{mainPage.reportRuleCreatePage.mainMenuComponent}"
                            label="Добавить правило" action="#{mainPage.showReportRuleCreatePage}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>


</rich:panelMenuGroup>

<rich:panelMenuGroup id="configurationProviderGroupMenu"
                     binding="#{mainPage.configurationProviderGroupPage.mainMenuComponent}"
                     label="Производственная конфигурация">
    <a4j:support event="onclick" action="#{mainPage.showConfigurationProviderGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="productGroupMenu" binding="#{mainPage.productGuideGroupPage.mainMenuComponent}"
                         label="Справочник продуктов" rendered="#{mainPage.eligibleToViewRule}">
        <a4j:support event="onclick" action="#{mainPage.showProductGuideGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="productGroupsGroupMenu" binding="#{mainPage.productGroupsGroupPage.mainMenuComponent}"
                             label="Группы продуктов">

            <rich:panelMenuItem id="listProductGroupsMenuItem" label="Список"
                                binding="#{productGroupListPage.mainMenuComponent}"
                                action="#{productGroupListPage.show}" reRender="workspaceForm" />


            <rich:panelMenuGroup id="selectedProductGroupGroupMenu"
                                 binding="#{selectedProductGroupGroupPage.mainMenuComponent}"
                                 label="#{selectedProductGroupGroupPage.title}" rendered="false">
                <a4j:support event="onclick" action="#{selectedProductGroupGroupPage.show}" reRender="workspaceForm" />

                <rich:panelMenuItem id="viewProductGroupsMenuItem" label="Просмотр"
                                    binding="#{productGroupViewPage.mainMenuComponent}"
                                    action="#{productGroupViewPage.show}" reRender="workspaceForm" />

                <rich:panelMenuItem id="editProductGroupsMenuItem" label="Редактирование"
                                    binding="#{productGroupEditPage.mainMenuComponent}"
                                    action="#{productGroupEditPage.show}" reRender="workspaceForm" />

            </rich:panelMenuGroup>

            <rich:panelMenuItem id="createProductGroupsMenuItem" label="Регистрация"
                                binding="#{productGroupCreatePage.mainMenuComponent}"
                                action="#{productGroupCreatePage.show}" reRender="workspaceForm" />


        </rich:panelMenuGroup>


        <rich:panelMenuItem id="productListMenuItem" label="Список" binding="#{productListPage.mainMenuComponent}"
                            action="#{productListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedProductMenu" binding="#{selectedProductGroupPage.mainMenuComponent}"
                             label="#{selectedProductGroupPage.title}" rendered="false">
            <a4j:support event="onclick" action="#{selectedProductGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewProductMenuItem" label="Просмотр" binding="#{productViewPage.mainMenuComponent}"
                                action="#{productViewPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editProductMenuItem" label="Редактирование"
                                binding="#{productEditPage.mainMenuComponent}" action="#{productEditPage.show}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="productCreateMenuItem" label="Регистрация"
                            binding="#{productCreatePage.mainMenuComponent}" action="#{productCreatePage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="technologicalMapGroupMenu"
                         binding="#{mainPage.technologicalMapGroupPage.mainMenuComponent}"
                         label="Технологические карты">
        <a4j:support event="onclick" action="#{mainPage.showTechnologicalMapGroupPage}" reRender="workspaceForm" />
        <rich:panelMenuGroup id="technologicalMapGroupsGroupMenu"
                             binding="#{mainPage.technologicalMapGroupsGroupPage.mainMenuComponent}"
                             label="Группы технологических карт">
            <rich:panelMenuItem id="listTechnologicalMapGroupsMenuItem" label="Список"
                                binding="#{technologicalMapGroupListPage.mainMenuComponent}"
                                action="#{technologicalMapGroupListPage.show}" reRender="workspaceForm" />

            <rich:panelMenuGroup id="selectedTechnologicalMapGroupsMenu"
                                 binding="#{selectedTechnologicalMapGroupGroupPage.mainMenuComponent}"
                                 label="#{selectedTechnologicalMapGroupGroupPage.title}" rendered="false">
                <a4j:support event="onclick" action="#{selectedTechnologicalMapGroupGroupPage.show}"
                             reRender="workspaceForm" />

                <rich:panelMenuItem id="viewTechnologicalMapGroupsMenuItem" label="Просмотр"
                                    binding="#{technologicalMapGroupViewPage.mainMenuComponent}"
                                    action="#{technologicalMapGroupViewPage.show}" reRender="workspaceForm" />

                <rich:panelMenuItem id="editTechnologicalMapGroupsMenuItem" label="Редактирование"
                                    binding="#{technologicalMapGroupEditPage.mainMenuComponent}"
                                    action="#{technologicalMapGroupEditPage.show}" reRender="workspaceForm" />
            </rich:panelMenuGroup>

            <rich:panelMenuItem id="createTechnologicalMapGroupsMenuItem" label="Регистрация"
                                binding="#{technologicalMapGroupCreatePage.mainMenuComponent}"
                                action="#{technologicalMapGroupCreatePage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="listTechnologicalMapMenuItem" label="Список"
                            binding="#{technologicalMapListPage.mainMenuComponent}"
                            action="#{technologicalMapListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedTechnologicalMapsMenu"
                             binding="#{selectedTechnologicalMapGroupPage.mainMenuComponent}"
                             label="#{selectedTechnologicalMapGroupPage.title}" rendered="false">
            <a4j:support event="onclick" action="#{selectedTechnologicalMapGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewTechnologicalMapMenuItem" label="Просмотр"
                                binding="#{technologicalMapViewPage.mainMenuComponent}"
                                action="#{technologicalMapViewPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editTechnologicalMapMenuItem" label="Редактирование"
                                binding="#{technologicalMapEditPage.mainMenuComponent}"
                                action="#{technologicalMapEditPage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createTechnologicalMapMenuItem" label="Регистрация"
                            binding="#{technologicalMapCreatePage.mainMenuComponent}"
                            action="#{technologicalMapCreatePage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="configurationProviderListMenuItem" label="Список"
                        binding="#{configurationProviderListPage.mainMenuComponent}"
                        action="#{configurationProviderListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="configurationProviderCreateMenuItem" label="Регистрация"
                        binding="#{configurationProviderCreatePage.mainMenuComponent}"
                        action="#{configurationProviderCreatePage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedConfigurationProviderGroupMenu"
                         binding="#{selectedConfigurationProviderGroupPage.mainMenuComponent}"
                         label="#{selectedConfigurationProviderGroupPage.title}" rendered="false">
        <a4j:support event="onclick" action="#{selectedConfigurationProviderGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="configurationProviderViewMenuItem"
                            binding="#{configurationProviderViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{configurationProviderViewPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="configurationProviderEditMenuItem"
                            binding="#{configurationProviderEditPage.mainMenuComponent}" label="Редактирование"
                            action="#{configurationProviderEditPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>


</rich:panelMenuGroup>

<rich:panelMenuGroup id="infoGroupMenu" binding="#{mainPage.infoGroupMenu.mainMenuComponent}"
                     label="Информация">
    <rich:panelMenuItem id="showCryptoInfo" binding="#{cryptoInfoPage.mainMenuComponent}" label="Крипто-провайдер"
                        action="#{cryptoInfoPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

</rich:panelMenuGroup>

</rich:panelMenu>
</a4j:form> <%-- Главное меню --%>