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
<h:panelGrid id="ruleEditPanel" binding="#{ruleEditPage.pageComponent}" styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Категории клиентов" styleClass="output-text" />
    <h:panelGroup>
        <a4j:commandButton id="categoryAjaxButton" value="..." action="#{mainPage.showCategoryListSelectPage}"
                           reRender="modalCategoryListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryListString}"
                                         target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" id="categoryListFilter" escape="true"
                      value=" {#{ruleEditPage.filter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Категории организаций" styleClass="output-text" />

    <h:panelGroup>
        <a4j:commandButton id="categoryOrgAjaxButton" value="..." action="#{mainPage.showCategoryOrgListSelectPage}"
                           reRender="modalCategoryOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryOrgListString}"
                                         target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true"
                      value=" {#{ruleEditPage.filterOrg}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field" />
    <h:selectOneMenu id="group" value="#{ruleEditPage.subCategory}" style="width:300px;" styleClass="groupSelect">
        <f:selectItems value="#{ruleEditPage.subCategories}"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Описание" styleClass="output-text required-field" />
    <h:inputText value="#{ruleEditPage.description}" maxlength="99" size="40" styleClass="input-text" />
    <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text" />
    <h:panelGrid columns="2">
        <h:inputText value="#{ruleEditPage.discountRate}" maxlength="3" styleClass="input-text" />
        <h:outputText escape="true" value="%" styleClass="output-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field" />
    <h:inputText value="#{ruleEditPage.priority}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Объединение комплексов" styleClass="output-text" />
    <h:selectOneListbox value="#{ruleEditPage.operationor}" size="1">
        <f:selectItem itemLabel="И" itemValue="false" />
        <f:selectItem itemLabel="ИЛИ" itemValue="true" />
    </h:selectOneListbox>
    <h:outputText value="Код МСП" styleClass="output-text"/>
    <h:selectOneListbox id="codeMSPEdit" value="#{ruleEditPage.codeMSP}" size="1"
                        styleClass="output-text" >
        <f:selectItems value="#{ruleEditPage.allMSP}" />
    </h:selectOneListbox>

    <h:outputText value="Комплексы" styleClass="output-text"/>
    <h:panelGroup layout="block" style="height: 300px; overflow-y: scroll;">
        <h:selectManyCheckbox id="complexs" value="#{ruleEditPage.selectedComplexIds}" layout="pageDirection"
                              styleClass="output-text">
            <f:selectItems value="#{ruleEditPage.availableComplexs}" />
        </h:selectManyCheckbox>
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{ruleEditPage.updateRule}" reRender="workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{ruleEditPage.reload}" reRender="workspaceTogglePanel"
                       ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>