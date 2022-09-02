package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;
import ru.axetta.ecafe.processor.core.service.DulDetailService;

import java.util.Date;

public class ClientOfAnotherOrgItem {
    private long orgOwner;
    private long idOfClient;
    private long version;
    private String firstName;
    private String surname;
    private String secondName;
    private String idDocument;
    private String address;
    private String phone;
    private String mobile;
    private String middleGroup;
    private String email;
    private String fax;
    private int contractState;
    private long contractId;
    private Integer freePayMaxCount;
    private String categoriesDiscounts;
    private boolean notifyViaEmail;
    private boolean notifyViaSMS;
    private boolean notifyViaPUSH;
    private String remarks;
    private boolean canConfirmGroupPayment;
    private int discountMode;
    private String guid;
    private String meshGUID;
    private boolean tempClient;
    private int clientType;
    private boolean isUseLastEEModeForPlan;
    private Integer gender;
    private Date birthDate;
    private String categoriesDiscountsDSZN;
    private Date lastDiscountsUpdate;
    private Date disablePlanCreationDate;
    private Date disablePlanEndDate;
    private String ageTypeGroup;
    private Long balanceToNotify;
    private String san;
    private Boolean specialMenu;
    private String passportNumber;
    private String passportSeries;
    private Boolean multiCardMode;
    private String parallel;
    private String ssoId;
    private Boolean dontShowToExternal;
    private String groupName;
    private Long idOfClientGroup;
    private Boolean isOnOutByVideo;
    private Long maxDailyLimit;

    public ClientOfAnotherOrgItem() {
    }

    public ClientOfAnotherOrgItem(Client client, int clientType) {
        this.orgOwner = client.getOrg().getIdOfOrg();
        this.idOfClient = client.getIdOfClient();
        this.version = client.getClientRegistryVersion();
        this.firstName = client.getPerson().getFirstName();
        this.surname = client.getPerson().getSurname();
        this.secondName = client.getPerson().getSecondName();
        this.idDocument = client.getPerson().getIdDocument();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.mobile = client.getMobile();
        this.middleGroup = client.getMiddleGroup();
        this.fax = client.getFax();
        this.email = client.getEmail();
        this.contractState = client.getContractState();
        this.contractId = client.getContractId();
        this.freePayMaxCount = client.getFreePayMaxCount();
        this.categoriesDiscounts = DiscountManager.getClientDiscountsAsString(client);
        this.notifyViaEmail = client.isNotifyViaEmail();
        this.notifyViaSMS = client.isNotifyViaSMS();
        this.notifyViaPUSH = client.isNotifyViaPUSH();
        this.remarks = client.getRemarks();
        this.canConfirmGroupPayment = client.getCanConfirmGroupPayment();
        this.discountMode = client.getDiscountMode();
        this.guid = client.getClientGUID();
        this.meshGUID = client.getMeshGUID();
        this.clientType = clientType;
        ClientGroup clientGroup = client.getClientGroup();
        if (clientGroup != null) {
            this.groupName = clientGroup.getGroupName(); // lazy load
            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
        }
        this.isUseLastEEModeForPlan = client.isUseLastEEModeForPlan() != null && client.isUseLastEEModeForPlan();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.categoriesDiscountsDSZN = DiscountManager.getClientDiscountsDSZNAsString(client);
        this.lastDiscountsUpdate = client.getLastDiscountsUpdate();
        this.disablePlanCreationDate = client.getDisablePlanCreationDate();
        this.disablePlanEndDate = client.getDisablePlanEndDate();
        this.ageTypeGroup = client.getAgeTypeGroup();
        this.balanceToNotify = client.getBalanceToNotify();
        this.san = client.getSan();
        this.specialMenu = client.getSpecialMenu();
        DulDetail dulDetailPassport = RuntimeContext.getAppContext().getBean(DulDetailService.class)
                .getPassportDulDetailByClient(client, Client.PASSPORT_RF_TYPE);
        this.passportNumber = dulDetailPassport == null ? "" : dulDetailPassport.getNumber();
        this.passportSeries = dulDetailPassport == null ? "" : dulDetailPassport.getSeries();
        this.multiCardMode = client.activeMultiCardMode();
        this.parallel = client.getParallel();
        this.ssoId = client.getSsoid();
        this.dontShowToExternal = client.isDontShowToExternal();
        this.isOnOutByVideo = client.getConfirmVisualRecognition();
        this.maxDailyLimit = client.getExpenditureLimit();
    }

