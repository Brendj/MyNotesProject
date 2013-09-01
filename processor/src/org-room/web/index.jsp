<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<f:view>
<head>
    <title><h:outputText value="Новая школа: Юзер"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/icon/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/icon/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/styles.css"/>" type="text/css">
</head>


<a4j:loadScript src="/scripts/jquery.maskedinput.min.js"/>

<body style="margin: 4px; padding: 0;">

    <f:subview id="groupCreateSubView">
        <c:import url="include/modal/group/create.jsp" />
    </f:subview>
    <f:subview id="yesNoConfirmSubView">
        <c:import url="include/modal/yesnoconfirm.jsp" />
    </f:subview>
    <f:subview id="clientFeedActionView">
        <c:import url="include/modal/feed_plan/client_action.jsp" />
    </f:subview>

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
            <%-- Заголовок страницы --%>
        <tr>
            <td>
                <a4j:form id="headerForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <rich:panel styleClass="header-panel" bodyClass="header-panel-body">
                        <h:panelGroup style="text-align: left;">
                            <h:graphicImage value="/images/icon/ecafe-favicon.png"
                                            style="border: 0; margin: 0 8px 0 0; vertical-align: middle; " />
                            <h:outputText escape="true" id="headerText" value="Управление школой: ГОУ СОШ № 1547"
                                          styleClass="page-header-text" />
                        </h:panelGroup>
                    </rich:panel>
                </a4j:form>
            </td>
        </tr>
            <%-- Верхнее (основное) меню --%>
        <tr>
            <td style="min-width: 210px; vertical-align: top;" width="215px">
                    <%-- Главное меню --%>
                    <f:subview id="mainMenuSubView">
                        <c:import url="/include/main_menu.jsp" />
                    </f:subview>
            </td>
        </tr>
            <%-- Рабочая область --%>
        <tr>
            <td style="vertical-align: top;" width="*">
                    <f:subview id="workspaceSubView">
                        <c:import url="/include/workspace.jsp" />
                    </f:subview>
            </td>
        </tr>
            <%-- Нижний колонтитул --%>
        <tr>
            <td colspan="2">
                <h:panelGrid width="100%" cellspacing="4px" cellpadding="0" styleClass="borderless-grid"
                             columnClasses="right-aligned-column">
                </h:panelGrid>
            </td>
        </tr>
    </table>
</body>
</html>
</f:view>