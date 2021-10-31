<%@ page language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%if (StringUtils.isNotEmpty(request.getRemoteUser())) {
    String mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/index.faces");
    response.sendRedirect(mainPage);
    return;
}%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<html>
<head>
    <title>Новая школа: Авторизация</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
    <link rel="stylesheet" href="<c:url value="/resources/css/login.css"/>" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="../resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="../resources/css/weather-icons.min.css">
</head>
<body style="background: url('../../../../../web/web/images/1.png')">

<f:view>

    <section data-ng-view="" id="content" class="animate-fade-up ng-scope">

        <form id="loginForm" method="post" enctype="application/x-www-form-urlencoded" action="j_security_check"
              class="borderless-form">
            <div class="page page-lock ng-scope">

                <div class="lock-centered clearfix">
                    <div class="lock-container">
                        <!-- <div ui-time class="lock-time"></div> -->

                        <section class="lock-box">
                            <div class="lock-user ng-binding">
                                <div  class="form-group">
                                    <input type="text" name="j_username" placeholder="username" class="form-control" style="border:0px;height: 50px;" />
                                </div>
                            </div>
                            <div class="lock-img">
                                <button type="submit" value="fdsaf"  style="border: 0; background: transparent" class="btn-submit">
                                    <img src="../images/31.png" alt="">
                                </button>
                            </div>
                            <div class="lock-pwd">
                                <form class="ng-pristine ng-valid" _lpchecked="1">
                                    <div class="form-group">
                                        <input type="password" name="j_password"  placeholder="Password" class="form-control" autocomplete="off"
                                               style="cursor: auto; background-image: url('../../../../../web/web/images/2.png'); background-attachment: scroll; background-position: 100% 50%; background-repeat: no-repeat;">
                                    </div>
                                </form>
                            </div>

                            <%if (null != request.getParameter("error")) {%>
                            <h:outputText styleClass="error-output-text"
                                          value="#{not empty requestScope['errorMessage'] ? requestScope['errorMessage'] : 'Ошибка аутентификации'}" />
                            <%}%>
                        </section>
                    </div>
                </div>
            </div>
        </form>

    </section>
</f:view>
</body>
</html>
