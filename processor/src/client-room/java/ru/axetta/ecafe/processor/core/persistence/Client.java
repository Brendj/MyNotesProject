/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
//import org.hibernate.Criteria;
//import org.hibernate.Session;
//import org.hibernate.criterion.Restrictions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    public static final String[] PAY_FOR_SMS_STATE_NAMES = {"Бесплатное", "Платное"};
    public static final String UNKNOWN_PAY_FOR_SMS_STATE_NAME = "Неизвестно";
    public static final String[] CONTRACT_STATE_NAMES = {
            "Не заключен", "Активен", "Расторгнут по времени", "Расторгнут по желанию клиента"};
    public static final int ACTIVE_CONTRACT_STATE = 1;
    public static final int CONTRACT_INIT_STATE_COUNT = 2;
    public static final String UNKNOWN_CONTRACT_STATE_NAME = "Статус неизвестен";
    public static final int DISCOUNT_MODE_NONE = 0;
    public static final int INITIAL_DISCOUNT_MODE = DISCOUNT_MODE_NONE;
    public static final int DISCOUNT_MODE_BY_CATEGORY = 3;
    public static final String[] DISCOUNT_MODE_NAMES = {"Отсутствует", "Дотация", "Бесплатно", "Льгота по категориям"};

    private Long idOfClient;
    private long version;
    //private Org org;
    private Long idOfClientGroup;
    private Long clientRegistryVersion;
    //private ClientGroup clientGroup;
    private ru.axetta.ecafe.processor.core.persistence.Person person;
    private ru.axetta.ecafe.processor.core.persistence.Person contractPerson;
    private Integer flags;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private boolean notifyViaEmail;
    private boolean notifyViaSMS;
    //private Blob image = Hibernate.createBlob(new byte[]{});
    private String remarks;
    private Date updateTime;
    private Long contractId;
    private Date contractTime;
    private Integer contractState;
    private String cypheredPassword;
    private Integer payForSMS;
    private Integer freePayMaxCount;
    private Integer freePayCount;
    private Date lastFreePayTime;
    private Integer discountMode;
    private Long balance;
    private Long limit;
    private Long expenditureLimit;
    private String categoriesDiscounts;
    private String san;
    private Long externalId;
    private String clientGUID;
    private Set<Card> cards = new HashSet<Card>();
    //private Set<Call> calls = new HashSet<Call>();
    //private Set<Notification> notifications = new HashSet<Notification>();
    private Set<ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder> clientPaymentOrders = new HashSet<ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder>();
    //private Set<DiaryValue> diaryValues = new HashSet<DiaryValue>();
    //private Set<Order> orders = new HashSet<Order>();
    private Set<ru.axetta.ecafe.processor.core.persistence.ClientSms> clientSms = new HashSet<ru.axetta.ecafe.processor.core.persistence.ClientSms>();
    //private Set<ContragentClientAccount> contragentClientAccounts = new HashSet<ContragentClientAccount>();
    //private Set<AccountTransaction> transactions = new HashSet<AccountTransaction>();
    //private Set<Circulation> circulations = new HashSet<Circulation>();
    private Set<EnterEvent> enterEvents = new HashSet<ru.axetta.ecafe.processor.core.persistence.EnterEvent>();
    private Set<CategoryDiscount> categoriesInternal = new HashSet<ru.axetta.ecafe.processor.core.persistence.CategoryDiscount>();

    public Set<ru.axetta.ecafe.processor.core.persistence.CategoryDiscount> getCategories(){
        return getCategoriesInternal();
    }

    public void setCategories(Set<ru.axetta.ecafe.processor.core.persistence.CategoryDiscount> categories) {
        this.categoriesInternal = categories;
    }

    private Set<ru.axetta.ecafe.processor.core.persistence.CategoryDiscount> getCategoriesInternal() {
        return categoriesInternal;
    }

    private void setCategoriesInternal(Set<ru.axetta.ecafe.processor.core.persistence.CategoryDiscount> categoriesInternal) {
        this.categoriesInternal = categoriesInternal;
    }

    public Client() {
        // For Hibernate only
    }

    public Client(Person person, Person contractPerson, int flags, boolean notifyViaEmail,
            boolean notifyViaSMS, long contractId, Date contractTime, int contractState, String plainPassword,
            int payForSMS, long clientRegistryVersion, long limit, long expenditureLimit, String categoriesDiscounts)
            throws Exception {
        this.person = person;
        this.contractPerson = contractPerson;
        this.flags = flags;
        this.notifyViaEmail = notifyViaEmail;
        this.notifyViaSMS = notifyViaSMS;
        this.updateTime = new Date();
        this.contractId = contractId;
        this.contractTime = contractTime;
        this.contractState = contractState;
        this.cypheredPassword = encryptPassword(plainPassword);
        this.payForSMS = payForSMS;
        this.freePayCount = 0;
        this.discountMode = INITIAL_DISCOUNT_MODE;
        this.clientRegistryVersion = clientRegistryVersion;
        this.balance = 0L;
        this.limit = limit;
        this.expenditureLimit = expenditureLimit;
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }
    public void setSanWithConvert(String san) {
        if (san!=null) {
            san = san.replaceAll("[\\s-]", "");
        }
        this.san = san;
    }

    public Long getBalance() {
        return balance;
    }

    // Обновление баланса только через ClientAccountManager!
    public void addBalanceNotForSave(Long sum) {
        setBalance(getBalance() + sum);
    }

    private void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getExpenditureLimit() {
        return expenditureLimit;
    }

    public void setExpenditureLimit(Long expenditureLimit) {
        this.expenditureLimit = expenditureLimit;
    }

    public String getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(String categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    private void setIdOfClient(Long idOfClient) {
        // For Hibernate only
        this.idOfClient = idOfClient;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    //public Org getOrg() {
    //    return org;
    //}
    //
    //public void setOrg(Org org) {
    //    this.org = org;
    //}

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getClientRegistryVersion() {
        return clientRegistryVersion;
    }

    public void setClientRegistryVersion(Long clientRegistryVersion) {
        this.clientRegistryVersion = clientRegistryVersion;
    }

    //public ClientGroup getClientGroup() {
    //    return clientGroup;
    //}
    //
    //private void setClientGroup(ClientGroup clientGroup) {
    //    // For Hibernate only
    //    this.clientGroup = clientGroup;
    //}

    public ru.axetta.ecafe.processor.core.persistence.Person getPerson() {
        return person;
    }

    public void setPerson(ru.axetta.ecafe.processor.core.persistence.Person person) {
        this.person = person;
    }

    public ru.axetta.ecafe.processor.core.persistence.Person getContractPerson() {
        return contractPerson;
    }

    public void setContractPerson(ru.axetta.ecafe.processor.core.persistence.Person contractPerson) {
        this.contractPerson = contractPerson;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isNotifyViaEmail() {
        return notifyViaEmail;
    }

    public void setNotifyViaEmail(boolean notifyViaEmail) {
        this.notifyViaEmail = notifyViaEmail;
    }

    public boolean isNotifyViaSMS() {
        return notifyViaSMS;
    }

    public void setNotifyViaSMS(boolean notifyViaSMS) {
        this.notifyViaSMS = notifyViaSMS;
    }

    /*public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }*/

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Integer getContractState() {
        return contractState;
    }

    public void setContractState(Integer contractState) {
        this.contractState = contractState;
    }

    private String getCypheredPassword() {
        // For Hibernate only
        return cypheredPassword;
    }

    private void setCypheredPassword(String cypheredPassword) {
        // For Hibernate only
        this.cypheredPassword = cypheredPassword;
    }

    public Integer getPayForSMS() {
        return payForSMS;
    }

    public void setPayForSMS(Integer payForSMS) {
        this.payForSMS = payForSMS;
    }

    public Integer getFreePayMaxCount() {
        return freePayMaxCount;
    }

    public void setFreePayMaxCount(Integer freePayMaxCount) {
        this.freePayMaxCount = freePayMaxCount;
    }

    public Integer getFreePayCount() {
        return freePayCount;
    }

    public void setFreePayCount(Integer freePayCount) {
        this.freePayCount = freePayCount;
    }

    public Date getLastFreePayTime() {
        return lastFreePayTime;
    }

    public void setLastFreePayTime(Date lastFreePayTime) {
        this.lastFreePayTime = lastFreePayTime;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(Integer discountMode) {
        this.discountMode = discountMode;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getClientGUID() {
        return clientGUID;
    }

    public void setClientGUID(String clientGUID) {
        this.clientGUID = clientGUID;
    }

    private Set<Card> getCardsInternal() {
        // For Hibernate only
        return cards;
    }

    private void setCardsInternal(Set<Card> cards) {
        // For Hibernate only
        this.cards = cards;
    }

    public Set<Card> getCards() {
        return Collections.unmodifiableSet(getCardsInternal());
    }

    //private Set<Notification> getNotificationsInternal() {
    //    // For Hibernate only
    //    return notifications;
    //}
    //
    //private void setNotificationsInternal(Set<Notification> notifications) {
    //    // For Hibernate only
    //    this.notifications = notifications;
    //}
    //
    //public Set<Notification> getNotifications() {
    //    return Collections.unmodifiableSet(getNotificationsInternal());
    //}
    //
    //private Set<Call> getCallsInternal() {
    //    // For Hibernate only
    //    return calls;
    //}
    //
    //private void setCallsInternal(Set<Call> calls) {
    //    // For Hibernate only
    //    this.calls = calls;
    //}
    //
    //public Set<Call> getCalls() {
    //    return Collections.unmodifiableSet(getCallsInternal());
    //}

    private Set<ClientPaymentOrder> getClientPaymentOrdersInternal() {
        // For Hibernate only
        return clientPaymentOrders;
    }

    private void setClientPaymentOrdersInternal(Set<ClientPaymentOrder> clientPaymentOrders) {
        // For Hibernate only
        this.clientPaymentOrders = clientPaymentOrders;
    }

    public Set<ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder> getClientPaymentOrders() {
        return Collections.unmodifiableSet(getClientPaymentOrdersInternal());
    }

    public void setPassword(String plainPassword) throws Exception {
        this.cypheredPassword = encryptPassword(plainPassword);
    }

    public boolean hasPassword(String plainPassword) throws Exception {
        return StringUtils.equals(this.cypheredPassword, encryptPassword(plainPassword));
    }
    public boolean hasEncryptedPassword(String encryptedPassword) throws Exception {
        return StringUtils.equals(this.cypheredPassword, encryptedPassword);
    }

    //private Set<DiaryValue> getDiaryValuesInternal() {
    //    // For Hibernate only
    //    return diaryValues;
    //}
    //
    //private void setDiaryValuesInternal(Set<DiaryValue> diaryValues) {
    //    // For Hibernate only
    //    this.diaryValues = diaryValues;
    //}
    //
    //public Set<DiaryValue> getDiaryValues() {
    //    return Collections.unmodifiableSet(getDiaryValuesInternal());
    //}
    //
    //public Set<ContragentClientAccount> getContragentClientAccounts() {
    //    return Collections.unmodifiableSet(getContragentClientAccountsInternal());
    //}
    //
    //private Set<ContragentClientAccount> getContragentClientAccountsInternal() {
    //    return contragentClientAccounts;
    //}
    //
    //private void setContragentClientAccountsInternal(Set<ContragentClientAccount> contragentClientAccounts) {
    //    this.contragentClientAccounts = contragentClientAccounts;
    //}
    //
    //public Set<Order> getOrders() {
    //    return Collections.unmodifiableSet(getOrdersInternal());
    //}

    //private Set<Order> getOrdersInternal() {
    //    // For Hibernate only
    //    return orders;
    //}
    //
    //private void setOrdersInternal(Set<Order> orders) {
    //    // For Hibernate only
    //    this.orders = orders;
    //}

    public Set<ru.axetta.ecafe.processor.core.persistence.ClientSms> getClientSms() {
        return Collections.unmodifiableSet(getClientSmsInternal());
    }

    private Set<ru.axetta.ecafe.processor.core.persistence.ClientSms> getClientSmsInternal() {
        // For Hibernate only
        return clientSms;
    }

    private void setClientSmsInternal(Set<ru.axetta.ecafe.processor.core.persistence.ClientSms> clientSms) {
        // For Hibernate only
        this.clientSms = clientSms;
    }

    //private Set<AccountTransaction> getTransactionsInternal() {
    //    // For Hibernate only
    //    return transactions;
    //}
    //
    //private void setTransactionsInternal(Set<AccountTransaction> accountTransactions) {
    //    // For Hibernate only
    //    this.transactions = accountTransactions;
    //}
    //
    //public Set<AccountTransaction> getTransactions() {
    //    return Collections.unmodifiableSet(getTransactionsInternal());
    //}

    private Set<ru.axetta.ecafe.processor.core.persistence.EnterEvent> getEnterEventsInternal() {
        // For Hibernate only
        return enterEvents;
    }

    private void setEnterEventsInternal(Set<ru.axetta.ecafe.processor.core.persistence.EnterEvent> enterEvents) {
        // For Hibernate only
        this.enterEvents = enterEvents;
    }

    public Set<ru.axetta.ecafe.processor.core.persistence.EnterEvent> getEnterEvents() {
        return Collections.unmodifiableSet(getEnterEventsInternal());
    }

    /*public Card findActiveCard(Session session, Card failCard) throws Exception {
        // Ищем активную карту
        Criteria activeClientCardCriteria = session.createCriteria(Card.class);
        activeClientCardCriteria.add(Restrictions.eq("client", this));
        activeClientCardCriteria.add(Restrictions.eq("state", Card.ACTIVE_STATE));
        List paymentApproachingCards = activeClientCardCriteria.list();
        if (!paymentApproachingCards.isEmpty()) {
            return (Card) paymentApproachingCards.iterator().next();
        }
        // Если задана приорететная карта, то ее и возвращаем
        if (null != failCard) {
            return failCard;
        }
        // Берем любую карту
        Set<Card> clientCards = getCards();
        if (!clientCards.isEmpty()) {
            return clientCards.iterator().next();
        }
        return null;
    }*/

    //private Set<Circulation> getCirculationsInternal() {
    //    // For Hibernate only
    //    return circulations;
    //}
    //
    //private void setCirculationsInternal(Set<Circulation> cards) {
    //    // For Hibernate only
    //    this.circulations = circulations;
    //}
    //
    //public Set<Circulation> getCirculations() {
    //    return Collections.unmodifiableSet(getCirculationsInternal());
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        final Client client = (Client) o;
        if (!idOfClient.equals(client.getIdOfClient())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idOfClient.hashCode();
    }

    @Override
    public String toString() {
        return "Client{" + "idOfClient=" + idOfClient + ", version=" + version + ", idOfClientGroup="
                + idOfClientGroup + ", clientRegistryVersion=" + clientRegistryVersion
                + ", person=" + person + ", contractPerson=" + contractPerson + ", flags=" + flags + ", address='"
                + address + '\'' + ", phone='" + phone + '\'' + ", mobile='" + mobile + '\'' + ", email='" + email
                + '\'' + ", notifyViaEmail=" + notifyViaEmail + ", notifyViaSMS=" + notifyViaSMS
                + ", remarks='" + remarks + '\'' + ", updateTime=" + updateTime + ", contractId=" + contractId
                + ", contractTime=" + contractTime + ", contractState=" + contractState + ", cypheredPassword='"
                + cypheredPassword + '\'' + ", payForSMS=" + payForSMS + ", freePayMaxCount=" + freePayMaxCount
                + ", freePayCount=" + freePayCount + ", lastFreePayTime=" + lastFreePayTime + ", discountMode="
                + discountMode + ", balance=" + balance + ", limit=" + limit + ", expenditureLimit="
                + expenditureLimit + ", categoriesDiscounts=" + categoriesDiscounts +'}';
    }

    public static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }

    //public String getClientGroupTypeAsString() {
    //    long idOfClientGroup = getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
    //    if(idOfClientGroup>=ClientGroup.PREDEFINED_ID_OF_GROUP_OTHER){
    //        return "Другое";
    //    } else if(idOfClientGroup>=ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES) {
    //        return "Сотрудники";
    //    } else {
    //        return "Ученик";
    //    }
    //}

    public static String checkAndConvertMobile(String mobilePhone) {
        if (mobilePhone==null || mobilePhone.length()==0) return mobilePhone;
        mobilePhone=mobilePhone.replaceAll("[+ -()]", "");
        if (mobilePhone.startsWith("8")) mobilePhone="7"+mobilePhone.substring(1);
        if (mobilePhone.length()==10) mobilePhone="7"+mobilePhone;
        else if (mobilePhone.length()!=11) return null;
        return mobilePhone;
    }

    public boolean hasIntegraPartnerAccessPermission(String id) {
        return getRemarks()!=null && getRemarks().contains("{integra.access:"+id+"}");
    }
    public void addIntegraPartnerAccessPermission(String id) {
        if (!hasIntegraPartnerAccessPermission(id)) {
            String r = getRemarks();
            String accessMarker = "{integra.access:"+id+"}";
            if (r==null) r=accessMarker;
            else r+="\n"+accessMarker;
            setRemarks(r);
        }
    }
}