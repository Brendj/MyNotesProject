<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель редактирования правила --%>
<%--@elvariable id="wtRuleEditPag" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.wtRuleEditPag"--%>
<h:panelGrid id="ruleEditPanel" binding="#{wtRuleEditPag.pageComponent}"
             styleClass="borderless-grid borderless-grid-align-top" columns="2">

    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Категории клиентов" styleClass="output-text"/>
        <h:panelGroup>
            <a4j:commandButton id="categoryAjaxButton" value="..." action="#{mainPage.showCategoryListSelectPage}"
                               reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{wtRuleEditPag.idOfCategoryListString}"
                                             target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryListFilter" escape="true"
                          value=" {#{wtRuleEditPag.filter}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Категории организаций" styleClass="output-text"/>

        <h:panelGroup>
            <a4j:commandButton id="categoryOrgAjaxButton" value="..." action="#{mainPage.showCategoryOrgListSelectPage}"
                               reRender="modalCategoryOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{wtRuleEditPag.idOfCategoryOrgListString}"
                                             target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true"
                          value=" {#{wtRuleEditPag.filterOrg}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field"/>
        <h:selectOneMenu id="group" value="#{wtRuleEditPag.subCategory}" style="width:300px;" styleClass="groupSelect">
            <f:selectItems value="#{wtRuleEditPag.subCategories}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Описание" styleClass="output-text required-field"/>
        <h:inputText value="#{wtRuleEditPag.description}" maxlength="99" size="40" styleClass="input-text"/>

        <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text"/>
        <h:panelGrid columns="2">
            <h:inputText value="#{wtRuleEditPag.discountRate}" maxlength="3" styleClass="input-text"/>
            <h:outputText escape="true" value="%" styleClass="output-text"/>
        </h:panelGrid>

        <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field"/>
        <h:inputText value="#{wtRuleEditPag.priority}" maxlength="11" styleClass="input-text"/>
        <h:outputText escape="true" value="Объединение комплексов" styleClass="output-text"/>
        <h:selectOneListbox value="#{wtRuleEditPag.operationor}" size="1">
            <f:selectItem itemLabel="И" itemValue="false"/>
            <f:selectItem itemLabel="ИЛИ" itemValue="true"/>
        </h:selectOneListbox>

        <h:outputText value="Комплексы" styleClass="output-text"/>
        <h:panelGroup layout="block" style="height: 300px; overflow-y: scroll;">
            <h:selectManyCheckbox id="complexs" value="#{wtRuleEditPag.selectedComplexIds}" layout="pageDirection"
                                  styleClass="output-text">
                <f:selectItems value="#{wtRuleEditPag.availableComplexs}"/>
            </h:selectManyCheckbox>
        </h:panelGroup>


        <h:panelGrid columns="4" styleClass="borderless-grid">
            <a4j:commandButton value="Сохранить" action="#{wtRuleEditPag.updateRule}" reRender="workspaceTogglePanel"
                               styleClass="command-button"/>
            <a4j:commandButton value="Восстановить" action="#{wtRuleEditPag.reload}" reRender="workspaceTogglePanel"
                               ajaxSingle="true" styleClass="command-button"/>
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages"/>
        </h:panelGrid>

    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid borderless-grid-align-top" id="specialDatesFileLoaderPanel">
        <rich:panel>

            <h:panelGrid columns="2" id="filter">

                <h:outputText escape="true" value="Использовать правило для Web-АРМа" styleClass="output-text"/>

                <a4j:commandLink reRender="filter">
                    <f:setPropertyActionListener value="#{wtRuleEditPag}" target="#{wtRuleEditPag.wt}"/>
                    <h:selectBooleanCheckbox id="arm" value="#{wtRuleEditPag.wt}"/>
                </a4j:commandLink>

                <h:outputText escape="true" value="Тип комплекса" styleClass="output-text"
                              rendered="#{wtRuleEditPag.showFilter}"/>

                <h:selectOneMenu id="typeMenu" value="#{wtRuleEditPag.complexType}"
                                 style="width:300px;" styleClass="groupSelect" rendered="#{wtRuleEditPag.showFilter}">
                    <f:selectItems value="#{wtRuleEditPag.complexTypes}"/>
                </h:selectOneMenu>

                <h:outputText escape="true" value="Возрастная категория" styleClass="output-text"
                              rendered="#{wtRuleEditPag.showFilter}"/>

                <h:selectOneMenu id="ageMenu" value="#{wtRuleEditPag.ageGroup}"
                                 style="width:300px;" styleClass="groupSelect" rendered="#{wtRuleEditPag.showFilter}">
                    <f:selectItems value="#{wtRuleEditPag.ageGroups}"/>
                </h:selectOneMenu>

