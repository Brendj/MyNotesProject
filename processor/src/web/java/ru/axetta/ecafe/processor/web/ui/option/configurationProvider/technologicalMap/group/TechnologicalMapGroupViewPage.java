/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapGroupViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupViewPage.class);
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    private Integer countTechnologicalMaps;
    @Autowired
    private SelectedTechnologicalMapGroupGroupPage selectedTechnologicalMapGroupGroupPage;
    @Autowired
    private TechnologicalMapListPage technologicalMapListPage;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupGroupPage.onShow();
        currentTechnologicalMapGroup = selectedTechnologicalMapGroupGroupPage.getCurrentTechnologicalMapGroup();
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup", TechnologicalMap.class);
        query.setParameter("technologicalMapGroup",currentTechnologicalMapGroup);
        countTechnologicalMaps = query.getResultList().size();
    }

    public Object showTechnologicalMaps() throws Exception{
        technologicalMapListPage.setSelectedTechnologicalMapGroup(currentTechnologicalMapGroup);
         //Показать и удаленный
        technologicalMapListPage.setDeletedStatusSelected(true);
        technologicalMapListPage.reload();
        technologicalMapListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/group/view";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }

    public Integer getCountTechnologicalMaps() {
        return countTechnologicalMaps;
    }
}
