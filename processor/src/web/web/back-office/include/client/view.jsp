<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра клиента --%>
<h:panelGrid id="clientViewGrid" styleClass="borderless-grid" columns="2"
             binding="#{mainPage.clientViewPage.pageComponent}">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{mainPage.clientViewPage.orgShortName}" styleClass="command-link"
                   action="#{mainPage.showOrgViewPage}">
        <f:setPropertyActionListener value="#{mainPage.clientViewPage.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
    </a4j:commandLink>
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.contractId}" readonly="true" styleClass="input-text"
                 converter="contractIdConverter" />
    <h:outputText escape="true" value="Статус договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.contractState}" readonly="true"
                 converter="clientContractStateConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.contractTime}" readonly="true" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Физическое лицо, заключившее контракт" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.contractPerson.surname}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.contractPerson.firstName}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.contractPerson.secondName}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.contractPerson.idDocument}" readonly="true"
                     styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Обслуживаемое физическое лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.person.surname}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.person.firstName}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.person.secondName}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientViewPage.person.idDocument}" readonly="true" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Группа" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.clientGroupName}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Подгруппа" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.middleGroup}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.balance}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Основной счет" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.subBalance0}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Субсчет АП" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.subBalance1}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />

    <h:outputText escape="true" value="Статус подписки АП" styleClass="output-text" />
    <h:selectBooleanCheckbox disabled="true" value="#{mainPage.clientViewPage.wasSuspended}" readonly="true"
                             styleClass="output-text" />

    <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.limit}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Лимит расходов" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.expenditureLimit}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.address}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.phone}" readonly="true" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Номер мобильного телефона" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.mobile}" readonly="true" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.fax}" readonly="true" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Уведомлять с помощью SMS" styleClass="output-text" />
    <h:selectBooleanCheckbox disabled="true" value="#{mainPage.clientViewPage.notifyViaSMS}" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Уведомлять с помощью PUSH-уведомлений" styleClass="output-text" />
    <h:selectBooleanCheckbox disabled="true" value="#{mainPage.clientViewPage.notifyViaPUSH}" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.email}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Уведомлять по электронной почте" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientViewPage.notifyViaEmail}" disabled="true" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Правила оповещения" styleClass="output-text" />
    <rich:dataTable id="clientNotificationSetting" value="#{mainPage.clientViewPage.clientNotificationSettingPage.items}" var="it"
                    rows="8"
                    columnClasses="left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
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
            <h:selectBooleanCheckbox value="#{it.enabled}" disabled="true" readonly="true" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Предельное количество покупок без предъявления карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.freePayMaxCount}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Текущее количество покупок без предъявления карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.freePayCount}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Время последней покупки без предъявления карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.lastFreePayTime}" converter="timeConverter" readonly="true"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Тип предоставляемой льготы" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.discountMode}" converter="clientDiscountModeConverter" readonly="true"
                 styleClass="input-text" />

    <h:outputText  escape="true" value="Категории" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Нет категорий" styleClass="output-text"
                      rendered="#{mainPage.clientViewPage.categoryiesDiscounts}" />
        <a4j:repeat value="#{mainPage.clientViewPage.categoriesDiscounts}" var="categoryDiscount">
            <h:outputText escape="true" value="#{categoryDiscount.categoryName}" styleClass="output-text" /><br/>
        </a4j:repeat>
    </h:panelGrid>

    <h:outputText escape="true" value="СНИЛС" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.san}" maxlength="11" styleClass="input-text" readonly="true"/>
    <h:outputText escape="true" value="СНИЛС опекун" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.guardsan}" maxlength="64" styleClass="input-text" readonly="true"/>
    <h:outputText escape="true" value="Внешний идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.externalId}" maxlength="64" styleClass="input-text" readonly="true"/>
    <h:outputText escape="true" value="Идентификатор GUID" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.clientGUID}" maxlength="64" styleClass="input-text" readonly="true"/>
    <h:outputText escape="true" value="Пол" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientViewPage.gender}" styleClass="input-text" readonly="true">
        <f:selectItems value="#{mainPage.clientViewPage.clientGenderMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата рождения" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientViewPage.birthDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" readonly="true" />

    <h:outputText escape="true" value="Опекуны" styleClass="output-text"/>
    <rich:dataTable id="clientGuardianViewTable" value="#{mainPage.clientViewPage.clientGuardianItems}" var="clientGuardian"
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
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Мобильный телефон" />
            </f:facet>
            <h:outputText escape="true" value="#{clientGuardian.mobile}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Опекунство активировано" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{!clientGuardian.disabled}" disabled="true" readonly="true" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Степень родства" />
            </f:facet>
            <h:outputText escape="true" value="#{clientGuardian.relationStr}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>

    <h:outputText escape="true" value="Опекаемые" styleClass="output-text"/>
    <rich:dataTable id="clientWardViewTable" value="#{mainPage.clientViewPage.clientWardItems}" var="clientWard"
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
            <h:selectBooleanCheckbox value="#{!clientWard.disabled}" disabled="true" readonly="true" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Кем приходится опекун" />
            </f:facet>
            <h:outputText escape="true" value="#{clientGuardian.relationStr}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>

    <h:outputText escape="true" value="Не показывать в списке представителей внешним сервисам" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientViewPage.dontShowToExternal}" disabled="true" readonly="true"
                             styleClass="output-text" />

    <h:outputText escape="true" value="Учет последнего события входа в планах питания" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientViewPage.useLastEEModeForPlan}" disabled="true" readonly="true"
                             styleClass="output-text" />

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea readonly="true" rows="5" cols="64" value="#{mainPage.clientViewPage.remarks}"
                     styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Льгота при поступлении" styleClass="output-text" />
    <h:inputTextarea rows="5" cols="64" value="#{mainPage.clientViewPage.benefitOnAdmission}" styleClass="input-text" readonly="true"/>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showClientEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
