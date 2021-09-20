<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="showMessagePanel" autosized="true">
    <f:facet name="header">
        <h:outputText value="Сообщение в АРМ администратора" />
    </f:facet>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Тип" styleClass="output-text" />
        <h:inputText value="#{mainPage.infoMessagePage.itemToShow.type}" style="width:400px;" styleClass="input-text" readonly="true" />

        <h:outputText escape="true" value="Заголовок" styleClass="output-text" />
        <h:inputText value="#{mainPage.infoMessagePage.itemToShow.header}" style="width:400px;" styleClass="input-text" readonly="true" />

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputTextarea value="#{mainPage.infoMessagePage.itemToShow.content}" style="width:400px;height:100px;" styleClass="input-text" readonly="true" />

        <h:outputText escape="true" value="Дата создания" styleClass="output-text" />
        <h:inputText value="#{mainPage.infoMessagePage.itemToShow.createdDate}" style="width:400px;"
                         converter="timeMinuteConverter" styleClass="input-text" readonly="true" />

        <h:outputText escape="true" value="Автор сообщения" styleClass="output-text" />
        <h:inputText value="#{mainPage.infoMessagePage.itemToShow.author}" style="width:400px;" styleClass="input-text" readonly="true" />

        <h:outputText escape="true" value="Список организаций" styleClass="output-text" />
        <rich:dataTable id="deliveryInfoDataTable" value="#{mainPage.infoMessagePage.itemToShow.details}" var="deliveryInfo" rows="10"
                        footerClass="data-table-footer"
                        reRender="workspaceForm">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Ид. орг." />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Краткое наименование" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Время отправки" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{deliveryInfo.idOfOrg}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{deliveryInfo.orgShortName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{deliveryInfo.sendDate}" converter="timeMinuteConverter" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="deliveryInfoDataTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
        <h:panelGroup>

        </h:panelGroup>

        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('showMessagePanel')" style="width: 180px;" ajaxSingle="true" />
    </h:panelGrid>
</rich:modalPanel>

<h:panelGrid id="infoMessagePageControls" binding="#{mainPage.infoMessagePage.pageComponent}" >
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:status id="updateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
        <h:outputText escape="true" value="Архив сообщений в АРМ администратора ОО" styleClass="output-text" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <rich:dataTable id="infoMessageDataTable" value="#{mainPage.infoMessagePage.items}" var="line" rows="20"
                        footerClass="data-table-footer" reRender="workspaceForm">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Дата" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Тип сообщения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Заголовок" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Автор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Подробности" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.createdDate}" converter="timeMinuteConverter" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.type}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.header}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.author}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <a4j:commandButton value="..." style="width: 25px; height:25px; text-align: right" title="Детали сообщения"
                                   reRender="showMessagePanel" styleClass="command-button" status="updateStatus" ajaxSingle="true"
                                   oncomplete="Richfaces.showModalPanel('showMessagePanel');" >
                    <f:setPropertyActionListener value="#{line}" target="#{mainPage.infoMessagePage.itemToShow}" />
                </a4j:commandButton>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="infoMessageDataTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
    </h:panelGrid>
</h:panelGrid>