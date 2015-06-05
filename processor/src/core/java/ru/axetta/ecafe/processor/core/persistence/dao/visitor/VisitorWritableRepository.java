/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.visitor;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;

/**
 * User: shamil
 * Date: 05.06.15
 * Time: 10:52
 */
@Repository
public class VisitorWritableRepository extends WritableJpaDao {
    public static VisitorWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(VisitorWritableRepository.class);
    }

    public Visitor find( Long id ) {
        return entityManager.find( Visitor.class, id );
    }

    public void update(Visitor visitor) {
        entityManager.merge(visitor);
    }

}
