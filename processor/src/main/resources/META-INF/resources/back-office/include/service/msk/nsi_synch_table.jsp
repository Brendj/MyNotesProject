<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="NSIOrgRegistrySynchOverviewPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySynchOverviewPage"--%>

<h:panelGrid id="NSIOrgRegistrySynchOverviewPage" styleClass="borderless-grid" binding="#{NSIOrgRegistrySynchOverviewPage.pageComponent}">
    <%--<a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>--%>


    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Фильтр по названию организации" styleClass="output-text" />
        <h:inputText value="#{NSIOrgRegistrySynchOverviewPage.orgFilter}" size="64" styleClass="input-text" />

        <h:outputText value="Только не синхронизированные последние 2 дня" styleClass="output-text"/>
        <h:selectBooleanCheckbox value="#{NSIOrgRegistrySynchOverviewPage.showOnlyUnsynch}"/>

        <h:outputText escape="true" value="Тип сверки" styleClass="output-text" />
        <h:selectOneMenu id="regionsList" value="#{NSIOrgRegistrySynchOverviewPage.typeFilter}" style="width:325px;" >
            <f:selectItems value="#{NSIOrgRegistrySynchOverviewPage.types}"/>
        </h:selectOneMenu>
    </h:panelGrid>

    <a4j:commandButton value="Обновить" action="#{NSIOrgRegistrySynchOverviewPage.doUpdate}"
                       reRender="workspaceTogglePanel" styleClass="command-button" />
    <h:commandButton value="Генерировать в Excel" actionListener="#{NSIOrgRegistrySynchOverviewPage.doGenerateXLS}" styleClass="command-button" />

    <rich:dataTable value="#{NSIOrgRegistrySynchOverviewPage.list}" var="e" footerClass="data-table-footer"
                    width="350px" rows="20" id="table" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№"></h:outputText>
            </f:facet>
            <h:outputText value="#{row+1}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}"></h:outputText>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="ID" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.idoforg}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Наименование" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.orgName}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Всего" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.total}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Добавленных" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.created}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Измененных" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.modified}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Перемещенных" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.moved}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Удаленных" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.deleted}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Дата сверки" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.date}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Тип сверки" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{e.type}"
                          style="#{(e.outOfSynch) ? 'color:red' : ''}" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="table" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

</h:panelGrid>
