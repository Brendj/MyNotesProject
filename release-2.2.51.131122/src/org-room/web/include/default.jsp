<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="defaultWorkspacePage" type="ru.axetta.ecafe.processor.web.ui.DefaultWorkspacePage"--%>
<%--@elvariable id="loginBean" type="ru.axetta.ecafe.processor.web.ui.auth.LoginBean"--%>
<a4j:form>
    <a4j:region>
        <h:outputText value="Добро пожаловать в Панель управление школой #{loginBean.org.officialName}" styleClass="output-text"/>
    </a4j:region>
</a4j:form>