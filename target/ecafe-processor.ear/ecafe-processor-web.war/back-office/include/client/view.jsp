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
    <h:commandLink value="#{mainPage.clientViewPage.orgShortName}" styleClass="command-link"
                   action="#{mainPage.showOrgViewPage}">
        <f:setPropertyActionListener value="#{mainPage.clientViewPage.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
    </h:commandLink>
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
    <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.balance}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.limit}" readonly="true" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.address}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.phone}" readonly="true" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Номер мобильного телефона" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.mobile}" readonly="true" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Уведомлять с помощью SMS" styleClass="output-text" />
    <h:selectBooleanCheckbox disabled="true" value="#{mainPage.clientViewPage.notifyViaSMS}" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.email}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Уведомлять по электронной почте" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientViewPage.notifyViaEmail}" disabled="true" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Тип SMS-уведомлений (не работает!)" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientViewPage.payForSMS}" readonly="true" converter="clientPayForSMSConverter"
                 styleClass="input-text" />
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
            <h:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </h:commandLink>
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
            <h:commandLink action="#{mainPage.showCardEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </h:commandLink>
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