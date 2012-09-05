<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%--<%@ page import="ru.axetta.ecafe.processor.core.client.ClientAuthenticator" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%--<%@ page import="File" %>--%>
<%--<%@ page import="java.util.LinkedList" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%--<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils" %>--%>
<%@ page import="java.util.Date" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ClientPasswordRecover" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.RequestWebParam" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.SendResult" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.City" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOService" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.password_jsp");

    final String CONTRACT_ID_PARAM = "contractId";
    final String HAVE_RECOVER_DATA_PARAM = "password";
    final String DATE_PARAM = "date";
    final String PASS_PARAM = "p";
    final String CITY_PARAM="cityName" ;
    String mainPage = ServletUtils.getHostRelativeResourceUri(request, "client-room/index.jsp");
    List<City>cities= DAOService.getInstance().getTowns(true);
    Boolean haveRecoverPasswordData = StringUtils.isNotEmpty(request.getParameter(HAVE_RECOVER_DATA_PARAM));
    Boolean sendEmailSucceed = false;
    String errorMessage = null, infoMessage = null;
    URI formActionUri;
    Long contractId;
    Long date;
    String ecp;

    try {
        formActionUri = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception e) {
        logger.error("Error during formActionUri building", e);
        throw new ServletException(e);
    }

    if(haveRecoverPasswordData){
         try{

             String cityName=request.getParameter(CITY_PARAM);
             City city= DAOService.getInstance().getCityByName(cityName);

             String sContractId = request.getParameter(CONTRACT_ID_PARAM);
             sContractId = sContractId.replaceAll("[^0-9]", "");
             if(sContractId.equalsIgnoreCase("")){
                 errorMessage = "Неверный номер договора.";
             } else {
                 contractId = Long.parseLong(sContractId);


                logger.info("From password: city="+cityName);
                ClientRoomControllerWSService service = new ClientRoomControllerWSService();
                 ClientRoomController port
                         = service.getClientRoomControllerWSPort();
                 ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, city.getServiceUrl());

                 //ClientAuthToken clientAuthToken=new ClientAuthToken(port,null,false);
                 //clientAuthToken.storeTo(session);
                 //ClientRoomController port=clientAuthToken.getPort();

                   RequestWebParam requestWebParam  =new RequestWebParam();
                   URI passwordRecoverURI=UriUtils.getURIWithNoParams(new URI(request.getRequestURL().toString()));

                  logger.info("from password recoverURI: "+passwordRecoverURI.toString());

                 // UriUtils.putParam(passwordRecoverURI,CITY_PARAM,cityName);

                // logger.info("from password recoverURI after putting: "+passwordRecoverURI.toString());



                 requestWebParam.setUrl(passwordRecoverURI.toString()+"?"+CITY_PARAM+"="+cityName);

                  SendResult sr=  port.sendPasswordRecoverURLFromEmail(contractId,requestWebParam);

                 //ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
                 int succeeded = //clientPasswordRecover.sendPasswordRecoverURLFromEmail(contractId,request);
                   sr.getRecoverStatus();
                 if (succeeded == ClientPasswordRecover.CONTRACT_SEND_RECOVER_PASSWORD){
                     sendEmailSucceed = true;
                     infoMessage = "Для восстановления пароля перейдите по ссылке, отправленной на Ваш адрес электронной почты.";
                 } else {
                     sendEmailSucceed = false;
                     switch (succeeded){
                         case ClientPasswordRecover.NOT_FOUND_CONTRACT_BY_ID:
                             errorMessage = "Договор с номером "+String.valueOf(contractId)+" не зарегистрирован."; break;
                         case ClientPasswordRecover.CONTRACT_HAS_NOT_EMAIL:
                             errorMessage = "Для данного договора не указан контактный адрес e-mail. Для восстановления пароля обратитесь в службу поддержки."; break;
                         default:
                             errorMessage ="";
                     }
                 }
             }
         } catch (Exception e) {
             logger.error("Failed to parse client password recover", e);
             response.sendRedirect(mainPage);
             return;
         }
    }
    if (infoMessage!=null) {%>
    <table class="borderless-form-login" align="center">
        <tr valign="middle" class="login-form-input-tr">
            <td align="center">
                <div class="output-text"><%=infoMessage%></div>
            </td>
        </tr>
    </table>
<%  } else {
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
                                <div class="output-text">Город</div>
                            </td>
                            <td>

                                <select style="width: 8em" name="<%=CITY_PARAM%>">
                                    <% for(City city:cities){%>
                                    <option name="<%=city.getIdOfCity()%>"><%=StringEscapeUtils.escapeHtml(city.getName())%></option>
                                    <%}%>
                                </select>
                            </td>
                        </tr>




                        <tr>


                            <td>
                                <div class="output-text">Номер договора</div>
                            </td>
                            <td>
                                <input type="text" name="<%=CONTRACT_ID_PARAM%>" size="16" maxlength="64"
                                       class="input-text" />
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
        <tr valign="middle" class="login-form-button-tr">
            <td align="center">
                <input type="submit" name="<%=HAVE_RECOVER_DATA_PARAM%>" value="Восстановить" class="command-button" />
                &nbsp;&nbsp;&nbsp;
                <a class="command-link"
                   href="javascript:history.back();">
                    <%=StringEscapeUtils.escapeHtml("Назад")%>
                </a>
            </td>
        </tr>
    </table>
</form>
<%
    }
%>