/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.12.13
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
//@Repository
public class DOVersionRepository {

    //@PersistenceContext(unitName = "reportsPU")
    //private EntityManager entityManager;

    //createNewClassVersion

    //@Transactional
    //public Long updateClassVersion(String doClass) {
    //    Long version;Criteria criteria = persistenceSession.createCriteria(DOVersion.class);
    //    criteria.add(Restrictions.eq("distributedObjectClassName", doClass).ignoreCase());
    //    criteria.setMaxResults(1);
    //    DOVersion doVersion = (DOVersion) criteria.uniqueResult();
    //    if(doVersion == null){
    //        doVersion = new DOVersion();
    //        doVersion.setDistributedObjectClassName(doClass);
    //        version = 0L;
    //    } else {
    //        version = doVersion.getCurrentVersion() + 1;
    //    }
    //    doVersion.setCurrentVersion(version);
    //    persistenceSession.save(doVersion);
    //    return version;
    //}
    //
    public static Long updateClassVersion(String doClass, Session persistenceSession) {
        Long version;Criteria criteria = persistenceSession.createCriteria(DOVersion.class);
        criteria.add(Restrictions.eq("distributedObjectClassName", doClass).ignoreCase());
        criteria.setMaxResults(1);
        DOVersion doVersion = (DOVersion) criteria.uniqueResult();
        if(doVersion == null){
            doVersion = new DOVersion();
            doVersion.setDistributedObjectClassName(doClass);
            version = 0L;
        } else {
            version = doVersion.getCurrentVersion() + 1;
        }
        doVersion.setCurrentVersion(version);
        persistenceSession.save(doVersion);
        return version;
    }

}
