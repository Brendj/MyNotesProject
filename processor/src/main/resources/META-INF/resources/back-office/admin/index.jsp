<%--
  Created by IntelliJ IDEA.
  User: Akhmetov
  Date: 26.04.16
  Time: 18:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    if (StringUtils.isNotEmpty(request.getRemoteUser())) {
        String mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/index.faces");
        response.sendRedirect(mainPage);
        return;
    }
%>

<html>
<head>
    <title></title>
</head>
<body>

</body>
</html>