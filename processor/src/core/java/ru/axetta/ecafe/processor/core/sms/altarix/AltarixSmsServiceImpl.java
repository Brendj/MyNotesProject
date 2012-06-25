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

public class AltarixSmsServiceImpl extends ISmsService {

    private static class SentParameters{

        private Date sentDate;
        private int sentStatus;

        public SentParameters(Date sentDate,int sentStatus){
            this.sentDate=sentDate;
            this.sentStatus=sentStatus;
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
    private String userServiceId;
    /**
     * ключ = messageId, значение = пара(sentDate, sentStatus)
     */
    private HashMap<String,SentParameters> sentParameters =new HashMap<String, SentParameters>();

    public AltarixSmsServiceImpl(Config config,String userServiceId){
         super(config);
         this.userServiceId=userServiceId;

    }
    public  String sendServiceRequest(NameValuePair[] queryParameters,String messageId)
            throws Exception{

        GetMethod httpMethod=new GetMethod(config.getServiceUrl());
        httpMethod.setQueryString(queryParameters);

       try{

        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("UTF-8");
        int statusCode = httpClient.executeMethod(httpMethod);
        if (HttpStatus.SC_OK != statusCode) {
            throw new HttpException(String.format("HTTP status is: %d", statusCode));
        }

         Date sentDate=new Date();

        String response = httpMethod.getResponseBodyAsString();

         int sentStatus= getStatus(response);
           sentParameters.put(messageId, new SentParameters(sentDate, sentStatus));

        logger.info(String.format("Retrived response from SMS service: %s", response));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Retrived response from SMS service: %s", response));
        }

         return response;
         }
         finally {
             httpMethod.releaseConnection();
         }
    }

    public  SendResponse sendTextMessage(String sender, String phoneNumber, String text)
            throws Exception{
        NameValuePair[] queryParameters=  createQueryParameters(userServiceId,phoneNumber,text,"send","sms");

        MessageIdGenerator messageIdGenerator = RuntimeContext.getInstance().getMessageIdGenerator();
        String messageId = messageIdGenerator.generate();

        String responseString=sendServiceRequest(queryParameters,messageId);

        int status=getStatus(responseString);

        return new SendResponse(translateSendStatus(status),null,messageId) ;

    }

    private NameValuePair[] createQueryParameters(String serviceParam,String msisdnParam,String textParam,String operationParam,String typeParam)throws Exception{

        NameValuePair[]queryParameters=new NameValuePair[] {
                new NameValuePair("login",config.getUserName()) ,
                new NameValuePair("passwd",config.getPassword()) ,
                new NameValuePair("service",serviceParam) ,
                new NameValuePair("msisdn",msisdnParam) ,
                new NameValuePair("text",textParam) ,
                new NameValuePair("operation",operationParam) ,
                new NameValuePair("type",typeParam)
        };

       return queryParameters;
    }

    public  DeliveryResponse getDeliveryStatus(String messageId) throws Exception{
         SentParameters sentParametersByMessageId= sentParameters.get(messageId);

       return new DeliveryResponse(translateDeliveryStatus(sentParametersByMessageId.getSentStatus()),sentParametersByMessageId.getSentDate(),null);

    }

    private Integer getStatus(String response){
        if(response==null){return null;}
        StringTokenizer st=new StringTokenizer(response,"|");
        if(!st.hasMoreTokens()){return null;}
        return Integer.parseInt(st.nextToken());


    }

    private int translateDeliveryStatus(int deliveryStatus){
        if(deliveryStatus==0)return DeliveryResponse.DELIVERED;
        else return DeliveryResponse.NOT_DELIVERED;

    }
    private int translateSendStatus(int sendStatus){
        if(sendStatus==0)return SendResponse.MIN_SUCCESS_STATUS;
        else return SendResponse.COMMON_FAILURE;



    }

}
