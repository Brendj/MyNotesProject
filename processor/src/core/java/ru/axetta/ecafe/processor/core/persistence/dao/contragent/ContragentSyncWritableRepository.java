/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ContragentSync;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: shamil
 * Date: 26.03.15
 * Time: 13:53
 */
@Repository
@Transactional
public class ContragentSyncWritableRepository extends WritableJpaDao {

    public static ContragentSyncWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ContragentSyncWritableRepository.class);
    }

    public ContragentSync findOne( Long id ){
        return entityManager.find( ContragentSync.class, id );
    }

    public void saveEntity(ContragentSync contragentSync) {
        entityManager.merge(contragentSync);
    }


}
