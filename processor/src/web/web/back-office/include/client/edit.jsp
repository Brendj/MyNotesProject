<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditClients())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<f:subview id="clientDeleteSubView">
    <c:import url="include/client/confirm_client_delete.jsp" />
</f:subview>

<%-- Панель редактирования клиента --%>
<h:panelGrid id="clientEditGrid" styleClass="borderless-grid" binding="#{mainPage.clientEditPage.pageComponent}"
             columns="2">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientEditPage.org.shortName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
        <h:outputText value="Внимание! Выбранная организация не имеет льготы по всем категориям данного клиента."
                      rendered="#{not mainPage.clientEditPage.newOrgHasCatDiscount}"
                      styleClass="warn-messages output-text" style="margin-left: 10px;"/>
    </h:panelGroup>
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.changePassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="clientEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль" styleClass="output-text"
                  rendered="#{mainPage.clientEditPage.changePassword}" />
    <h:inputSecret value="#{mainPage.clientEditPage.plainPassword}" maxlength="64" redisplay="true"
                   rendered="#{mainPage.clientEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text"
                  rendered="#{mainPage.clientEditPage.changePassword}" />
    <h:inputSecret value="#{mainPage.clientEditPage.plainPasswordConfirmation}" maxlength="64" redisplay="true"
                   rendered="#{mainPage.clientEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Номер лицевого счета" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.contractId}" converter="contractIdConverter"
                 maxlength="#{mainPage.clientEditPage.contractIdMaxLength}" styleClass="input-text" />
    <h:outputText escape="true" value="Статус договора" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientEditPage.contractState}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientEditPage.clientContractStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientEditPage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Физическое лицо, заключившее контракт" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.surname}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.firstName}" maxlength="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.secondName}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.idDocument}" maxlength="128"
                     styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Обслуживаемое физическое лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.surname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.firstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.secondName}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.idDocument}" maxlength="128" styleClass="input-text" />
    </h:panelGrid>

    <h:outputText escape="true" value="Параллель" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.parallel}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Группа" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientEditPage.clientGroupName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientGroupSelectPage}" reRender="modalClientGroupSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" disabled="#{mainPage.clientEditPage.org.idOfOrg == null}" >
            <f:param name="idOfOrg" value="#{mainPage.clientEditPage.org.idOfOrg}" />
            <f:setPropertyActionListener value="#{mainPage.clientEditPage.org.idOfOrg}" target="#{mainPage.clientGroupSelectPage.idOfOrg}" />
            <f:setPropertyActionListener value="#{null}" target="#{mainPage.clientGroupSelectPage.filter}" />
        </a4j:commandButton>
    </h:panelGroup>

    <h:outputText escape="true" value="Тип возрастной группы" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.ageTypeGroup}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.clientEditPage.balance}" maxlength="20"
                 converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Основной счет" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.subBalance0}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Субсчет АП" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.clientEditPage.subBalance1}" maxlength="20"
                 converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Блокировка баланса и вывод средств" styleClass="output-text" />
    <h:outputText escape="false" value="#{mainPage.clientEditPage.balanceHold}" styleClass="output-text" />
    <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.limit}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Лимит расходов" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.expenditureLimit}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Порог снижения до отправки уведомления" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.balanceToNotify}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.address}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Номер мобильного телефона" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.mobile}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.fax}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Уведомлять с помощью SMS" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.notifyViaSMS}" styleClass="output-text" />
    <h:outputText escape="true" value="Уведомлять с помощью PUSH-уведомлений" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.notifyViaPUSH}" styleClass="output-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.email}" maxlength="128" styleClass="input-text"
         readonly="#{mainPage.clientEditPage.isEmailReadOnly()}" disabled="#{mainPage.clientEditPage.isEmailReadOnly()}" />
    <h:outputText escape="true" value="Уведомлять по электронной почте" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.notifyViaEmail}" styleClass="output-text" />
    <h:outputText escape="true" value="Правила оповещения" styleClass="output-text" rendered="#{mainPage.clientEditPage.oldFlagsShow}" />
    <rich:dataTable id="clientNotificationSetting" value="#{mainPage.clientEditPage.notificationSettings}" var="it"
                    rows="11"
                    columnClasses="left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer"
                    rendered="#{mainPage.clientEditPage.oldFlagsShow}">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип оповещения" />
            </f:facet>
            <h:outputText escape="true" value="#{it.notifyName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{it.enabled}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Предельное количество покупок без предъявления карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.freePayMaxCount}" styleClass="input-text" />
    <h:outputText escape="true" value="Исключен из плана питания" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.disablePlanCreation}" styleClass="output-text" />
    <h:outputText escape="true" value="Дата исключения из плана питания" styleClass="output-text" />
    <h:outputText value="#{mainPage.clientEditPage.disablePlanCreationDate}" escape="true" converter="timeConverter"
                  styleClass="output-text" />
    <h:outputText escape="true" value="Дата окончания срока исключения из плана питания" styleClass="output-text" />
    <h:outputText value="#{mainPage.clientEditPage.disablePlanEndDate}" escape="true" converter="timeConverter"
                  styleClass="output-text" />

    <h:outputText escape="true" value="СНИЛС" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.san}" maxlength="14" styleClass="input-text" />
    <h:outputText escape="true" value="СНИЛС опекун" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.guardsan}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Серия паспорта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.passportSeries}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Номер паспорта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.passportNumber}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Внешний идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.externalId}" maxlength="64" styleClass="input-text"/>
    <h:outputText escape="true" value="Идентификатор GUID" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.clientGUID}" maxlength="64" styleClass="input-text long-field"/>
    <h:outputText escape="true" value="Идентификатор REGID" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.clientIacRegId}" maxlength="40" styleClass="input-text long-field"/>

    <h:outputText escape="true" value="Пол" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientEditPage.gender}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientEditPage.clientGenderMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата рождения" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientEditPage.birthDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />

    <h:outputText escape="true" value="Опекуны" styleClass="output-text"/>
    <rich:dataTable id="clientGuardianEditTable" value="#{mainPage.clientEditPage.clientGuardianItems}" var="clientGuardian"
                    columnClasses="left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer-center">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер договора" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{clientGuardian.contractId}" converter="contractIdConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{clientGuardian.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ФИО клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{clientGuardian.personName}" styleClass="output-text" />
            <br/>
            <h:outputText escape="true" value="#{clientGuardian.createdWhereGuardianStr}" rendered="#{!clientGuardian.isCreatedWhereDefault}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Мобильный телефон" />
            </f:facet>
            <h:outputText escape="true" value="#{clientGuardian.mobile}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" width="150">
            <f:facet name="header">
                <h:outputText escape="true" value="Опекунство активировано" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{clientGuardian.enabled}" styleClass="output-text" />
            <br/>
            <h:outputText escape="true" value="#{clientGuardian.createdWhereClientGuardianStr}" rendered="#{!clientGuardian.isCreatedWhereDefault}"/>
        </rich:column>
        <rich:column headerClass="column-header" width="150">
            <f:facet name="header">
                <h:outputText escape="true" value="Согласие на предзаказ" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{clientGuardian.informedSpecialMenu}" disabled="true" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" width="150">
            <f:facet name="header">
                <h:outputText escape="true" value="Законный представитель" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{clientGuardian.legalRepresentative}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" width="150">
            <f:facet name="header">
                <h:outputText escape="true" value="Степень родства" />
            </f:facet>
            <h:selectOneMenu value="#{clientGuardian.relation}" styleClass="input-text">
                <f:selectItems value="#{mainPage.clientEditPage.relations}" />
            </h:selectOneMenu>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Правила оповещения" />
            </f:facet>
            <rich:dataTable id="clientNotificationSetting" value="#{clientGuardian.notificationItems}" var="it"
                            rows="11" columnClasses="left-aligned-column, center-aligned-column"  styleClass="borderless-grid-all-client">
                <rich:column styleClass="borderless-grid-all-client">
                    <h:outputText escape="true" value="#{it.notifyName}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="borderless-grid-all-client">
                    <h:selectBooleanCheckbox value="#{it.enabled}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link" reRender="clientGuardianEditTable"
                    action="#{mainPage.clientEditPage.removeClientGuardian}">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{clientGuardian}" target="#{mainPage.clientEditPage.currentClientGuardian}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <a4j:commandButton value="Добавить" action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                               styleClass="command-link">
                <f:setPropertyActionListener value="guardian" target="#{mainPage.clientEditPage.typeAddClient}" />
            </a4j:commandButton>
        </f:facet>
    </rich:dataTable>

    <h:outputText escape="true" value="Опекаемые" styleClass="output-text"/>
    <rich:dataTable id="clientWardEditTable" value="#{mainPage.clientEditPage.clientWardItems}" var="clientWard"
                    columnClasses="left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer-center">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер договора" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{clientWard.contractId}" converter="contractIdConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{clientWard.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ФИО клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{clientWard.personName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Мобильный телефон" />
            </f:facet>
            <h:outputText escape="true" value="#{clientWard.mobile}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Опекунство активировано" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{clientWard.enabled}" styleClass="output-text" />
            <br/>
            <h:outputText escape="true" value="#{clientWard.createdWhereClientGuardianStr}" rendered="#{!clientWard.isCreatedWhereDefault}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Согласие на предзаказ" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{clientWard.informedSpecialMenu}" disabled="true" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Кем приходится опекун" />
            </f:facet>
            <h:selectOneMenu value="#{clientWard.relation}" styleClass="input-text">
                <f:selectItems value="#{mainPage.clientEditPage.relations}" />
            </h:selectOneMenu>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Правила оповещения" />
            </f:facet>
            <rich:dataTable id="clientNotificationSetting" value="#{clientWard.notificationItems}" var="it"
                            rows="11" columnClasses="left-aligned-column, center-aligned-column"  styleClass="borderless-grid-all-client">
                <rich:column styleClass="borderless-grid-all-client">
                    <h:outputText escape="true" value="#{it.notifyName}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="borderless-grid-all-client">
                    <h:selectBooleanCheckbox value="#{it.enabled}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link" reRender="clientWardEditTable"
                             action="#{mainPage.clientEditPage.removeClientWard}">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{clientWard}" target="#{mainPage.clientEditPage.currentClientWard}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <a4j:commandButton value="Добавить" action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                               styleClass="command-link">
                <f:setPropertyActionListener value="ward" target="#{mainPage.clientEditPage.typeAddClient}" />
            </a4j:commandButton>
        </f:facet>
    </rich:dataTable>
    <h:outputText escape="true" value="Тип предоставляемой льготы" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientEditPage.discountMode}">
        <a4j:support event="onchange" reRender="clientCategory" action="#{mainPage.clientEditPage.changeClientCategory}"/>
        <f:selectItems value="#{mainPage.clientEditPage.selectItemList}"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Льготы" styleClass="output-text"/>
    <rich:dataTable id="clientdiscountsViewTable" value="#{mainPage.clientEditPage.clientDiscountItems}" var="clientDiscount"
                    columnClasses="center-aligned-column"
                    footerClass="data-table-footer-center">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип предоставляемой льготы" />
            </f:facet>
            <h:outputText escape="true" value="#{clientDiscount.discountMode}" styleClass="output-text" converter="clientDiscountModeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Категории льгот" />
            </f:facet>
            <h:outputText escape="true" value="#{clientDiscount.printedCategoriesDiscounts}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата последнего изменения льготы ИСПП" />
            </f:facet>
            <h:outputText escape="true" value="#{clientDiscount.lastDiscountsUpdate}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <a4j:commandButton value="Редактировать" action="#{mainPage.showCategoryListSelectPage}" reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link">
                <f:param name="fullList" value="false" />
                <f:setPropertyActionListener value="#{mainPage.clientEditPage.idOfCategoryListString}"
                                             target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}" />
            </a4j:commandButton>
        </f:facet>
    </rich:dataTable>
    <h:outputText escape="true" value="Не показывать в списке представителей внешним сервисам	" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.dontShowToExternal}" styleClass="output-text" />

    <h:outputText escape="true" value="Учет последнего события входа в планах питания" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.useLastEEModeForPlan}"
                             styleClass="output-text" />

    <h:outputText escape="true" value="Дата и время подтверждения номера телефона" styleClass="output-text"
                  rendered="#{!mainPage.clientEditPage.isLastConfirmMobileEmpty()}" />
    <h:inputText value="#{mainPage.clientEditPage.lastConfirmMobile}" readonly="true" converter="timeConverter"
                 styleClass="input-text" rendered="#{!mainPage.clientEditPage.isLastConfirmMobileEmpty()}" />

    <h:outputText escape="true" value="Особенности в питании" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.specialMenu}" styleClass="output-text" />
    <h:outputText escape="true" value="Клиент может подтверждать групповые платежи (для сотрудников школы, отвечающих за работу с системой)" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.canConfirmGroupPayment}" styleClass="output-text"
                             disabled="true"  readonly="true"/>
    <h:outputText escape="true" value="Пользователь ОП" styleClass="output-text"
                  rendered="#{mainPage.clientEditPage.isEligibleToViewUserOP()}" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.userOP}" styleClass="output-text" disabled="true"
                             readonly="true" rendered="#{mainPage.clientEditPage.isEligibleToViewUserOP()}" />

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea rows="5" cols="64" value="#{mainPage.clientEditPage.remarks}" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Дополнительно" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.cardRequest}" maxlength="64" styleClass="input-text long-field" readonly="true"/>
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateClient}" reRender="mainMenu, workspaceTogglePanel, clientGuardianEditTable, clientWardEditTable"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showClientEditPage}"
                       reRender="mainMenu, workspaceTogglePanel, clientGuardianEditTable, clientWardEditTable" ajaxSingle="true" styleClass="command-button" />
    <rich:spacer height="1" width="15"/>
    <a4j:commandButton value="Удалить"
                       onclick="#{rich:component('clientDeletePanel')}.show();"
                       styleClass="command-button" >
    </a4j:commandButton>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>