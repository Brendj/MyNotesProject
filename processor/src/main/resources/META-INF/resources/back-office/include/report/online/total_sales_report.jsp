<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 07.05.13
  Time: 14:34
  To change this template use File | Settings | File Templates.
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

<h:panelGrid id="registerStampReportPanelGrid" binding="#{mainPage.totalSalesPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header" width="800">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <%--<h:selectOneMenu value="#{totalSalesPage.contragentId}">--%>
            <%--<f:selectItem />--%>
            <%--<f:selectItems value="#{totalSalesPage.contragentsSelectItems}"/>--%>
            <%--</h:selectOneMenu>--%>

            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.totalSalesPage.contragent.contragentName}" readonly="true"
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
                <a4j:commandButton value="..." action="#{mainPage.totalSalesPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   style="width: 25px;">
                    <f:setPropertyActionListener value="#{mainPage.totalSalesPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.totalSalesPage.filter}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.totalSalesPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,registerStampReportPanel"
                             actionListener="#{mainPage.totalSalesPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect" value="#{mainPage.totalSalesPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.totalSalesPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,registerStampReportPanel"
                             actionListener="#{mainPage.totalSalesPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.totalSalesPage.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,registerStampReportPanel"
                             actionListener="#{mainPage.totalSalesPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText escape="false" value="Показать в разрезе возрастных категорий" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.totalSalesPage.showAgeGroups}" styleClass="output-text">
            </h:selectBooleanCheckbox>

            <h:panelGrid styleClass="borderless-grid" columns="1">
                <h:panelGrid id="preferencialPanel" styleClass="borderless-grid" columns="1">
                    <h:panelGroup styleClass="borderless-div">
                        <h:outputText escape="true" value="Отображать детализацию буфетной продукции "
                                      styleClass="output-text" />
                        <h:selectBooleanCheckbox value="#{mainPage.totalSalesPage.showDetail}" styleClass="output-text">
                            <a4j:support event="onclick" reRender="preferencialPanel" ajaxSingle="true" />
                        </h:selectBooleanCheckbox>

                        <h:panelGroup layout="block" style="height: 150px; overflow-y: scroll;"
                                      rendered="#{mainPage.totalSalesPage.showDetail}">
                            <h:selectManyCheckbox id="preferentialTitles"
                                                  value="#{mainPage.totalSalesPage.preferentialTitleComplexes}"
                                                  layout="pageDirection" styleClass="output-text"
                                                  rendered="#{mainPage.totalSalesPage.showDetail}">
                                <f:selectItems value="#{mainPage.totalSalesPage.availableTitleComplexes}" />
                            </h:selectManyCheckbox>
                        </h:panelGroup>
                    </h:panelGroup>
                </h:panelGrid>

                <h:panelGrid id="benefitPanel" styleClass="borderless-grid" columns="1">
                    <h:panelGroup styleClass="borderless-div">
                        <h:outputText escape="true" value="Отображать детализацию льготных комплексов"
                                      styleClass="output-text" />
                        <h:selectBooleanCheckbox value="#{mainPage.totalSalesPage.showBenefitDetail}"
                                                 styleClass="output-text">
                            <a4j:support event="onclick" reRender="benefitPanel" ajaxSingle="true" />
                        </h:selectBooleanCheckbox>

                        <h:panelGroup layout="block" style="height: 150px; overflow-y: scroll;"
                                      rendered="#{mainPage.totalSalesPage.showBenefitDetail}">
                            <h:selectManyCheckbox id="benefitTitles"
                                                  value="#{mainPage.totalSalesPage.benefitTitleComplexes}"
                                                  layout="pageDirection" styleClass="output-text"
                                                  rendered="#{mainPage.totalSalesPage.showBenefitDetail}">
                                <f:selectItems
                                        value="#{mainPage.totalSalesPage.availableComplexesWithPriceTitlesBenefit}" />
                            </h:selectManyCheckbox>
                        </h:panelGroup>
                    </h:panelGroup>
                </h:panelGrid>

                <h:panelGrid id="paidPanel" styleClass="borderless-grid" columns="1">
                    <h:panelGroup styleClass="borderless-div">
                        <h:outputText escape="true" value="Отображать детализацию платных комплексов  "
                                      styleClass="output-text" />
                        <h:selectBooleanCheckbox value="#{mainPage.totalSalesPage.showPaidDetail}"
                                                 styleClass="output-text">
                            <a4j:support event="onclick" reRender="paidPanel" ajaxSingle="true" />
                        </h:selectBooleanCheckbox>

                        <h:panelGroup layout="block" style="height: 150px; overflow-y: scroll;"
                                      rendered="#{mainPage.totalSalesPage.showPaidDetail}">
                            <h:selectManyCheckbox id="paidTitles" value="#{mainPage.totalSalesPage.paidTitleComplexes}"
                                                  layout="pageDirection" styleClass="output-text"
                                                  rendered="#{mainPage.totalSalesPage.showPaidDetail}">
                                <f:selectItems
                                        value="#{mainPage.totalSalesPage.availableComplexesWithPriceTitlesPaid}" />
                            </h:selectManyCheckbox>
                        </h:panelGroup>
                    </h:panelGroup>
                </h:panelGrid>
            </h:panelGrid>

        </h:panelGrid>

    </rich:simpleTogglePanel>
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.totalSalesPage.buildReportHTML}"
                           reRender="registerStampReportPanel" styleClass="command-button" onclick="disableButtons(true);"
                           status="reportGenerateStatus" id="generateButton" oncomplete="disableButtons(false)"/>
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.totalSalesPage.showCSVList}"
                         styleClass="command-button" id="xlsButton" onclick="setTimeout('disableButtons(true);', 50);" >
        </h:commandButton>
        <a4j:commandButton value="Очистить" action="#{mainPage.totalSalesPage.clear}" onclick="disableButtons(true);"
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
        <c:if test="${mainPage.totalSalesPage.htmlReport!=null && not empty mainPage.totalSalesPage.htmlReport}">
            <f:verbatim>
                <div>${mainPage.totalSalesPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>


</h:panelGrid>