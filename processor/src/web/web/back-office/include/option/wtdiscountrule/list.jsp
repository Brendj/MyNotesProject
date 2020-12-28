<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Правила --%>
<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToViewRule()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>
<%--@elvariable id="wtRuleListPage" type="ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule.wtRuleListPage"--%>
<%--@elvariable id="wtRuleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule.wtRuleEditPage"--%>
<h:panelGrid id="ruleListPanel" binding="#{wtRuleListPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Категории клиентов" styleClass="output-text"/>
        <h:panelGroup>
            <a4j:commandButton id="categoryAjaxButton" value="..." action="#{mainPage.showCategoryListSelectPage}"
                               reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{wtRuleListPage.idOfCategoryListString}"
                                             target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryListFilter1" escape="true"
                          value=" {#{wtRuleListPage.filter}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Категории организаций" styleClass="output-text"/>

        <h:panelGroup>
            <a4j:commandButton id="categoryOrgAjaxButton" value="..." action="#{mainPage.showCategoryOrgListSelectPage}"
                               reRender="modalCategoryOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{wtRuleListPage.idOfCategoryOrgListString}"
                                             target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryOrgListFilter1" escape="true"
                          value=" {#{wtRuleListPage.filterOrg}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field"/>
        <h:selectOneMenu id="group1" value="#{wtRuleListPage.subCategory}" style="width:300px;" styleClass="groupSelect">
            <f:selectItems value="#{wtRuleListPage.subCategories}"/>
        </h:selectOneMenu>

        <h:outputText escape="false" value="Показывать архивные правила" styleClass="output-text"/>
        <h:selectBooleanCheckbox value="#{wtRuleListPage.showArchived}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Применить" action="#{wtRuleListPage.updatePage}"
                               reRender="workspaceTogglePanel, ruleListPanel" styleClass="command-button"/>

            <a4j:commandButton value="Очистить" action="#{wtRuleListPage.clearPageFilter}"
                               reRender="workspaceTogglePanel, ruleListPanel" ajaxSingle="true"
                               styleClass="command-button"/>
        </h:panelGrid>

        <a4j:status id="sStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:dataTable id="wtRuleTable" width="700" var="item" value="#{wtRuleListPage.items}" rows="15"
                    columnClasses="center-aligned-column"
                    footerClass="data-table-footer" rendered="true">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Идентификатор" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Приоритет" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Супер-категория" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Описание" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Категории клиентов" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Категории организаций" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Тип условия" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Код МСП" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Редактировать" escape="true" styleClass="column-header"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="Удалить" escape="true" styleClass="column-header"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.idOfRule}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.priority}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.subCategory}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.description}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.categoryDiscounts}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.categoryOrgs}"/>
        </rich:column>
        <rich:column>
            <h:outputText value="#{item.operationor?'ИЛИ':'И'}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.codeMSP}"/>
        </rich:column>
        <rich:column>
            <a4j:commandLink reRender="workspaceForm" action="#{wtRuleEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item.wtEntity}" target="#{wtRuleEditPage.wtEntity}"/>
            </a4j:commandLink>

        </rich:column>

        <rich:column style="text-align:center">
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('confirmDeletePanel')}.show();">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{wtRuleListPage}"
                                             target="#{confirmDeletePage.listener}"/>
                <f:setPropertyActionListener value="#{item.wtEntity}"
                                             target="#{confirmDeletePage.entity}"/>
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="wtRuleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png"/>
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png"/>
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages"/>
    </h:panelGrid>
</h:panelGrid>
