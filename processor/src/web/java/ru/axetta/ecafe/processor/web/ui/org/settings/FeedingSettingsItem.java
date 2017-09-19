/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;

import java.util.Date;

/**
 * Created by i.semenov on 15.09.2017.
 */
public class FeedingSettingsItem {
    private long idOfSetting;
    private String settingName;
    private Long limit;
    private Date lastUpdate;
    private String userName;

    public FeedingSettingsItem(FeedingSetting setting) {
        idOfSetting = setting.getIdOfSetting();
        settingName = setting.getSettingName();
        limit = setting.getLimit();
        lastUpdate = setting.getLastUpdate();
        userName = setting.getUser().getUserName();
    }

    public long getIdOfSetting() {
        return idOfSetting;
    }

    public void setIdOfSetting(long idOfSetting) {
        this.idOfSetting = idOfSetting;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
