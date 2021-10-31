<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Панель просмотра отчет Организации по контрактам --%>
<%--@elvariable id="orgOfContractsReportPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.OrgOfContractsReportPage"--%>
<h:panelGrid id="orgOfContractsReportPanelGrid" binding="#{orgOfContractsReportPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр" switchType="client"
                            opened="true" headerClass="filter-panel-header">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText styleClass="output-text" escape="true" value="Отобразить на дату" />
            <rich:calendar value="#{orgOfContractsReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText styleClass="output-text" escape="true" value="Вывести организации без привязанного контракта" />
            <h:selectBooleanCheckbox value="#{orgOfContractsReportPage.selectBindContract}"/>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid">
            <a4j:commandButton value="Генерировать отчет" action="#{orgOfContractsReportPage.build}"
                               reRender="workspaceTogglePanel, orgOfContractsReportTable"
                               styleClass="command-button" status="reportGenerateStatus" />
            <a4j:status id="reportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" rendered="#{orgOfContractsReportPage.contractsReport!=null}">
        <h:outputText escape="true" value="Организации по контрактам" styleClass="output-text" />
        <rich:dataTable id="orgOfContractsReportTable" value="#{orgOfContractsReportPage.contractsReport.items}"
                        var="item" rowKeyVar="row" rows="50"  reRender="menuViewListScroller" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Организация" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Контракт" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Контрагент" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Поставщик" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink value="#{item.contractNumber}" action="#{contractViewPage.show}" styleClass="command-link"
                                 reRender="mainMenu, workspaceForm">
                    <f:setPropertyActionListener value="#{item.idOfContract}" target="#{contractEditPage.selectedEntityGroupPage.currentEntityItemId}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.contragentName}" action="#{mainPage.showContragentViewPage}"
                                 styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.idOfContragent}" target="#{mainPage.selectedIdOfContragent}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.supplierName}" action="#{mainPage.showContragentViewPage}"
                                 styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.idOfSupplier}" target="#{mainPage.selectedIdOfContragent}" />
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="orgOfContractsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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