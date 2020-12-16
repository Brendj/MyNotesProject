<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>

<%--@elvariable id="mainPage" type="ru.axetta.ecafe.processor.web.ui.MainPage"--%>
<h:panelGrid id="feedingSettingEditPage" binding="#{mainPage.feedingSettingEditPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Идентификатор настройки" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingEditPage.idOfSetting}" styleClass="output-text" />

        <h:outputText escape="true" value="Название" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingEditPage.settingName}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Сумма лимита" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingEditPage.limit}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Сумма скидки" styleClass="output-text" />
        <h:inputText value="#{mainPage.feedingSettingEditPage.discount}" styleClass="output-text long-field" converter="copeckSumConverter"/>

        <h:outputText escape="true" value="Учитывать скидку при оплате платного плана" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.feedingSettingEditPage.useDiscount}" styleClass="output-text">
            <a4j:support event="onchange" reRender="feedingSettingEditPage" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Список организаций" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.feedingSettingEditPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text long-field" escape="true" value=" {#{mainPage.feedingSettingEditPage.filter}}"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Дата изменения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingEditPage.lastUpdate}" styleClass="output-text long-field" converter="timeConverter"/>

        <h:outputText escape="true" value="Пользователь" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.feedingSettingEditPage.userName}" styleClass="output-text long-field" />

    </h:panelGrid>
</h:panelGrid>

<h:panelGrid columns="2">
    <a4j:commandButton value="Сохранить" action="#{mainPage.feedingSettingEditPage.updateSetting}" reRender="mainMenu, workspaceTogglePanel"
                   styleClass="command-button" />
    <%--<a4j:commandButton value="Удалить" action="#{mainPage.deleteFeedingSetting}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />--%>
    <a4j:commandButton value="Удалить" ajaxSingle="true" oncomplete="#{rich:component('removeFeedingSettingDeletePanel')}.show()"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>

<rich:modalPanel id="removeFeedingSettingDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText value="Удаление настройки платного питания" styleClass="output-text" />
    </f:facet>
    <table class="borderless-grid" width="100%">
        <tr>
            <td style="text-align: center;">
                <h:outputText value="Удалить настройку?"
                              styleClass="output-text" />
            </td>
        </tr>
        <tr>
            <td style="text-align: center;">
                <h:panelGroup styleClass="borderless-div">
                    <a4j:commandButton value="Да" action="#{mainPage.deleteFeedingSetting}"
                                       oncomplete="#{rich:component('removeFeedingSettingDeletePanel')}.hide();"
                                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel"/>
                    <a4j:commandButton value="Отмена" styleClass="command-button"
                                       onclick="#{rich:component('removeFeedingSettingDeletePanel')}.hide();return false;" />
                </h:panelGroup>
            </td>
        </tr>
    </table>
</rich:modalPanel>

<a4j:status id="feedingSettingEditStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>