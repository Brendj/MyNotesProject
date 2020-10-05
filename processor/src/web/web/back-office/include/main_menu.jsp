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
                            rendered="#{mainPage.eligibleToEditOrgs}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewOrgMenuExchange" binding="#{mainPage.menuExchangePage.mainMenuComponent}"
                            action="#{mainPage.showMenuExchangePage}" label="Просмотр мастер-меню"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="viewOrgMenuView" binding="#{mainPage.menuViewPage.mainMenuComponent}"
                            action="#{mainPage.showMenuViewPage}" label="Просмотр меню" reRender="workspaceForm" />

        <%--@elvariable id="menuLoadPage" type="ru.axetta.ecafe.processor.web.ui.org.menu.MenuLoadPage"--%>
        <rich:panelMenuItem id="loadOrgMenuView" binding="#{menuLoadPage.mainMenuComponent}"
                            action="#{menuLoadPage.show}" label="Загрузка меню" reRender="workspaceForm">
            <f:setPropertyActionListener value="#{mainPage.selectedIdOfOrg}" target="#{menuLoadPage.idOfOrg}" />
        </rich:panelMenuItem>

        <%--@elvariable id="goodRequestListPage" type="ru.axetta.ecafe.processor.web.ui.org.goodRequest.GoodRequestListPage"--%>
        <rich:panelMenuItem id="viewGoodRequestView" binding="#{goodRequestListPage.mainMenuComponent}"
                            action="#{goodRequestListPage.show}" label="Просмотр заявок" reRender="workspaceForm">
            <f:setPropertyActionListener value="#{mainPage.selectedIdOfOrg}" target="#{goodRequestListPage.idOfOrg}" />
        </rich:panelMenuItem>

        <%--@elvariable id="goodRequestPositionListPage" type="ru.axetta.ecafe.processor.web.ui.org.goodRequest.goodRequestPosition.GoodRequestPositionListPage"--%>
        <rich:panelMenuItem id="viewGoodRequestPositionView" binding="#{goodRequestPositionListPage.mainMenuComponent}"
                            action="#{goodRequestPositionListPage.show}" label="Выбранная заявка"
                            reRender="workspaceForm" rendered="false" />


        <rich:panelMenuItem id="generateOrgBalanceReportMenuItem"
                            binding="#{mainPage.orgBalanceReportPage.mainMenuComponent}" label="Отчет по балансу"
                            action="#{mainPage.showOrgBalanceReportPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="accessoriesView" binding="#{accessoriesListPage.mainMenuComponent}"
                            action="#{accessoriesListPage.show}" label="Оборудование корпуса"
                            reRender="workspaceForm" rendered="false" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createOrgMenuItem" binding="#{mainPage.orgCreatePage.mainMenuComponent}" label="Регистрация"
                        rendered="#{mainPage.eligibleToEditOrgs}" action="#{mainPage.showOrgCreatePage}"
                        reRender="workspaceForm" />

    <%--@elvariable id="settingsGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingsGroupPage"--%>
    <rich:panelMenuGroup id="settingsMenuGroup" binding="#{settingsGroupPage.mainMenuComponent}" label="Настройки"
                         reRender="workspaceForm">

        <%--@elvariable id="settingsListPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingsListPage"--%>
        <rich:panelMenuItem id="settingsListMenuItem" binding="#{settingsListPage.mainMenuComponent}" label="Список"
                            action="#{settingsListPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="selectedSettingsGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SelectedSettingsGroupPage"--%>
        <rich:panelMenuGroup id="selectSettingMenuGroup" binding="#{selectedSettingsGroupPage.mainMenuComponent}"
                             label="#{selectedSettingsGroupPage.title}" reRender="workspaceForm" rendered="false">
            <a4j:support event="onclick" action="#{selectedSettingsGroupPage.show}" reRender="workspaceForm" />

            <%--@elvariable id="settingViewPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingViewPage"--%>
            <rich:panelMenuItem id="settingViewMenuItem" binding="#{settingViewPage.mainMenuComponent}" label="Просмотр"
                                action="#{settingViewPage.show}" reRender="workspaceForm" />

            <%--@elvariable id="settingEditPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingEditPage"--%>
            <rich:panelMenuItem id="settingEditMenuItem" binding="#{settingEditPage.mainMenuComponent}"
                                label="Редактировать" action="#{settingEditPage.show}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <%--@elvariable id="settingCreatePage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingCreatePage"--%>
        <rich:panelMenuItem id="settingsCreateMenuItem" binding="#{settingCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{settingCreatePage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <%--@elvariable id="questionariesGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.QuestionariesGroupPage"--%>
    <rich:panelMenuGroup id="questionariesMenuGroup" binding="#{questionariesGroupPage.mainMenuComponent}"
                         label="Анкетирование" reRender="workspaceForm">
        <a4j:support event="onclick" action="#{questionariesGroupPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="questionaryListPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryListPage"--%>
        <rich:panelMenuItem id="questionaryListMenuItem" binding="#{questionaryListPage.mainMenuComponent}"
                            label="Анкеты" action="#{questionaryListPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="questionaryGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryGroupPage"--%>
        <rich:panelMenuGroup id="questionaryMenuGroup" binding="#{questionaryGroupPage.mainMenuComponent}"
                             label="#{questionaryGroupPage.title}" reRender="workspaceForm" rendered="false">
            <a4j:support event="onclick" action="#{questionaryGroupPage.show}" reRender="workspaceForm" />
            <%--@elvariable id="questionaryViewPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryViewPage"--%>
            <rich:panelMenuItem id="questionaryViewMenuItem" binding="#{questionaryViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{questionaryViewPage.show}" reRender="workspaceForm" />
            <%--@elvariable id="questionaryEditPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryEditPage"--%>
            <rich:panelMenuItem id="questionaryEditMenuItem" binding="#{questionaryEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{questionaryEditPage.show}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <%--@elvariable id="questionaryLoadPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryLoadPage"--%>
        <rich:panelMenuItem id="questionaryLoadMenuItem" binding="#{questionaryLoadPage.mainMenuComponent}"
                            label="Загрузить с файла" action="#{questionaryLoadPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <%--@elvariable id="clientAllocationRulesPage" type="ru.axetta.ecafe.processor.web.ui.org.ClientAllocationRulesPage"--%>
    <rich:panelMenuItem id="clientAllocationRulesItem" binding="#{clientAllocationRulesPage.mainMenuComponent}"
                        label="Настройки распределения клиентов" action="#{clientAllocationRulesPage.show}"
                        reRender="workspaceForm" rendered="#{mainPage.eligibleToEditOrgs}" />

    <%--@elvariable id="distributionRulesPage" type="ru.axetta.ecafe.processor.web.ui.org.DistributionRulesPage"--%>
    <rich:panelMenuItem id="distributionRulesMenuItem" binding="#{distributionRulesPage.mainMenuComponent}"
                        label="Правила распространения" action="#{distributionRulesPage.show}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToEditOrgs}" />

    <rich:panelMenuItem id="orgListLoaderMenuItem" binding="#{orgListLoaderPage.mainMenuComponent}"
                        label="Загрузить из файла" action="#{orgListLoaderPage.show}" reRender="workspaceForm" />

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
                            rendered="#{mainPage.eligibleToEditContragents}" reRender="workspaceForm" />

        <rich:panelMenuItem id="generateContragentClientPaymentReportMenuItem"
                            binding="#{mainPage.contragentClientPaymentReportPage.mainMenuComponent}"
                            label="Отчет по платежам клиентов"
                            action="#{mainPage.showContragentClientPaymentReportPage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createContragentMenuItem" binding="#{mainPage.contragentCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{mainPage.showContragentCreatePage}"
                        rendered="#{mainPage.eligibleToEditContragents}" reRender="workspaceForm" />

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


        <%--@elvariable id="orgOfContractsReportPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.OrgOfContractsReportPage"--%>
        <rich:panelMenuItem id="orgOfContractsReportPageMenuItem" binding="#{orgOfContractsReportPage.mainMenuComponent}"
                            label="Отчет" action="#{orgOfContractsReportPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="caOpsGroupMenu" binding="#{mainPage.caOpsGroupPage.mainMenuComponent}" label="Операции">
        <a4j:support event="onclick" action="#{mainPage.showContragentOpsGroupPage}" reRender="workspaceForm" />
        <%--@elvariable id="reconciliationPage" type="ru.axetta.ecafe.processor.web.ui.contragent.ReconciliationPage"--%>
        <rich:panelMenuItem id="caReconcileMenuItem" label="Сверка реестров"
                            binding="#{reconciliationPage.mainMenuComponent}" action="#{reconciliationPage.show}"
                            reRender="workspaceForm" />
        <%--@elvariable id="paymentStatsPage" type="ru.axetta.ecafe.processor.web.ui.contragent.PaymentStatsPage"--%>
        <rich:panelMenuItem id="caPaymentStatsMenuItem" label="Статистика платежей"
                            binding="#{paymentStatsPage.mainMenuComponent}" action="#{paymentStatsPage.show}"
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

    <rich:panelMenuGroup id="clientOpsGroupMenu" binding="#{mainPage.clientOpsGroupPage.mainMenuComponent}"
                         label="Операции">
        <a4j:support event="onclick" action="#{mainPage.showClientOpsGroupPage}" reRender="workspaceForm" />

        <%--@elvariable id="clientBalanceTransferPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientBalanceTransferPage"--%>
        <rich:panelMenuItem id="clientBalanceTransferMenuItem" label="Перевод между счетами"
                            binding="#{clientBalanceTransferPage.mainMenuComponent}"
                            action="#{clientBalanceTransferPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="clientRefundPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientRefundPage"--%>
        <rich:panelMenuItem id="clientRefundMenuItem" label="Возврат средств"
                            binding="#{clientRefundPage.mainMenuComponent}" action="#{clientRefundPage.show}"
                            reRender="workspaceForm" />

        <%--@elvariable id="clientSubAccountTransferPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientSubAccountTransferPage"--%>
        <rich:panelMenuItem id="clientSubAccountTransferMenuItem" label="Перевод между субсчетами"
                            binding="#{clientSubAccountTransferPage.mainMenuComponent}"
                            action="#{clientSubAccountTransferPage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

</rich:panelMenuGroup>

