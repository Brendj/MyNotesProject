<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%--@elvariable id="mainPage" type="ru.axetta.ecafe.processor.web.ui.MainPage"--%>
<h:panelGrid id="feedingSettingViewPage" binding="#{mainPage.feedingSettingViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Идентификатор настройки" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.idOfSetting}" styleClass="output-text" />

        <h:outputText escape="true" value="Название" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.settingName}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Сумма лимита" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.limit}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Сумма скидки" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.discount}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Учитывать скидку при оплате платного плана" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.feedingSettingViewPage.useDiscount}" styleClass="output-text" readonly="true">
            <a4j:support event="onchange" reRender="feedingSettingViewPage" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Список организаций" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.orgName}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Дата изменения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.lastUpdate}" styleClass="output-text long-field" converter="timeConverter"/>

        <h:outputText escape="true" value="Пользователь" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingViewPage.userName}" styleClass="output-text long-field" />

    </h:panelGrid>
</h:panelGrid>
<a4j:commandButton value="Редактировать" action="#{mainPage.showFeedingSettingEditPage}"
                   styleClass="command-button" reRender="mainMenu, workspaceForm">
    <f:setPropertyActionListener value="#{mainPage.feedingSettingViewPage.idOfSetting}" target="#{mainPage.feedingSettingEditPage.idOfSetting}" />
    <f:setPropertyActionListener value="#{mainPage.feedingSettingViewPage.idOfSetting}" target="#{mainPage.selectedIdOfFeedingSetting}" />
</a4j:commandButton>

<a4j:status id="feedingSettingViewStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>