/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.03.13
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GoodGroupDAOService extends AbstractDAOService{

    public List<GoodGroup> findByDeleteState(Boolean state){
        Criteria criteria = getSession().createCriteria(GoodGroup.class);
        criteria.add(Restrictions.eq("deletedState",state));
        criteria.addOrder(Order.asc("globalId"));
        return criteria.list();
    }

}
