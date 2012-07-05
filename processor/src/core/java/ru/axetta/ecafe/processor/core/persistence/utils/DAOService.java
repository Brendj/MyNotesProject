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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public <T> T findRefDistributedObject(Class<T> clazz, Long longIdOfTechnoMap) {
        return em.getReference(clazz, longIdOfTechnoMap);
    }

    @Transactional
    public void setDeletedState(DistributedObject distributedObject) {
        distributedObject = em.find(distributedObject.getClass(), distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        distributedObject = em.merge(distributedObject);
    }

    public Product findProductByGUID(Class<Product> productClass, String stringRefGUID) {
        TypedQuery<Product> query = em.createQuery("from Product where guid='" + stringRefGUID + "'", Product.class);
        List<Product> productList = query.getResultList();
        if (productList.isEmpty()) {
            return null;
        }
        return productList.get(0);
    }

    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProduct(Long id) {
        return em.createQuery("from TechnologicalMapProduct where idOfTechnoMap=" + id, TechnologicalMapProduct.class)
                .getResultList();
    }

    @Transactional
    public <T> List<T> getDistributedObjects(Class<T> clazz) {
        return em.createQuery("from " + clazz.getSimpleName() + " where globalId>=0 order by globalId", clazz)
                .getResultList();
    }

    @Transactional
    public List<DistributedObject> getDistributedObjects(String className, Long currentMaxVersion, Long orgOwner) {
        TypedQuery<DistributedObject> query;
        if (orgOwner == null) {

            if (currentMaxVersion == null) {
                query = em.createQuery("from " + className + " order by globalId", DistributedObject.class);
            } else {
                query = em
                        .createQuery("from " + className + " where globalVersion>:currentMaxVersion order by globalId",
                                DistributedObject.class);
                query.setParameter("currentMaxVersion", currentMaxVersion);
            }
        } else {
            if (currentMaxVersion == null) {
                query = em.createQuery(
                        "from " + className + " where (orgOwner=:orgOwner or orgOwner = NULL) order by globalId",
                        DistributedObject.class);
                query.setParameter("orgOwner", orgOwner);
            } else {
                query = em.createQuery("from " + className
                        + " where globalVersion>:currentMaxVersion and (orgOwner=:orgOwner or orgOwner = NULL) order by globalId",
                        DistributedObject.class);
                query.setParameter("currentMaxVersion", currentMaxVersion);
                query.setParameter("orgOwner", orgOwner);
            }

        }
        return query.getResultList();
    }

    @Transactional
    public DOVersion updateVersionByDistributedObjects(String name) throws Exception {
        try {
            TypedQuery<DOVersion> query = em
                    .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                            DOVersion.class);
            query.setParameter("distributedObjectClassName", name.toUpperCase());
            List<DOVersion> doVersionList = query.getResultList();
            DOVersion doVersion = null;
            if (doVersionList.size() == 0) {
                doVersion = new DOVersion();
                doVersion.setCurrentVersion(0);
            } else {
                doVersion = doVersionList.get(0);
                doVersion.setCurrentVersion(doVersionList.get(0).getCurrentVersion() + 1);
            }
            doVersion.setDistributedObjectClassName(name);
            return em.merge(doVersion);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional
    public Long getDistributedObjectVersion(DistributedObject distributedObject) {
        TypedQuery<DistributedObject> query = em.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        return distributedObjectList.get(0).getGlobalVersion();
        /*DistributedObject currDo = em.find(distributedObject.getClass(), distributedObject.getGlobalId());
        return currDo.getGlobalVersion();*/
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DistributedObject createDistributedObject(DistributedObject distributedObject, long currentVersion)
            throws Exception {
        try {
            distributedObject.setCreatedDate(new Date());
            // Версия должна быть одна на всех, и получена заранее.
            /*TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
            query.setParameter("distributedObjectClassName",distributedObject.getClass().getSimpleName().toUpperCase());
            List<DOVersion> doVersionList = query.getResultList();
            if(doVersionList.size()==0) {
                distributedObject.setGlobalVersion(0L);
            } else {
                distributedObject.setGlobalVersion(doVersionList.get(0).getCurrentVersion()-1);
            }*/
            distributedObject.setGlobalVersion(currentVersion);

            return em.merge(distributedObject);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion)
            throws Exception {
        try {
            TypedQuery<DistributedObject> query = em.createQuery(
                    "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject
                            .getGuid() + "'", DistributedObject.class);
            List<DistributedObject> distributedObjectList = query.getResultList();
            if (distributedObjectList.isEmpty()) {
                return null;
            }
            Long id = distributedObjectList.get(0).getGlobalId();
            Query q = em.createQuery("update " + distributedObject.getClass().getSimpleName()
                    + " set globalVersion=:globalVersion, lastUpdate=:lastUpdate where globalId=:globalId");
            q.setParameter("globalVersion", globalVersion);
            q.setParameter("lastUpdate", new Date());
            q.setParameter("globalId", id);
            q.executeUpdate();
            DistributedObject d = em.find(distributedObject.getClass(), id);
            d.fill(distributedObject);
            return em.merge(d);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional
    public void persistEntity(Object entity) throws Exception {
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
        if (cl == null) {
            return null;
        }
        return cl.getContractId();
    }

    @Transactional
    public void deleteEntity(Object entity) {
        entity = em.merge(entity);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Transactional
    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(em, lCardId);
        if (client != null) {
            return client.getContractId();
        }
        return null;
    }

    @Transactional
    public boolean enableClientNotificationBySMS(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaSMS=:notifyViaSMS");
        q.setParameter("notifyViaSMS", state);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean enableClientNotificationByEmail(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaEmail=:notifyViaEmail");
        q.setParameter("notifyViaEmail", state);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientMobilePhone(Long contractId, String mobile) {
        Query q = em.createQuery("update Client set mobile=:mobile");
        q.setParameter("mobile", mobile);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientEmail(Long contractId, String email) {
        Query q = em.createQuery("update Client set email=:email");
        q.setParameter("email", email);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = em.createQuery("update Client set expenditureLimit=:expenditureLimit");
        q.setParameter("expenditureLimit", limit);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public Org getOrg(Long idOfOrg) {
        Query q = em.createQuery("from Org where idOfOrg = :idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return (Org) l.get(0);
    }

    @Transactional
    public User setUserInfo(User user) {
        return em.merge(user);
    }

    @Transactional
    public Client getClientByContractId(long contractId) throws Exception {
        Client cl = DAOUtils.findClientByContractId(em, contractId);
        if (cl == null) {
            return null;
        }
        return cl;
    }
}
