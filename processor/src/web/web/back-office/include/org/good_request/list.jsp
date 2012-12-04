<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="goodRequestListPage" type="ru.axetta.ecafe.processor.web.ui.org.goodRequest.GoodRequestListPage"--%>
<%--@elvariable id="goodRequestPositionListPage" type="ru.axetta.ecafe.processor.web.ui.org.goodRequest.goodRequestPosition.GoodRequestPositionListPage"--%>
<h:panelGrid id="goodRequestListPage" binding="#{goodRequestListPage.pageComponent}"
             styleClass="borderless-grid">
    <h:column>
        <rich:simpleTogglePanel label="Фильтр (#{goodRequestListPage.filter})" switchType="client"
                                eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">
            <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Базовая дата" styleClass="output-text" />
                <rich:calendar value="#{goodRequestListPage.baseDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

                <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
                <rich:calendar value="#{goodRequestListPage.endDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="Выводить удаленные заявки" styleClass="output-text" escape="true"/>
                    <h:selectBooleanCheckbox value="#{goodRequestListPage.useDeletedFilter}"/>
                </h:panelGroup>
                <h:selectOneMenu id="selectDeletedMenu" value="#{goodRequestListPage.deletedState}"
                                 converter="javax.faces.Boolean" styleClass="input-text" style="width: 250px;">
                    <f:selectItem itemValue="false" itemLabel="Не показывать"/>
                    <f:selectItem itemValue="true" itemLabel="Показывать"/>
                </h:selectOneMenu>

                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="Выводить только со статусами" styleClass="output-text" escape="true"/>
                    <h:selectBooleanCheckbox value="#{goodRequestListPage.useStateFilter}"/>
                </h:panelGroup>
                <h:selectManyListbox id="selectStateMenu" size="#{goodRequestListPage.statesListSize}"
                                     converter="javax.faces.Integer" value="#{goodRequestListPage.stateList}">
                    <f:selectItems value="#{goodRequestListPage.stateSelectItemList}"/>
                </h:selectManyListbox>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">

                <a4j:commandButton value="Применить" action="#{goodRequestListPage.onSearch}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />

                <a4j:commandButton value="Очистить" action="#{goodRequestListPage.onClear}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:column>

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="goodRequestListTable" width="700" var="goodRequest" value="#{goodRequestListPage.goodRequestList}" rendered="#{!goodRequestListPage.emptyGoodRequestList}"
                    rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row+1}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Версия" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodRequest.globalVersion}"
                             action="#{goodRequestPositionListPage.onSearch}" styleClass="command-link">
                <f:setPropertyActionListener value="#{goodRequest.globalId}" target="#{goodRequestPositionListPage.idOfGoodRequest}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата создания" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequest.createdDate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата последнего обновления" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequest.lastUpdate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Заявка отозвана" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{goodRequest.deletedState}" readonly="true" disabled="true"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата удаления" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequest.deleteDate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Состояние заявки" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequest.stateSelect}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата исполнения заявки" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequest.doneDate}" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="goodRequestListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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