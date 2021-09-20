/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.09.13
 * Time: 16:45
 * Направление движения от Потребителя к Поставщику и потребителя в случае востановления данных
 */
public abstract class ConsumerRequestDistributedObject extends DistributedObject {
    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Boolean isSupplier = DAOUtils.isSupplierByOrg(session, idOfOrg);
        if(isSupplier){
            /* Собираем всех потребителей Организации источника меню */
            Query query = session.createQuery("select rule.idOfDestOrg from MenuExchangeRule rule where rule.idOfSourceOrg=:idOfOrg");
            query.setParameter("idOfOrg", idOfOrg);
            List<Long> orgOwners = query.list();
            Criteria criteria = session.createCriteria(getClass());
            criteria.add(Restrictions.in("orgOwner", orgOwners));
            buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
            //criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
            createProjections(criteria);
            criteria.setCacheable(false);
            criteria.setReadOnly(true);
            criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
            return criteria.list();
        } else {
            // На тот случай если востанавливают базу
            return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }

    }
}
