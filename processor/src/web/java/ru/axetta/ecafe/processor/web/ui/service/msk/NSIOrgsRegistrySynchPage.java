/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterOrgsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.01.15
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgsRegistrySynchPage extends BasicWorkspacePage {
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final boolean ALLOW_TO_APPLY_PREVIOS_REVISIONS = false;

    protected String errorMessages;
    protected String infoMessages;

    protected String nameFilter = null;

    protected Long selectedRevision = -1L;
    protected List<Long> revisions;
    protected List<WebItem> items;


    public String getPageFilename() {
        return "service/msk/nsi_orgs_registry_sync_page";
    }

    public String getPageDirectoryRoot() {
        return "/back-office/include";
    }

    public String getPageTitle() {
        return "Синхронизация организаций с Реестрами";
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getResultTitle() {
        return "";
    }

    public long getCreationsCount() {
        return getCountOfOperation(OrgRegistryChange.CREATE_OPERATION);
    }

    public long getDeletionsCount() {
        return getCountOfOperation(OrgRegistryChange.DELETE_OPERATION);
    }

    public long getModificationsCount() {
        return getCountOfOperation(OrgRegistryChange.MODIFY_OPERATION);
    }

    public long getTotalCount() {
        if(items == null) {
            return 0;
        }
        return items.size();
    }

    private int getCountOfOperation(int operation) {
        if (items == null) {
            return 0;
        }
        int count = 0;
        for (WebItem i : items) {
            if (!i.isApplied() && i.getOperation() == operation) {
                count++;
            }
        }
        return count;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public long getSelectedRevision() {
        return selectedRevision;
    }

    public void setSelectedRevision(long selectedRevision) {
        this.selectedRevision = selectedRevision;
    }

    public List<SelectItem> getRevisions() {
        if (revisions == null || revisions.size() < 1) {
            loadRevisions();
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (long date : revisions) {
            if (items.size() < 1) {
                items.add(new SelectItem(date, df.format(new Date(date)) + " - Последняя"));
            } else {
                items.add(new SelectItem(date, df.format(new Date(date))));
            }
        }
        return items;
    }
    //Получаем даты обновлений реестров
    protected void loadRevisions() {
        try {
            revisions = DAOService.getInstance().getOrgRegistryChangeRevisionsList();
        } catch (Exception e) {
            getLogger().error("Failed to load revisions", e);
            revisions = Collections.EMPTY_LIST;
        }
    }



    public List<WebItem> getItems() {
        return items;
    }

    public String getLineStyleClass(WebItem wi) {
        switch (wi.operationType) {
            case OrgRegistryChange.CREATE_OPERATION:
                return "createOrgRow";
            case OrgRegistryChange.MODIFY_OPERATION:
                return "modifyOrgRow";
            case OrgRegistryChange.DELETE_OPERATION:
                return "deleteOrgRow";
            default:
                return "";
        }
    }

    public Boolean isRenderApplied(WebItem item, boolean isTextMessage) {
        //  если это строка
        if(isModifyableRevision()) {
            if (isTextMessage) {
                return item.isApplied();
            } else {
                return true;
            }
        }
        return item.isApplied();
    }

    protected boolean isModifyableRevision() {
        return (selectedRevision < 0 || revisions == null || revisions.size() < 1 || revisions.get(0) == null ||
                !selectedRevision.equals(revisions.get(0))) && !ALLOW_TO_APPLY_PREVIOS_REVISIONS;
    }

    public void onShow() throws Exception {
        if(revisions != null) {
            revisions.clear();
            if(items != null) items.clear();
        }
        loadRevisions();
        if(revisions != null && revisions.size() > 0) {
            selectedRevision = revisions.get(0);
            doUpdate();
        }
    }
    //Применяем фильтр
    public void doUpdate() {
        try {
            List<OrgRegistryChange> dbItems = DAOService.getInstance().getOrgRegistryChanges(nameFilter, selectedRevision);
            putDbItems(dbItems);
        } catch (Exception e) {
            errorMessages = "Не удалось произвести загрузку организаций из Реестров: " + e.getMessage();
            getLogger().error("Failed to load orgs from registry", e);
        }
    }
    //Провести полную сверку
    public void doRefresh() {
        StringBuffer logBuffer = new StringBuffer();
        try {
            RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).syncOrgsWithRegistry(nameFilter, logBuffer);
            loadRevisions();
            if(revisions != null || revisions.size() > 0) {
                selectedRevision = revisions.get(0);
            }
            doUpdate();
        } catch (Exception e) {
            errorMessages = "Не удалось произвести обновление организаций из Реестров: " + e.getMessage();
            getLogger().error("Failed to refresh orgs from registry", e);
        }
    }
    //Применить выбранные
    public void doApply(){
        if(revisions == null || revisions.size() < 1) {
            return;
        }
        if(items == null || items.size() < 1) {
            return;
        }

        for(WebItem i : items) {
            if(i.isApplied()) {
                boolean success = RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).
                                                applyOrgRegistryChange(i.getIdOfOrgRegistryChange());

                i.setApplied(success);

            }
        }
    }

    protected void putDbItems(List<OrgRegistryChange> dbItem) {
        if(dbItem == null || dbItem.size() < 1) {
            return;
        }
        if(items != null) {
            items.clear();
        } else {
            items = new ArrayList<WebItem>();
        }
        for(OrgRegistryChange i : dbItem) {
            WebItem wi = new WebItem(i);
            items.add(wi);
        }
    }

    public class WebItem {
        protected Long idOfOrgRegistryChange;
        protected Long idOfOrg;
        protected Long createDate;
        protected Integer operationType;

        protected Boolean applied = false;

        protected String shortName;
        protected String shortNameFrom;
        protected String officialName;
        protected String officialNameFrom;

        protected String address;
        protected String addressFrom;
        protected String city;
        protected String cityFrom;
        protected String region;
        protected String regionFrom;

        protected Long unom;
        protected Long unomFrom;
        protected Long unad;
        protected Long unadFrom;

        protected String guid;
        protected String guidFrom;
        protected Long additionalId;

        protected String interdistrictCouncil;
        protected String interdistrictCouncilFrom;
        protected String interdistrictCouncilChief;
        protected String interdistrictCouncilChiefFrom;



        public WebItem(OrgRegistryChange registryChange) {
            this.idOfOrgRegistryChange = registryChange.getIdOfOrgRegistryChange();
            this.idOfOrg = registryChange.getIdOfOrg();
            this.createDate = registryChange.getCreateDate();
            this.operationType = registryChange.getOperationType();
            this.applied = registryChange.getApplied();
            this.shortName = registryChange.getShortName();
            this.shortNameFrom = registryChange.getShortNameFrom();
            this.officialName = registryChange.getOfficialName();
            this.officialNameFrom = registryChange.getOfficialNameFrom();
            this.address = registryChange.getAddress();
            this.addressFrom = registryChange.getAddressFrom();
            this.city = registryChange.getCity();
            this.cityFrom = registryChange.getCityFrom();
            this.region = registryChange.getRegion();
            this.regionFrom = registryChange.getRegionFrom();
            this.unom = registryChange.getUnom();
            this.unomFrom = registryChange.getUnomFrom();
            this.unad = registryChange.getUnad();
            this.unadFrom = registryChange.getUnadFrom();
            this.guid = registryChange.getGuid();
            this.guidFrom = registryChange.getGuidFrom();
            this.additionalId = registryChange.getAdditionalId();

            this.interdistrictCouncil = registryChange.getInterdistrictCouncil();
            this.interdistrictCouncilFrom = registryChange.getInterdistrictCouncilFrom();
            this.interdistrictCouncilChief = registryChange.getInterdistrictCouncilChief();
            this.interdistrictCouncilChiefFrom = registryChange.getInterdistrictCouncilChiefFrom();
        }

        public String getOriginName() {
            return shortName + "<br/><br/>" + officialName;
        }

        public String getNewName() {
            if(shortNameFrom == null || StringUtils.isBlank(shortNameFrom) ||
               officialNameFrom == null || StringUtils.isBlank(officialNameFrom)) {
                return "";
            }
            return shortNameFrom + "<br/><br/>" + officialNameFrom;
        }

        public String getOriginAddress() {
            return address + "<br/><br/>" + city + "<br/><br/>" + region;
        }

        public String getNewAddress() {
            if(addressFrom == null || StringUtils.isBlank(addressFrom) ||
               cityFrom == null || StringUtils.isBlank(cityFrom) ||
               regionFrom == null || StringUtils.isBlank(regionFrom)) {
                return "";
            }
            return addressFrom + "<br/><br/>" + cityFrom + "<br/><br/>" + regionFrom;
        }

        public String getOriginSpec() {
            if(unom == null || unad == null) {
                return "";
            }
            return "УНОМ: " + unom + "<br/><br/>УНАД: " + unad;
        }

        public String getNewSpec() {
            if(unomFrom == null || unadFrom == null) {
                return "";
            }
            return "УНОМ: " + unomFrom + "<br/><br/>УНАД: " + unadFrom;
        }

        public Long getIdOfOrgRegistryChange() {
            return idOfOrgRegistryChange;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getCreateDate() {
            return createDate;
        }

        public Boolean getApplied() {
            return applied;
        }

        public String getShortName() {
            return shortName;
        }

        public String getShortNameFrom() {
            return shortNameFrom;
        }

        public String getOfficialName() {
            return officialName;
        }

        public String getOfficialNameFrom() {
            return officialNameFrom;
        }

        public String getAddress() {
            return address;
        }

        public String getAddressFrom() {
            return addressFrom;
        }

        public String getCity() {
            return city;
        }

        public String getCityFrom() {
            return cityFrom;
        }

        public String getRegion() {
            return region;
        }

        public String getRegionFrom() {
            return regionFrom;
        }

        public Long getUnom() {
            return unom;
        }

        public Long getUnomFrom() {
            return unomFrom;
        }

        public Long getUnad() {
            return unad;
        }

        public Long getUnadFrom() {
            return unadFrom;
        }

        public String getGuid() {
            return guid;
        }

        public String getGuidFrom() {
            return guidFrom;
        }

        public Long getAdditionalId() {
            return additionalId;
        }

        public boolean isApplied() {
            return applied;
        }

        public void setApplied(boolean applied) {
            this.applied = applied;
        }

        public String getInterdistrictCouncil() {
            return interdistrictCouncil;
        }

        public String getInterdistrictCouncilFrom() {
            return interdistrictCouncilFrom;
        }

        public String getInterdistrictCouncilChief() {
            return interdistrictCouncilChief;
        }

        public String getInterdistrictCouncilChiefFrom() {
            return interdistrictCouncilChiefFrom;
        }

        public String getOperationType() {
            switch (operationType) {
                case OrgRegistryChange.CREATE_OPERATION:
                    return "Создание";
                case OrgRegistryChange.MODIFY_OPERATION:
                    return "Изменение";
                case OrgRegistryChange.DELETE_OPERATION:
                    return "Удаление";
                default:
                    return "";
            }
        }

        public Integer getOperation() {
            return operationType;
        }
    }
}