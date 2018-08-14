/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

/**
 * Created by i.semenov on 14.08.2018.
 */
@Component
@Scope(value = "singleton")
public class EMPAuthorizeUserBySmsService implements IAuthorizeUserBySms {
    private static final Logger logger = LoggerFactory.getLogger(EMPAuthorizeUserBySmsService.class);
    @Override
    public String sendCodeAndGetError(User user, String code) {
        NameValuePair[] parameters = new NameValuePair[] {
                new NameValuePair("token", RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.token", "")),
                new NameValuePair("msisdn", user.getPhone()),
                new NameValuePair("message", String.format("Код авторизации - %s", code)),
                new NameValuePair("source", RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.source", "")),
                };

        GetMethod httpMethod = new GetMethod(RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.url", ""));
        httpMethod.setQueryString(parameters);

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode != HttpStatus.SC_OK) {
                return "Ошибка сети или неправильный формат мобильного телефона";
            }

            String response = httpMethod.getResponseBodyAsString();

            logger.info(String.format("Retrieved response for User %s auth code generation from SMS service: %s", user.getUserName(), response));

            String errCode = "";
            if (response != null) {
                StringTokenizer st = new StringTokenizer(response, ",:\"{}");
                if (!st.hasMoreTokens()) {
                    return "Ошибка при обращении к сервису отправки СМС";
                }
                errCode = st.nextToken();
                if (errCode.equals("errorCode") && st.hasMoreTokens()) {
                    errCode = st.nextToken();
                }
            }
            return errCode;
        } catch (Exception e) {
            return "Ошибка при обращении к сервису отправки СМС";
        }
        finally {
            httpMethod.releaseConnection();
        }
    }
}
