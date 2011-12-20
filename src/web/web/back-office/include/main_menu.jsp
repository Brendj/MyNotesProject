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
                iconItem="none" mode="server">

<rich:panelMenuGroup id="userGroupMenu" label="Пользователи" binding="#{mainPage.userGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToViewUsers}">
    <a4j:support event="onclick" action="#{mainPage.showUserGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="showUserListMenuItem" binding="#{mainPage.userListPage.mainMenuComponent}" label="Список"
                        action="#{mainPage.showUserListPage}" reRender="workspaceForm"/>

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
                            label="Редактирование" action="#{mainPage.showOrgEditPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="generateOrgBalanceReportMenuItem"
                            binding="#{mainPage.orgBalanceReportPage.mainMenuComponent}" label="Отчет по балансу"
                            action="#{mainPage.showOrgBalanceReportPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="generateOrgOrderReportMenuItem"
                            binding="#{mainPage.orgOrderReportPage.mainMenuComponent}" label="Отчет по покупкам"
                            action="#{mainPage.showOrgOrderReportPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createOrgMenuItem" binding="#{mainPage.orgCreatePage.mainMenuComponent}" label="Регистрация"
                        action="#{mainPage.showOrgCreatePage}" reRender="workspaceForm" />
</rich:panelMenuGroup>

