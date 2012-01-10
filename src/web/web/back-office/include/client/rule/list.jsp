<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Правила --%>
<h:panelGrid id="ruleListPanel" binding="#{mainPage.ruleListPage.pageComponent}" styleClass="borderless-grid">
    <rich:dataTable id="ruleTable" value="#{mainPage.ruleListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header" style="text-align:right">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfRule}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Категория" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.categoryName}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Описание" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.description}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 0" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex0}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 1" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex1}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 2" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex2}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 3" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex3}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 4" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex4}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 5" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex5}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 6" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex6}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 7" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex7}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 8" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex8}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплекс 9" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complex9}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Комплексы" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.complexes}" />
        </rich:column>

        <rich:column headerClass="center-aligned-column" style="text-align:right">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Приоритет" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.priority}" />
        </rich:column>

        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditRule}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <h:commandLink action="#{mainPage.showRuleEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfRule}" target="#{mainPage.selectedIdOfRule}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditRule}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('ruleDeletePanel')}.show();">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfRule}"
                                             target="#{mainPage.selectedIdOfRule}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="ruleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <!--h:commandButton value="Выгрузить в CSV" action="{mainPage.showRuleCSVList}" styleClass="command-button" /-->
</h:panelGrid>