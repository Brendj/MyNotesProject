<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка организаций --%>
<h:panelGrid id="orgListPanelGrid" binding="#{mainPage.orgListPage.pageComponent}" styleClass="borderless-grid">

    <%-- Панель фильтрации организации:
     фильтр производится по идентификатору и по имени организации --%>

    <rich:simpleTogglePanel label="Фильтр (#{mainPage.orgListPage.orgFilter.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Идентификатор организации" styleClass="output-text" />

                <h:inputText value="#{mainPage.orgListPage.orgFilter.idOfOrg}" maxlength="5"
                             styleClass="input-text" />

                <h:outputText escape="true" value="Наименование организации" styleClass="output-text" />

                <h:inputText value="#{mainPage.orgListPage.orgFilter.officialName}" maxlength="64"
                             styleClass="input-text" />

                <h:outputText escape="true" value="Тэг" styleClass="output-text" />
                <h:inputText value="#{mainPage.orgListPage.orgFilter.tag}" maxlength="64"
                             styleClass="input-text" />

                <h:outputText escape="true" value="Город" styleClass="output-text" />
                <h:inputText value="#{mainPage.orgListPage.orgFilter.city}" maxlength="64"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Район" styleClass="output-text" />
                <h:inputText value="#{mainPage.orgListPage.orgFilter.district}" maxlength="64"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Локация" styleClass="output-text" />
                <h:inputText value="#{mainPage.orgListPage.orgFilter.location}" maxlength="64"
                             styleClass="input-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Применить" action="#{mainPage.updateOrgListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />

            <a4j:commandButton value="Очистить" action="#{mainPage.clearOrgListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>


    <rich:dataTable id="orgListTable" value="#{mainPage.orgListPage.items}"
                    var="item" rows="20" footerClass="data-table-footer">
        <rich:column headerClass="column-header" styleClass="right-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" value="#{item.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" converter="orgStateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="right-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер договора" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contractId}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Тэги" />
            </f:facet>
            <h:outputText escape="true" value="#{item.tag}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Город" />
            </f:facet>
            <h:outputText escape="true" value="#{item.city}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Район" />
            </f:facet>
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Локация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.location}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="left-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Контактный телефон" />
            </f:facet>
            <h:outputText escape="true" value="#{item.phone}" converter="phoneConverter" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header" styleClass="center-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" action="#{mainPage.showOrgEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
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


