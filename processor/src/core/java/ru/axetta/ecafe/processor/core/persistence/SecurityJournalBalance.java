/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.ParameterMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 26.04.16
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class SecurityJournalBalance {
    private Long idOfJournalBalance;
    private SJBalanceTypeEnum eventType;
    private Date eventDate;
    private SJBalanceSourceEnum eventSource;
    private Boolean isSuccess;
    private String terminal;
    private String protocol;
    private String eventInterface;
    private Client client;
    private ClientPayment clientPayment;
    private Order order;
    private Long idOfOrder;
    private Long idOfOrg;
    private String request;
    private String clientAddress;
    private String serverAddress;
    private String certificate;
    private String message;

    public SecurityJournalBalance() {

    }

    public static SecurityJournalBalance createSecurityJournalBalance(SJBalanceTypeEnum eventType,
            Date eventDate,
            SJBalanceSourceEnum eventSource,
            String terminal,
            String protocol,
            String eventInterface,
            Client client,
            String request,
            String clientAddress,
            String serverAddress,
            String certificate) {
        SecurityJournalBalance result = new SecurityJournalBalance();
        result.eventType = eventType;
        result.eventDate = eventDate;
        result.eventSource = eventSource;
        result.terminal = terminal;
        result.protocol = protocol;
        result.eventInterface = eventInterface;
        result.client = client;
        result.request = request;
        result.clientAddress = clientAddress;
        result.serverAddress = serverAddress;
        result.certificate = certificate;
        return result;
    }

    public static SecurityJournalBalance getSecurityJournalBalanceDataFromOrder(Payment payment, Client client,
            SJBalanceTypeEnum eventType, SJBalanceSourceEnum eventSource, Long idOfOrg) {
        String serverAddress;
        try {
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            serverAddress = null;
        }
        try {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            String terminal = null;
            String protocol = httpServletRequest.getScheme();
            String parameters = httpServletRequest.getQueryString();
            String request = httpServletRequest.getScheme() + "://" +
                    httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() +
                    httpServletRequest.getRequestURI() + (parameters != null ? "?" + parameters : "");
            //String uri = ((RequestFacade)((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()).getRequestURI();
            String cert = null;
            X509Certificate[] certificates = (X509Certificate[]) httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
            if (certificates != null && certificates.length > 0) {
                cert = certificates[0].toString();
            }

            SecurityJournalBalance res = SecurityJournalBalance.createSecurityJournalBalance(
                    eventType,                                                                  //eventType
                    payment.getTime(),                                                          //eventdate
                    eventSource,                                                                //eventSource
                    terminal,                                                                   //terminal
                    protocol,                                                                   //protocol
                    null,                                                                       //eventInterface
                    client,                                                                     //client
                    request,                                                                    //request
                    httpServletRequest.getRemoteAddr(),                                         //clientAddress
                    serverAddress == null ? httpServletRequest.getLocalAddr() : serverAddress,  //serverAddress
                    cert);                                                                      //certificate
            res.setIdOfOrder(payment.getIdOfOrder());
            res.setIdOfOrg(idOfOrg);
            return res;
        } catch (Exception e) {
            SecurityJournalBalance res = new SecurityJournalBalance();
            res.setEventType(SJBalanceTypeEnum.SJBALANCE_TYPE_ORDER);
            res.setEventDate(payment.getTime());
            res.setClient(client);
            res.setServerAddress(serverAddress);
            res.setEventSource(eventSource);
            res.setIdOfOrder(payment.getIdOfOrder());
            res.setIdOfOrg(idOfOrg);
            return res;
        }
    }

    public static SecurityJournalBalance getSecurityJournalBalanceDataFromPayment(
            PaymentRequest.PaymentRegistry.Payment payment) {
        Client client;
        SJBalanceSourceEnum source;
        String serverAddress;
        try {
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            serverAddress = null;
        }
        try {
            client = DAOService.getInstance().getClientByContractId(payment.getContractId());
        } catch (Exception e) {
            client = null;
        }
        try {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String terminal = null;
            String protocol = httpServletRequest.getScheme();
            String parameters = httpServletRequest.getQueryString();
            String request = httpServletRequest.getScheme() + "://" +
                    httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() +
                    httpServletRequest.getRequestURI() + (parameters != null ? "?" + parameters : "");
            String uri = ((RequestFacade)((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()).getRequestURI();
            if (uri.equals("/processor/sync")) {
                source = SJBalanceSourceEnum.SJBALANCE_SOURCE_SYNC;
            } else if (uri.equals("/processor/payment-std")) {
                source = SJBalanceSourceEnum.SJBALANCE_SOURCE_STDPAY;
                ParameterMap m = (ParameterMap)httpServletRequest.getParameterMap();
                if (m.containsKey("termid")) {
                    String[] term = (String[])m.get("termid");
                    terminal = term[0];
                }
            } else if (uri.equals("/processor/pay")) {
                source = SJBalanceSourceEnum.SJBALANCE_SOURCE_PAY;
            } else {
                source = SJBalanceSourceEnum.SJBALANCE_SOURCE_UNKNOWN;
            }
            String cert = null;
            X509Certificate[] certificates = (X509Certificate[]) httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
            if (certificates != null && certificates.length > 0) {
                cert = certificates[0].toString();
            }
            SecurityJournalBalance res = SecurityJournalBalance.createSecurityJournalBalance(
                    SJBalanceTypeEnum.SJBALANCE_TYPE_PAYMENT,                                   //eventType
                    payment.getPayTime(),                                                       //eventdate
                    source,                                                                     //eventSource
                    terminal,                                                                   //terminal
                    protocol,                                                                   //protocol
                    ClientPayment.PAYMENT_METHOD_NAMES[payment.getPaymentMethod()],             //eventInterface
                    client,                                                                     //client
                    request,                                                                    //request
                    httpServletRequest.getRemoteAddr(),                                         //clientAddress
                    serverAddress == null ? httpServletRequest.getLocalAddr() : serverAddress,  //serverAddress
                    cert);                                                                      //certificate
            return res;
        } catch (Exception e) {
            SecurityJournalBalance res = new SecurityJournalBalance();
            res.setEventType(SJBalanceTypeEnum.SJBALANCE_TYPE_PAYMENT);
            res.setEventDate(payment.getPayTime());
            res.setEventInterface(ClientPayment.PAYMENT_METHOD_NAMES[payment.getPaymentMethod()]);
            res.setClient(client);
            res.setServerAddress(serverAddress);
            if(payment.getAddIdOfPayment() != null && payment.getAddIdOfPayment().startsWith("РНИП")) {
                source = SJBalanceSourceEnum.SJBALANCE_SOURCE_RNIP;
                res.setEventSource(source);
            }
            return res;
        }
    }

    public static void saveSecurityJournalBalanceFromPayment(SecurityJournalBalance journal, boolean success,
            String message, ClientPayment clientPayment) {
        journal.setIsSuccess(success);
        journal.setMessage(message);
        journal.setClientPayment(clientPayment);
        DAOService.getInstance().saveSecurityJournalBalance(journal);
    }

    public static void saveSecurityJournalBalanceFromOrder(SecurityJournalBalance journal, boolean success,
            String message) {
        journal.setIsSuccess(success);
        journal.setMessage(message);
        DAOService.getInstance().saveSecurityJournalBalance(journal);
    }

    public Long getIdOfJournalBalance() {
        return idOfJournalBalance;
    }

    public void setIdOfJournalBalance(Long idOfJournalBalance) {
        this.idOfJournalBalance = idOfJournalBalance;
    }

    public SJBalanceTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(SJBalanceTypeEnum eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public SJBalanceSourceEnum getEventSource() {
        return eventSource;
    }

    public void setEventSource(SJBalanceSourceEnum eventSource) {
        this.eventSource = eventSource;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getEventInterface() {
        return eventInterface;
    }

    public void setEventInterface(String eventInterface) {
        this.eventInterface = eventInterface;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientPayment getClientPayment() {
        return clientPayment;
    }

    public void setClientPayment(ClientPayment clientPayment) {
        this.clientPayment = clientPayment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}