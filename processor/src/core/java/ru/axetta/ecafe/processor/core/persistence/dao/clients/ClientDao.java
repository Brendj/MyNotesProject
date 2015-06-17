/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.ClientCount;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientDao extends WritableJpaDao {


    public static ClientDao getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientDao.class);
    }
    @Transactional
    public Client update( Client entity ){
        return entityManager.merge( entity );
    }

    @Transactional
    public List<Client> findAllByPassword(String password) {
        TypedQuery<Client> query = entityManager
                .createQuery("from Client c where c.cypheredPassword = :password", Client.class)
                .setParameter("password", password);
        return query.getResultList();    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Transactional
    public List<Client> findAllByOrg(Set<Long> orgsIdList ) {
        TypedQuery<Client> query = entityManager
                .createQuery("from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060", Client.class)
                .setParameter("orgsIdList", orgsIdList);
        return query.getResultList();
    }

    @Transactional
    public List<ClientCount> findAllStudentsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select idoforg, count(*) from cf_clients where idofclientgroup <  1100000000 group by idoforg ");
        List<ClientCount> result = new ArrayList<ClientCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new ClientCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }


    @Transactional
    public List<ClientCount> findAllBeneficiaryStudentsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select idoforg, count(*) from cf_clients where idofclientgroup <  :groupLimitation and DiscountMode > 0 group by idoforg ");
        nativeQuery.setParameter("groupLimitation", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
        List<ClientCount> result = new ArrayList<ClientCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new ClientCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public String extractSanFromClient(long idOfClient){
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Client.class);
        criteria.add(Restrictions.eq("idOfClient", idOfClient));
        criteria.setProjection(Projections.property("san"));
        return (String) criteria.uniqueResult();
    }
}
