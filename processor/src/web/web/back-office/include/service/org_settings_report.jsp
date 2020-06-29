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
        <a4j:commandButton value="Генерировать отчет" action="#{orgSettingsReportPage.buildHTML()}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{orgSettingsReportPage.buildXLS()}"
                         styleClass="command-button"  id="buildXLSButton" disabled="false">
            <a4j:support status="reportGenerateStatus" id="buildXLSButtonSupport"/>
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
        <a4j:commandLink value="Отметить все записи к применению" action="#{orgSettingsReportPage.doMarkAll()}"
                         reRender="workspaceTogglePanel" styleClass="command-button" />
        <rich:spacer width="20px" />
        <a4j:commandLink value="Снять все записи c применения" action="#{orgSettingsReportPage.doUnmarkAll()}"
                         reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
    <rich:dataTable id="orgSettingsTable" value="#{orgSettingsReportPage.items}" var="item" rows="25"
                    sortMode="single"
                    rowClasses="center-aligned-column" lang="rus"
                    footerClass="data-table-footer"
                    headerClass="column-header gray">
        <f:facet name="header">
            <rich:columnGroup>
                <%--<h:outputText escape="true" value="Отчет по образовательным комплексам" />--%>
                <rich:column colspan="1">
                    <h:outputText escape="true" value="Прим." />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.orgNumberInName}" rowspan="2">
                    <h:outputText escape="true" value="Номер" />
                </rich:column>
                <rich:column sortable="trueщ" sortBy="#{item.idOfOrg}" rowspan="2">
                    <h:outputText escape="true" value="ID ОО" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.shortName}" rowspan="2">
                    <h:outputText escape="true" value="Название ПП" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.officialName}" rowspan="2">
                    <h:outputText escape="true" value="Полное название" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.shortNameInfoService}"  rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Наименование" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.district}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Округ" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.shortAddress}" rowspan="2">
                    <h:outputText escape="true" value="Адрес" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.type}" rowspan="2">
                    <h:outputText escape="true" value="Тип ОУ" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.status}" rowspan="2">
                    <h:outputText escape="true" value="Статус" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.GUID}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="GUID" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.additionalIdBuilding}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Доп.ид здания" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.btiUnom}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="УНОМ" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.btiUnad}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="УНАД" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.INN}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="ИНН" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.typeInternal}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Тип ОУ при внедрении" />
                </rich:column>
                <rich:column rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Номер версии АРМа" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.defaultSupplierName}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Поставщик питания" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.productionConfig}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Производственная конфигурация" />
                </rich:column>
                <rich:column sortable="true" sortBy="#{item.orgCategory}" rowspan="2" rendered="#{orgSettingsReportPage.showRequisite}">
                    <h:outputText escape="true" value="Категории ОУ" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Использовать Web-АРМ" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Абонементное питание" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Вариатвиное питание" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Предзаказ" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Оплата/сторнирование месяц продажи + 5 дней следующего" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" colspan="1">
                    <h:outputText escape="true" value="Контроль расхождения времени оплаты" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showFeedingSettings}" rowspan="2">
                    <h:outputText escape="true" value="Возможность ухода в минус при оплате Платного плана / Абонементного питания" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showCardSettings}" colspan="1">
                    <h:outputText escape="true" value="Запрет на выдачу временной карты" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showCardSettings}" colspan="1">
                    <h:outputText escape="true" value="Дубликаты для основных карт" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showCardSettings}" colspan="1">
                    <h:outputText escape="true" value="Несколько активных карт в ОО" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showCardSettings}" colspan="1">
                    <h:outputText escape="true" value="ЭЦП для карт" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showOtherSetting}" colspan="1">
                    <h:outputText escape="true" value="Заявки на посещение других ОО" />
                </rich:column>
                <rich:column rendered="#{orgSettingsReportPage.showOtherSetting}" colspan="1">
                    <h:outputText escape="true" value="Режим \"Летний период\"" />
                </rich:column>
                <rich:column breakBefore="true">
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <h:selectBooleanCheckbox styleClass="checkboxes" disabled="false">
                        <a4j:support event="onchange"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <!-- main info -->
        <rich:column styleClass="#{item.style}" >
            <h:selectBooleanCheckbox value="#{item.select}" styleClass="checkboxes" disabled="false">
                <a4j:support event="onchange"/>
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.orgNumberInName}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.officialName}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>
        <rich:column  styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.shortAddress}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.type}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.status}" styleClass="output-text" />
        </rich:column>
        <!-- Requisites -->
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.GUID}" styleClass="output-text" />
        </rich:column>
        <rich:column rendered="#{orgSettingsReportPage.showRequisite}" styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.additionalIdBuilding}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.btiUnom}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.btiUnad}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.INN}" styleClass="output-text" />
        </rich:column>
        <rich:column rendered="#{orgSettingsReportPage.showRequisite}" styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.typeInternal}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.armVersionNumber}" styleClass="output-text" />
        </rich:column>
        <!-- orgSettingsSupplierInfoPart-->
        <rich:column rendered="#{orgSettingsReportPage.showRequisite}" styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.defaultSupplierName}" styleClass="output-text" />
        </rich:column>
        <rich:column rendered="#{orgSettingsReportPage.showRequisite}" styleClass="#{item.style}">
            <h:outputText escape="true" value="#{item.productionConfig}" styleClass="output-text" />
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showRequisite}">
            <h:outputText escape="true" value="#{item.orgCategory}" styleClass="output-text" />
        </rich:column>
        <!-- Feeding Settings -->
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.useWebArm}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.usePaydableSubscriptionFeeding}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.variableFeeding}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.preordersEnabled}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.reverseMonthOfSale}" disabled="false" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:selectBooleanCheckbox value="#{item.denyPayPlanForTimeDifference}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showFeedingSettings}">
            <h:panelGrid columnClasses="center-aligned-column" columns="1" rendered="#{item.idOfSetting != -1}" styleClass="center-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.settingName}"
                                 action="#{mainPage.showFeedingSettingEditPage}"
                                 styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.idOfSetting}"
                                                 target="#{mainPage.feedingSettingEditPage.idOfSetting}" />
                    <f:setPropertyActionListener value="#{item.idOfSetting}"
                                                 target="#{mainPage.selectedIdOfFeedingSetting}" />
                </a4j:commandLink>
                <h:outputText escape="true" value="#{item.limit}" converter="copeckSumConverter" styleClass="output-text" />
            </h:panelGrid>
            <h:panelGrid columnClasses="center-aligned-column" columns="1" rendered="#{item.idOfSetting == -1}">
                <h:outputText escape="true" styleClass="output-text" value="#{item.settingName}" />
            </h:panelGrid>
        </rich:column>
        <!-- Cards Settings -->
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showCardSettings}">
            <h:selectBooleanCheckbox value="#{item.oneActiveCard}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showCardSettings}">
            <h:selectBooleanCheckbox value="#{item.enableDuplicateCard}" styleClass="checkboxes" disabled="false">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showCardSettings}">
            <h:selectBooleanCheckbox value="#{item.multiCardModeEnabled}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showCardSettings}">
            <h:selectBooleanCheckbox value="#{item.needVerifyCardSign}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <!-- Other Settings -->
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showOtherSetting}">
            <h:selectBooleanCheckbox value="#{item.requestForVisitsToOtherOrg}" styleClass="checkboxes">
                <a4j:support event="onchange" />
            </h:selectBooleanCheckbox>
        </rich:column>
        <rich:column styleClass="#{item.style}" rendered="#{orgSettingsReportPage.showOtherSetting}">
            <h:selectBooleanCheckbox value="#{item.isWorkInSummerTime}" styleClass="checkboxes">
                <a4j:support event="onchange" />
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
        <a4j:commandButton value="Применить изменения" action="#{orgSettingsReportPage.applyChanges()}"
                           reRender="orgSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyChangesButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
