<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="discrepanciesDataOnOrdersAndPaymentReportPageGrid" binding="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText styleClass="output-text" escape="true" value="Организация поставщик меню" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.menuSourceOrgFilter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="2" target="#{mainPage.orgSelectPage.filterMode}" />
            </a4j:commandButton>
        </h:panelGroup>

        <%--<h:outputText escape="true" value="Поставщик меню" styleClass="output-text required-field" />--%>
        <%--<h:panelGroup>--%>
            <%--<a4j:commandButton value="..." action="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.showContragentListSelectPage}"--%>
                               <%--reRender="modalOrgListSelectorPanel"--%>
                               <%--oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"--%>
                               <%--styleClass="command-link" style="width: 25px;">--%>
                <%--<f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />--%>
                <%--<f:setPropertyActionListener value="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.contragentStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>--%>
                <%--<f:setPropertyActionListener value="Выбор контрагента" target="#{mainPage.orgFilterPageName}"/>--%>
            <%--</a4j:commandButton>--%>
            <%--<h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.contragentFilter}}" />--%>
        <%--</h:panelGroup>--%>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..."
                               action="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener
                        value="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.filter}}" />
        </h:panelGroup>

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.buildReportHTML}"
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
        <c:if test="${not empty mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.htmlReport}" >
            <f:verbatim>
                <div> ${mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.htmlReport} </div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
    <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.discrepanciesDataOnOrdersAndPaymentReportPage.generateXLS}" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>