<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="aggregateGoodRequestReportPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.AggregateGoodRequestReportPage"--%>
<h:panelGrid id="aggregateGoodRequestReportPanelGrid" binding="#{aggregateGoodRequestReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid id="goodRequestReportParamPanelGrid" styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{aggregateGoodRequestReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar value="#{aggregateGoodRequestReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
            </a4j:commandButton>

            <h:outputText styleClass="output-text" escape="true" value=" {#{aggregateGoodRequestReportPage.filter}}" />
        </h:panelGrid>
        <a4j:commandButton value="Генерировать отчет" action="#{aggregateGoodRequestReportPage.buildReport}"
                           reRender="mainMenu, workspaceTogglePanel, aggregateGoodRequestTable"
                           styleClass="command-button" status="sReportGenerateStatus" />
        <a4j:status id="sReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-div">
        <rich:dataTable id="aggregateGoodRequestTable" var="itemGroups"
                        value="#{aggregateGoodRequestReportPage.aggregateGoodRequestReport.itemGroupsList}"
                        rowKeyVar="row" rows="1"
                        footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column colspan="2">
                        <h:outputText styleClass="column-header" value="Поставщик"/>
                    </rich:column>
                    <rich:column colspan="6">
                        <h:outputText styleClass="column-header" value="Заявки"/>
                    </rich:column>
                    <rich:column breakBefore="true">
                        <h:outputText styleClass="column-header" value="ID"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Название поставщика"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Товар/продукт"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Общее количество заказов"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="ID"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Название учреждения"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Кол-во заказов"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText styleClass="column-header" value="Дата к исполнению"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>

            <rich:subTable id="aggregateGoodRequestSubTable" var="items"
                    value="#{itemGroups.itemsList}"
                    rowKeyVar="subTableRow">
                <rich:column rowspan="#{items.productDetails.supplierDetails.rowCount}"
                             rendered="#{items.productDetails.supplierDetails.rendered(subTableRow)}">
                    <h:outputText value="#{items.productDetails.supplierDetails.idOfSupplier}" escape="true"/>
                </rich:column>
                <rich:column rowspan="#{items.productDetails.supplierDetails.rowCount}"
                             rendered="#{items.productDetails.supplierDetails.rendered(subTableRow)}" styleClass="valign">
                    <h:outputText value="#{items.productDetails.supplierDetails.nameOfSupplier}" escape="true"/>
                </rich:column>
                <rich:column rowspan="#{items.productDetails.rowCount}"
                             rendered="#{items.productDetails.rendered(subTableRow)}">
                    <h:outputText value="#{items.productDetails.nameOfProduct}" escape="true"/>
                </rich:column>
                <rich:column rowspan="#{items.productDetails.rowCount}"
                             rendered="#{items.productDetails.rendered(subTableRow)}">
                    <h:outputText value="#{items.productDetails.totalCount}" escape="true"/>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{items.idOfOrg}" escape="true"/>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{items.nameOfOrg}" escape="true"/>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{items.productCount / 1000}" escape="true">
                        <f:convertNumber pattern="#0"/>
                    </h:outputText>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{items.dateOfExecutionFormatted}" escape="true"/>
                </rich:column>
            </rich:subTable>

            <rich:column colspan="9">
                <rich:spacer/>
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="aggregateGoodRequestTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
