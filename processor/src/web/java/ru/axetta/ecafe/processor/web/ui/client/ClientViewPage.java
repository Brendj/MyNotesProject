/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientDiscountItem;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.items.MigrantItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
    private List<BankSubscription> bankSubscriptions;
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
    private String passportNumber;
    private String passportSeries;
    private String cardRequest;
    private String balanceHold;
    private Boolean multiCardMode;
    private Boolean visitsSections;
    private String parallel;

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

        Criteria bankSubscriptionCriteria = session.createCriteria(BankSubscription.class);
        bankSubscriptionCriteria.add(Restrictions.eq("client", client))
                .add(Restrictions.isNotNull("activationDate"));
        this.bankSubscriptions = (List<BankSubscription>) bankSubscriptionCriteria.list();

        this.wasSuspended = DAOUtils.wasSuspendedLastSubscriptionFeedingByClient(session, idOfClient);

        this.clientGuardianItems = loadGuardiansByClient(session, idOfClient);

        this.clientWardItems = loadWardsByClient(session, idOfClient);

        List<Migrant> migrants = MigrantsUtils.getActiveMigrantsByIdOfClient(session, idOfClient);
        clientSectionsItems = buildClientSectionsItem(session, migrants);
        visitsSections = !clientSectionsItems.isEmpty();

        try {
            ClientPhoto clientPhoto = ImageUtils.findClientPhoto(session, client.getIdOfClient());
            this.photoURL = ImageUtils.getPhotoURL(client, clientPhoto, ImageUtils.ImageSize.MEDIUM.getValue(), false);
        } catch (Exception e){
            this.photoURL = ImageUtils.getDefaultImageURL();
        }

        this.specialMenu = client.getSpecialMenu();
        this.passportNumber = client.getPassportNumber();
        this.passportSeries = client.getPassportSeries();
        this.cardRequest = DAOUtils.getCardRequestString(session, client);
        this.parallel = client.getParallel();

        balanceHold = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).getBalanceHoldListAsString(session, client.getIdOfClient());

    }

    public static List<ClientDiscountItem> buildClientDiscountItem(Session session, Client client) {
        if(StringUtils.isEmpty(client.getCategoriesDiscounts()) && StringUtils.isEmpty(client.getCategoriesDiscountsDSZN())){
            return Collections.emptyList();
        }
        List<ClientDiscountItem> result = new LinkedList<ClientDiscountItem>();

        Query query = session.createSQLQuery(
                "select\n"
                        + "case \n"
                        + "    when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cdDSZN.idofcategorydiscount is null then cd.idofcategorydiscount\n"
                        + "\telse null\n"
                        + "end as idofcategorydiscount,\n"
                        + "case \n"
                        + "    when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cdDSZN.idofcategorydiscount is null then cd.categoryName\n"
                        + "\telse null \n"
                        + "end as categoryName,\n"
                        + "case \n"
                        + "    when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.idofclientdtiszndiscountinfo \n"
                        + "    else null \n"
                        + "end as idofclientdtiszndiscountinfo,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.dtiszncode \n"
                        + "     else null \n"
                        + "end as dtiszncode, \n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.dtisznDescription \n"
                        + "     else null \n"
                        + "end as dtisznDescription,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.status \n"
                        + "     else null \n"
                        + "end as status,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.datestart \n"
                        + "     else null \n"
                        + "end as datestart,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then ci.dateend \n"
                        + "     else null \n"
                        + "end as dateend\n"
                        + "from cf_clients c  \n"
                        + "join cf_categorydiscounts cd on cd.idofcategorydiscount in ( \n"
                        + " select cast (unnest(string_to_array(c.categoriesdiscounts, ',')) as bigint) from cf_clients c  \n"
                        + " where c.idofclient = :idOfClient\n"
                        + ")  \n"
                        + "left join cf_client_dtiszn_discount_info ci on ci.idofclient = c.idofclient \n"
                        + "left join CF_CategoryDiscounts_DSZN cdDSZN on ci.dtiszncode = cdDSZN.code and cd.idofcategorydiscount = cdDSZN.idofcategorydiscount\n"
                        + "where c.idofclient = :idOfClient \n"
                        + "union\t  \n"
                        + "select \n"
                        + "case\n"
                        + "\twhen cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then cd.idofcategorydiscount\n"
                        + "\telse null\n"
                        + "end as idofcategorydiscount, \n"
                        + "case\n"
                        + "\twhen cd.idofcategorydiscount = cdDSZN.idofcategorydiscount then cd.categoryName\n"
                        + "\telse null \n"
                        + "end as categoryName,\n"
                        + "case \n"
                        + "    when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.idofclientdtiszndiscountinfo \n"
                        + "    else null \n"
                        + "end as idofclientdtiszndiscountinfo,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.dtiszncode \n"
                        + "     else null \n"
                        + "end as dtiszncode, \n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.dtisznDescription \n"
                        + "     else null \n"
                        + "end as dtisznDescription,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.status \n"
                        + "     else null \n"
                        + "end as status,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.datestart \n"
                        + "     else null \n"
                        + "end as datestart,\n"
                        + "case \n"
                        + "     when cd.idofcategorydiscount = cdDSZN.idofcategorydiscount or cd.idofcategorydiscount is null then ci.dateend \n"
                        + "     else null \n"
                        + "end as dateend\n"
                        + "from cf_clients c  \n"
                        + "join cf_client_dtiszn_discount_info ci on ci.idofclient = c.idofclient \n"
                        + "join CF_CategoryDiscounts_DSZN cdDSZN on ci.dtiszncode = cdDSZN.code\n"
                        + "left join cf_categorydiscounts cd on cd.idofcategorydiscount in ( \n"
                        + " select cast (unnest(string_to_array(c.categoriesdiscounts, ',')) as bigint) from cf_clients c  \n"
                        + " where c.idofclient = :idOfClient\n"
                        + ")  and cd.idofcategorydiscount = cdDSZN.idofcategorydiscount\n"
                        + "where c.idofclient = :idOfClient\n"
                        + "order by 1"
        );

        query.setParameter("idOfClient", client.getIdOfClient());

        List<Object[]> dataFromDB = query.list();
        for(Object[] row : dataFromDB){
            Long idOfCategoryDiscount = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
            String categoryName = StringUtils.defaultString((String) row[1]);
            Long idOfClientDTiSZNDiscountInfo = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[2]);
            Long code = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[3]);
            String descriptionDSZN = (String) row[4];
            Integer status = (Integer) row[5];
            Date dateStart = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[6]);
            Date dateEnd = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[7]);

            ClientDiscountItem item = new ClientDiscountItem(idOfCategoryDiscount, categoryName, idOfClientDTiSZNDiscountInfo, code,
                    descriptionDSZN, status, dateStart, dateEnd, client.getLastDiscountsUpdate(), client.getDiscountMode());
            result.add(item);
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
}