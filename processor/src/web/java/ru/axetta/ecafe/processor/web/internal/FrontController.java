/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardNotFoundException;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardUidGivenAwayException;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardWrongStateException;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.ImportMigrantsService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterSpbClientsService;
import ru.axetta.ecafe.processor.core.service.RegistryChangeCallback;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.VersionUtils;
import ru.axetta.ecafe.processor.web.internal.front.items.*;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.persistence.Person.isEmptyFullNameFields;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyDocumentParams;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyFreeDocumentParams;
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
        if(RuntimeContext.RegistryType.isMsk()) {
            if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if(RuntimeContext.RegistryType.isSpb()) {
            if(actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
                af = null;
            }
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                                            loadRegistryChangeItems(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "loadRegistryChangeItemsV2")
    public List<RegistryChangeItemV2> loadRegistryChangeItemsV2(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "actionFilter") int actionFilter,
            @WebParam(name = "nameFilter") String nameFilter) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        Integer af = actionFilter;
        if(RuntimeContext.RegistryType.isMsk()) {
            if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if(RuntimeContext.RegistryType.isSpb()) {
            if(actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
                af = null;
            }
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeItemsV2(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "loadRegistryChangeEmployeeItemsV2")
    public List<RegistryChangeItemV2> loadRegistryChangeEmployeeItemsV2(@WebParam(name = "idOfOrg") long idOfOrg,
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
                loadRegistryChangeItemsEmployeeV2(idOfOrg, revisionDate, af, nameFilter);
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
        if(RuntimeContext.RegistryType.isMsk()) {
            if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if(RuntimeContext.RegistryType.isSpb()) {
            if(actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
                af = null;
            }
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeItems(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "loadRegistryChangeItemsInternalV2")
    public List<RegistryChangeItemV2> loadRegistryChangeItemsInternalV2(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "actionFilter") int actionFilter,
            @WebParam(name = "nameFilter") String nameFilter) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        Integer af = actionFilter;
        if(RuntimeContext.RegistryType.isMsk()) {
            if(actionFilter != ImportRegisterClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if(RuntimeContext.RegistryType.isSpb()) {
            if(actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION &&
                    actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
                af = null;
            }
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeItemsV2(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "refreshRegistryChangeEmployeeItems")
    public List<RegistryChangeItem> refreshRegistryChangeEmployeeItems(@WebParam(name = "idOfOrg") long idOfOrg) throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeEmployeeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItems")
    public List<RegistryChangeItem> refreshRegistryChangeItems(@WebParam(name = "idOfOrg") long idOfOrg) throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsV2")
    public List<RegistryChangeItemV2> refreshRegistryChangeItemsV2(@WebParam(name = "idOfOrg") long idOfOrg) throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItemsV2(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsInternal")
    public List<RegistryChangeItem> refreshRegistryChangeItemsInternal(@WebParam(name = "idOfOrg") long idOfOrg) throws Exception {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsInternalV2")
    public List<RegistryChangeItemV2> refreshRegistryChangeItemsInternalV2(@WebParam(name = "idOfOrg") long idOfOrg) throws Exception {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItemsV2(idOfOrg);
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
                if(RuntimeContext.RegistryType.isMsk()) {
                    RegistryChange change = RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class).getRegistryChange(changesList.get(0));
                    checkRequestValidity(change.getIdOfOrg());
                } else if(RuntimeContext.RegistryType.isSpb()) {
                    RegistryChange change = RuntimeContext.getAppContext().getBean(ImportRegisterSpbClientsService.class).getRegistryChange(changesList.get(0));
                    checkRequestValidity(change.getIdOfOrg());
                }
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

    @WebMethod(operationName = "proceedRegitryChangeEmployeeItem")
    /* Если метод возвращает null, значит операция произведена успешно, иначсе это будет сообщение об ошибке */
    public List<RegistryChangeCallback> proceedRegitryChangeEmployeeItem(@WebParam(name = "changesList") List<Long> changesList,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "fullNameValidation") boolean fullNameValidation,
            @WebParam(name = "groupName") String groupName) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegitryEmployeeChangeItem(changesList, operation, fullNameValidation, groupName);
    }

    @WebMethod(operationName = "loadRegistryChangeRevisions")
    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisions(@WebParam(name = "idOfOrg") long idOfOrg) {
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
    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisionsInternal(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeRevisions(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeEmployeeRevisions")
    public List<RegistryChangeRevisionItem> loadRegistryChangeEmployeeRevisions(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryEmployeeChangeRevisions(idOfOrg);
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

    @WebMethod(operationName = "loadRegistryChangeEmployeeErrorItems")
    public List<RegistryChangeErrorItem> loadRegistryChangeEmployeeErrorItems(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch(FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeEmployeeErrorItems(idOfOrg);
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
        RegistryChangeError e = null;
        if(RuntimeContext.RegistryType.isMsk()) {
            e = RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class).getRegistryChangeError(idOfRegistryChangeError);
        } else if(RuntimeContext.RegistryType.isSpb()) {
            e = RuntimeContext.getAppContext().getBean(ImportRegisterSpbClientsService.class).getRegistryChangeError(idOfRegistryChangeError);
        }
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

    @WebMethod(operationName = "loadRegistryChangeGuardians")
    public List<RegistryChangeGuardianItem> loadRegistryChangeGuardians(@WebParam(name = "idOfRegistryChange") long idOfRegistryChange) throws FrontControllerException {
        List<RegistryChangeGuardianItem> items = new ArrayList<RegistryChangeGuardianItem>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(RegistryChangeGuardians.class);
            criteria.add(Restrictions.eq("registryChange.idOfRegistryChange", idOfRegistryChange));
            List<RegistryChangeGuardians> list = criteria.list();
            for (RegistryChangeGuardians change : list) {
                RegistryChangeGuardianItem item = new RegistryChangeGuardianItem();
                item.setFio(change.getFamilyName() + " " + change.getFirstName() + " " + change.getSecondName());
                item.setGuardianType(change.getRelationship());
                item.setLegalRepresent(change.getLegal_representative());
                item.setPhone(change.getPhoneNumber());
                items.add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.info("Ошибка при регистрацию посетителя и временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        return items;
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
            logger.info("Ошибка при регистрацию посетителя и временной карты посетителя", e);
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
            logger.info("Ошибка при регистрацию посетителя и временной карты посетителя", e);
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

    /* Метод возвращает номер, напечатанный на новой карте, по номеру чипа карты */
    @WebMethod(operationName = "getCardPrintedNoByCardNo")
    public CardPrintedNoItem getCardPrintedNoByCardNo(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        try {
            CardManager.NewCardItem newCardItem = RuntimeContext.getInstance().getCardManager().getNewCardPrintedNo(cardNo);
            if (newCardItem == null) {
                throw new Exception(String.format("Карта с номером чипа '%s' не найдена",cardNo));
            }
            if (newCardItem.getCardPrintedNo() == null){
                throw new Exception("Номер на карте не найден");
            }
            return new CardPrintedNoItem(newCardItem.getCardPrintedNo(), newCardItem.getCardType());
        } catch (Exception e) {
            logger.error("Ошибка при запросе номера на карте по номеру чипа", e);
            throw new FrontControllerException(
                    String.format("Ошибка при запросе номера на карте по номеру чипа: %s", e.getMessage()), e);
        }
    }

    /* Метод возвращает данные опекуна и его опекаемых по номеру чипа карты, если владелец карты опекун
    * Возвращает данные учащегося и его опекаемых по номеру чипа карты, если владелец карты учащийся
    * */
    @WebMethod(operationName = "getGuardiansAndChildsByCard")
    public List<GuardianAndChildItem> getGuardiansAndChildsByCard(@WebParam(name = "orgId") Long idOfOrg,@WebParam(name = "cardNo") Long cardNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        List<GuardianAndChildItem> result = new ArrayList<GuardianAndChildItem>();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            if (c == null) {
                CardTemp ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
                if(ct != null){
                    throw new FrontControllerException("Карта зарегистрирована как временная");
                }
                throw new FrontControllerException("Карта не найдена");
            } else {
                if(c.getClient() == null){
                    throw new FrontControllerException("Карта не зарегистрирована на клиента");
                }
                if(!c.getState().equals(Card.ACTIVE_STATE)){
                    throw new FrontControllerException("Карта заблокирована");
                }
                if(!c.getLifeState().equals(Card.READY_LIFE_STATE)){
                    throw new FrontControllerException("Карта не активна");
                }
            }

            // Находим опекуна, его опекаемых и опекунов опекаемых
            Client clientByCard = c.getClient();
            GuardianAndChildItem clientByCardItem = new GuardianAndChildItem(clientByCard.getIdOfClient(), clientByCard.getOrg().getIdOfOrg(),
                    clientByCard.getPerson().getFullName());
            if (!clientByCard.isDeletedOrLeaving()) {
                result.add(clientByCardItem);
            }

            List<Client> childsList = ClientManager.findChildsByClient(persistenceSession, clientByCard.getIdOfClient());
            if(childsList.size() > 0) {
                List<GuardianAndChildItem> childItemList = new ArrayList<GuardianAndChildItem>();
                List<GuardianAndChildItem> guardiansItemList = new ArrayList<GuardianAndChildItem>();
                for (Client client : childsList) {
                    if (client.isDeletedOrLeaving()) continue;
                    GuardianAndChildItem clientItem = new GuardianAndChildItem(client.getIdOfClient(),
                            client.getOrg().getIdOfOrg(), client.getPerson().getFullName());
                    clientByCardItem.getIdOfChildren().add(client.getIdOfClient());
                    List<Client> guardians = ClientManager
                            .findGuardiansByClient(persistenceSession, client.getIdOfClient());
                    childItemList.add(clientItem);
                    if (guardians != null && guardians.size() > 0) {
                        for (Client g : guardians) {
                            if (g.isDeletedOrLeaving()) continue;
                            GuardianAndChildItem gItem = new GuardianAndChildItem(g.getIdOfClient(),
                                    g.getOrg().getIdOfOrg(), g.getPerson().getFullName());
                            clientItem.getIdOfGuardian().add(g.getIdOfClient());
                            guardiansItemList.add(gItem);
                        }
                    }
                }
                result.addAll(childItemList);
                result.addAll(guardiansItemList);
            } else {
                List<Client> guardiansList = ClientManager.findGuardiansByClient(persistenceSession, clientByCard.getIdOfClient());
                if(guardiansList.size() > 0) {
                    List<GuardianAndChildItem> guardiansItemList = new ArrayList<GuardianAndChildItem>();
                    for (Client g : guardiansList) {
                        GuardianAndChildItem gItem = new GuardianAndChildItem(g.getIdOfClient(), g.getOrg().getIdOfOrg(), g.getPerson().getFullName());
                        clientByCardItem.getIdOfGuardian().add(g.getIdOfClient());
                        guardiansItemList.add(gItem);
                    }
                    result.addAll(guardiansItemList);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } catch (Exception e) {
            logger.error("Ошибка при запросе опекуна и опекаемых по номеру карты", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    /* Создание заявок на посещение */
    @WebMethod(operationName = "createMigrateRequests")
    public void createMigrateRequests(@WebParam(name = "orgId") Long idOfOrg, @WebParam(name = "rqs") List<MigrateRequest> rqs)
            throws FrontControllerException{
        checkRequestValidity(idOfOrg);
        Date date = new Date();
        Date after5Seconds = CalendarUtils.addSeconds(date, 5);
        String resolConfirmed = "Заявка одобрена в организации посещения";
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Map<Long, List<MigrateRequest>> map = MigrateRequest.sortMigrateRequestsByOrg(persistenceSession, rqs);

           Org orgVisit = (Org) persistenceSession.load(Org.class, idOfOrg);

            for(Long idOfOrgRegistry : map.keySet()) {

                String requestNumber = null;

                for (MigrateRequest migrateRequest : map.get(idOfOrgRegistry)) {
                    Client client = (Client) persistenceSession.load(Client.class, migrateRequest.getMigrateClientId());

                    Client clientResol = (Client) persistenceSession.load(Client.class, migrateRequest.getIdOfClientResol());
                    if (clientResol == null) {
                        throw new FrontControllerException("Клиент-оператор с id=" + migrateRequest.getIdOfClientResol() + " найден");
                    }
                    migrateRequest.validateMigrateRequest();
                    Long idOfProcessorMigrantRequest = MigrantsUtils
                            .nextIdOfProcessorMigrantRequest(persistenceSession, idOfOrgRegistry);
                    CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(idOfProcessorMigrantRequest, idOfOrgRegistry);
                    if (requestNumber == null) {
                        requestNumber = MigrateRequest.formRequestNumber(client.getOrg().getIdOfOrg(), orgVisit.getIdOfOrg(),
                                idOfProcessorMigrantRequest, date);
                    }
                    Migrant migrant = new Migrant(compositeIdOfMigrant, client.getOrg().getDefaultSupplier(),
                            requestNumber, client, orgVisit, migrateRequest.getStartDate(), migrateRequest.getEndDate(), Migrant.SYNCHRONIZED);

                    Long idOfResol = MigrantsUtils.nextIdOfProcessorMigrantResolutions(persistenceSession, idOfOrgRegistry);
                    CompositeIdOfVisitReqResolutionHist comIdOfHist = new CompositeIdOfVisitReqResolutionHist(idOfResol,
                            migrant.getCompositeIdOfMigrant().getIdOfRequest(), idOfOrgRegistry);
                    VisitReqResolutionHist visitReqResolutionHist = new VisitReqResolutionHist(comIdOfHist, client.getOrg(),
                            VisitReqResolutionHist.RES_CREATED, date, migrateRequest.getResolutionCause(), clientResol,
                            migrateRequest.getContactInfo(), VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);

                    Long idOfResol1 = MigrantsUtils.nextIdOfProcessorMigrantResolutions(persistenceSession, idOfOrg)-1;
                    CompositeIdOfVisitReqResolutionHist comIdOfHist1 = new CompositeIdOfVisitReqResolutionHist(idOfResol1,
                            migrant.getCompositeIdOfMigrant().getIdOfRequest(), idOfOrg);
                    VisitReqResolutionHist visitReqResolutionHist1 = new VisitReqResolutionHist(comIdOfHist1, client.getOrg(),
                            VisitReqResolutionHist.RES_CONFIRMED, after5Seconds, resolConfirmed, null,
                            null, VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);
                    persistenceSession.save(migrant);
                    persistenceSession.save(visitReqResolutionHist);
                    persistenceSession.save(visitReqResolutionHist1);
                    persistenceSession.flush();
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при создании заявок на временное посещение", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            if (isEmptyDocumentParams(visitorItem.getDriverLicenceNumber(), visitorItem.getDriverLicenceDate()) &&
                    isEmptyDocumentParams(visitorItem.getPassportNumber(), visitorItem.getPassportDate()) &&
                    isEmptyDocumentParams(visitorItem.getWarTicketNumber(), visitorItem.getWarTicketDate())) {

                if (isEmptyFreeDocumentParams(visitorItem.getFreeDocName(), visitorItem.getFreeDocNumber(),
                        visitorItem.getFreeDocDate())) {
                    throw new FrontControllerException("Отсутствует информация об удостоверении личности");
                }
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
                visitor.setFreeDocName(visitorItem.getFreeDocName());
                visitor.setFreeDocNumber(visitorItem.getFreeDocNumber());
                visitor.setFreeDocDate(visitorItem.getFreeDocDate());
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
                    visitor.setFreeDocName(visitorItem.getFreeDocName());
                    visitor.setFreeDocNumber(visitorItem.getFreeDocNumber());
                    visitor.setFreeDocDate(visitorItem.getFreeDocDate());
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
            logger.info("Ошибка при регистрацию посетителя и временной карты посетителя", e);
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

            Org org = OrgReadOnlyRepository.getInstance().find(idOfOrg);
            if(org == null){
                throw new FrontControllerException(String.format("Организация не найдена"));
            }

            CardTemp cardTemp = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);

            if(cardTemp==null){
                //cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), ClientTypeEnum.VISITOR);
                cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), 1);
                cardTemp.setOrg(org);
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
            return RuntimeContext.getInstance().getCardManager()
                    .createCard(clientId, cardNo, cardType, Card.ACTIVE_STATE, validTime, Card.ISSUED_LIFE_STATE, "", issuedTime, cardPrintedNo);
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
            RuntimeContext.getInstance().getCardManager()
                    .changeCardOwner(newOwnerId, cardNo, changeTime, validTime);
        } catch (Exception e) {
            logger.error("Failed changeCardOwner", e);
            throw new FrontControllerException(String.format("Ошибка при смене владельца карты: %s", e.getMessage()), e);
        }
    }

    @WebMethod(operationName = "registerClientsV2")
    public List<RegisterClientResult> registerClientsV2(@WebParam(name = "orgId")Long orgId,
            @WebParam(name = "clientDescList") List<ClientDescV2> clientDescList, @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        boolean isExistsOrgByIdAndTags; // = DAOService.getInstance().existsOrgByIdAndTags(orgId, "БЛОК_РЕГ_УЧ");
        String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
        String notifyByEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";

        ArrayList<RegisterClientResult> results = new ArrayList<RegisterClientResult>();
        String recIdStr = null;
        Integer recId = null;
        for (ClientDescV2 cd : clientDescList) {
            try {
                logger.debug("create FieldConfig v2");
                recIdStr = getClientParamDescValueByName("recId", cd.getClientDescParams().getParam());
                if (recIdStr == null) {
                    throw new FrontControllerException("Не найден обязательный параметр recId");
                }
                String group = getClientParamDescValueByName("group", cd.getClientDescParams().getParam());
                recId = Integer.parseInt(recIdStr);
                if (group == null) {
                    throw new FrontControllerException("Не найден обязательный параметр group");
                }

                String orgIdForClient = getClientParamDescValueByName("orgId", cd.getClientDescParams().getParam());
                if (orgIdForClient == null) {
                    throw new FrontControllerException("Не найден обязательный параметр orgId");
                }
                isExistsOrgByIdAndTags = DAOService.getInstance().existsOrgByIdAndTags(Long.parseLong(orgIdForClient), "БЛОК_РЕГ_УЧ");
                if(isExistsOrgByIdAndTags && !ClientGroup.predefinedGroupNames().contains(group)){
                    throw new FrontControllerException("Запрещена регистрация учащихся, используйте синхронизацию с Реестрами");
                }

                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                logger.debug("check client params v2");

                String contractSurname = getClientParamDescValueByName("contractSurname", cd.getClientDescParams().getParam());
                String contractName = getClientParamDescValueByName("contractName", cd.getClientDescParams().getParam());
                String contractSecondName = getClientParamDescValueByName("contractSecondName", cd.getClientDescParams().getParam());
                String contractDoc = getClientParamDescValueByName("contractDoc", cd.getClientDescParams().getParam());
                String surname = getClientParamDescValueByName("surname", cd.getClientDescParams().getParam());
                String name = getClientParamDescValueByName("name", cd.getClientDescParams().getParam());
                String secondName = getClientParamDescValueByName("secondName", cd.getClientDescParams().getParam());
                String doc = getClientParamDescValueByName("doc", cd.getClientDescParams().getParam());
                String address = getClientParamDescValueByName("address", cd.getClientDescParams().getParam());
                String phone = getClientParamDescValueByName("phone", cd.getClientDescParams().getParam());
                String mobilePhone = getClientParamDescValueByName("mobilePhone", cd.getClientDescParams().getParam());
                String email = getClientParamDescValueByName("email", cd.getClientDescParams().getParam());
                String notifyBySms = getClientParamDescValueByName("notifyBySms", cd.getClientDescParams().getParam());
                String comments = getClientParamDescValueByName("comments", cd.getClientDescParams().getParam());
                String cardNo = getClientParamDescValueByName("cardNo", cd.getClientDescParams().getParam());
                String cardPrintedNo = getClientParamDescValueByName("cardPrintedNo", cd.getClientDescParams().getParam());
                String cardType = getClientParamDescValueByName("cardType", cd.getClientDescParams().getParam());
                String snils = getClientParamDescValueByName("snils", cd.getClientDescParams().getParam());
                String cardExpiry = getClientParamDescValueByName("cardExpiry", cd.getClientDescParams().getParam());
                String cardIssued = getClientParamDescValueByName("cardIssued", cd.getClientDescParams().getParam());
                String birthDate = getClientParamDescValueByName("birthDate", cd.getClientDescParams().getParam());
                String gender = getClientParamDescValueByName("gender", cd.getClientDescParams().getParam());
                String middleGroup = getClientParamDescValueByName("middleGroup", cd.getClientDescParams().getParam());

                fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, contractSurname == null ? " " : contractSurname);
                if (contractName!=null) fc.setValue(ClientManager.FieldId.CONTRACT_NAME, contractName);
                fc.setValue(ClientManager.FieldId.CONTRACT_SECONDNAME, contractSecondName == null ? "" : contractSecondName);
                if (contractDoc!=null) fc.setValue(ClientManager.FieldId.CONTRACT_DOC, contractDoc);
                if (surname!=null) fc.setValue(ClientManager.FieldId.SURNAME, surname);
                if (name!=null) fc.setValue(ClientManager.FieldId.NAME, name);
                fc.setValue(ClientManager.FieldId.SECONDNAME, secondName == null ? "" : secondName);
                if (doc!=null) fc.setValue(ClientManager.FieldId.DOC, doc);
                if (address!=null) fc.setValue(ClientManager.FieldId.ADDRESS, address);
                if (phone!=null) fc.setValue(ClientManager.FieldId.PHONE, phone);
                if (mobilePhone!=null) fc.setValue(ClientManager.FieldId.MOBILE_PHONE, mobilePhone);
                if (email!=null) fc.setValue(ClientManager.FieldId.EMAIL, email);
                if (group!=null) fc.setValue(ClientManager.FieldId.GROUP, group);
                if (notifyBySms!=null) fc.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, notifyBySms);
                if (notifyByEmail!=null) fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                if (notifyByPush!=null) fc.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                if (comments!=null) fc.setValue(ClientManager.FieldId.COMMENTS, comments);
                if (cardNo!=null) fc.setValue(ClientManager.FieldId.CARD_ID, cardNo);
                if (cardPrintedNo!=null) fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cardPrintedNo);
                try {
                    if (cardType!=null) fc.setValue(ClientManager.FieldId.CARD_TYPE, Integer.parseInt(cardType));
                } catch (Exception e) {
                    if (!cardType.equals("")) {
                        throw new FrontControllerException("Неправильный формат поля cardType");
                    }
                }
                if (cardExpiry!=null) fc.setValue(ClientManager.FieldId.CARD_EXPIRY, CalendarUtils.parseDate(cardExpiry));
                if (cardIssued!=null) fc.setValue(ClientManager.FieldId.CARD_ISSUED, CalendarUtils.parseDate(cardIssued));
                if (snils!=null) fc.setValue(ClientManager.FieldId.SAN, snils);
                if (birthDate!=null) fc.setValue(ClientManager.FieldId.BIRTH_DATE, birthDate);
                if (gender!=null) fc.setValue(ClientManager.FieldId.GENDER, gender);
                if (middleGroup!=null) fc.setValue(ClientManager.FieldId.MIDDLE_GROUP, middleGroup);

                logger.debug("register client v2");
                boolean noComment = true;
                long idOfClient = ClientManager.registerClient(Long.parseLong(orgIdForClient), fc, checkFullNameUniqueness, noComment);
                results.add(new RegisterClientResult(idOfClient, recId, true, null));
            } catch(Exception e) {
                results.add(new RegisterClientResult(null, recId, false, e.getMessage()));
            }
        }
        return results;
    }

    private String getClientParamDescValueByName(String paramName, List<ClientDescV2.ClientDescItemParam> params) {
        for(ClientDescV2.ClientDescItemParam param : params) {
            if (param.paramName.equalsIgnoreCase(paramName)) {
                return param.paramValue;
            }
        }
        return null;
    }

    @WebMethod(operationName = "registerClients")
    public List<RegisterClientResult> registerClients(@WebParam(name = "orgId")Long orgId,
            @WebParam(name = "clientDescList") List<ClientDesc> clientDescList, @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        boolean isExistsOrgByIdAndTags = DAOService.getInstance().existsOrgByIdAndTags(orgId, "БЛОК_РЕГ_УЧ");
        String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
        String notifyByEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";

        LinkedList<RegisterClientResult> results = new LinkedList<RegisterClientResult>();
        for (ClientDesc cd : clientDescList) {
            try {
                //ClientManager.ClientFieldConfig fc = ClientDesc.buildClientFieldConfig(cd);
                logger.debug("create FieldConfig");
                if(isExistsOrgByIdAndTags && !ClientGroup.predefinedGroupNames().contains(cd.group)){
                    throw new FrontControllerException("Запрещена регистрация учащихся, используйте синхронизацию с Реестрами");
                }
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
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                if (cd.comments!=null) fc.setValue(ClientManager.FieldId.COMMENTS, cd.comments);
                if (cd.cardNo!=null) fc.setValue(ClientManager.FieldId.CARD_ID, cd.cardNo);
                if (cd.cardPrintedNo!=null) fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cd.cardPrintedNo);
                fc.setValue(ClientManager.FieldId.CARD_TYPE, cd.cardType);
                if (cd.cardExpiry!=null) fc.setValue(ClientManager.FieldId.CARD_EXPIRY, cd.cardExpiry);
                if (cd.cardIssued!=null) fc.setValue(ClientManager.FieldId.CARD_ISSUED, cd.cardIssued);
                if (cd.snils!=null) fc.setValue(ClientManager.FieldId.SAN, cd.snils);
                logger.debug("register client");
                long idOfClient = ClientManager.registerClient(orgId, fc, checkFullNameUniqueness, false);
                results.add(new RegisterClientResult(idOfClient, cd.recId, true, null));
            } catch (Exception e) {
                results.add(new RegisterClientResult(null, cd.recId, false, e.getMessage()));
            }
        }
        return results;
    }

    @WebMethod(operationName = "getFriendlyOrganizations")
    public List<SimpleOrganizationItem> getFriendlyOrganizations(@WebParam(name = "orgId")Long orgId)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        List<SimpleOrganizationItem> result = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            result =  new LinkedList<SimpleOrganizationItem>();
            List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistenceSession, orgId);
            for (Org item : friendlyOrgs) {
                result.add(new SimpleOrganizationItem(
                        item.getIdOfOrg(),
                        item.getShortName(),
                        item.getType().ordinal()));
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при регистрации посетителя и временной карты посетителя", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
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
        if (RuntimeContext.getInstance().isTestMode()){
            return;
        }

        MessageContext msgContext = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
        X509Certificate[] cert = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        //X509Certificate cert = (X509Certificate)((WSSecurityEngineResult)wsContext.getMessageContext().get(WSS4JInInterceptor.SIGNATURE_RESULT)).get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);

        if (cert==null || cert.length==0) throw new FrontControllerException("В запросе нет валидных сертификатов, idOfOrg: " + orgId);
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

    @WebMethod(operationName = "registerCardWithoutClient")
    public CardResponseItem registerCardWithoutClient(@WebParam(name = "orgId") long idOfOrg,
            @WebParam(name = "cardNo") long cardNo, @WebParam(name = "cardPrintedNo") long cardPrintedNo,
            @WebParam(name = "type") int type, @WebParam(name = "cardSignVerifyRes") Integer cardSignVerifyRes,
            @WebParam(name = "cardSignCertNum") Integer cardSignCertNum, @WebParam(name = "isLongUid") boolean isLongUid)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        logger.info(String.format("Incoming registerCardWithoutClient request. orgId=%s, cardNo=%s, cardPrintedNo=%s, type=%s, cardSignVerifyRes=%s, cardSighCertNum=%s, isLongUid=%s",
                idOfOrg, cardNo, cardPrintedNo, type, cardSignVerifyRes, cardSignCertNum, isLongUid));
        CardService cardService = CardService.getInstance();
        if (!(type >=0 && type < Card.TYPE_NAMES.length)) {
            return new CardResponseItem(CardResponseItem.ERROR_INVALID_TYPE, CardResponseItem.ERROR_INVALID_TYPE_MESSAGE);
        }
        Card card;
        Long idOfCard;
        CardTransitionState transitionState = null;
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try{
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Card exCard = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            if (null == exCard) {
                card = cardService.registerNew(idOfOrg, cardNo, cardPrintedNo, type, cardSignVerifyRes, cardSignCertNum,
                        isLongUid);
                idOfCard = card.getIdOfCard();
                transitionState = CardTransitionState.fromInteger(card.getTransitionState());
            } else {
                if (VersionUtils.compareClientVersionForRegisterCard(persistenceSession, idOfOrg) < 0) {
                    throw new CardResponseItem.CardAlreadyExist(CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE);
                }

                if (CardState.BLOCKED.getValue() != exCard.getState()) {
                    throw new CardResponseItem.CardAlreadyExist(CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE);
                } else {
                    Integer blockPeriod = runtimeContext.getPropertiesValue("ecafe.processor.card.registration.block.period", 180);
                    Date now = new Date();
                    if (blockPeriod >= CalendarUtils.getDifferenceInDays(exCard.getUpdateTime(), now)) {
                        throw new CardResponseItem.CardAlreadyExist(String.format("%s. Минимальный срок блокировки карты не прошел - %dд",
                                CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE, blockPeriod));
                    }

                    List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(persistenceSession, exCard.getOrg().getIdOfOrg());
                    for (Org o : friendlyOrgs) {
                        if (o.getIdOfOrg() == idOfOrg) {
                            throw new CardResponseItem.CardAlreadyExistInYourOrg(CardResponseItem.ERROR_CARD_ALREADY_EXIST_IN_YOUR_ORG_MESSAGE);
                        }
                    }
                    card = cardService.registerNew(idOfOrg, cardNo, cardPrintedNo, type, cardSignVerifyRes, cardSignCertNum,
                            isLongUid, CardTransitionState.BORROWED.getCode());
                    idOfCard = card.getIdOfCard();
                    transitionState = CardTransitionState.fromInteger(card.getTransitionState());

                    exCard.setTransitionState(CardTransitionState.GIVEN_AWAY.getCode());
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }
            }
        } catch (CardResponseItem.CardAlreadyExist e) {
            logger.error("CardAlreadyExistException", e);
            return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE,
                    e.getMessage());
        } catch (CardResponseItem.CardAlreadyExistInYourOrg e) {
            logger.error("CardAlreadyExistInYourOrgException", e);
            return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE,
                    e.getMessage());
        } catch (Exception e){
            if (e.getMessage() == null) {
                logger.error("Error in register card", e);
                return new CardResponseItem(CardResponseItem.ERROR_INTERNAL, CardResponseItem.ERROR_INTERNAL_MESSAGE);
            }
            if (e.getMessage().contains("ConstraintViolationException")) {
                return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE, CardResponseItem.ERROR_DUPLICATE_CARD_MESSAGE);
            } else if (e instanceof IllegalStateException) {
                return new CardResponseItem(CardResponseItem.ERROR_SIGN_VERIFY, CardResponseItem.ERROR_SIGN_VERIFY_MESSAGE);
            } else {
                logger.error("Error in register card", e);
                return new CardResponseItem(CardResponseItem.ERROR_INTERNAL, CardResponseItem.ERROR_INTERNAL_MESSAGE);
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new CardResponseItem(idOfCard, (null != transitionState) ? transitionState.getCode() : null);
    }

    @WebMethod(operationName = "getEnterEventsManual")
    public List<EnterEventManualItem> getEnterEventsManual(@WebParam(name = "orgId") long idOfOrg) throws FrontControllerException {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        List<EnterEventManualItem> listResult = new ArrayList<EnterEventManualItem>();
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria criteria = persistenceSession.createCriteria(EnterEventManual.class);
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            List<EnterEventManual> list = criteria.list();
            for (EnterEventManual event : list) {
                EnterEventManualItem item = new EnterEventManualItem();
                item.setIdOfOrg(event.getIdOfOrg());
                item.setIdOfClient(event.getIdOfClient());
                item.setEvtDateTime(event.getEvtDateTime());
                item.setEnterName(event.getEnterName());
                listResult.add(item);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при получении сохраненных событий Enter Event от внешней системы", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return listResult;
    }

    @WebMethod(operationName = "getExternalEvents")
    public ExternalEventItems getExternalEvents(@WebParam(name = "orgId") long idOfOrg,
            @WebParam(name = "version") long version) throws FrontControllerException {
        ExternalEventItems result = new ExternalEventItems();
        ManualEvents manualEvents = new ManualEvents();
        manualEvents.setEnterEventsManual(getEnterEventsManual(idOfOrg));
        ExternalEvents externalEvents = new ExternalEvents();
        externalEvents.setExternalEvents(getExternalEventsInternal(idOfOrg, version));
        result.setExternalEvents(externalEvents);
        result.setManualEvents(manualEvents);
        return result;
    }

    private List<ExternalEventItem> getExternalEventsInternal(Long idOfOrg, long version) throws FrontControllerException {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        List<ExternalEventItem> listResult = new ArrayList<ExternalEventItem>();
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Query query = persistenceSession.createQuery("select e from ExternalEvent e "
                    + "where e.version > :version and e.client.org.idOfOrg = :idOfOrg");
            query.setParameter("version", version);
            query.setParameter("idOfOrg", idOfOrg);
            List<ExternalEvent> list = query.list();
            for (ExternalEvent event : list) {
                ExternalEventItem item = new ExternalEventItem();
                item.setIdOfClient(event.getClient().getIdOfClient());
                item.setEvtDateTime(event.getEvtDateTime());
                item.setOrgName(event.getOrgName());
                item.setType(event.getEvtType().ordinal());
                item.setVersion(event.getVersion());
                listResult.add(item);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при получении событий от внешних систем", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return listResult;
        }

    public List<ClientsInsideItem> getClientsInside(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "mode") int mode, @WebParam(name = "group") String group, @WebParam(name = "requestDate") long requestDate) throws FrontControllerException {
        try {
            if (mode == 1) {
                //Обнуление часов, минут, секунд
                Date beginDate = CalendarUtils.truncateToDayOfMonth(new Date(requestDate));
                Date endDate = CalendarUtils.addOneDay(beginDate);
                List<Long> result = DAOReadonlyService.getInstance()
                        .getClientsIdsWhereHasEnterEvents(idOfOrg, beginDate, endDate);
                List<ClientsInsideItem> clientsList = new ArrayList<ClientsInsideItem>();
                for (Long value : result) {
                    ClientsInsideItem clientsInsideItem = new ClientsInsideItem();
                    clientsInsideItem.setIdOfClient(value.longValue());
                    clientsList.add(clientsInsideItem);
                }
                return clientsList;
            }
            throw new Exception(String.format("Неизвестное значение параметра 'mode'=%s", mode));
        } catch (Exception e) {
            logger.error("Ошибка при получении клиентов, которые сегодня хотя бы раз были в школе", e);
            throw new FrontControllerException(String.format("Ошибка: %s", e.getMessage()));
        }
    }

    @WebMethod
    public List<SimpleEnterEventItem> getEnterEvents(@WebParam(name = "idOfOrg") long idOfOrg, @WebParam(name = "groupName") String groupName,
            @WebParam(name = "requestDate") long requestDate) throws FrontControllerException {
        try {
            Date beginDate = CalendarUtils.truncateToDayOfMonth(new Date(requestDate));
            Date endDate = CalendarUtils.addOneDay(beginDate);
            List<SimpleEnterEventItem> list = new ArrayList<SimpleEnterEventItem>();
            List<EnterEvent> eeList = DAOReadonlyService.getInstance().getEnterEventsByOrgAndGroup(idOfOrg, groupName, beginDate, endDate);
            for (EnterEvent ee : eeList) {
                SimpleEnterEventItem item = new SimpleEnterEventItem(ee);
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка событий прохода", e);
            throw new FrontControllerException(String.format("Ошибка: %s", e.getMessage()));
        }
    }

    @WebMethod
    public String getCardSignVerifyKey(@WebParam(name = "idOfOrg") long idOfOrg, @WebParam(name = "cardSignCertNum") int cardSignCertNum,
            @WebParam(name = "signType") Integer signType) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        try {
            byte[] signData = DAOReadonlyService.getInstance().getCardSignData(cardSignCertNum, signType);
            if (signData == null) {
                throw new FrontControllerException("Ключ не найден по входным данным");
            }
            return Base64.encodeBytes(signData);
        } catch (Exception e) {
            logger.error("Ошибка при получении ключа цифровой подписи для верификации карты", e);
            throw new FrontControllerException(String.format("Ошибка: %s", e.getMessage()));
        }
    }

    @WebMethod
    public List<OrgIstkSummaryItem> getOrgsForIstkSync() throws FrontControllerException {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        List<OrgIstkSummaryItem> listResult = new LinkedList<OrgIstkSummaryItem>();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria criteria = persistenceSession.createCriteria(Org.class);
            criteria.addOrder(org.hibernate.criterion.Order.asc("idOfOrg"));

            OrgService orgService = OrgService.getInstance();

            List<Org> list = criteria.list();
            for (Org org : list) {
                OrgIstkSummaryItem item = new OrgIstkSummaryItem(org.getShortName(), org.getIdOfOrg(), org.getAddress(),
                        org.getVersion(), org.getGuid(), org.getDistrict(), org.getShortNameInfoService(), orgService.getMainBulding(org).getIdOfOrg());
                listResult.add(item);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при синхронизации организаций с инфокиосками", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return listResult;
    }

    @WebMethod(operationName = "loadClientPhotoChanges")
    public List<ClilentPhotoChangeItem> loadClientPhotoChanges(@WebParam(name = "idOfOrg") long idOfOrg)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        List<ClilentPhotoChangeItem> results = new ArrayList<ClilentPhotoChangeItem>();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
            List<Org> orgs = new ArrayList<Org>();
            orgs.addAll(org.getFriendlyOrg());
            List<ClientPhoto> clientPhotos = ImageUtils.getNewClientPhotos(persistenceSession, orgs);

            List<Long> clientsIds = new ArrayList<Long>();
            for(ClientPhoto clientPhoto : clientPhotos) {
                clientsIds.add(clientPhoto.getIdOfClient());
            }
            List<Client> clientList = DAOUtils.findClients(persistenceSession, clientsIds);
            Map<Long, Client> clientMap = new HashMap<Long, Client>();
            for(Client client : clientList) {
                clientMap.put(client.getIdOfClient(), client);
            }

            for(ClientPhoto clientPhoto : clientPhotos){
                ImageUtils.PhotoContent photoContent = ImageUtils.getPhotoContent(clientMap.get(clientPhoto.getIdOfClient()),
                        clientPhoto, ImageUtils.ImageSize.SMALL.getValue(), true);
                Client guardian = clientPhoto.getGuardian();
                String guardianName = null;
                if(guardian != null){
                    guardianName = guardian.getPerson().getFullName();
                }
                ClilentPhotoChangeItem item = new ClilentPhotoChangeItem(clientPhoto.getIdOfClient(), photoContent.getBytes(),
                        photoContent.getHash(), clientPhoto.getLastProceedError(),guardianName);
                results.add(item);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return results;
        } catch (Exception e) {
            logger.error("Ошибка при запросе фото для сверки", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "proceedClientPhotoChanges")
    public void proceedClientPhotoChanges(@WebParam(name = "idOfOrg") long idOfOrg,
            @WebParam(name = "results") List<ClientPhotoChangeResult> results) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Long> clientIdsList = new ArrayList<Long>();
            for(ClientPhotoChangeResult result : results) {
                clientIdsList.add(result.getClientId());
            }
            List<Client> clientList = DAOUtils.findClients(persistenceSession, clientIdsList);
            Map<Long, Client> clientMap = new HashMap<Long, Client>();
            for(Client client : clientList) {
                clientMap.put(client.getIdOfClient(), client);
            }
            List<ClientPhoto> clientPhotosList = ImageUtils.findClientPhotos(persistenceSession, clientIdsList);
            Map<Long, ClientPhoto> clientPhotoMap = new HashMap<Long, ClientPhoto>();
            for(ClientPhoto clientPhoto : clientPhotosList) {
                clientPhotoMap.put(clientPhoto.getIdOfClient(), clientPhoto);
            }

            for(ClientPhotoChangeResult result : results){
                Client client = clientMap.get(result.getClientId());
                ClientPhoto clientPhoto = clientPhotoMap.get(client.getIdOfClient());
                int currentPhotoHash = ImageUtils.getPhotoHash(client, clientPhoto, ImageUtils.ImageSize.SMALL.getValue(), true);
                if(result.getState() == 1){
                    if(result.getSrc() == currentPhotoHash){
                        try {
                            ImageUtils.moveImage(client, clientPhoto);
                            clientPhoto.setIsNew(false);
                            clientPhoto.setIsCanceled(false);
                            clientPhoto.setIsApproved(true);
                            clientPhoto.setLastProceedError(null);
                            Long nextVersion = DAOUtils.nextVersionByClientPhoto(persistenceSession);
                            clientPhoto.setVersion(nextVersion);
                            persistenceSession.update(clientPhoto);
                        } catch (IOException e){
                            logger.error(e.getMessage(), e);
                            String error = "Не удалось принять фото-расхождение. Обратитесь к администратору сервера: " + e.getMessage();
                            if(error.length() > 256){
                                error = error.substring(0, 256);
                            }
                            clientPhoto.setLastProceedError(error);
                            persistenceSession.update(clientPhoto);
                        }
                    } else {
                        clientPhoto.setLastProceedError("Фото-расхождение было изменено во время сверки");
                        persistenceSession.update(clientPhoto);
                    }
                }
                if(result.getState() == 2){
                    if(result.getSrc() == currentPhotoHash){
                        boolean deleted = ImageUtils.deleteImage(client, clientPhoto, true);
                        if (!deleted) {
                            clientPhoto.setLastProceedError("Не удалось удалить фото-расхождение. Обратитесь к администратору сервера.");
                            persistenceSession.update(clientPhoto);
                        } else {
                            clientPhoto.setIsNew(false);
                            clientPhoto.setIsCanceled(true);
                            clientPhoto.setLastProceedError(null);
                            persistenceSession.update(clientPhoto);
                        }
                    }
                }
                persistenceSession.flush();
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при обработке результатов сверки фото", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "getClientPhotoChangesCount")
    public Long getClientPhotoChangesCount(@WebParam(name = "idOfOrg") long idOfOrg) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
            List<Org> orgs = new ArrayList<Org>();
            orgs.addAll(org.getFriendlyOrg());
            List<ClientPhoto> clientPhotos = ImageUtils.getNewClientPhotos(persistenceSession, orgs);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return (long) clientPhotos.size();
        } catch (Exception e) {
            logger.error("Ошибка при запросе количества фото для сверки", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "getServerDateTime")
    public String getServerDateTime() {
        return CalendarUtils.dateTimeToString(new Date());
    }


    @WebMethod(operationName = "unblockOrReturnCard")
    public ResponseItem unblockOrReturnCard(@WebParam(name="cardNo") Long cardNo, @WebParam(name="idOfOrg") Long idOfOrg)
        throws FrontControllerException{
        //checkRequestValidity(idOfOrg);
        ResponseItem responseItem = new ResponseItem();
        try {
            CardService.getInstance().unblockOrReturnCard(cardNo, idOfOrg);
            responseItem.code = ResponseItem.OK;
            responseItem.message = ResponseItem.OK_MESSAGE;
        } catch (CardNotFoundException e) {
            logger.error("Error in unblockOrReturnCard", e);
            responseItem.code = ResponseItem.ERROR_CARD_NOT_FOUND;
            responseItem.message = ResponseItem.ERROR_SPECIAL_CARD_NOT_FOUND_MESSAGE;
        } catch (CardWrongStateException e) {
            logger.error("Error in unblockOrReturnCard", e);
            responseItem.code = ResponseItem.ERROR_CARD_WRONG_STATE;
            responseItem.message = ResponseItem.ERROR_CARD_WRONG_STATE_MESSAGE;
        } catch (CardUidGivenAwayException e) {
            logger.error("Error in unblockOrReturnCard", e);
            responseItem.code = ResponseItem.ERROR_CARD_UID_GIVEN_AWAY;
            responseItem.message = ResponseItem.ERROR_CARD_UID_GIVEN_AWAY_MESSAGE;
        } catch (Exception e) {
            logger.error("Error in unblockOrReturnCard", e);
            responseItem.code = ResponseItem.ERROR_INTERNAL;
            responseItem.message = ResponseItem.ERROR_INTERNAL_MESSAGE;
        }
        return responseItem;
    }

    @WebMethod(operationName = "findClient")
    public List<FindClientResult> findClient(@WebParam(name = "orgId")Long orgId,
            @WebParam(name = "findClientFieldList") FindClientField findClientField)
            throws FrontControllerException {
        checkRequestValidity(orgId);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            String mobilePhone = FrontControllerProcessor.getFindClientFieldValueByName(FindClientField.FIELD_MOBILE, findClientField);

            if (StringUtils.isEmpty(mobilePhone)) {
                throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                        FindClientField.FIELD_MOBILE));
            }

            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (null == mobilePhone) {
                throw new FrontControllerException(ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE_MESSAGE);
            }

            String groupNames = FrontControllerProcessor.getFindClientFieldValueByName(FindClientField.FIELD_GROUP_NAMES, findClientField);
            if (StringUtils.isEmpty(groupNames)) {
                throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                        FindClientField.FIELD_GROUP_NAMES));
            }
            String[] groupNameArray = StringUtils.split(groupNames, ",");
            List<String> groupNameList = new LinkedList<String>();
            for (String groupName : groupNameArray) {
                groupNameList.add(StringUtils.trim(groupName));
            }

            List<Client> clientList = DAOUtils
                    .findClientsByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(persistenceSession, mobilePhone, groupNameList);
            if (clientList.isEmpty()) {
                return null;
            }

            List<FindClientResult> findClientResultList = new LinkedList<FindClientResult>();

            for (Client client : clientList) {
                FindClientResult findClientResult = new FindClientResult();

                Org org = client.getOrg();
                if (null != org) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_ORG_ID, org.getIdOfOrg().toString()));
                }

                findClientResult.getFindClientDescParams().getParam().add(
                        new FindClientResult.FindClientItemParam(FindClientResult.FIELD_CLIENT_ID, client.getIdOfClient().toString()));
                findClientResult.getFindClientDescParams().getParam().add(
                        new FindClientResult.FindClientItemParam(FindClientResult.FIELD_CLIENT_GUID, client.getClientGUID()));

                Person person = client.getPerson();
                if (null != person) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_SURNAME, person.getSurname()));
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_FIRST_NAME, person.getFirstName()));
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_SECOND_NAME, person.getSecondName()));
                }

                ClientGroup clientGroup = client.getClientGroup();
                if (null != clientGroup) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_GROUP, clientGroup.getGroupName()));
                }
                findClientResult.getFindClientDescParams().getParam()
                        .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_ORG_NAME, client.getOrg().getShortNameInfoService()));

                findClientResultList.add(findClientResult);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return findClientResultList;
        } catch (Exception e) {
            logger.error("Error in findClient", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "registerGuardian")
    public List<RegisterGuardianResult> registerGuardian(@WebParam(name = "orgId")Long orgId, @WebParam(name = "guardianDescList") GuardianDesc guardianDescList)
            throws FrontControllerException {
        checkRequestValidity(orgId);

        String firstName = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_FIRST_NAME, guardianDescList);
        if (StringUtils.isEmpty(firstName)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_FIRST_NAME));
        }
        String secondName = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_SECOND_NAME, guardianDescList);
        String surname = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_SURNAME, guardianDescList);
        if (StringUtils.isEmpty(surname)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_SURNAME));
        }
        String group = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_GROUP, guardianDescList);
        if (StringUtils.isEmpty(group)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_GROUP));
        }
        String relationDegree = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_RELATION_DEGREE, guardianDescList);
        if (StringUtils.isEmpty(relationDegree)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_RELATION_DEGREE));
        }
        String legalityStr = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_LEGALITY, guardianDescList);
        if (StringUtils.isEmpty(legalityStr)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_LEGALITY));
        }

        Boolean legality = Boolean.parseBoolean(legalityStr);

        String gender = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_GENDER, guardianDescList);
        if (StringUtils.isEmpty(gender)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_GENDER));
        }

        String guardianBirthDayStr = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_GUARDIAN_BIRTHDAY, guardianDescList);
        Date guardianBirthDay = null;
        if (!StringUtils.isEmpty(guardianBirthDayStr)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            try {
                guardianBirthDay = dateFormat.parse(guardianBirthDayStr);
            } catch (ParseException e) {
                logger.error("Error in registerGuardian", e);
                throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_INCORRECT_FORMAT,
                        GuardianDesc.FIELD_GUARDIAN_BIRTHDAY));
            }
        }

        String mobilePhone = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_MOBILE, guardianDescList);

        if (StringUtils.isEmpty(mobilePhone)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_MOBILE));
        }

        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (null == mobilePhone) {
            throw new FrontControllerException(ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE_MESSAGE);
        }

        String clientIdStr = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_CLIENT_ID, guardianDescList);
        if (StringUtils.isEmpty(clientIdStr)) {
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_CLIENT_ID));
        }

        Long clientId;
        try {
            clientId = Long.parseLong(clientIdStr);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            throw new FrontControllerException(String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_CLIENT_ID));
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<String> groupNameList = new ArrayList<String>();
            groupNameList.add(ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());

            Client client = DAOUtils
                    .findClientByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(persistenceSession, mobilePhone, groupNameList);
            if (null != client) {

                List<RegisterGuardianResult> registerGuardianResultList = new LinkedList<RegisterGuardianResult>();
                RegisterGuardianResult registerGuardianResult = new RegisterGuardianResult();

                Org org = client.getOrg();
                if (null != org) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_ORG_ID, org.getIdOfOrg().toString()));
                }

                registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                        new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_CLIENT_ID, client.getIdOfClient().toString()));
                registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                        new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_CLIENT_GUID, client.getClientGUID()));

                Person person = client.getPerson();
                if (null != person) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                            new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_SURNAME, person.getSurname()));
                    registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                            new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_FIRST_NAME, person.getFirstName()));
                    registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                            new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_SECOND_NAME, person.getSecondName()));
                }

                ClientGroup clientGroup = client.getClientGroup();
                if (null != clientGroup) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                            new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_GROUP, clientGroup.getGroupName()));
                }
                registerGuardianResult.getRegisterGuardianDescParams().getParam().add(
                        new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_ORG_NAME, client.getOrg().getShortNameInfoService()));
                registerGuardianResultList.add(registerGuardianResult);
                return registerGuardianResultList;
            }

            Org org = (Org) persistenceSession.load(Org.class, orgId);
            if (null == org) {
                throw new FrontControllerException(ResponseItem.ERROR_ORGANIZATION_NOT_FOUND_MESSAGE);
            }

            ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
            fc.setValue(ClientManager.FieldId.SURNAME, surname);
            fc.setValue(ClientManager.FieldId.NAME, firstName);
            if (!StringUtils.isEmpty(secondName)) {
                fc.setValue(ClientManager.FieldId.SECONDNAME, secondName);
            } else {
                fc.setValue(ClientManager.FieldId.SECONDNAME, "");
            }
            fc.setValue(ClientManager.FieldId.GROUP, group);
            fc.setValue(ClientManager.FieldId.GENDER, gender);
            if (null != guardianBirthDay) {
                fc.setValue(ClientManager.FieldId.BIRTH_DATE, guardianBirthDay);
            }
            fc.setValue(ClientManager.FieldId.MOBILE_PHONE, mobilePhone);

            Long idOfClient = ClientManager.registerClient(orgId, fc, false, true);

            Client guardian = (Client) persistenceSession.load(Client.class, idOfClient);

            ClientGuardian clientGuardian = ClientManager.createClientGuardianInfoTransactionFree(persistenceSession, guardian, relationDegree,
                    false, clientId, ClientCreatedFromType.ARM, null);

            clientGuardian.setIsLegalRepresent(legality);
            persistenceSession.merge(clientGuardian);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return null;
        } catch (Exception e) {
            logger.error("Error in registerGuardian", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "registerGuardianMigrantRequest")
    public ResponseItem registerGuardianMigrantRequest(@WebParam(name = "orgId")Long orgId, @WebParam(name = "guardianDescList") GuardianDesc guardianDescList)
            throws FrontControllerException {
        checkRequestValidity(orgId);

        ResponseItem result = new ResponseItem();

        String clientIdStr = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_CLIENT_ID, guardianDescList);
        if (StringUtils.isEmpty(clientIdStr)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_CLIENT_ID);
            return result;
        }

        Long clientId;
        try {
            clientId = Long.parseLong(clientIdStr);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_CLIENT_ID);
            return result;
        }

        String guardianIdString = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_GUARDIAN_ID, guardianDescList);
        if (StringUtils.isEmpty(guardianIdString)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_GUARDIAN_ID);
            return result;
        }

        Long guardianId;
        try {
            guardianId = Long.parseLong(guardianIdString);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_GUARDIAN_ID);
            return result;
        }

        String relationDegree = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_RELATION_DEGREE, guardianDescList);
        if (StringUtils.isEmpty(relationDegree)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_RELATION_DEGREE);
            return result;
        }
        String legalityStr = FrontControllerProcessor.getFindClientFieldValueByName(GuardianDesc.FIELD_LEGALITY, guardianDescList);
        if (StringUtils.isEmpty(legalityStr)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE, GuardianDesc.FIELD_LEGALITY);
            return result;
        }

        Boolean legality = Boolean.parseBoolean(legalityStr);

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = (Org) persistenceSession.load(Org.class, orgId);
            if (null == org) {
                result.code = ResponseItem.ERROR_ORGANIZATION_NOT_FOUND;
                result.message = ResponseItem.ERROR_ORGANIZATION_NOT_FOUND_MESSAGE;
                return result;
            }

            Date fireTime = new Date();

            Client guardian = (Client) persistenceSession.load(Client.class, guardianId);
            if (null == guardian) {
                result.code = ResponseItem.ERROR_CLIENT_NOT_FOUND;
                result.message = ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE;
                return result;
            }

            ClientGuardian clientGuardian = ClientManager.createClientGuardianInfoTransactionFree(persistenceSession, guardian, relationDegree,
                    false, clientId, ClientCreatedFromType.ARM, null);

            clientGuardian.setIsLegalRepresent(legality);
            persistenceSession.merge(clientGuardian);

            Long idOfProcessorMigrantRequest = MigrantsUtils
                    .nextIdOfProcessorMigrantRequest(persistenceSession, guardian.getOrg().getIdOfOrg());
            CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(idOfProcessorMigrantRequest,
                    guardian.getOrg().getIdOfOrg());
            String requestNumber = ImportMigrantsService.formRequestNumber(guardian.getOrg().getIdOfOrg(), orgId,
                    idOfProcessorMigrantRequest, fireTime);

            Client child = (Client) persistenceSession.load(Client.class, clientId);
            if (null == child) {
                result.code = ResponseItem.ERROR_CLIENT_NOT_FOUND;
                result.message = ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE;
                return result;
            }

            if (!DAOUtils.isFriendlyOrganizations(persistenceSession, guardian.getOrg(), child.getOrg())) {
                // TODO
                Migrant migrantNew = new Migrant(compositeIdOfMigrant, guardian.getOrg().getDefaultSupplier(),
                        requestNumber, guardian, org, fireTime, CalendarUtils.addYear(fireTime, 10), Migrant.NOT_SYNCHRONIZED);
                migrantNew.setInitiator(MigrantInitiatorEnum.INITIATOR_ORG);
                //migrantNew.setSection(request.getGroupName());
                //migrantNew.setResolutionCodeGroup(request.getIdOfServiceClass());
                persistenceSession.save(migrantNew);

                persistenceSession.save(ImportMigrantsService
                        .createResolutionHistory(persistenceSession, guardian, compositeIdOfMigrant.getIdOfRequest(),
                                VisitReqResolutionHist.RES_CREATED, fireTime));
                persistenceSession.flush();
                persistenceSession.save(ImportMigrantsService
                        .createResolutionHistory(persistenceSession, guardian, compositeIdOfMigrant.getIdOfRequest(),
                                VisitReqResolutionHist.RES_CONFIRMED, CalendarUtils.addSeconds(fireTime, 1)));

                result.code = ResponseItem.OK;
                result.message = ResponseItem.OK_MESSAGE;
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } catch (Exception e) {
            logger.error("Error in registerGuardianMigrantRequest", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}