/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
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

@Component
public class WebTechnologistCatalogService {

    private static Logger logger = LoggerFactory.getLogger(WebTechnologistCatalogService.class);

    public List<WebTechnologistCatalog> getItemsListByCatalogNameOrGUID(String catalogName, String GUID,
            Boolean showOnlyActive)
            throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

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
            HibernateUtils.rollback(transaction, logger);
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
        } catch (Exception e) {
            logger.error("Can't load catalogs from DB: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}