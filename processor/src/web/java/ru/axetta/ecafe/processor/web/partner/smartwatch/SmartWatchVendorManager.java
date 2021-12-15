/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import org.springframework.context.annotation.DependsOn;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SmartWatchVendor;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@DependsOn("runtimeContext")
public class SmartWatchVendorManager {
    private final Logger log = LoggerFactory.getLogger(SmartWatchVendorManager.class);

    public SmartWatchVendor getVendorIdByApiKey(String apiKey){
        if(!isUUID(apiKey)){
            return null;
        }
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            SmartWatchVendor vendor = DAOUtils.getVendorByApiKey(apiKey, session);

            session.close();
            return vendor;
        } catch (Exception e){
            log.error("", e);
            return null;
        } finally {
            HibernateUtils.close(session, log);
        }
    }

    public boolean isUUID(String uuidStr){
        try {
            UUID uuid = UUID.fromString(uuidStr);
            return true;
        } catch (Exception ignored){
            return false;
        }
    }

    public SmartWatchVendor getVendorById(Long vendorId) {
        if(vendorId == null){
            return null;
        }
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            SmartWatchVendor vendor = (SmartWatchVendor) session.get(SmartWatchVendor.class, vendorId);
            session.close();

            return vendor;
        } catch (Exception e){
            log.error("", e);
            return null;
        } finally {
            HibernateUtils.close(session, log);
        }
    }

    public List<SmartWatchVendor> getAllVendors() {
        Session session = null;
        List<SmartWatchVendor> result = Collections.emptyList();
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            Criteria criteria = session.createCriteria(SmartWatchVendor.class);
            criteria.addOrder(Order.asc("name"));

            result = criteria.list();

            session.close();
        } catch (Exception e) {
            log.error("Can't get all Vendors:", e);
        } finally {
            HibernateUtils.close(session, log);
        }
        return  result;
    }

    public List<SmartWatchVendor> findVendorByName(String name) {
        if(StringUtils.isEmpty(name)){
            return getAllVendors();
        }

        Session session = null;
        List<SmartWatchVendor> result = Collections.emptyList();
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            Criteria criteria = session.createCriteria(SmartWatchVendor.class);
            criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
            criteria.addOrder(Order.asc("name"));

            result = criteria.list();

            session.close();
        } catch (Exception e) {
            log.error("Can't get Vendors by name:", e);
            throw e;
        } finally {
            HibernateUtils.close(session, log);
        }
        return  result;
    }

    public void deleteVendor(SmartWatchVendor vendor) throws Exception {
        if(vendor == null){
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            vendor = (SmartWatchVendor) session.merge(vendor);
            session.delete(vendor);

            transaction.commit();
            transaction = null;

            session.close();
        } catch (Exception e) {
            log.error("Can't delete Vendor:", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }
}
