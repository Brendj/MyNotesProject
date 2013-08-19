/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional(readOnly = true)
public class GoodRequestService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<GoodRequest> findByFilter(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState){
        Session session =  (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(GoodRequest.class);
        criteria.add(Restrictions.between("doneDate", startDate, endDate));
        if (deletedState != 2) {
            boolean deletedFlag = false;
            if (deletedState == 1) {
                deletedFlag = true;
            }
            criteria.add(Restrictions.eq("deletedState",deletedFlag));
        }
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("orgOwner",idOfOrg));
        }
        if ((stateList != null) && !stateList.isEmpty()) {
            criteria.add(Restrictions.in("state",stateList));
        }
        criteria.addOrder(Order.desc("doneDate"));
        return criteria.list();
    }



    @Transactional(readOnly = true)
    public List<GoodRequest> findGoodRequestAll(List<Long> idOfOrg){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where deletedState=false and orgOwner in :orgOwner", GoodRequest.class);
        query.setParameter("orgOwner", idOfOrg);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<GoodRequest> findGoodRequestPositionAll(List<Long> idOfOrg){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequestPosition where deletedState=false and orgOwner in :orgOwner", GoodRequest.class);
        query.setParameter("orgOwner", idOfOrg);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public GoodRequest findGoodRequestById(Long id){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where id=:id", GoodRequest.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public GoodRequest findGoodRequestPositionById(Long id){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequestPosition where id=:id", GoodRequest.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<GoodRequestPosition> getGoodRequestPositionByGoodRequest(GoodRequest goodRequest){
        TypedQuery<GoodRequestPosition> query = entityManager.createQuery("from GoodRequestPosition gr where gr.goodRequest=:goodRequest", GoodRequestPosition.class);
        query.setParameter("goodRequest",goodRequest);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public GoodRequest findByGUID(String guid){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where guid=:guid", GoodRequest.class);
        query.setParameter("guid",guid);
        return query.getSingleResult();
    }

    public GoodRequest save(GoodRequest goodRequest){
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequest".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequest");
        if(goodRequest.getGlobalId()==null){
            goodRequest.setGlobalVersion(version);
            goodRequest.setGuid(UUID.randomUUID().toString());
            entityManager.persist(doVersion);
            entityManager.persist(goodRequest);
        } else {
            goodRequest.setGlobalVersion(version);
            entityManager.persist(doVersion);
            goodRequest=entityManager.merge(goodRequest);
        }
        return goodRequest;
    }

    public GoodRequestPosition save(GoodRequestPosition goodRequestPosition){
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequestPosition".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequestPosition");
        if(goodRequestPosition.getGlobalId()==null){
            goodRequestPosition.setGlobalVersion(version);
            goodRequestPosition.setGuid(UUID.randomUUID().toString());
            entityManager.persist(doVersion);
            entityManager.persist(goodRequestPosition);
        } else {
            goodRequestPosition.setGlobalVersion(version);
            entityManager.persist(doVersion);
            goodRequestPosition=entityManager.merge(goodRequestPosition);
        }
        return goodRequestPosition;
    }

    public void delete(GoodRequest goodRequest){
        goodRequest = entityManager.merge(goodRequest);
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequest".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequest");
        goodRequest.setDeletedState(true);
        goodRequest.setGlobalVersion(version);
        entityManager.persist(doVersion);
        goodRequest = entityManager.merge(goodRequest);
    }

    public void delete(GoodRequestPosition goodRequestPosition){
        goodRequestPosition = entityManager.merge(goodRequestPosition);
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequestPosition".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequestPosition");
        goodRequestPosition.setDeletedState(true);
        goodRequestPosition.setGlobalVersion(version);
        entityManager.persist(doVersion);
        goodRequestPosition = entityManager.merge(goodRequestPosition);
    }

}
