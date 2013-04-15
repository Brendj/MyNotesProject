/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.city;

import ru.axetta.ecafe.processor.core.persistence.AuthorizationType;
import ru.axetta.ecafe.processor.core.persistence.City;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
/*@Component
@Scope("singleton")*/

/*@Component
@Scope("session")*/
public class CityEditPage extends BasicWorkspacePage {
  /*  @PersistenceContext
    private EntityManager entityManager;*/

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
        return "option/city/edit";
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

    public int getIndexOfAuthType() {
        return indexOfAuthType;
    }

    public void setIndexOfAuthType(int indexOfAuthType) {
        this.indexOfAuthType = indexOfAuthType;
    }

    public SelectItem[] getAuthTypeItems() {
        return authTypeItems;
    }

    public void setAuthTypeItems(SelectItem[] authTypeItems) {
        this.authTypeItems = authTypeItems;
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


    public void fill(Session session, Long idOfCity) throws Exception {
        DAOClientRoomService daoService= DAOClientRoomService.getInstance();
        //Query q=entityManager.createQuery("from User where idOfCity=:idOfCity");
       // q.setParameter("idOfCity",idOfCity);
       City city =daoService.getCity(idOfCity).get(0);
        //User user = (User) session.load(User.class, idOfCity);
        fill(session, city);
    }

    public void updateCity(Session session, Long idOfCity) throws Exception {
       // User user = (User) session.load(User.class, idOfCity);
        DAOClientRoomService daoService= DAOClientRoomService.getInstance();
       // Query q=entityManager.createQuery("from User where idOfCity=:idOfCity");
        //q.setParameter("idOfCity",idOfCity);
        City city = daoService.getCity(idOfCity).get(0);
       city.setActivity(this.activity);
        city.setContractIdMask(this.contractIdMask);
        city.setName(this.name);
        city.setUserName(this.userName);
        city.setPassword(this.password);
        city.setServiceUrl(this.serviceUrl);
        Integer idOfAuthorizationType=(Integer)authTypeItems[indexOfAuthType-1].getValue();

        AuthorizationType authType=daoService.getAuthorizationType(idOfAuthorizationType).get(0);
        city.setAuthorizationType(authType);

       // session.update(user);
       daoService.updateCity(city);
        fill(session, city);
    }

    private void fill(Session session, City city) throws Exception {
        getAllTypeItems();
        this.activity=city.getActivity();
         int index=0;
        for(SelectItem typeItem:authTypeItems){
            if(typeItem.getValue().equals(city.getAuthorizationType().getIdOfAuthorizationType()))
            {break;
            }

            index++; }
        this.indexOfAuthType=index+1;
        this.contractIdMask=city.getContractIdMask();
        this.idOfCity=city.getIdOfCity();
        this.name=city.getName();
        this.serviceUrl=city.getServiceUrl();
        this.userName=city.getUserName();
        this.password=city.getPassword();

    }

    /* @PostConstruct
    private void init(){getAllTypeItems();}*/
}