/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional
public class ContragentReadOnlyRepository extends BaseJpaDao {

    public static ContragentReadOnlyRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ContragentReadOnlyRepository.class);
    }


    public List<Contragent> findAllByType(int type){
        Query query = entityManager
                .createQuery("from Contragent c where c.classId=:type", Contragent.class)
                .setParameter("type",type);

        return query.getResultList();
    }


    public List<Contragent> getContragentsList() {
        return getContragentsList(null);
    }

    @Transactional(readOnly = true)
    public List<Contragent> getContragentsList(Integer classId) {
        String q = "from Contragent";
        if(classId != null) {
            q += " c WHERE c.classId=:classId order by idOfContragent";
        }
        TypedQuery<Contragent> query = entityManager.createQuery(q, Contragent.class);
        if(classId != null) {
            query.setParameter("classId", classId);
        }
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


}
