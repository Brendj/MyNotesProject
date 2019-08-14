/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by nuc on 12.08.2019.
 */
@Component
@Scope("singleton")
public class AtolService {
    private static final Logger logger = LoggerFactory.getLogger(AtolService.class);
    private static final String ATOL_ADDRESS_PROPERTY = "ecafe.processing.atol.address";
    private static final String ATOL_USER_PROPERTY = "ecafe.processing.atol.user";
    private static final String ATOL_PASSWORD_PROPERTY = "ecafe.processing.atol.password";
    //private static final String DEFAULT_ADDRESS = "https://testonline.atol.ru/possystem/v4/";
    //private static final String DEFAULT_USER = "v4-online-atol-ru";
    //private static final String DEFAULT_PASSWORD = "iGFFuihss";

    private AtolToken token;

    public String getToken() throws Exception {
        if (token == null || !CalendarUtils.betweenDate(token.getDate(), CalendarUtils.startOfDay(new Date()), CalendarUtils.endOfDay(new Date()))) {
            token = getNewTokenFromAtol();
        }
        return token.getToken();
    }

    private AtolToken getNewTokenFromAtol() throws Exception {
        String parameters = String.format("login=%s&pass=%s", getUser(), getPassword());
        URL url = new URL(getServiceAddress() + "getToken?" + parameters);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setQueryString(parameters);
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                    new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                ObjectMapper objectMapper = new ObjectMapper();
                AtolTokenResponse atolResponse = objectMapper.readValue(inputStream, AtolTokenResponse.class);
                if (atolResponse.getError() != null) {
                    throw new Exception("Error in get Atol token: " + atolResponse.getError().toString());
                }
                return new AtolToken(atolResponse.getToken());
            } else {
                logger.error("Atol token request has status {}", statusCode);
            }
        } catch (Exception ex) {
            logger.error("Error in Atol send request: ", ex);
        } finally {
            httpMethod.releaseConnection();
        }
        return null;
    }

    /*public AtolToken getNewToken() throws Exception {
        String qqq = "https://212.11.151.174:28881/processor/payment-sbmsk?ACTION=check&ACCOUNT=39200019";
        URL url = new URL(qqq);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setQueryString("ACTION=check&ACCOUNT=39200019");
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                    new Protocol("https", new EasySSLProtocolSocketFactory(), url.getPort()));
            int statusCode = httpClient.executeMethod(httpMethod);
            InputStream inputStream = httpMethod.getResponseBodyAsStream();
            logger.info(IOUtils.toString(inputStream, "windows-1251"));
        } catch (Exception ex) {
            logger.error("Error in mfr send request: ", ex);
        } finally {
            httpMethod.releaseConnection();
        }
        return null;
    }*/

    private String getServiceAddress() throws Exception{
        String address =  RuntimeContext.getInstance().getConfigProperties().getProperty(ATOL_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("Atol address not specified");
        return address;
    }

    private String getUser() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(ATOL_USER_PROPERTY, "");
    }

    private String getPassword() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(ATOL_PASSWORD_PROPERTY, "");
    }

}
