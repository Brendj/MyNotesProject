/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 16.08.13
 * Time: 17:44
 */

@Repository("doDAO")
public class DistributedObjectDAO {

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public <T extends DistributedObject> T findByGuid(Class<T> doClass, String guid) {
        StringBuilder sql = new StringBuilder();
        TypedQuery<T> query = entityManager.createQuery(
                sql.append("select distinct o from ").append(doClass.getSimpleName()).append(" as o where o.guid = :guid").toString(),
                doClass).setParameter("guid", guid);
        List<T> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T extends DistributedObject> List<T> findDOByGuids(Class<T> doClass, List<String> guids, int currentLimit,
            String currentLastGuid) throws Exception {
        DistributedObject distributedObject = doClass.newInstance();
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(doClass);
        criteria.add(Restrictions.in("guid", guids));
        distributedObject.createProjections(criteria, currentLimit, currentLastGuid);
        criteria.setResultTransformer(Transformers.aliasToBean(doClass));
        return criteria.list();
    }

    public void saveDOConflict(DOConflict doConflict) {
        entityManager.persist(doConflict);
    }

    public <T extends DistributedObject> T updateDO(T distributedObject) {
        return entityManager.merge(distributedObject);
    }

    public <T extends DistributedObject> void saveDO(T distributedObject) {
        entityManager.persist(distributedObject);
    }

    public <T extends DistributedObject> DOVersion getDOVersion(String className) {
        TypedQuery<DOVersion> query = entityManager
                .createQuery("select v from DOVersion as v where UPPER(v.distributedObjectClassName) = :distributedObjectClassName",
                        DOVersion.class).setParameter("distributedObjectClassName", className.toUpperCase());
        List<DOVersion> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public DOVersion updateDOVersion(DOVersion doVersion) {
        return entityManager.merge(doVersion);
    }

    public List<DOConfirm> getDOConfirms(Long orgOwner, String doClassName, String guid, int currentLimit, String currentLastGuid) {
        TypedQuery<DOConfirm> query;
        if(StringUtils.isEmpty(currentLastGuid)){
            query= entityManager.createQuery("select distinct d from DOConfirm as d where orgOwner = :fp and guid = :tp",DOConfirm.class);
        } else {
            query= entityManager.createQuery("select distinct d from DOConfirm as d where orgOwner = :fp and guid = :tp and guid>=:guid order by guid",DOConfirm.class);
            query.setParameter("guid", currentLastGuid);
        }
        query.setParameter("fp", orgOwner).setParameter("tp", guid);
        if(currentLimit>0) query.setMaxResults(currentLimit);
        return query.getResultList();
    }

    public void removeDOConfirm(DOConfirm doConfirm) {
        entityManager.remove(doConfirm);
    }

    public void saveDOConfirm(DOConfirm doConfirm) {
        entityManager.persist(doConfirm);
    }

    @SuppressWarnings("unchecked")
    public List<String> findConfirmedGuids(Long orgOwner, String className, int currentLimit, String currentLastGuid) {
        Query query;
        if(StringUtils.isEmpty(currentLastGuid)){
            query= entityManager.createQuery(
                    "select distinct d.guid from DOConfirm as d where orgOwner = :orgOwner and distributedObjectClassName = :className")
                    .setParameter("orgOwner", orgOwner).setParameter("className", className);
        } else {
            query = entityManager.createQuery(
                    "select distinct d.guid from DOConfirm as d where orgOwner = :orgOwner and distributedObjectClassName = :className and guid>=:guid order by guid")
                    .setParameter("orgOwner", orgOwner).setParameter("className", className).setParameter("guid", currentLastGuid);
        }
        if(currentLimit>0) query.setMaxResults(currentLimit);
        return (List<String>) query.getResultList();
    }

    @Transactional(readOnly = true)
    public boolean isCommodityAccountingByOrg(Long idOfOrg){
        return DAOUtils.isCommodityAccountingByOrg(entityManager.unwrap(Session.class), idOfOrg);
    }
}
