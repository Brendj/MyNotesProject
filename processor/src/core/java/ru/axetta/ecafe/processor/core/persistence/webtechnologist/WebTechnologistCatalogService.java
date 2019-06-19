/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebTechnologistCatalogService {

    private static Logger logger = LoggerFactory.getLogger(WebTechnologistCatalogService.class);

    public List<WebTechnologistCatalog> getItemsListByCatalogNameOrGUID(String catalogName, String GUID,
            Boolean showOnlyActive) throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            Criteria criteria = session.createCriteria(WebTechnologistCatalog.class);
            if (!StringUtils.isEmpty(catalogName)) {
                criteria.add(Restrictions.ilike("catalogName", catalogName, MatchMode.ANYWHERE));
            }
            if (!StringUtils.isEmpty(GUID)) {
                criteria.add(Restrictions.ilike("GUID", GUID, MatchMode.ANYWHERE));
            }
            if (showOnlyActive) {
                criteria.add(Restrictions.eq("deleteState", false));
            }

            return criteria.list();
        } catch (Exception e) {
            logger.error("Can't load catalogs from DB: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public List<WebTechnologistCatalog> getAllCatalogs(Session session) {
        Criteria criteria = session.createCriteria(WebTechnologistCatalog.class);
        criteria.addOrder(Order.desc("createDate"));
        return criteria.list();
    }

    public void deleteItem(WebTechnologistCatalog webTechnologistCatalog) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            webTechnologistCatalog.setDeleteState(true);
            webTechnologistCatalog.setLastUpdate(new Date());

            session.merge(webTechnologistCatalog);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't load catalogs from DB: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void restoreItem(WebTechnologistCatalog webTechnologistCatalog) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            webTechnologistCatalog.setDeleteState(false);
            webTechnologistCatalog.setLastUpdate(new Date());

            session.merge(webTechnologistCatalog);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't load catalogs from DB: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WebTechnologistCatalog createNewCatalog(String catalogName, User userCreate) throws Exception {
        if (userCreate == null) {
            throw new IllegalArgumentException("User is NULL");
        } else if (StringUtils.isBlank(catalogName)) {
            throw new IllegalArgumentException("Catalog name is not valid");
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Date createDate = new Date();
            String GUID = UUID.randomUUID().toString();
            Long nextVersion = getNextVersionForCatalog(session);

            WebTechnologistCatalog catalog = new WebTechnologistCatalog();
            catalog.setCatalogName(catalogName);
            catalog.setUserCreator(userCreate);
            catalog.setCreateDate(createDate);
            catalog.setLastUpdate(createDate);
            catalog.setGUID(GUID);
            catalog.setVersion(nextVersion);
            catalog.setDeleteState(false);
            session.save(catalog);

            transaction.commit();
            transaction = null;

            return catalog;
        } catch (Exception e) {
            logger.error("Can't create catalog: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private Long getNextVersionForCatalog(Session session) {
        Query sqlQuery = session.createQuery("SELECT MAX(version) FROM WebTechnologistCatalog");
        Long maxVersion = (sqlQuery.uniqueResult() == null ? 0L : (Long) sqlQuery.uniqueResult()) + 1L;
        return maxVersion + 1L;
    }

    private Long getNextVersionForCatalogItem(Session session) {
        Query sqlQuery = session.createQuery("SELECT MAX(version) FROM WebTechnologistCatalogItem");
        Long maxVersion = (sqlQuery.uniqueResult() == null ? 0L : (Long) sqlQuery.uniqueResult()) + 1L;
        return maxVersion + 1L;
    }

    public void deleteCatalogElement(WebTechnologistCatalog webTechnologistCatalog,
            WebTechnologistCatalogItem selectedCatalogElement) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Date lastUpdateDate = new Date();

            selectedCatalogElement.setDeleteState(true);
            selectedCatalogElement.setLastUpdate(lastUpdateDate);
            selectedCatalogElement.setVersion(getNextVersionForCatalogItem(session));
            session.merge(selectedCatalogElement);

            webTechnologistCatalog.setLastUpdate(lastUpdateDate);
            webTechnologistCatalog.setVersion(getNextVersionForCatalog(session));
            session.merge(webTechnologistCatalog);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't change catalogs element: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void restoreCatalogElement(WebTechnologistCatalog webTechnologistCatalog,
            WebTechnologistCatalogItem selectedCatalogElement) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Date lastUpdateDate = new Date();

            selectedCatalogElement.setDeleteState(false);
            selectedCatalogElement.setLastUpdate(lastUpdateDate);
            selectedCatalogElement.setVersion(getNextVersionForCatalogItem(session));
            session.merge(selectedCatalogElement);

            webTechnologistCatalog.setLastUpdate(lastUpdateDate);
            webTechnologistCatalog.setVersion(getNextVersionForCatalog(session));
            session.merge(webTechnologistCatalog);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't change catalogs element: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void createNewElementOfCatalog(WebTechnologistCatalog webTechnologistCatalog,
            String createdCatalogElementDescription) throws Exception {
        if (StringUtils.isBlank(createdCatalogElementDescription)) {
            throw new IllegalArgumentException("Description of element is not valid");
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            String GUID = UUID.randomUUID().toString();
            Long nextVersion = getNextVersionForCatalogItem(session);
            Date createDate = new Date();

            WebTechnologistCatalogItem item = new WebTechnologistCatalogItem();
            item.setDescription(createdCatalogElementDescription);
            item.setGUID(GUID);
            item.setVersion(nextVersion);
            item.setCatalog(webTechnologistCatalog);
            item.setCreateDate(createDate);
            item.setLastUpdate(createDate);
            item.setDeleteState(false);
            session.save(item);

            webTechnologistCatalog.setVersion(getNextVersionForCatalog(session));
            webTechnologistCatalog.setLastUpdate(createDate);
            webTechnologistCatalog.getItems().add(item);
            session.merge(webTechnologistCatalog);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't create catalogs element from DB: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void applyChange(WebTechnologistCatalog changedCatalog, boolean catalogIsChanged) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long nextVersionForCatalog = getNextVersionForCatalog(session);
            Long nextVersionForCatalogItem = getNextVersionForCatalogItem(session);
            Date lastUpdateDate = new Date();
            Map<Long, WebTechnologistCatalogItem> mapCatalogItemsFromDB = buildMapOfCatalogItemsByCatalog(changedCatalog,session);

            for (WebTechnologistCatalogItem item : changedCatalog.getItems()) {
                WebTechnologistCatalogItem itemFromDB = mapCatalogItemsFromDB.get(item.getIdOfWebTechnologistCatalogItem());
                if (itemFromDB != null && !itemFromDB.getDescription().equals(item.getDescription())) {
                    item.setVersion(nextVersionForCatalogItem);
                    item.setLastUpdate(lastUpdateDate);
                    catalogIsChanged = true;
                    session.merge(item);
                }
            }
            if (catalogIsChanged) {
                changedCatalog.setVersion(nextVersionForCatalog);
                changedCatalog.setLastUpdate(lastUpdateDate);
                session.merge(changedCatalog);
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't apply change for catalog: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private Map<Long, WebTechnologistCatalogItem> buildMapOfCatalogItemsByCatalog(
            WebTechnologistCatalog catalogIsChanged, Session session) {
        Map<Long, WebTechnologistCatalogItem> result = new HashMap<>();

        Criteria criteria = session.createCriteria(WebTechnologistCatalogItem.class, "catalogItem");
        criteria.createAlias("catalogItem.catalog", "catalog");
        criteria.add(Restrictions.eq("catalog.idOfWebTechnologistCatalog", catalogIsChanged.getIdOfWebTechnologistCatalog()));
        List<WebTechnologistCatalogItem> catalogItemsFromDB = criteria.list();

        for (WebTechnologistCatalogItem item : catalogItemsFromDB) {
            result.put(item.getIdOfWebTechnologistCatalogItem(), item);
        }
        return result;
    }

    public WebTechnologistCatalog refreshCatalog(WebTechnologistCatalog catalog) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            session.refresh(catalog);

            transaction.commit();
            transaction = null;
            return catalog;
        } catch (Exception e) {
            logger.error("Can't apply change for catalog: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}