<rich:panelMenuGroup id="visitorDogmGroupMenu" binding="#{mainPage.visitorDogmPage.mainMenuComponent}" label="Сотрудники"
                     rendered="#{mainPage.eligibleToEditVisitorDogm}">
    <a4j:support event="onclick" action="#{mainPage.showVisitorDogmPage}" reRender="workspaceForm" />

    <%--@elvariable id="visitorDogmListPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmListPage"--%>
    <%--@elvariable id="visitorDogmGroupPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmGroupPage"--%>
    <%--@elvariable id="visitorDogmViewPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmViewPage"--%>
    <%--@elvariable id="visitorDogmEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmEditPage"--%>
    <%--@elvariable id="visitorDogmCreatePage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCreatePage"--%>
    <%--@elvariable id="visitorDogmHistoryReportPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmHistoryReportPage"--%>

    <rich:panelMenuItem id="showVisitorDogmListMenuItem" binding="#{visitorDogmListPage.mainMenuComponent}" label="Список"
                        action="#{visitorDogmListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedVisitorDogmGroupMenu" label="#{visitorDogmGroupPage.currentVisitorDogm.shortFullName}"
                         binding="#{visitorDogmGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{visitorDogmGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewVisitorDogmMenuItem" binding="#{visitorDogmViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{visitorDogmViewPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editVisitorDogmMenuItem" binding="#{visitorDogmEditPage.mainMenuComponent}"
                            label="Редактировать" action="#{visitorDogmEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createVisitorDogmListMenuItem" binding="#{visitorDogmCreatePage.mainMenuComponent}"
                        label="Создание" action="#{visitorDogmCreatePage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="loadVisitorFromFileMenuItem" binding="#{mainPage.visitorDogmLoadPage.mainMenuComponent}"
                        label="Загрузить из файла" action="#{mainPage.showVisitorDogmLoadPage}" reRender="workspaceForm" />

    <%--@elvariable id="visitorsDogmCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorsDogmCardGroupPage"--%>
    <rich:panelMenuGroup id="visitorDogmCardGroupMenu" label="Карты"
                         binding="#{visitorsDogmCardGroupPage.mainMenuComponent}">

        <%--@elvariable id="visitorDogmCardListPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardListPage"--%>
        <%--@elvariable id="visitorDogmCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardGroupPage"--%>
        <%--@elvariable id="visitorDogmCardViewPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardViewPage"--%>
        <%--@elvariable id="visitorDogmCardEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardEditPage"--%>
        <%--@elvariable id="visitorDogmCardCreatePage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardCreatePage"--%>

        <rich:panelMenuItem id="showVisitorDogmCardListMenuItem" binding="#{visitorDogmCardListPage.mainMenuComponent}"
                            label="Список" action="#{visitorDogmCardListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedVisitorDogmCardGroupMenu"
                             label="#{visitorDogmCardGroupPage.currentCard.cardPrintedNo}"
                             binding="#{visitorDogmCardGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{visitorDogmCardGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewVisitorDogmCardMenuItem" binding="#{visitorDogmCardViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{visitorDogmCardViewPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editVisitorDogmCardMenuItem" binding="#{visitorDogmCardEditPage.mainMenuComponent}"
                                label="Редактировать" action="#{visitorDogmCardEditPage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createVisitorDogmCardListMenuItem" binding="#{visitorDogmCardCreatePage.mainMenuComponent}"
                            label="Создание" action="#{visitorDogmCardCreatePage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="visitorDogmHistoryReportMenuItem" binding="#{visitorDogmHistoryReportPage.mainMenuComponent}"
                        label="Отчет по проходам" action="#{visitorDogmHistoryReportPage.show}" reRender="workspaceForm" />
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
    <rich:panelMenuItem id="loadNewCardFromFileMenuItem" binding="#{mainPage.newCardFileLoadPage.mainMenuComponent}"
                        label="Загрузить из файла новые карты" action="#{mainPage.showNewCardFileLoadPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="cardExpireBatchEditMenu" label="Изменить дату валидности"
                        binding="#{mainPage.cardExpireBatchEditPage.mainMenuComponent}"
                        action="#{mainPage.showCardExpireBatchEditPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToEditCards}"/>

    <%--@elvariable id="cardSignsGroupPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignsGroupPage"--%>
    <rich:panelMenuGroup id="cardSignsGroupMenu" label="Цифровые подписи" binding="#{cardSignsGroupPage.mainMenuComponent}">
        <%--@elvariable id="cardSignListPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignListPage"--%>
        <%--@elvariable id="cardSignGroupPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignGroupPage"--%>
        <%--@elvariable id="cardSignViewPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignViewPage"--%>
        <%--@elvariable id="cardSignEditPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignEditPage"--%>
        <%--@elvariable id="cardSignCreatePage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignCreatePage"--%>

        <rich:panelMenuItem id="showCardSignListMenuItem" binding="#{cardSignListPage.mainMenuComponent}"
                            label="Список" action="#{cardSignListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedCardSignGroupMenu"
                             label="#{cardSignGroupPage.currentCard.printedName}"
                             binding="#{cardSignGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{cardSignGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewCardSignMenuItem" binding="#{cardSignViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{cardSignViewPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editCardSignMenuItem" binding="#{cardSignEditPage.mainMenuComponent}"
                                label="Редактировать" action="#{cardSignEditPage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="cardSignListMenuItem" binding="#{cardSignCreatePage.mainMenuComponent}"
                            label="Создание" action="#{cardSignCreatePage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>
    <%--@elvariable id="showDetailedEnterEventReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.BlockUnblockReportPage"--%>
    <rich:panelMenuItem id="blockUnblockCard" binding="#{mainPage.blockUnblockReportPage.mainMenuComponent}"
                        label="Отчет по блокировке/разблокировке карт" action="#{mainPage.showBlockUnblockReportPage}"
                        reRender="workspaceForm" />

</rich:panelMenuGroup>

<%--@elvariable id="commodityAccountingGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.CommodityAccountingGroupPage"--%>
<rich:panelMenuGroup id="commodityAccountingGroupMenu" binding="#{commodityAccountingGroupPage.mainMenuComponent}"
                     label="Товарный учет" rendered="#{commodityAccountingGroupPage.eligibleToWorkCommodityAccounting}">
<%-- <a4j:support event="onclick" action="#{commodityAccountingGroupPage.show}" reRender="workspaceForm" />--%>

<rich:panelMenuGroup id="configurationProviderGroupMenu"
                     binding="#{mainPage.configurationProviderGroupPage.mainMenuComponent}"
                     label="Производственная конфигурация">
<a4j:support event="onclick" action="#{mainPage.showConfigurationProviderGroupPage}" reRender="workspaceForm" />

<%--@elvariable id="goodCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodCreatePage"--%>
<%--@elvariable id="goodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodEditPage"--%>
<%--@elvariable id="goodViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodViewPage"--%>
<%--@elvariable id="goodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodListPage"--%>
<%--@elvariable id="selectedGoodGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.SelectedGoodGroupPage"--%>

<%--@elvariable id="goodGroupCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupCreatePage"--%>
<%--@elvariable id="goodGroupListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupListPage"--%>
<%--@elvariable id="goodGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupEditPage"--%>
<%--@elvariable id="goodGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupViewPage"--%>
<%--@elvariable id="selectedGoodGroupGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.SelectedGoodGroupGroupPage"--%>

<rich:panelMenuGroup id="goodGroupMenu" binding="#{mainPage.goodGroupPage.mainMenuComponent}" label="Справочник товаров"
                     rendered="#{commodityAccountingGroupPage.eligibleToWorkCommodityAccounting}">
    <a4j:support event="onclick" action="#{mainPage.showGoodGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup label="Группы товаров" id="goodsGroupsGroupMenu"
                         binding="#{mainPage.goodGroupsGroupPage.mainMenuComponent}">

        <rich:panelMenuItem id="listGoodGroupsMenuItem" label="Список" binding="#{goodGroupListPage.mainMenuComponent}"
                            action="#{goodGroupListPage.show}" reRender="workspaceForm" />
        <rich:panelMenuGroup id="selectedGoodGroupGroupMenu" binding="#{selectedGoodGroupGroupPage.mainMenuComponent}"
                             label="#{selectedGoodGroupGroupPage.title}" rendered="false">
            <a4j:support event="onclick" action="#{selectedGoodGroupGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewGoodGroupsMenuItem" label="Просмотр"
                                binding="#{goodGroupViewPage.mainMenuComponent}" action="#{goodGroupViewPage.show}"
                                reRender="workspaceForm" />

            <rich:panelMenuItem id="editGoodGroupsMenuItem" label="Редактирование"
                                binding="#{goodGroupEditPage.mainMenuComponent}" action="#{goodGroupEditPage.show}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createGoodGroupsMenuItem" label="Регистрация"
                            binding="#{goodGroupCreatePage.mainMenuComponent}" action="#{goodGroupCreatePage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="goodListMenuItem" label="Список" binding="#{goodListPage.mainMenuComponent}"
                        action="#{goodListPage.show}" reRender="workspaceForm" />


    <rich:panelMenuGroup id="selectedGoodMenu" binding="#{selectedGoodGroupPage.mainMenuComponent}"
                         label="#{selectedGoodGroupPage.title}" rendered="false">
        <a4j:support event="onclick" action="#{selectedGoodGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewGoodMenuItem" label="Просмотр" binding="#{goodViewPage.mainMenuComponent}"
                            action="#{goodViewPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editGoodMenuItem" label="Редактирование" binding="#{goodEditPage.mainMenuComponent}"
                            action="#{goodEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="goodCreateMenuItem" label="Регистрация" binding="#{goodCreatePage.mainMenuComponent}"
                        action="#{goodCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<%--@elvariable id="basicGoodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodListPage"--%>
<%--@elvariable id="basicGoodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodEditPage"--%>
<%--@elvariable id="basicGoodCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodCreatePage"--%>
<%--@elvariable id="basicGoodViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodViewPage"--%>
<rich:panelMenuGroup id="basicGoodGroupMenu" label="Справочник базовых товаров"
                     binding="#{basicGoodListPage.groupPage.mainMenuComponent}">
    <rich:panelMenuItem id="listBasicGoodMenuItem" label="Список" binding="#{basicGoodListPage.mainMenuComponent}"
                        action="#{basicGoodListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedBasicGoodGroupMenu" label="#{basicGoodEditPage.selectedEntityGroupPage.title}"
                         binding="#{basicGoodEditPage.selectedEntityGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{basicGoodEditPage.selectedEntityGroupPage.show}"
                     reRender="workspaceForm" />


        <rich:panelMenuItem id="viewBasicGoodMenuItem" binding="#{basicGoodViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{basicGoodViewPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="editBasicGoodMenuItem" binding="#{basicGoodEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{basicGoodEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createBasicGoodMenuItem" binding="#{basicGoodCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{basicGoodCreatePage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="loadingElementsOfBasicGoodsMenuItem" binding="#{mainPage.loadingElementsOfBasicGoodsPage.mainMenuComponent}"
                        label="Загрузка элементов Базовых Товаров из csv" action="#{mainPage.loadingElementsOfBasicGoodsPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>


<rich:panelMenuGroup id="productGroupMenu" binding="#{mainPage.productGuideGroupPage.mainMenuComponent}"
                     label="Справочник продуктов"
                     rendered="#{commodityAccountingGroupPage.eligibleToWorkCommodityAccounting}">
    <a4j:support event="onclick" action="#{mainPage.showProductGuideGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="productGroupsGroupMenu" binding="#{mainPage.productGroupsGroupPage.mainMenuComponent}"
                         label="Группы продуктов">

        <rich:panelMenuItem id="listProductGroupsMenuItem" label="Список"
                            binding="#{productGroupListPage.mainMenuComponent}" action="#{productGroupListPage.show}"
                            reRender="workspaceForm" />


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

    <rich:panelMenuItem id="productCreateMenuItem" label="Регистрация" binding="#{productCreatePage.mainMenuComponent}"
                        action="#{productCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="technologicalMapGroupMenu" binding="#{mainPage.technologicalMapGroupPage.mainMenuComponent}"
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

<%--@elvariable id="configurationProviderListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderListPage"--%>
<rich:panelMenuItem id="configurationProviderListMenuItem" label="Список"
                    binding="#{configurationProviderListPage.mainMenuComponent}"
                    action="#{configurationProviderListPage.show}" reRender="workspaceForm"
                    rendered="#{configurationProviderListPage.eligibleToWorkConfigurationProviderList}" />

<%--@elvariable id="configurationProviderCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderCreatePage"--%>
<rich:panelMenuItem id="configurationProviderCreateMenuItem" label="Регистрация"
                    binding="#{configurationProviderCreatePage.mainMenuComponent}"
                    action="#{configurationProviderCreatePage.show}" reRender="workspaceForm"
                    rendered="#{configurationProviderCreatePage.eligibleToWorkConfigurationProviderList}" />

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
<%--@elvariable id="documentsGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.DocumentsGroupPage"--%>
<rich:panelMenuGroup id="documentsGroupMenu" binding="#{documentsGroupPage.mainMenuComponent}" label="Документы">
    <a4j:support event="onclick" action="#{documentsGroupPage.show}" reRender="workspaceForm" />
    <%--@elvariable id="wayBillGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillGroupPage"--%>
    <rich:panelMenuGroup id="wayBillGroupMenu" label="Накладные" binding="#{wayBillGroupPage.mainMenuComponent}"
                         reRender="workspaceForm">
        <a4j:support event="onclick" action="#{wayBillGroupPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="wayBillListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillListPage"--%>
        <rich:panelMenuItem id="wayBillListMenuItem" label="Список" binding="#{wayBillListPage.mainMenuComponent}"
                            action="#{wayBillListPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="wayBillPositionListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillPositionListPage"--%>
        <rich:panelMenuItem id="wayBillPositionListMenuItem" label="Подробно"
                            binding="#{wayBillPositionListPage.mainMenuComponent}"
                            action="#{wayBillPositionListPage.show}" reRender="workspaceForm"
                            rendered="#{wayBillPositionListPage.wayBillItem!=null}" />
    </rich:panelMenuGroup>

    <%--@elvariable id="actOfInventarizationListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.ActOfInventorizationListPage"--%>
    <rich:panelMenuItem id="actOfInventorizationListMenuItem" label="Акты инвентаризации"
                        binding="#{actOfInventorizationListPage.mainMenuComponent}"
                        action="#{actOfInventorizationListPage.show}" reRender="workspaceForm" />
    <%--@elvariable id="actOfWayBillDifferencePositionListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.ActOfWayBillDifferencePositionListPage"--%>
    <rich:panelMenuItem id="actOfWayBillDifferencePositionListMenuItem" label="Акты о наличии расхождений"
                        binding="#{actOfWayBillDifferencePositionListPage.mainMenuComponent}"
                        action="#{actOfWayBillDifferencePositionListPage.show}" reRender="workspaceForm" />
    <%--@elvariable id="tradeMaterialGoodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.TradeMaterialGoodListPage"--%>
    <rich:panelMenuItem id="tradeMaterialGoodListMenuItem" label="Товарно-материальные ценности"
                        binding="#{tradeMaterialGoodListPage.mainMenuComponent}"
                        action="#{tradeMaterialGoodListPage.show}" reRender="workspaceForm" />
