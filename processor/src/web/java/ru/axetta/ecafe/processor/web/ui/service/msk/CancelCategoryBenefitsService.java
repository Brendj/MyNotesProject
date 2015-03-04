/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.persistence.Client;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 04.03.15
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
public class CancelCategoryBenefitsService {

    private static final Logger logger = LoggerFactory.getLogger(CancelCategoryBenefitsService.class);

    public List<Client> getAllBenefitClients(Session session) {
        List<Client> clientList;
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("discountMode", 3));
        clientList = criteria.list();
        return clientList;
    }
}
