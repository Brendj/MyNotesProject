/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TechnologicalMapGroupEditPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    @Autowired
    private SelectedTechnologicalMapGroupGroupPage selectedTechnologicalMapGroupGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupGroupPage.onShow();
        currentTechnologicalMapGroup = selectedTechnologicalMapGroupGroupPage.getCurrentTechnologicalMapGroup();
        currentTechnologicalMapGroup = entityManager.merge(currentTechnologicalMapGroup);
    }

    public Object onSave(){
        try {
            if(currentTechnologicalMapGroup.getNameOfGroup() == null || currentTechnologicalMapGroup.getNameOfGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            currentTechnologicalMapGroup = (TechnologicalMapGroup) daoService.mergeDistributedObject(currentTechnologicalMapGroup,currentTechnologicalMapGroup.getGlobalVersion()+1);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы.");
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

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/group/edit";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }
}
