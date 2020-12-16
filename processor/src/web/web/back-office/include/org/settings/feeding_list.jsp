<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="mainPagePage" type="ru.axetta.ecafe.processor.web.ui.MainPage"--%>
<h:panelGrid id="feedingSettingsListPage" binding="#{mainPage.feedingSettingsListPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:dataTable id="feedingSettingsListTable" value="#{mainPage.feedingSettingsListPage.items}" var="setting">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Идентификатор"/>
            </f:facet>
            <h:outputText value="#{setting.idOfSetting}" converter="copeckSumConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Название"/>
            </f:facet>
            <a4j:commandLink value="#{setting.settingName}" action="#{mainPage.showFeedingSettingViewPage}"
                             reRender="mainMenu, workspaceForm">
                <f:setPropertyActionListener value="#{setting.idOfSetting}" target="#{mainPage.feedingSettingViewPage.idOfSetting}" />
                <f:setPropertyActionListener value="#{setting.idOfSetting}" target="#{mainPage.selectedIdOfFeedingSetting}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Сумма лимита" />
            </f:facet>
            <h:outputText value="#{setting.limit}" converter="copeckSumConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Сумма скидки" />
            </f:facet>
            <h:outputText value="#{setting.discount}" converter="copeckSumConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Учитывать скидку при оплате" />
            </f:facet>
            <h:outputText value="#{setting.useDiscountString}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата изменения"/>
            </f:facet>
            <h:outputText value="#{setting.lastUpdate}" converter="timeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Пользователь" />
            </f:facet>
            <h:outputText value="#{setting.userName}"/>
        </rich:column>
        <rich:column styleClass="center-aligned-column">
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showFeedingSettingEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{setting.idOfSetting}" target="#{mainPage.feedingSettingEditPage.idOfSetting}" />
                <f:setPropertyActionListener value="#{setting.idOfSetting}" target="#{mainPage.selectedIdOfFeedingSetting}" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>
</h:panelGrid>

<a4j:status id="feedingSettingsListStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>