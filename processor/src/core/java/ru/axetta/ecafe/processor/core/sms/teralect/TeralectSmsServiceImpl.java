/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.teralect;

import generated.nfp.TransactionListType;
import generated.nfp.TransactionService;
import generated.nfp.TransactionServicePortType;
import generated.ru.teralect.*;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

public class TeralectSmsServiceImpl extends ISmsService {

    public TeralectSmsServiceImpl(Config config) {
        super(config);
    }

    ISMSEntry port;
    ISMSEntry getPort() {
        if (this.port!=null) return port;
        SMSEntryProxy smsEntryProxy = new SMSEntryProxy();
        ISMSEntry port = smsEntryProxy.getBasicHttpBindingISMSEntry();
        Client client = ClientProxy.getClient(port);
        String url = config.getServiceUrl();
        BindingProvider provider = (BindingProvider)port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        this.port = port;
        return port;
    }

    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, String text)
            throws Exception {
        if (StringUtils.isEmpty(sender)) {
            sender = config.getDefaultSender();
        }
        ISMSEntry port = getPort();
        SendSMSReq sendSMSReq = new SendSMSReq();
        sendSMSReq.setDestinationAddress(phoneNumber);
        sendSMSReq.setLogin(config.getUserName());
        sendSMSReq.setPassword(config.getPassword());
        sendSMSReq.setSourceAddress(sender);
        sendSMSReq.setText(text);
        SendSMSResp resp = port.sendSMS(sendSMSReq);
        int rc = translateRC(resp.getRc());
        return new SendResponse(rc, resp.getRc()==null?null:resp.getRc().value(), resp.getTrxId());
    }

    private int translateRC(RetCode rc) {
        if (rc==null || rc.value()==null) return SendResponse.XML_ERROR;
        if (rc.value().equals("RC_OK")) return SendResponse.MIN_SUCCESS_STATUS;
        if (rc.value().equals("RC_OUTOFRANGE")) return SendResponse.INPUT_PARAMS_ERROR;
        if (rc.value().equals("RC_AUTHORIZATIONFAILED")) return SendResponse.AUTH_FAILED;
        if (rc.value().equals("RC_IPBLOCKED")) return SendResponse.AUTH_FAILED;
        if (rc.value().equals("RC_DELIVERERR")) return SendResponse.NO_RECIPIENTS;
        if (rc.value().equals("RC_SRCADRERR")) return SendResponse.AUTH_FAILED;
        return SendResponse.COMMON_FAILURE;
    }

    private JAXBElement<String> getJB(String value) {
        return new JAXBElement<String>(new QName(""), String.class, value);
    }

    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        ISMSEntry port = getPort();
        GetDeliveryStatusReq rq = new GetDeliveryStatusReq();
        rq.setLogin(config.getUserName());
        rq.setPassword(config.getPassword());
        rq.setTrxID(messageId);
        GetDeliveryStatusResp rp = port.getDeliveryStatus(rq);
        if (translateRC(rp.getRc())!=SendResponse.MIN_SUCCESS_STATUS) throw new Exception("Invalid response: "+(rp.getRc()==null?"null":rp.getRc().value()));
        return translateDeliverStatus(rp.getDeliveryStatus().value());
    }

    private DeliveryResponse translateDeliverStatus(String value) {
        int status;
        if (value.equalsIgnoreCase("Delivered")) status = DeliveryResponse.DELIVERED;
        else if (value.equalsIgnoreCase("SentToSmsc")) status = DeliveryResponse.SENT;
        else if (value.equalsIgnoreCase("Enqueued")) status = DeliveryResponse.SENT;
        else if (value.equalsIgnoreCase("Expired")) status = DeliveryResponse.EXPIRED;
        else if (value.equalsIgnoreCase("Undeliverable")) status = DeliveryResponse.INVALID_DESTINATION_ADDRESS;
        else status = DeliveryResponse.NOT_DELIVERED;
        return new DeliveryResponse(status, null, null);
    }
}
