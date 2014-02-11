/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgEditPage extends BasicWorkspacePage
        implements OrgSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList,
        CategoryOrgListSelectPage.CompleteHandlerList, ContragentSelectPage.CompleteHandler,
        ConfigurationProviderSelect {

    private Long idOfOrg;
    private String shortName;
    private String officialName;
    private String tag;
    private String address;
    private String phone;
    private String officialPersonFirstName;
    private String officialPersonSurname;
    private String officialPersonSecondName;
    private String officialPosition;
    private String contractId;
    private Date contractTime;
    private Integer state;
    private Long cardLimit;
    private String publicKey;
    private Long idOfPacket;
    private final OrgStateMenu orgStateMenu = new OrgStateMenu();
    private boolean changeSsoPassword = false;
    private String plainSsoPassword;
    private String plainSsoPasswordConfirmation;
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private Long menuExchangeSourceOrg;
    private String menuExchangeSourceOrgName;
    private ContragentItem defaultSupplier = new ContragentItem();
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private String guid;
    private ConfigurationProvider configurationProvider;
    private List<Long> idOfOrgList = new ArrayList<Long>();
    private String city;
    private String district;
    private String location;
    private String latitude;
    private String longitude;
    private Boolean fullSyncParam;
    private Boolean changeCommodityAccounting;
    private Boolean usePlanOrders;

    // тип организации "ПОТРЕБИТЕЛЬ / ПОСТАВЩИК"
    private OrganizationType organizationType;
    private final OrganizationTypeMenu organizationTypeMenu = new OrganizationTypeMenu();

    private Integer refectoryType;
    private List<SelectItem> refectoryTypeComboMenuItems;

    private String filterCategoryOrg;
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private List<Long> friendlyIdOfOrgList = new ArrayList<Long>();
    private String filterFriendlyOrgs = "Не выбрано";

    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        fill(org);
        ////
        menuExchangeSourceOrg = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        reloadMenuExchangeSourceOrgName(session);
    }

    public void updateOrg(Session session, Long idOfOrg) throws Exception {
        Contragent defaultSupplier = (Contragent) session.load(Contragent.class, this.defaultSupplier.getIdOfContragent());
        Org org = (Org) session.load(Org.class, idOfOrg);
        org.setRefectoryType(refectoryType);
        org.setShortName(shortName);
        org.setOfficialName(officialName);
        org.setTag(tag);
        org.setCity(city);
        org.setDistrict(district);
        org.setLocation(location);
        org.setLongitude(longitude);
        org.setLatitude(latitude);
        org.setAddress(address);
        org.setPhone(phone);
        Person officialPerson = org.getOfficialPerson();
        officialPerson.setFirstName(officialPersonFirstName);
        officialPerson.setSurname(officialPersonSurname);
        officialPerson.setSecondName(officialPersonSecondName);
        org.setOfficialPosition(officialPosition);
        org.setContractId(contractId);
        org.setContractTime(contractTime);
        org.setState(state);
        org.setCardLimit(cardLimit);
        org.setPublicKey(publicKey);
        org.setIdOfPacket(idOfPacket);
        if (changeSsoPassword) {
            org.setSsoPassword(plainSsoPassword);
        }
        org.setSmsSender(this.smsSender);
        org.setPriceOfSms(this.priceOfSms);
        org.setSubscriptionPrice(this.subscriptionPrice);

        org.setDefaultSupplier(defaultSupplier);
        org.setINN(INN);
        org.setOGRN(OGRN);
        org.setMailingListReportsOnNutrition(mailingListReportsOnNutrition);
        org.setMailingListReportsOnVisits(mailingListReportsOnVisits);
        org.setMailingListReports1(mailingListReports1);
        org.setMailingListReports2(mailingListReports2);
        org.setGuid(guid);
        org.setCategoriesInternal(new HashSet<CategoryOrg>());
        if (this.idOfCategoryOrgList.isEmpty()) org.setCategoriesInternal(null);
        else {
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(session, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                org.getCategories().add((CategoryOrg) object);
            }
        }

        Set<Org> friendlyOrg = org.getFriendlyOrg();
        if(idOfOrgList!=null && !idOfOrgList.isEmpty()){
            HashSet<Org> selectOrg = DAOUtils.findOrgById(session, idOfOrgList);
            if(!selectOrg.equals(friendlyOrg)){
                friendlyOrg.removeAll(selectOrg);
                for (Org o: friendlyOrg){
                    int count = DAOUtils.clearFriendlyOrgByOrg(session, o.getIdOfOrg());
                }
                for (Org o : selectOrg) {
                    ClientManager.updateClientVersion(session, o.getClients());
                    o.setFriendlyOrg(selectOrg);
                }
            }
        }



        org.setCommodityAccounting(changeCommodityAccounting);
        if(changeCommodityAccounting){
            org.setConfigurationProvider(configurationProvider);
            fullSyncParam=true;
        } else {
            org.setConfigurationProvider(null);
        }
        org.setType(this.organizationType);

        if(this.fullSyncParam){
            org.setFullSyncParam(fullSyncParam);
        }

        org.setUsePlanOrders(usePlanOrders);

        session.update(org);
        fill(org);
        /////
        DAOUtils.updateMenuExchangeLink(session, menuExchangeSourceOrg, idOfOrg);
    }

    private void fill(Org org) throws Exception {
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.officialName = org.getOfficialName();
        this.tag = org.getTag();
        this.city = org.getCity();
        this.district = org.getDistrict();
        this.location = org.getLocation();
        this.longitude = org.getLongitude();
        this.latitude = org.getLatitude();
        this.address = org.getAddress();
        this.phone = org.getPhone();
        Person officialPerson = org.getOfficialPerson();
        this.officialPersonFirstName = officialPerson.getFirstName();
        this.officialPersonSurname = officialPerson.getSurname();
        this.officialPersonSecondName = officialPerson.getSecondName();
        this.officialPosition = org.getOfficialPosition();
        this.contractId = org.getContractId();
        this.contractTime = org.getContractTime();
        this.state = org.getState();
        this.cardLimit = org.getCardLimit();
        this.publicKey = org.getPublicKey();
        this.idOfPacket = org.getIdOfPacket();
        this.smsSender = org.getSmsSender();
        this.priceOfSms = org.getPriceOfSms();
        this.subscriptionPrice = org.getSubscriptionPrice();
        this.defaultSupplier = new ContragentItem(org.getDefaultSupplier());
        this.OGRN=org.getOGRN();
        this.INN=org.getINN();
        this.mailingListReportsOnNutrition = org.getMailingListReportsOnNutrition();
        this.mailingListReportsOnVisits = org.getMailingListReportsOnVisits();
        this.mailingListReports1 = org.getMailingListReports1();
        this.mailingListReports2 = org.getMailingListReports2();
        this.guid = org.getGuid();
        this.fullSyncParam = org.getFullSyncParam();
        this.usePlanOrders = org.getUsePlanOrders();

        this.changeCommodityAccounting = org.getCommodityAccounting();
        this.organizationType = org.getType();

        this.refectoryType = org.getRefectoryType();
        if (this.refectoryType == null) {
            this.refectoryType = -1;
        }
        // Добавление элементов в выпадающий список для типа пищеблока
        refectoryTypeComboMenuItems = new ArrayList<SelectItem>();
        if (this.refectoryType != -1) {
            refectoryTypeComboMenuItems.add(new SelectItem(refectoryType, Org.REFECTORY_TYPE_NAMES[refectoryType]));
        }
        refectoryTypeComboMenuItems.add(new SelectItem(-1, ""));
        for (int i = 0; i < Org.REFECTORY_TYPE_NAMES.length; i++) {
            if (i != this.refectoryType) {
                SelectItem item = new SelectItem(i, Org.REFECTORY_TYPE_NAMES[i]);
                refectoryTypeComboMenuItems.add(item);
            }
        }

        idOfCategoryOrgList.clear();
        this.filterCategoryOrg ="Не выбрано";
        if(org.getCategories()!=null && !org.getCategories().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryOrg categoryOrg: org.getCategories()){
                stringBuilder.append(categoryOrg.getCategoryName());
                stringBuilder.append("; ");
                idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
            }
            this.filterCategoryOrg = stringBuilder.substring(0,stringBuilder.length()-2);
        }

        select(org.getConfigurationProvider());
        friendlyIdOfOrgList.clear();
        idOfOrgList.clear();
        filterFriendlyOrgs = "Не выбрано";
        if(!(org.getFriendlyOrg()==null || org.getFriendlyOrg().isEmpty())){
            StringBuilder stringBuilder = new StringBuilder();
            for (Org friendlyOrg: org.getFriendlyOrg()){
                stringBuilder.append(friendlyOrg.getShortName());
                stringBuilder.append("; ");
                friendlyIdOfOrgList.add(friendlyOrg.getIdOfOrg());
                idOfOrgList.add(friendlyOrg.getIdOfOrg());
            }
            filterFriendlyOrgs = stringBuilder.toString();
        }
    }

    public void checkCommodityAccountingConfiguration(Session session) throws Exception{
        //Org org = (Org) session.load(Org.class, menuExchangeSourceOrg);
        if(menuExchangeSourceOrg!=null){
            Org org = DAOUtils.findOrg(session, menuExchangeSourceOrg);
            final Long idOfProvider = configurationProvider.getIdOfConfigurationProvider();
            configurationProvider = ConfigurationProviderService.loadConfigurationProvider(session, idOfProvider);
            if(!configurationProvider.getOrgs().contains(org)){
                final StringBuilder message = new StringBuilder("Организации - источника меню школы ")
                        .append("'").append(org.getShortName()).append("'")
                        .append(" не входит в текущую конфигурацию провайдера");
                throw new Exception(message.toString());
            }
        }
    }

    /* interface implements methods */
    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if(null != orgMap) {
            idOfOrgList = new ArrayList<Long>();
            StringBuilder stringBuilder = new StringBuilder();
            if(!orgMap.isEmpty()){
                for(Long idOfOrg: orgMap.keySet()){
                    idOfOrgList.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg));
                    stringBuilder.append("; ");
                }
                filterFriendlyOrgs = stringBuilder.toString();
            } else {
                filterFriendlyOrgs = "Не выбрано.";
            }
        }
    }

    @Override
    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
        if(null != categoryOrgMap) {
            idOfCategoryOrgList = new ArrayList<Long>();
            if(!categoryOrgMap.isEmpty()){
                filterCategoryOrg ="";
                for(Long idOfCategoryOrg: categoryOrgMap.keySet()){
                    idOfCategoryOrgList.add(idOfCategoryOrg);
                    filterCategoryOrg = filterCategoryOrg.concat(categoryOrgMap.get(idOfCategoryOrg)+ "; ");
                }
                filterCategoryOrg = filterCategoryOrg.substring(0, filterCategoryOrg.length()-1);
            }

        }
    }

    public Object showConfigurationProviderSelection() throws Exception{
        RuntimeContext.getAppContext().getBean(ConfigurationProviderItemsPanel.class).reload();
        RuntimeContext.getAppContext().getBean(ConfigurationProviderItemsPanel.class).pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        if(configurationProvider!=null) configurationProvider.getName();    // lazy load
        this.configurationProvider = configurationProvider;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.menuExchangeSourceOrg = idOfOrg;
        reloadMenuExchangeSourceOrgName(session);
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlags, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.defaultSupplier = new ContragentItem(contragent);
        }
    }

    public String getPageFilename() {
        return "org/edit";
    }

    private void reloadMenuExchangeSourceOrgName(Session session) {
        if (menuExchangeSourceOrg == null) {
            menuExchangeSourceOrgName = null;
        } else {
            Org org = (Org) session.load(Org.class, menuExchangeSourceOrg);
            menuExchangeSourceOrgName = org.getShortName();
        }
    }

    /* Getters and Setters */
    public Long getMenuExchangeSourceOrg() {
        return menuExchangeSourceOrg;
    }

    public String getMenuExchangeSourceOrgName() {
        return menuExchangeSourceOrgName;
    }

    public String getIdOfCategoryOrgList() {
        return idOfCategoryOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getIdOfFriendlyOrgList() {
        return friendlyIdOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public List<Long> getFriendlyIdOfOrgList() {
        return friendlyIdOfOrgList;
    }

    public String getFilterCategoryOrg() {
        if (idOfCategoryOrgList.isEmpty()) return "Нет";
        return filterCategoryOrg;
    }

    public String getFriendlyFilterOrgs() {
        return filterFriendlyOrgs;
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public String getConfigurationProviderName() {
        if(configurationProvider==null) return null;
        else return configurationProvider.getName();
    }

    public void setChangeCommodityAccounting(Boolean changeCommodityAccounting) {
        this.changeCommodityAccounting = changeCommodityAccounting;
    }

    public Boolean getChangeCommodityAccounting() {
        return changeCommodityAccounting;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

    public String getOGRN() {
        return OGRN;
    }

    public void setOGRN(String OGRN) {
        this.OGRN = OGRN;
    }

    public OrgStateMenu getOrgStateMenu() {
        return orgStateMenu;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOfficialPersonFirstName() {
        return officialPersonFirstName;
    }

    public void setOfficialPersonFirstName(String officialPersonFirstName) {
        this.officialPersonFirstName = officialPersonFirstName;
    }

    public String getOfficialPersonSurname() {
        return officialPersonSurname;
    }

    public void setOfficialPersonSurname(String officialPersonSurname) {
        this.officialPersonSurname = officialPersonSurname;
    }

    public String getOfficialPersonSecondName() {
        return officialPersonSecondName;
    }

    public void setOfficialPersonSecondName(String officialPersonSecondName) {
        this.officialPersonSecondName = officialPersonSecondName;
    }

    public String getOfficialPosition() {
        return officialPosition;
    }

    public void setOfficialPosition(String officialPosition) {
        this.officialPosition = officialPosition;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(Long cardLimit) {
        this.cardLimit = cardLimit;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public void setIdOfPacket(Long idOfPacket) {
        this.idOfPacket = idOfPacket;
    }

    public boolean isChangeSsoPassword() {
        return changeSsoPassword;
    }

    public void setChangeSsoPassword(boolean changeSsoPassword) {
        this.changeSsoPassword = changeSsoPassword;
    }

    public String getPlainSsoPassword() {
        return plainSsoPassword;
    }

    public void setPlainSsoPassword(String plainSsoPassword) {
        this.plainSsoPassword = plainSsoPassword;
    }

    public String getPlainSsoPasswordConfirmation() {
        return plainSsoPasswordConfirmation;
    }

    public void setPlainSsoPasswordConfirmation(String plainSsoPasswordConfirmation) {
        this.plainSsoPasswordConfirmation = plainSsoPasswordConfirmation;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public void setSmsSender(String smsSender) {
        this.smsSender = smsSender;
    }

    public Long getPriceOfSms() {
        return priceOfSms;
    }

    public void setPriceOfSms(Long priceOfSms) {
        this.priceOfSms = priceOfSms;
    }

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Long subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public ContragentItem getDefaultSupplier() {
        return defaultSupplier;
    }

    public String getMailingListReportsOnNutrition() {
        return mailingListReportsOnNutrition;
    }

    public void setMailingListReportsOnNutrition(String mailingListReportsOnNutrition) {
        this.mailingListReportsOnNutrition = mailingListReportsOnNutrition;
    }

    public String getMailingListReportsOnVisits() {
        return mailingListReportsOnVisits;
    }

    public void setMailingListReportsOnVisits(String mailingListReportsOnVisits) {
        this.mailingListReportsOnVisits = mailingListReportsOnVisits;
    }

    public String getMailingListReports1() {
        return mailingListReports1;
    }

    public void setMailingListReports1(String mailingListReports1) {
        this.mailingListReports1 = mailingListReports1;
    }

    public String getMailingListReports2() {
        return mailingListReports2;
    }

    public void setMailingListReports2(String mailingListReports2) {
        this.mailingListReports2 = mailingListReports2;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getRefectoryType() {
        return refectoryType;
    }

    public void setRefectoryType(Integer refectoryType) {
        this.refectoryType = refectoryType;
    }

    public List<SelectItem> getRefectoryTypeComboMenuItems() {
        return refectoryTypeComboMenuItems;
    }

    public Boolean getFullSyncParam() {
        return fullSyncParam;
    }

    public void setFullSyncParam(Boolean fullSyncParam) {
        this.fullSyncParam = fullSyncParam;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationTypeMenu getOrganizationTypeMenu() {
        return organizationTypeMenu;
    }

    public Boolean getUsePlanOrders() {
        return usePlanOrders;
    }

    public void setUsePlanOrders(Boolean usePlanOrders) {
        this.usePlanOrders = usePlanOrders;
    }

    public static class ContragentItem {
        private final Long idOfContragent;

        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }
        public String getContragentName() {
            return contragentName;
        }
    }
}