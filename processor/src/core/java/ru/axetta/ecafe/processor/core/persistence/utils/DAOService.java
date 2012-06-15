/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TransactionJournal;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGuide;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
    public List<ProductGuide> getProductGuide(){
        TypedQuery<ProductGuide> query = em.createQuery("from ProductGuide",ProductGuide.class);
        return  query.getResultList();
    }

    @Transactional
    public Boolean existProductGuide(Long id) {
        TypedQuery<ProductGuide> query = em.createQuery("from ProductGuide where globalId=:id order by globalId",ProductGuide.class);
        query.setParameter("id",id);
        return query.getResultList().size()>0;
    }

    @Transactional
    public void setDeleteStatusProductGuide(Long id, Boolean status) {
        Query q = em.createQuery("update ProductGuide set status=:status,globalVersion=globalVersion+1 where globalId=:id");
        q.setParameter("id", id);
        q.setParameter("status", status);
        int updateCount = q.executeUpdate();
    }

    @Transactional
    public Long getProductGuideVersion(Long globalId) {
        Query query = em.createQuery("select globalVersion from ProductGuide where globalId=:globalId");
        query.setParameter("globalId",globalId);
        Number version = (Number) query.getSingleResult();
        return version.longValue();
    }

    public void setProductGuideVersion(Long globalId, Long globalVersion){
        Query q = em.createQuery("update ProductGuide set globalVersion=:globalVersion where globalId=:globalId");
        q.setParameter("globalId", globalId);
        q.setParameter("globalVersion", globalVersion);
        int updateCount = q.executeUpdate();
    }

    @Transactional
    public ProductGuide mergeProductGuide(ProductGuide productGuide) {
        if(productGuide.getGlobalId()!=null){
            Query q = em.createQuery("update ProductGuide set productName=:productName, okpCode=:okpCode,fullName=:fullName,code=:code,idOfConfigurationProvider=:idOfConfigurationProvider,globalVersion=globalVersion+1 where globalId=:id");
            q.setParameter("id", productGuide.getGlobalId());
            q.setParameter("code", productGuide.getCode());
            q.setParameter("fullName", productGuide.getFullName());
            q.setParameter("okpCode", productGuide.getOkpCode());
            q.setParameter("productName", productGuide.getProductName());
            q.setParameter("idOfConfigurationProvider", productGuide.getIdofconfigurationprovider());
            int updateCount = q.executeUpdate();
        } else {
            em.persist(productGuide);
        }
        //em.persist(productGuide);
        return productGuide;
    }

    @Transactional
    public void createConflict(DOConflict conflict) {
        em.persist(conflict);
    }

    public void deleteProductGuide(Long id) {
        ProductGuide productGuide = em.find(ProductGuide.class, id);
        em.remove(productGuide);
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
