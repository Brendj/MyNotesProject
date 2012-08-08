/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.08.12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeletePage<E> {

    protected abstract DAOService getDAOService();
    protected E currentEntity;
    protected abstract AbstractListPage getAbstractListPage();

    public Object delete(){
        try{
            getDAOService().deleteEntity(currentEntity);
            getAbstractListPage().getEntityList().remove(currentEntity);
            getAbstractListPage().printMessage("Выделенный объект удален.");
        } catch (Exception e){
            getAbstractListPage().printError("Ошибка при удалении обекта.");
            getAbstractListPage().getLogger().error("Error delete entity: ", e);
        }
        return null;
    }

    public E getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(E currentEntity) {
        this.currentEntity = currentEntity;
    }
}
