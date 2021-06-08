package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.SettingsIds;

import javax.persistence.*;

@Entity
@Table(name = "cf_ecafesettings")
public class ECafeSettings {
    @Id
    @Column(name = "IdOfECafeSetting")
    private Long globalId;

    @Column(name = "SettingValue")
    private String settingValue;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Identificator")
    private SettingsIds settingsId;

    @Column(name = "DeletedState")
    private Boolean deletedState;

    @Column(name = "OrgOwner")
    private Long orgOwner;

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public SettingsIds getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(SettingsIds settingsId) {
        this.settingsId = settingsId;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }
}
