<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<h:panelGrid id="coverageNutritionReportPanelGrid" binding="#{mainPage.coverageNutritionReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px;" opened="true"
                            headerClass="filter-panel-header" id="coverageNutritionReportFilterPanelGrid">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <h:panelGroup id="contragentFilter">
                <a4j:commandButton value="..."
                                   action="#{mainPage.coverageNutritionReportPage.showContragentListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener
                            value="#{mainPage.coverageNutritionReportPage.contragentStringIdOfOrgList}"
                            target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    <f:setPropertyActionListener value="Выбор организации - источника меню" target="#{mainPage.orgFilterPageName}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.coverageNutritionReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup id="orgFilter">
                <a4j:commandButton value="..." action="#{mainPage.coverageNutritionReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.supplierFilter}" />
                    <f:setPropertyActionListener value="#{mainPage.coverageNutritionReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.coverageNutritionReportPage.filter}}" />
            </h:panelGroup>
            <h:outputText escape="true" value="Дата от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.coverageNutritionReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,coverageNutritionReportPanel"
                             actionListener="#{mainPage.coverageNutritionReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.coverageNutritionReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.coverageNutritionReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,coverageNutritionReportPanel"
                             actionListener="#{mainPage.coverageNutritionReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.coverageNutritionReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,coverageNutritionReportPanel"
                             actionListener="#{mainPage.coverageNutritionReportPage.onEndDateSpecified}" />
            </rich:calendar>
            <h:panelGrid styleClass="borderless-grid">
                <h:outputText escape="true" styleClass="output-text"
                              value="Выбор групп"  />
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="1-4 классы"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showYoungerClasses}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="5-9 классы"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showMiddleClasses}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="10-11 классы"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showOlderClasses}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="Сотрудники"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showEmployee}"
                                             styleClass="output-text" />
                </h:panelGrid>
                <h:outputText escape="true" styleClass="output-text"
                              value="Выбор типа питания"  />
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="Бесплатное"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showFreeNutrition}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="Платное"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showPaidNutrition}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text mleft20px"
                                  value="Буфет"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showBuffet}"
                                             styleClass="output-text" />
                </h:panelGrid>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" styleClass="output-text"
                                  value="Комплексы, проданные по карте ОО"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showComplexesByOrgCard}"
                                             styleClass="output-text" />
                    <h:outputText escape="true" styleClass="output-text"
                                  value="Итоговые значения"  />
                    <h:selectBooleanCheckbox value="#{mainPage.coverageNutritionReportPage.showTotal}"
                                             styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.coverageNutritionReportPage.buildReportHTML}"
                           reRender="coverageNutritionReportPanel"
                           styleClass="command-button" status="coverageNutritionReportGenerateStatus" />

        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.coverageNutritionReportPage.exportToXLS}"
                         styleClass="command-button" />

    </h:panelGrid>

    <a4j:status id="coverageNutritionReportGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="coverageNutritionReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.coverageNutritionReportPage.htmlReport}" >
            <f:verbatim>
                <div>${mainPage.coverageNutritionReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>


</h:panelGrid>