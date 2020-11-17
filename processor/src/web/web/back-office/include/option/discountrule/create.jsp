<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания правила --%>
<%--@elvariable id="ruleCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleCreatePage"--%>
<h:panelGrid id="ruleCreatePanel" binding="#{ruleCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Категории клиентов" styleClass="output-text" />

    <h:panelGroup>
    <a4j:commandButton value="..." action="#{mainPage.showCategoryListSelectPage}" reRender="modalCategoryListSelectorPanel"
                     oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                     styleClass="command-link" style="width: 25px;" />
        <h:outputText styleClass="output-text" id="categoryListFilter" escape="true" value=" {#{ruleCreatePage.filter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Категории организаций" styleClass="output-text" />

    <h:panelGroup>
        <a4j:commandButton value="..." action="#{mainPage.showCategoryOrgListSelectPage}" reRender="modalCategoryOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
        <h:outputText styleClass="output-text" id="categoryOrgListFilter" escape="true" value=" {#{ruleCreatePage.filterOrg}}" />
    </h:panelGroup>


    <h:outputText escape="true" value="Супер-категория" styleClass="output-text required-field" />
    <h:selectOneMenu id="group" value="#{ruleCreatePage.subCategory}" style="width:300px;" styleClass="groupSelect">
        <f:selectItems value="#{ruleCreatePage.subCategories}"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Описание" styleClass="output-text required-field" />
    <h:inputText value="#{ruleCreatePage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text" />
    <h:panelGrid columns="2">
        <h:inputText value="#{ruleCreatePage.discountRate}" maxlength="3" styleClass="input-text" />
        <h:outputText escape="true" value="%" styleClass="output-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Приоритет" styleClass="output-text required-field" />
    <h:inputText value="#{ruleCreatePage.priority}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Тип условия" styleClass="output-text" />
    <h:selectOneListbox value="#{ruleCreatePage.operationOr}" size="1">
        <f:selectItem itemLabel="И" itemValue="false"/>
        <f:selectItem itemLabel="ИЛИ" itemValue="true"/>
    </h:selectOneListbox>
    <h:outputText value="Код МСП" styleClass="output-text"/>
    <h:selectOneListbox id="codeMSP" value="#{ruleCreatePage.codeMSP}"  size="1"
                        styleClass="output-text">
        <f:selectItems value="#{ruleCreatePage.allMSP}" />
    </h:selectOneListbox>

    <h:outputText value="Комплексы" styleClass="output-text"/>
    <h:panelGroup layout="block" style="height: 300px; overflow-y: scroll;">
        <h:selectManyCheckbox id="complexs" value="#{ruleCreatePage.selectedComplexIds}"
                              layout="pageDirection" styleClass="output-text">
            <f:selectItems value="#{ruleCreatePage.availableComplexs}"/>
        </h:selectManyCheckbox>
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid>

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" columns="2">
    <a4j:commandButton value="Зарегистрировать правило" action="#{ruleCreatePage.createRule}"
                       reRender="ruleCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>