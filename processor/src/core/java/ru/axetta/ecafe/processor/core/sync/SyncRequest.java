/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.TurnstileSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.TurnstileSettingsRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.balance.hold.ClientBalanceHoldBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.balance.hold.ClientBalanceHoldData;
import ru.axetta.ecafe.processor.core.sync.handlers.balance.hold.ClientBalanceHoldRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.card.request.CardRequests;
import ru.axetta.ecafe.processor.core.sync.handlers.card.request.CardRequestsBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.CategoriesDiscountsAndRulesBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.CategoriesDiscountsAndRulesRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientGroupManagerBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientGroupManagerRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ClientPhotosBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ClientsPhotos;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule.ListComplexSchedules;
import ru.axetta.ecafe.processor.core.sync.handlers.dtiszn.ClientDiscountDTSZNBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.dtiszn.ClientDiscountsDTSZNRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.emias.EmiasBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.emias.EmiasRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request.GoodRequestEZDBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request.GoodRequestEZDRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.groups.GroupsOrganizationRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.HelpRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.HelpRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReport;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportDataBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier.MenuSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier.MenuSupplierBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar.MenusCalendarBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar.MenusCalendarRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar.MenusCalendarSupplierBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar.MenusCalendarSupplierRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.Migrants;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.PaymentRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.PaymentRegistryBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions.PlanOrdersRestrictionsBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions.PlanOrdersRestrictionsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrdersFeedingBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrdersFeedingRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.PreorderFeedingStatusBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.PreorderFeedingStatusRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ReestrTaloonPreorder;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ReestrTaloonPreorderBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplierBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.SyncSettingsRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperationBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactions;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactionsBuilder;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.request.*;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequest;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.text.StrTokenizer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:52:14
 * To change this template use File | Settings | File Templates.
 */
public class SyncRequest {

    private static Logger logger = LoggerFactory.getLogger(SyncRequest.class);

    public String getSqlServerVersion() {
        return sqlServerVersion;
    }

    public void setSqlServerVersion(String sqlServerVersion) {
        this.sqlServerVersion = sqlServerVersion;
    }

    public Double getDatabaseSize() {
        return databaseSize;
    }

    public void setDatabaseSize(Double databaseSize) {
        this.databaseSize = databaseSize;
    }

    public CafeteriaExchangeContentType getContentType() {
        return contentType;
    }

    public void setContentType(CafeteriaExchangeContentType contentType) {
        this.contentType = contentType;
    }

    public static class ClientParamRegistry implements SectionRequest {

        public static final String SECTION_NAME = "ClientParams";

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class ClientParamItem {

            public Long getVersion() {
                return version;
            }

            public Long getBalanceToNotify() {
                return balanceToNotify;
            }

            public Long getOrgOwner() {
                return orgOwner;
            }

            public Date getDisablePlanEndDate() {
                return disablePlanEndDate;
            }

            public String getPassportNumber() {
                return passportNumber;
            }

            public String getPassportSeries() {
                return passportSeries;
            }

            public static class Builder {

                public Builder() {
                }

                /* TODO: сделать поля необязательными для проверки и заполнения */
                public ClientParamItem build(Node itemNode, LoadContext loadContext) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();
                    long idOfClient = getLongValue(namedNodeMap, "IdOfClient");
                    String[] freePayCountTokens = namedNodeMap.getNamedItem("FPCount").getTextContent().split(":");
                    int freePayCount = Integer.parseInt(freePayCountTokens[0]);
                    int freePayMaxCount = Integer.parseInt(freePayCountTokens[1]);
                    Date lastFreePayTime = null;
                    if (0 != freePayCount) {
                        lastFreePayTime = loadContext.getTimeFormat()
                                .parse(namedNodeMap.getNamedItem("FPLastTime").getTextContent());
                    }
                    int discountMode = getIntValue(namedNodeMap, "DiscountMode");
                    /* pridet string with "1, 9, 94, .." key of Category Discount*/
                    // Далее идут не обязательные поля
                    String categoriesDiscounts = getStringValueNullSafe(namedNodeMap, "CategoriesDiscounts");
                    ///
                    String name = getStringValueNullSafe(namedNodeMap, "Name");
                    String surname = getStringValueNullSafe(namedNodeMap, "Surname");
                    String secondName = getStringValueNullSafe(namedNodeMap, "Secondname");
                    String address = getStringValueNullSafe(namedNodeMap, "Address");
                    String phone = getStringValueNullSafe(namedNodeMap, "Phone");
                    String mobilePhone = getStringValueNullSafe(namedNodeMap, "Mobile");
                    String middleGroup = getStringValueNullSafe(namedNodeMap, "MiddleGroup");
                    String email = getStringValueNullSafe(namedNodeMap, "Email");
                    String fax = getStringValueNullSafe(namedNodeMap, "Fax");
                    String remarks = getStringValueNullSafe(namedNodeMap, "Remarks");
                    String notifyViaEmail = getStringValueNullSafe(namedNodeMap, "NotifyViaEmail");
                    String notifyViaSMS = getStringValueNullSafe(namedNodeMap, "NotifyViaSMS");
                    String notifyViaPUSH = getStringValueNullSafe(namedNodeMap, "NotifyViaPush");
                    String groupName = getStringValueNullSafe(namedNodeMap, "GroupName");
                    String canConfirmGroupPayment = getStringValueNullSafe(namedNodeMap, "CanConfirmGroupPayment");
                    String confirmVisualRecognition = getStringValueNullSafe(namedNodeMap, "IsInOutByVideo");
                    String guid = getStringValueNullSafe(namedNodeMap, "GUID");
                    Long expenditureLimit = getLongValueNullSafe(namedNodeMap, "ExpenditureLimit");
                    String isUseLastEEModeForPlan = getStringValueNullSafe(namedNodeMap, "IsUseLastEEModeForPlan");
                    Integer gender = getIntegerValueNullSafe(namedNodeMap, "Gender");
                    Date birthDate = getDateValueNullSafe(namedNodeMap,"BirthDate");
                    Long version = getLongValueNullSafe(namedNodeMap, "V");
                    Long balanceToNotify = getLongValueNullSafe(namedNodeMap, "BalanceToNotify");
                    String disablePlanCreationDateSt = getStringValueNullSafe(namedNodeMap, "DisablePlanCreationDate");
                    Date disablePlanCreationDate = null;
                    if(StringUtils.isNotEmpty(disablePlanCreationDateSt)) {
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                        disablePlanCreationDate = df.parse(disablePlanCreationDateSt);
                    }
                    String disablePlanEndDateSt = getStringValueNullSafe(namedNodeMap, "DisablePlanEndDate");
                    Date disablePlanEndDate = null;
                    if(StringUtils.isNotEmpty(disablePlanEndDateSt)) {
                        disablePlanEndDate = loadContext.getTimeFormat().parse(disablePlanEndDateSt);
                    }
                    Long orgOwner = getLongValueNullSafe(namedNodeMap, "OrgId");
                    String san = getStringValueNullSafe(namedNodeMap, "San");
                    String passportNumber = getStringValueNullSafe(namedNodeMap, "PassportNumber");
                    String passportSeries = getStringValueNullSafe(namedNodeMap, "PassportSeries");
                    return new ClientParamItem(idOfClient, freePayCount, freePayMaxCount, lastFreePayTime, discountMode,
                            categoriesDiscounts, name, surname, secondName, address, phone, mobilePhone, middleGroup,
                            fax, email, remarks, notifyViaEmail == null ? null : notifyViaEmail.equals("1"),
                            notifyViaSMS == null ? null : notifyViaSMS.equals("1"),
                            notifyViaPUSH == null ? null : notifyViaPUSH.equals("1"), groupName,
                            canConfirmGroupPayment == null ? null : canConfirmGroupPayment.equals("1"),
                            confirmVisualRecognition == null ? null : confirmVisualRecognition.equals("1"), guid,
                            expenditureLimit, isUseLastEEModeForPlan == null ? null : isUseLastEEModeForPlan.equals("1"),
                            gender,birthDate, version, balanceToNotify, disablePlanCreationDate, disablePlanEndDate, orgOwner, san,
                            passportNumber, passportSeries);
                }


            }

            private final long idOfClient;
            private final String name, surname, secondName, address, phone, mobilePhone, middleGroup, fax, email, remarks;
            private final int freePayCount;
            private final int freePayMaxCount;
            private final Date lastFreePayTime;
            private final int discountMode;
            private final String groupName;
            private final String categoriesDiscounts;
            private final Boolean notifyViaEmail, notifyViaSMS, notifyViaPUSH;
            private final Boolean canConfirmGroupPayment;
            private final Boolean confirmVisualRecognition;
            private final String guid;
            private final Long expenditureLimit;
            private final Boolean isUseLastEEModeForPlan;
            private final Date birthDate;
            private final Integer gender;
            private final Long version;
            private final Long balanceToNotify;
            private final Date disablePlanCreationDate;
            private final Date disablePlanEndDate;
            private final Long orgOwner;
            private final String san;
            private final String passportNumber;
            private final String passportSeries;


            public ClientParamItem(long idOfClient, int freePayCount, int freePayMaxCount, Date lastFreePayTime,
                    int discountMode, String categoriesDiscounts, String name, String surname, String secondName,
                    String address, String phone, String mobilePhone, String middleGroup, String fax, String email,
                    String remarks, Boolean notifyViaEmail, Boolean notifyViaSMS, Boolean notifyViaPUSH, String groupName, Boolean canConfirmGroupPayment,
                    Boolean confirmVisualRecognition, String guid, Long expenditureLimit, Boolean isUseLastEEModeForPlan,Integer gender,Date birthDate, Long version, Long balanceToNotify,
                    Date disablePlanCreationDate, Date disablePlanEndDate, Long orgOwner, String san, String passportNumber, String passportSeries) {
                this.idOfClient = idOfClient;
                this.freePayCount = freePayCount;
                this.freePayMaxCount = freePayMaxCount;
                this.lastFreePayTime = lastFreePayTime;
                this.discountMode = discountMode;
                this.categoriesDiscounts = categoriesDiscounts;
                this.name = name;
                this.surname = surname;
                this.secondName = secondName;
                this.address = address;
                this.phone = phone;
                this.mobilePhone = mobilePhone;
                this.middleGroup = middleGroup;
                this.fax = fax;
                this.email = email;
                this.remarks = remarks;
                this.notifyViaEmail = notifyViaEmail;
                this.notifyViaSMS = notifyViaSMS;
                this.notifyViaPUSH = notifyViaPUSH;
                this.groupName = groupName;
                this.canConfirmGroupPayment = canConfirmGroupPayment;
                this.confirmVisualRecognition = confirmVisualRecognition;
                this.guid = guid;
                this.expenditureLimit = expenditureLimit;
                this.isUseLastEEModeForPlan = isUseLastEEModeForPlan;
                this.gender = gender;
                this.birthDate = birthDate;
                this.version = version;
                this.balanceToNotify = balanceToNotify;
                this.disablePlanCreationDate = disablePlanCreationDate;
                this.disablePlanEndDate = disablePlanEndDate;
                this.orgOwner = orgOwner;
                this.san = san;
                this.passportNumber = passportNumber;
                this.passportSeries = passportSeries;
            }

            public long getIdOfClient() {
                return idOfClient;
            }

            public int getFreePayCount() {
                return freePayCount;
            }

            public int getFreePayMaxCount() {
                return freePayMaxCount;
            }

            public Date getLastFreePayTime() {
                return lastFreePayTime;
            }

            public int getDiscountMode() {
                return discountMode;
            }

            public String getCategoriesDiscounts() {
                return categoriesDiscounts;
            }

            public String getName() {
                return name;
            }

            public String getSurname() {
                return surname;
            }

            public String getSecondName() {
                return secondName;
            }

            public String getAddress() {
                return address;
            }

            public String getPhone() {
                return phone;
            }

            public String getMobilePhone() {
                return mobilePhone;
            }

            public String getMiddleGroup() {
                return middleGroup;
            }

            public String getFax() {
                return fax;
            }

            public String getEmail() {
                return email;
            }

            public String getRemarks() {
                return remarks;
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

            public String getGroupName() {
                return groupName;
            }

            public Boolean getCanConfirmGroupPayment() {
                return canConfirmGroupPayment;
            }

            public Boolean getConfirmVisualRecognition() {
                return confirmVisualRecognition;
            }

            public String getGuid() {
                return guid;
            }

            public Long getExpenditureLimit() {
                return expenditureLimit;
            }

            public Boolean getIsUseLastEEModeForPlan() {
                return isUseLastEEModeForPlan;
            }

            public Date getBirthDate() {
                return birthDate;
            }

            public Integer getGender() {
                return gender;
            }

            public Date getDisablePlanCreationDate() {
                return disablePlanCreationDate;
            }

            public String getSan() { return san; }

            @Override
            public String toString() {
                return "ClientParamItem{" + "idOfClient=" + idOfClient + ", name='" + name + '\'' + ", surname='"
                        + surname + '\'' + ", secondName='" + secondName + '\'' + ", address='" + address + '\''
                        + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", middleGroup='" + middleGroup + '\'' + ", fax='" + fax + '\''
                        + ", email='" + email + '\'' + ", remarks='" + remarks + '\'' + ", freePayCount=" + freePayCount
                        + ", freePayMaxCount=" + freePayMaxCount + ", lastFreePayTime=" + lastFreePayTime + ", discountMode=" + discountMode
                        + ", categoriesDiscounts='" + categoriesDiscounts + '\'' + ", expenditureLimit=" + expenditureLimit
                        + ", balanceToNotify=" + balanceToNotify + '}';
            }
        }

