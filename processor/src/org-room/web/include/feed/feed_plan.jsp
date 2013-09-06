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

.payed {
    background-color: #CCFFCC;
}
.selectClientGroup {
    background-color: #CCDBFF;
}
.subcategoryClientGroup {
    background-color: #FFDD57;
}
.totalMessages_col1 {
    width: 1%;
}
.totalMessages_col2 {
    width: 99%;
    text-align: right;
}
.calendar {
    width: 50%;
}
.calendarText {
    width: 1%;
    text-align: right;
}
.status {
    width: 49%
}
</style>

<%--@elvariable id="feedPlanPage" type="ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage"--%>
<%--@elvariable id="yesNoConfirmPanel" type="ru.axetta.ecafe.processor.web.ui.modal.YesNoConfirmPanel"--%>
<a4j:form id="setupFeedPlanForm">
    <h:panelGrid id="setupFeedPlanGrid" binding="#{feedPlanPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">
        <a4j:region>
        <h:panelGrid id="planDateCalendar" columns="3" columnClasses="calendar,status,calendarText">
            <h:panelGrid columns="4">
                <h:outputText styleClass="output-text-mod" value="План питания:"/>
                <a4j:commandButton value="<" action="#{feedPlanPage.doDecreaseDay}" reRender="planDateCalendar,planGrid,groupsGrid,messages,totalMessage" status="feedPlanStatus"/>
                <rich:calendar value="#{feedPlanPage.planDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false"
                               valueChangeListener="#{feedPlanPage.doChangePlanDate}" status="feedPlanStatus">
                    <a4j:support event="onchanged" reRender="planGrid,groupsGrid,messages,totalMessage" bypassUpdates="true" />
                </rich:calendar>
                <a4j:commandButton value=">" action="#{feedPlanPage.doIncreaseDay}" reRender="planDateCalendar,planGrid,groupsGrid,messages,totalMessage" status="feedPlanStatus"/>
            </h:panelGrid>
            <a4j:status id="feedPlanStatus">
                <f:facet name="start">
                    <h:panelGrid columns="2">
                        <h:outputText value="Загрузка... " styleClass="output-text-mod"/>
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </h:panelGrid>
                </f:facet>
            </a4j:status>
            <h:panelGrid id="currentTotalString">
                <h:outputText styleClass="output-text-mod" style="font-weight: bold" value="#{feedPlanPage.currentTotalString}"/>
            </h:panelGrid>
        </h:panelGrid>
        </a4j:region>

        <h:panelGrid id="planGrid" columns="2" style="width: 100%" columnClasses="clientsPanel,groupsPanel">
            <%-- КЛИЕНТЫ --%>
            <rich:panel style="height: 450px; width: 100%; overflow: auto;">
                <a4j:region>
                <rich:dataTable id="planTable" value="#{feedPlanPage.clients}" var="client" style="width: 100%">
                    <rich:column style="text-align: center;" styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оплата"/>
                        </f:facet>
                        <h:graphicImage url="/images/icon/#{client.actionIcon}.png"/>
                    </rich:column>

                    <rich:column style="text-align: center;" styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оператор"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text-mod" value="#{client.action}" rendered="#{!client.saved}">
                            <a4j:support event="onclick" reRender="clientFeedActionPanel" action="#{feedPlanPage.doShowClientFeedActionPanel(client)}"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.show();" status="feedPlanStatus"/>
                        </a4j:commandLink>
                        <h:outputText styleClass="output-text-mod" value="#{client.action}" rendered="#{client.saved}"/>
                    </rich:column>

                    <rich:column style="text-align: center;" styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Класс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{feedPlanPage.getGroupName(client.idofclientgroup)}"/>
                    </rich:column>

                    <rich:column styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="ФИО"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" escape="false" value="#{client.fullName}"/>
                    </rich:column>

                    <rich:column styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Замена"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text" value="#{feedPlanPage.getReplaceClient(client)}" rendered="#{!client.saved}">
                            <a4j:support event="onclick" reRender="replaceClientPanel" action="#{feedPlanPage.doShowReplaceClientPanel(client)}"
                                         oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('replaceClientPanel')}.show();" status="feedPlanStatus"/>
                        </a4j:commandLink>
                        <h:outputText styleClass="output-text" value="#{feedPlanPage.getReplaceClient(client)}" rendered="#{client.saved}"></h:outputText>
                    </rich:column>

                    <rich:column styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Правило"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{client.ruleDescription}"/>
                    </rich:column>

                    <rich:column styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Комплекс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="Комплекс №#{client.complex}"/>
                    </rich:column>

                    <rich:column styleClass="#{client.lineStyleClass}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Цена"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{client.price} р."/>
                    </rich:column>
                </rich:dataTable>
                </a4j:region>
            </rich:panel>

            <%-- КЛАССЫ --%>
            <rich:panel id="groupsGrid" style="height: 450px; width: 100%; overflow: auto;">
                <a4j:region>
                <rich:dataTable id="groupsTable" value="#{feedPlanPage.groups}" var="idoclientgroup" style="width: 100%">
                    <rich:column style="text-align: center; line-height: 0.9;" styleClass="#{feedPlanPage.getClientGroupStyleClass(idoclientgroup)}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="К<br/>л<br/>а<br/>с<br/>с"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text-mod" value="#{feedPlanPage.getGroupName(idoclientgroup)}"
                                         rendered="#{!feedPlanPage.isOrderedComplex(idoclientgroup)}" >
                            <a4j:support reRender="planTable,messages,groupsGrid,currentTotalString" event="onclick" action="#{feedPlanPage.doChangeGroup(idoclientgroup)}" status="feedPlanStatus" />
                        </a4j:commandLink>
                        <h:outputText styleClass="output-text-mod" value="#{feedPlanPage.getGroupName(idoclientgroup)}"
                                      rendered="#{feedPlanPage.isOrderedComplex(idoclientgroup)}"/>
                    </rich:column>

                    <rich:columns value="#{feedPlanPage.complexes}" var="complex"
                                  index="ind" headerClass="thin-center-aligned-column"  width="1%" style="width: 1%;"
                                  styleClass="#{feedPlanPage.getClientGroupStyleClass(idoclientgroup)}">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" escape="false" value="К<br/>о<br/>м<br/>п<br/>л<br/>е<br/>к<br/>с<br/><br/>№#{complex}" />
                        </f:facet>
                        <h:panelGrid style="background: no-repeat url('/orgroom/images/split.jpg'); width: 40px; height: 40px" columnClasses="numbersTableCol1,numbersTableCol2" columns="2">
                            <h:outputText styleClass="output-text-mod" style="font-weight: bold; color: red" value="#{feedPlanPage.getPayedComplexCount(idoclientgroup, complex)}"
                                          rendered="#{!feedPlanPage.isOrderedComplex(idoclientgroup)}"/>
                            <h:outputText styleClass="output-text-mod" style="font-weight: bold; color: red" escape="false" value="&nbsp;"
                                          rendered="#{feedPlanPage.isOrderedComplex(idoclientgroup)}"/>
                            <h:outputText value=""/>
                            <h:outputText value=""/>
                            <h:outputText styleClass="output-text-mod" style="font-weight: bold; color: blue" value="#{feedPlanPage.getComplexCount(idoclientgroup, complex)}" />
                        </h:panelGrid>
                    </rich:columns>
                </rich:dataTable>
                </a4j:region>
            </rich:panel>
        </h:panelGrid>

        <h:panelGrid id="messages">
            <h:outputText escape="true" value="#{feedPlanPage.errorMessages}" rendered="#{not empty feedPlanPage.errorMessages}" styleClass="error-messages" />
            <h:outputText escape="true" value="#{feedPlanPage.infoMessages}" rendered="#{not empty feedPlanPage.infoMessages}" styleClass="info-messages" />
        </h:panelGrid>

        <h:panelGrid id="totalMessage" columns="2" columnClasses="totalMessage_col1,totalMessage_col2">
            <h:outputText value=""/>
            <h:panelGrid columns="2">
                <h:outputText value="Итого по #{feedPlanPage.selectedClientGroupName} (оплачено/к оплате/всего):" styleClass="output-text" style="font-weight: bold"/>
                <h:outputText value="#{feedPlanPage.complexesTotalString}" styleClass="output-text"/>
            </h:panelGrid>
        </h:panelGrid>

        <h:panelGrid columns="6" columnClasses="setupFeedPlanGridControlImg1,setupFeedPlanGridControlTxt1,setupFeedPlanGridControlImg2,setupFeedPlanGridControlTxt2,setupFeedPlanGridControlImg3,setupFeedPlanGridControlTxt3">
            <a4j:region>
                <%--<a4j:commandButton image="/images/icon/money.png" style="padding-right: 5px">
                    <a4j:support event="onclick" reRender="orderRegistrationResultPanel" action="#{feedPlanPage.doShowOrderRegistrationResultPanel}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('orderRegistrationResultPanel')}.show();"/>
                </a4j:commandButton>
                <a4j:commandLink value="Оплатить" styleClass="output-text" style="vertical-align: middle; padding-right: 20px;">
                    <a4j:support event="onclick" reRender="orderRegistrationResultPanel" action="#{feedPlanPage.doShowOrderRegistrationResultPanel}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('orderRegistrationResultPanel')}.show();"/>
                </a4j:commandLink>--%>
                <a4j:commandLink value="Оплатить" styleClass="output-text" style="vertical-align: middle; padding-right: 20px;">
                    <a4j:support event="onclick" reRender="yesNoConfirmPanel" action="#{feedPlanPage.doShowOrderRegistrationResultPanel}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('yesNoConfirmPanel')}.show();">
                        <f:setPropertyActionListener value="Вы уверены что хотите произвести оплату?" target="#{yesNoConfirmPanel.message}" />
                        <f:setPropertyActionListener value="orderRegistrationResultPanel" target="#{yesNoConfirmPanel.nodePanel}" />
                    </a4j:support>
                </a4j:commandLink>

                <a4j:commandButton image="/images/icon/blank.png" reRender="planGrid,groupsGrid,messages" action="#{feedPlanPage.doClearPlan}" style="padding-right: 5px"/>
                <a4j:commandLink value="Очистить план" reRender="planGrid,groupsGrid,messages" styleClass="output-text" action="#{feedPlanPage.doClearPlan}" style="vertical-align: middle; padding-right: 20px;"/>

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