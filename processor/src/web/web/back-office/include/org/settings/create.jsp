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
<%--@elvariable id="settingCreatePage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingCreatePage"--%>
<h:panelGrid id="settingsCreatePage" binding="#{settingCreatePage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2" id="settingsCreateMainPanelGrid">

        <h:outputText escape="true" value="GUID" styleClass="output-text" />
        <h:inputText readonly="true" value="#{settingCreatePage.setting.guid}" styleClass="output-text long-field" />

        <h:outputText escape="true" value="Организация" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{settingCreatePage.orgItem.shortName}" readonly="true"
                         styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Тип устройства" styleClass="output-text required-field" />
        <rich:comboBox width="320" inputClass="input-text" itemClass="output-text"
                       itemSelectedClass="output-text-font"
                       suggestionValues="#{settingCreatePage.settingsIdses}" defaultLabel="#{settingCreatePage.settingsId}"
                       value="#{settingCreatePage.settingsId}" enableManualInput="false"
                       valueChangeListener="#{settingCreatePage.valueChangeListener}">
            <a4j:support event="onselect" reRender="settingValueConfig" />
            <a4j:support event="onchange" reRender="settingValueConfig" />
        </rich:comboBox>

    </h:panelGrid>

    <h:panelGrid id="settingValueConfig">
        <h:panelGrid columns="2" id="settingsCreateCashierCheckPrinterPanelGrid"
                     rendered="#{settingCreatePage.settingsIds==0}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" suggestionValues="#{settingCreatePage.allPrinters}" inputClass="input-text"
                           itemClass="output-text" itemSelectedClass="output-text-font"
                           value="#{settingCreatePage.parserBySettingValue.a}" />

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.h}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.b}"
                           value="#{settingCreatePage.parserBySettingValue.b}">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.c}"
                           value="#{settingCreatePage.parserBySettingValue.c}"
                           itemSelectedClass="output-text-font" >
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.e}"
                           value="#{settingCreatePage.parserBySettingValue.e}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueCashierCheckPrinterPanel" />
                <a4j:support event="onchange" reRender="resultvalueCashierCheckPrinterPanel" />
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
                <f:selectItem itemValue="4" itemLabel="4"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.f}"
                           value="#{settingCreatePage.parserBySettingValue.f}" itemSelectedClass="output-text-font">
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
                           defaultLabel="#{settingCreatePage.parserBySettingValue.g}"
                           value="#{settingCreatePage.parserBySettingValue.g}" itemSelectedClass="output-text-font">
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
                         value="#{settingCreatePage.parserBySettingValue.valuesByD}" styleClass="output-text" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreateSalesReportPrinterPanelGrid"
                     rendered="#{settingCreatePage.settingsIds==1}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" suggestionValues="#{settingCreatePage.allPrinters}"
                           value="#{settingCreatePage.parserBySettingValue.a}"/>

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.g}" styleClass="input-text" style="width: 207px" />

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.b}"
                           value="#{settingCreatePage.parserBySettingValue.b}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.c}"
                           value="#{settingCreatePage.parserBySettingValue.c}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки количество" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.e}"
                           value="#{settingCreatePage.parserBySettingValue.e}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueSalesReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueSalesReportPrinter" />
                <f:selectItem itemValue="6" itemLabel="6"/>
                <f:selectItem itemValue="7" itemLabel="7"/>
                <f:selectItem itemValue="8" itemLabel="8"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки стоимость" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.f}"
                           value="#{settingCreatePage.parserBySettingValue.f}" itemSelectedClass="output-text-font">
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
                         value="#{settingCreatePage.parserBySettingValue.valuesByD}" styleClass="output-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreateCardBalanceReportPrinterPanelGrid"
                     rendered="#{settingCreatePage.settingsIds==2}">
            <h:outputText escape="true" value="Наименование принтера" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text"
                           suggestionValues="#{settingCreatePage.allPrinters}"
                           value="#{settingCreatePage.parserBySettingValue.a}"/>

            <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.g}" styleClass="input-text" style="width: 207px" />

            <h:outputText escape="true" value="Общая ширина ленты принтера" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.b}"
                           value="#{settingCreatePage.parserBySettingValue.b}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="42" itemLabel="42"/>
                <f:selectItem itemValue="48" itemLabel="48"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина разделителя между колонками" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.c}"
                           value="#{settingCreatePage.parserBySettingValue.c}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="1" itemLabel="1"/>
                <f:selectItem itemValue="2" itemLabel="2"/>
                <f:selectItem itemValue="3" itemLabel="3"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки номер карты" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.e}"
                           value="#{settingCreatePage.parserBySettingValue.e}" itemSelectedClass="output-text-font">
                <a4j:support event="onselect" reRender="resultvalueCardBalanceReportPrinter" />
                <a4j:support event="onchange" reRender="resultvalueCardBalanceReportPrinter" />
                <f:selectItem itemValue="8" itemLabel="8"/>
                <f:selectItem itemValue="10" itemLabel="10"/>
                <f:selectItem itemValue="12" itemLabel="12"/>
                <f:selectItem itemValue="14" itemLabel="14"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Ширина колонки баланс" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text" enableManualInput="false"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.f}"
                           value="#{settingCreatePage.parserBySettingValue.f}" itemSelectedClass="output-text-font">
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
                         value="#{settingCreatePage.parserBySettingValue.valuesByD}" styleClass="output-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsAutoPlanPaymentSettingPanelGrid" rendered="#{settingCreatePage.settingsIds==3}" >
            <h:outputText escape="true" value="Включить/Выключить устройство" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{settingCreatePage.parserBySettingValue.offOnFlag}">
                <f:selectItem itemValue="true"/>
                <f:selectItem itemValue="false"/>
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Время автооплаты (Ч:ММ)" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.payTime}" id="payTimeValue"
                         maxlength="5"  styleClass="output-text" converterMessage="Не верный формат времени">
                <f:convertDateTime pattern="HH:mm"/>
            </h:inputText>

            <h:outputText escape="true" value="Порог срабатывания" styleClass="output-text" />
            <rich:inputNumberSlider inputClass="input-text" value="#{settingCreatePage.parserBySettingValue.porog}"
                                    maxValue="100" step="1" showToolTip="true" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreateSubscriberFeedingPanelGrid"
                     rendered="#{settingCreatePage.settingsIds==4}">

            <h:outputText escape="true" value="Количество дней, на которые оформляются заявки на поставку" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.dayRequest}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Количество дней, пропустив которые, клиент приостанавливает свою подписку" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.dayDeActivate}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Включить/Выключить автоматическую приостановку/возобновление подписок на услугу АП в зависимости от посещения учреждения" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{settingCreatePage.parserBySettingValue.enableFeeding}">
                <f:selectItem itemValue="true"/>
                <f:selectItem itemValue="false"/>
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Количество часов, в течение которых запрещено редактировать заявки" styleClass="output-text" />
            <%--<h:inputText value="#{settingCreatePage.parserBySettingValue.hoursForbidChange}" styleClass="input-text" style="width: 207px"/>--%>
            <rich:inputNumberSlider inputClass="input-text" value="#{settingCreatePage.parserBySettingValue.hoursForbidChange}"
                                    maxValue="72" step="3" minValue="3" showToolTip="true" />

            <h:outputText escape="true" value="Шестидневный план рабочих дней" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{settingCreatePage.parserBySettingValue.sixWorkWeek}">
                <f:selectItem itemValue="true"/>
                <f:selectItem itemValue="false"/>
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Количество рабочих дней блокировки баланса с учетом стоимости питания, отмеченного в циклограмме" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.daysToForbidChangeInPos}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Количество дней, на которые создаются заявки вариативного питания" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.dayCreateVP}" styleClass="input-text" style="width: 207px"/>

            <h:outputText escape="true" value="Количество часов, в течение которых запрещено редактировать заявки вариативного питания" styleClass="output-text" />
            <rich:inputNumberSlider inputClass="input-text" value="#{settingCreatePage.parserBySettingValue.hoursForbidVP}"
                                    maxValue="72" step="3" minValue="3" showToolTip="true" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreateReplacingMissingBeneficiariesSettingPanelGrid" rendered="#{settingCreatePage.settingsIds==5}" >
            <h:outputText escape="true" value="Группа" styleClass="output-text" />
            <rich:comboBox width="230" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.value}"
                           value="#{settingCreatePage.parserBySettingValue.value}" itemSelectedClass="output-text-font">
                <f:selectItem itemValue="Резерв" itemLabel="Резерв"/>
                <f:selectItem itemValue="Все" itemLabel="Все"/>
            </rich:comboBox>

            <h:outputText escape="true" value="Копуса (1 - только свой корпус / 2 - все корпуса)" styleClass="output-text" />
            <rich:comboBox width="230" enableManualInput="false" inputClass="input-text" itemClass="output-text"
                           defaultLabel="#{settingCreatePage.parserBySettingValue.orgParam}"
                           value="#{settingCreatePage.parserBySettingValue.orgParam}" itemSelectedClass="output-text-font">
                <f:selectItem itemValue="1" itemLabel="Только свой корпус"/>
                <f:selectItem itemValue="2" itemLabel="Все корпуса"/>
            </rich:comboBox>

        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreatePreorderFeedingSettingPanelGrid" rendered="#{settingCreatePage.setting.settingsId.id==6}">
            <h:outputText escape="true" value="Количество дней, в течение которых запрещено редактировать заявки платного питания" styleClass="output-text" />
            <rich:inputNumberSlider inputClass="input-text" value="#{settingCreatePage.parserBySettingValue.forbiddenDaysCount}"
                                    maxValue="3" step="1" minValue="1" showToolTip="true" />
        </h:panelGrid>

        <h:panelGrid columns="2" id="settingsCreatePreorderAutopaySettingPanelGrid" rendered="#{settingCreatePage.settingsIds==7}" >
            <h:outputText escape="true" value="Активность для предзаказов" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{settingCreatePage.parserBySettingValue.offisActivePreorderOnFlag}">
                <f:selectItem itemValue="true"/>
                <f:selectItem itemValue="false"/>
            </h:selectBooleanCheckbox>

            <h:outputText escape="true" value="Время автооплаты (Ч:ММ)" styleClass="output-text" />
            <h:inputText value="#{settingCreatePage.parserBySettingValue.processingTime_Preorder}" id="preorderAutopayTimeValue"
                         maxlength="5"  styleClass="output-text" converterMessage="Не верный формат времени">
                <f:convertDateTime pattern="HH:mm"/>
            </h:inputText>
        </h:panelGrid>

    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton action="#{settingCreatePage.create}" value="Зарегистрировать" reRender="settingsCreatePage"/>
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid" id="settingsMessagePanelGrid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>