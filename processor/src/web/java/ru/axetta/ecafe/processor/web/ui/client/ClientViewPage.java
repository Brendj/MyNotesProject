/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.loadGuardiansByClient;
import static ru.axetta.ecafe.processor.core.logic.ClientManager.loadWardsByClient;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientViewPage extends BasicWorkspacePage {

    public static class PersonData {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonData() {
            this.firstName = null;
            this.surname = null;
            this.secondName = null;
            this.idDocument = null;
        }

        public PersonData(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }
    }

    private List<CategoryDiscount> categoriesDiscounts;

    public List<CategoryDiscount> getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(List<CategoryDiscount> categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public boolean isCategoryiesDiscounts(){
        return categoriesDiscounts.isEmpty() ;
    }

    private Long idOfClient;
    private Long idOfOrg;
    private String orgShortName;
    private PersonData person = new PersonData();
    private PersonData contractPerson = new PersonData();
    private Integer flags;
    private String address;
    private String phone;
    private String mobile;
    private String fax;
    private String email;
    private Boolean notifyViaEmail;
    private Boolean notifyViaSMS;
    private Boolean notifyViaPUSH;
    private Boolean dontShowToExternal;
    private Boolean useLastEEModeForPlan;
    private String remarks;
    private Date updateTime;
    private Long contractId;
    private Date contractTime;
    private Integer contractState;
    private Integer payForSMS;
    private Integer freePayMaxCount;
    private Integer freePayCount;
    private Date lastFreePayTime;
    private Integer discountMode;
    private Long balance;
    private Long subBalance0;
    private Long subBalance1;

    private Boolean wasSuspended;

    private Long limit;
    private Long expenditureLimit;
    private String clientGroupName;
    private String middleGroup;
    private Long externalId;
    private String clientGUID;
    private List<BankSubscription> bankSubscriptions;
    private Integer gender;
    private Date birthDate;
    private String benefitOnAdmission;
    private String photoURL;


    private final ClientGenderMenu clientGenderMenu = new ClientGenderMenu();

    public ClientGenderMenu getClientGenderMenu() {
        return clientGenderMenu;
    }

    public Long getExternalId() {
        return externalId;
    }

    public String getClientGUID() {
        return clientGUID;
    }

    // Kadyrov (22.12.2011)
    private String san;
    private String guardsan;

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

    public String getClientGroupName() {
        return clientGroupName;
    }

    public String getMiddleGroup() {
        return middleGroup;
    }

    private final ClientCardListViewPage clientCardListViewPage = new ClientCardListViewPage();
    private final ClientNotificationSettingViewPage clientNotificationSettingViewPage = new ClientNotificationSettingViewPage();

    public String getPageFilename() {
        return "client/view";
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

    public Long getExpenditureLimit() {
        return expenditureLimit;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public PersonData getPerson() {
        return person;
    }

    public PersonData getContractPerson() {
        return contractPerson;
    }

    public Integer getFlags() {
        return flags;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getNotifyViaEmail() {
        return notifyViaEmail;
    }

    public Boolean getNotifyViaSMS() {
        return notifyViaSMS;
    }

    public Boolean getNotifyViaPUSH() {
        return notifyViaPUSH;
    }

    public Boolean getDontShowToExternal() {
        return dontShowToExternal;
    }

    public Boolean getUseLastEEModeForPlan() {
        return useLastEEModeForPlan;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Long getContractId() {
        return contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public Integer getContractState() {
        return contractState;
    }

    public Integer getPayForSMS() {
        return payForSMS;
    }

    public Integer getFreePayMaxCount() {
        return freePayMaxCount;
    }

    public Integer getFreePayCount() {
        return freePayCount;
    }

    public Date getLastFreePayTime() {
        return lastFreePayTime;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public ClientCardListViewPage getClientCardListViewPage() {
        return clientCardListViewPage;
    }

    public ClientNotificationSettingViewPage getClientNotificationSettingPage () {
        return clientNotificationSettingViewPage;
    }

		public Boolean getWasSuspended() {
        return wasSuspended;
    }

    public void setWasSuspended(Boolean wasSuspended) {
        this.wasSuspended = wasSuspended;
    }

    public List<BankSubscription> getBankSubscriptions() {
        return bankSubscriptions;
    }

    public void setBankSubscriptions(List<BankSubscription> bankSubscriptions) {
        this.bankSubscriptions = bankSubscriptions;
    }

    public String getBenefitOnAdmission() {
        return benefitOnAdmission;
    }

    public void setBenefitOnAdmission(String benefitOnAdmission) {
        this.benefitOnAdmission = benefitOnAdmission;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    @SuppressWarnings("unchecked")
    public void fill(Session session, Long idOfClient) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);
        Org org = client.getOrg();
        this.idOfClient = idOfClient;
        this.idOfOrg = org.getIdOfOrg();
        this.orgShortName = org.getShortName();
        this.person = new PersonData(client.getPerson());
        this.contractPerson = new PersonData(client.getContractPerson());
        this.flags = client.getFlags();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.mobile = client.getMobile();
        this.fax = client.getFax();
        this.email = client.getEmail();
        this.notifyViaEmail = client.isNotifyViaEmail();
        this.notifyViaSMS = client.isNotifyViaSMS();
        this.notifyViaPUSH = client.isNotifyViaPUSH();
        this.dontShowToExternal = client.isDontShowToExternal();
        this.remarks = client.getRemarks();
        this.updateTime = client.getUpdateTime();
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
        this.payForSMS = client.getPayForSMS();
        this.freePayMaxCount = client.getFreePayMaxCount();
        this.freePayCount = client.getFreePayCount();
        this.lastFreePayTime = client.getLastFreePayTime();
        this.discountMode = client.getDiscountMode();
        this.clientCardListViewPage.fill(client);
        this.clientNotificationSettingViewPage.fill(client);
        this.balance = client.getBalance();
        this.subBalance1 = client.getSubBalance1()==null?0L:client.getSubBalance1();
        this.subBalance0 = this.balance - this.subBalance1;
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();
        this.clientGUID = client.getClientGUID();
        this.externalId = client.getExternalId();
        this.useLastEEModeForPlan = client.isUseLastEEModeForPlan();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.benefitOnAdmission = client.getBenefitOnAdmission();

        // опекуны
        // (Kadyrov D) 23.12.2011
        this.san= client.getSan();
        Set <GuardSan> guardSans = client.getGuardSan();
        this.guardsan="";
        for (GuardSan guard : guardSans) {
            if (this.guardsan.length() > 0) {
                this.guardsan = this.guardsan + ",";
            }
            this.guardsan = this.guardsan + guard.getGuardSan();
        }


        ClientGroup group = client.getClientGroup();
        this.clientGroupName = group==null?"":group.getGroupName();

        this.middleGroup = client.getMiddleGroup();

        // Категории скидок
        this.categoriesDiscounts=new LinkedList<CategoryDiscount>();
        if(!client.getCategories().isEmpty()){
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                String name=categoryDiscount.getCategoryName();
                this.categoriesDiscounts.add(categoryDiscount);
            }
        }
        Criteria bankSubscriptionCriteria = session.createCriteria(BankSubscription.class);
        bankSubscriptionCriteria.add(Restrictions.eq("client", client))
                .add(Restrictions.isNotNull("activationDate"));
        this.bankSubscriptions = (List<BankSubscription>) bankSubscriptionCriteria.list();

        this.wasSuspended = DAOUtils.wasSuspendedLastSubscriptionFeedingByClient(session, idOfClient);

        this.clientGuardianItems = loadGuardiansByClient(session, idOfClient);

        this.clientWardItems = loadWardsByClient(session, idOfClient);

        try {
            ClientPhoto clientPhoto = ImageUtils.findClientPhoto(session, client.getIdOfClient());
            this.photoURL = ImageUtils.getPhotoURL(client, clientPhoto, ImageUtils.ImageSize.MEDIUM.getValue(), false);
        } catch (Exception e){
            this.photoURL = ImageUtils.getDefaultImageURL();
        }


        // Категории скидок старое не используется
        // TODO: переписать использутеся кривая логика с return! По этому не рекомендуется писать ниже код
        //categoryDiscountNames = new ArrayList<String>();
        List clientCategories = Arrays.asList(client.getCategoriesDiscounts().split(","));
        if (clientCategories.isEmpty())
            return;

        List<Long> idOfCategoryDiscountList = new ArrayList<Long>();

        for (Object clientCategory : clientCategories) {
            if (clientCategory.toString().isEmpty())
                return;
            Long idOfCategoryDiscount = Long.parseLong(clientCategory.toString());
            idOfCategoryDiscountList.add(idOfCategoryDiscount);
        }

        Criteria categoryDiscountCriteria = session.createCriteria(CategoryDiscount.class);
        categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryDiscountList));
        List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
          /*
        for (CategoryDiscount categoryDiscount : categoryDiscountList) {
            categoryDiscountNames.add(categoryDiscount.getCategoryName());
        }   */

        //Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        //criteria.add(Restrictions.eq("idOfClient", idOfClient));
        //criteria.setProjection(Projections.projectionList()
        //        .add(Projections.))
        //SubscriptionFeeding subscriptionFeeding =



    }

    private List<ClientGuardianItem> clientGuardianItems;

    public List<ClientGuardianItem> getClientGuardianItems() {
        return clientGuardianItems;
    }

    private List<ClientGuardianItem> clientWardItems;

    public List<ClientGuardianItem> getClientWardItems() {
        return clientWardItems;
    }

}