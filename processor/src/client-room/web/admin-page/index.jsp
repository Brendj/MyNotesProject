<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: timur
  Date: 20.08.12
  Time: 11:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%--@elvariable id="cityPage" type="ru.axetta.ecafe.processor.web.ui.admin.CityPage"--%>
<html>
<head>
    <title>Новая школа: администрирование</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/client-room/styles.css"/>" type="text/css">
</head>
<%--@elvariable id="loginPage" type="ru.axetta.ecafe.processor.web.ui.admin.LoginPage"--%>
<body style="margin: 4px; padding: 0;">

<f:view>



<f:subview id="adminPage" >

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td colspan="2">
                    <%-- Заголовок страницы --%>
                <a4j:form id="headerForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <rich:panel styleClass="header-panel" bodyClass="header-panel-body">
                        <h:panelGroup style="text-align: right; float: right;">
                            <%--<h:outputText escape="true" value="Версия #{runtimeContext.currentDBSchemaVersion}"
                                          styleClass="output-text" /><br />--%>
                            <%--<h:commandLink value="Мои настройки" binding="#{userSettings.mainMenuComponent}"
                                           action="#{userSettings.show}" styleClass="command-link"/>--%>
                            &nbsp;&nbsp;&nbsp;
                            <h:outputText escape="true" value="#{request.remoteUser} - " styleClass="output-text" />
                            <h:commandLink value="Выход" action="#{mainPage.logout}" styleClass="command-link" />
                        </h:panelGroup>
                        <h:panelGroup style="text-align: left;">
                            <h:graphicImage value="/images/ecafe-favicon.png"
                                            style="border: 0; margin: 0 8px 0 0; vertical-align: middle; " />
                            <h:outputText escape="true" id="headerText" value="Новая школа: администрирование"
                                          styleClass="page-header-text" />
                        </h:panelGroup>
                    </rich:panel>
                </a4j:form>
            </td>
        </tr>
            <%-- Центральная область --%>
        <tr>
            <td style="min-width: 210px; vertical-align: top;" width="215px">
                    <%-- Главное меню --%>
                <f:subview id="mainMenuSubView">
                    <c:import url="/admin-page/include/main_menu.jsp" />
                </f:subview>
            </td>
            <td style="vertical-align: top;" width="*">
                    <%-- Рабочая область --%>
                <f:subview id="workspaceSubView">

                    <c:import url="/admin-page/include/workspace.jsp" />

                </f:subview>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                    <%-- Нижний колонтитул --%>
                <h:panelGrid width="100%" cellspacing="4px" cellpadding="0" styleClass="borderless-grid"
                             columnClasses="right-aligned-column">
                </h:panelGrid> <%-- Нижний колонтитул --%>
            </td>
        </tr>
    </table>

    </f:subview >

 </f:view>

</body>
</html>