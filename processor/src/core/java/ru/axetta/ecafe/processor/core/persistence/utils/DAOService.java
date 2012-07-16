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
    public <T> T findRefDistributedObject(Class<T> clazz, Long longIdOfTechnoMap){
        return em.getReference(clazz, longIdOfTechnoMap);
    }

    @Transactional
    public void setDeletedState(DistributedObject distributedObject){
        distributedObject = em.find(distributedObject.getClass(),distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        distributedObject = em.merge(distributedObject);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid){
        TypedQuery<T> query = em.createQuery("from "+clazz.getSimpleName()+" where guid='"+guid+"'",clazz);
        List<T> list = query.getResultList();
        if(list.isEmpty()) return null;
        return list.get(0);
    }

    public List<DistributedObject> findDistributedObjectByInGUID(String name, List<String> guids){
        TypedQuery<DistributedObject> query = em.createQuery("from "+name+" where guid in (:guids)",DistributedObject.class);
        query.setParameter("guids",guids);
        return query.getResultList();
    }

    public Product findProductByGUID(Class<Product> productClass, String stringRefGUID) {
        TypedQuery<Product> query = em.createQuery("from Product where guid='"+stringRefGUID+"'", Product.class);
        List<Product> productList = query.getResultList();
        if(productList.isEmpty()) return null;
        return productList.get(0);
    }

    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProduct(Long id){
        return em.createQuery("from TechnologicalMapProduct where idOfTechnoMap="+id,TechnologicalMapProduct.class).getResultList();
    }

    @Transactional
    public <T> List<T> getDistributedObjects(Class<T> clazz){
        return em.createQuery("from "+clazz.getSimpleName()+" order by id",clazz).getResultList();
    }

    @Transactional
    public List<String> getGUIDsInConfirms(String className, Long orgOwner){
        TypedQuery<String> query ;
        query = em.createQuery("Select guid from "+className+" where orgOwner=:orgOwner",String.class);
        query.setParameter("orgOwner",orgOwner);
        return  query.getResultList();
    }

    @Transactional
    public List<DistributedObject> getDistributedObjects(String className, Long currentMaxVersion,Long orgOwner){
        TypedQuery<DistributedObject> query ;
        if(orgOwner==null){

            if(currentMaxVersion==null){
                query = em.createQuery("from "+className+" order by globalId",DistributedObject.class); }
            else{
                query=em.createQuery("from "+className+" where globalVersion>:currentMaxVersion order by globalId",DistributedObject.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
            }
        }else{
            if(currentMaxVersion==null){
                query = em.createQuery("from "+className+" where (orgOwner=:orgOwner or orgOwner = NULL) order by globalId",DistributedObject.class);
                query.setParameter("orgOwner",orgOwner);
            }
            else{
                query=em.createQuery("from "+className+" where globalVersion>:currentMaxVersion and (orgOwner=:orgOwner or orgOwner = NULL) order by globalId",DistributedObject.class);
                query.setParameter("currentMaxVersion",currentMaxVersion);
                query.setParameter("orgOwner",orgOwner);
            }

        }
        return  query.getResultList();
    }

    @Transactional
    public Long getVersionByDistributedObjects(Class clazz) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName", clazz.getSimpleName().toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        Long version = new Long(1);
        if(!doVersionList.isEmpty()){
            version = version + doVersionList.get(0).getCurrentVersion();
        }
        return version;
    }


    @Transactional
    public DOVersion updateVersionByDistributedObjects(String name) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",name.toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        if(doVersionList.size()==0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
        } else {
            doVersion = doVersionList.get(0);
            doVersion.setCurrentVersion(doVersionList.get(0).getCurrentVersion()+1);
        }
        doVersion.setDistributedObjectClassName(name);
        return em.merge(doVersion);
    }

    @Transactional
    public Long getDistributedObjectVersion(DistributedObject distributedObject) {
        TypedQuery<DistributedObject> query = em.createQuery("from "+distributedObject.getClass().getSimpleName()+" where guid='"+distributedObject.getGuid()+"'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if(distributedObjectList.isEmpty()) return null;
        return distributedObjectList.get(0).getGlobalVersion();
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
        TypedQuery<DistributedObject> query = em.createQuery("from "+distributedObject.getClass().getSimpleName()+" where guid='"+distributedObject.getGuid()+"'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if(distributedObjectList.isEmpty()) return null;
        DistributedObject d = em.find(distributedObject.getClass(),distributedObjectList.get(0).getGlobalId());
        d.fill(distributedObject);
        d.setGlobalVersion(globalVersion);
        d.setDeletedState(distributedObject.getDeletedState());
        d.setLastUpdate(new Date());
        return em.merge(d);
    }

    @Transactional
    public void persistEntity(Object entity) throws Exception{
        em.persist(entity);
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

    @Transactional
    public void addIntegraPartnerAccessPermissionToClient(Long idOfClient, String idOfIntegraPartner) throws Exception {
        Client cl = em.find(Client.class, idOfClient);
        if (cl==null) throw new Exception("Client not found: "+idOfClient);
        cl.addIntegraPartnerAccessPermission(idOfIntegraPartner);
        em.persist(cl);
    }
    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProducts(TechnologicalMap technologicalMap) {
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap", TechnologicalMapProduct.class);
        query.setParameter("technologicalMap",technologicalMap);
        return query.getResultList();
    }

    @Transactional
    public Publication getPublicationByIsbn(String isbn) {
        Query query = em.createQuery("from Publication2 where isbn=:isbn", Publication.class);
        query.setParameter("isbn", isbn);
        return (Publication) query.getSingleResult();
    }

    @Transactional
    public Publication getPublicationByHash(String hash) {
        Query query = em.createQuery("from Publication2 where hash=:hash", Publication.class);
        query.setParameter("hash", hash);
        return (Publication) query.getSingleResult();
    }
    
    @Transactional
    public Org findOrById(long idOfOrg) {
        return em.find(Org.class, idOfOrg);
    }
    
    @Transactional
    public Client findClientById(long idOfClient) {
        return em.find(Client.class, idOfClient);
    }
}
