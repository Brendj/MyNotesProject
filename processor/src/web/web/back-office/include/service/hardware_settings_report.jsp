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
                    <rich:column headerClass="center-aligned-column" colspan="10">
                        <h:outputText styleClass="column-header" escape="true" value="ПК. АРМ администратора ОУ" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="8">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="ПК. АРМ оператора питания(кассира)" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="8">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="ПК. АРМ контролера входа(охранника)" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="6">
                        <h:outputText styleClass="column-header" escape="true" value="Параметры турникетов" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" breakBefore="true">
                        <h:outputText styleClass="column-header" escape="true" value="Номер" />
                    </rich:column>
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
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Версия ПО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Размер БД" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--оператор питания(кассир)--%>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--контроллер входа(охранник)--%>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Идентификатор/модель считывателей карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Версия микропрограммы считывателя карт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОС" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="MySQL" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Net Framework" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Процессор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ОЗУ" />
                    </rich:column>
                    <%--турникеты--%>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="ip" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество входных групп" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество контроллеров" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Модели контроллера" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Версия прошивки контроллера" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true"
                                      value="Считыватель читает длинные идентификаторы" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
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
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.clientVersion}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.dataBaseSize}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.remoteAddressOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.readerNameOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.firmwareVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.osVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.sqlVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.dotNetVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.cpuVersionOU}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.ramSizeOU}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.remoteAddressFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.readerNameFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.firmwareVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.osVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.sqlVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.dotNetVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.cpuVersionFeeding}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.ramSizeFeeding}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.remoteAddressGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.readerNameGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.firmwareVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.osVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.sqlVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.dotNetVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.cpuVersionGuard}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.ramSizeGuard}" />
            </rich:column>
            <%----%>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.turnstileId}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.numOfEntries}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.controllerModel}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.controllerFirmwareVersion}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{item.isWorkWithLongIds}" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>


    <%--<rich:dataTable id="hardwareSettingsTable" value="#{hardwareSettingsReportPage.items}" var="item" rows="25"--%>
    <%--sortMode="single"--%>
    <%--rowClasses="center-aligned-column" lang="rus"--%>
    <%--footerClass="data-table-footer">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Отчет по оборудованию" />--%>
    <%--</f:facet>--%>
    <%--<!-- Данные ОО -->--%>
    <%--<!--id="orgSettingsMainInfoPart"-->--%>
    <%--<rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header"  label="Номер">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Номер" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.orgNumberInName}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header"  label="ID OO">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ID OO" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.shortName}" headerClass="column-header"  label="Название ПП">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Название ПП" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.shortNameInfoService}" headerClass="column-header"  label="Название ОО краткое">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Навзание ОО краткое" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.district}" headerClass="column-header"  label="Округ">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Округ" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.district}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.type}" headerClass="column-header"  label="Тип ОУ">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Тип ОУ" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.type}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--&lt;%&ndash;данные ОО&ndash;%&gt;--%>
    <%--&lt;%&ndash;ПК. АРМ Администратора&ndash;%&gt;--%>
    <%--<rich:column sortable="true" sortBy="#{item.clientVersion}" headerClass="column-header"  label="Версия ПО">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Версия ПО" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.clientVersion}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.dataBaseSize}" headerClass="column-header"  label="Размер БД">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Размер БД" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.dataBaseSize}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.remoteAddressOU}" headerClass="column-header" label="ip">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ip" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.remoteAddressOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.readerNameOU}" headerClass="column-header"  label="Идентификатор/модель считывателей карт">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Идентификатор/модель считывателей карт" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.readerNameOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.osVersionOU}" headerClass="column-header"  label="ОС">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ОС" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.osVersionOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.sqlVersionOU}" headerClass="column-header"  label="MySQL">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="MySQL" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.sqlVersionOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.dotNetVersionOU}" headerClass="column-header"  label="Net Framework">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Net Framework" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.dotNetVersionOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.cpuVersionOU}" headerClass="column-header"  label="Процессор">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Процессор" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.cpuVersionOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.ramSizeOU}" headerClass="column-header"  label="ОЗУ">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ОЗУ" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.ramSizeOU}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--&lt;%&ndash;ПК. АРМ Администратора&ndash;%&gt;--%>
    <%--&lt;%&ndash;ПК. АРМ оператора питания(кассира)&ndash;%&gt;--%>
    <%--<rich:column sortable="true" sortBy="#{item.remoteAddressFeeding}" headerClass="column-header" label="ip">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ip" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.remoteAddressFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.readerNameFeeding}" headerClass="column-header"  label="Идентификатор/модель считывателей карт">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Идентификатор/модель считывателей карт" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.readerNameFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.osVersionFeeding}" headerClass="column-header"  label="ОС">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ОС" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.osVersionFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.sqlVersionFeeding}" headerClass="column-header"  label="MySQL">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="MySQL" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.sqlVersionFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.dotNetVersionFeeding}" headerClass="column-header"  label="Net Framework">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Net Framework" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.dotNetVersionFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.cpuVersionFeeding}" headerClass="column-header"  label="Процессор">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="Процессор" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.cpuVersionFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--<rich:column sortable="true" sortBy="#{item.ramSizeFeeding}" headerClass="column-header"  label="ОЗУ">--%>
    <%--<f:facet name="header">--%>
    <%--<h:outputText escape="true" value="ОЗУ" />--%>
    <%--</f:facet>--%>
    <%--<h:outputText escape="true" value="#{item.ramSizeFeeding}" styleClass="output-text" />--%>
    <%--</rich:column>--%>
    <%--&lt;%&ndash;ПК. АРМ оператора питания(кассира)&ndash;%&gt;--%>
    <%--&lt;%&ndash;предпоследняя строка&ndash;%&gt;--%>
    <%--</rich:dataTable>--%>


    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
