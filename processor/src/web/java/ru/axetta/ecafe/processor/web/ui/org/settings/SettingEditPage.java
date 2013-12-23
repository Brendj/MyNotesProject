/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.daoservices.org.SettingService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.AbstractParserBySettingValue;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SettingEditPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private ECafeSettings setting;
    private OrgItem orgItem;
    private Integer settingsIds;
    private AbstractParserBySettingValue parserBySettingValue;

    private List<String> allPrinters;

    @Autowired
    private SelectedSettingsGroupPage selectedSettingsGroupPage;
    @Autowired
    private SettingService settingRepository;

    @Override
    public void onShow() throws Exception {
        init();
    }

    public Object reload(){
        try {
            init();
            printMessage("Настройки успешно восстановлены");
        } catch (Exception e){
            getLogger().error("Error reload info by settings: ",e);
            printError("Ошибка при загрузке данных.");
        }
        return null;
    }

    private void init() throws Exception {
        setting = selectedSettingsGroupPage.getSelectSettings();
        orgItem = selectedSettingsGroupPage.getCurrentOrg();
        settingsIds = setting.getSettingsId().getId();
        parserBySettingValue = setting.getSplitSettingValue();
        if(settingsIds.equals(0) || settingsIds.equals(1) || settingsIds.equals(2)){
            allPrinters = settingRepository.findAllPrinterNames();
        } else {
            allPrinters = new ArrayList<String>(0);
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.orgItem = new OrgItem(org);
        }
    }

    public Object save(){
        String settingValue = parserBySettingValue.build();
        setting.setSettingValue(settingValue);
        setting.setOrgOwner(orgItem.getIdOfOrg());
        try {
            settingRepository.save(setting);
            printMessage("Настройки успешно обновлены");
        } catch (Exception e) {
            getLogger().error("Error update setting: ",e);
            printError("Ошибка при сохранении данных.");
        }

        return null;
    }

    @Override
    public String getPageFilename() {
        return "org/settings/edit";
    }

    public List<String> getAllPrinters() {
        return allPrinters;
    }

    public void setAllPrinters(List<String> allPrinters) {
        this.allPrinters = allPrinters;
    }

    public Integer getSettingsIds() {
        return settingsIds;
    }

    public void setSettingsIds(Integer settingsIds) {
        this.settingsIds = settingsIds;
    }

    public ECafeSettings getSetting() {
        return setting;
    }

    public void setSetting(ECafeSettings setting) {
        this.setting = setting;
    }

    public OrgItem getOrgItem() {
        return orgItem;
    }

    public void setOrgItem(OrgItem orgItem) {
        this.orgItem = orgItem;
    }

    public AbstractParserBySettingValue getParserBySettingValue() {
        return parserBySettingValue;
    }

}