<%--                <h:outputText escape="true" value="Поставщик" styleClass="output-text"--%>
<%--                              rendered="#{wtRuleEditPag.showFilter}"/>--%>

<%--                <h:selectOneMenu id="supplierMenu" value="#{wtRuleEditPag.supplier}"--%>
<%--                                 style="width:300px;" styleClass="groupSelect" rendered="#{wtRuleEditPag.showFilter}">--%>
<%--                    <f:selectItems value="#{wtRuleEditPag.suppliers}"/>--%>
<%--                </h:selectOneMenu>--%>

                <a4j:outputPanel ajaxRendered="#{wtRuleEditPag.showFilter}" rendered="#{wtRuleEditPag.showFilter}">
                    <h:panelGrid styleClass="borderless-grid" columns="2">
                        <h:outputText escape="true" value="Список контрагентов" styleClass="output-text"/>
                        <h:panelGroup styleClass="borderless-div">

                            <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                                               reRender="modalContragentListSelectorPanel"
                                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                                               styleClass="command-link" style="width: 25px;">
                                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}"/>
                                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}"/>
                                <f:setPropertyActionListener value="#{wtRuleEditPag.contragentIds}"
                                                             target="#{mainPage.contragentListSelectPage.selectedIds}"/>
                            </a4j:commandButton>

                            <h:outputText value=" {#{wtRuleEditPag.contragentFilter}}" escape="true"
                                          styleClass="output-text"/>
                        </h:panelGroup>
                    </h:panelGrid>
                </a4j:outputPanel>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Отобразить" action="#{wtRuleEditPag.fillWtSelectedComplexes()}"
                                   reRender="workspaceTogglePanel"
                                   styleClass="command-button"/>
            </h:panelGrid>

            <h:panelGrid styleClass="borderless-grid">
                <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                               warnClass="warn-messages"/>
            </h:panelGrid>

            <a4j:status id="reportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
                </f:facet>
            </a4j:status>

            <rich:dataTable id="wtComplexesTable" value="#{wtRuleEditPag.wtSelectedComplexes}"
                            var="complex" rows="15" footerClass="data-table-footer" rowKeyVar="rowItemKey">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value=""/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="Название контрагента"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="ИД контрагента"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="ИД комплекса"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="Название"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="Возрастная категория"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="Цена"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="Тип"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>

                <rich:subTable value="#{complex}" var="complex" rowKeyVar="rowDetailKey"
                               columnClasses="center-aligned-column, left-aligned-column, center-aligned-column,
                           center-aligned-column, left-aligned-column, center-aligned-column, left-aligned-column,
                           center-aligned-column">

                    <%--       Чек-боксы--%>
                    <rich:column headerClass="column-header">
                        <h:selectBooleanCheckbox value="#{complex.checked}"/>
                    </rich:column>

                    <%--        Название контрагента--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.supplierName}"
                                      styleClass="output-text"/>
                    </rich:column>

                    <%--        ИД контрагента--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.idOfSupplier}"
                                      styleClass="output-text"/>
                    </rich:column>

                    <%--        ИД комплекса--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.idOfComplex}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Название--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.name}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Возрастная категория--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.wtAgeGroupItem.description}"
                                      styleClass="output-text"/>
                    </rich:column>

                    <%--        Цена, руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.price}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Тип--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.wtComplexGroupItem.description}"
                                      styleClass="output-text"/>
                    </rich:column>

                </rich:subTable>
                <f:facet name="footer">
                    <rich:datascroller for="wtComplexesTable" renderIfSinglePage="false"
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

        </rich:panel>
    </h:panelGrid>
</h:panelGrid>