</rich:panelMenuGroup>
</rich:panelMenuGroup>

<rich:panelMenuGroup id="serviceGroupMenu" binding="#{mainPage.serviceGroupPage.mainMenuComponent}" label="Сервис"
                     rendered="#{mainPage.eligibleToServiceAdmin || mainPage.eligibleToServiceSupport}">
    <a4j:support event="onclick" action="#{mainPage.showServiceGroupPage}" reRender="workspaceForm" />

    <%--@elvariable id="atolCompanyPage" type="ru.axetta.ecafe.processor.web.ui.service.atol.AtolCompanyPage"--%>
    <rich:panelMenuItem id="atolCompanyMenuItem" binding="#{atolCompanyPage.mainMenuComponent}"
                        label="Атрибуты компании" action="#{atolCompanyPage.show}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}" />

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
    <%--@elvariable id="supportSMSPage" type="ru.axetta.ecafe.processor.web.ui.service.SupportSMSPage"--%>
    <rich:panelMenuItem id="supportSMSMenuItem" binding="#{supportSMSPage.mainMenuComponent}" label="Отправка SMS"
                        action="#{supportSMSPage.show}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceSupport && !mainPage.SMSServiceEMP}" />
    <rich:panelMenuItem id="supportInfoMailingMenuItem" binding="#{mainPage.supportInfoMailingPage.mainMenuComponent}"
                        label="Информационная рассылка (ручной ввод)" action="#{mainPage.showSupportInfoMailingPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceSupport && mainPage.infospamEnabled}" />

    <rich:panelMenuItem id="showJournal" label="Очередь выгрузки транзакций"
                        binding="#{journalViewPage.mainMenuComponent}" action="#{journalViewPage.show}"
                        rendered="#{mainPage.eligibleToServiceAdmin}" reRender="workspaceForm" />

    <%--@elvariable id="usePlanOrdersRequestPage" type="ru.axetta.ecafe.processor.web.ui.service.UsePlanOrdersRequestPage"--%>
    <rich:panelMenuItem id="usePlanOrdersRequestItem" label="Запрос использования плана питания"
                        binding="#{usePlanOrdersRequestPage.mainMenuComponent}" action="#{usePlanOrdersRequestPage.show}"
                        reRender="workspaceForm" />


    <rich:panelMenuItem id="repositoryReportsRenameMenuItem" binding="#{repositoryReportsRenamePage.mainMenuComponent}"
                        label="Переименование отчетов репозитория" action="#{repositoryReportsRenamePage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="loadRegistryFromFileMenuItem" binding="#{mainPage.registryLoadPage.mainMenuComponent}"
                        label="Обработать параметры клиентов из файлов" action="#{mainPage.showRegistryLoadPage}" reRender="workspaceForm" />

    <%--@elvariable id="migrantsPage" type="ru.axetta.ecafe.processor.web.ui.client.MigrantsPage"--%>
    <rich:panelMenuItem id="migrantsMenuItem" binding="#{migrantsPage.mainMenuComponent}"
                        label="Заявки на посещение" action="#{migrantsPage.show}" reRender="workspaceForm" />

    <%--@elvariable id="otherActionsPage" type="ru.axetta.ecafe.processor.web.ui.service.OtherActionsPage"--%>
    <rich:panelMenuGroup id="otherActionsMenuItem" binding="#{otherActionsPage.mainMenuComponent}" label="Другое"
                         rendered="#{mainPage.eligibleToServiceAdmin}" >
        <a4j:support event="onclick" action="#{otherActionsPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="notificationsPage" type="ru.axetta.ecafe.processor.web.ui.service.NotificationsPage"--%>
        <rich:panelMenuItem id="notificationsMenuItem" binding="#{notificationsPage.mainMenuComponent}"
                            label="Уведомления" action="#{notificationsPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="nsiGroup" binding="#{mainPage.nsiGroupPage.mainMenuComponent}" label="Сверка"
                         rendered="#{mainPage.eligibleToServiceAdmin && mainPage.mskRegistry}">
        <a4j:support event="onclick" action="#{mainPage.showNSIGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="nsiGroupContingent" binding="#{mainPage.nsiGroupContingentPage.mainMenuComponent}" label="Сверка контингента"
                             rendered="#{mainPage.eligibleToServiceAdmin}">
            <a4j:support event="onclick" action="#{mainPage.showNSIGroupContingentPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="nsiPupilCatalogFind" binding="#{pupilCatalogFindPage.mainMenuComponent}"
                                label="Поиск учащихся" action="#{pupilCatalogFindPage.show}" reRender="workspaceForm" />
            <rich:panelMenuItem id="nsiOrgRegistrySync" binding="#{NSIOrgRegistrySynchPage.mainMenuComponent}"
                                label="Интерактивная сверка" action="#{NSIOrgRegistrySynchPage.show}"
                                reRender="workspaceForm" />
            <rich:panelMenuItem id="nsiOrgRegistrySyncOverview" binding="#{NSIOrgRegistrySynchOverviewPage.mainMenuComponent}"
                                label="Статистика сверки" action="#{NSIOrgRegistrySynchOverviewPage.show}"
                                reRender="workspaceForm" />

            <%--@elvariable id="photoRegistryPage" type="ru.axetta.ecafe.processor.web.ui.service.PhotoRegistryPage"--%>
            <rich:panelMenuItem id="photoRegistryItemMSK" binding="#{photoRegistryPage.mainMenuComponent}"
                                label="Сверка фотографий клиентов" action="#{photoRegistryPage.show}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuGroup id="nsiGroupOrg" binding="#{mainPage.nsiGroupOrgPage.mainMenuComponent}" label="Сверка организаций"
                             rendered="#{mainPage.eligibleToServiceAdmin}">
            <a4j:support event="onclick" action="#{mainPage.showNSIGroupOrgPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="nsiOrgCatalogFind" binding="#{orgCatalogFindPage.mainMenuComponent}"
                                label="Поиск организаций" action="#{orgCatalogFindPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="nsiOrgsRegistrySync" binding="#{NSIOrgsRegistrySynchPage.mainMenuComponent}"
                                label="Сверка организаций" action="#{NSIOrgsRegistrySynchPage.show}"
                                reRender="workspaceForm" />

            <rich:panelMenuItem id="nsiOrgsRegistrySyncSettings" binding="#{NSIOrgsRegistrySynchSettingsPage.mainMenuComponent}"
                                label="Параметры сверки" action="#{NSIOrgsRegistrySynchSettingsPage.show}"
                                reRender="workspaceForm" />

            <rich:panelMenuItem id="nsiOrgsRegistryStat" binding="#{NSIOrgsRegistryStatPage.mainMenuComponent}"
                                label="Статистика сверки" action="#{NSIOrgsRegistryStatPage.show}"
                                reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuGroup id="nsiGroupEmployee" binding="#{mainPage.nsiGroupEmployeePage.mainMenuComponent}" label="Сверка сотрудников"
                             rendered="#{mainPage.eligibleToServiceAdmin}">
            <rich:panelMenuItem id="nsiOrgRegistryEmployeeSync" binding="#{NSIOrgRegistryEmployeeSynchPage.mainMenuComponent}"
                                label="Интерактивная сверка" action="#{NSIOrgRegistryEmployeeSynchPage.show}"
                                reRender="workspaceForm" />
        </rich:panelMenuGroup>

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="spbGroup" binding="#{mainPage.spbGroupPage.mainMenuComponent}" label="Сверка"
                         rendered="#{mainPage.eligibleToServiceAdmin && mainPage.spbRegistry}">
        <a4j:support event="onclick" action="#{mainPage.showSpbGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="spbGroupContingent" binding="#{mainPage.spbGroupContingentPage.mainMenuComponent}" label="Сверка контингента"
                             rendered="#{mainPage.eligibleToServiceAdmin}">
            <a4j:support event="onclick" action="#{mainPage.showSpbGroupContingentPage}" reRender="workspaceForm" />

            <%--@elvariable id="spbRegistrySyncPage" type="ru.axetta.ecafe.processor.web.ui.service.spb.SpbRegistrySyncPage"--%>
            <rich:panelMenuItem id="spbOrgRegistrySync" binding="#{spbRegistrySyncPage.mainMenuComponent}"
                                label="Интерактивная сверка" action="#{spbRegistrySyncPage.show}"
                                reRender="workspaceForm" />
            <%--@elvariable id="spbRegistrySynchOverviewPage" type="ru.axetta.ecafe.processor.web.ui.service.spb.SpbRegistrySynchOverviewPage"--%>
            <rich:panelMenuItem id="spbOrgRegistrySyncOverview" binding="#{spbRegistrySynchOverviewPage.mainMenuComponent}"
                                label="Статистика сверки" action="#{spbRegistrySynchOverviewPage.show}"
                                reRender="workspaceForm" />
            <%--@elvariable id="photoRegistryPageSpb" type="ru.axetta.ecafe.processor.web.ui.service.PhotoRegistryPageSpb"--%>
            <rich:panelMenuItem id="photoRegistryPageSpb" binding="#{photoRegistryPageSpb.mainMenuComponent}"
                                label="Сверка фотографий клиентов" action="#{photoRegistryPageSpb.show}"
                                reRender="workspaceForm" />
        </rich:panelMenuGroup>
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="uosGroup" binding="#{mainPage.uosGroupPage.mainMenuComponent}" label="УОС"
                         rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" action="#{mainPage.showUOSGroupPage}" reRender="workspaceForm" />

        <%--@elvariable id="uosSendBlockPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.UosSendBlockPage"--%>
        <%--@elvariable id="uosStopListPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.UosStopListPage"--%>
        <rich:panelMenuItem id="uosSendBlock" binding="#{uosSendBlockPage.mainMenuComponent}" label="Запрос блокировки"
                            action="#{uosSendBlockPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="uosStopList" binding="#{uosStopListPage.mainMenuComponent}"
                            label="Получение стоп-листов" action="#{uosStopListPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>
    <%--<rich:panelMenuItem label="Отправка SMS" action="#{mainPage.showSupportSmsSender}"--%>
    <%--reRender="mainMenu, workspaceForm" />
    --%>
    <rich:panelMenuGroup id="autorecharge" binding="#{mainPage.autorechargePage.mainMenuComponent}" label="Автопополнение"
                        rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" action="#{mainPage.showAutorechargeGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="groupControlSubscriptions" binding="#{mainPage.groupControlSubscriptionsPage.mainMenuComponent}" label="Групповое управление подписками"
                            action="#{mainPage.groupControlSubscriptionsPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="benefit" binding="#{mainPage.benefitPage.mainMenuComponent}" label="Льготы"
                         rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" action="#{mainPage.showBenefitGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="groupControlBenefits" binding="#{mainPage.groupControlBenefitsPage.mainMenuComponent}" label="Групповая установка льготных категорий"
                            action="#{mainPage.groupControlBenefitsPage.show}" reRender="workspaceForm" />


        <rich:panelMenuItem id="cancelBenefits" binding="#{mainPage.cancelCategoryBenefitsPage.mainMenuComponent}" label="Отмена льготных категорий по всем учащимся"
                            action="#{mainPage.cancelCategoryBenefitsPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="preorderGroup" binding="#{mainPage.preorderPage.mainMenuComponent}" label="Предзаказ"
                         rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" action="#{mainPage.showPreorderGroupPage}" reRender="workspaceForm" />

        <%--@elvariable id="preorderJournalReportPage" type="ru.axetta.ecafe.processor.web.ui.service.PreorderJournalReportPage"--%>
        <rich:panelMenuItem id="preorderJournalReport" binding="#{preorderJournalReportPage.mainMenuComponent}"
                            label="Журнал операций ВП" action="#{preorderJournalReportPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="preorderStatsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderStatsReportPage"--%>
        <rich:panelMenuItem id="preorderStatsMenuItem" binding="#{preorderStatsReportPage.mainMenuComponent}"
                            label="Статистика по количеству заказанного питания по предзаказу" action="#{preorderStatsReportPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="preorderCheckReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderCheckReportPage"--%>
        <rich:panelMenuItem id="preorderCheckMenuItem" binding="#{preorderCheckReportPage.mainMenuComponent}"
                            label="Проверка создания заявок по предзаказу" action="#{preorderCheckReportPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="preorderDoublePaymentReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderDoublePaymentReportPage"--%>
        <rich:panelMenuItem id="preorderDoublePaymentMenuItem" binding="#{preorderDoublePaymentReportPage.mainMenuComponent}"
                            label="Проверка двойных оплат" action="#{preorderDoublePaymentReportPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuItem id="empInfo" binding="#{mainPage.empInfoPage.mainMenuComponent}"
                        label="Просмотр записей в ЕМП" action="#{mainPage.empInfoPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="syncStats" binding="#{mainPage.syncStatsPage.mainMenuComponent}"
                        label="Статистика по синхронизациям" action="#{mainPage.showSyncStatsPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="serviceRNIP" binding="#{mainPage.serviceRNIPPage.mainMenuComponent}"
                        label="РНИП" action="#{mainPage.serviceRNIPPage.show}" reRender="workspaceForm" />

    <%--@elvariable id="applicationForFoodReportPage" type="ru.axetta.ecafe.processor.web.ui.service.ApplicationForFoodReportPage"--%>
    <rich:panelMenuItem id="serviceApplicationForFoodReport" binding="#{applicationForFoodReportPage.mainMenuComponent}"
                        label="Заявления на льготное питание" action="#{applicationForFoodReportPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="orgParameters" binding="#{mainPage.orgParametersGroup.mainMenuComponent}" label="Параметры ОО"
                         rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" action="#{mainPage.showOrgParametersGroupPage}" reRender="workspaceForm" />
        <%--@elvariable id="orgSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSettingsReportPage"--%>
        <rich:panelMenuItem id="orgSettingsReport" binding="#{orgSettingsReportPage.mainMenuComponent}"
                            label="Настройки ОО" action="#{orgSettingsReportPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="orgSyncSettingReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncSettingReportPage"--%>
        <rich:panelMenuItem id="orgSyncSettingsReport" binding="#{orgSyncSettingReportPage.mainMenuComponent}"
                            label="Расписание синхронизации" action="#{orgSyncSettingReportPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="orgSyncRequestPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncRequestPage"--%>
        <rich:panelMenuItem id="orgSyncRequest" binding="#{orgSyncRequestPage.mainMenuComponent}" reRender="workspaceForm"
                            label="Запрос проведения синхронизации" action="#{orgSyncRequestPage.show}"/>
        <%--@elvariable id="hardwareSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.HardwareSettingsReportPage"--%>
        <rich:panelMenuItem id="orgHardwareSettingsReport" binding="#{hardwareSettingsReportPage.mainMenuComponent}"
                            label="Отчет по оборудованию" action="#{hardwareSettingsReportPage.show}" reRender="workspaceForm"/>
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="webTechnologistGroupMenuItem" label="WEB-Технолог"
                         binding="#{mainPage.webTechnologistGroupPage.mainMenuComponent}"
                         rendered="#{mainPage.eligibleToServiceAdmin}">
        <a4j:support event="onclick" reRender="workspaceForm" action="#{mainPage.showWebTechnologistGroupPage}" />
        <rich:panelMenuGroup id="webTechnologistCatalogs" label="Справочники" binding="#{mainPage.webTechnologistCatalogGroupPage.mainMenuComponent}">
            <a4j:support event="onclick" reRender="workspaceForm"
                         action="#{mainPage.showWebTechnologistCatalogGroupPage}"/>
            <%-- TODO: Функционал по задаче https://gitlab.iteco.mobi/ispp/processor/issues/148 отключен до востребования
            <%--@elvariable id="catalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogListPage"--%>
            <%--<rich:panelMenuItem id="webTechnologistUserCatalogsList"
                                binding="#{catalogListPage.mainMenuComponent}"
                                label="Пользовательские справочники категорий блюд" action="#{catalogListPage.show}"
                                reRender="workspaceForm" />--%>
            <%--@elvariable id="ageGroupCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.AgeGroupCatalogListPage"
            <rich:panelMenuItem id="webTechnologistAgeGroupItemList"
                                binding="#{ageGroupCatalogListPage.mainMenuComponent}"
                                label="Возрастная категория" action="#{ageGroupCatalogListPage.show}"
                                reRender="workspaceForm" />
            @elvariable id="typeOfProductionCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.TypeOfProductionCatalogListPage"
            <rich:panelMenuItem id="webTechnologistTypeOfProductionItemList"
                                binding="#{typeOfProductionCatalogListPage.mainMenuComponent}"
                                label="Вид производства" action="#{typeOfProductionCatalogListPage.show}"
                                reRender="workspaceForm" />--%>
            <%--@elvariable id="categoryCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.CategoryCatalogListPage"--%>
            <rich:panelMenuItem id="webTechnologistCategoryCatalogsItemList"
                                binding="#{categoryCatalogListPage.mainMenuComponent}"
                                label="Категории блюд" action="#{categoryCatalogListPage.show}"
                                reRender="workspaceForm" />
            <%--@elvariable id="groupItemCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.GroupItemCatalogListPage"
            <rich:panelMenuItem id="webTechnologistGroupCatalogsItemsList"
                                binding="#{groupItemCatalogListPage.mainMenuComponent}"
                                label="Группы блюд" action="#{groupItemCatalogListPage.show}"
                                reRender="workspaceForm" />--%>
        </rich:panelMenuGroup>
    </rich:panelMenuGroup>
