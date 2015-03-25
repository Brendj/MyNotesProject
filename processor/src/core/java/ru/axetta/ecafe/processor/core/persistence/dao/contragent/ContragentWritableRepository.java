/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional
public class ContragentWritableRepository extends WritableJpaDao {

    public static ContragentWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ContragentWritableRepository.class);
    }

    public Contragent findOne( Long id ){
        return entityManager.find( Contragent.class, id );
    }

    public void saveEntity(Contragent contragent) {
        entityManager.merge(contragent);
    }


}
