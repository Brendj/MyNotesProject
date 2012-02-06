/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class CategoryOrg {

    private long idOfCategoryOrg;
    private String categoryName;
    private Set<Org> orgsInternal;

    public Set<Org> getOrgs() {
        return Collections.unmodifiableSet(getOrgsInternal());
    }

    private Set<Org> getOrgsInternal() {
        return orgsInternal;
    }

    private void setOrgsInternal(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getIdOfCategoryOrg() {
        return idOfCategoryOrg;
    }

    public void setIdOfCategoryOrg(long idOfCategoryOrg) {
        this.idOfCategoryOrg = idOfCategoryOrg;
    }
}
