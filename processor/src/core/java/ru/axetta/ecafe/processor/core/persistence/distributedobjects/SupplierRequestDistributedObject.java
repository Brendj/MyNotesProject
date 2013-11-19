/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.09.13
 * Time: 16:45
 * Направление движения от Поставщика к Потребителю по правилу которое задано в накладной
 * Отправитель груза это организация указанный в поле Shipper в накладной
 * Получатель  груза это организация указанный в поле Receiver в накладной
 */
public abstract class SupplierRequestDistributedObject extends DistributedObject {

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws Exception {
        Boolean isSupplier = DAOUtils.isSupplierByOrg(session, idOfOrg);
        Criteria criteria = session.createCriteria(getClass());
        boolean result = addReceiverRestriction(criteria, session, String.valueOf(idOfOrg), !isSupplier);
        if(result){
            createProjections(criteria, currentLimit, currentLastGuid);
            criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
            criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
            return criteria.list();
        } else {
            return null;
        }
    }

    protected abstract boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver);

}
