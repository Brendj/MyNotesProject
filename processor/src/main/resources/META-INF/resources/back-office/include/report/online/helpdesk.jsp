<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="helpdeskReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.HelpdeskReportPage"--%>
<h:panelGrid id="helpdeskPanelGrid" binding="#{helpdeskReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{helpdeskReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
        </rich:calendar>

        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{helpdeskReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Обновить" action="#{helpdeskReportPage.reload}"
                           reRender="helpdeskPanelGrid" styleClass="command-button"
                           id="reloadHelpdeskButton" />
    </h:panelGrid>

    <rich:dataTable id="helpdeskTable" value="#{helpdeskReportPage.items}" var="item" rows="25"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:graphicImage value="/images/16x16/edit.png"/>
            </f:facet>
            <h:graphicImage value="/images/16x16/edit.png" rendered="#{item.changed}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{item.requestDate}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.requestUpdateDate}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер заявки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.requestNumber}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.status}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тема" />
            </f:facet>
            <h:outputText escape="true" value="#{item.theme}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Описание проблемы" />
            </f:facet>
            <h:outputText escape="true" value="#{item.message}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ФИО заявителя" />
            </f:facet>
            <h:outputText escape="true" value="#{item.declarer}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.org}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Телефон заявителя" />
            </f:facet>
            <h:outputText escape="true" value="#{item.phone}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Действия" />
            </f:facet>
            <h:outputText escape="true" value="Комментарий: " styleClass="output-text" rendered="#{item.editMode}"/>
            <h:inputText value="#{item.comment}" styleClass="output-text" rendered="#{item.editMode}">
                <a4j:support event="onchange" />
            </h:inputText>
            <a4j:commandButton value="Ок" reRender="helpdeskTable" ajaxSingle="true" rendered="#{item.editMode}"
                               action="#{helpdeskReportPage.closeHelpRequest()}">
                <f:setPropertyActionListener value="#{item.idOfHelpRequest}" target="#{helpdeskReportPage.selectedIdOfHelpRequest}" />
            </a4j:commandButton>
            <a4j:commandButton value="Отмена" reRender="helpdeskTable" ajaxSingle="true" rendered="#{item.editMode}"
                               action="#{helpdeskReportPage.cancelEditHelpRequest()}">
                <f:setPropertyActionListener value="#{item.idOfHelpRequest}" target="#{helpdeskReportPage.selectedIdOfHelpRequest}" />
            </a4j:commandButton>
            <a4j:commandButton value="Закрыть" reRender="helpdeskTable" ajaxSingle="true" rendered="#{item.isOpened && !item.editMode}"
                          action="#{helpdeskReportPage.editHelpRequest()}">
                <f:setPropertyActionListener value="#{item.idOfHelpRequest}" target="#{helpdeskReportPage.selectedIdOfHelpRequest}" />
            </a4j:commandButton>
            <h:graphicImage value="/images/taloons/canceled-gray.png" rendered="#{!item.isOpened}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Комментарий" />
            </f:facet>
            <h:outputText escape="true" value="#{item.comment}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="helpdeskTable" renderIfSinglePage="false"
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
        <a4j:commandButton value="Подтвердить" action="#{helpdeskReportPage.apply}"
                           reRender="helpdeskTable" styleClass="command-button"
                           id="applyHelpdeskButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
