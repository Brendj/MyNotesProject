<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://java.sun.com/jstl/fmt" %>


<a4j:jsFunction name="updateList" action="#{mainPage.syncMonitorPage.buildReportHtml}"
                reRender="orgUnsychMonitorListTable,lastOrgUpdateTime"></a4j:jsFunction>
<script type="text/javascript">
    var inter = setInterval(updateList, 1000 * 120);

    function onstartloading(){
        jQuery(".command-button, input[type='checkbox']").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button, input[type='checkbox']").attr('disabled', '');
    }

    document.onload = updateList();
</script>

<%-- Панель просмотра списка организаций --%>
<h:panelGrid id="syncMonitorPanelGrid" binding="#{mainPage.syncMonitorPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Обновить" action="#{mainPage.syncMonitorPage.buildReportHtml}"
                           reRender="orgUnsychMonitorListTable,lastOrgUpdateTime" status="syncMonitorReportStatus"
                           styleClass="command-button"/>

        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.syncMonitorPage.buildReportXLS}" styleClass="command-button" />

        <a4j:status id="syncMonitorReportStatus" onstart="onstartloading()" onstop="onstoploading()">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" value="Последнее обновление произведено: " />
        <h:outputText id="lastOrgUpdateTime" styleClass="output-text" value="#{mainPage.syncMonitorPage.lastUpdate}"
            converter="timeConverter" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:panelGrid id="syncMonitorVersionPanel" styleClass="borderless-grid" columns="1">
            <h:panelGroup styleClass="borderless-div">
                <h:outputText escape="true" value="Версия клиента"
                              styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{mainPage.syncMonitorPage.showVersion}" styleClass="output-text">
                    <a4j:support event="onclick" reRender="syncMonitorVersionPanel" ajaxSingle="true" />
                </h:selectBooleanCheckbox>

                <h:panelGroup layout="block" style="height: 150px; overflow-y: scroll;"
                              rendered="#{mainPage.syncMonitorPage.showVersion}">
                    <h:selectManyCheckbox id="versionTitles"
                                          value="#{mainPage.syncMonitorPage.versionTitles}"
                                          layout="pageDirection" styleClass="output-text"
                                          rendered="#{mainPage.syncMonitorPage.showVersion}">
                        <f:selectItems value="#{mainPage.syncMonitorPage.availableVersions.values()}" />
                    </h:selectManyCheckbox>
                </h:panelGroup>
            </h:panelGroup>
        </h:panelGrid>
    </h:panelGrid>
    <h:panelGrid columns="1" columnClasses="valign, valign">
        <rich:dataTable id="orgUnsychMonitorListTable" value="#{mainPage.syncMonitorPage.itemList}" var="item" rows="50"
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

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>