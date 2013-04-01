/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class Contragent {

    public static final int PAY_AGENT = 1;
    public static final int TSP = 2;
    public static final int OPERATOR = 3;
    public static final int BUDGET = 4;
    public static final int CLIENT = 5;

    public static final String[] CLASS_NAMES = {
            "Неизвестный", "Агент по приему платежей", "ТСП", "Оператор", "Бюджет", "Клиенты"};

    public static String getClassAsString(Integer classId) {
        if (classId >= 0 && classId < CLASS_NAMES.length) {
            return CLASS_NAMES[classId];
        }
        return CLASS_NAMES[0];
    }

    public static final String UNKNOWN_CLASS_NAME = "Неизвестный";

    private Long idOfContragent;
    private long version;
    private Person contactPerson;
    private Integer parentId;
    private String contragentName;
    private Integer classId;
    private Integer flags;
    private String title;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String fax;
    private String remarks;
    private String inn;
    private String bank;
    private String bic;
    private String corrAccount;
    private String account;
    private Date createTime;
    private Date updateTime;
    private String publicKey;
    private String publicKeyGOSTAlias;
    private Boolean needAccountTranslate;
    private Set<User> users = new HashSet<User>();
    private Set<ContragentPayment> contragentPayments = new HashSet<ContragentPayment>();
    private Set<ClientPayment> clientPayments = new HashSet<ClientPayment>();
    private Set<ClientPaymentOrder> clientPaymentOrders = new HashSet<ClientPaymentOrder>();
    private Set<ContragentClientAccount> contragentClientAccounts = new HashSet<ContragentClientAccount>();
    private Set<Settlement> payerSettlements = new HashSet<Settlement>();
    private Set<Settlement> receiverSettlements = new HashSet<Settlement>();
    private Set<CurrentPosition> debtorPositions = new HashSet<CurrentPosition>();
    private Set<CurrentPosition> creditorPositions = new HashSet<CurrentPosition>();
    private Set<POS> POSSet = new HashSet<POS>();
    private Set<Order> orders = new HashSet<Order>();
    private Set<Org> orgs = new HashSet<Org>();
    private Set<AddPayment> payerAddPayments = new HashSet<AddPayment>();
    private Set<AddPayment> receiverAddPayments = new HashSet<AddPayment>();
    private String kpp;
    private String ogrn;


    Contragent() {
        // For Hibernate only
    }

    public Contragent(Person contactPerson, String contragentName, int classId, int flags, String title, String address,
            Date createTime, Date updateTime, String publicKey, boolean needAccountTranslate) throws Exception {
        this.contactPerson = contactPerson;
        this.contragentName = contragentName;
        this.classId = classId;
        this.flags = flags;
        this.title = title;
        this.address = address;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.publicKey = publicKey;
        this.needAccountTranslate = needAccountTranslate;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    private void setIdOfContragent(Long idOfContragent) {
        // For Hibernate only
        this.idOfContragent = idOfContragent;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getCorrAccount() {
        return corrAccount;
    }

    public void setCorrAccount(String corrAccount) {
        this.corrAccount = corrAccount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    private Set<User> getUsersInternal() {
        // For Hibernate only
        return users;
    }

    private void setUsersInternal(Set<User> users) {
        // For Hibernate only
        this.users = users;
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(getUsersInternal());
    }

    private Set<ContragentPayment> getContragentPaymentsInternal() {
        // For Hibernate only
        return contragentPayments;
    }

    private void setContragentPaymentsInternal(Set<ContragentPayment> contragentPayments) {
        // For Hibernate only
        this.contragentPayments = contragentPayments;
    }

    public Set<ContragentPayment> getContragentPayments() {
        return Collections.unmodifiableSet(getContragentPaymentsInternal());
    }

    public Set<ClientPayment> getClientPaymentsInternal() {
        // For Hibernate only
        return clientPayments;
    }

    public void setClientPaymentsInternal(Set<ClientPayment> clientPayments) {
        // For Hibernate only
        this.clientPayments = clientPayments;
    }

    private Set<ClientPaymentOrder> getClientPaymentOrdersInternal() {
        // For Hibernate only
        return clientPaymentOrders;
    }

    private void setClientPaymentOrdersInternal(Set<ClientPaymentOrder> clientPaymentOrders) {
        // For Hibernate only
        this.clientPaymentOrders = clientPaymentOrders;
    }

    public Set<ClientPaymentOrder> getClientPaymentOrders() {
        return Collections.unmodifiableSet(getClientPaymentOrdersInternal());
    }

    public Boolean getNeedAccountTranslate() {
        return needAccountTranslate;
    }

    public void setNeedAccountTranslate(Boolean needAccountTranslate) {
        this.needAccountTranslate = needAccountTranslate;
    }

    public Set<ContragentClientAccount> getContragentClientAccounts() {
        return Collections.unmodifiableSet(getContragentClientAccountsInternal());
    }

    private Set<ContragentClientAccount> getContragentClientAccountsInternal() {
        return contragentClientAccounts;
    }

    private void setContragentClientAccountsInternal(Set<ContragentClientAccount> contragentClientAccounts) {
        this.contragentClientAccounts = contragentClientAccounts;
    }

    private Set<Settlement> getPayerSettlementsInternal() {
        // For Hibernate only
        return payerSettlements;
    }

    private void setPayerSettlementsInternal(Set<Settlement> payerSettlements) {
        // For Hibernate only
        this.payerSettlements = payerSettlements;
    }

    public Set<Settlement> getPayerSettlements() {
        return Collections.unmodifiableSet(getPayerSettlementsInternal());
    }

    private Set<Settlement> getReceiverSettlementsInternal() {
        // For Hibernate only
        return receiverSettlements;
    }

    private void setReceiverSettlementsInternal(Set<Settlement> receiverSettlements) {
        // For Hibernate only
        this.receiverSettlements = receiverSettlements;
    }

    public Set<Settlement> getReceiverSettlements() {
        return Collections.unmodifiableSet(getReceiverSettlementsInternal());
    }

    private Set<CurrentPosition> getDebtorPositionsInternal() {
        // For Hibernate only
        return debtorPositions;
    }

    private void setDebtorPositionsInternal(Set<CurrentPosition> debtorPositions) {
        // For Hibernate only
        this.debtorPositions = debtorPositions;
    }

    public Set<CurrentPosition> getDebtorPositions() {
        return Collections.unmodifiableSet(getDebtorPositionsInternal());
    }

    private Set<CurrentPosition> getCreditorPositionsInternal() {
        // For Hibernate only
        return creditorPositions;
    }

    private void setCreditorPositionsInternal(Set<CurrentPosition> creditorPositions) {
        // For Hibernate only
        this.creditorPositions = creditorPositions;
    }

    public Set<CurrentPosition> getCreditorPositions() {
        return Collections.unmodifiableSet(getCreditorPositionsInternal());
    }

    private Set<POS> getPOSSetInternal() {
        // For Hibernate only
        return POSSet;
    }

    private void setPOSSetInternal(Set<POS> POSSet) {
        // For Hibernate only
        this.POSSet = POSSet;
    }

    public Set<POS> getPOSSet() {
        return Collections.unmodifiableSet(getPOSSetInternal());
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

    private Set<Org> getOrgsInternal() {
        // For Hibernate only
        return orgs;
    }

    private void setOrgsInternal(Set<Org> orgs) {
        // For Hibernate only
        this.orgs = orgs;
    }

    public Set<Org> getOrgs() {
        return Collections.unmodifiableSet(getOrgsInternal());
    }

    private Set<AddPayment> getPayerAddPaymentsInternal() {
        // For Hibernate only
        return payerAddPayments;
    }

    private void setPayerAddPaymentsInternal(Set<AddPayment> payerAddPayments) {
        // For Hibernate only
        this.payerAddPayments = payerAddPayments;
    }

    public Set<AddPayment> getPayerAddPayments() {
        return Collections.unmodifiableSet(getPayerAddPaymentsInternal());
    }

    private Set<AddPayment> getReceiverAddPaymentsInternal() {
        // For Hibernate only
        return receiverAddPayments;
    }

    private void setReceiverAddPaymentsInternal(Set<AddPayment> receiverAddPayments) {
        // For Hibernate only
        this.receiverAddPayments = receiverAddPayments;
    }

    public Set<AddPayment> getReceiverAddPayments() {
        return Collections.unmodifiableSet(getReceiverAddPaymentsInternal());
    }

    public String getPublicKeyGOSTAlias() {
        return publicKeyGOSTAlias;
    }

    public void setPublicKeyGOSTAlias(String publicKeyGOSTAlias) {
        this.publicKeyGOSTAlias = publicKeyGOSTAlias;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contragent)) {
            return false;
        }
        final Contragent that = (Contragent) o;
        return idOfContragent.equals(that.getIdOfContragent());
    }

    @Override
    public int hashCode() {
        return idOfContragent.hashCode();
    }

    @Override
    public String toString() {
        return "Contragent{" + "idOfContragent=" + idOfContragent + ", contactPerson=" + contactPerson + ", parentId="
                + parentId + ", contragentName='" + contragentName + '\'' + ", classId=" + classId + ", flags=" + flags
                + ", title='" + title + '\'' + ", address='" + address + '\'' + ", phone='" + phone + '\''
                + ", mobile='" + mobile + '\'' + ", email='" + email + '\'' + ", fax='" + fax + '\'' + ", remarks='"
                + remarks + '\'' + ", inn='" + inn + '\'' + ", bank='" + bank + '\'' + ", bic='" + bic + '\''
                + ", corrAccount='" + corrAccount + '\'' + ", account='" + account + '\'' + ", createTime=" + createTime
                + ", updateTime=" + updateTime + ", publicKey='" + publicKey + '\''
                + ", KPP='" + kpp + '\'' + ", OGRN='" + ogrn+ '\'' + '}';
    }

}