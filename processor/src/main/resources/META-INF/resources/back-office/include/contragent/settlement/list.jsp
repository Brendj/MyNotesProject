<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Справочник платежей между контрагентами --%>
<h:panelGrid id="settlementListPanel" binding="#{mainPage.settlementListPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр (#{mainPage.settlementListPage.filter.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Плательщик" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.settlementListPage.filter.contragentPayer.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                        <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                        <f:setPropertyActionListener value="1,2,3,4"
                                             target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>
            <h:outputText escape="true" value="Получатель" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.settlementListPage.filter.contragentReceiver.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                        <f:setPropertyActionListener value="1"
                                             target="#{mainPage.multiContrFlag}" />
                        <f:setPropertyActionListener value="1,2,3,4"
                                             target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{mainPage.showSettlementListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{mainPage.clearSettlementListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="settlementTableGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="settlementTable" value="#{mainPage.settlementListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header" style="text-align:right">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfSettlement}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" style="text-align:left">
            <f:facet name="header">
                <h:outputText escape="true" value="Плательщик" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.contragentPayer.contragentName}" action="#{mainPage.showContragentViewPage}"
                           styleClass="command-link">
                <f:setPropertyActionListener value="#{item.contragentPayer.idOfContragent}"
                                             target="#{mainPage.selectedIdOfContragent}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" style="text-align:left">
            <f:facet name="header">
                <h:outputText escape="true" value="Получатель" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.contragentReceiver.contragentName}" action="#{mainPage.showContragentViewPage}"
                           styleClass="command-link">
                <f:setPropertyActionListener value="#{item.contragentReceiver.idOfContragent}"
                                             target="#{mainPage.selectedIdOfContragent}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Дата создания" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.createdDate}" converter="timeConverter" />
        </rich:column>
                <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Дата платежа" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.paymentDate}" converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Платежный документ" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.paymentDoc}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:center">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Сумма" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.summa}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditContragents}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showSettlementEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfSettlement}" target="#{mainPage.selectedIdOfSettlement}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditContragents}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('settlementDeletePanel')}.show();">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfSettlement}"
                                             target="#{mainPage.selectedIdOfSettlement}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="settlementTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSettlementCSVList}" styleClass="command-button" />
</h:panelGrid>
