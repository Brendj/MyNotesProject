/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

public abstract class AbstractEditPage<I extends AbstractEntityItem> extends BasicWorkspacePage {
    @PersistenceContext(unitName = "processorPU")
    protected EntityManager entityManager;
    @Autowired
    @Qualifier(value = "txManager")
    protected PlatformTransactionManager transactionManager;

    @Autowired
    protected DAOService daoService;

    protected I currentItem;

    @Override
    public abstract String getPageFilename();
    protected abstract boolean onCheckRequiredFields();

    public AbstractSelectedEntityGroupPage getSelectedEntityGroupPage() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        AbstractSelectedEntityGroupPage selectedEntityGroupPage = (AbstractSelectedEntityGroupPage)session.getAttribute(getPageFilename()+".groupPage");
        if (selectedEntityGroupPage==null) {
            selectedEntityGroupPage = new AbstractSelectedEntityGroupPage();
            session.setAttribute(getPageFilename() + ".groupPage", selectedEntityGroupPage);
        }
        return selectedEntityGroupPage;
    }

    @Override
    public void onShow() throws Exception {
        //getSelectedEntityGroupPage().show();
        currentItem = (I)getSelectedEntityGroupPage().getCurrentEntityItem();
        if(currentItem!=null){
            reload();
        }
    }

    public Object reload() {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                currentItem.refreshEntity(entityManager);
                return null;
            }
        });
    }

    public Object save(){
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                onSave();
                getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
                return null;
            }
        });
    }

    public Object create(){
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                onCreate();
                getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
                return null;
            }
        });
    }

    protected void onCreate(){
        try {
            if(onCheckRequiredFields()){
                currentItem.createEntity(entityManager);
                printMessage(currentItem.toString()+ " успешно создан.");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при создании: "+currentItem, e);
        }
    }

    protected void onSave() {
        try {
            if(onCheckRequiredFields()){
                currentItem.updateEntity(entityManager);
                printMessage(currentItem+" успешно изменен.");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при изменении: "+currentItem, e);
        }
    }


    public I getCurrentEntity() {
        return currentItem;
    }

    public void setCurrentEntity(I currentEntity) {
        this.currentItem = currentEntity;
    }
    
    public boolean isReadonly() {
        return false;
    }
    public boolean isEditMode() {
        return true;
    }
    public boolean isCreateMode() {
        return false;
    }
}
