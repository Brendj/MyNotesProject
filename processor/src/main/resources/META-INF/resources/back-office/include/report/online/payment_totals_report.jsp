<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="paymentTotalsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PaymentTotalsReportPage"--%>
<h:panelGrid id="paymentTotalsReportPanelGrid" binding="#{mainPage.paymentTotalsReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px;" opened="true"
                            headerClass="filter-panel-header" id="paymentTotalsReportFilterPanelGrid">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.paymentTotalsReportPage.filter}" readonly="true" styleClass="input-text long-field"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <%--Добавить варниг сообщ--%>
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.paymentTotalsReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,paymentTotalsReportPanel"
                             actionListener="#{mainPage.paymentTotalsReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.paymentTotalsReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.paymentTotalsReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar"
                             actionListener="#{mainPage.paymentTotalsReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>

            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.paymentTotalsReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,paymentTotalsReportPanel"
                             actionListener="#{mainPage.paymentTotalsReportPage.onEndDateSpecified}" />
            </rich:calendar>

            <%--<h:outputText escape="true" value="Скрывать ОО по которым не было движения за период" styleClass="output-text" />--%>
            <%--<h:selectBooleanCheckbox value="#{mainPage.paymentTotalsReportPage.hideNullRows}"--%>
                                     <%--styleClass="output-text">--%>
                <%--<a4j:support event="onclick" reRender="paymentTotalsReportPanelGrid" ajaxSingle="true"--%>
                        <%--actionListener="#{mainPage.paymentTotalsReportPage.onHideNullRowsChange}"/>--%>
            <%--</h:selectBooleanCheckbox>--%>

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <%--<rich:simpleTogglePanel label="Временные настройки отчета" switchType="client" style="width: 800px;" opened="true"--%>
                            <%--headerClass="filter-panel-header" id="paymentTotalsReportFilterPanelGridTemporary">--%>
        <%--<h:panelGrid styleClass="borderless-grid" columns="2">--%>

            <%--<h:outputText escape="true" value="Поставщик" styleClass="output-text" />--%>
            <%--<h:panelGroup styleClass="borderless-div">--%>
                <%--<h:inputText value="#{mainPage.paymentTotalsReportPage.contragentFilter.contragent.contragentName}" readonly="true"--%>
                             <%--styleClass="input-text long-field" style="margin-right: 2px;" />--%>
                <%--<a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"--%>
                                   <%--reRender="modalContragentSelectorPanel"--%>
                                   <%--oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"--%>
                                   <%--styleClass="command-link" style="width: 25px;">--%>
                    <%--<f:setPropertyActionListener value="0"--%>
                                                 <%--target="#{mainPage.multiContrFlag}" />--%>
                    <%--<f:setPropertyActionListener value="2,"--%>
                                                 <%--target="#{mainPage.classTypes}" />--%>
                <%--</a4j:commandButton>--%>
            <%--</h:panelGroup>--%>

            <%--<h:outputText styleClass="output-text" escape="true" value="Организация" />--%>
            <%--<h:panelGroup id="orgFilter">--%>
                <%--<a4j:commandButton value="..."--%>
                                   <%--action="#{mainPage.paymentTotalsReportPage.showOrgListSelectPage}"--%>
                                   <%--reRender="modalOrgListSelectorPanel"--%>
                                   <%--oncomplete="if (#{facesContext.maximumSeverity == null})--%>
                                        <%--#{rich:component('modalOrgListSelectorPanel')}.show();"--%>
                                   <%--styleClass="command-link" style="width: 25px;">--%>
                    <%--<f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />--%>
                    <%--<f:setPropertyActionListener--%>
                            <%--value="#{mainPage.paymentTotalsReportPage.getStringIdOfOrgList}"--%>
                            <%--target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />--%>
                <%--</a4j:commandButton>--%>
                <%--<h:outputText styleClass="output-text" escape="true"--%>
                              <%--value=" {#{mainPage.paymentTotalsReportPage.filter}}" />--%>
            <%--</h:panelGroup>--%>
        <%--</h:panelGrid>--%>
    <%--</rich:simpleTogglePanel>--%>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.paymentTotalsReportPage.exportToHTML}"
                           reRender="paymentTotalsReportPanel"
                           styleClass="command-button" status="paymentTotalsGenerateStatus" />

        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.paymentTotalsReportPage.exportToXLS}"
                         styleClass="command-button" />

    </h:panelGrid>

    <a4j:status id="paymentTotalsGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="paymentTotalsReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.paymentTotalsReportPage.htmlReport}" >
            <f:verbatim>
                <div>${mainPage.paymentTotalsReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>
