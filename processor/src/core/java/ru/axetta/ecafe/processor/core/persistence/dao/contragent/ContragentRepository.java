/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
public class ContragentRepository extends BaseJpaDao {

    public static ContragentRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ContragentRepository.class);
    }


    public List<Contragent> findAllByType(int type){
        Query query = entityManager
                .createQuery("from Contragent c where c.classId=:type", Contragent.class)
                .setParameter("type",type);

        return query.getResultList();
    }

}
