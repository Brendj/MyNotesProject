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

<h:panelGrid id="goodRequestsNewReportPanelGrid" binding="#{mainPage.goodRequestsNewReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px;" opened="true"
                            headerClass="filter-panel-header" id="goodRequestsNewReportFilterPanelGrid">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <h:panelGroup id="contragetFilter">
                <a4j:commandButton value="..."
                                   action="#{mainPage.goodRequestsNewReportPage.showContragentListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   disabled="#{mainPage.goodRequestsNewReportPage.applyUserSettings}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener
                            value="#{mainPage.goodRequestsNewReportPage.contragentStringIdOfOrgList}"
                            target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    <f:setPropertyActionListener value="Выбор организации - источника меню" target="#{mainPage.orgFilterPageName}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.goodRequestsNewReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup id="orgFilter">
                <a4j:commandButton value="..." action="#{mainPage.goodRequestsNewReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   disabled="#{mainPage.goodRequestsNewReportPage.applyUserSettings}"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.goodRequestsNewReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.goodRequestsNewReportPage.filter}}" />
            </h:panelGroup>
            <h:outputText escape="true" value="Формировать по Списку организаций рассылки" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestsNewReportPage.applyUserSettings}"
                                     styleClass="output-text">
                <a4j:support event="onclick" reRender="contragetFilter, orgFilter"
                             actionListener="#{mainPage.goodRequestsNewReportPage.applyOfOrgList}" ajaxSingle="true" />
            </h:selectBooleanCheckbox>
            <%--Добавить варниг сообщ--%>
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.goodRequestsNewReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,goodRequestsNewReportPanel"
                             actionListener="#{mainPage.goodRequestsNewReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.goodRequestsNewReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.goodRequestsNewReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,goodRequestsNewReportPanel"
                             actionListener="#{mainPage.goodRequestsNewReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.goodRequestsNewReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,goodRequestsNewReportPanel"
                             actionListener="#{mainPage.goodRequestsNewReportPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText escape="true" value="Скрывать даты с пустыми значениями" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestsNewReportPage.hideMissedColumns}"
                                     styleClass="output-text" />

            <h:outputText escape="true" value="Скрывать значения суточной пробы и заявки обучающихся других ОО" styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestsNewReportPage.hideDailySamplesCount}"
                                     styleClass="output-text"/>

            <h:outputText escape="true" value="Фильтры по заявкам" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.goodRequestsNewReportPage.orgRequest.orgRequestFilterEnum}"
                             styleClass="output-text">
                <f:converter converterId="orgRequestFilterConverter" />
                <f:selectItems value="#{mainPage.goodRequestsNewReportPage.orgRequest.items}" />
            </h:selectOneMenu>

            <h:outputText escape="true" value="Наименование товара" styleClass="output-text" />
            <h:inputText value="#{mainPage.goodRequestsNewReportPage.nameFiler}" styleClass="input-text" size="50" />

            <h:outputText escape="true" value="Использовать цветовую раскраску изменений" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"
                                     styleClass="output-text">
                <a4j:support event="onclick" reRender="goodRequestsNewReportFilterPanelGrid" ajaxSingle="true" />
            </h:selectBooleanCheckbox>

            <a4j:commandButton value="Обновить время генерации" action="#{mainPage.showGoodRequestNewReportPage}"
                               reRender="goodRequestsNewReportPanelGrid"
                               styleClass="command-button mleft20px" status="goodRequestsNewReportGenerateStatus"
                               rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>
            <rich:spacer rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>
            <h:outputText escape="true" value="Время генерации от" styleClass="output-text mleft20px"
                          rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>
            <rich:calendar value="#{mainPage.goodRequestsNewReportPage.generateBeginDate}"
                           datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter" inputClass="input-text"
                           showWeeksBar="false" rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}">
                <a4j:support event="onchanged" reRender="generateEndDateCal"
                             actionListener="#{mainPage.goodRequestsNewReportPage.onGeneratePeriodChanged}" />
            </rich:calendar>
            <h:outputText escape="true" value="Время генерации до" styleClass="output-text mleft20px"
                          rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>
            <rich:calendar id="generateEndDateCal" value="#{mainPage.goodRequestsNewReportPage.generateEndDate}"
                           datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter" inputClass="input-text"
                           showWeeksBar="false" rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>
            <h:outputText escape="true" styleClass="output-text mleft20px"
                          rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"
                          value="Скрыть предыдущее значение в скобках при изменении"  />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestsNewReportPage.hideLastValue}"
                                     styleClass="output-text"
                                     rendered="#{mainPage.goodRequestsNewReportPage.hideGeneratePeriod}"/>

            <h:outputText escape="true" value="Типы заявок" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.goodRequestsNewReportPage.preorderType}"
                             styleClass="output-text">
                <f:converter converterId="preorderTypeConverter" />
                <f:selectItems value="#{mainPage.goodRequestsNewReportPage.preorderTypeItems}" />
            </h:selectOneMenu>

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.goodRequestsNewReportPage.buildReportHTML}"
                           reRender="goodRequestsNewReportPanel"
                           styleClass="command-button" status="goodRequestsNewReportGenerateStatus" />

        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.goodRequestsNewReportPage.exportToXLS}"
                         styleClass="command-button" />

    </h:panelGrid>

    <a4j:status id="goodRequestsNewReportGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="goodRequestsNewReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.goodRequestsNewReportPage.htmlReport}" >
            <f:verbatim>
                <div>${mainPage.goodRequestsNewReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>


</h:panelGrid>