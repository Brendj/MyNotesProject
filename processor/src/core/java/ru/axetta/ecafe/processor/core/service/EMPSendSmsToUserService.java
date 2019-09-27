/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPReqestSmsType;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPResponseSmsType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by a.voinov on 23.08.2019.
 */
@Component
@Scope(value = "singleton")
public class EMPSendSmsToUserService implements IAuthorizeUserBySms {
    private static final Logger logger = LoggerFactory.getLogger(EMPSendSmsToUserService.class);

    @Override
    public String sendCodeAndGetError(User user, String code) throws IOException {
        EMPReqestSmsType empReqestSmsType = new EMPReqestSmsType(
                RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.token", ""),
                user.getPhone(),
                String.format("Код авторизации - %s", code),
                RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.source", ""));

        PostMethod httpMethod = new PostMethod(RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.url", ""));
        httpMethod.addRequestHeader("Content-Type", "application/json; charset=utf-8");
        setParametrsForRequest(httpMethod, empReqestSmsType);
        EMPResponseSmsType empResponseSmsType = new EMPResponseSmsType();
        try {
            HttpClient httpClient = new HttpClient();
            int statusCode = httpClient.executeMethod(httpMethod);
            String response = httpMethod.getResponseBodyAsString();
            if (statusCode != HttpStatus.SC_OK) {
                empResponseSmsType.setErrorCode(1);
                empResponseSmsType.setErrorMessage("Ошибка сетевого соединения. Попробуйте позже.");
                return "Ошибка сетевого соединения. Попробуйте позже.";
            }
            logger.info(String.format("Retrieved response for number %s send message SMS service: %s", user.getPhone(), response));

            //return readDataFromResponse(response, EMPResponseSmsType.class);
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
            empResponseSmsType.setErrorCode(413);
            empResponseSmsType.setErrorMessage("Ошибка разбора ответа службы");
            return "Ошибка разбора ответа службы";
        }
        finally {
            httpMethod.releaseConnection();
        }
    }

    private void setParametrsForRequest (PostMethod httpMethod, EMPReqestSmsType empReqestSmsType) throws IOException {
        //Установка параметров запроса
        ObjectMapper objectMapper = new ObjectMapper();
        String serialized = objectMapper.writeValueAsString(empReqestSmsType);
        StringRequestEntity requestEntity = new StringRequestEntity(serialized, "application/json", "UTF-8");
        httpMethod.setRequestEntity(requestEntity);
    }
}
