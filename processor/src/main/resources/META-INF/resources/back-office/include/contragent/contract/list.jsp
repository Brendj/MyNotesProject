<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="contractListPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractListPage"--%>
<%--@elvariable id="contractViewPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractViewPage"--%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractEditPage"--%>
<h:panelGrid id="contractListPanelGrid" binding="#{contractListPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр (#{contractListPage.filter.status})" switchType="client"
                            opened="false" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Номер" styleClass="output-text" />
                <h:inputText value="#{contractListPage.filter.contractNum}" styleClass="input-text" />
                <h:outputText escape="true" value="Исполнитель" styleClass="output-text" />
                <h:inputText value="#{contractListPage.filter.performer}" styleClass="input-text" />
                <h:outputText escape="true" value="Заказчик" styleClass="output-text" />
                <h:inputText value="#{contractListPage.filter.customer}" styleClass="input-text" />
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{contractListPage.reload}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{contractListPage.resetFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="contractListTableGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="contractListTable" value="#{contractListPage.itemList}"
                    var="item" rows="20" footerClass="data-table-footer"
                    columnClasses="center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfContract}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер" />
            </f:facet>
            <a4j:commandLink value="#{item.contractNumber}" action="#{contractViewPage.show}" styleClass="command-link"
                             reRender="mainMenu, workspaceForm">
                <f:setPropertyActionListener value="#{item}" target="#{contractEditPage.selectedEntityGroupPage.currentEntityItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Исполнитель" />
            </f:facet>
            <h:outputText escape="true" value="#{item.performer}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Заказчик" />
            </f:facet>
            <h:outputText escape="true" value="#{item.customer}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contractStateAsString}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата заключения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.dateOfConclusion}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy"/>
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Срок действия" />
            </f:facet>
            <h:outputText escape="true" value="#{item.dateOfClosing}" styleClass="output-text" >
                <f:convertDateTime pattern="dd.MM.yyyy"/>
            </h:outputText>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" action="#{contractEditPage.show}" styleClass="command-link"
                                               reRender="mainMenu, workspaceForm">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item}" target="#{contractEditPage.selectedEntityGroupPage.currentEntityItem}" />
        </a4j:commandLink>
        </rich:column>

        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             reRender="uvDeleteConfirmPanel"
                             action="#{uvDeletePage.show}"
                             oncomplete="#{rich:component('uvDeleteConfirmPanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{uvDeletePage.currentEntityItem}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="contractListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>