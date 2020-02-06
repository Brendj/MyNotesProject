<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>
<
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="H" uri="http://richfaces.ajax4jsf.org/rich" %>
<%--@elvariable id="orgEquipmentReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgEquipmentReportPage"--%>
<%--<h:panelGrid id="orgSyncSettingsReportPanelGrid" binding="#{orgEquipmentReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{orgEquipmentReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{orgEquipmentReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Округ" />
        <h:selectOneMenu id="orgDistricts" value="#{orgEquipmentReportPage.selectedDistricts}" styleClass="input-text"
                         style="width: 250px;">
            <f:selectItems value="#{orgEquipmentReportPage.listOfOrgDistricts}" />
        </h:selectOneMenu>%-->
        <%--<h:outputText styleClass="output-text" escape="true" value="Тип синхронизации" />
        <h:selectOneMenu id="contentType" value="#{orgSyncSettingReportPage.selectedContentType}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingReportPage.listOfContentType}" />
        </h:selectOneMenu>--%>
        <%--<h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО" />
        <h:selectBooleanCheckbox value="#{orgEquipmentReportPage.allFriendlyOrgs}" styleClass="checkboxes" />
    </h:panelGrid>
    <%-- ГЕНЕРАЦИЯ ОТЧЕТОВ /// ПЕРЕДЕЛАТЬ
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{orgEquipmentReportPage.buildHTML()}"
                           reRender="orgSyncSettingsTable, autoDistributionSyncSettingsButton" styleClass="command-button" status="orgSyncReportStatus"
                           id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{orgEquipmentReportPage.buildXLS}"
                         styleClass="command-button" id="buildXLSButton" disabled="false">
            <a4j:support status="orgSyncReportStatus" id="buildXLSButtonSupport" />
        </h:commandButton>
    </h:panelGrid>
    --%>
<%--    <a4j:status id="orgSyncReportStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:dataTable id="orgEquipmentReportTable" value="#{orgEquipmentReportPage.items}" var="item" rows="25"
                    sortMode="single" rowClasses="center-aligned-column" lang="rus" footerClass="data-table-footer">
        <f:facet name="header">
            <h:outputText escape="true" value="Отчет по образовательным комплексам" />
        </f:facet>
        <rich:column sortable="true" sortBy="#{item.orgName}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ID ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortName}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Название ПП" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortNameInfoService}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Название ОО краткое" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.district}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Округ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Краткий адрес" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.type}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип ОУ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.type}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.statusDetailing}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Детализация статуса" />
            </f:facet>
            <h:outputText escape="true" value="#{item.statusDetailing}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.clientVersion}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Версия ПО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.clientVersion}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.databaseSize}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Размер БД" />
            </f:facet>
            <h:outputText escape="true" value="#{item.databaseSize}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.remoteAddress}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="IP" />
            </f:facet>
            <h:outputText escape="true" value="#{item.remoteAddress}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.sqlServerVersion}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="MySQL" />
            </f:facet>
            <h:outputText escape="true" value="#{item.sqlServerVersion}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Восстановить" action="#{orgEquipmentReportPage.resetChanges()}"
                           reRender="orgSyncSettingsTable" styleClass="command-button" status="orgSyncReportStatus"
                           id="resetChangesButton" />
        <a4j:commandButton action="#{orgSyncSettingReportPage.beginDistributionSyncSettings()}" disabled="#{orgSyncSettingReportPage.disableDistribution()}"
                           value="Автоматически распределить время" reRender="orgSyncSettingsTable"
                           styleClass="command-button"
                           id="autoDistributionSyncSettingsButton"/>
        <a4j:commandButton value="Применить изменения" action="#{orgSyncSettingReportPage.applyChanges()}"
                           reRender="orgSyncSettingsTable" styleClass="command-button" status="orgSyncReportStatus"
                           id="applyChangesButton" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
%-->
