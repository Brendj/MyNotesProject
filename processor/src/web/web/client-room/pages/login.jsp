<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

    <%@include file="login_check.jsp"%>

<%
    if (!(haveLoginData && loginSucceed)) {
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
                                <div class="output-text">Номер договора</div>
                            </td>
                            <td>
                                <input type="text" name="<%=CONTRACT_ID_PARAM%>" size="16" maxlength="64"
                                       class="input-text" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Пароль</div>
                            </td>
                            <td>
                                <input type="password" name="<%=PASSWORD_PARAM%>" size="16" maxlength="64"
                                       class="input-text" />
                            </td>
                        </tr>
                    </table>
                </div>
        </td>
    </tr>
    <tr valign="middle" class="login-form-button-tr">
        <td align="center">
            <input type="submit" name="<%=HAVE_LOGIN_DATA_PARAM%>" value="Войти" class="command-button" />
        </td>
    </tr>
    <tr style="margin-top:4px">
        <td align="right">
            <a class="command-link"
               href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(UriUtils.putParam(formActionUri, "page", "password").toString()))%>">
                <%=StringEscapeUtils.escapeHtml("Забыли пароль?")%>
            </a>
        </td>
    </tr>
</table>
</form>
<%
    }
%>