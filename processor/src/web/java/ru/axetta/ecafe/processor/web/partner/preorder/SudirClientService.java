/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SudirToken;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.ESIAPersonData;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.SudirPersonData;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by i.semenov on 01.03.2018.
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class SudirClientService {
    private static final Logger logger = LoggerFactory.getLogger(SudirClientService.class);

    public String CLIENT_ID;
    private String CLIENT_SECRET;
    public String REDIRECT_URI;
    public String SUDIR_AUTHORIZE_ADDRESS;
    public String SUDIR_LOGOUT_ADDRESS;
    public String REDIRECT_LOGOUT_URI;
    public String SUDIR_TOKEN_ADDRESS; // = "https://login-test.mos.ru/sps/oauth/oauth20/token";
    public String SUDIR_DATA_ADDRESS;
    public boolean SECURITY_ON;

    @PostConstruct
    private void initialise() {
        CLIENT_ID = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.client_id", "kkp3ZpdBoc9FOy4HWDC9");
        CLIENT_SECRET = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.client_secret", "XWcdMXHsxBge8gwVK6wk");
        REDIRECT_URI = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.redirect_uri", "http://localhost:8000/redirect");
        SUDIR_AUTHORIZE_ADDRESS = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.authorize_address", "https://login-test.mos.ru/sps/oauth/oauth20/authorize");
        SUDIR_LOGOUT_ADDRESS = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.logout_address", "https://login-test.mos.ru/logout/logout.jsp");
        REDIRECT_LOGOUT_URI = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.redirect_logout_uri", "http://localhost:8000/notfound");
        SUDIR_TOKEN_ADDRESS = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.token_address", "https://login-test.mos.ru/sps/oauth/oauth20/token");
        SUDIR_DATA_ADDRESS = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.sudir.data_address", "https://login-test.mos.ru/auth/authenticationWS/getUserData");
        SECURITY_ON = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.security", "0").equals("1");
    }

    public SudirToken getToken(String authCode) {
        ClientRequest request = new ClientRequest(SUDIR_TOKEN_ADDRESS);
        request.accept("application/json");
        request.body(MediaType.APPLICATION_FORM_URLENCODED, buildTokenRequestString(authCode));
        try {
            ClientResponse<SudirToken> response = request.post(SudirToken.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            SudirToken token = response.getEntity();
            logSudirResponse("Token message: ", token);
            return token;
        } catch (Exception e) {
            logger.error("Error get token from SUDIR: ", e);
        }
        return null;
    }

    private void logSudirResponse(String message, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info(message + mapper.writeValueAsString(object));
        } catch (IOException e) {
            logger.error("Error unmarshall sudir object to string", e);
        }
    }

    private String buildTokenRequestString(String authCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code" + "&")
                .append("code=" + authCode + "&")
                .append("client_id=" + CLIENT_ID + "&")
                .append("client_secret=" + CLIENT_SECRET + "&")
                .append("redirect_uri=" + REDIRECT_URI);
        return sb.toString();
    }

    public SudirPersonData getPersonData(String token) {
        ClientRequest request = new ClientRequest(SUDIR_DATA_ADDRESS + "?access_token=" + token);
        request.accept("application/json");
        try {
            try {
                ClientResponse<SudirPersonData> response = request.get(SudirPersonData.class);
                if (response.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                SudirPersonData data = response.getEntity();
                logSudirResponse("Person data message: ", data);
                return data;
            } catch (Exception e) {
                try {
                    ClientResponse<ESIAPersonData> response2 = request.get(ESIAPersonData.class);
                    if (response2.getStatus() != 200) {
                        throw new RuntimeException("Failed : HTTP error code (2): " + response2.getStatus());
                    }
                    SudirPersonData data2 = new SudirPersonData(response2.getEntity());
                    logSudirResponse("Person data message esia: ", data2);
                    return data2;
                } catch (Exception e2) {
                    ClientResponse<String> response1 = request.get(String.class);
                    String str = response1.getEntity();
                    logSudirResponse(String.format("Person data message2 (status=%s): ", response1.getStatus()), str);
                }
            }
        } catch (Exception e) {
            logger.error("Error get person data from SUDIR: ", e);
        }
        return null;
    }

    private String quotedStr(String source) {
        return "\"" + source + "\"";
    }
}
