<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .bordered {
        border-top: 2px solid #000000;
    }
</style>
<script type="text/javascript">
    function onstartloading() {
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading() {
        jQuery(".command-button").attr('disabled', '');
        updateWidth();
    }
    jQuery(document).ready(function () {
        updateWidth();
    });
</script>

<h:panelGrid id="requestsAndOrdersReportPanelGrid" binding="#{mainPage.requestsAndOrdersReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px;" opened="true"
                            headerClass="filter-panel-header" id="requestsAndOrdersReportFilterPanelGrid">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <h:panelGroup id="contragetFilter">
                <a4j:commandButton value="..."
                                   action="#{mainPage.requestsAndOrdersReportPage.showContragentListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   disabled="#{mainPage.requestsAndOrdersReportPage.applyUserSettings}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener
                            value="#{mainPage.requestsAndOrdersReportPage.contragentStringIdOfOrgList}"
                            target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    <f:setPropertyActionListener value="Выбор организации - источника меню"
                                                 target="#{mainPage.orgFilterPageName}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.requestsAndOrdersReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup id="orgFilter">
                <a4j:commandButton value="..." action="#{mainPage.requestsAndOrdersReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   disabled="#{mainPage.requestsAndOrdersReportPage.applyUserSettings}"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.requestsAndOrdersReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.requestsAndOrdersReportPage.filter}}" />
            </h:panelGroup>

            <%--Добавить варниг сообщ--%>
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.requestsAndOrdersReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,requestsAndOrdersReportPanel"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.requestsAndOrdersReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.requestsAndOrdersReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,requestsAndOrdersReportPanel"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.requestsAndOrdersReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,requestsAndOrdersReportPanel"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText escape="true" value="Скрывать даты с пустыми значениями" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.requestsAndOrdersReportPage.hideMissedColumns}"
                                     styleClass="output-text">
                <a4j:support event="onclick" reRender="requestsAndOrdersReportFilterPanelGrid" ajaxSingle="true"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onHideMissedColumnsChange}" />
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Включить цветовую индикацию расхождений" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.requestsAndOrdersReportPage.useColorAccent}"
                                     styleClass="output-text">
                <a4j:support event="onclick" reRender="requestsAndOrdersReportFilterPanelGrid" ajaxSingle="true"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onUseColorAccentChange}" />
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Отображать только расхождения" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.requestsAndOrdersReportPage.showOnlyDivergence}"
                                     styleClass="output-text">
                <a4j:support event="onclick" reRender="requestsAndOrdersReportFilterPanelGrid" ajaxSingle="true"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onShowOnlyDivergenceChange}" />
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Тип питания" styleClass="output-text" />
            <h:selectOneMenu id="feedingTypeList" value="#{mainPage.requestsAndOrdersReportPage.feedingPlanType}"
                             style="width:100px;">
                <f:selectItems value="#{mainPage.requestsAndOrdersReportPage.feedingPlanTypes}" />
                <a4j:support event="onchange" reRender="requestsAndOrdersReportFilterPanelGrid" ajaxSingle="true"
                             actionListener="#{mainPage.requestsAndOrdersReportPage.onFeedingPlanTypeChange}" />
            </h:selectOneMenu>

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.requestsAndOrdersReportPage.buildReportHTML}"
                           reRender="requestsAndOrdersReportPanel" styleClass="command-button"
                           status="requestsAndOrdersReportGenerateStatus" />

        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.requestsAndOrdersReportPage.exportToXLS}"
                         styleClass="command-button" />

    </h:panelGrid>

    <a4j:status id="requestsAndOrdersReportGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="requestsAndOrdersReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.requestsAndOrdersReportPage.htmlReport}">
            <f:verbatim>
                <div>${mainPage.requestsAndOrdersReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>
