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
    <h:panelGrid columns="2" id="settingsEditMainPanelGrid">
        <h:outputText escape="true" value="Глобальный идентификатор" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.globalId}" styleClass="output-text" />

        <h:outputText escape="true" value="GUID" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.guid}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Версия" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingEditPage.setting.globalVersion}" styleClass="output-text" />

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{settingEditPage.orgItem.shortName}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Тип устройства" styleClass="output-text" />
        <h:inputText readonly="true" disabled="true" value="#{settingEditPage.setting.settingsId}"
                     styleClass="output-text long-field" />

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneMenu value="#{settingEditPage.setting.deletedState}" styleClass="input-text">
            <f:selectItem itemValue="true" itemLabel="Удален"/>
            <f:selectItem itemValue="false" itemLabel="Активен"/>
        </h:selectOneMenu>

    </h:panelGrid>

    <h:panelGrid id="settingValueConfig">
        <h:panelGrid columns="2" id="settingsEditCashierCheckPrinterPanelGrid"
                     rendered="#{settingEditPage.settingsIds==0}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="true" inputClass="input-text" itemClass="output-text"
                           suggestionValues="#{settingEditPage.allPrinters}"
                           value="#{settingEditPage.parserBySettingValue.a}" />

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.h}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false"
                           defaultLabel="#{settingEditPage.parserBySettingValue.b}"
                           value="#{settingEditPage.parserBySettingValue.b}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.c}"
                           value="#{settingEditPage.parserBySettingValue.c}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.e}"
                           value="#{settingEditPage.parserBySettingValue.e}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
                <f:selectItem itemValue="4" itemLabel="4"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.f}"
                           value="#{settingEditPage.parserBySettingValue.f}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="7" itemLabel="7"/>
                <f:selectItem itemValue="8" itemLabel="8"/>
                <f:selectItem itemValue="9" itemLabel="9"/>
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="11" itemLabel="11"/>
                <f:selectItem itemValue="12" itemLabel="12"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки цена" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.g}"
                           value="#{settingEditPage.parserBySettingValue.g}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="6" itemLabel="6"/>
                <f:selectItem itemValue="7" itemLabel="7"/>
                <f:selectItem itemValue="8" itemLabel="8"/>
                <f:selectItem itemValue="9" itemLabel="9"/>
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="11" itemLabel="11"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
            <h:inputText id="resultvalueCashierCheckPrinterPanel" readonly="true" disabled="true"
                         value="#{settingEditPage.parserBySettingValue.valuesByD}" styleClass="output-text" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsEditSalesReportPrinterPanelGrid"
                     rendered="#{settingEditPage.settingsIds==1}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="true" inputClass="input-text" itemClass="output-text"
                           suggestionValues="#{settingEditPage.allPrinters}"
                           value="#{settingEditPage.parserBySettingValue.a}"/>

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.g}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.b}"
                           value="#{settingEditPage.parserBySettingValue.b}">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.c}"
                           value="#{settingEditPage.parserBySettingValue.c}">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.e}"
                           value="#{settingEditPage.parserBySettingValue.e}">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="6" itemLabel="6"/>
                <f:selectItem itemValue="7" itemLabel="7"/>
                <f:selectItem itemValue="8" itemLabel="8"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.f}"
                           value="#{settingEditPage.parserBySettingValue.f}">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="11" itemLabel="11"/>
                <f:selectItem itemValue="12" itemLabel="12"/>
                <f:selectItem itemValue="13" itemLabel="13"/>
                <f:selectItem itemValue="14" itemLabel="14"/>
                <f:selectItem itemValue="15" itemLabel="15"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
            <h:inputText id="resultvalueSalesReportPrinter" readonly="true" disabled="true"
                         value="#{settingEditPage.parserBySettingValue.valuesByD}" styleClass="output-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsEditCardBalanceReportPrinterPanelGrid"
                     rendered="#{settingEditPage.settingsIds==2}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="true" inputClass="input-text" itemClass="output-text"
                           suggestionValues="#{settingEditPage.allPrinters}"
                           value="#{settingEditPage.parserBySettingValue.a}"/>

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.g}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.b}"
                           value="#{settingEditPage.parserBySettingValue.b}">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.c}"
                           value="#{settingEditPage.parserBySettingValue.c}">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки номер карты" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.e}"
                           value="#{settingEditPage.parserBySettingValue.e}">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="8" itemLabel="8"/>
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="12" itemLabel="12"/>
                <f:selectItem itemValue="14" itemLabel="14"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки баланс" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingEditPage.parserBySettingValue.f}"
                           value="#{settingEditPage.parserBySettingValue.f}">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="7" itemLabel="7"/>
                <f:selectItem itemValue="8" itemLabel="8"/>
                <f:selectItem itemValue="9" itemLabel="9"/>
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="11" itemLabel="11"/>
                <f:selectItem itemValue="12" itemLabel="12"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки наименование" styleClass="output-text" />
            <h:inputText id="resultvalueCardBalanceReportPrinter" readonly="true" disabled="true"
                         value="#{settingEditPage.parserBySettingValue.valuesByD}" styleClass="output-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsAutoPlanPaymentSettingPanelGrid"
                     rendered="#{settingEditPage.settingsIds==3}" >
            <h:outputText escape="true" value="Включить/Выключить устройство" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{settingEditPage.parserBySettingValue.offOnFlag}">
                <f:selectItem itemValue="true"/>
                <f:selectItem itemValue="false"/>
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Время автооплаты (Ч:ММ)" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.payTime}" id="payTimeValue"
                         maxlength="5"  styleClass="output-text" converterMessage="Не верный формат времени">
                <f:convertDateTime pattern="HH:mm"/>
            </h:inputText>

            <h:outputText escape="true" value="Порог срабатывания" styleClass="output-text" />
            <rich:inputNumberSlider value="#{settingEditPage.parserBySettingValue.porog}" maxValue="100"
                                    step="1" showToolTip="true" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreateSubscriberFeedingPanelGrid"
                     rendered="#{settingEditPage.settingsIds==4}">

            <h:outputText escape="true" value="Количество дней, отводимое на активизацию услуги" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.dayActivae}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Количество дней, на которые оформляются заявки на поставку" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.dayRequest}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Максимальное количество дней, на котороые сформированные заявки запрещено редактировать" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.maxDayRequest}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Количество дней, пропустив которые, клиент приостанавливает свою подписку" styleClass="output-text" />
            <h:inputText value="#{settingEditPage.parserBySettingValue.dayDeActivae}" styleClass="input-text" style="width: 207px"/>

        </h:panelGrid>

    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton action="#{settingEditPage.save}" value="Сохранить" reRender="settingsEditPage"/>
        <a4j:commandButton action="#{settingEditPage.reload}" value="Востановить" reRender="settingsEditPage"/>
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid" id="settingsMessagePanelGrid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>