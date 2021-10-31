<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<h:panelGrid id="cancelCategoryBenefitsPage" binding="#{mainPage.cancelCategoryBenefitsPage.pageComponent}"
             styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>

    <h:panelGrid columns="1" styleClass="borderless-grid">
        <h:outputText value="Пожалуйста ответьте на этот простой математический вопрос " styleClass="output-text" />
    </h:panelGrid>

    <h:panelGrid columns="7" styleClass="borderless-grid">
        <h:outputText value="#{mainPage.cancelCategoryBenefitsPage.number1}" styleClass="output-text" />
        <h:outputText value=" + " styleClass="output-text" />
        <h:outputText value="#{mainPage.cancelCategoryBenefitsPage.number2}" styleClass="output-text" />
        <h:outputText value=" = " styleClass="output-text" />
        <h:inputText value="#{mainPage.cancelCategoryBenefitsPage.plusResult}" size="10" styleClass="input-text" />
        <a4j:commandButton value="Подсчитать"
                           action="#{mainPage.cancelCategoryBenefitsPage.resultChecker}" reRender="workspaceTogglePanel"
                           styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Отмена льготных категорий по всем учащимся"
                           action="#{mainPage.cancelCategoryBenefitsGenerate}" reRender="workspaceTogglePanel"
                           styleClass="command-button" disabled="#{mainPage.cancelCategoryBenefitsPage.disabled}" />
    </h:panelGrid>

    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable id="groupControlBenefitsTable"
                    value="#{mainPage.cancelCategoryBenefitsPage.groupControlBenefitsItemsList}" var="item" rows="30"
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
                <h:outputText value="Наименование ОУ" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Группа (класс)" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.groupName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Фамилия" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.surname}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Имя" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.firstName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Отчество" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.secondName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Номер л/с" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.contractId}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Льготы" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.benefits}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Результат" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.result}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="groupControlBenefitsTable" renderIfSinglePage="false" maxPages="5"
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
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showCancelCategoryBenefitsCSVList}"
                     styleClass="command-button" />
</h:panelGrid>