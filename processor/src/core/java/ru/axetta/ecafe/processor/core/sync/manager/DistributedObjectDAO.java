/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.springframework.stereotype.Repository;

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

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public <T extends DistributedObject> T findByGuid(Class<T> doClass, String guid) {
        StringBuilder sql = new StringBuilder();
        TypedQuery<T> query = entityManager.createQuery(
                sql.append("select distinct o from ").append(doClass.getSimpleName()).append(" as o where o.guid = :guid").toString(),
                doClass).setParameter("guid", guid);
        List<T> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public <T extends DistributedObject> List<T> findDOByGuids(Class<T> doClass, List<String> guids) {
        StringBuilder sql = new StringBuilder();
        TypedQuery<T> query = entityManager.createQuery(
                sql.append("select distinct o from ").append(doClass.getSimpleName()).append(" as o where o.guid in (:guids)").toString(),
                doClass).setParameter("guids", guids);
        return query.getResultList();
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

    public List<DOConfirm> getDOConfirms(Long orgOwner, String doClassName, String guid) {
        /*and UPPER(distributedObjectClassName) = :sp .setParameter("sp", doClassName.toUpperCase())*/
        TypedQuery<DOConfirm> query = entityManager.createQuery(
                "select distinct d from DOConfirm as d where orgOwner = :fp and guid = :tp",
                DOConfirm.class);
        query.setParameter("fp", orgOwner).setParameter("tp", guid);
        return query.getResultList();
    }

    public void removeDOConfirm(DOConfirm doConfirm) {
        entityManager.remove(doConfirm);
    }

    public void saveDOConfirm(DOConfirm doConfirm) {
        entityManager.persist(doConfirm);
    }

    @SuppressWarnings("unchecked")
    public List<String> findConfirmedGuids(Long orgOwner, String className) {
        Query query = entityManager.createQuery(
                "select distinct d.guid from DOConfirm as d where orgOwner = :orgOwner and distributedObjectClassName = :className")
                .setParameter("orgOwner", orgOwner).setParameter("className", className);
        return (List<String>) query.getResultList();
    }
}
