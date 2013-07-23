package ru.axetta.ecafe.processor.core.daoservices.discount.rules;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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

    @PersistenceContext
    private EntityManager entityManager;



}
