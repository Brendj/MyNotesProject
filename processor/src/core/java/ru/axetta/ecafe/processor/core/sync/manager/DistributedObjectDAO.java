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

    public void saveDOConflict(DOConflict doConflict) {
        entityManager.persist(doConflict);
    }

    public <T extends DistributedObject> T updateDO(T distributedObject) {
        return entityManager.merge(distributedObject);
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

}
