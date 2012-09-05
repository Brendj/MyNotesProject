/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
@Table(name = "cf_cities")
@Entity
public class City {
    @Id
    @Column(name = "idOfCity")
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="cf_cities_idofcity_seq")
    @SequenceGenerator(name="cf_cities_idofcity_seq", sequenceName="cf_cities_idofcity_seq", allocationSize = 1)

   private  Long idOfCity;

    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
    private  String name;

    @Column(name = "activity", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
    private Boolean activity;

    @Column(name = "serviceUrl", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
   private  String serviceUrl;

    @Column(name = "contractIdMask", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
   private  String contractIdMask;

    @Column(name = "userName", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
    private  String userName;

    @Column(name = "password", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
    private  String password;



    @JoinColumn(name = "idOfAuthorizationType", referencedColumnName = "idOfAuthorizationType")
    @ManyToOne(fetch = FetchType.EAGER)
   private  AuthorizationType authorizationType;

    public City(){
        }


    public Long getIdOfCity() {
        return idOfCity;
    }

    public void setIdOfCity(Long idOfCity) {
        this.idOfCity = idOfCity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getContractIdMask() {
        return contractIdMask;
    }

    public void setContractIdMask(String contractIdMask) {
        this.contractIdMask = contractIdMask;
    }

    public AuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public void setAuthorizationType(AuthorizationType authorizationType) {
        this.authorizationType = authorizationType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
