<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditReports())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования правила обработки автоматических отчетов --%>
<h:panelGrid id="reportRuleEditGrid" binding="#{mainPage.reportRuleEditPage.pageComponent}" styleClass="borderless-grid"
             columns="1">
    <h:panelGrid columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.reportRuleEditPage.idOfReportHandleRule}" styleClass="input-text" />
    <h:outputText escape="true" value="Название" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportRuleEditPage.ruleName}" maxlength="64" styleClass="input-text" style="width: 600px;" />
    <h:outputText escape="true" value="Тэг" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportRuleEditPage.tag}" maxlength="12" styleClass="input-text" style="width: 600px;" />
    <h:outputText escape="true" value="Включено" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportRuleEditPage.enabled}" styleClass="output-text" />
    <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.reportRuleEditPage.reportType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.reportRuleEditPage.reportTypeMenu.items}" />
        <a4j:support event="onchange" reRender="paramHints, templateFileSelect" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Шаблон отчета" styleClass="output-text" />
    <h:selectOneMenu id="templateFileSelect" value="#{mainPage.reportRuleEditPage.reportTemplateFileName}" styleClass="input-text">
        <f:selectItems value="#{mainPage.reportRuleEditPage.reportTemplatesFiles}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Формат отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.reportRuleEditPage.documentFormat}" styleClass="input-text">
        <f:selectItems value="#{mainPage.reportRuleEditPage.reportFormatMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Запуск в ручном режиме" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportRuleEditPage.manualReportRun}" styleClass="output-text" />
    <h:outputText escape="true" value="Время хранения в репозитории" styleClass="output-text" />
    <h:selectOneMenu id="revisionDates" value="#{mainPage.reportRuleEditPage.storagePeriod}" style="width:150px;" >
        <f:selectItems value="#{mainPage.reportRuleEditPage.storagePeriods}"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тема письма" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportRuleEditPage.subject}" maxlength="128" style="width: 600px;"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адреса рассылки" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportRuleEditPage.routeAddresses}" maxlength="1024" style="width: 600px;"
                 styleClass="input-text" />
    <h:outputText/>
    <h:outputText escape="true" value="Списки рассылок организаций: #{mainPage.reportRuleEditPage.mailListNames}" styleClass="hint-output-text" style="width: 600px;"/>

    <%--<h:outputText escape="true" value="Условия применения правила" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportRuleEditPage.ruleConditionItems}" maxlength="1024" style="width: 600px;"
                 styleClass="input-text" />
    <h:outputText/>
    <h:outputText escape="true" value="Условия перечисляются через ;" styleClass="hint-output-text" style="width: 600px;"/>--%>
    </h:panelGrid>

    <rich:dataTable id="paramHints" value="#{mainPage.reportRuleEditPage.paramHints}" var="item"
                    columnClasses="left-aligned-column, left-aligned-column">
        <f:facet name="header">
            <h:outputText escape="true" value="Параметры отчета" styleClass="output-text" style="color: #FFFFFF" />
        </f:facet>
        <rich:column>
            <h:outputText escape="true" value="#{item.hint.paramHint.name}" styleClass="output-text" />
            <h:outputText escape="true" value="*" style="color: #FF0000; font-weight: bold;" rendered="#{item.hint.required}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.hint.paramHint.description}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:selectOneMenu rendered="#{item.type=='combobox'}" styleClass="output-text" value="#{item.value}">
                <f:selectItems value="#{item.listItems}"/>
            </h:selectOneMenu>

            <h:selectOneRadio rendered="#{item.type=='radio'}" styleClass="output-text" value="#{item.value}">
                <f:selectItems value="#{item.listItems}"/>
            </h:selectOneRadio>

            <h:selectManyCheckbox rendered="#{item.type=='checkbox'}" styleClass="output-text" value="#{item.valueItems}">
                <f:selectItems value="#{item.listItems}"/>
            </h:selectManyCheckbox>

            <h:inputText value="#{item.value}" rendered="#{item.type=='input'}" styleClass="output-text" />

            <h:outputText escape="true" value="#{item.value}" rendered="#{item.type=='output'}" styleClass="output-text" />

            <h:panelGroup styleClass="borderless-div" rendered="#{item.type=='contragent'}">
                <h:inputText value="#{mainPage.reportRuleEditPage.contragentFilter.contragent.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.reportRuleEditPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0"
                                                 target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value=""
                                                 target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>

            <h:panelGroup styleClass="borderless-div" rendered="#{item.type=='contragent-payagent'}">
                <h:inputText value="#{mainPage.reportRuleEditPage.contragentPayAgentFilter.contragent.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.reportRuleEditPage.showContragentPayAgentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0"
                                                 target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="1"
                                                 target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>

            <h:panelGroup styleClass="borderless-div" rendered="#{item.type=='contragent-receiver'}">
                <h:inputText value="#{mainPage.reportRuleEditPage.contragentReceiverFilter.contragent.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.reportRuleEditPage.showContragentReceiverSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0"
                                                 target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="1"
                                                 target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>

            <h:panelGroup styleClass="borderless-div"  rendered="#{item.type=='contract'}">
                <h:inputText value="#{mainPage.reportRuleEditPage.contractFilter.contract.contractName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showContractSelectPage}"
                                   reRender="modalContractSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0"
                                                 target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value=""
                                                 target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>

            <h:panelGroup  rendered="#{item.type=='org'}">
                <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="#{mainPage.reportRuleEditPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.reportRuleEditPage.filter}}" />
            </h:panelGroup>
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateReportRule}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showReportRuleEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>