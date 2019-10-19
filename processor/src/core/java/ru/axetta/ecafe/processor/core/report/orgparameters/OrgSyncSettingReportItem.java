/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orgparameters;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ConcreteTime;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettings;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class OrgSyncSettingReportItem implements Comparable<OrgSyncSettingReportItem>{
    private String orgName;
    private Long idOfOrg;
    private String shortAddress;
    private SyncInfo fullSync;
    private SyncInfo accIncSync;
    private SyncInfo orgSettingSync;
    private SyncInfo clientDataSync;
    private SyncInfo menuSync;
    private SyncInfo photoSync;
    private SyncInfo helpRequestsSync;
    private SyncInfo libSync;
    private List<SyncSettings> allSyncSettings = new LinkedList<>();

    private static final Comparator<String> comparable = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            String[] oa1 = o1.split(":");
            String[] oa2 = o2.split(":");
            int res = oa1[0].compareTo(oa2[0]);

            return res == 0 ? oa1[1].compareTo(oa2[1]) : res;
        }
    };

    public OrgSyncSettingReportItem(Org org, List<SyncSettings> settings) {
        this.orgName = org.getShortName();
        this.idOfOrg = org.getIdOfOrg();
        this.shortAddress = org.getShortAddress();

        for(SyncSettings setting : settings){
            switch (setting.getContentType()){
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
        this.allSyncSettings = settings;
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

    public List<SyncSettings> getAllSyncSettings() {
        return allSyncSettings;
    }

    public void setAllSyncSettings(List<SyncSettings> allSyncSettings) {
        this.allSyncSettings = allSyncSettings;
    }

    public class SyncInfo {
        private String times;
        private String days = "";
        private String fullInf;

        SyncInfo(SyncSettings setting) {
            times = buildStringTimes(setting.getConcreteTime());

            if (setting.getMonday() && setting.getTuesday() && setting.getWednesday() && setting.getThursday()
                    && setting.getFriday() && setting.getSaturday() && setting.getSunday()) {
                days = "Все дни";
            } else {
                List<String> daysList = new LinkedList<>();
                if(setting.getMonday()){
                    daysList.add("ПН");
                }
                if(setting.getTuesday()){
                    daysList.add("ВТ");
                }
                if(setting.getWednesday()){
                    daysList.add("СР");
                }
                if(setting.getThursday()){
                    daysList.add("ЧТ");
                }
                if(setting.getFriday()){
                    daysList.add("ПТ");
                }
                if(setting.getSaturday()){
                    daysList.add("СБ");
                }
                if(setting.getSunday()){
                    daysList.add("ВС");
                }
                days = StringUtils.join(daysList, ";");
            }
            fullInf = times + "\n" + days;
        }

        private String buildStringTimes(Set<ConcreteTime> concreteTime) {
            List<String> stringTimes = new LinkedList<>();
            for (ConcreteTime time : concreteTime) {
                stringTimes.add(time.getConcreteTime());
            }
            Collections.sort(stringTimes, comparable);
            return StringUtils.join(stringTimes, "; ");
        }

        public String getTimes() {
            return times;
        }

        public void setTimes(String times) {
            this.times = times;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getFullInf() {
            return fullInf;
        }

        public void setFullInf(String fullInf) {
            this.fullInf = fullInf;
        }
    }
}
