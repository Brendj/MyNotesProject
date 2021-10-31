<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="ServiceCheckSumsPage" type="ru.axetta.ecafe.processor.controllers.ServiceCheckSumsPage"--%>
<h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid>
        <a4j:commandButton value="Запустить подсчет" action="#{mainPage.serviceCheckSumsPage.run()}"
                           reRender="serviceCheckSumsPageTable" />
        <a4j:status id="updateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid>
        <rich:dataTable id="serviceCheckSumsPageTable"
                        value="#{mainPage.serviceCheckSumsPage.serviceCheckSumsPageItemsList}" var="item" rows="30"
                        columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,  center-aligned-column"
                        footerClass="data-table-footer" rowKeyVar="row">
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="№" styleClass="output-text" />
                </f:facet>
                <h:outputText value="#{row+1}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Дата проверки" styleClass="output-text" />
                </f:facet>
                <h:outputText value="#{item.checkSumsDate}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Версия дистрибутива" styleClass="output-text" />
                </f:facet>
                <h:outputText value="#{item.distributionVersion}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Контрольная сумма по классам" styleClass="output-text" />
                </f:facet>
                <h:outputText value="#{item.checkSumsMd5}" styleClass="output-text" rendered="#{!item.redMd5}"/>
                <h:outputText value="#{item.checkSumsMd5}" styleClass="error-output-text" rendered="#{item.redMd5}"/>
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Контрольная сумма по настройкам ИБ" styleClass="output-text" />
                </f:facet>
                <h:outputText value="#{item.checkSumsOnSettings}" styleClass="output-text" rendered="#{!item.redSettings}"/>
                <h:outputText value="#{item.checkSumsOnSettings}" styleClass="error-output-text" rendered="#{item.redSettings}"/>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="serviceCheckSumsPageTable" renderIfSinglePage="false" maxPages="5"
                                   fastControls="hide" stepControls="auto" boundaryControls="hide">
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