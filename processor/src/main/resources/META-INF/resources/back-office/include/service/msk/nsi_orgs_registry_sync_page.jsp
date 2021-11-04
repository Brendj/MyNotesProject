<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style lang="">
    .createOrgRow {
        background-color: #EBFFE0;
    }
    .deleteOrgRow {
        background-color: #FFE3E0;
    }
    .modifyOrgRow {
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
    .padding-zero {
        padding: 0;
    }
</style>


<%--@elvariable id="NSIOrgsRegistrySynchPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgsRegistrySynchPage"--%>
<h:panelGrid id="NSIOrgsRegistrySynchPage" styleClass="borderless-grid" binding="#{NSIOrgsRegistrySynchPage.pageComponent}">

<h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
    <h:outputText escape="true" value="#{NSIOrgsRegistrySynchPage.errorMessages}" rendered="#{not empty NSIOrgsRegistrySynchPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
    <h:outputText escape="true" value="#{NSIOrgsRegistrySynchPage.infoMessages}" rendered="#{not empty NSIOrgsRegistrySynchPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
</h:panelGrid>

<rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                        headerClass="filter-panel-header">
    <h:panelGrid columns="2">
        <h:column>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
        <h:selectOneMenu id="revisionDates" value="#{NSIOrgsRegistrySynchPage.selectedRevision}" style="width:350px;" >
            <f:selectItems value="#{NSIOrgsRegistrySynchPage.revisions}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Фильтр наименования ОО" styleClass="output-text" />
        <h:inputText value="#{NSIOrgsRegistrySynchPage.nameFilter}" style="width:350px;" styleClass="input-text" />
        <h:outputText escape="true" value="Поиск по названиям в ИСПП" styleClass="output-text"/>
        <h:selectBooleanCheckbox value="#{NSIOrgsRegistrySynchPage.isNeedAddElements}" style="width:350px;margin-left:-168px"/>

        <h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:selectOneMenu id="regionNSIOrgs" value="#{NSIOrgsRegistrySynchPage.selectedRegion}" style="width:350px;" >
            <f:selectItems value="#{NSIOrgsRegistrySynchPage.regions}"/>
        </h:selectOneMenu>

    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:selectBooleanCheckbox value="#{NSIOrgsRegistrySynchPage.hideApplied}"/>
        <h:outputText escape="true" value="Скрывать уже примененные изменения" styleClass="output-text" />
    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Обновить" action="#{NSIOrgsRegistrySynchPage.doUpdate}"
                           reRender="synchTable,synchTableInfoPanel,revisionInfo,resultTitle,synchTableControl" styleClass="command-button" status="updateStatus"
                           onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
        <a4j:status id="updateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
            <rich:spacer height="40" />
            <h:panelGrid>
            <a4j:commandButton value="Провести полную сверку" action="#{NSIOrgsRegistrySynchPage.doRefresh}" reRender="synchTable,synchTableInfoPanel,revisionInfo,revisionDates" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            </h:panelGrid>
        </h:column>
        <h:column>
            <h:panelGrid>
                <h:panelGrid id="synchTableControl">
                    <a4j:commandButton value="Выбрать все" action="#{NSIOrgsRegistrySynchPage.doCheckAll}" reRender="synchTable,synchTableInfoPanel" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"
                                       rendered="#{NSIOrgsRegistrySynchPage.isRevisionLast()}"/>
                    <a4j:commandButton value="Очистить все" action="#{NSIOrgsRegistrySynchPage.doUncheckAll}" reRender="synchTable,synchTableInfoPanel" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"
                                       rendered="#{NSIOrgsRegistrySynchPage.isRevisionLast()}"/>
                    <rich:separator />
                    <a4j:commandButton value="Применить выбранные" action="#{NSIOrgsRegistrySynchPage.doApply}" reRender="synchTable,synchTableInfoPanel,revisionInfo,synchTableControl" status="updateStatus"
                                       onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
                </h:panelGrid>
                <h:panelGrid id="revisionInfo" columns="2">
                    <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество принятых разногласий" styleClass="output-text"/></h:panelGroup>
                    <h:outputText value="#{NSIOrgsRegistrySynchPage.modificationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                    <rich:spacer width="10px" />
                    <rich:spacer width="10px" />
                    <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество найденных разногласий" styleClass="output-text"/></h:panelGroup>
                    <h:outputText value="#{NSIOrgsRegistrySynchPage.totalCount}" styleClass="output-text" style="font-weight: bold;"/>
                </h:panelGrid>
            </h:panelGrid>
        </h:column>
    </h:panelGrid>
</rich:simpleTogglePanel>


<h:panelGrid style="text-align: center" columns="2">
    <h:panelGroup id="synchTable">
        <h:outputText id="resultTitle" value="Результаты #{NSIOrgsRegistrySynchPage.resultTitle}"
                      styleClass="page-header-text"/>
        <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
            <h:outputText value="Всего в списке: #{NSIOrgsRegistrySynchPage.totalCount} (" styleClass="output-text" />
            <h:graphicImage value="/images/tips/red.png" style="border: 0; margin: 2;" />
            <h:outputText value=" - Данные АИС Реестр;" styleClass="output-text" />
            <h:graphicImage value="/images/tips/green.png" style="border: 0; margin: 2;" />
            <h:outputText value=" - Данные ИС ПП)" styleClass="output-text" />
        </h:panelGrid>

        <rich:modalPanel id="editSverkaPanel" autosized="true" minWidth="600">
            <f:facet name="header">
                <h:outputText value="Принятие сверки по зданию" />
            </f:facet>
            <a4j:commandButton value="Выбрать все" action="#{NSIOrgsRegistrySynchPage.doCheckAllSverkaPanel}" reRender="editSverkaPanelTable"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;" ajaxSingle="true" />
            <a4j:commandButton value="Очистить все" action="#{NSIOrgsRegistrySynchPage.doUncheckAllSverkaPanel}" reRender="editSverkaPanelTable"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;" ajaxSingle="true" />
            <rich:spacer height="20px" />
            <rich:dataTable value="#{NSIOrgsRegistrySynchPage.orgModifyChangeItems}" id="editSverkaPanelTable" var="orgModifyItem" width="100%" columns="4">
                <rich:column>
                    <h:selectBooleanCheckbox value="#{orgModifyItem.selected}" styleClass="checkboxes">
                        <a4j:support event="onchange" ajaxSingle="true"/>
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column>
                    <f:facet name="header"><h:outputText styleClass="headerText" value="Изменяемое поле" /></f:facet>
                    <h:outputText value="#{orgModifyItem.valueName}" />
                </rich:column>
                <rich:column>
                    <f:facet name="header"><h:outputText styleClass="headerText" value="Данные ИС ПП" /></f:facet>
                    <h:outputText value="#{orgModifyItem.oldValue}" />
                </rich:column>
                <rich:column>
                    <f:facet name="header"><h:outputText styleClass="headerText" value="Данные АИС Реестр" /></f:facet>
                    <h:outputText value="#{orgModifyItem.newValue}" rendered="#{orgModifyItem.isEqual()}"/>
                    <h:outputText value="#{orgModifyItem.newValue}" styleClass="error-output-text" rendered="#{!orgModifyItem.isEqual()}"/>
                </rich:column>
            </rich:dataTable>
            <rich:spacer height="20px" />
            <a4j:commandButton value="Применить" action="#{NSIOrgsRegistrySynchPage.doApplyOneOrg}" status="updateStatus" ajaxSingle="true"
                               onclick="this.disabled = true;" oncomplete="Richfaces.hideModalPanel('editSverkaPanel');this.disabled = false;"
                               style="width: 180px;" reRender="synchTable,synchTableInfoPanel,resultTitle,synchTableControl"/>
            <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('editSverkaPanel')" style="width: 180px;" ajaxSingle="true" />
        </rich:modalPanel>

        <rich:dataTable value="#{NSIOrgsRegistrySynchPage.items}" var="e" footerClass="data-table-footer"
                        width="100%" rows="20" columns="3" id="tableSverka" rowKeyVar="row" >

            <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}" rowspan="2" colspan="1">
                <f:facet name="header">
                    <h:outputText value="№"/>
                </f:facet>
                <h:outputText value="#{row+1}"/>
            </rich:column>

            <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}"  rowspan="1" colspan="1">
                <h:outputText value="Идентификатор ОО - " rendered="#{NSIOrgsRegistrySynchPage.isDeleteOperation(e.operation)}" />
                <h:outputText value="#{e.idOfOrg}" escape="false" rendered="#{NSIOrgsRegistrySynchPage.isDeleteOperation(e.operation)}" /><br/>
                <h:outputText value="Номер ОО"/> - <h:outputText value="#{e.orgNumber}" escape="false"/><br/>
                <h:outputText value="Наименование"/> - <h:outputText value="#{e.shortName} (#{e.officialName})" escape="false"/><br/>
            </rich:column>

            <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}" rowspan="2" colspan="1">
                <f:facet name="header">
                    <h:outputText value="Применить" />
                </f:facet>
                <h:selectBooleanCheckbox value="#{e.selected}" styleClass="checkboxes"
                                         rendered="#{!NSIOrgsRegistrySynchPage.isRenderApplied(e, false)}"/>
                <h:outputText value="применено" styleClass="output-text"
                              rendered="#{NSIOrgsRegistrySynchPage.isRenderApplied(e, true)}"/>
            </rich:column>
            <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)} padding-zero" rowspan="1" colspan="1"  breakBefore="true">
                <rich:dataTable rendered="#{e.orgsSize > 0}" var="org" value="#{e.orgs}" >
                    <rich:column>
                        <h:selectBooleanCheckbox value="#{org.selected}" styleClass="checkboxes"
                                      rendered="#{!NSIOrgsRegistrySynchPage.isRenderApplied(org, false) && !org.isSimilar && !org.isAdding}"/>
                        <h:outputText value="#{org.appliedItem}" escape="false"
                                      rendered="#{NSIOrgsRegistrySynchPage.isRenderApplied(org, false) && !org.isSimilar && !org.isAdding}"/>
                        <h:outputText value="#{org.itemType}" escape="false" rendered="#{org.isAdding}"/>
                        <rich:spacer height="10" />
                        <a4j:commandButton value="..." style="width: 25px; height:25px; text-align: right" title="Редактировать запись сверки"
                                           reRender="editSverkaPanel" styleClass="command-button" status="updateStatus" ajaxSingle="true"
                                           oncomplete="Richfaces.showModalPanel('editSverkaPanel');"
                                           rendered="#{!NSIOrgsRegistrySynchPage.isRenderApplied(org, false) && org.isModify}">
                        <f:setPropertyActionListener value="#{org}" target="#{NSIOrgsRegistrySynchPage.orgForEdit}" />
                        </a4j:commandButton>
                    </rich:column>
                    <rich:column rendered="#{!NSIOrgsRegistrySynchPage.nsi3()}">
                        <f:facet name="header">
                            <h:outputText value="Guid" />
                        </f:facet>
                        <h:outputText value="#{org.guid}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Ид НСИ-3" />
                        </f:facet>
                        <h:outputText value="#{org.globalId}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="ЕКИС ид" />
                        </f:facet>
                        <h:outputText value="#{org.ekisId}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="№ здания" />
                        </f:facet>
                        <h:outputText value="#{org.uniqueAddressId}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="ИНН" />
                        </f:facet>
                        <h:outputText value="#{org.inn}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="УНОМ" />
                        </f:facet>
                        <h:outputText value="#{org.unom}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="УНАД" />
                        </f:facet>
                        <h:outputText value="#{org.unad}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="ЕГИССО ид" />
                        </f:facet>
                        <h:outputText value="#{org.egissoId}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Адрес корпуса" />
                        </f:facet>
                        <h:outputText value="#{org.address}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Короткий адрес" />
                        </f:facet>
                        <h:outputText value="#{org.shortAddress}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Полное наименование" />
                        </f:facet>
                        <h:outputText value="#{org.officialName}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Краткое наименование" />
                        </f:facet>
                        <h:outputText value="#{org.shortName}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Статус" />
                        </f:facet>
                        <h:outputText value="#{org.stringState}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Руководитель ОО" />
                        </f:facet>
                        <h:outputText value="#{org.director}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Учредитель" />
                        </f:facet>
                        <h:outputText value="#{org.founder}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Подчиненность" />
                        </f:facet>
                        <h:outputText value="#{org.subordination}" escape="false" />
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Ид. орг." />
                        </f:facet>
                        <h:outputText value="#{org.idOfOrg}" escape="false" />
                    </rich:column>
                </rich:dataTable>
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="tableSverka" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
                    <a4j:support event="onpagechange"/>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>

    </h:panelGroup>


</h:panelGrid>

</h:panelGrid>