</rich:panelMenuGroup>

<rich:panelMenuGroup id="monitoringGroupMenu" binding="#{mainPage.monitoringGroupPage.mainMenuComponent}"
                     label="Мониторинг" rendered="#{mainPage.eligibleToMonitor}">
    <a4j:support event="onclick" action="#{mainPage.showMonitoringGroupPage}" reRender="workspaceForm" />
    <rich:panelMenuItem id="syncMonitorItem" binding="#{mainPage.syncMonitorPage.mainMenuComponent}"
                        label="Мониторинг синхронизации" action="#{mainPage.syncMonitorPage.show}" reRender="workspaceForm" />

    <%--@elvariable id="dashboardPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.DashboardPage"--%>
    <rich:panelMenuItem id="dashboardMenuItem" binding="#{dashboardPage.mainMenuComponent}"
                        label="Мониторинг активности" action="#{dashboardPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="syncReportMenuItem" binding="#{mainPage.syncReportPage.mainMenuComponent}"
                        label="Отчет по синхронизации" action="#{mainPage.showSyncReportPage}"
                        reRender="workspaceForm" />

    <rich:panelMenuItem id="statusSyncReportMenuItem" binding="#{mainPage.statusSyncReportPage.mainMenuComponent}"
                        label="Статус синхронизации" action="#{mainPage.showStatusSyncReportPage}"
                        reRender="workspaceForm" />

    <%-- Скрыто в соответствии с задачей EP-266
    <rich:panelMenuItem id="projectStateMenuItem" label="Ключевые показатели"
                        onclick="window.open('/processor/back-office/project_state.jsp', 'Ключевые показатели')" />--%>

    <%--<rich:panelMenuItem id="ordersMonitoringMenuItem"
                        binding="#{ordersMonitoringReportPage.mainMenuComponent}"
                        label="Заказ питания" action="#{ordersMonitoringReportPage.show}"
                        reRender="workspaceForm" />--%>

    <%--@elvariable id="monitoringPersistanceCachePage" type="ru.axetta.ecafe.processor.web.ui.monitoring.MonitoringPersistanceCachePage"--%>
    <rich:panelMenuItem id="persistanceCacheItem" binding="#{monitoringPersistanceCachePage.mainMenuComponent}"
                        label="Системные показатели" action="#{monitoringPersistanceCachePage.show}" reRender="workspaceForm" />
</rich:panelMenuGroup>

<%--<rich:panelMenuGroup id="chartsGroupMenu" binding="#{mainPage.chartsGroupPage.mainMenuComponent}"
                         label="Мониторинг ОУ">
    <rich:panelMenuItem id="enterCardsChartReportMenuItem" binding="#{mainPage.enterCardsChartReportPage.mainMenuComponent}"
                        label="Использование электронных носителей при посещении здания ОО"
                        action="#{mainPage.showEnterCardsChartReportPage}" reRender="workspaceForm" />
</rich:panelMenuGroup>--%>

