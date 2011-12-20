<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>

<%ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);%>

<html>
<head>
    <title>ECafe: Личный кабинет клиента<%=null == clientAuthToken ? ""
            : StringEscapeUtils.escapeHtml(String.format(" (договор %s)", ContractIdFormat.format(clientAuthToken.getContractId())))%>
    </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon"
          href="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "images/ecafe-favicon.png"))%>"
          type="image/x-icon">
    <link rel="shortcut icon"
          href="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "images/ecafe-favicon.png"))%>"
          type="image/x-icon">    
</head>
<body>
<jsp:include page="inlinecabinet.jsp"/>   
</body>
</html>