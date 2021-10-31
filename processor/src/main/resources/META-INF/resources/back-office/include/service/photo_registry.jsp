<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="photoRegistryPage" type="ru.axetta.ecafe.processor.web.ui.service.PhotoRegistryPage"--%>
<h:panelGrid id="photoRegistryPage" styleClass="borderless-grid" binding="#{photoRegistryPage.pageComponent}">

    <h:panelGrid styleClass="borderless-grid" id="synchTableInfoPanel" style="padding-bottom: 5px;">
        <h:outputText escape="true" value="#{photoRegistryPage.errorMessages}" rendered="#{not empty photoRegistryPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
        <h:outputText escape="true" value="#{photoRegistryPage.infoMessages}" rendered="#{not empty photoRegistryPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:simpleTogglePanel label="Параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid" rendered="#{photoRegistryPage.displayOrgSelection}">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{photoRegistryPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Провести сверку" action="#{photoRegistryPage.doUpdate()}" reRender="synchTable,synchTableInfoPanel"
                               status="updateStatus" onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
            <a4j:status id="updateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGrid style="text-align: center" columns="2">
        <h:panelGroup id="synchTable">
            <h:panelGrid style="text-align: center" columns="10" columnClasses="selectAll_text,selectAll_button">
                <h:outputText value="Всего в списке: #{photoRegistryPage.totalCount}" styleClass="output-text" />
                <rich:spacer width="20px" />
                <a4j:commandLink value="Применить все записи" action="#{photoRegistryPage.doSelectAll()}"
                                 reRender="workspaceTogglePanel,synchTable" styleClass="command-button" />
                <rich:spacer width="20px" />
                <a4j:commandLink value="Отклонить все записи" action="#{photoRegistryPage.doDenyAll()}"
                                 reRender="workspaceTogglePanel,synchTable" styleClass="command-button" />
                <rich:spacer width="20px" />
                <a4j:commandLink value="Снять все действия для записей" action="#{photoRegistryPage.doUnmarkAll()}"
                                 reRender="workspaceTogglePanel,synchTable" styleClass="command-button" />
            </h:panelGrid>
            <rich:dataTable align="center" value="#{photoRegistryPage.items}" var="e" footerClass="data-table-footer"
                            width="1200px" rows="20" id="table" rowKeyVar="row" columnClasses="center-aligned-column">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="№"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{row+1}"></h:outputText>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="ФИО обучающегося" />
                    </f:facet>
                    <h:outputText styleClass="output-text" value="#{e.fullName}" />
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="ФИО представителя" />
                    </f:facet>
                    <h:outputText styleClass="output-text" value="#{e.guardianName}" />
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Предыдущая фотография" />
                    </f:facet>
                    <h:graphicImage value="data:image/jpg;base64,#{e.photoContentBase64}"
                                    width="100"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Новая фотография" />
                    </f:facet>
                    <h:graphicImage value="data:image/jpg;base64,#{e.newPhotoContentBase64}"
                                    width="100"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Действие" />
                    </f:facet>
                    <h:panelGrid columns="2" styleClass="borderless-grid" rendered="#{photoRegistryPage.displayOrgSelection}">
                        <h:selectBooleanCheckbox value="#{e.selected}" styleClass="checkboxes"
                                                 valueChangeListener="#{e.onSelectedStatusChange}">
                            <a4j:support reRender="synchTable" event="onclick" ajaxSingle="true"/>
                        </h:selectBooleanCheckbox>
                        <h:outputText escape="true" value="Принять" styleClass="output-text" />

                        <h:selectBooleanCheckbox value="#{e.denied}" styleClass="checkboxes"
                                                 valueChangeListener="#{e.onDeniedStatusChange}">
                            <a4j:support reRender="synchTable" event="onclick" ajaxSingle="true"/>
                        </h:selectBooleanCheckbox>
                        <h:outputText escape="true" value="Отклонить" styleClass="output-text" />
                    </h:panelGrid>
                </rich:column>
                <rich:column width="150">
                    <f:facet name="header">
                        <h:outputText value="Сообщение" />
                    </f:facet>
                    <h:outputText styleClass="output-text" value="#{e.error}" rendered="#{not empty e.error}" />
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
            <a4j:commandButton value="Принять изменения" action="#{photoRegistryPage.doApply()}" reRender="synchTable,synchTableInfoPanel" status="updateStatus"
                               onclick="this.disabled = true;" oncomplete="this.disabled = false;" disabled="#{photoRegistryPage.items.size() == 0}"/>
        </h:panelGroup>
    </h:panelGrid>

    <rich:modalPanel id="confirmation" width="400" height="200" headerClass="modal-panel-header">
        <f:facet name="header"><h:outputText value="Подтверждение" styleClass="output-text" style="text-align: center"/></f:facet>
        <h:panelGrid>
            <table class="borderless-grid">
                <tr>
                    <td style="text-align: center;">
                        <h:panelGrid columns="2">
                            <h:outputText value="#{photoRegistryPage.confirm}" styleClass="output-text" style="text-align: center"/>
                        </h:panelGrid>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: right;">
                        <h:panelGroup styleClass="borderless-div">
                            <h:commandButton type="button" value="Ок" style="width: 80px; margin-right: 10px; margin-left: 50px;"
                                   onclick="#{rich:component('confirmation')}.hide();submit();return false" styleClass="command-link"/>
                            <h:commandButton type="button" value="Отмена" style="width: 80px;"
                                   onclick="#{rich:component('confirmation')}.hide();return false" styleClass="command-link"/>
                        </h:panelGroup>
                    </td>
                </tr>
            </table>
        </h:panelGrid>
    </rich:modalPanel>

</h:panelGrid>