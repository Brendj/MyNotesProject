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
    <rich:modalPanel id="applicationForFoodMessagePanel" autosized="true" minWidth="500">
        <f:facet name="header">
            <h:outputText value="История статусов по заявлению #{applicationForFoodReportPage.currentItem.serviceNumber}" />
        </f:facet>
        <rich:dataTable id="applicationForFoodHistoryTable" value="#{applicationForFoodReportPage.historyItems}" var="item" rows="25"
                        footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Статус" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Расшифровка" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Дата создания" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Отправка в ЕТП" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.applicationForFoodStateString}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.statusTitle}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.createdDate}" styleClass="output-text" converter="timeConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.sendDate}" styleClass="output-text" converter="timeConverter" />
            </rich:column>
        </rich:dataTable>
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('applicationForFoodMessagePanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>
    <rich:modalPanel id="applicationForFoodMessageBenefitPanel" autosized="true" minWidth="400">
        <f:facet name="header">
            <h:outputText value="Льгота #{applicationForFoodReportPage.currentItem.benefit}" />
        </f:facet>
        <h:inputTextarea value="#{applicationForFoodReportPage.benefitText}" cols="80" rows="10" readonly="true" />
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('applicationForFoodMessageBenefitPanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>

    <rich:modalPanel id="applicationForFoodChangeDatesPanel" autosized="true" minWidth="500" >
        <f:facet name="header">
            <h:outputText value="Изменение срока действия льготы по заявлению #{applicationForFoodReportPage.currentItem.serviceNumber}" />
        </f:facet>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
            <rich:calendar value="#{applicationForFoodReportPage.benefitStartDate}" datePattern="dd.MM.yyyy" id="benefitStartDateCalendar"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" ajaxSingle="true"/>
            </rich:calendar>
            <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
            <rich:calendar value="#{applicationForFoodReportPage.benefitEndDate}" datePattern="dd.MM.yyyy" id="benefitEndDateCalendar"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" ajaxSingle="true"/>
            </rich:calendar>
            <a4j:commandButton value="Сохранить" action="#{applicationForFoodReportPage.changeDates()}" reRender="applicationForFoodPanelGrid,applicationForFoodChangeDatesPanel"
                       oncomplete="if (#{!applicationForFoodReportPage.validateDates()}) Richfaces.showModalPanel('applicationForFoodChangeDatesPanel')" style="width: 180px;" ajaxSingle="true"/>
            <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('applicationForFoodChangeDatesPanel')" style="width: 180px;" ajaxSingle="true" />
        </h:panelGrid>
        <h:outputText style="color: red" escape="true" value="#{applicationForFoodReportPage.errorMessage}" />
    </rich:modalPanel>
    <h:panelGrid styleClass="borderless-grid" columns="2" id="applicationForFoodParameters">
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
        <h:outputText styleClass="output-text" escape="true" value="Статус" />
        <h:selectOneMenu id="affStatus"
                         value="#{applicationForFoodReportPage.status}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{applicationForFoodReportPage.statuses}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Льгота" />
        <h:selectOneMenu id="affBenefit"
                         value="#{applicationForFoodReportPage.benefit}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{applicationForFoodReportPage.benefits}" />
            <a4j:support event="onchange" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Физ. лицо" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectListPage(applicationForFoodReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{applicationForFoodReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{applicationForFoodReportPage.stringClientList}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Номер заявления" />
        <h:inputText value="#{applicationForFoodReportPage.number}" styleClass="input-text long-field" />

        <h:outputText styleClass="output-text" escape="true" value="Выбрать период" />
        <h:selectBooleanCheckbox value="#{applicationForFoodReportPage.showPeriod}" styleClass="checkboxes">
            <a4j:support event="onchange" reRender="applicationForFoodParameters" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Дата заявления от" styleClass="output-text" rendered="#{applicationForFoodReportPage.showPeriod}"/>
        <rich:calendar value="#{applicationForFoodReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" rendered="#{applicationForFoodReportPage.showPeriod}">
            <a4j:support event="onchanged" reRender="endDateCalendar,applicationForFoodPanelGrid"
                         actionListener="#{applicationForFoodReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" rendered="#{applicationForFoodReportPage.showPeriod}"/>
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{applicationForFoodReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;" rendered="#{applicationForFoodReportPage.showPeriod}">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{applicationForFoodReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,applicationForFoodPanelGrid"
                         actionListener="#{applicationForFoodReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата заявления до" styleClass="output-text" rendered="#{applicationForFoodReportPage.showPeriod}"/>
        <rich:calendar id="endDateCalendar" value="#{applicationForFoodReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" rendered="#{applicationForFoodReportPage.showPeriod}">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,applicationForFoodPanelGrid"
                         actionListener="#{applicationForFoodReportPage.onEndDateSpecified}" />
        </rich:calendar>
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
    <h:outputText escape="true" value="Есть несохраненные изменения. Нажмите \"Подтвердить\" чтобы сохранить их"
                  rendered="#{applicationForFoodReportPage.needAction()}" style="color:red" />

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
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Доп. информация" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Архивное" />
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
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.contractId}" converter="contractIdConverter" />
                <f:setPropertyActionListener value="#{item.applicationForFood.client.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
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
            <a4j:commandLink value="#{item.benefit}" styleClass="command-link" reRender="applicationForFoodPanelGrid" ajaxSingle="true"
                               oncomplete="Richfaces.showModalPanel('applicationForFoodMessageBenefitPanel');">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandLink reRender="applicationForFoodPanelGrid" rendered="#{item.isPaused}" value="Принесли документы"
                             action="#{applicationForFoodReportPage.makeResume()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <a4j:commandLink reRender="applicationForFoodPanelGrid" rendered="#{item.isResumed}" value="Решение положительное"
                             action="#{applicationForFoodReportPage.makeOK()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <h:outputText escape="false" value="<br/>" rendered="#{item.isResumed}" />
            <a4j:commandLink reRender="applicationForFoodPanelGrid" rendered="#{item.isResumed}" value="Решение отрицательное"
                             action="#{applicationForFoodReportPage.makeDenied()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <h:outputText escape="false" value="<br/>" rendered="#{item.canBeMovedToArchieve()}" />
            <a4j:commandLink reRender="applicationForFoodPanelGrid" rendered="#{item.canBeMovedToArchieve()}" value="Архивировать"
                             action="#{applicationForFoodReportPage.makeArchieved()}" styleClass="important-command-link">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>
            <h:outputText escape="false" value="<br/>" rendered="#{item.canChangeDates()}" />
            <a4j:commandLink reRender="applicationForFoodPanelGrid,applicationForFoodChangeDatesPanel" rendered="#{item.canChangeDates()}" value="Изменить даты"
                             styleClass="command-link" oncomplete="Richfaces.showModalPanel('applicationForFoodChangeDatesPanel');" ajaxSingle="true"
                            action="#{applicationForFoodReportPage.setErrorMessage(\"\")}">
                <f:setPropertyActionListener value="#{item.startDate}" target="#{applicationForFoodReportPage.benefitStartDate}" />
                <f:setPropertyActionListener value="#{item.endDate}" target="#{applicationForFoodReportPage.benefitEndDate}" />
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandLink>

        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandButton value="..." reRender="applicationForFoodHistoryTable,applicationForFoodMessagePanel" ajaxSingle="true"
                               oncomplete="Richfaces.showModalPanel('applicationForFoodMessagePanel');">
                <f:setPropertyActionListener value="#{item}" target="#{applicationForFoodReportPage.currentItem}" />
            </a4j:commandButton>
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.archieved}" styleClass="output-text" />
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
                           reRender="applicationForFoodPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="applyButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
