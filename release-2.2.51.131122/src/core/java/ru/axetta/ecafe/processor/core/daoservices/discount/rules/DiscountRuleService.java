package ru.axetta.ecafe.processor.core.daoservices.discount.rules;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.07.13
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class DiscountRuleService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;



}
