/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChangeItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterOrgsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.service.OrgModifyChangeItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    protected Boolean hideApplied = true;
    private WebItem orgForEdit;
    protected Integer selectedRegion = 0;

    private final List<OrgModifyChangeItem> orgModifyChangeItems = new ArrayList<OrgModifyChangeItem>();
    private Boolean isNeedAddElements = true;

    public NSIOrgsRegistrySynchPage() {
        super();
        orgModifyChangeItems.clear();
        if (!RuntimeContext.getInstance().isNSI3()) {
            orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_GUID, "", ""));
        }
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_NSI_ID, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_EKIS_ID, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_EGISSO_ID, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_UNIQUE_ADDRESS_ID, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_INN, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_UNOM, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_UNAD, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_ADDRESS, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_SHORT_ADDRESS, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_MUNICIPAL_DISTRICT, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_OFFICIAL_NAME, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_SHORT_NAME, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_DIRECTOR, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_FOUNDER, "", ""));
        orgModifyChangeItems.add(new OrgModifyChangeItem(ImportRegisterOrgsService.VALUE_SUBORDINATION, "", ""));
    }

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
        //return items.size();
        int count = 0;
        for (WebItem i : items) {
            if (i.getOperation() == OrgRegistryChange.DELETE_OPERATION) {
                count++;
                continue;
            }
            for (WebItem j : i.orgs) {
                if (j.getOperation() != OrgRegistryChange.SIMILAR) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getCountOfOperation(int operation) {
        if (items == null) {
            return 0;
        }
        int count = 0;
        if (operation == OrgRegistryChange.DELETE_OPERATION) {
            for (WebItem i : items) {
                if (!i.isApplied() && i.getOperation() == operation) {
                    count++;
                }
            }
        } else {
            for (WebItem i : items) {
                for (WebItem j : i.orgs) {
                    if (!j.isApplied() && j.getOperation() == operation) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public String getRegionFilter() {
        return getOptionFilter(Option.OPTION_REGIONS_FROM_NSI, selectedRegion, ",");
    }

    private String getOptionFilter(int option, int selected, String delim) {
        String str = RuntimeContext.getInstance().getOptionValueString(option);
        if (selected == 0) {
            return "";
        } else {
            String[] regions = str.split(delim);
            return regions[selected-1];
        }
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

    public List<SelectItem> getRegions() {
        return getSelectItemsList(Option.OPTION_REGIONS_FROM_NSI, ",");
    }

    public List<SelectItem> getSelectItemsList(int option, String delim) {
        String options = RuntimeContext.getInstance().getOptionValueString(option);
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(0, new SelectItem(0, "Все"));
        try {
            String[] regions = options.split(delim);
            int number = 1;
            for (String st : regions) {
                items.add(number, new SelectItem(number, st.trim()));
                number++;
            }
        } catch (Exception e) {
            getLogger().error("Error parse option string.", e);
            printError("Не удается распарсить строку с перечислением настроек");
        }
        return items;
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

    public Boolean isDeleteOperation(Integer operationType) {
        return (operationType == OrgRegistryChange.DELETE_OPERATION);
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
            //doUpdate();
        }
    }
    //Применяем фильтр
    public void doUpdate() {
        try {
            List<OrgRegistryChange> dbItems = DAOService.getInstance().getOrgRegistryChanges(nameFilter, getRegionFilter(),
                    selectedRevision, 2, hideApplied);
            if(isNeedAddElements && nameFilter != null && nameFilter.length() > 0){
                dbItems = DAOService.getInstance()
                        .getOrgRegistryChangesThroughOrgRegistryChangeItems(nameFilter, selectedRevision, getRegionFilter(),
                                2, hideApplied, dbItems);
            }
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
            RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).syncOrgsWithRegistry(nameFilter, getRegionFilter(), logBuffer);
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

        try {
            for(WebItem i : items) {
                if(i.isSelected()) {
                    List<Long> buildingsList = new LinkedList<Long>();

                    for (WebItem webItem : i.getOrgs()) {
                        if (webItem.isSelected() && webItem.getOperation() != OrgRegistryChange.SIMILAR && webItem.getOperation() != OrgRegistryChange.CREATE_OPERATION){
                            buildingsList.add(webItem.getIdOfOrgRegistryChange());
                        }
                    }
                    boolean success = RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).
                            applyOrgRegistryChange(i.getIdOfOrgRegistryChange(), buildingsList, null);

                    i.setApplied(success);
                    i.setSelected(false);
                    if (success) {
                        for (WebItem webItem : i.getOrgs()) {
                            if (webItem.isSelected()){
                                webItem.setApplied(true);
                                webItem.setSelected(false);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            errorMessages = String.format("Не удается применить операцию к выбранным организациям. Текст ошибки: %s", e.getMessage());
            getLogger().error("Failed to apply changes from registry. Error " + e.getMessage(), e);
        }
    }

    public void doApplyOneOrg() {
        try {
            List<Long> buildingsList = new LinkedList<Long>();
            Long itemId = orgForEdit.getIdOfOrgRegistryChange();
            buildingsList.add(itemId);
            Long mainRegistryId = DAOService.getInstance().getMainRegistryByItemId(itemId);
            if (mainRegistryId == null) {
                errorMessages = "Не удается применить операцию к выбранной организации.";
                return;
            }
            Set<String> flags = new HashSet<String>();
            for (OrgModifyChangeItem item : orgModifyChangeItems) {
                if (item.getSelected()) {
                    flags.add(item.getValueName());
                }
            }
            RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).
                    applyOrgRegistryChange(mainRegistryId, buildingsList, flags);
            doUpdateOne(mainRegistryId);
        } catch (Exception e) {
            errorMessages = String.format("Не удается применить операцию к выбранным организациям. Текст ошибки: %s", e.getMessage());
            getLogger().error("Failed to apply changes from registry. Error " + e.getMessage(), e);
        }
    }

    private void doUpdateOne(Long idOfRegistryChange) {
        try {
            OrgRegistryChange dbItem = DAOService.getInstance().getOrgRegistryChange(idOfRegistryChange);
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getIdOfOrgRegistryChange().equals(idOfRegistryChange)) {
                    items.set(i, new WebItem(dbItem));
                    break;
                }
            }
        } catch (Exception e) {
            errorMessages = "Не удалось произвести загрузку организаций из Реестров: " + e.getMessage();
            getLogger().error("Failed to load orgs from registry", e);
        }
    }

    public void doCheckAllSverkaPanel() {
        for (OrgModifyChangeItem item : orgModifyChangeItems) {
            item.setSelected(true);
        }
    }

    public void doUncheckAllSverkaPanel() {
        for (OrgModifyChangeItem item : orgModifyChangeItems) {
            item.setSelected(false);
        }
    }

    public void doCheckAll() {
        try {
            for(WebItem i : items) {
                i.setSelected(true);
            }
        }
        catch (Exception e) {
            errorMessages = String.format("Не удается установить выбор всех записей. Текст ошибки: %s", e.getMessage());
            getLogger().error("Failed to check all records. Error " + e.getMessage(), e);
        }
    }

    public void doUncheckAll() {
        try {
            for(WebItem i : items) {
                i.setSelected(false);
            }
        }
        catch (Exception e) {
            errorMessages = String.format("Не удается очистить выбор всех записей. Текст ошибки: %s", e.getMessage());
            getLogger().error("Failed to uncheck all records. Error " + e.getMessage(), e);
        }
    }

    public boolean isRevisionLast() {
        if (revisions != null && revisions.size() > 0) {
            return (selectedRevision.equals(revisions.get(0)));
        } else {
            return false;
        }
    }

    protected void putDbItems(List<OrgRegistryChange> dbItem) {
        if (items == null) {
            items = new ArrayList<WebItem>();
        }
        items.clear();
        if(dbItem == null || dbItem.size() < 1) {
            return;
        }
        for(OrgRegistryChange i : dbItem) {
            WebItem wi = new WebItem(i);
            if (!getHideApplied() || (getHideApplied() && !wi.getApplied())) {
                items.add(wi);
            }
        }

    }

    public Boolean getHideApplied() {
        return hideApplied;
    }

    public void setHideApplied(Boolean hideApplied) {
        this.hideApplied = hideApplied;
    }

    public WebItem getOrgForEdit() {
        return orgForEdit;
    }

    public boolean nsi3() {
        return RuntimeContext.getInstance().isNSI3();
    }

    public void setOrgForEdit(WebItem orgForEdit) {
        this.orgForEdit = orgForEdit;
    }

    public List<OrgModifyChangeItem> getOrgModifyChangeItems() {
        if (orgForEdit == null) {
            return orgModifyChangeItems;
        }
        for (OrgModifyChangeItem item : orgModifyChangeItems) {
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_GUID) && !RuntimeContext.getInstance().isNSI3()) {
                item.setOldValue(orgForEdit.getGuidFrom());
                item.setNewValue(orgForEdit.getGuidReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_NSI_ID)) {
                item.setOldValue(orgForEdit.getGlobalIdFromNullSafe());
                item.setNewValue(orgForEdit.getGlobalIdReestrNullSafe());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_EKIS_ID)) {
                item.setOldValue(orgForEdit.getEkisIdFromNullSafe());
                item.setNewValue(orgForEdit.getEkisIdReestrNullSafe());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_EGISSO_ID)) {
                item.setOldValue(orgForEdit.getEgissoIdFrom());
                item.setNewValue(orgForEdit.getEgissoIdReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_UNIQUE_ADDRESS_ID)) {
                item.setOldValue(orgForEdit.getUniqueAddressIdFromNullSafe());
                item.setNewValue(orgForEdit.getUniqueAddressIdReestrNullSafe());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_INN)) {
                item.setOldValue(orgForEdit.getInnFrom());
                item.setNewValue(orgForEdit.getInnReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_UNOM)) {
                item.setOldValue(orgForEdit.getUnomFromNullSafe());
                item.setNewValue(orgForEdit.getUnomReestrNullSafe());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_UNAD)) {
                item.setOldValue(orgForEdit.getUnadFromNullSafe());
                item.setNewValue(orgForEdit.getUnadReestrNullSafe());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_ADDRESS)) {
                item.setOldValue(orgForEdit.getAddressFrom());
                item.setNewValue(orgForEdit.getAddressReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_SHORT_ADDRESS)) {
                item.setOldValue(orgForEdit.getShortAddressFrom());
                item.setNewValue(orgForEdit.getShortAddressReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_MUNICIPAL_DISTRICT)) {
                item.setOldValue(orgForEdit.getMunicipalDistrictFrom());
                item.setNewValue(orgForEdit.getMunicipalDistrictReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_OFFICIAL_NAME)) {
                item.setOldValue(orgForEdit.getOfficialNameFrom());
                item.setNewValue(orgForEdit.getOfficialNameReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_SHORT_NAME)) {
                item.setOldValue(orgForEdit.getShortNameFrom());
                item.setNewValue(orgForEdit.getShortNameReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_DIRECTOR)) {
                item.setOldValue(orgForEdit.getDirectorFrom());
                item.setNewValue(orgForEdit.getDirectorReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_FOUNDER)) {
                item.setOldValue(orgForEdit.getFounderFrom());
                item.setNewValue(orgForEdit.getFounderReestr());
            }
            if (item.getValueName().equals(ImportRegisterOrgsService.VALUE_SUBORDINATION)) {
                item.setOldValue(orgForEdit.getSubordinationFrom());
                item.setNewValue(orgForEdit.getSubordinationReestr());
            }
        }
        return orgModifyChangeItems;
    }

    public Integer getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(Integer selectedRegion) {
        this.selectedRegion = selectedRegion;
    }

    public void setIsNeedAddElements(Boolean isNeedAddElements) {
        this.isNeedAddElements = isNeedAddElements;
    }

    public Boolean getIsNeedAddElements() {
        return isNeedAddElements;
    }

    public class WebItem {
        protected Long idOfOrgRegistryChange;
        protected Long idOfOrg;
        private Integer state;
        protected Long createDate;
        protected Integer operationType;

        protected Boolean applied = false;

        protected String shortName;
        protected String shortNameFrom;
        protected String officialName;
        protected String officialNameFrom;
        protected String shortNameSupplier;

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
        protected Long uniqueAddressId;
        protected Long uniqueAddressIdFrom;
        protected String inn;
        protected String innFrom;

        protected String guid;
        protected String guidFrom;
        protected Long ekisId;
        protected Long ekisIdFrom;
        protected String egissoId;
        protected String egissoIdFrom;
        protected Long additionalId;
        protected String director;
        protected String directorFrom;
        protected String shortAddress;
        protected String shortAddressFrom;
        protected String municipalDistrict;
        protected String municipalDistrictFrom;
        protected String founder;
        protected String founderFrom;
        protected String subordination;
        protected String subordinationFrom;
        protected Long globalId;
        protected Long globalIdFrom;

        private boolean selected = false;

        private List<WebItem> orgs = new LinkedList<WebItem>();

        public WebItem(OrgRegistryChange registryChange) {
            this.idOfOrgRegistryChange = registryChange.getIdOfOrgRegistryChange();
            this.idOfOrg = registryChange.getIdOfOrg();
            this.state = 0; //stub
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
            this.ekisId = registryChange.getEkisId();
            this.ekisIdFrom = registryChange.getEkisIdFrom();
            this.egissoId = registryChange.getEgissoId();
            this.egissoIdFrom = registryChange.getEgissoIdFrom();
            this.uniqueAddressId = registryChange.getUniqueAddressId();
            this.uniqueAddressIdFrom = registryChange.getUniqueAddressIdFrom();
            this.inn = registryChange.getInn();
            this.innFrom = registryChange.getInnFrom();
            this.additionalId = registryChange.getAdditionalId();
            this.shortAddress = registryChange.getShortAddress();
            this.shortAddressFrom = registryChange.getShortAddressFrom();
            this.municipalDistrict = registryChange.getMunicipalDistrict();
            this.municipalDistrictFrom = registryChange.getMunicipalDistrictFrom();

            this.selected = registryChange.getApplied() ? true: false;
            boolean doAdd;
            for (OrgRegistryChangeItem orgRegistryChangeItem : registryChange.getOrgs()) {
                doAdd = true;
                if (hideApplied && orgRegistryChangeItem.getApplied()) {
                    doAdd = false;
                }
                if (doAdd) {
                    orgs.add(new WebItem(orgRegistryChangeItem));
                }
            }

        }

        public WebItem(OrgRegistryChangeItem registryChangeItem) {
            this.idOfOrgRegistryChange = registryChangeItem.getIdOfOrgRegistryChangeItem();
            this.idOfOrg = registryChangeItem.getIdOfOrg();
            this.state = registryChangeItem.getState();
            this.createDate = registryChangeItem.getCreateDate();
            this.operationType = registryChangeItem.getOperationType();
            this.applied = registryChangeItem.getApplied();
            this.shortName = registryChangeItem.getShortName();
            this.shortNameFrom = registryChangeItem.getShortNameFrom();
            this.shortNameSupplier = registryChangeItem.getShortNameSupplierFrom();
            this.officialName = registryChangeItem.getOfficialName();
            this.officialNameFrom = registryChangeItem.getOfficialNameFrom();
            this.address = registryChangeItem.getAddress();
            this.addressFrom = registryChangeItem.getAddressFrom();
            this.city = registryChangeItem.getCity();
            this.cityFrom = registryChangeItem.getCityFrom();
            this.region = registryChangeItem.getRegion();
            this.regionFrom = registryChangeItem.getRegionFrom();
            this.unom = registryChangeItem.getUnom();
            this.unomFrom = registryChangeItem.getUnomFrom();
            this.unad = registryChangeItem.getUnad();
            this.unadFrom = registryChangeItem.getUnadFrom();
            this.uniqueAddressId = registryChangeItem.getUniqueAddressId();
            this.uniqueAddressIdFrom = registryChangeItem.getUniqueAddressIdFrom();
            this.inn = registryChangeItem.getInn();
            this.innFrom = registryChangeItem.getInnFrom();
            this.guid = registryChangeItem.getGuid();
            this.guidFrom = registryChangeItem.getGuidFrom();
            this.ekisId = registryChangeItem.getEkisId();
            this.ekisIdFrom = registryChangeItem.getEkisIdFrom();
            this.egissoId = registryChangeItem.getEgissoId();
            this.egissoIdFrom = registryChangeItem.getEgissoIdFrom();
            this.additionalId = registryChangeItem.getAdditionalId();
            this.director = registryChangeItem.getDirector();
            this.directorFrom = registryChangeItem.getDirectorFrom();
            this.shortAddress = registryChangeItem.getShortAddress();
            this.shortAddressFrom = registryChangeItem.getShortAddressFrom();
            this.municipalDistrict = registryChangeItem.getMunicipalDistrict();
            this.municipalDistrictFrom = registryChangeItem.getMunicipalDistrictFrom();
            this.founder = registryChangeItem.getFounder();
            this.founderFrom = registryChangeItem.getFounderFrom();
            this.subordination = registryChangeItem.getSubordination();
            this.subordinationFrom = registryChangeItem.getSubordinationFrom();

            this.selected = registryChangeItem.getOperationType().equals(OrgRegistryChange.CREATE_OPERATION) ? false : true;
        }

        private String getImagedString(String image, String value) {
            return String.format("<img src=\"/processor/images/tips/%1$s.png\" style=\"border: 0; margin: 2;\"> %2$s", image, value);
        }

        public String getItemType() {
            if (operationType == OrgRegistryChange.MODIFY_OPERATION) {
                return getImagedString("edit", "");
            }
            if (operationType == OrgRegistryChange.CREATE_OPERATION) {
                return getImagedString("add", "");
            }
            else {
                return "";
            }
        }

        public String getAppliedItem() {
            if (getApplied()) {
                return getImagedString("applied", "");
            } else {
                return "";
            }
        }

        private String getResultString(String value, String valueFrom) {
            if (value == null) {
                value = "";
            }
            if (valueFrom == null) {
                valueFrom = "";
            }

            if (value.equals(valueFrom)) {
                return value;
            }

            if (StringUtils.isEmpty(value)) {
                return getImagedString("green", valueFrom);
            }

            if (StringUtils.isEmpty(valueFrom)) {
                return getImagedString("red", value);
            }

            return getImagedString("red", value) + "<hr/>" + getImagedString("green", valueFrom);
        }

        private String getResultString(Long value, Long valueFrom) {
            String v, vFrom;
            if (value == null) { v = ""; } else { v = value.toString(); }
            if (valueFrom == null) { vFrom = ""; } else { vFrom = valueFrom.toString(); }

            return getResultString(v.toString(), vFrom.toString());
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

        public int getOrgsSize() {
            if (orgs != null) {
                return orgs.size();
            } else {
                return 1;
            }
        }

        public String getComplexName() {
            return shortName + "<br/>" + "(" + officialName + ")";
        }

        public String getComplexNameFrom() {
            return shortNameFrom + "<br/>" + "(" + officialNameFrom + ")";
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
            return getResultString(shortName, shortNameFrom);
        }

        public String getShortNameReestr() {
            return shortName;
        }

        public String getShortNameSupplier() {
            return shortNameSupplier;
        }

        public String getOrgNumber() {
            String str = (shortName == null) ? "" : shortName;
            String strFrom = (shortNameFrom == null) ? "" : shortNameFrom;
            return getResultString(Org.extractOrgNumberFromName(str), Org.extractOrgNumberFromName(strFrom));
        }

        public boolean getIsSimilar() {
            return operationType == OrgRegistryChange.SIMILAR;
        }

        public boolean getIsAdding() {
            return operationType == OrgRegistryChange.CREATE_OPERATION;
        }

        public boolean getIsModify() {
            return operationType == OrgRegistryChange.MODIFY_OPERATION;
        }

        public String getShortNameFrom() {
            return shortNameFrom;
        }

        public String getOfficialName() {
            return getResultString(officialName, officialNameFrom);
        }

        public String getOfficialNameReestr() {
            return officialName;
        }

        public String getOfficialNameFrom() {
            return officialNameFrom;
        }

        public String getAddress() {
            return getResultString(address, addressFrom);
        }

        public String getShortAddress() {
            return getResultString(shortAddress, shortAddressFrom);
        }

        public String getAddressReestr() {
            return address;
        }

        public String getAddressAISReestr() {
            return address;
        }

        public String getAddressFrom() {
            return addressFrom;
        }

        public String getShortAddressFrom() {
            return shortAddressFrom;
        }

        public String getShortAddressReestr() {
            return shortAddress;
        }

        public String getMunicipalDistrictFrom() {
            return municipalDistrictFrom;
        }

        public String getMunicipalDistrictReestr() {
            return municipalDistrict;
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

        public String getUnom() {
            return getResultString(unom, unomFrom);
        }

        public String getUnomReestrNullSafe() {
            return unom == null ? "" : unom.toString();
        }

        public Long getUnomFrom() {
            return unomFrom;
        }

        public String getUnomFromNullSafe() {
            return unomFrom == null ? "" : unomFrom.toString();
        }

        public String getUnad() {
            return getResultString(unad, unadFrom);
        }

        public String getEgissoId() {
            return getResultString(egissoId, egissoIdFrom);
        }

        public String getUnadReestrNullSafe() {
            return unad == null ? "" : unad.toString();
        }

        public Long getUnadFrom() {
            return unadFrom;
        }

        public String getUnadFromNullSafe() {
            return unadFrom == null ? "" : unadFrom.toString();
        }

        public String getGuid() {
            return getResultString(guid, guidFrom);
        }

        public String getEkisId() {
            return getResultString(ekisId, ekisIdFrom);
        }

        public String getGlobalId() {
            return getResultString(globalId, globalIdFrom);
        }

        public String getGuidReestr() {
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

        public String getOperationType() {
            switch (operationType) {
                case OrgRegistryChange.CREATE_OPERATION:
                    return "Создание";
                case OrgRegistryChange.MODIFY_OPERATION:
                    return "Изменение";
                case OrgRegistryChange.DELETE_OPERATION:
                    return "Отключение";
                default:
                    return "";
            }
        }

        public Integer getOperation() {
            return operationType;
        }

        public List<WebItem> getOrgs() {
            return orgs;
        }

        public void setOrgs(List<WebItem> orgs) {
            this.orgs = orgs;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getUniqueAddressId() {
            return getResultString(uniqueAddressId, uniqueAddressIdFrom);
        }

        public Long getUniqueAddressIdReestr() {
            return uniqueAddressId;
        }

        public String getUniqueAddressIdReestrNullSafe() {
            return uniqueAddressId == null ? "" : uniqueAddressId.toString();
        }

        public void setUniqueAddressId(Long uniqueAddressId) {
            this.uniqueAddressId = uniqueAddressId;
        }

        public Long getUniqueAddressIdFrom() {
            return uniqueAddressIdFrom;
        }

        public String getUniqueAddressIdFromNullSafe() {
            return uniqueAddressIdFrom == null ? "" : uniqueAddressIdFrom.toString();
        }

        public void setUniqueAddressIdFrom(Long uniqueAddressIdFrom) {
            this.uniqueAddressIdFrom = uniqueAddressIdFrom;
        }

        public String getInn() {
            return getResultString(inn, innFrom);
        }

        public String getInnReestr() {
            return inn;
        }

        public void setInn(String inn) {
            this.inn = inn;
        }

        public String getInnFrom() {
            return innFrom;
        }

        public void setInnFrom(String innFrom) {
            this.innFrom = innFrom;
        }

        public Integer getState() {
            return state;
        }

        public String getStringState() {
            if (state == null) return "";
            if (state.equals(Org.ACTIVE_STATE)) return "Обслуживается";
            else {
                if (state.equals(Org.INACTIVE_STATE))
                    return "Не обслуживается";
                else
                    return "";
            }
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public String getDirector() {
            return getResultString(director, directorFrom);
        }

        public String getFounder() {
            return getResultString(founder, founderFrom);
        }

        public String getSubordination() {
            return getResultString(subordination, subordinationFrom);
        }

        public String getDirectorReestr() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getDirectorFrom() {
            return directorFrom;
        }

        public void setDirectorFrom(String directorFrom) {
            this.directorFrom = directorFrom;
        }

        public String getEkisIdReestrNullSafe() {
            return ekisId == null ? "" : ekisId.toString();
        }

        public String getEkisIdFromNullSafe() {
            return ekisIdFrom == null ? "" : ekisIdFrom.toString();
        }

        public String getGlobalIdReestrNullSafe() {
            return globalId == null ? "" : globalId.toString();
        }

        public String getGlobalIdFromNullSafe() {
            return globalIdFrom == null ? "" : globalIdFrom.toString();
        }

        public String getEgissoIdFrom() {
            return egissoIdFrom;
        }

        public String getEgissoIdReestr() {
            return egissoId;
        }

        public String getFounderReestr() {
            return founder;
        }

        public void setFounder(String founder) {
            this.founder = founder;
        }

        public String getFounderFrom() {
            return founderFrom;
        }

        public void setFounderFrom(String founderFrom) {
            this.founderFrom = founderFrom;
        }

        public String getSubordinationReestr() {
            return subordination;
        }

        public void setSubordination(String subordination) {
            this.subordination = subordination;
        }

        public String getSubordinationFrom() {
            return subordinationFrom;
        }

        public void setSubordinationFrom(String subordinationFrom) {
            this.subordinationFrom = subordinationFrom;
        }
    }
}