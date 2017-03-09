/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientGuardSanRebuildService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.*;

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
        ClientSelectPage.CompleteHandler {

    private final String MESSAGE_GUARDIAN_EXISTS = "Ошибка: выбранный клиент уже присутствует в списке";
    private final String MESSAGE_GUARDIAN_SAME = "Ошибка: выбранный клиент редактируется в данный момент";
    private String fax;

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return fax;
    }

    public Long getBalanceToNotify() {
        return balanceToNotify;
    }

    public void setBalanceToNotify(Long balanceToNotify) {
        this.balanceToNotify = balanceToNotify;
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

    private String typeAddClient;

    public String getTypeAddClient() {
        return typeAddClient;
    }

    public void setTypeAddClient(String typeAddClient) {
        this.typeAddClient = typeAddClient;
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
    private Boolean notifyViaPUSH;
    private Boolean dontShowToExternal;
    private Boolean useLastEEModeForPlan;
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
    private Integer gender;
    private Date birthDate;
    private String categoriesDiscountsDSZN;
    private Date lastDiscountsUpdate;
    private Date disablePlanCreationDate;
    private String ageTypeGroup;
    private Long balanceToNotify;

    private final ClientGenderMenu clientGenderMenu = new ClientGenderMenu();

    public ClientGenderMenu getClientGenderMenu() {
        return clientGenderMenu;
    }

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

    public Boolean isEmailReadOnly() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_DISABLE_EMAIL_EDIT);
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

    public Boolean getNotifyViaPUSH() {
        return notifyViaPUSH;
    }

    public void setNotifyViaPUSH(Boolean notifyViaPUSH) {
        this.notifyViaPUSH = notifyViaPUSH;
    }

    public Boolean getDontShowToExternal() {
        return dontShowToExternal;
    }

    public void setDontShowToExternal(Boolean dontShowToExternal) {
        this.dontShowToExternal = dontShowToExternal;
    }

    public Boolean getUseLastEEModeForPlan() {
        return useLastEEModeForPlan;
    }

    public void setUseLastEEModeForPlan(Boolean useLastEEModeForPlan) {
        this.useLastEEModeForPlan = useLastEEModeForPlan;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAgeTypeGroup() {
        return ageTypeGroup;
    }

    public void setAgeTypeGroup(String ageTypeGroup) {
        this.ageTypeGroup = ageTypeGroup;
    }

    public Date getDisablePlanCreationDate() {
        return disablePlanCreationDate;
    }

    public void setDisablePlanCreationDate(Date disablePlanCreationDate) {
        this.disablePlanCreationDate = disablePlanCreationDate;
    }

    public Date getLastDiscountsUpdate() {
        return lastDiscountsUpdate;
    }

    public void setLastDiscountsUpdate(Date lastDiscountsUpdate) {
        this.lastDiscountsUpdate = lastDiscountsUpdate;
    }

    public String getCategoriesDiscountsDSZN() {
        return categoriesDiscountsDSZN;
    }

    public void setCategoriesDiscountsDSZN(String categoriesDiscountsDSZN) {
        this.categoriesDiscountsDSZN = categoriesDiscountsDSZN;
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
        StringBuilder categoryDiscountsSB = new StringBuilder();
        for (CategoryDiscount categoryDiscount : categoryDiscountList) {
            categoryDiscounts.add(new CategoryDiscountItem(
                    clientCategories.contains(categoryDiscount.getIdOfCategoryDiscount() + ""),
                    categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount.getCategoryName()));
            if(clientCategories.contains(categoryDiscount.getIdOfCategoryDiscount() + "")) {
                categoryDiscountsSB.append(categoryDiscount.getCategoryName());
                categoryDiscountsSB.append(";");
            }
        }
        this.filter = categoryDiscountsSB.length() > 2 ? categoryDiscountsSB.substring(0, categoryDiscountsSB.length() - 1) : categoryDiscountsSB.toString();
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
        this.idOfClientGroup = client.getClientGroup() == null ? null : client.getClientGroup()
                .getCompositeIdOfClientGroup().getIdOfClientGroup();

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
        this.clientWardItems = loadWardsByClient(session, idOfClient);
        this.changePassword = false;

        fill(client);
    }

    public Boolean getAddClientGuardianButtonRendered() {
        return clientGuardianItems == null || clientGuardianItems.isEmpty() || clientGuardianItems.size() < 2;
    }

    public Object removeClientGuardian() {
        if (currentClientGuardian != null && !clientGuardianItems.isEmpty()) {
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

    private ClientGuardianItem currentClientWard;

    public ClientGuardianItem getCurrentClientWard() {
        return currentClientWard;
    }

    public void setCurrentClientWard(ClientGuardianItem currentClientWard) {
        this.currentClientWard = currentClientWard;
    }

    public Object removeClientWard() {
        if (currentClientWard != null && !clientWardItems.isEmpty()) {
            clientWardItems.remove(currentClientWard);
            removeListWardItems.add(currentClientWard);
        }
        return null;
    }

    private List<ClientGuardianItem> clientGuardianItems;
    private List<ClientGuardianItem> removeListGuardianItems = new ArrayList<ClientGuardianItem>();
    private List<ClientGuardianItem> removeListWardItems = new ArrayList<ClientGuardianItem>();

    public List<ClientGuardianItem> getClientGuardianItems() {
        return clientGuardianItems;
    }

    public boolean getOldFlagsShow() {
        if (clientGuardianItems == null) return true;
        boolean result = true;
        for (ClientGuardianItem item : clientGuardianItems) {
            if (!item.getDisabled()) return false;
        }
        return result;
    }

    private List<ClientGuardianItem> clientWardItems;

    public List<ClientGuardianItem> getClientWardItems() {
        return clientWardItems;
    }

    public boolean existAddedWards() {
        for (ClientGuardianItem item : clientWardItems) {
            if (item.getIsNew()) return true;
        }
        return false;
    }

    private boolean isParent(String groupName) {
        return groupName.equalsIgnoreCase(ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            if (typeAddClient == null) return;
            if (typeAddClient.equals("guardian")) {
                if (!guardianExists(idOfClient))
                    clientGuardianItems.add(new ClientGuardianItem(client, false, null, ClientManager.getNotificationSettings()));
            }
            if (typeAddClient.equals("ward")) {
                if (!wardExists(idOfClient))
                    clientWardItems.add(new ClientGuardianItem(client, false, null, ClientManager.getNotificationSettings()));
            }
        }
    }

    private boolean guardianExists(Long idOfClient) {
        if (this.idOfClient.equals(idOfClient)) {
            printMessage(MESSAGE_GUARDIAN_SAME);
            return true;
        }
        for (ClientGuardianItem item : clientGuardianItems) {
            if (item.getIdOfClient().equals(idOfClient)) {
                printMessage(MESSAGE_GUARDIAN_EXISTS);
                return true;
            }
        }
        return false;
    }

    private boolean wardExists(Long idOfClient) {
        if (this.idOfClient.equals(idOfClient)) {
            printMessage(MESSAGE_GUARDIAN_SAME);
            return true;
        }
        for (ClientGuardianItem item : clientWardItems) {
            if (item.getIdOfClient().equals(idOfClient)) {
                printMessage(MESSAGE_GUARDIAN_EXISTS);
                return true;
            }
        }
        return false;
    }

    public void printMessage(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public void printMessage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "NOT OK", null));
    }

    public void completeClientGroupSelection(Session session, Long idOfClientGroup) throws Exception {
        if (null != idOfClientGroup) {
            ClientGroupMigrationHistory clientGroupMigrationHistory = new ClientGroupMigrationHistory();
            Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
            Client client = (Client) session.load(Client.class, idOfClient);

            clientGroupMigrationHistory.setOrg(org);
            clientGroupMigrationHistory.setClient(client);
            clientGroupMigrationHistory.setRegistrationDate(new Date());
            if (client.getIdOfClientGroup() != null) {
                clientGroupMigrationHistory.setOldGroupId(client.getIdOfClientGroup());
            }
            if (client.getClientGroup() != null) {
                clientGroupMigrationHistory.setOldGroupName(client.getClientGroup().getGroupName());
            }

            this.idOfClientGroup = idOfClientGroup;
            this.clientGroupName = DAOUtils
                    .findClientGroup(session, new CompositeIdOfClientGroup(this.org.idOfOrg, idOfClientGroup))
                    .getGroupName();
            clientGroupMigrationHistory.setNewGroupId(this.idOfClientGroup);
            clientGroupMigrationHistory.setNewGroupName(this.clientGroupName);
            clientGroupMigrationHistory.setComment(
                    ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance()
                            .getExternalContext().getRemoteUser());

            session.save(clientGroupMigrationHistory);
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
        ClientMigration clientMigration = null;
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

        Org org = (Org) persistenceSession.load(Org.class, this.org.getIdOfOrg());
        Boolean isReplaceOrg = !(client.getOrg().getIdOfOrg().equals(org.getIdOfOrg()));
        if (isReplaceOrg) {
            clientMigration = new ClientMigration(client.getOrg());

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Set<Org> orgSet = client.getOrg().getFriendlyOrg();
            runtimeContext.getProcessor().disableClientCardsIfChangeOrg(client, orgSet, org.getIdOfOrg());

        }
        client.setOrg(org);
        client.setPerson(person);
        client.setContractPerson(contractPerson);
        client.setClientRegistryVersion(clientRegistryVersion);
        client.setFlags(this.flags);
        client.setAddress(this.address);
        client.setPhone(this.phone);
        getLogger().info("class : ClientEditPage, method : updateClient line : 868, idOfClient : " + client.getIdOfClient() + " phone : " + client.getPhone());
        //  если у клиента есть мобильный и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
        if (client != null && client.getMobile() != null && !client.getMobile().equals(mobile)) {
            client.setSsoid("");
        }
        client.setMobile(mobile);
        getLogger().info("class : ClientEditPage, method : updateClient line : 874, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
        client.setFax(this.fax);
        //  если у клиента есть емайл и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
        if (client != null && client.getEmail() != null && !client.getEmail().equals(this.email)) {
            client.setSsoid("");
        }
        client.setEmail(this.email);
        client.setNotifyViaEmail(this.notifyViaEmail);
        client.setNotifyViaSMS(this.notifyViaSMS);
        client.setNotifyViaPUSH(this.notifyViaPUSH);
        client.setDontShowToExternal(this.dontShowToExternal);
        client.setUseLastEEModeForPlan(this.useLastEEModeForPlan);
        client.setRemarks(this.remarks);
        client.setUpdateTime(new Date());
        client.setContractTime(this.contractTime);
        client.setContractId(this.contractId);
        client.setContractState(this.contractState);
        client.setLimit(this.limit);
        client.setExpenditureLimit(this.expenditureLimit);
        client.setFreePayMaxCount(this.freePayMaxCount);
        client.setSan(this.san);
        client.setBalanceToNotify(this.balanceToNotify);
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
        Set<GuardSan> newGuardSans = ClientGuardSanRebuildService.getInstance().addGuardSan(idOfClient, guardsan);
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
            //Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            //categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
        }

        if (isDiscountsChanged(client)) {
            saveDiscountChange(client, persistenceSession);
        }

        if (null != discountMode) {
            client.setDiscountMode(discountMode);
        }

        client.setCategoriesDiscounts(
                clientCategories.length() == 0 ? "" : clientCategories.substring(0, clientCategories.length() - 1));
        client.setCategories(categoryDiscountSet.isEmpty() ? new HashSet<CategoryDiscount>() : categoryDiscountSet);

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

            ClientGroupMigrationHistory clientGroupMigrationHistory = new ClientGroupMigrationHistory();
            clientGroupMigrationHistory.setOrg(org);
            clientGroupMigrationHistory.setClient(client);
            clientGroupMigrationHistory.setRegistrationDate(new Date());
            if (client.getIdOfClientGroup() != null) {
                clientGroupMigrationHistory.setOldGroupId(client.getIdOfClientGroup());
            }
            if (client.getClientGroup() != null) {
                clientGroupMigrationHistory.setOldGroupName(client.getClientGroup().getGroupName());
                clientMigration.setOldGroupName(client.getClientGroup().getGroupName());
            }

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

            clientGroupMigrationHistory.setNewGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            clientGroupMigrationHistory.setNewGroupName(clientGroup.getGroupName());
            clientMigration.setNewGroupName(clientGroup.getGroupName());
            persistenceSession.save(clientGroupMigrationHistory);

            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            client.setIdOfClientGroup(this.idOfClientGroup);
            clientMigration.setClient(client);
            clientMigration.setOrg(org);
            clientMigration.setNewContragent(org.getDefaultSupplier());
            clientMigration.setBalance(client.getBalance());
            clientMigration.setComment(ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance()
                    .getExternalContext().getRemoteUser());

            persistenceSession.save(clientMigration);
        } else {
            if (this.idOfClientGroup != null) {
                client.setIdOfClientGroup(this.idOfClientGroup);
            }
        }

        if (clientGuardianItems != null && !clientGuardianItems.isEmpty()) {
            addGuardiansByClient(persistenceSession, idOfClient, clientGuardianItems);
        }
        if (removeListGuardianItems != null && !removeListGuardianItems.isEmpty()) {
            removeGuardiansByClient(persistenceSession, idOfClient, removeListGuardianItems);
        }

        if (clientWardItems != null && !clientWardItems.isEmpty()) {
            addWardsByClient(persistenceSession, idOfClient, clientWardItems);
        }
        if (removeListWardItems != null && !removeListWardItems.isEmpty()) {
            removeWardsByClient(persistenceSession, idOfClient, removeListWardItems);
        }

        resetNewFlags();

        client.setGender(this.gender);
        client.setBirthDate(this.birthDate);
        client.setAgeTypeGroup(this.ageTypeGroup);

        persistenceSession.update(client);

        fill(client);

        if (client.getSsoid() != null && !client.getSsoid().equals("")) {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            processor.updateNotificationParams(client);
        }
    }

    private void resetNewFlags() {
        for (ClientGuardianItem item : clientGuardianItems) {
            item.setIsNew(false);
        }
        for (ClientGuardianItem item : clientWardItems) {
            item.setIsNew(false);
        }
    }

    private boolean isDiscountsChanged(Client client) {
        int a = client.getDiscountMode();
        int b = discountMode;
        boolean isDiscountModeChanged = !(client.getDiscountMode().equals(discountMode));
        boolean isCategoryListChanged = !(client.getCategories().equals(getCategoryDiscountSet()));
        Set<CategoryDiscount> old = client.getCategories();
        Set<CategoryDiscount> newSet = getCategoryDiscountSet();
        boolean isOld = old.equals(newSet);
        return isDiscountModeChanged || isCategoryListChanged;
    }

    private void saveDiscountChange(Client client, Session persistenceSession) {
        DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, null, discountMode, client.getDiscountMode(),
                getCategoriesString(getCategoryDiscountSet()), getCategoriesString(client.getCategories()));
        discountChangeHistory.setComment(
                DiscountChangeHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance()
                        .getExternalContext().getRemoteUser() + ".");
        persistenceSession.save(discountChangeHistory);

    }

    private String getCategoriesString(Set<CategoryDiscount> categoryDiscounts) {
        StringBuilder clientCategories = new StringBuilder();
        for (CategoryDiscount categoryDiscount : categoryDiscounts) {
            clientCategories.append(categoryDiscount.getIdOfCategoryDiscount());
            clientCategories.append(",");
        }
        return clientCategories.length() == 0 ? "" : clientCategories.substring(0, clientCategories.length() - 1);
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
        this.notifyViaPUSH = client.isNotifyViaPUSH();
        this.dontShowToExternal = client.isDontShowToExternal();
        this.remarks = client.getRemarks();
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
        this.payForSMS = client.getPayForSMS();
        this.balance = client.getBalance();
        this.subBalance1 = client.getSubBalance1() == null ? 0L : client.getSubBalance1();
        this.subBalance0 = this.balance - this.subBalance1;
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();
        this.balanceToNotify = client.getBalanceToNotify();
        this.freePayMaxCount = client.getFreePayMaxCount();
        this.san = client.getSan();
        Set<GuardSan> guardSans = client.getGuardSan();
        this.guardsan = "";
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
        this.useLastEEModeForPlan = client.isUseLastEEModeForPlan();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.categoriesDiscountsDSZN = client.getCategoriesDiscountsDSZN();
        this.lastDiscountsUpdate = client.getLastDiscountsUpdate();
        this.disablePlanCreationDate = client.getDisablePlanCreationDate();
        this.ageTypeGroup = client.getAgeTypeGroup();
        removeListGuardianItems.clear();
        removeListWardItems.clear();
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
        Criteria criteria = session.createCriteria(CategoryDiscount.class).createAlias("discountRulesInternal", "dri")
                .createAlias("dri.categoryOrgsInternal", "coi").createAlias("coi.orgsInternal", "o")
                .add(Restrictions.eq("o.idOfOrg", idOfOrg))
                .add(Restrictions.in("idOfCategoryDiscount", idOfCategoryList))
                .setProjection(Projections.projectionList().add(Projections.countDistinct("idOfCategoryDiscount")));
        return (Long) criteria.uniqueResult() > 0;
    }

    public SelectItem[] getRelations() {
        SelectItem[] result = new SelectItem[ClientGuardianRelationType.values().length + 1];
        result[0] = new SelectItem(-1, "");
        for (int i = 0; i < ClientGuardianRelationType.values().length; i++) {
            result[i+1] = new SelectItem(i, ClientGuardianRelationType.fromInteger(i).toString());
        }
        return result;
    }
}