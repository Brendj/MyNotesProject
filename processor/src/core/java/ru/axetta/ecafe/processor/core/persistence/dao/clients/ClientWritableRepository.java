/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: shamil
 * Date: 22.04.15
 * Time: 13:53
 */
@Repository
@Transactional
public class ClientWritableRepository extends WritableJpaDao {

    public static ClientWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ClientWritableRepository.class);
    }

    public Client find( Long id ) {
        return entityManager.find( Client.class, id );
    }

    public Client findWithCards( Long id ){
        TypedQuery<Client> query = entityManager
                .createQuery("select c from Client c left join fetch c.cardsInternal where c.idOfClient=:idOfClient", Client.class);
        query.setParameter("idOfClient",id);
        List<Client> resultList = query.getResultList();
        if(resultList.size()> 0){
            return resultList.get(0);
        }else {
            return null;
        }
    }

    public void saveEntity(Client client) {
        entityManager.merge(client);
    }
    public void update(Client client) {
        entityManager.merge(client);
    }

    public void refresh(Client client){
        entityManager.refresh(client);
    }
}
