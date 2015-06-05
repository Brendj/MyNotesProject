/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.visitor;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * User: shamil
 * Date: 05.06.15
 * Time: 11:36
 */
@Repository
public class VisitorReadOnlyRepository   extends BaseJpaDao {

    public static VisitorReadOnlyRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(VisitorReadOnlyRepository.class);
    }


    public List<Visitor> findAllActiveByOrg(List<Long> idOfOrgs) {
        Query query = entityManager
                .createQuery("from Visitor v where v.org.idOfOrg in (:idOfOrgs) and c.idOfClientGroup < :idOfClientGroup ")
                .setParameter("idOfOrgs", idOfOrgs);

        return query.getResultList();
    }
}
