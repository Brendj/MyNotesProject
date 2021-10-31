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

    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsViewCashierCheckPrinterPanelGrid"
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

    <h:panelGrid columns="2" id="settingsViewSalesReportPrinterPanelGrid"
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

    <h:panelGrid columns="2" id="settingsViewCardBalanceReportPrinterPanelGrid"
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

    <h:panelGrid columns="2" id="settingsViewAutoPlanPaymentSettingPanelGrid"
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

    <h:panelGrid columns="2" id="settingsViewSubscriberFeedingPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==4}">

        <h:outputText escape="true" value="Количество дней, на которые оформляются заявки на поставку" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.dayRequest}" styleClass="input-text" style="width: 207px"/>

        <h:outputText escape="true" value="Количество дней, пропустив которые, клиент приостанавливает свою подписку" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.dayDeActivate}" styleClass="input-text" style="width: 207px"/>

        <h:outputText escape="true" value="Включить автоматическую приостановку/возобновление подписок на услугу АП в зависимости от посещения учреждения" styleClass="output-text" />
        <h:inputText readonly="true" value="#{(settingViewPage.setting.splitSettingValue.enableFeeding?'Включен':'Выключен')}"
                     styleClass="output-text" />
        <h:outputText escape="true" value="Количество часов, в течение которых запрещено редактировать заявки" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.hoursForbidChange}" styleClass="input-text" style="width: 207px" />

        <h:outputText escape="true" value="Шестидневный план рабочих дней" styleClass="output-text" />
        <h:inputText readonly="true" value="#{(settingViewPage.setting.splitSettingValue.sixWorkWeek?'Включен':'Выключен')}"
                     styleClass="output-text" />

        <h:outputText escape="true" value="Количество рабочих дней блокировки баланса с учетом стоимости питания, отмеченного в циклограмме" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.daysToForbidChangeInPos}" styleClass="input-text" style="width: 207px"/>

        <h:outputText escape="true" value="Количество дней, на которые создаются заявки вариативного питания" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.dayCreateVP}" styleClass="input-text" style="width: 207px"/>

        <h:outputText escape="true" value="Количество часов, в течение которых запрещено редактировать заявки вариативного питания" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.hoursForbidVP}" styleClass="input-text" style="width: 207px" />
    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsViewReplacingMissingBeneficiariesSettingPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==5}" >

        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.value}"
                     styleClass="output-text" />

        <h:outputText escape="true" value="Копуса (1 - только свой корпус / 2 - все корпуса)" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.orgParam}"
                     styleClass="output-text" />
    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsViewPreorderFeedingSettingPanelGrid" rendered="#{settingViewPage.setting.settingsId.id==6}">
        <h:outputText escape="true" value="Количество дней, в течение которых запрещено редактировать заявки платного питания" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingViewPage.setting.splitSettingValue.forbiddenDaysCount}" styleClass="input-text" style="width: 207px" />
    </h:panelGrid>

    <h:panelGrid columns="2" id="settingsViewPreorderAutopaySettingPanelGrid"
                 rendered="#{settingViewPage.setting.settingsId.id==7}" >

        <h:outputText escape="true" value="Активность для предзаказов" styleClass="output-text" />
        <h:inputText readonly="true" value="#{(settingViewPage.setting.splitSettingValue.isActivePreorder?'Включено':'Выключено')}"
                     styleClass="output-text" />

        <h:outputText escape="true" value="Время автооплаты (Ч:ММ)" styleClass="output-text" />
        <h:inputText value="#{settingViewPage.setting.splitSettingValue.processingTime_Preorder}" id="preorderAutopayTimeValue"
                     maxlength="5" readonly="true" styleClass="output-text" converterMessage="Не верный формат времени">
            <f:convertDateTime pattern="HH:mm"/>
        </h:inputText>

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