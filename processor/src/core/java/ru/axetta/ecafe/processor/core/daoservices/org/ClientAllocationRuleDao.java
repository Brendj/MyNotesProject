/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.org;

import ru.axetta.ecafe.processor.core.persistence.ClientAllocationRule;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 03.09.13
 * Time: 13:06
 */

@Repository
public class ClientAllocationRuleDao {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<ClientAllocationRule> findAll() {
        TypedQuery<ClientAllocationRule> query = em
                .createQuery("select distinct c from ClientAllocationRule as c order by c.id", ClientAllocationRule.class);
        return query.getResultList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientAllocationRule saveOrUpdate(ClientAllocationRule c) {
        if (c.getId() == null) {
            em.persist(c);
            return c;
        } else {
            return em.merge(c);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ClientAllocationRule rule = em.getReference(ClientAllocationRule.class, id);
        em.remove(rule);
    }

    @Transactional(readOnly = true)
    public ClientAllocationRule find(Long id) {
        return em.find(ClientAllocationRule.class, id);
    }
}
