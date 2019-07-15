/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class HardCodeCatalogService {
    private static Logger logger = LoggerFactory.getLogger(HardCodeCatalogService.class);

    public List<WTAgeGroupItem> getAllAgeGroupItems() {
        Session session = null;
        Transaction transaction = null;
        List<WTAgeGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTAgeGroupItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WTAgeGroupItem> findAgeGroupItemsByDescriptionOrGUID(String description, String guid) {
        Session session = null;
        Transaction transaction = null;
        List<WTAgeGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTAgeGroupItem.class);
            if(!StringUtils.isEmpty(guid)) {
                criteria.add(Restrictions.ilike("GUID", guid, MatchMode.ANYWHERE));
            }
            if(!StringUtils.isEmpty(description)){
                criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            }

            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WTAgeGroupItem createAgeGroupItem(String description, User user) throws Exception {
        if(StringUtils.isBlank(description)){
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if(user == null){
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WTAgeGroupItem item = new WTAgeGroupItem();
            Long nextVersion =  getLastVersionAgeGroup(session) + 1L;
            fillItem(item, description, user, nextVersion);

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

    private AbstractHardCodeCatalogItem fillItem(AbstractHardCodeCatalogItem item, String description, User user, Long version) {
        Date createdDate = new Date();

        item.setCreateDate(createdDate);
        item.setLastUpdate(createdDate);
        item.setUser(user);
        item.setDescription(description);
        item.setGUID(UUID.randomUUID().toString());
        item.setVersion(version);

        return item;
    }

    public List<WTTypeOfProductionItem> getAllTypeOfProductionItems() {
        Session session = null;
        Transaction transaction = null;
        List<WTTypeOfProductionItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTTypeOfProductionItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WTTypeOfProductionItem> findProductionTypeItemsByDescriptionOrGUID(String description, String guid) {
        Session session = null;
        Transaction transaction = null;
        List<WTTypeOfProductionItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTTypeOfProductionItem.class);
            if(!StringUtils.isEmpty(guid)) {
                criteria.add(Restrictions.ilike("GUID", guid, MatchMode.ANYWHERE));
            }
            if(!StringUtils.isEmpty(description)){
                criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            }

            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WTTypeOfProductionItem createProductTypeItem(String description, User user) throws Exception {
        if(StringUtils.isBlank(description)){
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if(user == null){
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WTTypeOfProductionItem item = new WTTypeOfProductionItem();
            Long nextVersion = getLastVersionProductionType(session) + 1L;
            fillItem(item, description, user, nextVersion);

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

    public List<WTCategoryItem> getAllCategoryItem() {
        Session session = null;
        Transaction transaction = null;
        List<WTCategoryItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTCategoryItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WTCategoryItem> findCategoryItemByDescriptionOrGUID(String description, String guid) {
        Session session = null;
        Transaction transaction = null;
        List<WTCategoryItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTCategoryItem.class);
            if(!StringUtils.isEmpty(guid)) {
                criteria.add(Restrictions.ilike("GUID", guid, MatchMode.ANYWHERE));
            }
            if(!StringUtils.isEmpty(description)){
                criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            }

            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WTCategoryItem createCategoryItem(String description, User user) throws Exception {
        if(StringUtils.isBlank(description)){
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if(user == null){
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WTCategoryItem item = new WTCategoryItem();
            Long nextVersion = getLastVersionCategoryItem(session) + 1L;
            fillItem(item, description, user, nextVersion);

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

    public List<WTGroupItem> getAllGroupItems() {
        Session session = null;
        Transaction transaction = null;
        List<WTGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTGroupItem.class);
            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<WTGroupItem> findGroupItemsByDescriptionOrGUID(String description, String guid) {
        Session session = null;
        Transaction transaction = null;
        List<WTGroupItem> result;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(WTGroupItem.class);
            if(!StringUtils.isEmpty(guid)) {
                criteria.add(Restrictions.ilike("GUID", guid, MatchMode.ANYWHERE));
            }
            if(!StringUtils.isEmpty(description)){
                criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
            }

            criteria.addOrder(Order.asc("createDate"));
            result = criteria.list();

            transaction.commit();
            transaction = null;

            return result;
        } catch (Exception e){
            logger.error("", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public WTGroupItem createGroupItem(String description, User user) throws Exception {
        if(StringUtils.isBlank(description)){
            throw new IllegalArgumentException("Invalid description (NULL or is empty)");
        } else if(user == null){
            throw new IllegalArgumentException("User is NULL");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            WTGroupItem item = new WTGroupItem();
            Long nextVersion = getLastVersionGroupItem(session) + 1L;
            fillItem(item, description, user, nextVersion);

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
        Criteria criteria = session.createCriteria(WTAgeGroupItem.class);
        criteria.setProjection(Projections.max("version"));
        return (Long) criteria.uniqueResult();
    }

    private Long getLastVersionProductionType(Session session) {
        Criteria criteria = session.createCriteria(WTTypeOfProductionItem.class);
        criteria.setProjection(Projections.max("version"));
        return (Long) criteria.uniqueResult();
    }

    private Long getLastVersionCategoryItem(Session session) {
        Criteria criteria = session.createCriteria(WTCategoryItem.class);
        criteria.setProjection(Projections.max("version"));
        return (Long) criteria.uniqueResult();
    }

    private Long getLastVersionGroupItem(Session session){
        Criteria criteria = session.createCriteria(WTGroupItem.class);
        criteria.setProjection(Projections.max("version"));
        return (Long) criteria.uniqueResult();
    }
}
