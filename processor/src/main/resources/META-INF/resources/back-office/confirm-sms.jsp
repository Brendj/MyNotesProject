<%@ page language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<script type="text/javascript">
function sendFocus() {
    document.getElementById('confirmSmsForm:enterSmsCodeButton').focus();
}
function sendEvent() {
    document.getElementById('confirmSmsForm:enterSmsCodeButton').click();
}
</script>
<html>
<head>
    <title>Новая школа: Авторизация</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
</head>
<body>
<f:view>
    <table style="width: 100%; height: 100%">
        <tr valign="middle">
            <td align="center">
                <h:panelGrid cellpadding="0" cellspacing="0">
                    <a4j:form id="confirmSmsForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <rich:panel header="Необходимо подтверждение" styleClass="login-panel"
                                headerClass="login-panel-header" bodyClass="login-panel-body">
                        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                                       warnClass="warn-messages" />
                        <h:panelGrid cellpadding="0" cellspacing="0" styleClass="borderless-grid">
                            <div align="center">
                                <h:panelGrid columns="2">
                                    <h:outputText value="Введите код активации, полученный в СМС-сообщении" styleClass="output-text" />
                                    <h:inputText id="smscode-security" size="16" maxlength="64" styleClass="input-text" value="#{mainPage.smsCode}" onblur="sendFocus()"
                                    onkeydown="if (event.keyCode === 13) { sendEvent(); return false;}"/>
                                </h:panelGrid>
                                <h:panelGrid columns="2">
                                    <a4j:commandButton type="submit" value="Отправить sms повторно" action="#{mainPage.sendSMSagain()}" disabled="#{mainPage.canSendAgain}"/>
                                    <a4j:commandButton id="enterSmsCodeButton" type="submit" value="Войти" action="#{mainPage.checkUserSmsCode}" reRender="smscode-security" />
                                </h:panelGrid>
                                <a4j:commandLink id="eer-iiop" value="Назад" action="#{mainPage.logout}" styleClass="command-link" oncomplete="window.location.href='/processor/back-office/index.faces'"/>
                            </div>
                        </h:panelGrid>
                    </rich:panel>
                    </a4j:form>
                </h:panelGrid>
            </td>
        </tr>
    </table>
</f:view>
</body>
</html>