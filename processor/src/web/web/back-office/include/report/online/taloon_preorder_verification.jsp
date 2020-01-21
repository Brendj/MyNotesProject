<%--
  ~ Copyright (c) 2019. Axe
  tta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: o.petrova
  Date: 09.12.2019
  Time: 16:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://richfaces.org/a4j" %>
<script language="javascript">
    function disableButtons(value) {
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:generateButton").disabled = value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:xlsButton").disabled = value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clearButton").disabled = value;
    }
</script>

<h:panelGrid id="taloonPreorderVerificationPanelGrid" binding="#{mainPage.taloonPreorderVerificationPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:modalPanel id="taloonPreorderMessagePanel" autosized="true" minWidth="400">
        <f:facet name="header">
            <h:outputText value="История изменений записи"/>
        </f:facet>
        <h:inputTextarea value="#{mainPage.taloonPreorderVerificationPage.remarksToShow}" cols="80" rows="10"
                         id="tp_remarks_toshow" readonly="true"/>
        <rich:spacer height="20px"/>
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('taloonPreorderMessagePanel')"
                           style="width: 180px;" ajaxSingle="true"/>
    </rich:modalPanel>
    <%--    Панель фильтров--%>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация"/>
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.taloonPreorderVerificationPage.filter}" readonly="true"
                         styleClass="input-text long-field"
                         style="margin-right: 2px;"/>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;"/>
        </h:panelGroup>
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text"/>
        <rich:calendar value="#{mainPage.taloonPreorderVerificationPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.totalSalesPage.onReportPeriodChanged}"/>
        </rich:calendar>

        <h:outputText escape="true" value="Конечная дата" styleClass="output-text"/>
        <rich:calendar id="endDateCalendar" value="#{mainPage.taloonPreorderVerificationPage.endDate}"
                       datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.totalSalesPage.onEndDateSpecified}"/>
        </rich:calendar>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Обновить" action="#{mainPage.taloonPreorderVerificationPage.reload}"
                           reRender="taloonPreorderVerificationPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton"/>
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="taloonPreorderVerificationTable" value="#{mainPage.taloonPreorderVerificationPage.items}"
                    var="item" rows="25"
                    footerClass="data-table-footer" rowKeyVar="rowItemKey" >
        <f:facet name="header">
            <rich:columnGroup>

                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Рацион"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Цена"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Заказ ИСПП (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Оплата ИСПП (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Блокировано (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Сторнировано (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Отгрузка (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header" colspan="2">
                    <h:outputText escape="true" value="Разница (шт/руб)"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус ОО"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус ПП"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Комментарий"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="История изменений"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:subTable value="#{item.complexes}" var="complex" rowKeyVar="rowComplexKey">
            <rich:subTable value="#{complex.details}" var="detail" rowKeyVar="rowDetailKey"
                           columnClasses="left-aligned-column, left-aligned-column, left-aligned-column,
                           center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                           center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                           center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                           center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                           center-aligned-column">
                <%--       Дата--%>
                <rich:column headerClass="column-header" rowspan="#{item.getDetailsSize()}"
                             rendered="#{item.getRowInItem(rowComplexKey, rowDetailKey) eq 0}" >
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{item.taloonDate}" styleClass="output-text"
                                  converter="dateConverter" rendered="#{!complex.taloonDateEmpty()}"/>
                    <h:outputText escape="true" value="Итого" styleClass="output-text"
                                  rendered="#{complex.taloonDateEmpty()}"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Комплекс--%>
                <rich:column headerClass="column-header" rowspan="#{complex.details.size()}"
                             rendered="#{rowDetailKey eq 0}">
                    <h:outputText value="#{detail.complexName}"/>
                </rich:column>
                <%--        Товары--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.goodsName}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Цена, руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="#{detail.price}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                </rich:column>
                <%--        Заказ ИСПП шт--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.requestedQty}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Заказ ИСПП руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.requestedSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Оплата ИСПП шт--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.soldQty}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Оплата ИСПП руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.soldSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Блокировано шт--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.blockedQty}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Блокировано руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.blockedSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Сторнировано шт--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.reservedQty}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Сторнировано руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.reservedSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Отгрузка шт--%>
                <rich:column headerClass="column-header">
                    <h:inputText value="#{detail.shippedQty}" styleClass="output-text"
                                 rendered="#{detail.enableEditShippedQty()}">
                        <a4j:support event="onchange"/>
                    </h:inputText>
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.shippedQty}" styleClass="output-text"
                                  rendered="#{!detail.enableEditShippedQty()}" />
