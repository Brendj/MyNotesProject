/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.city;

import ru.axetta.ecafe.processor.core.persistence.AuthorizationType;
import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class CityCreatePage extends BasicWorkspacePage {

    private Long idOfCity;
    private String  name;
    private Boolean activity;
    private int indexOfAuthType;

    private  String contractIdMask;
    private String userName;
    private String password;

    private String serviceUrl;

    private SelectItem[] authTypeItems;

    public String getPageFilename() {
        return "option/city/create";
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

    public int getIndexOfAuthType() {
        return indexOfAuthType;
    }

    public void setIndexOfAuthType(int indexOfAuthType) {
        this.indexOfAuthType = indexOfAuthType;
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

    public SelectItem[] getAuthTypeItems() {
        return authTypeItems;
    }

    public void setAuthTypeItems(SelectItem[] authTypeItems) {
        this.authTypeItems = authTypeItems;
    }

    public void fill(Session session) throws Exception {
       getAllTypeItems();
    }
    private void getAllTypeItems(){

        DAOClientRoomService daoService= DAOClientRoomService.getInstance();

        List<AuthorizationType> authTypes=daoService.getAuthorizationType(null);
        this.authTypeItems=new SelectItem[authTypes.size()] ;

        int index=0;
        for(AuthorizationType authType:authTypes){
            this.authTypeItems[index]=new SelectItem(authType.getIdOfAuthorizationType(),authType.getName());
            index++;
        }


    }
     @PostConstruct
     private void init(){
         getAllTypeItems();
     }

    public void createCity(Session session) throws Exception {
        City city=new City();
        city.setName(this.name);
        city.setActivity(this.activity);
        city.setUserName(this.getUserName());
        city.setPassword(this.getPassword());
        city.setServiceUrl(this.serviceUrl);
        city.setContractIdMask(this.contractIdMask);
        Integer idOfAuthorizationType=(Integer)authTypeItems[indexOfAuthType-1].getValue();
        DAOClientRoomService daoService= DAOClientRoomService.getInstance();
        AuthorizationType authType=daoService.getAuthorizationType(idOfAuthorizationType).get(0);
        city.setAuthorizationType(authType);


        daoService.createCity(city);
      //  session.save(user);
    }
}