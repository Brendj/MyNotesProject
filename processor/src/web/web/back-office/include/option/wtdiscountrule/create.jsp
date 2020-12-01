<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель создания правила --%>
<%--@elvariable id="wtRuleCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.wtRuleCreatePage"--%>
<h:panelGrid id="wtRuleCreatePanel" binding="#{wtRuleCreatePage.pageComponent}"
             styleClass="borderless-grid borderless-grid-align-top" columns="2">

    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Категории клиентов" styleClass="output-text"/>

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showCategoryListSelectPage}"
                               reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;"/>
            <h:outputText styleClass="output-text" id="categoryListFilter" escape="true"
                          value=" {#{wtRuleCreatePage.filter}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Категории организаций" styleClass="output-text"/>

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showCategoryOrgListSelectPage}"
                               reRender="modalCategoryOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;"/>
            <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true"
                          value=" {#{wtRuleCreatePage.filterOrg}}"/>
        </h:panelGroup>


        <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field"/>
        <h:selectOneMenu id="group" value="#{wtRuleCreatePage.subCategory}" style="width:300px;" styleClass="groupSelect">
            <f:selectItems value="#{wtRuleCreatePage.subCategories}"/>
        </h:selectOneMenu>
        
        <h:outputText escape="true" value="Описание" styleClass="output-text required-field"/>
        <h:inputText value="#{wtRuleCreatePage.description}" maxlength="32" styleClass="input-text"/>
        
        <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text"/>
        <h:panelGrid columns="2">
            <h:inputText value="#{wtRuleCreatePage.discountRate}" maxlength="3" styleClass="input-text"/>
            <h:outputText escape="true" value="%" styleClass="output-text"/>
        </h:panelGrid>
        
        <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field"/>
        <h:inputText value="#{wtRuleCreatePage.priority}" maxlength="11" styleClass="input-text"/>
        
        <h:outputText escape="true" value="Тип условия" styleClass="output-text"/>
        <h:selectOneListbox value="#{wtRuleCreatePage.operationOr}" size="1">
            <f:selectItem itemLabel="И" itemValue="false"/>
            <f:selectItem itemLabel="ИЛИ" itemValue="true"/>
        </h:selectOneListbox>

        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages"/>
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Зарегистрировать правило" action="#{wtRuleCreatePage.createRule}"
                               reRender="wtRuleCreatePanel" styleClass="command-button"/>
        </h:panelGrid>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid borderless-grid-align-top" id="specialDatesFileLoaderPanel">
        <rich:panel>

            <h:panelGrid columns="2" id="filter">

                <h:outputText escape="true" value="Тип комплекса" styleClass="output-text"
                              rendered="true"/>
                <h:selectOneMenu id="typeMenu" value="#{wtRuleCreatePage.complexType}"
                                 style="width:300px;" styleClass="groupSelect" rendered="true">
                    <f:selectItems value="#{wtRuleCreatePage.complexTypes}"/>
                </h:selectOneMenu>

                <h:outputText escape="true" value="Возрастная категория" styleClass="output-text"
                              rendered="true"/>
                <h:selectOneMenu id="ageMenu" value="#{wtRuleCreatePage.ageGroup}"
                                 style="width:300px;" styleClass="groupSelect" rendered="true">
                    <f:selectItems value="#{wtRuleCreatePage.ageGroups}"/>
                </h:selectOneMenu>

                <h:outputText escape="true" value="Рацион" styleClass="output-text" rendered="true"/>
                <h:selectOneMenu id="dietMenu" value="#{wtRuleCreatePage.dietType}"
                                 style="width:300px;" styleClass="groupSelect" rendered="true">
                    <f:selectItems value="#{wtRuleCreatePage.dietTypes}"/>
                </h:selectOneMenu>
            </h:panelGrid>

            <a4j:outputPanel ajaxRendered="true" rendered="true">
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText escape="true" value="Список контрагентов" styleClass="output-text"/>
                    <h:panelGroup styleClass="borderless-div">

                        <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                                           reRender="modalContragentListSelectorPanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                                           styleClass="command-link" style="width: 25px;">
                            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}"/>
                            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}"/>
                            <f:setPropertyActionListener value="#{wtRuleCreatePage.contragentIds}"
                                                         target="#{mainPage.contragentListSelectPage.selectedIds}"/>
                        </a4j:commandButton>

                        <h:outputText value=" {#{wtRuleCreatePage.contragentFilter}}" escape="true"
                                      styleClass="output-text"/>
                    </h:panelGroup>
                </h:panelGrid>
            </a4j:outputPanel>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Отобразить" action="#{wtRuleCreatePage.fillWtSelectedComplexes()}"
                                   reRender="workspaceTogglePanel"
                                   styleClass="command-button"/>
            </h:panelGrid>

            <a4j:status id="reportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
                </f:facet>
            </a4j:status>

            <rich:dataTable id="wtComplexesTable" value="#{wtRuleCreatePage.wtSelectedComplexes}"
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
                            <h:outputText escape="true" value="Рацион"/>
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

                    <%--        Рацион--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{complex.wtComplex.wtDietType.description}" styleClass="output-text" />
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