<rich:panelMenuGroup id="contragentGroupMenu" label="Контрагенты"
                     binding="#{mainPage.contragentGroupPage.mainMenuComponent}" rendered="#{mainPage.eligibleToViewContragents}">
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
                            reRender="workspaceForm" />

        <rich:panelMenuItem id="generateContragentClientPaymentReportMenuItem"
                            binding="#{mainPage.contragentClientPaymentReportPage.mainMenuComponent}"
                            label="Отчет по платежам клиентов"
                            action="#{mainPage.showContragentClientPaymentReportPage}" reRender="workspaceForm" />
    </rich:panelMenuGroup>

    <rich:panelMenuItem id="createContragentMenuItem" binding="#{mainPage.contragentCreatePage.mainMenuComponent}"
                        label="Регистрация" action="#{mainPage.showContragentCreatePage}" reRender="workspaceForm" />

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
                         label="Cправочник точек продаж"
                         rendered="#{mainPage.eligibleToViewPos}">
        <a4j:support event="onclick" action="#{mainPage.showPosGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="posListMenuItem" label="Список"
                            binding="#{mainPage.posListPage.mainMenuComponent}"
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
                         label="Платежи между контрагентами"
                         rendered="#{mainPage.eligibleToViewPayment}">
        <a4j:support event="onclick" action="#{mainPage.showSettlementGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="settlementListMenuItem" label="Список"
                            binding="#{mainPage.settlementListPage.mainMenuComponent}"
                            action="#{mainPage.showSettlementListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedSettlementGroupMenu" label="#{mainPage.selectedSettlementGroupPage.name}"
                         binding="#{mainPage.selectedSettlementGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedSettlementGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editSettlementMenuItem" binding="#{mainPage.settlementEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showSettlementEditPage}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="settlementCreateMenuItem" binding="#{mainPage.settlementCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showSettlementCreatePage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="addPaymentGroupMenu" binding="#{mainPage.addPaymentGroupPage.mainMenuComponent}"
                         label="Начисление платы"
                         rendered="#{mainPage.eligibleToViewPayment}">
        <a4j:support event="onclick" action="#{mainPage.showAddPaymentGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="addPaymentListMenuItem" label="Список"
                            binding="#{mainPage.addPaymentListPage.mainMenuComponent}"
                            action="#{mainPage.showAddPaymentListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedAddPaymentGroupMenu" label="#{mainPage.selectedAddPaymentGroupPage.name}"
                         binding="#{mainPage.selectedAddPaymentGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedAddPaymentGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editAddPaymentMenuItem" binding="#{mainPage.addPaymentEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showAddPaymentEditPage}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="addPaymentCreateMenuItem" binding="#{mainPage.addPaymentCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showAddPaymentCreatePage}" reRender="workspaceForm" />

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

    <rich:panelMenuItem id="loadClientUpdateFromFileMenuItem" binding="#{mainPage.clientUpdateFileLoadPage.mainMenuComponent}"
                        label="Обновить из файла" action="#{mainPage.showClientUpdateFileLoadPage}"
                        reRender="workspaceForm" />

    <rich:panelMenuItem id="contractBuildMenu" label="Подготовка договора"
                        binding="#{mainPage.contractBuildPage.mainMenuComponent}"
                        action="#{mainPage.showContractBuildPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="clientLimitBatchEditMenu" label="Изменить лимит овердрафта"
                        binding="#{mainPage.clientLimitBatchEditPage.mainMenuComponent}"
                        action="#{mainPage.showClientLimitBatchEditPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="clientSmsListMenu" label="SMS" binding="#{mainPage.clientSmsListPage.mainMenuComponent}"
                        action="#{mainPage.showClientSmsListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="categoryGroupMenu" binding="#{mainPage.categoryGroupPage.mainMenuComponent}"
                         label="Категории льгот"
                         rendered="#{mainPage.eligibleToViewCategory}">
        <a4j:support event="onclick" action="#{mainPage.showCategoryGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="categoryListMenuItem" label="Список"
                            binding="#{mainPage.categoryListPage.mainMenuComponent}"
                            action="#{mainPage.showCategoryListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedCategoryGroupMenu" label="#{mainPage.selectedCategoryGroupPage.name}"
                         binding="#{mainPage.selectedCategoryGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedCategoryGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editCategoryMenuItem" binding="#{mainPage.categoryEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showCategoryEditPage}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="categoryCreateMenuItem" binding="#{mainPage.categoryCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showCategoryCreatePage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
    <rich:panelMenuGroup id="ruleGroupMenu" binding="#{mainPage.ruleGroupPage.mainMenuComponent}"
                         label="Правила категорий"
                         rendered="#{mainPage.eligibleToViewRule}">
        <a4j:support event="onclick" action="#{mainPage.showRuleGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="ruleListMenuItem" label="Список"
                            binding="#{mainPage.ruleListPage.mainMenuComponent}"
                            action="#{mainPage.showRuleListPage}" reRender="workspaceForm" />

        <rich:panelMenuGroup id="selectedRuleGroupMenu" label="#{mainPage.selectedRuleGroupPage.name}"
                         binding="#{mainPage.selectedRuleGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedRuleGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editRuleMenuItem" binding="#{mainPage.ruleEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showRuleEditPage}" reRender="workspaceForm" />

        </rich:panelMenuGroup>

        <rich:panelMenuItem id="ruleCreateMenuItem" binding="#{mainPage.ruleCreatePage.mainMenuComponent}"
                            label="Регистрация" action="#{mainPage.showRuleCreatePage}" reRender="workspaceForm" />

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
                        rendered="#{mainPage.eligibleToServiceAdmin}"/>

    <rich:panelMenuItem id="logTestMenuItem" binding="#{mainPage.testLogPage.mainMenuComponent}" label="Тест лога"
                        action="#{mainPage.showTestLogPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}"/>

    <rich:panelMenuItem id="buildSignKeysMenuItem" binding="#{mainPage.buildSignKeysPage.mainMenuComponent}"
                        label="Генерация ключей ЭЦП" action="#{mainPage.showBuildSignKeysPage}"
                        reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceAdmin}"/>

    <rich:panelMenuItem id="supportEmailMenuItem" binding="#{mainPage.supportEmailPage.mainMenuComponent}"
                        label="Отправка письма" action="#{mainPage.showSupportEmailPage}" reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceSupport}"/>

    <%--<rich:panelMenuItem label="Отправка SMS" action="#{mainPage.showSupportSmsSender}"--%>
    <%--reRender="mainMenu, workspaceForm" />--%>

    <rich:panelMenuItem id="loadSochiClientsMenuItem" binding="#{mainPage.sochiClientsLoadPage.mainMenuComponent}"
                        label="Загрузка списка клиентов по Сочи" action="#{mainPage.showSochiClientsLoadPage}"
                        reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceClients}"/>

    <rich:panelMenuItem id="viewSochiClientsMenuItem" binding="#{mainPage.sochiClientsViewPage.mainMenuComponent}"
                        label="Просмотр клиентов по Сочи" action="#{mainPage.showSochiClientsViewPage}"
                        reRender="workspaceForm"
                        rendered="#{mainPage.eligibleToServiceClients}"/>
