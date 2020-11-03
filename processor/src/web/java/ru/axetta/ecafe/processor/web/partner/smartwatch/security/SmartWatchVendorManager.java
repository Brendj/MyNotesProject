/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.security;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SmartWatchVendor;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ejb.DependsOn;
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

    private boolean isUUID(String uuidStr){
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
}
