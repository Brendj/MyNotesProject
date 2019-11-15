/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orgparameters;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSetting;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class OrgSyncSettingReportItem implements Comparable<OrgSyncSettingReportItem> {
    public static final Integer NOT_CHANGED = 0;
    public static final Integer CHANGED = 1;
    private String orgName;
    private Long idOfOrg;
    private String shortAddress;
    private SyncInfo fullSync = new SyncInfo(ContentType.FULL_SYNC);
    private SyncInfo accIncSync = new SyncInfo(ContentType.BALANCES_AND_ENTEREVENTS);
    private SyncInfo orgSettingSync = new SyncInfo(ContentType.ORGSETTINGS);
    private SyncInfo clientDataSync = new SyncInfo(ContentType.CLIENTS_DATA);
    private SyncInfo menuSync = new SyncInfo(ContentType.MENU);
    private SyncInfo photoSync = new SyncInfo(ContentType.PHOTOS);
    private SyncInfo helpRequestsSync = new SyncInfo(ContentType.SUPPORT_SERVICE);
    private SyncInfo libSync = new SyncInfo(ContentType.LIBRARY);
    private Boolean isChange = false;
    private Org org;
    private Integer numOfPhotoEnabled;

    public OrgSyncSettingReportItem(Org org, List<SyncSetting> settings) {
        this.orgName = org.getShortName();
        this.idOfOrg = org.getIdOfOrg();
        this.shortAddress = org.getShortAddress();
        this.org = org;

        for (SyncSetting setting : settings) {
            switch (setting.getContentType()) {
                case FULL_SYNC:
                    fullSync = new SyncInfo(setting);
                    break;
                case BALANCES_AND_ENTEREVENTS:
                    accIncSync = new SyncInfo(setting);
                    break;
                case ORGSETTINGS:
                    orgSettingSync = new SyncInfo(setting);
                    break;
                case CLIENTS_DATA:
                    clientDataSync = new SyncInfo(setting);
                    break;
                case MENU:
                    menuSync = new SyncInfo(setting);
                    break;
                case PHOTOS:
                    photoSync = new SyncInfo(setting);
                    break;
                case SUPPORT_SERVICE:
                    helpRequestsSync = new SyncInfo(setting);
                    break;
                case LIBRARY:
                    libSync = new SyncInfo(setting);
            }
        }
        setOrgForAllSetting(org);
    }

    private void setOrgForAllSetting(Org org) {
        getFullSync().getSetting().setOrg(org);
        getOrgSettingSync().getSetting().setOrg(org);
        getClientDataSync().getSetting().setOrg(org);
        getMenuSync().getSetting().setOrg(org);
        getPhotoSync().getSetting().setOrg(org);
        getLibSync().getSetting().setOrg(org);
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public SyncInfo getFullSync() {
        return fullSync;
    }

    public void setFullSync(SyncInfo fullSync) {
        this.fullSync = fullSync;
    }

    public SyncInfo getAccIncSync() {
        return accIncSync;
    }

    public void setAccIncSync(SyncInfo accIncSync) {
        this.accIncSync = accIncSync;
    }

    public SyncInfo getOrgSettingSync() {
        return orgSettingSync;
    }

    public void setOrgSettingSync(SyncInfo orgSettingSync) {
        this.orgSettingSync = orgSettingSync;
    }

    public SyncInfo getMenuSync() {
        return menuSync;
    }

    public void setMenuSync(SyncInfo menuSync) {
        this.menuSync = menuSync;
    }

    public SyncInfo getPhotoSync() {
        return photoSync;
    }

    public void setPhotoSync(SyncInfo photoSync) {
        this.photoSync = photoSync;
    }

    public SyncInfo getHelpRequestsSync() {
        return helpRequestsSync;
    }

    public void setHelpRequestsSync(SyncInfo helpRequestsSync) {
        this.helpRequestsSync = helpRequestsSync;
    }

    public SyncInfo getLibSync() {
        return libSync;
    }

    public void setLibSync(SyncInfo libSync) {
        this.libSync = libSync;
    }

    @Override
    public int compareTo(OrgSyncSettingReportItem o) {
        return this.orgName.compareTo(o.orgName);
    }

    public SyncInfo getClientDataSync() {
        return clientDataSync;
    }

    public void setClientDataSync(SyncInfo clientDataSync) {
        this.clientDataSync = clientDataSync;
    }

    public void buildSyncInfo(Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday,
            Boolean saturday, Boolean sunday, Integer everySecond, String buildTime, Integer modalSelectedContentType,
            Integer limitStartHour, Integer limitEndHour) {
        ContentType type = ContentType.getContentTypeByCode(modalSelectedContentType);
        switch (type) {
            case FULL_SYNC:
                fullSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                        buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case BALANCES_AND_ENTEREVENTS:
                accIncSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                        buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case ORGSETTINGS:
                orgSettingSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                        everySecond, buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case CLIENTS_DATA:
                clientDataSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                        everySecond, buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case MENU:
                menuSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                        buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case PHOTOS:
                photoSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                        buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case SUPPORT_SERVICE:
                helpRequestsSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                        everySecond, buildTime, limitStartHour, limitEndHour, this.org);
                break;
            case LIBRARY:
                libSync = new SyncInfo(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                        buildTime, limitStartHour, limitEndHour, this.org);
        }
        isChange = true;
    }

    public void rebuildSyncInfo(Integer modalSelectedContentType, SyncSetting setting) {
        ContentType type = ContentType.getContentTypeByCode(modalSelectedContentType);
        switch (type) {
            case FULL_SYNC:
                fullSync = new SyncInfo(setting);
                fullSync.setState(CHANGED);
                break;
            case BALANCES_AND_ENTEREVENTS:
                accIncSync = new SyncInfo(setting);
                accIncSync.setState(CHANGED);
                break;
            case ORGSETTINGS:
                orgSettingSync = new SyncInfo(setting);
                orgSettingSync.setState(CHANGED);
                break;
            case CLIENTS_DATA:
                clientDataSync = new SyncInfo(setting);
                clientDataSync.setState(CHANGED);
                break;
            case MENU:
                menuSync = new SyncInfo(setting);
                menuSync.setState(CHANGED);
                break;
            case PHOTOS:
                photoSync = new SyncInfo(setting);
                photoSync.setState(CHANGED);
                break;
            case SUPPORT_SERVICE:
                helpRequestsSync = new SyncInfo(setting);
                helpRequestsSync.setState(CHANGED);
                break;
            case LIBRARY:
                libSync = new SyncInfo(setting);
                libSync.setState(CHANGED);
        }
        isChange = true;
    }

    public Boolean getIsChange() {
        return isChange;
    }

    public void setIsChange(Boolean change) {
        this.isChange = change;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Integer getNumOfPhotoEnabled() {
        return numOfPhotoEnabled;
    }

    public void setNumOfPhotoEnabled(Integer numOfPhotoEnabled) {
        this.numOfPhotoEnabled = numOfPhotoEnabled;
    }

    public class SyncInfo {
        private String fullInf = "";
        private SyncSetting setting;
        private Integer state = NOT_CHANGED;

        SyncInfo(ContentType type){
            setting = new SyncSetting();
            setting.setContentType(type);
        }

        SyncInfo(SyncSetting setting) {
            String times = setting.getConcreteTime();
            String days;

            if (setting.getMonday() && setting.getTuesday() && setting.getWednesday() && setting.getThursday()
                    && setting.getFriday() && setting.getSaturday() && setting.getSunday()) {
                days = "Все дни";
            } else {
                List<String> daysList = new LinkedList<>();
                if (setting.getMonday()) {
                    daysList.add("ПН");
                }
                if (setting.getTuesday()) {
                    daysList.add("ВТ");
                }
                if (setting.getWednesday()) {
                    daysList.add("СР");
                }
                if (setting.getThursday()) {
                    daysList.add("ЧТ");
                }
                if (setting.getFriday()) {
                    daysList.add("ПТ");
                }
                if (setting.getSaturday()) {
                    daysList.add("СБ");
                }
                if (setting.getSunday()) {
                    daysList.add("ВС");
                }
                days = StringUtils.join(daysList, ";");
            }
            buildFullInfo(setting.getEverySecond(), setting.getLimitStartHour(), setting.getLimitEndHour(), days, times);
            this.setting = setting;
        }

        SyncInfo(Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday,
                Boolean sunday, Integer everySecond, String buildTime, Integer limitStartHour, Integer limitEndHour, Org org ) {
            String days;
            String times;
            if (monday && tuesday && wednesday && thursday && friday && saturday && sunday) {
                days = "Все дни";
            } else {
                List<String> daysList = new LinkedList<>();
                if (monday) {
                    daysList.add("ПН");
                }
                if (tuesday) {
                    daysList.add("ВТ");
                }
                if (wednesday) {
                    daysList.add("СР");
                }
                if (thursday) {
                    daysList.add("ЧТ");
                }
                if (friday) {
                    daysList.add("ПТ");
                }
                if (saturday) {
                    daysList.add("СБ");
                }
                if (sunday) {
                    daysList.add("ВС");
                }
                days = StringUtils.join(daysList, ";");
            }
            times = buildTime;
            buildFullInfo(everySecond, limitStartHour, limitEndHour, days, times);
            setting = new SyncSetting(monday, tuesday, wednesday, thursday, friday, saturday, sunday, everySecond,
                    buildTime, limitStartHour, limitEndHour);
            setting.setOrg(org);
            state = CHANGED;
        }

        private void buildFullInfo(Integer everySecond, Integer limitStartHour, Integer limitEndHour, String days,
                String times) {
            StringBuilder sb = new StringBuilder();
            if(StringUtils.isNotBlank(times)) {
                sb.append(times);
                sb.append("\n");
            } else if(everySecond != null) {
                int min = everySecond / 60;
                int sec = everySecond % 60;
                sb.append("Каждые ");
                sb.append(min);
                sb.append(" мин. ");
                if(sec != 0) {
                    sb.append(sec);
                    sb.append(" сек.");
                }
                if(limitStartHour != null && !(limitStartHour.equals(limitEndHour) && limitStartHour.equals(0))){
                    sb.append(" с ");
                    sb.append(limitStartHour);
                    sb.append(":00 по ");
                    sb.append(limitEndHour);
                    sb.append(":00");
                }
                sb.append("\n");
            } else {
                sb.append("\n");
            }
            sb.append(days);
            fullInf = sb.toString();
        }

        public String getFullInf() {
            return fullInf;
        }

        public void setFullInf(String fullInf) {
            this.fullInf = fullInf;
        }

        public SyncSetting getSetting() {
            return setting;
        }

        public void setSetting(SyncSetting setting) {
            this.setting = setting;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        @Override
        public String toString(){
            return fullInf;
        }
    }
}
