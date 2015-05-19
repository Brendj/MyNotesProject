/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * User: shamil
 * Date: 18.05.15
 * Time: 13:53
 */
@Repository
@Transactional
public class OrgSyncWritableRepository extends WritableJpaDao {

    public static OrgSyncWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgSyncWritableRepository.class);
    }

    public void saveEntity(Card card) {
        entityManager.merge(card);
    }

    public int updateAccRegistryDate(long idOfOrg) {
        return entityManager.createNativeQuery("update CF_Orgs_sync set LastAccRegistrySync = :lastAccRegistrySync where IdOfOrg = :idOfOrg")
                .setParameter("lastAccRegistrySync", new Date().getTime())
                .setParameter("idOfOrg", idOfOrg)
                .executeUpdate();
    }

}
