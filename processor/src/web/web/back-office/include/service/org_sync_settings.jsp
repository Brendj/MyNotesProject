<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .mainBuilding {
        font-weight: bold;
    }

    .notServiced {
        background-color: #CECECE;
    }
</style>

<%--@elvariable id="orgSyncSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncSettingReportPage"--%>
<h:panelGrid id="orgSettingsReportPanelGrid" binding="#{orgSyncSettingsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{orgSyncSettingsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{orgSyncSettingsReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Округ" />
        <h:selectOneMenu id="orgDistricts"
                         value="#{orgSyncSettingsReportPage.selectedDistricts}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingsReportPage.listOfOrgDistricts}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО" />
        <h:selectBooleanCheckbox value="#{orgSyncSettingsReportPage.allFriendlyOrgs}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{orgSyncSettingsReportPage.buildHTML}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{orgSyncSettingsReportPage.buildXLS}"
                         styleClass="command-button"  id="buildXLSButton" disabled="false">
            <a4j:support status="reportGenerateStatus" id="buildXLSButtonSupport"/>
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:dataTable id="orgSettingsTable" value="#{orgSyncSettingsReportPage.items}" var="item" rows="25"
                    sortMode="single"
                    rowClasses="center-aligned-column" lang="rus"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <h:outputText escape="true" value="Отчет по образовательным комплексам" />
        </f:facet>
        <rich:column sortable="true" sortBy="#{item.orgName}" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="ID ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header" >
        <f:facet name="header">
                <h:outputText escape="true" value="Адрес" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortAddress}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Полная" />
            </f:facet>
            <h:outputText escape="true" value="#{item.fullSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Балансы и проходы" />
            </f:facet>
            <h:outputText escape="true" value="#{item.accIncSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Настройки ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgSettingSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Данные по клиентам" />
            </f:facet>
            <h:outputText escape="true" value="#{item.clientDataSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Меню" />
            </f:facet>
            <h:outputText escape="true" value="#{item.menuSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Фотографии" />
            </f:facet>
            <h:outputText escape="true" value="#{item.photoSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Служба помощи" />
            </f:facet>
            <h:outputText escape="true" value="#{item.helpRequestsSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Библиотека" />
            </f:facet>
            <h:outputText escape="true" value="#{item.libSync.fullInf}"
                          styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="orgSettingsTable" renderIfSinglePage="false"
                               maxPages="15" fastControls="hide" stepControls="auto"
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
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Применить изменения" action="#{orgSyncSettingsReportPage.applyChanges}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyChangesButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
