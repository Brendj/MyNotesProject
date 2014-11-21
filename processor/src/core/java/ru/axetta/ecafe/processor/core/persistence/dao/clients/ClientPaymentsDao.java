/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.persistence.ClientPayment;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
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


    public ClientPayment findAllIn5Minutes(long idofclient){
        long requiredTime = ((new Date()).getTime() - 5*60*1000);
        List<ClientPayment> list =  entityManager.createQuery( "from ClientPayment cp "
                + " where cp.createTime > :requiredTime "
                + " and cp.transaction.client.idOfClient = :idofclient "
                + " and cp.paymentMethod != :paymentMethod "
                + " order by cp.createTime desc", ClientPayment.class )
                .setParameter("requiredTime", requiredTime)
                .setParameter("idofclient", idofclient)
                .setParameter("paymentMethod",ClientPayment.AUTO_PAYMENT_METHOD )
                .setMaxResults(1)
                .getResultList();

        return !list.isEmpty() ? list.get(0) : null;
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
