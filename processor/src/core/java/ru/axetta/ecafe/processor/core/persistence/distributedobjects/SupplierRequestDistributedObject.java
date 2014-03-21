/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.ArrayList;
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
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        final boolean hasWayBillLinks = hasWayBillLinks(session);
        Boolean isSupplier = DAOUtils.isSupplierByOrg(session, idOfOrg);
        Criteria criteria = session.createCriteria(getClass());
        if(hasWayBillLinks){
            boolean result = addReceiverRestriction(criteria, session, String.valueOf(idOfOrg), !isSupplier);
            if(result){
                createProjections(criteria);
                buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
                criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
                return criteria.list();
            } else {
                return new ArrayList<DistributedObject>(0);
            }
        } else {
            List<Long> idOfOrgs = new ArrayList<Long>();
            idOfOrgs.add(idOfOrg);
            if(isSupplier){
                List<Long> sourceMenuOrg = DAOUtils.findMenuExchangeDestOrg(session, idOfOrg);
                idOfOrgs.addAll(sourceMenuOrg);
            }
            buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
            //criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
            criteria.add(Restrictions.in("orgOwner",idOfOrgs));
            createProjections(criteria);
            criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
            return criteria.list();
        }
    }

    protected abstract boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver);

    /* часть объектов не имеет прямую ссылку на накладную для них строится другая логика по отправке объектов
     * отправка идет от клиента к поставщику, реализована отправка только для Инвентаризации. */
    protected boolean hasWayBillLinks(Session session) throws DistributedObjectException {return true;}

}
