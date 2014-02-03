<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="discrepanciesDataOnOrdersAndPaymentReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.DiscrepanciesDataOnOrdersAndPaymentReportPage"--%>
<h:panelGrid id="discrepanciesDataOnOrdersAndPaymentReportPageGrid" binding="#{discrepanciesDataOnOrdersAndPaymentReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{discrepanciesDataOnOrdersAndPaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{discrepanciesDataOnOrdersAndPaymentReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{discrepanciesDataOnOrdersAndPaymentReportPage.contragentFilter.contragent.contragentName}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2,"
                                             target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{discrepanciesDataOnOrdersAndPaymentReportPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{discrepanciesDataOnOrdersAndPaymentReportPage.filter}}" />
        </h:panelGroup>

        <a4j:commandButton value="Генерировать отчет" action="#{discrepanciesDataOnOrdersAndPaymentReportPage.buildReportHTML}"
                           reRender="workspaceTogglePanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty discrepanciesDataOnOrdersAndPaymentReportPage.htmlReport}" >
            <f:verbatim>
                <div> ${discrepanciesDataOnOrdersAndPaymentReportPage.htmlReport} </div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
    <h:commandButton value="Выгрузить в Excel" actionListener="#{discrepanciesDataOnOrdersAndPaymentReportPage.generateXLS}" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>