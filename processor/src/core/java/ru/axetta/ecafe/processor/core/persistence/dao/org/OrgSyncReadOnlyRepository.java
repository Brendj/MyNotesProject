/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class OrgSyncReadOnlyRepository extends BaseJpaDao {

    public static OrgSyncReadOnlyRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgSyncReadOnlyRepository.class);
    }

    public OrgSync find(Long id){
        return entityManager.find( OrgSync.class, id );
    }


    public Long findLastAccRegistrySyncDate(long idOfOrg){
        List resultList = entityManager
                .createNativeQuery("select lastAccRegistrySync from CF_Orgs_sync where IdOfOrg = :idOfOrg")
                .setParameter("idOfOrg", idOfOrg).getResultList();
        if(resultList.size() > 0){
            return (Long) resultList.get(0);
        }else{
            return null;
        }

    }
}
