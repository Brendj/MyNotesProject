<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--<%@ page import="RuntimeContext" %>--%>
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
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="java.util.Map" %>
<%--<%@ page import="Client" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.Result" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.City" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOService" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>

<%
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
            //RuntimeContext runtimeContext = null;
            try {

               /* Cookie ck=new Cookie("cityName",cityName);
                ck.setMaxAge(60*60*24*183);
                response.addCookie(ck);*/



                City city= DAOService.getInstance().getCityByName(cityName);


                ClientRoomControllerWSService service = new ClientRoomControllerWSService();
                ClientRoomController port
                        = service.getClientRoomControllerWSPort();
                ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, city.getServiceUrl());

               // Map context = ((BindingProvider) port).getRequestContext();


                logger.info("password: "+password);

                String plainPassword=password;
                final byte[] plainPasswordBytes = plainPassword.getBytes(CharEncoding.UTF_8);
                final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                String sha1HashString = new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);

                Result ra = port.authorizeClient(contractId, sha1HashString);
                //System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());


                 logger.info("resultCode: "+ra.getResultCode().toString());

               // runtimeContext = RuntimeContext.getInstance();
               // ClientAuthenticator clientAuthenticator = runtimeContext.getClientAuthenticator();
                loginSucceed = ra.getResultCode().equals(new Long(0));

                if (!loginSucceed) {
                    errorMessage = "Неверный номер договора и/или пароль";
                } else {
                    HttpServletResponse indexResponse=(HttpServletResponse) application.getAttribute("indexResponse");
                    logger.info("from login_check: getIdOfCity="+city.getIdOfCity().toString());
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
