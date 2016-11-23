<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

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


<%--@elvariable id="NSIOrgRegistrySynchPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySynchPage"--%>
<h:panelGrid id="NSIOrgRegistrySynchPage" styleClass="borderless-grid" binding="#{NSIOrgRegistrySynchPage.pageComponent}">

    <h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
        <h:outputText escape="true" value="#{NSIOrgRegistrySynchPage.errorMessages}" rendered="#{not empty NSIOrgRegistrySynchPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
        <h:outputText escape="true" value="#{NSIOrgRegistrySynchPage.infoMessages}" rendered="#{not empty NSIOrgRegistrySynchPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid" rendered="#{NSIOrgRegistrySynchPage.displayOrgSelection}">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{NSIOrgRegistrySynchPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
            <h:selectOneMenu id="revisionDates" value="#{NSIOrgRegistrySynchPage.revisionCreateDate}" style="width:350px;" >
                <f:selectItems value="#{NSIOrgRegistrySynchPage.revisions}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр расхождений" styleClass="output-text" />
            <h:selectOneMenu id="actionFilter" value="#{NSIOrgRegistrySynchPage.actionFilter}" style="width:150px;" >
                <f:selectItems value="#{NSIOrgRegistrySynchPage.actionFilters}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр ФИО" styleClass="output-text" />
            <h:inputText value="#{NSIOrgRegistrySynchPage.nameFilter}" size="64" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Проверка ФИО на дубликат при регистрации" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{NSIOrgRegistrySynchPage.fullNameValidation}" styleClass="output-text"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Включать только классы:" styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{NSIOrgRegistrySynchPage.showOnlyClientGoups}"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Обновить" action="#{NSIOrgRegistrySynchPage.doUpdate}"
                               reRender="synchTable,synchTableInfoPanel,revisionInfo,NSIOrgRegistrySynchPage_tabpanel,resultTitle" styleClass="command-button" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            <a4j:status id="updateStatus">
                <f:facet name="start">
                     <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:tabPanel id="NSIOrgRegistrySynchPage_tabpanel" valueChangeListener="#{NSIOrgRegistrySynchPage.doChangePanel}">
        <rich:tab label="Просмотр изменений"  switchType="ajax" immediate="true" id="browseChangesPanel" reRender="NSIOrgRegistrySynchPage_tabpanel">

            <h:panelGrid style="text-align: center" columns="2">
                <h:panelGroup id="synchTable">
                    <h:outputText id="resultTitle" value="Результаты #{NSIOrgRegistrySynchPage.resultTitle}"
                                  styleClass="page-header-text"/>
                    <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
                        <h:outputText value="Всего в списке: #{NSIOrgRegistrySynchPage.totalCount}" styleClass="output-text" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Отметить все записи к применению" action="#{NSIOrgRegistrySynchPage.doMarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                        <rich:spacer width="20px" />
                        <a4j:commandLink value="Снять все записи c применения" action="#{NSIOrgRegistrySynchPage.doUnmarkAll}"
                                         reRender="workspaceTogglePanel" styleClass="command-button" />
                    </h:panelGrid>
                    <rich:dataTable value="#{NSIOrgRegistrySynchPage.items}" var="e" footerClass="data-table-footer"
                                    width="350px" rows="20" id="table" rowKeyVar="row">
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="№"></h:outputText>
                            </f:facet>
                            <h:outputText value="#{row+1}"></h:outputText>
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Действие" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.operationName}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDate}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Дата рождения" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.birthDateFrom}" rendered="#{e.birthDateFromChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Пол" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.gender}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущий Пол" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.genderFrom}" rendered="#{e.genderFromChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.fullname}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущее ФИО" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.prevFullname}" rendered="#{e.fullnameChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupName}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Группа" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.groupNameFrom}" rendered="#{e.groupChangeExists}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Перевод из школы" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.migrateFromOrgName}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Льгота при поступлении" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.benefitOnAdmission}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущая Льгота при поступлении" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.benefitOnAdmissionFrom}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Кол. представителей" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.guardiansCount}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Предыдущий тип возрастной группы" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.ageTypeGroupFrom}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Тип возрастной группы" />
                            </f:facet>
                            <h:outputText styleClass="output-text" value="#{e.ageTypeGroup}" />
                        </rich:column>
                        <rich:column styleClass="#{NSIOrgRegistrySynchPage.getLineStyleClass(e)}">
                            <f:facet name="header">
                                <h:outputText value="Применить" />
                            </f:facet>
                            <h:selectBooleanCheckbox value="#{e.selected}" styleClass="checkboxes"
                                                     rendered="#{!NSIOrgRegistrySynchPage.isError(e) && !NSIOrgRegistrySynchPage.isApplied(e, false)}"/>
                            <h:outputText value="применено" styleClass="output-text"
                                          rendered="#{!NSIOrgRegistrySynchPage.isError(e) && NSIOrgRegistrySynchPage.isApplied(e, true)}"/>
                            <h:outputText value="ошибка" styleClass="output-text"
                                          rendered="#{NSIOrgRegistrySynchPage.isError(e)}"/>
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
                    <a4j:commandButton value="Провести полную сверку" action="#{NSIOrgRegistrySynchPage.doRefresh}" reRender="synchTable,synchTableInfoPanel,revisionInfo,revisionDates" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGroup>

                <h:panelGrid>
                    <h:panelGrid id="synchTableControl">
                        <a4j:commandButton value="Подтвердить все" action="#{NSIOrgRegistrySynchPage.doApply}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                           onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                        <a4j:commandButton value="Сообщение об ошибке" status="updateStatus" style="width: 180px;">
                            <a4j:support event="onclick" action="#{NSIOrgRegistrySynchErrorPage.onShow}" reRender="NSIOrgRegistrySynchErrorPage"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('NSIOrgRegistrySynchErrorPage')}.show();">
                                <f:setPropertyActionListener value="#{NSIOrgRegistrySynchPage.idOfOrg}" target="#{NSIOrgRegistrySynchErrorPage.idOfOrg}" />
                                <f:setPropertyActionListener value="#{NSIOrgRegistrySynchPage.revisionCreateDate}" target="#{NSIOrgRegistrySynchErrorPage.revisionCreateDate}" />
                            </a4j:support>
                        </a4j:commandButton>
                    </h:panelGrid>
                    <h:panelGrid id="revisionInfo" columns="2">
                        <h:panelGroup styleClass="createClientRow"><h:outputText value="Количество созданных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{NSIOrgRegistrySynchPage.creationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="deleteClientRow"><h:outputText value="Количество удаленных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{NSIOrgRegistrySynchPage.deletionsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="moveClientRow"><h:outputText value="Количество перемещений" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{NSIOrgRegistrySynchPage.movesCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество измененных записей" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{NSIOrgRegistrySynchPage.modificationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                        <rich:spacer width="10px" />
                        <rich:spacer width="10px" />
                        <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество найденных разногласий всего" styleClass="output-text"/></h:panelGroup>
                        <h:outputText value="#{NSIOrgRegistrySynchPage.totalCount}" styleClass="output-text" style="font-weight: bold;"/>
                    </h:panelGrid>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="Редактирование ошибок" switchType="ajax" rendered="#{NSIOrgRegistrySynchPage.showErrorEditPanel}" immediate="true" id="editErrorsPanel" reRender="NSIOrgRegistrySynchPage_tabpanel">
            <h:panelGrid columns="2">
                <h:panelGrid>
                    <h:selectOneMenu id="displaymodes" value="#{NSIOrgRegistrySynchPage.displayMode}" style="width:150px;"
                                     rendered="#{!claimCalendarEditPage.changesMade}">
                        <f:selectItems value="#{NSIOrgRegistrySynchPage.displayModes}"/>
                        <a4j:support event="onchange" actionListener="#{NSIOrgRegistrySynchPage.doChangeDisplayMode}"
                                     reRender="synchTableInfoPanel,NSIOrgRegistrySynchPage_tabpanel,errorCommentDescription"/>
                    </h:selectOneMenu>
                    <h:selectOneListbox id="subscriptions"
                                        value="#{NSIOrgRegistrySynchPage.idOfSelectedError}" style="width:200px; heigth: 300px;" size="11">
                        <f:selectItems value="#{NSIOrgRegistrySynchPage.errors}"/>
                        <a4j:support ajaxSingle="true" reRender="errorQuestion,errorAnswer,synchTableInfoPanel,errorCommentDescription"
                                     actionListener="#{NSIOrgRegistrySynchPage.doChangeErrorQuestion}" event="onchange"/>
                    </h:selectOneListbox>
                </h:panelGrid>
                <h:panelGrid>
                    <h:outputText value="Описание" styleClass="output-text"/>
                    <h:inputTextarea id="errorQuestion" readonly="true" value="#{NSIOrgRegistrySynchPage.errorMessage}" style="width: 400px; height: 100px;"/>
                    <h:outputText id="errorCommentDescription" value="Комментарий #{NSIOrgRegistrySynchPage.commentInfo}" styleClass="output-text"/>
                    <h:inputTextarea id="errorAnswer" value="#{NSIOrgRegistrySynchPage.errorComment}" style="width: 400px; height: 100px;" disabled="#{!NSIOrgRegistrySynchPage.selectedErrorEditable}"/>
                    <a4j:commandButton value="Принять" action="#{NSIOrgRegistrySynchPage.doComment}"
                                       styleClass="command-button" status="updateStatus" reRender="synchTableInfoPanel,NSIOrgRegistrySynchPage_tabpanel"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

</h:panelGrid>