<rich:panelMenuGroup id="reportOnlineGroupMenu" binding="#{mainPage.reportOnlineGroupPage.mainMenuComponent}"
                     label="Онлайн отчеты" rendered="#{mainPage.eligibleToWorkOnlineReport}">
    <a4j:support event="onclick" action="#{mainPage.showReportOnlineGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="infoMessageGroupMenu" label="Сообщения в АРМ администратора ОО"
                         binding="#{mainPage.infoMessageGroupPage.mainMenuComponent}" rendered="#{mainPage.eligibleToViewMessageARM}">
        <a4j:support event="onclick" action="#{mainPage.showInfoMessageGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="infoMessageList" binding="#{mainPage.infoMessagePage.mainMenuComponent}"
                            label="Список" action="#{mainPage.showInfoMessagePage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="infoMessageCreate" binding="#{mainPage.infoMessageCreatePage.mainMenuComponent}"
                            label="Новое сообщение" action="#{mainPage.showInfoMessageCreatePage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="complexGroupMenu" binding="#{mainPage.complexGroupPage.mainMenuComponent}"
                         label="Отчеты по комплексам" rendered="#{mainPage.eligibleToViewComplexReports}">
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
                         label="Отчеты по льготам" rendered="#{mainPage.eligibleToViewBenefitReports}">
        <a4j:support event="onclick" action="#{mainPage.showDiscountGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="allOrgsReportMenuItem"
                            binding="#{mainPage.allOrgsDiscountsReportPage.mainMenuComponent}"
                            label="Отчет по всем организациям" action="#{mainPage.showAllOrgsDiscountReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="orgDiscountsReportMenuItem"
                            binding="#{mainPage.orgDiscountsReportPage.mainMenuComponent}" label="Отчет по организации"
                            action="#{mainPage.showOrgDiscountsReportPage}" reRender="workspaceForm" />

        <%--@elvariable id="statisticsPaymentPreferentialSupplyReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.StatisticsPaymentPreferentialSupplyReportPage"--%>
        <rich:panelMenuItem id="statisticsPaymentPreferentialSupplyReportMenuItem"
                            binding="#{statisticsPaymentPreferentialSupplyReportPage.mainMenuComponent}"
                            label="Статистика оплаты льготного питания"
                            action="#{statisticsPaymentPreferentialSupplyReportPage.show}"
                            reRender="workspaceForm" rendered="false"/>

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="goodRequestsGroupMenu" binding="#{mainPage.goodRequestsGroupMenu.mainMenuComponent}"
                         label="Отчеты по заявкам и заказам" rendered="#{mainPage.eligibleToViewRequestReports}">
        <a4j:support event="onclick" action="#{mainPage.showGoodRequestsGroupMenu}" reRender="workspaceForm" />

        <rich:panelMenuItem id="goodRequestNewReportMenuItem" binding="#{mainPage.goodRequestsNewReportPage.mainMenuComponent}"
                            label="Сводный отчет по заявкам" action="#{mainPage.showGoodRequestNewReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="detailedGoodRequestReportMenuItem"
                            binding="#{mainPage.detailedGoodRequestReportPage.mainMenuComponent}"
                            label="Детальный отчет по заявкам" action="#{mainPage.showAggregateGoodRequestReportPage}"
                            reRender="workspaceForm" />

        <%--@elvariable id="preordersReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreordersReportPage"--%>
        <rich:panelMenuItem id="preordersMenuItem" binding="#{preordersReportPage.mainMenuComponent}"
                            label="Отчет по предзаказам" action="#{preordersReportPage.show}"
                            reRender="workspaceForm" />
    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="budgetReportGroupMenu" binding="#{mainPage.budgetFoodGroupMenu.mainMenuComponent}"
                         label="Льготное питание" rendered="#{mainPage.eligibleToViewMealsReports}">
        <a4j:support event="onclick" action="#{mainPage.showBudgetFoodGroupMenu}" reRender="workspaceForm" />
        <rich:panelMenuGroup id="registerStampGroupMenu" binding="#{mainPage.registerStampGroupMenu.mainMenuComponent}"
                             label="Реестр талонов (предварительный)" rendered="#{mainPage.eligibleToViewMealsReports}">

            <%--@elvariable id="registerStampNewPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampNewPage"--%>
            <rich:panelMenuItem id="registerStampNewReportMenuItem" binding="#{registerStampNewPage.mainMenuComponent}"
                                label="Реестр талонов (новая форма)" action="#{registerStampNewPage.show}"
                                reRender="workspaceForm" />

            <%--@elvariable id="registerStampPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampPage"--%>
            <rich:panelMenuItem id="registerStampReportMenuItem" binding="#{registerStampPage.mainMenuComponent}"
                                label="Реестр талонов" action="#{registerStampPage.show}" reRender="workspaceForm" />
        </rich:panelMenuGroup>
        <rich:panelMenuGroup id="registerStampElectronicCollationGroupMenu"
                             binding="#{mainPage.registerStampElectronicCollationGroupMenu.mainMenuComponent}"
                             label="Реестр талонов (электронная сверка)"
                             rendered="#{mainPage.eligibleToViewMealsReports}">
            <%--@elvariable id="registerStampNewElectronicCollationPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampNewElectronicCollationPage"--%>
            <rich:panelMenuItem id="registerStampNewElectronicCollationReportMenuItem"
                                binding="#{registerStampNewElectronicCollationPage.mainMenuComponent}"
                                label="Реестр талонов (новая форма)"
                                action="#{registerStampNewElectronicCollationPage.show}" reRender="workspaceForm" />
            <%--@elvariable id="registerStampElectronicCollationPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampElectronicCollationPage"--%>
            <rich:panelMenuItem id="registerStampElectronicCollationReportMenuItem"
                                binding="#{registerStampElectronicCollationPage.mainMenuComponent}"
                                label="Реестр талонов" action="#{registerStampElectronicCollationPage.show}"
                                reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <%--@elvariable id="feedingAndVisitSPage" type="ru.axetta.ecafe.processor.web.ui.report.online.FeedingAndVisitSPage"--%>
        <rich:panelMenuItem id="feedingAndVisitSReportMenuItem" binding="#{feedingAndVisitSPage.mainMenuComponent}"
                            label="Отчет по питанию и посещению" action="#{feedingAndVisitSPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="deliveredServicesReportMenuItem"
                            binding="#{mainPage.deliveredServicesReportPage.mainMenuComponent}"
                            label="Сводный отчет по услугам (предварительный)"
                            action="#{mainPage.showDeliveredServicesReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="deliveredServicesElectronicCollationReportMenuItem"
                            binding="#{mainPage.deliveredServicesElectronicCollationReportPage.mainMenuComponent}"
                            label="Сводный отчет по услугам (электронная сверка)"
                            action="#{mainPage.showDeliveredServicesElectronicCollationReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="referReportMenuItem" binding="#{referReportPage.mainMenuComponent}"
                            label="Справки расходования средств" action="#{referReportPage.show}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="taloonApprovalVerificationMenuItem" rendered="#{mainPage.isSupplier() || mainPage.eligibleToWorkOnlineReport}"
                            binding="#{mainPage.taloonApprovalVerificationPage.mainMenuComponent}"
                            label="Электронная сверка" action="#{mainPage.showTaloonApprovalVerificationPage}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="electronicReconciliationStatisticsMenuItem" rendered="#{mainPage.isSupplier() || mainPage.eligibleToWorkOnlineReport}"
                            binding="#{mainPage.electronicReconciliationStatisticsPage.mainMenuComponent}"
                            label="Статистика электронной сверки"
                            action="#{mainPage.showElectronicReconciliationStatisticsPage}" reRender="workspaceForm" />


        <%--@elvariable id="totalBenefFeedReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TotalBenefFeedReportPage"--%>
        <%--<rich:panelMenuItem id="totalBenefFeedReportMenuItem" binding="#{totalBenefFeedReportPage.mainMenuComponent}"
                            label="Сводный отчет по льготному питанию" action="#{totalBenefFeedReportPage.show}" reRender="workspaceForm" />--%>
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="paidFoodGroupMenu" binding="#{mainPage.paidFoodGroupMenu.mainMenuComponent}"
                         label="Платное питание" reRender="#{mainPage.eligibleToViewMealsReports}" rendered="#{mainPage.eligibleToViewPaidFoodReport}">
        <a4j:support event="onclick" action="#{mainPage.showPaidFoodGroupMenu}" reRender="workspaceForm" />
        <%--@elvariable id="registerStampPaidPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampPaidPage"--%>
        <rich:panelMenuItem id="registerStampPaidReportMenuItem" binding="#{registerStampPaidPage.mainMenuComponent}"
                            label="Реестр талонов" action="#{registerStampPaidPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="taloonPreorderVerificationMenuItem" rendered="#{mainPage.isSupplier() || mainPage.eligibleToWorkOnlineReport}"
                            binding="#{mainPage.taloonPreorderVerificationPage.mainMenuComponent}"
                            label="Электронная сверка" action="#{mainPage.showTaloonPreorderVerificationPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuGroup id="feedingSettingsGroupMenu" label="Настройки платного питания" rendered="#{mainPage.eligibleToViewOrEditFeedingSettings}">
            <rich:panelMenuItem id="feedingSettingsListMenuItem" binding="#{mainPage.feedingSettingsListPage.mainMenuComponent}"
                            label="Список" action="#{mainPage.showFeedingSettingListPage}" reRender="workspaceForm" />

            <rich:panelMenuGroup id="selectedFeedingSettingGroupMenu" label="#{mainPage.feedingSettingsGroupPage.title}" rendered="false"
                                 binding="#{mainPage.feedingSettingsGroupPage.mainMenuComponent}">
                <a4j:support event="onclick" action="#{mainPage.showFeedingSettingGroupPage}"
                             reRender="workspaceForm" />
                <rich:panelMenuItem id="viewFeedingSettingMenuItem" binding="#{mainPage.feedingSettingViewPage.mainMenuComponent}"
                                    label="Просмотр" action="#{mainPage.showFeedingSettingViewPage}" reRender="mainMenu, workspaceForm" />
                <rich:panelMenuItem id="editFeedingSettingMenuItem" binding="#{mainPage.feedingSettingEditPage.mainMenuComponent}"
                                    label="Редактирование" action="#{mainPage.showFeedingSettingEditPage}" reRender="mainMenu, workspaceForm" />
            </rich:panelMenuGroup>

            <rich:panelMenuItem id="showFeedingSettingCreateMenuItem" binding="#{mainPage.feedingSettingCreatePage.mainMenuComponent}"
                                label="Регистрация" action="#{mainPage.showFeedingSettingCreatePage}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="subscriptionFeedingGroupMenu" binding="#{mainPage.subscriptionFeedingGroupMenu.mainMenuComponent}"
                         label="Абонементное питание" reRender="#{mainPage.eligibleToViewMealsReports}" rendered="#{mainPage.eligibleToViewSubscriptionFeeding}">
        <a4j:support event="onclick" action="#{mainPage.showSubscriptionFeedingGroupMenu}" reRender="workspaceForm" />
        <%--@elvariable id="registerStampSubscriptionFeedingPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampSubscriptionFeedingPage"--%>
        <rich:panelMenuItem id="registerStampSubscriptionFeedingReportMenuItem" binding="#{registerStampSubscriptionFeedingPage.mainMenuComponent}"
                            label="Реестр талонов" action="#{registerStampSubscriptionFeedingPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="acceptanceActGroupMenu" binding="#{mainPage.acceptanceActGroupMenu.mainMenuComponent}"
                         label="Документы" >
        <a4j:support event="onclick" action="#{mainPage.showAcceptanceGroupMenu}" reRender="workspaceForm" />
        <%--@elvariable id="acceptanceOfCompletedWorksActPage" type="ru.axetta.ecafe.processor.web.ui.report.online.AcceptanceOfCompletedWorksActPage"--%>
        <rich:panelMenuItem id="acceptanceOfCompletedWorksActMenuItem"
                            binding="#{acceptanceOfCompletedWorksActPage.mainMenuComponent}"
                            label="Акт сдачи-приёмки оказанных услуг"
                            action="#{acceptanceOfCompletedWorksActPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="paymentReportsGroupMenu" binding="#{mainPage.paymentReportsGroupMenu.mainMenuComponent}"
                         label="Отчеты по пополнениям" rendered="#{mainPage.eligibleToViewRefillReports}">
        <a4j:support event="onclick" action="#{mainPage.showPaymentReportsGroupMenu}" reRender="workspaceForm" />
        <%--@elvariable id="contragentCompletionReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ContragentCompletionReportPage"--%>
        <rich:panelMenuItem id="contragentCompletionReportMenuItem" binding="#{contragentCompletionReportPage.mainMenuComponent}"
                            label="Отчет по обороту" action="#{contragentCompletionReportPage.show}"
                            reRender="workspaceForm" />
        <%--<rich:panelMenuItem id="dailySalesByGroupsReportMenuItem" binding="#{dailySalesByGroupsReportPage.mainMenuComponent}"
                            label="Дневные продажи по категориям" action="#{dailySalesByGroupsReportPage.show}"
                            reRender="workspaceForm" />--%>
        <rich:panelMenuItem id="contragentPaymentReportMenuItem" binding="#{mainPage.contragentPaymentReportPage.mainMenuComponent}"
                            label="Отчет по платежам" action="#{mainPage.showContragentPaymentsReportPage}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="clientPaymentsReportMenuItem" binding="#{mainPage.clientPaymentsReportPage.mainMenuComponent}"
                            label="Отчет по начислениям" action="#{mainPage.showClientPaymentsReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="currentPositionReportMenuItem"
                            binding="#{mainPage.currentPositionsReportPage.mainMenuComponent}"
                            label="Просмотр текущих позиций" action="#{mainPage.showCurrentPositionsReportPage}"
                            reRender="workspaceForm" />
        <%--@elvariable id="payStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PayStatsPage"--%>
        <rich:panelMenuItem id="payStatsMenuItem" binding="#{payStatsPage.mainMenuComponent}" label="Отчет по агентам"
                            action="#{payStatsPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="regularPaymentsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegularPaymentsReportPage"--%>
        <rich:panelMenuItem id="regularPaymentsReportMenuItem" binding="#{regularPaymentsReportPage.mainMenuComponent}"
                            label="Отчет по регулярным платежам" action="#{regularPaymentsReportPage.show}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="paymentTotalsReportMenuItem"
                            binding="#{mainPage.paymentTotalsReportPage.mainMenuComponent}"
                            label="Итоговые показатели"
                            action="#{mainPage.paymentTotalsReportPage.show}"
                            reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="activityReportsGroupMenu" binding="#{mainPage.activityReportsGroupMenu.mainMenuComponent}"
                         label="Отчеты по активности" rendered="#{mainPage.eligibleToViewActivityReports}">
        <a4j:support event="onclick" action="#{mainPage.showActivityReportsGroupMenu}" reRender="workspaceForm" />
        <%--@elvariable id="activeClientsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ActiveClientsReportPage"--%>
        <rich:panelMenuItem id="activeClientsMenuItem" binding="#{activeClientsReportPage.mainMenuComponent}"
                            label="Отчет по активным клиентам" action="#{activeClientsReportPage.show}"
                            reRender="workspaceForm" />
        <%--@elvariable id="commonStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CommonStatsPage"--%>
        <rich:panelMenuItem id="commonStatsMenuItem" binding="#{commonStatsPage.mainMenuComponent}"
                            label="Общая статистика" action="#{commonStatsPage.show}" reRender="workspaceForm" />
        <%--@elvariable id="activeDiscountClientsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ActiveDiscountClientsReportPage"--%>
        <rich:panelMenuItem id="activeDiscountClientsMenuItem"
                            binding="#{activeDiscountClientsReportPage.mainMenuComponent}"
                            label="Отчет по питающимся льготникам" action="#{activeDiscountClientsReportPage.show}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="zeroTransactionsReportMenuItem" binding="#{mainPage.zeroTransactionsReportPage.mainMenuComponent}"
                            label="Учет причин снижения объемов оказания услуг" action="#{mainPage.showZeroTransactionsReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="specialDatesReportMenuItem" binding="#{mainPage.specialDatesReportPage.mainMenuComponent}"
                            label="Отчет по учебным дням" action="#{mainPage.showSpecialDatesReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="migrantsReportMenuItem" binding="#{mainPage.migrantsReportPage.mainMenuComponent}"
                            label="#{mainPage.migrantsReportPage.reportNameForMenu}" action="#{mainPage.showMigrantsReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="monitoringOfMenuItem" binding="#{mainPage.monitoringOfReportPage.mainMenuComponent}"
                            label="Мониторинг" action="#{mainPage.showMonitoringOfReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="clientTransactionsReportMenuItem" binding="#{mainPage.clientTransactionsReportPage.mainMenuComponent}"
                            label="Транзакции клиента" action="#{mainPage.showClientTransactionsReportPage}" reRender="workspaceForm" />
        <a4j:support event="onclick" action="#{mainPage.showBudgetFoodGroupMenu}" reRender="workspaceForm" />
        <%--rendered="#{mainPage.eligibleToViewCoverageNutritionReport}"--%>
        <rich:panelMenuGroup id="coverageNutritionReportGroupMenu" label="Отчет по охвату питания">
            <%--@elvariable id="coverageNutritionReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CoverageNutritionReportPage"--%>
            <rich:panelMenuItem id="coverageNutritionReport" binding="#{coverageNutritionReportPage.mainMenuComponent}"
                                label="Отчет по охвату питания" action="#{coverageNutritionReportPage.show}" reRender="workspaceForm" />

            <rich:panelMenuGroup id="kznClientsStatisticMenuGroup" label="Статистика по клиентам" reRender="workspaceForm">

                <%--@elvariable id="kznClientsStatisticViewPage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticViewPage"--%>
                <rich:panelMenuItem id="kznClientsStatisticViewMenuItem" binding="#{kznClientsStatisticViewPage.mainMenuComponent}" label="Просмотр"
                                    action="#{kznClientsStatisticViewPage.show}" reRender="workspaceForm" />
                <%--@elvariable id="kznClientsStatisticCreatePage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticCreatePage"--%>
                <rich:panelMenuItem id="kznClientsStatisticCreateMenuItem" binding="#{kznClientsStatisticCreatePage.mainMenuComponent}" label="Создание"
                                    action="#{kznClientsStatisticCreatePage.show}" reRender="workspaceForm" />
                <%--@elvariable id="kznClientsStatisticEditPage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticEditPage"--%>
                <rich:panelMenuItem id="kznClientsStatisticEditMenuItem" binding="#{kznClientsStatisticEditPage.mainMenuComponent}" label="Редактирование"
                                    action="#{kznClientsStatisticEditPage.show}" reRender="workspaceForm" />
            </rich:panelMenuGroup>
        </rich:panelMenuGroup>
    </rich:panelMenuGroup>

    <%--@elvariable id="clientBalanceHoldPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ClientBalanceHoldPage"--%>
    <rich:panelMenuGroup id="clientsReportsGroupMenu" binding="#{mainPage.clientReportsGroupMenu.mainMenuComponent}"
                         label="Отчеты по балансам" rendered="#{mainPage.eligibleToViewClientReports}">
        <a4j:support event="onclick" action="#{mainPage.showClientReportsGroupMenu}" reRender="workspaceForm" />
        <rich:panelMenuItem id="сlientBalanceByDayReportMenuItem" binding="#{mainPage.clientBalanceByDayReportPage.mainMenuComponent}"
                            label="Баланс клиентов на дату" action="#{mainPage.showClientBalanceByDayReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="сlientBalanceByOrgReportMenuItem" binding="#{mainPage.clientBalanceByOrgReportPage.mainMenuComponent}"
                            label="Остаток денежных средств по организациям на дату" action="#{mainPage.showClientBalanceByOrgReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="balanceLeavingReportMenuItem" binding="#{mainPage.balanceLeavingReportPage.mainMenuComponent}"
                            label="Отчет ухода баланса в минус/большой плюс"
                            action="#{mainPage.showBalanceLeavingReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="clientReportMenuItem" binding="#{mainPage.clientReportPage.mainMenuComponent}"
                            label="Статистика по балансам клиентов" action="#{mainPage.showClientReportPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="clientBalanceHoldMenuItem" binding="#{clientBalanceHoldPage.mainMenuComponent}"
                            label="Блокировка баланса и вывод средств" action="#{clientBalanceHoldPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="informReportsGroupMenu" binding="#{mainPage.informReportsGroupMenu.mainMenuComponent}"
                         label="Отчеты по информированию" rendered="#{mainPage.eligibleToViewInformReports}">
        <a4j:support event="onclick" action="#{mainPage.showInformReportsGroupMenu}" reRender="workspaceForm" />
        <rich:panelMenuItem id="SentSmsReportPageMenuItem"
                            binding="#{sentSmsReportPage.mainMenuComponent}"
                            label="Статистика отправки сообщений информирования по дням"
                            action="#{sentSmsReportPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="OrgSmsDeliveryReportPageMenuItem"
                            binding="#{smsDeliveryReportPage.mainMenuComponent}"
                            label="Отчет по быстрой синхронизации и времени доставки сообщений информирования"
                            action="#{smsDeliveryReportPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="OrgSmsStatsReportPageMenuItem"
                            binding="#{orgSmsStatsReportPage.mainMenuComponent}"
                            label="Общая статистика"
                            action="#{orgSmsStatsReportPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="SmsAddressesReportPageMenuItem"
                            binding="#{smsAddressesReportPage.mainMenuComponent}"
                            label="Отчет по доставке сообщений адресатам"
                            action="#{smsAddressesReportPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <%--@elvariable id="statisticDifferencesGroupPage" type="ru.axetta.ecafe.processor.web.ui.report.online.StatisticDifferencesGroupPage"--%>
    <rich:panelMenuGroup id="statisticDifferencesGroupMenu" binding="#{statisticDifferencesGroupPage.mainMenuComponent}"
                     label="Статистика по расхождениям данных" rendered="#{mainPage.eligibleToViewStatisticDifferences}">
        <a4j:support event="onclick" action="#{statisticDifferencesGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="statisticsDiscrepanciesOnOrdersAndAttendanceReportMenuItem"
                            binding="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.mainMenuComponent}"
                            label="Статистика расхождения данных по заказам и оплате"
                            action="#{mainPage.showDiscrepanciesOnOrdersAndAttendanceReportPage}"
                            reRender="workspaceForm"/>

        <rich:panelMenuItem id="requestsAndOrdersReportMenuItem" binding="#{mainPage.requestsAndOrdersReportPage.mainMenuComponent}"
                            label="Расхождение данных по заявкам и заказам" action="#{mainPage.showRequestsAndOrdersReportPage}"
                            reRender="workspaceForm" />

        <%--todo выпилить функционал - реализован некорректно, не работает, не используется--%>
        <%--отключен в связи с реализацией ECAFE-2169--%>
        <%--&lt;%&ndash;@elvariable id="discrepanciesDataOnOrdersAndPaymentReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.DiscrepanciesDataOnOrdersAndPaymentReportPage"&ndash;%&gt;--%>
        <%--<rich:panelMenuItem id="discrepanciesDataOnOrdersAndPaymentReportMenuItem"--%>
                            <%--binding="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.mainMenuComponent}"--%>
                            <%--label="Статистика по актам расхождений"--%>
                            <%--action="#{mainPage.showDiscrepanciesDataOnOrdersAndPaymentReportPage}"--%>
                            <%--reRender="workspaceForm" />--%>

        <%--@elvariable id="detailedDeviationsPaymentOrReducedPriceMealsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.DetailedDeviationsPaymentOrReducedPriceMealsReportPage"--%>
        <%--<rich:panelMenuItem id="detailedDeviationsPaymentOrReducedPriceMealsReportMenuItem"--%>
                            <%--binding="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.mainMenuComponent}"--%>
                            <%--label="Детализированный отчет отклонений оплаты льготного питания"--%>
                            <%--action="#{mainPage.showDetailedDeviationsPaymentOrReducedPriceMealsReportPage}"--%>
                            <%--reRender="workspaceForm" />--%>
        <%--@elvariable id="detailedDeviationsWithoutCorpsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.DetailedDeviationsWithoutCorpsReportPage"--%>
        <%--<rich:panelMenuItem id="detailedDeviationsWithoutCorpsReportMenuItem"
                            binding="#{mainPage.detailedDeviationsWithoutCorpsReportPage.mainMenuComponent}"
                            label="Детализированный отчет отклонений оплаты льготного питания"
                            action="#{mainPage.showDetailedDeviationsWithoutCorpsReportPage}"
                            reRender="workspaceForm" />--%>
        <rich:panelMenuItem id="detailedDeviationsWithoutCorpsNewReportMenuItem"
                            binding="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.mainMenuComponent}"
                            label="Детализированный отчет отклонений оплаты льготного питания"
                            action="#{mainPage.showDetailedDeviationsWithoutCorpsNewReportPage}"
                            reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <%--@elvariable id="financialControlPage" type="ru.axetta.ecafe.processor.web.ui.report.online.FinancialControlPage"--%>
    <rich:panelMenuGroup id="financialControlMenu" binding="#{financialControlPage.mainMenuComponent}"
                         label="Отчеты для службы финансового контроля"
                         rendered="#{mainPage.eligibleToViewFinancialControl}">
        <a4j:support event="onclick" action="#{financialControlPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="latePaymentReportMenuItem"
                            binding="#{mainPage.latePaymentReportPage.mainMenuComponent}"
                            label="Сводный отчет по несвоевременной оплате питания"
                            action="#{mainPage.showLatePaymentReportPage}"
                            reRender="workspaceForm"/>

        <rich:panelMenuItem id="latePaymentDetailedReportMenuItem"
                            binding="#{mainPage.latePaymentDetailedReportPage.mainMenuComponent}"
                            label="Детализированный отчет по несвоевременной оплате питания"
                            action="#{mainPage.showLatePaymentDetailedReportPage}"
                            reRender="workspaceForm"/>

        <%--<rich:panelMenuItem id="adjustmentPaymentReportMenuItem"
                            binding="#{mainPage.adjustmentPaymentReportPage.mainMenuComponent}"
                            label="Отчет по ручной корректировке оплаты льготного питания"
                            action="#{mainPage.showAdjustmentPaymentReportPage}"
                            reRender="workspaceForm"/>--%>

    </rich:panelMenuGroup>
    <%--@elvariable id="salesReportGroupPage" type="ru.axetta.ecafe.processor.web.ui.report.online.SalesReportGroupPage"--%>

    <rich:panelMenuGroup id="salesReportGroupMenu" binding="#{salesReportGroupPage.mainMenuComponent}"
                         label="Отчеты по продажам"
                         rendered="#{mainPage.eligibleToViewSalesReport}">
        <a4j:support event="onclick" action="#{salesReportGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="dailySalesByGroupsReportMenuItem" binding="#{dailySalesByGroupsReportPage.mainMenuComponent}"
                            label="Дневные продажи по категориям" action="#{dailySalesByGroupsReportPage.show}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="salesReportMenuItem" binding="#{mainPage.salesReportPage.mainMenuComponent}"
                            label="Отчет по продажам" action="#{mainPage.showSalesReportPage}" reRender="workspaceForm" />

        <%--@elvariable id="aggregateCostsAndSalesReportPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.AggregateCostsAndSalesReportPage"--%>
        <rich:panelMenuItem id="aggregateCostsAndSalesReportMenuItem"
                            binding="#{aggregateCostsAndSalesReportPage.mainMenuComponent}"
                            label="Отчет по показателям цен и продаж" action="#{aggregateCostsAndSalesReportPage.show}"
                            reRender="workspaceForm" />
        <%--@elvariable id="totalSalesPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TotalSalesPage"--%>
        <rich:panelMenuItem id="totalSalesReportMenuItem" binding="#{mainPage.totalSalesPage.mainMenuComponent}"
                            label="Сводный отчет по продажам" action="#{mainPage.showTotalSalesPage}" reRender="workspaceForm" />

        <%--@elvariable id="ordersByManufacturerReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.OrdersByManufacturerReportPage"--%>
        <rich:panelMenuItem id="ordersByManufacturerReportMenuItem" binding="#{mainPage.ordersByManufacturerReportPage.mainMenuComponent}"
                            label="Сводный отчет по производителю" action="#{mainPage.showOrdersByManufacturerReportPage}" reRender="workspaceForm" />

        <%--@elvariable id="orgOrderReportPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgOrderReportPage"--%>
        <rich:panelMenuItem id="generateOrgOrderReportMenuItem"
                            binding="#{mainPage.orgOrderReportPage.mainMenuComponent}" label="Отчет по покупкам"
                            action="#{mainPage.showOrgOrderReportPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="basicBasketReportMenuItem"
                            binding="#{basicBasketReportPage.mainMenuComponent}" label="Отчет по базовой корзине"
                            action="#{basicBasketReportPage.show}" reRender="workspaceForm"/>

        <rich:panelMenuItem id="paymentsBetweenContragentsReportMenuItem"
                            binding="#{paymentsBetweenContragentsReportPage.mainMenuComponent}" label="Отчет по объемам оплат между поставщиками питания"
                            action="#{paymentsBetweenContragentsReportPage.show}" reRender="workspaceForm"/>

        <%--@elvariable id="consolidatedSellingReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ConsolidatedSellingReportPage"--%>
        <rich:panelMenuItem id="consolidatedSellingReportMenuItem"
                            binding="#{consolidatedSellingReportPage.mainMenuComponent}" label="Сводный отчет по реализации"
                            action="#{consolidatedSellingReportPage.show}" reRender="workspaceForm"/>

        <rich:panelMenuItem id="contragentPreordersReportMenuItem"
                            binding="#{mainPage.contragentPreordersReportPage.mainMenuComponent}" label="Отчет по предварительным заказам"
                            action="#{mainPage.contragentPreordersReportPage.show}" reRender="workspaceForm"/>


    </rich:panelMenuGroup>

    <%--@elvariable id="cardGroupPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CardGroupPage"--%>
    <rich:panelMenuGroup id="cardGroupPageMenu" binding="#{cardGroupPage.mainMenuComponent}" label="Отчеты по картам"
                         rendered="#{mainPage.eligibleToViewCardReports}">
        <a4j:support event="onclick" action="#{cardGroupPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="typesOfCardReportMenuItem" binding="#{mainPage.typesOfCardReportPage.mainMenuComponent}"
                            label="Отчет по типам карт" action="#{mainPage.typesOfCardReportPage.show}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="interactiveCardDataMenuItem"
                            binding="#{interactiveCardDataReportPage.mainMenuComponent}"
                            label="Отчет по обороту электронных карт" action="#{interactiveCardDataReportPage.show}"
                            reRender="workspaceForm" />
        <rich:panelMenuItem id="issuedCardsReportMenuItem"
                            binding="#{mainPage.issuedCardsReportPage.mainMenuComponent}"
                            label="Отчет о выданных и перевыпущенных картах" action="#{mainPage.issuedCardsReportPage.show}"
                            reRender="workspaceForm" />
        <%--@elvariable id="cardApplicationReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CardApplicationReportPage"--%>
        <rich:panelMenuItem id="cardApplicationReport" binding="#{cardApplicationReportPage.mainMenuComponent}"
                            label="Отчет по заявлениям на карту" action="#{cardApplicationReportPage.show}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <%--@elvariable id="passReportsGroupPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PassReportsGroupPage"--%>
    <rich:panelMenuGroup id="passReportsGroupPageMenu" binding="#{passReportsGroupPage.mainMenuComponent}"
                         label="Отчеты по проходам">
        <a4j:support event="onclick" action="#{passReportsGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="detailedEnterEventReportMenuItem" binding="#{mainPage.detailedEnterEventReportPage.mainMenuComponent}"
                            label="Детализированный отчет по посещению" action="#{mainPage.showDetailedEnterEventReportPage}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="autoEnterEventReportMenuItem"
                            binding="#{mainPage.autoEnterEventReportPage.mainMenuComponent}"
                            label="Сводный отчет по посещению" action="#{mainPage.autoEnterEventReportPage.show}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="enterEventJournalReportPageMenuItem"
                            binding="#{mainPage.enterEventJournalReportPage.mainMenuComponent}"
                            label="Журнал посещений" action="#{mainPage.enterEventJournalReportPage.show}"
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="enterEventReportMenuItem" binding="#{mainPage.enterEventReportPage.mainMenuComponent}"
                            label="Отчет по турникетам" action="#{mainPage.showEnterEventReportPage}"
                            reRender="workspaceForm" rendered="#{mainPage.eligibleToViewEnterEventReport}" />
    </rich:panelMenuGroup>

    <%--@elvariable id="helpdeskGroupPage" type="ru.axetta.ecafe.processor.web.ui.report.online.HelpdeskGroupPage"--%>
    <rich:panelMenuGroup id="helpdeskGroupPageMenu" binding="#{helpdeskGroupPage.mainMenuComponent}" label="Служба помощи"
                         rendered="#{mainPage.eligibleToViewHelpdesk}">
        <a4j:support event="onclick" action="#{helpdeskGroupPage.show}" reRender="workspaceForm" />

        <%--@elvariable id="helpdeskReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.HelpdeskReportPage"--%>
        <rich:panelMenuItem id="helpdeskMenuItem" binding="#{helpdeskReportPage.mainMenuComponent}"
                            label="Заявки в службу помощи" action="#{helpdeskReportPage.show}"
                            reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <%--@elvariable id="totalServicesReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TotalServicesReportPage"--%>
    <rich:panelMenuItem id="totalServicesReportPage" binding="#{totalServicesReportPage.mainMenuComponent}"
                        label="Свод по услугам" action="#{totalServicesReportPage.show}" reRender="workspaceForm" rendered="#{mainPage.eligibleToViewTotalServicesReport}" />
    <rich:panelMenuItem id="clientsBenefitsReportMenuItem"
                        binding="#{mainPage.clientsBenefitsReportPage.mainMenuComponent}"
                        label="Расчет комплексов по льготным правилам"
                        action="#{mainPage.showClientsBenefitsReportPage}" reRender="workspaceForm" rendered="#{mainPage.eligibleToViewClientsBenefitsReport}" />
    <%--@elvariable id="referReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ReferReportPage"--%>

    <rich:panelMenuItem id="transactionsReportMenuItem" binding="#{transactionsReportPage.mainMenuComponent}"
                        label="Отчет по транзакциям" action="#{transactionsReportPage.show}"
                        reRender="workspaceForm" rendered="#{mainPage.eligibleToViewTransactionsReport}" />

    <%--@elvariable id="manualReportRunnerPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ManualReportRunnerPage"--%>
    <rich:panelMenuItem id="manualReportRunnerMenuItem" binding="#{manualReportRunnerPage.mainMenuComponent}"
                        label="Ручной запуск отчетов" action="#{manualReportRunnerPage.show}" rendered="#{mainPage.eligibleToViewManualReport}"
                        reRender="workspaceForm" />

