<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="blockunblockReportPanel" binding="#{mainPage.blockUnblockReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.blockUnblockReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Клиенты" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..."
                               action="#{mainPage.showClientSelectListPage(mainPage.blockUnblockReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                    #{rich:component('modalClientListSelectorPanel')}.show();" styleClass="command-link"
                               style="width: 25px;" id="clientFilterButton">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{mainPage.blockUnblockReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{mainPage.blockUnblockReportPage.filterClient}}" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.blockUnblockReportPage.allFriendlyOrgs}" styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Статус блокировки" styleClass="output-text" />
        <h:selectOneMenu id="regionsList" value="#{mainPage.blockUnblockReportPage.cardStatusFilter}"
                         style="width:120px;">
            <f:selectItems value="#{mainPage.blockUnblockReportPage.statusFilters}" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.blockUnblockReportPage.buildReportHTML}"
                           reRender="blockunblockReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.blockUnblockReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid columns="1" columnClasses="valign, valign">
        <rich:dataTable id="blockUnblockTable" value="#{mainPage.blockUnblockReportPage.allFriendlyOrgs}" var="item" rows="50"
                        footerClass="data-table-footer" columnClasses="center-aligned-column" reRender="lastOrgUpdateTime">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Ид" />
                </f:facet>
                <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Наименование" />
                </f:facet>
                <%--<h:outputText escape="true" value="#{item.orgName}" styleClass="output-text"--%>
                <%--style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />--%>
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.orgName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link"
                                 style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}">
                    <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Адрес" />
                </f:facet>
                <h:outputText escape="false" value="#{item.address}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Округ" />
                </f:facet>
                <h:outputText escape="false" value="#{item.district}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Тип здания" />
                </f:facet>
                <h:outputText escape="false" value="#{item.organizationTypeName}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Очередь внедрения" />
                </f:facet>
                <h:outputText escape="false" value="#{item.introductionQueue}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="false" value="Посл. успешная <br/>синхр. балансов" />
                </f:facet>
                <h:outputText escape="true" value="#{item.lastSuccessfulBalanceSync}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" converter="timeConverter" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия клиента" />
                </f:facet>
                <h:outputText escape="true" value="#{item.version}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlServerVersion}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Размер БД" />
                </f:facet>
                <h:outputText escape="true" value="#{item.databaseSize}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="IP-адрес" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddr}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and mainPage.syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="orgUnsychMonitorListTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    </h:panelGrid>
</h:panelGrid>

