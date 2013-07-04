/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.front.items.VisitorItem;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
import java.util.UUID;

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
    private WebServiceContext wsContext;

    private final Logger logger = LoggerFactory.getLogger(FrontController.class);

    @WebMethod(operationName = "test")
    public String test(@WebParam(name = "orgId") Long orgId)
            throws FrontControllerException {
        checkRequestValidity(orgId);
        return "OK";
    }

    /* Выполняет проверку наличия карты с физическим идентификатором  idOfTempCard и признаком карты посетителя в таблице временных карт */
    @WebMethod(operationName = "checkVisitorByCard")
    public VisitorItem checkVisitorByCard(Long idOfOrg, Long cardNo) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        VisitorItem visitorItem = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае совпадения cardNo временной карты с cardNo в таблице постоянных карт,  *
             * выбрасывать исключение с сообщением «Карта уже зарегистрирована как постоянная» *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            if (c != null) {
                throw new Exception("Карта уже зарегистрирована как постоянная на клиента: "+c.getClient().getIdOfClient());
            }

            CardTemp ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае не обнаружения cardNo временной карты в таблице временных    *
             * карт выбрасывать исключение «Карта не зарегистрирована как временная» *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            if (ct == null) {
                throw new Exception("Карта не зарегистрирована как временная");
            }

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае совпадения cardNo временной карты с cardNo врем. карты клиента в таблице временных карт, *
             * выбрасывать исключение с сообщением «Карта уже зарегистрирована как временная карта клиента»      *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            if(ct.getClient() != null){
                throw new Exception("Карта уже зарегистрирована как временная карта клиента");
            }

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае совпадения cardNo временной карты с cardNo карты посетителя в таблице  *
             * временных карт - возвращать с процессинга информацию об этом посетителе         *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            if(ct.getVisitor() != null){
                String name = ct.getVisitor().getPerson().getFirstName();
                VisitorItem item = new VisitorItem(ct.getVisitor());
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return visitorItem;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    /* Выполняет регистрацию посетителя и временной карты посетителя */
    @WebMethod(operationName = "registerVisitor")
    public Long registerVisitor(Long idOfOrg,  Long cardNo, Date validDate, VisitorItem visitor) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Long idOfVisitor = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            /**
             * Если есть совпадение idOfCardNo регистрируемой временной карты с idOfCardNo в таблице постоянных карт,
             * выбрасывать исключение с сообщением «Карта уже зарегистрирована как постоянная»
             * */
            Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            if (c != null) {
                throw new Exception("Карта уже зарегистрирована как постоянная на клиента: "+c.getClient().getIdOfClient());
            }

            /**
             * Если есть совпадение id временной карты с id врем. карты клиента в таблице временных карт,
             * то выбрасывать исключение с сообщением «Карта уже зарегистрирована как временная карта клиента».
             * */
            CardTemp ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
            if (ct != null && ct.getClient()!=null) {
                String fio= ct.getClient().getPerson().getFullName();
                throw new Exception("Карта уже зарегистрирована как временная карта клиента: "+fio);
            }

            /**
             * Если параметр  validDate меньше текущей даты — выбрасывать исключение с
             * сообщением «Неверное значение даты окончания действия карты»
             * */
            if(System.currentTimeMillis()>validDate.getTime()){
                throw new Exception("Неверное значение даты окончания действия карты");
            }

            /**
             * Если хотя бы одно из полей имени == null выбрасывать исключение с
             * сообщением «все поля ФИО должны быть заполнены»
             * */
            if(StringUtils.isEmpty(visitor.getFirstName()) || StringUtils.isEmpty(visitor.getSurname()) || StringUtils.isEmpty(visitor.getSecondName())) {
                throw new  Exception("Все поля ФИО должны быть заполнены");
            }
            /**
             * Если поле-массив PersonDocuments не содержит ни одного описания удостоверния личности,
             * выбрасывать исключение с сообщением «Отсутствует информация об удостоверении личности»
             * */
            if((visitor.getDriverLicenceDate()==null || StringUtils.isEmpty(visitor.getDriverLicenceNumber())) &&
               (visitor.getPassportDate()==null || StringUtils.isEmpty(visitor.getPassportNumber())) &&
               (visitor.getWarTicketDate()==null || StringUtils.isEmpty(visitor.getWarTicketNumber()))
              ) {
                throw new  Exception("Отсутствует информация об удостоверении личности");
            }

            /**
             * Если поле-массив PersonDocuments содержит более одного описания документа одного и
             * того же типа (2 паспорта или 3 ВУ и т.п.) выбрасывать исключение с сообщением «Дублирование типов документов».
             * */

            /**
             * Если в  поле-массиве PersonDocuments у какого-либо документа для даты выдачи
             * будет указана еще не наступившая дата, выбрасывать исключение с сообщением «Неверная дата выдачи документа»
             * */
            if( (visitor.getDriverLicenceDate()!=null && System.currentTimeMillis()<visitor.getDriverLicenceDate().getTime()) ||
                (visitor.getPassportDate()!=null && System.currentTimeMillis()<visitor.getPassportDate().getTime()) ||
                (visitor.getWarTicketDate()!=null && System.currentTimeMillis()<visitor.getWarTicketDate().getTime())
               ){
                throw new Exception("Неверное значение даты окончания действия карты");
            }

            /**
             * Если есть совпадение id временной карты с id врем. карты посетителя в таблице врем. карт, то:
             * */
            if(ct!=null && ct.getCardNo().equals(cardNo)){

                /**
                 * если параметр  idOfOrg не совпадает с соотв. параметром в таблице врем. карт — обновляем значение этого параметра.
                 * */
                if (ct.getOrg().getIdOfOrg()==null || !ct.getOrg().getIdOfOrg().equals(idOfOrg)){
                    ct.setOrg(DAOUtils.getOrgReference(persistenceSession, idOfOrg));
                }

                /**
                 * если для какого-то из документов не совпадает серийный номер, но дата его выдачи более поздняя,
                 * чем указанная в таблице на сервере — обновляем дату выдачи и серийный номер для этого документа.
                 * */
                 if(ct.getVisitor()!=null){
                     /* водительского удостоверения */
                     if(ct.getVisitor().getDriverLicenceNumber()==null){
                         ct.getVisitor().setDriverLicenceDate(visitor.getDriverLicenceDate());
                         ct.getVisitor().setDriverLicenceNumber(visitor.getDriverLicenceNumber());
                     } else {
                         if(!ct.getVisitor().getDriverLicenceNumber().equals(visitor.getDriverLicenceNumber()) &&
                                 ct.getVisitor().getDriverLicenceDate().getTime()<visitor.getDriverLicenceDate().getTime()) {
                             ct.getVisitor().setDriverLicenceDate(visitor.getDriverLicenceDate());
                             ct.getVisitor().setDriverLicenceNumber(visitor.getDriverLicenceNumber());
                         }  else {
                             throw new Exception("Водительского удостоверения  личности не соотвествует владельцу карты");
                         }
                     }

                     /* паспорта */
                     if(ct.getVisitor().getPassportNumber()==null){
                         ct.getVisitor().setPassportDate(visitor.getPassportDate());
                         ct.getVisitor().setPassportNumber(visitor.getPassportNumber());
                     } else {
                         if(!ct.getVisitor().getPassportNumber().equals(visitor.getPassportNumber()) &&
                                 ct.getVisitor().getPassportDate().getTime()<visitor.getPassportDate().getTime()) {
                             ct.getVisitor().setPassportDate(visitor.getPassportDate());
                             ct.getVisitor().setPassportNumber(visitor.getPassportNumber());
                         }  else {
                             throw new Exception("Паспорт личности не соотвествует владельцу карты");
                         }
                     }

                     /* паспорта */
                     if(ct.getVisitor().getWarTicketNumber()==null){
                         ct.getVisitor().setWarTicketDate(visitor.getWarTicketDate());
                         ct.getVisitor().setWarTicketNumber(visitor.getWarTicketNumber());
                     } else {
                         if(!ct.getVisitor().getWarTicketNumber().equals(visitor.getWarTicketNumber()) &&
                                 ct.getVisitor().getWarTicketDate().getTime()<visitor.getWarTicketDate().getTime()) {
                             ct.getVisitor().setWarTicketDate(visitor.getWarTicketDate());
                             ct.getVisitor().setWarTicketNumber(visitor.getWarTicketNumber());
                         }  else {
                             throw new Exception("Военный билет не соотвествует владельцу карты");
                         }
                     }
                     ct.setValidDate(validDate);
                     ct.setCardStation(CardOperationStation.ISSUE);

                 } else {
                     Person person = new Person(visitor.getFirstName(), visitor.getSurname(), visitor.getSecondName());
                     Visitor newVisitor = new Visitor(person);
                     newVisitor.setDriverLicenceDate(visitor.getDriverLicenceDate());
                     newVisitor.setDriverLicenceNumber(visitor.getDriverLicenceNumber());
                     newVisitor.setPassportDate(visitor.getPassportDate());
                     newVisitor.setPassportNumber(visitor.getPassportNumber());
                     newVisitor.setWarTicketDate(visitor.getWarTicketDate());
                     newVisitor.setWarTicketNumber(visitor.getWarTicketNumber());
                     ct.setVisitor(newVisitor);
                     ct.setCardStation(CardOperationStation.ISSUE);
                     ct.setValidDate(validDate);
                 }
                persistenceSession.save(ct);
                idOfVisitor = ct.getVisitor().getIdOfVisitor();
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfVisitor;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "registerTempCard")
    public void registerTempCard(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo, @WebParam(name = "cardPrintedNo") String cardPrintedNo)
            throws FrontControllerException {
        //checkRequestValidity(idOfOrg);
        ///
        try {
            RuntimeContext.getInstance().getCardManager().createTempCard(idOfOrg, cardNo, cardPrintedNo);
        } catch (Exception e) {
            logger.error("Failed registerTempCard", e);
            throw new FrontControllerException(
                    String.format("Ошибка при регистрации времменой карты: %s", e.getMessage()), e);
        }
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
            throw new FrontControllerException(String.format("Ошибка при регистрации карты: %s", e.getMessage()), e);
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
                //ClientManager.ClientFieldConfig fc = ClientDesc.buildClientFieldConfig(cd);
                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                if (cd.contractSurname!=null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, cd.contractSurname);
                }  else {
                    fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, " ");
                }
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
                /* Генерируем GUID клиента при регистрации  */
                fc.setValue(ClientManager.FieldId.CLIENT_GUID, UUID.randomUUID().toString());
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
        if (org==null) throw new FrontControllerException(String.format("Неизвестная организация: %d", orgId));
        PublicKey publicKey = null;
        try {
            publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
        } catch (Exception e) {
            throw new FrontControllerException("Внутренняя ошибка", e);
        }
        if (!publicKey.equals(cert[0].getPublicKey())) throw new FrontControllerException(
                String.format("Ключ сертификата невалиден: %d", orgId));
    }
    
    @WebMethod(operationName = "generateLinkingToken")
    public String generateLinkingToken(@WebParam(name = "orgId") Long orgId, @WebParam(name = "idOfClient") Long idOfClient)
            throws Exception {
        checkRequestValidity(orgId);

        DAOService daoService = DAOService.getInstance();
        Client client = daoService.findClientById(idOfClient);
        if (client==null) {
            throw new FrontControllerException(String.format("Клиент не найден: %d", idOfClient));
        }
        if (!daoService.doesClientBelongToFriendlyOrgs(orgId, idOfClient)) {
            throw new FrontControllerException(String.format("Клиент %d не принадлежит организации", idOfClient));
        }
        LinkingToken linkingToken = daoService.generateLinkingToken(client);
        return linkingToken.getToken();
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
}
