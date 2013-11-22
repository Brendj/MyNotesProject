/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Scope("session")
public class UvDeletePage extends AbstractModalPage {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    
    protected Object currentEntityItem;
    private AbstractListPage listPage;

    @Override
    public void show() {
        listPage = (AbstractListPage)MainPage.getSessionInstance().getCurrentWorkspacePage();
        super.show();
    }

    @Transactional
    public Object delete(){
        try{
            ((AbstractEntityItem)currentEntityItem).removeEntity(entityManager);
            listPage.getItemList().remove(currentEntityItem);
            listPage.printMessage("Объект удален: "+currentEntityItem);
        } catch (Exception e){
            listPage.logAndPrintMessage("Ошибка при удалении объекта.", e);
        }
        hide();
        return null;
    }

    public Object getCurrentEntityItem() {
        return currentEntityItem;
    }

    public void setCurrentEntityItem(Object currentEntityItem) {
        this.currentEntityItem = currentEntityItem;
    }
}
