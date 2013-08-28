/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AbstractSelectedEntityGroupPage extends BasicWorkspacePage {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String buildTitle() {
        if (currentEntityItem==null) return "Нет выбранного объекта";
        return currentEntityItem.toString();
    }

    private Object currentEntityItem;
    private Long currentEntityItemId;

    public String getTitle() {
        return buildTitle();
    }

    public Object getCurrentEntityItem() {
        return currentEntityItem;
    }

    public void setCurrentEntityItem(Object currentEntityItem) {
        this.currentEntityItem = currentEntityItem;
    }

    public Long getCurrentEntityItemId() {
        return currentEntityItemId;
    }

    public void setCurrentEntityItemId(Long currentEntityItemId) {
        this.currentEntityItemId = currentEntityItemId;
    }
}
