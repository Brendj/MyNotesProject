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
                    <rich:column headerClass="center-aligned-column" colspan="6"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Параметры турникетов" />
                    </rich:column>
                    <%--<rich:column  headerClass="center-aligned-column" breakBefore="true">--%>
                        <%--<h:outputText styleClass="column-header" escape="true" value="Номер" />--%>
                    <%--</rich:column>--%>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ID OO" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Название ПП" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Название ОО краткое" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Округ" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Краткий адрес" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Тип ОУ" />
                    </rich:column>
                    <%--Администратор ОУ--%>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="Версия ПО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="Размер БД" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showAdministrator}">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--оператор питания(кассир)--%>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showCashier}">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--контроллер входа(охранник)--%>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--турникеты--%>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Количество входных групп" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Количество контроллеров" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Модели контроллера" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true" value="Версия прошивки контроллера" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column"
                                 rendered="#{hardwareSettingsReportPage.showTurnstile}">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Считыватель читает длинные идентификаторы" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column sortable="true" sortBy="#{item.orgNumberInName}" headerClass="column-header" styleClass="center-aligned-column" label="Номер">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер"/>
                </f:facet>
                <h:outputText value="#{item.orgNumberInName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.idOfOrg}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.shortName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.shortNameInfoService}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.district}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.shortAddress}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.type}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.clientVersion}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.dataBaseSize}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.remoteAddressOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.readerNameOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.firmwareVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.osVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.sqlVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.dotNetVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.cpuVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showAdministrator}">
                <h:outputText value="#{item.ramSizeOU}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.remoteAddressFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.readerNameFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.firmwareVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.osVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.sqlVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.dotNetVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.cpuVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showCashier}">
                <h:outputText value="#{item.ramSizeFeeding}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.remoteAddressGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.readerNameGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.firmwareVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.osVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.sqlVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.dotNetVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.cpuVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showGuard}">
                <h:outputText value="#{item.ramSizeGuard}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.turnstileId}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.numOfEntries}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.numOfTurnstile}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.controllerModel}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.controllerFirmwareVersion}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rendered="#{hardwareSettingsReportPage.showTurnstile}">
                <h:outputText value="#{item.isWorkWithLongIds}" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
