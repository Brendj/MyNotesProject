<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="accessoriesListPage" type="ru.axetta.ecafe.processor.web.ui.org.accessories.AccessoriesListPage"--%>
<h:panelGrid id="accessoriesListPage" binding="#{accessoriesListPage.pageComponent}" styleClass="borderless-grid">
    <%--<h:panelGrid styleClass="borderless-grid">
        <h:panelGrid columns="2">
            <h:outputText escape="true" value="Организация-источник событий (в файле синхронизации)" styleClass="output-text" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{accessoriesListPage.filter}}" />
            </h:panelGroup>

            <a4j:commandButton value="Обновить" action="#{accessoriesListPage.update}"
                               reRender="workspaceTogglePanel,accessories"
                               styleClass="command-button" status="accessoriesStatus" />
        </h:panelGrid>

    </h:panelGrid>--%>

    <c:if test="${accessoriesListPage.accessories != null && not empty accessoriesListPage.accessories}">
        <a4j:region>
            <c:forEach items="#{accessoriesListPage.accessories}" var="it" varStatus="loop">
                <h:panelGrid id="accessories" columns="6">
                    <h:outputText value="Тип оборудоания: " style="font: 12px Arial"/>
                    <h:selectOneMenu value="#{it.accessoryType}" styleClass="input-text" style="width: 150px">
                        <f:selectItems value="#{accessoriesListPage.accessoryTypes}" />
                    </h:selectOneMenu>
                    <h:outputText value="Id оборудования: " style="padding-left: 20px; font: 12px Arial"/>
                    <h:inputText size="3" value="#{it.accessoryNumber}"/>
                    <h:outputText value="Id корпуса: " style="padding-left: 50px; font: 12px Arial"/>
                    <h:inputText size="3" value="#{it.idOfTargetOrg}"/>
                </h:panelGrid>
            </c:forEach>
            <h:panelGrid columns="3">
                    <a4j:commandButton value="Сохранить" action="#{accessoriesListPage.save}"
                                       reRender="accessories"
                                       styleClass="command-button" status="accessoriesStatus" />
                    <a4j:commandButton value="Отменить" action="#{accessoriesListPage.update}"
                                       reRender="accessories"
                                       styleClass="command-button" status="accessoriesStatus" />

                    <a4j:status id="accessoriesStatus">
                        <f:facet name="start">
                            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                        </f:facet>
                    </a4j:status>
            </h:panelGrid>
        </a4j:region>
    </c:if>

</h:panelGrid>