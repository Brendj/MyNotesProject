<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%--@elvariable id="categoryOrgCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgCreatePage"--%>
<h:panelGrid id="categoryOrgCreateTable" binding="#{categoryOrgCreatePage.pageComponent}"
                 styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Название" styleClass="output-text required-field" />
    <h:inputText value="#{categoryOrgCreatePage.categoryName}" maxlength="32" styleClass="input-text" />

    <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />

    <h:panelGroup>
        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="#{categoryOrgCreatePage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" id="categoryListFilter" escape="true" value=" {#{categoryOrgCreatePage.filter}}" />
    </h:panelGroup>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Зарегистрировать категорию" action="#{categoryOrgCreatePage.createCategoryOrg}"
                           reRender="categoryOrgCreateTable" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>