<%--                    converter="quantityConverter"--%>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Отгрузка руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.shippedSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Разница шт--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.differedQty}" styleClass="output-text"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Разница руб--%>
                <rich:column headerClass="column-header">
                    <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay}"/>
                    <h:outputText escape="true" value="#{detail.differedSum}" styleClass="output-text"
                                  converter="copeckSumConverter"/>
                    <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay}"/>
                </rich:column>
                <%--        Статус ОО--%>
                <rich:column headerClass="column-header">
                    <h:graphicImage value="/images/taloons/applied.png" rendered="#{detail.isppStateConfirmed}"/>
                </rich:column>
                <%--        Статус ПП--%>
                <rich:column headerClass="column-header">
                    <%--            Изменить статус записи--%>

<%--                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateNotSelected}"--%>
<%--                                     action="#{mainPage.taloonPreorderVerificationPage.switchPpState()}"--%>
<%--                                     onclick="if (#{!detail.allowedSetFirstFlag()}) { alert('Операция запрещена'); return false; }"--%>
<%--                                     style="color:lightgray;">--%>
<%--                    <f:setPropertyActionListener value="#{detail}"--%>
<%--                                                 target="#{mainPage.taloonPreorderVerificationPage.currentTaloonPreorderVerificationItemDetail}"/>--%>
<%--                    <f:setPropertyActionListener value="#{detail.ppStateToTurnOnFirst}"--%>
<%--                                                 target="#{mainPage.taloonPreorderVerificationPage.currentState}"/>--%>
<%--                    <h:graphicImage value="/images/taloons/applied-gray.png"/>--%>
<%--                </a4j:commandLink>--%>

                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateNotSelected}"
                                     action="#{detail.confirmPpState}"
                                     onclick="if (#{!detail.allowedSetFirstFlag()}) { alert('Операция запрещена'); return false; }"
                                     style="color:lightgray;">
                        <f:setPropertyActionListener value="#{detail}" target="#{detail.ppState}"/>
                        <h:graphicImage value="/images/taloons/applied-gray.png"/>
                    </a4j:commandLink>

<%--                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateNotSelected}"--%>
<%--                                     action="#{mainPage.taloonPreorderVerificationPage.switchPpState()}"--%>
<%--                                     oncomplete="if (#{detail.needFillShippedQty()}) { alert('Заполните отгрузку ПП'); }"--%>
<%--                                     onclick="if (#{!detail.allowedSetSecondFlag()}) { alert('Операция запрещена'); return false; }"--%>
<%--                    style="color:lightgray;">--%>
<%--                        <f:setPropertyActionListener value="#{detail}"--%>
<%--                                                     target="#{mainPage.taloonPreorderVerificationPage.currentTaloonPreorderVerificationItemDetail}"/>--%>
<%--                        <f:setPropertyActionListener value="#{detail.ppStateToTurnOnSecond}"--%>
<%--                                                     target="#{mainPage.taloonPreorderVerificationPage.currentState}"/>--%>
<%--                        <h:graphicImage value="/images/taloons/canceled-gray.png"/>--%>
<%--                    </a4j:commandLink>--%>

                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateNotSelected}"
                                     action="#{detail.cancelPpState}"
                                     oncomplete="if (#{detail.needFillShippedQty()}) { alert('Заполните отгрузку ПП'); }"
                                     onclick="if (#{!detail.allowedSetSecondFlag()}) { alert('Операция запрещена'); return false; }"
                                     style="color:lightgray;">
                        <f:setPropertyActionListener value="#{detail}" target="#{detail.ppState}"/>
                        <h:graphicImage value="/images/taloons/canceled-gray.png"/>
                    </a4j:commandLink>

