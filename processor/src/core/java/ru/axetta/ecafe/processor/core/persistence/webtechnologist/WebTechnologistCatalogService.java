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

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class WebTechnologistCatalogService {

    private static Logger logger = LoggerFactory.getLogger(WebTechnologistCatalogService.class);

    public List<WebTechnologistCatalog> getItemsListByCatalogNameOrGUID(String catalogName, String GUID,
            Boolean showOnlyActive)
            throws Exception {
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
            if(showOnlyActive){
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

    public void deleteItem(WebTechnologistCatalog selectedItem) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            selectedItem.setDeleteState(true);
            selectedItem.setLastUpdate(new Date());

            session.update(selectedItem);

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

    public Long createNewCatalog(String catalogName, User userCreate) throws Exception{
        if(userCreate == null){
            throw new IllegalArgumentException("User is NULL");
        } else if(StringUtils.isBlank(catalogName)){
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

            return catalog.getIdOfWebTechnologistCatalog();
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
        return (Long) sqlQuery.uniqueResult() + 1L;
    }
}