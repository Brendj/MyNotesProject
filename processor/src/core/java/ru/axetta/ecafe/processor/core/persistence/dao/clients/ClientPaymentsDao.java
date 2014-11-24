/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

/**
 * User: shamil
 * Date: 20.11.14
 * Time: 18:58
 */
@Repository
@Transactional
public class ClientPaymentsDao  {
    @PersistenceContext(unitName = "processorPU")
    protected EntityManager entityManager;

    public static ClientPaymentsDao getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientPaymentsDao.class);
    }

    public Long findAllIn5Minutes(long idofclient){
        //long requiredTime = ((new Date()).getTime() - 5*60*1000);
        long requiredTime = 1413919382408L - 5*60*1000;
        List list =  entityManager.createNativeQuery("select cp.idofclientpayment " + "from cf_clientpayments cp "
                + "inner join cf_transactions t on cp.idoftransaction = t.idoftransaction "
                + "where t.idofclient = :idofclient "
                + " and cp.paymentmethod <> :paymentMethod " + "and cp.createddate > :requiredTime "
                + "order by createddate desc ")
                .setParameter("requiredTime", requiredTime)
                .setParameter("idofclient", idofclient)
                .setParameter("paymentMethod", ClientPayment.AUTO_PAYMENT_METHOD)
                .setMaxResults(1)
                .getResultList();

        return !list.isEmpty() ? ((BigInteger)list.get(0)).longValue() : null;
    }

    public void save( ClientPayment entity) {
        entityManager.persist( entity );
    }

    public void updatePaymentMethod(long idOfClientPayment, int autoPaymentMethod) {
        entityManager.createNativeQuery("UPDATE cf_clientpayments  SET paymentmethod = :autoPaymentMethod where idofclientpayment =:idOfClientPayment")
                .setParameter("autoPaymentMethod",autoPaymentMethod)
                .setParameter("idOfClientPayment",idOfClientPayment)
                .executeUpdate();
    }
}
