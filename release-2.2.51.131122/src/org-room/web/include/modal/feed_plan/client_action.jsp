<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<style>
.clientFeedModalCol1 {
    width: 1px;
    padding-bottom: 20px;
}
.clientFeedModalCol2 {
    width: 33%;
    padding-bottom: 20px;
}
.clientFeedModalCol3 {
    width: 1px;
    padding-bottom: 20px;
}
.clientFeedModalCol4 {
    width: 33%;
    padding-bottom: 20px;
}
.clientFeedModalCol5 {
    width: 1px;
    padding-bottom: 20px;
}
.clientFeedModalCol6 {
    width: 33%;
    padding-bottom: 20px;
}
</style>


<%--@elvariable id="clientFeedActionPanel" type="ru.axetta.ecafe.processor.web.ui.modal.feed_plan.ClientFeedActionPanel"--%>
<rich:modalPanel id="clientFeedActionPanel" width="300" height="100" resizeable="false" moveable="false" binding="#{clientFeedActionPanel.pageComponent}">
    <a4j:form>
        <a4j:region>
            <h:panelGrid columns="6" columnClasses="clientFeedModalCol1,clientFeedModalCol2,clientFeedModalCol3,clientFeedModalCol4,clientFeedModalCol5,clientFeedModalCol6">
                <a4j:commandButton image="/images/icon/stop.png"
                                   action="#{clientFeedActionPanel.doBlock}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="БЛОК" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doBlock}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>


                <a4j:commandButton image="/images/icon/play.png"
                                   action="#{clientFeedActionPanel.doPay}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="ОПЛАТА" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doPay}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>


                <a4j:commandButton image="/images/icon/release.png"
                                   action="#{clientFeedActionPanel.doRelease}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="СБРОС" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doRelease}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>




                <a4j:commandButton image="/images/icon/stop.png"
                                   action="#{clientFeedActionPanel.doBlockAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="БЛОК ВСЕ" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doBlockAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>


                <a4j:commandButton image="/images/icon/play.png"
                                   action="#{clientFeedActionPanel.doPayAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="ОПЛАТА ВСЕ" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doPayAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>


                <a4j:commandButton image="/images/icon/release.png"
                                   action="#{clientFeedActionPanel.doReleaseAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
                <a4j:commandLink value="СБРОС ВСЕ" styleClass="output-text"
                                 action="#{clientFeedActionPanel.doReleaseAllClients}" reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                 oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"/>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>