<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 05.04.13
  Time: 16:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="settingEditPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingEditPage"--%>
<h:panelGrid id="settingsEditPage" binding="#{settingEditPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Глобальный идентификатор" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.globalId}" styleClass="output-text" />

        <h:outputText escape="true" value="GUID" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.guid}" styleClass="output-text" />

        <h:outputText escape="true" value="Версия" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.globalVersion}" styleClass="output-text" />

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{settingEditPage.orgItem.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Тип устройства" styleClass="output-text" />
        <h:selectOneMenu value="#{settingEditPage.settingsIds}" styleClass="input-text">
            <f:selectItems value="#{settingEditPage.settingsIdEnumTypeMenu.items}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneMenu value="#{settingEditPage.setting.deletedState}" styleClass="input-text">
            <f:selectItem itemValue="true" itemLabel="Удален"/>
            <f:selectItem itemValue="false" itemLabel="Активен"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.settingText}" styleClass="output-text" />
    </h:panelGrid>

    <h:panelGrid columns="1" rendered="#{settingEditPage.settingsIds==1}" columnClasses="center-aligned-column">
        <h:outputText escape="true" value="Наименование принтреа" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.name}" styleClass="output-text long-field" />
        <h:panelGrid columns="5" columnClasses="center-aligned-column">

            <h:outputText escape="true" value="Общая ширина ленты" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки товар " styleClass="output-text" />

            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.s}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.a}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.b}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.c}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.d}" styleClass="output-text" />

        </h:panelGrid>
    </h:panelGrid>

    <h:panelGrid columns="1" rendered="#{settingEditPage.settingsIds==2}" columnClasses="center-aligned-column">
        <h:outputText escape="true" value="Наименование принтреа" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.name}" styleClass="output-text long-field" />
        <h:panelGrid columns="5" columnClasses="center-aligned-column">

            <h:outputText escape="true" value="Общая ширина ленты" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки товар " styleClass="output-text" />


            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.s}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.a}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.b}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.c}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.d}" styleClass="output-text" />

        </h:panelGrid>
    </h:panelGrid>

    <h:panelGrid columns="1" rendered="#{settingEditPage.settingsIds==3}" columnClasses="center-aligned-column">
        <h:outputText escape="true" value="Наименование принтреа" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.name}" styleClass="output-text long-field" />
        <h:panelGrid columns="5" columnClasses="center-aligned-column">

            <h:outputText escape="true" value="Общая ширина ленты" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <h:outputText escape="true" value="Ширина колонки товар " styleClass="output-text" />


            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.s}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.a}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.b}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.c}" styleClass="output-text" />
            <h:inputText readonly="true" value="#{settingEditPage.parserBySettingValue.d}" styleClass="output-text" />

        </h:panelGrid>
    </h:panelGrid>

</h:panelGrid>