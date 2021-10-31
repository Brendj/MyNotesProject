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

    <c:if test="${accessoriesListPage.accessories != null && not empty accessoriesListPage.accessories}">
        <a4j:region>
            <h:outputText value="Технические характеристики:" style="font: 12px Arial; font-weight: bold;"/>
            <c:forEach items="#{accessoriesListPage.accessories}" var="it" varStatus="loop">
                <h:panelGrid id="accessories" columns="6">
                    <h:outputText value="Тип оборудования: " style="padding-left: 10px; font: 12px Arial"/>
                    <h:selectOneMenu value="#{it.accessoryType}" styleClass="input-text" style="width: 150px">
                        <f:selectItems value="#{accessoriesListPage.accessoryTypes}" />
                    </h:selectOneMenu>
                    <h:outputText value="Id оборудования: " style="padding-left: 30px; font: 12px Arial"/>
                    <h:inputText size="15" value="#{it.accessoryNumber}"/>
                    <h:outputText value="Id корпуса: " style="padding-left: 50px; font: 12px Arial"/>
                    <h:inputText size="3" value="#{it.idOfTargetOrg}"/>
                </h:panelGrid>
            </c:forEach>
            <br/>
            <h:outputText value="Оборудование корпусов:" style="font: 12px Arial; font-weight: bold;"/>
            <h:panelGrid id="org_equipment" columns="2">
                <h:outputText value="АРМ администратора ОУ (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_armadmin}" maxlength="2"/>
                <h:outputText value="АРМ контролера входа (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_armcontroller}" maxlength="2"/>
                <h:outputText value="АРМ оператора питания (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_armoperator}" maxlength="2"/>
                <h:outputText value="АРМ библиотекаря (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_armlibrary}" maxlength="2"/>
                <h:outputText value="Турникеты (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_turnstiles}" maxlength="2"/>
                <h:outputText value="Электрозамки со считывателями (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_elocks}" maxlength="2"/>
                <h:outputText value="Считыватели автономные настенные (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_ereaders}" maxlength="2"/>
                <h:outputText value="Инфопанели (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_infopanels}" maxlength="2"/>
                <h:outputText value="Инфокиоски (кол-во): " style="padding-left: 10px; font: 12px Arial"/>
                <h:inputText size="2" value="#{accessoriesListPage.orgInventoryItem.amount_infokiosks}" maxlength="2"/>
            </h:panelGrid>
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