</rich:panelMenuGroup>

<rich:panelMenuGroup id="reportJobGroupMenu" binding="#{mainPage.reportJobGroupPage.mainMenuComponent}"
                     label="Расписание отчетов"
                        rendered="#{mainPage.eligibleToViewReports}">
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
                        label="Добавить задачу" action="#{mainPage.showReportJobCreatePage}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="reportRuleGroupMenu" binding="#{mainPage.reportRuleGroupPage.mainMenuComponent}"
                     label="Обработка отчетов"
                        rendered="#{mainPage.eligibleToViewReports}">
    <a4j:support event="onclick" action="#{mainPage.showReportRuleGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="reportRuleListMenuItem" binding="#{mainPage.reportRuleListPage.mainMenuComponent}"
                        label="Список правил" action="#{mainPage.showReportRuleListPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="selectedReportRuleGroupMenu"
                         binding="#{mainPage.selectedReportRuleGroupPage.mainMenuComponent}"
                         label="#{mainPage.selectedReportRuleGroupPage.title}" rendered="false">
        <a4j:support event="onclick" action="#{mainPage.showSelectedReportRuleGroupPage}" reRender="workspaceForm" />

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

<rich:panelMenuGroup id="reportOnlineGroupMenu" binding="#{mainPage.reportOnlineGroupPage.mainMenuComponent}"
                     label="Онлайн отчеты" rendered="#{mainPage.eligibleToWorkOnlineReport}">
    <a4j:support event="onclick" action="#{mainPage.showReportOnlineGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="complexGroupMenu" binding="#{mainPage.complexGroupPage.mainMenuComponent}"
                         label="Отчет по комплексам">
        <a4j:support event="onclick" action="#{mainPage.showComplexGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="freeComplexReportMenuItem" binding="#{mainPage.freeComplexReportPage.mainMenuComponent}"
                        label="Бесплатные комплексы" action="#{mainPage.showFreeComplexReportPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="payComplexReportMenuItem" binding="#{mainPage.payComplexReportPage.mainMenuComponent}"
                        label="Платные комплексы" action="#{mainPage.showPayComplexReportPage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>


    <rich:panelMenuItem id="salesReportMenuItem" binding="#{mainPage.salesReportPage.mainMenuComponent}"
                        label="Отчет по продажам" action="#{mainPage.showSalesReportPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="syncReportMenuItem" binding="#{mainPage.syncReportPage.mainMenuComponent}"
                        label="Отчет по синхронизации" action="#{mainPage.showSyncReportPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="statusSyncReportMenuItem" binding="#{mainPage.statusSyncReportPage.mainMenuComponent}"
                        label="Статус синхронизации" action="#{mainPage.showStatusSyncReportPage}" reRender="workspaceForm" />

    <rich:panelMenuItem id="clientReportMenuItem" binding="#{mainPage.clientReportPage.mainMenuComponent}"
                        label="Отчет по учащимся" action="#{mainPage.showClientReportPage}" reRender="workspaceForm" />
    <rich:panelMenuItem id="currentPositionReportMenuItem" binding="#{mainPage.currentPositionsReportPage.mainMenuComponent}"
                        label="Просмотр текущих позиций" action="#{mainPage.showCurrentPositionsReportPage}" reRender="workspaceForm" />

</rich:panelMenuGroup>

<rich:panelMenuGroup id="eventNotificationGroupMenu" binding="#{mainPage.eventNotificationGroupPage.mainMenuComponent}"
                     label="Уведомления о событиях"
                        rendered="#{mainPage.eligibleToViewReports}">
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

    <rich:panelMenuItem id="showConfigurationMenuItem" binding="#{mainPage.configurationPage.mainMenuComponent}" label="Конфигурация"
                        action="#{mainPage.showConfigurationPage}" reRender="workspaceForm"/>

    <rich:panelMenuItem id="showOptionMenuItem" binding="#{mainPage.optionPage.mainMenuComponent}" label="Настройки"
                        action="#{mainPage.showOptionPage}" reRender="workspaceForm"/>
</rich:panelMenuGroup>
</rich:panelMenu>
</a4j:form> <%-- Главное меню --%>