    public long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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

    public String getMiddleGroup() {
        return middleGroup;
    }

    public void setMiddleGroup(String middleGroup) {
        this.middleGroup = middleGroup;
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

    public int getContractState() {
        return contractState;
    }

    public void setContractState(int contractState) {
        this.contractState = contractState;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public Integer getFreePayMaxCount() {
        return freePayMaxCount;
    }

    public void setFreePayMaxCount(Integer freePayMaxCount) {
        this.freePayMaxCount = freePayMaxCount;
    }

    public String getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(String categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
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

    public boolean isNotifyViaPUSH() {
        return notifyViaPUSH;
    }

    public void setNotifyViaPUSH(boolean notifyViaPUSH) {
        this.notifyViaPUSH = notifyViaPUSH;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isCanConfirmGroupPayment() {
        return canConfirmGroupPayment;
    }

    public void setCanConfirmGroupPayment(boolean canConfirmGroupPayment) {
        this.canConfirmGroupPayment = canConfirmGroupPayment;
    }

    public int getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(int discountMode) {
        this.discountMode = discountMode;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMeshGUID() {
        return meshGUID;
    }

    public void setMeshGUID(String meshGUID) {
        this.meshGUID = meshGUID;
    }

    public boolean isTempClient() {
        return tempClient;
    }

    public void setTempClient(boolean tempClient) {
        this.tempClient = tempClient;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public boolean isUseLastEEModeForPlan() {
        return isUseLastEEModeForPlan;
    }

    public void setUseLastEEModeForPlan(boolean useLastEEModeForPlan) {
        isUseLastEEModeForPlan = useLastEEModeForPlan;
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

    public String getCategoriesDiscountsDSZN() {
        return categoriesDiscountsDSZN;
    }

    public void setCategoriesDiscountsDSZN(String categoriesDiscountsDSZN) {
        this.categoriesDiscountsDSZN = categoriesDiscountsDSZN;
    }

    public Date getLastDiscountsUpdate() {
        return lastDiscountsUpdate;
    }

    public void setLastDiscountsUpdate(Date lastDiscountsUpdate) {
        this.lastDiscountsUpdate = lastDiscountsUpdate;
    }

    public Date getDisablePlanCreationDate() {
        return disablePlanCreationDate;
    }

    public void setDisablePlanCreationDate(Date disablePlanCreationDate) {
        this.disablePlanCreationDate = disablePlanCreationDate;
    }

    public Date getDisablePlanEndDate() {
        return disablePlanEndDate;
    }

    public void setDisablePlanEndDate(Date disablePlanEndDate) {
        this.disablePlanEndDate = disablePlanEndDate;
    }

    public String getAgeTypeGroup() {
        return ageTypeGroup;
    }

    public void setAgeTypeGroup(String ageTypeGroup) {
        this.ageTypeGroup = ageTypeGroup;
    }

    public Long getBalanceToNotify() {
        return balanceToNotify;
    }

    public void setBalanceToNotify(Long balanceToNotify) {
        this.balanceToNotify = balanceToNotify;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public Boolean getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Boolean specialMenu) {
        this.specialMenu = specialMenu;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public Boolean getMultiCardMode() {
        return multiCardMode;
    }

    public void setMultiCardMode(Boolean multiCardMode) {
        this.multiCardMode = multiCardMode;
    }

    public String getParallel() {
        return parallel;
    }

    public void setParallel(String parallel) {
        this.parallel = parallel;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public Boolean getDontShowToExternal() {
        return dontShowToExternal;
    }

    public void setDontShowToExternal(Boolean dontShowToExternal) {
        this.dontShowToExternal = dontShowToExternal;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Boolean getOnOutByVideo() {
        return isOnOutByVideo;
    }

    public void setOnOutByVideo(Boolean onOutByVideo) {
        isOnOutByVideo = onOutByVideo;
    }

    public Long getMaxDailyLimit() {
        return maxDailyLimit;
    }

    public void setMaxDailyLimit(Long maxDailyLimit) {
        this.maxDailyLimit = maxDailyLimit;
    }
}
