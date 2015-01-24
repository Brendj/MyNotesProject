/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class BankSubscriptionDao extends WritableJpaDao {


    public static BankSubscriptionDao getInstance() {
        return RuntimeContext.getAppContext().getBean(BankSubscriptionDao.class);
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
    public BankSubscription updateLastSuccessfulPayment(long id, Date date){
        BankSubscription bankSubscription = entityManager.find(BankSubscription.class, id);
        bankSubscription.setLastSuccessfulPaymentDate(date);
        return bankSubscription;
    }

    @Transactional
    public Long findSubscription(Long contractId) {
        Query query = entityManager.createQuery("select distinct bs from BankSubscription bs \n" +
                " where bs.active = true and bs.client.balance <= bs.thresholdAmount \n " +
                " and bs.client.contractId = :contractId " +
                " and (bs.client.idOfClientGroup not in (:cg) or bs.client.idOfClientGroup is null)",
                BankSubscription.class).setParameter("contractId", contractId).setParameter("cg",
                Arrays.asList(ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                        ClientGroup.Predefined.CLIENT_DELETED.getValue()));
        List<BankSubscription> resultList = query.getResultList();

        if ((resultList != null) && (resultList.size() > 0)) {
            BankSubscription subscription = resultList.get(0);
            Long today = CalendarUtils.truncateToDayOfMonth(new Date()).getTime();
            //Если у человека есть запросы сегодня со статусом MfrRequest.PAYMENT_RECEIVED_BY_BK, то сбрасываем
            for (RegularPayment payment : subscription.getRegularPayments()) {
                if((today<payment.getPaymentDate().getTime())&&(MfrRequest.PAYMENT_RECEIVED_BY_BK.equals(payment.getStatus()))){
                    return null;
                }
            }
            return subscription.getIdOfSubscription();
        } else {
            return null;
        }
    }
}
