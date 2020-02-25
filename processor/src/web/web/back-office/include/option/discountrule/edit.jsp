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
<%--@elvariable id="ruleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleEditPage"--%>
<h:panelGrid id="ruleEditPanel" binding="#{ruleEditPage.pageComponent}"
             styleClass="borderless-grid borderless-grid-align-top" columns="2">

    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Категории клиентов" styleClass="output-text"/>
        <h:panelGroup>
            <a4j:commandButton id="categoryAjaxButton" value="..." action="#{mainPage.showCategoryListSelectPage}"
                               reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryListString}"
                                             target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryListFilter" escape="true"
                          value=" {#{ruleEditPage.filter}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Категории организаций" styleClass="output-text"/>

        <h:panelGroup>
            <a4j:commandButton id="categoryOrgAjaxButton" value="..." action="#{mainPage.showCategoryOrgListSelectPage}"
                               reRender="modalCategoryOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryOrgListString}"
                                             target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true"
                          value=" {#{ruleEditPage.filterOrg}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field"/>
        <h:selectOneMenu id="group" value="#{ruleEditPage.subCategory}" style="width:300px;" styleClass="groupSelect">
            <f:selectItems value="#{ruleEditPage.subCategories}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Описание" styleClass="output-text required-field"/>
        <h:inputText value="#{ruleEditPage.description}" maxlength="99" size="40" styleClass="input-text"/>

        <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text"/>
        <h:panelGrid columns="2">
            <h:inputText value="#{ruleEditPage.discountRate}" maxlength="3" styleClass="input-text"/>
            <h:outputText escape="true" value="%" styleClass="output-text"/>
        </h:panelGrid>

        <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field"/>
        <h:inputText value="#{ruleEditPage.priority}" maxlength="11" styleClass="input-text"/>
        <h:outputText escape="true" value="Объединение комплексов" styleClass="output-text"/>
        <h:selectOneListbox value="#{ruleEditPage.operationor}" size="1">
            <f:selectItem itemLabel="И" itemValue="false"/>
            <f:selectItem itemLabel="ИЛИ" itemValue="true"/>
        </h:selectOneListbox>

        <h:outputText value="Комплексы" styleClass="output-text"/>
        <h:panelGroup layout="block" style="height: 300px; overflow-y: scroll;">
            <h:selectManyCheckbox id="complexs" value="#{ruleEditPage.selectedComplexIds}" layout="pageDirection"
                                  styleClass="output-text">
                <f:selectItems value="#{ruleEditPage.availableComplexs}"/>
            </h:selectManyCheckbox>
        </h:panelGroup>


        <h:panelGrid columns="4" styleClass="borderless-grid">
            <a4j:commandButton value="Сохранить" action="#{ruleEditPage.updateRule}" reRender="workspaceTogglePanel"
                               styleClass="command-button"/>
            <a4j:commandButton value="Восстановить" action="#{ruleEditPage.reload}" reRender="workspaceTogglePanel"
                               ajaxSingle="true" styleClass="command-button"/>
        </h:panelGrid>

        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages"/>
        </h:panelGrid>

    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid borderless-grid-align-top" id="specialDatesFileLoaderPanel">
        <rich:panel>

            <h:panelGrid columns="2">

                <h:outputText escape="true" value="Использовать правило для Web-АРМа" styleClass="output-text"/>
                <h:selectBooleanCheckbox id="arm" value="true">
                </h:selectBooleanCheckbox>

                <h:outputText escape="true" value="Тип комплекса" styleClass="output-text"/>
                <h:selectOneMenu id="type" value="#{ruleEditPage.complexType}" style="width:300px;"
                                 styleClass="groupSelect">
                    <f:selectItems value="#{ruleEditPage.complexTypes}"/>
                </h:selectOneMenu>

                <h:outputText escape="true" value="Возрастная категория" styleClass="output-text"/>
                <h:selectOneMenu id="age" value="#{ruleEditPage.ageGroup}" style="width:300px;"
                                 styleClass="groupSelect">
                    <f:selectItems value="#{ruleEditPage.ageGroups}"/>
                </h:selectOneMenu>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Отобразить" action="#{ruleEditPage.updateRule}"
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

            <rich:dataTable id="wtDiscountRules" value="#{RuleEditPage.wtRules}"
                            var="wtrule" rows="25"
                            footerClass="data-table-footer" rowKeyVar="rowItemKey">
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


                <rich:subTable value="#{wtrule}" var="item" rowKeyVar="rowDetailKey"
                               columnClasses="center-aligned-column, left-aligned-column, left-aligned-column,
                           left-aligned-column, left-aligned-column, center-aligned-column, left-aligned-column,
                           center-aligned-column">

                    <%--       Чек-боксы--%>
                    <rich:column headerClass="column-header">
                        <h:panelGroup layout="block" style="height: 300px; overflow-y: scroll;">
                            <h:selectManyCheckbox id="complexs" value="#{item.selectedComplexes}" layout="pageDirection"
                                                  styleClass="output-text">
                                <f:selectItems value="#{item.availableComplexes}"/>
                            </h:selectManyCheckbox>
                        </h:panelGroup>
                    </rich:column>

                    <%--        Название контрагента--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.contragentName}" styleClass="output-text"/>
                    </rich:column>

                    <%--        ИД контрагента--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.contragentId}" styleClass="output-text"/>
                    </rich:column>

                    <%--        ИД комплекса--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.complexId}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Название--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.complexName}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Возрастная категория--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.ageCategory}" styleClass="output-text"/>
                    </rich:column>

                    <%--        Цена, руб--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.price}" styleClass="output-text"
                                      converter="copeckSumConverter"/>
                    </rich:column>

                    <%--        Тип--%>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.complexType}" styleClass="output-text"/>
                    </rich:column>

                </rich:subTable>
            </rich:dataTable>
        </rich:panel>
    </h:panelGrid>
</h:panelGrid>

