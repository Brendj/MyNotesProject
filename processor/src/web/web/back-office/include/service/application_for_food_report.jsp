<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="applicationForFoodReportPage" type="ru.axetta.ecafe.processor.web.ui.service.ApplicationForFoodReportPage"--%>
<h:panelGrid id="applicationForFoodPanelGrid" binding="#{applicationForFoodReportPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:modalPanel id="applicationForFoodMessagePanel" autosized="true" minWidth="400">
        <f:facet name="header">
            <h:outputText value="История изменений записи" />
        </f:facet>
        <%--<h:inputTextarea value="#{applicationForFoodReportPage.remarksToShow}" cols="80" rows="10" id="ta_remarks_toshow" readonly="true" />--%>
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('applicationForFoodMessagePanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{applicationForFoodReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{applicationForFoodReportPage.filter}}" />
        </h:panelGroup>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Сформировать" action="#{applicationForFoodReportPage.reload}"
                           reRender="applicationForFoodPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="applicationForFoodTable" value="#{applicationForFoodReportPage.items}" var="item" rows="25"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Номер заявления" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата заявления" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата статуса" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Л/с обучающегося" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ФИО обучающегося" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Ид ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Название ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Льгота" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Управление" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.serviceNumber}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.createdDate}" styleClass="output-text" converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.applicationForFoodStateString}" styleClass="output-text" title="#{item.statusTitle}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text" converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.contractId}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.fio}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.benefit}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandLink reRender="applicationForFoodTable" rendered="#{item.isPaused}" value="Принесли документы"
                             action="#{applicationForFoodReportPage.makeResume()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <a4j:commandLink reRender="applicationForFoodTable" rendered="#{item.isResumed}" value="Решение положительное"
                             action="#{applicationForFoodReportPage.makeOK()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <h:outputText escape="false" value="<br/>" rendered="#{item.isResumed}" />
            <a4j:commandLink reRender="applicationForFoodTable" rendered="#{item.isResumed}" value="Решение отрицательное"
                             action="#{applicationForFoodReportPage.makeDenied()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="applicationForFoodTable" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Подтвердить" action="#{applicationForFoodReportPage.apply}"
                           reRender="applicationForFoodTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
