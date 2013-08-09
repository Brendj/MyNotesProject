<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%
    if (request.getHeader("referer")!=null) {
        response.sendRedirect(request.getHeader("referer"));
    }
%>