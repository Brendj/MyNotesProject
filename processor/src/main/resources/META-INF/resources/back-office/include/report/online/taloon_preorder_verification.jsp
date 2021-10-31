<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
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
            <a4j:support event="onchanged" actionListener="#{mainPage.totalSalesPage.onEndDateSpecified}"/>
        </rich:calendar>
    </h:panelGrid>
    <%--    Кнопки--%>
    <h:panelGroup id="buttons">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Обновить" action="#{mainPage.taloonPreorderVerificationPage.reload}"
                               reRender="taloonPreorderVerificationPanelGrid" styleClass="command-button"
                               status="reportGenerateStatus" id="reloadButton"/>
            <a4j:outputPanel ajaxRendered="true">
                <a4j:commandButton value="Подтвердить" action="#{mainPage.taloonPreorderVerificationPage.apply}"
                                   reRender="taloonPreorderVerificationPanelGrid" styleClass="command-button"
                                   disabled="#{!mainPage.taloonPreorderVerificationPage.changedData or
                               mainPage.taloonPreorderVerificationPage.needFillQty}"
                                   status="reportGenerateStatus" id="applyAbove"/>
            </a4j:outputPanel>
        </h:panelGrid>
    </h:panelGroup>

        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages"/>
        </h:panelGrid>
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>

        <rich:dataTable id="taloonPreorderVerificationTable" value="#{mainPage.taloonPreorderVerificationPage.items}"
                        var="item" rows="25"
                        footerClass="data-table-footer" rowKeyVar="rowItemKey">
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
                        <h:outputText escape="true" value="Заказ ОО (шт/руб)"/>
                    </rich:column>
                    <rich:column headerClass="column-header" colspan="2">
                        <h:outputText escape="true" value="Оплата ОО (шт/руб)"/>
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
                    <rich:column headerClass="column-header" rowspan="#{item.detailsSize}"
                                 rendered="#{item.getRowInItem(rowComplexKey, rowDetailKey) eq 0}">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{item.taloonDate}" styleClass="output-text"
                                      converter="dateConverter" rendered="#{!complex.taloonDateEmpty}"/>
                        <h:outputText escape="true" value="Итого" styleClass="output-text"
                                      rendered="#{complex.taloonDateEmpty}"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Комплекс--%>
                    <rich:column headerClass="column-header" rowspan="#{complex.detailsSize}"
                                 rendered="#{rowDetailKey eq 0}">
                        <h:outputText value="#{detail.complexName}" styleClass="output-text"/>
                    </rich:column>
                    <%--        Товары--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.goodsName}" styleClass="output-text"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Цена, руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{detail.price}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                    </rich:column>
                    <%--        Заказ ОО шт--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.requestedQty}" styleClass="output-text"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Заказ ОО руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.requestedSum}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Оплата ОО шт--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.soldQty}" styleClass="output-text"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Оплата ОО руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.soldSum}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Блокировано шт--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.blockedQty}" styleClass="output-text"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Блокировано руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.blockedSum}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Сторнировано шт--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.reservedQty}" styleClass="output-text"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Сторнировано руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.reservedSum}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Отгрузка шт--%>
                    <rich:column headerClass="column-header" width="4">
                        <h:inputTextarea value="#{detail.shippedQty}" styleClass="output-text" cols="4" rows="1"
                                         rendered="#{detail.enableEditShippedQty and !detail.needFillShippedQty}"
                                         validatorMessage="Поле Отгрузка не может содержать более 4 символов">
                            <f:validateLength maximum="4"/>
                            <a4j:support event="onchange" action="#{detail.setChangedData(true)}" />
                        </h:inputTextarea>
                        <h:inputTextarea value="#{detail.shippedQty}" styleClass="output-text" cols="4" rows="1"
                                         rendered="#{detail.enableEditShippedQty and detail.needFillShippedQty}"
                                         validatorMessage="Заполните поле Отгрузка">
                            <f:validateLongRange minimum="0" maximum="9999"/>
                            <a4j:support event="onchange" action="#{detail.setChangedData(true)}" />
                        </h:inputTextarea>
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.shippedQty}" styleClass="output-text"
                                      rendered="#{!detail.enableEditShippedQty and !detail.emptyShippedQty}"/>
                        <h:outputText escape="true" value="#{detail.reservedQty}" styleClass="output-text"
                                      rendered="#{!detail.enableEditShippedQty and detail.emptyShippedQty}"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--                Отгрузка руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay}"/>
                        <h:outputText escape="true" value="#{detail.shippedSum}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay}"/>
                    </rich:column>
                    <%--        Разница шт--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>" rendered="#{detail.summaryDay and !detail.emptyTotal}"/>
                        <h:outputText escape="true" value="#{detail.shippedQty - detail.soldQty}" styleClass="output-text"
                                      rendered="#{!detail.emptyTotal}"/>
                        <h:outputText escape="false" value="</strong>" rendered="#{detail.summaryDay and !detail.emptyTotal}"/>
                    </rich:column>
                    <%--        Разница руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="false" value="<strong>"
                                      rendered="#{detail.summaryDay and !detail.emptyTotal}"/>
                        <h:outputText escape="true" value="#{(detail.shippedQty - detail.soldQty) * detail.price}" styleClass="output-text"
                                      rendered="#{!detail.emptyTotal}" converter="copeckSumConverter"/>
                        <h:outputText escape="false" value="</strong>"
                                      rendered="#{detail.summaryDay and !detail.emptyTotal}"/>
                    </rich:column>
                    <%--        Статус ОО--%>
                    <rich:column headerClass="column-header">
                        <h:graphicImage value="/images/taloons/applied.png" rendered="#{detail.isppStateConfirmed}"/>
                    </rich:column>
                    <%--        Статус ПП--%>
                    <rich:column headerClass="column-header">
                        <%--            Изменить статус записи--%>
                        <a4j:outputPanel ajaxRendered="true">
                            <a4j:commandLink rendered="#{detail.ppStateNotSelected}"
                                             action="#{detail.confirmPpState}"
                                             onclick="if (#{!detail.allowedSetFirstFlag}) { alert('Операция запрещена'); return false; }"
                                             style="color:lightgray;">
                                <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                                <h:graphicImage value="/images/taloons/applied-gray.png"/>
                            </a4j:commandLink>
                        </a4j:outputPanel>

                        <a4j:outputPanel ajaxRendered="true">
                            <a4j:commandLink reRender="taloonPreorderVerificationTable"
                                             rendered="#{detail.ppStateNotSelected}"
                                             action="#{detail.cancelPpState}"
                                             oncomplete="if (#{detail.needFillShippedQty}) { alert('Заполните отгрузку ПП'); }"
                                             onclick="if (#{!detail.allowedSetSecondFlag}) { alert('Операция запрещена'); return false; }"
                                             style="color:lightgray;">
                                <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                                <h:graphicImage value="/images/taloons/canceled-gray.png"/>
                            </a4j:commandLink>
                        </a4j:outputPanel>

                        <a4j:outputPanel ajaxRendered="true">
                            <a4j:commandLink rendered="#{detail.ppStateCanceled}"
                                             action="#{detail.deselectPpState}"
                                             onclick="if (#{!detail.allowedClearSecondFlag}) { alert('Операция запрещена'); return false; }">
                                <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                                <h:graphicImage value="/images/taloons/canceled.png"/>
                            </a4j:commandLink>
                        </a4j:outputPanel>

                        <a4j:outputPanel ajaxRendered="true">
                            <a4j:commandLink rendered="#{detail.ppStateConfirmed}"
                                             action="#{detail.deselectPpState}"
                                             onclick="if (#{!detail.allowedClearFirstFlag}) { alert('Операция запрещена'); return false; }">
                                <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                                <h:graphicImage value="/images/taloons/applied.png"/>
                            </a4j:commandLink>
                        </a4j:outputPanel>

                        <%--            Подтвердить для всего дня--%>
                        <a4j:commandLink reRender="taloonPreorderVerificationTable"
                                         rendered="#{detail.summaryDay and !detail.total and !detail.emptyTotal}"
                                         action="#{item.confirmPpState}"
                                         onclick="if (#{!item.allowedSetFirstFlag}) { alert('Операция запрещена'); return false; }">
                            <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                            <h:graphicImage value="/images/taloons/applied-big.png"/>
                        </a4j:commandLink>
                        <%--            Отменить выбор для всего дня--%>
                        <a4j:commandLink reRender="taloonPreorderVerificationTable"
                                         rendered="#{detail.summaryDay and !detail.total and !detail.emptyTotal}"
                                         action="#{item.deselectPpState}" style="color:lightgray;"
                                         onclick="if (#{!item.allowedClearFirstFlag}) { alert('Операция запрещена'); return false; }">
                            <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                            <h:graphicImage value="/images/taloons/applied-big-gray.png"/>
                        </a4j:commandLink>

                        <%--            Подтвердить для всего периода--%>
                        <a4j:commandLink reRender="taloonPreorderVerificationTable"
                                         rendered="#{detail.summaryDay and !detail.total and detail.emptyTotal}"
                                         action="#{mainPage.taloonPreorderVerificationPage.confirmPpStateAllDay}"
                                         onclick="if (#{!mainPage.taloonPreorderVerificationPage.allowedSetPeriodFirstFlag}) { alert('Операция запрещена'); return false; }">
                            <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                            <h:graphicImage value="/images/taloons/applied-big.png"/>
                        </a4j:commandLink>
                        <%--            Отменить выбор для всего периода--%>
                        <a4j:commandLink reRender="taloonPreorderVerificationTable"
                                         rendered="#{detail.summaryDay and !detail.total and detail.emptyTotal}"
                                         action="#{mainPage.taloonPreorderVerificationPage.deselectPpStateAllDay}"
                                         style="color:lightgray;"
                                         onclick="if (#{!mainPage.taloonPreorderVerificationPage.allowedClearPeriodFirstFlag}) { alert('Операция запрещена'); return false; }">
                            <f:setPropertyActionListener value="true" target="#{detail.changedData}"/>
                            <h:graphicImage value="/images/taloons/applied-big-gray.png"/>
                        </a4j:commandLink>

                    </rich:column>

                    <%--        Комментарий--%>
                    <rich:column headerClass="column-header">
                        <h:inputTextarea value="#{detail.comments}" styleClass="output-text" id="comment" cols="20"
                                         rows="2" rendered="#{!detail.summaryDay and !detail.total}"
                                         validatorMessage="Комментарий не может быть больше 128 символов">
                            <f:validateLength maximum="128"/>
                            <a4j:support event="onchange"
                                         action="#{detail.setChangedData(true)}" />
                        </h:inputTextarea>
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
                                   maxPages="3" fastControls="hide" stepControls="auto"
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

    <h:panelGroup id="button">
        <h:panelGrid styleClass="borderless-grid" columns="1">
            <a4j:outputPanel ajaxRendered="true">
                <a4j:commandButton value="Подтвердить" action="#{mainPage.taloonPreorderVerificationPage.apply}"
                                   disabled="#{!mainPage.taloonPreorderVerificationPage.changedData or
                               mainPage.taloonPreorderVerificationPage.needFillQty}"
                                   reRender="taloonPreorderVerificationTable" styleClass="command-button"
                                   status="reportGenerateStatus" id="applyButton"/>
            </a4j:outputPanel>
        </h:panelGrid>
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages"/>
</h:panelGrid>
