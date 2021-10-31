<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка организаций --%>
<h:panelGrid id="orgsSecurityGrid" binding="#{mainPage.orgsSecurityPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Фильтр (#{mainPage.orgsSecurityPage.filterStatus})" switchType="client" id="securityFilterPanel"
                            eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Идентификатор организации" styleClass="output-text" />
            <h:inputText value="#{mainPage.orgsSecurityPage.filterIdOfOrg}" maxlength="5"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Наименование организации" styleClass="output-text" />
            <h:inputText value="#{mainPage.orgsSecurityPage.filterOfficialName}" maxlength="64"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Guid" styleClass="output-text" />
            <h:inputText value="#{mainPage.orgsSecurityPage.filterGuid}" maxlength="64"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Округ" styleClass="output-text" />
            <h:inputText value="#{mainPage.orgsSecurityPage.filterDistrict}" maxlength="64"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Уровень безопасности" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.orgsSecurityPage.securityLevel}" styleClass="input-text" style="width: 250px;">
                <f:selectItems value="#{mainPage.orgsSecurityPage.securityLevels}" />
            </h:selectOneMenu>

        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Применить" action="#{mainPage.orgsSecurityPage.updateOrgListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />

            <a4j:commandButton value="Очистить" action="#{mainPage.orgsSecurityPage.clearOrgListPageFilter}"
                               reRender="workspaceTogglePanel, securityFilterPanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>


    <rich:dataTable id="orgListTable" value="#{mainPage.orgsSecurityPage.orgsList}"
                    var="item" rows="20" footerClass="data-table-footer">
        <rich:column headerClass="column-header" styleClass="right-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Guid" />
            </f:facet>
            <h:outputText escape="true" value="#{item.guid}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование для поставщика" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Полное наименование" />
            </f:facet>
            <h:outputText escape="true" value="#{item.officialName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Краткое наименование" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Округ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес" />
            </f:facet>
            <h:outputText escape="true" value="#{item.address}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="ИНН" />
            </f:facet>
            <h:outputText escape="true" value="#{item.inn}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип организации" />
            </f:facet>
            <h:outputText escape="true" value="#{item.type}" converter="orgTypeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" converter="orgStateConverter" styleClass="output-text" />
            <h:outputText escape="true" value="#{item.statusDetailing}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Уровень безопасности" />
            </f:facet>
            <h:graphicImage value="/images/tips/applied.png" />
            <h:outputText escape="true" value="#{item.securityLevel}" converter="orgSecurityLevelConverter" styleClass="output-text" style="font-weight: bold" />
            <%--<h:outputText escape="true" value="#{item.state}" styleClass="output-text" rendered="#{item.isExtendedSecurityLevel()}" />--%>
            <br/>
            <h:outputText escape="true" value="Включить:" styleClass="output-text" />
            <a4j:commandLink reRender="orgListTable" value="#{item.securityLevelToTurnOn}" action="#{mainPage.orgsSecurityPage.switchSecurityLevel()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.orgsSecurityPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="orgListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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