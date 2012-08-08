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
public abstract class AbstractListPage<T> extends BasicWorkspacePage {

    protected abstract EntityManager getEntityManager();
    protected abstract String getPageFileName();
    protected abstract Class<T> getEntityClass();
    protected abstract String getFilter();

    protected List<T> entityList;

    @Override
    public String getPageFilename() {
        return getPageFileName();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", entityList.size());
    }

    @Override
    public void onShow() throws Exception {
         fill();
    }

    private void fill() throws IllegalAccessException, InstantiationException {
        String query = "from "+getEntityClass().getSimpleName()+" "+ getFilter() +" order by id";
        entityList = getEntityManager().createQuery(query,getEntityClass()).getResultList();
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }
}
