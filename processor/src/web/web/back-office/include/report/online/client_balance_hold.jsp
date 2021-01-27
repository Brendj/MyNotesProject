<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="clientBalanceHoldPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ClientBalanceHoldPage"--%>
<h:panelGrid id="clientBalanceHoldPanelGrid" binding="#{clientBalanceHoldPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{clientBalanceHoldPage.filter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Клиент" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectListPage(clientBalanceHoldPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{clientBalanceHoldPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{clientBalanceHoldPage.filterClient}}" />
        </h:panelGroup>
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:panelGroup>
            <h:selectOneMenu value="#{clientBalanceHoldPage.requestStatus}"
                             styleClass="input-text long-field">
                <f:selectItems value="#{clientBalanceHoldPage.requestStatusItems}" />
                <a4j:support event="onchange" reRender="clientBalanceHoldPanelGrid" />
            </h:selectOneMenu>
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Обновить" action="#{clientBalanceHoldPage.reload}"
                           reRender="clientBalanceHoldPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton" />
        <%--<h:commandButton value="Выгрузить в CSV" action="#{clientBalanceHoldPage.showClientBalanceHoldCSVList}"
                         styleClass="command-button" />--%>
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="clientBalanceHoldTable" value="#{clientBalanceHoldPage.items}" var="item" rows="25"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата и время заявления" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Номер л/с обучающегося" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ФИО обучающегося" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Номер л/с заявителя" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Телефон заявителя" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ФИО заявителя" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ИНН заявителя" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Р/с заявителя" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Наименование банка" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="БИК банка" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Корр. счет банка" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Размер баланса" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Сумма возврата" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Управление" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус заявления" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.createdDate}" converter="timeConverter" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.contractId}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.fio}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.declarerContractId}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.declarerPhone}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.declarerFio}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.inn}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.rs}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.bank}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.bik}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.korr}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.balance}" converter="copeckSumConverter" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.balanceHold}" converter="copeckSumConverter" />
        </rich:column>
        <rich:column>
            <a4j:commandLink reRender="clientBalanceHoldTable" rendered="#{item.showButton()}"
                             action="#{clientBalanceHoldPage.confirm()}" value="Подтвердить возврат">
                <f:setPropertyActionListener value="#{item}" target="#{clientBalanceHoldPage.currentItem}" />
            </a4j:commandLink>
            &nbsp;
            <a4j:commandLink reRender="clientBalanceHoldTable" rendered="#{item.showButton()}"
                               action="#{clientBalanceHoldPage.decline()}" value="Отказать в возврате">
                <f:setPropertyActionListener value="#{item}" target="#{clientBalanceHoldPage.currentItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.requestStatus}" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="clientBalanceHoldTable" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
