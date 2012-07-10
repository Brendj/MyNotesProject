/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.product.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group.TechnologicalMapGroupCreatePage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductGroupEditPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductGroupEditPage.class);
    private ProductGroup currentProductGroup;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        //RuntimeContext.getAppContext().getBean(getClass()).reload();
        currentProductGroup = entityManager.merge(currentProductGroup);

    }

    public Object onSave(){
        try {
            //RuntimeContext.getAppContext().getBean(getClass()).save();
            //currentProductGroup = entityManager.merge(currentProductGroup);
            currentProductGroup = (ProductGroup) DAOService.getInstance().mergeDistributedObject(currentProductGroup,currentProductGroup.getGlobalVersion()+1);
            printMessage("Группа для продуктов сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы для продуктов.");
            logger.trace("Error saved Product Group",e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentProductGroup.getDeletedState()) {
            printMessage("Группа не может быть удалена.");
            return;
        }
        try{
            ProductGroup pg = entityManager.getReference(ProductGroup.class, currentProductGroup.getGlobalId());
            entityManager.remove(pg);
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            logger.error("Error by delete Product Group.", e);
        }
    }

    @Transactional
    private void reload() throws Exception{
        //currentProductGroup = entityManager.find(ProductGroup.class, currentProductGroup.getGlobalId());
        currentProductGroup = entityManager.merge(currentProductGroup);
    }

    @Transactional
    private void save() throws Exception{
        currentProductGroup = (ProductGroup) DAOService.getInstance().mergeDistributedObject(currentProductGroup,currentProductGroup.getGlobalVersion()+1);
    }

    public String getPageFilename() {
        return "option/product/group/edit";
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public void setCurrentProductGroup(ProductGroup currentProductGroup) {
        this.currentProductGroup = currentProductGroup;
    }
}
