/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSelectedEntityGroupPage<I extends AbstractEntityItem> extends BasicWorkspacePage {

    protected abstract String buildTitle();

    private String title;
    private I currentEntity;
    private Long currentEntityId;
    public abstract I getEntity();

    @Override
    public void onShow() throws Exception {
        if (null == currentEntityId) {
            this.title = null;
        } else {
            this.currentEntity = getEntity();
            this.title = buildTitle();
        }
    }

    public String getTitle() {
        return title;
    }

    public I getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(I currentEntity) {
        this.currentEntity = currentEntity;
    }

    public Long getCurrentEntityId() {
        return currentEntityId;
    }

    public void setCurrentEntityId(Long currentEntityId) {
        this.currentEntityId = currentEntityId;
    }
}
