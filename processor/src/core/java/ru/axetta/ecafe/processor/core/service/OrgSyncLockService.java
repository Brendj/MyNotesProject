/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created by i.semenov on 29.06.2017.
 */
@Component
@Scope("singleton")
public class OrgSyncLockService {

    private Logger logger = LoggerFactory.getLogger(OrgSyncLockService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static OrgSyncLockService getInstance() {
        return RuntimeContext.getAppContext().getBean(OrgSyncLockService.class);
    }

    public void clean() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        OrgSyncLockService.getInstance().cleanOrgSyncLocks();
    }

    /**
     * Проверяем, не идет ли в данный момент синхронизация по ОО с ид=idOfOrg на других серверах
     * @param idOfOrg
     * @return true, если удалось заблокировать запись для текущей синхры, и false, если обнаружена синхра на другом сервере
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean lockOrgForSync(long idOfOrg) {
        Query query = entityManager.createNativeQuery("select sync from cf_org_lock where idoforg = :idoforg for update");
        query.setParameter("idoforg", idOfOrg);
        boolean addRecord = false;
        try {
            Integer res = (Integer)query.getSingleResult();
            if (res.equals(1)) {
                return false;
            } else {
                query = entityManager.createNativeQuery("update cf_org_lock set sync = 1, datetime = :datetime where idoforg = :idoforg");
                query.setParameter("idoforg", idOfOrg);
                query.setParameter("datetime", new Date().getTime());
                query.executeUpdate();
                return true;
            }
        } catch (NoResultException e) {
            addRecord = true;
        } catch (Exception ex) {
            logger.error("Error in lockOrgForSync:", ex);
        }
        if (addRecord) {
            query = entityManager.createNativeQuery("insert into cf_org_lock(idoforg, sync, datetime) values(:idoforg, 1, :datetime)");
            query.setParameter("idoforg", idOfOrg);
            query.setParameter("datetime", new Date().getTime());
            query.executeUpdate();
        }
        return true;
    }

    /**
     * Устанавливаем флаг окончания синхронизации
     * @param idOfOrg
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unlockOrgForSync(long idOfOrg) {
        Query query = entityManager.createNativeQuery("update cf_org_lock set sync = 0, datetime = null where idoforg = :idoforg");
        query.setParameter("idoforg", idOfOrg);
        query.executeUpdate();
    }

    /**
     * Очистка флагов работающей синхронизации по расписанию раз в 30 минут (защита от нештатной ситуации, когда флаг не очистился сам)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanOrgSyncLocks() {
        long time = System.currentTimeMillis();
        Query query = entityManager.createNativeQuery("update cf_org_lock set sync = 0, datetime = null where sync = 1");
        int count = query.executeUpdate();
        time = System.currentTimeMillis() - time;
        logger.info(String.format("cleanOrgSyncLocks - cleaned up %s records, time taken = %s ms", count, time));
    }
}