</rich:panelMenuGroup>

<%--@elvariable id="reportRepositoryListPage" type="ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryListPage"--%>
<rich:panelMenuGroup id="reportRepositoryGroupMenu" binding="#{reportRepositoryListPage.groupPage.mainMenuComponent}"
                     label="Репозиторий отчетов" rendered="#{mainPage.eligibleToShowReportsRepository}">
    <a4j:support event="onclick" action="#{reportRepositoryListPage.groupPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="reportRepositoryListMenuItem" binding="#{reportRepositoryListPage.mainMenuComponent}"
                        label="Просмотр" action="#{reportRepositoryListPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="optionGroupMenu" label="Настройки" binding="#{mainPage.optionGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToEditOptions}" expanded="#{mainPage.eligibleToViewUsers}">
<a4j:support event="onclick" action="#{mainPage.showOptionGroupPage}" reRender="workspaceForm" />

<rich:panelMenuGroup id="optionsGroupMenu" label="Настройки" binding="#{mainPage.optionsGroupPage.mainMenuComponent}"
                     rendered="#{!mainPage.eligibleToViewUsers}">
    <%--@elvariable id="optionPage" type="ru.axetta.ecafe.processor.web.ui.option.OptionPage"--%>
    <rich:panelMenuItem id="showOptionMenuItem" binding="#{optionPage.mainMenuComponent}" label="Настройки"
                        action="#{optionPage.show}" reRender="workspaceForm" />
    <%--@elvariable id="messageConfigurePage" type="ru.axetta.ecafe.processor.web.ui.option.MessageConfigurePage"--%>
    <rich:panelMenuItem id="showMessageConfigureMenuItem" binding="#{messageConfigurePage.mainMenuComponent}"
                        label="Шаблоны уведомлений" action="#{messageConfigurePage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showConfigurationMenuItem" binding="#{mainPage.configurationPage.mainMenuComponent}"
                        label="Конфигурация" action="#{mainPage.showConfigurationPage}" reRender="workspaceForm" />
    <%--@elvariable id="licInfoPage" type="ru.axetta.ecafe.processor.web.ui.option.LicInfoPage"--%>
    <rich:panelMenuItem id="showLicInfoMenuItem" binding="#{licInfoPage.mainMenuComponent}" label="Лицензии"
                        action="#{licInfoPage.show}" reRender="workspaceForm" />
    <%--@elvariable id="istkKioskOptionsPage" type="ru.axetta.ecafe.processor.web.ui.option.IstkKioskOptionsPage"--%>
    <rich:panelMenuItem id="showIstkKioskOptionsMenuItem" binding="#{istkKioskOptionsPage.mainMenuComponent}"
                        label="Инфокиоски" action="#{istkKioskOptionsPage.show}" reRender="workspaceForm"/>
    <%--@elvariable id="externalSystemsPage" type="ru.axetta.ecafe.processor.web.ui.option.ExternalSystemsPage"--%>
    <rich:panelMenuItem id="showexternalSystemsItem" binding="#{externalSystemsPage.mainMenuComponent}"
                        label="Внешние системы" action="#{externalSystemsPage.show}" reRender="workspaceForm"/>

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

