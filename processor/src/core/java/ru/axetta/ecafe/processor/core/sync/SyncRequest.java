/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:52:14
 * To change this template use File | Settings | File Templates.
 */
public class SyncRequest {

    private static int getIntValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        return Integer.parseInt(namedNodeMap.getNamedItem(name).getTextContent());
    }

    private static long getLongValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node n = namedNodeMap.getNamedItem(name);
        return Long.parseLong(n.getTextContent());
    }

    private static Long getLongValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            node = namedNodeMap.getNamedItem(name.toUpperCase());
            if (node == null) {
                return null;
            }
        }
        return Long.parseLong(node.getTextContent());
    }

    private static String getStringValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            return null;
        }
        return node.getTextContent();
    }

    public int getType() {
        return type;
    }

    public static class PaymentRegistry {

        public static class Payment {

            public static class Purchase {


                public static class Builder {

                    public Purchase build(Node purchaseNode, MenuGroups menuGroups) throws Exception {
                        NamedNodeMap namedNodeMap = purchaseNode.getAttributes();
                        long discount = getLongValue(namedNodeMap, "Discount");
                        Long socDiscount = null;
                        if (namedNodeMap.getNamedItem("SocDiscount") != null) {
                            socDiscount = getLongValue(namedNodeMap, "SocDiscount");
                        }
                        long idOfOrderDetail = getLongValue(namedNodeMap, "IdOfOrderDetails");
                        String name = StringUtils.substring(namedNodeMap.getNamedItem("Name").getTextContent(), 0, 90);
                        long qty = getLongValue(namedNodeMap, "Qty");
                        long rPrice = getLongValue(namedNodeMap, "rPrice");
                        String rootMenu = getStringValueNullSafe(namedNodeMap, "RootMenu");
                        String menuOutput = getStringValueNullSafe(namedNodeMap, "O");
                        if (menuOutput == null) {
                            menuOutput = "";
                        }
                        int type = 0; // свободный выбор
                        String typeStr = getStringValueNullSafe(namedNodeMap, "T");
                        if (typeStr != null) {
                            type = Integer.parseInt(typeStr);
                        }
                        int menuOrigin = 0; // собственное
                        String menuOriginStr = getStringValueNullSafe(namedNodeMap, "MenuOrigin");
                        if (menuOriginStr != null) {
                            menuOrigin = Integer.parseInt(menuOriginStr);
                        }
                        String menuGroup = null;
                        String refIdOfMenuGroupStr = getStringValueNullSafe(namedNodeMap, "refIdOfMG");
                        if (refIdOfMenuGroupStr != null) {
                            long refIdOfMenuGroup = Long.parseLong(refIdOfMenuGroupStr);
                            menuGroup = menuGroups.findMenuGroup(refIdOfMenuGroup);
                        }
                        if (menuGroup == null) {
                            menuGroup = MenuDetail.DEFAULT_GROUP_NAME;
                        }

                        return new Purchase(discount, socDiscount, idOfOrderDetail, name, qty, rPrice, rootMenu,
                                menuOutput, type, menuGroup, menuOrigin);
                    }

                }

                private final long discount;
                private final long socDiscount;
                private final long idOfOrderDetail;
                private final String name;
                private final long qty;
                private final long rPrice;
                private final String rootMenu;
                private final String menuOutput;
                private final int type;
                private final String menuGroup;
                private final int menuOrigin;

                public Purchase(long discount, long socDiscount, long idOfOrderDetail, String name, long qty,
                        long rPrice, String rootMenu, String menuOutput, int type, String menuGroup, int menuOrigin) {
                    this.discount = discount;
                    this.socDiscount = socDiscount;
                    this.idOfOrderDetail = idOfOrderDetail;
                    this.name = name;
                    this.qty = qty;
                    this.rPrice = rPrice;
                    this.rootMenu = rootMenu;
                    this.menuOutput = menuOutput;
                    this.type = type;
                    this.menuGroup = menuGroup;
                    this.menuOrigin = menuOrigin;
                }

                public long getDiscount() {
                    return discount;
                }

                public long getSocDiscount() {
                    return socDiscount;
                }

                public long getIdOfOrderDetail() {
                    return idOfOrderDetail;
                }

                public String getName() {
                    return name;
                }

                public long getQty() {
                    return qty;
                }

                public long getRPrice() {
                    return rPrice;
                }

                public String getRootMenu() {
                    return rootMenu;
                }

                public String getMenuGroup() {
                    return menuGroup;
                }

                public String getMenuOutput() {
                    return menuOutput;
                }

                public int getType() {
                    return type;
                }

                public int getMenuOrigin() {
                    return menuOrigin;
                }

                @Override
                public String toString() {
                    return "Purchase{" + "discount=" + discount + "socDiscount=" + socDiscount + ", idOfOrderDetail="
                            + idOfOrderDetail + ", name='" + name + '\'' + ", qty=" + qty + ", rPrice=" + rPrice
                            + ", rootMenu='" + rootMenu + '\'' + ", menuOutput='" + menuOutput + '\'' + ", type=" + type
                            + ", menuGroup='" + menuGroup + '\'' + ", menuOrigin=" + menuOrigin + '}';
                }
            }

            public static class Builder {

                private final Purchase.Builder purchaseBuilder;

                public Builder() {
                    this.purchaseBuilder = new Purchase.Builder();
                }

                public Payment build(Node paymentNode, LoadContext loadContext) throws Exception {
                    NamedNodeMap namedNodeMap = paymentNode.getAttributes();
                    Long cardNo = getLongValueNullSafe(namedNodeMap, "CardNo");
                    Date date = loadContext.timeFormat.parse(namedNodeMap.getNamedItem("Date").getTextContent());
                    Long socDiscount = 0L;
                    Long trdDiscount = 0L;
                    long rSum = getLongValue(namedNodeMap, "rSum");
                    if (namedNodeMap.getNamedItem("Discount") != null) {
                        long discount = getLongValue(namedNodeMap, "Discount");
                        if (discount == rSum) {
                            socDiscount = discount;
                        } else {
                            trdDiscount = discount;
                        }
                    } else {
                        socDiscount = getLongValueNullSafe(namedNodeMap, "SocDiscount");
                        trdDiscount = getLongValueNullSafe(namedNodeMap, "TrdDiscount");
                        if (socDiscount == null) {
                            socDiscount = 0L;
                        }
                        if (trdDiscount == null) {
                            trdDiscount = 0L;
                        }
                    }
                    long grant = getLongValue(namedNodeMap, "Grant");
                    Long idOfClient = getLongValueNullSafe(namedNodeMap, "IdOfClient");
                    long idOfOrder = getLongValue(namedNodeMap, "IdOfOrder");
                    long idOfCashier = getLongValue(namedNodeMap, "IdOfCashier");
                    long sumByCard = getLongValue(namedNodeMap, "SumByCard");
                    long sumByCash = getLongValue(namedNodeMap, "SumByCash");
                    Long idOfPOS = null;
                    if (namedNodeMap.getNamedItem("IdOfPOS") != null) {
                        idOfPOS = getLongValue(namedNodeMap, "IdOfPOS");
                    }
                    List<Purchase> purchases = new LinkedList<Purchase>();
                    Node purchaseNode = paymentNode.getFirstChild();
                    while (null != purchaseNode) {
                        if (Node.ELEMENT_NODE == purchaseNode.getNodeType() && purchaseNode.getNodeName()
                                .equals("PC")) {
                            purchases.add(purchaseBuilder.build(purchaseNode, loadContext.menuGroups));
                        }
                        purchaseNode = purchaseNode.getNextSibling();
                    }
                    return new Payment(cardNo, date, socDiscount, trdDiscount, grant, idOfClient, idOfOrder,
                            idOfCashier, sumByCard, sumByCash, rSum, idOfPOS, purchases);
                }

            }

            private final Long cardNo;
            private final Date time;
            private final Long socDiscount;
            private final Long trdDiscount;
            private final long grant;
            private final Long idOfClient;
            private final long idOfOrder;
            private final long idOfCashier;
            private final long sumByCard;
            private final long sumByCash;
            private final long rSum;
            private final Long idOfPOS;
            private final List<Purchase> purchases;

            public Payment(Long cardNo, Date time, long socDiscount, long trdDiscount, long grant, Long idOfClient,
                    long idOfOrder, long idOfCashier, long sumByCard, long sumByCash, long rSum, Long idOfPOS,
                    List<Purchase> purchases) {
                this.cardNo = cardNo;
                this.time = time;
                this.socDiscount = socDiscount;
                this.trdDiscount = trdDiscount;
                this.grant = grant;
                this.idOfClient = idOfClient;
                this.idOfOrder = idOfOrder;
                this.idOfCashier = idOfCashier;
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.rSum = rSum;
                this.idOfPOS = idOfPOS;
                this.purchases = purchases;
            }

            public Long getCardNo() {
                return cardNo;
            }

            public Date getTime() {
                return time;
            }

            public Long getSocDiscount() {
                return socDiscount;
            }

            public Long getTrdDiscount() {
                return trdDiscount;
            }

            public long getGrant() {
                return grant;
            }

            public Long getIdOfClient() {
                return idOfClient;
            }

            public long getIdOfOrder() {
                return idOfOrder;
            }

            public long getIdOfCashier() {
                return idOfCashier;
            }

            public long getSumByCard() {
                return sumByCard;
            }

            public long getSumByCash() {
                return sumByCash;
            }

            public long getRSum() {
                return rSum;
            }

            public Long getIdOfPOS() {
                return idOfPOS;
            }

            public Enumeration<Purchase> getPurchases() {
                return Collections.enumeration(purchases);
            }

            @Override
            public String toString() {
                return "Payment{" + "cardNo=" + cardNo + ", date=" + time + ", socDiscount=" + socDiscount
                        + ", trdDiscount=" + trdDiscount + ", idOfClient=" + idOfClient + ", idOfOrder=" + idOfOrder
                        + ", idOfCashier=" + idOfCashier + ", sumByCard=" + sumByCard + ", sumByCash=" + sumByCash
                        + ", rSum=" + rSum + ", idOfPOS=" + idOfPOS + ", purchases=" + purchases + '}';
            }
        }

        public static class Builder {

            private final Payment.Builder paymentBuilder;

            public Builder() {
                this.paymentBuilder = new Payment.Builder();
            }

            public PaymentRegistry build(Node paymentRegistryNode, LoadContext loadContext) throws Exception {
                List<Payment> payments = new LinkedList<Payment>();
                Node itemNode = paymentRegistryNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("PT")) {
                        payments.add(paymentBuilder.build(itemNode, loadContext));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new PaymentRegistry(payments);
            }
        }

        private final List<Payment> payments;

        public PaymentRegistry(List<Payment> payments) {
            this.payments = payments;
        }

        public Enumeration<Payment> getPayments() {
            return Collections.enumeration(payments);
        }

        @Override
        public String toString() {
            return "PaymentRegistry{" + "payments=" + payments + '}';
        }
    }

    public static class ClientParamRegistry {

        public static class ClientParamItem {

            public static class Builder {

                public Builder() {
                }

                public ClientParamItem build(Node itemNode, LoadContext loadContext) throws Exception {
                    NamedNodeMap namedNodeMap = itemNode.getAttributes();
                    long idOfClient = getLongValue(namedNodeMap, "IdOfClient");
                    String[] freePayCountTokens = namedNodeMap.getNamedItem("FPCount").getTextContent().split(":");
                    int freePayCount = Integer.parseInt(freePayCountTokens[0]);
                    int freePayMaxCount = Integer.parseInt(freePayCountTokens[1]);
                    Date lastFreePayTime = null;
                    if (0 != freePayCount) {
                        lastFreePayTime = loadContext.timeFormat
                                .parse(namedNodeMap.getNamedItem("FPLastTime").getTextContent());
                    }
                    int discountMode = getIntValue(namedNodeMap, "DiscountMode");
                    /* pridet string with "1, 9, 94, .." key of Category Discount*/
                    String categoriesDiscounts = getStringValueNullSafe(namedNodeMap, "CategoriesDiscounts");
                    ///
                    String name = getStringValueNullSafe(namedNodeMap, "Name");
                    String surname = getStringValueNullSafe(namedNodeMap, "Surname");
                    String secondName = getStringValueNullSafe(namedNodeMap, "Secondname");
                    String address = getStringValueNullSafe(namedNodeMap, "Address");
                    String phone = getStringValueNullSafe(namedNodeMap, "Phone");
                    String mobilePhone = getStringValueNullSafe(namedNodeMap, "Mobile");
                    String email = getStringValueNullSafe(namedNodeMap, "Email");
                    String fax = getStringValueNullSafe(namedNodeMap, "Fax");
                    String remarks = getStringValueNullSafe(namedNodeMap, "Remarks");
                    return new ClientParamItem(idOfClient, freePayCount, freePayMaxCount, lastFreePayTime, discountMode,
                            categoriesDiscounts, name, surname, secondName, address, phone, mobilePhone, fax, email,
                            remarks);
                }

            }

            private final long idOfClient;
            private final String name, surname, secondName, address, phone, mobilePhone, fax, email, remarks;
            private final int freePayCount;
            private final int freePayMaxCount;
            private final Date lastFreePayTime;
            private final int discountMode;
            private final String categoriesDiscounts;

            public ClientParamItem(long idOfClient, int freePayCount, int freePayMaxCount, Date lastFreePayTime,
                    int discountMode, String categoriesDiscounts, String name, String surname, String secondName,
                    String address, String phone, String mobilePhone, String fax, String email, String remarks) {
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
                this.fax = fax;
                this.email = email;
                this.remarks = remarks;
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

            public String getFax() {
                return fax;
            }

            public String getEmail() {
                return email;
            }

            public String getRemarks() {
                return remarks;
            }

            @Override
            public String toString() {
                return "ClientParamItem{" + "idOfClient=" + idOfClient + ", name='" + name + '\'' + ", surname='"
                        + surname + '\'' + ", secondName='" + secondName + '\'' + ", address='" + address + '\''
                        + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", fax='" + fax + '\''
                        + ", email='" + email + '\'' + ", remarks='" + remarks + '\'' + ", freePayCount=" + freePayCount
                        + ", freePayMaxCount=" + freePayMaxCount + ", lastFreePayTime=" + lastFreePayTime
                        + ", discountMode=" + discountMode + ", categoriesDiscounts='" + categoriesDiscounts + '\''
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

        public Enumeration<ClientParamItem> getPayments() {
            return Collections.enumeration(items);
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

            public Enumeration<Long> getClients() {
                return Collections.enumeration(clients);
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

        public Enumeration<Group> getGroups() {
            return Collections.enumeration(groups);
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
                Date dateTime = loadContext.timeFormat.parse(namedNodeMap.getNamedItem("Date").getTextContent());
                return new AccIncRegistryRequest(dateTime);
            }
        }
    }


    public static class ReqMenu {

        public static class Item {

            public static class ReqComplexInfo {

                public static class ReqComplexInfoDetail {

                    ReqMenuDetail reqMenuDetail;

                    ReqComplexInfoDetail(ReqMenuDetail reqMenuDetail) {
                        this.reqMenuDetail = reqMenuDetail;
                    }

                    public ReqMenuDetail getReqMenuDetail() {
                        return reqMenuDetail;
                    }

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
                            return new ReqComplexInfoDetail(reqMenuDetail);
                        }
                    }
                }


                public static class Builder {

                    public ReqComplexInfo build(Node node, HashMap<Long, ReqMenuDetail> reqMenuDetailMap)
                            throws Exception {
                        NamedNodeMap namedNodeMap = node.getAttributes();
                        int complexId = Integer.parseInt(namedNodeMap.getNamedItem("ComplexId").getTextContent());
                        String complexMenuName = StringUtils
                                .substring(namedNodeMap.getNamedItem("ComplexMenuName").getTextContent(), 0, 60);
                        long price = Long.parseLong(namedNodeMap.getNamedItem("pr").getTextContent());
                        int modeFree = Integer.parseInt(namedNodeMap.getNamedItem("d").getTextContent());
                        int modeGrant = Integer.parseInt(namedNodeMap.getNamedItem("g").getTextContent());
                        int modeOfAdd = Integer.parseInt(namedNodeMap.getNamedItem("m").getTextContent());
                        Node childNode = node.getFirstChild();
                        ReqComplexInfoDetail.Builder reqComplexInfoDetailBuilder = new ReqComplexInfoDetail.Builder();
                        LinkedList<ReqComplexInfoDetail> reqComplexInfoDetailLinkedList = new LinkedList<ReqComplexInfoDetail>();
                        while (null != childNode) {
                            if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("CMI")) {
                                ReqComplexInfoDetail reqComplexInfoDetail = reqComplexInfoDetailBuilder
                                        .build(childNode, reqMenuDetailMap);
                                reqComplexInfoDetailLinkedList.add(reqComplexInfoDetail);
                            }
                            childNode = childNode.getNextSibling();
                        }
                        return new ReqComplexInfo(complexId, complexMenuName, modeFree, modeGrant, modeOfAdd,
                                reqComplexInfoDetailLinkedList);
                    }

                }

                private final int complexId;
                private final String complexMenuName;
                private final int modeFree, modeGrant, modeOfAdd;
                private final List<ReqComplexInfoDetail> complexInfoDetails;

                public ReqComplexInfo(int complexId, String complexMenuName, int modeFree, int modeGrant, int modeOfAdd,
                        List<ReqComplexInfoDetail> complexInfoDetails) {
                    this.complexId = complexId;
                    this.complexMenuName = complexMenuName;
                    this.modeFree = modeFree;
                    this.modeGrant = modeGrant;
                    this.modeOfAdd = modeOfAdd;
                    this.complexInfoDetails = complexInfoDetails;
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

                public List<ReqComplexInfoDetail> getComplexInfoDetails() {
                    return complexInfoDetails;
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
                        String idOfMenuStr = getTextContent(namedNodeMap.getNamedItem("IdOfMenu"));
                        Long idOfMenu = null;
                        if (idOfMenuStr != null) {
                            idOfMenu = Long.parseLong(idOfMenuStr);
                        }
                        long price = Long.parseLong(namedNodeMap.getNamedItem("Price").getTextContent());
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
                        return new ReqMenuDetail(idOfMenu, path, name, group, output, price, menuOrigin, availableNow,
                                protein, fat, carbohydrates, calories, vitB1, vitC, vitA, vitE, minCa, minP, minMg,
                                minFe);
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

                private final Long idOfMenu;
                private final String path;
                private final String name;
                private final String group;
                private final String output;
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
                private final int menuOrigin;
                private final int availableNow;

                public ReqMenuDetail(Long idOfMenu, String path, String name, String group, String output, long price,
                        int menuOrigin, int availableNow, Double protein, Double fat, Double carbohydrates,
                        Double calories, Double vitB1, Double vitC, Double vitA, Double vitE, Double minCa, Double minP,
                        Double minMg, Double minFe) {
                    this.idOfMenu = idOfMenu;
                    this.path = path;
                    this.name = name;
                    this.group = group;
                    this.output = output;
                    this.price = price;
                    this.menuOrigin = menuOrigin;
                    this.availableNow = availableNow;
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

                @Override
                public String toString() {
                    return "ReqMenuDetail{" + "idOfMenu=" + idOfMenu + ", path='" + path + '\'' + ", name='" + name
                            + '\'' + ", group='" + group + '\'' + ", output='" + output + '\'' + ", price=" + price
                            + ", protein=" + protein + ", fat=" + fat + ", carbohydrates=" + carbohydrates
                            + ", calories=" + calories + ", vitB1=" + vitB1 + ", vitC=" + vitC + ", vitA=" + vitA
                            + ", vitE=" + vitE + ", minCa=" + minCa + ", minP=" + minP + ", minMg=" + minMg + ", minFe="
                            + minFe + ", menuOrigin=" + menuOrigin + ", availableNow=" + availableNow + '}';
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
                        return new ReqAssortment(name, fullName, group, output, price, menuOrigin, protein, fat,
                                carbohydrates, calories, vitB1, vitC, vitA, vitE, minCa, minP, minMg, minFe);
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
                private final int menuOrigin;

                public ReqAssortment(String name, String fullName, String group, String menuOutput, long price,
                        int menuOrigin, Double protein, Double fat, Double carbohydrates, Double calories, Double vitB1,
                        Double vitC, Double vitA, Double vitE, Double minCa, Double minP, Double minMg, Double minFe) {
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

                @Override
                public String toString() {
                    return "ReqAssortment{" + "name='" + name + '\'' + ", group='" + group + '\'' + ", menuOutput='"
                            + menuOutput + '\'' + ", price=" + price + ", protein=" + protein + ", fat=" + fat
                            + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1=" + vitB1
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + '}';
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
                    Date date = loadContext.dateOnlyFormat.parse(namedNodeMap.getNamedItem("Value").getTextContent());
                    ////// process ML items (menu list)
                    List<ReqMenuDetail> reqMenuDetails = new LinkedList<ReqMenuDetail>();
                    HashMap<Long, ReqMenuDetail> reqMenuDetailMap = new HashMap<Long, ReqMenuDetail>();
                    List<ReqAssortment> reqAssortments = new LinkedList<ReqAssortment>();
                    Node childNode = itemNode.getFirstChild();
                    while (null != childNode) {
                        if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("ML")) {
                            ReqMenuDetail reqMenuDetail = reqMenuDetailBuilder.build(childNode, loadContext.menuGroups);
                            reqMenuDetails.add(reqMenuDetail);
                            if (reqMenuDetail.idOfMenu != null) {
                                reqMenuDetailMap.put(reqMenuDetail.idOfMenu, reqMenuDetail);
                            }
                        } else if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName()
                                .equals("AMI")) {
                            ReqAssortment reqAssortment = reqAssortmentBuilder.build(childNode, loadContext.menuGroups);
                            reqAssortments.add(reqAssortment);
                        }
                        childNode = childNode.getNextSibling();
                    }
                    ////// process CML items (complex menu)
                    List<ReqComplexInfo> reqComplexInfos = new LinkedList<ReqComplexInfo>();
                    if (loadContext.protoVersion >= 5) {
                        childNode = itemNode.getFirstChild();
                        while (null != childNode) {
                            if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("CML")) {
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

            public Enumeration<ReqMenuDetail> getReqMenuDetails() {
                return Collections.enumeration(reqMenuDetails);
            }

            public String getRawXmlText() {
                return rawXmlText;
            }

            @Override
            public String toString() {
                return "Item{" + "date=" + date + ", reqMenuDetails=" + reqMenuDetails + '}';
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

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
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
                return new ClientRegistryRequest(currentVersion);
            }
        }

        private final long currentVersion;

        public ClientRegistryRequest(long currentVersion) {
            this.currentVersion = currentVersion;
        }

        public long getCurrentVersion() {
            return currentVersion;
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
                    Date date = loadContext.dateOnlyFormat.parse(namedNodeMap.getNamedItem("Date").getTextContent());
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

    public static class EnterEvents {

        public static class EnterEvent {

            public EnterEvent(long idOfEnterEvent, long idOfOrg, String enterName, String turnstileAddr,
                    int passDirection, int eventCode, Long idOfCard, Long idOfClient, Long idOfTempCard,
                    Date evtDateTime, Long idOfVisitor, String visitorFullName, Integer docType, String docSerialNum,
                    Date issueDocDate, Date visitDateTime) {
                this.idOfEnterEvent = idOfEnterEvent;
                this.idOfOrg = idOfOrg;
                this.enterName = enterName;
                this.turnstileAddr = turnstileAddr;
                this.passDirection = passDirection;
                this.eventCode = eventCode;
                this.idOfCard = idOfCard;
                this.idOfClient = idOfClient;
                this.idOfTempCard = idOfTempCard;
                this.evtDateTime = evtDateTime;
                this.idOfVisitor = idOfVisitor;
                this.visitorFullName = visitorFullName;
                this.docType = docType;
                this.docSerialNum = docSerialNum;
                this.issueDocDate = issueDocDate;
                this.visitDateTime = visitDateTime;
            }

            public static class Builder {

                public EnterEvent build(Node enterEventNode, LoadContext loadContext, long idOfOrg) throws Exception {
                    long idOfEnterEvent = Long
                            .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfEnterEvent").getTextContent());
                    String enterName = enterEventNode.getAttributes().getNamedItem("EnterName").getTextContent();
                    String turnstileAddr = enterEventNode.getAttributes().getNamedItem("TurnstileAddr")
                            .getTextContent();
                    int passDirection = Integer
                            .parseInt(enterEventNode.getAttributes().getNamedItem("PassDirection").getTextContent());
                    int eventCode = Integer
                            .parseInt(enterEventNode.getAttributes().getNamedItem("EventCode").getTextContent());
                    Long idOfCard = null;
                    if (enterEventNode.getAttributes().getNamedItem("IdOfCard") != null) {
                        idOfCard = Long
                                .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfCard").getTextContent());
                    }
                    Long idOfClient = null;
                    if (enterEventNode.getAttributes().getNamedItem("IdOfClient") != null) {
                        idOfClient = Long
                                .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfClient").getTextContent());
                    }
                    Long idOfTempCard = null;
                    if (enterEventNode.getAttributes().getNamedItem("IdOfTempCard") != null) {
                        idOfTempCard = Long.parseLong(
                                enterEventNode.getAttributes().getNamedItem("IdOfTempCard").getTextContent());
                    }
                    TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
                    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    timeFormat.setTimeZone(localTimeZone);
                    Date evtDateTime = timeFormat
                            .parse(enterEventNode.getAttributes().getNamedItem("EvtDateTime").getTextContent());
                    Long idOfVisitor = null;
                    if (enterEventNode.getAttributes().getNamedItem("IdOfVisitor") != null) {
                        idOfVisitor = Long
                                .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfVisitor").getTextContent());
                    }
                    String visitorFullName = null;
                    if (enterEventNode.getAttributes().getNamedItem("VisitorFullName") != null) {
                        visitorFullName = enterEventNode.getAttributes().getNamedItem("VisitorFullName")
                                .getTextContent();
                    }
                    Integer docType = null;
                    if (enterEventNode.getAttributes().getNamedItem("DocType") != null) {
                        docType = Integer
                                .parseInt(enterEventNode.getAttributes().getNamedItem("DocType").getTextContent());
                    }
                    String docSerialNum = null;
                    if (enterEventNode.getAttributes().getNamedItem("DocSerialNum") != null) {
                        docSerialNum = enterEventNode.getAttributes().getNamedItem("DocSerialNum").getTextContent();
                    }
                    Date issueDocDate = null;
                    if (enterEventNode.getAttributes().getNamedItem("IssueDocDate") != null) {
                        issueDocDate = loadContext.dateOnlyFormat
                                .parse(enterEventNode.getAttributes().getNamedItem("IssueDocDate").getTextContent());
                    }
                    Date visitDateTime = null;
                    if (enterEventNode.getAttributes().getNamedItem("VisitDateTime") != null) {
                        visitDateTime = timeFormat
                                .parse(enterEventNode.getAttributes().getNamedItem("VisitDateTime").getTextContent());
                    }
                    return new EnterEvent(idOfEnterEvent, idOfOrg, enterName, turnstileAddr, passDirection, eventCode,
                            idOfCard, idOfClient, idOfTempCard, evtDateTime, idOfVisitor, visitorFullName, docType,
                            docSerialNum, issueDocDate, visitDateTime);
                }
            }

            private final long idOfEnterEvent;
            private final long idOfOrg;
            private final String enterName;
            private final String turnstileAddr;
            private final int passDirection;
            private final int eventCode;
            private final Long idOfCard;
            private final Long idOfClient;
            private final Long idOfTempCard;
            private final Date evtDateTime;
            private final Long idOfVisitor;
            private final String visitorFullName;
            private final Integer docType;
            private final String docSerialNum;
            private final Date issueDocDate;
            private final Date visitDateTime;

            public long getIdOfEnterEvent() {
                return idOfEnterEvent;
            }

            public long getIdOfOrg() {
                return idOfOrg;
            }

            public String getEnterName() {
                return enterName;
            }

            public String getTurnstileAddr() {
                return turnstileAddr;
            }

            public int getPassDirection() {
                return passDirection;
            }

            public int getEventCode() {
                return eventCode;
            }

            public Long getIdOfCard() {
                return idOfCard;
            }

            public Long getIdOfClient() {
                return idOfClient;
            }

            public Long getIdOfTempCard() {
                return idOfTempCard;
            }

            public Date getEvtDateTime() {
                return evtDateTime;
            }

            public Long getIdOfVisitor() {
                return idOfVisitor;
            }

            public String getVisitorFullName() {
                return visitorFullName;
            }

            public Integer getDocType() {
                return docType;
            }

            public String getDocSerialNum() {
                return docSerialNum;
            }

            public Date getIssueDocDate() {
                return issueDocDate;
            }

            public Date getVisitDateTime() {
                return visitDateTime;
            }

            @Override
            public String toString() {
                return "EnterEvent{" + "idOfEnterEvent=" + idOfEnterEvent + ", idOfOrg=" + idOfOrg + ", enterName='"
                        + enterName + '\'' + ", turnstileAddr='" + turnstileAddr + '\'' + ", passDirection="
                        + passDirection + ", eventCode=" + eventCode + ", idOfCard=" + idOfCard + ", idOfClient="
                        + idOfClient + ", idOfTempCard=" + idOfTempCard + ", evtDateTime=" + evtDateTime
                        + ", idOfVisitor=" + idOfVisitor + ", visitorFullName='" + visitorFullName + '\'' + ", docType="
                        + docType + ", docSerialNum='" + docSerialNum + '\'' + ", issueDocDate=" + issueDocDate
                        + ", visitDateTime=" + visitDateTime + '}';
            }
        }

        public static class Builder {

            private final EnterEvent.Builder enterEventBuilder;

            public Builder() {
                this.enterEventBuilder = new EnterEvent.Builder();
            }

            public EnterEvents build(Node enterEventsNode, LoadContext loadContext, long idOfOrg) throws Exception {
                List<EnterEvent> enterEventList = new ArrayList<EnterEvent>();
                Node itemNode = enterEventsNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("EE")) {
                        enterEventList.add(enterEventBuilder.build(itemNode, loadContext, idOfOrg));
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new EnterEvents(enterEventList);
            }
        }

        private final List<EnterEvent> events;

        public EnterEvents(List<EnterEvent> events) {
            this.events = events;
        }

        public List<EnterEvent> getEvents() {
            return events;
        }
    }

    public static class LibraryData {

        public static class Circulations {

            public static class Circulation {

                public Circulation(String action, long idOfCirculation, long idOfClient, long idOfPublication,
                        long idOfOrg, Date issuanceDate, Date refundDate, Date realRefundDate, int status,
                        long version) {
                    this.action = action;
                    this.idOfCirculation = idOfCirculation;
                    this.idOfClient = idOfClient;
                    this.idOfPublication = idOfPublication;
                    this.idOfOrg = idOfOrg;
                    this.issuanceDate = issuanceDate;
                    this.refundDate = refundDate;
                    this.realRefundDate = realRefundDate;
                    this.status = status;
                    this.version = version;
                }

                public static class Builder {

                    public Circulation build(Node circulationNode, long idOfOrg) throws Exception {
                        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
                        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
                        timeFormat.setTimeZone(localTimeZone);
                        String action = circulationNode.getAttributes().getNamedItem("Action").getTextContent();
                        long idOfCirculation = Long.parseLong(
                                circulationNode.getAttributes().getNamedItem("IdOfCirculation").getTextContent());
                        long idOfClient = Long
                                .parseLong(circulationNode.getAttributes().getNamedItem("IdOfClient").getTextContent());
                        long idOfPublication = Long.parseLong(
                                circulationNode.getAttributes().getNamedItem("IdOfPublication").getTextContent());
                        Date issuanceDate = timeFormat
                                .parse(circulationNode.getAttributes().getNamedItem("IssuanceDate").getTextContent());
                        Date refundDate = timeFormat
                                .parse(circulationNode.getAttributes().getNamedItem("RefundDate").getTextContent());
                        Date realRefundDate = null;
                        if (circulationNode.getAttributes().getNamedItem("RealRefundDate") != null) {
                            realRefundDate = timeFormat
                                    .parse(circulationNode.getAttributes().getNamedItem("RealRefundDate")
                                            .getTextContent());
                        }
                        int status = Integer
                                .parseInt(circulationNode.getAttributes().getNamedItem("Status").getTextContent());
                        long version = Long
                                .parseLong(circulationNode.getAttributes().getNamedItem("Version").getTextContent());
                        return new Circulation(action, idOfCirculation, idOfClient, idOfPublication, idOfOrg,
                                issuanceDate, refundDate, realRefundDate, status, version);
                    }
                }

                private final String action;
                private final long idOfCirculation;
                private final long idOfClient;
                private final long idOfPublication;
                private final long idOfOrg;
                private final Date issuanceDate;
                private final Date refundDate;
                private final Date realRefundDate;
                private final int status;
                private final long version;

                public String getAction() {
                    return action;
                }

                public long getIdOfCirculation() {
                    return idOfCirculation;
                }

                public long getIdOfClient() {
                    return idOfClient;
                }

                public long getIdOfPublication() {
                    return idOfPublication;
                }

                public long getIdOfOrg() {
                    return idOfOrg;
                }

                public Date getIssuanceDate() {
                    return issuanceDate;
                }

                public Date getRefundDate() {
                    return refundDate;
                }

                public Date getRealRefundDate() {
                    return realRefundDate;
                }

                public int getStatus() {
                    return status;
                }

                public long getVersion() {
                    return version;
                }

                @Override
                public String toString() {
                    return "Circulation{" + "action='" + action + '\'' + ", idOfCirculation=" + idOfCirculation
                            + ", idOfClient=" + idOfClient + ", idOfPublication=" + idOfPublication + ", idOfOrg="
                            + idOfOrg + ", issuanceDate=" + issuanceDate + ", refundDate=" + refundDate
                            + ", realRefundDate=" + realRefundDate + ", status=" + status + ", version=" + version
                            + '}';
                }
            }

            public static class Builder {

                private final Circulation.Builder circulationBuilder;

                public Builder() {
                    this.circulationBuilder = new Circulation.Builder();
                }

                public Circulations build(Node circulationsNode, long idOfOrg) throws Exception {
                    List<Circulation> circulationList = new ArrayList<Circulation>();
                    Node itemNode = circulationsNode.getFirstChild();
                    while (null != itemNode) {
                        if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName()
                                .equals("Circulation")) {
                            circulationList.add(circulationBuilder.build(itemNode, idOfOrg));
                        }
                        itemNode = itemNode.getNextSibling();
                    }
                    return new Circulations(circulationList);
                }
            }

            private final List<Circulation> circulationList;

            public Circulations() {
                this.circulationList = new ArrayList<Circulation>();
            }

            public Circulations(List<Circulation> circulationList) {
                this.circulationList = circulationList;
            }

            public List<Circulation> getCirculationList() {
                return circulationList;
            }

        }

        public static class Publications {

            public static class Publication {

                public Publication(String action, long idOfPublication, long idOfOrg, String recordStatus,
                        String recordType, String bibliographicLevel, String hierarchicalLevel, String codingLevel,
                        String formOfCatalogingDescription, String data, String author, String title, String title2,
                        String publicationDate, String publisher, long version) {
                    this.action = action;
                    this.idOfPublication = idOfPublication;
                    this.idOfOrg = idOfOrg;
                    this.recordStatus = recordStatus;
                    this.recordType = recordType;
                    this.bibliographicLevel = bibliographicLevel;
                    this.hierarchicalLevel = hierarchicalLevel;
                    this.codingLevel = codingLevel;
                    this.formOfCatalogingDescription = formOfCatalogingDescription;
                    this.data = data;
                    this.author = author;
                    this.title = title;
                    this.title2 = title2;
                    this.publicationDate = publicationDate;
                    this.publisher = publisher;
                    this.version = version;
                }

                public static class Builder {

                    public Publication build(Node publicationNode, long idOfOrg) throws Exception {
                        String action = publicationNode.getAttributes().getNamedItem("Action").getTextContent();
                        long idOfPublication = Long.parseLong(
                                publicationNode.getAttributes().getNamedItem("IdOfPublication").getTextContent());
                        String recordStatus = publicationNode.getAttributes().getNamedItem("RecordStatus")
                                .getTextContent();
                        String recordType = publicationNode.getAttributes().getNamedItem("RecordType").getTextContent();
                        String bibliographicLevel = publicationNode.getAttributes().getNamedItem("BibliographicLevel")
                                .getTextContent();
                        String hierarchicalLevel = publicationNode.getAttributes().getNamedItem("HierarchicalLevel")
                                .getTextContent();
                        String codingLevel = publicationNode.getAttributes().getNamedItem("CodingLevel")
                                .getTextContent();
                        String formOfCatalogingDescription = publicationNode.getAttributes()
                                .getNamedItem("formOfCatalogingDescription").getTextContent();
                        String data = null;
                        if (publicationNode.getTextContent() != null) {
                            data = publicationNode.getTextContent();
                        }
                        String author = null;
                        if (publicationNode.getAttributes().getNamedItem("Author") != null) {
                            author = publicationNode.getAttributes().getNamedItem("Author").getTextContent();
                        }
                        String title = null;
                        if (publicationNode.getAttributes().getNamedItem("Title") != null) {
                            title = publicationNode.getAttributes().getNamedItem("Title").getTextContent();
                        }
                        String title2 = null;
                        if (publicationNode.getAttributes().getNamedItem("Title2") != null) {
                            title2 = publicationNode.getAttributes().getNamedItem("Title2").getTextContent();
                        }
                        String publicationDate = null;
                        if (publicationNode.getAttributes().getNamedItem("PublicationDate") != null) {
                            publicationDate = publicationNode.getAttributes().getNamedItem("PublicationDate")
                                    .getTextContent();
                        }
                        String publisher = null;
                        if (publicationNode.getAttributes().getNamedItem("Publisher") != null) {
                            publisher = publicationNode.getAttributes().getNamedItem("Publisher").getTextContent();
                        }
                        long version = Long
                                .parseLong(publicationNode.getAttributes().getNamedItem("Version").getTextContent());

                        return new Publication(action, idOfPublication, idOfOrg, recordStatus, recordType,
                                bibliographicLevel, hierarchicalLevel, codingLevel, formOfCatalogingDescription, data,
                                author, title, title2, publicationDate, publisher, version);
                    }
                }

                private final String action;
                private final long idOfPublication;
                private final long idOfOrg;
                private final String recordStatus;
                private final String recordType;
                private final String bibliographicLevel;
                private final String hierarchicalLevel;
                private final String codingLevel;
                private final String formOfCatalogingDescription;
                private final String data;
                private final String author;
                private final String title;
                private final String title2;
                private final String publicationDate;
                private final String publisher;
                private final long version;

                public String getAction() {
                    return action;
                }

                public long getIdOfPublication() {
                    return idOfPublication;
                }

                public long getIdOfOrg() {
                    return idOfOrg;
                }

                public String getRecordStatus() {
                    return recordStatus;
                }

                public String getRecordType() {
                    return recordType;
                }

                public String getBibliographicLevel() {
                    return bibliographicLevel;
                }

                public String getHierarchicalLevel() {
                    return hierarchicalLevel;
                }

                public String getCodingLevel() {
                    return codingLevel;
                }

                public String getFormOfCatalogingDescription() {
                    return formOfCatalogingDescription;
                }

                public String getData() {
                    return data;
                }

                public String getAuthor() {
                    return author;
                }

                public String getTitle() {
                    return title;
                }

                public String getTitle2() {
                    return title2;
                }

                public String getPublicationDate() {
                    return publicationDate;
                }

                public String getPublisher() {
                    return publisher;
                }

                public long getVersion() {
                    return version;
                }

                @Override
                public String toString() {
                    return "Publication{" + "action='" + action + '\'' + ", idOfPublication=" + idOfPublication
                            + ", idOfOrg=" + idOfOrg + ", recordStatus='" + recordStatus + '\'' + ", recordType='"
                            + recordType + '\'' + ", bibliographicLevel='" + bibliographicLevel + '\''
                            + ", hierarchicalLevel='" + hierarchicalLevel + '\'' + ", codingLevel='" + codingLevel
                            + '\'' + ", formOfCatalogingDescription='" + formOfCatalogingDescription + '\'' + ", data='"
                            + data + '\'' + ", author='" + author + '\'' + ", title='" + title + '\'' + ", title2='"
                            + title2 + '\'' + ", publicationDate='" + publicationDate + '\'' + ", publisher='"
                            + publisher + '\'' + ", version=" + version + '}';
                }
            }

            public static class Builder {

                private final Publication.Builder publicationBuilder;

                public Builder() {
                    this.publicationBuilder = new Publication.Builder();
                }

                public Publications build(Node publicationsNode, long idOfOrg) throws Exception {
                    List<Publication> publicationList = new ArrayList<Publication>();
                    Node itemNode = publicationsNode.getFirstChild();
                    while (null != itemNode) {
                        if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName()
                                .equals("Publication")) {
                            publicationList.add(publicationBuilder.build(itemNode, idOfOrg));
                        }
                        itemNode = itemNode.getNextSibling();
                    }
                    return new Publications(publicationList);
                }
            }

            private final List<Publication> publicationList;

            public Publications() {
                this.publicationList = new ArrayList<Publication>();
            }

            public Publications(List<Publication> publicationList) {
                this.publicationList = publicationList;
            }

            public List<Publication> getPublicationList() {
                return publicationList;
            }
        }

        public static class Builder {

            private final Circulations.Builder circulationsBuilder;

            private final Publications.Builder publicationsBuilder;

            public Builder() {
                this.circulationsBuilder = new Circulations.Builder();
                this.publicationsBuilder = new Publications.Builder();
            }

            public LibraryData build(Node libraryDataNode, long idOfOrg) throws Exception {
                Node itemNode = libraryDataNode.getFirstChild();
                Circulations circulations = new Circulations();
                Publications publications = new Publications();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Circulations")) {
                        circulations = circulationsBuilder.build(itemNode, idOfOrg);
                    }
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Publications")) {
                        publications = publicationsBuilder.build(itemNode, idOfOrg);
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new LibraryData(circulations, publications);
            }
        }

        public LibraryData(Circulations circulations, Publications publications) {
            this.circulations = circulations;
            this.publications = publications;
        }

        private final Circulations circulations;
        private final Publications publications;

        public Circulations getCirculations() {
            return circulations;
        }

        public Publications getPublications() {
            return publications;
        }
    }


    public static class LibraryData2 {


        public static class Publs {

            public static class Publ {

                public Publ(Long idOfPubl, String isbn, String data, String author, String title, String title2,
                        String publicationDate, String publisher) {
                    this.idOfPubl = idOfPubl;
                    this.isbn = isbn;
                    this.data = data;
                    this.author = author;
                    this.title = title;
                    this.title2 = title2;
                    this.publicationDate = publicationDate;
                    this.publisher = publisher;
                }

                public static class Builder {

                    public Publ build(Node publicationNode) throws Exception {

                        Long idOfPublication = null;
                        if (publicationNode.getAttributes().getNamedItem("IdOfPublication") != null) {
                            String publicationId = publicationNode.getAttributes().getNamedItem("IdOfPublication")
                                    .getTextContent();
                            if ((publicationId != null) && (!publicationId.isEmpty())) {
                                idOfPublication = Long.parseLong(publicationId);
                            }
                        }

                        String isbn = null;
                        if (publicationNode.getAttributes().getNamedItem("ISBN") != null) {
                            isbn = publicationNode.getAttributes().getNamedItem("ISBN").getTextContent();
                        }

                        String data = null;
                        if (publicationNode.getTextContent() != null) {
                            data = publicationNode.getTextContent();
                        }
                        String author = null;
                        if (publicationNode.getAttributes().getNamedItem("Author") != null) {
                            author = publicationNode.getAttributes().getNamedItem("Author").getTextContent();
                        }
                        String title = null;
                        if (publicationNode.getAttributes().getNamedItem("Title") != null) {
                            title = publicationNode.getAttributes().getNamedItem("Title").getTextContent();
                        }
                        String title2 = null;
                        if (publicationNode.getAttributes().getNamedItem("Title2") != null) {
                            title2 = publicationNode.getAttributes().getNamedItem("Title2").getTextContent();
                        }
                        String publicationDate = null;
                        if (publicationNode.getAttributes().getNamedItem("PublicationDate") != null) {
                            publicationDate = publicationNode.getAttributes().getNamedItem("PublicationDate")
                                    .getTextContent();
                        }
                        String publisher = null;
                        if (publicationNode.getAttributes().getNamedItem("Publisher") != null) {
                            publisher = publicationNode.getAttributes().getNamedItem("Publisher").getTextContent();
                        }

                        return new Publ(idOfPublication, isbn, data, author, title, title2, publicationDate, publisher);
                    }
                }

                private final Long idOfPubl;
                private final String isbn;
                private final String data;
                private final String author;
                private final String title;
                private final String title2;
                private final String publicationDate;
                private final String publisher;

                public Long getIdOfPubl() {
                    return idOfPubl;
                }

                public String getIsbn() {
                    return isbn;
                }

                public String getData() {
                    return data;
                }

                public String getAuthor() {
                    return author;
                }

                public String getTitle() {
                    return title;
                }

                public String getTitle2() {
                    return title2;
                }

                public String getPublicationDate() {
                    return publicationDate;
                }

                public String getPublisher() {
                    return publisher;
                }

                @Override
                public String toString() {
                    return "Publ{" +
                            "idOfPubl=" + idOfPubl +
                            ", isbn='" + isbn + '\'' +
                            ", data='" + data + '\'' +
                            ", author='" + author + '\'' +
                            ", title='" + title + '\'' +
                            ", title2='" + title2 + '\'' +
                            ", publicationDate='" + publicationDate + '\'' +
                            ", publisher='" + publisher + '\'' +
                            '}';
                }
            }

            public static class Builder {

                private final Publ.Builder publicationBuilder;

                public Builder() {
                    this.publicationBuilder = new Publ.Builder();
                }

                public Publs build(Node publicationsNode) throws Exception {
                    List<Publ> publicationList = new ArrayList<Publ>();
                    Node itemNode = publicationsNode.getFirstChild();
                    while (null != itemNode) {
                        if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Publ")) {
                            publicationList.add(publicationBuilder.build(itemNode));
                        }
                        itemNode = itemNode.getNextSibling();
                    }
                    return new Publs(publicationList);
                }
            }

            private final List<Publ> publicationList;

            public Publs() {
                this.publicationList = new ArrayList<Publ>();
            }

            public Publs(List<Publ> publicationList) {
                this.publicationList = publicationList;
            }

            public List<Publ> getPublList() {
                return publicationList;
            }
        }

        public static class Circuls {

            private final List<Circul> circulationList;

            public static class Circul {

                private final Long idofcircul;
                private final long idofclient;
                private final long idofpubl;
                private final long idoforg;
                private final Date issuancedate;
                private final Date refundDate;
                private final Date realRefundDate;
                private final int quantity;
                private final Boolean delete;

                public Circul(Long idofcircul, long idofclient, long idofpubl, long idoforg, Date issuancedate,
                        Date refundDate, Date realRefundDate, int quantity, Boolean delete) {
                    this.idofcircul = idofcircul;
                    this.idofclient = idofclient;
                    this.idofpubl = idofpubl;
                    this.idoforg = idoforg;
                    this.issuancedate = issuancedate;
                    this.refundDate = refundDate;
                    this.realRefundDate = realRefundDate;
                    this.quantity = quantity;
                    this.delete = delete;
                }

                public static class Builder {

                    public Circul build(Node circulationNode) throws Exception {

                        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
                        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
                        timeFormat.setTimeZone(localTimeZone);

                        Long idOfCirculation = null;
                        if (circulationNode.getAttributes().getNamedItem("IdOfCirculation") != null) {
                            String circulationId = circulationNode.getAttributes().getNamedItem("IdOfCirculation")
                                    .getTextContent();
                            if ((circulationId != null) && (!circulationId.isEmpty())) {
                                idOfCirculation = Long.parseLong(circulationId);
                            }
                        }

                        long idOfClient = Long
                                .parseLong(circulationNode.getAttributes().getNamedItem("IdOfClient").getTextContent());

                        long idOfPublication = Long.parseLong(
                                circulationNode.getAttributes().getNamedItem("IdOfPublication").getTextContent());

                        long idOfOrg = Long
                                .parseLong(circulationNode.getAttributes().getNamedItem("IdOfOrg").getTextContent());

                        Date issuanceDate = null;
                        if (circulationNode.getAttributes().getNamedItem("IssuanceDate") != null) {
                            issuanceDate = timeFormat.parse(circulationNode.getAttributes().getNamedItem("IssuanceDate")
                                    .getTextContent());
                        }

                        Date refundDate = null;
                        if (circulationNode.getAttributes().getNamedItem("RefundDate") != null) {
                            refundDate = timeFormat
                                    .parse(circulationNode.getAttributes().getNamedItem("RefundDate").getTextContent());
                        }

                        Date realRefundDate = null;
                        if (circulationNode.getAttributes().getNamedItem("RealRefundDate") != null) {
                            realRefundDate = timeFormat
                                    .parse(circulationNode.getAttributes().getNamedItem("RealRefundDate")
                                            .getTextContent());
                        }

                        int quantity = Integer.parseInt(circulationNode.getAttributes().getNamedItem("Quantity").getTextContent());
                        
                        Boolean delete = null;
                        if (circulationNode.getAttributes().getNamedItem("Delete") != null) {
                            Boolean.parseBoolean(circulationNode.getAttributes().getNamedItem("Delete").getTextContent());
                        }
                        return new Circul(idOfCirculation, idOfClient, idOfPublication, idOfOrg, issuanceDate,
                                refundDate, realRefundDate, quantity, delete);
                    }
                }

                public Long getIdofcircul() {
                    return idofcircul;
                }

                public long getIdofclient() {
                    return idofclient;
                }

                public long getIdofpubl() {
                    return idofpubl;
                }

                public long getIdoforg() {
                    return idoforg;
                }

                public Date getIssuancedate() {
                    return issuancedate;
                }

                public Date getRefundDate() {
                    return refundDate;
                }

                public Date getRealRefundDate() {
                    return realRefundDate;
                }

                public int getQuantity() {
                    return quantity;
                }

                public Boolean isDelete() {
                    return delete;
                }

                @Override
                public String toString() {
                    return "Circul{" +
                            "idofcircul=" + idofcircul +
                            ", idofclient=" + idofclient +
                            ", idofpubl=" + idofpubl +
                            ", idoforg=" + idoforg +
                            ", issuancedate=" + issuancedate +
                            ", refundDate=" + refundDate +
                            ", realRefundDate=" + realRefundDate +
                            ", quantity=" + quantity +
                            '}';
                }
            }

            public static class Builder {

                private final Circul.Builder circulationBuilder;

                public Builder() {
                    this.circulationBuilder = new Circul.Builder();
                }

                public Circuls build(Node circulationsNode) throws Exception {
                    List<Circul> circulationList = new ArrayList<Circul>();
                    Node itemNode = circulationsNode.getFirstChild();
                    while (null != itemNode) {
                        if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Circulation")) {
                            circulationList.add(circulationBuilder.build(itemNode));
                        }
                        itemNode = itemNode.getNextSibling();
                    }
                    return new Circuls(circulationList);
                }
            }

            public Circuls() {
                this.circulationList = new ArrayList<Circul>();
            }

            public Circuls(List<Circul> circulationList) {
                this.circulationList = circulationList;
            }

            public List<Circul> getCirculationList() {
                return circulationList;
            }
        }

        public static class Builder {

            private final Publs.Builder publicationsBuilder;
            private final Circuls.Builder circulationBuilder;

            public Builder() {
                this.publicationsBuilder = new Publs.Builder();
                this.circulationBuilder = new Circuls.Builder();
            }

            public LibraryData2 build(Node libraryData2Node) throws Exception {
                Node itemNode = libraryData2Node.getFirstChild();
                Publs publications = new Publs();
                Circuls circulations = new Circuls();
                long version = 0;
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Publs")) {
                        publications = publicationsBuilder.build(itemNode);
                    }
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Circulations")) {
                        circulations = circulationBuilder.build(itemNode);
                    }
                    if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("Version")) {
                        version = Long.parseLong(itemNode.getTextContent());
                    }
                    itemNode = itemNode.getNextSibling();
                }
                return new LibraryData2(publications, circulations, version);
            }
        }

        public LibraryData2(Publs publications, Circuls circulations,long version) {
            this.publications = publications;
            this.circulations = circulations;
            this.version = version;
        }

        private final Publs publications;
        private final Circuls circulations;

        long version;

        public Publs getPubls() {
            return publications;
        }

        public Circuls getCirculs() {
            return circulations;
        }

        public long getVersion() {
            return version;
        }
    }

    public static class LoadContext {

        MenuGroups menuGroups;
        public long protoVersion;
        DateFormat timeFormat, dateOnlyFormat;
    }

    public static class Builder {

        private final DateFormat dateOnlyFormat;
        private final DateFormat timeFormat;
        private final PaymentRegistry.Builder paymentRegistryBuilder;
        private final AccIncRegistryRequest.Builder accIncRegistryRequestBuilder;
        private final ClientParamRegistry.Builder clientParamRegistryBuilder;
        private final ClientRegistryRequest.Builder clientRegistryRequestBuilder;
        private final OrgStructure.Builder orgStructureBuilder;
        private final ReqMenu.Builder reqMenuBuilder;
        private final ReqDiary.Builder reqDiaryBuilder;
        private final MenuGroups.Builder menuGroupsBuilder;
        private final EnterEvents.Builder enterEventsBuilder;
        private final LibraryData.Builder libraryDataBuilder;
        private final LibraryData2.Builder libraryData2Builder;

        public Builder() {
            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            this.dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.dateOnlyFormat.setTimeZone(utcTimeZone);

            TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
            this.timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            this.timeFormat.setTimeZone(localTimeZone);

            this.paymentRegistryBuilder = new PaymentRegistry.Builder();
            this.accIncRegistryRequestBuilder = new AccIncRegistryRequest.Builder();
            this.clientParamRegistryBuilder = new ClientParamRegistry.Builder();
            this.clientRegistryRequestBuilder = new ClientRegistryRequest.Builder();
            this.orgStructureBuilder = new OrgStructure.Builder();
            this.reqMenuBuilder = new ReqMenu.Builder();
            this.reqDiaryBuilder = new ReqDiary.Builder();
            this.menuGroupsBuilder = new MenuGroups.Builder();
            this.enterEventsBuilder = new EnterEvents.Builder();
            this.libraryDataBuilder = new LibraryData.Builder();
            this.libraryData2Builder = new LibraryData2.Builder();
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

        public static int getSyncType(NamedNodeMap namedNodeMap) throws Exception {
            return parseSyncType(getStringValueNullSafe(namedNodeMap, "Type"));
        }

        public SyncRequest build(Node envelopeNode, NamedNodeMap namedNodeMap, Org org, String idOfSync)
                throws Exception {
            long version = getLongValue(namedNodeMap, "Version");
            if (3L != version && 4L != version && 5L != version) {
                throw new Exception(String.format("Unsupported protoVersion: %d", version));
            }
            String sSyncType = getStringValueNullSafe(namedNodeMap, "Type");
            int type = parseSyncType(sSyncType);

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

            LoadContext loadContext = new LoadContext();
            loadContext.menuGroups = menuGroups;
            loadContext.protoVersion = version;
            loadContext.dateOnlyFormat = dateOnlyFormat;
            loadContext.timeFormat = timeFormat;

            Node paymentRegistryNode = findFirstChildElement(envelopeNode, "PaymentRegistry");
            PaymentRegistry paymentRegistry = null;
            if (paymentRegistryNode != null) {
                paymentRegistry = paymentRegistryBuilder.build(paymentRegistryNode, loadContext);
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

            // 07.09.2011 EnterEvents
            Node enterEventsNode = findFirstChildElement(envelopeNode, "EnterEvents");
            EnterEvents enterEvents = null;
            if (enterEventsNode != null) {
                enterEvents = enterEventsBuilder.build(enterEventsNode, loadContext, org.getIdOfOrg());
            }

            // 15.09.2011 LibraryData
            Node libraryDataNode = findFirstChildElement(envelopeNode, "LibraryData");
            LibraryData libraryData = null;
            if (libraryDataNode != null) {
                libraryData = libraryDataBuilder.build(libraryDataNode, org.getIdOfOrg());
            }

            Node libraryData2Node = findFirstChildElement(envelopeNode, "LibraryData2");
            LibraryData2 libraryData2 = null;
            if (libraryData2Node != null) {
                libraryData2 = libraryData2Builder.build(libraryData2Node);
            }


            return new SyncRequest(version, type, org, syncTime, idOfPacket, paymentRegistry, accIncRegistryRequest,
                    clientParamRegistry, clientRegistryRequest, orgStructure, menuGroups, reqMenu, reqDiary, message,
                    enterEvents, libraryData, libraryData2);
        }

        private static int parseSyncType(String sSyncType) throws Exception {
            int type;
            if (sSyncType != null && sSyncType.equals("GetAccInc")) {
                type = SyncRequest.TYPE_GET_ACC_INC;
            } else if (sSyncType == null || sSyncType.equals("Full")) {
                type = SyncRequest.TYPE_FULL;
            } else {
                throw new Exception("Invalid request type: " + sSyncType);
            }
            return type;
        }

        private static Node findFirstChildElement(Node node, String name) throws Exception {
            Node currNode = node.getFirstChild();
            while (null != currNode) {
                if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                    return currNode;
                }
                currNode = currNode.getNextSibling();
            }
            return null;
        }

        private static Node findFirstChildTextNode(Node node) throws Exception {
            Node currNode = node.getFirstChild();
            while (null != currNode) {
                if (Node.TEXT_NODE == currNode.getNodeType()) {
                    return currNode;
                }
                currNode = currNode.getNextSibling();
            }
            return null;
        }
    }

    public final static int TYPE_FULL = 0, TYPE_GET_ACC_INC = 1;

    private final long protoVersion;
    private final long idOfOrg;
    private final Org org;
    private final Date syncTime;
    private final Long idOfPacket;
    private final MenuGroups menuGroups;
    private final PaymentRegistry paymentRegistry;
    private final ClientParamRegistry clientParamRegistry;
    private final ClientRegistryRequest clientRegistryRequest;
    private final AccIncRegistryRequest accIncRegistryRequest;
    private final OrgStructure orgStructure;
    private final ReqMenu reqMenu;
    private final ReqDiary reqDiary;
    private final String message;
    private final int type;
    private final EnterEvents enterEvents;
    private final LibraryData libraryData;
    private final LibraryData2 libraryData2;

    public SyncRequest(long protoVersion, int type, Org org, Date syncTime, Long idOfPacket,
            PaymentRegistry paymentRegistry, AccIncRegistryRequest accIncRegistryRequest,
            ClientParamRegistry clientParamRegistry, ClientRegistryRequest clientRegistryRequest,
            OrgStructure orgStructure, MenuGroups menuGroups, ReqMenu reqMenu, ReqDiary reqDiary, String message,
            EnterEvents enterEvents, LibraryData libraryData, LibraryData2 libraryData2) {
        this.protoVersion = protoVersion;
        this.type = type;
        this.idOfOrg = org.getIdOfOrg();
        this.org = org;
        this.syncTime = syncTime;
        this.idOfPacket = idOfPacket;
        this.paymentRegistry = paymentRegistry;
        this.accIncRegistryRequest = accIncRegistryRequest;
        this.clientParamRegistry = clientParamRegistry;
        this.clientRegistryRequest = clientRegistryRequest;
        this.orgStructure = orgStructure;
        this.menuGroups = menuGroups;
        this.reqMenu = reqMenu;
        this.reqDiary = reqDiary;
        this.message = message;
        this.enterEvents = enterEvents;
        this.libraryData = libraryData;
        this.libraryData2 = libraryData2;
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

    public LibraryData getLibraryData() {
        return libraryData;
    }

    public LibraryData2 getLibraryData2() {
        return libraryData2;
    }

    @Override
    public String toString() {
        return "SyncRequest{" + "protoVersion=" + protoVersion + ", idOfOrg=" + idOfOrg + ", syncTime=" + syncTime
                + ", idOfPacket=" + idOfPacket + ", paymentRegistry=" + paymentRegistry + ", clientParamRegistry="
                + clientParamRegistry + ", clientRegistryRequest=" + clientRegistryRequest + ", orgStructure="
                + orgStructure + ", reqMenu=" + reqMenu + ", reqDiary=" + reqDiary + ", message='" + message + '\''
                + ", enterEvents=" + enterEvents + ", libraryData=" + libraryData + '}';
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
}
