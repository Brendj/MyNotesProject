/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class BankSubscritpionDao extends WritableJpaDao {


    public static BankSubscritpionDao getInstance() {
        return RuntimeContext.getAppContext().getBean(BankSubscritpionDao.class);
    }
    @Transactional
    public BankSubscription update( BankSubscription entity ){
        return entityManager.merge( entity );
    }
    @Transactional
    public BankSubscription updateLastUnsuccessfulPayment(long id, Date date){
        BankSubscription bankSubscription = entityManager.find(BankSubscription.class, id);
        bankSubscription.setLastUnsuccessfulPaymentDate(date);
        bankSubscription.setUnsuccessfulPaymentsCount(bankSubscription.getUnsuccessfulPaymentsCount()+1);
        return bankSubscription;
    }

    @Transactional
    public BankSubscription updateLastsuccessfulPayment(BankSubscription bankSubscription, Date date){
        bankSubscription.setLastSuccessfulPaymentDate(date);
        return bankSubscription;
    }
}
