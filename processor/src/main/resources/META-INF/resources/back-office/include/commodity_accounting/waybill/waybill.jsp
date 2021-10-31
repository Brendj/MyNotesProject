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
  Date: 18.12.12
  Time: 16:33
  Список накладных
--%>
<%--@elvariable id="wayBillListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillListPage"--%>
<%--@elvariable id="wayBillPositionListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillPositionListPage"--%>
<%--@elvariable id="actOfWayBillDifferencePositionListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.ActOfWayBillDifferencePositionListPage"--%>
<h:panelGrid id="wayBillListPageGrid" binding="#{wayBillListPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Фильтр (#{wayBillListPage.filter.status})" switchType="client" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{wayBillListPage.filter.shortName}" readonly="true" styleClass="input-text long-field"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
            <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
            <h:inputText value="#{wayBillListPage.filter.number}" styleClass="input-text" />
            <h:outputText escape="true" value="Удаленные акты" styleClass="output-text" />
            <h:selectOneMenu id="selectDeletedStatus" value="#{wayBillListPage.filter.deletedState}" styleClass="input-text">
                <f:selectItem itemLabel="Скрыть" itemValue="true"/>
                <f:selectItem itemLabel="Показать" itemValue="false"/>
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{wayBillListPage.filter.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar value="#{wayBillListPage.filter.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{wayBillListPage.reload}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{wayBillListPage.resetFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button"/>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="wayBillListPageStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="wayBillListPageTable" value="#{wayBillListPage.itemList}" var="wayBill" rowKeyVar="row" rows="20">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{wayBill.number}">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{wayBill.number}" action="#{wayBillPositionListPage.show}" styleClass="command-link">
                <%--<f:setPropertyActionListener value="#{wayBill.idOfWayBill}" target="#{wayBillPositionListPage.idOfWayBill}" />--%>
                <f:setPropertyActionListener value="#{wayBill}" target="#{wayBillPositionListPage.wayBillItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата составления" />
            </f:facet>
            <h:outputText escape="true" value="#{wayBill.dateOfWayBill}" styleClass="output-text" converter="timeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{wayBill.state}">
            <f:facet name="header">
                <h:outputText escape="true" value="Состояние" />
            </f:facet>
            <h:outputText escape="true" value="#{wayBill.state}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ИНН контрагента" />
            </f:facet>
            <h:outputText escape="true" value="#{wayBill.inn}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер акт о наличии расхождений" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{wayBill.actOfWayBillDiffwrenceNumber}" action="#{actOfWayBillDifferencePositionListPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{wayBill.actOfWayBillDiffwrenceNumber}" target="#{actOfWayBillDifferencePositionListPage.filter.number}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{wayBill.orgOwner.idOfOrg}">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация получатель" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{wayBill.orgOwner.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{wayBill.orgOwner.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{wayBill.shipperOrg.idOfOrg}">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация отправитель" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{wayBill.shipperOrg.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{wayBill.shipperOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{wayBill.deletedState}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="wayBillListPageTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

