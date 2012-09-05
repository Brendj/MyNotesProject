/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.admin;

import ru.axetta.ecafe.processor.core.SpringApplicationContext;
import ru.axetta.ecafe.processor.core.persistence.City;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 20.08.12
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */

public class CityItem implements Serializable {
    final Logger logger = LoggerFactory
            .getLogger(CityItem.class);
   private String name;
    private  String contractIdMask;
    private String userName;
    private String password;
    private Boolean activity;
    private int indexOfAuthType;
    private String serviceUrl;
    private Long idOfCity;




    public void fill(City city){
        //CityItem cityItem=new CityItem();
        this.setActivity(city.getActivity());
        this.setIndexOfAuthType(city.getAuthorizationType().getIdOfAuthorizationType());
        this.setContractIdMask(city.getContractIdMask());
        this.setIdOfCity(city.getIdOfCity());
        this.setName(city.getName());
        this.setPassword(city.getPassword());
        this.setServiceUrl(city.getServiceUrl());
        this.setUserName(city.getUserName());
        //return cityItem;

    }




    public String getName() {
        return name;
    }



    public void setName(String name) {
        logger.info("cityItem.setName() new value="+name);
        this.name = name;
    }

    public String getContractIdMask() {
        return contractIdMask;
    }

    public void setContractIdMask(String contractIdMask) {
        this.contractIdMask = contractIdMask;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }


    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Long getIdOfCity() {
        return idOfCity;
    }

    public void setIdOfCity(Long idOfCity) {
        this.idOfCity = idOfCity;
    }

    public int getIndexOfAuthType() {
        return indexOfAuthType;
    }

    public void setIndexOfAuthType(int indexOfAuthType) {
        logger.info("setIndexOfAuthType(): "+indexOfAuthType);
        this.indexOfAuthType = indexOfAuthType;
    }

    public Object delete(){

        CityPage cityPage=(CityPage) SpringApplicationContext.getBean("cityPage");
        return cityPage.deleteCity(idOfCity);

    }
}
