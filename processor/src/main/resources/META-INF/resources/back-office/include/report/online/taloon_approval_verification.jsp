<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: i.semenov
  Date: 18.07.2016
  Time: 16:53
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

<h:panelGrid id="taloonApprovalVerificationPanelGrid" binding="#{mainPage.taloonApprovalVerificationPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:modalPanel id="taloonApprovalMessagePanel" autosized="true" minWidth="400">
        <f:facet name="header">
            <h:outputText value="История изменений записи" />
        </f:facet>
        <h:inputTextarea value="#{mainPage.taloonApprovalVerificationPage.remarksToShow}" cols="80" rows="10" id="ta_remarks_toshow" readonly="true" />
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('taloonApprovalMessagePanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.taloonApprovalVerificationPage.filter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.taloonApprovalVerificationPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.totalSalesPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.taloonApprovalVerificationPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.totalSalesPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Обновить" action="#{mainPage.taloonApprovalVerificationPage.reload}"
                           reRender="taloonApprovalVerificationPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="taloonApprovalVerificationTable" value="#{mainPage.taloonApprovalVerificationPage.items}" var="item" rows="25"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Ид ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Название" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Заказ ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Оплата ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Отгрузка ПП" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Цена" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Сумма" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус ПП - согласие" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус ПП - отказ" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="История изменений"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:subTable value="#{item.details}" var="detail" rowKeyVar="rowKey"
                       columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column,
                       right-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
            <rich:column headerClass="column-header" rowspan="#{item.details.size()}" rendered="#{rowKey eq 0}">
                <h:outputText escape="true" value="#{item.taloonDate}" styleClass="output-text" converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{detail.idOfOrgCreated}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                <h:outputText escape="true" value="#{detail.taloonName}" styleClass="output-text" />
                <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                <h:outputText escape="true" value="#{detail.requestedQty}" styleClass="output-text" />
                <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                <h:outputText escape="true" value="#{detail.soldedQty}" styleClass="output-text" />
                <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:inputText value="#{detail.shippedQty}" styleClass="output-text" rendered="#{detail.enableEditShippedQty()}">
                    <a4j:support event="onchange" />
                </h:inputText>
                <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                <h:outputText escape="true" value="#{detail.shippedQty}" styleClass="output-text" rendered="#{!detail.enableEditShippedQty()}" />
                <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{detail.price}" styleClass="output-text" converter="copeckSumConverter" rendered="#{!detail.summaryDay}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                <h:outputText escape="true" value="#{detail.summa}" styleClass="output-text" converter="copeckSumConverter" />
                <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:graphicImage value="/images/taloons/applied.png" rendered="#{detail.isppStateConfirmed}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.ppStateNotSelected || detail.ppStateCanceled}"
                                 action="#{mainPage.taloonApprovalVerificationPage.switchPpState()}" style="color:lightgray;"
                                 onclick="if (#{!detail.allowedSetFirstFlag()}) { alert('Операция запрещена'); return false; }">
                    <f:setPropertyActionListener value="#{detail}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItemDetail}" />
                    <f:setPropertyActionListener value="#{detail.ppStateToTurnOnFirst}" target="#{mainPage.taloonApprovalVerificationPage.currentState}" />
                    <h:graphicImage value="/images/taloons/applied-gray.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.ppStateConfirmed}"
                                 action="#{mainPage.taloonApprovalVerificationPage.resetPpState()}"
                                 onclick="if (#{!detail.allowedClearFirstFlag()}) { alert('Операция запрещена'); return false; }">
                    <f:setPropertyActionListener value="#{detail}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItemDetail}" />
                    <f:setPropertyActionListener value="#{detail.ppStateToTurnOnFirst}" target="#{mainPage.taloonApprovalVerificationPage.currentState}" />
                    <h:graphicImage value="/images/taloons/applied.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.summaryDay and !detail.isTotal()}"
                                 action="#{mainPage.taloonApprovalVerificationPage.confirmPpStateAllDay()}" style="color:lightgray;"
                                 onclick="if (#{!detail.allowedSetFirstFlag()}) { alert('Операция запрещена'); return false; }">
                    <f:setPropertyActionListener value="#{item}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItem}" />
                    <h:graphicImage value="/images/taloons/applied-big.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.summaryDay and !detail.isTotal()}"
                                 action="#{mainPage.taloonApprovalVerificationPage.deselectPpStateAllDay()}" style="color:lightgray;"
                                 onclick="if (#{!detail.allowedClearFirstFlag()}) { alert('Операция запрещена'); return false; }">
                    <f:setPropertyActionListener value="#{item}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItem}" />
                    <h:graphicImage value="/images/taloons/applied-big-gray.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.summaryDay and detail.isTotal()}"
                                 action="#{mainPage.taloonApprovalVerificationPage.confirmPpStatePeriod()}" style="color:lightgray;"
                                 onclick="if (#{!mainPage.taloonApprovalVerificationPage.allowedSetFirstFlagPeriod()}) { alert('Операция запрещена'); return false; }">
                    <h:graphicImage value="/images/taloons/applied-big.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.summaryDay and detail.isTotal()}"
                                 action="#{mainPage.taloonApprovalVerificationPage.deselectPpStatePeriod()}" style="color:lightgray;"
                                 onclick="if (#{!mainPage.taloonApprovalVerificationPage.allowedClearFirstFlagPeriod()}) { alert('Операция запрещена'); return false; }">
                    <h:graphicImage value="/images/taloons/applied-big-gray.png" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <a4j:commandLink oncomplete="if (#{detail.needFillShippedQty()}) { alert('Заполните отгрузку ПП'); }"
                                 reRender="taloonApprovalVerificationTable" rendered="#{detail.ppStateNotSelected || detail.ppStateConfirmed}"
                                 action="#{mainPage.taloonApprovalVerificationPage.switchPpState()}"
                                 onclick="if (#{!detail.allowedSetSecondFlag()}) { alert('Операция запрещена'); return false; }"
                                 style="color:lightgray;" >
                    <f:setPropertyActionListener value="#{detail}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItemDetail}" />
                    <f:setPropertyActionListener value="#{detail.ppStateToTurnOnSecond}" target="#{mainPage.taloonApprovalVerificationPage.currentState}" />
                    <h:graphicImage value="/images/taloons/canceled-gray.png" />
                </a4j:commandLink>
                <a4j:commandLink reRender="taloonApprovalVerificationTable" rendered="#{detail.ppStateCanceled}"
                                 action="#{mainPage.taloonApprovalVerificationPage.resetPpState()}"
                                 onclick="if (#{!detail.allowedClearSecondFlag()}) { alert('Операция запрещена'); return false; }">
                    <f:setPropertyActionListener value="#{detail}" target="#{mainPage.taloonApprovalVerificationPage.currentTaloonApprovalVerificationItemDetail}" />
                    <f:setPropertyActionListener value="#{detail.ppStateToTurnOnFirst}" target="#{mainPage.taloonApprovalVerificationPage.currentState}" />
                    <h:graphicImage value="/images/taloons/canceled.png" />
                </a4j:commandLink>
            </rich:column>
            <rich:column>
                <a4j:commandButton value="..." reRender="taloonApprovalVerificationTable,ta_remarks_toshow" rendered="#{!detail.remarksEmpty}" ajaxSingle="true"
                                   title="#{detail.remarks}" oncomplete="Richfaces.showModalPanel('taloonApprovalMessagePanel');">
                    <f:setPropertyActionListener value="#{detail.remarks}" target="#{mainPage.taloonApprovalVerificationPage.remarksToShow}" />
                </a4j:commandButton>
            </rich:column>
        </rich:subTable>
        <f:facet name="footer">
            <rich:datascroller for="taloonApprovalVerificationTable" renderIfSinglePage="false"
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
        <a4j:commandButton value="Подтвердить" action="#{mainPage.taloonApprovalVerificationPage.apply}"
                           reRender="taloonApprovalVerificationTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