<rich:panelMenuGroup id="journalGroupMenu" label="Журналы" binding="#{mainPage.journalGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewUsers}" expanded="true">
    <a4j:support event="onclick" action="#{mainPage.showJournalGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showJournalBalancesMenuItem"
                        binding="#{journalBalancesReportPage.mainMenuComponent}"
                        label="Журнал изменения балансов л/с"
                        action="#{journalBalancesReportPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showJournalAuthenticationMenuItem"
                        binding="#{journalAuthenticationReportPage.mainMenuComponent}"
                        label="Журнал активности пользователей"
                        action="#{journalAuthenticationReportPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showJournalProcessesMenuItem"
                        binding="#{journalProcessesReportPage.mainMenuComponent}"
                        label="Журнал запуска фоновых процессов"
                        action="#{journalProcessesReportPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showJournalReportsMenuItem"
                        binding="#{journalReportsReportPage.mainMenuComponent}"
                        label="Журнал сохранения файлов отчетов"
                        action="#{journalReportsReportPage.show}" reRender="workspaceForm" />
</rich:panelMenuGroup>

<rich:panelMenuGroup id="userGroupMenu" label="Пользователи" binding="#{mainPage.userGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewUsers}" expanded="true">
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

    <rich:panelMenuGroup id="userGroupGroupMenu" label="Роли пользователей" binding="#{mainPage.userGroupGroupPage.mainMenuComponent}"
                         rendered="#{mainPage.eligibleToViewUsers}" expanded="false">
        <a4j:support event="onclick" action="#{mainPage.showUserGroupPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="showUserGroupListMenuItem" binding="#{mainPage.userGroupListPage.mainMenuComponent}" label="Список"
                            action="#{mainPage.showUserGroupListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedUserGroupGroupMenu" label="#{mainPage.selectedUserGroupGroupPage.userName}"
                             binding="#{mainPage.selectedUserGroupGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedUserGroupGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewUserGroupMenuItem" binding="#{mainPage.userGroupViewPage.mainMenuComponent}" label="Просмотр"
                                action="#{mainPage.showUserGroupViewPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editUserGroupMenuItem" binding="#{mainPage.userGroupEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showUserGroupEditPage}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createUserGroupMenuItem" binding="#{mainPage.userGroupCreatePage.mainMenuComponent}" label="Создание"
                            action="#{mainPage.showUserGroupCreatePage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>


    <rich:panelMenuGroup id="thinClientUsersGroupMenu" label="Тонкий клиент" binding="#{mainPage.thinClientUserGroupPage.mainMenuComponent}"
                         rendered="#{!mainPage.eligibleToViewUsers}">
        <a4j:support event="onclick" action="#{mainPage.showThinClientUsersGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="thinClientUserListPageMenuItem" binding="#{thinClientUserListPage.mainMenuComponent}" label="Список"
                            action="#{thinClientUserListPage.show}" reRender="workspaceForm" />
        <rich:panelMenuItem id="thinClientUserViewPageMenuItem" binding="#{thinClientUserViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{thinClientUserViewPage.show}" reRender="workspaceForm" />
        <%--<rich:panelMenuItem id="thinClientUserEditPageMenuItem" binding="#{thinClientUserEditPage.mainMenuComponent}" label="Изменение"
                            action="#{thinClientUserEditPage.show}" reRender="workspaceForm" >
            <f:setPropertyActionListener value="1" target="#{thinClientUserEditPage.callFromMenu}" />
        </rich:panelMenuItem>--%>
        <rich:panelMenuItem id="thinClientUserCreatePageMenuItem" binding="#{thinClientUserEditPage.mainMenuComponent}" label="Создание"
                            action="#{thinClientUserEditPage.show}" reRender="workspaceForm" >
            <f:setPropertyActionListener value="-1" target="#{thinClientUserEditPage.idOfClient}" />
            <f:setPropertyActionListener value="1" target="#{thinClientUserEditPage.callFromMenu}" />
        </rich:panelMenuItem>
    </rich:panelMenuGroup>

</rich:panelMenuGroup>

