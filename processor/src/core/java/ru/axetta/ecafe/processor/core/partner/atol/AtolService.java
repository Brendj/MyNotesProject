/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.atol.json.*;
import ru.axetta.ecafe.processor.core.persistence.AtolCompany;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final String ATOL_CALLBACK_URL = "ecafe.processor.atol.callback.url";
    private static final String ATOL_GROUP_CODE = "ecafe.processor.atol.group.code";
    private static final Integer ATOL_TOKEN_EXPIRED = 11;
    //private static final String DEFAULT_ADDRESS = "https://testonline.atol.ru/possystem/v4/";
    //private static final String DEFAULT_USER = "v4-online-atol-ru";
    //private static final String DEFAULT_PASSWORD = "iGFFuihss";

    private static final String NAME_FORMAT = "Пополнение счета %s";
    private static final String OPERATION = "sell";
    private static final String OPERATION_REFUND = "sell_refund";

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
            logger.info("Get new token for Atol");
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                ObjectMapper objectMapper = new ObjectMapper();
                AtolTokenResponse atolResponse = objectMapper.readValue(inputStream, AtolTokenResponse.class);
                logger.info(String.format("Got token from Atol. Status code = %s, Response body - %s", statusCode, objectMapper.writeValueAsString(atolResponse)));
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

    private String getOperation(ClientPaymentAddon clientPaymentAddon) {
        if (clientPaymentAddon.getClientPayment().getPaySum() > 0) return OPERATION; else return OPERATION_REFUND;
    }

    public void sendPayment(ClientPaymentAddon clientPaymentAddon) {
        try {
            AtolPaymentRequest request = getAtolPaymentRequest(clientPaymentAddon);
            URL url = new URL(getServiceAddress() + getAtolGroupCode() + "/" + getOperation(clientPaymentAddon));
            PostMethod httpMethod = new PostMethod(url.getPath());
            httpMethod.setRequestHeader("Content-type", "application/json; charset=utf-8");
            httpMethod.setRequestHeader("Token", getToken());

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(request);
            logger.info("Send request to Atol. Request: " + jsonString);
            AtolDAOService.getInstance().saveAtolPacket(clientPaymentAddon, jsonString);
            StringRequestEntity requestEntity = new StringRequestEntity(jsonString, "application/json", "UTF-8");
            httpMethod.setRequestEntity(requestEntity);
            try {
                HttpClient httpClient = getHttpClient(url);
                int statusCode = httpClient.executeMethod(httpMethod);
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                String responseBody = responseToString(inputStream);

                logger.info(String.format("Got response from Atol. Status code = %s, Response body - %s", statusCode, responseBody));
                AtolPaymentResponse atolResponse = mapper.readValue(responseBody, AtolPaymentResponse.class);
                if (statusCode == HttpStatus.SC_OK || !StringUtils.isEmpty(atolResponse.getUuid())) {
                    AtolDAOService.getInstance().saveWithSuccess(clientPaymentAddon, atolResponse.getUuid(), responseBody);
                } else {
                    AtolDAOService.getInstance().saveWithError(clientPaymentAddon, statusCode);
                    if (atolResponse.getError() != null && atolResponse.getError().getCode().equals(ATOL_TOKEN_EXPIRED)) token = null;
                }
            } finally {
                httpMethod.releaseConnection();
            }
        } catch (Exception e) {
            logger.error("Error in send payment to Atol: ", e);
        }
    }

    private String responseToString(InputStream inputStream) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private HttpClient getHttpClient(URL url) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
        return httpClient;
    }

    private AtolPaymentRequest getAtolPaymentRequest(ClientPaymentAddon clientPaymentAddon) {
        AtolPaymentRequest request = new AtolPaymentRequest();
        request.setExternalId(clientPaymentAddon.getClientPayment().getTransaction().getIdOfTransaction().toString());
        request.setTimestamp(CalendarUtils.dateTimeToString(clientPaymentAddon.getClientPayment().getCreateTime()));

        Service service = new Service();
        service.setCallbackUrl(getAtolServiceCallbackUrl());
        request.setService(service);

        Receipt receipt = new Receipt();

        //Секция Receipt/Company
        Company company = new Company();
        AtolCompany atolCompany = AtolDAOService.getInstance().getAtolCompany();
        company.setEmail(atolCompany.getEmailOrg());
        company.setInn(atolCompany.getInn());
        company.setPaymentAddress(atolCompany.getPlace());
        company.setSno(Company.Sno.OSN);
        receipt.setCompany(company);

        Double paymentSum = doubleValue(clientPaymentAddon.getClientPayment().getPaySum());
        if (paymentSum < 0) paymentSum = -paymentSum;

        //Секция Receipt/Client
        receipt.setClient(getClientInfo(clientPaymentAddon, atolCompany));

        //Секция Receipt/Total
        receipt.setTotal(paymentSum);

        //Секция Receipt/Payments
        AtolJsonPayment payment = new AtolJsonPayment();
        payment.setSum(paymentSum);
        payment.setType(AtolJsonPayment.Type._1);
        List<AtolJsonPayment> paymentsList = new ArrayList<>();
        paymentsList.add(payment);
        receipt.setPayments(paymentsList);

        //Секция Receipt/Items
        AtolJsonItem item = new AtolJsonItem();
        item.setName(String.format(NAME_FORMAT, clientPaymentAddon.getClientPayment().getTransaction().getClient().getContractId()));
        item.setPrice(paymentSum);
        item.setQuantity(1);
        item.setSum(paymentSum);
        Vat vat = new Vat();
        vat.setType(Vat.Type.VAT_20);
        item.setVat(vat);
        item.setPaymentMethod(AtolJsonItem.PaymentMethod.FULL_PAYMENT);
        item.setPaymentObject(AtolJsonItem.PaymentObject.SERVICE);
        List<AtolJsonItem> itemsList = new ArrayList<>();
        itemsList.add(item);
        receipt.setItems(itemsList);

        request.setReceipt(receipt);
        return request;
    }

    private Double doubleValue(Long value) {
        return new Double(value) / 100d;
    }

    private AtolJsonClient getClientInfo(ClientPaymentAddon clientPaymentAddon, AtolCompany atolCompany) {
        AtolJsonClient client = new AtolJsonClient();
        String email = clientPaymentAddon.getClientPayment().getTransaction().getClient().getEmail();
        String mobile = clientPaymentAddon.getClientPayment().getTransaction().getClient().getMobile();
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            client.getAdditionalProperties().put("email", atolCompany.getEmailCheck());
            return client;
        }
        if (!StringUtils.isEmpty(email)) client.getAdditionalProperties().put("email", email);
        if (!StringUtils.isEmpty(mobile)) client.getAdditionalProperties().put("phone", mobile);
        return client;
    }

    private String getAtolServiceCallbackUrl() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(ATOL_CALLBACK_URL, "");
    }

    private String getAtolGroupCode() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(ATOL_GROUP_CODE, "");
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
