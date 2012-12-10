/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ClientPasswordRecover;
import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.persistence.*;

import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Circulation;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Publication;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;
import ru.axetta.ecafe.processor.web.util.EntityManagerUtils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 12.12.11
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */

@WebService()
public class ClientRoomControllerWS extends HttpServlet implements ClientRoomController {

    final Logger logger = LoggerFactory.getLogger(ClientRoomControllerWS.class);
    private static final Long RC_CLIENT_NOT_FOUND = 110L;
    private static final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    private static final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    private static final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    private static final Long RC_INVALID_DATA = 150L;
    private static final Long RC_NO_CONTACT_DATA = 160L;
    private static final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    private static final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;

    private static final String RC_OK_DESC = "OK";
    private static final String RC_CLIENT_NOT_FOUND_DESC = "Клиент не найден";
    private static final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC = "По условиям найден более одного клиента";
    private static final String RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC = "У клиента нет СНИЛС опекуна";
    private static final String RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC = "У клиента уже есть данный СНИЛС опекуна";
    private static final String RC_CLIENT_AUTHORIZATION_FAILED_DESC = "Ошибка авторизации клиента";
    private static final String RC_INTERNAL_ERROR_DESC = "Внутренняя ошибка";
    private static final String RC_NO_CONTACT_DATA_DESC = "У лицевого счета нет контактных данных";

    @Resource
    private WebServiceContext context;


    static class Processor {

        public void process(Client client, Data data, ObjectFactory objectFactory, Session persistenceSession,
                Transaction transaction) throws Exception {
        }

        public void process(Org org, Data data, ObjectFactory objectFactory, Session persistenceSession,
                Transaction transaction) throws Exception {
        }
    }

    @Override
    public ListOfProductsResult getListOfProducts(Long orgId) {
        authenticateRequest(null);

        ListOfProductsResult result = new ListOfProductsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> setIdOfOrgs = new HashSet<Long>();
            if (orgId != null) {
                Org org = (Org) persistenceSession.load(Org.class, orgId);
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
                setIdOfOrgs.add(orgId);
                List<Long> longList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                setIdOfOrgs.addAll(longList);
            }
            ListOfProductGroups listOfProductGroups = new ListOfProductGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria productGroupCriteria = persistenceSession.createCriteria(ProductGroup.class);
            productGroupCriteria.add(Restrictions.in("orgOwner",setIdOfOrgs));
            List groupObjects = productGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    ProductGroup productGroup = (ProductGroup) groupObject;
                    ListOfProductGroupsExt listOfProductGroupsExt = objectFactory.createListOfProductGroupsExt();
                    listOfProductGroupsExt.setNameOfGroup(productGroup.getNameOfGroup());
                    listOfProductGroupsExt.setClassificationCode(productGroup.getСlassificationCode());
                    listOfProductGroupsExt.setDeletedState(productGroup.getDeletedState());
                    listOfProductGroupsExt.setGuid(productGroup.getGuid());
                    listOfProductGroupsExt.setOrgOwner(productGroup.getOrgOwner());
                    listOfProductGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(productGroup.getCreatedDate()));
                    ListOfProducts listOfProducts = new ListOfProducts();
                    listOfProductGroupsExt.getProducts().add(listOfProducts);
                    Criteria productCriteria = persistenceSession.createCriteria(Product.class);
                    productCriteria.add(Restrictions.eq("productGroup", productGroup));
                    productCriteria.add(Restrictions.in("orgOwner",setIdOfOrgs));
                    List objects = productCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Product product = (Product) object;
                            ListOfProductsExt listOfProductsExt = objectFactory.createListOfProductsExt();
                            listOfProductsExt.setCode(product.getCode());
                            listOfProductsExt.setOkpCode(product.getOkpCode());
                            listOfProductsExt.setClassificationCode(product.getClassificationCode());
                            listOfProductsExt.setDeletedState(product.getDeletedState());
                            listOfProductsExt.setGuid(product.getGuid());
                            listOfProductsExt.setProductName(product.getProductName());
                            listOfProductsExt.setFullName(product.getFullName());
                            listOfProductsExt.setDensity(product.getDensity());
                            listOfProductsExt.setOrgOwner(product.getOrgOwner());
                            listOfProductsExt.setCreatedDate(getXMLGregorianCalendarByDate(product.getCreatedDate()));

