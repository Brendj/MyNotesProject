<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<h:panelGrid id="dishMenuWabARMPPReportPanel" binding="#{mainPage.dishMenuReportWebArmPP.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="filterdishMenuWabARMPPReportPanel" columns="2">
        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                               reRender="modalContragentListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.contragentIds}"
                                             target="#{mainPage.contragentListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{mainPage.dishMenuReportWebArmPP.contragentFilter}}" escape="true"
                          styleClass="output-text" />
        </h:panelGroup>

        <h:outputText escape="true" value="Организации" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" id="dishMenuPanelModalOrgListSelectorPanel"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.filter}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.dishMenuReportWebArmPP.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Выбор типа питания" />
        <h:selectOneMenu value="#{mainPage.dishMenuReportWebArmPP.selectidTypeFoodId}" >
            <f:selectItems value="#{mainPage.dishMenuReportWebArmPP.getTypesOfFood()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Возрастной рацион" />
        <h:selectOneMenu value="#{mainPage.dishMenuReportWebArmPP.selectidAgeGroup}" >
            <f:selectItems value="#{mainPage.dishMenuReportWebArmPP.getAgeGroup()}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Выбор комплекса" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..." action="#{mainPage.showComplexListSelectPage}"
                               reRender="modalComplexListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalComplexListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <%--<f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />--%>
                <%--<f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />--%>
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.complexIds}"
                                             target="#{mainPage.complexWebListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{mainPage.dishMenuReportWebArmPP.complexFilter}}" escape="true"
                          styleClass="output-text" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Архивные" />
        <h:selectBooleanCheckbox value="#{mainPage.dishMenuReportWebArmPP.archived}"/>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Построить отчет" action="#{mainPage.dishMenuReportWebArmPP.buildReportHTML}"
                               reRender="dishMenuWabARMPPReportPanel" styleClass="command-button"
                               status="reportGenerateStatus" />
            <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.dishMenuReportWebArmPP.generateXLS}"
                             styleClass="command-button" />
            <a4j:status id="reportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Список блюд" styleClass="output-text" />
        <rich:dataTable id="dishMenuWabARMPPReportTable" value="#{mainPage.dishMenuReportWebArmPP.items}"
                        var="dishElement" rows="25" footerClass="data-table-footer" columnClasses="center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column
center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column
center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column,center-aligned-column, center-aligned-column">
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Код ИСПП" />
                </f:facet>
                <h:outputText value="#{dishElement.codeISPP}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Название" />
                </f:facet>
                <h:outputText value="#{dishElement.dishname}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Состав" />
                </f:facet>
                <h:outputText value="#{dishElement.componentsofdish}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Код поставщика" />
                </f:facet>
                <h:outputText value="#{dishElement.idcontragent}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Цена" />
                </f:facet>
                <h:outputText value="#{dishElement.price}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Дата в меню с" />
                </f:facet>
                <h:outputText value="#{dishElement.dateFrom}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Дата в меню по" />
                </f:facet>
                <h:outputText value="#{dishElement.dateTo}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Возрастная категория" />
                </f:facet>
                <h:outputText value="#{dishElement.agegroup}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Тип производства" />
                </f:facet>
                <h:outputText value="#{dishElement.typeOfProduction}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Тип питания" />
                </f:facet>
                <h:outputText value="#{dishElement.typefood}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Категория" />
                </f:facet>
                <h:outputText value="#{dishElement.category}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Подкатегория" />
                </f:facet>
                <h:outputText value="#{dishElement.subcategory}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Калории" />
                </f:facet>
                <h:outputText value="#{dishElement.calories}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Масса/Количество" />
                </f:facet>
                <h:outputText value="#{dishElement.qty}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Белки" />
                </f:facet>
                <h:outputText value="#{dishElement.protein}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Жиры" />
                </f:facet>
                <h:outputText value="#{dishElement.fat}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Углеводы" />
                </f:facet>
                <h:outputText value="#{dishElement.carbohydrates}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Штрих-код" />
                </f:facet>
                <h:outputText value="#{dishElement.barcode}" styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="dishMenuWabARMPPReportTable" renderIfSinglePage="false" maxPages="10"
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
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

<rich:modalPanel id="modalComplexListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalComplexListSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор комплекса" />
    </f:facet>
    <a4j:form id="modalComplexListSelectorForm" binding="#{mainPage.complexWebListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalComplexListSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText id="contragentListValue" value="#{mainPage.complexWebListSelectPage.selectedItems}"
                                     readonly="true" size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.complexWebListSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.showComplexListSelectPage}"
                                         reRender="modalComplexListSelectorTable" />
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.showComplexListSelectPage}"
                                         reRender="modalComplexListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value=""
                                                         target="#{mainPage.complexWebListSelectPage.filter}" />
                            <a4j:support event="onclick" action="#{mainPage.complexWebListSelectPage.cancelFilter}"
                                         reRender="modalComplexListSelectorForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllComplexListSelectedItemsList}"
                                           reRender="modalComplexListSelectorForm" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearComplexListSelectedItemsList}"
                                           reRender="modalComplexListSelectorForm" styleClass="command-link"
                                           value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalComplexListSelectorTable"
                                    value="#{mainPage.complexWebListSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column>
                            <h:selectBooleanCheckbox value="#{item.selected}" styleClass="output-text">
                                <a4j:support event="onchange" action="#{mainPage.complexWebListSelectPage.updateSelectedIds(item.idOfComplex, item.selected)}"
                                             reRender="contragentListValue" />
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfComplex}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.complexName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalComplexListSelectorTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                                <a4j:support event="onpagechange" />
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{mainPage.completeComplexListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalComplexListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelComplexListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalComplexListSelectorPanel')}.hide();return false;"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>