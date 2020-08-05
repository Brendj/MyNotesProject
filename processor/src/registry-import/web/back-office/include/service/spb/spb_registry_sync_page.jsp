<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<style lang="">
.createClientRow {
    background-color: #EBFFE0;
}
.deleteClientRow {
    background-color: #FFE3E0;
}
.moveClientRow {
    background-color: #FFFFE0;
}
.modifyClientRow {
}
.disabledClientRow {
    background-color: #EFEFEF;
}
.revisionInfo_operation {
    background-color: #FFFFE0;
}
.revisionInfo_count {
    font-weight: bold;
}
</style>


<%--@elvariable id="spbRegistrySyncPage" type="ru.axetta.ecafe.processor.web.ui.service.spb.SpbRegistrySyncPage"--%>
<h:panelGrid id="SpbRegistrySynchPage" styleClass="borderless-grid" binding="#{spbRegistrySyncPage.pageComponent}">

    <h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
        <h:outputText escape="true" value="#{spbRegistrySyncPage.errorMessages}" rendered="#{not empty spbRegistrySyncPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
        <h:outputText escape="true" value="#{spbRegistrySyncPage.infoMessages}" rendered="#{not empty spbRegistrySyncPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid" rendered="#{spbRegistrySyncPage.displayOrgSelection}">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{spbRegistrySyncPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
            <h:selectOneMenu id="revisionDates" value="#{spbRegistrySyncPage.revisionCreateDate}" style="width:350px;" >
                <f:selectItems value="#{spbRegistrySyncPage.revisions}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр расхождений" styleClass="output-text" />
            <h:selectOneMenu id="actionFilter" value="#{spbRegistrySyncPage.actionFilter}" style="width:150px;" >
                <f:selectItems value="#{spbRegistrySyncPage.actionFilters}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр ФИО" styleClass="output-text" />
            <h:inputText value="#{spbRegistrySyncPage.nameFilter}" size="64" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Проверка ФИО на дубликат при регистрации" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{spbRegistrySyncPage.fullNameValidation}" styleClass="output-text"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Включать только классы:" styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{spbRegistrySyncPage.showOnlyClientGoups}"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Обновить" action="#{spbRegistrySyncPage.doUpdate}"
                               reRender="synchTable,synchTableInfoPanel,revisionInfo,SpbRegistrySynchPage_tabpanel,resultTitle" styleClass="command-button" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            <a4j:status id="updateStatus">
                <f:facet name="start">
                     <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:tabPanel id="SpbRegistrySynchPage_tabpanel" valueChangeListener="#{spbRegistrySyncPage.doChangePanel}">
        <rich:tab label="Просмотр изменений"  switchType="ajax" immediate="true" id="browseChangesPanel" reRender="SpbRegistrySynchPage_tabpanel">

            <h:panelGrid style="text-align: center" columns="2">
                <h:panelGroup id="synchTable">
                    <h:outputText id="resultTitle" value="Результаты #{spbRegistrySyncPage.resultTitle}"
                                  styleClass="page-header-text"/>
                    <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
                        <h:outputText value="Всего в списке: #{spbRegistrySyncPage.totalCount}" styleClass="output-text" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Отметить все записи к применению" action="#{spbRegistrySyncPage.doMarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Снять все записи c применения" action="#{spbRegistrySyncPage.doUnmarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                    </h:panelGrid>
                    <rich:dataTable value="#{spbRegistrySyncPage.items}" var="e" footerClass="data-table-footer"
                                    width="350px" rows="20" id="table" rowKeyVar="row">
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="№"></h:outputText>
                            </f:facet>
                            <h:outputText value="#{row+1}"></h:outputText>
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Действие" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.operationName}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.fullname}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущее ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.prevFullname}" rendered="#{e.fullnameChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDate}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDateFrom}" rendered="#{e.birthDateFromChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupName}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupNameFrom}" rendered="#{e.groupChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySyncPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Применить" />
                            </f:facet>
                            <h:selectBooleanCheckbox value="#{e.selected}" styleClass="checkboxes"
                                                     rendered="#{!spbRegistrySyncPage.isError(e) && !spbRegistrySyncPage.isApplied(e, false)}"/>
                            <h:outputText value="применено" styleClass="output-text"
                                          rendered="#{!spbRegistrySyncPage.isError(e) && spbRegistrySyncPage.isApplied(e, true)}"/>
                            <h:outputText value="ошибка" styleClass="output-text"
                                          rendered="#{spbRegistrySyncPage.isError(e)}"/>
                        </rich:column>

                        <f:facet name="footer">
                            <rich:datascroller for="table" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                                               stepControls="auto">
                                <f:facet name="first">
                                    <h:graphicImage value="/images/16x16/first.png" />
                                </f:facet>
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                                <f:facet name="last">
                                    <h:graphicImage value="/images/16x16/last.png" />
                                </f:facet>
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                    <a4j:commandButton value="Провести полную сверку" action="#{spbRegistrySyncPage.doRefresh}" reRender="synchTable,synchTableInfoPanel,revisionInfo,revisionDates" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGroup>

                <h:panelGrid>
                    <h:panelGrid id="synchTableControl">
                        <a4j:commandButton value="Подтвердить все" action="#{spbRegistrySyncPage.doApply}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                           onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                        <a4j:commandButton value="Сообщение об ошибке" status="updateStatus" style="width: 180px;">
                            <a4j:support event="onclick" action="#{spbRegistrySynchErrorPage.onShow}" reRender="SpbRegistrySynchErrorPage"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('SpbRegistrySynchErrorPage')}.show();">
                                <f:setPropertyActionListener value="#{spbRegistrySyncPage.idOfOrg}" target="#{spbRegistrySynchErrorPage.idOfOrg}" />
                                <f:setPropertyActionListener value="#{spbRegistrySyncPage.revisionCreateDate}" target="#{spbRegistrySynchErrorPage.revisionCreateDate}" />
                            </a4j:support>
                        </a4j:commandButton>
                    </h:panelGrid>
                    <h:panelGrid id="revisionInfo" columns="2">
                        <h:panelGroup styleClass="createClientRow"><h:outputText value="Количество созданных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySyncPage.creationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="deleteClientRow"><h:outputText value="Количество удаленных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySyncPage.deletionsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="moveClientRow"><h:outputText value="Количество перемещений" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySyncPage.movesCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество измененных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySyncPage.modificationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <rich:spacer width="10px" />
                        <rich:spacer width="10px" />
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество найденных разногласий всего" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySyncPage.totalCount}" styleClass="output-text" style="font-weight: bold;"/>
                    </h:panelGrid>
                    <a4j:commandButton value="Создать карты" action="#{spbRegistrySyncPage.createCards}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="Редактирование ошибок" switchType="ajax" rendered="#{spbRegistrySyncPage.showErrorEditPanel}" immediate="true" id="editErrorsPanel" reRender="SpbRegistrySynchPage_tabpanel">
            <h:panelGrid columns="2">
                <h:panelGrid>
                    <h:selectOneMenu id="displaymodes" value="#{spbRegistrySyncPage.displayMode}" style="width:150px;"
                                     rendered="#{!claimCalendarEditPage.changesMade}">
                        <f:selectItems value="#{spbRegistrySyncPage.displayModes}"/>
                        <a4j:support event="onchange" actionListener="#{spbRegistrySyncPage.doChangeDisplayMode}"
                                     reRender="synchTableInfoPanel,SpbRegistrySynchPage_tabpanel,errorCommentDescription"/>
                    </h:selectOneMenu>
                    <h:selectOneListbox id="subscriptions"
                                        value="#{spbRegistrySyncPage.idOfSelectedError}" style="width:200px; heigth: 300px;" size="11">
                        <f:selectItems value="#{spbRegistrySyncPage.errors}"/>
                        <a4j:support ajaxSingle="true" reRender="errorQuestion,errorAnswer,synchTableInfoPanel,errorCommentDescription"
                                     actionListener="#{spbRegistrySyncPage.doChangeErrorQuestion}" event="onchange"/>
                    </h:selectOneListbox>
                </h:panelGrid>
                <h:panelGrid>
                    <h:outputText value="Описание" styleClass="output-text"/>
                    <h:inputTextarea id="errorQuestion" readonly="true" value="#{spbRegistrySyncPage.errorMessage}" style="width: 400px; height: 100px;"/>
                    <h:outputText id="errorCommentDescription" value="Комментарий #{spbRegistrySyncPage.commentInfo}" styleClass="output-text"/>
                    <h:inputTextarea id="errorAnswer" value="#{spbRegistrySyncPage.errorComment}" style="width: 400px; height: 100px;" disabled="#{!spbRegistrySyncPage.selectedErrorEditable}"/>
                    <a4j:commandButton value="Принять" action="#{spbRegistrySyncPage.doComment}"
                                       styleClass="command-button" status="updateStatus" reRender="synchTableInfoPanel,SpbRegistrySynchPage_tabpanel"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

</h:panelGrid>