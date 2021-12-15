<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
.region {
    font-weight: bold;
    background-color: #E3F6FF;
}
.overall {
    font-weight: bold;
    background-color: #D5E7F0;
}
</style>

<%--@elvariable id="statisticsPaymentPreferentialSupplyReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.StatisticsPaymentPreferentialSupplyReportPage"--%>
<h:panelGrid id="statisticsPaymentPreferentialSupplyReportPanelGrid"
             binding="#{statisticsPaymentPreferentialSupplyReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{statisticsPaymentPreferentialSupplyReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{statisticsPaymentPreferentialSupplyReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{statisticsPaymentPreferentialSupplyReportPage.contragentFilter.contragent.contragentName}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{statisticsPaymentPreferentialSupplyReportPage.showContractSelectPage}"
                               reRender="modalContragentSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2"
                                             target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{mainPage.enterEventReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="enterEventFilter" escape="true" value=" {#{mainPage.enterEventReportPage.filter}}" />
        </h:panelGroup>

        <a4j:commandButton value="Генерировать отчет" action="#{statisticsPaymentPreferentialSupplyReportPage.buildReport}"
                           reRender="workspaceTogglePanel, statisticsPaymentPreferentialSupplyReportPanelGrid"
                           styleClass="command-button" status="statisticsPaymentPreferentialSupplyReportGenerateStatus" />
        <a4j:status id="statisticsPaymentPreferentialSupplyReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="itemsReportTable"
            value="#{statisticsPaymentPreferentialSupplyReportPage.report.statisticsPaymentPreferentialSupplyItems}"
                        var="item" rowKeyVar="row" rows="20" footerClass="data-table-footer"
                        columnClasses="left-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column>
                        <h:outputText value="Наименование ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="№ ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Адрес ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Округ"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Текущая дата"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Число заказов"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Факт присутствия"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="% несоответствия"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column>
                <h:outputText value="#{item.shortName}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.number}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.address}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.district}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.paymentDate}" converter="dateConverter"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.orderedCount==null?0:item.orderedCount}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.actualPresenceCount==null?0:item.actualPresenceCount}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{(item.orderedCount==null || item.actualPresenceCount==null || item.orderedCount==0)?0:(item.orderedCount-item.actualPresenceCount)*100/item.orderedCount}">
                    <f:convertNumber pattern="#0.00"/>
                </h:outputText>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="itemsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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