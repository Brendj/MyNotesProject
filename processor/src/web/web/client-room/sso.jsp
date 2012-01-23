<%@ page contentType="text/html;charset=UTF-8" language="java" %><%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%><%@
    page import="org.slf4j.Logger" %><%@
    page import="org.slf4j.LoggerFactory" %><%@
    page import="ru.axetta.ecafe.processor.core.client.ClientAuthenticator" %><%@
    page import="ru.axetta.ecafe.processor.core.RuntimeContext" %><%@
    page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %><%@
    page import="ru.axetta.ecafe.processor.web.ServletUtils" %><%@
    page import="java.net.URI" %><%@
    page import="java.net.URLEncoder" %><%
    
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.sso_jsp");
    final int INACTIVE_SESSION_TIMEOUT_SECONDS = 1800;
    final String CONTRACT_ID_PARAM = "contractId";
    final String PASSWORD_PARAM = "password";

    Long contractId;
    String password;
    try {
        contractId = Long.parseLong(request.getParameter(CONTRACT_ID_PARAM));
        password = request.getParameter(PASSWORD_PARAM);
    } catch (Exception e) {
        logger.error("Failed to parse client credentials", e);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        ClientAuthenticator clientAuthenticator = runtimeContext.getClientAuthenticator();
        boolean loginSucceed = clientAuthenticator.checkSSOCredentials(contractId, password);
        if (loginSucceed) {
            ClientAuthToken clientAuthToken = new ClientAuthToken(contractId, true);
            clientAuthToken.storeTo(session);
            session.setMaxInactiveInterval(INACTIVE_SESSION_TIMEOUT_SECONDS);
            URI uri = new URI(
                    ServletUtils.getHostRelativeResourceUri(request, "client-room/no-styles-inlinecabinet.jsp"));
            out.write(uri.toString());
            out.write(";jsessionid=");
            out.write(URLEncoder.encode(session.getId(), "UTF-8"));
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    } catch (Exception e) {
        logger.error("Failed to proceed SSO credentials check", e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);        
    } finally {
        RuntimeContext.release(runtimeContext);
    }
%>