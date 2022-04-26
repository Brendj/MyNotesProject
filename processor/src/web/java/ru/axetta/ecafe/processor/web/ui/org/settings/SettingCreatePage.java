/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.daoservices.org.SettingService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.AbstractParserBySettingValue;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ValueChangeEvent;
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
public class SettingCreatePage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private ECafeSettings setting = new ECafeSettings();
    private OrgItem orgItem;
    private Integer settingsIds;
    private String settingsId;
    private AbstractParserBySettingValue parserBySettingValue;
    private SettingsIds[]  settingsIdses = getSettingsIdsWithoutFiveElm();
    private List<String> allPrinters;

    @Autowired
    private SettingService settingRepository;

    public void valueChangeListener(ValueChangeEvent event){
        try {
            if(StringUtils.isNotEmpty(String.valueOf(event.getNewValue()))){
                settingsIds = SettingsIds.valueOf((String)event.getNewValue()).getId();
                init();
            }
        } catch (Exception e) {
            printError("Ошибка: "+e.getMessage());
            getLogger().error("Error create setting: ",e);
        }
    }

    @Override
    public void onShow() throws Exception {
        init();
    }

    private SettingsIds[] getSettingsIdsWithoutFiveElm(){
        SettingsIds[] buffer = SettingsIds.values();
        SettingsIds[] settingsIds = new SettingsIds[buffer.length-1];
        int j = 0;
        for(int i = 0; i != buffer.length; i++){
            if(i == 5) continue;
            settingsIds[j] = buffer[i];
            j++;
        }
        return settingsIds;
    }


    private void init() throws Exception {
        if(settingsIds!=null){
            /* Задать значения по умолчанию */
            switch (settingsIds){
                case 0: setting.setSettingValue("Microsoft XPS DocumentItem Writer;42;1;19;3;10;10;Спасибо;"); break;
                case 1: setting.setSettingValue("Microsoft XPS DocumentItem Writer;42;1;22;6;12;Спасибо;"); break;
                case 2: setting.setSettingValue("Microsoft XPS DocumentItem Writer;42;1;16;12;12;Спасибо;"); break;
                case 3: setting.setSettingValue("0;0:00;100;");break;
                case 4: setting.setSettingValue("5;2;0;2;");break;
                case 5: setting.setSettingValue("Резерв;2;");break;
                case 6: setting.setSettingValue("1;1;");break;
                case 7: setting.setSettingValue("0;0:00;"); break;
            }
            setting.setSettingsId(SettingsIds.fromInteger(settingsIds));
            parserBySettingValue = setting.getSplitSettingValue();
            if(settingsIds.equals(0) || settingsIds.equals(1) || settingsIds.equals(2)){
                allPrinters = settingRepository.findAllPrinterNames();
            } else {
                allPrinters = new ArrayList<String>(0);
            }
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.orgItem = new OrgItem(org);
        }
    }

    public Object create(){
        if(orgItem==null){
            printError("Не выбрана организация");
            return null;
        }
        if(settingsIds==null){
            printError("Не указан тип устройства");
            return null;
        }
        String settingValue = parserBySettingValue.build();
        setting.setDeletedState(false);
        setting.setSettingValue(settingValue);
        setting.setOrgOwner(orgItem.getIdOfOrg());
        try {
            if(setting.getGlobalId()==null){
                settingRepository.create(setting);
                printMessage("Настройки успешно зарегистрированы");
            } else{
                printWarn("Настройки уже зарегистрирована");
            }
        } catch (Exception e) {
            getLogger().error("Error create setting: ",e);
            printError("Ошибка при регистрации данных");
        }
        return null;
    }

    @Override
    public String getPageFilename() {
        return "org/settings/create";
    }

    public SettingsIds[] getSettingsIdses() {
        return settingsIdses;
    }

    public void setSettingsIdses(SettingsIds[] settingsIdses) {
        this.settingsIdses = settingsIdses;
    }

    public List<String> getAllPrinters() {
        return allPrinters;
    }

    public String getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(String settingsId) {
        this.settingsId = settingsId;
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
