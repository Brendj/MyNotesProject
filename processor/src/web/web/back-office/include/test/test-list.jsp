<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<h:panelGrid id="testListPanelGrid" binding="#{mainPage.testTestListPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Фильтр (#{mainPage.testTestListPage.filter.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Имя контрагента" styleClass="output-text" />

            <h:inputText value="#{mainPage.testTestListPage.filter.contragentName}" styleClass="input-text" />
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Применить" action="#{mainPage.updateTestPage}" reRender="testListPanelGrid"
                               styleClass="command-button" />

            <a4j:commandButton value="Очистить" action="#{mainPage.clearTestListPageFilter}"
                               reRender="testListPanelGrid" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:dataTable id="testTable" value="#{mainPage.testTestListPage.items}" var="item" rows="20"
                    footerClass="data-table-footer"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
        <rich:columns var="property" value="#{mainPage.testTestListPage.narrowProperties}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="#{property.name}" escape="true" />
            </f:facet>
            <h:outputText escape="true" value="#{item.narrowFields[property.fieldName]}" styleClass="output-text" />
        </rich:columns>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Просмотр" />
            </f:facet>
            <h:commandLink action="#{mainPage.showTestViewPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.id}" target="#{mainPage.selectedTestId}" />
            </h:commandLink>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <h:commandLink action="#{testBean.showEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.id}" target="#{mainPage.selectedTestId}" />
            </h:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="testTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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