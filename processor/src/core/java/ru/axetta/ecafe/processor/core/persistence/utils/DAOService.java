/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TransactionJournal;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class DAOService {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public List<TransactionJournal> fetchTransactionJournal(int nRecs) {
        return DAOUtils.fetchTransactionJournalRecs(em, nRecs);
    }

    public static DAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOService.class);
    }


    @Transactional
    public List<Product> getProductGuide(Long currentMaxVersion,Long orgOwner){
        TypedQuery<Product> query ;
        if(orgOwner==null){

            if(currentMaxVersion==null){
                query = em.createQuery("from ProductGuide",Product.class); }
            else{
                query=em.createQuery("from ProductGuide where globalVersion>:currentMaxVersion",Product.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
            }
        }else{
            if(currentMaxVersion==null){
                query = em.createQuery("from ProductGuide where orgOwner=:orgOwner",Product.class);
                query.setParameter("orgOwner",orgOwner);
            }
            else{
                query=em.createQuery("from ProductGuide where globalVersion>:currentMaxVersion and orgOwner=:orgOwner",Product.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
                query.setParameter("orgOwner",orgOwner);
            }

        }
        return  query.getResultList();
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProducts(Long currentMaxVersion, Long orgOwner) {
        TypedQuery<TechnologicalMapProduct> query ;
        if(orgOwner==null){

            if(currentMaxVersion==null){
                query = em.createQuery("from TechnologicalMapProduct",TechnologicalMapProduct.class); }
            else{
                query=em.createQuery("from TechnologicalMapProduct where globalVersion>:currentMaxVersion",TechnologicalMapProduct.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
            }
        }else{
            if(currentMaxVersion==null){
                query = em.createQuery("from TechnologicalMapProduct where orgOwner=:orgOwner",TechnologicalMapProduct.class);
                query.setParameter("orgOwner",orgOwner);
            }
            else{
                query=em.createQuery("from TechnologicalMapProduct where globalVersion>:currentMaxVersion and orgOwner=:orgOwner",TechnologicalMapProduct.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
                query.setParameter("orgOwner",orgOwner);
            }

        }
        return  query.getResultList();
    }

    public List<TechnologicalMap> getTechnologicalMap(Long currentMaxVersion, Long orgOwner) {
        TypedQuery<TechnologicalMap> query ;
        if(orgOwner==null){

            if(currentMaxVersion==null){
                query = em.createQuery("from TechnologicalMap",TechnologicalMap.class); }
            else{
                query=em.createQuery("from TechnologicalMap where globalVersion>:currentMaxVersion",TechnologicalMap.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
            }
        }else{
            if(currentMaxVersion==null){
                query = em.createQuery("from TechnologicalMap where orgOwner=:orgOwner",TechnologicalMap.class);
                query.setParameter("orgOwner",orgOwner);
            }
            else{
                query=em.createQuery("from TechnologicalMap where globalVersion>:currentMaxVersion and orgOwner=:orgOwner",TechnologicalMap.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
                query.setParameter("orgOwner",orgOwner);
            }

        }
        return  query.getResultList();
    }

    @Transactional
    public Boolean existProductGuide(Long id) {
        TypedQuery<Product> query = em.createQuery("from ProductGuide where globalId=:id order by globalId",Product.class);
        query.setParameter("id",id);
        return query.getResultList().size()>0;
    }

    @Transactional
    public DistributedObject setStatusDistributedObject(DistributedObject distributedObject, Boolean status) {
        distributedObject = em.find(distributedObject.getClass(), distributedObject.getGlobalId());
        distributedObject.setDeletedState(status);
        return em.merge(distributedObject);
    }

    @Transactional
    public DOVersion updateVersionByDistributedObjects(String name) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",name.toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        if(doVersionList.size()==0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0);
        } else {
            doVersion = doVersionList.get(0);
            doVersion.setCurrentVersion(doVersionList.get(0).getCurrentVersion()+1);
        }
        doVersion.setDistributedObjectClassName(name);
        return em.merge(doVersion);
    }

    @Transactional
    public void saveVersionByDistributedObjects(String name, long v) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",name.toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        if(doVersionList.size()==0) {
            doVersion = new DOVersion();
        } else {
            doVersion = doVersionList.get(0);
        }
        doVersion.setDistributedObjectClassName(name);
        doVersion.setCurrentVersion(v);
        em.persist(doVersion);
    }

    @Transactional
    public DistributedObject getDistributedObject(DistributedObject distributedObject) {
        return em.find(distributedObject.getClass(), distributedObject.getGlobalId());
    }

    @Transactional
    public Long getDistributedObjectVersion(DistributedObject distributedObject) {
        DistributedObject currDo = em.find(distributedObject.getClass(), distributedObject.getGlobalId());
        return currDo.getGlobalVersion();
    }

    @Transactional
    public DistributedObject createDistributedObject(DistributedObject distributedObject){
        distributedObject.setCreatedDate(new Date());
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",distributedObject.getClass().getSimpleName().toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        if(doVersionList.size()==0) {
            distributedObject.setGlobalVersion(0L);
        } else {
            distributedObject.setGlobalVersion(doVersionList.get(0).getCurrentVersion()-1);
        }
        return em.merge(distributedObject);
    }

    @Transactional
    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion){
        Query q = em.createQuery("update " + distributedObject.getClass().getSimpleName() + " set globalVersion=:globalVersion, lastUpdate=:lastUpdate where globalId = :globalId ");
        q.setParameter("globalVersion", globalVersion);
        q.setParameter("lastUpdate", new Date());
        q.setParameter("globalId", distributedObject.getGlobalId());
        q.executeUpdate();
        return em.merge(distributedObject);
    }

    @Transactional
    public void persistEntity(Object entity) throws Exception{
        em.persist(entity);
    }

    @Transactional
    public void createConflict(DOConflict conflict) {
        em.persist(conflict);
    }

    public void deleteProductGuide(Long id) {
        Product product = em.find(Product.class, id);
        em.remove(product);
    }

    @Transactional
    public Long getClientContractIdByCardId(String idOfCard) throws Exception {
        Client cl = DAOUtils.findClientByCardNo(em, Long.decode(idOfCard));
        if (cl==null) return null;
        return cl.getContractId();
    }

    @Transactional
    public void deleteEntity(Object entity) {
        entity = em.merge(entity);
        if (entity!=null) em.remove(entity);
    }

    @Transactional
    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(em, lCardId);
        if (client!=null) return client.getContractId();
        return null;
    }

    @Transactional
    public boolean enableClientNotificationBySMS(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaSMS=:notifyViaSMS");
        q.setParameter("notifyViaSMS", state);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean enableClientNotificationByEmail(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaEmail=:notifyViaEmail");
        q.setParameter("notifyViaEmail", state);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientMobilePhone(Long contractId, String mobile) {
        Query q = em.createQuery("update Client set mobile=:mobile");
        q.setParameter("mobile", mobile);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientEmail(Long contractId, String email) {
        Query q = em.createQuery("update Client set email=:email");
        q.setParameter("email", email);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = em.createQuery("update Client set expenditureLimit=:expenditureLimit");
        q.setParameter("expenditureLimit", limit);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public Org getOrg(Long idOfOrg) {
        Query q = em.createQuery("from Org where idOfOrg = :idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return (Org)l.get(0);
    }

    @Transactional
    public User setUserInfo(User user){
        return em.merge(user);
    }

    @Transactional
    public Client getClientByContractId(long contractId) throws Exception {
        Client cl = DAOUtils.findClientByContractId(em, contractId);
        if (cl==null) return null;
        return cl;
    }
}
