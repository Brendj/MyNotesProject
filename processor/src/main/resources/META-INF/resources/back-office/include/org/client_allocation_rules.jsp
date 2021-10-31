<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="clientAllocationRulesPage" type="ru.axetta.ecafe.processor.web.ui.org.ClientAllocationRulesPage"--%>
<h:panelGrid id="clientAllocationRulesPage" binding="#{clientAllocationRulesPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:dataTable id="clientAllocationRulesTable" var="rule" value="#{clientAllocationRulesPage.rules}"
                    rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация-источник" />
            </f:facet>
            <%--Отображается при выводе сохраненного правила распределения клиентов--%>
            <a4j:commandLink rendered="#{not rule.editable}" reRender="mainMenu, workspaceForm"
                             value="#{rule.sourceOrgName}" action="#{mainPage.showOrgViewPage}"
                             styleClass="command-link">
                <f:setPropertyActionListener value="#{rule.idOfSourceOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
            <%--Отображается при выводе редактируемого или добавленного (но еще не сохраненного) правила распределения клиентов--%>
            <h:panelGrid rendered="#{rule.editable}" styleClass="borderless-div" columns="1">
                <h:panelGroup styleClass="borderless-div">
                    <h:inputText value="#{rule.sourceOrgName}" readonly="true" styleClass="input-text"
                                 style="margin-right: 2px;" size="20" />
                    <a4j:commandButton value="..." action="#{clientAllocationRulesPage.processSourceOrg(row)}"
                                       reRender="modalOrgSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" />
                </h:panelGroup>
            </h:panelGrid>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Фильтр групп" />
            </f:facet>
            <%--Отображается при выводе сохраненного правила распределения клиентов--%>
            <h:outputText rendered="#{not rule.editable}" escape="true" value="#{rule.groupFilter}"
                          styleClass="output-text" />
            <%--Отображается при выводе редактируемого или добавленного (но еще не сохраненного) правила распределения клиентов--%>
            <h:inputText rendered="#{rule.editable}" value="#{rule.groupFilter}" styleClass="input-text" size="30" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация-цель" />
            </f:facet>
            <%--Отображается при выводе сохраненного правила распределения клиентов--%>
            <a4j:commandLink rendered="#{not rule.editable}" reRender="mainMenu, workspaceForm"
                             value="#{rule.destOrgName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{rule.idOfDestOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
            <%--Отображается при выводе редактируемого или добавленного (но еще не сохраненного) правила распределения клиентов--%>
            <h:panelGrid rendered="#{rule.editable}" styleClass="borderless-div" columns="1">
                <h:panelGroup styleClass="borderless-div">
                    <h:inputText value="#{rule.destOrgName}" readonly="true" styleClass="input-text"
                                 style="margin-right: 2px;" size="20" />
                    <a4j:commandButton value="..." action="#{clientAllocationRulesPage.processDestinationOrg(row)}"
                                       reRender="modalOrgSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" />
                </h:panelGroup>
            </h:panelGrid>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Признак временных клиентов" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{rule.tempClient}" styleClass="output-text"
                                     disabled="#{not rule.editable}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink rendered="#{not rule.editable}" reRender="clientAllocationRulesTable"
                             action="#{clientAllocationRulesPage.editRule(row)}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" />
            </f:facet>
            <a4j:commandLink reRender="clientAllocationRulesTable" action="#{clientAllocationRulesPage.deleteRule(row)}"
                             styleClass="command-link">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>

    <h:panelGrid styleClass="borderless-div" columns="1">
        <a4j:commandButton id="addRule" value="Добавить правило" action="#{clientAllocationRulesPage.addRule}"
                           reRender="clientAllocationRulesTable" />
        <h:panelGroup style="margin-top: 10px">
            <a4j:commandButton value="Сохранить" action="#{clientAllocationRulesPage.save}"
                               reRender="clientAllocationRulesTable"
                               onclick="this.disabled = true;"
                               oncomplete="this.disabled = false;" />
            <a4j:commandButton id="resetRules" value="Отмена" action="#{clientAllocationRulesPage.cancel}"
                               reRender="clientAllocationRulesTable" />
            <a4j:status id="sSaveRulesStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGroup>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>