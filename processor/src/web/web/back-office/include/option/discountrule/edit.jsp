<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования правила --%>
<%--@elvariable id="ruleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleEditPage"--%>
<h:panelGrid id="ruleEditPanel" binding="#{ruleEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Категории клиентов" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton id="categoryAjaxButton" value="..." action="#{mainPage.showCategoryListSelectPage}" reRender="modalCategoryListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryListString}" target="#{mainPage.categoryFilterOfSelectCategoryListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryListFilter" escape="true" value=" {#{ruleEditPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Категории организаций" styleClass="output-text" />

        <h:panelGroup>
            <a4j:commandButton id="categoryOrgAjaxButton" value="..." action="#{mainPage.showCategoryOrgListSelectPage}" reRender="modalCategoryOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{ruleEditPage.idOfCategoryOrgListString}" target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}"/>
              </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true" value=" {#{ruleEditPage.filterOrg}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Описание" styleClass="output-text required-field" />
        <h:inputText value="#{ruleEditPage.description}" maxlength="99" size="40" styleClass="input-text" />
        <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field" />
        <h:inputText value="#{ruleEditPage.priority}" maxlength="11" styleClass="input-text" />
        <h:outputText escape="true" value="Объединение комплексов" styleClass="output-text" />
        <h:selectOneListbox value="#{ruleEditPage.operationor}">
            <f:selectItem itemLabel="И" itemValue="false"/>
            <f:selectItem itemLabel="ИЛИ" itemValue="true"/>
        </h:selectOneListbox>
        <h:outputText escape="true" value="Комплекс 0" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex0}"/>
        <h:outputText escape="true" value="Комплекс 1" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex1}"/>
        <h:outputText escape="true" value="Комплекс 2" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex2}"/>
        <h:outputText escape="true" value="Комплекс 3" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex3}"/>
        <h:outputText escape="true" value="Комплекс 4" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex4}"/>
        <h:outputText escape="true" value="Комплекс 5" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex5}"/>
        <h:outputText escape="true" value="Комплекс 6" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex6}"/>
        <h:outputText escape="true" value="Комплекс 7" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex7}"/>
        <h:outputText escape="true" value="Комплекс 8" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex8}"/>
        <h:outputText escape="true" value="Комплекс 9" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{ruleEditPage.complex9}"/>

    </h:panelGrid>
    <h:panelGrid columns="4" styleClass="borderless-grid">
        <a4j:commandButton value="Сохранить" action="#{ruleEditPage.updateRule}" reRender="mainMenu, workspaceTogglePanel"
                           styleClass="command-button" />
        <a4j:commandButton value="Восстановить" action="#{ruleEditPage.reload}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>