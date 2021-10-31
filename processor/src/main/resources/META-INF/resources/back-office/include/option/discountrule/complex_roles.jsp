<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель редактирования списка ролей комплексов --%>
<%--@elvariable id="complexRuleEditPage" type="ru.axetta.ecafe.processor.web.ui.option.discountrule.ComplexRuleEditPage"--%>
<h:panelGrid id="complexRuleEditPanel" binding="#{complexRuleEditPage.pageComponent}" styleClass="borderless-grid" columns="1">

    <rich:dataTable id="complexRuleTable" value="#{complexRuleEditPage.complexRoles}" var="complexRole" rows="20"
                    columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Идентификатор" escape="true" styleClass="column-header"/>
            </f:facet>
            <h:outputText value="#{complexRole.idOfRole}" escape="true" styleClass="column-header"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Роль комплекса" escape="true" styleClass="column-header"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{complexRole.roleName}"
                               id="inplaceRoleName"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">

                <a4j:support event="onviewactivated" reRender="complexRuleEditPanel"/>

            </rich:inplaceInput>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Обобщенная роль" escape="true" styleClass="column-header"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{complexRole.extendRoleName}"
                               id="inplaceExtendRoleName"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">

                <a4j:support event="onviewactivated" reRender="complexRuleEditPanel"/>

            </rich:inplaceInput>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="complexRuleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Сохранить" action="#{complexRuleEditPage.updateRule}" reRender="complexRuleEditPanel"
                           styleClass="command-button" />
        <a4j:commandButton value="Восстановить" action="#{complexRuleEditPage.show}" reRender="complexRuleEditPanel"
                           ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>