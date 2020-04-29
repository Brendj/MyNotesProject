/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist.services;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtTypeOfProductionItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

;

@Component
public class WtCatalogService {
    // TODO проверить код на повторы в DAOService и DAOUtils
    private static Logger logger = LoggerFactory.getLogger(WtCatalogService.class);

    public List<WtAgeGroupItem> getAllAgeGroupItems() {
        Session session = null;
        Transaction transaction = null;
        List<ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtAgeGroupItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtAgeGroupItem> findAgeGroupItemsByDescription(String description) {
        if(StringUtils.isEmpty(description)){
            return getAllAgeGroupItems();
        }
        Session session = null;
        Transaction transaction = null;
        List<WtAgeGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtAgeGroupItem.class);
            criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            criteria.addOrder(Order.asc("createDate"));

            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WtAgeGroupItem createAgeGroupItem(
            String description, User user) throws Exception {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if (user == null) {
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WtAgeGroupItem item = new WtAgeGroupItem();
            Long nextVersion = getLastVersionAgeGroup(session) + 1L;

            item.setDescription(description);
            item.setVersion(nextVersion);

            session.save(item);

            transaction.commit();
            transaction = null;

            return item;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtTypeOfProductionItem> getAllTypeOfProductionItems() {
        Session session = null;
        Transaction transaction = null;
        List<WtTypeOfProductionItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtTypeOfProductionItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtTypeOfProductionItem> findProductionTypeItemsByDescription(String description) {
        if(StringUtils.isEmpty(description)){
            return getAllTypeOfProductionItems();
        }
        Session session = null;
        Transaction transaction = null;
        List<WtTypeOfProductionItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtTypeOfProductionItem.class);
            criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            criteria.addOrder(Order.asc("createDate"));

            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WtTypeOfProductionItem createProductTypeItem(String description, User user) throws Exception {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if (user == null) {
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WtTypeOfProductionItem item = new WtTypeOfProductionItem();
            Long nextVersion = getLastVersionProductionType(session) + 1L;

            item.setDescription(description);
            item.setVersion(nextVersion);

            session.save(item);
            transaction.commit();
            transaction = null;

            return item;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtCategoryItem> getAllCategoryItem() {
        Session session = null;
        List<WtCategoryItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            Criteria criteria = session.createCriteria(WtCategoryItem.class);
            criteria.createAlias("user", "u", JoinType.INNER_JOIN);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtCategoryItem> findCategoryItemByDescription(String description) {
        if(StringUtils.isEmpty(description)){
            return getAllCategoryItem();
        }
        Session session = null;
        Transaction transaction = null;
        List<WtCategoryItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtCategoryItem.class);
            criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            criteria.addOrder(Order.asc("createDate"));

            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WtCategoryItem createCategoryItem(String description, User user) throws Exception {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if (user == null) {
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WtCategoryItem item = new WtCategoryItem();
            Long nextVersion = getLastVersionCategoryItem(session) + 1L;
            Date createdDate = new Date();

            item.setCreateDate(createdDate);
            item.setLastUpdate(createdDate);
            item.setUser(user);
            item.setDescription(description);
            item.setVersion(nextVersion);

            session.save(item);
            transaction.commit();
            transaction = null;

            return item;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtGroupItem> getAllGroupItems() {
        Session session = null;
        Transaction transaction = null;
        List<WtGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtGroupItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WtGroupItem> findGroupItemsByDescription(String description) {
        Session session = null;
        Transaction transaction = null;
        List<WtGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WtGroupItem.class);
            criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            criteria.addOrder(Order.asc("createDate"));

            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WtGroupItem createGroupItem(String description, User user) throws Exception {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if (user == null) {
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WtGroupItem item = new WtGroupItem();
            Long nextVersion = getLastVersionGroupItem(session) + 1L;

            item.setDescription(description);
            item.setVersion(nextVersion);

            session.save(item);
            transaction.commit();
            transaction = null;

            return item;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public Long getLastVersionAgeGroup(Session session) {
        Criteria criteria = session.createCriteria(WtAgeGroupItem.class);
        criteria.setProjection(Projections.max("version"));
        Long result = (Long) criteria.uniqueResult();
        return result == null ? 0L : result;
    }

    private Long getLastVersionProductionType(Session session) {
        Criteria criteria = session.createCriteria(WtTypeOfProductionItem.class);
        criteria.setProjection(Projections.max("version"));
        Long result = (Long) criteria.uniqueResult();
        return result == null ? 0L : result;
    }

    private Long getLastVersionCategoryItem(Session session) {
        Criteria criteria = session.createCriteria(WtCategoryItem.class);
        criteria.setProjection(Projections.max("version"));
        Long result = (Long) criteria.uniqueResult();
        return result == null ? 0L : result;
    }

    private Long getLastVersionGroupItem(Session session) {
        Criteria criteria = session.createCriteria(WtGroupItem.class);
        criteria.setProjection(Projections.max("version"));
        Long result = (Long) criteria.uniqueResult();
        return result == null ? 0L : result;
    }
}
