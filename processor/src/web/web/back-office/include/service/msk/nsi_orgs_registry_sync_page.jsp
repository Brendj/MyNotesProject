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
</style>


<%--@elvariable id="NSIOrgsRegistrySynchPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgsRegistrySynchPage"--%>
<h:panelGrid id="NSIOrgsRegistrySynchPage" styleClass="borderless-grid" binding="#{NSIOrgsRegistrySynchPage.pageComponent}">

    <h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
        <h:outputText escape="true" value="#{NSIOrgsRegistrySynchPage.errorMessages}" rendered="#{not empty NSIOrgsRegistrySynchPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
        <h:outputText escape="true" value="#{NSIOrgsRegistrySynchPage.infoMessages}" rendered="#{not empty NSIOrgsRegistrySynchPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
            <h:selectOneMenu id="revisionDates" value="#{NSIOrgsRegistrySynchPage.selectedRevision}" style="width:350px;" >
                <f:selectItems value="#{NSIOrgsRegistrySynchPage.revisions}"/>
            </h:selectOneMenu>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фильтр наименования ОО" styleClass="output-text" />
            <h:inputText value="#{NSIOrgsRegistrySynchPage.nameFilter}" size="64" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Обновить" action="#{NSIOrgsRegistrySynchPage.doUpdate}"
                               reRender="synchTable,synchTableInfoPanel,revisionInfo,resultTitle" styleClass="command-button" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            <a4j:status id="updateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>


    <h:panelGrid style="text-align: center" columns="2">
        <h:panelGroup id="synchTable">
            <h:outputText id="resultTitle" value="Результаты #{NSIOrgsRegistrySynchPage.resultTitle}"
                          styleClass="page-header-text"/>
            <h:panelGrid style="text-align: right" columns="5" columnClasses="selectAll_text,selectAll_button">
                <h:outputText value="Всего в списке: #{NSIOrgsRegistrySynchPage.totalCount}" styleClass="output-text" />
            </h:panelGrid>
            <rich:dataTable value="#{NSIOrgsRegistrySynchPage.items}" var="e" footerClass="data-table-footer"
                            width="350px" rows="20" id="table" rowKeyVar="row">
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="№"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{row+1}"></h:outputText>
                </rich:column>

                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Оригинальное наименование"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.originName}" escape="false"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Новое наименование"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.newName}" escape="false"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Оригинальный адрес"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.originAddress}" escape="false"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Новый адрес"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.newAddress}" escape="false"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Оригинальная дополнительная инф."></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.originSpec}" escape="false"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Новая дополнительная инф."></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.newSpec}" escape="false"></h:outputText>
                </rich:column>

                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Тип операции"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{e.operationType}"></h:outputText>
                </rich:column>
                <rich:column styleClass="#{NSIOrgsRegistrySynchPage.getLineStyleClass(e)}">
                    <f:facet name="header">
                        <h:outputText value="Применить" />
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{e.applied}" styleClass="checkboxes"
                                             rendered="#{!NSIOrgsRegistrySynchPage.isRenderApplied(e, false)}"/>
                    <h:outputText value="применено" styleClass="output-text"
                                  rendered="#{NSIOrgsRegistrySynchPage.isRenderApplied(e, true)}"/>
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
            <a4j:commandButton value="Провести полную сверку" action="#{NSIOrgsRegistrySynchPage.doRefresh}" reRender="synchTable,synchTableInfoPanel,revisionInfo,revisionDates" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
        </h:panelGroup>

        <h:panelGrid>
            <h:panelGrid id="synchTableControl">
                <a4j:commandButton value="Подтвердить все" action="#{NSIOrgsRegistrySynchPage.doApply}" reRender="synchTable,synchTableInfoPanel,revisionInfo" status="updateStatus"
                                   onclick="this.disabled = true;" oncomplete="this.disabled = false;" style="width: 180px;"/>
            </h:panelGrid>
            <h:panelGrid id="revisionInfo" columns="2">
                <h:panelGroup styleClass="createClientRow"><h:outputText value="Количество созданных записей" styleClass="output-text"/></h:panelGroup>
                <h:outputText value="#{NSIOrgsRegistrySynchPage.creationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                <h:panelGroup styleClass="deleteClientRow"><h:outputText value="Количество удаленных записей" styleClass="output-text"/></h:panelGroup>
                <h:outputText value="#{NSIOrgsRegistrySynchPage.deletionsCount}" styleClass="output-text" style="font-weight: bold;"/>
                <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество измененных записей" styleClass="output-text"/></h:panelGroup>
                <h:outputText value="#{NSIOrgsRegistrySynchPage.modificationsCount}" styleClass="output-text" style="font-weight: bold;"/>
                <rich:spacer width="10px" />
                <rich:spacer width="10px" />
                <h:panelGroup styleClass="modifyClientRow"><h:outputText value="Количество найденных разногласий всего" styleClass="output-text"/></h:panelGroup>
                <h:outputText value="#{NSIOrgsRegistrySynchPage.totalCount}" styleClass="output-text" style="font-weight: bold;"/>
            </h:panelGrid>
        </h:panelGrid>
    </h:panelGrid>

</h:panelGrid>