                            listOfProducts.getP().add(listOfProductsExt);
                        }
                    }

                    listOfProductGroups.getPG().add(listOfProductGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfProductGroups = listOfProductGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public ListOfGoodsResult getListOfGoods(Long orgId) {
        authenticateRequest(null);

        ListOfGoodsResult result = new ListOfGoodsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = null;
            if (orgId != null) {
                Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
                org = (Org) orgCriteria.uniqueResult();
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
            }

            ListOfGoodGroups listOfGoodGroups = new ListOfGoodGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria goodGroupCriteria = persistenceSession.createCriteria(GoodGroup.class);
            List groupObjects = goodGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    GoodGroup goodGroup = (GoodGroup) groupObject;
                    ListOfGoodGroupsExt listOfGoodGroupsExt = objectFactory.createListOfGoodGroupsExt();
                    listOfGoodGroupsExt.setNameOfGoodsGroup(goodGroup.getNameOfGoodsGroup());
                    listOfGoodGroupsExt.setDeletedState(goodGroup.getDeletedState());
                    listOfGoodGroupsExt.setGuid(goodGroup.getGuid());
                    listOfGoodGroupsExt.setOrgOwner(goodGroup.getOrgOwner());
                    listOfGoodGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(goodGroup.getCreatedDate()));

                    ListOfGoods listOfGoods = new ListOfGoods();
                    listOfGoodGroupsExt.getGoods().add(listOfGoods);

                    Criteria goodCriteria = persistenceSession.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.eq("goodGroup", goodGroup));
                    if (org != null) {
                        List<Long> menuExchangeRuleList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                        StringBuffer sqlRestriction = new StringBuffer();
                        for (Long idOfProvider : menuExchangeRuleList) {
                            sqlRestriction.append("orgOwner=");
                            sqlRestriction.append(idOfProvider);
                            sqlRestriction.append(" or ");
                        }
                        sqlRestriction.append("orgOwner=");
                        sqlRestriction.append(org.getIdOfOrg());
                        goodCriteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
                    }
                    List objects = goodCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Good good = (Good) object;
                            ListOfGoodsExt listOfGoodsExt = objectFactory.createListOfGoodsExt();
                            listOfGoodsExt.setGoodsCode(good.getGoodsCode());
                            listOfGoodsExt.setGuid(good.getGuid());
                            listOfGoodsExt.setDeletedState(good.getDeletedState());
                            listOfGoodsExt.setOrgOwner(good.getOrgOwner());
                            listOfGoodsExt.setNameOfGood(good.getNameOfGood());
                            listOfGoodsExt.setFullName(good.getFullName());
                            listOfGoodsExt.setUnitsScale(good.getUnitsScale());
                            listOfGoodsExt.setNetWeight(good.getNetWeight());
                            listOfGoodsExt.setLifetime(good.getLifeTime());
                            listOfGoodsExt.setMargin(good.getMargin());
                            listOfGoodsExt.setCreatedDate(getXMLGregorianCalendarByDate(good.getCreatedDate()));

                            listOfGoods.getG().add(listOfGoodsExt);
                        }
                    }

                    listOfGoodGroups.getGG().add(listOfGoodGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfGoodGroups = listOfGoodGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;


    }

    @Override
    public ProhibitionsListResult getDishProhibitionsList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(null);

        ProhibitionsListResult prohibitionsListResult = new ProhibitionsListResult();
        prohibitionsListResult.resultCode = RC_OK;
        prohibitionsListResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                prohibitionsListResult.resultCode = RC_CLIENT_NOT_FOUND;
                prohibitionsListResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return prohibitionsListResult;
            }

            ProhibitionsList prohibitionsList = new ProhibitionsList();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("client", client));
            List objects = prohibitionCriteria.list();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Prohibition prohibition = (Prohibition) object;
                    ProhibitionsListExt prohibitionsListExt = objectFactory.createProhibitionsListExt();
                    prohibitionsListExt.setGuid(prohibition.getGuid());
                    prohibitionsListExt.setDeletedState(prohibition.getDeletedState());
                    prohibitionsListExt.setCreatedDate(getXMLGregorianCalendarByDate(prohibition.getCreatedDate()));
                    prohibitionsListExt.setContactId(client.getContractId());
                    Product bannedProduct = prohibition.getProduct();
                    ProductGroup bannedProductGroup = prohibition.getProductGroup();
                    Good bannedGood = prohibition.getGood();
                    GoodGroup bannedGoodGroup = prohibition.getGoodGroup();
                    if (bannedProduct != null) {
                        prohibitionsListExt.setGuidOfProducts(bannedProduct.getGuid());
                    } else if (bannedProductGroup != null) {
                        prohibitionsListExt.setGuidOfProductGroups(bannedProductGroup.getGuid());
                    } else if (bannedGood != null) {
                        prohibitionsListExt.setGuidOfGood(bannedGood.getGuid());
                    } else if (bannedGoodGroup != null) {
                        prohibitionsListExt.setGuidOfGoodsGroup(bannedGoodGroup.getGuid());
                    }

                    ProhibitionExclusionsList exclusionsList = new ProhibitionExclusionsList();
                    prohibitionsListExt.getExclusions().add(exclusionsList);

                    Criteria exclusionCriteria = persistenceSession.createCriteria(ProhibitionExclusion.class);
                    exclusionCriteria.add(Restrictions.eq("prohibition", prohibition));
                    List exclusionObjects = exclusionCriteria.list();
                    if (!exclusionObjects.isEmpty()) {
                        for (Object exclusionObject : exclusionObjects) {
                            ProhibitionExclusion exclusion = (ProhibitionExclusion) exclusionObject;
                            ProhibitionExclusionsListExt prohibitionExclusionsListExt = objectFactory
                                    .createProhibitionExclusionsListExt();
                            prohibitionExclusionsListExt.setGuid(exclusion.getGuid());
                            prohibitionExclusionsListExt.setDeletedState(exclusion.getDeletedState());
                            prohibitionExclusionsListExt
                                    .setCreatedDate(getXMLGregorianCalendarByDate(exclusion.getCreatedDate()));
                            Good excludedGood = exclusion.getGood();
                            GoodGroup excludedGoodGroup = exclusion.getGoodsGroup();
                            if (excludedGood != null) {
                                prohibitionExclusionsListExt.setGuidOfGood(excludedGood.getGuid());
                            } else if (excludedGoodGroup != null) {
                                prohibitionExclusionsListExt.setGuidOfGoodsGroup(excludedGoodGroup.getGuid());
                            }

                            exclusionsList.getE().add(prohibitionExclusionsListExt);
                        }
                    }

                    prohibitionsList.getC().add(prohibitionsListExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            prohibitionsListResult.prohibitionsList = prohibitionsList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            prohibitionsListResult.resultCode = RC_INTERNAL_ERROR;
            prohibitionsListResult.description = RC_INTERNAL_ERROR_DESC;
        }
        return prohibitionsListResult;
    }

    private XMLGregorianCalendar getXMLGregorianCalendarByDate(Date date) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        return xmlCalendar;
    }

    @Override
    public IdResult setProhibitionOnProduct(Long orgId, Long contractId, Long idOfProduct, Boolean isDeleted) {
        if (isDeleted!=null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        } else{
            return setProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        }
    }

    @Override
    public IdResult setProhibitionOnProductGroup(Long orgId, Long contractId, Long idOfProductGroup,
            Boolean isDeleted) {
        if (isDeleted!=null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        } else{
            return setProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        }
    }

    @Override
    public IdResult setProhibitionOnGood(Long orgId, Long contractId, Long idOfGood, Boolean isDeleted) {
        if (isDeleted!=null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        } else{
            return setProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        }
    }

    @Override
    public IdResult setProhibitionOnGoodGroup(Long orgId, Long contractId, Long idOfGoodGroup, Boolean isDeleted) {
        if (isDeleted!=null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        } else{
            return setProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        }
    }

    private IdResult deteteProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);
        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCriteria.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            prohibition.setDeletedState(true);
            prohibition.setGlobalVersion(DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);
            idResult.id = prohibition.getGlobalId();
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    private IdResult setProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Prohibition prohibition = new Prohibition();
            prohibition.setClient(client);
            prohibition.setOrgOwner(org.getIdOfOrg());
            prohibition.setGuid(UUID.randomUUID().toString());
            prohibition.setCreatedDate(new Date());
            prohibition.setDeletedState(false);
            prohibition.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if(p==null){
                    Product product = (Product) objectCriteria.uniqueResult();
                    prohibition.setProduct(product);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if(p==null){
                    ProductGroup productGroup = (ProductGroup) objectCriteria.uniqueResult();
                    prohibition.setProductGroup(productGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if(p==null){
                    Good good = (Good) objectCriteria.uniqueResult();
                    prohibition.setGood(good);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }

            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if(p==null){
                    GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                    prohibition.setGoodGroup(goodGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            prohibition.setGlobalVersion(
                    DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);

            idResult.id = prohibition.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public IdResult excludeGoodFromProhibition(Long orgId, Long idOfProhibition, Long idOfGood) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, Good.class, idOfGood);
    }

    @Override
    public IdResult excludeGoodGroupFromProhibition(Long orgId, Long idOfProhibition, Long idOfGoodGroup) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, GoodGroup.class, idOfGoodGroup);
    }

    private IdResult excludeObjectFromProhibition(Long orgId, Long idOfProhibition, Class objectClass,
            Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("globalId", idOfProhibition));
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            if (prohibition == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Запрет с указанным id не найден";
                return idResult;
            }

            ProhibitionExclusion exclusion = new ProhibitionExclusion();
            exclusion.setProhibition(prohibition);
            exclusion.setOrgOwner(org.getIdOfOrg());
            exclusion.setGuid(UUID.randomUUID().toString());
            exclusion.setCreatedDate(new Date());
            exclusion.setDeletedState(false);
            exclusion.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            if (objectClass.equals(Good.class)) {
                Good good = (Good) objectCriteria.uniqueResult();
                exclusion.setGood(good);
            } else if (objectClass.equals(GoodGroup.class)) {
                GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                exclusion.setGoodsGroup(goodGroup);
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            exclusion.setGlobalVersion(DAOService.getInstance()
                    .updateVersionByDistributedObjects(ProhibitionExclusion.class.getSimpleName()));
            persistenceSession.save(exclusion);

            idResult.id = exclusion.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public ClassStudentListResult getStudentListByIdOfClientGroup(Long idOfClientGroup) {
        authenticateRequest(null);

        ClassStudentListResult classStudentListResult = new ClassStudentListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("idOfClientGroup", idOfClientGroup));
            List objects = clientCriteria.list();
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            ClassStudentList classStudentList = new ClassStudentList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Client client = (Client) object;
                    ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
                    clientSummaryExt.setContractId(client.getContractId());
                    clientSummaryExt.setFirstName(client.getPerson().getFirstName());
                    clientSummaryExt.setLastName(client.getPerson().getSurname());
                    clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
                    classStudentList.getC().add(clientSummaryExt);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            classStudentListResult.classStudentList = classStudentList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return classStudentListResult;
    }

    @Override
    public ClientGroupListResult getGroupListByOrg(Long idOfOrg) {
        authenticateRequest(null);

        ClientGroupListResult clientGroupListResult = new ClientGroupListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
            clientGroupCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));

            List objects = clientGroupCriteria.list();
            ClientGroupList clientGroupList = new ClientGroupList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    ClientGroup clientGroup = (ClientGroup) object;
                    ClientGroupItem clientGroupItem = new ClientGroupItem();
                    clientGroupItem.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    clientGroupItem.setGroupName(clientGroup.getGroupName());
                    clientGroupList.getG().add(clientGroupItem);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            clientGroupListResult.clientGroupList = clientGroupList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGroupListResult;
    }

    class ClientRequest {

        public Data process(Long contractId, Processor processor) {
            return process(contractId, CLIENT_ID_INTERNALID, processor);
        }

        final static int CLIENT_ID_INTERNALID = 0, CLIENT_ID_SAN = 1, CLIENT_ID_EXTERNAL_ID = 2, CLIENT_ID_GUID = 3;

        public Data process(Object id, int clientIdType, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                if (clientIdType == CLIENT_ID_INTERNALID) {
                    clientCriteria.add(Restrictions.eq("contractId", (Long) id));
                } else if (clientIdType == CLIENT_ID_SAN) {
                    clientCriteria.add(Restrictions.ilike("san", (String) id, MatchMode.EXACT));
                } else if (clientIdType == CLIENT_ID_EXTERNAL_ID) {
                    clientCriteria.add(Restrictions.eq("externalId", (Long) id));
                } else if (clientIdType == CLIENT_ID_GUID) {
                    clientCriteria.add(Restrictions.eq("clientGUID", (String) id));
                }

                List<Client> clients = clientCriteria.list();

                if (clients.isEmpty()) {
                    data.setResultCode(RC_CLIENT_NOT_FOUND);
                    data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                } else if (clients.size() > 1) {
                    data.setResultCode(RC_SEVERAL_CLIENTS_WERE_FOUND);
                    data.setDescription(RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
                } else {
                    Client client = (Client) clients.get(0);
                    processor.process(client, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setIdOfContract(client.getContractId());
                    data.setResultCode(RC_OK);
                    data.setDescription("OK");
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }


    }

    class OrgRequest {

        public Data process(long orgId, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria criteria = persistenceSession.createCriteria(Org.class);
                criteria.add(Restrictions.eq("idOfOrg", orgId));
                List<Org> orgs = criteria.list();

                if (orgs.isEmpty()) {
                    data.setResultCode(RC_INVALID_DATA);
                    data.setDescription("Организация не найдена");
                } else {
                    Org org = (Org) orgs.get(0);
                    processor.process(org, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setIdOfContract(null);
                    data.setResultCode(RC_OK);
                    data.setDescription("OK");
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }

    }

    @Override
    public ClientSummaryResult getSummary(Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummary(String san) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processSummary(client, data, objectFactory, session);
                    }
                });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummaryByTypedId(String id, int idType) {
        authenticateRequest(null);

        Object idVal = null;
        if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_INTERNALID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_EXTERNAL_ID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN) {
            idVal = id;
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_GUID) {
            idVal = id;
        } else {
            return new ClientSummaryResult(null, RC_INVALID_DATA, "idType invalid");
        }

        Data data = new ClientRequest().process(idVal, idType, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    private void processSummary(Client client, Data data, ObjectFactory objectFactory, Session session)
            throws DatatypeConfigurationException {
        ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
        /* Номер контракта */
        clientSummaryExt.setContractId(client.getContractId());
        /* дата заключения контракта */
        clientSummaryExt.setDateOfContract(toXmlDateTime(client.getContractTime()));
        /* Текущий баланс клиента */
        clientSummaryExt.setBalance(client.getBalance());
        /* лимит овердрафта */
        clientSummaryExt.setOverdraftLimit(client.getLimit());
        /* Статус контракта (Текстовое значение) */
        clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
        /*ограничения дневных затрат за день*/
        clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
        /* ФИО Клиента */
        clientSummaryExt.setFirstName(client.getPerson().getFirstName());
        clientSummaryExt.setLastName(client.getPerson().getSurname());
        clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
        /* Флаги увидомлений клиента (Истина/ложь)*/
        clientSummaryExt.setNotifyViaEmail(client.isNotifyViaEmail());
        clientSummaryExt.setNotifyViaSMS(client.isNotifyViaSMS());
        /* контактный телефон и емайл адрес электронной почты */
        clientSummaryExt.setMobilePhone(client.getMobile());
        clientSummaryExt.setEmail(client.getEmail());
        Contragent defaultMerchant = client.getOrg().getDefaultSupplier();
        if (defaultMerchant != null) {
            clientSummaryExt.setDefaultMerchantId(defaultMerchant.getIdOfContragent());
            clientSummaryExt.setDefaultMerchantInfo(
                    ParameterStringUtils.extractParameters("TSP.", defaultMerchant.getRemarks()));
        }
        EnterEvent ee = DAOUtils.getLastEnterEvent(session, client);
        if (ee != null) {
            clientSummaryExt.setLastEnterEventCode(ee.getPassDirection());
            clientSummaryExt.setLastEnterEventTime(toXmlDateTime(ee.getEvtDateTime()));
        }
        /* Группа к которой относится клиент (Наименование класса учиника) */
        if (client.getClientGroup() == null) {
            clientSummaryExt.setGrade(null);
        } else {
            clientSummaryExt.setGrade(client.getClientGroup().getGroupName());
        }
        /* Официальное наименование Учебного учереждения */
        clientSummaryExt.setOfficialName(client.getOrg().getOfficialName());
        // Новые параметры:
        String phone = client.getPhone();
        if (phone != null) {
            clientSummaryExt.setPhone(phone);
        }

        String address = client.getAddress();
        if (address != null) {
            clientSummaryExt.setAddress(address);
        }


        clientSummaryExt.setLimit(client.getLimit());

        Integer freePayCount = client.getFreePayCount();
        if (freePayCount != null) {
            clientSummaryExt.setFreePayCount(client.getFreePayCount());
        }

        Integer freePayMaxCount = client.getFreePayMaxCount();
        if (freePayMaxCount != null) {
            clientSummaryExt.setFreePayMaxCount(client.getFreePayMaxCount());
        }

        Date lastFreePayTime = client.getLastFreePayTime();

        if (lastFreePayTime != null) {
            GregorianCalendar greLastFreePayTime = new GregorianCalendar();
            greLastFreePayTime.setTime(lastFreePayTime);
            XMLGregorianCalendar xmlLastFreePayTime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(greLastFreePayTime);

            clientSummaryExt.setLastFreePayTime(xmlLastFreePayTime);
        }


        clientSummaryExt.setDiscountMode(client.getDiscountMode());


        data.setClientSummaryExt(clientSummaryExt);
    }

    final static int MAX_RECS = 50;

    @Override
    public PurchaseListResult getPurchaseList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPurchaseList(client, data, objectFactory, session, endDate, startDate);
            }
        });
        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();

        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processPurchaseList(client, data, objectFactory, session, endDate, startDate);
                    }
                });

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    private void processPurchaseList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws DatatypeConfigurationException {
        int nRecs = 0;
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria ordersCriteria = session.createCriteria(Order.class);
        ordersCriteria.add(Restrictions.eq("client", client));
        ordersCriteria.add(Restrictions.ge("createTime", startDate));
        ordersCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        ordersCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        List ordersList = ordersCriteria.list();
        PurchaseListExt purchaseListExt = objectFactory.createPurchaseListExt();
        for (Object o : ordersList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            Order order = (Order) o;
            PurchaseExt purchaseExt = objectFactory.createPurchaseExt();
            purchaseExt.setByCard(order.getSumByCard());
            purchaseExt.setSocDiscount(order.getSocDiscount());
            purchaseExt.setTrdDiscount(order.getTrdDiscount());
            purchaseExt.setDonation(order.getGrantSum());
            purchaseExt.setSum(order.getRSum());
            purchaseExt.setByCash(order.getSumByCash());
            if (order.getCard() == null) {
                purchaseExt.setIdOfCard(null);
            } else {
                purchaseExt.setIdOfCard(order.getCard().getIdOfCard());
            }
            //было так: purchaseExt.setIdOfCard(order.getCard().getCardPrintedNo());
            purchaseExt.setTime(toXmlDateTime(order.getCreateTime()));

            Set<OrderDetail> orderDetailSet = ((Order) o).getOrderDetails();
            for (OrderDetail od : orderDetailSet) {
                PurchaseElementExt purchaseElementExt = objectFactory.createPurchaseElementExt();
                purchaseElementExt.setAmount(od.getQty());
                purchaseElementExt.setName(od.getMenuDetailName());
                purchaseElementExt.setSum(od.getRPrice());
                if (od.isComplex()) {
                    purchaseElementExt.setType(1);
                } else if (od.isComplexItem()) {
                    purchaseElementExt.setType(2);
                } else {
                    purchaseElementExt.setType(0);
                }
                purchaseExt.getE().add(purchaseElementExt);
            }

            purchaseListExt.getP().add(purchaseExt);
        }
        data.setPurchaseListExt(purchaseListExt);
    }

    @Override
    public PaymentListResult getPaymentList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPaymentList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    @Override
    public PaymentListResult getPaymentList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processPaymentList(client, data, objectFactory, session, endDate, startDate);
                    }
                });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    private void processPaymentList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria clientPaymentsCriteria = session.createCriteria(ClientPayment.class);
        clientPaymentsCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
        clientPaymentsCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        clientPaymentsCriteria.add(Restrictions.ge("createTime", startDate));
        clientPaymentsCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        clientPaymentsCriteria = clientPaymentsCriteria.createCriteria("transaction");
        clientPaymentsCriteria.add(Restrictions.eq("client", client));
        List clientPaymentsList = clientPaymentsCriteria.list();
        PaymentList paymentList = objectFactory.createPaymentList();
        int nRecs = 0;
        for (Object o : clientPaymentsList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            ClientPayment cp = (ClientPayment) o;
            Payment payment = new Payment();
            payment.setOrigin(PaymentTextUtils.buildTransferInfo(cp));
            payment.setSum(cp.getPaySum());
            payment.setTime(toXmlDateTime(cp.getCreateTime()));
            paymentList.getP().add(payment);
        }
        data.setPaymentList(paymentList);
    }

    @Override
    public MenuListResult getMenuList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processMenuList(client.getOrg(), data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processMenuList(client.getOrg(), data, objectFactory, session, startDate, endDate);
                    }
                });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuListByOrg(@WebParam(name = "orgId") Long orgId, final Date startDate,
            final Date endDate) {
        authenticateRequest(null);

        Data data = new OrgRequest().process(orgId, new Processor() {
            public void process(Org org, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processMenuList(org, data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }


    @Override
    public ComplexListResult getComplexList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processComplexList(client.getOrg(), data, objectFactory, session, startDate, endDate);
            }
        });

        ComplexListResult complexListResult = new ComplexListResult();
        complexListResult.complexDateList = data.getComplexDateList();
        complexListResult.resultCode = data.getResultCode();
        complexListResult.description = data.getDescription();
        return complexListResult;
    }


    public void calendarResetTime(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    private void processMenuList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {
        Criteria menuCriteria = session.createCriteria(Menu.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        calendarResetTime(fromCal);
        calendarResetTime(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuCriteria.add(Restrictions.eq("org", org));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));
        //menuCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List menus = menuCriteria.list();
        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            Menu menu = (Menu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
            menuDetailCriteria.add(Restrictions.eq("menu", menu));
            HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
            HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
            List menuDetails = menuDetailCriteria.list();

            for (Object o : menuDetails) {
                MenuDetail menuDetail = (MenuDetail) o;
                MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
                menuItemExt.setGroup(menuDetail.getGroupName());
                menuItemExt.setName(menuDetail.getMenuDetailName());
                menuItemExt.setPrice(menuDetail.getPrice());
                menuItemExt.setCalories(menuDetail.getCalories());
                menuItemExt.setVitB1(menuDetail.getVitB1());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuDateItemExt.getE().add(menuItemExt);
            }

            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    private void processComplexList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {


        Criteria complexCriteria = session.createCriteria(ComplexInfo.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        calendarResetTime(fromCal);
        calendarResetTime(toCal);
        fromCal.add(Calendar.HOUR, -1);
        complexCriteria.add(Restrictions.eq("org", org));

        complexCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        complexCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        //complexCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List<ComplexInfo> complexes = complexCriteria.list();


        ArrayList<ArrayList<ComplexInfo>> sortedComplexes = new ArrayList<ArrayList<ComplexInfo>>();

        Date currDate = null;
        ArrayList<ComplexInfo> currComplexListWithSameDate = new ArrayList<ComplexInfo>();

        for (Object complexObject : complexes) {

            ComplexInfo currComplex = (ComplexInfo) complexObject;

            if (currDate == null) {
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();
                continue;
            }

            if (currComplex.getMenuDate().equals(currDate)) {
                currComplexListWithSameDate.add(currComplex);

            } else {

                ArrayList<ComplexInfo> newComplexes = new ArrayList<ComplexInfo>();
                newComplexes.addAll(currComplexListWithSameDate);

                sortedComplexes.add(newComplexes);

                currComplexListWithSameDate = new ArrayList<ComplexInfo>();
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();

            }


        }


        currDate = null;
        ComplexDateList complexDateList = new ComplexDateList();


        for (ArrayList<ComplexInfo> complexesWithSameDate : sortedComplexes) {

            ComplexDate complexDate = new ComplexDate();

            // boolean emptyComplexDate=true;

            // ComplexInfo currComplex=complexesWithSameDate.get(0);

            //currDate=currComplex.getMenuDate();


            // for(Object complexObject:complexes){
            // ArrayList<ArrayList<ComplexInfoDetail>> complexDetailsWithSameDate =new ArrayList<ArrayList<ComplexInfoDetail>>();

            for (ComplexInfo complexInfo : complexesWithSameDate) {

                Complex complex = new Complex();

                Criteria complexDetailsCriteria = session.createCriteria(ComplexInfoDetail.class);
                complexDetailsCriteria.add(Restrictions.eq("complexInfo", complexInfo));


                List<ComplexInfoDetail> complexDetails = complexDetailsCriteria.list();

                if (!complexDetails.isEmpty()) {

                    for (ComplexInfoDetail complexInfoDetail : complexDetails) {
                        ComplexDetail complexDetail = new ComplexDetail();
                        complexDetail.setName(complexInfoDetail.getMenuDetail().getMenuDetailName());
                        complex.getE().add(complexDetail);
                        complex.setName(complexInfoDetail.getComplexInfo().getComplexName());
                    }

                    complexDate.getE().add(complex);
                    complexDate.setDate(toXmlDateTime(complexInfo.getMenuDate()));


                    // emptyComplexDate=false;
                    logger.info("complexName: " + complexInfo.getComplexName());

                    //  ArrayList<ComplexInfoDetail>complexDetailList=new ArrayList<ComplexInfoDetail>();
                    // complexDetailList.addAll(complexDetails);
                    // complexDetailsWithSameDate.add(complexDetailList);

                }


            }

            if (!complexDate.getE().isEmpty()) {

                complexDateList.getE().add(complexDate);
            }

        }


        data.setComplexDateList(complexDateList);

    }


    @Override
    public CardListResult getCardList(Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processCardList(client, data, objectFactory);
            }
        });

        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    @Override
    public CardListResult getCardList(String san) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processCardList(client, data, objectFactory);
                    }
                });

        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    private void processCardList(Client client, Data data, ObjectFactory objectFactory)
            throws DatatypeConfigurationException {
        Set<Card> cardSet = client.getCards();
        CardList cardList = objectFactory.createCardList();
        for (Card card : cardSet) {
            CardItem cardItem = objectFactory.createCardItem();
            cardItem.setState(card.getState());
            cardItem.setType(card.getCardType());
            cardItem.setChangeDate(toXmlDateTime(card.getUpdateTime()));
            cardItem.setCrystalId(card.getCardNo());
            cardItem.setIdOfCard(card.getIdOfCard());
            cardItem.setLifeState(card.getLifeState());
            cardItem.setExpiryDate(toXmlDateTime(card.getValidTime()));

            cardList.getC().add(cardItem);
        }
        data.setCardList(cardList);
    }

    @Override
    public EnterEventListResult getEnterEventList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    @Override
    public EnterEventListResult getEnterEventList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                            Transaction transaction) throws Exception {
                        processEnterEventList(client, data, objectFactory, session, endDate, startDate);
                    }
                });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    private void processEnterEventList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws DatatypeConfigurationException {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(Restrictions.eq("client", client));
        enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventCriteria.list();
        EnterEventList enterEventList = objectFactory.createEnterEventList();
        int nRecs = 0;
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventList.getE().add(enterEventItem);
        }
        data.setEnterEventList(enterEventList);
    }

    @Override
    public ClientsData getClientsByGuardSan(String guardSan) {
        authenticateRequest(null);

        ClientsData data = new ClientsData();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);

            Criterion exp1 = Restrictions.or(Restrictions.ilike("guardSan", guardSan, MatchMode.EXACT),
                    Restrictions.ilike("guardSan", guardSan + ";", MatchMode.START));
            Criterion exp2 = Restrictions.or(Restrictions.like("guardSan", ";" + guardSan, MatchMode.END),
                    Restrictions.like("guardSan", ";" + guardSan + ";", MatchMode.ANYWHERE));
            Criterion expression = Restrictions.or(exp1, exp2);
            clientCriteria.add(expression);

            List<Client> clients = clientCriteria.list();

            data.clientList = new ClientList();
            for (Client client : clients) {
                ClientItem clientItem = new ClientItem();
                clientItem.setContractId(client.getContractId());
                clientItem.setSan(client.getSan());
                data.clientList.getClients().add(clientItem);
            }
            data.resultCode = RC_OK;
            data.description = "OK";
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.toString();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return data;
    }

    @Override
    public AttachGuardSanResult attachGuardSan(String san, String guardSan) {
        authenticateRequest(null);

        AttachGuardSanResult data = new AttachGuardSanResult();
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager
                    .createNativeQuery("select c.IdOfClient, c.GuardSan from CF_Clients c where c.san like :san");
            query.setParameter("san", san);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return data;
    }

    @Override
    public AttachGuardSanResult attachGuardSan(Long contractId, String guardSan) {
        authenticateRequest(null);

        AttachGuardSanResult data = new AttachGuardSanResult();
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(String san, String guardSan) {
        authenticateRequest(null);

        DetachGuardSanResult data = new DetachGuardSanResult();

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager
                    .createNativeQuery("select c.IdOfClient, c.GuardSan from CF_Clients c where c.san like :san");
            query.setParameter("san", san);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(Long contractId, String guardSan) {
        authenticateRequest(null);

        DetachGuardSanResult data = new DetachGuardSanResult();

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return data;
    }

    private void workClientSan(EntityManager entityManager, String guardSan, Result data, List clientList) {
        if (clientList.size() == 0) {
            data.resultCode = RC_CLIENT_NOT_FOUND;
            data.description = RC_CLIENT_NOT_FOUND_DESC;
        } else if (clientList.size() > 1) {
            data.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            data.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
        } else {
            Object[] clientObject = (Object[]) clientList.get(0);
            Long idOfClient = ((BigInteger) clientObject[0]).longValue();
            String clientGuardSan = (String) clientObject[1];
            if (clientGuardSan == null) {
                if (data instanceof AttachGuardSanResult) {
                    Query query = entityManager.createNativeQuery(
                            "update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                    query.setParameter("guardSan", guardSan);
                    query.setParameter("idOfClient", idOfClient);
                    query.executeUpdate();
                    data.resultCode = RC_OK;
                    data.description = "Ok";
                } else if (data instanceof DetachGuardSanResult) {
                    data.resultCode = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS;
                    data.description = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC;
                }
            } else {
                if (data instanceof AttachGuardSanResult) {
                    if (isGuardSanExists(guardSan, clientGuardSan)) {
                        data.resultCode = RC_CLIENT_HAS_THIS_SNILS_ALREADY;
                        data.description = RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC;
                    } else {
                        String gs = "";
                        if (clientGuardSan.endsWith(";")) {
                            gs = clientGuardSan + guardSan;
                        } else {
                            gs = clientGuardSan + ";" + guardSan;
                        }
                        Query query = entityManager.createNativeQuery(
                                "update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                        query.setParameter("guardSan", gs);
                        query.setParameter("idOfClient", idOfClient);
                        query.executeUpdate();
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                } else if (data instanceof DetachGuardSanResult) {
                    if (!isGuardSanExists(guardSan, clientGuardSan)) {
                        data.resultCode = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS;
                        data.description = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC;
                    } else {
                        if (clientGuardSan.contains(";" + guardSan + ";")) {
                            clientGuardSan = clientGuardSan.replace(";" + guardSan + ";", ";");
                        } else if (clientGuardSan.startsWith(guardSan + ";")) {
                            clientGuardSan = clientGuardSan.substring((guardSan + ";").length());
                        } else if (clientGuardSan.endsWith(";" + guardSan)) {
                            clientGuardSan = clientGuardSan
                                    .substring(0, clientGuardSan.length() - (";" + guardSan).length());
                        } else {
                            clientGuardSan = clientGuardSan.replace(guardSan, "");
                        }
                        Query query = entityManager.createNativeQuery(
                                "update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                        query.setParameter("guardSan", clientGuardSan);
                        query.setParameter("idOfClient", idOfClient);
                        query.executeUpdate();
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                }
            }
        }
    }


    private boolean isGuardSanExists(String guardSan, String clientGuardSans) {
        String[] guardSans = clientGuardSans.split(";");
        for (String gs : guardSans) {
            if (gs.equals(guardSan)) {
                return true;
            }
        }
        return false;
    }

    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc = DatatypeFactory.newInstance()
                .newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }

    @Override
    public Long getContractIdByCardNo(@WebParam(name = "cardId") String cardId) {
        authenticateRequest(null);

        long lCardId = Long.parseLong(cardId);
        try {
            return DAOService.getInstance().getContractIdByCardNo(lCardId);
        } catch (Exception e) {
            logger.error("ClientRoomController failed", e);
            return null;
        }
    }

    @Override
    public ClientSummaryExt[] getSummaryByGuardSan(String guardSan) {
        authenticateRequest(null);

        ClientsData cd = getClientsByGuardSan(guardSan);
        LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
        if (cd != null && cd.clientList != null) {
            for (ClientItem ci : cd.clientList.getClients()) {
                ClientSummaryResult cs = getSummary(ci.getContractId());
                if (cs.clientSummary != null) {
                    clientSummaries.add(cs.clientSummary);
                }
            }
        }
        return clientSummaries.toArray(new ClientSummaryExt[0]);
    }

    @Override
    public Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationBySMS(contractId, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationByEmail(contractId, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "mobilePhone") String mobilePhone) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone == null) {
            r.resultCode = RC_INVALID_DATA;
            r.description = "Неверный формат телефона";
            return r;
        }
        if (!DAOService.getInstance().setClientMobilePhone(contractId, mobilePhone)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeEmail(@WebParam(name = "contractId") Long contractId, @WebParam(name = "email") String email) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientEmail(contractId, email)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeExpenditureLimit(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "limit") long limit) {
        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (limit < 0) {
            r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientExpenditureLimit(contractId, limit)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        }
        return r;
    }


    @Override
    public CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId, int state) {
        authenticateRequest(contractId);

        final int fState = state;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processCirculationList(client, data, objectFactory, session, fState);
            }
        });

        CirculationListResult circListResult = new CirculationListResult();
        circListResult.circulationList = data.getCirculationItemList();
        circListResult.resultCode = data.getResultCode();
        circListResult.description = data.getDescription();
        return circListResult;
    }

    public final static int CIRCULATION_STATUS_FILTER_ALL = -1, CIRCULATION_STATUS_FILTER_ALL_ON_HANDS = -2;

    private void processCirculationList(Client client, Data data, ObjectFactory objectFactory, Session session,
            int state) throws DatatypeConfigurationException {
        Criteria circulationCriteria = session.createCriteria(Circulation.class);
        circulationCriteria.add(Restrictions.eq("client", client));
        if (state == CIRCULATION_STATUS_FILTER_ALL_ON_HANDS) {
            circulationCriteria.add(Restrictions.or(Restrictions.eq("status", Circulation.EXTENDED),
                    Restrictions.eq("status", Circulation.ISSUED)));
        } else if (state != CIRCULATION_STATUS_FILTER_ALL) {
            circulationCriteria.add(Restrictions.eq("status", state));
        }
        circulationCriteria.addOrder(org.hibernate.criterion.Order.desc("issuanceDate"));

        List<Circulation> circulationList = circulationCriteria.list();

        CirculationItemList ciList = objectFactory.createCirculationItemList();
        for (Circulation c : circulationList) {
            CirculationItem ci = new CirculationItem();
            ci.setIssuanceDate(toXmlDateTime(c.getIssuanceDate()));
            ci.setStatus(c.getStatus());
            ci.setRealRefundDate(toXmlDateTime(c.getRealRefundDate()));
            ci.setRefundDate(toXmlDateTime(c.getRefundDate()));
            Publication p = c.getIssuable().getInstance().getPublication();
            if (p != null) {
                PublicationItem pi = new PublicationItem();
                pi.setAuthor(p.getAuthor());
                pi.setPublisher(p.getPublisher());
                pi.setTitle(p.getTitle());
                pi.setTitle2(p.getTitle2());
                pi.setPublicationDate(p.getPublicationdate());
                ci.setPublication(pi);
            }
            ciList.getC().add(ci);
        }
        data.setCirculationItemList(ciList);
    }

    @Override
    public Result authorizeClient(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "token") String token) {
        IntegraPartnerConfig.LinkConfig partnerLinkConfig = null;
        //logger.info("init authorizeClient");
        partnerLinkConfig = authenticateRequest(null);
        if (logger.isDebugEnabled()) {
            logger.debug("begin authorizeClient");
        }
        try {

            DAOService daoService = DAOService.getInstance();
            //logger.info("begin get Client");
            Client client = daoService.getClientByContractId(contractId);
            //logger.info("find client");
            if (client == null) {
                //logger.info("find client == null");
                if (logger.isDebugEnabled()) {
                    logger.debug("Client not found");
                }
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            //logger.info("find client != null");
            boolean authorized = false;
            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_NAME) {
                //logger.info("MD5");
                String fullNameUpCase = client.getPerson().getFullName().replaceAll("\\s", "").toUpperCase();
                fullNameUpCase = fullNameUpCase + "Nb37wwZWufB";
                byte[] bytesOfMessage = fullNameUpCase.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(bytesOfMessage);
                BigInteger bigInt = new BigInteger(1, hash);
                //String md5HashString = bigInt.toString(16);
                String md5HashString = String.format("%0" + (hash.length << 1) + "X", bigInt);
                if (logger.isDebugEnabled()) {
                    logger.info("token    md5: " + token.toUpperCase());
                    logger.info("generate md5: " + md5HashString.toUpperCase());
                }
                if (md5HashString.toUpperCase().compareTo(token.toUpperCase()) == 0) {
                    authorized = true;
                    if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                        daoService.addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(),
                                partnerLinkConfig.id);
                    }
                }
            }
            if (client.hasEncryptedPasswordSHA1(token)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("hasEncryptedPassword");
                }
                authorized = true;
                if (!authorized
                        && partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                    daoService.addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(), partnerLinkConfig.id);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("authorized" + String.valueOf(authorized));
            }
            if (authorized) {
                return new Result(RC_OK, RC_OK_DESC);
            } else {
                return new Result(RC_CLIENT_AUTHORIZATION_FAILED, RC_CLIENT_AUTHORIZATION_FAILED_DESC);
            }
        } catch (Exception e) {
            logger.error("Failed to authorized client", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public ActivateLinkingTokenResult activateLinkingToken(String linkingToken) {
        authenticateRequest(null);

        ActivateLinkingTokenResult result = new ActivateLinkingTokenResult();
        try {

            DAOService daoService = DAOService.getInstance();

            Client client = daoService.findAndDeleteLinkingToken(linkingToken);
            if (client == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Код активации не найден";
            } else {
                result.contractId = client.getContractId();
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }
        } catch (Exception e) {
            logger.error("Failed to activate linking token: " + linkingToken, e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public GenerateLinkingTokenResult generateLinkingToken(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        GenerateLinkingTokenResult result = new GenerateLinkingTokenResult();
        try {
            DAOService daoService = DAOService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            result.linkingToken = linkingToken.getToken();
            result.contractId = contractId;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Failed to generate linking token", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result sendLinkingTokenByContractId(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        try {
            Result result = new Result();
            DAOService daoService = DAOService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            String info = "";
            if (client.hasEmail()) {
                info += "e-mail";
            }
            if (client.hasMobile()) {
                if (info.length() > 0) {
                    info += ", ";
                }
                info += "SMS";
            }
            if (info.length() == 0) {
                result.resultCode = RC_NO_CONTACT_DATA;
                result.description = RC_NO_CONTACT_DATA_DESC;
            } else {
                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                        .sendMessageAsync(client, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                                new String[]{"linkingToken", linkingToken.getToken()});
                result.resultCode = RC_OK;
                result.description = "Код активации отправлен по " + info;
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public Result sendLinkingTokenByMobile(@WebParam(name = "mobilePhone") String mobilePhone) {
        authenticateRequest(null);
        Result result = new Result();

        try {
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Неверный формат телефона";
                return result;
            }

            DAOService daoService = DAOService.getInstance();
            List<Client> clientList = daoService.findClientsByMobilePhone(mobilePhone);
            if (clientList.size() == 0) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            String codes = "";
            for (Client cl : clientList) {
                LinkingToken linkingToken = daoService.generateLinkingToken(cl);
                if (codes.length() > 0) {
                    codes += ", ";
                }
                codes += linkingToken.getToken();
            }
            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                    .sendMessageAsync(clientList.get(0), EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                            new String[]{"linkingToken", codes});
            result.resultCode = RC_OK;
            result.description = "Код активации отправлен по SMS для " + clientList.size() + " л/с";
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId) throws Error {
        MessageContext jaxwsContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) jaxwsContext.get(SOAPMessageContext.SERVLET_REQUEST);
        String clientAddress = request.getRemoteAddr();
        ////
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        X509Certificate[] certificates = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");
        ////
        IntegraPartnerConfig.LinkConfig linkConfig = null;
        String DNs = "";
        if (certificates != null && certificates.length > 0) {
            for (int n = 0; n < certificates.length; ++n) {
                String dn = certificates[0].getSubjectDN().getName();
                linkConfig = runtimeContext.getIntegraPartnerConfig().getLinkConfigByCertDN(dn);
                if (linkConfig != null) {
                    break;
                }
                DNs += dn + ";";
            }
        }
        /////
        // пробуем по имени и паролю
        if (linkConfig == null) {
            AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext
                    .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (authorizationPolicy != null && authorizationPolicy.getUserName() != null) {
                linkConfig = runtimeContext.getIntegraPartnerConfig()
                        .getLinkConfigWithAuthTypeBasicMatching(authorizationPolicy.getUserName(),
                                authorizationPolicy.getPassword());
            }
        }
        /////
        if (linkConfig == null) {
            linkConfig = runtimeContext.getIntegraPartnerConfig()
                    .getLinkConfigWithAuthTypeNoneAndMatchingAddress(clientAddress);
        } else {
            // check remote addr
            if (!linkConfig.matchAddress(clientAddress)) {
                throw new Error("Integra partner auth failed: remote address does not match: " + clientAddress
                        + " for link config: " + linkConfig.id + "; request: ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        /////
        if (linkConfig == null) {
            throw new Error(
                    "Integra partner auth failed: link config not found: ip=" + clientAddress + "; ssl DNs=" + DNs);
        }
        /////
        if (contractId != null && linkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
            DAOService daoService = DAOService.getInstance();
            Client client = null;
            try {
                client = daoService.getClientByContractId(contractId);
            } catch (Throwable e) {
            }
            if (client == null) {
                throw new Error("Integra partner auth failed: client not found: contractId=" + contractId + "; ip="
                        + clientAddress + "; ssl DNs=" + DNs);
            }

            if (!client.hasIntegraPartnerAccessPermission(linkConfig.id)) {
                throw new Error("Integra partner auth failed: access prohibited for client: contractId=" + contractId
                        + ", authorize client first; ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        return linkConfig;
    }

    @Override
    public Result changePassword(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "base64passwordHash") String base64passwordHash) {

        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientPassword(contractId, base64passwordHash)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public SendResult sendPasswordRecoverURLFromEmail(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        SendResult sr = new SendResult();
        sr.resultCode = RC_OK;
        sr.description = RC_OK_DESC;
        try {
            int succeeded = clientPasswordRecover.sendPasswordRecoverURLFromEmail(contractId, request);
            sr.recoverStatus = succeeded;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sr.resultCode = RC_INTERNAL_ERROR;
            sr.description = RC_INTERNAL_ERROR_DESC;


        }
        return sr;
    }

    @Override
    public CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        CheckPasswordResult cpr = new CheckPasswordResult();
        cpr.resultCode = RC_OK;
        cpr.description = RC_OK_DESC;
        try {
            boolean succeeded = clientPasswordRecover.checkPasswordRestoreRequest(request);
            cpr.succeeded = succeeded;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            cpr.resultCode = RC_INTERNAL_ERROR;
            cpr.description = RC_INTERNAL_ERROR_DESC;

        }
        return cpr;
    }

    @Override
    public IdResult getIdOfClient(@WebParam(name = "contractId") Long contractId) {
        Long idOfClient = null;


        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            idOfClient = client.getIdOfClient();


            r.id = idOfClient;
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;

    }

    public IdResult getIdOfContragent(@WebParam(name = "contragentName") String contragentName) {


        Long idOfContragent = null;

        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;

        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();


            Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
            contragentCriteria.add(Restrictions.eq("contragentName", contragentName));
            Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
            idOfContragent = contragent.getIdOfContragent();

            r.id = idOfContragent;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return r;
    }

    @Override
    public IdResult createPaymentOrder(@WebParam(name = "idOfClient") Long idOfClient,
            @WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "paymentMethod") int paymentMethod,
            @WebParam(name = "copecksAmount") Long copecksAmount,
            @WebParam(name = "contragentSum") Long contragentSum) {
        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();
            Long idOfClientPaymentOrder = runtimeContext.getClientPaymentOrderProcessor()
                    .createPaymentOrder(idOfClient, idOfContragent, paymentMethod, copecksAmount, contragentSum);
            r.id = idOfClientPaymentOrder;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }
        return r;
    }


    @Override
    public Result changePaymentOrderStatus(@WebParam(name = "idOfClient") Long idOfClient,
            @WebParam(name = "idOfClientPaymentOrder") Long idOfClientPaymentOrder,
            @WebParam(name = "orderStatus") int orderStatus) {

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        RuntimeContext runtimeContext = null;

        runtimeContext = RuntimeContext.getInstance();
        ClientPaymentOrderProcessor clientPaymentOrderProcessor = runtimeContext.getClientPaymentOrderProcessor();
        try {
            clientPaymentOrderProcessor.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder,
                    ClientPaymentOrder.ORDER_STATUS_CANCELLED);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        }
        return r;

    }

    @Override
    public RBKMoneyConfigResult getRBKMoneyConfig() {
        RuntimeContext runtimeContext = null;
        RBKMoneyConfigResult r = new RBKMoneyConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
        RBKMoneyConfigExt rbkMoneyConfigExt = new RBKMoneyConfigExt();
        rbkMoneyConfigExt.setContragentName(rbkMoneyConfig.getContragentName());
        rbkMoneyConfigExt.setEshopId(rbkMoneyConfig.getEshopId());
        rbkMoneyConfigExt.setPurchaseUri(rbkMoneyConfig.getPurchaseUri().toString());
        rbkMoneyConfigExt.setRate(rbkMoneyConfig.getRate());
        rbkMoneyConfigExt.setSecretKey(rbkMoneyConfig.getSecretKey());
        rbkMoneyConfigExt.setServiceName(rbkMoneyConfig.getServiceName());
        rbkMoneyConfigExt.setShow(rbkMoneyConfig.getShow());
        r.rbkConfig = rbkMoneyConfigExt;
        return r;

    }

    @Override
    public ChronopayConfigResult getChronopayConfig() {
        RuntimeContext runtimeContext = null;
        ChronopayConfigResult r = new ChronopayConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        ChronopayConfig chronopayConfig = runtimeContext.getPartnerChronopayConfig();
        ChronopayConfigExt chronopayConfigExt = new ChronopayConfigExt();
        chronopayConfigExt.setCallbackUrl(chronopayConfig.getCallbackUrl());
        chronopayConfigExt.setContragentName(chronopayConfig.getContragentName());
        chronopayConfigExt.setIp(chronopayConfig.getIp());
        chronopayConfigExt.setPurchaseUri(chronopayConfig.getPurchaseUri());
        chronopayConfigExt.setRate(chronopayConfig.getRate());
        chronopayConfigExt.setSharedSec(chronopayConfig.getSharedSec());
        chronopayConfigExt.setShow(chronopayConfig.getShow());

        r.chronopayConfig = chronopayConfigExt;
        return r;

    }

    @Override
    public ClientSmsListResult getClientSmsList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        ClientSmsListResult r = new ClientSmsListResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();

            Date nextToEndDate = DateUtils.addDays(endDate, 1);


            Criteria clientSmsCriteria = persistenceSession.createCriteria(ClientSms.class);
            clientSmsCriteria.add(Restrictions.ge("serviceSendTime", startDate));
            clientSmsCriteria.add(Restrictions.lt("serviceSendTime", nextToEndDate));
            clientSmsCriteria.add(Restrictions.eq("client", client));
            List clientSmsList = clientSmsCriteria.list();
            ClientSmsList clientSmsListR = new ClientSmsList();

            for (Object clientSmsObject : clientSmsList) {
                ClientSms clientSms = (ClientSms) clientSmsObject;
                AccountTransaction accountTransaction = clientSms.getTransaction();

                Sms sms = new Sms();

                sms.setDeliveryStatus(clientSms.getDeliveryStatus());
                sms.setPrice(clientSms.getPrice());

                sms.setContentsType(clientSms.getContentsType());

                GregorianCalendar greSendTime = new GregorianCalendar();
                greSendTime.setTime(clientSms.getServiceSendTime());
                XMLGregorianCalendar xmlSendTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(greSendTime);

                sms.setServiceSendTime(xmlSendTime);

                Long transactionSum = 0L;
                if (null != accountTransaction) {
                    transactionSum = accountTransaction.getTransactionSum();
                }

                sms.setTransactionSum(transactionSum);

                if (null != accountTransaction) {
                    Card card = accountTransaction.getCard();
                    if (null != card) {

                        sms.setCardNo(card.getCardNo());
                    }
                }
                clientSmsListR.getS().add(sms);


            }
            r.clientSmsList = clientSmsListR;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }


        return r;
    }


    @Override
    public BanksData getBanks() {

        BanksData bd = new BanksData();
        bd.resultCode = RC_OK;
        bd.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        BanksList bankItemList = new BanksList();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria banksCriteria = persistenceSession.createCriteria(Bank.class);

            List<Bank> banksList = (List<Bank>) banksCriteria.list();
            //List<BankItem>banks=new ArrayList<BankItem>();
            for (Bank bank : banksList) {
                BankItem bankItem = new BankItem();
                bankItem.setEnrollmentType(bank.getEnrollmentType());
                bankItem.setLogoUrl(bank.getLogoUrl());
                bankItem.setMinRate(bank.getMinRate());
                bankItem.setName(bank.getName());
                bankItem.setTerminalsUrl(bank.getTerminalsUrl());
                bankItem.setRate(bank.getRate());
                bankItem.setIdOfBank(bank.getIdOfBank());

                bankItemList.getBanks().add(bankItem);
            }

            bd.banksList = bankItemList;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;


        } catch (Exception e) {
            bd.resultCode = RC_INTERNAL_ERROR;
            bd.description = RC_INTERNAL_ERROR_DESC;


            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return bd;

    }

    @Override
    public Result changePersonalInfo(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "limit") Long limit, @WebParam(name = "address") String address,
            @WebParam(name = "phone") String phone, @WebParam(name = "mobilePhone") String mobilePhone,
            @WebParam(name = "email") String email,
            @WebParam(name = "smsNotificationState") boolean smsNotificationState) {

        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);

        try {
            DAOService daoService = DAOService.getInstance();


            //change limit
            if (limit < 0) {
                r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
                return r;
            }
            if (!daoService.setClientExpenditureLimit(contractId, limit)) {
                r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            //change email
            if (!daoService.setClientEmail(contractId, email)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change mobile phone
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                r.resultCode = RC_INVALID_DATA;
                r.description = "Неверный формат телефона";
                return r;
            }
            if (!daoService.setClientMobilePhone(contractId, mobilePhone)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //enableNotificationBySms
            if (!daoService.enableClientNotificationBySMS(contractId, smsNotificationState)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }
            //change phone
            if (!daoService.setClientPhone(contractId, phone)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change address
            if (!daoService.setClientAddress(contractId, address)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

        } catch (Exception e) {
            logger.error("error in changePersonalInfo: ", e);
        }
        return r;
    }


    @WebMethod(operationName = "getHiddenPages")

    public HiddenPagesResult getHiddenPages() {
        RuntimeContext runtimeContext = null;
        HiddenPagesResult r = new HiddenPagesResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        String hiddenPages = runtimeContext
                .getPropertiesValue(RuntimeContext.PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM, "");
        r.hiddenPages = hiddenPages;
        return r;
    }

}