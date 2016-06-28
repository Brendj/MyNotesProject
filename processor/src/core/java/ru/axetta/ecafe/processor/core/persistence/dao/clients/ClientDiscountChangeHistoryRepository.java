/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountChange;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientDiscountChangeHistoryRepository extends AbstractJpaDao<DiscountChange> {

    public static ClientDiscountChangeHistoryRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientDiscountChangeHistoryRepository.class);
    }

    public List<DiscountChange> findAll(Client client) {
        return entityManager.createQuery(
                "from DiscountChange d " + "left join fetch d.org " + "left join fetch d.client " + " where d.client=:client "
                        + "order by d.registrationDate desc", DiscountChange.class).setParameter("client", client)
                .getResultList();
    }
}
