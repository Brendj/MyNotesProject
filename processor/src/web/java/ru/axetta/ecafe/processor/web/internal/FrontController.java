/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.WSSecurityEngineResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class FrontController extends HttpServlet {
    public static class FrontControllerException extends Exception {

        public FrontControllerException(String message) {
            super(message);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public FrontControllerException(String message, Throwable cause) {
            super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    @Resource
    WebServiceContext wsContext;

    final Logger logger = LoggerFactory.getLogger(FrontController.class);

    @WebMethod(operationName = "test")
    public String test(@WebParam(name = "orgId") Long orgId)
            throws FrontControllerException {
        checkRequestValidity(orgId);
        return "OK";
    }
    @WebMethod(operationName = "registerCard")
    public Long registerCard(@WebParam(name = "orgId") Long orgId, @WebParam(name = "clientId") Long clientId,
            @WebParam(name = "cardNo") Long cardNo, @WebParam(name = "cardPrintedNo") Long cardPrintedNo,
            @WebParam(name = "cardType") int cardType, @WebParam(name = "issuedTime") Date issuedTime, @WebParam(name = "validTime") Date validTime)
            throws FrontControllerException {
        checkRequestValidity(orgId);
        ///
        try {
            return RuntimeContext.getInstance().getCardManager().createCard(clientId, cardNo, cardType, Card.ACTIVE_STATE,
                    validTime, Card.ISSUED_LIFE_STATE, null, issuedTime, cardPrintedNo);
        } catch (Exception e) {
            logger.error("Failed registerCard", e);
            throw new FrontControllerException("Ошибка при регистрации карты: "+e.getMessage(), e);
        }
    }

    public static class ClientDesc {
        public int recId;
        public String contractSurname;
        public String contractName;
        public String contractSecondName;
        public String contractDoc;
        public String surname;
        public String name;
        public String secondName;
        public String doc;
        public String address;
        public String phone;
        public String mobilePhone;
        public String email;
        public String group;
        public String snils;
        public boolean notifyBySms;
        public boolean notifyByEmail;
        public String comments;
        public Long cardNo;
        public Long cardPrintedNo;
        public int cardType;
        public Date cardExpiry;
        public Date cardIssued;
    }

    public static class RegisterClientResult {
        public Long idOfClient;
        public int recId;
        public boolean success;
        public String error;

        public RegisterClientResult() {}
        public RegisterClientResult(Long idOfClient, int recId, boolean success, String error) {
            this.idOfClient = idOfClient;
            this.recId = recId;
            this.success = success;
            this.error = error;
        }
    }

    @WebMethod(operationName = "registerClients")
    public List<RegisterClientResult> registerClients(@WebParam(name = "orgId")Long orgId,
            @WebParam(name = "clientDescList") List<ClientDesc> clientDescList, @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness)
            throws FrontControllerException {
        checkRequestValidity(orgId);

        LinkedList<RegisterClientResult> results = new LinkedList<RegisterClientResult>();
        for (ClientDesc cd : clientDescList) {
            try {
                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                if (cd.contractSurname!=null) fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, cd.contractSurname);
                if (cd.contractName!=null) fc.setValue(ClientManager.FieldId.CONTRACT_NAME, cd.contractName);
                if (cd.contractSecondName!=null) fc.setValue(ClientManager.FieldId.CONTRACT_SECONDNAME, cd.contractSecondName);
                if (cd.contractDoc!=null) fc.setValue(ClientManager.FieldId.CONTRACT_DOC, cd.contractDoc);
                if (cd.surname!=null) fc.setValue(ClientManager.FieldId.SURNAME, cd.surname);
                if (cd.name!=null) fc.setValue(ClientManager.FieldId.NAME, cd.name);
                if (cd.secondName!=null) fc.setValue(ClientManager.FieldId.SECONDNAME, cd.secondName);
                if (cd.doc!=null) fc.setValue(ClientManager.FieldId.DOC, cd.doc);
                if (cd.address!=null) fc.setValue(ClientManager.FieldId.ADDRESS, cd.address);
                if (cd.phone!=null) fc.setValue(ClientManager.FieldId.PHONE, cd.phone);
                if (cd.mobilePhone!=null) fc.setValue(ClientManager.FieldId.MOBILE_PHONE, cd.mobilePhone);
                if (cd.email!=null) fc.setValue(ClientManager.FieldId.EMAIL, cd.email);
                if (cd.group!=null) fc.setValue(ClientManager.FieldId.GROUP, cd.group);
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, cd.notifyBySms?"1":"0");
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, cd.notifyByEmail?"1":"0");
                if (cd.comments!=null) fc.setValue(ClientManager.FieldId.COMMENTS, cd.comments);
                if (cd.cardNo!=null) fc.setValue(ClientManager.FieldId.CARD_ID, cd.cardNo);
                if (cd.cardPrintedNo!=null) fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cd.cardPrintedNo);
                fc.setValue(ClientManager.FieldId.CARD_TYPE, cd.cardType);
                if (cd.cardExpiry!=null) fc.setValue(ClientManager.FieldId.CARD_EXPIRY, cd.cardExpiry);
                if (cd.cardIssued!=null) fc.setValue(ClientManager.FieldId.CARD_ISSUED, cd.cardIssued);
                if (cd.snils!=null) fc.setValue(ClientManager.FieldId.SAN, cd.snils);
                long idOfClient = ClientManager.registerClient(orgId, fc, checkFullNameUniqueness);
                results.add(new RegisterClientResult(idOfClient, cd.recId, true, null));
            } catch (Exception e) {
                results.add(new RegisterClientResult(null, cd.recId, false, e.getMessage()));
            }
        }
        return results;
    }

    private void checkRequestValidity(Long orgId) throws FrontControllerException {
        MessageContext msgContext = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
        X509Certificate[] cert = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        //X509Certificate cert = (X509Certificate)((WSSecurityEngineResult)wsContext.getMessageContext().get(WSS4JInInterceptor.SIGNATURE_RESULT)).get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);
        if (cert==null || cert.length==0) throw new FrontControllerException("В запросе нет валидных сертификатов");
        Org org = DAOService.getInstance().getOrg(orgId);
        if (org==null) throw new FrontControllerException("Неизвестная организация: "+orgId);
        PublicKey publicKey = null;
        try {
            publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
        } catch (Exception e) {
            throw new FrontControllerException("Внутренняя ошибка", e);
        }
        if (!publicKey.equals(cert[0].getPublicKey())) throw new FrontControllerException("Ключ сертификата невалиден: "+orgId);
    }
}
