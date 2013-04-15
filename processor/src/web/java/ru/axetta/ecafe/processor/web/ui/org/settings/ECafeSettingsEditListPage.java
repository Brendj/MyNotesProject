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
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private List<ECafeSettings> list;

    public Boolean view(){
        return true;
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Override
    public String getPageFilename() {
        return "org/ecafesettings";
    }

    private void reload(){
        //list = daoService.geteCafeSettingses(idOfOrg);
        this.cafeSettingsList = new ListDataModel(list);
    }

    public Object updateList(){
        reload();
        return null;
    }

    public Object addSetting() {
        ECafeSettings settings = new ECafeSettings();
        settings.setOrgOwner(idOfOrg);
        settings.setGuid(UUID.randomUUID().toString());
        settings.setDeletedState(true);
        list.add(settings);
        return null;
    }

    public Object remove(){
        ECafeSettings currentSetting = getEntityFromRequestParam();
        if(!currentSetting.getDeletedState()){
            printError("Настройки нельзя удалить.");
            return null;
        }
        daoService.removeSetting(currentSetting);
        printMessage("Натройка удалена успешно");
        return null;
    }

    public Object save(){
        ECafeSettings currentSetting = getEntityFromRequestParam();
        if(currentSetting.getSettingValue()==null || currentSetting.getSettingValue().isEmpty()){
            printError("Введите настройки Параметры принтера (формат чека) в параметр с GUID:"+currentSetting.getGuid());
        }
        if(checkFormat(currentSetting.getSettingValue())){

        }
        ECafeSettings cafeSettings = daoService.findDistributedObjectByRefGUID(ECafeSettings.class, currentSetting.getGuid());
        if(cafeSettings == null){
            cafeSettings = currentSetting;
            cafeSettings.setGlobalVersion(daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName()));
            cafeSettings.setCreatedDate(new Date());
            try {
                daoService.persistEntity(cafeSettings);
            } catch (Exception e) {
                getLogger().error("Error persist ECafeSettings: ",e);
                printError("Ошибка при сохранении Настройки."+e.getMessage());
            }
        }
        cafeSettings.fill(currentSetting);
        cafeSettings.setDeletedState(currentSetting.getDeletedState());
        cafeSettings.setGlobalVersion(daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName()));
        daoService.mergeDistributedObject(cafeSettings, cafeSettings.getGlobalVersion()+1);
        return null;
    }

    private boolean checkFormat(String settingValue) {

        return false;
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
