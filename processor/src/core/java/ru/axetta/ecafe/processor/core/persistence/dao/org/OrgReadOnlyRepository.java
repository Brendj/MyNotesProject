/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
public class OrgReadOnlyRepository extends BaseJpaDao {

    public static OrgReadOnlyRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgReadOnlyRepository.class);
    }

    public Org find(Long id){
        return entityManager.find( Org.class, id );
    }

    public  List<Long> findFriendlyOrgIds(Long orgId) {
        return entityManager
                .createQuery("select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg")
                .setParameter("idOfOrg", orgId).getResultList();

    }
}
