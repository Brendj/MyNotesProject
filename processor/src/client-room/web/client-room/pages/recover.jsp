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
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.City" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOService" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.recover_jsp");
    try{

   Long  cityId=Long.parseLong(request.getParameter("cityId"));
    logger.info("from recover: cityId="+cityId);


    City city= DAOService.getInstance().getCity(cityId).get(0);

    ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService service = new ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService();
    ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port
            = service.getClientRoomControllerWSPort();
    ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, city.getServiceUrl());

    RequestWebParam requestWebParam=new RequestWebParam();
    requestWebParam.setUrl(request.getRequestURL().toString());
    requestWebParam.setPParam(request.getParameter("p"));
    requestWebParam.setContractIdParam(request.getParameter("contractId"));
    requestWebParam.setDateParam(request.getParameter("date"));

    CheckPasswordResult cpr=port.checkPasswordRestoreRequest(requestWebParam);
    boolean succeeded= cpr.isSucceeded();

    String mainPage = ServletUtils.getHostRelativeResourceUri(request, "client-room/index.jsp");
    if (succeeded) {
       // final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.recover_jsp");
        final String CONTRACT_ID_PARAM = "contractId";
        final String HAVE_PASSWORD_DATA_PARAM = "recover";
        final String NEW_PASSWORD_PARAM = "new-password";
        final String NEW_PASSWORD_CONFIRM_PARAM = "new-password-confirm";

        final int INACTIVE_SESSION_TIMOUT_SECONDS = 1800;

        Boolean havePasswordData = StringUtils.isNotEmpty(request.getParameter(HAVE_PASSWORD_DATA_PARAM));
        boolean dataToProcessVerified = true;
        String errorMessage = null;
        URI formActionUri;
        Long contractId;
        String newPassword = null;
        String newPasswordConfirmation = null;


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

        boolean bPasswordChanged = false;
        if (havePasswordData && dataToProcessVerified) {
            contractId = Long.valueOf(request.getParameter(CONTRACT_ID_PARAM));

            String base64passwordHash  =new String(Base64.encodeBase64(newPassword.getBytes()), CharEncoding.US_ASCII) ;
            ru.axetta.ecafe.processor.web.bo.client.Result r= port.changePassword(contractId, base64passwordHash);


            if (r.getResultCode().equals(0L)) {
                logger.info("Change password");
                ClientAuthToken clientAuthToken = new ClientAuthToken(port,contractId, false);
                clientAuthToken.storeTo(session);
                session.setMaxInactiveInterval(INACTIVE_SESSION_TIMOUT_SECONDS);
                bPasswordChanged = true;
                //response.sendRedirect(mainPage);
            } else {
                errorMessage="Внутренняя ошибка";
                logger.info("Error");
            }
        }

        if (bPasswordChanged) {%>
            <table class="borderless-form-login">
                <tr><td style="color:green">Пароль был успешно изменен.</td></tr>
            </table>
        <%} else {%>
<form method="post" enctype="application/x-www-form-urlencoded"
      action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formActionUri.toString()))%>"
      class="borderless-form-login">
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
                                <div class="output-text">Номер договора</div>
                            </td>
                            <td>
                                <div class="output-text"><%=request.getParameter(CONTRACT_ID_PARAM)%>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Введите новый пароль</div>
                            </td>
                            <td>
                                <input type="password" name="<%=NEW_PASSWORD_PARAM%>" size="16" maxlength="64"
                                       class="input-text" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Введите подтверждение</div>
                            </td>
                            <td>
                                <input type="password" name="<%=NEW_PASSWORD_CONFIRM_PARAM%>" size="16" maxlength="64"
                                       class="input-text" />
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
        <tr valign="middle" class="login-form-button-tr">
            <td align="center">
                <input type="submit" name="<%=HAVE_PASSWORD_DATA_PARAM%>" value="Сохранить" class="command-button" />
                &nbsp;&nbsp;&nbsp;
                <a class="command-link" href="<%=UriUtils.getURIWithNoParams(new URI(request.getRequestURL().toString()))%>">
                    <%=StringEscapeUtils.escapeHtml("Перейти на главную")%>
                </a>
            </td>
        </tr>
    </table>
</form>

<% }
} else {%>
<table class="borderless-form-login">
    <tr>
        <td>Ссылка недействительна.</td>
    </tr>
</table>
<%}}catch(Exception e){logger.error("error",e);}%>