        public static class Builder implements SectionRequestBuilder {

            private final ClientParamItem.Builder itemBuilder;
            private final LoadContext loadContext;

            public Builder(LoadContext loadContext) {
                this.loadContext = loadContext;
                this.itemBuilder = new ClientParamItem.Builder();
            }

            public ClientParamRegistry build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (ClientParamRegistry) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement);
                } else
                    return null;
            }

            private ClientParamRegistry buildFromCorrectSection(Node paymentRegistryNode) throws Exception {
                List<ClientParamItem> items = new LinkedList<ClientParamItem>();
                if (paymentRegistryNode == null)
                    return new ClientParamRegistry();

                //TODO - УБРАТЬ ВРЕМЕННОЕ ОГРАНИЧЕНИЕ НА 1000 ЗАПИСЕЙ
                Node itemNode = paymentRegistryNode.getFirstChild();
                while (null != itemNode) {
                    if (items.size() > 1000) {
                        try {
                            Node cafeteriaExchangeNode = itemNode.getParentNode().getParentNode();
                            Long idOfOrg = Long.parseLong(
                                    cafeteriaExchangeNode.getAttributes().getNamedItem("IdOfOrg").getNodeValue());
                            DAOService.getInstance().setFullSyncByOrg(idOfOrg, true);
                        } catch (Exception ignore) {}
                        break;
                    }
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CP")) {
                        items.add(itemBuilder.build(itemNode, loadContext));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new ClientParamRegistry(items);
            }
        }

        private final List<ClientParamItem> items;

        public ClientParamRegistry(List<ClientParamItem> items) {
            this.items = items;
        }

        public ClientParamRegistry() {
            this.items = new LinkedList<ClientParamItem>();
        }

        public List<ClientParamItem> getPayments() {
            return items;
        }

        @Override
        public String toString() {
            return "ClientParamRegistry{" + "items=" + items + '}';
        }
    }

    public static class OrgStructure implements SectionRequest {

        public static final String SECTION_NAME = "OrgStructure";

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class Group {

            public static class Builder {

                public Group build(Node itemNode) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();
                    long idOfGroup = getLongValue(namedNodeMap, "IdOfGroup");
                    String name = StringUtils.substring(namedNodeMap.getNamedItem("Name").getTextContent(), 0, 60);
                    String clientList = StringUtils
                            .substring(namedNodeMap.getNamedItem("ClientList").getTextContent(), 0, 2048);
                    StrTokenizer strTokenizer = new StrTokenizer(clientList, ";");
                    List<Long> clients = new LinkedList<Long>();
                    while (strTokenizer.hasNext()) {
                        clients.add(Long.parseLong(StringUtils.strip(strTokenizer.nextToken())));
                    }
                    return new Group(idOfGroup, name, clients);
                }

            }

            private final long idOfGroup;
            private final String name;
            private final List<Long> clients;

            public Group(long idOfGroup, String name, List<Long> clients) {
                this.idOfGroup = idOfGroup;
                this.name = name;
                this.clients = clients;
            }

            public long getIdOfGroup() {
                return idOfGroup;
            }

            public String getName() {
                return name;
            }

            public List<Long> getClients() {
                return clients;
            }

            @Override
            public String toString() {
                return "Group{" + "idOfGroup=" + idOfGroup + ", name='" + name + '\'' + ", clients=" + clients + '}';
            }
        }

        public static class Builder implements SectionRequestBuilder {

            private final Group.Builder groupBuilder;

            public Builder() {
                this.groupBuilder = new Group.Builder();
            }

            public OrgStructure build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (OrgStructure) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, OrgStructure.SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement);
                } else
                    return null;
            }

            private OrgStructure buildFromCorrectSection(Node orgStructureNode) throws Exception {
                List<Group> groups = new LinkedList<Group>();
                Node itemNode = orgStructureNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("OS")) {
                        groups.add(groupBuilder.build(itemNode));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new OrgStructure(groups);
            }

        }

        private final List<Group> groups;

        public OrgStructure(List<Group> groups) {
            this.groups = groups;
        }

        public List<Group> getGroups() {
            return groups;
        }

        @Override
        public String toString() {
            return "OrgStructure{" + "groups=" + groups + '}';
        }
    }

    public static class MenuGroups implements SectionRequest {

        public static final String SECTION_NAME = "MenuGroups";

        public String findMenuGroup(long idOfMenuGroup) {
            for (MenuGroup menuGroup : menuGroups) {
                if (menuGroup.idOfMenuGroup == idOfMenuGroup) {
                    return menuGroup.getName();
                }
            }
            return null;
        }

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }


        public static class MenuGroup {

            public static class Builder {

                public MenuGroup build(Node itemNode) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();
                    long idOfMenuGroup = getLongValue(namedNodeMap, "IdOfMenuGroup");
                    String name = StringUtils.substring(namedNodeMap.getNamedItem("MenuGroup").getTextContent(), 0, 90);
                    long printOrder = getLongValue(namedNodeMap, "PrintOrder");
                    return new MenuGroup(idOfMenuGroup, name, printOrder);
                }

            }

            private final long idOfMenuGroup;
            private final String name;
            private final long printOrder;

            public MenuGroup(long idOfMenuGroup, String name, long printOrder) {
                this.idOfMenuGroup = idOfMenuGroup;
                this.name = name;
                this.printOrder = printOrder;
            }

            public long getIdOfMenuGroup() {
                return idOfMenuGroup;
            }

            public String getName() {
                return name;
            }

            public long getPrintOrder() {
                return printOrder;
            }

            @Override
            public String toString() {
                return "MenuGroup{" + "idOfMenuGroup=" + idOfMenuGroup + ", name='" + name + '\'' + ", printOrder="
                        + printOrder + '}';
            }
        }

        public static class Builder implements SectionRequestBuilder {

            private final MenuGroup.Builder groupBuilder;

            public Builder() {
                this.groupBuilder = new MenuGroup.Builder();
            }

            public MenuGroups build(Node envelopeNode) {
                try {
                    SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                    if (sectionRequest != null)
                        return (MenuGroups) sectionRequest;
                } catch (Exception ex) {
                    logger.error("Failed to build section request, MenuGroups");
                }
                return new MenuGroups(new LinkedList<MenuGroup>());
            }

            private MenuGroups buildFromCorrectSection(Node node) throws Exception {
                List<MenuGroup> groups = new LinkedList<MenuGroup>();
                if (node != null) {
                    Node itemNode = node.getFirstChild();
                    while (null != itemNode) {
                        if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("MGI")) {
                            groups.add(groupBuilder.build(itemNode));
                        }
                        itemNode = itemNode.getNextSibling();
                    }
                    return new MenuGroups(groups);
                } else {
                    return new MenuGroups(groups);
                }
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node menuNode = findFirstChildElement(envelopeNode, ReqMenu.SECTION_NAME);
                Node menuGroupsNode = findFirstChildElement(envelopeNode, MenuGroups.SECTION_NAME);
                if (menuGroupsNode == null) {
                    // может быть как на верхнем уровне (старый протокол), так и в Menu / Settings
                    if (menuNode != null) {
                        Node settingsNode = findFirstChildElement(menuNode, "Settings");
                        if (settingsNode != null) {
                            menuGroupsNode = findFirstChildElement(settingsNode, MenuGroups.SECTION_NAME);
                        }
                    }
                }
                return buildFromCorrectSection(menuGroupsNode);
            }
        }

        private final List<MenuGroup> menuGroups;

        public MenuGroups(List<MenuGroup> menuGroups) {
            this.menuGroups = menuGroups;
        }

        public List<MenuGroup> getMenuGroups() {
            return menuGroups;
        }

    }

    public static class AccIncRegistryRequest implements SectionRequest {

        public final static String SECTION_NAME = "AccIncRegistryRequest";
        public final Date dateTime;

        public AccIncRegistryRequest(Date dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class Builder implements SectionRequestBuilder {

            private LoadContext loadContext;

            public Builder(LoadContext loadContext) {
                this.loadContext = loadContext;
            }

            public AccIncRegistryRequest build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (AccIncRegistryRequest) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement, loadContext);
                } else
                    return null;
            }

            private AccIncRegistryRequest buildFromCorrectSection(Node node, LoadContext loadContext) throws Exception {
                NamedNodeMap namedNodeMap = node.getAttributes();
                Date dateTime = loadContext.getTimeFormat().parse(namedNodeMap.getNamedItem("Date").getTextContent());
                return new AccIncRegistryRequest(dateTime);
            }

        }
    }

    public static class ReqMenu implements SectionRequest {

        private static final String SECTION_NAME = "Menu";

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class Item {

            public static class ReqComplexInfo {

                public static class ReqComplexInfoDetail {

                    private ReqMenuDetail reqMenuDetail;
                    private Long idOfItem;
                    private Integer count;

                    public static class Builder {

                        public ReqComplexInfoDetail build(Node node, HashMap<Long, ReqMenuDetail> reqMenuDetailMap)
                                throws Exception {
                            NamedNodeMap namedNodeMap = node.getAttributes();
                            long refIdOfMenu = Long
                                    .parseLong(namedNodeMap.getNamedItem("refIdOfMenu").getTextContent());
                            ReqMenuDetail reqMenuDetail = reqMenuDetailMap.get(refIdOfMenu);
                            if (reqMenuDetail == null) {
                                throw new Exception("Menu detail not found by refIdOfMenu: " + refIdOfMenu);
                            }
                            Long idOfItem = null;
                            Node idOfItemNode = namedNodeMap.getNamedItem("IdOfItem");
                            if (idOfItemNode != null) {
                                try {
                                    idOfItem = Long.parseLong(idOfItemNode.getTextContent());
                                } catch (NumberFormatException e) {
                                    throw new Exception("Attribute IdOfItem contains value of type different from Float");
                                }
                            }
                            Integer menuItemCount = null;
                            Node menuItemCountNode = namedNodeMap.getNamedItem("Count");
                            if (menuItemCountNode != null) {
                                try {
                                    menuItemCount = Integer.parseInt(menuItemCountNode.getTextContent());
                                } catch (NumberFormatException e) {
                                    throw new Exception(
                                            "Attribute MenuItemCount contains value of type different from Integer");
                                }
                            }
                            return new ReqComplexInfoDetail(reqMenuDetail, idOfItem, menuItemCount);
                        }
                    }

                    ReqComplexInfoDetail(ReqMenuDetail reqMenuDetail, Long idOfItem, Integer count) {
                        this.reqMenuDetail = reqMenuDetail;
                        this.idOfItem = idOfItem;
                        this.count = count;
                    }

                    public ReqMenuDetail getReqMenuDetail() {
                        return reqMenuDetail;
                    }

                    public Long getIdOfItem() {
                        return idOfItem;
                    }


                    public Integer getCount() {
                        return count;
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) {
                            return true;
                        }
                        if (o == null || getClass() != o.getClass()) {
                            return false;
                        }

                        ReqComplexInfoDetail that = (ReqComplexInfoDetail) o;

                        if (!count.equals(that.count)) {
                            return false;
                        }
                        if (!idOfItem.equals(that.idOfItem)) {
                            return false;
                        }
                        if (!reqMenuDetail.equals(that.reqMenuDetail)) {
                            return false;
                        }

                        return true;
                    }

                    @Override
                    public int hashCode() {
                        HashCodeBuilder builder = new HashCodeBuilder();
                        builder.append(reqMenuDetail);
                        builder.append(idOfItem);
                        builder.append(count);
                        return builder.toHashCode();

                    }
                }

                public static class ReqComplexInfoDiscountDetail {

                    public static class Builder {

                        public ReqComplexInfoDiscountDetail build(Node itemNode) throws Exception {
                            NamedNodeMap namedNodeMap = itemNode.getAttributes();

                            Double size = getDoubleValue(namedNodeMap, "Size");
                            Integer isAllGroups = getIntValue(namedNodeMap, "IsAllGroups");
                            Node maxCountNode = namedNodeMap.getNamedItem("MaxCount");
                            Integer maxCount = null;
                            if (maxCountNode != null) {
                                maxCount = getIntValue(namedNodeMap, "MaxCount");
                            }
                            Node idOfClientGroupNode = namedNodeMap.getNamedItem("IdOfClientGroup");
                            Long idOfClientGroup = null;
                            if (idOfClientGroupNode != null) {
                                idOfClientGroup = getLongValue(namedNodeMap, "IdOfClientGroup");
                            }
                            return new ReqComplexInfoDiscountDetail(size, isAllGroups, maxCount, idOfClientGroup);
                        }

                        private static Double getDoubleValue(NamedNodeMap namedNodeMap, String name) throws Exception {
                            Node node = namedNodeMap.getNamedItem(name);
                            if (null == node) {
                                return null;
                            }
                            String calString = node.getTextContent();
                            if (calString.equals("")) {
                                return null;
                            }
                            String replacedString = calString.replaceAll(",", ".");
                            return Double.parseDouble(replacedString);
                        }

                    }

                    private final double size;
                    private final int isAllGroups;
                    private final Integer maxCount;
                    private final Long idOfClientGroup;

                    public ReqComplexInfoDiscountDetail(double size, int allGroups, Integer maxCount,
                            Long idOfClientGroup) {
                        this.size = size;
                        this.isAllGroups = allGroups;
                        this.maxCount = maxCount;
                        this.idOfClientGroup = idOfClientGroup;
                    }

                    public double getSize() {
                        return size;
                    }

                    public int getIsAllGroups() {
                        return isAllGroups;
                    }

                    public Integer getMaxCount() {
                        return maxCount;
                    }

                    public Long getIdOfClientGroup() {
                        return idOfClientGroup;
                    }

                    @Override
                    public String toString() {
                        return "ReqComplexInfoDiscountDetail{" +
                                "size=" + size +
                                ", isAllGroups=" + isAllGroups +
                                ", maxCount=" + maxCount +
                                ", idOfClientGroup=" + idOfClientGroup +
                                '}';
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) {
                            return true;
                        }
                        if (o == null || getClass() != o.getClass()) {
                            return false;
                        }

                        ReqComplexInfoDiscountDetail that = (ReqComplexInfoDiscountDetail) o;

                        if (isAllGroups != that.isAllGroups) {
                            return false;
                        }
                        if (Double.compare(that.size, size) != 0) {
                            return false;
                        }
                        if (!idOfClientGroup.equals(that.idOfClientGroup)) {
                            return false;
                        }
                        if (!maxCount.equals(that.maxCount)) {
                            return false;
                        }

                        return true;
                    }

                    @Override
                    public int hashCode() {
                        HashCodeBuilder builder = new HashCodeBuilder();
                        builder.append(size);
                        builder.append(isAllGroups);
                        builder.append(maxCount);
                        builder.append(idOfClientGroup);
                        return builder.toHashCode();
                    }
                }

                public static class Builder {

                    public ReqComplexInfo build(Node node, HashMap<Long, ReqMenuDetail> reqMenuDetailMap)
                            throws Exception {

                        NamedNodeMap namedNodeMap = node.getAttributes();
                        int complexId = Integer.parseInt(namedNodeMap.getNamedItem("ComplexId").getTextContent());
                        String complexMenuName = StringUtils
                                .substring(namedNodeMap.getNamedItem("ComplexMenuName").getTextContent(), 0, 60);
                        // фиксированная цена, не сохраняется в БД
                        long price = Long.parseLong(namedNodeMap.getNamedItem("pr").getTextContent());
                        // текущая цена
                        Node currentPriceNode = namedNodeMap.getNamedItem("CurrentPrice");
                        Long currentPrice = null;
                        if (currentPriceNode != null) {
                            currentPrice = Long.parseLong(currentPriceNode.getTextContent());
                        }
                        // получение id товара по guid
                        Node goodsGuidNode = namedNodeMap.getNamedItem("GoodsGuid");
                        String goodsGuid = null;
                        if (goodsGuidNode != null) {
                            goodsGuid = StringUtils.substring(goodsGuidNode.getTextContent(), 0, 36);
                        }

                        Node usedSubscriptionFeedingNode = namedNodeMap.getNamedItem("usedSubscriptionFeeding");
                        int usedSubscriptionFeeding = 0;
                        if (usedSubscriptionFeedingNode != null) {
                            usedSubscriptionFeeding = Integer.parseInt(usedSubscriptionFeedingNode.getTextContent());
                        }

                        Node usedVariableFeedingNode = namedNodeMap.getNamedItem("usedVariableFeeding");
                        int usedVariableFeeding = 0;
                        if (usedVariableFeedingNode != null) {
                            usedVariableFeeding = Integer.parseInt(usedVariableFeedingNode.getTextContent());
                        }

                        Node rootComplexNode = namedNodeMap.getNamedItem("rootComplex");
                        Integer rootComplex = null;
                        if (rootComplexNode != null) {
                            rootComplex = Integer.parseInt(rootComplexNode.getTextContent());
                        }

                        Node usedSpecialMenuNode = namedNodeMap.getNamedItem("usedSpecialMenu");
                        int usedSpecialMenu = 0;
                        if (usedSpecialMenuNode != null) {
                            usedSpecialMenu = Integer.parseInt(usedSpecialMenuNode.getTextContent());
                        }

                        int modeFree = Integer.parseInt(namedNodeMap.getNamedItem("d").getTextContent());
                        int modeGrant = Integer.parseInt(namedNodeMap.getNamedItem("g").getTextContent());
                        int modeOfAdd = Integer.parseInt(namedNodeMap.getNamedItem("m").getTextContent());
                        Integer modeVisible = null;
                        Node modeVisibleNode = namedNodeMap.getNamedItem("cv");
                        if (modeVisibleNode != null) {
                            modeVisible = Integer.parseInt(namedNodeMap.getNamedItem("cv").getTextContent());
                        }
                        Node useTrDiscountNode = namedNodeMap.getNamedItem("UseTrDiscount");
                        Integer useTrDiscount = null;
                        if (useTrDiscountNode != null) {
                            useTrDiscount = Integer.parseInt(useTrDiscountNode.getTextContent());
                        }
                        ReqMenuDetail reqMenuDetail = null;
                        if (namedNodeMap.getNamedItem("refIdOfMenu") != null) {
                            long refIdOfMenu = Long.parseLong(namedNodeMap.getNamedItem("refIdOfMenu").getTextContent());
                            reqMenuDetail = reqMenuDetailMap.get(refIdOfMenu);
                        }
                        Node childNode = node.getFirstChild();
                        ReqComplexInfoDetail.Builder reqComplexInfoDetailBuilder = new ReqComplexInfoDetail.Builder();
                        LinkedList<ReqComplexInfoDetail> reqComplexInfoDetailLinkedList = new LinkedList<ReqComplexInfoDetail>();
                        ReqComplexInfoDiscountDetail.Builder reqComplexInfoDiscountDetailBuilder = new ReqComplexInfoDiscountDetail.Builder();
                        ReqComplexInfoDiscountDetail reqComplexInfoDiscountDetail = null;
                        while (null != childNode) {
                            if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                                if (childNode.getNodeName().equals("CMI")) {
                                    ReqComplexInfoDetail reqComplexInfoDetail = reqComplexInfoDetailBuilder
                                            .build(childNode, reqMenuDetailMap);
                                    reqComplexInfoDetailLinkedList.add(reqComplexInfoDetail);
                                } else if (childNode.getNodeName().equals("TRD")) {
                                    reqComplexInfoDiscountDetail = reqComplexInfoDiscountDetailBuilder.build(childNode);
                                }
                            }
                            childNode = childNode.getNextSibling();
                        }
                        return new ReqComplexInfo(complexId, complexMenuName, modeFree, modeGrant, modeOfAdd,
                                usedSubscriptionFeeding, reqComplexInfoDetailLinkedList, useTrDiscount, reqMenuDetail,
                                reqComplexInfoDiscountDetail, currentPrice, goodsGuid, modeVisible, usedVariableFeeding,
                                rootComplex, usedSpecialMenu);
                    }

                }

                private final int complexId;
                private final String complexMenuName;
                private final int modeFree;
                private final int modeGrant;
                private final int modeOfAdd;
                private final int usedSubscriptionFeeding;
                private final int usedVariableFeeding;
                private final int usedSpecialMenu;
                private final Integer useTrDiscount;
                private final ReqMenuDetail reqMenuDetail;
                private final List<ReqComplexInfoDetail> complexInfoDetails;
                private final ReqComplexInfoDiscountDetail complexInfoDiscountDetail;
                private final Long currentPrice;
                private final String goodsGuid;
                private final Integer modeVisible;
                private final Integer rootComplex;

                public ReqComplexInfo(int complexId, String complexMenuName, int modeFree, int modeGrant, int modeOfAdd,
                        int usedSubscriptionFeeding, List<ReqComplexInfoDetail> complexInfoDetails, Integer useTrDiscount, ReqMenuDetail reqMenuDetail,
                        ReqComplexInfoDiscountDetail complexInfoDiscountDetail, Long currentPrice, String goodsGuid,
                        Integer modeVisible, int usedVariableFeeding, Integer rootComplex, Integer useSpecialMenu) {
                    this.complexId = complexId;
                    this.complexMenuName = complexMenuName;
                    this.modeFree = modeFree;
                    this.modeGrant = modeGrant;
                    this.modeOfAdd = modeOfAdd;
                    this.usedSubscriptionFeeding = usedSubscriptionFeeding;
                    this.usedVariableFeeding = usedVariableFeeding;
                    this.complexInfoDetails = complexInfoDetails;
                    this.useTrDiscount = useTrDiscount;
                    this.complexInfoDiscountDetail = complexInfoDiscountDetail;
                    this.reqMenuDetail = reqMenuDetail;
                    this.currentPrice = currentPrice;
                    this.goodsGuid = goodsGuid;
                    this.modeVisible = modeVisible;
                    this.rootComplex = rootComplex;
                    this.usedSpecialMenu = useSpecialMenu;
                }

                public int getComplexId() {
                    return complexId;
                }

                public String getComplexMenuName() {
                    return complexMenuName;
                }

                public int getModeFree() {
                    return modeFree;
                }

                public int getModeGrant() {
                    return modeGrant;
                }

                public int getModeOfAdd() {
                    return modeOfAdd;
                }

                public Integer getUseTrDiscount() {
                    return useTrDiscount;
                }

                public ReqMenuDetail getReqMenuDetail() {
                    return reqMenuDetail;
                }

                public List<ReqComplexInfoDetail> getComplexInfoDetails() {
                    return complexInfoDetails;
                }

                public ReqComplexInfoDiscountDetail getComplexInfoDiscountDetail() {
                    return complexInfoDiscountDetail;
                }

                public Long getCurrentPrice() {
                    return currentPrice;
                }

                public String getGoodsGuid() {
                    return goodsGuid;
                }

                public int getUsedSubscriptionFeeding() {
                    return usedSubscriptionFeeding;
                }

                public int getUsedVariableFeeding() {
                    return usedVariableFeeding;
                }

                public Integer getRootComplex() {
                    return rootComplex;
                }

                public int getUsedSpecialMenu() {
                    return usedSpecialMenu;
                }

                public Integer getModeVisible() {
                    return modeVisible;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (o == null || getClass() != o.getClass()) {
                        return false;
                    }

                    ReqComplexInfo that = (ReqComplexInfo) o;

                    if (complexId != that.complexId) {
                        return false;
                    }
                    if (modeFree != that.modeFree) {
                        return false;
                    }
                    if (modeGrant != that.modeGrant) {
                        return false;
                    }
                    if (modeOfAdd != that.modeOfAdd) {
                        return false;
                    }
                    if (usedSubscriptionFeeding != that.usedSubscriptionFeeding) {
                        return false;
                    }
                    if (complexInfoDetails != null ? !complexInfoDetails.equals(that.complexInfoDetails)
                            : that.complexInfoDetails != null) {
                        return false;
                    }
                    if (complexInfoDiscountDetail != null ? !complexInfoDiscountDetail
                            .equals(that.complexInfoDiscountDetail) : that.complexInfoDiscountDetail != null) {
                        return false;
                    }
                    if (complexMenuName != null ? !complexMenuName.equals(that.complexMenuName)
                            : that.complexMenuName != null) {
                        return false;
                    }
                    if (currentPrice != null ? !currentPrice.equals(that.currentPrice) : that.currentPrice != null) {
                        return false;
                    }
                    if (goodsGuid != null ? !goodsGuid.equals(that.goodsGuid) : that.goodsGuid != null) {
                        return false;
                    }
                    if (reqMenuDetail != null ? !reqMenuDetail.equals(that.reqMenuDetail)
                            : that.reqMenuDetail != null) {
                        return false;
                    }
                    if (useTrDiscount != null ? !useTrDiscount.equals(that.useTrDiscount)
                            : that.useTrDiscount != null) {
                        return false;
                    }
                    if (modeVisible != null ? !modeVisible.equals(that.modeVisible) : that.modeVisible != null) {
                        return false;
                    }

                    return true;
                }

                @Override
                public int hashCode() {
                    HashCodeBuilder builder = new HashCodeBuilder();
                    builder.append(complexId);
                    builder.append(complexMenuName);
                    builder.append(modeFree);
                    builder.append(modeGrant);
                    builder.append(modeOfAdd);
                    builder.append(usedSubscriptionFeeding);
                    builder.append(usedVariableFeeding);
                    builder.append(usedSpecialMenu);
                    builder.append(useTrDiscount);
                    builder.append(currentPrice);
                    builder.append(goodsGuid);
                    builder.append(reqMenuDetail);
                    builder.append(complexInfoDiscountDetail);
                    builder.append(modeVisible);
                    if (complexInfoDetails != null) {
                        for (ReqComplexInfoDetail obj : complexInfoDetails) {
                            builder.append(obj);
                        }
                    }
                    return builder.toHashCode();
                }
            }

            public static class ReqMenuDetail {

                public String getShortName() {
                    return shortName;
                }

                public static boolean areMenuDetailsEqual(MenuDetail menuDetail,
                        ReqMenuDetail reqMenuDetail) {
                    return valueEqualNullSafe(reqMenuDetail.getPath(), menuDetail.getMenuPath())
                            && valueEqualNullSafe(reqMenuDetail.getPrice(), menuDetail.getPrice())
                            && valueEqualNullSafe(reqMenuDetail.getGroup(), menuDetail.getGroupName())
                            && valueEqualNullSafe(reqMenuDetail.getOutput(), menuDetail.getMenuDetailOutput())
                            && valueEqualNullSafe(reqMenuDetail.getName(), menuDetail.getMenuDetailName())
                            && valueEqualNullSafe(reqMenuDetail.getShortName(), menuDetail.getShortName())
                            && valueEqualNullSafe(reqMenuDetail.getCalories(), menuDetail.getCalories())
                            && valueEqualNullSafe(reqMenuDetail.getProtein(), menuDetail.getProtein())
                            && valueEqualNullSafe(reqMenuDetail.getFat(), menuDetail.getFat())
                            && valueEqualNullSafe(reqMenuDetail.getCarbohydrates(), menuDetail.getCarbohydrates())
                            && valueEqualNullSafe(reqMenuDetail.getItemCode(), menuDetail.getItemCode())
                            && valueEqualNullSafe(reqMenuDetail.getIdOfMenu(), menuDetail.getLocalIdOfMenu());
                }

                private static boolean valueEqualNullSafe(Object str1, Object str2) {
                    return (str1 == null ? "" : str1).equals(str2 == null ? "" : str2);
                }

                public String getItemCode() {
                    return itemCode;
                }

                public static class Builder {

                    public ReqMenuDetail build(Node menuDetailNode, MenuGroups menuGroups, Map<String, Long> idofGoodsMap) throws Exception {
                        NamedNodeMap namedNodeMap = menuDetailNode.getAttributes();
                        String name = StringUtils.substring(getTextContent(namedNodeMap.getNamedItem("Name")), 0, 256);
                        String shortName = name;
                        Node fullNameNode = namedNodeMap.getNamedItem("FullName");
                        if (null != fullNameNode && StringUtils.isNotEmpty(fullNameNode.getTextContent())) {
                            name = StringUtils.substring(fullNameNode.getTextContent(), 0, 256);
                        }
                        String path = getTextContent(namedNodeMap.getNamedItem("Path"));
                        if (path == null) {
                            path = "";
                        }

                        String group = MenuDetail.DEFAULT_GROUP_NAME;
                        Node refIdOfMGNode = namedNodeMap.getNamedItem("refIdOfMG");
                        if (refIdOfMGNode != null) {
                            String refIdOfMenuGroup = getTextContent(refIdOfMGNode);
                            if (refIdOfMenuGroup != null) {
                                group = menuGroups.findMenuGroup(Long.parseLong(refIdOfMenuGroup));
                            }
                        }
                        String output = null;
                        Node outputNode = namedNodeMap.getNamedItem("Output");
                        if (outputNode != null) {
                            output = StringUtils.substring(getTextContent(outputNode), 0, 32);
                        }
                        String idOfMenuStr = getTextContent(namedNodeMap.getNamedItem("IdOfMenu"));
                        Long idOfMenu = null;
                        if (idOfMenuStr != null) {
                            idOfMenu = Long.parseLong(idOfMenuStr);
                        }
                        Long price = null;
                        Node priceNode = namedNodeMap.getNamedItem("Price");
                        if (priceNode != null) {
                            price = Long.parseLong(priceNode.getTextContent());
                        }
                        String menuOriginStr = getTextContent(namedNodeMap.getNamedItem("MenuOrigin"));
                        int menuOrigin = 0; // собственное прозводство
                        if (menuOriginStr != null) {
                            menuOrigin = Integer.parseInt(menuOriginStr);
                        }
                        String availableNowStr = getTextContent(namedNodeMap.getNamedItem("Avail"));
                        int availableNow = 0; // нет
                        if (availableNowStr != null) {
                            availableNow = Integer.parseInt(availableNowStr);
                        }

                        int flags = 1;
                        Node flagsNode = namedNodeMap.getNamedItem("Flags");
                        if (flagsNode != null) {
                            String flagsStr = getTextContent(flagsNode);
                            if (flagsStr != null) {
                                flags = Integer.parseInt(flagsStr);
                            }
                        }
                        int priority = 0;
                        Node priorityNode = namedNodeMap.getNamedItem("Priority");
                        if (priceNode != null) {
                            String priorityStr = getTextContent(priorityNode);
                            if (priorityStr != null) {
                                priority = Integer.parseInt(priorityStr);
                            }
                        }
                        String guidOfGood;
                        Long idOfGood = null;
                        Node guidOfGoodNode = namedNodeMap.getNamedItem("GuidOfGoods");
                        if (null != guidOfGoodNode) {
                            guidOfGood = getTextContent(guidOfGoodNode);
                            if (null != guidOfGood) {
                                Long idFromMap = idofGoodsMap.get(guidOfGood);
                                if (idFromMap != null) {
                                    idOfGood = idFromMap;
                                } else {
                                    RuntimeContext runtimeContext = RuntimeContext.getInstance();
                                    Session session = null;
                                    Transaction transaction = null;
                                    try {
                                        session = runtimeContext.createReportPersistenceSession();
                                        transaction = session.beginTransaction();
                                        idOfGood = DAOUtils.findIdOfGoodByGuid(session, guidOfGood);
                                        idofGoodsMap.put(guidOfGood, idOfGood);
                                        transaction.commit();
                                        transaction = null;
                                    } catch (Exception e) {
                                        logger.error("Failed build MenuDetail section ", e);
                                    } finally {
                                        HibernateUtils.rollback(transaction, logger);
                                        HibernateUtils.close(session, logger);
                                    }
                                }
                            }
                        }

                        String gBasket = getTextContent(namedNodeMap.getNamedItem("GBasketEl"));
                        String itemCode = getTextContent(namedNodeMap.getNamedItem("ItemCode"));

                        Double protein = getDoubleValue(namedNodeMap, "Protein");
                        Double fat = getDoubleValue(namedNodeMap, "Fat");
                        Double carbohydrates = getDoubleValue(namedNodeMap, "Carbohydrates");
                        Double calories = getDoubleValue(namedNodeMap, "Calories");

                        Double vitB1 = getDoubleValue(namedNodeMap, "VitB1");
                        Double vitC = getDoubleValue(namedNodeMap, "VitC");
                        Double vitA = getDoubleValue(namedNodeMap, "VitA");
                        Double vitE = getDoubleValue(namedNodeMap, "VitE");
                        Double minCa = getDoubleValue(namedNodeMap, "MinCa");
                        Double minP = getDoubleValue(namedNodeMap, "MinP");
                        Double minMg = getDoubleValue(namedNodeMap, "MinMg");
                        Double minFe = getDoubleValue(namedNodeMap, "MinFe");
                        Double vitB2 = getDoubleValue(namedNodeMap, "VitB2");
                        Double vitPp = getDoubleValue(namedNodeMap, "VitPP");
                        return new ReqMenuDetail(idOfMenu, path, name, group, output, price, menuOrigin, availableNow,
                                flags, priority, protein, fat, carbohydrates, calories, vitB1, vitC, vitA, vitE, minCa,
                                minP, minMg, minFe, vitB2, vitPp, gBasket, shortName, idOfGood, itemCode);
                    }

                    private static String getTextContent(Node node) throws Exception {
                        if (null == node) {
                            return null;
                        }
                        return node.getTextContent();
                    }

                    private static Double getDoubleValue(NamedNodeMap namedNodeMap, String name) throws Exception {
                        Node node = namedNodeMap.getNamedItem(name);
                        if (null == node) {
                            return null;
                        }
                        String calString = node.getTextContent();
                        if (calString.equals("")) {
                            return null;
                        }

                        String replacedString = calString.replaceAll(",", ".");

                        String[] parts = replacedString.split("\\.");

                        if (parts[0].length() > 8) {
                            logger.error("Ошибка при сохранении в базу элемента меню " + "IdOfMenu = " + getTextContent(
                                    namedNodeMap.getNamedItem("IdOfMenu")) + ", " + "Patch = " + getTextContent(
                                    namedNodeMap.getNamedItem("Path")) + ", " + "FullName = " + getTextContent(
                                    namedNodeMap.getNamedItem("FullName")) + ", не верно задана размерность, параметра "
                                    + name + " = " + calString);
                            return null;
                        }


                        if (Double.parseDouble(replacedString) < 0) {
                            logger.error("Ошибка при сохранении в базу элемента меню " + "IdOfMenu = " + getTextContent(
                                    namedNodeMap.getNamedItem("IdOfMenu")) + ", " + "Patch = " + getTextContent(
                                    namedNodeMap.getNamedItem("Path")) + ", " + "FullName = " + getTextContent(
                                    namedNodeMap.getNamedItem("FullName")) + " задано отрицательное число, в параметре "
                                    + name + " = " + calString);
                            return null;
                        }

                        return Double.parseDouble(replacedString);
                    }
                }

                private final Long idOfMenu;
                private final String path;
                private final String name;
                private final String group;
                private final String output;
                private final Long price;
                private final Double protein;
                private final Double fat;
                private final Double carbohydrates;
                private final Double calories;
                private final Double vitB1;
                private final Double vitB2;
                private final Double vitPp;
                private final Double vitC;
                private final Double vitA;
                private final Double vitE;
                private final Double minCa;
                private final Double minP;
                private final Double minMg;
                private final Double minFe;
                private final int menuOrigin;
                private final int availableNow;
                private final Integer flags;
                private final Integer priority;
                private final String gBasket;
                private final String shortName;
                private final Long idOfGood;
                private final String itemCode;

                public ReqMenuDetail(Long idOfMenu, String path, String name, String group, String output, Long price,
                        int menuOrigin, int availableNow, Integer flags, Integer priority, Double protein, Double fat,
                        Double carbohydrates, Double calories, Double vitB1, Double vitC, Double vitA, Double vitE,
                        Double minCa, Double minP, Double minMg, Double minFe, Double vitB2, Double vitPp, String gBasket,
                        String shortName, Long idOfGood, String itemCode) {
                    this.idOfMenu = idOfMenu;
                    this.path = path;
                    this.name = name;
                    this.group = group;
                    this.output = output;
                    this.price = price;
                    this.menuOrigin = menuOrigin;
                    this.availableNow = availableNow;
                    this.flags = flags;
                    this.priority = priority;
                    this.protein = protein;
                    this.fat = fat;
                    this.carbohydrates = carbohydrates;
                    this.calories = calories;
                    this.vitB1 = vitB1;
                    this.vitC = vitC;
                    this.vitA = vitA;
                    this.vitE = vitE;
                    this.minCa = minCa;
                    this.minP = minP;
                    this.minMg = minMg;
                    this.minFe = minFe;
                    this.vitB2 = vitB2;
                    this.vitPp = vitPp;
                    this.gBasket = gBasket;
                    this.shortName = shortName;
                    this.idOfGood = idOfGood;
                    this.itemCode = itemCode;
                }

                public Long getIdOfMenu() {
                    return idOfMenu;
                }

                public String getPath() {
                    return path;
                }

                public int getAvailableNow() {
                    return availableNow;
                }

                public int getMenuOrigin() {
                    return menuOrigin;
                }

                public String getName() {
                    return name;
                }

                public String getGroup() {
                    return group;
                }

                public String getOutput() {
                    return output;
                }

                public Long getPrice() {
                    return price;
                }

                public Integer getFlags() {
                    return flags;
                }

                public Integer getPriority() {
                    return priority;
                }

                public Double getProtein() {
                    return protein;
                }

                public Double getFat() {
                    return fat;
                }

                public Double getCarbohydrates() {
                    return carbohydrates;
                }

                public Double getCalories() {
                    return calories;
                }

                public Double getVitB1() {
                    return vitB1;
                }

                public Double getVitC() {
                    return vitC;
                }

                public Double getVitA() {
                    return vitA;
                }

                public Double getVitE() {
                    return vitE;
                }

                public Double getMinCa() {
                    return minCa;
                }

                public Double getMinP() {
                    return minP;
                }

                public Double getMinMg() {
                    return minMg;
                }

                public Double getMinFe() {
                    return minFe;
                }

                public Double getVitB2() {
                    return vitB2;
                }

                public Double getVitPp() {
                    return vitPp;
                }

                public String getgBasket() {
                    return gBasket;
                }

                public Long getIdOfGood() {
                    return idOfGood;
                }

                @Override
                public String toString() {
                    return "ReqMenuDetail{" + "idOfMenu=" + idOfMenu + ", path='" + path + '\'' + ", name='" + name
                            + '\'' + ", group='" + group + '\'' + ", output='" + output + '\'' + ", price=" + price
                            + ", protein=" + protein + ", fat=" + fat + ", carbohydrates=" + carbohydrates
                            + ", calories=" + calories + ", vitB1=" + vitB1 + ", vitB2=" + vitB2 + ", vitPp=" + vitPp
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + ", menuOrigin=" + menuOrigin
                            + ", availableNow=" + availableNow + ", flags=" + flags + ", priority=" + priority
                            + ", gBasketEl=" + gBasket + ", shortName=" + shortName + ", idOfGood=" + idOfGood + '}';
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (o == null || getClass() != o.getClass()) {
                        return false;
                    }

                    ReqMenuDetail that = (ReqMenuDetail) o;

                    if (availableNow != that.availableNow) {
                        return false;
                    }
                    if (menuOrigin != that.menuOrigin) {
                        return false;
                    }
                    if (calories != null ? !calories.equals(that.calories) : that.calories != null) {
                        return false;
                    }
                    if (carbohydrates != null ? !carbohydrates.equals(that.carbohydrates)
                            : that.carbohydrates != null) {
                        return false;
                    }
                    if (fat != null ? !fat.equals(that.fat) : that.fat != null) {
                        return false;
                    }
                    if (flags != null ? !flags.equals(that.flags) : that.flags != null) {
                        return false;
                    }
                    if (group != null ? !group.equals(that.group) : that.group != null) {
                        return false;
                    }
                    if (idOfMenu != null ? !idOfMenu.equals(that.idOfMenu) : that.idOfMenu != null) {
                        return false;
                    }
                    if (minCa != null ? !minCa.equals(that.minCa) : that.minCa != null) {
                        return false;
                    }
                    if (minFe != null ? !minFe.equals(that.minFe) : that.minFe != null) {
                        return false;
                    }
                    if (minMg != null ? !minMg.equals(that.minMg) : that.minMg != null) {
                        return false;
                    }
                    if (minP != null ? !minP.equals(that.minP) : that.minP != null) {
                        return false;
                    }
                    if (name != null ? !name.equals(that.name) : that.name != null) {
                        return false;
                    }
                    if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) {
                        return false;
                    }
                    if (output != null ? !output.equals(that.output) : that.output != null) {
                        return false;
                    }
                    if (path != null ? !path.equals(that.path) : that.path != null) {
                        return false;
                    }
                    if (price != null ? !price.equals(that.price) : that.price != null) {
                        return false;
                    }
                    if (priority != null ? !priority.equals(that.priority) : that.priority != null) {
                        return false;
                    }
                    if (protein != null ? !protein.equals(that.protein) : that.protein != null) {
                        return false;
                    }
                    if (vitA != null ? !vitA.equals(that.vitA) : that.vitA != null) {
                        return false;
                    }
                    if (vitB1 != null ? !vitB1.equals(that.vitB1) : that.vitB1 != null) {
                        return false;
                    }
                    if (vitC != null ? !vitC.equals(that.vitC) : that.vitC != null) {
                        return false;
                    }
                    if (vitE != null ? !vitE.equals(that.vitE) : that.vitE != null) {
                        return false;
                    }
                    if (gBasket != null ? !gBasket.equals(that.gBasket) : that.gBasket != null) {
                        return false;
                    }

                    return true;
                }

                @Override
                public int hashCode() {
                    HashCodeBuilder builder = new HashCodeBuilder();
                    builder.append(path);
                    builder.append(name);
                    builder.append(shortName);
                    builder.append(group);
                    builder.append(output);
                    builder.append(price);
                    builder.append(protein);
                    builder.append(fat);
                    builder.append(carbohydrates);
                    builder.append(calories);
                    builder.append(vitB1);
                    builder.append(vitC);
                    builder.append(vitA);
                    builder.append(vitE);
                    builder.append(minP);
                    builder.append(minMg);
                    builder.append(minFe);
                    builder.append(menuOrigin);
                    builder.append(availableNow);
                    builder.append(flags);
                    builder.append(priority);
                    builder.append(gBasket);
                    return builder.toHashCode();
                }
            }

            public static class ReqAssortment {

                public static class Builder {

                    public ReqAssortment build(Node node, MenuGroups menuGroups) throws Exception {
                        NamedNodeMap namedNodeMap = node.getAttributes();
                        long idOfAst = Long.parseLong(namedNodeMap.getNamedItem("IdOfAst").getTextContent());
                        String name = StringUtils.substring(getTextContent(namedNodeMap.getNamedItem("Name")), 0, 256);
                        Node fullNameNode = namedNodeMap.getNamedItem("FullName");
                        String fullName = name;
                        if (null != fullNameNode && StringUtils.isNotEmpty(fullNameNode.getTextContent())) {
                            fullName = StringUtils.substring(fullNameNode.getTextContent(), 0, 256);
                        }
                        String group = StringUtils.substring(getTextContent(namedNodeMap.getNamedItem("Group")), 0, 60);
                        if (StringUtils.isEmpty(group)) {
                            String refIdOfMenuGroup = getTextContent(namedNodeMap.getNamedItem("refIdOfMG"));
                            if (refIdOfMenuGroup != null) {
                                group = menuGroups.findMenuGroup(Long.parseLong(refIdOfMenuGroup));
                            }
                            if (StringUtils.isEmpty(group)) {
                                group = MenuDetail.DEFAULT_GROUP_NAME;
                            }
                        }
                        String output = StringUtils
                                .substring(getTextContent(namedNodeMap.getNamedItem("Output")), 0, 32);
                        if (output == null) {
                            output = "";
                        }
                        long price = Long.parseLong(namedNodeMap.getNamedItem("Price").getTextContent());
                        String menuOriginStr = getTextContent(namedNodeMap.getNamedItem("MenuOrigin"));
                        int menuOrigin = 0; // собственное прозводство
                        if (menuOriginStr == null) {
                            menuOrigin = Integer.parseInt(menuOriginStr);
                        }
                        Double protein = getMinorComponent(namedNodeMap, "Protein");
                        Double fat = getMinorComponent(namedNodeMap, "Fat");
                        Double carbohydrates = getMinorComponent(namedNodeMap, "Carbohydrates");
                        Double calories = getCalories(namedNodeMap, "Calories");
                        Double vitB1 = getMinorComponent(namedNodeMap, "VitB1");
                        Double vitC = getMinorComponent(namedNodeMap, "VitC");
                        Double vitA = getMinorComponent(namedNodeMap, "VitA");
                        Double vitE = getMinorComponent(namedNodeMap, "VitE");
                        Double minCa = getMinorComponent(namedNodeMap, "MinCa");
                        Double minP = getMinorComponent(namedNodeMap, "MinP");
                        Double minMg = getMinorComponent(namedNodeMap, "MinMg");
                        Double minFe = getMinorComponent(namedNodeMap, "MinFe");
                        Double vitB2 = getMinorComponent(namedNodeMap, "VitB2");
                        Double vitPp = getMinorComponent(namedNodeMap, "VitPP");
                        return new ReqAssortment(name, fullName, group, output, price, menuOrigin, protein, fat,
                                carbohydrates, calories, vitB1, vitC, vitA, vitE, minCa, minP, minMg, minFe, vitB2,
                                vitPp);
                    }

                    private static Double getMinorComponent(NamedNodeMap namedNodeMap, String name) throws Exception {
                        Node node = namedNodeMap.getNamedItem(name);
                        if (null == node) {
                            return null;
                        }
                        return ((double) Long.parseLong(node.getTextContent())) / 100;
                    }

                    private static Double getCalories(NamedNodeMap namedNodeMap, String name) {
                        Node node = namedNodeMap.getNamedItem(name);
                        if (null == node) {
                            return null;
                        }
                        String calString = node.getTextContent();
                        if (calString.equals("")) {
                            return null;
                        }
                        String replacedString = calString.replaceAll(",", ".");
                        return Double.parseDouble(replacedString);
                    }

                    private static String getTextContent(Node node) throws Exception {
                        if (null == node) {
                            return null;
                        }
                        return node.getTextContent();
                    }
                }

                private final String name;
                private final String fullName;
                private final String group;
                private final String menuOutput;
                private final long price;
                private final Double protein;
                private final Double fat;
                private final Double carbohydrates;
                private final Double calories;
                private final Double vitB1;
                private final Double vitC;
                private final Double vitA;
                private final Double vitE;
                private final Double minCa;
                private final Double minP;
                private final Double minMg;
                private final Double minFe;
                private final Double vitB2;
                private final Double vitPp;
                private final int menuOrigin;

                public ReqAssortment(String name, String fullName, String group, String menuOutput, long price,
                        int menuOrigin, Double protein, Double fat, Double carbohydrates, Double calories, Double vitB1,
                        Double vitC, Double vitA, Double vitE, Double minCa, Double minP, Double minMg, Double minFe,
                        Double vitB2, Double vitPp) {
                    this.name = name;
                    this.fullName = fullName;
                    this.group = group;
                    this.menuOutput = menuOutput;
                    this.price = price;
                    this.menuOrigin = menuOrigin;
                    this.protein = protein;
                    this.fat = fat;
                    this.carbohydrates = carbohydrates;
                    this.calories = calories;
                    this.vitB1 = vitB1;
                    this.vitC = vitC;
                    this.vitA = vitA;
                    this.vitE = vitE;
                    this.minCa = minCa;
                    this.minP = minP;
                    this.minMg = minMg;
                    this.minFe = minFe;
                    this.vitPp = vitPp;
                    this.vitB2 = vitB2;
                }

                public String getFullName() {
                    return fullName;
                }

                public int getMenuOrigin() {
                    return menuOrigin;
                }

                public String getName() {
                    return name;
                }

                public String getGroup() {
                    return group;
                }

                public String getMenuOutput() {
                    return menuOutput;
                }

                public long getPrice() {
                    return price;
                }

                public Double getProtein() {
                    return protein;
                }

                public Double getFat() {
                    return fat;
                }

                public Double getCarbohydrates() {
                    return carbohydrates;
                }

                public Double getCalories() {
                    return calories;
                }

                public Double getVitB1() {
                    return vitB1;
                }

                public Double getVitC() {
                    return vitC;
                }

                public Double getVitA() {
                    return vitA;
                }

                public Double getVitE() {
                    return vitE;
                }

                public Double getMinCa() {
                    return minCa;
                }

                public Double getMinP() {
                    return minP;
                }

                public Double getMinMg() {
                    return minMg;
                }

                public Double getMinFe() {
                    return minFe;
                }

                public Double getVitB2() {
                    return vitB2;
                }

                public Double getVitPp() {
                    return vitPp;
                }

                @Override
                public String toString() {
                    return "ReqAssortment{" + "name='" + name + '\'' + ", group='" + group + '\'' + ", menuOutput='"
                            + menuOutput + '\'' + ", price=" + price + ", protein=" + protein + ", fat=" + fat
                            + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1=" + vitB1
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + ", vitB2=" + vitB2 + ", vitPp=" + vitPp + '}';
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (o == null || getClass() != o.getClass()) {
                        return false;
                    }

                    ReqAssortment that = (ReqAssortment) o;

                    if (menuOrigin != that.menuOrigin) {
                        return false;
                    }
                    if (price != that.price) {
                        return false;
                    }
                    if (calories != null ? !calories.equals(that.calories) : that.calories != null) {
                        return false;
                    }
                    if (carbohydrates != null ? !carbohydrates.equals(that.carbohydrates)
                            : that.carbohydrates != null) {
                        return false;
                    }
                    if (fat != null ? !fat.equals(that.fat) : that.fat != null) {
                        return false;
                    }
                    if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) {
                        return false;
                    }
                    if (group != null ? !group.equals(that.group) : that.group != null) {
                        return false;
                    }
                    if (menuOutput != null ? !menuOutput.equals(that.menuOutput) : that.menuOutput != null) {
                        return false;
                    }
                    if (minCa != null ? !minCa.equals(that.minCa) : that.minCa != null) {
                        return false;
                    }
                    if (minFe != null ? !minFe.equals(that.minFe) : that.minFe != null) {
                        return false;
                    }
                    if (minMg != null ? !minMg.equals(that.minMg) : that.minMg != null) {
                        return false;
                    }
                    if (minP != null ? !minP.equals(that.minP) : that.minP != null) {
                        return false;
                    }
                    if (name != null ? !name.equals(that.name) : that.name != null) {
                        return false;
                    }
                    if (protein != null ? !protein.equals(that.protein) : that.protein != null) {
                        return false;
                    }
                    if (vitA != null ? !vitA.equals(that.vitA) : that.vitA != null) {
                        return false;
                    }
                    if (vitB1 != null ? !vitB1.equals(that.vitB1) : that.vitB1 != null) {
                        return false;
                    }
                    if (vitB2 != null ? !vitB2.equals(that.vitB2) : that.vitB2 != null) {
                        return false;
                    }
                    if (vitPp != null ? !vitPp.equals(that.vitPp) : that.vitPp != null) {
                        return false;
                    }
                    if (vitC != null ? !vitC.equals(that.vitC) : that.vitC != null) {
                        return false;
                    }
                    if (vitE != null ? !vitE.equals(that.vitE) : that.vitE != null) {
                        return false;
                    }

                    return true;
                }

                @Override
                public int hashCode() {
                    HashCodeBuilder builder = new HashCodeBuilder();
                    builder.append(name);
                    builder.append(fullName);
                    builder.append(group);
                    builder.append(menuOutput);
                    builder.append(price);
                    builder.append(protein);
                    builder.append(fat);
                    builder.append(carbohydrates);
                    builder.append(calories);
                    builder.append(vitB1);
                    builder.append(vitB2);
                    builder.append(vitPp);
                    builder.append(vitC);
                    builder.append(vitA);
                    builder.append(vitE);
                    builder.append(minP);
                    builder.append(minMg);
                    builder.append(minFe);
                    builder.append(menuOrigin);
                    return builder.toHashCode();
                }
            }

            public static class Builder {

                private final ReqMenuDetail.Builder reqMenuDetailBuilder;
                private final ReqComplexInfo.Builder reqComplexInfoBuilder;
                private final ReqAssortment.Builder reqAssortmentBuilder;

                public Builder() {
                    this.reqMenuDetailBuilder = new ReqMenuDetail.Builder();
                    this.reqComplexInfoBuilder = new ReqComplexInfo.Builder();
                    this.reqAssortmentBuilder = new ReqAssortment.Builder();
                }

                public Item build(Node itemNode, LoadContext loadContext, Map<String, Long> idofGoodsMap) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();

                    Date date = loadContext.getDateOnlyFormat().parse(namedNodeMap.getNamedItem("Value").getTextContent());
                    ////// process ML items (menu list)
                    List<ReqMenuDetail> reqMenuDetails = new LinkedList<ReqMenuDetail>();
                    HashMap<Long, ReqMenuDetail> reqMenuDetailMap = new HashMap<Long, ReqMenuDetail>();
                    List<ReqAssortment> reqAssortments = new LinkedList<ReqAssortment>();
                    Node childNode = itemNode.getFirstChild();
                    while (null != childNode) {
                        if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("ML")) {
                            ReqMenuDetail reqMenuDetail = reqMenuDetailBuilder.build(childNode, loadContext.getMenuGroups(), idofGoodsMap);
                            reqMenuDetails.add(reqMenuDetail);
                            if (reqMenuDetail.idOfMenu != null) {
                                reqMenuDetailMap.put(reqMenuDetail.idOfMenu, reqMenuDetail);
                            }
                        } else if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName()
                                .equals("AMI")) {
                            ReqAssortment reqAssortment = reqAssortmentBuilder.build(childNode, loadContext.getMenuGroups());
                            reqAssortments.add(reqAssortment);
                        }
                        childNode = childNode.getNextSibling();
                    }
                    ////// process CML items (complex menu)
                    List<ReqComplexInfo> reqComplexInfos = new LinkedList<ReqComplexInfo>();
                    if (loadContext.getProtoVersion() >= 5) {
                        childNode = itemNode.getFirstChild();
                        while (null != childNode) {
                            if (Node.ELEMENT_NODE == childNode.getNodeType() && (childNode.getNodeName().equals("CML")
                                    || childNode.getNodeName().equals("CML2") || childNode.getNodeName().equals("DCML"))) {
                                ReqComplexInfo reqComplexInfo = reqComplexInfoBuilder
                                        .build(childNode, reqMenuDetailMap);
                                reqComplexInfos.add(reqComplexInfo);
                            }
                            childNode = childNode.getNextSibling();
                        }
                    }
                    ////////
                    String rawXML = ru.axetta.ecafe.processor.core.utils.XMLUtils.nodeToString(itemNode);
                    ////////
                    return new Item(date, reqMenuDetails, reqMenuDetailMap, reqComplexInfos, reqAssortments, rawXML);
                }

            }

            private final String rawXmlText;
            private final Date date;
            private final List<ReqMenuDetail> reqMenuDetails;
            private final HashMap<Long, ReqMenuDetail> reqMenuDetailsByIdOfMenu;
            private final List<ReqComplexInfo> reqComplexInfos;
            private final List<ReqAssortment> reqAssortments;

            public Item(Date date, List<ReqMenuDetail> reqMenuDetails,
                    HashMap<Long, ReqMenuDetail> reqMenuDetailsByIdOfMenu, List<ReqComplexInfo> reqComplexInfos,
                    List<ReqAssortment> reqAssortments, String rawXmlText) {
                this.date = date;
                this.reqMenuDetails = reqMenuDetails;
                this.reqMenuDetailsByIdOfMenu = reqMenuDetailsByIdOfMenu;
                this.reqComplexInfos = reqComplexInfos;
                this.reqAssortments = reqAssortments;
                this.rawXmlText = rawXmlText;
            }

            public List<ReqComplexInfo> getReqComplexInfos() {
                return reqComplexInfos;
            }

            public List<ReqAssortment> getReqAssortments() {
                return reqAssortments;
            }

            private ReqMenuDetail findMenuDetail(long refIdOfMenu) {
                return reqMenuDetailsByIdOfMenu.get(refIdOfMenu);
            }

            public Date getDate() {
                return date;
            }

            public Iterator<ReqMenuDetail> getReqMenuDetails() {
                return reqMenuDetails.iterator();
            }

            public String getRawXmlText() {
                return rawXmlText;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                Item item = (Item) o;

                if (date != null ? !date.equals(item.date) : item.date != null) {
                    return false;
                }
                if (reqMenuDetails != null ? !reqMenuDetails.equals(item.reqMenuDetails)
                        : item.reqMenuDetails != null) {
                    return false;
                }
                if (reqAssortments != null ? !reqAssortments.equals(item.reqAssortments)
                        : item.reqAssortments != null) {
                    return false;
                }
                if (reqComplexInfos != null ? !reqComplexInfos.equals(item.reqComplexInfos)
                        : item.reqComplexInfos != null) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                HashCodeBuilder builder = new HashCodeBuilder();
                if (reqMenuDetails != null) {
                    for (ReqMenuDetail obj : reqMenuDetails) {
                        builder.append(obj);
                    }
                }
                if (reqAssortments != null) {
                    for (ReqAssortment obj : reqAssortments) {
                        builder.append(obj);
                    }
                }
                if (reqComplexInfos != null) {
                    for (ReqComplexInfo obj : reqComplexInfos) {
                        builder.append(obj);
                    }
                }
                return builder.toHashCode();
            }

            @Override
            public String toString() {
                return "Item{" +
                        "date=" + date +
                        ", reqMenuDetails=" + reqMenuDetails +
                        '}';
            }

        }

        public static class Builder implements SectionRequestBuilder {

            private final Item.Builder itemBuilder;
            private final LoadContext loadContext;

            public Builder(LoadContext loadContext) {
                this.loadContext = loadContext;
                this.itemBuilder = new Item.Builder();
            }

            public ReqMenu build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (ReqMenu) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ReqMenu.SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement);
                } else
                    return null;
            }

            private ReqMenu buildFromCorrectSection(Node menuNode) throws Exception {
                List<Item> items = new LinkedList<Item>();
                Node itemNode = menuNode.getFirstChild();
                String settingsSectionRawXML = null;
                Map<String, Long> idofGoodsMap = new HashMap<>();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Settings")) {
                        settingsSectionRawXML = ru.axetta.ecafe.processor.core.utils.XMLUtils.nodeToString(itemNode);
                    } else if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Date")) {
                        items.add(itemBuilder.build(itemNode, loadContext, idofGoodsMap));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new ReqMenu(items, settingsSectionRawXML);
            }
        }

        private final List<Item> items;
        private final String settingsSectionRawXML;

        public ReqMenu(List<Item> items, String settingsSectionRawXML) {
            this.items = items;
            this.settingsSectionRawXML = settingsSectionRawXML;
        }

        public Iterator<Item> getItems() {
            return items.iterator();
        }

        public String getSettingsSectionRawXML() {
            return settingsSectionRawXML;
        }

        @Override
        public String toString() {
            return "ReqMenu{" + "items=" + items + '}';
        }
    }

    public static class ClientRegistryRequest implements SectionRequest {

        public static final String SECTION_NAME = "ClientRegistryRequest";

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class Builder implements SectionRequestBuilder {

            public ClientRegistryRequest build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (ClientRegistryRequest) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ClientRegistryRequest.SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement);
                } else
                    return null;
            }

            private ClientRegistryRequest buildFromCorrectSection(Node clientRegistryRequestNode) throws Exception {
                long currentVersion = Long.parseLong(
                        clientRegistryRequestNode.getAttributes().getNamedItem("CurrentVersion").getTextContent());
                Long currentCount = XMLUtils.getLongAttributeValue(clientRegistryRequestNode, "CurrentCount");
                return new ClientRegistryRequest(currentVersion, currentCount);
            }

        }

        private final long currentVersion;
        private final Long currentCount;

        public ClientRegistryRequest(long currentVersion, Long currentCount) {
            this.currentVersion = currentVersion;
            this.currentCount = currentCount;
        }

        public long getCurrentVersion() {
            return currentVersion;
        }

        public Long getCurrentCount() {
            return currentCount;
        }

        @Override
        public String toString() {
            return "ClientRegistryRequest{" + "currentVersion=" + currentVersion + '}';
        }
    }

    public static class ReqDiary implements SectionRequest {

        public static final String SECTION_NAME = "Diary";

        @Override
        public String getRequestSectionName() {
            return SECTION_NAME;
        }

        public static class ReqDiaryClass {

            public static class Builder {

                public ReqDiaryClass build(Node reqDiaryClassNode) throws Exception {
                    NamedNodeMap namedNodeMap = reqDiaryClassNode.getAttributes();
                    long idOfClass = getLongValue(namedNodeMap, "IdOfClass");
                    String name = StringUtils.substring(namedNodeMap.getNamedItem("Name").getTextContent(), 0, 64);
                    return new ReqDiaryClass(idOfClass, name);
                }

            }

            private final long idOfClass;
            private final String name;

            public ReqDiaryClass(long idOfClass, String name) {
                this.idOfClass = idOfClass;
                this.name = name;
            }

            public long getIdOfClass() {
                return idOfClass;
            }

            public String getName() {
                return name;
            }

            @Override
            public String toString() {
                return "ReqDiaryClass{" + "idOfClass=" + idOfClass + ", name='" + name + '\'' + '}';
            }
        }

        public static class ReqDiaryTimesheet {

            public static class ReqDiaryValue {

                public static class Builder {

                    public ReqDiaryValue build(Node reqDiaryValueNode) throws Exception {
                        NamedNodeMap namedNodeMap = reqDiaryValueNode.getAttributes();
                        long idOfClient = getLongValue(namedNodeMap, "CT");
                        long idOfClass = getLongValue(namedNodeMap, "CS");
                        String value = StringUtils.substring(namedNodeMap.getNamedItem("V").getTextContent(), 0, 20);
                        int vType = getIntValue(namedNodeMap, "T");
                        return new ReqDiaryValue(idOfClient, idOfClass, value, vType);
                    }

                }

                private final long idOfClient;
                private final long idOfClass;
                private final String value;
                private final int vType;

                public ReqDiaryValue(long idOfClient, long idOfClass, String value, int vType) {
                    this.idOfClient = idOfClient;
                    this.idOfClass = idOfClass;
                    this.value = value;
                    this.vType = vType;
                }

                public long getIdOfClient() {
                    return idOfClient;
                }

                public long getIdOfClass() {
                    return idOfClass;
                }

                public String getValue() {
                    return value;
                }

                public int getVType() {
                    return vType;
                }

                @Override
                public String toString() {
                    return "ReqDiaryValue{" + "idOfClient=" + idOfClient + ", idOfClass=" + idOfClass + ", value='"
                            + value + '\'' + ", vType=" + vType + '}';
                }
            }

            public static class Builder {

                private final ReqDiaryValue.Builder reqDiaryValueBuilder;

                public Builder() {
                    this.reqDiaryValueBuilder = new ReqDiaryValue.Builder();
                }

                public ReqDiaryTimesheet build(Node reqDiaryTimesheetNode, LoadContext loadContext) throws Exception {
                    NamedNodeMap namedNodeMap = reqDiaryTimesheetNode.getAttributes();
                    Date date = loadContext.getDateOnlyFormat().parse(namedNodeMap.getNamedItem("Date").getTextContent());
                    long idOfClientGroup = getLongValue(namedNodeMap, "IdOfGroup");
                    Long classes[] = new Long[]{
                            getLongValueNullSafe(namedNodeMap, "C0"), getLongValueNullSafe(namedNodeMap, "C1"),
                            getLongValueNullSafe(namedNodeMap, "C2"), getLongValueNullSafe(namedNodeMap, "C3"),
                            getLongValueNullSafe(namedNodeMap, "C4"), getLongValueNullSafe(namedNodeMap, "C5"),
                            getLongValueNullSafe(namedNodeMap, "C6"), getLongValueNullSafe(namedNodeMap, "C7"),
                            getLongValueNullSafe(namedNodeMap, "C8"), getLongValueNullSafe(namedNodeMap, "C9")};

                    List<ReqDiaryValue> reqDiaryValues = new LinkedList<ReqDiaryValue>();
                    Node reqDiaryValueNode = reqDiaryTimesheetNode.getFirstChild();
                    while (null != reqDiaryValueNode) {
                        if (Node.ELEMENT_NODE == reqDiaryValueNode.getNodeType() && reqDiaryValueNode.getNodeName()
                                .equals("R")) {
                            reqDiaryValues.add(reqDiaryValueBuilder.build(reqDiaryValueNode));
                        }
                        reqDiaryValueNode = reqDiaryValueNode.getNextSibling();
                    }
                    return new ReqDiaryTimesheet(date, idOfClientGroup, classes, reqDiaryValues);
                }

            }

            private final Date date;
            private final long idOfClientGroup;
            private final Long classes[];
            private final List<ReqDiaryValue> reqDiaryValues;

            public ReqDiaryTimesheet(Date date, long idOfClientGroup, Long[] classes,
                    List<ReqDiaryValue> reqDiaryValues) {
                this.date = date;
                this.idOfClientGroup = idOfClientGroup;
                this.classes = classes;
                this.reqDiaryValues = reqDiaryValues;
            }

            public Date getDate() {
                return date;
            }

            public long getIdOfClientGroup() {
                return idOfClientGroup;
            }

            public Long[] getClasses() {
                return classes;
            }

            public Enumeration<ReqDiaryValue> getReqDiaryValues() {
                return Collections.enumeration(reqDiaryValues);
            }

            @Override
            public String toString() {
                return "ReqDiaryTimesheet{" + "date=" + date + ", idOfClientGroup=" + idOfClientGroup + ", classes=" + (
                        classes == null ? null : Arrays.asList(classes)) + ", reqDiaryValues=" + reqDiaryValues + '}';
            }
        }

        public static class Builder implements SectionRequestBuilder {

            private final ReqDiaryClass.Builder reqDiaryClassBuilder;
            private final ReqDiaryTimesheet.Builder reqDiaryTimesheetBuilder;
            private final LoadContext loadContext;

            public Builder(LoadContext loadContext) {
                this.loadContext = loadContext;
                this.reqDiaryClassBuilder = new ReqDiaryClass.Builder();
                this.reqDiaryTimesheetBuilder = new ReqDiaryTimesheet.Builder();
            }

            public ReqDiary build(Node envelopeNode) throws Exception {
                SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
                return sectionRequest != null ? (ReqDiary) sectionRequest : null;
            }

            @Override
            public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
                Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ReqDiary.SECTION_NAME);
                if (sectionElement != null) {
                    return buildFromCorrectSection(sectionElement);
                } else
                    return null;
            }

            private ReqDiary buildFromCorrectSection(Node diaryNode) throws Exception {
                List<ReqDiaryClass> reqDiaryClasses = new LinkedList<ReqDiaryClass>();
                List<ReqDiaryTimesheet> reqDiaryTimesheets = new LinkedList<ReqDiaryTimesheet>();
                Node currNode = diaryNode.getFirstChild();
                while (null != currNode) {
                    if (Node.ELEMENT_NODE == currNode.getNodeType()) {
                        String currNodeName = currNode.getNodeName();
                        if (currNodeName.equals("C")) {
                            reqDiaryClasses.add(reqDiaryClassBuilder.build(currNode));
                        } else if (currNodeName.equals("DV")) {
                            reqDiaryTimesheets.add(reqDiaryTimesheetBuilder.build(currNode, loadContext));
                        }
                    }
                    currNode = currNode.getNextSibling();
                }
                return new ReqDiary(reqDiaryClasses, reqDiaryTimesheets);
            }


        }

        private final List<ReqDiaryClass> reqDiaryClasses;
        private final List<ReqDiaryTimesheet> reqDiaryTimesheets;

        public ReqDiary(List<ReqDiaryClass> reqDiaryClasses, List<ReqDiaryTimesheet> reqDiaryTimesheets) {
            this.reqDiaryClasses = reqDiaryClasses;
            this.reqDiaryTimesheets = reqDiaryTimesheets;
        }

        public Enumeration<ReqDiaryClass> getReqDiaryClasses() {
            return Collections.enumeration(reqDiaryClasses);
        }

        public Enumeration<ReqDiaryTimesheet> getReqDiaryTimesheets() {
            return Collections.enumeration(reqDiaryTimesheets);
        }

        @Override
        public String toString() {
            return "ReqDiary{" + "reqDiaryClasses=" + reqDiaryClasses + ", reqDiaryTimesheets=" + reqDiaryTimesheets
                    + '}';
        }
    }

    public static class Builder {

        private final DateFormat dateOnlyFormat;
        private final DateFormat timeFormat;
        private final static int CLIENT_VERSION_FOR_PLAN_ORDER_MAJOR = 93;
        private final static int CLIENT_VERSION_FOR_PLAN_ORDER_MINOR = 1;

        public Builder() {
            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            this.dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.dateOnlyFormat.setTimeZone(utcTimeZone);
            TimeZone localTimeZone = RuntimeContext.getInstance().getDefaultLocalTimeZone(null);
            this.timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            this.timeFormat.setTimeZone(localTimeZone);
        }

        public static Node findEnvelopeNode(Document document) throws Exception {
            Node dataNode = findFirstChildElement(document, "Data");
            Node bodyNode = findFirstChildElement(dataNode, "Body");
            return findFirstChildElement(bodyNode, "CafeteriaExchange");
        }

        public static long getIdOfOrg(NamedNodeMap namedNodeMap) throws Exception {
            return getLongValue(namedNodeMap, "IdOfOrg");
        }

        public static String getIdOfSync(NamedNodeMap namedNodeMap) throws Exception {
            return namedNodeMap.getNamedItem("Date").getTextContent();
        }

        public static Long getIdOfPacket(NamedNodeMap namedNodeMap) throws Exception {
            return getLongValueNullSafe(namedNodeMap, "IdOfPacket");
        }

        public static String getClientVersion(NamedNodeMap namedNodeMap) throws Exception {
            if (namedNodeMap.getNamedItem("ClientVersion") == null)
                return null;
            return namedNodeMap.getNamedItem("ClientVersion").getTextContent();
        }

        public static long getVersion(NamedNodeMap namedNodeMap) throws Exception {
            return getLongValue(namedNodeMap, "Version");
        }

        public static SyncType getSyncType(NamedNodeMap namedNodeMap) throws Exception {
            return SyncType.parse(getStringValueNullSafe(namedNodeMap, "Type"));
        }

        public SyncRequest build(Node envelopeNode, NamedNodeMap namedNodeMap, Org org, String idOfSync,
                String remoteAddr) throws Exception {
            long version = getVersion(namedNodeMap);
            checkClientVersion(version);
            SyncType syncType = getSyncType(namedNodeMap);
            String clientVersion = getClientVersion(namedNodeMap);
            String sqlServerVersion = getSQLServerVersion(namedNodeMap);
            Double databaseSize = getDatabaseSizeValue(namedNodeMap);
            CafeteriaExchangeContentType contentType = getCafeterialExchangeContentType(namedNodeMap);
            Date syncTime = timeFormat.parse(idOfSync);
            Long idOfPacket = getIdOfPacket(namedNodeMap);
            List<SectionRequest> result = new ArrayList<SectionRequest>();
            List<SectionRequestBuilder> builders = createSectionRequestBuilders(version, org.getIdOfOrg(), envelopeNode, clientVersion);
            for (SectionRequestBuilder builder : builders) {
                SectionRequest sectionRequest = buildSeactionRequest(envelopeNode, builder);
                if (sectionRequest != null) {
                    result.add(sectionRequest);
                }
            }
            Manager manager = createManagerSyncRO(envelopeNode, syncType, org);
            String message = getMessage(envelopeNode);
            return new SyncRequest(remoteAddr, version, syncType, clientVersion, org, syncTime, idOfPacket, message,
                    result, manager, sqlServerVersion, databaseSize, contentType);
        }

        private CafeteriaExchangeContentType getCafeterialExchangeContentType(NamedNodeMap namedNodeMap) {
            String contentType = namedNodeMap.getNamedItem("ContentType") == null ?
                    null : namedNodeMap.getNamedItem("ContentType").getTextContent();
            if(contentType != null){
                return CafeteriaExchangeContentType.getContentTypeByCode(Integer.valueOf(contentType));
            }
            return null;
        }

        private String getSQLServerVersion(NamedNodeMap namedNodeMap) {
            return namedNodeMap.getNamedItem("SqlServerVersion") == null ?
                    null : namedNodeMap.getNamedItem("SqlServerVersion").getTextContent();
        }

        private Double getDatabaseSizeValue(NamedNodeMap namedNodeMap) {
            try {
                return namedNodeMap.getNamedItem("DatabaseSize") == null ? null
                        : Double.parseDouble(namedNodeMap.getNamedItem("DatabaseSize").getTextContent().replace(',', '.'));
            } catch (Exception e) {
                logger.error("Error in get database size from packet: ", e);
                return null;
            }
        }

        private SectionRequest buildSeactionRequest(Node envelopeNode, SectionRequestBuilder builder) {
            SectionRequest request = null;
            try {
                request = builder.searchSectionNodeAndBuild(envelopeNode);
            } catch (Exception ex) {
                request = null;
                logger.error("Failed to build section request, " + builder.toString(), ex);
            }
            return request;
        }

        private int[] parseClientVersion(String clientVersion) throws Exception {
            String[] arr = clientVersion.split("\\.");
            int[] result = new int[arr.length];
            int i = 0;
            for (String str : arr) {
                result[i] = Integer.parseInt(str);
                i++;
            }
            return result;
        }

        private boolean addPlanOrdersBuilderByClientVersion(String clientVersion) {
            //false если clientVersion < 93.1
            try {
                int[] versions = parseClientVersion(clientVersion);
                if (versions[2] < CLIENT_VERSION_FOR_PLAN_ORDER_MAJOR) {
                    return false;
                } else {
                    if (versions[2] == CLIENT_VERSION_FOR_PLAN_ORDER_MAJOR && versions[3] < CLIENT_VERSION_FOR_PLAN_ORDER_MINOR) return false; else return true;
                }
            } catch (Exception e) {
                logger.error("Error in parsing client version from packet: ", e);
                return false;
            }
        }

        private List<SectionRequestBuilder> createSectionRequestBuilders(long version, long idOfOrg, Node envelopeNode,
                String clientVersion) {
            MenuGroups menuGroups = new MenuGroups.Builder().build(envelopeNode);
            LoadContext loadContext = new LoadContext(menuGroups, version, timeFormat, dateOnlyFormat);

            ArrayList<SectionRequestBuilder> builders = new ArrayList<SectionRequestBuilder>();
            builders.add(new PaymentRegistryBuilder(loadContext));
            builders.add(new AccountOperationsRegistry.Builder(loadContext));
            builders.add(new AccIncRegistryRequest.Builder(loadContext));
            builders.add(new ClientParamRegistry.Builder(loadContext));
            builders.add(new ClientRegistryRequest.Builder());
            builders.add(new AccRegistryUpdateRequestBuilder());
            builders.add(new OrgStructure.Builder());
            builders.add(new ReqMenu.Builder(loadContext));
            builders.add(new ReqDiary.Builder(loadContext));
            builders.add(new EnterEventsBuilder(loadContext));
            builders.add(new TempCardsOperationBuilder(idOfOrg));
            builders.add(new ClientRequestBuilder());
            builders.add(new ClientGuardianBuilder());
            builders.add(new ProhibitionMenuRequestBuilder());
            builders.add(new OrganizationStructureRequestBuilder());
            builders.add(new CategoriesDiscountsAndRulesBuilder());
            builders.add(new ClientGroupManagerBuilder());
            builders.add(new AccountsRegistryRequestBuilder());
            builders.add(new PreorderFeedingStatusBuilder(idOfOrg));
            builders.add(new ReestrTaloonApprovalBuilder(idOfOrg));
            builders.add(new ReestrTaloonPreorderBuilder(idOfOrg));
            if (addPlanOrdersBuilderByClientVersion(clientVersion)) {
                builders.add(new PlanOrdersRestrictionsBuilder(idOfOrg));
            }
            builders.add(new ZeroTransactionsBuilder(idOfOrg));
            builders.add(new SpecialDatesBuilder(idOfOrg));
            builders.add(new MigrantsBuilder(idOfOrg));
            builders.add(new ClientPhotosBuilder(idOfOrg));
            builders.add(new CardsOperationsRegistry.Builder(loadContext));
            builders.add(new InteractiveReportDataBuilder());
            builders.add(new GoodsBasicBasketRequest.Builder());
            builders.add(new OrganizationComplexesStructureRequest.Builder());
            builders.add(new OrgOwnerDataRequest.Builder());
            builders.add(new CorrectingNumbersOrdersRegistryRequest.Builder());
            builders.add(new DirectivesRequest.Builder());
            builders.add(new QuestionaryClientsRequest.Builder());
            builders.add(new GroupsOrganizationRequest.Builder(idOfOrg));
            builders.add(new InfoMessageRequest.InfoMessageRequestBuilder());
            builders.add(new ListComplexSchedules.Builder(idOfOrg));
            builders.add(new OrgFilesRequestBuilder(idOfOrg));
            builders.add(new HelpRequestBuilder(idOfOrg));
            builders.add(new PreOrdersFeedingBuilder(idOfOrg));
            builders.add(new CardRequestsBuilder(idOfOrg));
            builders.add(new MenusCalendarBuilder(idOfOrg));
            builders.add(new MenusCalendarSupplierBuilder());
            builders.add(new ClientBalanceHoldBuilder(idOfOrg));
            builders.add(new ClientBalanceHoldData.Builder(idOfOrg));
            builders.add(new RequestFeedingBuilder(idOfOrg));
            builders.add(new ClientDiscountDTSZNBuilder(idOfOrg));
            builders.add(new OrgSettingsBuilder(idOfOrg));
			builders.add(new GoodRequestEZDBuilder());
            builders.add(new SyncSettingsRequestBuilder(idOfOrg));
			builders.add(new EmiasBuilder());
            builders.add(new MenuSupplierBuilder(idOfOrg));
            builders.add(new RequestsSupplierBuilder(idOfOrg));
            builders.add(new HardwareSettingsRequestBuilder(idOfOrg));
            builders.add(new TurnstileSettingsRequestBuilder(idOfOrg));
            return builders;
        }

        private Manager createManagerSyncRO(Node envelopeNode, SyncType syncType, Org org) {
            Manager manager = null;
            try {
                Node roNode = findFirstChildElement(envelopeNode, "RO");
                if (roNode == null) {
                    return manager;
                }

                Boolean enableSubscriptionFeeding = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING);
                List<String> groups = new ArrayList<String>();
                groups.add("SettingsGroup");
                /* В короткой синхронизации не участвует библиотека */
                if (!syncType.equals(SyncType.TYPE_COMMODITY_ACCOUNTING))
                    groups.add("LibraryGroup");
                /* Если включена опция товарного учета у организации */
                if (org.getCommodityAccounting()) {
                    groups.addAll(Arrays.asList("ProductsGroup", "DocumentGroup"));
                    /* В обработка абонентного питания*/
                    if (enableSubscriptionFeeding)
                        groups.add("SubscriptionGroup");
                }
                manager = new Manager(org.getIdOfOrg(), groups);
                manager.buildRO(roNode);
            } catch (Exception ex) {
                manager = null;
                logger.error("Failed to build section request, RO");
            }
            return manager;
        }

        private void checkClientVersion(long version) throws Exception {
            if (3L != version && 4L != version && 5L != version && 6L != version) {
                throw new Exception(String.format("Unsupported protoVersion: %d", version));
            }
        }

        private String getMessage(Node envelopeNode) throws Exception {
            String message = null;
            Node messageNode = findFirstChildElement(envelopeNode, "Message");
            if (null != messageNode) {
                message = findFirstChildTextNode(messageNode).getTextContent();
            }
            return message;
        }

    }

    public SyncType getSyncType() {
        return syncType;
    }

    private final SyncType syncType;
    private final String remoteAddr;
    private final long protoVersion;
    private final long idOfOrg;
    private final Org org;
    private final Date syncTime;
    private final Long idOfPacket;
    private String message;
    private String clientVersion;
    private Manager manager;
    private String sqlServerVersion;
    private Double databaseSize;
    private final List<SectionRequest> sectionRequests = new ArrayList<SectionRequest>();
    private CafeteriaExchangeContentType contentType;

    public SyncRequest(String remoteAddr, long protoVersion, SyncType syncType, String clientVersion, Org org, Date syncTime, Long idOfPacket,
            String message, List<SectionRequest> sectionRequests, Manager manager, String sqlServerVersion, Double databaseSize,
            CafeteriaExchangeContentType contentType) {
        this.remoteAddr = remoteAddr;
        this.protoVersion = protoVersion;
        this.syncType = syncType;
        this.clientVersion = clientVersion;
        this.idOfOrg = org.getIdOfOrg();
        this.org = org;
        this.syncTime = syncTime;
        this.idOfPacket = idOfPacket;
        this.message = message;
        this.manager = manager;
        this.sectionRequests.addAll(sectionRequests);
        this.sqlServerVersion = sqlServerVersion;
        this.databaseSize = databaseSize;
        this.contentType = contentType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public long getProtoVersion() {
        return protoVersion;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public Org getOrg() {
        return org;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public long getIdOfPacket() {
        return idOfPacket;
    }

    public boolean isFullSync() {
        return getSyncType() == SyncType.TYPE_FULL;
    }

    public boolean isAccIncSync() {
        return getSyncType() == SyncType.TYPE_GET_ACC_INC;
    }

    public PaymentRegistry getPaymentRegistry() {
        return this.<PaymentRegistry>findSection(PaymentRegistry.class);
    }

    public AccountOperationsRegistry getAccountOperationsRegistry() {
        return this.<AccountOperationsRegistry>findSection(AccountOperationsRegistry.class);
    }

    public AccIncRegistryRequest getAccIncRegistryRequest() {
        return this.<AccIncRegistryRequest>findSection(AccIncRegistryRequest.class);
    }

    public ClientParamRegistry getClientParamRegistry() {
        return this.<ClientParamRegistry>findSection(ClientParamRegistry.class);
    }

    public ClientRegistryRequest getClientRegistryRequest() {
        return this.<ClientRegistryRequest>findSection(ClientRegistryRequest.class);
    }

    public OrgStructure getOrgStructure() {
        return this.<OrgStructure>findSection(OrgStructure.class);
    }

    public ReqMenu getReqMenu() {
        return this.<ReqMenu>findSection(ReqMenu.class);
    }

    public ReqDiary getReqDiary() {
        return this.<ReqDiary>findSection(ReqDiary.class);
    }

    public String getMessage() {
        return message;
    }

    public EnterEvents getEnterEvents() {
        return this.<EnterEvents>findSection(EnterEvents.class);
    }

    public Manager getManager() {
        return manager;
    }

    public TempCardsOperations getTempCardsOperations() {
        return this.<TempCardsOperations>findSection(TempCardsOperations.class);
    }

    public ClientRequests getClientRequests() {
        return this.<ClientRequests>findSection(ClientRequests.class);
    }

    public AccRegistryUpdateRequest getAccRegistryUpdateRequest() {
        return this.<AccRegistryUpdateRequest>findSection(AccRegistryUpdateRequest.class);
    }

    public ClientGuardianRequest getClientGuardianRequest() {
        return this.<ClientGuardianRequest>findSection(ClientGuardianRequest.class);
    }

    public InfoMessageRequest getInfoMessageRequest() {
        return this.<InfoMessageRequest>findSection(InfoMessageRequest.class);
    }

    public ClientGroupManagerRequest getClientGroupManagerRequest() {
        return this.<ClientGroupManagerRequest>findSection(ClientGroupManagerRequest.class);
    }

    public ProhibitionMenuRequest getProhibitionMenuRequest() {
        return this.<ProhibitionMenuRequest>findSection(ProhibitionMenuRequest.class);
    }

    public OrganizationComplexesStructureRequest getOrganizationComplexesStructureRequest() {
        return this.<OrganizationComplexesStructureRequest>findSection(OrganizationComplexesStructureRequest.class);
    }

    public OrganizationStructureRequest getOrganizationStructureRequest() {
        return this.<OrganizationStructureRequest>findSection(OrganizationStructureRequest.class);
    }

    public CategoriesDiscountsAndRulesRequest getCategoriesAndDiscountsRequest() {
        return this.<CategoriesDiscountsAndRulesRequest>findSection(CategoriesDiscountsAndRulesRequest.class);
    }

    public CardsOperationsRegistry getCardsOperationsRegistry() {
        return this.<CardsOperationsRegistry>findSection(CardsOperationsRegistry.class);
    }

    public ZeroTransactions getZeroTransactions() {
        return this.<ZeroTransactions>findSection(ZeroTransactions.class);
    }

    public ListComplexSchedules getComplexSchedules() {
        return this.<ListComplexSchedules>findSection(ListComplexSchedules.class);
    }

    public AccountsRegistryRequest getAccountsRegistryRequest() {
        return this.<AccountsRegistryRequest>findSection(AccountsRegistryRequest.class);
    }

    public PreorderFeedingStatusRequest getPreorderFeedingStatusRequest() {
        return this.<PreorderFeedingStatusRequest>findSection(PreorderFeedingStatusRequest.class);
    }

    public ReestrTaloonApproval getReestrTaloonApproval() {
        return this.<ReestrTaloonApproval>findSection(ReestrTaloonApproval.class);
    }

    public ReestrTaloonPreorder getReestrTaloonPreorder() {
        return this.<ReestrTaloonPreorder>findSection(ReestrTaloonPreorder.class);
    }

    public PlanOrdersRestrictionsRequest getPlanOrdersRestrictionsRequest() {
        return this.<PlanOrdersRestrictionsRequest>findSection(PlanOrdersRestrictionsRequest.class);
    }

    public InteractiveReport getInteractiveReport() {
        return this.<InteractiveReport>findSection(InteractiveReport.class);
    }

    public SpecialDates getSpecialDates() {
        return this.<SpecialDates>findSection(SpecialDates.class);
    }

    public Migrants getMigrants() {
        return this.<Migrants>findSection(Migrants.class);
    }

    public ClientsPhotos getClientPhotos() {
        return this.<ClientsPhotos>findSection(ClientsPhotos.class);
    }

    public DirectivesRequest getDirectivesRequest() {
        return this.<DirectivesRequest>findSection(DirectivesRequest.class);
    }

    public HelpRequest getHelpRequest() {
        return this.<HelpRequest>findSection(HelpRequest.class);
    }

    public PreOrdersFeedingRequest getPreOrderFeedingRequest() {
        return this.<PreOrdersFeedingRequest>findSection(PreOrdersFeedingRequest.class);
    }

    public CardRequests getCardRequests() {
        return this.<CardRequests>findSection(CardRequests.class);
    }

    public MenusCalendarRequest getMenusCalendarRequest() {
        return this.<MenusCalendarRequest>findSection(MenusCalendarRequest.class);
    }

    public MenusCalendarSupplierRequest getMenusCalendarSupplierRequest() {
        return this.<MenusCalendarSupplierRequest>findSection(MenusCalendarSupplierRequest.class);
    }

    public ClientBalanceHoldRequest getClientBalanceHoldRequest() {
        return this.<ClientBalanceHoldRequest>findSection(ClientBalanceHoldRequest.class);
    }

    public ClientBalanceHoldData getClientBalanceHoldData() {
        return this.<ClientBalanceHoldData>findSection(ClientBalanceHoldData.class);
    }

    public RequestFeeding getRequestFeeding() {
        return this.<RequestFeeding>findSection(RequestFeeding.class);
    }

    public ClientDiscountsDTSZNRequest getClientDiscountDSZNRequest() {
        return this.<ClientDiscountsDTSZNRequest>findSection(ClientDiscountsDTSZNRequest.class);
    }

    public OrgSettingsRequest getOrgSettingsRequest(){
        return this.<OrgSettingsRequest>findSection(OrgSettingsRequest.class);
    }

    public GoodRequestEZDRequest getGoodRequestEZDRequest(){
        return this.<GoodRequestEZDRequest>findSection(GoodRequestEZDRequest.class);
    }

	public EmiasRequest getEmiasRequest(){
        return this.<EmiasRequest>findSection(EmiasRequest.class);
    }

    public MenuSupplier getMenuSupplier() {
        return this.findSection(MenuSupplier.class);
    }

    public RequestsSupplier getRequestsSupplier() {
        return this.findSection(RequestsSupplier.class);
    }

    public HardwareSettingsRequest getHardwareSettingsRequest() {
        return this.<HardwareSettingsRequest>findSection(HardwareSettingsRequest.class);
    }

    public TurnstileSettingsRequest getTurnstileSettingsRequest() {
        return this.<TurnstileSettingsRequest>findSection(TurnstileSettingsRequest.class);
    }

    public <T extends SectionRequest> T findSection(Class classT) {
        for (SectionRequest sectionRequest : sectionRequests) {
            if (sectionRequest.getClass() == classT) {
                return (T) sectionRequest;
            }
        }
        return null;
    }

    public OrgFilesRequest getOrgFilesRequest() {
        return this.<OrgFilesRequest>findSection(OrgFilesRequest.class);
    }

    @Override
    public String toString() {
        return "SyncRequest{" + "protoVersion=" + protoVersion + ", idOfOrg=" + idOfOrg + ", syncTime=" + syncTime
                + ", idOfPacket=" + idOfPacket + ", message='" + message + '\'' + '}';
    }

    public static boolean versionIsAfter(String clientVersion, String expectedVersion) {
        List<String> cVL = Arrays.asList(clientVersion.split("\\."));
        List<String> eVL = Arrays.asList(expectedVersion.split("\\."));
        for (int i = 0; i < eVL.size(); i++) {
            if (Integer.valueOf(cVL.get(i)) > Integer.valueOf(eVL.get(i))) {
                return true;
            } else if (Integer.valueOf(cVL.get(i)) < Integer.valueOf(eVL.get(i))) {
                return false;
            }
        }
        return true;
    }
}