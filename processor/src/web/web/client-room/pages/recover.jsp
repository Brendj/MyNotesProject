<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ClientPasswordRecover" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.recover_jsp");
    final String CONTRACT_ID_PARAM = "contractId";
    final String HAVE_PASSWORD_DATA_PARAM = "recover";
    final String NEW_PASSWORD_PARAM = "new-password";
    final String NEW_PASSWORD_CONFIRM_PARAM = "new-password-confirm";
    final int INACTIVE_SESSION_TIMOUT_SECONDS = 1800;
    final String DATE_PARAM = "date";
    final String ECP_PARAM = "ecp";

    Boolean havePasswordData = StringUtils.isNotEmpty(request.getParameter(HAVE_PASSWORD_DATA_PARAM));
    boolean dataToProcessVerified = true;
    String errorMessage = null;
    URI formActionUri;
    Long contractId;
    Long date;
    String ecp;
    String newPassword = null;
    String newPasswordConfirmation = null;

    ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();

    try {
        formActionUri = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception e) {
        logger.error("Error during formActionUri building", e);
        throw new ServletException(e);
    }

    if (havePasswordData) {
        try {
            newPassword = request.getParameter(NEW_PASSWORD_PARAM);
            newPasswordConfirmation = request.getParameter(NEW_PASSWORD_CONFIRM_PARAM);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse client credentials", e);
            }
            errorMessage = "Неверные данные и/или формат данных";
            dataToProcessVerified = false;
        }

        if (StringUtils.isEmpty(newPassword)) {
            dataToProcessVerified = false;
            errorMessage = "Недопустимое значение для нового пароля";
        }
        if (dataToProcessVerified && !StringUtils.equals(newPassword, newPasswordConfirmation)) {
            dataToProcessVerified = false;
            errorMessage = "Новый пароль и подтверждение не совпадают";
        }
    }

    if (havePasswordData && dataToProcessVerified) {
        contractId = Long.valueOf(request.getParameter(CONTRACT_ID_PARAM));
        if(clientPasswordRecover.changePassword(contractId, newPassword)){
            logger.info("Change password");
            ClientAuthToken clientAuthToken = new ClientAuthToken(contractId, false);
            clientAuthToken.storeTo(session);
            session.setMaxInactiveInterval(INACTIVE_SESSION_TIMOUT_SECONDS);

            %>
                <meta http-equiv="refresh" content="0; url=https://localhost:8443/processor/client-room/index.jsp">
            <%
        } else {
            logger.info("Error");
        }
    }


    if(clientPasswordRecover.checkContractURI(request)){
%>
<style>
    .login-page { display:block !important; }
</style>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formActionUri.toString()))%>" class="borderless-form-login">
    <table id="login-form" align="center">
        <tr valign="middle" class="login-form-input-tr">
            <td align="center">
                <div align="center" class="login-panel-body">
                    <%if (null != errorMessage) {%>
                    <div class="error-output-text"><%=StringEscapeUtils.escapeHtml(errorMessage)%>
                    </div>
                    <%}%>
                    <table class="login-data-table">
                        <tr>
                            <td>
                                <div class="output-text">Ваш Номер договора</div>
                            </td>
                            <td>
                                <div class="output-text"><%=request.getParameter(CONTRACT_ID_PARAM)%></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Введите новый пароль</div>
                            </td>
                            <td>
                                <input type="password" name="<%=NEW_PASSWORD_PARAM%>" size="16" maxlength="64" class="input-text" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Введите подтверждение</div>
                            </td>
                            <td>
                                <input type="password" name="<%=NEW_PASSWORD_CONFIRM_PARAM%>" size="16" maxlength="64" class="input-text" />
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
        <tr valign="middle" class="login-form-button-tr">
            <td align="center">
                <input type="submit" name="<%=HAVE_PASSWORD_DATA_PARAM%>" value="Восстановить" class="command-button" />
                &nbsp;&nbsp;&nbsp;
                <a class="command-link"
                   href="https://localhost:8443/processor/client-room/index.jsp">
                    <%=StringEscapeUtils.escapeHtml("Перейти на главную")%>
                </a>
            </td>
        </tr>
    </table>
</form>
<%
     } else{
     %>
<meta http-equiv="refresh" content="0; url=https://localhost:8443/processor/client-room/index.jsp">
    <%
     }
 %>