<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.persistence.City" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.Result" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.util.Arrays" %>

<%
     final Long RC_CLIENT_NOT_FOUND = 110L;
     final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
     final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
     final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
     final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
     final Long RC_INVALID_DATA = 150L;
     final Long RC_NO_CONTACT_DATA = 160L;
     final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
     final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;

     final String RC_OK_DESC="OK";
     final String RC_CLIENT_NOT_FOUND_DESC="Клиент не найден";
     final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="По условиям найден более одного клиента";
     final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="Ошибка авторизации клиента";
     final String RC_INTERNAL_ERROR_DESC="Внутренняя ошибка";
     final String RC_NO_CONTACT_DATA_DESC="У лицевого счета нет контактных данных";


    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.client-room.web.client-room.pages.login_jsp");
    final String HAVE_LOGIN_DATA_PARAM = "login";
    final String CONTRACT_ID_PARAM = "contractId";
    final String PASSWORD_PARAM = "password";
    final String CITY_PARAM="cityName";
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
        String  cityName=null;
        Long contractId = null;
        String password = null;
        try {
            logger.info("city: "+request.getParameter(CITY_PARAM));
            cityName=request.getParameter(CITY_PARAM) ;
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
        if (null != contractId && null != password&& null!=cityName) {

            try {

                City city= DAOClientRoomService.getInstance().getCityByName(cityName);

                ClientRoomControllerWSService service = new ClientRoomControllerWSService();
                ClientRoomController port
                        = service.getClientRoomControllerWSPort();

                if(port==null){

                    throw new Exception("web service is not available");
                }

                ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, city.getServiceUrl());

               // Map context = ((BindingProvider) port).getRequestContext();


                logger.info("password: "+password);

                String plainPassword=password;
                final byte[] plainPasswordBytes = plainPassword.getBytes(CharEncoding.UTF_8);
                final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                String sha1HashString = new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);

                Result ra = port.authorizeClient(contractId, sha1HashString);

               // logger.info("resultCode: "+ra.getResultCode().toString());

                loginSucceed = ra.getResultCode().equals(RC_OK);

                if(ra.getResultCode().equals(RC_INTERNAL_ERROR))
                {
                    throw new Exception(RC_INTERNAL_ERROR_DESC);
                }

                if (!loginSucceed) {
                    errorMessage = "Неверный номер договора и/или пароль";
                } else {
                    HttpServletResponse indexResponse=(HttpServletResponse) application.getAttribute("indexResponse");
                    //logger.info("from login_check: getIdOfCity="+city.getIdOfCity().toString());

                    Cookie ck=new Cookie("cityId",city.getIdOfCity().toString());

                    ck.setMaxAge(60*60*24*183);

                    indexResponse.addCookie(ck);

                    ClientAuthToken clientAuthToken = new ClientAuthToken(port,contractId, false);

                    clientAuthToken.storeTo(session);

                    session.setMaxInactiveInterval(INACTIVE_SESSION_TIMOUT_SECONDS);
                }
            /*} catch (RuntimeContext.NotInitializedException e) {
                logger.error("Failed to proceed client credentials check", e);
                errorMessage = "Внутренняя ошибка (сервис временно недоступен)"; */
            } catch (Exception e) {
                logger.error("Failed to proceed client credentials check", e);
                errorMessage = "Внутренняя ошибка";
            }
        }
    }

%>
