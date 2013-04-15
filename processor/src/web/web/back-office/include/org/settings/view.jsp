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
<%--@elvariable id="settingViewPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingViewPage"--%>
<h:panelGrid id="settingsEditPage" binding="#{settingViewPage.pageComponent}"
             styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Глобальный идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.globalId}" styleClass="output-text" />

    <h:outputText escape="true" value="GUID" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.guid}" styleClass="output-text long-field" />

    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.orgItem.shortName}" styleClass="output-text long-field" />

    <h:outputText escape="true" value="Версия" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.globalVersion}" styleClass="output-text" />

    <h:outputText escape="true" value="Тип устройства" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.settingsId}" styleClass="output-text long-field" />

    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.deletedState}" styleClass="output-text" />

    <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.settingText}" styleClass="output-text long-field" />

    <h:outputText escape="true" value="Параметры" styleClass="output-text" />
    <h:inputText readonly="true" value="#{settingViewPage.setting.settingValue}" styleClass="output-text long-field" />

</h:panelGrid>