/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.regularpayments;

import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.DuplicatePaymentException;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.NotFoundPaymentException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: Shamil
 * Date: 23.12.14
 */
@Repository
@Transactional(value = "txManager")
public class RegularPaymentsRepository {
    @PersistenceContext(unitName = "processorPU")
    protected EntityManager entityManager;

    public RegularPayment findPayment(Long actionId){
        return (RegularPayment) entityManager.createQuery("from RegularPayment p left join fetch p.client where p.idOfPayment = :idOfPayment ")
                .setParameter("idOfPayment", actionId)
                .getSingleResult();
    }

    @Transactional(value = "txManager")
    public void updateRegularPayment(long idofpayment, String status, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        RegularPayment payment = entityManager.find(RegularPayment.class, idofpayment);
        if (payment != null) {
            if ((!payment.isSuccess())) {
                payment.setStatus(status);

                if (realAmount !=null){
                    payment.setPaymentAmount(realAmount);
                }
                //payment.setAuthCode(ip);
                entityManager.merge(payment);
            } else {
                throw new DuplicatePaymentException();
            }
        } else {
            throw new NotFoundPaymentException();
        }
    }

    @Transactional(value = "txManager")
    public RegularPayment finalizeRegularPayment(long idofpayment, String status, Long realAmount, String ip)
            throws DuplicatePaymentException, NotFoundPaymentException {
        RegularPayment payment = entityManager.find(RegularPayment.class, idofpayment);
        if (payment != null) {
            if ((!payment.isSuccess())) {
                payment.setStatus(status);
                payment.setSuccess(true);

                if (realAmount !=null){
                    payment.setPaymentAmount(realAmount);
                }
                //payment.setAuthCode(ip);
                entityManager.merge(payment);
            } else {
                throw new DuplicatePaymentException();
            }
        } else {
            throw new NotFoundPaymentException();
        }

        return payment;
    }
}
