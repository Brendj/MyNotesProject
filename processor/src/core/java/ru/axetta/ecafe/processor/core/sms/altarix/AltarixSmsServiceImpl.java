/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.altarix;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.MessageIdGenerator;
import ru.axetta.ecafe.processor.core.sms.SendResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: yashin
 * Date: 22.06.12
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */

/**
 * Коннектор к шлюзу Altarix
 */
public class AltarixSmsServiceImpl extends ISmsService {

    /**
     * Пара (дата отправки СМС, статус отправки)
     */
    private static class SentParameters {

        /**
         * дата отправки СМС
         */
        private Date sentDate;
        /**
         * Статус, записанный в ответе шлюза
         */
        private int sentStatus;

        public SentParameters(Date sentDate, int sentStatus) {
            this.sentDate = sentDate;
            this.sentStatus = sentStatus;
        }

        public Date getSentDate() {
            return sentDate;
        }

        public void setSentDate(Date sentDate) {
            this.sentDate = sentDate;
        }

        public int getSentStatus() {
            return sentStatus;
        }

        public void setSentStatus(int sentStatus) {
            this.sentStatus = sentStatus;
        }
    }

    /**
     * id сервиса партнера
     */
    private String userServiceId;
    /**
     * Параметры отправки для отправленных сообщений
     * ключ = messageId, значение = пара(sentDate, sentStatus)
     */
    //private HashMap<String, SentParameters> sentParameters = new HashMap<String, SentParameters>();

    /**
     * Конструктор
     *
     * @param config        конфигурация коннектора
     * @param userServiceId id сервиса партнера
     */
    public AltarixSmsServiceImpl(Config config, String userServiceId) {
        super(config);
        this.userServiceId = userServiceId;

    }

    /**
     * Отправляет на шлюз запрос составленный из Url шлюза параметров из аргумента queryParameters.
     * Сохраняет параметры отправки в поле sentParameters
     *
     * @param queryParameters параметры запроса
     * @param messageId       id сообщения
     * @return ответ сервера в виде строки, содержащий статус отправки и UUID сообщения
     * @throws Exception
     */
    public String sendServiceRequest(NameValuePair[] queryParameters, String messageId) throws Exception {

        GetMethod httpMethod = new GetMethod(config.getServiceUrl());
        httpMethod.setQueryString(queryParameters);

        try {

            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            int statusCode = HttpStatus.SC_BAD_REQUEST;
            int attempts = 1;
            while (statusCode == HttpStatus.SC_OK && attempts <= 3) {
                statusCode = httpClient.executeMethod(httpMethod);
                attempts++;
            }
            if (attempts == 3 && statusCode != HttpStatus.SC_OK) {
                throw new HttpException(String.format("HTTP status is: %d", statusCode));
            }

            //Date sentDate = new Date();

            String response = httpMethod.getResponseBodyAsString();

            int sentStatus = getStatus(response);
            //sentParameters.put(messageId, new SentParameters(sentDate, sentStatus));

            logger.info(String.format("Retrieved response from SMS service: %s", response));
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Retrieved response from SMS service: %s", response));
            }

            return response;
        } finally {
            httpMethod.releaseConnection();
        }
    }

    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, String text) throws Exception {
        NameValuePair[] queryParameters = createQueryParameters(userServiceId, phoneNumber, text, "send", "sms");

        MessageIdGenerator messageIdGenerator = RuntimeContext.getInstance().getMessageIdGenerator();
        String messageId = messageIdGenerator.generate();

        String responseString = sendServiceRequest(queryParameters, messageId);

        int status = getStatus(responseString);

        return new SendResponse(translateSendStatus(status), null, messageId);

    }

    /**
     * Создает строку параметров запроса в виде массива пар
     *
     * @param serviceParam   параметр запроса к шлюзу
     * @param msisdnParam    параметр запроса к шлюзу
     * @param textParam      параметр запроса к шлюзу
     * @param operationParam параметр запроса к шлюзу
     * @param typeParam      параметр запроса к шлюзу
     * @return массив пар (имя параметра, значение параметра)
     * @throws Exception
     */
    private NameValuePair[] createQueryParameters(String serviceParam, String msisdnParam, String textParam,
            String operationParam, String typeParam) throws Exception {

        NameValuePair[] queryParameters = new NameValuePair[]{
                new NameValuePair("login", config.getUserName()), new NameValuePair("passwd", config.getPassword()),
                new NameValuePair("service", serviceParam), new NameValuePair("msisdn", msisdnParam),
                new NameValuePair("text", textParam), new NameValuePair("operation", operationParam),
                new NameValuePair("type", typeParam)};

        return queryParameters;
    }

    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        //TODO: Not implemented
        //SentParameters sentParametersByMessageId = sentParameters.get(messageId);

        //return new DeliveryResponse(translateDeliveryStatus(sentParametersByMessageId.getSentStatus()),
         //       sentParametersByMessageId.getSentDate(), null);
        return new DeliveryResponse(DeliveryResponse.DELIVERED, null, null);

    }

    /**
     * Извлекает из ответа шлюза статус отправки
     *
     * @param response ответ шлюза
     * @return статус отправки СМС
     */
    private Integer getStatus(String response) {
        if (response == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(response, "|");
        if (!st.hasMoreTokens()) {
            return null;
        }
        return Integer.parseInt(st.nextToken());


    }

    /**
     * По ответу серверу делает вывод о статусе доставки
     *
     * @param deliveryStatus статус из ответа шлюза
     * @return статус доставки
     */
    private int translateDeliveryStatus(int deliveryStatus) {
        if (deliveryStatus == 0) {
            return DeliveryResponse.DELIVERED;
        } else {
            return DeliveryResponse.NOT_DELIVERED;
        }

    }

    /**
     * По ответу сервера делает вывод о статусе отправки
     *
     * @param sendStatus статус из ответа шлюза
     * @return статус отправки
     */
    private int translateSendStatus(int sendStatus) {
        if (sendStatus == 0) {
            return SendResponse.MIN_SUCCESS_STATUS;
        } else {
            return SendResponse.COMMON_FAILURE;
        }


    }

}
