/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractViewPage<I extends AbstractEntityItem> extends BasicWorkspacePage {

    protected abstract String getPageFileName();
    protected abstract AbstractSelectedEntityGroupPage<I> getSelectedEntityGroupPage();

    protected I currentEntity;

    @Override
    public String getPageFilename() {
        return getPageFileName();
    }

    @Override
    public void onShow() throws Exception {
        getSelectedEntityGroupPage().show();
        currentEntity = getSelectedEntityGroupPage().getCurrentEntity();
    }

    public I getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(I currentEntity) {
        this.currentEntity = currentEntity;
    }
}
