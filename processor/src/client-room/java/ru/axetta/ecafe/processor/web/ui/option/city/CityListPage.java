/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.city;

import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */

/*@Component
@Scope("singleton")*/
public class CityListPage extends BasicWorkspacePage {
    final Logger logger = LoggerFactory
            .getLogger(CityListPage.class);

  /*  @PersistenceContext
    private EntityManager entityManager;*/


  /*  @PostConstruct
    public void init(){


    }*/

    public static class Item {


        private  Long idOfCity;
        private  String name;
        private Boolean activity;
        private String authorizationType;

        public Item(City city) {
            this.idOfCity=city.getIdOfCity();
            this.name=city.getName();
            this.activity=city.getActivity();
            this.authorizationType=city.getAuthorizationType().getName();

        }

        public Long getIdOfCity() {
            return idOfCity;
        }

        public void setIdOfCity(Long idOfCity) {
            this.idOfCity = idOfCity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getActivity() {
            return activity;
        }

        public void setActivity(Boolean activity) {
            this.activity = activity;
        }

        public String getAuthorizationType() {
            return authorizationType;
        }

        public void setAuthorizationType(String authorizationType) {
            this.authorizationType = authorizationType;
        }
    }

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "option/city/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    /*public void fill(Session session) throws Exception {

        List<Item> items = new LinkedList<Item>();
        Criteria criteria = session.createCriteria(User.class);
        List users = criteria.list();
        for (Object object : users) {
            User user = (User) object;
            items.add(new Item(user));
        }
        this.items = items;
    }*/

    public void fill(Session session) throws Exception {
         DAOService daoService= DAOService.getInstance();
        List<Item> items = new LinkedList<Item>();
        //Criteria criteria = session.createCriteria(User.class);
         //logger.info("entityManager: "+entityManager);
        // Query q=entityManager.createQuery("from User") ;
        List<City> cities = daoService.getCity(null);
        for (City city : cities) {

            items.add(new Item(city));
        }
        this.items = items;
    }

    public void removeCity(Session session, Long idOfCity) throws Exception {
        DAOService daoService= DAOService.getInstance();
       // User user = (User) session.load(User.class, idOfCity);
       // Query q=entityManager.createQuery("from User where idOfCity=:idOfCity");
       // q.setParameter("idOfCity",idOfCity);
        City city = daoService.getCity(idOfCity).get(0);
        logger.info("from CityListPage: city="+city.getName()+" idOfCity="+city.getIdOfCity());
        //session.delete(user);
        daoService.deleteCity(city);
        fill(session);
    }
}