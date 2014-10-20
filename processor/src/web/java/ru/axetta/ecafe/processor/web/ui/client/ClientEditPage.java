/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientGuardSanRebuildService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.faces.model.SelectItem;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.addGuardiansByClient;
import static ru.axetta.ecafe.processor.core.logic.ClientManager.loadGuardiansByClient;
import static ru.axetta.ecafe.processor.core.logic.ClientManager.removeGuardiansByClient;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientEditPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler,
        CategoryListSelectPage.CompleteHandlerList,
        ClientGroupSelectPage.CompleteHandler,
        ClientSelectPage.CompleteHandler{


    private String fax;

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return fax;
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
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
    }

    public static class PersonItem {

        private String firstName;
        private String surname;
        private String secondName;
        private String idDocument;

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }

        public void setIdDocument(String idDocument) {
            this.idDocument = idDocument;
        }

        public void copyTo(Person person) {
            person.setFirstName(firstName);
            person.setSurname(surname);
            person.setSecondName(secondName);
            person.setIdDocument(idDocument);
        }
    }

    public static class CategoryDiscountItem {

        private long idOfCategoryDiscount;
        private Boolean selected;
        private String categoryName;

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public long getIdOfCategoryDiscount() {
            return idOfCategoryDiscount;
        }

        public CategoryDiscountItem(Boolean selected, long idOfCategoryDiscount, String categoryName) {
            this.selected = selected;
            this.idOfCategoryDiscount = idOfCategoryDiscount;
            this.categoryName = categoryName;
        }
    }

    private List<CategoryDiscountItem> categoryDiscounts;
    private List<NotificationSettingItem> notificationSettings;

    public List<CategoryDiscountItem> getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public List<NotificationSettingItem> getNotificationSettings() {
        return notificationSettings;
    }

    public int getNotificationSettingsCount() {
        int cnt = 0;
        for (NotificationSettingItem i : notificationSettings) {
            if (i.isEnabled()) {
                cnt++;
            }
        }
        return cnt;
    }

    public boolean isCategoriesEmpty() {
        return categoryDiscounts.isEmpty();
    }

    private List<CategoryDiscount> categoryDiscountList = new ArrayList<CategoryDiscount>();

    public List<CategoryDiscount> getCategoryDiscountList() {
        return categoryDiscountList;
    }

    public void setCategoryDiscountList(List<CategoryDiscount> categoryDiscountList) {
        this.categoryDiscountList = categoryDiscountList;
    }

    private static final int CONTRACT_ID_MAX_LENGTH = ContractIdFormat.MAX_LENGTH;

    private Long idOfClient;
    private OrgItem org;
    private PersonItem person;
    private PersonItem contractPerson;
    private Integer flags;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private Boolean notifyViaEmail;
    private Boolean notifyViaSMS;
    private String remarks;
    private boolean changePassword;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private Long contractId;
    private Date contractTime;
    private Integer contractState;
    private Integer payForSMS;
    private Long balance;
    private Long subBalance0;
    private Long subBalance1;
    private Long limit;
    private Long expenditureLimit;
    private String clientGroupName;
    private Long idOfClientGroup;
    private Long externalId;
    private String clientGUID;
    private Integer discountMode;
    private List<SelectItem> selectItemList = new ArrayList<SelectItem>();
    private String san;
    private String guardsan;
    private final ClientPayForSMSMenu clientPayForSMSMenu = new ClientPayForSMSMenu();
    private final ClientContractStateMenu clientContractStateMenu = new ClientContractStateMenu();
    private Integer freePayMaxCount;

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
    }

    /* Является ли данный тип льгот как "Льгота по категорям" если да то TRUE, False в противном случает */
    public Boolean getDiscountModeIsCategory() {
        return discountMode == Client.DISCOUNT_MODE_BY_CATEGORY;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(Integer discountMode) {
        this.discountMode = discountMode;
    }

    public String getClientGroupName() {
        return clientGroupName;
    }

    public void setClientGroupName(String clientGroupName) {
        this.clientGroupName = clientGroupName;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public String getGuardsan() {
        return guardsan;
    }

    public void setGuardsan(String guardsan) {
        this.guardsan = guardsan;
    }

    public int getContractIdMaxLength() {
        return CONTRACT_ID_MAX_LENGTH;
    }

    public Long getBalance() {
        return balance;
    }

    public Long getSubBalance0() {
        return subBalance0;
    }

    public Long getSubBalance1() {
        return subBalance1;
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

    public String getPageFilename() {
        return "client/edit";
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public OrgItem getOrg() {
        return org;
    }

    public void setOrg(OrgItem org) {
        this.org = org;
    }

    public PersonItem getPerson() {
        return person;
    }

    public void setPerson(PersonItem person) {
        this.person = person;
    }

    public PersonItem getContractPerson() {
        return contractPerson;
    }

    public void setContractPerson(PersonItem contractPerson) {
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

    public Boolean getNotifyViaEmail() {
        return notifyViaEmail;
    }

    public void setNotifyViaEmail(Boolean notifyViaEmail) {
        this.notifyViaEmail = notifyViaEmail;
    }

    public Boolean getNotifyViaSMS() {
        return notifyViaSMS;
    }

    public void setNotifyViaSMS(Boolean notifyViaSMS) {
        this.notifyViaSMS = notifyViaSMS;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPlainPasswordConfirmation() {
        return plainPasswordConfirmation;
    }

    public void setPlainPasswordConfirmation(String plainPasswordConfirmation) {
        this.plainPasswordConfirmation = plainPasswordConfirmation;
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

    public Integer getPayForSMS() {
        return payForSMS;
    }

    public void setPayForSMS(Integer payForSMS) {
        this.payForSMS = payForSMS;
    }

    public ClientPayForSMSMenu getClientPayForSMSMenu() {
        return clientPayForSMSMenu;
    }

    public ClientContractStateMenu getClientContractStateMenu() {
        return clientContractStateMenu;
    }

    public Integer getFreePayMaxCount() {
        return freePayMaxCount;
    }

    public void setFreePayMaxCount(Integer freePayMaxCount) {
        this.freePayMaxCount = freePayMaxCount;
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

    public void fill(Session session, Long idOfClient) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);
        List clientCategories = Arrays.asList(client.getCategoriesDiscounts().split(","));
        Criteria categoryDiscountCriteria = session.createCriteria(CategoryDiscount.class);
        List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
        categoryDiscounts = new ArrayList<CategoryDiscountItem>();
        for (CategoryDiscount categoryDiscount : categoryDiscountList) {
            categoryDiscounts.add(new CategoryDiscountItem(
                    clientCategories.contains(categoryDiscount.getIdOfCategoryDiscount() + ""),
                    categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount.getCategoryName()));
        }
        Set<ClientNotificationSetting> settings = client.getNotificationSettings();
        notificationSettings = new ArrayList<NotificationSettingItem>();
        for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
            if (predefined.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            notificationSettings.add(new NotificationSettingItem(predefined, settings));
        }

        idOfCategoryList.clear();
        categoryDiscountList.clear();
        if (!client.getCategories().isEmpty()) {
            for (CategoryDiscount categoryDiscount : client.getCategories()) {
                idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                this.categoryDiscountList.add(categoryDiscount);
            }
        }

        this.selectItemList = new ArrayList<SelectItem>();
        /* если у клиента уже выбрана льгота то она будет первой */
        if (null != client.getDiscountMode() && client.getDiscountMode() > 0) {
            this.selectItemList.add(new SelectItem(client.getDiscountMode(),
                    Client.DISCOUNT_MODE_NAMES[client.getDiscountMode()]));
        }
        this.selectItemList.add(new SelectItem(0, Client.DISCOUNT_MODE_NAMES[0]));
        for (Integer i = 1; i < Client.DISCOUNT_MODE_NAMES.length; i++) {
            SelectItem selectItem = new SelectItem(i, Client.DISCOUNT_MODE_NAMES[i]);
            if (!i.equals(client.getDiscountMode())) {
                this.selectItemList.add(selectItem);
            }
        }
        this.clientGroupName = client.getClientGroup() == null ? "" : client.getClientGroup().getGroupName();

        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfClient));
        //if(isParent(clientGroupName)){
        //    criteria.add(Restrictions.eq("idOfGuardian", idOfClient));
        //} else {
        //}
        List results = criteria.list();
        //clientGuardianItems = new ArrayList<ClientGuardianItem>();
        //for (Object o: results){
        //    ClientGuardian clientGuardian = (ClientGuardian) o;
        //    Client cl = null;
        //    //if(isParent(clientGroupName)){
        //    //    cl = DAOUtils.findClient(session, clientGuardian.getIdOfChildren());
        //    //} else {
        //    //}
        //    cl = DAOUtils.findClient(session, clientGuardian.getIdOfGuardian());
        //    if(cl != null){
        //        clientGuardianItems.add(new ClientGuardianItem(cl));
        //    }
        //}

        this.clientGuardianItems = loadGuardiansByClient(session, idOfClient);


        fill(client);
    }

    public Boolean getAddClientGuardianButtonRendered(){
        return clientGuardianItems == null || clientGuardianItems.isEmpty() || clientGuardianItems.size() < 2;
    }

    public Object removeClientGuardian() {
        if(currentClientGuardian!=null && !clientGuardianItems.isEmpty()){
            clientGuardianItems.remove(currentClientGuardian);
            removeListGuardianItems.add(currentClientGuardian);
        }
        return null;
    }

    private ClientGuardianItem currentClientGuardian;

    public ClientGuardianItem getCurrentClientGuardian() {
        return currentClientGuardian;
    }

    public void setCurrentClientGuardian(ClientGuardianItem currentClientGuardian) {
        this.currentClientGuardian = currentClientGuardian;
    }

    private List<ClientGuardianItem> clientGuardianItems;
    private List<ClientGuardianItem> removeListGuardianItems = new ArrayList<ClientGuardianItem>();

    public List<ClientGuardianItem> getClientGuardianItems() {
        return clientGuardianItems;
    }

    private boolean isParent(String groupName) {
        return groupName.equalsIgnoreCase(ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            clientGuardianItems.add(new ClientGuardianItem(client));
        }
    }

    public void completeClientGroupSelection(Session session, Long idOfClientGroup) throws Exception {
        if (null != idOfClientGroup) {
            this.idOfClientGroup = idOfClientGroup;
            this.clientGroupName = DAOUtils
                    .findClientGroup(session, new CompositeIdOfClientGroup(this.org.idOfOrg, idOfClientGroup))
                    .getGroupName();
        }
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Long oldOrgId = this.org.getIdOfOrg();
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
            if (!oldOrgId.equals(idOfOrg) && !idOfCategoryList.isEmpty()) {
                newOrgHasCatDiscount = checkOrgDiscounts(session, idOfOrg);
        }
    }
    }

    public Object changeClientCategory() {
        if (this.discountMode != Client.DISCOUNT_MODE_BY_CATEGORY) {
            this.idOfCategoryList = new ArrayList<Long>(0);
            filter = "Не выбрано";
        }
        return null;
    }

    public void updateClient(Session persistenceSession, Long idOfClient) throws Exception {
        String mobile = Client.checkAndConvertMobile(this.mobile);
        if (mobile == null) {
            throw new Exception("Неверный формат мобильного телефона");
        }

        Client client = (Client) persistenceSession.load(Client.class, idOfClient);
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);

        Person person = client.getPerson();
        this.person.copyTo(person);
        persistenceSession.update(person);

        Person contractPerson = client.getContractPerson();
        this.contractPerson.copyTo(contractPerson);
        persistenceSession.update(contractPerson);

        Set<Long> idOfFriendlyOrg = DAOUtils.getIdOfFriendlyOrg(persistenceSession, client.getOrg().getIdOfOrg());
        Org org = null;
        if (idOfFriendlyOrg.contains(this.org.getIdOfOrg())) {
            org = (Org) persistenceSession.load(Org.class, client.getOrg().getIdOfOrg());
        } else {
            org = (Org) persistenceSession.load(Org.class, this.org.getIdOfOrg());
        }
        Boolean isReplaceOrg = !(client.getOrg().getIdOfOrg().equals(org.getIdOfOrg()));
        client.setOrg(org);
        client.setPerson(person);
        client.setContractPerson(contractPerson);
        client.setClientRegistryVersion(clientRegistryVersion);
        client.setFlags(this.flags);
        client.setAddress(this.address);
        client.setPhone(this.phone);
        //  если у клиента есть мобильный, кот. отличается от нового, то сбрсываем ССОИД для ЕМП
        if(client != null && client.getMobile() != null && StringUtils.isBlank(client.getMobile()) &&
           !client.getMobile().equals(mobile)) {
            client.setSsoid("");
        }
        client.setMobile(mobile);
        client.setFax(this.fax);
        client.setEmail(this.email);
        client.setNotifyViaEmail(this.notifyViaEmail);
        client.setNotifyViaSMS(this.notifyViaSMS);
        client.setRemarks(this.remarks);
        client.setUpdateTime(new Date());
        client.setContractTime(this.contractTime);
        client.setContractId(this.contractId);
        client.setContractState(this.contractState);
        client.setLimit(this.limit);
        client.setExpenditureLimit(this.expenditureLimit);
        client.setFreePayMaxCount(this.freePayMaxCount);
        client.setSan(this.san);
        /* Добавление СНИЛС опекунов */
        /*Set<GuardSan> guardSans = new HashSet<GuardSan>();
        String gSanList [] = null;
        if (guardsan.indexOf(ClientGuardSanRebuildService.DELIMETER_1) > -1) {
            gSanList = guardsan.split(ClientGuardSanRebuildService.DELIMETER_1);
        } else if (guardsan.indexOf(ClientGuardSanRebuildService.DELIMETER_2) > -1) {
            gSanList = guardsan.split(ClientGuardSanRebuildService.DELIMETER_2);
        } else {
            gSanList = new String [] { guardsan };
        }
        for (String gSan : gSanList) {
            gSan = ClientGuardSanRebuildService.clearGuardSan(gSan);
            if (gSan.length() < 1) {
                continue;
            }
            GuardSan obj = new GuardSan(client, gSan);
            guardSans.add(obj);
        }*/
        /*Set <GuardSan> guardSans = client.getGuardSan();
        guardSans.removeAll(guardSans);*/
        ClientGuardSanRebuildService.getInstance().removeGuardSan(idOfClient);
        Set <GuardSan> newGuardSans = ClientGuardSanRebuildService.getInstance().addGuardSan(idOfClient, guardsan);
        /*guardSans.addAll(newGuardSans);
        client.setGuardSan(guardSans);*/
        if (this.externalId == null || this.externalId == 0) {
            client.setExternalId(null);
        } else {
            client.setExternalId(this.externalId);
        }
        if (this.clientGUID == null || this.clientGUID.isEmpty()) {
            client.setClientGUID(null);
        } else {
            client.setClientGUID(this.clientGUID);
        }
        if (this.changePassword) {
            client.setPassword(this.plainPassword);
        }
        client.setPayForSMS(this.payForSMS);
        if (null != discountMode) {
            client.setDiscountMode(discountMode);
        }
        if (discountMode == Client.DISCOUNT_MODE_BY_CATEGORY && idOfCategoryList.size() == 0) {
            throw new Exception("Выберите хотя бы одну категорию льгот");
        }
        /* категори скидок */
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        StringBuilder clientCategories = new StringBuilder();
        if (this.idOfCategoryList.size() != 0) {
            Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
            for (Object object : categoryCriteria.list()) {
                CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                clientCategories.append(categoryDiscount.getIdOfCategoryDiscount());
                clientCategories.append(",");
                this.categoryDiscountSet.add(categoryDiscount);
            }
        } else {
            /* очистить список если он не пуст */
            Set<CategoryDiscount> categories = client.getCategories();
            for (CategoryDiscount categoryDiscount : categories) {
                categoryDiscount.getClients().remove(client);
                persistenceSession.update(categoryDiscount);
            }
            client.setCategories(new HashSet<CategoryDiscount>(0));
            //Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            //categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
        }
        client.setCategoriesDiscounts(
                clientCategories.length() == 0 ? "" : clientCategories.substring(0, clientCategories.length() - 1));
        client.setCategories(categoryDiscountSet);
        /* настройки смс оповещений */
        for (NotificationSettingItem item : notificationSettings) {
            ClientNotificationSetting newSetting = new ClientNotificationSetting(client, item.getNotifyType());
            if (item.isEnabled()) {
                client.getNotificationSettings().add(newSetting);
            } else {
                client.getNotificationSettings().remove(newSetting);
            }
        }
        if (isReplaceOrg) {
            ClientGroup clientGroup = DAOUtils
                    .findClientGroupByGroupNameAndIdOfOrg(persistenceSession, org.getIdOfOrg(),
                            ClientGroup.Predefined.CLIENT_DISPLACED.getNameOfGroup());
            if (clientGroup == null) {
                clientGroup = DAOUtils.findClientGroupByIdOfClientGroupAndIdOfOrg(persistenceSession, org.getIdOfOrg(),
                        ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
                if (clientGroup != null) {
                    clientGroup.setGroupName(ClientGroup.Predefined.CLIENT_DISPLACED.getNameOfGroup());
                }
            }
            getLogger().info(org.getShortName());
            if (clientGroup == null) {
                clientGroup = DAOUtils.createClientGroup(persistenceSession, org.getIdOfOrg(),
                        ClientGroup.Predefined.CLIENT_DISPLACED);
            }
            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            client.setIdOfClientGroup(this.idOfClientGroup);
            ClientMigration clientMigration = new ClientMigration(client, org);
            persistenceSession.save(clientMigration);
        } else {
            if (this.idOfClientGroup != null) {
                client.setIdOfClientGroup(this.idOfClientGroup);
            }
        }

        if(clientGuardianItems!=null && !clientGuardianItems.isEmpty()){
            addGuardiansByClient(persistenceSession, idOfClient, clientGuardianItems);
        }
        if(removeListGuardianItems!=null && !removeListGuardianItems.isEmpty()){
            removeGuardiansByClient(persistenceSession, idOfClient, removeListGuardianItems);
        }

        persistenceSession.update(client);

        fill(client);
    }

    public void removeClient(Session persistenceSession) throws Exception {
        Client client = (Client) persistenceSession.load(Client.class, idOfClient);
        if (!client.getOrders().isEmpty()) {
            throw new Exception("Имеются зарегистрированные заказы");
        }
        if (!client.getClientPaymentOrders().isEmpty()) {
            throw new Exception("Имеются зарегистрированные пополнения счета");
        }
        if (!client.getCards().isEmpty()) {
            throw new Exception("Имеются зарегистрированные карты");
        }
        if (!client.getCategories().isEmpty()) {
            for (CategoryDiscount categoryDiscount : client.getCategories()) {
                client.getCategories().remove(categoryDiscount);
            }
        }
        persistenceSession.delete(client);
    }

    private void fill(Client client) throws Exception {
        this.idOfClient = client.getIdOfClient();
        this.org = new OrgItem(client.getOrg());
        this.person = new PersonItem(client.getPerson());
        this.contractPerson = new PersonItem(client.getContractPerson());
        this.flags = client.getFlags();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.mobile = client.getMobile();
        this.email = client.getEmail();
        this.fax = client.getFax();
        this.notifyViaEmail = client.isNotifyViaEmail();
        this.notifyViaSMS = client.isNotifyViaSMS();
        this.remarks = client.getRemarks();
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
        this.payForSMS = client.getPayForSMS();
        this.balance = client.getBalance();
        this.subBalance1 = client.getSubBalance1()==null?0L:client.getSubBalance1();
        this.subBalance0 = this.balance - this.subBalance1;
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();
        this.freePayMaxCount = client.getFreePayMaxCount();
        this.san = client.getSan();
        Set <GuardSan> guardSans = client.getGuardSan();
        this.guardsan="";
        for (GuardSan guard : guardSans) {
            if (this.guardsan.length() > 0) {
                this.guardsan = this.guardsan + ",";
            }
            this.guardsan = this.guardsan + guard.getGuardSan();
        }
        this.externalId = client.getExternalId();
        this.clientGUID = client.getClientGUID();
        this.discountMode = client.getDiscountMode();
        /* filter fill*/
        StringBuilder categoriesFilter = new StringBuilder();
        if (!client.getCategories().isEmpty()) {
            for (CategoryDiscount categoryDiscount : client.getCategories()) {
                categoriesFilter.append(categoryDiscount.getCategoryName());
                categoriesFilter.append("; ");
                this.categoryDiscountList.add(categoryDiscount);
            }
        } else {
            categoriesFilter.append("Не выбрано");
        }

    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]", "");
    }

    private String filter = "Не выбрано";
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();
    private boolean newOrgHasCatDiscount = true;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public void setIdOfCategoryList(List<Long> idOfCategoryList) {
        this.idOfCategoryList = idOfCategoryList;
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public boolean isNewOrgHasCatDiscount() {
        return newOrgHasCatDiscount;
    }

    public void setNewOrgHasCatDiscount(boolean newOrgHasCatDiscount) {
        this.newOrgHasCatDiscount = newOrgHasCatDiscount;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
        //To change body of implemented methods use File | Settings | File Templates.
        if (null != categoryMap) {
            idOfCategoryList = new ArrayList<Long>();
            if (categoryMap.isEmpty()) {
                filter = "Не выбрано";
            } else {
                filter = "";
                for (Long idOfCategory : categoryMap.keySet()) {
                    idOfCategoryList.add(idOfCategory);
                    filter = filter.concat(categoryMap.get(idOfCategory) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }

        }
    }

    // Проверяет, имеет ли организация льготы по категориям льгот клиента.
    @SuppressWarnings("unchecked")
    private boolean checkOrgDiscounts(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class)
                .createAlias("discountRulesInternal", "dri")
                .createAlias("dri.categoryOrgsInternal", "coi")
                .createAlias("coi.orgsInternal", "o")
                .add(Restrictions.eq("o.idOfOrg", idOfOrg))
                .add(Restrictions.in("idOfCategoryDiscount", idOfCategoryList))
                .setProjection(Projections.projectionList().add(Projections.countDistinct("idOfCategoryDiscount")));
        return (Long) criteria.uniqueResult() > 0;
    }
}