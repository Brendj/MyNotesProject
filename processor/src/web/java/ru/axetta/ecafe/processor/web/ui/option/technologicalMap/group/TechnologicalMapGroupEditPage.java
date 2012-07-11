/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItem;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapGroupEditPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        //RuntimeContext.getAppContext().getBean(getClass()).reload();
        currentTechnologicalMapGroup = entityManager.merge(currentTechnologicalMapGroup);
        //reload();
    }

    public Object onSave(){
        try {
            //RuntimeContext.getAppContext().getBean(getClass()).save();
            //currentTechnologicalMapGroup = entityManager.merge(currentTechnologicalMapGroup);
            currentTechnologicalMapGroup = (TechnologicalMapGroup) DAOService.getInstance().mergeDistributedObject(currentTechnologicalMapGroup,currentTechnologicalMapGroup.getGlobalVersion()+1);
            printMessage("Группа технологических карт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы технологических карт.");
            logger.error("Error saved Technological Map Group",e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentTechnologicalMapGroup.getDeletedState()) {
            printMessage("Группа не может быть удалена.");
            return;
        }
        try{
            TechnologicalMapGroup tmg = entityManager.getReference(TechnologicalMapGroup.class, currentTechnologicalMapGroup.getGlobalId());
            entityManager.remove(tmg);
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            logger.error("Error by delete Technological Map Group.", e);
        }
    }

    @Transactional
    private void reload() throws Exception{
        //currentTechnologicalMapGroup = entityManager.find(TechnologicalMapGroup.class, currentTechnologicalMapGroup.getGlobalId());
        currentTechnologicalMapGroup = entityManager.merge(currentTechnologicalMapGroup);
    }

    @Transactional
    private void save() throws Exception{
        currentTechnologicalMapGroup = (TechnologicalMapGroup) DAOService.getInstance().mergeDistributedObject(currentTechnologicalMapGroup,currentTechnologicalMapGroup.getGlobalVersion()+1);
    }

    public String getPageFilename() {
        return "option/technologicalMap/group/edit";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }
}
