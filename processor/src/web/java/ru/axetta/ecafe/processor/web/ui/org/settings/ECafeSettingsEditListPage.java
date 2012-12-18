/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
@Component("cafeSettingsEditListPage")
@Scope("session")
public class ECafeSettingsEditListPage extends BasicWorkspacePage {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    private Long idOfOrg;
    private DataModel cafeSettingsList;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Override
    public String getPageFilename() {
        return "org/ecafesettings";
    }

    @Transactional
    protected void reload(){
        TypedQuery<ECafeSettings> query = entityManager.createQuery("from ECafeSettings where orgOwner=:idOfOrg order by identificator, deletedState", ECafeSettings.class);
        query.setParameter("idOfOrg",idOfOrg);
        List<ECafeSettings> list = query.getResultList();
        this.cafeSettingsList = new ListDataModel(list);
    }

    public Object updateList(){
        reload();
        return null;
    }

    public Object save(){
        ECafeSettings currentSetting = getEntityFromRequestParam();
        ECafeSettings cafeSettings = daoService.findDistributedObjectByRefGUID(ECafeSettings.class, currentSetting.getGuid());
        cafeSettings.fill(currentSetting);
        cafeSettings.setDeletedState(currentSetting.getDeletedState());
        cafeSettings.setGlobalVersion(daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName()));
        daoService.mergeDistributedObject(cafeSettings, cafeSettings.getGlobalVersion()+1);
        return null;
    }

    private ECafeSettings getEntityFromRequestParam() {
        if (cafeSettingsList == null) return null;
        return (ECafeSettings) cafeSettingsList.getRowData();
    }


    public DataModel getCafeSettingsList() {
        return cafeSettingsList;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
