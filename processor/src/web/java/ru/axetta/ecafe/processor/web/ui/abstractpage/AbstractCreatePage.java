/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.08.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCreatePage<I extends AbstractEntityItem> extends BasicWorkspacePage {

    protected abstract String getPageFileName();
    protected abstract void onSave();
    protected abstract EntityManager getEntityManager();
    protected I currentEntity;

    @Override
    public String getPageFilename() {
        return getPageFileName();
    }

    @Override
    public void onShow() throws Exception {

    }

    public Object save(){
        onSave();
        return null;
    }

    public I getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(I currentEntity) {
        this.currentEntity = currentEntity;
    }
}
