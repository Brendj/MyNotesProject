/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListPage<E> extends BasicWorkspacePage {

    protected abstract EntityManager getEntityManager();
    protected abstract String getPageFileName();
    protected abstract Class<E> getEntityClass();
    protected abstract String getFilter();

    protected List<E> entityList;

    @Override
    public String getPageFilename() {
        return getPageFileName();
    }

    @Override
    public String getPageTitle() {
        return String.format("%s (%d)",super.getPageTitle(), entityList.size());
    }

    @Override
    public void onShow() throws Exception {
         fill();
    }

    private void fill(){
        String query = String.format("from %s %s order by id",getEntityClass().getSimpleName(), getFilter());
        entityList = getEntityManager().createQuery(query,getEntityClass()).getResultList();
    }

    public List<E> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<E> entityList) {
        this.entityList = entityList;
    }
}
