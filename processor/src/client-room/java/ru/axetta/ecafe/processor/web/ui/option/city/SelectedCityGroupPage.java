/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.city;

import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
/*@Component
      @Scope("singleton")*/
public class SelectedCityGroupPage extends BasicWorkspacePage {
    /*@PersistenceContext
    private EntityManager entityManager;*/

    private String cityName;

    public String getCityName() {
        return cityName;
    }


    public void fill(Session session, Long idOfCity) throws Exception {
         DAOService daoService= DAOService.getInstance();
       // Query q=entityManager.createQuery("from User where idOfCity=:idOfCity");
       // q.setParameter("idOfCity",idOfCity);
       List<City> cities= daoService.getCity(idOfCity) ;
        if (cities == null || cities.size()==0) {
            this.cityName = null;
        } else {
            this.cityName = cities.get(0).getName();
        }
    }

}