/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.banks;

import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 10:02
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class BankDeletePage{

    @Autowired
    private DAOService daoService;
    @Autowired
    private BankListPage bankListPage;

    protected Bank currentEntity;

    public Object delete(){
        try{
            daoService.deleteEntity(currentEntity);
            bankListPage.getEntityList().remove(currentEntity);
            bankListPage.printMessage("Выделенный объект удален.");
        } catch (Exception e){
            bankListPage.printError("Ошибка при удалении обекта.");
            bankListPage.getLogger().error("Error delete entity: ", e);
        }
        return null;
    }

    public Bank getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(Bank currentEntity) {
        this.currentEntity = currentEntity;
    }

}
