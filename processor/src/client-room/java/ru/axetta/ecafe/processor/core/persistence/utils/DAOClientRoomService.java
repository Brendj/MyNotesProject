/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.SpringApplicationContext;
import ru.axetta.ecafe.processor.core.persistence.AuthorizationType;
import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
public class DAOClientRoomService {
    private static final Logger logger = LoggerFactory.getLogger(DAOClientRoomService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public  List<City> getTowns(Boolean activity){
        TypedQuery<City> query ;
        if(activity==null){
            query = entityManager.createQuery("from City", City.class);
            return query.getResultList();
        }
        query = entityManager.createQuery("from City where activity=:activity", City.class);
        query.setParameter("activity",activity);
        return query.getResultList();
     }
    public List<City> getCity(Long idOfCity){
        TypedQuery<City> query;
        if(idOfCity!=null){
            query=entityManager.createQuery("from City where idOfCity=:idOfCity",City.class);
            query.setParameter("idOfCity",idOfCity);
            return query.getResultList();
        }
        query=entityManager.createQuery("from City",City.class);
        return query.getResultList();
    }

    @Transactional
    public City getCityByName(String name){
        TypedQuery<City> query = entityManager.createQuery("from City where name=:name", City.class);
          query.setParameter("name",name) ;
         List<City>result=query.getResultList();
        if(result==null || result.size()==0){return null;}
        return result.get(0);

    }

    public static DAOClientRoomService getInstance() {
        return(DAOClientRoomService) SpringApplicationContext.getBean("DAOService");
    }
    @Transactional
    public List<User> getUser(Long idOfUser){
        TypedQuery<User> query=null;
        if(idOfUser==null){
      query=entityManager.createQuery("from User ",User.class);
         return query.getResultList();
        }
        query=entityManager.createQuery("from User where idOfUser=:idOfUser",User.class);
       query.setParameter("idOfUser",idOfUser);
       return query.getResultList();

    }
    @Transactional
    public List<Function> getFunction(Long idOfFunction){
        TypedQuery<Function> query=null;
        if(idOfFunction==null){
            query=entityManager.createQuery("from Function ",Function.class);
            return query.getResultList();
        }

        query=entityManager.createQuery("from Function where idOfFunction=:idOfFunction",Function.class);
        query.setParameter("idOfFunction",idOfFunction);
        return query.getResultList();

    }
    @Transactional
    public void updateUser(User user){
        entityManager.merge(user);
    }
    @Transactional
    public void updateCity(City city){
        entityManager.merge(city);
    }

    @Transactional
    public void updateFunction(Function function){
        entityManager.merge(function);
    }
    @Transactional
    public void deleteUser(User user){
        user=entityManager.merge(user);
       entityManager.remove(user);

   }

    @Transactional
    public void deleteCity(City city){
       city=entityManager.merge(city);
        logger.info("from DAOService: idOfCity="+city.getIdOfCity());
        entityManager.remove(city);

    }
    @Transactional
    public void deleteFunction(Function function){
        entityManager.remove(function);
    }

   @Transactional
   public void  createUser(User user){
       entityManager.persist(user);
   }

    @Transactional
    public void  createCity(City city){
        entityManager.persist(city);
    }
    @Transactional
    public List<User> getUserByName(String userName) throws Exception{
        TypedQuery<User> query=null;
        if(userName==null){
            return null;
        }
        query=entityManager.createQuery("from User where userName=:userName",User.class);
        query.setParameter("userName",userName);
        return query.getResultList();

    }

    @Transactional
    public List<AuthorizationType>getAuthorizationType(Integer idOfAuthorizationType){
        TypedQuery<AuthorizationType>query;
        if(idOfAuthorizationType==null){
        query=entityManager.createQuery("from AuthorizationType",AuthorizationType.class);
        return query.getResultList();
        }
        query=entityManager.createQuery("from AuthorizationType where idOfAuthorizationType=:idOfAuthorizationType",AuthorizationType.class);
        query.setParameter("idOfAuthorizationType",idOfAuthorizationType);
        return query.getResultList();



    }
}
