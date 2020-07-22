<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
    .mainBuilding {
        font-weight: bold;
    }

    .notServiced {
        background-color: #CECECE;
    }
</style>

<%--@elvariable id="hardwareSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.HardwareSettingsReportPage"--%>
<h:panelGrid id="hardwareSettingsReportPage" binding="#{hardwareSettingsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{hardwareSettingsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{hardwareSettingsReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Округ" />
        <h:selectOneMenu id="orgDistricts" value="#{hardwareSettingsReportPage.selectedDistricts}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{hardwareSettingsReportPage.listOfOrgDistricts}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.allFriendlyOrgs}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="hardwareSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отображать данные АРМ администратора ОО" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showAdministrator}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true"
                      value="Отображать данные АРМ оператора питания (кассира)" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showCashier}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отображать данные АРМ контролера входа" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showGuard}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отображать Инфопанель" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showInfo}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отображать параметры турникетов" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showTurnstile}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>

    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{hardwareSettingsReportPage.buildHTML}"
                           reRender="hardwareSettingsTable" styleClass="command-button" status="reportGenerateStatus"
                           id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{hardwareSettingsReportPage.buildXLS}"
                         styleClass="command-button" id="buildXLSButton" disabled="false">
            <a4j:support status="reportGenerateStatus" id="buildXLSButtonSupport" />
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText styleClass="output-text" escape="true" value="Отчет по оборудованию" />
        <rich:dataTable id="hardwareSettingsTable" value="#{hardwareSettingsReportPage.items}" var="item"
                        rowKeyVar="row" rows="20" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column,left-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" colspan="7">
                        <h:outputText styleClass="column-header" escape="true" value="Данные ОО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="17">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Параметры" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column sortable="true" sortBy="#{item.orgNumberInName}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Номер">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер" />
                </f:facet>
                <h:outputText value="#{item.orgNumberInName}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header"
                         styleClass="center-aligned-column" label="ID OO">
                <f:facet name="header">
                    <h:outputText escape="true" value="ID OO" />
                </f:facet>
                <h:outputText value="#{item.idOfOrg}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.shortName}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Название ПП">
                <f:facet name="header">
                    <h:outputText escape="false" value="Название<br/> ПП" />
                </f:facet>
                <h:outputText value="#{item.shortName}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.shortNameInfoService}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Название ОО краткое">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Название ОО<br/> краткое" />
                </f:facet>
                <h:outputText value="#{item.shortNameInfoService}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.district}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Округ">
                <f:facet name="header">
                    <h:outputText escape="true" value="Округ" />
                </f:facet>
                <h:outputText value="#{item.district}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Краткий адрес">
                <f:facet name="header">
                    <h:outputText escape="false" value="Краткий<br/> адрес" />
                </f:facet>
                <h:outputText value="#{item.shortAddress}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.typeOU}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Тип ОУ">
                <f:facet name="header">
                    <h:outputText escape="true" value="Тип ОУ" />
                </f:facet>
                <h:outputText value="#{item.typeOU}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.moduleType}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Модуль">
                <f:facet name="header">
                    <h:outputText escape="true" value="Модуль" />
                </f:facet>
                <h:outputText value="#{item.moduleType}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.clientVersion}" headerClass="column-header" label="Версия ПО" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Версия<br/> ПО" />
                </f:facet>
                <h:outputText escape="false" value="#{item.clientVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dataBaseSize}" headerClass="column-header" label="Размер БД(Mb)" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Размер<br/> БД(Mb)" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dataBaseSize}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.remoteAddress}" headerClass="column-header" label="ip ПК" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip ПК" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddress}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.lastUpdate}" headerClass="column-header "
                         label="Последнее изменение" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Последнее<br/> изменение"/>
                </f:facet>
                <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.readerName}" headerClass="column-header"
                         label="Идентификатор/модель считывателей ЭИ" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Идентификатор/модель<br/> считывателей ЭИ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.readerName}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.firmwareVersion}" headerClass="column-header"
                         label="Версия микропрограммы считывателя ЭИ" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Версия микропрограммы<br/> считывателя ЭИ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.firmwareVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.osVersion}" headerClass="column-header" label="Версия ОС" styleClass="center-aligned-column">
                <f:facet name="header" >
                    <h:outputText escape="true" value="Версия ОС" />
                </f:facet>
                <h:outputText escape="true" value="#{item.osVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.sqlVersion}" headerClass="column-header" label="Версия MySQL" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dotNetVersion}" headerClass="column-header"
                         label="Версия Net Framework" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Версия Net<br/> Framework" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dotNetVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.cpuVersion}" headerClass="column-header" label="Процессор" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="true" value="Процессор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.cpuVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.ramSize}" headerClass="column-header" label="Размер ОЗУ" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="true" value="Размер ОЗУ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.ramSize}" styleClass="output-text" />
            </rich:column>
            <%----%>
            <rich:column sortable="true" sortBy="#{item.turnstileId}" headerClass="column-header" label="ip/mac турникета" styleClass="center-aligned-column">
                <f:facet name="header" >
                    <h:outputText escape="true" value="ip/mac турникета" />
                </f:facet>
                <h:outputText escape="true" value="#{item.turnstileId}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.numOfEntries}" headerClass="column-header"
                         label="Количество входных групп" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Количество<br/> входных групп" />
                </f:facet>
                <h:outputText escape="true" value="#{item.numOfEntries}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.controllerModel}" headerClass="column-header"
                         label="Модели контроллера" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText escape="false" value="Модели<br/> контроллера" />
                </f:facet>
                <h:outputText escape="true" value="#{item.controllerModel}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.controllerFirmwareVersion}" headerClass="column-header"
                         label="Версия прошивки контроллера" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Версия<br/> прошивки<br/> контроллера" />
                </f:facet>
                <h:outputText escape="false" value="#{item.controllerFirmwareVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.isWorkWithLongIds}" headerClass="column-header"
                         label="Считыватель  читает длинные идентификаторы" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Считыватель<br/> читает длинные<br/> идентификаторы" />
                </f:facet>
                <h:outputText escape="true" value="#{item.isWorkWithLongIds}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.timeCoefficient}" headerClass="column-header"
                         label="Коэффициент коррекции времени турникета" styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText styleClass="column-header" escape="false" value="Коэффициент<br/> коррекции<br/> времени<br/> турникета" />
                </f:facet>
                <h:outputText escape="false" value="#{item.timeCoefficient}" styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="hardwareSettingsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
