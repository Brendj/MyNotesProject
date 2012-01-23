<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ClientAuthenticator" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Arrays" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.login_jsp");
    final String HAVE_LOGIN_DATA_PARAM = "login";
    final String CONTRACT_ID_PARAM = "contractId";
    final String PASSWORD_PARAM = "password";
    final int INACTIVE_SESSION_TIMOUT_SECONDS = 1800;
    final String PARAMS_TO_REMOVE[] = {
            "submit", "start-date", "end-date", "order-id", "org-id",
            CONTRACT_ID_PARAM, HAVE_LOGIN_DATA_PARAM, PASSWORD_PARAM};

    URI formActionUri;
    try {
        formActionUri = UriUtils
                .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
        String pageName=request.getParameter("page");
        if (pageName!=null && pageName.equals("logout")) formActionUri = UriUtils
                .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList("page"));
    } catch (Exception e) {
        logger.error("Error during formActionUri building", e);
        throw new ServletException(e);
    }

    boolean haveLoginData = StringUtils.isNotEmpty(request.getParameter(HAVE_LOGIN_DATA_PARAM));
    boolean loginSucceed = false;
    String errorMessage = null;

    if (haveLoginData) {
        Long contractId = null;
        String password = null;
        try {
            String sContractId = request.getParameter(CONTRACT_ID_PARAM);
            sContractId = sContractId.replaceAll("[^0-9]", "");
            contractId = Long.parseLong(sContractId);
            password = request.getParameter(PASSWORD_PARAM);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse client credentials", e);
            }            
            errorMessage = "Неверные данные и/или формат данных";
        }
        if (null != contractId && null != password) {
            RuntimeContext runtimeContext = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                ClientAuthenticator clientAuthenticator = runtimeContext.getClientAuthenticator();                
                loginSucceed = clientAuthenticator.checkClientCredentials(contractId, password);
                if (!loginSucceed) {
                    errorMessage = "Неверный номер контракта и/или пароль";
                } else {
                    ClientAuthToken clientAuthToken = new ClientAuthToken(contractId, false);
                    clientAuthToken.storeTo(session);
                    session.setMaxInactiveInterval(INACTIVE_SESSION_TIMOUT_SECONDS);
                }
            } catch (RuntimeContext.NotInitializedException e) {
                logger.error("Failed to proceed client credentials check", e);
                errorMessage = "Внутренняя ошибка (сервис временно недоступен)";                
            } catch (Exception e) {
                logger.error("Failed to proceed client credentials check", e);
                errorMessage = "Внутренняя ошибка";
            } finally {
                RuntimeContext.release(runtimeContext);
            }
        }
    }

%>
