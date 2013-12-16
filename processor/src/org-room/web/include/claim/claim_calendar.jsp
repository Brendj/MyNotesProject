<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .output-text-mod {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
        white-space: nowrap;
        padding-right: 10px;
    }
</style>



<%--@elvariable id="claimCalendarEditPage" type="ru.axetta.ecafe.processor.web.ui.claim.ClaimCalendarEditPage"--%>
<%--@elvariable id="yesNoConfirmPanel" type="ru.axetta.ecafe.processor.web.ui.modal.YesNoConfirmPanel"--%>
<a4j:form id="claimCalendarForm">
    <h:panelGrid id="claimCalendarEditPage" binding="#{claimCalendarEditPage.pageComponent}" styleClass="borderless-grid" style="width: 100%"
                 >


        <h:panelGrid columns="2">
            <a4j:region>
                <h:panelGrid id="months" styleClass="borderless-grid">
                    <h:selectOneMenu id="month" value="#{claimCalendarEditPage.month}" style="width:150px;" rendered="#{!claimCalendarEditPage.changesMade}">
                        <f:selectItems value="#{claimCalendarEditPage.months}"/>
                        <a4j:support event="onchange" actionListener="#{claimCalendarEditPage.doChangeMonth}" reRender="claimsCalendar, controls"/>
                    </h:selectOneMenu>


                    <h:selectOneMenu id="monthWithConfirm" value="#{claimCalendarEditPage.month}" style="width:150px;" rendered="#{claimCalendarEditPage.changesMade}" >
                        <f:selectItems value="#{claimCalendarEditPage.months}"/>
                        <a4j:support event="onchange" reRender="yesNoConfirmPanel" action="#{mainPage.doShowYesNoConfirmModal}"
                                     oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('yesNoConfirmPanel')}.show();">
                            <f:setPropertyActionListener value="Все изменения будут утеряны, Вы хотите продолжить?" target="#{yesNoConfirmPanel.message}" />
                        </a4j:support>
                    </h:selectOneMenu>
                </h:panelGrid>
            </a4j:region>

            <a4j:region>
                <h:panelGrid id="group" styleClass="borderless-grid">
                    <h:selectOneMenu id="goodsGroup" value="#{claimCalendarEditPage.goodGroup}" style="width:150px;" rendered="#{!claimCalendarEditPage.changesMade}">
                        <f:selectItems value="#{claimCalendarEditPage.goodsGroups}"/>
                        <a4j:support event="onchange" actionListener="#{claimCalendarEditPage.doChangeGoodsGroup}" reRender="claimsCalendar, controls"/>
                    </h:selectOneMenu>

                    <h:selectOneMenu id="goodsGroupWithConfirm" value="#{claimCalendarEditPage.goodGroup}" style="width:150px;" rendered="#{claimCalendarEditPage.changesMade}" >
                        <f:selectItems value="#{claimCalendarEditPage.goodsGroups}"/>
                        <a4j:support event="onchange" reRender="yesNoConfirmPanel" action="#{mainPage.doShowYesNoConfirmModal}"
                                     oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('yesNoConfirmPanel')}.show();">
                            <f:setPropertyActionListener value="Все изменения будут утеряны, Вы хотите продолжить?" target="#{yesNoConfirmPanel.message}" />
                        </a4j:support>
                    </h:selectOneMenu>
                </h:panelGrid>
            </a4j:region>
        </h:panelGrid>

        <a4j:region>
        <rich:panel id="claimsCalendar" style="height: 450px; width: 1350px; overflow: auto;">
            <rich:dataTable value="#{claimCalendarEditPage.entries}" var="e"
                            width="350px" rows="15" id="table" rowKeyVar="row" footerClass="data-table-footer">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="№"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{row+1}"></h:outputText>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Комплекс" />
                    </f:facet>
                    <h:outputText styleClass="output-text-mod" value="#{e.food}" />
                </rich:column>
                <rich:columns value="#{claimCalendarEditPage.columns}"
                              var="col" styleClass="left-aligned-column" style="#{claimCalendarEditPage.getColumnColor(col.date)}"
                              index="ind" headerClass="center-aligned-column" >
                    <f:facet name="header">
                        <h:outputText escape="true" value="#{col.title}" />
                    </f:facet>

                    <h:outputText rendered="#{!claimCalendarEditPage.isEditable(e.idofgood,col.date)}" value="#{e.data[col.date]}"
                                  styleClass="output-text" />

                    <rich:inplaceInput layout="block" value="#{e.data[col.date]}" rendered="#{claimCalendarEditPage.isEditable(e.idofgood,col.date)}"
                                       converterMessage="Price value should be integer. Price at row #{row+1} can't be changed."
                                       id="inplace" changedHoverClass="hover" viewHoverClass="hover"
                                       viewClass="inplace" changedClass="inplace"
                                       selectOnEdit="true" editEvent="ondblclick">
                        <a4j:support event="onviewactivated" reRender="controls,months,group,messages" actionListener="#{claimCalendarEditPage.doValueChange}" >
                            <f:attribute name="prevValue" value="#{e.sourceData[col.date]}"/>
                            <f:attribute name="newValue" value="#{e.data[col.date]}"/>
                        </a4j:support>
                    </rich:inplaceInput>
                </rich:columns>
                <%--<rich:column>
                    <f:facet name="header">
                        <h:outputText value="Price" />
                    </f:facet>
                </rich:column>--%>
                <f:facet name="footer">
                    <rich:datascroller ajaxSingle="false"/>
                </f:facet>
            </rich:dataTable>
        </rich:panel>

        <h:panelGrid style="width: 100%; background: none" id="controls">
            <h:outputText escape="true" value="#{claimCalendarEditPage.errorMessages}" rendered="#{not empty claimCalendarEditPage.errorMessages}" styleClass="error-messages" />
            <h:outputText escape="true" value="#{claimCalendarEditPage.infoMessages}" rendered="#{not empty claimCalendarEditPage.infoMessages}" styleClass="info-messages" />
            <h:panelGrid columns="2">
                <h:panelGrid columns="2">
                    <a4j:commandButton value="Применить" reRender="claimsCalendar,messages,months,group,controls" disabled="#{!claimCalendarEditPage.changesMade}"
                                       action="#{claimCalendarEditPage.doApply}">
                    </a4j:commandButton>

                    <a4j:commandButton value="Отменить" reRender="claimsCalendar,messages,months,group,controls" disabled="#{!claimCalendarEditPage.changesMade}"
                                       action="#{claimCalendarEditPage.doCancel}">
                    </a4j:commandButton>
                </h:panelGrid>

                <a4j:status id="registerStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
            </h:panelGrid>
        </h:panelGrid>
        </a4j:region>
    </h:panelGrid>
</a4j:form>