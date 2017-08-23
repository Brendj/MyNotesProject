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


<%--@elvariable id="spbRegistrySynchPage" type="ru.axetta.ecafe.processor.web.ui.service.spb.SpbRegistrySynchPage"--%>
<h:panelGrid id="SpbRegistrySynchPage" styleClass="borderless-grid" binding="#{spbRegistrySynchPage.pageComponent}">

    <h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
        <h:outputText escape="true" value="#{spbRegistrySynchPage.errorMessages}" rendered="#{not empty spbRegistrySynchPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
        <h:outputText escape="true" value="#{spbRegistrySynchPage.infoMessages}" rendered="#{not empty spbRegistrySynchPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid" rendered="#{spbRegistrySynchPage.displayOrgSelection}">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{spbRegistrySynchPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
            <h:selectOneMenu id="revisionDates" value="#{spbRegistrySynchPage.revisionCreateDate}" style="width:350px;" >
                <f:selectItems value="#{spbRegistrySynchPage.revisions}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр расхождений" styleClass="output-text" />
            <h:selectOneMenu id="actionFilter" value="#{spbRegistrySynchPage.actionFilter}" style="width:150px;" >
                <f:selectItems value="#{spbRegistrySynchPage.actionFilters}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр ФИО" styleClass="output-text" />
            <h:inputText value="#{spbRegistrySynchPage.nameFilter}" size="64" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Проверка ФИО на дубликат при регистрации" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{spbRegistrySynchPage.fullNameValidation}" styleClass="output-text"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Включать только классы:" styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{spbRegistrySynchPage.showOnlyClientGoups}"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Обновить" action="#{spbRegistrySynchPage.doUpdate}"
                               reRender="synchTable,synchTableInfoPanel,revisionInfo,SpbRegistrySynchPage_tabpanel,resultTitle" styleClass="command-button" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            <a4j:status id="updateStatus">
                <f:facet name="start">
                     <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:tabPanel id="SpbRegistrySynchPage_tabpanel" valueChangeListener="#{spbRegistrySynchPage.doChangePanel}">
        <rich:tab label="Просмотр изменений"  switchType="ajax" immediate="true" id="browseChangesPanel" reRender="SpbRegistrySynchPage_tabpanel">

            <h:panelGrid style="text-align: center" columns="2">
                <h:panelGroup id="synchTable">
                    <h:outputText id="resultTitle" value="Результаты #{spbRegistrySynchPage.resultTitle}"
                                  styleClass="page-header-text"/>
                    <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
                        <h:outputText value="Всего в списке: #{spbRegistrySynchPage.totalCount}" styleClass="output-text" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Отметить все записи к применению" action="#{spbRegistrySynchPage.doMarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Снять все записи c применения" action="#{spbRegistrySynchPage.doUnmarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                    </h:panelGrid>
                    <rich:dataTable value="#{spbRegistrySynchPage.items}" var="e" footerClass="data-table-footer"
                                    width="350px" rows="20" id="table" rowKeyVar="row">
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="№"></h:outputText>
                            </f:facet>
                            <h:outputText value="#{row+1}"></h:outputText>
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Действие" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.operationName}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.fullname}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущее ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.prevFullname}" rendered="#{e.fullnameChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDate}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDateFrom}" rendered="#{e.birthDateFromChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupName}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupNameFrom}" rendered="#{e.groupChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Льгота" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.benefitDSZN}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Льгота" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.benefitDSZNFrom}" />
                        </rich:column>
                        <rich:column styleClass="#{spbRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Применить" />
                            </f:facet>
                            <h:selectBooleanCheckbox value="#{e.selected}" styleClass="checkboxes"
                                                     rendered="#{!spbRegistrySynchPage.isError(e) && !spbRegistrySynchPage.isApplied(e, false)}"/>
                            <h:outputText value="применено" styleClass="output-text"
                                          rendered="#{!spbRegistrySynchPage.isError(e) && spbRegistrySynchPage.isApplied(e, true)}"/>
                            <h:outputText value="ошибка" styleClass="output-text"
                                          rendered="#{spbRegistrySynchPage.isError(e)}"/>
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
                    <a4j:commandButton value="Провести полную сверку" action="#{spbRegistrySynchPage.doRefresh}" reRender="synchTable,synchTableInfoPanel,revisionInfo,revisionDates" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGroup>

                <h:panelGrid>
                    <h:panelGrid id="synchTableControl">
                        <a4j:commandButton value="Подтвердить все" action="#{spbRegistrySynchPage.doApply}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                           onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                        <a4j:commandButton value="Сообщение об ошибке" status="updateStatus" style="width: 180px;">
                            <a4j:support event="onclick" action="#{spbRegistrySynchErrorPage.onShow}" reRender="SpbRegistrySynchErrorPage"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('SpbRegistrySynchErrorPage')}.show();">
                                <f:setPropertyActionListener value="#{spbRegistrySynchPage.idOfOrg}" target="#{spbRegistrySynchErrorPage.idOfOrg}" />
                                <f:setPropertyActionListener value="#{spbRegistrySynchPage.revisionCreateDate}" target="#{spbRegistrySynchErrorPage.revisionCreateDate}" />
                            </a4j:support>
                        </a4j:commandButton>
                    </h:panelGrid>
                    <h:panelGrid id="revisionInfo" columns="2">
                        <h:panelGroup styleClass="createClientRow"><h:outputText value="Количество созданных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySynchPage.creationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="deleteClientRow"><h:outputText value="Количество удаленных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySynchPage.deletionsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="moveClientRow"><h:outputText value="Количество перемещений" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySynchPage.movesCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество измененных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySynchPage.modificationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <rich:spacer width="10px" />
                        <rich:spacer width="10px" />
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество найденных разногласий всего" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{spbRegistrySynchPage.totalCount}" styleClass="output-text" style="font-weight: bold;"/>
                    </h:panelGrid>
                    <a4j:commandButton value="Создать карты" action="#{spbRegistrySynchPage.createCards}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="Редактирование ошибок" switchType="ajax" rendered="#{spbRegistrySynchPage.showErrorEditPanel}" immediate="true" id="editErrorsPanel" reRender="SpbRegistrySynchPage_tabpanel">
            <h:panelGrid columns="2">
                <h:panelGrid>
                    <h:selectOneMenu id="displaymodes" value="#{spbRegistrySynchPage.displayMode}" style="width:150px;"
                                     rendered="#{!claimCalendarEditPage.changesMade}">
                        <f:selectItems value="#{spbRegistrySynchPage.displayModes}"/>
                        <a4j:support event="onchange" actionListener="#{spbRegistrySynchPage.doChangeDisplayMode}"
                                     reRender="synchTableInfoPanel,SpbRegistrySynchPage_tabpanel,errorCommentDescription"/>
                    </h:selectOneMenu>
                    <h:selectOneListbox id="subscriptions"
                                        value="#{spbRegistrySynchPage.idOfSelectedError}" style="width:200px; heigth: 300px;" size="11">
                        <f:selectItems value="#{spbRegistrySynchPage.errors}"/>
                        <a4j:support ajaxSingle="true" reRender="errorQuestion,errorAnswer,synchTableInfoPanel,errorCommentDescription"
                                     actionListener="#{spbRegistrySynchPage.doChangeErrorQuestion}" event="onchange"/>
                    </h:selectOneListbox>
                </h:panelGrid>
                <h:panelGrid>
                    <h:outputText value="Описание" styleClass="output-text"/>
                    <h:inputTextarea id="errorQuestion" readonly="true" value="#{spbRegistrySynchPage.errorMessage}" style="width: 400px; height: 100px;"/>
                    <h:outputText id="errorCommentDescription" value="Комментарий #{spbRegistrySynchPage.commentInfo}" styleClass="output-text"/>
                    <h:inputTextarea id="errorAnswer" value="#{spbRegistrySynchPage.errorComment}" style="width: 400px; height: 100px;" disabled="#{!spbRegistrySynchPage.selectedErrorEditable}"/>
                    <a4j:commandButton value="Принять" action="#{spbRegistrySynchPage.doComment}"
                                       styleClass="command-button" status="updateStatus" reRender="synchTableInfoPanel,SpbRegistrySynchPage_tabpanel"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

</h:panelGrid>