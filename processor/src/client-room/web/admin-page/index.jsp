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

<%--<f:subview id="loginPage" rendered="#{!loginPage.loginSuccess}" >
&lt;%&ndash;<c:import url="login.jsp" />&ndash;%&gt;
    <table style="width: 100%; height: 100%">
        <tr valign="middle">
            <td align="center">
                <h:panelGrid cellpadding="0" cellspacing="0">
                    <rich:panel header="Необходима авторизация" styleClass="login-panel"
                                headerClass="login-panel-header" bodyClass="login-panel-body">
                        <div align="center">
                           &lt;%&ndash; <a4j:outputPanel id="outPanel">&ndash;%&gt;
                                <h:outputText id="errorText" styleClass="error-output-text" value="Ошибка аутентификации" rendered="#{loginPage.rendered}"/>
                           &lt;%&ndash; </a4j:outputPanel>&ndash;%&gt;
                            <h:form id="loginForm">


                               &lt;%&ndash; <%if (null != request.getParameter("error")) {%>&ndash;%&gt;
                               &lt;%&ndash; <h:outputText styleClass="error-output-text" value="Ошибка аутентификации" />&ndash;%&gt;
                                &lt;%&ndash;<%}%>&ndash;%&gt;


                                <h:panelGrid columns="2">
                                    <h:outputText value="Пользователь" styleClass="output-text" />
                                    <h:inputText value="#{loginPage.userName}" size="16" maxlength="64" styleClass="input-text" />
                                    <h:outputText value="Пароль" styleClass="output-text" />
                                    <h:inputSecret value="#{loginPage.password}" size="16" maxlength="64" styleClass="input-text" />
                                </h:panelGrid>

                                   <a4j:commandButton id="submitBtn" value="Войти"  action="#{loginPage.login}" reRender="adminPage, errorText" onclick="submit()"
                                                 styleClass="command-button" />



                        </h:form>
                        </div>
                    </rich:panel>
                </h:panelGrid>
            </td>
        </tr>
    </table>

</f:subview>--%>

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
            <%--        <h:form id="buttonForm">
                        <a4j:commandButton value="Добавить город" action="#{cityPage.addCity}"
                                           reRender="cityTable"
                                           styleClass="command-button"/>
                    </h:form>

                    <h:form id="saveForm" >

                        <a4j:commandButton value="Сохранить" action="#{cityPage.save}"
                                           reRender="cityTable"
                                           styleClass="command-button"/>


                        <rich:dataTable id="cityTable" width="700" var="cityItem" value="#{cityPage.cityItems}"
                                        rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">

                            <rich:column  headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Название"> </h:outputText>
                                </f:facet>
                                <h:inputText value="#{cityItem.name}" />

                            </rich:column>

                            <rich:column  headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="URL сервиса">   </h:outputText>
                                </f:facet>
                                <h:inputText value="#{cityItem.serviceUrl}"/>
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Маска лицевого счета">  </h:outputText>
                                </f:facet>
                                <h:inputText value="#{cityItem.contractIdMask}"/>
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Активность">   </h:outputText>
                                </f:facet>
                                <h:selectBooleanCheckbox value="#{cityItem.activity}" styleClass="output-text" />
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Имя пользователя">  </h:outputText>
                                </f:facet>
                                <h:inputText value="#{cityItem.userName}"/>
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Пароль"> </h:outputText>
                                </f:facet>
                                <h:inputText value="#{cityItem.password}"/>
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Тип авторизации "> </h:outputText>
                                </f:facet>

                                <h:selectOneMenu value="#{cityItem.indexOfAuthType}"
                                                 styleClass="input-text">
                                    <f:selectItems value="#{cityPage.authTypeItems}" />
                                </h:selectOneMenu>
                            </rich:column>

                            <rich:column headerClass="column-header">
                                <f:facet name="header">
                                    <h:outputText value="Удалить">  </h:outputText>
                                </f:facet>

                                <a4j:commandButton value="Удалить" action="#{cityItem.delete}"
                                                   reRender="cityTable"
                                                   styleClass="command-button"/>
                            </rich:column>

                        </rich:dataTable>

                    </h:form>--%>
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