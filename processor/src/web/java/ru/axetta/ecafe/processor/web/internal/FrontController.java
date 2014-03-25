/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.front.items.*;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.security.PublicKey;
import java.util.*;

import static ru.axetta.ecafe.processor.core.persistence.Person.isEmptyFullNameFields;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyDocumentParams;
import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.isDateEqLtCurrentDate;

@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class FrontController extends HttpServlet {
    public static class FrontControllerException extends Exception {

        public String msg;

        public FrontControllerException(String message) {
            super(message);    //To change body of overridden methods use File | Settings | File Templates.
            msg = message;
        }

        public FrontControllerException(String message, Throwable cause) {
            super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
            msg = message;
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

    @WebMethod(operationName = "loadRegistryChangeItems")
    public List<RegistryChangeItem> loadRegistryChangeItems(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "actionFilter") int actionFilter,
            @WebParam(name = "nameFilter") String nameFilter) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        Integer af = actionFilter;
        if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
           actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
           actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
           actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
            af = null;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                                            loadRegistryChangeItems(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "loadRegistryChangeItemsInternal")
    public List<RegistryChangeItem> loadRegistryChangeItemsInternal(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "actionFilter") int actionFilter,
            @WebParam(name = "nameFilter") String nameFilter) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        Integer af = actionFilter;
        if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
                actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
                actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
                actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
            af = null;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeItems(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "refreshRegistryChangeItems")
    public List<RegistryChangeItem> refreshRegistryChangeItems(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsInternal")
    public List<RegistryChangeItem> refreshRegistryChangeItemsInternal(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "proceedRegitryChangeItem")
    /* Если метод возвращает null, значит операция произведена успешно, иначсе это будет сообщение об ошибке */
    public List<RegistryChangeCallback> proceedRegitryChangeItem(@WebParam(name = "changesList") List<Long> changesList,
                                           @WebParam(name = "operation") int operation,
                                           @WebParam(name = "fullNameValidation") boolean fullNameValidation) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }

        try {
            if(changesList != null && changesList.size() > 0) {
                RegistryChange change = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChange(changesList.get(0));
                checkRequestValidity(change.getIdOfOrg());
            }
        } catch (Exception e) {
            logger.error("Failed to pass auth", e);
            //return "При подтверждении изменения из Реестров, произошла ошибка: " + e.getMessage();
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegitryChangeItem(changesList, operation, fullNameValidation);
    }

    @WebMethod(operationName = "proceedRegitryChangeItemInternal")
    /* Если метод возвращает null, значит операция произведена успешно, иначсе это будет сообщение об ошибке */
    public List<RegistryChangeCallback> proceedRegitryChangeItemInternal(@WebParam(name = "changesList") List<Long> changesList,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "fullNameValidation") boolean fullNameValidation) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
            //return "При подтверждении изменения из Реестров, произошла ошибка: " + fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegitryChangeItem(changesList, operation, fullNameValidation);
    }

    @WebMethod(operationName = "loadRegistryChangeRevisions")
    public List<Long> loadRegistryChangeRevisions(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeRevisions(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeRevisionsInternal")
    public List<Long> loadRegistryChangeRevisionsInternal(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeRevisions(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeErrorItems")
    public List<RegistryChangeErrorItem> loadRegistryChangeErrorItems(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeErrorItems(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeErrorItemsInternal")
    public List<RegistryChangeErrorItem> loadRegistryChangeErrorItemsInternal(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeErrorItems(idOfOrg);
    }

    @WebMethod(operationName = "addRegistryChangeError")
    public String addRegistryChangeError(@WebParam(name = "idOfOrg") long idOfOrg,
                                         @WebParam(name = "revisionDate") long revisionDate,
                                         @WebParam(name = "error") String error,
                                         @WebParam(name = "errorDetails") String errorDetails) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                addRegistryChangeError(idOfOrg, revisionDate, error, errorDetails);
    }

    @WebMethod(operationName = "addRegistryChangeErrorInternal")
    public String addRegistryChangeErrorInternal(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "revisionDate") long revisionDate,
            @WebParam(name = "error") String error,
            @WebParam(name = "errorDetails") String errorDetails) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
            return fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                addRegistryChangeError(idOfOrg, revisionDate, error, errorDetails);
    }

    @WebMethod(operationName = "commentRegistryChangeError")
    public String commentRegistryChangeError(@WebParam(name = "idOfRegistryChangeError") long idOfRegistryChangeError,
                                             @WebParam(name = "comment") String comment,
                                             @WebParam(name = "author") String author) {
        RegistryChangeError e = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).getRegistryChangeError(idOfRegistryChangeError);
        try {
            checkRequestValidity(e.getIdOfOrg());
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                commentRegistryChangeError(idOfRegistryChangeError, comment, author);
    }

    @WebMethod(operationName = "commentRegistryChangeErrorInternal")
    public String commentRegistryChangeErrorInternal(@WebParam(name = "idOfRegistryChangeError") long idOfRegistryChangeError,
            @WebParam(name = "comment") String comment,
            @WebParam(name = "author") String author) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
            return fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                commentRegistryChangeError(idOfRegistryChangeError, comment, author);
    }

    /* Выполняет проверку наличия «не нашей customerType=1» карты с физическим идентификатором  cardNo в таблице временных карт. */
    @WebMethod(operationName = "checkVisitorByCard")
    public VisitorItem checkVisitorByCard(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo) throws FrontControllerException{
        checkRequestValidity(idOfOrg);
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
                throw new FrontControllerException("Карта уже зарегистрирована как постоянная на клиента: "+c.getClient().getIdOfClient());
            }

            CardTemp ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае не обнаружения cardNo временной карты в таблице временных    *
             * карт выбрасывать исключение «Карта не зарегистрирована как временная» *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            if (ct == null) {
                throw new FrontControllerException("Карта не зарегистрирована как временная");
            }

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * В случае совпадения cardNo временной карты с cardNo врем. карты клиента в таблице временных карт, *
             * выбрасывать исключение с сообщением «Карта уже зарегистрирована как временная карта клиента»      *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            if(ct.getClient() != null){
                throw new FrontControllerException("Карта уже зарегистрирована как временная карта клиента");
            }

            int type = DAOUtils.extractCardTypeByCartNo(persistenceSession, cardNo);
            if(type==Visitor.DEFAULT_TYPE){
                /**
                 * В случае совпадения id временной карты с id врем. карты клиента системы («нашей карты»)
                 * в таблице временных карт, выбрасывать исключение с сообщением «Карта уже зарегистрирована
                 * как временная карта клиента системы»
                 * */
                 logger.error(ct.toString());
                 throw new FrontControllerException("Карта уже зарегистрирована как временная карта клиента системы");
            } else {
                 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * В случае совпадения cardNo временной карты с cardNo карты посетителя в таблице  *
                 * временных карт - возвращать с процессинга информацию об этом посетителе         *
                 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
                if(ct.getVisitor() != null){
                    visitorItem = new VisitorItem(ct.getVisitor());
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return visitorItem;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    /* возвращающий последнюю операцию по врем. карте */
    @WebMethod(operationName = "getLastTempCardOperation")
    public TempCardOperationItem getLastTempCardOperation(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo) throws FrontControllerException{
        checkRequestValidity(idOfOrg);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        TempCardOperationItem tempCardOperationItem = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            CardTempOperation cardTempOperation = DAOUtils.getLastTempCardOperationByOrgAndCartNo(persistenceSession, idOfOrg, cardNo);
            if(cardTempOperation!=null){
                tempCardOperationItem = new TempCardOperationItem(cardTempOperation);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return tempCardOperationItem;
    }

    /* Выполняет регистрацию временной карты системы customerType=0 */
    @WebMethod(operationName = "registerTempCard")
    public void registerTempCard(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo, @WebParam(name = "cardPrintedNo") String cardPrintedNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        ///
        try {
            RuntimeContext.getInstance().getCardManager().createTempCard(idOfOrg, cardNo, cardPrintedNo);
        } catch (Exception e) {
            logger.error("Failed registerTempCard", e);
            throw new FrontControllerException(
                    String.format("Ошибка при регистрации времменой карты: %s", e.getMessage()), e);
        }
    }


    @WebMethod(operationName = "registerVisitor")
    public Long registerVisitor(@WebParam(name = "orgId")Long idOfOrg, @WebParam(name = "visitor") VisitorItem visitorItem) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Long idOfVisitor = -1L;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            /**
             * Если хотя бы одно из полей имени == null выбрасывать исключение с
             * сообщением «все поля ФИО должны быть заполнены»
             * */
            if(isEmptyFullNameFields(visitorItem.getFirstName(), visitorItem.getSurname(), visitorItem.getSecondName())) {
                throw new  FrontControllerException("Все поля ФИО должны быть заполнены");
            }

            /**
            * Если поле-массив PersonDocuments не содержит ни одного описания удостоверния личности,
            * выбрасывать исключение с сообщением «Отсутствует информация об удостоверении личности»
            * */
            if(isEmptyDocumentParams(visitorItem.getDriverLicenceNumber(), visitorItem.getDriverLicenceDate()) &&
                    isEmptyDocumentParams(visitorItem.getPassportNumber(), visitorItem.getPassportDate()) &&
                    isEmptyDocumentParams(visitorItem.getWarTicketNumber(), visitorItem.getWarTicketDate())
                    ) {
                throw new  FrontControllerException("Отсутствует информация об удостоверении личности");
            }

            /**
            * Если в  поле-массиве PersonDocuments у какого-либо документа для даты выдачи
            * будет указана еще не наступившая дата, выбрасывать исключение с сообщением «Неверная дата выдачи документа»
            * */
            if( isDateEqLtCurrentDate(visitorItem.getDriverLicenceDate()) ||
                    isDateEqLtCurrentDate(visitorItem.getPassportDate()) ||
                    isDateEqLtCurrentDate(visitorItem.getWarTicketDate())){
                throw new FrontControllerException("Неверное значение даты окончания действия карты");
            }

            if(visitorItem.getIdOfVisitor()==null){
                Person person = new Person(visitorItem.getFirstName(), visitorItem.getSurname(), visitorItem.getSecondName());
                persistenceSession.save(person);
                Visitor visitor = new Visitor(person);
                visitor.setPassportNumber(visitorItem.getPassportNumber());
                visitor.setPassportDate(visitorItem.getPassportDate());
                visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber());
                visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
                visitor.setWarTicketNumber(visitorItem.getWarTicketNumber());
                visitor.setWarTicketDate(visitorItem.getWarTicketDate());
                visitor.setVisitorType(Visitor.DEFAULT_TYPE);
                persistenceSession.save(visitor);
                idOfVisitor = visitor.getIdOfVisitor();
            } else {
                Visitor visitor = DAOUtils.findVisitorById(persistenceSession,visitorItem.getIdOfVisitor());
                if(visitor==null){
                    throw new FrontControllerException("Посетитель не найден");
                } else {
                    Person person = visitor.getPerson();
                    person.setFirstName(visitorItem.getFirstName());
                    person.setSecondName(visitorItem.getSecondName());
                    person.setSurname(visitorItem.getSurname());
                    persistenceSession.save(person);
                    visitor.setPassportNumber(visitorItem.getPassportNumber());
                    visitor.setPassportDate(visitorItem.getPassportDate());
                    visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber());
                    visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
                    visitor.setWarTicketNumber(visitorItem.getWarTicketNumber());
                    visitor.setWarTicketDate(visitorItem.getWarTicketDate());
                    if(visitor.getVisitorType()==null){
                        visitor.setVisitorType(Visitor.DEFAULT_TYPE);
                    }
                    persistenceSession.save(visitor);
                    idOfVisitor = visitor.getIdOfVisitor();
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfVisitor;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    /* Выполняет регистрацию врем. карты посетителя customerType=1. */
    @WebMethod(operationName = "registerVisitorTempCard")
    public void registerVisitorTempCard(@WebParam(name = "orgId") Long idOfOrg, @WebParam(name = "idOfVisitor") Long idOfVisitor, @WebParam(name = "cardNo") Long cardNo)
            throws FrontControllerException{
        checkRequestValidity(idOfOrg);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            /**
             * Если отсутствует посетитель с таким id, выбрасывать исключение «Посетитель не зарегистрирован»
             * */
            Visitor visitor = DAOUtils.findVisitorById(persistenceSession, idOfVisitor);
            if(visitor==null){
                throw new FrontControllerException(String.format("Посетитель не зарегистрирован"));
            }

            Card card = DAOUtils.findCardByCardNo(persistenceSession, cardNo);

            /**
             * Если id карты совпадает с идентификатором постоянной карты из таблицы постоянных карт —
             * возвращать ошибку с сообщением «карта уже зарегистрирована как постоянная»
             * */
            if(card!=null){
                throw new FrontControllerException(String.format("Карта уже зарегистрирована как постоянная"));
            }

            CardTemp cardTemp = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);

            if(cardTemp==null){
                //cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), ClientTypeEnum.VISITOR);
                cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), 1);
                cardTemp.setVisitor(visitor);
                persistenceSession.save(cardTemp);
            } else {
                /**
                 * Если id карты совпадает с идентификатором временной карты и карта является временной картой системы
                 * («наша карта»), то выбрасывать исключение «карта уже зарегистрирована как временная»
                 * */
                //if(cardTemp.getClientTypeEnum() == ClientTypeEnum.CLIENT){
                if(cardTemp.getVisitorType() == 0){
                    throw new FrontControllerException(String.format("карта уже зарегистрирована как временная"));
                } else {
                    if(cardTemp.getVisitor()==null){
                        /**
                         * Если посетитель уже зарегистрирован, но временной карты у него нет —
                         * регистрируем временную карту с идентификатором  idOfTempCard
                         * */
                        cardTemp.setVisitor(visitor);
                        //cardTemp.setClientTypeEnum(ClientTypeEnum.VISITOR);
                        cardTemp.setVisitorType(1);
                        persistenceSession.save(cardTemp);
                    } else {
                        /**
                         * Если id карты совпадает с идентификатором временной карты и карта является временной картой посетителя
                         * («не наша карта»), но id посетителя не совпадает с параметром  idOfVisitor, выбрасывать исключение
                         * «Карта зарегистрирована на другого посетителя».
                         * */
                        if(!cardTemp.getVisitor().equals(visitor)){
                            throw new FrontControllerException(String.format("Карта зарегистрирована на другого посетителя"));
                        }
                    }
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при регистрацию временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
                    validTime, Card.ISSUED_LIFE_STATE, "", issuedTime, cardPrintedNo);
        } catch (Exception e) {
            logger.error("Failed registerCard", e);
            throw new FrontControllerException(String.format("Ошибка при регистрации карты: %s", e.getMessage()), e);
        }
    }

    @WebMethod(operationName = "changeCardOwner")
    public void changeCardOwner(@WebParam(name = "orgId") Long orgId,@WebParam(name = "newOwnerId") Long newOwnerId,
            @WebParam(name = "cardNo") Long cardNo, @WebParam(name = "changeTime") Date changeTime,
            @WebParam(name = "validTime") Date validTime) throws FrontControllerException{
        checkRequestValidity(orgId);
        ///
        try {
            RuntimeContext.getInstance().getCardManager().changeCardOwner(newOwnerId, cardNo, changeTime, validTime);
        } catch (Exception e) {
            logger.error("Failed changeCardOwner", e);
            throw new FrontControllerException(String.format("Ошибка при смене владельца карты: %s", e.getMessage()), e);
        }
    }


    @WebMethod(operationName = "registerClients")
    public List<RegisterClientResult> registerClients(@WebParam(name = "orgId")Long orgId,
            @WebParam(name = "clientDescList") List<ClientDesc> clientDescList, @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        LinkedList<RegisterClientResult> results = new LinkedList<RegisterClientResult>();
        for (ClientDesc cd : clientDescList) {
            try {
                //ClientManager.ClientFieldConfig fc = ClientDesc.buildClientFieldConfig(cd);
                logger.debug("create FieldConfig");
                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                logger.debug("check client params");
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
                logger.debug("register client");
                long idOfClient = ClientManager.registerClient(orgId, fc, checkFullNameUniqueness);
                results.add(new RegisterClientResult(idOfClient, cd.recId, true, null));
            } catch (Exception e) {
                results.add(new RegisterClientResult(null, cd.recId, false, e.getMessage()));
            }
        }
        return results;
    }

    protected void checkIpValidity() throws FrontControllerException {
        MessageContext msgContext = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
        String ipPattern = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_FRON_CONTROLLER_REQ_IP_MASK);
        String remoteIp = request.getRemoteAddr();
        if(!remoteIp.matches(ipPattern)) {
            throw new FrontControllerException("Запрос с входящего узла обязан проходить проходить проверку сертификатов");
        }
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