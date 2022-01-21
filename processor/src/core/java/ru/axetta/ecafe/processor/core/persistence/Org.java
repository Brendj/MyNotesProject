/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.service.CommonTaskService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostUpdate;
import javax.persistence.Version;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class Org implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Org.class);
    public static final String[] STATE_NAMES = {"Не обслуживается", "Обслуживается"};
    public static final int INACTIVE_STATE = 0;
    public static final int ACTIVE_STATE = 1;
    public static final String UNKNOWN_STATE_NAME = "Неизвестное состояние";
    public static final String[] REFECTORY_TYPE_NAMES = {
            "Сырьевая столовая", "Столовая-доготовочная", "Буфет-раздаточная", "Комбинат питания"};

    public static final int REFECTORY_TYPE_FOOD_FACTORY = 3;

    private Long idOfOrg;
    private long version;
    private Long orgStructureVersion;
    private String shortName;
    private String shortNameInfoService;
    private String officialName;
    private String address;
    private String shortAddress;
    private String phone;
    private Person officialPerson;
    private String officialPosition;
    private String contractId;
    private Date contractTime;
    private Integer state;
    private Long cardLimit;
    private String publicKey;
    //@Deprecated
    //private Long idOfPacket;
    private Long lastClientContractId;
    private String cypheredSsoPassword;
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private Contragent defaultSupplier;
    private Contragent coSupplier;
    private Set<Client> clients = new HashSet<Client>();
    private Set<ClientGroup> clientGroups = new HashSet<ClientGroup>();
    private Set<SyncHistory> syncHistories = new HashSet<SyncHistory>();
    private Set<Order> orders = new HashSet<Order>();
    private Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();
    private Set<DiaryTimesheet> diaryTimesheets = new HashSet<DiaryTimesheet>();
    private Set<DiaryClass> diaryClasses = new HashSet<DiaryClass>();
    private Set<DiaryValue> diaryValues = new HashSet<DiaryValue>();
    private Set<OrgSetting> orgSettings = new HashSet<OrgSetting>();
    private String OGRN;
    private String INN;
    private Set<CategoryOrg> categoriesInternal = new HashSet<CategoryOrg>();
    private Set<Org> sourceMenuOrgs;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private ConfigurationProvider configurationProvider;
    private String guid;
    private Contract contract;
    //@Deprecated
    //private Date lastSuccessfulBalanceSync;
    //@Deprecated
    //private Date lastUnSuccessfulBalanceSync;
    private Set<Org> friendlyOrg;
    private Boolean mainBuilding;
    private String tag;
    private String city; 
    private String district;
    private String location;
    private String longitude;
    private String latitude;
    private Integer refectoryType;
    private Set<Questionary> questionaries = new HashSet<Questionary>();
    //@Deprecated
    //private String clientVersion;
    //@Deprecated
    //private String remoteAddress;
    private Set<ClientMigration> clientMigration = new HashSet<ClientMigration>();
    private Boolean fullSyncParam;
    private Boolean menusSyncParam;
    private Boolean orgSettingsSyncParam;
    private Boolean clientsSyncParam;
    private Boolean usePlanOrders;
    private Boolean commodityAccounting;
    private Boolean disableEditingClientsFromAISReestr;
    private Boolean usePaydableSubscriptionFeeding;
    private Boolean variableFeeding;
    // тип организации "Школа / ДОУ / Поставщик питания"
    private OrganizationType type;
    private OrganizationType typeInitial;
    private OrganizationStatus status;
    private OrganizationSecurityLevel securityLevel;
    private PhotoRegistryDirective photoRegistryDirective;
    private TradeAccountConfigChange tradeAccountConfigChangeDirective;
    private Long btiUnom;
    private Long btiUnad;
    private Long uniqueAddressId; //поле unique_address_id из сверки по реестрам
    private String introductionQueue;
    private Long additionalIdBuilding;
    private String statusDetailing;
    //private OrgSync orgSync;
    private OrgContractId orgContractId;
    private Boolean payByCashier;
    private Boolean oneActiveCard;
    private Boolean changesDSZN; // Получение из реестров льгот ДСЗН
    private Set<Card> cards = new HashSet<Card>();
    private Date updateTime;
    private String registryUrl;

    private String interdistrictCouncil; //В каком межрайонном совете состоит ОО
    private String interdistrictCouncilChief ; //Председателем какого межрайонного совета является руководитель ОО
    private Boolean isWorkInSummerTime;
    private Boolean isRecyclingEnabled;
    private Boolean autoCreateCards;
    private Boolean needVerifyCardSign;
    private Boolean denyPayPlanForTimeDifference;
    private Boolean allowRegistryChangeEmployee;
    private Boolean helpdeskEnabled;
    private Boolean requestForVisitsToOtherOrg;
    private Boolean preordersEnabled;
    private Boolean multiCardModeEnabled;
    private Boolean participantOP;
    private Boolean preorderlp;
    private Boolean haveNewLP;
    private Long ekisId;
    private Boolean preorderSyncParam;
    private Boolean useWebArm;
    private Boolean useWebArmAdmin;
    private String egissoId;
    private String municipalDistrict;
    private String founder;
    private String subordination;
    private Long orgIdFromNsi;
    private Boolean gooddatecheck;
    private Boolean governmentContract;
    private Boolean useLongCardNo;

    /*@PostUpdate
    public void sendInvalidateCache() {
        logger.info("Send invalidate org id = " + idOfOrg);
        RuntimeContext.getAppContext().getBean(CommonTaskService.class).invalidateOrgMulticast(idOfOrg);
    }*/

    public static void sendInvalidateCache(long idOfOrg) {
        /*logger.info("Send invalidate org id = " + idOfOrg);
        RuntimeContext.getAppContext().getBean(CommonTaskService.class).invalidateOrgMulticast(idOfOrg);*/
    }

    public Org(String shortName, String shortNameInfoService, String officialName, String address, String shortAddress, Person officialPerson, String officialPosition,
            String contractId, Date contractTime, OrganizationType type, int state, long cardLimit, String publicKey, Long priceOfSms,
            Long subscriptionPrice, Contragent defaultSupplier, String INN, String OGRN, String mailingListReportsOnNutrition,
            String mailingListReportsOnVisits, String mailingListReports1, String mailingListReports2,
            Long btiUnom, Long btiUnad, Long uniqueAddressId, String introductionQueue, Long additionalIdBuilding, String statusDetailing, Long orgStructureVersion,
            Boolean changesDSZN) throws Exception {
        this.shortName = shortName;
        this.shortNameInfoService = shortNameInfoService;
        this.officialName = officialName;
        this.address = address;
        this.shortAddress = shortAddress;
        this.officialPerson = officialPerson;
        this.officialPosition = officialPosition;
        this.contractId = contractId;
        this.contractTime = contractTime;
        this.state = state;
        this.cardLimit = cardLimit;
        this.publicKey = publicKey;
        //this.idOfPacket = 0L;
        this.lastClientContractId = 0L;
        this.priceOfSms = priceOfSms;
        this.subscriptionPrice = subscriptionPrice;
        this.defaultSupplier = defaultSupplier;
        this.OGRN=OGRN;
        this.INN=INN;
        this.mailingListReportsOnNutrition = mailingListReportsOnNutrition;
        this.mailingListReportsOnVisits = mailingListReportsOnVisits;
        this.mailingListReports1 = mailingListReports1;
        this.mailingListReports2 = mailingListReports2;
        this.fullSyncParam = false;
        this.menusSyncParam = false;
        this.clientsSyncParam = false;
        this.orgSettingsSyncParam = false;
        this.commodityAccounting=false;
        this.usePlanOrders = true;  // плана питания включен по умолчаню
        this.disableEditingClientsFromAISReestr = false;
        this.usePaydableSubscriptionFeeding = false;
        this.type = type;
        this.status = OrganizationStatus.ACTIVE;
        this.btiUnom = btiUnom;
        this.btiUnad = btiUnad;
        this.uniqueAddressId = uniqueAddressId;
        this.introductionQueue = introductionQueue;
        this.additionalIdBuilding = additionalIdBuilding;
        this.statusDetailing = statusDetailing;

        this.orgContractId = new OrgContractId();
        this.orgContractId.setLastClientContractId(1L);
        this.orgContractId.setOrg(this);
        updateTime = new Date();
        this.orgStructureVersion = orgStructureVersion;
        this.tradeAccountConfigChangeDirective = TradeAccountConfigChange.NOT_CHANGED;
        this.changesDSZN = changesDSZN;
        this.variableFeeding = false;
        this.isWorkInSummerTime = false;
        this.isRecyclingEnabled = false;
        this.autoCreateCards = false;
        this.needVerifyCardSign = false;
        this.denyPayPlanForTimeDifference = false;
        this.allowRegistryChangeEmployee = false;
        this.typeInitial = OrganizationType.SCHOOL;
        this.helpdeskEnabled = false;
        this.requestForVisitsToOtherOrg = false;
        this.preordersEnabled = false;
        this.useWebArm = false;
        this.useWebArmAdmin = false;
        this.governmentContract = false;
        this.useLongCardNo = false;
    }

    static Pattern patterNumber = Pattern.compile("\\d+");
    public String getOrgNumberInName() {
        return extractOrgNumberFromName(shortName);
    }
    public static String extractOrgNumberFromName(String name) {
        Matcher m = patterNumber.matcher(name);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    public String getOrgNumberFromNameInfoService() {
        return extractOrgNumberFromName(shortNameInfoService);
    }

    private static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }

    public Boolean getCommodityAccounting() {
        return commodityAccounting;
    }

    public void setCommodityAccounting(Boolean commodityAccounting) {
        this.commodityAccounting = commodityAccounting;
    }

    public Boolean getFullSyncParam() {
        return fullSyncParam;
    }

    public void setFullSyncParam(Boolean fullSyncParam) {
        this.fullSyncParam = fullSyncParam;
    }

    public Boolean getMenusSyncParam() {
        return menusSyncParam;
    }

    public void setMenusSyncParam(Boolean menusSyncParam) {
        this.menusSyncParam = menusSyncParam;
    }

    public Boolean getOrgSettingsSyncParam() {
        return orgSettingsSyncParam;
    }

    public void setOrgSettingsSyncParam(Boolean orgSettingsSyncParam) {
        this.orgSettingsSyncParam = orgSettingsSyncParam;
    }

    public Boolean getClientsSyncParam() {
        return clientsSyncParam;
    }

    public void setClientsSyncParam(Boolean clientsSyncParam) {
        this.clientsSyncParam = clientsSyncParam;
    }

    public Boolean getUsePlanOrders() {
        return usePlanOrders;
    }

    public void setUsePlanOrders(Boolean payPlanParam) {
        this.usePlanOrders = payPlanParam;
    }

    public Boolean getDisableEditingClientsFromAISReestr() {
        return disableEditingClientsFromAISReestr;
    }

    public void setDisableEditingClientsFromAISReestr(Boolean disableEditingClientsFromAISReestr) {
        this.disableEditingClientsFromAISReestr = disableEditingClientsFromAISReestr;
    }

    public Boolean getUsePaydableSubscriptionFeeding() {
        return usePaydableSubscriptionFeeding;
    }

    public void setUsePaydableSubscriptionFeeding(Boolean usePaydableSubscriptionFeeding) {
        this.usePaydableSubscriptionFeeding = usePaydableSubscriptionFeeding;
    }

    public Set<ClientMigration> getClientMigration() {
        return clientMigration;
    }

    public void setClientMigration(Set<ClientMigration> clientMigration) {
        this.clientMigration = clientMigration;
    }
    //@Deprecated
    //public String getRemoteAddress() {
    //    return remoteAddress;
    //}
    //@Deprecated
    //public void setRemoteAddress(String remoteAddress) {
    //    this.remoteAddress = remoteAddress;
    //}
    //@Deprecated
    //public String getClientVersion() {
    //    return clientVersion;
    //}
    //@Deprecated
    //public void setClientVersion(String clientVersion) {
    //    this.clientVersion = clientVersion;
    //}

    public Set<Questionary> getQuestionaries() {
        return questionaries;
    }

    public void setQuestionaries(Set<Questionary> questionaries) {
        this.questionaries = questionaries;
    }

    public Integer getRefectoryType() {
        return refectoryType;
    }

    public void setRefectoryType(Integer refectoryType) {
        this.refectoryType = refectoryType;
    }

    public Set<Org> getFriendlyOrg() {
        return friendlyOrg;
    }

    public void setFriendlyOrg(Set<Org> friendlyOrg) {
        this.friendlyOrg = friendlyOrg;
    }

    public Boolean isMainBuilding() {
        return mainBuilding == null? false : mainBuilding;
    }

    public void setMainBuilding(Boolean mainBuilding) {
        this.mainBuilding = mainBuilding;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }


    public Set<CategoryOrg> getCategories(){
        return getCategoriesInternal();
        //return Collections.unmodifiableSet(getCategoriesInternal());
    }

    private Set<CategoryOrg> getCategoriesInternal() {
        return categoriesInternal;
    }

    public void setCategoriesInternal(Set<CategoryOrg> categoriesInternal) {
        this.categoriesInternal = categoriesInternal;
    }

    public Set<Org> getSourceMenuOrgs() {
        return sourceMenuOrgs;
    }

    public void setSourceMenuOrgs(Set<Org> sourceMenuOrgs) {
        this.sourceMenuOrgs = sourceMenuOrgs;
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

    protected Org() {
        // For Hibernate only
    }

    /*
        private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    * */

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    protected void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    @Version
    public long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public Long getOrgStructureVersion() {
        return orgStructureVersion;
    }

    public void setOrgStructureVersion(Long orgStructureVersion) {
        this.orgStructureVersion = orgStructureVersion;
    }

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Long subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Person getOfficialPerson() {
        return officialPerson;
    }

    public void setOfficialPerson(Person officialPerson) {
        this.officialPerson = officialPerson;
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
        // For Hibernate only
        this.contractTime = contractTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        if (null != state && state >= STATE_NAMES.length) {
            this.state = 0;
        } else {
            this.state = state;
        }
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

    //@Deprecated
    //public Long getIdOfPacket() {
    //    return idOfPacket;
    //}
    //
    //@Deprecated
    //public void setIdOfPacket(Long idOfPacket) {
    //    this.idOfPacket = idOfPacket;
    //}

    public Long getLastClientContractId() {
        return lastClientContractId;
    }

    public void setLastClientContractId(Long lastClientContractId) {
        this.lastClientContractId = lastClientContractId;
    }

    public Contragent getDefaultSupplier() {
        return defaultSupplier;
    }

    public void setDefaultSupplier(Contragent defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    private Set<Client> getClientsInternal() {
        // For Hibernate only
        return clients;
    }

    private void setClientsInternal(Set<Client> clients) {
        // For Hibernate only
        this.clients = clients;
    }

    public Set<Client> getClients() {
        return Collections.unmodifiableSet(getClientsInternal());
    }

    private Set<ClientGroup> getClientGroupsInternal() {
        // For Hibernate only
        return clientGroups;
    }

    private void setClientGroupsInternal(Set<ClientGroup> clientGroups) {
        // For Hibernate only
        this.clientGroups = clientGroups;
    }

    public Set<ClientGroup> getClientGroups() {
        return Collections.unmodifiableSet(getClientGroupsInternal());
    }

    void addClientGroup(ClientGroup clientGroup) {
        getClientGroupsInternal().add(clientGroup);
    }

    public void removeClientGroup(ClientGroup clientGroup) {
        getClientGroupsInternal().remove(clientGroup);
    }

    public void setSsoPassword(String plainSsoPassword) throws Exception {
        this.cypheredSsoPassword = encryptPassword(plainSsoPassword);
    }

    public boolean hasSsoPassword(String plainSsoPassword) throws Exception {
        if (StringUtils.isEmpty(cypheredSsoPassword) && StringUtils.isEmpty(plainSsoPassword)) {
            return true;
        }
        return StringUtils.equals(this.cypheredSsoPassword, encryptPassword(plainSsoPassword));
    }

    private String getCypheredSsoPassword() {
        // For Hibernate
        return cypheredSsoPassword;
    }

    private void setCypheredSsoPassword(String cypheredSsoPassword) throws Exception {
        // For Hibernate
        this.cypheredSsoPassword = cypheredSsoPassword;
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
    //@Deprecated
    //public Date getLastSuccessfulBalanceSync() {
    //    return lastSuccessfulBalanceSync;
    //}
    //@Deprecated
    //public void setLastSuccessfulBalanceSync(Date lastSuccessfulBalanceSync) {
    //    this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
    //}
    //@Deprecated
    //public Date getLastUnSuccessfulBalanceSync() {
    //    return lastUnSuccessfulBalanceSync;
    //}
    //@Deprecated
    //public void setLastUnSuccessfulBalanceSync(Date lastUnSuccessfulBalanceSync) {
    //    this.lastUnSuccessfulBalanceSync = lastUnSuccessfulBalanceSync;
    //}

    public Set<SyncHistory> getSyncHistoriesInternal() {
        // For Hibernate only
        return syncHistories;
    }

    public void setSyncHistoriesInternal(Set<SyncHistory> syncHistories) {
        // For Hibernate only
        this.syncHistories = syncHistories;
    }

    private Set<Order> getOrdersInternal() {
        // For Hibernate only
        return orders;
    }

    private void setOrdersInternal(Set<Order> orders) {
        // For Hibernate only
        this.orders = orders;
    }

    public Set<Order> getOrders() {
        return Collections.unmodifiableSet(getOrdersInternal());
    }

    private Set<OrderDetail> getOrderDetailsInternal() {
        // For Hibernate only
        return orderDetails;
    }

    private void setOrderDetailsInternal(Set<OrderDetail> orderDetails) {
        // For Hibernate only
        this.orderDetails = orderDetails;
    }

    public Set<OrderDetail> getOrderDetails() {
        return Collections.unmodifiableSet(getOrderDetailsInternal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheetsInternal() {
        // For Hibernate only
        return diaryTimesheets;
    }

    private void setDiaryTimesheetsInternal(Set<DiaryTimesheet> diaryTimesheets) {
        // For Hibernate only
        this.diaryTimesheets = diaryTimesheets;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets() {
        return Collections.unmodifiableSet(getDiaryTimesheetsInternal());
    }

    public void addDiaryTimesheet(DiaryTimesheet diaryTimesheet) {
        getDiaryTimesheetsInternal().add(diaryTimesheet);
    }

    private Set<DiaryClass> getDiaryClassesInternal() {
        // For Hibernate only
        return diaryClasses;
    }

    private void setDiaryClassesInternal(Set<DiaryClass> diaryClasses) {
        // For Hibernate only
        this.diaryClasses = diaryClasses;
    }

    public Set<DiaryClass> getDiaryClasses() {
        return Collections.unmodifiableSet(getDiaryClassesInternal());
    }

    public void addDiaryClass(DiaryClass diaryClass) {
        getDiaryClassesInternal().add(diaryClass);
    }

    public void removeDiaryClass(DiaryClass diaryClass) {
        getDiaryClassesInternal().remove(diaryClass);
    }

    private Set<DiaryValue> getDiaryValuesInternal() {
        // For Hibernate only
        return diaryValues;
    }

    private void setDiaryValuesInternal(Set<DiaryValue> diaryValues) {
        // For Hibernate only
        this.diaryValues = diaryValues;
    }

    public Set<DiaryValue> getDiaryValues() {
        return Collections.unmodifiableSet(getDiaryValuesInternal());
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /*private Set<EnterEvent> getEnterEventsInternal() {
        // For Hibernate only
        return enterEvents;
    }

    private void setEnterEventsInternal(Set<EnterEvent> enterEvents) {
        // For Hibernate only
        this.enterEvents = enterEvents;
    }

    public Set<EnterEvent> getEnterEvents() {
        return Collections.unmodifiableSet(getEnterEventsInternal());
    }*/

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

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public OrganizationType getTypeInitial() {
        return typeInitial;
    }

    public void setTypeInitial(OrganizationType typeInitial) {
        this.typeInitial = typeInitial;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public Long getBtiUnom() {
        return btiUnom;
    }

    public void setBtiUnom(Long btiUnom) {
        this.btiUnom = btiUnom;
    }

    public Long getBtiUnad() {
        return btiUnad;
    }

    public void setBtiUnad(Long btiUnad) {
        this.btiUnad = btiUnad;
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

    public Long getAdditionalIdBuilding() {
        return additionalIdBuilding;
    }

    public void setAdditionalIdBuilding(Long additionalIdBuilding) {
        this.additionalIdBuilding = additionalIdBuilding;
    }

    public String getStatusDetailing() {
        return statusDetailing;
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
    }

    /*public OrgSync getOrgSync() {
        return orgSync;
    }

    public void setOrgSync(OrgSync orgSync) {
        this.orgSync = orgSync;
    }*/

    public Boolean getPayByCashier() {
        return payByCashier;
    }

    public void setPayByCashier(Boolean payByCashier) {
        this.payByCashier = payByCashier;
    }

    public Boolean getOneActiveCard() {
        return oneActiveCard;
    }

    public void setOneActiveCard(Boolean oneActiveCard) {
        this.oneActiveCard = oneActiveCard;
    }

    public Boolean getChangesDSZN() {
        return changesDSZN;
    }

    public void setChangesDSZN(Boolean changesDSZN) {
        this.changesDSZN = changesDSZN;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    public String getInterdistrictCouncil() {
        return interdistrictCouncil;
    }

    public void setInterdistrictCouncil(String interdistrictCouncil) {
        this.interdistrictCouncil = interdistrictCouncil;
    }

    public String getInterdistrictCouncilChief() {
        return interdistrictCouncilChief;
    }

    public void setInterdistrictCouncilChief(String interdistrictCouncilChief) {
        this.interdistrictCouncilChief = interdistrictCouncilChief;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Org)) {
            return false;
        }
        final Org org = (Org) o;
        return idOfOrg.equals(org.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return idOfOrg.hashCode();
    }

    @Override
    public String toString() {
        return "Org{" + "idOfOrg=" + idOfOrg + ", version=" + version + ", globalVersion=" + orgStructureVersion + ", shortName='" + shortName + '\''
                + ", shortNameInfoService='" + shortNameInfoService + '\''
                + ", officialName='" + officialName + '\'' + ", address='" + address + '\'' + ", shortAddress='" + shortAddress + '\'' + ", phone='" + phone + '\''
                + ", officialPerson=" + officialPerson + ", officialPosition='" + officialPosition + '\''
                + ", contractId='" + contractId + '\'' + ", contractTime=" + contractTime + ", state=" + state
                + ", cardLimit=" + cardLimit + ", publicKey='" + publicKey + '\''// + ", idOfPacket=" + idOfPacket
                + ", lastClientContractId=" + lastClientContractId + ", cypheredSsoPassword='" + cypheredSsoPassword
                + '\'' + ", smsSender='" + smsSender + '\'' + ", priceOfSms=" + priceOfSms + ", subscriptionPrice="
                + subscriptionPrice + ", defaultSupplier=" + defaultSupplier +", updateTime=" + updateTime+'}';
    }

    public OrganizationSecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(OrganizationSecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public PhotoRegistryDirective getPhotoRegistryDirective() {
        return photoRegistryDirective;
    }

    public void setPhotoRegistryDirective(PhotoRegistryDirective photoRegistryDirective) {
        this.photoRegistryDirective = photoRegistryDirective;
    }

    public TradeAccountConfigChange getTradeAccountConfigChangeDirective() {
        return tradeAccountConfigChangeDirective;
    }

    public void setTradeAccountConfigChangeDirective(TradeAccountConfigChange tradeAccountConfigChangeDirective) {
        this.tradeAccountConfigChangeDirective = tradeAccountConfigChangeDirective;
    }

    public Contragent getCoSupplier() {
        return coSupplier;
    }

    public void setCoSupplier(Contragent coSupplier) {
        this.coSupplier = coSupplier;
    }

    public OrgContractId getOrgContractId() {
        return orgContractId;
    }

    public void setOrgContractId(OrgContractId orgContractId) {
        this.orgContractId = orgContractId;
    }

    public Boolean getVariableFeeding() {
        return variableFeeding;
    }

    public void setVariableFeeding(Boolean variableFeeding) {
        this.variableFeeding = variableFeeding;
    }

    public Boolean getIsWorkInSummerTime() {
        return isWorkInSummerTime;
    }

    public void setIsWorkInSummerTime(Boolean workInSummerTime) {
        isWorkInSummerTime = workInSummerTime;
    }

    public Boolean getIsRecyclingEnabled() {
        return isRecyclingEnabled;
    }

    public void setIsRecyclingEnabled(Boolean recyclingEnabled) {
        isRecyclingEnabled = recyclingEnabled;
    }

    public Boolean getAutoCreateCards() {
        return autoCreateCards;
    }

    public void setAutoCreateCards(Boolean autoCreateCards) {
        this.autoCreateCards = autoCreateCards;
    }

    public Boolean getNeedVerifyCardSign() {
        return needVerifyCardSign;
    }

    public void setNeedVerifyCardSign(Boolean needVerifyCardSign) {
        this.needVerifyCardSign = needVerifyCardSign;
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

    public String stateString() {
        String result;
        if (this.state >= 0 && this.state < Org.STATE_NAMES.length) {
            result = Org.STATE_NAMES[this.state];
        } else {
            result = Org.UNKNOWN_STATE_NAME;
        }

        if (!this.statusDetailing.isEmpty() && !this.statusDetailing.equalsIgnoreCase("/"))
            result += " (" + this.statusDetailing + ")";
        return result;
    }

    public Boolean getMultiCardModeEnabled() {
        return multiCardModeEnabled;
    }

    public void setMultiCardModeEnabled(Boolean multiCardModeEnabled) {
        this.multiCardModeEnabled = multiCardModeEnabled;
    }

    public boolean multiCardModeIsEnabled(){
        if(this.multiCardModeEnabled == null){
            return false;
        } else {
            return this.multiCardModeEnabled;
        }
    }

    public Set<OrgSetting> getOrgSettings() {
        return orgSettings;
    }

    public void setOrgSettings(Set<OrgSetting> orgSettings) {
        this.orgSettings = orgSettings;
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

    public Boolean getHaveNewLP() {
        return haveNewLP;
    }

    public void setHaveNewLP(Boolean haveNewLP) {
        this.haveNewLP = haveNewLP;
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

    public Boolean getPreorderSyncParam() {
        return preorderSyncParam;
    }

    public void setPreorderSyncParam(Boolean preorderSyncParam) {
        this.preorderSyncParam = preorderSyncParam;
    }

    public Boolean getMainBuilding() {
        return mainBuilding;
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

    public Boolean getGooddatecheck() {
        if (gooddatecheck == null)
            return true;
        return gooddatecheck;
    }

    public void setGooddatecheck(Boolean gooddatecheck) {
        this.gooddatecheck = gooddatecheck;
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

    public boolean longCardNoIsOn() {
        return useLongCardNo != null && useLongCardNo;
    }
}