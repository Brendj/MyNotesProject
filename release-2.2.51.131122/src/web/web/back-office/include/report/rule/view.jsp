<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра правила обработки автоматических отчетов --%>
<h:panelGrid id="reportRuleViewGrid" binding="#{mainPage.reportRuleViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.idOfReportHandleRule}"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Название" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.ruleName}" style="width: 600px;"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Тэг" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.tag}" style="width: 600px;"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Включено" styleClass="output-text" />
        <h:selectBooleanCheckbox disabled="true" readonly="true" value="#{mainPage.reportRuleViewPage.enabled}"
                                 styleClass="output-text" />
        <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.reportType}" style="width: 600px;"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Шаблон отчета" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.reportTemplateFileName}" style="width: 600px;"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Формат отчета" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.documentFormat}"
                     converter="reportFormatConverter" style="width: 600px;" styleClass="input-text" />

        <h:outputText escape="true" value="Запуск в ручном режиме" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.reportRuleViewPage.manualReportRun}" styleClass="output-text" disabled="true" />
        <h:outputText escape="true" value="Время хранения в репозитории" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.reportRuleViewPage.storagePeriod}" styleClass="output-text" />

        <h:outputText escape="true" value="Тема письма" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportRuleViewPage.subject}" style="width: 600px;"
                     styleClass="input-text" />
    </h:panelGrid>
    <rich:dataTable value="#{mainPage.reportRuleViewPage.routeAddresses}" var="item"
                    columnClasses="left-aligned-column">
        <f:facet name="header">
            <h:outputText escape="true" value="Адреса рассылки" styleClass="output-text" style="color: #FFFFFF" />
        </f:facet>
        <rich:column>
            <h:outputText escape="true" value="#{item}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <%--<rich:dataTable value="#{mainPage.reportRuleViewPage.ruleConditionItems}" var="item"
                    columnClasses="left-aligned-column, center-aligned-column, left-aligned-column">
        <f:facet name="header">
            <h:outputText escape="true" value="Условия применения правила" styleClass="output-text" />
        </f:facet>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionArgument}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionOperationText}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionConstant}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>--%>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showReportRuleEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
<rich:dataTable value="#{mainPage.reportRuleViewPage.paramHints}" var="item"
                columnClasses="left-aligned-column, left-aligned-column">
    <f:facet name="header">
        <h:outputText escape="true" value="Параметры отчеты" styleClass="output-text" style="color: #FFFFFF" />
    </f:facet>
    <rich:column>
        <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
    </rich:column>
    <rich:column>
        <h:outputText escape="true" value="#{item.description}" styleClass="output-text" />
    </rich:column>
    <rich:column>
        <h:outputText escape="true" value="#{item.value}" styleClass="output-text" />
    </rich:column>
</rich:dataTable>