/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientDiscountItem;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.web.partner.oku.OkuDAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.items.MigrantItem;

import org.apache.commons.collections.CollectionUtils;
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

    public Long getBalanceToNotify() {
        return balanceToNotify;
    }

    public void setBalanceToNotify(Long balanceToNotify) {
        this.balanceToNotify = balanceToNotify;
    }

    public Date getDisablePlanEndDate() {
        return disablePlanEndDate;
    }

    public void setDisablePlanEndDate(Date disablePlanEndDate) {
        this.disablePlanEndDate = disablePlanEndDate;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getCardRequest() {
        return cardRequest;
    }

    public void setCardRequest(String cardRequest) {
        this.cardRequest = cardRequest;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getBalanceHold() {
        return balanceHold;
    }

    public void setBalanceHold(String balanceHold) {
        this.balanceHold = balanceHold;
    }

    public Boolean getMultiCardMode() {
        return multiCardMode;
    }

    public void setMultiCardMode(Boolean multiCardMode) {
        this.multiCardMode = multiCardMode;
    }

    public Boolean getVisitsSections() {
        return visitsSections;
    }

    public void setVisitsSections(Boolean visitsSections) {
        this.visitsSections = visitsSections;
    }

    public String getParallel() {
        return parallel;
    }

    public void setParallel(String parallel) {
        this.parallel = parallel;
    }

    public Boolean getCanConfirmGroupPayment() {
        return canConfirmGroupPayment;
    }

    public void setCanConfirmGroupPayment(Boolean canConfirmGroupPayment) {
        this.canConfirmGroupPayment = canConfirmGroupPayment;
    }

    public void setConfirmVisualRecognition(Boolean confirmVisualRecognition) {
        this.confirmVisualRecognition = confirmVisualRecognition;
    }

    public Boolean getConfirmVisualRecognition() {
        return confirmVisualRecognition;
    }

    public Boolean getInformedSpecialMenu() {
        return informedSpecialMenu;
    }

    public void setInformedSpecialMenu(Boolean informedSpecialMenu) {
        this.informedSpecialMenu = informedSpecialMenu;
    }

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
    private String meshGUID;
    private String clientSSOID;
    private String clientIacRegId;
    private Integer gender;
    private Date birthDate;
    private Boolean disablePlanCreation;
    private Date disablePlanCreationDate;
    private Date disablePlanEndDate;
    private String ageTypeGroup;
    private String photoURL;
    private Long balanceToNotify;
    private Date lastConfirmMobile;
    private Boolean specialMenu;
    private Boolean informedSpecialMenu;
    private String passportNumber;
    private String passportSeries;
    private String cardRequest;
    private String balanceHold;
    private Boolean multiCardMode;
    private Boolean visitsSections;
    private String parallel;
    private Boolean canConfirmGroupPayment;
    private Boolean confirmVisualRecognition;
    private Boolean userOP;
    private Long idOfClientGroup;

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

    public String getMeshGUID() {
        return meshGUID;
    }

    public void setMeshGUID(String meshGUID) {
        this.meshGUID = meshGUID;
    }

    public String getClientSSOID() {
        return clientSSOID;
    }


    // Kadyrov (22.12.2011)
    private String san;

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
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

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Boolean getDisablePlanCreation() {
        return disablePlanCreation;
    }

    public void setDisablePlanCreation(Boolean disablePlanCreation) {
        this.disablePlanCreation = disablePlanCreation;
    }

    public Date getDisablePlanCreationDate() {
        return disablePlanCreationDate;
    }

    public void setDisablePlanCreationDate(Date disablePlanCreationDate) {
        this.disablePlanCreationDate = disablePlanCreationDate;
    }

    public String getAgeTypeGroup() {
        return ageTypeGroup;
    }

    public void setAgeTypeGroup(String ageTypeGroup) {
        this.ageTypeGroup = ageTypeGroup;
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

    public boolean getOldFlagsShow() {
        if (clientGuardianItems == null) return true;
        boolean result = true;
        for (ClientGuardianItem item : clientGuardianItems) {
            if (!item.getDisabled()) return false;
        }
        return result;
    }

    public Boolean getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Boolean specialMenu) {
        this.specialMenu = specialMenu;
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
        this.clientCardListViewPage.fill(client);
        this.clientNotificationSettingViewPage.fill(client);
        this.balance = client.getBalance();
        this.subBalance1 = client.getSubBalance1()==null?0L:client.getSubBalance1();
        this.subBalance0 = this.balance - this.subBalance1;
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();
        this.clientGUID = client.getClientGUID();
        this.meshGUID = client.getMeshGUID();
        this.clientSSOID = client.getSsoid();
        this.clientIacRegId = client.getIacRegId();
        this.externalId = client.getExternalId();
        this.useLastEEModeForPlan = client.isUseLastEEModeForPlan();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.disablePlanCreationDate = client.getDisablePlanCreationDate();
        this.disablePlanCreation = this.disablePlanCreationDate != null;
        this.disablePlanEndDate = client.getDisablePlanEndDate();
        this.ageTypeGroup = client.getAgeTypeGroup();
        this.balanceToNotify = client.getBalanceToNotify();
        this.lastConfirmMobile = client.getLastConfirmMobile();
        this.multiCardMode = client.activeMultiCardMode();
        this.clientDiscountItems = buildClientDiscountItem(session, client);
        this.canConfirmGroupPayment = client.getCanConfirmGroupPayment();
        this.confirmVisualRecognition = client.getConfirmVisualRecognition();

        // опекуны
        // (Kadyrov D) 23.12.2011
        this.san= client.getSan();


        ClientGroup group = client.getClientGroup();
        this.clientGroupName = group==null?"":group.getGroupName();
        this.idOfClientGroup = group == null ? null : group.getCompositeIdOfClientGroup().getIdOfClientGroup();

        this.middleGroup = client.getMiddleGroup();

        this.wasSuspended = DAOUtils.wasSuspendedLastSubscriptionFeedingByClient(session, idOfClient);

        this.clientGuardianItems = loadGuardiansByClient(session, idOfClient);

        this.clientWardItems = loadWardsByClient(session, idOfClient);

        List<Migrant> migrants = MigrantsUtils.getAllMigrantsByIdOfClient(session, idOfClient);
        clientSectionsItems = buildClientSectionsItem(session, migrants);
        visitsSections = !clientSectionsItems.isEmpty();

        try {
            ClientPhoto clientPhoto = ImageUtils.findClientPhoto(session, client.getIdOfClient());
            this.photoURL = ImageUtils.getPhotoURL(client, clientPhoto, ImageUtils.ImageSize.MEDIUM.getValue(), false);
        } catch (Exception e){
            this.photoURL = ImageUtils.getDefaultImageURL();
        }

        this.specialMenu = client.getSpecialMenu();
        this.informedSpecialMenu = ClientManager.getInformedSpecialMenu(session, client.getIdOfClient(), null);
        this.passportNumber = client.getPassportNumber();
        this.passportSeries = client.getPassportSeries();
        this.cardRequest = DAOUtils.getCardRequestString(session, client);
        this.parallel = client.getParallel();
        this.userOP = client.getUserOP();

        balanceHold = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).getBalanceHoldListAsString(session, client.getIdOfClient());

    }

    public static List<ClientDiscountItem> buildClientDiscountItem(Session session, Client client) {
        List<ClientDiscountItem> result = new LinkedList<ClientDiscountItem>();

        Set<CategoryDiscount> clientDiscountsList = client.getCategories();

        Criteria clientDiscountsDTiSZNCriteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        clientDiscountsDTiSZNCriteria.add(Restrictions.eq("client", client));
        clientDiscountsDTiSZNCriteria.add(Restrictions.eq("archived", false));
        List<ClientDtisznDiscountInfo> clientDiscountsDTiSZNList = clientDiscountsDTiSZNCriteria.list();

        if(CollectionUtils.isEmpty(clientDiscountsList) && CollectionUtils.isEmpty(clientDiscountsDTiSZNList)){
            return Collections.emptyList();
        } else if(CollectionUtils.isEmpty(clientDiscountsList)){
            for(ClientDtisznDiscountInfo discountInfo : clientDiscountsDTiSZNList){
                ClientDiscountItem item = new ClientDiscountItem(null, null, discountInfo.getIdOfClientDTISZNDiscountInfo(),
                        discountInfo.getDtisznCode(), discountInfo.getDtisznDescription(), discountInfo.getStatus().getValue(), discountInfo.getDateStart(),
                        discountInfo.getDateEnd(), client.getLastDiscountsUpdate(), client.getDiscountMode());
                result.add(item);
            }
        } else if(CollectionUtils.isEmpty(clientDiscountsDTiSZNList)){
            for(CategoryDiscount categoryDiscount : clientDiscountsList){
                ClientDiscountItem item = new ClientDiscountItem(categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount.getCategoryName(),
                        null, null, null, null, null, null, client.getLastDiscountsUpdate(),
                        client.getDiscountMode());
                result.add(item);
            }
        } else {
            for (CategoryDiscount categoryDiscount : clientDiscountsList) {
                boolean findSuitable = false;
                for (ClientDtisznDiscountInfo discountInfo : clientDiscountsDTiSZNList) {
                    if(findSuitable){
                        break;
                    }
                    for (CategoryDiscountDSZN discountDSZN : categoryDiscount.getCategoriesDiscountDSZN()) {
                        if (discountDSZN.getCode().equals(discountInfo.getDtisznCode().intValue())) {
                            ClientDiscountItem item = new ClientDiscountItem(categoryDiscount.getIdOfCategoryDiscount(),
                                    categoryDiscount.getCategoryName(), discountInfo.getIdOfClientDTISZNDiscountInfo(),
                                    discountInfo.getDtisznCode(), discountInfo.getDtisznDescription(),
                                    discountInfo.getStatus().getValue(), discountInfo.getDateStart(), discountInfo.getDateEnd(), client.getLastDiscountsUpdate(),
                                    client.getDiscountMode());
                            result.add(item);
                            findSuitable = true;
                            break;
                        }
                    }
                }
                if(!findSuitable){
                    ClientDiscountItem item = new ClientDiscountItem(categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount.getCategoryName(),
                            null, null, null, null, null, null, client.getLastDiscountsUpdate(),
                            client.getDiscountMode());
                    result.add(item);
                }
            }
            for(ClientDtisznDiscountInfo discountInfo : clientDiscountsDTiSZNList){
                boolean findSuitableItem = false;
                for(ClientDiscountItem item : result){
                    Long currentIdOfClientDTiSZNDiscountInfo = item.getIdOfClientDTiSZNDiscountInfo();
                     if(currentIdOfClientDTiSZNDiscountInfo != null && currentIdOfClientDTiSZNDiscountInfo.equals(discountInfo.getIdOfClientDTISZNDiscountInfo())){
                        findSuitableItem = true;
                        break;
                    }
                }
                if(!findSuitableItem){
                    ClientDiscountItem item = new ClientDiscountItem(null, null, discountInfo.getIdOfClientDTISZNDiscountInfo(),
                            discountInfo.getDtisznCode(), discountInfo.getDtisznDescription(), discountInfo.getStatus().getValue(), discountInfo.getDateStart(),
                            discountInfo.getDateEnd(), client.getLastDiscountsUpdate(), client.getDiscountMode());
                    result.add(item);
                }
            }
        }
        return result;
    }

    private List<MigrantItem> buildClientSectionsItem(Session session, List<Migrant> migrants) {
        List<MigrantItem> result = new LinkedList<MigrantItem>();
        if(migrants != null){
            for(Migrant migrant : migrants){
                MigrantItem item = new MigrantItem(session, migrant);
                result.add(item);
            }
        }
        return result;
    }

    private List<ClientGuardianItem> clientGuardianItems;

    public List<ClientGuardianItem> getClientGuardianItems() {
        return clientGuardianItems;
    }

    private List<ClientGuardianItem> clientWardItems;

    public List<ClientGuardianItem> getClientWardItems() {
        return clientWardItems;
    }

    private List<MigrantItem> clientSectionsItems;

    public List<MigrantItem> getClientSectionsItems(){
        return clientSectionsItems;
    }

    private List<ClientDiscountItem> clientDiscountItems;

    public List<ClientDiscountItem> getClientDiscountItems() {
        return clientDiscountItems;
    }

    public Date getLastConfirmMobile() {
        return lastConfirmMobile;
    }

    public void setLastConfirmMobile(Date lastConfirmMobile) {
        this.lastConfirmMobile = lastConfirmMobile;
    }

    public boolean isLastConfirmMobileEmpty() {
        return getLastConfirmMobile() == null;
    }

    public Boolean getUserOP() {
        return userOP;
    }

    public void setUserOP(Boolean userOP) {
        this.userOP = userOP;
    }

    public String getClientIacRegId() {
        return clientIacRegId;
    }

    public void setClientIacRegId(String clientIacRegId) {
        this.clientIacRegId = clientIacRegId;
    }

    public boolean isEligibleToViewUserOP() {
        if (null == this.idOfClientGroup) {
            return false;
        }
        return OkuDAOService.getClientGroupList().contains(this.idOfClientGroup);
    }
}