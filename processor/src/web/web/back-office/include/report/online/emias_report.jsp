<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="blockunblockReportPanel" binding="#{mainPage.emiasReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.emiasReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Клиенты" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..."
                               action="#{mainPage.showClientSelectListPage(mainPage.emiasReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                    #{rich:component('modalClientListSelectorPanel')}.show();" styleClass="command-link"
                               style="width: 25px;" id="clientFilterButton">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{mainPage.emiasReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{mainPage.emiasReportPage.filterClient}}" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Дата получения от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.emiasReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.emiasReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.emiasReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.emiasReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.emiasReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата получения до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.emiasReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.emiasReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.emiasReportPage.allFriendlyOrgs}" styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.emiasReportPage.buildReportHTML}"
                           reRender="blockunblockReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.emiasReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid columns="1" columnClasses="valign, valign">
        <rich:dataTable id="emiasTable" value="#{mainPage.emiasReportPage.items}" var="item" rows="50"
                        footerClass="data-table-footer" columnClasses="center-aligned-column" reRender="lastOrgUpdateTime">

            <f:facet name="header">
                <rich:columnGroup columnClasses="gray">
                    <rich:column headerClass="column-header" rowspan="2" width="30px">
                        <h:outputText value="П/п" />
                    </rich:column>

                    <rich:column headerClass="column-header" colspan="3">
                        <h:outputText value="Данные ОО" />
                    </rich:column>

                    <rich:column headerClass="column-header" colspan="4">
                        <h:outputText value="Данные обучающегося" />
                    </rich:column>

                    <rich:column headerClass="column-header" colspan="6">
                        <h:outputText value="Данные события" />
                    </rich:column>

                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText value="Отметка о несогласии представителя" />
                    </rich:column>

                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText value="Принято к сведению ОО" />
                    </rich:column>

                    <rich:column headerClass="column-header" breakBefore="true">
                        <h:outputText value="ИД" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Название" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Адрес" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Группа" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="ФИО" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Лицевой счет" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Список льгот" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Дата получения" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Ид" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Начало отсутствия" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Окончание отсутствия" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Дата отмены" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Статус" />
                    </rich:column>

                </rich:columnGroup>
            </f:facet>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.recordID}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.orgID}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.orgAdress}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.clientGroup}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.lastname} #{item.firstname} #{item.middlename}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.contractID}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.benefits}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.emiasDate}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.emiasID}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.dateStartLiberation}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.dateEndLiberation}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.emiasDatearchived}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.status}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.acceptedDate}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.acceptedinOO}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="emiasTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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

