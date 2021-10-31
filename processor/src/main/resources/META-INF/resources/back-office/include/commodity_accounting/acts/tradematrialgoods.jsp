<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 20.12.12
  Time: 12:33
  Список ТМЦ
--%>
<%--@elvariable id="tradeMaterialGoodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.TradeMaterialGoodListPage"--%>
<h:panelGrid id="tradeMaterialGoodListPage" binding="#{tradeMaterialGoodListPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid id="tradeMaterialGoodListFilter" styleClass="borderless-grid">
        <rich:simpleTogglePanel label="Фильтр (#{tradeMaterialGoodListPage.filter.status})" switchType="client" opened="true"
                                headerClass="filter-panel-header">

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Организация" styleClass="output-text" />
                <h:panelGroup styleClass="borderless-div">
                    <h:inputText value="#{tradeMaterialGoodListPage.filter.shortName}" readonly="true" styleClass="input-text long-field"
                                 style="margin-right: 2px;" />
                    <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" />
                </h:panelGroup>
                <h:outputText escape="true" value="Удаленные акты" styleClass="output-text" />
                <h:selectOneMenu id="selectDeletedStatus" value="#{tradeMaterialGoodListPage.filter.deletedState}" styleClass="input-text">
                    <f:selectItem itemLabel="Скрыть" itemValue="true"/>
                    <f:selectItem itemLabel="Показать" itemValue="false"/>
                </h:selectOneMenu>
                <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
                <rich:calendar value="#{tradeMaterialGoodListPage.filter.startDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
                <rich:calendar value="#{tradeMaterialGoodListPage.filter.endDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{tradeMaterialGoodListPage.reload}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{tradeMaterialGoodListPage.resetFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button"/>
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>


    <a4j:status id="tradeMaterialGoodListPageStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="tradeMaterialGoodListTable" value="#{tradeMaterialGoodListPage.itemList}"
                    var="tradeMaterialGood" rowKeyVar="row" rows="10" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата составления" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.createdDate}" styleClass="output-text" converter="timeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Товар" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{tradeMaterialGood.good.nameOfGood}" action="#{goodViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{tradeMaterialGood.good}" target="#{selectedGoodGroupPage.currentGood}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.lifeTime}">
            <f:facet name="header">
                <h:outputText escape="true" value="Срок годности (в минутах)" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.lifeTime}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.unitsScale}">
            <f:facet name="header">
                <h:outputText escape="true" value="Единица измерения" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.unitsScale}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.totalCount}">
            <f:facet name="header">
                <h:outputText escape="true" value="Количество единиц" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{tradeMaterialGood.totalCount/1000}">
                <f:convertNumber pattern="#0"/>
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.netWeight}">
            <f:facet name="header">
                <h:outputText escape="true" value="Масса нетто" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.netWeight}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.selfPrice}">
            <f:facet name="header">
                <h:outputText escape="true" value="Цена за единицу" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.selfPrice}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="НДС" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.nds}%" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата изготовления" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.goodsCreationDate}" styleClass="output-text" converter="timeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{tradeMaterialGood.orgOwner.idOfOrg}">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{tradeMaterialGood.orgOwner.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{tradeMaterialGood.orgOwner.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{tradeMaterialGood.deletedState}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="tradeMaterialGoodListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>
