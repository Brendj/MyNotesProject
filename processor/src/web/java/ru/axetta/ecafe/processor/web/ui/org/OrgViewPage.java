/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingManager;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.ARMsSettingsType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgViewPage extends BasicWorkspacePage {

    private Long idOfOrg;
    private String shortName;
    private String shortNameInfoService;
    private String founder;
    private String subordination;
    private String officialName;
    private String tag;
    private String address;
    private String shortAddress;
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
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private String menuExchangeSourceOrgName;
    private String defaultSupplierName;
    private String coSupplierName;
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private String guid;
    private Long ekisId;
    private String egissoId;
    private List<CategoryOrg> categoryOrg;
    private String configurationProviderName;
    private List<Long> idOfOrgList;
    private String filterOrgs = "Не выбрано";
    private List<Org> friendlyOrganisation = new ArrayList<Org>();
    private boolean mainBuidling;
    private String city;
    private String district;
    private String municipalDistrict;
    private String location;
    private String latitude;
    private String longitude;
    private Integer refectoryType;
    private Boolean commodityAccountingParam;
    private Boolean usePlanOrders;
    private Boolean disableEditingClientsFromAISReestr;
    private Boolean usePaydableSubscriptionFeeding;
    private Boolean multiCardModeEnabled;

    // тип организации "ПОТРЕБИТЕЛЬ / ПОСТАВЩИК"
    private OrganizationType organizationType;
    private OrganizationType organizationTypeInitial;
    private String refectoryTypeStringRepresentation;
    private Long btiUnom;
    private Long btiUnad;
    private String introductionQueue;
    private Long additionalIdBuilding;
    private Long uniqueAddressId;
    private String statusDetailing;
    private String securityLevel;
    private Boolean photoRegistry;
    private Boolean variableFeeding;
    private Boolean isWorkInSummerTime;
    private Boolean isRecyclingEnabled;
    private Boolean autoCreateCards;
    private Boolean needVirifyCardSign;
    private String registryUrl;
    private Boolean denyPayPlanForTimeDifference;
    private Boolean allowRegistryChangeEmployee;
    private Boolean helpdeskEnabled;
    private Boolean preordersEnabled;
    private Boolean participantOP;
    private Boolean preorderlp;
    private Boolean useLongCardNo;

    private String interdistrictCouncil; //В каком межрайонном совете состоит ОО
    private String interdistrictCouncilChief ; //Председателем какого межрайонного совета является руководитель ОО

    private Boolean payByCashier;
    private Boolean oneActiveCard;
    private Boolean changesDSZN;

    private Boolean useWebArm;
    private Boolean useWebArmAdmin;

    private Boolean goodDateCheck;

    private Long orgIdFromNsi;
    private Boolean governmentContract;

    private SelectItem[] statusDetails = readStatusDetailsComboMenuItems();

    private Boolean useMealSchedule;
    private Boolean newСashierMode;

    private SelectItem[] readStatusDetailsComboMenuItems() {
        SelectItem[] items = new SelectItem[5];
        items[0] = new SelectItem(0, "");
        items[1] = new SelectItem(1, "Запланировано подключение");
        items[2] = new SelectItem(2, "На ремонте");
        items[3] = new SelectItem(3, "Закрыто");
        items[4] = new SelectItem(4, "Другое");
        return items;
    }

    private List detailsItem = readDetailItems();

    private List<String> readDetailItems() {
        List<String> items = new ArrayList();
        items.add("");
        items.add("Запланировано подключение");
        items.add("На ремонте");
        items.add("Закрыто");
        items.add("Другое");
        return items;
    }

    private String statusTextArea;

    private String statusDetail;

    private Boolean requestForVisitsToOtherOrg;

    public String getRefectoryTypeStringRepresentation() {
        if ((refectoryType == null) || (refectoryType >= Org.REFECTORY_TYPE_NAMES.length)) {
            refectoryType = -1;
            refectoryTypeStringRepresentation = "";
        } else if (refectoryType == -1) {
            refectoryTypeStringRepresentation = "";
        } else {
            refectoryTypeStringRepresentation = Org.REFECTORY_TYPE_NAMES[refectoryType];
        }
        return refectoryTypeStringRepresentation;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.shortNameInfoService = org.getShortNameInfoService();
        this.founder = org.getFounder();
        this.subordination = org.getSubordination();
        this.officialName = org.getOfficialName();
        this.tag = org.getTag();
        this.city = org.getCity();
        this.district = org.getDistrict();
        this.municipalDistrict = org.getMunicipalDistrict();
        this.location = org.getLocation();
        this.longitude = org.getLongitude();
        this.latitude = org.getLatitude();
        this.address = org.getAddress();
        this.shortAddress = org.getShortAddress();
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
        this.idOfPacket = DAOReadonlyService.getInstance().getIdOfPacket(idOfOrg);
        this.smsSender = org.getSmsSender();
        this.priceOfSms = org.getPriceOfSms();
        this.subscriptionPrice = org.getSubscriptionPrice();
        this.defaultSupplierName = org.getDefaultSupplier().getContragentName();
        if (org.getCoSupplier() != null) {
            this.coSupplierName = org.getCoSupplier().getContragentName();
        }
        this.INN=org.getINN();
        this.OGRN=org.getOGRN();
        this.guid = org.getGuid();
        this.ekisId = org.getEkisId();
        this.egissoId = org.getEgissoId();
        this.commodityAccountingParam = org.getCommodityAccounting();
        this.disableEditingClientsFromAISReestr = org.getDisableEditingClientsFromAISReestr();
        this.usePlanOrders = org.getUsePlanOrders();
        this.usePaydableSubscriptionFeeding = org.getUsePaydableSubscriptionFeeding();
        this.categoryOrg= new LinkedList<CategoryOrg>();
        if(!org.getCategories().isEmpty()){
           for (CategoryOrg co: org.getCategories()){
               this.categoryOrg.add(co);
           }
        }

        this.refectoryType = org.getRefectoryType();
        getRefectoryTypeStringRepresentation();
        this.organizationType= org.getType();
        this.organizationTypeInitial= org.getTypeInitial();
        this.registryUrl = org.getRegistryUrl();

        ////  menu exchange source
        Long menuExchangeSourceOrgId = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (menuExchangeSourceOrgId == null) {
            menuExchangeSourceOrgName = "";
        } else {
            Org menuExchangeSourceOrg = (Org) session.load(Org.class, menuExchangeSourceOrgId);
            menuExchangeSourceOrgName = menuExchangeSourceOrg.getShortName();
        }

        ConfigurationProvider configurationProvider = org.getConfigurationProvider();
        if (configurationProvider == null) {
            configurationProviderName = "";
        } else {
           configurationProviderName = configurationProvider.getName();
        }
        friendlyOrganisation.clear();
        for (Org o : org.getFriendlyOrg()) {
            friendlyOrganisation.add(o);
        }
        this.mainBuidling = org.isMainBuilding();
        this.mailingListReportsOnNutrition = org.getMailingListReportsOnNutrition();
        this.mailingListReportsOnVisits = org.getMailingListReportsOnVisits();
        this.mailingListReports1 = org.getMailingListReports1();
        this.mailingListReports2 = org.getMailingListReports2();
        this.btiUnom = org.getBtiUnom();
        this.btiUnad = org.getBtiUnad();
        this.additionalIdBuilding = org.getAdditionalIdBuilding();
        this.uniqueAddressId = org.getUniqueAddressId();
        this.introductionQueue = org.getIntroductionQueue();
        this.statusDetailing = org.getStatusDetailing();

        String[] strings = this.statusDetailing.split("/");

        if (strings.length > 0) {
            if (strings[0].equals("")) {
                this.statusDetail = "0";
            } else if (strings[0].equals("Запланировано подключение")) {
                this.statusDetail = "1";
            } else if (strings[0].equals("На ремонте")) {
                this.statusDetail = "2";
            } else if (strings[0].equals("Закрыто")) {
                this.statusDetail = "3";
            } else if (strings[0].equals("Другое")) {
                this.statusDetail = "4";
            }
        } else {
            this.statusDetail = "";
        }

        if (strings.length == 2) {
            this.statusTextArea = strings[1];
        } else {
            this.statusTextArea = "";
        }

        this.payByCashier = org.getPayByCashier();
        this.oneActiveCard = org.getOneActiveCard();
        this.changesDSZN = org.getChangesDSZN();


        this.interdistrictCouncil = org.getInterdistrictCouncil();
        this.interdistrictCouncilChief = org.getInterdistrictCouncilChief();
        this.securityLevel = org.getSecurityLevel().toString();
        this.photoRegistry = org.getPhotoRegistryDirective().getCode().equals(1);
        this.variableFeeding = org.getVariableFeeding();
        this.isWorkInSummerTime = org.getIsWorkInSummerTime();
        this.isRecyclingEnabled = org.getIsRecyclingEnabled();
        this.needVirifyCardSign = org.getNeedVerifyCardSign();
        this.autoCreateCards = org.getAutoCreateCards();
        this.denyPayPlanForTimeDifference = org.getDenyPayPlanForTimeDifference();
        this.allowRegistryChangeEmployee = org.getAllowRegistryChangeEmployee();
        this.helpdeskEnabled = org.getHelpdeskEnabled();
        this.requestForVisitsToOtherOrg = org.getRequestForVisitsToOtherOrg();
        this.preordersEnabled = org.getPreordersEnabled();
        this.multiCardModeEnabled = org.multiCardModeIsEnabled();
        this.participantOP = org.getParticipantOP();
        this.preorderlp = org.getPreorderlp();

        this.useWebArm = org.getUseWebArm();
        this.useWebArmAdmin = org.getUseWebArmAdmin();
        this.orgIdFromNsi = org.getOrgIdFromNsi();
        this.goodDateCheck = org.getGooddatecheck();
        this.governmentContract = org.getGovernmentContract() != null && org.getGovernmentContract();
        this.useLongCardNo = org.getUseLongCardNo();

        OrgSettingManager manager = RuntimeContext.getAppContext().getBean(OrgSettingManager.class);
        this.useMealSchedule = (Boolean) manager.getSettingValueFromOrg(org, ARMsSettingsType.USE_MEAL_SCHEDULE);
        this.newСashierMode = org.getNewСashierMode();
    }

    public String getFilterOrgs() {
        return filterOrgs;
    }

    public String getConfigurationProviderName() {
        return configurationProviderName;
    }

    public void setConfigurationProviderName(String configurationProviderName) {
        this.configurationProviderName = configurationProviderName;
    }

    public List<CategoryOrg> getCategoryOrg() {
        return categoryOrg;
    }

    public void setCategoryOrg(List<CategoryOrg> categoryOrg) {
        this.categoryOrg = categoryOrg;
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

    public String getMenuExchangeSourceOrgName() {
        return menuExchangeSourceOrgName;
    }


    public String getPageFilename() {
        return "org/view";
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getTag() {
        return tag;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public String getPhone() {
        return phone;
    }

    public String getOfficialPersonFirstName() {
        return officialPersonFirstName;
    }

    public String getOfficialPersonSurname() {
        return officialPersonSurname;
    }

    public String getOfficialPersonSecondName() {
        return officialPersonSecondName;
    }

    public String getOfficialPosition() {
        return officialPosition;
    }

    public String getContractId() {
        return contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public Integer getState() {
        return state;
    }

    public Long getCardLimit() {
        return cardLimit;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public Long getPriceOfSms() {
        return priceOfSms;
    }

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public String getDefaultSupplierName() {
        return defaultSupplierName;
    }

    public Integer getRefectoryType() {
        return refectoryType;
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

    public Boolean getUsePlanOrders() {
        return usePlanOrders;
    }

    public void setUsePlanOrders(Boolean usePlanOrders) {
        this.usePlanOrders = usePlanOrders;
    }

    public Boolean getCommodityAccountingParam() {
        return commodityAccountingParam;
    }

    public void setCommodityAccountingParam(Boolean commodityAccountingParam) {
        this.commodityAccountingParam = commodityAccountingParam;
    }

    public boolean isMainBuidling() {
        return mainBuidling;
    }

    public Long getAdditionalIdBuilding() {
        return additionalIdBuilding;
    }

    public void setAdditionalIdBuilding(Long additionalIdBuilding) {
        this.additionalIdBuilding = additionalIdBuilding;
    }

    public Long getUniqueAddressId() {
        return uniqueAddressId;
    }

    public void setUniqueAddressId(Long uniqueAddressId) {
        this.uniqueAddressId = uniqueAddressId;
    }

    public String getIntroductionQueue() {
        return introductionQueue;
    }

    public void setIntroductionQueue(String introductionQueue) {
        this.introductionQueue = introductionQueue;
    }

    public Long getBtiUnad() {
        return btiUnad;
    }

    public void setBtiUnad(Long btiUnad) {
        this.btiUnad = btiUnad;
    }

    public Long getBtiUnom() {
        return btiUnom;
    }

    public void setBtiUnom(Long btiUnom) {
        this.btiUnom = btiUnom;
    }

    public String getStatusDetailing() {
        return statusDetailing;
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getStatusTextArea() {
        return statusTextArea;
    }

    public void setStatusTextArea(String statusTextArea) {
        this.statusTextArea = statusTextArea;
    }

    public List getDetailsItem() {
        return detailsItem;
    }

    public void setDetailsItem(List detailsItem) {
        this.detailsItem = detailsItem;
    }

    public SelectItem[] getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(SelectItem[] statusDetails) {
        this.statusDetails = statusDetails;
    }

    public Boolean getPayByCashier() {
        return payByCashier;
    }

    public Boolean getOneActiveCard() {
        return oneActiveCard;
    }

    public Boolean getChangesDSZN() {
        return changesDSZN;
    }

    public String getInterdistrictCouncil() {
        return interdistrictCouncil;
    }

    public String getInterdistrictCouncilChief() {
        return interdistrictCouncilChief;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public Boolean getPhotoRegistry() {
        return photoRegistry;
    }



    public String getCoSupplierName() {
        return coSupplierName;
    }

    public void setCoSupplierName(String coSupplierName) {
        this.coSupplierName = coSupplierName;
    }

    public List<Org> getFriendlyOrganisation() {
        return friendlyOrganisation;
    }

    public void setFriendlyOrganisation(List<Org> friendlyOrganisation) {
        this.friendlyOrganisation = friendlyOrganisation;
    }

    public String getStyleClass(Boolean mb) {
        return mb ? "output-text-strong" : "output-text";
    }

    public String getStyleClassLink(Boolean mb) {
        return mb ? "output-text-org-main" : "output-text-org";
    }

    public Boolean isCurrentOrg(Long idOrg) {
        return idOfOrg.equals(idOrg);
    }

    public Boolean getVariableFeeding() {
        return variableFeeding;
    }

    public Boolean getWorkInSummerTime() {
        return isWorkInSummerTime;
    }

    public void setWorkInSummerTime(Boolean workInSummerTime) {
        isWorkInSummerTime = workInSummerTime;
    }

    public Boolean getUsePaydableSubscriptionFeeding() {
        return usePaydableSubscriptionFeeding;
    }

    public void setUsePaydableSubscriptionFeeding(Boolean usePaydableSubscriptionFeeding) {
        this.usePaydableSubscriptionFeeding = usePaydableSubscriptionFeeding;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public OrganizationType getOrganizationTypeInitial() {
        return organizationTypeInitial;
    }

    public void setOrganizationTypeInitial(OrganizationType organizationTypeInitial) {
        this.organizationTypeInitial = organizationTypeInitial;
    }

    public Boolean getRecyclingEnabled() {
        return isRecyclingEnabled;
    }

    public void setRecyclingEnabled(Boolean recyclingEnabled) {
        isRecyclingEnabled = recyclingEnabled;
    }

    public Boolean getAutoCreateCards() {
        return autoCreateCards;
    }

    public void setAutoCreateCards(Boolean autoCreateCards) {
        this.autoCreateCards = autoCreateCards;
    }

    public Boolean getNeedVirifyCardSign() {
        return needVirifyCardSign;
    }

    public void setNeedVirifyCardSign(Boolean needVirifyCardSign) {
        this.needVirifyCardSign = needVirifyCardSign;
    }

    public Boolean getDenyPayPlanForTimeDifference() {
        return denyPayPlanForTimeDifference;
    }

    public void setDenyPayPlanForTimeDifference(Boolean denyPayPlanForTimeDifference) {
        this.denyPayPlanForTimeDifference = denyPayPlanForTimeDifference;
    }

    public Boolean getAllowRegistryChangeEmployee() {
        return allowRegistryChangeEmployee;
    }

    public void setAllowRegistryChangeEmployee(Boolean allowRegistryChangeEmployee) {
        this.allowRegistryChangeEmployee = allowRegistryChangeEmployee;
    }

    public Boolean getHelpdeskEnabled() {
        return helpdeskEnabled;
    }

    public void setHelpdeskEnabled(Boolean helpdeskEnabled) {
        this.helpdeskEnabled = helpdeskEnabled;
    }

    public Boolean getRequestForVisitsToOtherOrg() {
        return requestForVisitsToOtherOrg;
    }

    public void setRequestForVisitsToOtherOrg(Boolean requestForVisitsToOtherOrg) {
        this.requestForVisitsToOtherOrg = requestForVisitsToOtherOrg;
    }

    public Boolean getPreordersEnabled() {
        return preordersEnabled;
    }

    public void setPreordersEnabled(Boolean preordersEnabled) {
        this.preordersEnabled = preordersEnabled;
    }

    public Boolean getMultiCardModeEnabled() {
        return multiCardModeEnabled;
    }

    public void setMultiCardModeEnabled(Boolean multiCardModeEnabled) {
        this.multiCardModeEnabled = multiCardModeEnabled;
    }

    public Boolean getDisableEditingClientsFromAISReestr() {
        return disableEditingClientsFromAISReestr;
    }

    public void setDisableEditingClientsFromAISReestr(Boolean disableEditingClientsFromAISReestr) {
        this.disableEditingClientsFromAISReestr = disableEditingClientsFromAISReestr;
    }

    public Boolean getParticipantOP() {
        return participantOP;
    }

    public void setParticipantOP(Boolean participantOP) {
        this.participantOP = participantOP;
    }

    public Boolean getPreorderlp() {
        return preorderlp;
    }

    public void setPreorderlp(Boolean preorderlp) {
        this.preorderlp = preorderlp;
    }

    public Long getEkisId() {
        return ekisId;
    }

    public void setEkisId(Long ekisId) {
        this.ekisId = ekisId;
    }

    public String getEgissoId() {
        return egissoId;
    }

    public void setEgissoId(String egissoId) {
        this.egissoId = egissoId;
    }

    public String getMunicipalDistrict() {
        return municipalDistrict;
    }

    public void setMunicipalDistrict(String municipalDistrict) {
        this.municipalDistrict = municipalDistrict;
    }

    public Boolean getUseWebArm() {
        return useWebArm;
    }

    public void setUseWebArm(Boolean useWebArm) {
        this.useWebArm = useWebArm;
    }

    public Boolean getUseWebArmAdmin() {
        return useWebArmAdmin;
    }

    public void setUseWebArmAdmin(Boolean useWebArmAdmin) {
        this.useWebArmAdmin = useWebArmAdmin;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getSubordination() {
        return subordination;
    }

    public void setSubordination(String subordination) {
        this.subordination = subordination;
    }

    public Long getOrgIdFromNsi() { return orgIdFromNsi; }

    public void setOrgIdFromNsi(Long orgIdFromNsi) { this.orgIdFromNsi = orgIdFromNsi; }

    public Boolean getGoodDateCheck() {
        return goodDateCheck;
    }

    public void setGoodDateCheck(Boolean goodDateCheck) {
        this.goodDateCheck = goodDateCheck;
    }

    public Boolean getGovernmentContract() {
        return governmentContract;
    }

    public void setGovernmentContract(Boolean governmentContract) {
        this.governmentContract = governmentContract;
    }

    public Boolean getUseLongCardNo() {
        return useLongCardNo;
    }

    public void setUseLongCardNo(Boolean useLongCardNo) {
        this.useLongCardNo = useLongCardNo;
    }

    public Boolean getUseMealSchedule() {
        return useMealSchedule;
    }

    public void setUseMealSchedule(Boolean useMealSchedule) {
        this.useMealSchedule = useMealSchedule;
    }

    public Boolean getNewСashierMode() {
        return newСashierMode;
    }

    public void setNewСashierMode(Boolean newСashierMode) {
        this.newСashierMode = newСashierMode;
    }
}