<rich:panel headerClass="workspace-panel-header">
    <f:facet name="header">
        <h:outputText escape="true" value="Карты (#{mainPage.clientViewPage.clientCardListViewPage.itemCount})" />
    </f:facet>
    <rich:dataTable id="clientCardTable" value="#{mainPage.clientViewPage.clientCardListViewPage.items}" var="item"
                    rows="8"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showCardViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" converter="cardStateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус расположения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lifeState}" converter="cardLifeStateConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showCardEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="clientCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
</rich:panel>
<rich:panel headerClass="workspace-panel-header" rendered="#{not empty mainPage.clientViewPage.bankSubscriptions}">
    <f:facet name="header">
        <h:outputText escape="true" value="Подписки на автопополнение баланса" />
    </f:facet>
    <rich:dataTable id="clientBankSubscriptions" value="#{mainPage.clientViewPage.bankSubscriptions}" var="sub" rows="8"
                    rowKeyVar="row" columnClasses="right-aligned-column, left-aligned-column, center-aligned-column, right-aligned-column, right-aligned-column,
                    left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row + 1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата подключения" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.activationDate}" styleClass="output-text"
                          converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Активная" />
            </f:facet>
            <h:outputText escape="true" value='#{sub.active ? "Да" : "Нет"}' styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Нижний порог баланса" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.thresholdAmount}" styleClass="output-text"
                          converter="copeckSumConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сумма пополнения" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.paymentAmount}" styleClass="output-text"
                          converter="copeckSumConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Действительна до" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.validToDate}" styleClass="output-text" converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата отключения" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.deactivationDate}" styleClass="output-text"
                          converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последний успешный платеж" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.lastSuccessfulPaymentDate}" styleClass="output-text"
                          converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последний неуспешный платеж" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.lastUnsuccessfulPaymentDate}" styleClass="output-text"
                          converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <h:outputText escape="true" value="#{sub.maskedCardNumber}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="clientBankSubscriptions" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
</rich:panel>