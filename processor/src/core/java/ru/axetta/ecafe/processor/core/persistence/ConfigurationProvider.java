/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 14.05.12
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProvider {
    private Long idOfConfigurationProvider;
    private String name;
    private Set products;
    private Set<Org> orgInternal;


    public List<Org> getOrgs(){
        return new ArrayList<Org>(Collections.unmodifiableSet(getOrgInternal()));
    }

    public void add(Org org){
        orgInternal.add(org);
    }

    private Set<Org> getOrgInternal() {
        return orgInternal;
    }

    private void setOrgInternal(Set<Org> orgInternal) {
        this.orgInternal = orgInternal;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getProducts() {
        return products;
    }

    public void setProducts(Set products) {
        this.products = products;
    }
}
