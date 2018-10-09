/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientMigrationHistoryRepository extends AbstractJpaDao<ClientMigration> {
    public static ClientMigrationHistoryRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientMigrationHistoryRepository.class);
    }

    public List<ClientMigration> findAll(Org org, Client client){
        return entityManager.createQuery("from ClientMigration c "
                + "left join fetch c.org "
                + "left join fetch c.oldOrg "
                + "left join fetch c.client "
                + " left join fetch c.newContragent "
                + " left join fetch c.oldContragent "
                + " where "
                + "c.client=:client "
                + "order by c.registrationDate desc", ClientMigration.class)
                .setParameter("client", client)
                .getResultList();
    }

    public List<ClientMigration> findAllSinceDate(Date date) {
        return entityManager.createQuery("select cm from ClientMigration cm "
                + "left join fetch cm.org "
                + "left join fetch cm.oldOrg "
                + "left join fetch cm.client "
                + "left join fetch cm.newContragent "
                + "left join fetch cm.oldContragent "
                + "where cm.registrationDate > :date order by cm.registrationDate")
                .setParameter("date", date)
                .getResultList();
    }

    public Date getDateLastOrgChangeProcess() {
        String str = (String)entityManager.createQuery("select o.optionText from Option o where o.idOfOption = :idOfOption")
                .setParameter("idOfOption", new Long(Option.OPTION_LAST_ORG_CHANGE_PROCESS))
                .getSingleResult();
        return new Date(Long.parseLong(str));
    }
}
