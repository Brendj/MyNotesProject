<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .mainBuilding {
        font-weight: bold;
    }

    .notServiced {
        background-color: #CECECE;
    }
</style>

<%--@elvariable id="orgSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSettingsReportPage"--%>
<h:panelGrid id="orgSettingsReportPanelGrid" binding="#{orgSettingsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{orgSettingsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{orgSettingsReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Статус" />
        <h:selectOneMenu id="orgStatus"
                         value="#{orgSettingsReportPage.status}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSettingsReportPage.statuses}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Округ" />
        <h:selectOneMenu id="orgDistricts"
                         value="#{orgSettingsReportPage.selectedDistricts}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSettingsReportPage.listOfOrgDistricts}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО" />
        <h:selectBooleanCheckbox value="#{orgSettingsReportPage.allFriendlyOrgs}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отобразить реквизиты" />
        <h:selectBooleanCheckbox value="#{orgSettingsReportPage.showRequisite}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отобразить настройки питания" />
        <h:selectBooleanCheckbox value="#{orgSettingsReportPage.showFeedingSettings}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отобразить настройки карт" />
        <h:selectBooleanCheckbox value="#{orgSettingsReportPage.showCardSettings}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Отобразить другие настройки" />
        <h:selectBooleanCheckbox value="#{orgSettingsReportPage.showOtherSetting}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="orgSettingsTable" />
        </h:selectBooleanCheckbox>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{orgSettingsReportPage.buildHTML}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{orgSettingsReportPage.buildXLS}"
                         styleClass="command-button"  id="buildXLSButton" disabled="false">
            <a4j:support status="reportGenerateStatus" id="buildXLSButtonSupport"/>
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:dataTable id="orgSettingsTable" value="#{orgSettingsReportPage.items}" var="item" rows="25"
                    sortMode="single"
                    rowClasses="center-aligned-column" lang="rus"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <h:outputText escape="true" value="Отчет по образовательным комплексам" />
        </f:facet>
        <!-- main info -->
        <!--id="orgSettingsMainInfoPart"-->
        <rich:column sortable="true" sortBy="#{item.orgNumberInName}" headerClass="column-header" styleClass="#{item.style}" label="Номер">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgNumberInName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header" styleClass="#{item.style}" label="ID OO">
            <f:facet name="header">
                <h:outputText escape="true" value="ID ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortName}" headerClass="column-header" styleClass="#{item.style}" label="Название">
            <f:facet name="header">
                <h:outputText escape="true" value="Название ПП" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.officialName}" headerClass="column-header" styleClass="#{item.style}" label="Название" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="Полное название" />
            </f:facet>
            <h:outputText escape="true" value="#{item.officialName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortNameInfoService}" headerClass="column-header" styleClass="#{item.style}" label="Название" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.district}" headerClass="column-header" styleClass="#{item.style}" label="Округ">
            <f:facet name="header">
                <h:outputText escape="true" value="Округ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header" styleClass="#{item.style}" label="Адрес">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortAddress}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.type}" headerClass="column-header" styleClass="#{item.style}" label="Тип ОУ">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип ОУ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.type}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.status}" headerClass="column-header" styleClass="#{item.style}" label="Статус">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.status}" styleClass="output-text" />
        </rich:column>
        <!-- Requisites -->
        <!--id="orgSettingsRequisitesInfoPart" rendered="orgSettingsReportPage.showRequisite"-->
        <rich:column sortable="true" sortBy="#{item.GUID}" headerClass="column-header" styleClass="#{item.style}" label="GUID" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID" />
            </f:facet>
            <h:outputText escape="true" value="#{item.GUID}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.additionalIdBuilding}" headerClass="column-header" rendered="#{orgSettingsReportPage.showRequisite}"
                     styleClass="#{item.style}" label="Доп.ид здания">
            <f:facet name="header">
                <h:outputText escape="true" value="Доп.ид здания" />
            </f:facet>
            <h:outputText escape="true" value="#{item.additionalIdBuilding}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.btiUnom}" headerClass="column-header" styleClass="#{item.style}" label="УНОМ" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="УНОМ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.btiUnom}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.btiUnad}" headerClass="column-header" styleClass="#{item.style}" label="УНАД" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="УНАД" />
            </f:facet>
            <h:outputText escape="true" value="#{item.btiUnad}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.INN}" headerClass="column-header" styleClass="#{item.style}" label="ИНН" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="ИНН" />
            </f:facet>
            <h:outputText escape="true" value="#{item.INN}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.typeInternal}" headerClass="column-header" rendered="#{orgSettingsReportPage.showRequisite}"
                     styleClass="#{item.style}" label="Тип ОУ при внедрении">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип ОУ при внедрении" />
            </f:facet>
            <h:outputText escape="true" value="#{item.typeInternal}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Номер версии АРМа" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер версии АРМа" />
            </f:facet>
            <h:outputText escape="true" value="#{item.armVersionNumber}" styleClass="output-text" />
        </rich:column>
        <!-- id="orgSettingsSupplierInfoPart"-->
        <rich:column sortable="true" sortBy="#{item.defaultSupplierName}" headerClass="column-header" rendered="#{orgSettingsReportPage.showRequisite}"
                     styleClass="#{item.style}" label="Поставщик питания">
            <f:facet name="header">
                <h:outputText escape="true" value="Поставщик питания" />
            </f:facet>
            <h:outputText escape="true" value="#{item.defaultSupplierName}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.productionConfig}" headerClass="column-header" rendered="#{orgSettingsReportPage.showRequisite}"
                     styleClass="#{item.style}" label="Производственная конфигурация">
            <f:facet name="header">
                <h:outputText escape="true" value="Производственная конфигурация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.productionConfig}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.orgCategory}" headerClass="column-header" styleClass="#{item.style}" label="Категории ОУ" rendered="#{orgSettingsReportPage.showRequisite}">
            <f:facet name="header">
                <h:outputText escape="true" value="Категории ОУ" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgCategory}" styleClass="output-text" />
        </rich:column>
        <!-- Feeding Settings -->
        <!--id="orgSettingsFeedingPart" rendered="orgSettingsReportPage.showFeedingSettings"-->
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Абонементное питание" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Абонементное питание" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.usePaydableSubscriptionFeeding}"
                                     styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Вариатвиное питание" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Вариатвиное питание" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.variableFeeding}"
                                     styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Предзаказ" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Предзаказ" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.preordersEnabled}" styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Оплата/сторнирование месяц продажи" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Оплата/сторнирование месяц продажи + 5 дней следующего" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.reverseMonthOfSale}" disabled="false"
                                     styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Контроль расхождения времени оплаты" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Контроль расхождения времени оплаты" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.denyPayPlanForTimeDifference}"
                                     styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Возможность ухода в минус"
                     rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <f:facet name="header">
                <h:outputText escape="true"
                              value="Возможность ухода в минус при оплате Платного плана / Абонементного питания" />
            </f:facet>
            <h:panelGrid columnClasses="center-aligned-column" columns="1" rendered="#{item.idOfSetting != -1}" styleClass="center-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.settingName}"
                                 action="#{mainPage.showFeedingSettingEditPage}"
                                 styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.idOfSetting}"
                                                 target="#{mainPage.feedingSettingEditPage.idOfSetting}" />
                    <f:setPropertyActionListener value="#{item.idOfSetting}"
                                                 target="#{mainPage.selectedIdOfFeedingSetting}" />
                </a4j:commandLink>
                <h:outputText escape="true" value="#{item.limit}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </h:panelGrid>
            <h:panelGrid columnClasses="center-aligned-column" columns="1" rendered="#{item.idOfSetting == -1}">
                <h:outputText escape="true" styleClass="output-text" value="#{item.settingName}" />
            </h:panelGrid>
        </rich:column>
        <!-- Cards Settings -->
        <!--id="orgSettingsCardPart" rendered="orgSettingsReportPage.showCardSettings"-->
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Запрет на выдачу временной карты" rendered="#{orgSettingsReportPage.showCardSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Запрет на выдачу временной карты" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.oneActiveCard}" styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Дубликаты для основных карт" rendered="#{orgSettingsReportPage.showCardSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Дубликаты для основных карт" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.enableDuplicateCard}" styleClass="checkboxes" disabled="false">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Несколько активных карт в ОО" rendered="#{orgSettingsReportPage.showCardSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Несколько активных карт в ОО" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.multiCardModeEnabled}" styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="ЭЦП для карт" rendered="#{orgSettingsReportPage.showCardSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="ЭЦП для карт" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.needVerifyCardSign}" styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <!-- Other Settings -->
        <!--id="orgSettingsOtherPart" rendered="orgSettingsReportPage.showOtherSetting"-->
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Заявки на посещение других ОО" rendered="#{orgSettingsReportPage.showOtherSetting}">
            <f:facet name="header">
                <h:outputText escape="true" value="Заявки на посещение других ОО" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.requestForVisitsToOtherOrg}"
                                     styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.style}" label="Режим \"Летний период\"" rendered="#{orgSettingsReportPage.showOtherSetting}">
            <f:facet name="header">
                <h:outputText escape="true" value="Режим \"Летний период\"" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.isWorkInSummerTime}" styleClass="checkboxes">
                <a4j:support event="onchange" action="#{item.isChangedWhenModify()}" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="orgSettingsTable" renderIfSinglePage="false"
                               maxPages="15" fastControls="hide" stepControls="auto"
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
        <a4j:commandButton value="Применить изменения" action="#{orgSettingsReportPage.applyChanges}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyChangesButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
