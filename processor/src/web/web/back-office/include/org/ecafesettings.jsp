<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 18.12.12
  Time: 12:20
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="cafeSettingsEditListPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.ECafeSettingsEditListPage"--%>
<h:panelGrid id="ecafeSettingsEditListPage" binding="#{cafeSettingsEditListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:dataTable id="ecafeSettingsEditListTable" value="#{cafeSettingsEditListPage.cafeSettingsList}" var="setting" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№"/>
            </f:facet>
            <h:outputText value="#{row+1}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="GUID"/>
            </f:facet>
            <h:outputText value="#{setting.guid}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Версия"/>
            </f:facet>
            <h:outputText value="#{setting.globalVersion}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Идентификатор притнера"/>
            </f:facet>
            <h:outputText value="#{setting.identificator}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Статус"/>
            </f:facet>
            <h:selectOneListbox value="#{setting.deletedState}" size="1">
                <f:selectItem itemLabel="Не удален" itemValue="false"/>
                <f:selectItem itemLabel="Удален" itemValue="true"/>
            </h:selectOneListbox>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Значение"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{setting.settingValue}"
                               id="inplaceSettingValue" required="true"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">
                <a4j:support event="onviewactivated" reRender="ecafeSettingsEditListTable"/>
            </rich:inplaceInput>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Текстовое сообщение"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{setting.settingText}"
                               id="inplaceSettingText" required="true"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">
                <a4j:support event="onviewactivated" reRender="ecafeSettingsEditListTable"/>
            </rich:inplaceInput>
        </rich:column>
        <rich:column>
            <a4j:commandButton action="#{cafeSettingsEditListPage.save}" value="Сохранить" >
                <f:param name="id" value="#{setting.globalId}"/>
            </a4j:commandButton>
        </rich:column>
    </rich:dataTable>

    <a4j:commandButton value="Востановить" action="#{cafeSettingsEditListPage.updateList}" reRender="ecafeSettingsEditListTable"/>

</h:panelGrid>
<a4j:status id="sOrgCreateStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>