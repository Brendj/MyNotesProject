/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.client.items.ClientDiscountItem;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.ClientParallel;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.service.DulDetailService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.web.internal.GuardianResponse;
import ru.axetta.ecafe.processor.web.internal.ModifyContactsItem;
import ru.axetta.ecafe.processor.web.partner.oku.OkuDAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.dul.DulSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;

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
        ClientSelectPage.CompleteHandler,
        DulSelectPage.CompleteHandler,
        MeshClientSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientEditPage.class);
    private final String MESSAGE_GUARDIAN_EXISTS = "Ошибка: выбранный клиент уже присутствует в списке";
    private final String MESSAGE_GUARDIAN_SAME = "Ошибка: выбранный клиент редактируется в данный момент";

    private String fax;

    public ClientEditPage() {
    }

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

    public Date getDisablePlanEndDate() {
        return disablePlanEndDate;
    }

    public void setDisablePlanEndDate(Date disablePlanEndDate) {
        this.disablePlanEndDate = disablePlanEndDate;
    }

    public String getCardRequest() {
        return cardRequest;
    }

    public void setCardRequest(String cardRequest) {
        this.cardRequest = cardRequest;
    }

    public String getBalanceHold() {
        return balanceHold;
    }

    public void setBalanceHold(String balanceHold) {
        this.balanceHold = balanceHold;
    }

    public Boolean getInOrgEnabledMultiCardMode() {
        return inOrgEnabledMultiCardMode;
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

    public Boolean getConfirmVisualRecognition() {
        return confirmVisualRecognition;
    }

    public void setConfirmVisualRecognition(Boolean confirmVisualRecognition) {
        this.confirmVisualRecognition = confirmVisualRecognition;
    }

    public String getClientSSOID() {
        return clientSSOID;
    }

    public void setClientSSOID(String clientSSOID) {
        this.clientSSOID = clientSSOID;
    }

    @Override
    public void completeDulSelection(Session session, DulGuide dulGuide) throws Exception {
        this.dulDetail.add(new DulDetail(this.idOfClient, dulGuide.getDocumentTypeId(), dulGuide));
    }

    @Override
    public void completeMeshClientSelection(Session session, MeshGuardianPerson meshGuardianPerson) throws Exception {
        Client client;
        if (meshGuardianPerson == null)
            throw new Exception("Представитель не выбран");
        ClientGuardianItem clientGuardianItem = null;
        if (meshGuardianPerson.getAlreadyInISPP()) {
            client = ClientManager.findClientByMeshGuid(session, meshGuardianPerson.getMeshGuid());
        } else {
            Org clientOrg = session.get(Org.class, this.org.idOfOrg);
            User user = MainPage.getSessionInstance().getCurrentUser();

            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("Добавление представителя через редактирование клиента");
            clientsMobileHistory.setUser(user);
            clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
            client = getMeshGuardiansService().createGuardianInternalByMeshGuardianPerson(session, meshGuardianPerson, clientOrg,
                    "", ClientCreatedFromType.BACK_OFFICE, clientsMobileHistory);
        }
        if (!guardianExists(client.getIdOfClient())) {
            clientGuardianItem = new ClientGuardianItem(client, meshGuardianPerson.getAlreadyInISPP(), null, ClientManager.getNotificationSettings(),
                    ClientCreatedFromType.DEFAULT, ClientCreatedFromType.BACK_OFFICE,
                    DAOReadonlyService.getInstance().getUserFromSession().getUserName(), false,
                    ClientGuardianRepresentType.UNKNOWN, false, null);
        }
        if (clientGuardianItem != null) {
            clientGuardianItem.setIsNew(true);
            this.clientGuardianItems.add(clientGuardianItem);
        }
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

    private List<NotificationSettingItem> notificationSettings;
    private List<ClientDiscountItem> clientDiscountItems;

    public List<ClientDiscountItem> getClientDiscountItems() {
        return clientDiscountItems;
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
    private String meshGUID;
    private String clientSSOID;
    private String clientIacRegId;
    private Integer discountMode;
    private List<SelectItem> selectItemList = new ArrayList<SelectItem>();
    private String san;
    private final ClientPayForSMSMenu clientPayForSMSMenu = new ClientPayForSMSMenu();
    private final ClientContractStateMenu clientContractStateMenu = new ClientContractStateMenu();
    private Integer freePayMaxCount;
    private Integer gender;
    private Date birthDate;
    private Date lastDiscountsUpdate;
    private Boolean disablePlanCreation;
    private Date disablePlanCreationDate;
    private Date disablePlanEndDate;
    private String ageTypeGroup;
    private Long balanceToNotify;
    private Date lastConfirmMobile;
    private Boolean specialMenu;
    private String cardRequest;
    private String balanceHold;
    private Boolean inOrgEnabledMultiCardMode;
    private String parallel;
    private Boolean canConfirmGroupPayment;
    private Boolean confirmVisualRecognition;
    private Boolean userOP;
    private String middleGroup;
    private List<DulDetail> dulDetail = new ArrayList<>();
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

    public Date getLastDiscountsUpdate() {
        return lastDiscountsUpdate;
    }

    public void setLastDiscountsUpdate(Date lastDiscountsUpdate) {
        this.lastDiscountsUpdate = lastDiscountsUpdate;
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

    public String getMeshGUID() {
        return meshGUID;
    }

    public void setMeshGUID(String meshGUID) {
        this.meshGUID = meshGUID;
    }

    public String getClientIacRegId() {
        return clientIacRegId;
    }

    public void setClientIacRegId(String clientIacRegId) {
        this.clientIacRegId = clientIacRegId;
    }

    public Boolean getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Boolean specialMenu) {
        this.specialMenu = specialMenu;
    }

    public String getMiddleGroup() {
        return middleGroup;
    }

    public void setMiddleGroup(String middleGroup) {
        this.middleGroup = middleGroup;
    }

    public List<DulDetail> getDulDetail() {
        return dulDetail;
    }

    public void setDulDetail(List<DulDetail> dulDetail) {
        this.dulDetail = dulDetail;
    }

    public void fill(Session session, Long idOfClient) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);

        idOfCategoryList.clear();
        for (CategoryDiscount categoryDiscount : client.getCategories()) {
            idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
        }
        Set<ClientNotificationSetting> settings = client.getNotificationSettings();
        notificationSettings = new ArrayList<NotificationSettingItem>();
        for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
            if (predefined.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            notificationSettings.add(new NotificationSettingItem(predefined, settings));
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
        this.idOfClientGroup = client.getClientGroup() == null ? null
                : client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();

        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfClient));

        this.clientGuardianItems = loadGuardiansByClient(session, idOfClient, true);
        this.clientWardItems = loadWardsByClient(session, idOfClient, true);
        this.changePassword = false;
        this.dulDetail = client.getDulDetail().stream().filter(d -> d.getDeleteState() == null
                || !d.getDeleteState()).collect(Collectors.toList());
        this.dulDetail.sort(Comparator.comparing(p -> p.getDulGuide().getName()));
        this.dulDetail.forEach(d -> d.setNew(false));
        fill(session, client);
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
        if (clientGuardianItems == null)
            return true;
        boolean result = true;
        for (ClientGuardianItem item : clientGuardianItems) {
            if (!item.getDisabled())
                return false;
        }
        return result;
    }

    private List<ClientGuardianItem> clientWardItems;

    public List<ClientGuardianItem> getClientWardItems() {
        return clientWardItems;
    }

    public boolean existAddedWards() {
        for (ClientGuardianItem item : clientWardItems) {
            if (item.getIsNew())
                return true;
        }
        return false;
    }

    private boolean isParent(String groupName) {
        return groupName.equalsIgnoreCase(ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            if (typeAddClient == null)
                return;
            if (typeAddClient.equals("guardian")) {
                if (client.isDeletedOrLeaving())
                    printMessage("Выбранный клиент является выбывшим или удаленным и не может быть выбран в качестве представителя");
                if (!guardianExists(idOfClient)) {
                    ClientGuardianItem clientGuardianItem = new ClientGuardianItem(client, false, null, ClientManager.getNotificationSettings(),
                            ClientCreatedFromType.DEFAULT, ClientCreatedFromType.BACK_OFFICE,
                            DAOReadonlyService.getInstance().getUserFromSession().getUserName(), false,
                            ClientGuardianRepresentType.UNKNOWN, false, null);
                    clientGuardianItem.setIsNew(true);
                    clientGuardianItems.add(clientGuardianItem);
                }
            }
            if (typeAddClient.equals("ward")) {
                if (!wardExists(idOfClient)) {
                    ClientGuardianItem clientGuardianItem = new ClientGuardianItem(client, false, null, ClientManager.getNotificationSettings(),
                            ClientCreatedFromType.DEFAULT, ClientCreatedFromType.BACK_OFFICE,
                            DAOReadonlyService.getInstance().getUserFromSession().getUserName(), false,
                            ClientGuardianRepresentType.UNKNOWN, false, null);
                    clientGuardianItem.setIsNew(true);
                    clientWardItems.add(clientGuardianItem);
                }
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
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public void printMessage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "NOT OK", null));
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
        if ((this.org.getIdOfOrg() == null && idOfOrg != null) || (this.org.getIdOfOrg() != null && idOfOrg == null)
                || (this.org.getIdOfOrg() != null && !this.org.getIdOfOrg().equals(idOfOrg))) {
            this.clientGroupName = "";
            this.idOfClientGroup = null;
        }
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
        if (this.email != null && !this.email.isEmpty()) {
            ClientManager.validateEmail(this.email);
        }
        if (discountMode.equals(Client.DISCOUNT_MODE_NONE) && !idOfCategoryList.isEmpty()) {
            idOfCategoryList = Collections.emptyList();
            clientDiscountItems = Collections.emptyList();
        }
        validateExistingGuardians(this.clientGuardianItems);
        validateExistingGuardians(this.clientWardItems);
        ClientManager.validateFio(this.person.surname, this.person.firstName, this.person.secondName);
//        ClientManager.isUniqueFioAndMobileOrEmail(persistenceSession, this.idOfClient, this.person.surname,
//                this.person.firstName, this.mobile, this.email);

        Client client = (Client) persistenceSession.load(Client.class, idOfClient);
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
        String oldMobile = client.getMobile();
        String oldEmail = client.getEmail();
        Person person = client.getPerson();
        Boolean isChangePersonForSaveMk = checkChangePersonForSaveMk(client, person);

        this.person.copyTo(person);
        persistenceSession.update(person);

        Person contractPerson = client.getContractPerson();
        this.contractPerson.copyTo(contractPerson);
        persistenceSession.update(contractPerson);

        Org org = (Org) persistenceSession.load(Org.class, this.org.getIdOfOrg());
        Boolean isReplaceOrg = !(client.getOrg().getIdOfOrg().equals(org.getIdOfOrg()));
        Boolean isFriendlyReplaceOrg = false;
        if (isReplaceOrg) {
            clientMigration = new ClientMigration(client.getOrg());

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Set<Org> orgSet = client.getOrg().getFriendlyOrg();
            for (Org o : orgSet) {
                if (o.getIdOfOrg().equals(org.getIdOfOrg())) {
                    isFriendlyReplaceOrg = true;
                    break;
                }
            }
            runtimeContext.getProcessor().disableClientCardsIfChangeOrg(client, orgSet, org.getIdOfOrg());
            archiveApplicationForFoodWithoutDiscount(client, persistenceSession);
        }
        ClientManager.checkUserOPFlag(persistenceSession, client.getOrg(), org, this.idOfClientGroup, client);
        client.setPerson(person);
        client.setContractPerson(contractPerson);
        client.setClientRegistryVersion(clientRegistryVersion);
        client.setFlags(this.flags);
        client.setAddress(this.address);
        client.setPhone(this.phone);
        ClientsMobileHistory clientsMobileHistory = new ClientsMobileHistory("Изменение клиента через редактирование");
        User user = MainPage.getSessionInstance().getCurrentUser();
        clientsMobileHistory.setUser(user);
        clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
        client.initClientMobileHistory(clientsMobileHistory);
        String ssoidOld = client.getSsoid();
        if (ssoidOld == null)
            ssoidOld = "";
        if (clientSSOID == null)
            clientSSOID = "";
        if (ssoidOld.equals(clientSSOID))
            client.setMobile(mobile);
        else
            client.setMobileNotClearSsoid(mobile);
        client.setFax(this.fax);
        client.setEmail(this.email);
        client.setNotifyViaEmail(this.notifyViaEmail);
        client.setNotifyViaSMS(this.notifyViaSMS);
        client.setNotifyViaPUSH(this.notifyViaPUSH);
        client.setDontShowToExternal(this.dontShowToExternal);
        client.setConfirmVisualRecognition(this.confirmVisualRecognition);
        client.setUseLastEEModeForPlan(this.useLastEEModeForPlan);
        client.setRemarks(this.remarks);
        client.setUpdateTime(new Date());
        client.setContractTime(this.contractTime);
        client.setContractId(this.contractId);
        client.setContractState(this.contractState);
        client.setLimit(this.limit);
        client.setExpenditureLimit(this.expenditureLimit);
        client.setFreePayMaxCount(this.freePayMaxCount);
        client.setSan(StringUtils.isBlank(this.san) ? null : this.san);
        client.setBalanceToNotify(this.balanceToNotify);

        if (this.externalId == null || this.externalId == 0) {
            client.setExternalId(null);
        } else {
            client.setExternalId(this.externalId);
        }
        if (StringUtils.isEmpty(clientGUID)) {
            client.setClientGUID(null);
        } else {
            client.setClientGUID(this.clientGUID);
        }

        if (StringUtils.isEmpty(meshGUID)) {
            client.setMeshGUID(null);
        } else {
            client.setMeshGUID(meshGUID);
        }
        if (client.getClearedSsoid()) {
            clientSSOID = "";
            client.setClearedSsoid(false);
        }
        if (StringUtils.isEmpty(clientSSOID)) {
            client.setSsoid(null);
        } else {
            client.setSsoid(clientSSOID);
        }
        if (this.clientIacRegId == null || this.clientIacRegId.isEmpty()) {
            client.setIacRegId(null);
        } else {
            client.setIacRegId(this.clientIacRegId);
        }
        if (this.changePassword) {
            client.setPassword(this.plainPassword);
        }
        client.setPayForSMS(this.payForSMS);

        /* категори скидок */
        Set<CategoryDiscount> categoryDiscountSet = new HashSet<>();
        if (this.idOfCategoryList.size() != 0) {
            Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
            categoryCriteria.addOrder(Order.asc("idOfCategoryDiscount"));
            for (Object object : categoryCriteria.list()) {
                CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                if (isReplaceOrg && !isFriendlyReplaceOrg && categoryDiscount.getEligibleToDelete()) {
                    DiscountManager.archiveDtisznDiscount(client, persistenceSession, categoryDiscount.getIdOfCategoryDiscount());
                    continue;
                }
                categoryDiscountSet.add(categoryDiscount);
            }
        }

        if (isDiscountsChanged(client, categoryDiscountSet)) {
            try {
                discountMode = categoryDiscountSet.size() == 0 ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;
                DiscountManager.saveDiscountHistory(persistenceSession, client, null, client.getCategories(), categoryDiscountSet, client.getDiscountMode(), discountMode,
                        DiscountChangeHistory.MODIFY_IN_WEBAPP + DAOReadonlyService.getInstance().getUserFromSession().getUserName());
                client.setLastDiscountsUpdate(new Date());
            } catch (Exception ignore) {
            }
        }

        if (CollectionUtils.isEmpty(this.idOfCategoryList)) {
            /* очистить список если он не пуст */
            client.getCategories().clear();
        }

        if (null != discountMode) {
            client.setDiscountMode(discountMode);
        }

        client.setCategories(categoryDiscountSet);

        if (this.disablePlanCreation && this.disablePlanCreationDate == null) {
            client.setDisablePlanCreationDate(new Date());
        }
        if (!this.disablePlanCreation) {
            client.setDisablePlanCreationDate(null);
        }

        /* настройки смс оповещений */
        for (NotificationSettingItem item : notificationSettings) {
            //Для 17 типа мы не можем менять напрямую, только через 11
            if (item.getNotifyType().equals(ClientNotificationSetting.Predefined.SMS_NOTIFY_CULTURE.getValue())) {
                continue;
            }


            ClientNotificationSetting newSetting = new ClientNotificationSetting(client, item.getNotifyType());
            if (item.isEnabled()) {
                client.getNotificationSettings().add(newSetting);
            } else {
                client.getNotificationSettings().remove(newSetting);
            }

            //Если поменяли 11, то меняем и 17 событие
            if (item.getNotifyType().equals(ClientNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue())) {
                ClientNotificationSetting culture = new ClientNotificationSetting(client, ClientNotificationSetting.Predefined.SMS_NOTIFY_CULTURE.getValue());
                if (item.isEnabled()) {
                    client.getNotificationSettings().add(culture);
                } else {
                    client.getNotificationSettings().remove(culture);
                }
            }
        }

        if (isReplaceOrg) {
            if (client.getClientGroup() != null) {
                clientMigration.setOldGroupName(client.getClientGroup().getGroupName());
            }

            if (StringUtils.isEmpty(this.clientGroupName)) {
                this.idOfClientGroup = ClientGroup.Predefined.CLIENT_DISPLACED.getValue();
                this.clientGroupName = ClientGroup.Predefined.CLIENT_DISPLACED.getNameOfGroup();
            }

            ClientGroup clientGroup = DAOUtils
                    .findClientGroupByGroupNameAndIdOfOrg(persistenceSession, org.getIdOfOrg(), this.clientGroupName);
            if (clientGroup == null) {
                clientGroup = DAOUtils.findClientGroupByIdOfClientGroupAndIdOfOrg(persistenceSession, org.getIdOfOrg(),
                        ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
                //Если такая круппа не найдена у целевой ОО, то она созается
                if (clientGroup == null)
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_DISPLACED);
            }

            clientMigration.setNewGroupName(this.clientGroupName);

            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            client.setIdOfClientGroup(this.idOfClientGroup);
            clientMigration.setClient(client);
            clientMigration.setOrg(org);
            clientMigration.setNewContragent(org.getDefaultSupplier());
            clientMigration.setBalance(client.getBalance());
            clientMigration.setComment(ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance()
                    .getExternalContext().getRemoteUser());

            persistenceSession.save(clientMigration);
            if (!isFriendlyReplaceOrg) {
                ClientManager.archiveApplicationForFoodIfClientLeaving(persistenceSession, client, idOfClientGroup);
            }
        } else {
            if ((this.idOfClientGroup != null && client.getIdOfClientGroup() == null) || (this.idOfClientGroup == null
                    && client.getIdOfClientGroup() != null) || (client.getIdOfClientGroup() != null && !client.getIdOfClientGroup().equals(this.idOfClientGroup))) {
                ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
                clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
                clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
                clientGuardianHistory.setReason(String.format("Обновление данных клиента через карточку клиента id = %s",
                        idOfClient));
                ClientManager.createClientGroupMigrationHistory(persistenceSession, client, org, this.idOfClientGroup,
                        this.clientGroupName, ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance()
                                .getExternalContext().getRemoteUser(), clientGuardianHistory);
            }
            client.setIdOfClientGroup(this.idOfClientGroup);
        }
        if (!this.clientWardItems.isEmpty()) {
            if ((this.san == null || this.san.isEmpty()) && (dulDetail == null || dulDetail.isEmpty() || isDulRemoved(dulDetail))) {
                throw new Exception("Не заполнено поле \"СНИЛС\" или \"Документы\"");
            }
        }
        if (this.san != null && !this.san.isEmpty()) {
            this.san = this.san.replaceAll("[\\D]", "");
            ClientManager.validateSan(persistenceSession, this.san, idOfClient);
        }
        client.setSan(StringUtils.isBlank(this.san) ? null : this.san);
        client.setOrg(org);
        client.setGender(this.gender);
        client.setBirthDate(this.birthDate);
        client.setAgeTypeGroup(this.ageTypeGroup);
        client.setSpecialMenu(this.specialMenu);
        client.setParallel(this.parallel);
        if (middleGroup != null && !middleGroup.isEmpty())
            createMiddleGroup(persistenceSession, this.org.idOfOrg, this.clientGroupName, this.middleGroup);
        client.setMiddleGroup(this.middleGroup);

        //Получаем параллель клиента после изменений
        ClientParallel.addFoodBoxModifire(client);

        //Нельзя изменять документы студентов
        if (isStudentGroup()) {
            if (this.dulDetail.stream().anyMatch(DulDetail::getNew))
                throw new Exception("Нельзя изменить документы студентов");
        }

        //Валидация документов
        try {
            List<DulDetail> dulDetailWithoutRemove = dulDetail
                    .stream().filter(d -> d.getDeleteState() == null || !d.getDeleteState()).collect(Collectors.toList());
            getMeshDulDetailService().validateDulList(persistenceSession, dulDetailWithoutRemove, true);
        } catch (DocumentValidateException e) {
            documentExceptionProcess(persistenceSession, e.getDocumentTypeId(), e.getMessage());
        } catch (DocumentExistsException e) {
            documentExceptionProcess(persistenceSession, e.getDocumentTypeId(), e.getMessage());
        }

        //У родителей должны быть опекаемые
        if (isParentGroup() && this.clientWardItems.isEmpty() && this.removeListWardItems.isEmpty()) {
            throw new Exception("Не выбраны \"Опекаемые\"");
        }
        List<ClientGuardianItem> newClientWardItems = new ArrayList<>();
        for (ClientGuardianItem clientWardItem : this.clientWardItems) {
            if (clientWardItem.getIsNew())
                newClientWardItems.add(clientWardItem);
        }
        if (newClientWardItems.size() > 1) {
            throw new Exception("Нельзя добавить больше одного опекаемого за один раз");
        }
        if (!clientWardItems.isEmpty() && !isGuardianGroup(client)) {
            throw new Exception(String.format("Нельзя выбрать группу \"%s\" для представителя", this.clientGroupName));
        }

        //Удаляем связки у клиента без meshGuid без сохранения его в мк
        if (clientWardItems.isEmpty() && client.getMeshGUID() == null && !this.removeListWardItems.isEmpty()) {
            //Удаление связи с опекаемыми в ИСПП
            removeWardsISPP(persistenceSession, client);
            this.removeListWardItems = new ArrayList<>();
        }

        //Поиск измененных элементов
        List<ClientGuardianItem> originalClientGuardianItems = loadGuardiansByClient(persistenceSession, idOfClient, true);
        List<ClientGuardianItem> originalClientWardItems = loadWardsByClient(persistenceSession, idOfClient, true);
        setClientGuardianItemChange(originalClientWardItems, this.clientWardItems);
        setClientGuardianItemChange(originalClientGuardianItems, this.clientGuardianItems);

        //Перенос выбывших в группу родителей при создании связки
        if (this.idOfClientGroup.equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())) {
            if (this.clientWardItems.stream().anyMatch(ClientGuardianItem::getIsNew)) {
                client.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            }
        }

        //Перенос выбывших в группу родителей при создании связки
        List<Client> newGuardians = this.clientGuardianItems
                .stream()
                .filter(ClientGuardianItem::getIsNew)
                .map(c -> persistenceSession.get(Client.class, c.getIdOfClient()))
                .filter(c -> c.getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue()))
                .collect(Collectors.toList());

        for (Client newGuardian : newGuardians) {
            newGuardian.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            persistenceSession.merge(newGuardian);
        }

        //Создание связи с опекунами в ИСПП
        addGuardiansISPP(persistenceSession, client);
        //Удаление связи с опекунами в ИСПП
        removeGuardiansISPP(persistenceSession, client);

        //Явялется ли клиент представителем
        if (!this.clientWardItems.isEmpty() && isGuardianGroup(client) ||
                (this.clientWardItems.isEmpty() && !this.removeListWardItems.isEmpty() && isGuardianGroup(client))) {

            if (birthDate == null) {
                throw new Exception("Не заполнено поле \"Дата рождения\"");
            }
            for (ClientGuardianItem clientWardItem : clientWardItems) {
                if (clientWardItem.getIdOfClient().equals(this.idOfClient))
                    throw new Exception("Персона не может быть представителем самой себя");
            }
            for (ClientGuardianItem clientWardItem : clientWardItems) {
                if (StringUtils.isBlank(clientWardItem.getMeshGuid()))
                    throw new Exception(String.format("У опекаемого %s не указан meshGuid", clientWardItem.getPersonName()));
            }

            //Создание связи с опекаемыми в ИСПП
            addWardsISPP(persistenceSession, client);
            //Удаление связи с опекаемыми в ИСПП
            removeWardsISPP(persistenceSession, client);

            //Создание представителя в МК
            if (client.getMeshGUID() == null) {
                if (this.clientWardItems.size() > 1) {
                    throw new Exception("Нельзя добавить больше одного опекаемого за один раз");
                }
                //Удаляем документы в ИСПП выбранные пользователем для удаления
                deleteDocuments(persistenceSession, client, false);
                //Сохраняем документы в ИСПП
                for (DulDetail dulDetail : this.dulDetail) {
                    if (dulDetail.getNew()) {
                        getMeshDulDetailService().saveDulDetail(persistenceSession, dulDetail, client, false);
                    } else {
                        getMeshDulDetailService().updateDulDetail(persistenceSession, dulDetail, client, false);
                    }
                }

                MeshAgentResponse meshAgentResponse = getMeshGuardiansService()
                        .createPersonOnlyMK(this.person.getFirstName(), this.person.getSecondName(),
                                this.person.getSurname(), this.gender == 0 ? 2 : 1, this.birthDate, this.san, this.mobile, this.email,
                                this.dulDetail, this.clientWardItems.get(0).getRole(), clientWardItems.get(0).getMeshGuid());
                if (meshAgentResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    client.setMeshGUID(meshAgentResponse.getAgentPerson().getMeshGuid());
                    for (DulDetail dulDetail : dulDetail) {
                        //Присваеваем MKid документу
                        for (MeshDocumentResponse meshDocumentResponse : meshAgentResponse.getAgentPerson().getDocument()) {
                            if (meshDocumentResponse.getDocumentTypeId() == dulDetail.getDocumentTypeId()) {
                                dulDetail.setIdMkDocument(meshDocumentResponse.getId());
                                dulDetail.setNew(false);
                                persistenceSession.merge(dulDetail);
                            }
                        }
                    }
                } else {
                    logger.error(String.format("code: %s message: %s", meshAgentResponse.getCode(), meshAgentResponse.getMessage()));
                    throw new Exception(String.format("Ошибка сохранения представителя в МК: %s", meshAgentResponse.getMessage()));
                }
            } else {
                //Изменение представителя в МК
                if (isChangePersonForSaveMk) {
                    updateClientToMK();
                }
                //Изменение контактов представителя в МК
                updateContactToMK(oldMobile, oldEmail);
                //Удаление связи с опекаемыми в МК
                removeWardsMK(client);
                //Создание связи с опекаемыми в МК
                addWardsMK(client);
            }
        }

        //Удаление связи с опекунами в МК
        removeGuardiansMK(client);
        //Создание связи с опекунами в МК
        addGuardiansMK(client);

        //Сохраняем документы в МК только для представителей
        boolean safeToMk = ClientManager.isClientGuardian(persistenceSession, client);
        //Удаляем документы выбранные пользователем для удаления
        deleteDocuments(persistenceSession, client, safeToMk);
        //Сохраняем документы
        saveDocuments(persistenceSession, client, safeToMk);

        DiscountManager.deleteDOUDiscountsIfNeedAfterSetAgeTypeGroup(persistenceSession, client);

        persistenceSession.update(client);

        fill(persistenceSession, client);

        if (client.getSsoid() != null && !client.getSsoid().equals("")) {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            processor.updateNotificationParams(client);
        }
    }

    private void setClientGuardianItemChange(List<ClientGuardianItem> originalClientWardItems, List<ClientGuardianItem> clientGuardianItem) {
        for (ClientGuardianItem originalClientWardItem : originalClientWardItems) {
            for (ClientGuardianItem clientWardItem : clientGuardianItem) {
                try {
                    if (originalClientWardItem.getIdOfClient().equals(clientWardItem.getIdOfClient()) &&
                            !originalClientWardItem.getRole().equals(clientWardItem.getRole())) {
                        clientWardItem.setChange(true);
                    }
                } catch (NullPointerException e) {
                    clientWardItem.setChange(true);
                }
            }
        }
    }

    private Boolean checkChangePersonForSaveMk(Client client, Person person) {
        try {
            return !(person.getFirstName().equals(this.person.firstName) && person.getSecondName().equals(this.person.secondName) &&
                    person.getSurname().equals(this.person.surname) && client.getGender().equals(this.gender) &&
                    Objects.equals(client.getBirthDate(), this.birthDate) && client.getSan().equals(this.san));
        } catch (NullPointerException e) {
            return true;
        }
    }

    private void saveDocuments(Session persistenceSession, Client client, boolean safeToMk) throws Exception {
        for (DulDetail dulDetail : this.dulDetail) {
            try {
                MeshDocumentResponse meshDocumentResponse = null;
                if (dulDetail.getNew()) {
                    meshDocumentResponse = getMeshDulDetailService().saveDulDetail(persistenceSession, dulDetail, client, safeToMk);
                    dulDetail.setNew(false);
                } else if (isDulChange(dulDetail, client)) {
                    meshDocumentResponse = getMeshDulDetailService().updateDulDetail(persistenceSession, dulDetail, client, safeToMk);
                }
                if (meshDocumentResponse != null && !meshDocumentResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка сохранения документов в МК: %s", meshDocumentResponse.getMessage()));
                }
            } catch (DocumentExistsException e) {
                documentExceptionProcess(persistenceSession, e.getDocumentTypeId(), e.getMessage());
            }
        }
    }

    private void deleteDocuments(Session persistenceSession, Client client, Boolean safeToMk) throws Exception {
        // Удаление документов в ИСПП
        for (DulDetail dulDetail : this.dulDetail) {
            if (dulDetail.getDeleteState() != null && dulDetail.getDeleteState()) {
                if (dulDetail.getNew())
                    continue;
                MeshDocumentResponse meshDocumentResponse = getMeshDulDetailService()
                        .deleteDulDetail(persistenceSession, dulDetail, client, safeToMk);
                if (!meshDocumentResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка удаления документов в МК: %s", meshDocumentResponse.getMessage()));
                }
            }
        }
        //Убираем удаленные документы
        if (!this.dulDetail.isEmpty()) {
            this.dulDetail = this.dulDetail
                    .stream().filter(d -> d.getDeleteState() == null || !d.getDeleteState()).collect(Collectors.toList());
        }
    }

    private boolean isDulChange(DulDetail dulDetail, Client client) {
        try {
            Set<DulDetail> originDulDetails = new HashSet<>();
            if (client.getDulDetail() != null) {
                originDulDetails = client.getDulDetail().stream()
                        .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                        .collect(Collectors.toSet());
            }
            for (DulDetail originDul : originDulDetails)
                if (dulDetail.equals(originDul))
                    return false;
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private void documentExceptionProcess(Session persistenceSession, Long documentTypeId, String message) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(DulGuide.class);
        criteria.add(Restrictions.eq("documentTypeId", documentTypeId));
        DulGuide dulGuide = (DulGuide) criteria.uniqueResult();
        throw new Exception(String.format("Ошибка: %s. Документ: %s.", message, dulGuide.getName()));
    }

    private boolean isDulRemoved(List<DulDetail> dulDetail) {
        for (DulDetail detail : dulDetail) {
            if (detail.getDeleteState() == null || !detail.getDeleteState()) {
                return false;
            }
        }
        return true;
    }

    private void updateClientToMK() throws Exception {

        PersonResponse personResponse = getMeshGuardiansService()
                .changePerson(this.meshGUID, this.person.firstName, this.person.secondName,
                        this.person.surname, this.gender == 0 ? 2 : 1, this.birthDate, this.san);
        if (personResponse != null && !personResponse.getCode().equals(GuardianResponse.OK)) {
            logger.error(String.format("code: %s message: %s", personResponse.getCode(), personResponse.getMessage()));
            throw new Exception(String.format("Ошибка изменения представителя в МК: %s", personResponse.getMessage()));
        }
    }

    private void updateContactToMK(String oldMobile, String oldEmail) throws Exception {
        List<ModifyContactsItem> modifyContactsItems = new ArrayList<>();
        modifyContactsItems.add(new ModifyContactsItem(MeshGuardiansService.CONTACT_MOBILE_TYPE_ID, oldMobile, this.mobile));
        modifyContactsItems.add(new ModifyContactsItem(MeshGuardiansService.CONTACT_EMAIL_TYPE_ID, oldEmail, this.email));
        MeshContactResponse meshContactResponse = getMeshGuardiansService()
                .savePersonContact(this.meshGUID, modifyContactsItems);
        if (meshContactResponse != null && !meshContactResponse.getCode().equals(GuardianResponse.OK)) {
            logger.error(String.format("code: %s message: %s", meshContactResponse.getCode(), meshContactResponse.getMessage()));
            throw new Exception(String.format("Ошибка изменения контактов представителя в МК: %s", meshContactResponse.getMessage()));
        }
    }

    private void addGuardiansMK(Client client) throws Exception {
        for (ClientGuardianItem clientGuardianItem : this.clientGuardianItems) {
            if (clientGuardianItem.getIsNew()) {
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .addGuardianToClient(clientGuardianItem.getMeshGuid(), client.getMeshGUID(), clientGuardianItem.getRole());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка создания связи с представителем в МК: %s", personResponse.getMessage()));
                }
                clientGuardianItem.setIsNew(false);
            } else if (clientGuardianItem.getChange()) {
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .changeGuardianToClient(clientGuardianItem.getMeshGuid(), client.getMeshGUID(), clientGuardianItem.getRole());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка изменения связи с представителем в МК: %s", personResponse.getMessage()));
                }
            }
        }
    }

    private void addGuardiansISPP(Session persistenceSession, Client client) throws Exception {
        if (!this.clientGuardianItems.isEmpty()) {
            //Создание связи с опекунами
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            clientGuardianHistory.setReason(String.format("Создана/отредактирована связка на карточке клиента id = %s как опекаемый",
                    client.getIdOfClient()));
            addGuardiansByClient(persistenceSession, client, this.clientGuardianItems,
                    clientGuardianHistory);
        }
    }

    private void removeWardsMK(Client client) throws Exception {
        if (this.removeListWardItems != null && !this.removeListWardItems.isEmpty() && client.getMeshGUID() != null) {
            for (ClientGuardianItem clientWardItem : this.removeListWardItems) {
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .deleteGuardianToClient(client.getMeshGUID(), clientWardItem.getMeshGuid());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка удаления связи с обучающимся в МК: %s", personResponse.getMessage()));
                }
            }
        }
    }

    private void removeWardsISPP(Session persistenceSession, Client client) throws Exception {
        if (this.removeListWardItems != null && !this.removeListWardItems.isEmpty()) {
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            clientGuardianHistory.setReason(String.format("Связка удалена на карточке клиента id = %s как опекаемый",
                    client.getIdOfClient()));
            removeWardsByClient(persistenceSession, client.getIdOfClient(), this.removeListWardItems, clientGuardianHistory);

            //Перенос в группу выбывшие в случае удаления связок с опекунами
            if (this.clientWardItems.isEmpty() && client.getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_PARENTS.getValue())) {
                ClientManager.guardianToLeaving(persistenceSession, client, clientGuardianHistory);
            }

        }
    }

    private void addWardsMK(Client client) throws Exception {
        for (ClientGuardianItem clientWardItem : this.clientWardItems) {
            if (clientWardItem.getIsNew()) {
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .addGuardianToClient(client.getMeshGUID(), clientWardItem.getMeshGuid(), clientWardItem.getRole());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка создания связи с обучающимся в МК: %s", personResponse.getMessage()));
                }
                clientWardItem.setIsNew(false);
            } else if (clientWardItem.getChange()) {
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .changeGuardianToClient(client.getMeshGUID(), clientWardItem.getMeshGuid(), clientWardItem.getRole());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка изменения связи с обучающимся в МК: %s", personResponse.getMessage()));
                }
            }
        }
    }

    private void addWardsISPP(Session persistenceSession, Client client) throws Exception {
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
        clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
        clientGuardianHistory.setReason(String.format("Создана/отредактирована связка на карточке клиента id = %s как опекун",
                client.getIdOfClient()));
        addWardsByClient(persistenceSession, client, this.clientWardItems, clientGuardianHistory);
    }

    private void removeGuardiansISPP(Session persistenceSession, Client client) throws Exception {
        if (this.removeListGuardianItems != null && !this.removeListGuardianItems.isEmpty()) {
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            clientGuardianHistory.setReason(String.format("Связка удалена на карточке клиента id = %s как опекун",
                    client.getIdOfClient()));
            removeGuardiansByClient(persistenceSession, client, this.removeListGuardianItems, clientGuardianHistory);

            //Проверка на наличие активных связок у удаленных опекунов
            List<Long> idsOfGuardians = this.removeListGuardianItems
                    .stream().map(ClientGuardianItem::getIdOfClient).collect(Collectors.toList());
            for (Long idsOfGuardian : idsOfGuardians) {
                Client guardian = persistenceSession.load(Client.class, idsOfGuardian);
                if (guardian.getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_PARENTS.getValue())) {
                    //Перенос в группу выбывшие в случае удаления связок с опекунами
                    List<ClientGuardianItem> guardianWards = loadWardsByClient(persistenceSession, idsOfGuardian, true);
                    if (guardianWards.isEmpty()) {
                        ClientManager.guardianToLeaving(persistenceSession, guardian, clientGuardianHistory);
                    }
                }
            }
        }
    }


    private void removeGuardiansMK(Client client) throws Exception {
        if (this.removeListGuardianItems != null && !this.removeListGuardianItems.isEmpty()) {
            for (ClientGuardianItem clientGuardianItem : this.removeListGuardianItems) {
                if (clientGuardianItem.getMeshGuid() == null)
                    continue;
                MeshAgentResponse personResponse = getMeshGuardiansService()
                        .deleteGuardianToClient(clientGuardianItem.getMeshGuid(), client.getMeshGUID());
                if (!personResponse.getCode().equals(PersonResponse.OK_CODE)) {
                    throw new Exception(String.format("Ошибка удаления связи с обучающимся в МК: %s", personResponse.getMessage()));
                }
            }
        }
    }

    private void createMiddleGroup(Session session, Long idOfOrg, String groupName, String middleGroupName) throws Exception {
        List<GroupNamesToOrgs> groupNamesToOrgList = DAOUtils.findMiddleGroupNamesToOrgByIdOfOrg(session, idOfOrg);

        if (groupNamesToOrgList.stream().anyMatch(g -> g.getGroupName().equals(middleGroupName)
                && !g.getParentGroupName().equals(groupName))) {
            throw new Exception("Подгруппа уже существует в другой группе данной организации");
        }

        if (groupNamesToOrgList.stream().anyMatch(g -> g.getGroupName().equals(middleGroupName)
                && g.getParentGroupName().equals(groupName))) {
            return;
        }
        Org org = session.get(Org.class, idOfOrg);
        Org mainBuildingOrg = org.getFriendlyOrg().stream().filter(Org::getMainBuilding).findAny().orElse(null);

        if (mainBuildingOrg == null) {
            throw new Exception(String.format("Не найден главный корпус у организации с id = %s", idOfOrg));
        }

        GroupNamesToOrgs groupNamesToOrgs = new GroupNamesToOrgs();
        groupNamesToOrgs.setIdOfOrg(idOfOrg);
        groupNamesToOrgs.setIdOfMainOrg(mainBuildingOrg.getIdOfOrg());
        groupNamesToOrgs.setMainBuilding(1);
        groupNamesToOrgs.setVersion(DAOUtils.nextVersionByGroupNameToOrg(session));
        groupNamesToOrgs.setGroupName(middleGroupName);
        groupNamesToOrgs.setParentGroupName(groupName);
        groupNamesToOrgs.setIsMiddleGroup(true);
        session.save(groupNamesToOrgs);
    }

    public void deletePDClient() throws Exception {
        this.person.firstName = "Ручная обработка";
        this.person.secondName = "";
        this.person.surname = "Отзыв на обработку ПД";
        this.mobile = "";
        this.clientSSOID = "";
        removeListGuardianItems.clear();
        removeListGuardianItems.addAll(clientGuardianItems);
        clientGuardianItems.clear();
        removeListWardItems.clear();
        removeListWardItems.addAll(clientWardItems);
        clientWardItems.clear();
        this.idOfClientGroup = ClientGroup.Predefined.CLIENT_LEAVING.getValue();
        this.clientGroupName = ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup();
    }

    public boolean predefined() {
        if (ClientGroup.Predefined.parse(this.clientGroupName) == null)
            return true;
        else
            return false;
    }

    private void resetNewFlags() {
        for (ClientGuardianItem item : clientGuardianItems) {
            item.setIsNew(false);
        }
        for (ClientGuardianItem item : clientWardItems) {
            item.setIsNew(false);
        }
    }

    private void resetNewFlagsDul() {
        this.dulDetail.forEach(d -> d.setNew(false));
    }

    private boolean isDiscountsChanged(Client client, Set<CategoryDiscount> newCategoriesDiscounts) {
        boolean isDiscountModeChanged = !(client.getDiscountMode().equals(discountMode));
        boolean isCategoryListChanged = (client.getCategories().size() != newCategoriesDiscounts.size());
        if (isCategoryListChanged) return true;
        isCategoryListChanged = !client.getCategories().equals(newCategoriesDiscounts);
        return isDiscountModeChanged || isCategoryListChanged;
    }

    private void saveDiscountChange(Client client, Session persistenceSession, Set<CategoryDiscount> newCategoriesDiscounts) {
        DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, null, discountMode, client.getDiscountMode(),
                StringUtils.join(newCategoriesDiscounts, ','), StringUtils.join(client.getCategories(), ','));
        discountChangeHistory.setComment(DiscountChangeHistory.MODIFY_BY_TRANSITION);
        persistenceSession.save(discountChangeHistory);

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

    private void fill(Session session, Client client) throws Exception {
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
        this.externalId = client.getExternalId();
        this.clientGUID = client.getClientGUID();
        this.meshGUID = client.getMeshGUID();
        this.clientSSOID = client.getSsoid();
        this.clientIacRegId = client.getIacRegId();
        this.discountMode = client.getDiscountMode();
        /* filter fill*/
        this.useLastEEModeForPlan = client.isUseLastEEModeForPlan();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.lastDiscountsUpdate = client.getLastDiscountsUpdate();
        this.disablePlanCreationDate = client.getDisablePlanCreationDate();
        this.disablePlanEndDate = client.getDisablePlanEndDate();
        this.disablePlanCreation = this.disablePlanCreationDate != null;
        this.ageTypeGroup = client.getAgeTypeGroup();
        removeListGuardianItems.clear();
        removeListWardItems.clear();
        this.lastConfirmMobile = client.getLastConfirmMobile();
        this.cardRequest = DAOUtils.getCardRequestString(session, client);
        balanceHold = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).getBalanceHoldListAsString(session, client.getIdOfClient());
        this.inOrgEnabledMultiCardMode = client.getOrg().multiCardModeIsEnabled();
        this.parallel = StringUtils.defaultString(client.getParallel());
        this.clientDiscountItems = ClientViewPage.buildClientDiscountItem(session, client);
        this.canConfirmGroupPayment = client.getCanConfirmGroupPayment();
        this.confirmVisualRecognition = client.getConfirmVisualRecognition();
        this.userOP = client.getUserOP();
        this.middleGroup = client.getMiddleGroup();
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]", "");
    }

    private String filter = "Не выбрано";
    private List<Long> idOfCategoryList = new ArrayList<Long>();
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

    public boolean isNewOrgHasCatDiscount() {
        return newOrgHasCatDiscount;
    }

    public void setNewOrgHasCatDiscount(boolean newOrgHasCatDiscount) {
        this.newOrgHasCatDiscount = newOrgHasCatDiscount;
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

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
        //To change body of implemented methods use File | Settings | File Templates.
        if (null != categoryMap) {
            idOfCategoryList = new ArrayList<Long>();
            clientDiscountItems = new ArrayList<>();
            List<ClientDiscountItem> newClientDiscountItems = new LinkedList<ClientDiscountItem>();
            if (!categoryMap.isEmpty()) {
                for (Long idOfCategory : categoryMap.keySet()) {
                    idOfCategoryList.add(idOfCategory);
                    ClientDiscountItem item = new ClientDiscountItem(idOfCategory, categoryMap.get(idOfCategory), null,
                            null, null, null, null, null, lastDiscountsUpdate, discountMode);
                    newClientDiscountItems.add(item);
                    clientDiscountItems = newClientDiscountItems;
                }
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
        int i = 0;
        for (ClientGuardianRelationType relType : ClientGuardianRelationType.values()) {
            result[i + 1] = new SelectItem(relType.getCode(), relType.getDescription());
            i++;
        }

        return result;
    }

    public SelectItem[] getRoles() {
        SelectItem[] result = new SelectItem[ClientGuardianRoleType.values().length + 1];
        result[0] = new SelectItem(-1, "");
        int i = 0;
        for (ClientGuardianRoleType roleType : ClientGuardianRoleType.values()) {
            result[i + 1] = new SelectItem(roleType.getCode(), roleType.getDescription());
            i++;
        }
        return result;
    }

    public SelectItem[] getRepresentativeList() {
        SelectItem[] result = new SelectItem[ClientGuardianRepresentType.values().length];
        int i = 0;
        for (ClientGuardianRepresentType type : ClientGuardianRepresentType.values()) {
            result[i] = new SelectItem(type.getCode(), type.getDescription());
            i++;
        }
        return result;
    }

    public boolean isEligibleToViewUserOP() {
        if (null == this.idOfClientGroup) {
            return false;
        }
        return OkuDAOService.getClientGroupList().contains(this.idOfClientGroup);
    }

    public Boolean isParentGroup() {
        if (this.idOfClientGroup != null)
            return this.idOfClientGroup.equals(ClientGroup.Predefined.CLIENT_PARENTS.getValue());
        return false;
    }

    public Boolean isStudentGroup() {
        if (this.idOfClientGroup != null)
            return this.idOfClientGroup < 1100000000L;
        return false;
    }

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }

    private DulDetailService getMeshDulDetailService() {
        return RuntimeContext.getAppContext().getBean(DulDetailService.class);
    }

    private boolean isGuardianGroup(Client client) {
        if (client.getIdOfClientGroup() < 1100000000L)
            return false;
        return !Objects.equals(client.getIdOfClientGroup(), ClientGroup.Predefined.CLIENT_DELETED.getValue());
    }
}