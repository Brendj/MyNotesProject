<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%--@elvariable id="mainPage" type="ru.axetta.ecafe.processor.web.ui.MainPage"--%>
<h:panelGrid id="feedingSettingCreatePage" binding="#{mainPage.feedingSettingCreatePage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Название" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingCreatePage.settingName}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Сумма лимита" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingCreatePage.limit}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Сумма скидки" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingCreatePage.discount}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Скидка для комплексов" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.feedingSettingCreatePage.useDiscount}" styleClass="output-text">
            <a4j:support event="onchange" reRender="feedingSettingCreatePage" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Скидка для буфетной продукции" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.feedingSettingCreatePage.useDiscountBuffet}" styleClass="output-text">
            <a4j:support event="onchange" reRender="feedingSettingCreatePage" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Список организаций" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.feedingSettingCreatePage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text long-field" escape="true" value=" {#{mainPage.feedingSettingCreatePage.filter}}"/>
        </h:panelGroup>
    </h:panelGrid>
</h:panelGrid>
<a4j:commandButton value="Сохранить" action="#{mainPage.feedingSettingCreatePage.createFeedingSetting}"
                   styleClass="command-button" reRender="feedingSettingCreatePage"/>

<a4j:status id="feedingSettingCreateStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>