/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
  @Table(name="cf_authorization_types")
 @Entity
public class AuthorizationType {

    @Id
    @Column(name = "idOfAuthorizationType")
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="cf_authorization_types_id_seq")
    @SequenceGenerator(name="cf_authorization_types_id_seq", sequenceName="cf_authorization_types_id_seq", allocationSize = 1)
  private Integer idOfAuthorizationType;


    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 128, precision = 0)
    @Basic
    private String name;

    @OneToMany(mappedBy = "authorizationType", fetch = FetchType.LAZY)
    private Set<City> cities = new HashSet<City>();


    public Integer getIdOfAuthorizationType() {
        return idOfAuthorizationType;
    }

    public void setIdOfAuthorizationType(Integer idOfAuthorizationType) {
        this.idOfAuthorizationType = idOfAuthorizationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> towns) {
        this.cities = towns;
    }
}
