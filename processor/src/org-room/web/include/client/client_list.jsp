<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .output-text-mod {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
        white-space: nowrap;
        padding-right: 10px;
    }
</style>

<%--@elvariable id="clientListEditPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientListEditPage"--%>
<a4j:form>
<h:panelGrid id="clientListViewGrid" binding="#{clientListEditPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid columns="3">
        <%-- ДЕРЕВО --%>
        <a4j:region>
            <h:panelGrid columns="2" styleClass="borderless-grid" style="padding-bottom: 10px">
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <a4j:commandButton image="/images/icon/add_client.png" action="#{clientListEditPage.doRegisterClient}"
                                       reRender="editPanels, clientCardPanel, mesasges_panel" styleClass="command-button" status="clientLookupStatus" />
                    <a4j:commandLink value="Регистрация клиента" action="#{clientListEditPage.doRegisterClient}"
                                       reRender="editPanels, clientCardPanel, mesasges_panel" styleClass="command-button" status="clientLookupStatus" />
                </h:panelGrid>
                <h:panelGrid styleClass="borderless-grid" id="manageClientGroup">
                    <h:outputLink value="#" id="createGroupCommandLink" styleClass="command-button">
                        <h:graphicImage url="/images/icon/group_add.png" />
                        <h:outputText styleClass="output-text-mod" value="Добавить группу"/>
                        <a4j:support event="onclick" action="#{mainPage.doShowSelectCreateGroupModal}" reRender="groupCreatePanel, mesasges_panel"
                                     oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('groupCreatePanel')}.show();"/>
                        <%--<rich:componentControl for="groupCreatePanel" attachTo="createGroupCommandLink"
                                               operation="show" event="onclick"/>--%>
                    </h:outputLink>

                    <h:outputLink value="#" id="removeGroupCommandLink" styleClass="command-button" rendered="#{clientListEditPage.allowRemoveGroup}">
                        <h:graphicImage url="/images/icon/group_delete.png" />
                        <h:outputText styleClass="output-text-mod" value="Удалить группу"/>
                        <a4j:support event="onclick" action="#{clientListEditPage.doRemoveClientGroup}" reRender="clientListViewGrid"/>
                        <%--<rich:componentControl for="groupCreatePanel" attachTo="createGroupCommandLink"
                                               operation="show" event="onclick"/>--%>
                    </h:outputLink>
                    <h:outputLink value="#" id="removeGroupDisCommandLink" styleClass="command-button" rendered="#{!clientListEditPage.allowRemoveGroup}">
                        <h:graphicImage url="/images/icon/group_grey.png"/>
                        <h:outputText styleClass="output-text-mod" value="Удалить группу" style="color: #CCCCCC"/>
                    </h:outputLink>
                </h:panelGrid>
            </h:panelGrid>

            <rich:panel id="clientTree" style="width:300px; height: 550px; overflow:auto;">
                <rich:tree style="width:285px; height: 500px" nodeSelectListener="#{clientListEditPage.doSelectClient}"
                           changeExpandListener="#{clientListEditPage.doClientsNodeExpand}"
                           ajaxSubmitSelection="true"
                           reRender="editPanels, clientCardPanel, mesasges_panel, manageClientGroup"
                           switchType="ajax" binding="#{clientListEditPage.treeComponent}" status="clientLookupStatus"
                           value="#{clientListEditPage.tree}" var="item" ajaxKeys="#{null}">
                    <rich:treeNode>
                        <h:outputText value="#{item}" styleClass="output-text-mod" />
                    </rich:treeNode>
                </rich:tree>
            </rich:panel>
            <h:panelGrid columns="4" styleClass="borderless-grid" id="lookupPanel">
                <h:inputText value="#{clientListEditPage.lookupClientName}" style="width: 220px" maxlength="100" styleClass="output-text" />
                <a4j:commandButton action="#{clientListEditPage.doLookupClient}" image="/images/icon/search.png"
                                   reRender="clientTree, editPanels, clientCardPanel, lookupPanel, mesasges_panel" styleClass="command-button" status="clientLookupStatus" />
                <a4j:commandButton action="#{clientListEditPage.doResetLookupClient}" image="/images/icon/cancel.png"
                                   reRender="clientTree, editPanels, clientCardPanel, lookupPanel, mesasges_panel" styleClass="command-button" status="clientLookupStatus" />
                <a4j:status id="clientLookupStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
            </h:panelGrid>
        </a4j:region>



        <%-- НАСТРОЙКИ КЛИЕНТА --%>
        <h:panelGrid style="padding-top: 50px">
            <h:panelGrid id="editPanels">
                <a4j:region>
                <%--<rich:jQuery selector="#phoneInput" query="setMask({mask:'(999) 999-9999'})" timing="immediate"/>--%>
                <rich:jQuery selector="#phoneInput" query="mask('(999) 999-9999')" timing="immediate"/>
                <rich:jQuery selector="#mobileInput" query="mask('(999) 999-9999')" timing="immediate"/>
                <rich:jQuery selector="#faxInput" query="mask('(999) 999-9999')" timing="immediate"/>

                <h:panelGrid id="classEditPanel" rendered="#{clientListEditPage.showClassEditPanel}">
                    <h:panelGrid styleClass="borderless-grid" columns="2" style="vertical-align: top;">
                        <h:outputText escape="true" value="Классный руководитель" styleClass="output-text-mod" />
                        <%--<h:selectOneMenu id="classTeacher" style="width:370px;" >
                            <f:selectItems value="#{clientListEditPage.teachers}"/>
                        </h:selectOneMenu>--%>
                    </h:panelGrid>
                </h:panelGrid>
                <h:panelGrid id="clientEditPanel" rendered="#{clientListEditPage.showClientEditPanel}">
                    <h:panelGrid styleClass="borderless-grid" columns="2" style="vertical-align: top;">
                        <h:outputText escape="true" value="Класс" styleClass="output-text-mod" />
                        <h:panelGrid styleClass="borderless-grid" columns="2" style="vertical-align: top;">
                            <h:selectOneMenu id="clientGroup" value="#{clientListEditPage.clientGroup}" style="width:325px;" >
                                <f:selectItems value="#{clientListEditPage.groups}"/>
                            </h:selectOneMenu>
                            <a4j:commandButton value="В выбывшие" action="#{clientListEditPage.doRemoveClient}" reRender="clientGroup, mesasges_panel" />
                        </h:panelGrid>
                        <h:outputText escape="true" value="№" styleClass="output-text-mod" />
                        <h:panelGrid styleClass="borderless-grid" columns="3">
                            <h:inputText value="#{clientListEditPage.selectedClient.idOfClient}" styleClass="output-text" disabled="true" style="width: 170px;" />
                            <h:outputText escape="true" value="Л/с" styleClass="output-text-mod" style="padding-left: 50px;" />
                            <h:inputText value="#{clientListEditPage.selectedClient.contractId}" styleClass="output-text" disabled="true" style="width: 170px;" />
                        </h:panelGrid>
                        <h:outputText escape="true" value="Фамилия" styleClass="output-text-mod" />
                        <h:inputText value="#{clientListEditPage.selectedClient.surname}" styleClass="output-text" style="width: 430px;" />
                        <h:outputText escape="true" value="Имя" styleClass="output-text-mod" />
                        <h:inputText value="#{clientListEditPage.selectedClient.firstName}" styleClass="output-text" style="width: 430px;" />
                        <h:outputText escape="true" value="Отчество" styleClass="output-text-mod" />
                        <h:inputText value="#{clientListEditPage.selectedClient.secondName}" styleClass="output-text" style="width: 430px;" />
                    </h:panelGrid>
                    <rich:tabPanel style="width: 500px;" switchType="client">
                        <rich:tab label="Контактные данные">
                            <h:panelGrid columns="2" styleClass="borderless-grid">
                                <h:outputText escape="true" value="Адрес" styleClass="output-text-mod" />
                                <h:inputText value="#{clientListEditPage.selectedClient.address}" styleClass="output-text" style="width: 390px;" />
                                <h:outputText escape="true" value="Телефон" styleClass="output-text-mod" />
                                <h:inputText id="phoneInput" value="#{clientListEditPage.selectedClient.phone}" styleClass="output-text" style="width: 390px;" />
                                <h:outputText escape="true" value="Моб. телефон" styleClass="output-text-mod" />
                                <h:panelGrid columns="4" styleClass="borderless-grid">
                                    <h:outputText escape="true" value="+" styleClass="output-text" />
                                    <h:inputText id="mobileInput" value="#{clientListEditPage.selectedClient.mobile}" styleClass="output-text" style="width: 220px;" />
                                    <h:selectBooleanCheckbox value="#{clientListEditPage.selectedClient.notifyViaSMS}" styleClass="output-text" />
                                    <h:outputText escape="true" value="Уведомлять по СМС" styleClass="output-text-mod" style="white-space: nowrap;" />
                                </h:panelGrid>
                                <h:outputText escape="true" value="Факс" styleClass="output-text-mod" />
                                <h:inputText id="faxInput" value="#{clientListEditPage.selectedClient.fax}" styleClass="output-text" style="width: 390px;" />
                                <h:outputText escape="true" value="E-mail" styleClass="output-text-mod" />
                                <h:panelGrid columns="3" styleClass="borderless-grid">
                                    <h:inputText id="emailInput" value="#{clientListEditPage.selectedClient.email}" styleClass="output-text" style="width: 240px;" />
                                    <h:selectBooleanCheckbox value="#{clientListEditPage.selectedClient.notifyViaEmail}" styleClass="output-text" />
                                    <h:outputText escape="true" value="Уведомлять по Email" styleClass="output-text-mod" style="white-space: nowrap;" />
                                </h:panelGrid>
                                <h:outputText escape="true" value="Примечание" styleClass="output-text-mod" />
                                <h:inputText value="#{clientListEditPage.selectedClient.remarks}" styleClass="output-text" style="width: 390px;" />
                            </h:panelGrid>
                        </rich:tab>
                        <rich:tab label="Оплата без карты">
                            <h:panelGrid columns="2" styleClass="borderless-grid">
                                <h:outputText escape="true" value="Статус" styleClass="output-text-mod" />
                                <h:inputText value="ЗАБЛОКИРОВАНО" disabled="true" styleClass="output-text" style="width: 367px;" />
                                <h:outputText escape="false" value="Макс. кол-во <br/>оплат без карты" styleClass="output-text" />
                                <h:panelGrid styleClass="borderless-grid" columns="3">
                                    <h:inputText value="0" styleClass="output-text" disabled="true" style="width: 137px;" />
                                    <h:outputText escape="false" value="Кол-во оплат <br/>без карты" styleClass="output-text-mod" />
                                    <h:inputText value="0" styleClass="output-text" disabled="true" style="width: 137px;" />
                                </h:panelGrid>
                                <h:outputText escape="false" value="Время последней <br/>оплаты без карты" styleClass="output-text-mod" />
                                <h:inputText value="" disabled="true" styleClass="output-text" style="width: 367px;" />
                            </h:panelGrid>
                        </rich:tab>
                        <rich:tab label="Льготы">
                            <h:panelGrid columns="1" styleClass="borderless-grid">
                                <rich:panel style="width:450px; height: 200px; overflow:auto; background: none;">
                                    <h:panelGrid columns="2" styleClass="borderless-grid">
                                        <h:selectBooleanCheckbox value="true" styleClass="output-text" disabled="true"
                                                                 rendered="#{not empty clientListEditPage.selectedClient.clientGroupDiscount}" />
                                        <h:outputText escape="true" value="#{clientListEditPage.selectedClient.clientGroupDiscount}" styleClass="output-text-mod"
                                                      rendered="#{not empty clientListEditPage.selectedClient.clientGroupDiscount}" />
                                        <h:selectBooleanCheckbox value="true" styleClass="output-text" disabled="true"
                                                                 rendered="#{not empty clientListEditPage.selectedClient.clientSuperGroupDiscount}" />
                                        <h:outputText escape="true" value="#{clientListEditPage.selectedClient.clientSuperGroupDiscount}" styleClass="output-text-mod"
                                                      rendered="#{not empty clientListEditPage.selectedClient.clientSuperGroupDiscount}" />

                                        <c:forEach items="#{clientListEditPage.categoryDiscounts}" var="i">
                                            <h:selectBooleanCheckbox value="#{clientListEditPage.selectedClient.discounts[i.idofcategorydiscount]}" styleClass="output-text" />
                                            <h:outputText escape="true" value="#{i.name}" styleClass="output-text-mod" />
                                        </c:forEach>
                                    </h:panelGrid>
                                </rich:panel>
                            </h:panelGrid>
                        </rich:tab>
                        <rich:tab label="Посещения">
                            <h:panelGrid columns="2" styleClass="borderless-grid">
                                <h:outputText escape="true" value="Текущий статус " styleClass="output-text-mod" />
                                <h:outputText escape="true" value="#{clientListEditPage.selectedClient.isInSchool ? 'Присутствует в здании' : 'Нет в здании'}" styleClass="output-text-mod" style="font-weight: bold" />
                                <h:outputText escape="false" value="История посещений на дату " styleClass="output-text-mod" />
                                <rich:calendar value="#{clientListEditPage.enterEventDate}" datePattern="dd.MM.yyyy"
                                               converter="dateConverter" inputClass="input-text" showWeeksBar="false"
                                               valueChangeListener="#{clientListEditPage.doChangeEnterEventDate}">
                                    <a4j:support event="onchanged" reRender="enterEventsTable, mesasges_panel" bypassUpdates="true" />
                                </rich:calendar>
                            </h:panelGrid>
                            <rich:dataTable id="enterEventsTable" value="#{clientListEditPage.selectedClient.enterEvents}"
                                            var="event" rowKeyVar="row" rows="15" footerClass="data-table-footer" style="width: 490px"
                                            columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
                                <f:facet name="header">
                                    <rich:columnGroup>
                                        <rich:column headerClass="center-aligned-column" rowspan="2">
                                            <h:outputText styleClass="column-header" escape="true" value="Время" />
                                        </rich:column>
                                        <rich:column headerClass="center-aligned-column" rowspan="2">
                                            <h:outputText styleClass="column-header" escape="true" value="Местонахождение" />
                                        </rich:column>
                                    </rich:columnGroup>
                                </f:facet>
                                <rich:column styleClass="left-aligned-column">
                                    <h:outputText styleClass="output-text" value="#{event.date}" converter="dateTimeConverter" />
                                </rich:column>
                                <rich:column styleClass="left-aligned-column">
                                    <h:outputText styleClass="output-text" value="#{event.location}" />
                                </rich:column>
                            </rich:dataTable>
                        </rich:tab>
                        <rich:tab label="Перемещения">
                            <rich:dataTable id="migrationHistoryTable" value="#{clientListEditPage.selectedClient.migrationsHistory}"
                                            var="migration" rowKeyVar="row" rows="15" footerClass="data-table-footer" style="width: 490px"
                                            columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
                                <f:facet name="header">
                                    <rich:columnGroup>
                                        <rich:column headerClass="center-aligned-column" rowspan="2">
                                            <h:outputText styleClass="column-header" escape="true" value="Дата перемещения" />
                                        </rich:column>
                                        <rich:column headerClass="center-aligned-column" rowspan="2">
                                            <h:outputText styleClass="column-header" escape="true" value="Перемещен в группу" />
                                        </rich:column>
                                    </rich:columnGroup>
                                </f:facet>
                                <rich:column styleClass="left-aligned-column">
                                    <h:outputText styleClass="output-text" value="#{migration.date}" converter="dateTimeConverter" />
                                </rich:column>
                                <rich:column styleClass="left-aligned-column">
                                    <h:outputText styleClass="output-text" value="#{migration.destination}" />
                                </rich:column>
                            </rich:dataTable>
                        </rich:tab>
                    </rich:tabPanel>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Создано" styleClass="output-text-mod" />
                        <h:inputText value="#{clientListEditPage.selectedClient.createdDate}" styleClass="output-text" style="width: 420px;" disabled="true"  />
                        <h:outputText escape="true" value="Обновлено" styleClass="output-text-mod" />
                        <h:inputText value="#{clientListEditPage.selectedClient.lastUpdateDate}" styleClass="output-text" style="width: 420px;" disabled="true"  />
                    </h:panelGrid>

                    <%--<h:panelGrid styleClass="borderless-grid" columns="2" style="vertical-align: top;">
                        <h:outputText escape="true" value="Классный руководитель" styleClass="output-text-mod" />
                        <h:selectOneMenu id="clientClassTearcher" style="width:345px;" disabled="true" >
                            <f:selectItems value="#{clientListEditPage.teachers}"/>
                        </h:selectOneMenu>
                    </h:panelGrid>--%>

                    <h:panelGrid styleClass="borderless-grid" columns="2" style="vertical-align: top; align: right;">
                            <a4j:commandButton value="#{clientListEditPage.submitButtonLabel}" action="#{clientListEditPage.doApplyChanges}" reRender="clientTree, editPanels, clientCardPanel, mesasges_panel" />
                            <a4j:commandButton value="Отменить" action="#{clientListEditPage.doCancelChanges}" reRender="editPanels, clientCardPanel, mesasges_panel" />
                    </h:panelGrid>

                </h:panelGrid>

                <h:panelGrid id="mesasges_panel">
                    <h:outputText escape="true" value="#{clientListEditPage.errorMessages}" rendered="#{not empty clientListEditPage.errorMessages}" styleClass="error-messages" />
                    <h:outputText escape="true" value="#{clientListEditPage.infoMessages}" rendered="#{not empty clientListEditPage.infoMessages}" styleClass="info-messages" />
                </h:panelGrid>

                </a4j:region>
            </h:panelGrid>
        </h:panelGrid>

        <%-- НАСТРОЙКИ КАРТЫ --%>
        <h:panelGrid id="clientCardPanel" style="padding-top: 50px;">
            <h:panelGrid columns="2" style="vertical-align: top;" rendered="#{clientListEditPage.showClientEditPanel}">
                <h:outputText escape="true" value="Карта" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.cardNo}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Тип" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.cardType}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Дата выдачи" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.createdDate}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Срок действия" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.expiredDate}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Статус" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.status}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Баланс" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.balance}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.overdraftLimit}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Ограничение затрат" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.limit}" styleClass="output-text" style="width: 200px;" disabled="true" />
                <h:outputText escape="true" value="Причина блокировки" styleClass="output-text-mod" />
                <h:inputText value="#{clientListEditPage.selectedClient.card.blockReason}" styleClass="output-text" style="width: 200px;" disabled="true" />
            </h:panelGrid>
        </h:panelGrid>
    </h:panelGrid>
</h:panelGrid>
</a4j:form>