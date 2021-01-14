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
            <a4j:commandButton value="..." action="#{mainPage.dishMenuReportWebArmPP.showContragentListSelectPage}"
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
            <a4j:commandButton value="..." action="#{mainPage.dishMenuReportWebArmPP.showOrgListSelectPage}" id="dishMenuPanelModalOrgListSelectorPanel"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.getStringIdOfOrgList}"
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
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.idOfOrgList}" target="#{mainPage.complexWebListSelectPage.filterOrgs}" />
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.contragentIds}" target="#{mainPage.complexWebListSelectPage.filterContagents}" />
                <f:setPropertyActionListener value="#{mainPage.dishMenuReportWebArmPP.complexIds}"
                                             target="#{mainPage.complexWebListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{mainPage.dishMenuReportWebArmPP.complexFilter}}" escape="true"
                          styleClass="output-text" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Архивные" />
        <h:selectOneMenu value="#{mainPage.dishMenuReportWebArmPP.selectArchived}" >
            <f:selectItems value="#{mainPage.dishMenuReportWebArmPP.getArchiveds()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Нахождение в меню буфета" />
        <h:selectOneMenu value="#{mainPage.dishMenuReportWebArmPP.selectinBufet}" >
            <f:selectItems value="#{mainPage.dishMenuReportWebArmPP.getFiltersForAddedColumns()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Нахождение в комплексном меню" />
        <h:selectOneMenu value="#{mainPage.dishMenuReportWebArmPP.selectinComplex}" >
            <f:selectItems value="#{mainPage.dishMenuReportWebArmPP.getFiltersForAddedColumns()}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Поиск по названию" styleClass="output-text" />
        <h:inputText value="#{mainPage.dishMenuReportWebArmPP.seachName}"/>

        <h:panelGrid styleClass="borderless-grid" columns="3">
            <a4j:commandButton value="Построить отчет" action="#{mainPage.dishMenuReportWebArmPP.buildReportHTML}"
                               reRender="dishMenuWabARMPPReportPanel" styleClass="command-button"
                               status="reportGenerateStatus" />
            <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.dishMenuReportWebArmPP.generateXLS}"
                             styleClass="command-button" />
            <a4j:commandButton value="Легенда"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('dishMenuLegentPanel')}.show();"
                               styleClass="command-link">
            </a4j:commandButton>
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
            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="ИС \"ПП\"" />
                </f:facet>
                <h:outputText value="#{dishElement.codeISPP}" styleClass="output-text"/>
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Название" />
                </f:facet>
                <h:outputText value="#{dishElement.dishname}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Состав" />
                </f:facet>
                <h:outputText value="#{dishElement.componentsofdish}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Код поставщика" />
                </f:facet>
                <h:outputText value="#{dishElement.idsupplier}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Цена" />
                </f:facet>
                <h:outputText value="#{dishElement.price}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Дата в меню с" />
                </f:facet>
                <h:outputText value="#{dishElement.dateFrom}" styleClass="output-text" converter="dateConverter"/>
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Дата в меню по" />
                </f:facet>
                <h:outputText value="#{dishElement.dateTo}" styleClass="output-text" converter="dateConverter"/>
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Возрастная категория" />
                </f:facet>
                <h:outputText value="#{dishElement.agegroup}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Тип производства" />
                </f:facet>
                <h:outputText value="#{dishElement.typeOfProduction}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Тип питания" />
                </f:facet>
                <h:outputText value="#{dishElement.typefood}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Категория" />
                </f:facet>
                <h:outputText value="#{dishElement.category}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Подкатегория" />
                </f:facet>
                <h:outputText value="#{dishElement.subcategory}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Калории" />
                </f:facet>
                <h:outputText value="#{dishElement.calories}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Масса/Количество" />
                </f:facet>
                <h:outputText value="#{dishElement.qty}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Белки" />
                </f:facet>
                <h:outputText value="#{dishElement.protein}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Жиры" />
                </f:facet>
                <h:outputText value="#{dishElement.fat}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Углеводы" />
                </f:facet>
                <h:outputText value="#{dishElement.carbohydrates}" styleClass="output-text" />
            </rich:column>

            <rich:column style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Штрих-код" />
                </f:facet>
                <h:outputText value="#{dishElement.barcode}" styleClass="output-text" />
            </rich:column>

            <rich:column rendered="#{mainPage.dishMenuReportWebArmPP.renderBufet()}"
                         style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Нахождение в буфете" />
                </f:facet>
                <h:outputText value="#{dishElement.countInMenu}" styleClass="output-text" />
            </rich:column>

            <rich:column rendered="#{mainPage.dishMenuReportWebArmPP.renderComplex()}"
                         style="#{mainPage.dishMenuReportWebArmPP.getColourForSell(dishElement)}">
                <f:facet name="header">
                    <h:outputText value="Нахождение в комплексе" />
                </f:facet>
                <h:outputText value="#{dishElement.countInComplex}" styleClass="output-text" />
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
        <h:outputText escape="true" value="Количество: #{mainPage.dishMenuReportWebArmPP.dishCount}" styleClass="output-text" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

<rich:modalPanel id="dishMenuLegentPanel" width="370" height="100" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('dishMenuLegentPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Легенда" />
    </f:facet>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="false" value="Серым цветом выделены архивные блюда"/>
        <a4j:commandButton value="Ok"
                           oncomplete="#{rich:component('dishMenuLegentPanel')}.hide();"
                           styleClass="command-button" />
    </h:panelGrid>
</rich:modalPanel>