/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * User: shamil
 * Date: 20.02.15
 * Time: 13:12
 */
public abstract class WritableAbstractJpaDao< T extends Serializable> extends WritableJpaDao implements IGenericDao<T> {

    private Class< T > clazz;

    public void setClazz( Class< T > clazzToSet ){
        this.clazz = clazzToSet;
    }

    @Override
    public T findOne(long id) {
        return entityManager.find( clazz, id );
    }

    public List< T > findAll(){
        return entityManager.createQuery( "from " + clazz.getName() )
                .getResultList();
    }

    public List<T> findAllByIdOfOrg(long idOfOrg ){
        return entityManager.createQuery( "from " + clazz.getName() + " where idoforg=:idOfOrg" )
                .setParameter("idOfOrg",idOfOrg)
                .getResultList();
    }

    @Transactional
    public void create( T entity ){
        entityManager.persist( entity );
    }

    public T update( T entity ){
        return entityManager.merge( entity );
    }

    public void delete( T entity ){
        entityManager.remove( entity );
    }
    public void deleteById(final long entityId ){
        T entity = findOne(entityId);
        delete( entity );
    }
}