<rich:panelMenuGroup id="serviceNewGroupMenu" label="Сервис" binding="#{mainPage.serviceNewGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewUsers}" expanded="true">

    <rich:panelMenuItem id="serviceChecksums" binding="#{mainPage.serviceCheckSumsPage.mainMenuComponent}"
                        label="Контрольные суммы" action="#{mainPage.serviceCheckSumsPage.show}"
                        reRender="workspaceForm" rendered="#{mainPage.eligibleToViewUsers}" />

    <rich:panelMenuItem id="showOptionsSecurityMenuItem" binding="#{mainPage.optionsSecurityPage.mainMenuComponent}"
                        label="Настройки веб" action="#{mainPage.showOptionsSecurityPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToViewUsers}" />

    <rich:panelMenuItem id="showOptionsSecurityClientMenuItem" binding="#{mainPage.optionsSecurityClientPage.mainMenuComponent}"
                        label="Настройки ОО" action="#{mainPage.showOptionsSecurityClientPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToViewUsers}" />

    <rich:panelMenuItem id="showOrgsSecurityMenuItem" binding="#{mainPage.orgsSecurityPage.mainMenuComponent}"
                        label="Организации" action="#{mainPage.showOrgsSecurityPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToViewUsers}" />

</rich:panelMenuGroup>


<%--@elvariable id="employeesGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeesGroupPage"--%>
 <rich:panelMenuGroup id="employeesGroupMenu" label="Инженеры" binding="#{employeesGroupPage.mainMenuComponent}"
                     rendered="#{!mainPage.eligibleToViewUsers}">

    <%--@elvariable id="employeeListPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeListPage"--%>
    <%--@elvariable id="employeeGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeGroupPage"--%>
    <%--@elvariable id="employeeViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeViewPage"--%>
    <%--@elvariable id="employeeEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeEditPage"--%>
    <%--@elvariable id="employeeCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCreatePage"--%>
    <%--@elvariable id="employeeHistoryReportPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeHistoryReportPage"--%>

    <rich:panelMenuItem id="showEmployeeListMenuItem" binding="#{employeeListPage.mainMenuComponent}" label="Список"
                        action="#{employeeListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedEmployeeGroupMenu" label="#{employeeGroupPage.currentEmployee.shortFullName}"
                         binding="#{employeeGroupPage.mainMenuComponent}" rendered="false">
        <a4j:support event="onclick" action="#{employeeGroupPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="viewEmployeeMenuItem" binding="#{employeeViewPage.mainMenuComponent}" label="Просмотр"
                            action="#{employeeViewPage.show}" reRender="workspaceForm" />

        <rich:panelMenuItem id="editEmployeeMenuItem" binding="#{employeeEditPage.mainMenuComponent}"
                            label="Редактировать" action="#{employeeEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createEmployeeListMenuItem" binding="#{employeeCreatePage.mainMenuComponent}"
                        label="Создание" action="#{employeeCreatePage.show}" reRender="workspaceForm" />

    <%--@elvariable id="employeesCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeesCardGroupPage"--%>
    <rich:panelMenuGroup id="employeesCardGroupMenu" label="Карты"
                         binding="#{employeesCardGroupPage.mainMenuComponent}">

        <%--@elvariable id="employeeCardListPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardListPage"--%>
        <%--@elvariable id="employeeCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardGroupPage"--%>
        <%--@elvariable id="employeeCardViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardViewPage"--%>
        <%--@elvariable id="employeeCardEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardEditPage"--%>
        <%--@elvariable id="employeeCardCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardCreatePage"--%>

        <rich:panelMenuItem id="showEmployeeCardListMenuItem" binding="#{employeeCardListPage.mainMenuComponent}"
                            label="Список" action="#{employeeCardListPage.show}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedEmployeeCardGroupMenu"
                             label="#{employeeCardGroupPage.currentCard.cardPrintedNo}"
                             binding="#{employeeCardGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{employeeCardGroupPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewEmployeeCardMenuItem" binding="#{employeeCardViewPage.mainMenuComponent}"
                                label="Просмотр" action="#{employeeCardViewPage.show}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editEmployeeCardMenuItem" binding="#{employeeCardEditPage.mainMenuComponent}"
                                label="Редактировать" action="#{employeeCardEditPage.show}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createEmployeeCardListMenuItem" binding="#{employeeCardCreatePage.mainMenuComponent}"
                            label="Создание" action="#{employeeCardCreatePage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="employeeHistoryReportMenuItem" binding="#{employeeHistoryReportPage.mainMenuComponent}"
                        label="Отчет по проходам" action="#{employeeHistoryReportPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="benefitsOptionsMenu" binding="#{mainPage.benefitsOptionsPage.mainMenuComponent}"
                         label="Льготы" rendered="#{mainPage.eligibleToViewCategory || !mainPage.eligibleToViewUsers || mainPage.eligibleToViewRule}">
    <a4j:support event="onclick" action="#{mainPage.showBenefitsOptionsPage}" reRender="workspaceForm" />

<rich:panelMenuGroup id="categoryOrgGroupMenu" label="Категории организаций"
                     binding="#{mainPage.categoryOrgGroupPage.mainMenuComponent}" rendered="#{!mainPage.eligibleToViewUsers}">

    <a4j:support event="onclick" action="#{mainPage.showCategoryOrgGroupPage}" reRender="workspaceForm" />

    <%--@elvariable id="categoryOrgListPage" type="ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListPage"--%>
    <%--@elvariable id="categoryOrgEditPage" type="ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgEditPage"--%>
    <%--@elvariable id="categoryOrgCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgCreatePage"--%>
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
    <%--@elvariable id="categoryDiscountListPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountListPage"--%>
    <%--@elvariable id="categoryDiscountCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountCreatePage"--%>
    <%--@elvariable id="categoryDiscountEditPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage"--%>
    <rich:panelMenuItem id="categoryListMenuItem" label="Список" binding="#{categoryDiscountListPage.mainMenuComponent}"
                        action="#{categoryDiscountListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedCategoryGroupMenu" label="#{categoryDiscountEditPage.entityName}" rendered="false">
        <rich:panelMenuItem id="editCategoryMenuItem" binding="#{categoryDiscountEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{categoryDiscountEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="categoryCreateMenuItem" binding="#{categoryDiscountCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{categoryDiscountCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>
<rich:panelMenuGroup id="categoryDSZNGroupMenu" binding="#{mainPage.categoryDSZNGroupPage.mainMenuComponent}"
                     label="Категории ДТиСЗН" rendered="#{mainPage.eligibleToViewCategory}">
    <a4j:support event="onclick" action="#{mainPage.showCategoryDSZNGroupPage}" reRender="workspaceForm" />
    <%--@elvariable id="categoryDiscountDSZNListPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn.CategoryDiscounDSZNtListPage"--%>
    <%--@elvariable id="categoryDiscountDSZNCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn.CategoryDiscountDSZNCreatePage"--%>
    <%--@elvariable id="categoryDiscountDSZNEditPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn.CategoryDiscountDSZNEditPage"--%>
    <rich:panelMenuItem id="categoryDSZNListMenuItem" label="Список" binding="#{categoryDiscountDSZNListPage.mainMenuComponent}"
                        action="#{categoryDiscountDSZNListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedCategoryDSZNGroupMenu" label="#{categoryDiscountDSZNEditPage.entityName}" rendered="false">
        <rich:panelMenuItem id="editCategoryDSZNMenuItem" binding="#{categoryDiscountDSZNEditPage.mainMenuComponent}"
                            label="Редактирование" action="#{categoryDiscountDSZNEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuItem id="categoryCreateDSZNMenuItem" binding="#{categoryDiscountDSZNCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{categoryDiscountDSZNCreatePage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>


<rich:panelMenuGroup id="ruleGroupMenu" binding="#{mainPage.ruleGroupPage.mainMenuComponent}"
                     label="Правила соц. скидок" rendered="#{mainPage.eligibleToViewRule}">
    <a4j:support event="onclick" action="#{mainPage.showRuleGroupPage}" reRender="workspaceForm" />
    <%--@elvariable id="ruleCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleCreatePage"--%>
    <%--@elvariable id="ruleListPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleListPage"--%>
    <%--@elvariable id="ruleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleEditPage"--%>
    <rich:panelMenuItem id="ruleListMenuItem" label="Список" binding="#{ruleListPage.mainMenuComponent}"
                        action="#{ruleListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedRuleGroupMenu" label="#{ruleEditPage.entityName}" rendered="false">

        <rich:panelMenuItem id="editRuleMenuItem" binding="#{ruleEditPage.mainMenuComponent}" label="Редактирование"
                            action="#{ruleEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="ruleCreateMenuItem" binding="#{ruleCreatePage.mainMenuComponent}" label="Регистрация"
                        action="#{ruleCreatePage.show}" reRender="workspaceForm" />

    <%--@elvariable id="complexRuleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.ComplexRuleEditPage"--%>
    <rich:panelMenuItem id="complexRoleEditMenuItem" binding="#{complexRuleEditPage.mainMenuComponent}"
                        label="Роли комплексов" action="#{complexRuleEditPage.show}" reRender="workspaceForm" />

    <rich:panelMenuItem id="wtRuleListMenuItem" label="Список (веб-технолог)" binding="#{wtRuleListPage.mainMenuComponent}"
                        action="#{wtRuleListPage.show}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="wtSelectedRuleGroupMenu" label="#{wtRuleEditPage.entityName}" rendered="false">

        <rich:panelMenuItem id="wtEditRuleMenuItem" binding="#{wtRuleEditPage.mainMenuComponent}" label="Редактирование (веб-технолог)"
                            action="#{wtRuleEditPage.show}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="wtRuleCreateMenuItem" binding="#{wtRuleCreatePage.mainMenuComponent}" label="Регистрация (веб-технолог)"
                        action="#{wtRuleCreatePage.show}" reRender="workspaceForm" />


</rich:panelMenuGroup>

</rich:panelMenuGroup>

<rich:panelMenuGroup id="reportGroupMenu" binding="#{mainPage.reportGroupPage.mainMenuComponent}" label="Отчеты" rendered="#{!mainPage.eligibleToViewUsers}">
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

        <rich:panelMenuItem id="reportQuartzJobsListMenuItem" binding="#{mainPage.quartzJobsListPage.mainMenuComponent}"
                            label="Quartz jobs" action="#{mainPage.showQuartzJobsListPage}"
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

<rich:panelMenuGroup id="infoGroupMenu" binding="#{mainPage.infoGroupMenu.mainMenuComponent}" label="Информация" rendered="#{!mainPage.eligibleToViewUsers}">
    <rich:panelMenuItem id="showCryptoInfo" binding="#{cryptoInfoPage.mainMenuComponent}" label="Крипто-провайдер"
                        action="#{cryptoInfoPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="debugGroupMenu" binding="#{mainPage.debugGroupMenu.mainMenuComponent}" label="Debug"
                     rendered="#{mainPage.isTestMode() && !mainPage.eligibleToViewUsers}">
    <rich:panelMenuItem id="showDebugInfo" binding="#{debugInfoPage.mainMenuComponent}" label="Задачи"
                        action="#{debugInfoPage.show}" reRender="workspaceForm" />

</rich:panelMenuGroup>

</rich:panelMenuGroup>

    <rich:panelMenuGroup id="cardOperatorMenu" binding="#{mainPage.cardGroupMenu.mainMenuComponent}"
                         label="Операции по картам" rendered="#{mainPage.eligibleToViewCardOperator}" >
        <%--@elvariable id="cardOperatorPage" type="ru.axetta.ecafe.processor.web.ui.cardoperator.CardOperatorPage"--%>
        <rich:panelMenuItem id="cardOpatorItem" binding="#{cardOperatorPage.mainMenuComponent}"
                            label="Список карт" action="#{cardOperatorPage.show}" reRender="workspaceForm" oncomplete=""/>
        <rich:panelMenuItem id="cardRegistrationAndIssueItem" binding="#{mainPage.cardRegistrationAndIssuePage.mainMenuComponent}"
                            label="Регистрация и выдача карты" action="#{mainPage.showCardRegistrationAndIssuePage}" reRender="workspaceForm"/>
        <rich:panelMenuItem id="clientRegistrationByOperatorItem" binding="#{mainPage.clientRegistrationByCardOperatorPage.mainMenuComponent}"
                            label="Регистрация клиента" action="#{mainPage.showClientRegistrationByCardOperatorPage}" reRender="workspaceForm"/>
        <rich:panelMenuItem id="cardOperatorList" binding="#{mainPage.cardOperatorListPage.mainMenuComponent}"
                            label="Список операций" action="#{mainPage.showCardOperatorListPage}" reRender="workspaceForm" />
        <rich:panelMenuItem id="crateAndReissueCardReport" binding="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.mainMenuComponent}"
                            label="Отчет по операциям" action="#{mainPage.showCreatedAndReissuedCardReport}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

</rich:panelMenu>
</a4j:form> <%-- Главное меню --%>