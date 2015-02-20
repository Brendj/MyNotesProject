/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 19.02.15
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitService {

    private static final Logger logger = LoggerFactory.getLogger(GroupControlBenefitService.class);

    // Находит клиента по Л/с
    public Client findClientByContractId(Long contractId, Session session) {
        Client client = DAOUtils.findClientByContractId(session, contractId);
        return client;
    }

    // Находит все категории по имени
    public CategoryDiscount findCategoryDiscountByCategoryName(String categoryName, Session session) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.add(Restrictions.eq("categoryName", categoryName));
        return (CategoryDiscount) criteria.uniqueResult();
    }

    public void setBenefitsByClient(Client client, Set<CategoryDiscount> categoryDiscountSet, Session session, Transaction transaction) {
        client.setDiscountMode(3);
        session.persist(client);

    }

}
