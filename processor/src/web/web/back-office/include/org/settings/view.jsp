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
<h:panelGrid id="settingsViewPage" binding="#{settingViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2">

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
        <h:inputText readonly="true" value="#{(settingViewPage.setting.deletedState?'Удален':'Активен')}" styleClass="output-text" />

    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsEditCashierCheckPrinterPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==0}">

        <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.a}"
                     styleClass="input-text long-field"/>

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.h}" styleClass="input-text long-field" />

        <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.b}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.c}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.e}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.f}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки цена" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.g}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.d}"
                     styleClass="input-text"/>
    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsEditSalesReportPrinterPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==1}">

        <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.a}"
                     styleClass="input-text long-field"/>

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.g}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.b}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.c}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.e}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.f}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.d}"
                     styleClass="input-text"/>

    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsEditCardBalanceReportPrinterPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==2}">

        <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.a}"
                     styleClass="input-text long-field"/>

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.g}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.b}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.c}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки номер карты" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.e}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки баланс" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.f}"
                     styleClass="input-text"/>

        <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.d}"
                     styleClass="input-text"/>
    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsAutoPlanPaymentSettingPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==3}" >

        <h:outputText escape="true" value="Включить/Выключить устройство" styleClass="output-text" />
        <h:inputText readonly="true" value="#{(settingViewPage.setting.splitSettingValue.offOnFlag?'Включен':'Выключен')}"
                     styleClass="output-text" />

        <h:outputText escape="true" value="Время автооплаты (Ч:ММ)" styleClass="output-text" />
        <h:inputText value="#{settingViewPage.setting.splitSettingValue.payTime}" id="payTimeValue"
                     maxlength="5" readonly="true" styleClass="output-text" converterMessage="Не верный формат времени">
            <f:convertDateTime pattern="HH:mm"/>
        </h:inputText>

        <h:outputText escape="true" value="Порог срабатывания" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.porog}"
                     styleClass="output-text" />
    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton action="#{settingViewPage.edit}" value="Редактировать" styleClass="command-button" reRender="mainMenu, workspaceTogglePanel"/>
    </h:panelGrid>

</h:panelGrid>

<a4j:status id="settingsViewStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>