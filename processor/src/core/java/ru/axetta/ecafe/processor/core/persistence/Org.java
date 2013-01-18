/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class Org {

    public static final String[] STATE_NAMES = {"Не обслуживается", "Обслуживается"};
    public static final String UNKNOWN_STATE_NAME = "Неизвестное состояние";
    public static final String[] REFECTORY_TYPE_NAMES={"Сырьевая столовая", "Столовая-доготовочная", "Буфет-раздаточная"};

    private Long idOfOrg;
    private long version;
    private String shortName;
    private String officialName;
    private String address;
    private String phone;
    private Person officialPerson;
    private String officialPosition;
    private String contractId;
    private Date contractTime;
    private Integer state;
    private Long cardLimit;
    private String publicKey;
    private Long idOfPacket;
    private Long lastClientContractId;
    private String cypheredSsoPassword;
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private Contragent defaultSupplier;
    private Set<Client> clients = new HashSet<Client>();
    private Set<ClientGroup> clientGroups = new HashSet<ClientGroup>();
    private Set<SyncHistory> syncHistories = new HashSet<SyncHistory>();
    private Set<Order> orders = new HashSet<Order>();
    private Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();
    private Set<DiaryTimesheet> diaryTimesheets = new HashSet<DiaryTimesheet>();
    private Set<DiaryClass> diaryClasses = new HashSet<DiaryClass>();
    private Set<DiaryValue> diaryValues = new HashSet<DiaryValue>();
    private String OGRN;
    private String INN;
    private Set<CategoryOrg> categoriesInternal = new HashSet<CategoryOrg>();
    private Set<Org> sourceMenuOrgs;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private ConfigurationProvider configurationProvider;
    //private Org thisOrg;
    private String guid;
    private Contract contract;
    private Date lastSuccessfulBalanceSync;
    private Date lastUnSuccessfulBalanceSync;
    private Set<Org> friendlyOrg;
    private String tag;
    private String city; 
    private String district;
    private String location;
    private String longitude;
    private String latitude;
    private Integer refectoryType;
    private Set<Questionary> questionaries = new HashSet<Questionary>();
    private String clientVersion;
    private String remoteAddress;
    private Set<ClientMigration> clientMigration = new HashSet<ClientMigration>();

    public Set<ClientMigration> getClientMigration() {
        return clientMigration;
    }

    public void setClientMigration(Set<ClientMigration> clientMigration) {
        this.clientMigration = clientMigration;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    //private Org getThisOrg() {
    //    return thisOrg;
    //}
    //
    //private  void setThisOrg(Org thisOrg) {
    //    this.thisOrg = thisOrg;
    //}

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
    //private Set<EnterEvent> enterEvents;

    Org() {
        // For Hibernate only
    }

    /*
        private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    * */

    public Org(String shortName, String officialName, String address, Person officialPerson, String officialPosition,
            String contractId, Date contractTime, int state, long cardLimit, String publicKey, Long priceOfSms,
            Long subscriptionPrice, Contragent defaultSupplier, String INN, String ORGN, String mailingListReportsOnNutrition,
            String mailingListReportsOnVisits, String mailingListReports1, String mailingListReports2) throws Exception {
        this.shortName = shortName;
        this.officialName = officialName;
        this.address = address;
        this.officialPerson = officialPerson;
        this.officialPosition = officialPosition;
        this.contractId = contractId;
        this.contractTime = contractTime;
        this.state = state;
        this.cardLimit = cardLimit;
        this.publicKey = publicKey;
        this.idOfPacket = 0L;
        this.lastClientContractId = 0L;
        this.priceOfSms = priceOfSms;
        this.subscriptionPrice = subscriptionPrice;
        this.defaultSupplier = defaultSupplier;
        this.OGRN=ORGN;
        this.INN=INN;
        this.mailingListReportsOnNutrition = mailingListReportsOnNutrition;
        this.mailingListReportsOnVisits = mailingListReportsOnVisits;
        this.mailingListReports1 = mailingListReports1;
        this.mailingListReports2 = mailingListReports2;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
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

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public void setIdOfPacket(Long idOfPacket) {
        this.idOfPacket = idOfPacket;
    }

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

    public Date getLastSuccessfulBalanceSync() {
        return lastSuccessfulBalanceSync;
    }

    public void setLastSuccessfulBalanceSync(Date lastSuccessfulBalanceSync) {
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
    }

    public Date getLastUnSuccessfulBalanceSync() {
        return lastUnSuccessfulBalanceSync;
    }

    public void setLastUnSuccessfulBalanceSync(Date lastUnSuccessfulBalanceSync) {
        this.lastUnSuccessfulBalanceSync = lastUnSuccessfulBalanceSync;
    }

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
        return "Org{" + "idOfOrg=" + idOfOrg + ", version=" + version + ", shortName='" + shortName + '\''
                + ", officialName='" + officialName + '\'' + ", address='" + address + '\'' + ", phone='" + phone + '\''
                + ", officialPerson=" + officialPerson + ", officialPosition='" + officialPosition + '\''
                + ", contractId='" + contractId + '\'' + ", contractTime=" + contractTime + ", state=" + state
                + ", cardLimit=" + cardLimit + ", publicKey='" + publicKey + '\'' + ", idOfPacket=" + idOfPacket
                + ", lastClientContractId=" + lastClientContractId + ", cypheredSsoPassword='" + cypheredSsoPassword
                + '\'' + ", smsSender='" + smsSender + '\'' + ", priceOfSms=" + priceOfSms + ", subscriptionPrice="
                + subscriptionPrice + ", defaultSupplier=" + defaultSupplier +'}';
    }

    private static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }
}