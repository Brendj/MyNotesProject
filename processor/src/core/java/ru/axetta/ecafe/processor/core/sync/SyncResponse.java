/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardOperationData;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoles;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.response.DirectiveElement;
import ru.axetta.ecafe.processor.core.sync.response.GoodsBasicBasketData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.response.QuestionaryData;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class SyncResponse {

    public static class ResPaymentRegistry {

        public static class Item {

            private final long idOfOrder;
            private final int result;
            private final String error;

            public Item(long idOfOrder, int result, String error) {
                this.idOfOrder = idOfOrder;
                this.result = result;
                this.error = error;
            }

            public long getIdOfOrder() {
                return idOfOrder;
            }

            public int getResult() {
                return result;
            }

            public String getError() {
                return error;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("RPT");
                element.setAttribute("IdOfOrder", Long.toString(this.idOfOrder));
                element.setAttribute("Result", Integer.toString(this.result));
                if (null != this.error) {
                    element.setAttribute("Error", this.error);
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "idOfOrder=" + idOfOrder + ", result=" + result + ", error='" + error + '\'' + '}';
            }
        }

        private final List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Iterator<Item> getItems() {
            return items.iterator();
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResPaymentRegistry");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResPaymentRegistry{" + "items=" + items + '}';
        }
    }

    public static class AccRegistry {

        public static class Item {

            private final long cardNo;
            private final int cardType;
            private final long idOfClient;
            private final Date updateTime;
            private final long balance;
            private final long limit;
            private final long expenditureLimit;
            private final int state;
            private final String lockReason;
            private final Date validTime;
            private final Date issueTime;

            public Item(Client client, Card card) {
                this.cardNo = card.getCardNo();
                this.cardType = card.getCardType();
                this.idOfClient = card.getClient().getIdOfClient();
                this.updateTime = card.getUpdateTime();
                //Client client = card.getClient();
                this.balance = client.getBalance();
                this.limit = client.getLimit();
                this.expenditureLimit = client.getExpenditureLimit();
                this.state = card.getState();
                this.lockReason = card.getLockReason();
                this.validTime = card.getValidTime();
                this.issueTime = card.getIssueTime();
            }

            public long getCardNo() {
                return cardNo;
            }

            public int getCardType() {
                return cardType;
            }

            public Date getUpdateTime() {
                return updateTime;
            }

            public long getBalance() {
                return balance;
            }

            public long getLimit() {
                return limit;
            }

            public long getExpenditureLimit() {
                return expenditureLimit;
            }

            public int getState() {
                return state;
            }

            public Element toElement(Document document, DateFormat dateFormat, DateFormat timeFormat) throws Exception {
                Element element = document.createElement("AR");
                element.setAttribute("CardNo", Long.toString(cardNo));
                element.setAttribute("CardType", Integer.toString(cardType));
                element.setAttribute("IdOfClient", Long.toString(idOfClient));
                element.setAttribute("LastUpdate", timeFormat.format(updateTime));
                element.setAttribute("Balance", Long.toString(balance));
                element.setAttribute("Limit", Long.toString(limit));
                element.setAttribute("ExpenditureLimit", Long.toString(expenditureLimit));
                element.setAttribute("State", Long.toString(state));
                if (null != lockReason) {
                    element.setAttribute("LockReason", lockReason);
                }
                element.setAttribute("ValidDate", dateFormat.format(validTime));
                //todo change to
                //element.setAttribute("ValidDate", timeFormat.format(validTime));
                if (null != issueTime) {
                    element.setAttribute("IssueDate", dateFormat.format(issueTime));
                    //todo change to
                    //element.setAttribute("IssueDate", timeFormat.format(issueTime));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "cardNo=" + cardNo + ", cardType=" + cardType + ", lastUpdate=" + updateTime
                        + ", balance=" + balance + ", limit=" + limit + ", expenditureLimit=" + expenditureLimit
                        + ", state=" + state + '}';
            }
        }

        private final List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
        }

        public Element toElement(Document document, DateFormat dateFormat, DateFormat timeFormat) throws Exception {
            Element element = document.createElement("AccRegistry");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document, dateFormat, timeFormat));
            }
            return element;
        }

        @Override
        public String toString() {
            return "AccRegistry{" + "items=" + items + '}';
        }
    }

    public static class AccIncRegistry {

        public static class Item {

            private final long idOfPaymentOrder;
            private final long idOfClient;
            private final Date dateTime;
            private final long sum;

            public Item(long idOfPaymentOrder, long idOfClient, Date dateTime, long sum) {
                this.idOfPaymentOrder = idOfPaymentOrder;
                this.idOfClient = idOfClient;
                this.dateTime = dateTime;
                this.sum = sum;
            }

            public long getIdOfPaymentOrder() {
                return idOfPaymentOrder;
            }

            public long getIdOfClient() {
                return idOfClient;
            }

            public Date getDateTime() {
                return dateTime;
            }

            public long getSum() {
                return sum;
            }

            public Element toElement(Document document, DateFormat timeFormat) throws Exception {
                Element element = document.createElement("AI");
                element.setAttribute("IdOfPaymentOrder", Long.toString(idOfPaymentOrder));
                element.setAttribute("IdOfClient", Long.toString(idOfClient));
                element.setAttribute("Date", timeFormat.format(dateTime));
                element.setAttribute("Sum", Long.toString(sum));
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "idOfPaymentOrder=" + idOfPaymentOrder + ", idOfClient=" + idOfClient + ", dateTime="
                        + dateTime + ", sum=" + sum + '}';
            }
        }

        private Date date;
        private final List<Item> items = new ArrayList<Item>();

        public AccIncRegistry() {
        }

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
        }

        public Element toElement(Document document, DateFormat dateFormat, DateFormat timeFormat) throws Exception {
            Element element = document.createElement("AccIncRegistry");
            element.setAttribute("Date", timeFormat.format(date));
            for (Item item : this.items) {
                element.appendChild(item.toElement(document, timeFormat));
            }
            return element;
        }

        @Override
        public String toString() {
            return "AccIncRegistry{" + "items=" + items + '}';
        }
    }

    public static class ClientRegistry {

        public static class Item {

            private final long idOfClient;
            private final long version;
            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String idDocument;
            private final String address;
            private final String phone;
            private final String mobile;
            private final String email;
            private final String fax;
            private final int contractState;
            private final long contractId;
            private final Integer freePayMaxCount;
            private final String categoriesDiscounts;
            private final ClientGroup clientGroup;
            private final boolean notifyViaEmail;
            private final boolean notifyViaSMS;
            private final String remarks;
            private final boolean canConfirmGroupPayment;
            private final int discountMode;
            private final String guid;

            public Item(Client client) {
                this.idOfClient = client.getIdOfClient();
                this.version = client.getClientRegistryVersion();
                this.firstName = client.getPerson().getFirstName();
                this.surname = client.getPerson().getSurname();
                this.secondName = client.getPerson().getSecondName();
                this.idDocument = client.getPerson().getIdDocument();
                this.address = client.getAddress();
                this.phone = client.getPhone();
                this.mobile = client.getMobile();
                this.fax = client.getFax();
                this.email = client.getEmail();
                this.contractState = client.getContractState();
                this.contractId = client.getContractId();
                this.freePayMaxCount = client.getFreePayMaxCount();
                this.categoriesDiscounts = client.getCategoriesDiscounts();
                this.clientGroup=client.getClientGroup();
                this.notifyViaEmail=client.isNotifyViaEmail();
                this.notifyViaSMS=client.isNotifyViaSMS();
                this.remarks = client.getRemarks();
                this.canConfirmGroupPayment = client.getCanConfirmGroupPayment();
                this.discountMode = client.getDiscountMode();
                this.guid = client.getClientGUID();
                if (this.clientGroup!=null) this.clientGroup.getGroupName(); // lazy load
            }

            public ClientGroup getClientGroup(){
                return clientGroup;
            }

            public long getIdOfClient() {
                return idOfClient;
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

            public int getContractState() {
                return contractState;
            }

            public Integer getFreePayMaxCount() {
                return freePayMaxCount;
            }

            public String getCategoriesDiscounts() {
                return categoriesDiscounts;
            }

            public int getDiscountMode() {
                return discountMode;
            }

            public boolean isCanConfirmGroupPayment() {
                return canConfirmGroupPayment;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("CC");
                element.setAttribute("IdOfClient", Long.toString(this.idOfClient));
                element.setAttribute("ContractId", Long.toString(this.contractId));
                element.setAttribute("Version", Long.toString(this.version));
                element.setAttribute("FirstName", this.firstName);
                element.setAttribute("Surname", this.surname);
                element.setAttribute("SecondName", this.secondName);
                element.setAttribute("IDDocument", this.idDocument);
                element.setAttribute("Address", this.address);
                element.setAttribute("Phone", this.phone);
                element.setAttribute("Mobile", this.mobile);
                element.setAttribute("Fax", this.fax);
                element.setAttribute("NotifyViaEmail", this.notifyViaEmail?"1":"0");
                element.setAttribute("NotifyViaSMS", this.notifyViaSMS?"1":"0");
                element.setAttribute("CanConfirmGroupPayment", this.canConfirmGroupPayment?"1":"0");
                element.setAttribute("Remarks", this.remarks);
                if (null != this.email) {
                    element.setAttribute("Email", this.email);
                }
                if (null != this.guid) {
                    element.setAttribute("GUID", this.guid);
                }
                element.setAttribute("ContractState", Integer.toString(this.contractState));
                if (null != this.freePayMaxCount) {
                    element.setAttribute("FreePayMaxCount", Integer.toString(this.freePayMaxCount));
                }
                element.setAttribute("DiscountMode", Integer.toString(this.discountMode));
                element.setAttribute("CategoriesDiscounts", this.categoriesDiscounts);

                if (this.clientGroup != null) {
			        element.setAttribute("GroupName", this.clientGroup.getGroupName());
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "idOfClient=" + idOfClient + ", version=" + version + ", firstName='" + firstName
                        + '\'' + ", surname='" + surname + '\'' + ", secondName='" + secondName + '\''
                        + ", idDocument='" + idDocument + '\'' + ", address='" + address + '\'' + ", phone='" + phone
                        + '\'' + ", mobile='" + mobile + '\'' + ", email='" + email + '\'' + ", contractState="
                        + contractState + ", freePayMaxCount=" + freePayMaxCount + ", categoriesDiscounts='"
                        + categoriesDiscounts + '\''+", clientGroup="+ clientGroup+'}';

            }
        }

        private final List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Iterator<Item> getItems() {
            return items.iterator();
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ClientRegistry");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ClientRegistry{" + "items=" + items + '}';
        }
    }

    public static class CorrectingNumbersOrdersRegistry {

        private  Long IdOfOrder;
        private  Long IdOfOrderDetail;
        private  Long IdOfEnterEvent;

        public CorrectingNumbersOrdersRegistry() {
            this.IdOfOrder = 0L;
            this.IdOfOrderDetail = 0L;
            this.IdOfEnterEvent = 0L;
        }

        public CorrectingNumbersOrdersRegistry(Long IdOfOrder, Long IdOfOrderDetail, Long IdOfEnterEvent) {
            this.IdOfOrder = IdOfOrder;
            this.IdOfOrderDetail = IdOfOrderDetail;
            this.IdOfEnterEvent = IdOfEnterEvent;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("CorrectingNumbersOrdersRegistry");
            element.setAttribute("IdOfOrder", Long.toString(this.IdOfOrder));
            element.setAttribute("IdOfOrderDetails", Long.toString(this.IdOfOrderDetail));
            element.setAttribute("IdOfEnterEvent", Long.toString(this.IdOfEnterEvent));
            return element;
        }

        @Override
        public String toString() {
            return "CorrectingNumbersOrdersRegistry{" + "IdOfOrder=" + IdOfOrder + ", IdOfOrderDetails=" + IdOfOrderDetail +", IdOfEnterEvent='"+IdOfEnterEvent + '}';
        }
    }

    public static class ResOrgStructure {

        private final int result;
        private final String error;

        public ResOrgStructure() {
            this.result = 0;
            this.error = null;
        }

        public ResOrgStructure(int result, String error) {
            this.result = result;
            this.error = error;
        }

        public int getResult() {
            return result;
        }

        public String getError() {
            return error;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResOrgStructure");
            element.setAttribute("Result", Integer.toString(this.result));
            if (null != this.error) {
                element.setAttribute("Error", this.error);
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResOrgStructure{" + "result=" + result + ", error='" + error + '\'' + '}';
        }
    }

    public static class ResMenuExchangeData {

        public static class Item {

            private String menuXmlData;

            Item(String menuXmlData) {
                this.menuXmlData = menuXmlData;
            }

            public String toString() {
                return "Item{" + "menuXmlData='" + menuXmlData + "'" + '}';
            }
        }

        private final List<Item> items = new LinkedList<Item>();

        public void addItem(String menuData) throws Exception {
            this.items.add(new Item(menuData));
        }

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("Menu");
            for (Item item : this.items) {
                Node node = ru.axetta.ecafe.processor.core.utils.XMLUtils
                        .importXmlFragmentToDocument(document, item.menuXmlData);
                element.appendChild(node);
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResMenuExchangeData{" + "items=" + items + '}';
        }
    }

    public static class ResMenu {

        public static class Item {

            public static class ResMenuDetail {

                private final String name;
                private final String group;
                private final String output;
                private final Long price;
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

                public ResMenuDetail(MenuDetail menuDetail) {
                    this.name = menuDetail.getMenuDetailName();
                    this.group = menuDetail.getGroupName();
                    this.output = menuDetail.getMenuDetailOutput();
                    this.price = menuDetail.getPrice();
                    this.protein = menuDetail.getProtein();
                    this.fat = menuDetail.getFat();
                    this.carbohydrates = menuDetail.getCarbohydrates();
                    this.calories = menuDetail.getCalories();
                    this.vitB1 = menuDetail.getVitB1();
                    this.vitC = menuDetail.getVitC();
                    this.vitA = menuDetail.getVitA();
                    this.vitE = menuDetail.getVitE();
                    this.minCa = menuDetail.getMinCa();
                    this.minP = menuDetail.getMinP();
                    this.minMg = menuDetail.getMinMg();
                    this.minFe = menuDetail.getCalories();
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

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("ML");
                    element.setAttribute("Name", this.name);
                    element.setAttribute("Group", this.group);
                    element.setAttribute("Output", this.output);
                    element.setAttribute("Price", this.price.toString());
                    setMinorComponent(element, "Protein", this.protein);
                    setMinorComponent(element, "Fat", this.fat);
                    setMinorComponent(element, "Carbohydrates", this.carbohydrates);
                    setMinorComponent(element, "Calories", this.calories);
                    setMinorComponent(element, "VitB1", this.vitB1);
                    setMinorComponent(element, "VitC", this.vitC);
                    setMinorComponent(element, "VitA", this.vitA);
                    setMinorComponent(element, "VitE", this.vitE);
                    setMinorComponent(element, "MinCa", this.minCa);
                    setMinorComponent(element, "MinP", this.minP);
                    setMinorComponent(element, "MinMg", this.minMg);
                    setMinorComponent(element, "MinFe", this.minFe);
                    return element;
                }

                private static void setMinorComponent(Element element, String attributeName, Double value)
                        throws Exception {
                    Integer numericValue = convertMinorComponentAmountToLong(value);
                    if (null != numericValue) {
                        element.setAttribute(attributeName, numericValue.toString());
                    }
                }

                private static Integer convertMinorComponentAmountToLong(Double value) throws Exception {
                    if (null == value) {
                        return null;
                    } else {
                        return (int) (value * 100);
                    }
                }

                @Override
                 public String toString() {
                    return "ResMenuDetail{" + "name='" + name + '\'' + ", group='" + group + '\'' + ", output='"
                            + output + '\'' + ", price=" + price + ", protein=" + protein + ", fat=" + fat
                            + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1=" + vitB1
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + '}';
                }
            }

            private final Date date;
            private final List<ResMenuDetail> resMenuDetails;

            public Item(Menu menu) throws Exception {
                this.date = menu.getMenuDate();
                this.resMenuDetails = new LinkedList<ResMenuDetail>();
                for (MenuDetail menuDetail : menu.getMenuDetails()) {
                    this.resMenuDetails.add(new ResMenuDetail(menuDetail));
                }
            }

            public Date getDate() {
                return date;
            }

            public Enumeration<ResMenuDetail> getMenuDetails() {
                return Collections.enumeration(resMenuDetails);
            }

            public Element toElement(Document document, DateFormat dateOnlyFormat) throws Exception {
                Element element = document.createElement("Date");
                element.setAttribute("Value", dateOnlyFormat.format(this.date));
                for (ResMenuDetail resMenuDetail : this.resMenuDetails) {
                    element.appendChild(resMenuDetail.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "date=" + date + ", resMenuDetails=" + resMenuDetails + '}';
            }
        }

        private final List<Item> items = new LinkedList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
        }

        public Element toElement(Document document, DateFormat dateOnlyFormat) throws Exception {
            Element element = document.createElement("Menu");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document, dateOnlyFormat));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ReqMenu{" + "items=" + items + '}';
        }
    }

    public static class ResDiary {

        private final int result;
        private final String error;

        public ResDiary() {
            this.result = 0;
            this.error = null;
        }

        public ResDiary(int result, String error) {
            this.result = result;
            this.error = error;
        }

        public int getResult() {
            return result;
        }

        public String getError() {
            return error;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResDiary");
            element.setAttribute("Result", Integer.toString(this.result));
            if (null != this.error) {
                element.setAttribute("Error", this.error);
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResDiary{" + "result=" + result + ", error='" + error + '\'' + '}';
        }
    }

    public static class ResEnterEvents {

        public static class Item {

            private final long idOfEnterEvent;
            private final int resEvent;
            private final String error;
            public static final int RC_OK = 0, RC_EVENT_EXISTS_WITH_DIFFERENT_ATTRIBUTES=2, RC_CLIENT_NOT_FOUND=3;

            public Item(long idOfEnterEvent, int resEvent, String error) {
                this.idOfEnterEvent = idOfEnterEvent;
                this.resEvent = resEvent;
                this.error = error;
            }

            public long getIdOfEnterEvent() {
                return idOfEnterEvent;
            }

            public int getResEvent() {
                return resEvent;
            }

            public String getError() {
                return error;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("REE");
                element.setAttribute("IdOfEnterEvent", Long.toString(this.idOfEnterEvent));
                element.setAttribute("ResEvent", Long.toString(this.resEvent));
                if (null != this.error) {
                    element.setAttribute("Error", this.error);
                }

                return element;
            }

            @Override
            public String toString() {
                return "Item{" + "idOfEnterEvent=" + idOfEnterEvent + ", resEvent=" + resEvent + ", error='" + error
                        + '\'' + '}';
            }
        }

        private final List<Item> items = new LinkedList<Item>();

        public void addItem(Item item) {
            this.items.add(item);
        }

        public Enumeration<Item> getItems() {
            return Collections.enumeration(items);
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResEnterEvents");
            for (Item item : this.items) {
                element.appendChild(item.toElement(document));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResEnterEvents{" + "items=" + items + '}';
        }
    }

    public static class ResCategoriesDiscountsAndRules {
        public static class DCI {
            private long idOfCategoryDiscount;
            private String categoryName;
            private Integer categoryType;

            private String discountRules;

            public DCI(long idOfCategoryDiscount, String categoryName, Integer categoryType, String discountRules) {
                this.idOfCategoryDiscount = idOfCategoryDiscount;
                this.categoryName = categoryName;
                this.discountRules = discountRules;
                this.categoryType = categoryType;
            }

            public long getIdOfCategoryDiscount() {
                return idOfCategoryDiscount;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public Integer getCategoryType() {
                return categoryType;
            }

            public String getDiscountRules() {
                return discountRules;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("DCI");
                element.setAttribute("IdOfCategoryDiscount", Long.toString(this.idOfCategoryDiscount));
                element.setAttribute("CategoryName", this.categoryName);
                element.setAttribute("CategoryType", Integer.toString(this.categoryType));
                return element;
            }

            @Override
            public String toString() {
                return "DCI{" + "idOfCategoryDiscount=" + idOfCategoryDiscount + ", categoryName='" + categoryName
                        + ", discountRules='" + discountRules + '\'' + '}';
            }
        }

        public static class DCRI {
            private long idOfRule;
            //private long idOfCategoryDiscount;
            private String description;
            private int complex0;
            private int complex1;
            private int complex2;
            private int complex3;
            private int complex4;
            private int complex5;
            private int complex6;
            private int complex7;
            private int complex8;
            private int complex9;
            private int priority;
            private String categoryDiscounts;
            private Boolean operationor;
            private String complexesMap;

            public String getComplexesMap() {
                return complexesMap;
            }

            public void setComplexesMap(String complexesMap) {
                this.complexesMap = complexesMap;
            }

            public Boolean getOperationor() {
                return operationor;
            }

            public void setOperationor(Boolean operationor) {
                this.operationor = operationor;
            }

            public String getCategoryDiscounts() {
                return categoryDiscounts;
            }

            public int getPriority() {
                return priority;
            }
            //

            public DCRI(DiscountRule discountRule){
                this.idOfRule = discountRule.getIdOfRule();
                this.description = discountRule.getDescription();
                this.categoryDiscounts = discountRule.getCategoryDiscounts();
                this.complex0 = discountRule.getComplex0();
                this.complex1 = discountRule.getComplex1();
                this.complex2 = discountRule.getComplex2();
                this.complex3 = discountRule.getComplex3();
                this.complex4 = discountRule.getComplex4();
                this.complex5 = discountRule.getComplex5();
                this.complex6 = discountRule.getComplex6();
                this.complex7 = discountRule.getComplex7();
                this.complex8 = discountRule.getComplex8();
                this.complex9 = discountRule.getComplex9();
                this.priority = discountRule.getPriority();
                this.operationor = discountRule.getOperationOr();
                this.complexesMap = discountRule.getComplexesMap();
            }


            public DCRI(long idOfRule, String description, String categoryDiscounts, int complex0, int complex1,
                    int complex2, int complex3, int complex4, int complex5, int complex6, int complex7, int complex8,
                    int complex9, int priority, Boolean operationor, String complexesMap) {
                this.idOfRule = idOfRule;
                this.description = description;
                this.categoryDiscounts = categoryDiscounts;
                this.complex0 = complex0;
                this.complex1 = complex1;
                this.complex2 = complex2;
                this.complex3 = complex3;
                this.complex4 = complex4;
                this.complex5 = complex5;
                this.complex6 = complex6;
                this.complex7 = complex7;
                this.complex8 = complex8;
                this.complex9 = complex9;
                this.priority = priority;
                this.operationor = operationor;
                this.complexesMap = complexesMap;
            }

            public long getIdOfRule() {
                return idOfRule;
            }

            public String getDescription() {
                return description;
            }

            public int getComplex0() {
                return complex0;
            }

            public int getComplex1() {
                return complex1;
            }

            public int getComplex2() {
                return complex2;
            }

            public int getComplex3() {
                return complex3;
            }

            public int getComplex4() {
                return complex4;
            }

            public int getComplex5() {
                return complex5;
            }

            public int getComplex6() {
                return complex6;
            }

            public int getComplex7() {
                return complex7;
            }

            public int getComplex8() {
                return complex8;
            }

            public int getComplex9() {
                return complex9;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("DCRI");
                element.setAttribute("IdOfRule", Long.toString(this.idOfRule));
                element.setAttribute("Description", this.description);
                element.setAttribute("CategoriesDiscounts", this.categoryDiscounts);
                element.setAttribute("Complex0", Integer.toString(this.complex0));
                element.setAttribute("Complex1", Integer.toString(this.complex1));
                element.setAttribute("Complex2", Integer.toString(this.complex2));
                element.setAttribute("Complex3", Integer.toString(this.complex3));
                element.setAttribute("Complex4", Integer.toString(this.complex4));
                element.setAttribute("Complex5", Integer.toString(this.complex5));
                element.setAttribute("Complex6", Integer.toString(this.complex6));
                element.setAttribute("Complex7", Integer.toString(this.complex7));
                element.setAttribute("Complex8", Integer.toString(this.complex8));
                element.setAttribute("Complex9", Integer.toString(this.complex9));
                element.setAttribute("Priority", Integer.toString(this.priority));
                element.setAttribute("OperationOr", Boolean.toString(this.operationor));
                if(StringUtils.isNotEmpty(complexesMap)){
                    element.setAttribute("ComplexesMap", this.complexesMap);
                }
                return element;
            }

            @Override
            public String toString() {
                return "DCRI{" + "idOfRule=" + idOfRule + ", categoriesDiscounts='" + categoryDiscounts + '\''
                        + ", description='" + description + '\'' + ", complex0=" + complex0 + ", complex1=" + complex1
                        + ", complex2=" + complex2 + ", complex3=" + complex3 + ", complex4=" + complex4 + ", complex5="
                        + complex5 + ", complex6=" + complex6 + ", complex7=" + complex7 + ", complex8=" + complex8
                        + ", complex9=" + complex9 + ", priority=" + priority +", operationor=" +operationor
                        +", complexesMap=\'" +complexesMap  +'\'' +'}';
            }
        }

        private final List<DCI> dcis = new LinkedList<DCI>();
        private final List<DCRI> dcris = new LinkedList<DCRI>();

        public void addDCI(DCI dci) {
            this.dcis.add(dci);
        }

        public void addDCRI(DCRI dcri) {
            this.dcris.add(dcri);
        }

        public Enumeration<DCI> getDcis() {
            return Collections.enumeration(dcis);
        }

        public Enumeration<DCRI> getDcris() {
            return Collections.enumeration(dcris);
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResCategoriesDiscountsAndRules");
            for (DCI dci : this.dcis) {
                element.appendChild(dci.toElement(document));
            }
            for (DCRI dcri : this.dcris) {
                element.appendChild(dcri.toElement(document));
            }
            return element;
        }

        @Override
        public String toString() {
            return "ResCategoriesDiscountsAndRules{" + "dcis=" + dcis + ", dcris=" + dcris + '}';
        }
    }

    private final SyncType syncType;

    public SyncType getSyncType() {
        return syncType;
    }

    private final Long idOfOrg;
    private final String orgName;
    private final Long idOfPacket;
    private final Long protoVersion;
    private final Date time;
    private final String options;
    private final AccRegistry accRegistry;
    private final ResPaymentRegistry resPaymentRegistry;
    private final AccIncRegistry accIncRegistry;
    private final ClientRegistry clientRegistry;
    private final ResOrgStructure resOrgStructure;
    private final ResMenuExchangeData resMenuExchangeData;
    private final ResDiary resDiary;
    private final String message;
    private final ResEnterEvents resEnterEvents;
    private final ResTempCardsOperations resTempCardsOperations;
    private final TempCardOperationData tempCardOperationData;
    private final ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules;
    private final ComplexRoles complexRoles;
    private final CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry;
    private final OrgOwnerData orgOwnerData;
    private final QuestionaryData questionaryData;
    private final GoodsBasicBasketData goodsBasicBasketData;
    private final Manager manager;
    private final DirectiveElement directiveElement;

    public CorrectingNumbersOrdersRegistry getCorrectingNumbersOrdersRegistry() {
        return correctingNumbersOrdersRegistry;
    }

    public SyncResponse(SyncType syncType, Long idOfOrg, String orgName, Long idOfPacket, Long protoVersion, Date time,
            String options, AccRegistry accRegistry, ResPaymentRegistry resPaymentRegistry, AccIncRegistry accIncRegistry,
            ClientRegistry clientRegistry, ResOrgStructure resOrgStructure, ResMenuExchangeData resMenuExchangeData,
            ResDiary resDiary, String message, ResEnterEvents resEnterEvents,
            ResTempCardsOperations resTempCardsOperations, TempCardOperationData tempCardOperationData,
            ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules, ComplexRoles complexRoles,
            CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry, Manager manager, OrgOwnerData orgOwnerData,
            QuestionaryData questionaryData, GoodsBasicBasketData goodsBasicBasketData,
            DirectiveElement directiveElement) {
        this.syncType = syncType;
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.idOfPacket = idOfPacket;
        this.protoVersion = protoVersion;
        this.time = time;
        this.options = options;
        this.accRegistry = accRegistry;
        this.accIncRegistry = accIncRegistry;
        this.resPaymentRegistry = resPaymentRegistry;
        this.clientRegistry = clientRegistry;
        this.resOrgStructure = resOrgStructure;
        this.resMenuExchangeData = resMenuExchangeData;
        this.resDiary = resDiary;
        this.message = message;
        this.resEnterEvents = resEnterEvents;
        this.resTempCardsOperations = resTempCardsOperations;
        this.tempCardOperationData = tempCardOperationData;
        this.resCategoriesDiscountsAndRules = resCategoriesDiscountsAndRules;
        this.complexRoles = complexRoles;
        this.correctingNumbersOrdersRegistry = correctingNumbersOrdersRegistry;
        this.manager = manager;
        this.orgOwnerData = orgOwnerData;
        this.questionaryData = questionaryData;
        this.goodsBasicBasketData = goodsBasicBasketData;
        this.directiveElement = directiveElement;
    }

    public Document toDocument() throws Exception {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateOnlyFormat.setTimeZone(utcTimeZone);

        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element dataElement = document.createElement("Data");
        Element bodyElement = document.createElement("Body");

        Element ecafeEnvelopeElement = document.createElement("CafeteriaExchange");
        ecafeEnvelopeElement.setAttribute("IdOfOrg", this.idOfOrg.toString());
        ecafeEnvelopeElement.setAttribute("Org", this.orgName);
        if (this.idOfPacket != null) {
            ecafeEnvelopeElement.setAttribute("IdOfPacket", this.idOfPacket.toString());
        }
        ecafeEnvelopeElement.setAttribute("Version", this.protoVersion.toString());
        ecafeEnvelopeElement.setAttribute("Date", timeFormat.format(this.time));
        ecafeEnvelopeElement.setAttribute("Options", this.options);
        //ecafeEnvelopeElement.setAttribute("Type", TYPE_NAMES[type]);
        ecafeEnvelopeElement.setAttribute("Type",syncType.toString());

        // ResPaymentRegistry
        if (null != resPaymentRegistry) {
            ecafeEnvelopeElement.appendChild(resPaymentRegistry.toElement(document));
        }

        // AccIncRegistry
        if (null != accIncRegistry) {
            ecafeEnvelopeElement.appendChild(accIncRegistry.toElement(document, dateFormat, timeFormat));
        }

        // AccRegistry
        if (null != accRegistry) {
            ecafeEnvelopeElement.appendChild(accRegistry.toElement(document, dateFormat, timeFormat));
        }

        // ClientRegistry
        if (null != clientRegistry) {
            ecafeEnvelopeElement.appendChild(clientRegistry.toElement(document));
        }

        // ResOrgStructure
        if (null != resOrgStructure) {
            ecafeEnvelopeElement.appendChild(resOrgStructure.toElement(document));
        }

        // Menu
        if (null != resMenuExchangeData) {
            ecafeEnvelopeElement.appendChild(resMenuExchangeData.toElement(document));
        }

        if (resDiary != null) {
            ecafeEnvelopeElement.appendChild(resDiary.toElement(document));
        }

        if (StringUtils.isNotEmpty(this.message)) {
            Element messageElement = document.createElement("Message");
            messageElement.appendChild(document.createTextNode(this.message));
            ecafeEnvelopeElement.appendChild(messageElement);
        }

        // ResEnterEvents
        if (resEnterEvents != null) {
            ecafeEnvelopeElement.appendChild(resEnterEvents.toElement(document));
        }

        // ResTempCardsOperations
        if (resTempCardsOperations != null) {
            ecafeEnvelopeElement.appendChild(resTempCardsOperations.toElement(document));
        }

        // TempCardOperationData
        if (tempCardOperationData != null) {
            ecafeEnvelopeElement.appendChild(tempCardOperationData.toElement(document));
        }

        // ResLibraryData
        //if (resLibraryData != null) {
        //    ecafeEnvelopeElement.appendChild(resLibraryData.toElement(document));
        //}
        //
        //// ResLibraryData2
        //if (resLibraryData2 != null) {
        //    ecafeEnvelopeElement.appendChild(resLibraryData2.toElement(document));
        //}

        // ResCategoriesDiscountsAndRules
        if (resCategoriesDiscountsAndRules != null) {
            ecafeEnvelopeElement.appendChild(resCategoriesDiscountsAndRules.toElement(document));
        }

        // ComplexRoles
        if (complexRoles != null) {
            ecafeEnvelopeElement.appendChild(complexRoles.toElement(document));
        }

        // CorrectingNumbersOrdersRegistry
        if (correctingNumbersOrdersRegistry != null){
            ecafeEnvelopeElement.appendChild(correctingNumbersOrdersRegistry.toElement(document));
        }

        if(manager != null){
            ecafeEnvelopeElement.appendChild(manager.toElement(document));
        }

        if(orgOwnerData != null){
            ecafeEnvelopeElement.appendChild(orgOwnerData.toElement(document));
        }

        if(questionaryData != null) {
            ecafeEnvelopeElement.appendChild(questionaryData.toElement(document));
        }

        if(goodsBasicBasketData != null) {
            ecafeEnvelopeElement.appendChild(goodsBasicBasketData.toElement(document));
        }

        if(directiveElement != null) {
            ecafeEnvelopeElement.appendChild(directiveElement.toElement(document));
        }

        bodyElement.appendChild(ecafeEnvelopeElement);
        dataElement.appendChild(bodyElement);
        document.appendChild(dataElement);
        return document;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public Long getProtoVersion() {
        return protoVersion;
    }

    public Date getTime() {
        return time;
    }

    public String getOptions() {
        return options;
    }

    public AccRegistry getAccRegistry() {
        return accRegistry;
    }

    public ResPaymentRegistry getResAccRegistry() {
        return resPaymentRegistry;
    }

    public ClientRegistry getClientRegistry() {
        return clientRegistry;
    }

    public ResOrgStructure getResOrgStructure() {
        return resOrgStructure;
    }

    public ResMenuExchangeData getResMenuExchangeData() {
        return resMenuExchangeData;
    }

    public String getMessage() {
        return message;
    }

    public ResEnterEvents getResEnterEvents() {
        return resEnterEvents;
    }

    public Manager getManager(){
        return manager;
    }

    public OrgOwnerData getOrgOwnerData() {
        return orgOwnerData;
    }

    public QuestionaryData getQuestionaryData() {
        return questionaryData;
    }

    public GoodsBasicBasketData getGoodsBasicBasketData() {
        return goodsBasicBasketData;
    }

    @Override
    public String toString() {
        return "SyncResponse{" + "idOfOrg=" + idOfOrg + ", idOfPacket=" + idOfPacket + ", protoVersion=" + protoVersion
                + ", time=" + time + ", options='" + options + '\'' + ", accRegistry=" + accRegistry
                + ", resPaymentRegistry=" + resPaymentRegistry + ", clientRegistry=" + clientRegistry
                + ", resOrgStructure=" + resOrgStructure + ", resMenuExchangeData=" + resMenuExchangeData
                + ", resEnterEvents=" + resEnterEvents + '}';
    }

}

/* public static class ResLibraryData {

        private Circulations circulations = new Circulations();
        private Publications publications = new Publications();

        public Circulations getCirculations() {
            return circulations;
        }

        public Publications getPublications() {
            return publications;
        }

        public void setCirculations(Circulations circulations) {
            this.circulations = circulations;
        }

        public void setPublications(Publications publications) {
            this.publications = publications;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResLibraryData");
            element.appendChild(circulations.toElement(document));
            element.appendChild(publications.toElement(document));
            return element;
        }

        public static class Circulations {

            public static class Circulation {

                private final long idOfCirculation;
                private final long version;
                private final int errCode;
                private final String error;

                public Circulation(long idOfCirculation, long version, int errCode, String error) {
                    this.idOfCirculation = idOfCirculation;
                    this.version = version;
                    this.errCode = errCode;
                    this.error = error;
                }

                public long getIdOfCirculation() {
                    return idOfCirculation;
                }

                public long getVersion() {
                    return version;
                }

                public int getErrCode() {
                    return errCode;
                }

                public String getError() {
                    return error;
                }

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("Circulation");
                    element.setAttribute("IdOfCirculation", Long.toString(this.idOfCirculation));
                    element.setAttribute("Version", Long.toString(this.version));
                    element.setAttribute("ErrCode", Long.toString(this.errCode));
                    if (null != this.error) {
                        element.setAttribute("Error", this.error);
                    }

                    return element;
                }

                @Override
                public String toString() {
                    return "Circulation{" + "idOfCirculation=" + idOfCirculation + ", version=" + version + ", errCode="
                            + errCode + ", error='" + error + '\'' + '}';
                }
            }

            private final List<Circulation> circulationList = new LinkedList<Circulation>();

            public void addItem(Circulation circulation) {
                this.circulationList.add(circulation);
            }

            public Enumeration<Circulation> getCirculationList() {
                return Collections.enumeration(circulationList);
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("Circulations");
                for (Circulation circulation : this.circulationList) {
                    element.appendChild(circulation.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Circulations{" + "circulationList=" + circulationList + '}';
            }
        }

        public static class Publications {

            public static class Publication {

                private final long idOfPublication;
                private final long version;
                private final int errCode;
                private final String error;

                public Publication(long idOfPublication, long version, int errCode, String error) {
                    this.idOfPublication = idOfPublication;
                    this.version = version;
                    this.errCode = errCode;
                    this.error = error;
                }

                public long getIdOfPublication() {
                    return idOfPublication;
                }

                public long getVersion() {
                    return version;
                }

                public int getErrCode() {
                    return errCode;
                }

                public String getError() {
                    return error;
                }

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("Publication");
                    element.setAttribute("IdOfPublication", Long.toString(this.idOfPublication));
                    element.setAttribute("Version", Long.toString(this.version));
                    element.setAttribute("ErrCode", Long.toString(this.errCode));
                    if (null != this.error) {
                        element.setAttribute("Error", this.error);
                    }

                    return element;
                }

                @Override
                public String toString() {
                    return "Publication{" + "idOfPublication=" + idOfPublication + ", version=" + version + ", errCode="
                            + errCode + ", error='" + error + '\'' + '}';
                }
            }

            private final List<Publication> publicationList = new LinkedList<Publication>();

            public void addItem(Publication publication) {
                this.publicationList.add(publication);
            }

            public Enumeration<Publication> getPublicationList() {
                return Collections.enumeration(publicationList);
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("Publications");
                for (Publication publication : this.publicationList) {
                    element.appendChild(publication.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Publications{" + "publicationList=" + publicationList + '}';
            }
        }

        @Override
        public String toString() {
            return "ResLibraryData{" + "circulations=" + circulations + ", publications=" + publications + '}';
        }
    }

    public static class ResLibraryData2 {

        private Publs publs = new Publs();
        private Circuls circuls = new Circuls();
        private CommonUpdate commonUpdate;
        private int status;

        public Publs getPubls() {
            return publs;
        }

        public void setPubls(Publs publs) {
            this.publs = publs;
        }

        public Circuls getCirculs() {
            return circuls;
        }

        public void setCirculs(Circuls circuls) {
            this.circuls = circuls;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public CommonUpdate getCommonUpdate() {
            return commonUpdate;
        }

        public void setCommonUpdate(CommonUpdate commonUpdate) {
            this.commonUpdate = commonUpdate;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("ResLibraryData2");
            element.appendChild(publs.toElement(document));
            element.appendChild(circuls.toElement(document));
            element.appendChild(commonUpdate.toElement(document));
            return element;
        }

        public static class Publs {

            public static class Publ {

                private final long idOfPubl;
                private final int errCode;
                private final String error;

                public Publ(long idOfPubl, int errCode, String error) {
                    this.idOfPubl = idOfPubl;
                    this.errCode = errCode;
                    this.error = error;
                }

                public long getIdOfPublication() {
                    return idOfPubl;
                }

                public int getErrCode() {
                    return errCode;
                }

                public String getError() {
                    return error;
                }

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("Publ");
                    element.setAttribute("IdOfPublication", Long.toString(this.idOfPubl));
                    element.setAttribute("ErrCode", Long.toString(this.errCode));
                    if (null != this.error) {
                        element.setAttribute("Error", this.error);
                    }

                    return element;
                }


            }

            private final List<Publ> publList = new LinkedList<Publ>();

            public void addItem(Publ publ) {
                this.publList.add(publ);
            }

            public Enumeration<Publ> getPublList() {
                return Collections.enumeration(publList);
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("Publs");
                for (Publ publ : this.publList) {
                    element.appendChild(publ.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Publs{" + "publList=" + publList + '}';
            }
        }

        public static class Circuls {

            private int result = 0;

            public static class Circul {

                private final Long idOfCircul;
                private final int errCode;
                private final String error;
                private final long idOfClient;
                private final long idOfPublication;
                private final long idOfOrg;

                public Circul(Long idOfCircul, int errCode, String error, long idOfClient, long idOfPublication, long idOfOrg) {
                    this.idOfCircul = idOfCircul;
                    this.errCode = errCode;
                    this.error = error;
                    this.idOfClient = idOfClient;
                    this.idOfPublication = idOfPublication;
                    this.idOfOrg = idOfOrg;
                }

                public long getIdOfCircul() {
                    return idOfCircul;
                }

                public int getErrCode() {
                    return errCode;
                }

                public String getError() {
                    return error;
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

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("Circul");
                    element.setAttribute("IdOfCirculation", Long.toString(this.idOfCircul));
                    element.setAttribute("ErrCode", Long.toString(this.errCode));
                    if (null != this.error) {
                        element.setAttribute("Error", this.error);
                    }
                    element.setAttribute("IdOfClient", Long.toString(this.idOfClient));
                    element.setAttribute("IdOfPublication", Long.toString(this.idOfPublication));
                    element.setAttribute("IdOfOrg", Long.toString(this.idOfOrg));
                    return element;
                }

                @Override
                public String toString() {
                    return "Circul{" +
                            "idOfCircul=" + idOfCircul +
                            ", errCode=" + errCode +
                            ", error='" + error + '\'' +
                            ", idOfClient=" + idOfClient +
                            ", idOfPublication=" + idOfPublication +
                            ", idOfOrg=" + idOfOrg +
                            '}';
                }
            }

            private final List<Circul> circulList = new LinkedList<Circul>();

            public int getResult() {
                return result;
            }

            public void setResult(int result) {
                this.result = result;
            }

            public void addItem(Circul circul) {
                this.circulList.add(circul);
            }

            public Enumeration<Circul> getCirculList() {
                return Collections.enumeration(circulList);
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("Circuls");
                for (Circul circul : this.circulList) {
                    element.appendChild(circul.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "Circuls{" + "circulList=" + circulList + '}';
            }
        }

        public static class CommonUpdate {

            private final long upToVersion;

            public CommonUpdate(long upToVersion) {
                this.upToVersion = upToVersion;

            }

            public static class Publ {

                private Long idofpubl;
                private String isbn;
                private String data;
                private String author;
                private String title;
                private String title2;
                private String publicationdate;
                private String publisher;
                private String hash;
                private long version;

                public Publ() {
                }

                public Publ(long idofpubl, String isbn, String data, String author, String title, String title2, String publicationDate,
                        String publisher, String hash, long version) {
                    this.idofpubl = idofpubl;
                    this.data = data;
                    this.author = author;
                    this.title = title;
                    this.title2 = title2;
                    this.publicationdate = publicationDate;
                    this.publisher = publisher;
                    this.hash = hash;
                    this.version = version;
                }

                public Long getIdofpubl() {
                    return idofpubl;
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

                public String getPublicationdate() {
                    return publicationdate;
                }

                public String getPublisher() {
                    return publisher;
                }

                public String getHash() {
                    return hash;
                }

                public long getVersion() {
                    return version;
                }

                public Element toElement(Document document) throws Exception {
                    Element element = document.createElement("Publ");
                    element.setAttribute("IdOfPublication", Long.toString(this.idofpubl));
                    element.setAttribute("ISBN", this.isbn);
                    element.setAttribute("Data", this.data);
                    element.setAttribute("Author", this.author);
                    element.setAttribute("Title", this.title);
                    element.setAttribute("Title2", this.title2);
                    element.setAttribute("PublicationDate", this.publicationdate);
                    element.setAttribute("Publisher", this.hash);
                    element.setAttribute("Version", Long.toString(this.version));
                    return element;
                }

                @Override
                public String toString() {
                    return "Publ{" +
                            "idofpubl=" + idofpubl +
                            ", isbn='" + isbn + '\'' +
                            ", data='" + data + '\'' +
                            ", author='" + author + '\'' +
                            ", title='" + title + '\'' +
                            ", title2='" + title2 + '\'' +
                            ", publicationdate='" + publicationdate + '\'' +
                            ", publisher='" + publisher + '\'' +
                            ", hash='" + hash + '\'' +
                            ", version=" + version +
                            '}';
                }
            }

            private final List<Publ> publList = new LinkedList<Publ>();

            public void addItem(Publ publ) {
                this.publList.add(publ);
            }

            public void addAll(List<Publ> publs) {
                publList.addAll(publs);
            }

            public Enumeration<Publ> getPublList() {
                return Collections.enumeration(publList);
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("CommonUpdate");
                for (Publ publ : this.publList) {
                    element.appendChild(publ.toElement(document));
                }
                return element;
            }

            @Override
            public String toString() {
                return "CommonUpdate{" +
                        "publList=" + publList +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResLibraryData2{" +
                    "publs=" + publs +
                    ", circuls=" + circuls +
                    ", commonUpdate=" + commonUpdate +
                    '}';
        }
    }*/