<%--                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateCanceled}"--%>
<%--                                     action="#{mainPage.taloonPreorderVerificationPage.resetPpState()}"--%>
<%--                                     onclick="if (#{!detail.allowedClearSecondFlag()}) { alert('Операция запрещена'); return false; }">--%>
<%--                        <f:setPropertyActionListener value="#{detail}"--%>
<%--                                                     target="#{mainPage.taloonPreorderVerificationPage.currentTaloonPreorderVerificationItemDetail}"/>--%>
<%--                        <f:setPropertyActionListener value="#{detail.ppStateToTurnOnFirst}"--%>
<%--                                                     target="#{mainPage.taloonPreorderVerificationPage.currentState}"/>--%>
<%--                        <h:graphicImage value="/images/taloons/canceled.png"/>--%>
<%--                    </a4j:commandLink>--%>

                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateCanceled}"
                                     action="#{detail.deselectPpState}"
                                     onclick="if (#{!detail.allowedClearSecondFlag()}) { alert('Операция запрещена'); return false; }">
                        <f:setPropertyActionListener value="#{detail}" target="#{detail.ppState}"/>
                        <h:graphicImage value="/images/taloons/canceled.png"/>
                    </a4j:commandLink>

                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{detail.ppStateConfirmed}"
                                     action="#{detail.deselectPpState}"
                                     onclick="if (#{!detail.allowedClearFirstFlag()}) { alert('Операция запрещена'); return false; }">
                        <f:setPropertyActionListener value="#{detail}" target="#{detail.ppState}"/>
                        <h:graphicImage value="/images/taloons/applied.png"/>
                    </a4j:commandLink>

                    <%--            Подтвердить для всего дня--%>
                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{item.isPpStateNotSelected() and detail.summaryDay}"
                                     action="#{item.confirmPpState()}"
                                     onclick="if (#{!item.allowedSetFirstFlag()}) { alert('Операция запрещена'); return false; }">
                        <f:setPropertyActionListener value="#{item}" target="#{item.getPpState()}"/>
                        <h:graphicImage value="/images/taloons/applied-big-gray.png"/>
                    </a4j:commandLink>
                    <%--            Отменить выбор для всего дня--%>
                    <a4j:commandLink reRender="taloonPreorderVerificationTable" rendered="#{item.isPpStateConfirmed() and detail.summaryDay}"
                                     action="#{item.deselectPpState()}" style="color:lightgray;"
                                     onclick="if (#{!item.allowedClearFirstFlag()}) { alert('Операция запрещена'); return false; }">
                        <f:setPropertyActionListener value="#{item}"
                                                     target="#{item.getPpState()}"/>
                        <h:graphicImage value="/images/taloons/applied-big.png"/>
                    </a4j:commandLink>
                </rich:column>

                <%--        Комментарий--%>
                <rich:column headerClass="column-header">
                    <h:inputText value="#{detail.comments}" styleClass="output-text" rendered="#{!detail.summaryDay}">
                    <a4j:support event="onchange"/>
                    </h:inputText>
                </rich:column>

                <%--        История изменений--%>
                <rich:column>
                    <a4j:commandButton value="..." reRender="taloonPreorderVerificationTable,tp_remarks_toshow"
                                       rendered="#{!detail.remarksEmpty}" ajaxSingle="true"
                                       title="#{detail.remarks}"
                                       oncomplete="Richfaces.showModalPanel('taloonPreorderMessagePanel');">
                        <f:setPropertyActionListener value="#{detail.remarks}"
                                                     target="#{mainPage.taloonPreorderVerificationPage.remarksToShow}"/>
                    </a4j:commandButton>
                </rich:column>

            </rich:subTable>
        </rich:subTable>

        <f:facet name="footer">
            <rich:datascroller for="taloonPreorderVerificationTable" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange"/>
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png"/>
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png"/>
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Подтвердить" action="#{mainPage.taloonPreorderVerificationPage.apply}"
                           reRender="taloonPreorderVerificationTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyButton"/>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages"/>
</h:panelGrid>
