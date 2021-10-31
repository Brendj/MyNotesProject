<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script language="javascript">
    function disableButtons(value) {
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:generateButton").disabled=value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:xlsButton").disabled=value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clearButton").disabled=value;
    }
</script>

<h:panelGrid id="registerStampReportPanelGrid" binding="#{mainPage.ordersByManufacturerReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header" width="800">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <%--<h:selectOneMenu value="#{ordersByManufacturerReportPage.contragentId}">--%>
            <%--<f:selectItem />--%>
            <%--<f:selectItems value="#{ordersByManufacturerReportPage.contragentsSelectItems}"/>--%>
            <%--</h:selectOneMenu>--%>

            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.ordersByManufacturerReportPage.contragent.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px; width: 275px;" />
                <a4j:commandButton value="..."
                                   action="#{mainPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel,registerStampReportPanelGrid"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <a4j:commandButton value="..." action="#{mainPage.ordersByManufacturerReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   style="width: 25px;">
                    <f:setPropertyActionListener value="#{mainPage.ordersByManufacturerReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.ordersByManufacturerReportPage.filter}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.ordersByManufacturerReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,registerStampReportPanel"
                             actionListener="#{mainPage.ordersByManufacturerReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect" value="#{mainPage.ordersByManufacturerReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.ordersByManufacturerReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,registerStampReportPanel"
                             actionListener="#{mainPage.ordersByManufacturerReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.ordersByManufacturerReportPage.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,registerStampReportPanel"
                             actionListener="#{mainPage.ordersByManufacturerReportPage.onEndDateSpecified}" />
            </rich:calendar>

        </h:panelGrid>

    </rich:simpleTogglePanel>
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.ordersByManufacturerReportPage.buildReportHTML}"
                           reRender="registerStampReportPanel" styleClass="command-button" onclick="disableButtons(true);"
                           status="reportGenerateStatus" id="generateButton" oncomplete="disableButtons(false)"/>
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.ordersByManufacturerReportPage.showCSVList}"
                         styleClass="command-button" id="xlsButton">
        </h:commandButton>
        <a4j:commandButton value="Очистить" action="#{mainPage.ordersByManufacturerReportPage.clear}" onclick="disableButtons(true);"
                           reRender="registerStampReportPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="clearButton" oncomplete="disableButtons(false)" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="registerStampReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${mainPage.ordersByManufacturerReportPage.htmlReport!=null && not empty mainPage.ordersByManufacturerReportPage.htmlReport}">
            <f:verbatim>
                <div>${mainPage.ordersByManufacturerReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>


</h:panelGrid>