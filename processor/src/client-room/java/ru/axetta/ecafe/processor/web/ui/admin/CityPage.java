/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.admin;

import ru.axetta.ecafe.processor.core.persistence.AuthorizationType;
import ru.axetta.ecafe.processor.core.persistence.City;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 20.08.12
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class CityPage {


    private static final Logger logger = LoggerFactory.getLogger(CityPage.class);

    @PersistenceContext
    private  EntityManager entityManager;

    private SelectItem[] authTypeItems;
    private List<City> cities;

    private ArrayList<CityItem> cityItems;



    public ArrayList<CityItem> getCityItems() {

        return cityItems;
    }

    public void setCityItems(ArrayList<CityItem> cityItems) {

        this.cityItems = cityItems;
    }




    @Transactional
    protected void getAllTypeItems(){
        TypedQuery<AuthorizationType> query = entityManager.createQuery("select a from AuthorizationType a", AuthorizationType.class);
        List<AuthorizationType> authTypes=query.getResultList();
        this.authTypeItems=new SelectItem[authTypes.size()];
        int index=0;
        for(AuthorizationType authType:authTypes){
           this.authTypeItems[index]=new SelectItem(authType.getIdOfAuthorizationType(),authType.getName());
            index++;
        }
    }


    @PostConstruct
    public void init(){
        cityItems=new ArrayList<CityItem>();
        loadCitiesList();
        getAllTypeItems();

    }

    public  void loadCitiesList() {
        TypedQuery<City> query = entityManager.createQuery("from City order by idOfCity", City.class);
        cities = query.getResultList();
        cityItems=new ArrayList<CityItem>();
         for(City city:cities){
             CityItem cityItem=new CityItem();
             cityItem.fill(city);
             cityItems.add(cityItem);
         }
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }


    public SelectItem[] getAuthTypeItems() {
        return authTypeItems;
    }

    public void setAuthTypeItems(SelectItem[] authTypeItems) {
        this.authTypeItems = authTypeItems;
    }

    @Transactional
     public Object addCity(){
         City newCity =new City();
         TypedQuery<AuthorizationType> query = entityManager.createQuery("from AuthorizationType where idOfAuthorizationType=1", AuthorizationType.class);
         newCity.setAuthorizationType(query.getResultList().get(0));
          newCity.setActivity(true);
         entityManager.persist(newCity);
         loadCitiesList();
         return null;
     }

      @Transactional
    public Object deleteCity(Long idOfCity){
        Query q=entityManager.createQuery("delete from City where idOfCity=:idOfCity");
        q.setParameter("idOfCity",idOfCity);
        q.executeUpdate();
        loadCitiesList();
        return null;

    }
    @Transactional
    public Object save(){
        for (int i=0;i<cityItems.size();i++){
             CityItem cityItem=cityItems.get(i);
            TypedQuery<City> cityQuery = entityManager.createQuery("from City where idOfCity=:idOfCity", City.class);
            cityQuery.setParameter("idOfCity",cityItem.getIdOfCity()) ;
            List<City> cityList= cityQuery.getResultList();
            City city=cityList.get(0);
             logger.info("cityItem: "+cityItem.getName());


            city.setIdOfCity(cityItem.getIdOfCity());
            city.setActivity(cityItem.getActivity());
            city.setContractIdMask(cityItem.getContractIdMask());
            city.setName(cityItem.getName());
            city.setPassword(cityItem.getPassword());
            city.setServiceUrl(cityItem.getServiceUrl());
            city.setUserName(cityItem.getUserName());
            Integer authTypeId=(Integer)authTypeItems[cityItem.getIndexOfAuthType()-1].getValue();
            TypedQuery<AuthorizationType> typeQuery = entityManager.createQuery("from AuthorizationType where idOfAuthorizationType=:idOfAuthType", AuthorizationType.class);
            typeQuery.setParameter("idOfAuthType",authTypeId);
            AuthorizationType type=typeQuery.getResultList().get(0);
            city.setAuthorizationType(type);
            City newCity =entityManager.merge(city);
        }
        loadCitiesList();
       return null;

    }

}
