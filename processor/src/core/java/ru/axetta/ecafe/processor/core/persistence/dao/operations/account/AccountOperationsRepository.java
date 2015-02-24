/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.operations.account;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AccountOperations;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableAbstractJpaDao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: shamil
 * Date: 20.02.15
 * Time: 14:12
 */
@Repository
public class AccountOperationsRepository extends WritableAbstractJpaDao<AccountOperations> {
    public static AccountOperationsRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(AccountOperationsRepository.class);
    }

    public AccountOperationsRepository() {
        setClazz(AccountOperations.class);
    }

    public List<AccountOperations> findByIdOfOperation(long idOfOperation, long idOfOrg){
        return entityManager.createQuery("from AccountOperations a where a.idOfOrg = :idOfOrg and a.idOfOperation = :idOfOperation")
                .setParameter("idOfOperation",idOfOperation)
                .setParameter("idOfOrg", idOfOrg).getResultList();
    }
}
