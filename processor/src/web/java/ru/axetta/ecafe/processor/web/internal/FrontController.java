/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.partner.mesh.guardians.*;
import ru.axetta.ecafe.processor.core.service.DulDetailService;
import ru.axetta.ecafe.processor.core.utils.*;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.card.CardBlockPeriodConfig;
import sun.security.provider.X509Factory;

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
import ru.axetta.ecafe.processor.core.persistence.utils.*;
import ru.axetta.ecafe.processor.core.service.ImportRegisterMSKClientsService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterSpbClientsService;
import ru.axetta.ecafe.processor.core.service.RegistryChangeCallback;
import ru.axetta.ecafe.processor.web.internal.front.items.*;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderDAOService;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.BooleanUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public String test(@WebParam(name = "orgId") Long orgId) throws FrontControllerException {
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
        if (RuntimeContext.RegistryType.isMsk()) {
            if (actionFilter != ImportRegisterMSKClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if (RuntimeContext.RegistryType.isSpb()) {
            if (actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
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
        if (RuntimeContext.RegistryType.isMsk()) {
            if (actionFilter != ImportRegisterMSKClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if (RuntimeContext.RegistryType.isSpb()) {
            if (actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
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
        if (actionFilter != ImportRegisterMSKClientsService.CREATE_OPERATION
                && actionFilter != ImportRegisterMSKClientsService.DELETE_OPERATION
                && actionFilter != ImportRegisterMSKClientsService.MODIFY_OPERATION
                && actionFilter != ImportRegisterMSKClientsService.MOVE_OPERATION) {
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
        if (RuntimeContext.RegistryType.isMsk()) {
            if (actionFilter != ImportRegisterMSKClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if (RuntimeContext.RegistryType.isSpb()) {
            if (actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
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
        if (RuntimeContext.RegistryType.isMsk()) {
            if (actionFilter != ImportRegisterMSKClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterMSKClientsService.MOVE_OPERATION) {
                af = null;
            }
        } else if (RuntimeContext.RegistryType.isSpb()) {
            if (actionFilter != ImportRegisterSpbClientsService.CREATE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.DELETE_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MODIFY_OPERATION
                    && actionFilter != ImportRegisterSpbClientsService.MOVE_OPERATION) {
                af = null;
            }
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeItemsV2(idOfOrg, revisionDate, af, nameFilter);
    }

    @WebMethod(operationName = "refreshRegistryChangeEmployeeItems")
    public List<RegistryChangeItem> refreshRegistryChangeEmployeeItems(@WebParam(name = "idOfOrg") long idOfOrg)
            throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeEmployeeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItems")
    public List<RegistryChangeItem> refreshRegistryChangeItems(@WebParam(name = "idOfOrg") long idOfOrg)
            throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsV2")
    public List<RegistryChangeItemV2> refreshRegistryChangeItemsV2(@WebParam(name = "idOfOrg") long idOfOrg)
            throws Exception {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItemsV2(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsInternal")
    public List<RegistryChangeItem> refreshRegistryChangeItemsInternal(@WebParam(name = "idOfOrg") long idOfOrg)
            throws Exception {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                refreshRegistryChangeItems(idOfOrg);
    }

    @WebMethod(operationName = "refreshRegistryChangeItemsInternalV2")
    public List<RegistryChangeItemV2> refreshRegistryChangeItemsInternalV2(@WebParam(name = "idOfOrg") long idOfOrg)
            throws Exception {
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
    public List<RegistryChangeCallback> proceedRegitryChangeItem(
            @WebParam(name = "changesList") List<Long> changesList,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "fullNameValidation") boolean fullNameValidation,
            @WebParam(name = "orgId") Long orgId,
            @WebParam(name = "guidStaff") String guidStaff) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }

        try {
            if (changesList != null && changesList.size() > 0) {
                if (RuntimeContext.RegistryType.isMsk()) {
                    RegistryChange change = RuntimeContext.getAppContext()
                            .getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class)
                            .getRegistryChange(changesList.get(0));
                    checkRequestValidity(change.getIdOfOrg());
                } else if (RuntimeContext.RegistryType.isSpb()) {
                    RegistryChange change = RuntimeContext.getAppContext()
                            .getBean(ImportRegisterSpbClientsService.class).getRegistryChange(changesList.get(0));
                    checkRequestValidity(change.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to pass auth", e);
            //return "При подтверждении изменения из Реестров, произошла ошибка: " + e.getMessage();
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод proceedRegitryChangeItem (фронт)");
        if (orgId != null) {
            Org org = DAOReadonlyService.getInstance().findOrg(orgId);
            if (org != null) {
                clientsMobileHistory.setOrg(org);
            }
            clientsMobileHistory.setShowing("АРМ ОО (ид." + orgId + ")");
        } else {
            clientsMobileHistory.setShowing("АРМ");
        }
        if (guidStaff != null) {
            clientsMobileHistory.setStaffguid(guidStaff);
        }
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод proceedRegitryChangeItem (front)");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegistryChangeItem(changesList, operation, fullNameValidation, clientsMobileHistory, clientGuardianHistory);
    }

    @WebMethod(operationName = "proceedRegitryChangeItemInternal")
    /* Если метод возвращает null, значит операция произведена успешно, иначсе это будет сообщение об ошибке */
    public List<RegistryChangeCallback> proceedRegitryChangeItemInternal(
            @WebParam(name = "changesList") List<Long> changesList,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "fullNameValidation") boolean fullNameValidation,
            @WebParam(name = "idoforg") Long orgId,
            @WebParam(name = "guidStaff") String guidStaff) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
            //return "При подтверждении изменения из Реестров, произошла ошибка: " + fce.getMessage();
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод proceedRegitryChangeItemInternal (фронт)");
        if (orgId != null) {
            Org org = DAOReadonlyService.getInstance().findOrg(orgId);
            if (org != null) {
                clientsMobileHistory.setOrg(org);
            }
            clientsMobileHistory.setShowing("АРМ ОО (ид." + orgId + ")");
        } else {
            clientsMobileHistory.setShowing("АРМ");
        }
        if (guidStaff != null) {
            clientsMobileHistory.setStaffguid(guidStaff);
        }
        clientsMobileHistory.setStaffguid(guidStaff);
        //
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод proceedRegitryChangeItemInternal (front)");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        //
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegistryChangeItem(changesList, operation, fullNameValidation, clientsMobileHistory, clientGuardianHistory);
    }

    @WebMethod(operationName = "proceedRegitryChangeEmployeeItem")
    /* Если метод возвращает null, значит операция произведена успешно, иначсе это будет сообщение об ошибке */ public List<RegistryChangeCallback> proceedRegitryChangeEmployeeItem(
            @WebParam(name = "changesList") List<Long> changesList, @WebParam(name = "operation") int operation,
            @WebParam(name = "fullNameValidation") boolean fullNameValidation,
            @WebParam(name = "groupName") String groupName,
            @WebParam(name = "orgId") Long orgId,
            @WebParam(name = "guidStaff") String guidStaff) {
        if (operation != ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE) {
            return Collections.EMPTY_LIST;
        }
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass ip check", fce);
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод proceedRegitryChangeEmployeeItem (фронт)");
        if (orgId != null) {
            Org org = DAOReadonlyService.getInstance().findOrg(orgId);
            if (org != null) {
                clientsMobileHistory.setOrg(org);
            }
            clientsMobileHistory.setShowing("АРМ ОО (ид." + orgId + ")");
        } else {
            clientsMobileHistory.setShowing("АРМ");
        }
        if (guidStaff != null) {
            clientsMobileHistory.setStaffguid(guidStaff);
        }
        clientsMobileHistory.setStaffguid(guidStaff);
        //
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод proceedRegitryChangeEmployeeItem (front)");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        //
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                proceedRegistryEmployeeChangeItem(changesList, operation, fullNameValidation, groupName, clientsMobileHistory, clientGuardianHistory);
    }

    @WebMethod(operationName = "loadRegistryChangeRevisions")
    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisions(@WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeRevisions(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeRevisionsInternal")
    public List<RegistryChangeRevisionItem> loadRegistryChangeRevisionsInternal(
            @WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkIpValidity();
        } catch (FrontControllerException fce) {
            return Collections.EMPTY_LIST;
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeRevisions(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeEmployeeRevisions")
    public List<RegistryChangeRevisionItem> loadRegistryChangeEmployeeRevisions(
            @WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
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
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeErrorItems(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeEmployeeErrorItems")
    public List<RegistryChangeErrorItem> loadRegistryChangeEmployeeErrorItems(
            @WebParam(name = "idOfOrg") long idOfOrg) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return Collections.EMPTY_LIST;
        }

        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                loadRegistryChangeEmployeeErrorItems(idOfOrg);
    }

    @WebMethod(operationName = "loadRegistryChangeErrorItemsInternal")
    public List<RegistryChangeErrorItem> loadRegistryChangeErrorItemsInternal(
            @WebParam(name = "idOfOrg") long idOfOrg) {
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
                                         @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "error") String error,
                                         @WebParam(name = "errorDetails") String errorDetails) {
        try {
            checkRequestValidity(idOfOrg);
        } catch (FrontControllerException fce) {
            logger.error("Failed to pass auth", fce);
            return fce.getMessage();
        }
        return RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                addRegistryChangeError(idOfOrg, revisionDate, error, errorDetails);
    }

    @WebMethod(operationName = "addRegistryChangeErrorInternal")
    public String addRegistryChangeErrorInternal(@WebParam(name = "idOfOrg") long idOfOrg,
                                                 @WebParam(name = "revisionDate") long revisionDate, @WebParam(name = "error") String error,
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
                                             @WebParam(name = "comment") String comment, @WebParam(name = "author") String author) {
        RegistryChangeError e = null;
        if (RuntimeContext.RegistryType.isMsk()) {
            e = RuntimeContext.getAppContext()
                    .getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class)
                    .getRegistryChangeError(idOfRegistryChangeError);
        } else if (RuntimeContext.RegistryType.isSpb()) {
            e = RuntimeContext.getAppContext().getBean(ImportRegisterSpbClientsService.class)
                    .getRegistryChangeError(idOfRegistryChangeError);
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
    public String commentRegistryChangeErrorInternal(
            @WebParam(name = "idOfRegistryChangeError") long idOfRegistryChangeError,
            @WebParam(name = "comment") String comment, @WebParam(name = "author") String author) {
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
    public List<RegistryChangeGuardianItem> loadRegistryChangeGuardians(
            @WebParam(name = "idOfRegistryChange") long idOfRegistryChange) throws FrontControllerException {
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
    public VisitorItem checkVisitorByCard(@WebParam(name = "orgId") Long idOfOrg,
                                          @WebParam(name = "cardNo") Long cardNo, @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
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
            Card c = null;
            if (longCardNo == null) {
                c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            } else {
                c = DAOUtils.findCardByLongCardNo(persistenceSession, longCardNo);
                if (c == null) {
                    c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
                }
            }
            if (c != null) {
                throw new FrontControllerException(
                        "Карта уже зарегистрирована как постоянная на клиента: " + c.getClient().getIdOfClient());
            }

            CardTemp ct = null;
            if (longCardNo == null) {
                ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
            } else {
                ct = DAOUtils.findCardTempByLongCardNo(persistenceSession, longCardNo);
            }

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
            if (ct.getClient() != null) {
                throw new FrontControllerException("Карта уже зарегистрирована как временная карта клиента");
            }

            int type = DAOUtils.extractCardTypeByCartNo(persistenceSession, cardNo);
            if (type == Visitor.DEFAULT_TYPE) {
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
                if (ct.getVisitor() != null) {
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
    public TempCardOperationItem getLastTempCardOperation(@WebParam(name = "orgId") Long idOfOrg,
                                                          @WebParam(name = "cardNo") Long cardNo, @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        TempCardOperationItem tempCardOperationItem = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            CardTempOperation cardTempOperation = DAOUtils
                    .getLastTempCardOperationByOrgAndCartNo(persistenceSession, idOfOrg, cardNo);
            if (cardTempOperation != null) {
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
    public void registerTempCard(@WebParam(name = "orgId") Long idOfOrg, @WebParam(name = "cardNo") Long cardNo,
                                 @WebParam(name = "cardPrintedNo") String cardPrintedNo, @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        ///
        try {
            RuntimeContext.getInstance().getCardManager().createTempCard(idOfOrg, cardNo, cardPrintedNo, longCardNo);
        } catch (Exception e) {
            logger.error("Failed registerTempCard", e);
            throw new FrontControllerException(
                    String.format("Ошибка при регистрации времменой карты: %s", e.getMessage()), e);
        }
    }

    /* Метод возвращает номер, напечатанный на новой карте, по номеру чипа карты */
    @WebMethod(operationName = "getCardPrintedNoByCardNo")
    public CardPrintedNoItem getCardPrintedNoByCardNo(@WebParam(name = "orgId") Long idOfOrg,
                                                      @WebParam(name = "cardNo") Long cardNo) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        try {
            CardManager.NewCardItem newCardItem = RuntimeContext.getInstance().getCardManager()
                    .getNewCardPrintedNo(cardNo);
            if (newCardItem == null) {
                throw new Exception(String.format("Карта с номером чипа '%s' не найдена", cardNo));
            }
            if (newCardItem.getCardPrintedNo() == null) {
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
    public List<GuardianAndChildItem> getGuardiansAndChildsByCard(@WebParam(name = "orgId") Long idOfOrg,
                                                                  @WebParam(name = "cardNo") Long cardNo) throws FrontControllerException {
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
                if (ct != null) {
                    throw new FrontControllerException("Карта зарегистрирована как временная");
                }
                throw new FrontControllerException("Карта не найдена");
            } else {
                if (c.getClient() == null) {
                    throw new FrontControllerException("Карта не зарегистрирована на клиента");
                }
                if (!c.getState().equals(Card.ACTIVE_STATE)) {
                    throw new FrontControllerException("Карта заблокирована");
                }
                if (!c.getLifeState().equals(Card.READY_LIFE_STATE)) {
                    throw new FrontControllerException("Карта не активна");
                }
            }

            // Находим опекуна, его опекаемых и опекунов опекаемых
            Client clientByCard = c.getClient();
            GuardianAndChildItem clientByCardItem = new GuardianAndChildItem(clientByCard.getIdOfClient(),
                    clientByCard.getOrg().getIdOfOrg(), clientByCard.getPerson().getFullName());
            if (!clientByCard.isDeletedOrLeaving()) {
                result.add(clientByCardItem);
            }

            List<Client> childsList = ClientManager
                    .findChildsByClient(persistenceSession, clientByCard.getIdOfClient());
            if (childsList.size() > 0) {
                List<GuardianAndChildItem> childItemList = new ArrayList<GuardianAndChildItem>();
                List<GuardianAndChildItem> guardiansItemList = new ArrayList<GuardianAndChildItem>();
                for (Client client : childsList) {
                    if (client.isDeletedOrLeaving()) {
                        continue;
                    }
                    GuardianAndChildItem clientItem = new GuardianAndChildItem(client.getIdOfClient(),
                            client.getOrg().getIdOfOrg(), client.getPerson().getFullName());
                    clientByCardItem.getIdOfChildren().add(client.getIdOfClient());
                    List<Client> guardians = ClientManager
                            .findGuardiansByClient(persistenceSession, client.getIdOfClient());
                    childItemList.add(clientItem);
                    if (guardians != null && guardians.size() > 0) {
                        for (Client g : guardians) {
                            if (g.isDeletedOrLeaving()) {
                                continue;
                            }
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
                List<Client> guardiansList = ClientManager
                        .findGuardiansByClient(persistenceSession, clientByCard.getIdOfClient());
                if (guardiansList.size() > 0) {
                    List<GuardianAndChildItem> guardiansItemList = new ArrayList<GuardianAndChildItem>();
                    for (Client g : guardiansList) {
                        GuardianAndChildItem gItem = new GuardianAndChildItem(g.getIdOfClient(),
                                g.getOrg().getIdOfOrg(), g.getPerson().getFullName());
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
    public void createMigrateRequests(@WebParam(name = "orgId") Long idOfOrg,
                                      @WebParam(name = "rqs") List<MigrateRequest> rqs) throws FrontControllerException {
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

            for (Long idOfOrgRegistry : map.keySet()) {

                String requestNumber = null;

                for (MigrateRequest migrateRequest : map.get(idOfOrgRegistry)) {
                    Client client = (Client) persistenceSession.load(Client.class, migrateRequest.getMigrateClientId());

                    Client clientResol = (Client) persistenceSession
                            .load(Client.class, migrateRequest.getIdOfClientResol());
                    if (clientResol == null) {
                        throw new FrontControllerException(
                                "Клиент-оператор с id=" + migrateRequest.getIdOfClientResol() + " найден");
                    }
                    migrateRequest.validateMigrateRequest();
                    Long idOfProcessorMigrantRequest = MigrantsUtils
                            .nextIdOfProcessorMigrantRequest(persistenceSession, idOfOrgRegistry);
                    CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(idOfProcessorMigrantRequest,
                            idOfOrgRegistry);
                    if (requestNumber == null) {
                        requestNumber = MigrateRequest
                                .formRequestNumber(client.getOrg().getIdOfOrg(), orgVisit.getIdOfOrg(),
                                        idOfProcessorMigrantRequest, date);
                    }
                    Migrant migrant = new Migrant(compositeIdOfMigrant, client.getOrg().getDefaultSupplier(),
                            requestNumber, client, orgVisit, migrateRequest.getStartDate(), migrateRequest.getEndDate(),
                            Migrant.SYNCHRONIZED);

                    Long idOfResol = MigrantsUtils
                            .nextIdOfProcessorMigrantResolutions(persistenceSession, idOfOrgRegistry);
                    CompositeIdOfVisitReqResolutionHist comIdOfHist = new CompositeIdOfVisitReqResolutionHist(idOfResol,
                            migrant.getCompositeIdOfMigrant().getIdOfRequest(), idOfOrgRegistry);
                    VisitReqResolutionHist visitReqResolutionHist = new VisitReqResolutionHist(comIdOfHist,
                            client.getOrg(), VisitReqResolutionHist.RES_CREATED, date,
                            migrateRequest.getResolutionCause(), clientResol, migrateRequest.getContactInfo(),
                            VisitReqResolutionHist.NOT_SYNCHRONIZED,
                            VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);

                    Long idOfResol1 =
                            MigrantsUtils.nextIdOfProcessorMigrantResolutions(persistenceSession, idOfOrg) - 1;
                    CompositeIdOfVisitReqResolutionHist comIdOfHist1 = new CompositeIdOfVisitReqResolutionHist(
                            idOfResol1, migrant.getCompositeIdOfMigrant().getIdOfRequest(), idOfOrg);
                    VisitReqResolutionHist visitReqResolutionHist1 = new VisitReqResolutionHist(comIdOfHist1,
                            client.getOrg(), VisitReqResolutionHist.RES_CONFIRMED, after5Seconds, resolConfirmed, null,
                            null, VisitReqResolutionHist.NOT_SYNCHRONIZED,
                            VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);
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
    public Long registerVisitor(@WebParam(name = "orgId") Long idOfOrg,
                                @WebParam(name = "visitor") VisitorItem visitorItem) throws FrontControllerException {
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
            if (isEmptyFullNameFields(visitorItem.getFirstName(), visitorItem.getSurname(),
                    visitorItem.getSecondName())) {
                throw new FrontControllerException("Все поля ФИО должны быть заполнены");
            }

            /**
             * Если поле-массив PersonDocuments не содержит ни одного описания удостоверния личности,
             * выбрасывать исключение с сообщением «Отсутствует информация об удостоверении личности»
             * */
            if (isEmptyDocumentParams(visitorItem.getDriverLicenceNumber(), visitorItem.getDriverLicenceDate())
                    && isEmptyDocumentParams(visitorItem.getPassportNumber(), visitorItem.getPassportDate())
                    && isEmptyDocumentParams(visitorItem.getWarTicketNumber(), visitorItem.getWarTicketDate())) {

                if (isEmptyFreeDocumentParams(visitorItem.getFreeDocName(), visitorItem.getFreeDocNumber(),
                        visitorItem.getFreeDocDate())) {
                    throw new FrontControllerException("Отсутствует информация об удостоверении личности");
                }
            }

            /**
             * Если в  поле-массиве PersonDocuments у какого-либо документа для даты выдачи
             * будет указана еще не наступившая дата, выбрасывать исключение с сообщением «Неверная дата выдачи документа»
             * */
            if (isDateEqLtCurrentDate(visitorItem.getDriverLicenceDate()) || isDateEqLtCurrentDate(
                    visitorItem.getPassportDate()) || isDateEqLtCurrentDate(visitorItem.getWarTicketDate())) {
                throw new FrontControllerException("Неверное значение даты окончания действия карты");
            }

            if (visitorItem.getIdOfVisitor() == null) {
                Person person = new Person(visitorItem.getFirstName(), visitorItem.getSurname(),
                        visitorItem.getSecondName());
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
                Visitor visitor = DAOUtils.findVisitorById(persistenceSession, visitorItem.getIdOfVisitor());
                if (visitor == null) {
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
                    if (visitor.getVisitorType() == null) {
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
    public void registerVisitorTempCard(@WebParam(name = "orgId") Long idOfOrg,
                                        @WebParam(name = "idOfVisitor") Long idOfVisitor, @WebParam(name = "cardNo") Long cardNo,
                                        @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            /**
             * Если отсутствует посетитель с таким id, выбрасывать исключение «Посетитель не зарегистрирован»
             * */
            Visitor visitor = DAOUtils.findVisitorById(persistenceSession, idOfVisitor);
            if (visitor == null) {
                throw new FrontControllerException("Посетитель не зарегистрирован");
            }
            Card card = null;
            if (longCardNo == null) {
                card = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
            } else {
                card = DAOUtils.findCardByLongCardNo(persistenceSession, longCardNo);
                if (card == null) {
                    card = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
                }
            }

            /**
             * Если id карты совпадает с идентификатором постоянной карты из таблицы постоянных карт —
             * возвращать ошибку с сообщением «карта уже зарегистрирована как постоянная»
             * */
            if (card != null) {
                throw new FrontControllerException("Карта уже зарегистрирована как постоянная");
            }

            Org org = OrgReadOnlyRepository.getInstance().find(idOfOrg);
            if (org == null) {
                throw new FrontControllerException("Организация не найдена");
            }

            CardTemp cardTemp = null;
            if (longCardNo == null) {
                cardTemp = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
            } else {
                cardTemp = DAOUtils.findCardTempByLongCardNo(persistenceSession, cardNo);
            }

            if (cardTemp == null) {
                //cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), ClientTypeEnum.VISITOR);
                cardTemp = new CardTemp(cardNo, String.valueOf(cardNo), 1, longCardNo);
                cardTemp.setOrg(org);
                cardTemp.setVisitor(visitor);
                persistenceSession.save(cardTemp);
            } else {
                /**
                 * Если id карты совпадает с идентификатором временной карты и карта является временной картой системы
                 * («наша карта»), то выбрасывать исключение «карта уже зарегистрирована как временная»
                 * */
                //if(cardTemp.getClientTypeEnum() == ClientTypeEnum.CLIENT){
                if (cardTemp.getVisitorType() == 0) {
                    throw new FrontControllerException("карта уже зарегистрирована как временная");
                } else {
                    if (cardTemp.getVisitor() == null) {
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
                        if (!cardTemp.getVisitor().equals(visitor)) {
                            throw new FrontControllerException("Карта зарегистрирована на другого посетителя");
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
                             @WebParam(name = "cardType") int cardType, @WebParam(name = "issuedTime") Date issuedTime,
                             @WebParam(name = "validTime") Date validTime, @WebParam(name = "longCardNo") Long longCardNo) throws FrontControllerException {
        checkRequestValidity(orgId);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        ///
        try {
            return RuntimeContext.getInstance().getCardManager()
                    .createCard(clientId, cardNo, cardType, Card.ACTIVE_STATE, validTime, Card.ISSUED_LIFE_STATE,
                            "", issuedTime, cardPrintedNo, longCardNo);
        } catch (Exception e) {
            logger.error("Failed registerCard", e);
            throw new FrontControllerException(String.format("Ошибка при регистрации карты: %s", e.getMessage()), e);
        }
    }

    @WebMethod(operationName = "changeCardOwner")
    public void changeCardOwner(@WebParam(name = "orgId") Long orgId, @WebParam(name = "newOwnerId") Long newOwnerId,
                                @WebParam(name = "cardNo") Long cardNo, @WebParam(name = "changeTime") Date changeTime,
                                @WebParam(name = "validTime") Date validTime, @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        checkRequestValidity(orgId);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        ///
        try {
            RuntimeContext.getInstance().getCardManager().changeCardOwner(newOwnerId, cardNo, longCardNo, changeTime, validTime);
        } catch (Exception e) {
            logger.error("Failed changeCardOwner", e);
            throw new FrontControllerException(String.format("Ошибка при смене владельца карты: %s", e.getMessage()),
                    e);
        }
    }

    @WebMethod(operationName = "registerClientsV2")
    public List<RegisterClientResult> registerClientsV2(
            @WebParam(name = "orgId") Long orgId,
            @WebParam(name = "clientDescList") List<ClientDescV2> clientDescList,
            @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness,
            @WebParam(name = "guidStaff") String guidStaff)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        String notifyByPush =
                RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
        String notifyByEmail =
                RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";

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

                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                logger.debug("check client params v2");

                String contractSurname = getClientParamDescValueByName("contractSurname",
                        cd.getClientDescParams().getParam());
                String contractName = getClientParamDescValueByName("contractName",
                        cd.getClientDescParams().getParam());
                String contractSecondName = getClientParamDescValueByName("contractSecondName",
                        cd.getClientDescParams().getParam());
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
                String cardPrintedNo = getClientParamDescValueByName("cardPrintedNo",
                        cd.getClientDescParams().getParam());
                String cardType = getClientParamDescValueByName("cardType", cd.getClientDescParams().getParam());
                String snils = getClientParamDescValueByName("snils", cd.getClientDescParams().getParam());
                String cardExpiry = getClientParamDescValueByName("cardExpiry", cd.getClientDescParams().getParam());
                String cardIssued = getClientParamDescValueByName("cardIssued", cd.getClientDescParams().getParam());
                String birthDate = getClientParamDescValueByName("birthDate", cd.getClientDescParams().getParam());
                String gender = getClientParamDescValueByName("gender", cd.getClientDescParams().getParam());
                String middleGroup = getClientParamDescValueByName("middleGroup", cd.getClientDescParams().getParam());

                fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, contractSurname == null ? " " : contractSurname);
                if (contractName != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_NAME, contractName);
                }
                fc.setValue(ClientManager.FieldId.CONTRACT_SECONDNAME,
                        contractSecondName == null ? "" : contractSecondName);
                if (contractDoc != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_DOC, contractDoc);
                }
                if (surname != null) {
                    fc.setValue(ClientManager.FieldId.SURNAME, surname);
                }
                if (name != null) {
                    fc.setValue(ClientManager.FieldId.NAME, name);
                }
                fc.setValue(ClientManager.FieldId.SECONDNAME, secondName == null ? "" : secondName);
                if (doc != null) {
                    fc.setValue(ClientManager.FieldId.DOC, doc);
                }
                if (address != null) {
                    fc.setValue(ClientManager.FieldId.ADDRESS, address);
                }
                if (phone != null) {
                    fc.setValue(ClientManager.FieldId.PHONE, phone);
                }
                if (mobilePhone != null) {
                    fc.setValue(ClientManager.FieldId.MOBILE_PHONE, mobilePhone);
                }
                if (email != null) {
                    fc.setValue(ClientManager.FieldId.EMAIL, email);
                }
                if (group != null) {
                    fc.setValue(ClientManager.FieldId.GROUP, group);
                }
                if (notifyBySms != null) {
                    fc.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, notifyBySms);
                }
                if (notifyByEmail != null) {
                    fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                }
                if (notifyByPush != null) {
                    fc.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                }
                if (comments != null) {
                    fc.setValue(ClientManager.FieldId.COMMENTS, comments);
                }
                if (cardNo != null) {
                    fc.setValue(ClientManager.FieldId.CARD_ID, cardNo);
                }
                if (cardPrintedNo != null) {
                    fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cardPrintedNo);
                }
                try {
                    if (cardType != null) {
                        fc.setValue(ClientManager.FieldId.CARD_TYPE, Integer.parseInt(cardType));
                    }
                } catch (Exception e) {
                    if (!cardType.equals("")) {
                        throw new FrontControllerException("Неправильный формат поля cardType");
                    }
                }
                if (cardExpiry != null) {
                    fc.setValue(ClientManager.FieldId.CARD_EXPIRY, CalendarUtils.parseDate(cardExpiry));
                }
                if (cardIssued != null) {
                    fc.setValue(ClientManager.FieldId.CARD_ISSUED, CalendarUtils.parseDate(cardIssued));
                }
                if (snils != null) {
                    fc.setValue(ClientManager.FieldId.SAN, snils);
                }
                if (birthDate != null) {
                    fc.setValue(ClientManager.FieldId.BIRTH_DATE, birthDate);
                }
                if (gender != null) {
                    fc.setValue(ClientManager.FieldId.GENDER, gender);
                }
                if (middleGroup != null) {
                    fc.setValue(ClientManager.FieldId.MIDDLE_GROUP, middleGroup);
                }

                logger.debug("register client v2");
                boolean noComment = true;
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("soap метод registerClientsV2 (фронт)");
                if (orgId != null) {
                    Org org = DAOReadonlyService.getInstance().findOrg(orgId);
                    if (org != null) {
                        clientsMobileHistory.setOrg(org);
                    }
                    clientsMobileHistory.setShowing("АРМ ОО (ид." + orgId + ")");
                } else {
                    clientsMobileHistory.setShowing("АРМ");
                }
                if (guidStaff != null) {
                    clientsMobileHistory.setStaffguid(guidStaff);
                }
                long idOfClient = ClientManager
                        .registerClient(Long.parseLong(orgIdForClient), fc, checkFullNameUniqueness, noComment,
                                clientsMobileHistory);
                results.add(new RegisterClientResult(idOfClient, recId, true, null));
            } catch (Exception e) {
                results.add(new RegisterClientResult(null, recId, false, e.getMessage()));
            }
        }
        return results;
    }

    private String getClientParamDescValueByName(String paramName, List<ClientDescV2.ClientDescItemParam> params) {
        for (ClientDescV2.ClientDescItemParam param : params) {
            if (param.paramName.equalsIgnoreCase(paramName)) {
                return param.paramValue;
            }
        }
        return null;
    }

    @WebMethod(operationName = "registerClients")
    public List<RegisterClientResult> registerClients(@WebParam(name = "orgId") Long orgId,
                                                      @WebParam(name = "clientDescList") List<ClientDesc> clientDescList,
                                                      @WebParam(name = "checkFullNameUniqueness") boolean checkFullNameUniqueness)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        String notifyByPush =
                RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
        String notifyByEmail =
                RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";

        LinkedList<RegisterClientResult> results = new LinkedList<RegisterClientResult>();
        for (ClientDesc cd : clientDescList) {
            try {
                //ClientManager.ClientFieldConfig fc = ClientDesc.buildClientFieldConfig(cd);
                logger.debug("create FieldConfig");
                ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
                logger.debug("check client params");
                if (cd.contractSurname != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, cd.contractSurname);
                } else {
                    fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, " ");
                }
                if (cd.contractName != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_NAME, cd.contractName);
                }
                if (cd.contractSecondName != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_SECONDNAME, cd.contractSecondName);
                }
                if (cd.contractDoc != null) {
                    fc.setValue(ClientManager.FieldId.CONTRACT_DOC, cd.contractDoc);
                }
                if (cd.surname != null) {
                    fc.setValue(ClientManager.FieldId.SURNAME, cd.surname);
                }
                if (cd.name != null) {
                    fc.setValue(ClientManager.FieldId.NAME, cd.name);
                }
                if (cd.secondName != null) {
                    fc.setValue(ClientManager.FieldId.SECONDNAME, cd.secondName);
                }
                if (cd.doc != null) {
                    fc.setValue(ClientManager.FieldId.DOC, cd.doc);
                }
                if (cd.address != null) {
                    fc.setValue(ClientManager.FieldId.ADDRESS, cd.address);
                }
                if (cd.phone != null) {
                    fc.setValue(ClientManager.FieldId.PHONE, cd.phone);
                }
                if (cd.mobilePhone != null) {
                    fc.setValue(ClientManager.FieldId.MOBILE_PHONE, cd.mobilePhone);
                }
                if (cd.email != null) {
                    fc.setValue(ClientManager.FieldId.EMAIL, cd.email);
                }
                if (cd.group != null) {
                    fc.setValue(ClientManager.FieldId.GROUP, cd.group);
                }
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, cd.notifyBySms ? "1" : "0");
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                fc.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                if (cd.comments != null) {
                    fc.setValue(ClientManager.FieldId.COMMENTS, cd.comments);
                }
                if (cd.cardNo != null) {
                    fc.setValue(ClientManager.FieldId.CARD_ID, cd.cardNo);
                }
                if (cd.cardPrintedNo != null) {
                    fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cd.cardPrintedNo);
                }
                fc.setValue(ClientManager.FieldId.CARD_TYPE, cd.cardType);
                if (cd.cardExpiry != null) {
                    fc.setValue(ClientManager.FieldId.CARD_EXPIRY, cd.cardExpiry);
                }
                if (cd.cardIssued != null) {
                    fc.setValue(ClientManager.FieldId.CARD_ISSUED, cd.cardIssued);
                }
                if (cd.snils != null) {
                    fc.setValue(ClientManager.FieldId.SAN, cd.snils);
                }
                logger.debug("register client");
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("soap метод registerClients (фронт)");
                clientsMobileHistory.setShowing("АРМ");
                long idOfClient = ClientManager.registerClient(orgId, fc, checkFullNameUniqueness, false,
                        clientsMobileHistory);
                results.add(new RegisterClientResult(idOfClient, cd.recId, true, null));
            } catch (Exception e) {
                results.add(new RegisterClientResult(null, cd.recId, false, e.getMessage()));
            }
        }
        return results;
    }

    @WebMethod(operationName = "getFriendlyOrganizations")
    public List<SimpleOrganizationItem> getFriendlyOrganizations(@WebParam(name = "orgId") Long orgId)
            throws FrontControllerException {
        logger.debug("checkRequestValidity");
        checkRequestValidity(orgId);

        List<SimpleOrganizationItem> result = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            result = new LinkedList<SimpleOrganizationItem>();
            List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistenceSession, orgId);
            for (Org item : friendlyOrgs) {
                result.add(
                        new SimpleOrganizationItem(item.getIdOfOrg(), item.getShortName(), item.getType().ordinal()));
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
        if (!remoteIp.matches(ipPattern)) {
            throw new FrontControllerException(
                    "Запрос с входящего узла обязан проходить проходить проверку сертификатов");
        }
    }

    private String getApiKey() {
        return RuntimeContext.getInstance().getFrontControllerApiKey();
    }

    private void checkRequestValidityExtended(Long orgId) throws FrontControllerException {
        try {
            checkRequestValidity(orgId);
        } catch (FrontControllerException e) {
            if (!e.msg.contains("Ключ сертификата невалиден") && !e.msg.contains("У организации не установлен открытый ключ")) {
                throw e;
            }
            try {
                Set<Org> orgs = DAOReadonlyService.getInstance().findFriendlyOrgs(orgId);
                PublicKey publicKey;
                MessageContext msgContext = wsContext.getMessageContext();
                HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
                X509Certificate[] certs = getCertificateFromContextOrHeaders(request);
                for (Org org : orgs) {
                    if (org.getPublicKey() == null || org.getPublicKey().trim().isEmpty()) {
                        logger.info(String.format("У организации не установлен открытый ключ: %d", orgId));
                    } else {
                        publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
                        if (publicKey.equals(certs[0].getPublicKey())) {
                            return;
                        }
                    }
                }
            } catch (Exception e2) {
                throw new FrontControllerException("Внутренняя ошибка", e2);
            }
            if (e.msg.contains("Ключ сертификата невалиден"))
                throw new FrontControllerException(String.format("Ключ сертификата невалиден: %d", orgId));
            if (e.msg.contains("У организации не установлен открытый ключ"))
                throw new FrontControllerException(String.format("У организации не установлен открытый ключ: %d", orgId));
        }
    }

    private void checkRequestValidity(Long orgId) throws FrontControllerException {
        if (RuntimeContext.getInstance().isTestMode()) {
            return;
        }
        X509Certificate[] certs;
        PublicKey publicKey;
        try {
            MessageContext msgContext = wsContext.getMessageContext();
            HttpServletRequest request = (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);

            String apiKey = getApiKey();
            if (!StringUtils.isEmpty(apiKey)) {
                String requestApiKey = request.getHeader("API-KEY");
                if (!StringUtils.isEmpty(requestApiKey)) {
                    if (!requestApiKey.equals(apiKey)) {
                        throw new FrontControllerException("Invalid API KEY");
                    } else {
                        return;
                    }
                }
            }

            certs = getCertificateFromContextOrHeaders(request);

            if (certs == null || certs.length == 0) {
                throw new FrontControllerException("В запросе нет валидных сертификатов, idOfOrg: " + orgId);
            }

            Org org = DAOReadonlyService.getInstance().findOrg(orgId);
            if (org == null) {
                throw new FrontControllerException(String.format("Неизвестная организация: %d", orgId));
            }

            if (org.getPublicKey() == null || org.getPublicKey().trim().isEmpty()) {
                throw new FrontControllerException(
                        String.format("У организации не установлен открытый ключ: %d", orgId));
            } else {
                publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
                if (!publicKey.equals(certs[0].getPublicKey())) {
                    throw new FrontControllerException(String.format("Ключ сертификата невалиден: %d", orgId));
                }
            }
        } catch (FrontControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new FrontControllerException("Внутренняя ошибка", e);
        }
    }

    // Получение клиенсткого сертификата из контекста или из заголовка запроса
    private X509Certificate[] getCertificateFromContextOrHeaders(HttpServletRequest request) throws Exception {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            return certs;
        } else {
            String urlCert = request.getHeader("X-SSL-CERT");
            if (StringUtils.isEmpty(urlCert)) {
                return null;
            }
            String strCert = URLDecoder.decode(urlCert, StandardCharsets.UTF_8.name());
            strCert = strCert.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, "");
            byte[] decodedCert = Base64.decode(strCert);
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(decodedCert));

            certs = new X509Certificate[]{cert};
            return certs;
        }
    }

    @WebMethod(operationName = "generateLinkingToken")
    public String generateLinkingToken(@WebParam(name = "orgId") Long orgId,
                                       @WebParam(name = "idOfClient") Long idOfClient) throws Exception {
        checkRequestValidity(orgId);

        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.findClientById(idOfClient);
        if (client == null) {
            throw new FrontControllerException(String.format("Клиент не найден: %d", idOfClient));
        }
        if (!daoReadonlyService.doesClientBelongToFriendlyOrgs(orgId, idOfClient)) {
            throw new FrontControllerException(String.format("Клиент %d не принадлежит организации", idOfClient));
        }
        LinkingToken linkingToken = DAOService.getInstance().generateLinkingToken(client);
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

        public RegisterClientResult() {
        }

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
                                                      @WebParam(name = "cardSignCertNum") Integer cardSignCertNum,
                                                      @WebParam(name = "isLongUid") boolean isLongUid, @WebParam(name = "forceRegister") Integer forceRegister,
                                                      @WebParam(name = "longCardNo") Long longCardNo) throws FrontControllerException {
        checkRequestValidity(idOfOrg);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        logger.info(String.format(
                "Incoming registerCardWithoutClient request. orgId=%s, cardNo=%s, cardPrintedNo=%s, type=%s, "
                        + "cardSignVerifyRes=%s, cardSighCertNum=%s, isLongUid=%s, forceRegister=%s, longCardNo=%s",
                idOfOrg, cardNo, cardPrintedNo, type, cardSignVerifyRes, cardSignCertNum, isLongUid, forceRegister,
                longCardNo));
        CardService cardService = CardService.getInstance();
        if (!(type >= 0 && type < Card.TYPE_NAMES.length)) {
            return new CardResponseItem(CardResponseItem.ERROR_INVALID_TYPE,
                    CardResponseItem.ERROR_INVALID_TYPE_MESSAGE);
        }
        Card card;
        Long idOfCard;
        CardTransitionState transitionState = null;
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org org = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (org.longCardNoIsOn() && longCardNo == null) {
                throw new CardResponseItem.LongCardNoNotSpecified(CardResponseItem.ERROR_LONG_CARDNO_MATCH_ORG_MESSAGE);
            }
            Card exCard = null;
            if (!org.longCardNoIsOn()) {
                if (VersionUtils.doublesAllowed(persistenceSession, idOfOrg) && org.getNeedVerifyCardSign()) {
                    exCard = DAOUtils.findCardByCardNoDoublesAllowed(persistenceSession, org, cardNo, cardPrintedNo,
                            cardSignCertNum, type);
                } else {
                    exCard = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
                }
            } else {
                if (!isLongUid) {
                    exCard = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
                    if (exCard != null && !exCard.getState().equals(CardState.BLOCKED.getValue())) {
                        throw new CardResponseItem.CardAlreadyExist(CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE);
                    }
                } else {
                    //Идет регистрация 7-байтной карты
                    //Вначале проверяем на дубли среди старых карт (у них не было longCardNo)
                    Card cardNullLong = DAOUtils.findCardByCardNoAndWithoutLongCardNo(persistenceSession, cardNo);
                    if (cardNullLong != null)
                    {
                        if (cardNullLong.getIsLongUid() == null || cardNullLong.getIsLongUid())
                        {
                            //в этом случае карта также считается 7-байтной
                            // (т.е. она зарегистрирована ДО внедрения longCardNo, но флаг isLongUid выставлен)
                            if (cardPrintedNo == cardNullLong.getCardPrintedNo())
                            {
                                //Если у найденной карты и регистрируемой одинаковый cardPrintedNo, то это дубль
                                exCard = cardNullLong;

                            }
                        }
                    }
                    if (exCard == null) {
                        //Проверяем для новых карт (у них есть longCardNo)
                        exCard = DAOUtils.findCardByLongCardNoWithUniqueCheck(persistenceSession, longCardNo);
                    }
//                    //Если дубль найден, то идет проверка на принудительную регистрацию
//                    if (exCard != null && (forceRegister == null || forceRegister != 1)) {
//                        throw new NoUniqueCardNoException(CardResponseItem.ERROR_LONG_CARDNO_NOT_UNIQUE_MESSAGE);
//                    }
                }
            }
            if (null == exCard) {
                card = cardService
                        .registerNew(org, cardNo, cardPrintedNo, type, longCardNo, cardSignVerifyRes, cardSignCertNum, isLongUid);
            } else {
                if (VersionUtils.compareClientVersionForRegisterCard(persistenceSession, idOfOrg) < 0) {
                    throw new CardResponseItem.CardAlreadyExist(CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE);
                }

                boolean secondRegisterAllowed = VersionUtils.secondRegisterAllowed(persistenceSession, idOfOrg);

                if (exCard.getState() != CardState.BLOCKED.getValue() && !secondRegisterAllowed  && !Card.isServiceType(type)) {
                    throw new CardResponseItem.CardAlreadyExist(CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE);
                } else if (!secondRegisterAllowed) {
                    testForRegisterConditions(persistenceSession, exCard, idOfOrg, secondRegisterAllowed, type);
                }
                if (secondRegisterAllowed && (forceRegister == null || forceRegister != 1)) {
                    throw new CardResponseItem.CardAlreadyExistSecondRegisterAllowed(
                            CardResponseItem.ERROR_DUPLICATE_CARD_SECOND_REGISTER_MESSAGE + exCard.getOrg()
                                    .getShortNameInfoService() + ". Статус: " + CardState
                                    .fromInteger(exCard.getState()));
                }

                card = cardService
                        .registerNew(org, cardNo, cardPrintedNo, type, longCardNo, cardSignVerifyRes, cardSignCertNum,
                                isLongUid, CardTransitionState.BORROWED.getCode());

                if (secondRegisterAllowed && exCard.getState() != CardState.BLOCKED.getValue()) {
                    cardService.blockAndReset(exCard.getCardNo(), exCard.getOrg().getIdOfOrg(),
                            exCard.getClient() == null ? null : exCard.getClient().getIdOfClient(), false,
                            CardResponseItem.USED_IN_ANOTHER_ORG, CardTransitionState.GIVEN_AWAY.getCode());
                } else {
                    cardService.updateTransitionState(exCard, CardTransitionState.GIVEN_AWAY.getCode());
                    persistenceSession.update(exCard);
                }
            }
            idOfCard = card.getIdOfCard();
            transitionState = CardTransitionState.fromInteger(card.getTransitionState());
            idOfCard = card.getIdOfCard();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (CardResponseItem.CardAlreadyExist e) {
            logger.error("CardAlreadyExistException: ", e);
            return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE, e.getMessage());
        } catch (CardResponseItem.LongCardNoNotSpecified e) {
            logger.error("LongCardNoNotSpecified: ", e);
            return new CardResponseItem(CardResponseItem.ERROR_LONG_CARDNO_MATCH_ORG, e.getMessage());
        } catch (NoUniqueCardNoException e) {
            logger.error("NoUniqueCardNoException: ", e);
            return new CardResponseItem(CardResponseItem.ERROR_LONG_CARDNO_NOT_UNIQUE, CardResponseItem.ERROR_LONG_CARDNO_NOT_UNIQUE_MESSAGE);
        } catch (CardResponseItem.CardAlreadyExistInYourOrg e) {
            logger.error("CardAlreadyExistInYourOrgException: ", e);
            return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE, e.getMessage());
        } catch (CardResponseItem.CardAlreadyExistSecondRegisterAllowed e) {
            logger.error("CardAlreadyExistSecondRegisterAllowed: ", e);
            return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE_FOR_SECOND_REGISTER, e.getMessage());
        } catch (Exception e) {
            if (e.getMessage() == null) {
                logger.error("Error in register card", e);
                return new CardResponseItem(CardResponseItem.ERROR_INTERNAL, CardResponseItem.ERROR_INTERNAL_MESSAGE);
            }
            if (e.getMessage().contains("ConstraintViolationException")) {
                return new CardResponseItem(CardResponseItem.ERROR_DUPLICATE,
                        CardResponseItem.ERROR_DUPLICATE_CARD_MESSAGE);
            } else if (e instanceof IllegalStateException) {
                return new CardResponseItem(CardResponseItem.ERROR_SIGN_VERIFY,
                        CardResponseItem.ERROR_SIGN_VERIFY_MESSAGE);
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

    private void testForRegisterConditions(Session persistenceSession, Card exCard, long idOfOrg,
            boolean secondRegisterAllowed, int type) throws Exception {
        if (!secondRegisterAllowed) {
            Integer blockPeriod = getBlockPeriodByCardType(type);
            Date now = new Date();
            if (blockPeriod >= CalendarUtils.getDifferenceInDays(exCard.getUpdateTime(), now)) {
                throw new CardResponseItem.CardAlreadyExist(
                        String.format("%s. Минимальный срок блокировки карты не прошел - %dд",
                                CardResponseItem.ERROR_CARD_ALREADY_EXIST_MESSAGE, blockPeriod));
            }
        }

        List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(persistenceSession, exCard.getOrg().getIdOfOrg());
        for (Org o : friendlyOrgs) {
            if (o.getIdOfOrg() == idOfOrg) {
                throw new CardResponseItem.CardAlreadyExistInYourOrg(
                        CardResponseItem.ERROR_CARD_ALREADY_EXIST_IN_YOUR_ORG_MESSAGE);
            }
        }
    }

    private Integer getBlockPeriodByCardType(int type) {
        CardBlockPeriodConfig cardBlockPeriodConfig = RuntimeContext.getInstance().getCardBlockPeriodConfig();
        for (CardBlockPeriodConfig.BlockPeriodCardTypes blockPeriodCardTypes: cardBlockPeriodConfig.getList()) {
            for (Integer cardType: blockPeriodCardTypes.getCardTypes()) {
                if (cardType == type) return blockPeriodCardTypes.getPeriod();
            }
        }
        return RuntimeContext.getInstance()
                .getPropertiesValue(CardBlockPeriodConfig.PARAM_BASE, 180);
    }

    @WebMethod(operationName = "getEnterEventsManual")
    public List<EnterEventManualItem> getEnterEventsManual(@WebParam(name = "orgId") long idOfOrg)
            throws FrontControllerException {
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

    private List<ExternalEventItem> getExternalEventsInternal(Long idOfOrg, long version)
            throws FrontControllerException {
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
                                                    @WebParam(name = "mode") int mode, @WebParam(name = "group") String group,
                                                    @WebParam(name = "requestDate") long requestDate) throws FrontControllerException {
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
    public List<SimpleEnterEventItem> getEnterEvents(@WebParam(name = "idOfOrg") long idOfOrg,
                                                     @WebParam(name = "groupName") String groupName, @WebParam(name = "requestDate") long requestDate)
            throws FrontControllerException {
        try {
            Date beginDate = CalendarUtils.truncateToDayOfMonth(new Date(requestDate));
            Date endDate = CalendarUtils.addOneDay(beginDate);
            List<SimpleEnterEventItem> list = new ArrayList<SimpleEnterEventItem>();
            List<EnterEvent> eeList = DAOReadonlyService.getInstance()
                    .getEnterEventsByOrgAndGroup(idOfOrg, groupName, beginDate, endDate);
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
    public String getCardSignVerifyKey(@WebParam(name = "idOfOrg") long idOfOrg,
                                       @WebParam(name = "cardSignCertNum") int cardSignCertNum, @WebParam(name = "signType") Integer signType)
            throws FrontControllerException {
        //checkRequestValidity(idOfOrg);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardSign cardSign = (CardSign) session.load(CardSign.class, cardSignCertNum);
            if (cardSign == null) {
                throw new FrontControllerException("Ключ не найден по входным данным");
            }
            byte[] signVerifyData = DAOReadonlyService.getInstance()
                    .getCardSignVerifyData(cardSignCertNum, signType, cardSign.getNewtypeprovider());
            //Здесь сформируется конечный вариант
            if (signVerifyData == null) {
                throw new FrontControllerException("Ключ не найден по входным данным");
            }
            //Для старого типа поставщиков
            if (!cardSign.getNewtypeprovider()) {
                return Base64.encodeBytes(signVerifyData);
            }
            //Если тип подписи Scrips и длина ключа более 64 байт...
            if (signType == 0 && signVerifyData.length > 64) {
                byte[] privKeyCard = new byte[64];
                System.arraycopy(signVerifyData, 0, privKeyCard, 0, 64);
                return Base64.encodeBytes(privKeyCard);
            }
            return Base64.encodeBytes(signVerifyData);
        } catch (Exception e) {
            logger.error("Ошибка при получении ключа цифровой подписи для верификации карты", e);
            throw new FrontControllerException(String.format("Ошибка: %s", e.getMessage()));
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
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
                        org.getVersion(), org.getGuid(), org.getDistrict(), org.getShortNameInfoService(),
                        orgService.getMainBulding(org).getIdOfOrg());
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
            for (ClientPhoto clientPhoto : clientPhotos) {
                clientsIds.add(clientPhoto.getIdOfClient());
            }
            List<Client> clientList = DAOUtils.findClients(persistenceSession, clientsIds);
            Map<Long, Client> clientMap = new HashMap<Long, Client>();
            for (Client client : clientList) {
                clientMap.put(client.getIdOfClient(), client);
            }

            for (ClientPhoto clientPhoto : clientPhotos) {
                ImageUtils.PhotoContent photoContent = ImageUtils
                        .getPhotoContent(clientMap.get(clientPhoto.getIdOfClient()), clientPhoto,
                                ImageUtils.ImageSize.SMALL.getValue(), true);
                Client guardian = clientPhoto.getGuardian();
                String guardianName = null;
                if (guardian != null) {
                    guardianName = guardian.getPerson().getFullName();
                }
                ClilentPhotoChangeItem item = new ClilentPhotoChangeItem(clientPhoto.getIdOfClient(),
                        photoContent.getBytes(), photoContent.getHash(), clientPhoto.getLastProceedError(),
                        guardianName);
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
            for (ClientPhotoChangeResult result : results) {
                clientIdsList.add(result.getClientId());
            }
            List<Client> clientList = DAOUtils.findClients(persistenceSession, clientIdsList);
            Map<Long, Client> clientMap = new HashMap<Long, Client>();
            for (Client client : clientList) {
                clientMap.put(client.getIdOfClient(), client);
            }
            List<ClientPhoto> clientPhotosList = ImageUtils.findClientPhotos(persistenceSession, clientIdsList);
            Map<Long, ClientPhoto> clientPhotoMap = new HashMap<Long, ClientPhoto>();
            for (ClientPhoto clientPhoto : clientPhotosList) {
                clientPhotoMap.put(clientPhoto.getIdOfClient(), clientPhoto);
            }

            for (ClientPhotoChangeResult result : results) {
                Client client = clientMap.get(result.getClientId());
                ClientPhoto clientPhoto = clientPhotoMap.get(client.getIdOfClient());
                int currentPhotoHash = ImageUtils
                        .getPhotoHash(client, clientPhoto, ImageUtils.ImageSize.SMALL.getValue(), true);
                if (result.getState() == 1) {
                    if (result.getSrc() == currentPhotoHash) {
                        try {
                            ImageUtils.moveImage(client, clientPhoto);
                            clientPhoto.setIsNew(false);
                            clientPhoto.setIsCanceled(false);
                            clientPhoto.setIsApproved(true);
                            clientPhoto.setLastProceedError(null);
                            Long nextVersion = DAOUtils.nextVersionByClientPhoto(persistenceSession);
                            clientPhoto.setVersion(nextVersion);
                            persistenceSession.update(clientPhoto);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            String error =
                                    "Не удалось принять фото-расхождение. Обратитесь к администратору сервера: " + e
                                            .getMessage();
                            if (error.length() > 256) {
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
                if (result.getState() == 2) {
                    if (result.getSrc() == currentPhotoHash) {
                        boolean deleted = ImageUtils.deleteImage(client, clientPhoto, true);
                        if (!deleted) {
                            clientPhoto.setLastProceedError(
                                    "Не удалось удалить фото-расхождение. Обратитесь к администратору сервера.");
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

    @WebMethod(operationName = "getBalancesForPayPlan")
    public PayPlanBalanceListResponse getBalancesForPayPlan(@WebParam(name = "orgId") Long orgId,
                                                            @WebParam(name = "balanceList") PayPlanBalanceList payPlanBalanceList) throws FrontControllerException {
        checkRequestValidityExtended(orgId);
        PayPlanBalanceListResponse result = new PayPlanBalanceListResponse();
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            for (PayPlanBalanceItem item : payPlanBalanceList.getItems()) {
                PayPlanBalanceItem resultItem = new PayPlanBalanceItem(item.getIdOfClient());
                Client client = DAOReadonlyService.getInstance().findClientById(item.getIdOfClient());
                if (client == null) {
                    throw new Exception(
                            "Client not found in getBalancesForPayPlan. IdOfClient = " + item.getIdOfClient());
                }
                long preorderSum = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getNotPaidPreordersSum(client, CalendarUtils.startOfDay(new Date()));
                long resultSum = client.getBalance() - item.getSumma() - preorderSum;
                resultItem.setSumma(resultSum);
                result.addItem(resultItem);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error in getBalancesForPayPlan", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @WebMethod(operationName = "unblockOrReturnCard")
    public ResponseItem unblockOrReturnCard(@WebParam(name = "cardNo") Long cardNo,
                                            @WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "longCardNo") Long longCardNo)
            throws FrontControllerException {
        //checkRequestValidity(idOfOrg);
        if (longCardNo != null && longCardNo.equals(-1L)) { // Если АРМ прислал -1, то считать поле как NULL
            longCardNo = null;
        }
        ResponseItem responseItem = new ResponseItem();
        try {
            Card card = CardService.getInstance().unblockOrReturnCard(cardNo, longCardNo, idOfOrg);
            CardService.getInstance().updateSyncStatus(card, idOfOrg, 0L, true);
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
    public List<FindClientResult> findClient(@WebParam(name = "orgId") Long orgId,
                                             @WebParam(name = "findClientFieldList") FindClientField findClientField) throws FrontControllerException {
        checkRequestValidity(orgId);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            String mobilePhone = FrontControllerProcessor
                    .getFindClientFieldValueByName(FindClientField.FIELD_MOBILE, findClientField);

            if (StringUtils.isEmpty(mobilePhone)) {
                throw new FrontControllerException(
                        String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                                FindClientField.FIELD_MOBILE));
            }

            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (null == mobilePhone) {
                throw new FrontControllerException(ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE_MESSAGE);
            }

            String groupNames = FrontControllerProcessor
                    .getFindClientFieldValueByName(FindClientField.FIELD_GROUP_NAMES, findClientField);
            if (StringUtils.isEmpty(groupNames)) {
                throw new FrontControllerException(
                        String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                                FindClientField.FIELD_GROUP_NAMES));
            }
            String[] groupNameArray = StringUtils.split(groupNames, ",");
            List<String> groupNameList = new LinkedList<String>();
            for (String groupName : groupNameArray) {
                groupNameList.add(StringUtils.trim(groupName));
            }

            List<Client> clientList = DAOUtils
                    .findClientsByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(persistenceSession, mobilePhone,
                            groupNameList);
            if (clientList.isEmpty()) {
                return null;
            }

            List<FindClientResult> findClientResultList = new LinkedList<FindClientResult>();

            for (Client client : clientList) {
                FindClientResult findClientResult = new FindClientResult();

                Org org = client.getOrg();
                if (null != org) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_ORG_ID,
                                    org.getIdOfOrg().toString()));
                }

                findClientResult.getFindClientDescParams().getParam()
                        .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_CLIENT_ID,
                                client.getIdOfClient().toString()));
                findClientResult.getFindClientDescParams().getParam()
                        .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_CLIENT_GUID,
                                client.getClientGUID()));

                Person person = client.getPerson();
                if (null != person) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_SURNAME,
                                    person.getSurname()));
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_FIRST_NAME,
                                    person.getFirstName()));
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_SECOND_NAME,
                                    person.getSecondName()));
                }

                ClientGroup clientGroup = client.getClientGroup();
                if (null != clientGroup) {
                    findClientResult.getFindClientDescParams().getParam()
                            .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_GROUP,
                                    clientGroup.getGroupName()));
                }
                findClientResult.getFindClientDescParams().getParam()
                        .add(new FindClientResult.FindClientItemParam(FindClientResult.FIELD_ORG_NAME,
                                client.getOrg().getShortNameInfoService()));

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
    public List<RegisterGuardianResult> registerGuardian(
            @WebParam(name = "orgId") Long orgId,
            @WebParam(name = "guardianDescList") GuardianDesc guardianDescList,
            @WebParam(name = "guidStaff") String guidStaff
    ) throws FrontControllerException {
        checkRequestValidity(orgId);

        String firstName = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_FIRST_NAME, guardianDescList);
        if (StringUtils.isEmpty(firstName)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_FIRST_NAME));
        }
        String secondName = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_SECOND_NAME, guardianDescList);
        String surname = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_SURNAME, guardianDescList);
        if (StringUtils.isEmpty(surname)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_SURNAME));
        }
        String group = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_GROUP, guardianDescList);
        if (StringUtils.isEmpty(group)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_GROUP));
        }
        String relationDegree = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_RELATION_DEGREE, guardianDescList);
        if (StringUtils.isEmpty(relationDegree)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_RELATION_DEGREE));
        }
        String legalityStr = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_LEGALITY, guardianDescList);
        if (StringUtils.isEmpty(legalityStr)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_LEGALITY));
        }

        Integer legality = convertLegality(legalityStr);

        String gender = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_GENDER, guardianDescList);
        if (StringUtils.isEmpty(gender)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_GENDER));
        }

        String guardianBirthDayStr = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_GUARDIAN_BIRTHDAY, guardianDescList);
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

        String mobilePhone = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_MOBILE, guardianDescList);

        if (StringUtils.isEmpty(mobilePhone)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_MOBILE));
        }

        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (null == mobilePhone) {
            throw new FrontControllerException(ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE_MESSAGE);
        }

        String clientIdStr = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_CLIENT_ID, guardianDescList);
        if (StringUtils.isEmpty(clientIdStr)) {
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                            GuardianDesc.FIELD_CLIENT_ID));
        }

        Long clientId;
        try {
            clientId = Long.parseLong(clientIdStr);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            throw new FrontControllerException(
                    String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
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
                    .findClientByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(persistenceSession, mobilePhone,
                            groupNameList);
            if (null != client) {

                List<RegisterGuardianResult> registerGuardianResultList = new LinkedList<RegisterGuardianResult>();
                RegisterGuardianResult registerGuardianResult = new RegisterGuardianResult();

                Org org = client.getOrg();
                if (null != org) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                    RegisterGuardianResult.FIELD_ORG_ID, org.getIdOfOrg().toString()));
                }

                registerGuardianResult.getRegisterGuardianDescParams().getParam()
                        .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                RegisterGuardianResult.FIELD_CLIENT_ID, client.getIdOfClient().toString()));
                registerGuardianResult.getRegisterGuardianDescParams().getParam()
                        .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                RegisterGuardianResult.FIELD_CLIENT_GUID, client.getClientGUID()));

                Person person = client.getPerson();
                if (null != person) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                    RegisterGuardianResult.FIELD_SURNAME, person.getSurname()));
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                    RegisterGuardianResult.FIELD_FIRST_NAME, person.getFirstName()));
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                    RegisterGuardianResult.FIELD_SECOND_NAME, person.getSecondName()));
                }

                ClientGroup clientGroup = client.getClientGroup();
                if (null != clientGroup) {
                    registerGuardianResult.getRegisterGuardianDescParams().getParam()
                            .add(new RegisterGuardianResult.RegisterGuardianItemParam(
                                    RegisterGuardianResult.FIELD_GROUP, clientGroup.getGroupName()));
                }
                registerGuardianResult.getRegisterGuardianDescParams().getParam()
                        .add(new RegisterGuardianResult.RegisterGuardianItemParam(RegisterGuardianResult.FIELD_ORG_NAME,
                                client.getOrg().getShortNameInfoService()));
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

            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("soap метод registerGuardian (фронт)");
            clientsMobileHistory.setOrg(org);
            clientsMobileHistory.setShowing("АРМ ОО (ид." + orgId + ")");
            clientsMobileHistory.setStaffguid(guidStaff);
            Long idOfClient = ClientManager.registerClient(orgId, fc, false, true,
                    clientsMobileHistory);

            Client guardian = (Client) persistenceSession.load(Client.class, idOfClient);
            //
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setOrg(org);
            clientGuardianHistory.setReason("Веб метод registerGuardian (front)");
            clientGuardianHistory.setWebAdress(req.getRemoteAddr());

            //
            ClientGuardian clientGuardian = ClientManager
                    .createClientGuardianInfoTransactionFree(persistenceSession, guardian, relationDegree, null, false,
                            clientId, ClientCreatedFromType.ARM, null, clientGuardianHistory);

            clientGuardian.setRepresentType(ClientGuardianRepresentType.fromInteger(legality));
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

    private Integer convertLegality(String legality_str) {
        if (legality_str.equals("true")) {
            return 1;
        }
        if (legality_str.equals("false")) {
            return 0;
        }
        return Integer.parseInt(legality_str);
    }

    @WebMethod(operationName = "registerGuardianMigrantRequest")
    public ResponseItem registerGuardianMigrantRequest(@WebParam(name = "orgId") Long orgId,
                                                       @WebParam(name = "guardianDescList") GuardianDesc guardianDescList) throws FrontControllerException {
        checkRequestValidity(orgId);

        ResponseItem result = new ResponseItem();

        String clientIdStr = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_CLIENT_ID, guardianDescList);
        if (StringUtils.isEmpty(clientIdStr)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_CLIENT_ID);
            return result;
        }

        Long clientId;
        try {
            clientId = Long.parseLong(clientIdStr);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_CLIENT_ID);
            return result;
        }

        String guardianIdString = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_GUARDIAN_ID, guardianDescList);
        if (StringUtils.isEmpty(guardianIdString)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_GUARDIAN_ID);
            return result;
        }

        Long guardianId;
        try {
            guardianId = Long.parseLong(guardianIdString);
        } catch (NumberFormatException e) {
            logger.error("Error in registerGuardian", e);
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_GUARDIAN_ID);
            return result;
        }

        String relationDegree = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_RELATION_DEGREE, guardianDescList);
        if (StringUtils.isEmpty(relationDegree)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_RELATION_DEGREE);
            return result;
        }
        String legalityStr = FrontControllerProcessor
                .getFindClientFieldValueByName(GuardianDesc.FIELD_LEGALITY, guardianDescList);
        if (StringUtils.isEmpty(legalityStr)) {
            result.code = ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED;
            result.message = String.format("%s: %s", ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE,
                    GuardianDesc.FIELD_LEGALITY);
            return result;
        }

        Integer legality = convertLegality(legalityStr);

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

            Client child = (Client) persistenceSession.load(Client.class, clientId);
            if (null == child) {
                result.code = ResponseItem.ERROR_CLIENT_NOT_FOUND;
                result.message = ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE;
                return result;
            }

            ClientGuardian existingRef = DAOUtils.findClientGuardian(persistenceSession, clientId, guardianId);
            if (existingRef == null) {
                //
                MessageContext mc = wsContext.getMessageContext();
                HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
                ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
                clientGuardianHistory.setOrg(org);
                clientGuardianHistory.setReason("Веб метод registerGuardianMigrantRequest (front)");
                clientGuardianHistory.setWebAdress(req.getRemoteAddr());
                //
                ClientGuardian clientGuardian = ClientManager
                        .createClientGuardianInfoTransactionFree(persistenceSession, guardian, relationDegree, null,
                                false, clientId, ClientCreatedFromType.ARM, null, clientGuardianHistory);

                clientGuardian.setRepresentType(ClientGuardianRepresentType.fromInteger(legality));
                persistenceSession.merge(clientGuardian);
            } else {
                logger.warn(String.format(
                        "In registerGuardianMigrantRequest: ClientGuardian Reference already exist between Guardian ID: %d and Client^ %d",
                        guardianId, clientId));
            }

            if (!DAOUtils.isFriendlyOrganizations(persistenceSession, guardian.getOrg(), child.getOrg())) {
                ClientManager.createMigrationForGuardianWithConfirm(persistenceSession, guardian, fireTime, org,
                        MigrantInitiatorEnum.INITIATOR_ORG, VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT, 10);
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

    @WebMethod(operationName = "updateCardFieldsRequest")
    public UpdateCardFieldsResponse updateCardFieldsRequest(@WebParam(name = "cardInfo") CardInfo info)
            throws FrontControllerException {
        UpdateCardFieldsResponse result = new UpdateCardFieldsResponse();

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (CardInfoItem item : info.getItems()) {
                Card c = (Card) persistenceSession.get(Card.class, item.getProcessingCardId());

                if (c == null) {
                    logger.warn(
                            String.format("Card CardNo: %d LongCardId: %d not found",
                                    item.getCardNo(), item.getLongCardId())
                    );

                    result.setCode(ResponseItem.ERROR_INTERNAL);
                    result.getProblemProcessingCardIds().add(item.getProcessingCardId());
                    continue;
                }
                c.setLongCardNo(item.getLongCardId());
                c.setIsLongUid(BooleanUtils.toBoolean(item.getIsLongId()));
                c.setUpdateTime(new Date());

                persistenceSession.update(c);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;

            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in updateCardFieldsRequest", e);
            throw new FrontControllerException("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @WebMethod(operationName = "createDocumentForClient")
    public DocumentResponse createDocumentForClient(
            @WebParam(name = "documentItem") DocumentItem documentItem) {

        DulDetailService dulDetailService = RuntimeContext.getAppContext().getBean(DulDetailService.class);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Long idOfDocument;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = persistenceSession.get(Client.class, documentItem.getIdOfClient());

            if (client == null) {
                return new DocumentResponse(DocumentResponse.ERROR_CLIENT_NOT_FOUND,
                        DocumentResponse.ERROR_CLIENT_NOT_FOUND_MESSAGE);
            }
            if (documentItem.getIdOfClient() == null || documentItem.getDocumentTypeId() == null
                    || documentItem.getNumber() == null) {
                return new DocumentResponse(DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            DulDetail dulDetail = fillingDulDetail(persistenceSession, documentItem);
            MeshDocumentResponse meshDocumentResponse = dulDetailService.saveDulDetail(persistenceSession, dulDetail, client);
            if (!meshDocumentResponse.getCode().equals(MeshDocumentResponse.OK_CODE)) {
                return new DocumentResponse(meshDocumentResponse.getCode(), meshDocumentResponse.getMessage());
            }
            idOfDocument = meshDocumentResponse.getId();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in createDocumentForClient", e);
            if (e instanceof DocumentExistsException) {
                return new DocumentResponse(DocumentResponse.ERROR_DOCUMENT_EXISTS, e.getMessage());
            } else {
                return new DocumentResponse(DocumentResponse.ERROR_INTERNAL,
                        DocumentResponse.ERROR_INTERNAL_MESSAGE);
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        DocumentItem documentResponse = new DocumentItem();
        documentResponse.setIdDocument(idOfDocument);
        return new DocumentResponse(Collections.singletonList(documentResponse));
    }

    @WebMethod(operationName = "updateDocumentForClient")
    public DocumentResponse updateDocumentForClient(
            @WebParam(name = "documentItem") DocumentItem documentItem) {

        DulDetailService dulDetailService = RuntimeContext.getAppContext().getBean(DulDetailService.class);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (documentItem.getIdDocument() == null || documentItem.getDocumentTypeId() == null
                    || documentItem.getNumber() == null) {
                return new DocumentResponse(DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            if (persistenceSession.load(DulDetail.class, documentItem.getIdDocument()) == null) {
                return new DocumentResponse(DocumentResponse.ERROR_DOCUMENT_NOT_FOUND,
                        DocumentResponse.ERROR_DOCUMENT_NOT_FOUND_MESSAGE);
            }
            DulDetail dulDetail = fillingDulDetail(persistenceSession, documentItem);
            Client client = persistenceSession.get(Client.class, dulDetail.getIdOfClient());
            MeshDocumentResponse meshDocumentResponse = dulDetailService.updateDulDetail(persistenceSession, dulDetail, client);
            if (!meshDocumentResponse.getCode().equals(MeshDocumentResponse.OK_CODE)) {
                return new DocumentResponse(meshDocumentResponse.getCode(), meshDocumentResponse.getMessage());
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in updateDocumentForClient", e);
            return new DocumentResponse(DocumentResponse.ERROR_INTERNAL,
                    DocumentResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new DocumentResponse(DocumentResponse.OK, DocumentResponse.OK_MESSAGE);
    }

    @WebMethod(operationName = "deleteDocumentForClient")
    public DocumentResponse deleteDocumentForClient(
            @WebParam(name = "idDocument") Long idDocument) {

        DulDetailService dulDetailService = RuntimeContext.getAppContext().getBean(DulDetailService.class);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (idDocument == null) {
                return new DocumentResponse(DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            DulDetail dulDetail = persistenceSession.get(DulDetail.class, idDocument);
            if (dulDetail == null) {
                return new DocumentResponse(DocumentResponse.ERROR_DOCUMENT_NOT_FOUND,
                        DocumentResponse.ERROR_DOCUMENT_NOT_FOUND_MESSAGE);
            }
            dulDetail.setDeleteState(true);
            dulDetail.setLastUpdate(new Date());
            Client client = persistenceSession.get(Client.class, dulDetail.getIdOfClient());
            MeshDocumentResponse meshDocumentResponse = dulDetailService.deleteDulDetail(persistenceSession, dulDetail, client);
            if (!meshDocumentResponse.getCode().equals(MeshDocumentResponse.OK_CODE)) {
                return new DocumentResponse(meshDocumentResponse.getCode(), meshDocumentResponse.getMessage());
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in deleteDocumentForClient", e);
            return new DocumentResponse(DocumentResponse.ERROR_INTERNAL,
                    DocumentResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new DocumentResponse(DocumentResponse.OK, DocumentResponse.OK_MESSAGE);
    }

    @WebMethod(operationName = "getDocumentForClient")
    public DocumentResponse getDocumentForClient(
            @WebParam(name = "idOfClient") Long idOfClient) {

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (idOfClient == null) {
                return new DocumentResponse(DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        DocumentResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            Client client = persistenceSession.get(Client.class, idOfClient);
            if (client == null) {
                return new DocumentResponse(DocumentResponse.ERROR_CLIENT_NOT_FOUND,
                        DocumentResponse.ERROR_CLIENT_NOT_FOUND_MESSAGE);
            }
            List<DulDetail> dulDetails = new ArrayList<>();
            if (client.getDulDetail() != null) {
                dulDetails = client.getDulDetail().stream().filter(d -> !d.getDeleteState()).collect(Collectors.toList());
            }

            DocumentResponse documentResponse = new DocumentResponse(DocumentResponse.OK, DocumentResponse.OK_MESSAGE);
            if (client.getDulDetail() != null || !dulDetails.isEmpty()) {
                List<DocumentItem> documentItems = new ArrayList<>();
                dulDetails.forEach(dulDetail -> {
                    DocumentItem documentItem = new DocumentItem();
                    documentItem.setIdDocument(dulDetail.getId());
                    documentItem.setDocumentTypeId(dulDetail.getDocumentTypeId());
                    documentItem.setSeries(dulDetail.getSeries());
                    documentItem.setNumber(dulDetail.getNumber());
                    documentItem.setSubdivisionCode(dulDetail.getSubdivisionCode());
                    documentItem.setIssuer(dulDetail.getIssuer());
                    documentItem.setIssued(dulDetail.getIssued());
                    documentItem.setExpiration(dulDetail.getExpiration());
                    documentItems.add(documentItem);
                });
                documentResponse = new DocumentResponse(documentItems);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
            return documentResponse;
        } catch (Exception e) {
            logger.error("Error in getDocumentForClient", e);
            return new DocumentResponse(DocumentResponse.ERROR_INTERNAL, DocumentResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @WebMethod(operationName = "searchMeshPerson")
    public GuardianResponse searchMeshPerson(
            @WebParam(name = "firstname") String firstName,
            @WebParam(name = "patronymic") String patronymic,
            @WebParam(name = "lastname") String lastName,
            @WebParam(name = "genderId") Integer genderId,
            @WebParam(name = "birthDate") Date birthDate,
            @WebParam(name = "snils") String snils,
            @WebParam(name = "mobile") String mobile,
            @WebParam(name = "email") String email,
            @WebParam(name = "documents") List<DocumentItem> documents) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (checkSearchMeshPerson(firstName, lastName, genderId, birthDate, snils, documents)) {
                return new GuardianResponse(GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            List<DulDetail> dulDetails = new ArrayList<>();
            if (documents != null)
                for (DocumentItem item : documents) {
                    dulDetails.add(getDulDetailFromDocumentItem(item));
                }
            PersonListResponse personListResponse = getMeshGuardiansService()
                    .searchPerson(firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, dulDetails);

            if (!personListResponse.getCode().equals(PersonListResponse.OK_CODE)) {
                return new GuardianResponse(personListResponse.getCode(), personListResponse.getMessage());
            }
            GuardianResponse guardianResponse = new GuardianResponse(GuardianResponse.OK, GuardianResponse.OK_MESSAGE);
            if (personListResponse.getResponse() != null && !personListResponse.getResponse().isEmpty()) {
                List<String> meshGuidList = personListResponse.getResponse()
                        .stream().map(MeshGuardianPerson::getMeshGuid).collect(Collectors.toList());

                Query query = persistenceSession.createQuery("select c.meshGUID from Client c "
                        + "where meshGuid in :meshGuidList");
                query.setParameter("meshGuidList", meshGuidList);
                List<String> list = query.list();
                personListResponse.getResponse().forEach(p -> p.setAlreadyInISPP(list.contains(p.getMeshGuid())));
                guardianResponse.setPersonsList(fillingGuardianResponse(personListResponse));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
            return guardianResponse;
        } catch (Exception e) {
            logger.error("Error in searchMeshPerson", e);
            return new GuardianResponse(GuardianResponse.ERROR_INTERNAL, GuardianResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private boolean checkSearchMeshPerson(String firstName, String lastName, Integer genderId,
                                          Date birthDate, String snils, List<DocumentItem> documents) {
        return StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || genderId == null
                || birthDate == null || (snils == null && (documents == null || documents.isEmpty()));
    }

    @WebMethod(operationName = "createMeshPerson")
    public GuardianMeshGuidResponse createMeshPerson(@WebParam(name = "idOfOrg") Long idOfOrg,
                                                     @WebParam(name = "firstname") String firstName,
                                                     @WebParam(name = "patronymic") String patronymic,
                                                     @WebParam(name = "lastname") String lastName,
                                                     @WebParam(name = "genderId") Integer genderId,
                                                     @WebParam(name = "birthDate") Date birthDate,
                                                     @WebParam(name = "snils") String snils,
                                                     @WebParam(name = "mobile") String mobile,
                                                     @WebParam(name = "email") String email,
                                                     @WebParam(name = "childMeshGuid") String childMeshGuid,
                                                     @WebParam(name = "documents") List<DocumentItem> documents,
                                                     @WebParam(name = "agentTypeId") Integer agentTypeId,
                                                     @WebParam(name = "relation") Integer relation,
                                                     @WebParam(name = "typeOfLegalRepresent") Integer typeOfLegalRepresent,
                                                     @WebParam(name = "informing") Boolean informing) throws FrontControllerException {
        try {
            if (checkCreateMeshPersonParameters(idOfOrg, firstName, lastName, genderId, birthDate, snils, childMeshGuid, agentTypeId,
                    relation, typeOfLegalRepresent, informing, documents)) {
                return new GuardianMeshGuidResponse(ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED, ResponseItem.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            List<DulDetail> dulDetails = new ArrayList<>();
            if (!CollectionUtils.isEmpty(documents)) {
                for (DocumentItem item : documents) {
                    dulDetails.add(getDulDetailFromDocumentItem(item));
                }
            }
            if (DAOReadonlyService.getInstance().findClientsBySan(snils).size() > 0) {
                return new GuardianMeshGuidResponse(ResponseItem.ERROR_SNILS_EXISTS, ResponseItem.ERROR_SNILS_EXISTS_MESSAGE);
            }
            MeshAgentResponse personResponse = getMeshGuardiansService().createPersonWithEducation(idOfOrg, firstName, patronymic, lastName, genderId, birthDate, snils,
                    mobile, email, childMeshGuid, dulDetails, agentTypeId, relation, typeOfLegalRepresent, informing);

            if (!personResponse.getCode().equals(GuardianResponse.OK)) {
                logger.error(personResponse.getMessage());
                return new GuardianMeshGuidResponse(personResponse.getCode(), personResponse.getMessage());
            }
            return new GuardianMeshGuidResponse(personResponse.getAgentPersonId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new GuardianMeshGuidResponse(ResponseItem.ERROR_INTERNAL, ResponseItem.ERROR_INTERNAL_MESSAGE);
        }
    }

    @WebMethod(operationName = "getGuardians")
    public GuardianResponse getGuardians(
            @WebParam(name = "firstName") String firstName,
            @WebParam(name = "lastName") String lastName,
            @WebParam(name = "patronymic") String patronymic,
            @WebParam(name = "mobile") String mobile,
            @WebParam(name = "snils") String snils) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        GuardianResponse guardianResponse;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (lastName == null && snils == null && mobile == null) {
                return new GuardianResponse(GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }
            List<Client> clients = ClientManager
                    .findGuardianByNameOrMobileOrSun(persistenceSession, firstName, lastName, patronymic, mobile, snils);
            if (clients.isEmpty()) {
                return new GuardianResponse(GuardianResponse.ERROR_CLIENT_NOT_FOUND,
                        GuardianResponse.ERROR_CLIENT_NOT_FOUND_MESSAGE);
            }
            guardianResponse = new GuardianResponse(clients);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in getGuardians", e);
            return new GuardianResponse(GuardianResponse.ERROR_INTERNAL, GuardianResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return guardianResponse;
    }

    @WebMethod(operationName = "changeGuardians")
    public GuardianResponse changeGuardians(
            @WebParam(name = "idOfClient") Long idOfClient,
            @WebParam(name = "lastName") String lastName,
            @WebParam(name = "firstName") String firstName,
            @WebParam(name = "patronymic") String patronymic,
            @WebParam(name = "birthDate") Date birthDate,
            @WebParam(name = "snils") String snils,
            @WebParam(name = "genderId") Integer genderId) {

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (idOfClient == null || lastName == null || firstName == null || birthDate == null || genderId == null)
                return new GuardianResponse(GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);

            Client client = persistenceSession.get(Client.class, idOfClient);
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);

            client.setClientRegistryVersion(clientRegistryVersion);
            client.getPerson().setSurname(lastName);
            client.getPerson().setFirstName(firstName);
            client.getPerson().setSecondName(patronymic);
            client.setSan(snils);
            client.setGender(genderId);
            client.setBirthDate(birthDate);
            persistenceSession.update(client);

            if (client.getMeshGUID() != null) {
                PersonResponse personResponse = getMeshGuardiansService()
                        .changePerson(client.getMeshGUID(), firstName, patronymic, lastName, genderId, birthDate, snils);
                if (!personResponse.getCode().equals(GuardianResponse.OK)) {
                    return new GuardianResponse(personResponse.getCode(), personResponse.getMessage());
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in changeGuardians", e);
            return new GuardianResponse(GuardianResponse.ERROR_INTERNAL, GuardianResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new GuardianResponse(GuardianResponse.OK, GuardianResponse.OK_MESSAGE);
    }

    @WebMethod(operationName = "addGuardianToClient")
    public GuardianResponse addGuardianToClient(
            @WebParam(name = "meshGuid") String meshGuid,
            @WebParam(name = "childMeshGuid") String childMeshGuid,
            @WebParam(name = "agentTypeId") Integer agentTypeId,
            @WebParam(name = "relation") Integer relation,
            @WebParam(name = "typeOfLegalRepresent") Integer typeOfLegalRepresent,
            @WebParam(name = "idOfOrg") Long idOfOrg,
            @WebParam(name = "informing") Boolean informing) {

        //todo для чего idOfOrg?

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (meshGuid == null || childMeshGuid == null || agentTypeId == null || relation == null
                    || typeOfLegalRepresent == null || idOfOrg == null || informing == null) {
                return new GuardianResponse(GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }

            Criteria criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("meshGUID", meshGuid));
            Client guardian = (Client) criteria.uniqueResult();

            criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("meshGUID", childMeshGuid));
            Client child = (Client) criteria.uniqueResult();

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Создана связка арм методом \"addGuardianToClient\"");
            clientGuardianHistory.setWebAdress(req.getRemoteAddr());

            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
            ClientManager.addGuardianByClient(persistenceSession, child.getIdOfClient(), guardian.getIdOfClient(), newGuardiansVersions,
                    true, ClientGuardianRelationType.fromInteger(relation), ClientManager.getNotificationSettings(),
                    ClientCreatedFromType.ARM, ClientGuardianRepresentType.fromInteger(typeOfLegalRepresent), clientGuardianHistory,
                    ClientGuardianRoleType.fromInteger(agentTypeId), informing);

            MeshAgentResponse personResponse = getMeshGuardiansService().addGuardianToClient(meshGuid, childMeshGuid, agentTypeId);
            if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                logger.error(String.format("Error in addGuardianToClient %s: %s", personResponse.getCode(), personResponse.getMessage()));
                return new GuardianResponse(personResponse.getCode(), personResponse.getMessage());
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in addGuardianToClient", e);
            return new GuardianResponse(GuardianResponse.ERROR_INTERNAL, GuardianResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new GuardianResponse(GuardianResponse.OK, GuardianResponse.OK_MESSAGE);
    }

    @WebMethod(operationName = "deleteGuardianToClient")
    public GuardianResponse deleteGuardianToClient(
            @WebParam(name = "agentMeshGuid") String agentMeshGuid,
            @WebParam(name = "childMeshGuid") String childMeshGuid) {

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (agentMeshGuid == null || childMeshGuid == null) {
                return new GuardianResponse(GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED,
                        GuardianResponse.ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE);
            }

            Criteria criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("meshGUID", agentMeshGuid));
            Client guardian = (Client) criteria.uniqueResult();

            criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("meshGUID", childMeshGuid));
            Client child = (Client) criteria.uniqueResult();

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Удалена связка арм методом \"deleteGuardianToClient\"");
            clientGuardianHistory.setWebAdress(req.getRemoteAddr());

            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);

            ClientManager.removeGuardianByClient(persistenceSession, child.getIdOfClient(), guardian.getIdOfClient(),
                    newGuardiansVersions, clientGuardianHistory);

            MeshAgentResponse personResponse = getMeshGuardiansService().deleteGuardianToClient(agentMeshGuid, childMeshGuid);

            if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                return new GuardianResponse(personResponse.getCode(), personResponse.getMessage());
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            persistenceSession.close();
        } catch (Exception e) {
            logger.error("Error in deleteGuardianToClient", e);
            return new GuardianResponse(GuardianResponse.ERROR_INTERNAL, GuardianResponse.ERROR_INTERNAL_MESSAGE);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new GuardianResponse(GuardianResponse.OK, GuardianResponse.OK_MESSAGE);
    }

    private boolean checkCreateMeshPersonParameters(Long idOfOrg, String firstName, String lastName, Integer genderId,
                                                    Date birthDate, String snils, String childMeshGuid, Integer agentTypeId,
                                                    Integer relation, Integer typeOfLegalRepresent, Boolean informing, List<DocumentItem> documents) throws FrontControllerException {
        return idOfOrg == null || StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || genderId == null
                || birthDate == null || StringUtils.isEmpty(childMeshGuid) || agentTypeId == null
                || relation == null || typeOfLegalRepresent == null || informing == null ||
                (StringUtils.isEmpty(snils) && (documents == null || documents.isEmpty()));
    }

    private List<GuardianItem> fillingGuardianResponse(PersonListResponse personListResponse) {
        return personListResponse.getResponse().stream().map(r -> {
            GuardianItem guardianItem = new GuardianItem();
            guardianItem.setMeshGuid(r.getMeshGuid());
            guardianItem.setLastName(r.getSurname());
            guardianItem.setFirstName(r.getFirstName());
            guardianItem.setPatronymic(r.getSecondName());
            guardianItem.setGender(r.getIsppGender());
            guardianItem.setBirthDate(r.getBirthDate());
            guardianItem.setSnils(r.getSnils());
            guardianItem.setMobile(r.getMobile());
            guardianItem.setEmail(r.getEmail());
            guardianItem.setDegree(r.getDegree());
            guardianItem.setValidationStateId(r.getValidationStateId());
            guardianItem.setAlreadyInISPP(r.getAlreadyInISPP());
            guardianItem.setDocument(r.getDocument());
            return guardianItem;
        }).collect(Collectors.toList());
    }

    private DulDetail getDulDetailFromDocumentItem(DocumentItem item) {
        DulDetail dulDetail = new DulDetail();
        dulDetail.setDocumentTypeId(item.getDocumentTypeId());
        dulDetail.setSeries(item.getSeries());
        dulDetail.setNumber(item.getNumber());
        dulDetail.setSubdivisionCode(item.getSubdivisionCode());
        dulDetail.setIssuer(item.getIssuer());
        dulDetail.setIssued(item.getIssued());
        dulDetail.setExpiration(item.getExpiration());
        return dulDetail;
    }

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }

    private DulDetail fillingDulDetail(Session session, DocumentItem documentItem) {
        DulDetail dulDetail;
        Date currentDate = new Date();
        if (documentItem.getIdDocument() == null) {
            dulDetail = new DulDetail();
            dulDetail.setCreateDate(currentDate);
            dulDetail.setDeleteState(false);
        } else
            dulDetail = session.get(DulDetail.class, documentItem.getIdDocument());
        if (documentItem.getIdOfClient() != null && documentItem.getIdOfClient() != 0L)
            dulDetail.setIdOfClient(documentItem.getIdOfClient());
        dulDetail.setDocumentTypeId(documentItem.getDocumentTypeId());
        dulDetail.setSeries(documentItem.getSeries());
        dulDetail.setNumber(documentItem.getNumber());
        dulDetail.setSubdivisionCode(documentItem.getSubdivisionCode());
        dulDetail.setIssuer(documentItem.getIssuer());
        dulDetail.setIssued(documentItem.getIssued());
        dulDetail.setExpiration(documentItem.getExpiration());
        dulDetail.setLastUpdate(currentDate);
        return dulDetail;
    }

}