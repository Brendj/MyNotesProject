package ru.axetta.ecafe.processor.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * User: shamil
 * Date: 14.08.14
 * Time: 13:12
 */
public abstract class AbstractJpaDao< T extends Serializable> {

    private Class< T > clazz;

    @PersistenceContext(unitName = "processorPU")
    EntityManager entityManager;

    public void setClazz( Class< T > clazzToSet ){
        this.clazz = clazzToSet;
    }

    public T findOne( Long id ){
        return entityManager.find( clazz, id );
    }
    public List< T > findAll(){
        return entityManager.createQuery( "from " + clazz.getName() )
                .getResultList();
    }

    public void save( T entity ){
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
