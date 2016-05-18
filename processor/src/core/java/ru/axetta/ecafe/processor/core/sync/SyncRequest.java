/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.CategoriesDiscountsAndRulesBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.CategoriesDiscountsAndRulesRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientGroupManagerBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientGroupManagerRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReport;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportDataBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.Migrants;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.PaymentRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.PaymentRegistryBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperationBuilder;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactions;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactionsBuilder;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.request.*;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequest;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequestBuilder;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.text.StrTokenizer;
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

    public ZeroTransactions getZeroTransactions() {
        return zeroTransactions;
    }

    public static class ClientParamRegistry {

        public static class ClientParamItem {

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
                    //String notifyViaPUSH = getStringValueNullSafe(namedNodeMap, "NotifyViaPUSH");
                    String groupName = getStringValueNullSafe(namedNodeMap, "GroupName");
                    String canConfirmGroupPayment = getStringValueNullSafe(namedNodeMap,"CanConfirmGroupPayment");
                    String guid = getStringValueNullSafe(namedNodeMap, "GUID");
                    Long expenditureLimit = getLongValueNullSafe(namedNodeMap, "ExpenditureLimit");
                    String isUseLastEEModeForPlan = getStringValueNullSafe(namedNodeMap, "IsUseLastEEModeForPlan");
                    //return new ClientParamItem(idOfClient, freePayCount, freePayMaxCount, lastFreePayTime, discountMode,
                    //        categoriesDiscounts, name, surname, secondName, address, phone, mobilePhone, middleGroup,
                    //        fax, email, remarks, notifyViaEmail == null ? null : notifyViaEmail.equals("1"),
                    //        notifyViaSMS == null ? null : notifyViaSMS.equals("1"),
                    //        notifyViaPUSH == null ? null : notifyViaPUSH.equals("1"), groupName,
                    //        canConfirmGroupPayment == null ? null : canConfirmGroupPayment.equals("1"), guid,
                    //        expenditureLimit);
                    return new ClientParamItem(idOfClient, freePayCount, freePayMaxCount, lastFreePayTime, discountMode,
                            categoriesDiscounts, name, surname, secondName, address, phone, mobilePhone, middleGroup,
                            fax, email, remarks, notifyViaEmail == null ? null : notifyViaEmail.equals("1"),
                            notifyViaSMS == null ? null : notifyViaSMS.equals("1"), groupName,
                            canConfirmGroupPayment == null ? null : canConfirmGroupPayment.equals("1"), guid,
                            expenditureLimit, isUseLastEEModeForPlan == null ? null : isUseLastEEModeForPlan.equals("1"));
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
            private final Boolean notifyViaEmail, notifyViaSMS; //, notifyViaPUSH;
            private final Boolean canConfirmGroupPayment;
            private final String guid;
            private final Long expenditureLimit;
            private final Boolean isUseLastEEModeForPlan;

            //public ClientParamItem(long idOfClient, int freePayCount, int freePayMaxCount, Date lastFreePayTime,
            //        int discountMode, String categoriesDiscounts, String name, String surname, String secondName,
            //        String address, String phone, String mobilePhone, String middleGroup, String fax, String email, String remarks,
            //        Boolean notifyViaEmail, Boolean notifyViaSMS, Boolean notifyViaPUSH, String groupName, Boolean canConfirmGroupPayment,
            //        String guid, Long expenditureLimit) {
            public ClientParamItem(long idOfClient, int freePayCount, int freePayMaxCount, Date lastFreePayTime,
                    int discountMode, String categoriesDiscounts, String name, String surname, String secondName,
                    String address, String phone, String mobilePhone, String middleGroup, String fax, String email, String remarks,
                    Boolean notifyViaEmail, Boolean notifyViaSMS, String groupName, Boolean canConfirmGroupPayment,
                    String guid, Long expenditureLimit, Boolean isUseLastEEModeForPlan) {
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
                //this.notifyViaPUSH = notifyViaPUSH;
                this.groupName = groupName;
                this.canConfirmGroupPayment = canConfirmGroupPayment;
                this.guid = guid;
                this.expenditureLimit = expenditureLimit;
                this.isUseLastEEModeForPlan = isUseLastEEModeForPlan;
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

            //public Boolean getNotifyViaPUSH() {
            //    return notifyViaPUSH;
            //}
            //
            public String getGroupName() {
                return groupName;
            }

            public Boolean getCanConfirmGroupPayment() {
                return canConfirmGroupPayment;
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

            @Override
            public String toString() {
                return "ClientParamItem{" + "idOfClient=" + idOfClient + ", name='" + name + '\'' + ", surname='"
                        + surname + '\'' + ", secondName='" + secondName + '\'' + ", address='" + address + '\''
                        + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", middleGroup='" + middleGroup + '\'' + ", fax='" + fax + '\''
                        + ", email='" + email + '\'' + ", remarks='" + remarks + '\'' + ", freePayCount=" + freePayCount
                        + ", freePayMaxCount=" + freePayMaxCount + ", lastFreePayTime=" + lastFreePayTime
                        + ", discountMode=" + discountMode + ", categoriesDiscounts='" + categoriesDiscounts + '\''
                        + ", expenditureLimit=" + expenditureLimit +
                        + '}';
            }
        }

        public static class Builder {

            private final ClientParamItem.Builder itemBuilder;

            public Builder() {
                this.itemBuilder = new ClientParamItem.Builder();
            }

            public ClientParamRegistry build(Node paymentRegistryNode, LoadContext loadContext) throws Exception {
                List<ClientParamItem> items = new LinkedList<ClientParamItem>();
                Node itemNode = paymentRegistryNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CP")) {
                        items.add(itemBuilder.build(itemNode, loadContext));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new ClientParamRegistry(items);
            }

            public ClientParamRegistry build() throws Exception {
                return new ClientParamRegistry();
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

    public static class OrgStructure {

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

        public static class Builder {

            private final Group.Builder groupBuilder;

            public Builder() {
                this.groupBuilder = new Group.Builder();
            }

            public OrgStructure build(Node orgStructureNode) throws Exception {
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

    public static class MenuGroups {

        public String findMenuGroup(long idOfMenuGroup) {
            for (MenuGroup menuGroup : menuGroups) {
                if (menuGroup.idOfMenuGroup == idOfMenuGroup) {
                    return menuGroup.getName();
                }
            }
            return null;
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

        public static class Builder {

            private final MenuGroup.Builder groupBuilder;

            public Builder() {
                this.groupBuilder = new MenuGroup.Builder();
            }

            public MenuGroups build(Node node) throws Exception {
                List<MenuGroup> groups = new LinkedList<MenuGroup>();
                Node itemNode = node.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("MGI")) {
                        groups.add(groupBuilder.build(itemNode));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new MenuGroups(groups);
            }

            public static MenuGroups buildEmpty() {
                return new MenuGroups(new LinkedList<MenuGroup>());
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

    public static class AccIncRegistryRequest {

        public final Date dateTime;

        public AccIncRegistryRequest(Date dateTime) {
            this.dateTime = dateTime;
        }

        public static class Builder {

            public AccIncRegistryRequest build(Node node, LoadContext loadContext) throws Exception {
                NamedNodeMap namedNodeMap = node.getAttributes();
                Date dateTime = loadContext.getTimeFormat().parse(namedNodeMap.getNamedItem("Date").getTextContent());
                return new AccIncRegistryRequest(dateTime);
            }
        }
    }

    public static class ReqMenu {

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
                                    throw new Exception("Attribute MenuItemCount contains value of type different from Integer");
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

                        private static Double getDoubleValue(NamedNodeMap namedNodeMap, String name) throws Exception{
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
                                reqComplexInfoDiscountDetail, currentPrice, goodsGuid, modeVisible);
                    }

                }

                private final int complexId;
                private final String complexMenuName;
                private final int modeFree;
                private final int modeGrant;
                private final int modeOfAdd;
                private final int usedSubscriptionFeeding;
                private final Integer useTrDiscount;
                private final ReqMenuDetail reqMenuDetail;
                private final List<ReqComplexInfoDetail> complexInfoDetails;
                private final ReqComplexInfoDiscountDetail complexInfoDiscountDetail;
                private final Long currentPrice;
                private final String goodsGuid;
                private final Integer modeVisible;

                public ReqComplexInfo(int complexId, String complexMenuName, int modeFree, int modeGrant, int modeOfAdd,
                        int usedSubscriptionFeeding, List<ReqComplexInfoDetail> complexInfoDetails, Integer useTrDiscount, ReqMenuDetail reqMenuDetail,
                        ReqComplexInfoDiscountDetail complexInfoDiscountDetail, Long currentPrice, String goodsGuid, Integer modeVisible) {
                    this.complexId = complexId;
                    this.complexMenuName = complexMenuName;
                    this.modeFree = modeFree;
                    this.modeGrant = modeGrant;
                    this.modeOfAdd = modeOfAdd;
                    this.usedSubscriptionFeeding = usedSubscriptionFeeding;
                    this.complexInfoDetails = complexInfoDetails;
                    this.useTrDiscount = useTrDiscount;
                    this.complexInfoDiscountDetail = complexInfoDiscountDetail;
                    this.reqMenuDetail = reqMenuDetail;
                    this.currentPrice = currentPrice;
                    this.goodsGuid = goodsGuid;
                    this.modeVisible = modeVisible;
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
                    if (modeVisible != null ? !modeVisible.equals(that.modeVisible)
                            : that.modeVisible != null) {
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
                    builder.append(useTrDiscount);
                    builder.append(currentPrice);
                    builder.append(goodsGuid);
                    builder.append(reqMenuDetail);
                    builder.append(complexInfoDiscountDetail);
                    builder.append(modeVisible);
                    if(complexInfoDetails != null){
                        for (ReqComplexInfoDetail obj : complexInfoDetails) {
                            builder.append(obj);
                        }
                    }
                    return builder.toHashCode();
                }
            }

            public static class ReqMenuDetail {

                public static class Builder {

                    public ReqMenuDetail build(Node menuDetailNode, MenuGroups menuGroups) throws Exception {
                        NamedNodeMap namedNodeMap = menuDetailNode.getAttributes();
                        String name = StringUtils.substring(getTextContent(namedNodeMap.getNamedItem("Name")), 0, 90);
                        Node fullNameNode = namedNodeMap.getNamedItem("FullName");
                        if (null != fullNameNode && StringUtils.isNotEmpty(fullNameNode.getTextContent())) {
                            name = StringUtils.substring(fullNameNode.getTextContent(), 0, 90);
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
                            output = StringUtils
                                    .substring(getTextContent(outputNode), 0, 32);
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

                        String gBasket = getTextContent(namedNodeMap.getNamedItem("GBasketEl"));

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
                                flags, priority, protein, fat, carbohydrates, calories, vitB1, vitC, vitA, vitE,
                                minCa, minP, minMg, minFe, vitB2, vitPp, gBasket);
                    }

                    private static String getTextContent(Node node) throws Exception {
                        if (null == node) {
                            return null;
                        }
                        return node.getTextContent();
                    }

                    private static Double getDoubleValue(NamedNodeMap namedNodeMap, String name) throws Exception{
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
                            logger.error("Ошибка при сохранении в базу элемента меню "
                                    + "IdOfMenu = " + getTextContent(namedNodeMap.getNamedItem("IdOfMenu")) + ", "
                                    + "Patch = " + getTextContent(namedNodeMap.getNamedItem("Path")) + ", "
                                    + "FullName = " + getTextContent(namedNodeMap.getNamedItem("FullName"))
                                    + ", не верно задана размерность, параметра " + name + " = " + calString);
                            return null;
                        }



                        if (Double.parseDouble(replacedString) < 0) {
                            logger.error("Ошибка при сохранении в базу элемента меню "
                                    + "IdOfMenu = " + getTextContent(namedNodeMap.getNamedItem("IdOfMenu")) + ", "
                                    + "Patch = " + getTextContent(namedNodeMap.getNamedItem("Path")) + ", "
                                    + "FullName = " + getTextContent(namedNodeMap.getNamedItem("FullName"))
                                    + " задано отрицательное число, в параметре " + name + " = " + calString);
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

                public ReqMenuDetail(Long idOfMenu, String path, String name, String group, String output, Long price,
                        int menuOrigin, int availableNow, Integer flags, Integer priority, Double protein, Double fat,
                        Double carbohydrates, Double calories, Double vitB1, Double vitC, Double vitA, Double vitE,
                        Double minCa, Double minP, Double minMg, Double minFe, Double vitB2, Double vitPp, String gBasket) {
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

                @Override
                public String toString() {
                    return "ReqMenuDetail{" + "idOfMenu=" + idOfMenu + ", path='" + path + '\'' + ", name='" + name
                            + '\'' + ", group='" + group + '\'' + ", output='" + output + '\'' + ", price=" + price
                            + ", protein=" + protein + ", fat=" + fat + ", carbohydrates=" + carbohydrates
                            + ", calories=" + calories + ", vitB1=" + vitB1 + ", vitB2=" + vitB2 + ", vitPp=" + vitPp
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + ", menuOrigin=" + menuOrigin
                            + ", availableNow=" + availableNow + ", flags=" + flags + ", priority=" + priority
                            + ", gBasketEl=" + gBasket + '}';
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
                        String name = StringUtils.substring(getTextContent(namedNodeMap.getNamedItem("Name")), 0, 90);
                        Node fullNameNode = namedNodeMap.getNamedItem("FullName");
                        String fullName = name;
                        if (null != fullNameNode && StringUtils.isNotEmpty(fullNameNode.getTextContent())) {
                            fullName = StringUtils.substring(fullNameNode.getTextContent(), 0, 90);
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
                        Double calories = SyncRequest.getCalories(namedNodeMap, "Calories");
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
                                carbohydrates, calories, vitB1, vitC, vitA, vitE, minCa, minP, minMg, minFe, vitB2, vitPp);
                    }

                    private static Double getMinorComponent(NamedNodeMap namedNodeMap, String name) throws Exception {
                        Node node = namedNodeMap.getNamedItem(name);
                        if (null == node) {
                            return null;
                        }
                        return ((double) Long.parseLong(node.getTextContent())) / 100;
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
                        Double vitC, Double vitA, Double vitE, Double minCa, Double minP, Double minMg, Double minFe, Double vitB2, Double vitPp) {
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

                public Item build(Node itemNode, LoadContext loadContext) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();

                    Date date = loadContext.getDateOnlyFormat().parse(namedNodeMap.getNamedItem("Value").getTextContent());
                    ////// process ML items (menu list)
                    List<ReqMenuDetail> reqMenuDetails = new LinkedList<ReqMenuDetail>();
                    HashMap<Long, ReqMenuDetail> reqMenuDetailMap = new HashMap<Long, ReqMenuDetail>();
                    List<ReqAssortment> reqAssortments = new LinkedList<ReqAssortment>();
                    Node childNode = itemNode.getFirstChild();
                    while (null != childNode) {
                        if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("ML")) {
                            ReqMenuDetail reqMenuDetail = reqMenuDetailBuilder.build(childNode, loadContext.getMenuGroups());
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
                                    || childNode.getNodeName().equals("CML2")
                                    || childNode.getNodeName().equals("DCML") )) {
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
                if(reqMenuDetails != null){
                    for (ReqMenuDetail obj : reqMenuDetails) {
                        builder.append(obj);
                    }
                }
                if(reqAssortments != null){
                    for (ReqAssortment obj : reqAssortments) {
                        builder.append(obj);
                    }
                }
                if(reqComplexInfos != null){
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

        public static class Builder {

            private final Item.Builder itemBuilder;

            public Builder() {
                this.itemBuilder = new Item.Builder();
            }

            public ReqMenu build(Node menuNode, LoadContext loadContext) throws Exception {
                List<Item> items = new LinkedList<Item>();
                Node itemNode = menuNode.getFirstChild();
                String settingsSectionRawXML = null;
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Settings")) {
                        settingsSectionRawXML = ru.axetta.ecafe.processor.core.utils.XMLUtils.nodeToString(itemNode);
                    } else if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Date")) {
                        items.add(itemBuilder.build(itemNode, loadContext));
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

        public Iterator<Item> getItems(){
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

    public static class ClientRegistryRequest {

        public static class Builder {

            public ClientRegistryRequest build(Node clientRegistryRequestNode) throws Exception {
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

    public static class ReqDiary {

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

        public static class Builder {

            private final ReqDiaryClass.Builder reqDiaryClassBuilder;
            private final ReqDiaryTimesheet.Builder reqDiaryTimesheetBuilder;

            public Builder() {
                this.reqDiaryClassBuilder = new ReqDiaryClass.Builder();
                this.reqDiaryTimesheetBuilder = new ReqDiaryTimesheet.Builder();
            }

            public ReqDiary build(Node diaryNode, LoadContext loadContext) throws Exception {
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
        private final PaymentRegistryBuilder paymentRegistryBuilder;
        private final AccIncRegistryRequest.Builder accIncRegistryRequestBuilder;
        private final ClientParamRegistry.Builder clientParamRegistryBuilder;
        private final ClientRegistryRequest.Builder clientRegistryRequestBuilder;
        private final OrgStructure.Builder orgStructureBuilder;
        private final ReqMenu.Builder reqMenuBuilder;
        private final ReqDiary.Builder reqDiaryBuilder;
        private final MenuGroups.Builder menuGroupsBuilder;
        //private final EnterEvents.Builder enterEventsBuilder;
        private final TempCardsOperationBuilder tempCardsOperationBuilder;
        private Manager manager;
        private final ClientRequestBuilder clientRequestBuilder;
        private final EnterEventsBuilder enterEventsBuilder;
        private final AccRegistryUpdateRequestBuilder accRegistryUpdateRequestBuilder;
        private final ClientGuardianBuilder clientGuardianBuilder;
        private final ProhibitionMenuRequestBuilder prohibitionMenuRequestBuilder;
        private final OrganizationStructureRequestBuilder organizationStructureRequestBuilder;
        private final CategoriesDiscountsAndRulesBuilder categoriesAndDiscountsBuilder;
        private final AccountsRegistryRequestBuilder accountsRegistryRequestBuilder;
        private final ReestrTaloonApprovalBuilder reestrTaloonApprovalBuilder;
        private final InteractiveReportDataBuilder interactiveReportDataBuilder;
        private final ZeroTransactionsBuilder zeroTransactionsBuilder;
        private final SpecialDatesBuilder specialDatesBuilder;
        private final MigrantsBuilder migrantsBuilder;
        private final ClientGroupManagerBuilder clientGroupManagerBuilder;

        public Builder() {
            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            this.dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.dateOnlyFormat.setTimeZone(utcTimeZone);

            //TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
            TimeZone localTimeZone = RuntimeContext.getInstance().getDefaultLocalTimeZone(null);
            this.timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            this.timeFormat.setTimeZone(localTimeZone);

            this.paymentRegistryBuilder = new PaymentRegistryBuilder();
            this.accIncRegistryRequestBuilder = new AccIncRegistryRequest.Builder();
            this.clientParamRegistryBuilder = new ClientParamRegistry.Builder();
            this.clientRegistryRequestBuilder = new ClientRegistryRequest.Builder();
            this.orgStructureBuilder = new OrgStructure.Builder();
            this.reqMenuBuilder = new ReqMenu.Builder();
            this.reqDiaryBuilder = new ReqDiary.Builder();
            this.menuGroupsBuilder = new MenuGroups.Builder();
            //this.enterEventsBuilder = new EnterEvents.Builder();
            this.enterEventsBuilder = new EnterEventsBuilder();
            this.tempCardsOperationBuilder = new TempCardsOperationBuilder();
            this.clientRequestBuilder = new ClientRequestBuilder();
            this.accRegistryUpdateRequestBuilder = new AccRegistryUpdateRequestBuilder();
            this.clientGuardianBuilder = new ClientGuardianBuilder();
            this.prohibitionMenuRequestBuilder = new ProhibitionMenuRequestBuilder();
            this.organizationStructureRequestBuilder = new OrganizationStructureRequestBuilder();
            this.categoriesAndDiscountsBuilder = new CategoriesDiscountsAndRulesBuilder();
            this.accountsRegistryRequestBuilder = new AccountsRegistryRequestBuilder();
            this.reestrTaloonApprovalBuilder = new ReestrTaloonApprovalBuilder();
            this.interactiveReportDataBuilder = new InteractiveReportDataBuilder();
            this.zeroTransactionsBuilder = new ZeroTransactionsBuilder();
            this.specialDatesBuilder = new SpecialDatesBuilder();
            this.migrantsBuilder = new MigrantsBuilder();
            this.clientGroupManagerBuilder = new ClientGroupManagerBuilder();
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

        public static String getClientVersion(NamedNodeMap namedNodeMap) throws Exception {
            if(namedNodeMap.getNamedItem("ClientVersion")==null) return null;
            return namedNodeMap.getNamedItem("ClientVersion").getTextContent();
        }

        public static SyncType getSyncType(NamedNodeMap namedNodeMap) throws Exception {
            return SyncType.parse(getStringValueNullSafe(namedNodeMap, "Type"));
        }

        public SyncRequest build(Node envelopeNode, NamedNodeMap namedNodeMap, Org org, String idOfSync, String remoteAddr)
                throws Exception {
            long version = getLongValue(namedNodeMap, "Version");
            if (3L != version && 4L != version && 5L != version && 6L != version) {
                throw new Exception(String.format("Unsupported protoVersion: %d", version));
            }
            String sSyncType = getStringValueNullSafe(namedNodeMap, "Type");

            SyncType syncType = SyncType.parse(sSyncType);

            String clientVersion = getClientVersion(namedNodeMap);

            Date syncTime = timeFormat.parse(idOfSync);
            Long idOfPacket = getLongValueNullSafe(namedNodeMap, "IdOfPacket");

            Node menuNode = findFirstChildElement(envelopeNode, "Menu");

            MenuGroups menuGroups = null;
            Node menuGroupsNode = findFirstChildElement(envelopeNode, "MenuGroups");
            if (menuGroupsNode == null) {
                // может быть как на верхнем уровне (старый протокол), так и в Menu / Settings
                if (menuNode != null) {
                    Node settingsNode = findFirstChildElement(menuNode, "Settings");
                    if (settingsNode != null) {
                        menuGroupsNode = findFirstChildElement(settingsNode, "MenuGroups");
                    }
                }
            }
            if (menuGroupsNode != null) {
                menuGroups = menuGroupsBuilder.build(menuGroupsNode);
            } else {
                menuGroups = MenuGroups.Builder.buildEmpty();
            }

            LoadContext loadContext = new LoadContext(menuGroups, version, timeFormat, dateOnlyFormat);

            Node paymentRegistryNode = findFirstChildElement(envelopeNode, "PaymentRegistry");
            PaymentRegistry paymentRegistry = null;
            if (paymentRegistryNode != null) {
                paymentRegistry = paymentRegistryBuilder.build(paymentRegistryNode, loadContext);
            }

            Node accountOperationsRegistryNode = findFirstChildElement(envelopeNode, AccountOperationsRegistry.SYNC_NAME);
            AccountOperationsRegistry accountOperationsRegistry = null;
            if (accountOperationsRegistryNode != null) {
                accountOperationsRegistry = AccountOperationsRegistry.build(accountOperationsRegistryNode, loadContext);
            }

            Node accIncRegistryRequestNode = findFirstChildElement(envelopeNode, "AccIncRegistryRequest");
            AccIncRegistryRequest accIncRegistryRequest = null;
            if (accIncRegistryRequestNode != null) {
                accIncRegistryRequest = accIncRegistryRequestBuilder.build(accIncRegistryRequestNode, loadContext);
            }

            Node clientParamRegistryNode = findFirstChildElement(envelopeNode, "ClientParams");
            ClientParamRegistry clientParamRegistry;
            if (null == clientParamRegistryNode) {
                clientParamRegistry = clientParamRegistryBuilder.build();
            } else {
                clientParamRegistry = clientParamRegistryBuilder.build(clientParamRegistryNode, loadContext);
            }

            Node clientRegistryRequestNode = findFirstChildElement(envelopeNode, "ClientRegistryRequest");
            ClientRegistryRequest clientRegistryRequest = null;
            if (clientRegistryRequestNode != null) {
                clientRegistryRequest = clientRegistryRequestBuilder.build(clientRegistryRequestNode);
            }

            Node accRegistryUpdateRequestParseRequestNode = findFirstChildElement(envelopeNode, "AccRegistryUpdateRequest");
            AccRegistryUpdateRequest accRegistryUpdateRequest = null;
            if (accRegistryUpdateRequestParseRequestNode != null) {
               accRegistryUpdateRequest=accRegistryUpdateRequestBuilder.build(accRegistryUpdateRequestParseRequestNode);
            }

            Node orgStructureNode = findFirstChildElement(envelopeNode, "OrgStructure");
            OrgStructure orgStructure = null;
            if (orgStructureNode != null) {
                orgStructure = orgStructureBuilder.build(orgStructureNode);
            }


            ReqMenu reqMenu = null;
            if (menuNode != null) {
                reqMenu = reqMenuBuilder.build(menuNode, loadContext);
            }

            Node messageNode = findFirstChildElement(envelopeNode, "Message");
            Node reqDiaryNode = findFirstChildElement(envelopeNode, "Diary");

            ReqDiary reqDiary = null;
            if (reqDiaryNode != null) {
                reqDiary = reqDiaryBuilder.build(reqDiaryNode, loadContext);
            }

            String message = null;
            if (null != messageNode) {
                message = findFirstChildTextNode(messageNode).getTextContent();
            }

            enterEventsBuilder.createMainNode(envelopeNode);
            EnterEvents enterEvents = enterEventsBuilder.build(loadContext);

            TempCardsOperations tempCardsOperations = null;
            Node tempCardsOperationsNode = findFirstChildElement(envelopeNode, "TempCardsOperations");
            if (tempCardsOperationsNode != null) {
                tempCardsOperations = tempCardsOperationBuilder.build(tempCardsOperationsNode, org.getIdOfOrg());
            }

            clientRequestBuilder.createMainNode(envelopeNode);
            ClientRequests clientRequests = clientRequestBuilder.build();

            clientGuardianBuilder.createMainNode(envelopeNode);
            ClientGuardianRequest clientGuardianRequest = clientGuardianBuilder.build();

            prohibitionMenuRequestBuilder.createMainNode(envelopeNode);
            ProhibitionMenuRequest prohibitionMenuRequest = prohibitionMenuRequestBuilder.build();

            organizationStructureRequestBuilder.createMainNode(envelopeNode);
            OrganizationStructureRequest organizationStructureRequest = organizationStructureRequestBuilder.build();

            categoriesAndDiscountsBuilder.createMainNode(envelopeNode);
            CategoriesDiscountsAndRulesRequest categoriesAndDiscountsRequest = categoriesAndDiscountsBuilder.build();

            clientGroupManagerBuilder.createMainNode(envelopeNode);
            ClientGroupManagerRequest clientGroupManagerRequest = clientGroupManagerBuilder.build();

            accountsRegistryRequestBuilder.createMainNode(envelopeNode);
            AccountsRegistryRequest accountsRegistryRequest = accountsRegistryRequestBuilder.build();

            Node reestrTaloonApprovalNode = findFirstChildElement(envelopeNode, "ReestrTaloonApproval");
            ReestrTaloonApproval reestrTaloonApprovalRequest = null;
            if (reestrTaloonApprovalNode != null) {
                reestrTaloonApprovalRequest = reestrTaloonApprovalBuilder.build(reestrTaloonApprovalNode, org.getIdOfOrg());
            }

            Node zeroTransactionsNode = findFirstChildElement(envelopeNode, "ZeroTransactions");
            ZeroTransactions zeroTransactionsRequest = null;
            if (zeroTransactionsNode != null) {
                zeroTransactionsRequest = zeroTransactionsBuilder.build(zeroTransactionsNode, org.getIdOfOrg());
            }

            Node specialDatesNode = findFirstChildElement(envelopeNode, "SpecialDates");
            SpecialDates specialDatesRequest = null;
            if (specialDatesNode != null) {
                specialDatesRequest = specialDatesBuilder.build(specialDatesNode, org.getIdOfOrg());
            }

            Node migrantsNode = findFirstChildElement(envelopeNode, "Migrants");
            Migrants migrantsRequest = null;
            if (migrantsNode != null) {
                migrantsRequest = migrantsBuilder.build(migrantsNode, org.getIdOfOrg());
            }

            Node interactiveReportNode = findFirstChildElement(envelopeNode, "InteractiveReportData");
            InteractiveReport interactiveReportRequest = null;
            if(interactiveReportNode != null) {
                interactiveReportRequest = interactiveReportDataBuilder.buildInteractiveReport(interactiveReportNode);
            }

            /*  Модуль распределенной синхронизации объектов */
            Node roNode = findFirstChildElement(envelopeNode, "RO");
            if (roNode != null){
                String[] doGroupNames;
                Boolean enableSubscriptionFeeding = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING);
                List<String> groups = new ArrayList<String>();
                groups.add("SettingsGroup");
                /* В короткой синхронизации не участвует библиотека */
                if(!syncType.equals(SyncType.TYPE_COMMODITY_ACCOUNTING)) groups.add("LibraryGroup");

                /* Если включена опция товарного учета у организации */
                if(org.getCommodityAccounting()) {
                    groups.addAll(Arrays.asList("ProductsGroup", "DocumentGroup"));
                    /* В обработка абонентного питания*/
                    if(enableSubscriptionFeeding) groups.add("SubscriptionGroup");
                }
                manager = new Manager(org.getIdOfOrg(), groups);
                manager.buildRO(roNode);
            }

            CardsOperationsRegistry cardsOperationsRegistry = CardsOperationsRegistry.find(envelopeNode,loadContext);

            return new SyncRequest(remoteAddr, version, syncType , clientVersion, org, syncTime, idOfPacket, paymentRegistry, accountOperationsRegistry, accIncRegistryRequest,
                    clientParamRegistry, clientRegistryRequest, orgStructure, menuGroups, reqMenu, reqDiary, message,
                    enterEvents, tempCardsOperations, clientRequests, manager, accRegistryUpdateRequest,
                    clientGuardianRequest, prohibitionMenuRequest,cardsOperationsRegistry, accountsRegistryRequest, organizationStructureRequest, reestrTaloonApprovalRequest,
                    interactiveReportRequest, zeroTransactionsRequest, specialDatesRequest, migrantsRequest, categoriesAndDiscountsRequest,
                    clientGroupManagerRequest);
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
    private final MenuGroups menuGroups;
    private final PaymentRegistry paymentRegistry;
    private final AccountOperationsRegistry accountOperationsRegistry;
    private final ClientParamRegistry clientParamRegistry;
    private final ClientRegistryRequest clientRegistryRequest;
    private final AccIncRegistryRequest accIncRegistryRequest;
    private final OrgStructure orgStructure;
    private final ReqMenu reqMenu;
    private final ReqDiary reqDiary;
    private final String message;
    private final String clientVersion;
    private final EnterEvents enterEvents;
    private final TempCardsOperations tempCardsOperations;
    private final ClientRequests clientRequests;
    private final Manager manager;
    private final AccRegistryUpdateRequest accRegistryUpdateRequest;
    private final ClientGuardianRequest clientGuardianRequest;
    private final ProhibitionMenuRequest prohibitionMenuRequest;
    private final OrganizationStructureRequest organizationStructureRequest;
    private final CategoriesDiscountsAndRulesRequest categoriesAndDiscountsRequest;
    private final CardsOperationsRegistry cardsOperationsRegistry;
    private final AccountsRegistryRequest accountsRegistryRequest;
    private final ReestrTaloonApproval reestrTaloonApprovalRequest;
    private final InteractiveReport interactiveReport;
    private final ZeroTransactions zeroTransactions;
    private final SpecialDates specialDates;
    private final Migrants migrants;
    ClientGroupManagerRequest clientGroupManagerRequest;

    public SyncRequest(String remoteAddr, long protoVersion, SyncType syncType, String clientVersion, Org org, Date syncTime, Long idOfPacket, PaymentRegistry paymentRegistry,
            AccountOperationsRegistry accountOperationsRegistry, AccIncRegistryRequest accIncRegistryRequest,
            ClientParamRegistry clientParamRegistry, ClientRegistryRequest clientRegistryRequest, OrgStructure orgStructure, MenuGroups menuGroups, ReqMenu reqMenu, ReqDiary reqDiary, String message,
            EnterEvents enterEvents, TempCardsOperations tempCardsOperations, ClientRequests clientRequests, Manager manager,
            AccRegistryUpdateRequest accRegistryUpdateRequest, ClientGuardianRequest clientGuardianRequest,
            ProhibitionMenuRequest prohibitionMenuRequest, CardsOperationsRegistry cardsOperationsRegistry,
            AccountsRegistryRequest accountsRegistryRequest, OrganizationStructureRequest organizationStructureRequest,
            ReestrTaloonApproval reestrTaloonApprovalRequest, InteractiveReport interactiveReport, ZeroTransactions zeroTransactions, SpecialDates specialDates,
            Migrants migrants, CategoriesDiscountsAndRulesRequest categoriesAndDiscountsRequest, ClientGroupManagerRequest clientGroupManagerRequest) {
        this.remoteAddr = remoteAddr;
        this.protoVersion = protoVersion;
        this.syncType = syncType;
        this.clientVersion = clientVersion;
        this.tempCardsOperations = tempCardsOperations;
        this.clientRequests = clientRequests;
        this.manager = manager;
        this.accRegistryUpdateRequest = accRegistryUpdateRequest;
        this.clientGuardianRequest = clientGuardianRequest;
        this.prohibitionMenuRequest = prohibitionMenuRequest;
        this.organizationStructureRequest = organizationStructureRequest;
        this.categoriesAndDiscountsRequest = categoriesAndDiscountsRequest;
        this.idOfOrg = org.getIdOfOrg();
        this.org = org;
        this.syncTime = syncTime;
        this.idOfPacket = idOfPacket;
        this.paymentRegistry = paymentRegistry;
        this.accountOperationsRegistry = accountOperationsRegistry;
        this.accIncRegistryRequest = accIncRegistryRequest;
        this.clientParamRegistry = clientParamRegistry;
        this.clientRegistryRequest = clientRegistryRequest;
        this.orgStructure = orgStructure;
        this.menuGroups = menuGroups;
        this.reqMenu = reqMenu;
        this.reqDiary = reqDiary;
        this.message = message;
        this.enterEvents = enterEvents;
        this.cardsOperationsRegistry = cardsOperationsRegistry;
        this.accountsRegistryRequest = accountsRegistryRequest;
        this.reestrTaloonApprovalRequest = reestrTaloonApprovalRequest;
        this.interactiveReport = interactiveReport;
        this.zeroTransactions = zeroTransactions;
        this.specialDates = specialDates;
        this.migrants = migrants;
        this.clientGroupManagerRequest = clientGroupManagerRequest;
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

    public PaymentRegistry getPaymentRegistry() {
        return paymentRegistry;
    }

    public AccountOperationsRegistry getAccountOperationsRegistry() {
        return accountOperationsRegistry;
    }

    public AccIncRegistryRequest getAccIncRegistryRequest() {
        return accIncRegistryRequest;
    }

    public ClientParamRegistry getClientParamRegistry() {
        return clientParamRegistry;
    }

    public ClientRegistryRequest getClientRegistryRequest() {
        return clientRegistryRequest;
    }

    public OrgStructure getOrgStructure() {
        return orgStructure;
    }

    public ReqMenu getReqMenu() {
        return reqMenu;
    }

    public ReqDiary getReqDiary() {
        return reqDiary;
    }

    public String getMessage() {
        return message;
    }

    public EnterEvents getEnterEvents() {
        return enterEvents;
    }

    public Manager getManager() {
        return manager;
    }

    public TempCardsOperations getTempCardsOperations() {
        return tempCardsOperations;
    }

    public ClientRequests getClientRequests() {
        return clientRequests;
    }

    public AccRegistryUpdateRequest getAccRegistryUpdateRequest() {
        return accRegistryUpdateRequest;
    }

    public ClientGuardianRequest getClientGuardianRequest() {
        return clientGuardianRequest;
    }

    public ClientGroupManagerRequest getClientGroupManagerRequest() {
        return clientGroupManagerRequest;
    }

    public ProhibitionMenuRequest getProhibitionMenuRequest() {
        return prohibitionMenuRequest;
    }

    public OrganizationStructureRequest getOrganizationStructureRequest() {
        return organizationStructureRequest;
    }

    public CategoriesDiscountsAndRulesRequest getCategoriesAndDiscountsRequest() {
        return categoriesAndDiscountsRequest;
    }

    public CardsOperationsRegistry getCardsOperationsRegistry() {
        return cardsOperationsRegistry;
    }

    public AccountsRegistryRequest getAccountsRegistryRequest() {
        return accountsRegistryRequest;
    }

    public ReestrTaloonApproval getReestrTaloonApproval() {
        return reestrTaloonApprovalRequest;
    }

    public InteractiveReport getInteractiveReport() {
        return interactiveReport;
    }

    public SpecialDates getSpecialDates() {
        return specialDates;
    }

    public Migrants getMigrants() {
        return migrants;
    }

    @Override
    public String toString() {
        return "SyncRequest{" + "protoVersion=" + protoVersion + ", idOfOrg=" + idOfOrg + ", syncTime=" + syncTime
                + ", idOfPacket=" + idOfPacket + ", paymentRegistry=" + paymentRegistry
                + ", accountOperationsRegistry=" + accountOperationsRegistry + ", clientParamRegistry="
                + clientParamRegistry + ", clientRegistryRequest=" + clientRegistryRequest + ", orgStructure="
                + orgStructure + ", reqMenu=" + reqMenu + ", reqDiary=" + reqDiary + ", message='" + message + '\''
                + ", enterEvents=" + enterEvents + '}';
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



    public static boolean versionIsAfter(String clientVersion, String expectedVersion){
        List<String> cVL =  Arrays.asList(clientVersion.split("\\."));
        List<String> eVL =  Arrays.asList(expectedVersion.split("\\."));
        for (int i = 0; i< eVL.size(); i++){
            if(Integer.valueOf(cVL.get(i)) > Integer.valueOf(eVL.get(i))){
                return true;
            }else if(Integer.valueOf(cVL.get(i)) < Integer.valueOf(eVL.get(i))){
                return false;
            }
        }
        return true;
    }
}