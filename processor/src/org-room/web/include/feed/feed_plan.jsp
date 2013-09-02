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
}

.thin-center-aligned-column {
    text-align: center;
    vertical-align: middle;
    width: 1%;
    height: 100px;
    line-height: 0.9;
}

.clientsPanel {
    width: 70%;
}

.groupsPanel {
    width: 30%;
}

.numbersTableCol1 {
    width: 100%;
}
.numbersTableCol2 {
    text-align: right;

}

.setupFeedPlanGridControlImg1 {
    padding-right: 5px;
    vertical-align: middle;
}
.setupFeedPlanGridControlTxt1 {
    padding-right: 20px;
    vertical-align: middle;
}
.setupFeedPlanGridControlImg2 {
    padding-right: 5px;
    vertical-align: middle;
}
.setupFeedPlanGridControlTxt2 {
    padding-right: 20px;
    vertical-align: middle;
}
.setupFeedPlanGridControlImg3 {
    padding-right: 5px;
    vertical-align: middle;
}
.setupFeedPlanGridControlTxt3 {
    padding-right: 20px;
    vertical-align: middle;

}
</style>

<%--@elvariable id="feedPlanPage" type="ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage"--%>
<a4j:form id="setupFeedPlanForm">
    <h:panelGrid id="setupFeedPlanGrid" binding="#{feedPlanPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">
        <h:panelGrid columns="2">
            <a4j:region>
            <h:outputText styleClass="output-text-mod" value="План питания:"/>
            <rich:calendar value="#{feedPlanPage.planDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false"
                           valueChangeListener="#{feedPlanPage.doChangePlanDate}">
                <a4j:support event="onchanged" reRender="planGrid,groupsGrid" bypassUpdates="true" />
            </rich:calendar>
            </a4j:region>
        </h:panelGrid>

        <h:panelGrid id="planGrid" columns="2" style="width: 100%" columnClasses="clientsPanel,groupsPanel">
            <%-- КЛИЕНТЫ --%>
            <rich:panel id="claimsCalendar" style="height: 450px; width: 100%; overflow: auto;">
                <a4j:region>
                <rich:dataTable id="planTable" value="#{feedPlanPage.clients}" var="client" style="width: 100%">
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оплата"/>
                        </f:facet>
                        <a4j:commandButton image="/images/icon/#{client.actionIcon}.png" styleClass="command-button" />
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оператор"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text-mod" value="#{client.action}" >
                            <a4j:support event="onclick" reRender="clientFeedActionPanel" action="#{feedPlanPage.doShowClientFeedActionPanel(client)}"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.show();"/>
                        </a4j:commandLink>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Класс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{feedPlanPage.getGroupName(client.idofclientgroup)}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="ФИО"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" escape="false" value="#{client.fullName}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Замена"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text" value="[Нажмите для выбора замены]" >
                        </a4j:commandLink>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Правило"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{client.ruleDescription}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Комплекс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="Комплекс №#{client.complex}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Цена"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{client.price} р."/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Заказ"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="нет"/>
                    </rich:column>
                </rich:dataTable>
                </a4j:region>
            </rich:panel>

            <%-- КЛАССЫ --%>
            <rich:panel id="groupsGrid" style="height: 450px; width: 100%; overflow: auto;">
                <a4j:region>
                <rich:dataTable id="groupsTable" value="#{feedPlanPage.groups}" var="idoclientgroup" style="width: 100%">
                    <rich:column style="text-align: center; line-height: 0.9;">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="К<br/>л<br/>а<br/>с<br/>с"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text-mod" value="#{feedPlanPage.getGroupName(idoclientgroup)}" >
                            <a4j:support reRender="planTable" event="onclick" action="#{feedPlanPage.doChangeGroup(idoclientgroup)}" />
                        </a4j:commandLink>
                    </rich:column>

                    <rich:columns value="#{feedPlanPage.complexes}" var="complex" styleClass="left-aligned-column"
                                  index="ind" headerClass="thin-center-aligned-column"  width="1%" style="width: 1%;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" escape="false" value="К<br/>о<br/>м<br/>п<br/>л<br/>е<br/>к<br/>с<br/><br/>№#{complex}" />
                        </f:facet>
                        <h:panelGrid style="background: no-repeat url('/orgroom/images/split.jpg'); width: 40px; height: 40px" columnClasses="numbersTableCol1,numbersTableCol2" columns="2">
                            <h:outputText styleClass="output-text-mod" value="0"/>
                            <h:outputText value=""/>
                            <h:outputText value=""/>
                            <h:outputText styleClass="output-text-mod" value="#{feedPlanPage.getComplexCount(idoclientgroup, complex)}" />
                        </h:panelGrid>
                    </rich:columns>
                </rich:dataTable>
                </a4j:region>
            </rich:panel>
        </h:panelGrid>

        <h:panelGrid columns="6" columnClasses="setupFeedPlanGridControlImg1,setupFeedPlanGridControlTxt1,setupFeedPlanGridControlImg2,setupFeedPlanGridControlTxt2,setupFeedPlanGridControlImg3,setupFeedPlanGridControlTxt3">
            <a4j:region>
                <a4j:commandButton image="/images/icon/money.png" action="#{feedPlanPage.doApply}" style="padding-right: 5px"/>
                <a4j:commandLink value="Оплатить" styleClass="output-text" action="#{feedPlanPage.doApply}" style="vertical-align: middle; padding-right: 20px;"/>

                <a4j:commandButton image="/images/icon/blank.png" reRender="planGrid,groupsGrid" action="#{feedPlanPage.doClearPlan}" style="padding-right: 5px"/>
                <a4j:commandLink value="Очистить план" reRender="" styleClass="output-text" action="#{feedPlanPage.doClearPlan}" style="vertical-align: middle; padding-right: 20px;"/>

                <a4j:commandButton image="/images/icon/cancel.png" style="padding-right: 5px">
                    <a4j:support event="onclick" reRender="disableComplexPanel" action="#{feedPlanPage.doShowDisableComplexPanel}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('disableComplexPanel')}.show();"/>
                </a4j:commandButton>
                <a4j:commandLink value="Исключить комплекс" styleClass="output-text" style="vertical-align: middle;">
                    <a4j:support event="onclick" reRender="disableComplexPanel" action="#{feedPlanPage.doShowDisableComplexPanel}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('disableComplexPanel')}.show();"/>
                </a4j:commandLink>
            </a4j:region>
        </h:panelGrid>
    </h:panelGrid>
</a4j:form>