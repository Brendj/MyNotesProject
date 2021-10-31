<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра операций по карте --%>
<%--@elvariable id="distributionRulesPage" type="ru.axetta.ecafe.processor.web.ui.org.DistributionRulesPage"--%>
<h:panelGrid id="distributionRulesListGrid" binding="#{distributionRulesPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:extendedDataTable id="distributionRulesTable" value="#{distributionRulesPage.ruleItemList}" var="ruleItem"
                            columnClasses="left-aligned-column, left-aligned-column, left-aligned-column" rows="500"
                            sortMode="multi" selectionMode="single" width="1200" height="900"
                            footerClass="data-table-footer">
        <rich:column headerClass="column-header" sortable="false" sortBy="#{ruleItem.contragentLabel}"  width="300px" filterBy="#{ruleItem.contragentLabel}" filterEvent="onkeyup">
            <f:facet name="header">
                <h:outputText value="Поставщик по умолчанию" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{ruleItem.contragentLabel}" action="#{mainPage.showContragentViewPage}"
                           styleClass="command-link">
                <f:setPropertyActionListener value="#{ruleItem.contragent.idOfContragent}"
                                             target="#{mainPage.selectedIdOfContragent}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortable="false" sortBy="#{ruleItem.distributionOrgLabel}" width="300px" filterBy="#{ruleItem.distributionOrgLabel}" filterEvent="onkeyup">
            <f:facet name="header">
                <h:outputText value="Организация - источник меню" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{ruleItem.distributionOrgLabel}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{ruleItem.distributionOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortable="false" sortBy="#{ruleItem.sourceOrgLabel}" width="300px" filterBy="#{ruleItem.sourceOrgLabel}" filterEvent="onkeyup">
            <f:facet name="header">
                <h:outputText value="Организация" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{ruleItem.sourceOrgLabel}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{ruleItem.sourceOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortable="false" sortBy="#{ruleItem.sourceOrg.shortName}" width="300px" filterBy="#{ruleItem.sourceOrg.orgNumberInName}" filterEvent="onkeyup">
            <f:facet name="header">
                <h:outputText value="Организация - номер" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{ruleItem.sourceOrg.orgNumberInName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{ruleItem.sourceOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="distributionRulesTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:extendedDataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
