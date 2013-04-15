/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.city;

import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */


public class CityViewPage extends BasicWorkspacePage {


    private Long idOfCity;
    private String  name;
    private Boolean activity;
    private String authTypeName;

    private  String contractIdMask;
    private String userName;
    private String password;

    private String serviceUrl;

    public String getPageFilename() {
        return "option/city/view";
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

    public String getAuthTypeName() {
        return authTypeName;
    }

    public void setAuthTypeName(String authTypeName) {
        this.authTypeName = authTypeName;
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

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void fill(Session session, Long idOfCity) throws Exception {
         DAOClientRoomService daoService= DAOClientRoomService.getInstance();
        // User user = (User) session.load(User.class, idOfUser);
       // Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
      //  q.setParameter("idOfUser",idOfUser);
       City city=daoService.getCity(idOfCity).get(0);
        this.activity=city.getActivity();
        this.authTypeName=city.getAuthorizationType().getName();
        this.contractIdMask=city.getContractIdMask();
        this.idOfCity=city.getIdOfCity();
        this.name=city.getName();
        this.serviceUrl=city.getServiceUrl();
        this.userName=city.getUserName();
        this.password=city.getPassword();
    }

}