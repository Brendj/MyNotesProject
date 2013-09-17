<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 19.07.13
  Time: 12:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditOrgs())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель просмотра организации --%>
<%--@elvariable id="orgClientSettingsPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgClientSettingsPage"--%>
<%--@elvariable id="clientAllocationRulesPage" type="ru.axetta.ecafe.processor.web.ui.org.ClientAllocationRulesPage"--%>
<h:panelGrid id="orgClientSettingsPage" binding="#{orgClientSettingsPage.pageComponent}" styleClass="borderless-grid" columns="2">
    <rich:tabPanel>
        <rich:tab label="Синхронизация">
            <rich:panel>
                <f:facet name="header"><h:outputText styleClass="column-header"
                                                     value="Запрос полной синхронизации" /></f:facet>
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />
                    <h:panelGrid columns="2">
                        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                           styleClass="command-link" style="width: 25px;">
                            <f:setPropertyActionListener value="#{orgClientSettingsPage.idOfOrgList}"
                                                         target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                        </a4j:commandButton>
                        <h:outputText styleClass="output-text" id="orgClientSettingsFilter" escape="true"
                                      value=" {#{orgClientSettingsPage.filter}}" />
                    </h:panelGrid>
                    <a4j:commandButton value="Запросить" action="#{orgClientSettingsPage.applyFullSyncOperation}" />
                    <rich:spacer />
                    <h:panelGrid styleClass="borderless-grid">
                        <a4j:status id="sClientSettingsStatus">
                            <f:facet name="start">
                                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                            </f:facet>
                        </a4j:status>
                        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                                       warnClass="warn-messages" />
                    </h:panelGrid>
                </h:panelGrid>
            </rich:panel>
        </rich:tab>
        <rich:tab label="Настройки распределения клиентов">
            <rich:dataTable id="clientAllocationRulesTable" var="rule"
                            value="#{clientAllocationRulesPage.rules}" rowKeyVar="row"
                            columnClasses="center-aligned-column" footerClass="data-table-footer">
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
                        <f:setPropertyActionListener value="#{rule.idOfSourceOrg}"
                                                     target="#{mainPage.selectedIdOfOrg}" />
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
                        <h:outputText escape="true" value="Фильтр групп (regexp)" />
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
                                     value="#{rule.destOrgName}" action="#{mainPage.showOrgViewPage}"
                                     styleClass="command-link">
                        <f:setPropertyActionListener value="#{rule.idOfDestOrg}"
                                                     target="#{mainPage.selectedIdOfOrg}" />
                    </a4j:commandLink>
                    <%--Отображается при выводе редактируемого или добавленного (но еще не сохраненного) правила распределения клиентов--%>
                    <h:panelGrid rendered="#{rule.editable}" styleClass="borderless-div" columns="1">
                        <h:panelGroup styleClass="borderless-div">
                            <h:inputText value="#{rule.destOrgName}" readonly="true"
                                         styleClass="input-text" style="margin-right: 2px;" size="20"/>
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
                    <a4j:commandLink reRender="clientAllocationRulesTable"
                                     action="#{clientAllocationRulesPage.deleteRule(row)}" styleClass="command-link">
                        <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                    </a4j:commandLink>
                </rich:column>
            </rich:dataTable>

            <h:panelGrid styleClass="borderless-div" columns="1">
                <a4j:commandButton value="Добавить правило" action="#{clientAllocationRulesPage.addRule}"
                                   reRender="clientAllocationRulesTable" />
                <h:panelGroup style="margin-top: 10px">
                    <a4j:commandButton value="Сохранить" action="#{clientAllocationRulesPage.save}"
                                       reRender="clientAllocationRulesTable" />
                    <a4j:commandButton value="Отмена" action="#{clientAllocationRulesPage.cancel}"
                                       reRender="clientAllocationRulesTable" />
                </h:panelGroup>
            </h:panelGrid>

            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </rich:tab>
    </rich:tabPanel>
</h:panelGrid>