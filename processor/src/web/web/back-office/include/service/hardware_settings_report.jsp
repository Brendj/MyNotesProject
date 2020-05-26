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
        <%--<h:outputText styleClass="output-text" escape="true" value="Статус" />--%>
        <%--<h:selectOneMenu id="orgStatus"--%>
        <%--value="#{hardwareSettingsReportPage.status}"--%>
        <%--styleClass="input-text" style="width: 250px;">--%>
        <%--<f:selectItems value="#{hardwareSettingsReportPage.statuses}" />--%>
        <%--<a4j:support event="onchange" />--%>
        <%--</h:selectOneMenu>--%>
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
        <h:outputText styleClass="output-text" escape="true" value="Отображать данные АРМ Администратора ОО" />
        <h:selectBooleanCheckbox value="#{hardwareSettingsReportPage.showAdministrator}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true"
                      value="Отображать данные АРМ Оператора Питания (кассира)" />
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
                        rowKeyVar="row" rows="44" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column,left-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" colspan="7">
                        <h:outputText styleClass="column-header" escape="true" value="Данные ОО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="10"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="ПК. АРМ администратора ОУ" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="8"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="ПК. АРМ оператора питания(кассира)" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="8"
                                 rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="ПК. АРМ контролера входа(охранника)" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="8"
                                 rendered="#{hardwareSettingsReportPage.showInfo}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Инфопанель" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="7"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Параметры турникетов" />
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
                    <h:outputText escape="true" value="Название ПП" />
                </f:facet>
                <h:outputText value="#{item.shortName}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.shortNameInfoService}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Название ОО краткое">
                <f:facet name="header">
                    <h:outputText escape="true" value="Название ОО краткое" />
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
                    <h:outputText escape="true" value="Краткий адрес" />
                </f:facet>
                <h:outputText value="#{item.shortAddress}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.type}" headerClass="column-header"
                         styleClass="center-aligned-column" label="Тип ОУ">
                <f:facet name="header">
                    <h:outputText escape="true" value="Тип ОУ" />
                </f:facet>
                <h:outputText value="#{item.type}" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.clientVersion}" headerClass="column-header" label="Версия ПО"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия ПО" />
                </f:facet>
                <h:outputText escape="true" value="#{item.clientVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dataBaseSize}" headerClass="column-header" label="Размер БД"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Размер БД" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dataBaseSize}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.remoteAddressOU}" headerClass="column-header" label="ip"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddressOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.readerNameOU}" headerClass="column-header" label="Идентификатор/модель считывателей карт"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор/модель считывателей карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.readerNameOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.firmwareVersionOU}" headerClass="column-header" label="Версия микропрограммы считывателя карт"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия микропрограммы считывателя карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.firmwareVersionOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.osVersionOU}" headerClass="column-header" label="OC"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="OC" />
                </f:facet>
                <h:outputText escape="true" value="#{item.osVersionOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.sqlVersionOU}" headerClass="column-header" label="MySQL"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlVersionOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dotNetVersionOU}" headerClass="column-header" label="Net Framework"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Net Framework" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dotNetVersionOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.cpuVersionOU}" headerClass="column-header" label="Процессор"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Процессор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.cpuVersionOU}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.ramSizeOU}" headerClass="column-header" label="ОЗУ"
                         rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ОЗУ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.ramSizeOU}" styleClass="output-text" />
            </rich:column>
            <%----%>
            <rich:column sortable="true" sortBy="#{item.remoteAddressFeeding}" headerClass="column-header" label="ip"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddressFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.readerNameFeeding}" headerClass="column-header" label="Идентификатор/модель считывателей карт"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор/модель считывателей карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.readerNameFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.firmwareVersionFeeding}" headerClass="column-header" label="Версия микропрограммы считывателя карт"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия микропрограммы считывателя карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.firmwareVersionFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.osVersionFeeding}" headerClass="column-header" label="OC"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="OC" />
                </f:facet>
                <h:outputText escape="true" value="#{item.osVersionFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.sqlVersionFeeding}" headerClass="column-header" label="MySQL"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlVersionFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dotNetVersionFeeding}" headerClass="column-header" label="Net Framework"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Net Framework" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dotNetVersionFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.cpuVersionFeeding}" headerClass="column-header" label="Процессор"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Процессор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.cpuVersionFeeding}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.ramSizeFeeding}" headerClass="column-header" label="ОЗУ"
                         rendered="#{hardwareSettingsReportPage.showCashier}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ОЗУ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.ramSizeFeeding}" styleClass="output-text" />
            </rich:column>
            <%----%>
            <rich:column sortable="true" sortBy="#{item.remoteAddressGuard}" headerClass="column-header" label="ip"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddressGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.readerNameGuard}" headerClass="column-header" label="Идентификатор/модель считывателей карт"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор/модель считывателей карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.readerNameGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.firmwareVersionGuard}" headerClass="column-header" label="Версия микропрограммы считывателя карт"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия микропрограммы считывателя карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.firmwareVersionGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.osVersionGuard}" headerClass="column-header" label="OC"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="OC" />
                </f:facet>
                <h:outputText escape="true" value="#{item.osVersionGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.sqlVersionGuard}" headerClass="column-header" label="MySQL"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlVersionGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dotNetVersionGuard}" headerClass="column-header" label="Net Framework"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Net Framework" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dotNetVersionGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.cpuVersionGuard}" headerClass="column-header" label="Процессор"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Процессор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.cpuVersionGuard}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.ramSizeGuard}" headerClass="column-header" label="ОЗУ"
                         rendered="#{hardwareSettingsReportPage.showGuard}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ОЗУ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.ramSizeGuard}" styleClass="output-text" />
            </rich:column>

            <%----%>
            <rich:column sortable="true" sortBy="#{item.remoteAddressInfo}" headerClass="column-header" label="ip"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddressInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.readerNameInfo}" headerClass="column-header" label="Идентификатор/модель считывателей карт"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор/модель считывателей карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.readerNameInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.firmwareVersionInfo}" headerClass="column-header" label="Версия микропрограммы считывателя карт"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия микропрограммы считывателя карт" />
                </f:facet>
                <h:outputText escape="true" value="#{item.firmwareVersionInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.osVersionInfo}" headerClass="column-header" label="OC"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="OC" />
                </f:facet>
                <h:outputText escape="true" value="#{item.osVersionInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.sqlVersionInfo}" headerClass="column-header" label="MySQL"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="MySQL" />
                </f:facet>
                <h:outputText escape="true" value="#{item.sqlVersionInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.dotNetVersionInfo}" headerClass="column-header" label="Net Framework"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Net Framework" />
                </f:facet>
                <h:outputText escape="true" value="#{item.dotNetVersionInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.cpuVersionInfo}" headerClass="column-header" label="Процессор"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Процессор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.cpuVersionInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.ramSizeInfo}" headerClass="column-header" label="ОЗУ"
                         rendered="#{hardwareSettingsReportPage.showInfo}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ОЗУ" />
                </f:facet>
                <h:outputText escape="true" value="#{item.ramSizeInfo}" styleClass="output-text" />
            </rich:column>
            <%----%>
            <rich:column sortable="true" sortBy="#{item.turnstileId}" headerClass="column-header" label="ip"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="ip" />
                </f:facet>
                <h:outputText escape="true" value="#{item.turnstileId}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.numOfEntries}" headerClass="column-header" label="Количество входных групп"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Количество входных групп" />
                </f:facet>
                <h:outputText escape="true" value="#{item.numOfEntries}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.numOfTurnstile}" headerClass="column-header" label="Количество контроллеров"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Количество контроллеров" />
                </f:facet>
                <h:outputText escape="true" value="#{item.numOfTurnstile}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.controllerModel}" headerClass="column-header" label="Модели контроллера"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Модели контроллера" />
                </f:facet>
                <h:outputText escape="true" value="#{item.controllerModel}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.controllerFirmwareVersion}" headerClass="column-header" label="Версия прошивки контроллера"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия прошивки контроллера" />
                </f:facet>
                <h:outputText escape="true" value="#{item.controllerFirmwareVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.isWorkWithLongIds}" headerClass="column-header" label="Считыватель  читает длинные идентификаторы"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Считыватель  читает длинные идентификаторы" />
                </f:facet>
                <h:outputText escape="true" value="#{item.isWorkWithLongIds}" styleClass="output-text" />
            </rich:column>
            <rich:column sortable="true" sortBy="#{item.timeCoefficient}" headerClass="column-header" label="Коэффициент коррекции времени турникета"
                         rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Коэффициент коррекции времени турникета" />
                </f:facet>
                <h:outputText escape="true" value="#{item.timeCoefficient}" styleClass="output-text" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
