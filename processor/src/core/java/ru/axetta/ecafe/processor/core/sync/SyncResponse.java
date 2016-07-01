/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.ResCategoriesDiscountsAndRules;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardOperationData;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoles;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportData;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsData;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.ResMigrants;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.ResPaymentRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalData;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ResReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.ResAccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.ResSpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesData;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ResZeroTransactions;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactionData;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.response.*;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountsRegistry;

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

    public static class AccRegistry {

        public static class Item {

            private final long cardNo;
            private final Long cardPrintedNo;
            private final int cardType;
            private final Long idOfClient;
            private final Date updateTime;
            private final long balance;
            private final Long subBalance1;
            private final long limit;
            private final long expenditureLimit;
            private final int state;
            private final String lockReason;
            private final Date validTime;
            private final Date issueTime;

            public Item(Card card) {
                this.cardNo = card.getCardNo();
                this.cardPrintedNo = card.getCardPrintedNo();
                this.cardType = card.getCardType();
                this.idOfClient = (card.getClient()!= null)?card.getClient().getIdOfClient():null;
                this.updateTime = card.getUpdateTime();
                Client client = card.getClient();
                if(client.getSubBalance1()==null){
                    this.subBalance1 = 0L;
                }else {
                    this.subBalance1 = client.getSubBalance1();
                }
                this.balance = client.getBalance() - this.subBalance1;
                this.limit = client.getLimit();
                this.expenditureLimit = client.getExpenditureLimit();
                this.state = card.getState();
                this.lockReason = card.getLockReason();
                this.validTime = card.getValidTime();
                this.issueTime = card.getIssueTime();
            }

            public Item(Client client, Card card) {
                this.cardNo = card.getCardNo();
                this.cardPrintedNo = card.getCardPrintedNo();
                this.cardType = card.getCardType();
                this.idOfClient = (card.getClient()!= null)?card.getClient().getIdOfClient():null;
                this.updateTime = card.getUpdateTime();
                //Client client = card.getClient();
                if(client.getSubBalance1()==null){
                    this.subBalance1 = 0L;
                }else {
                    this.subBalance1 = client.getSubBalance1();
                }
                this.balance = client.getBalance() - this.subBalance1;
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

            public Long cardPrintedNo() {
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

            public Long getSubBalance1() {
                return subBalance1;
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
                if(cardPrintedNo != null){
                    element.setAttribute("CardPrintedNo", Long.toString(cardPrintedNo));
                }
                element.setAttribute("CardType", Integer.toString(cardType));
                element.setAttribute("IdOfClient", idOfClient.toString());
                element.setAttribute("LastUpdate", timeFormat.format(updateTime));
                element.setAttribute("Balance", Long.toString(balance));
                element.setAttribute("SubBalance1", Long.toString(subBalance1));
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
                return "Item{" + "cardNo=" + cardNo + ", cardPrintedNo=" + cardPrintedNo +  ", cardType=" + cardType + ", lastUpdate=" + updateTime
                        + ", balance=" + balance + ", subBalance1=" + subBalance1 + ", limit=" + limit + ", expenditureLimit=" + expenditureLimit
                        + ", state=" + state + '}';
            }
        }

        private final List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) throws Exception {
            this.items.add(item);
        }

        public Iterator<Item> getItems() {
            return items.iterator();
        }

        public Integer getItemCounts() {
            return items.size();
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
            private final Long sumSubBalance1;

            public Item(long idOfPaymentOrder, long idOfClient, Date dateTime, long sum, Long sumSubBalance1) {
                this.idOfPaymentOrder = idOfPaymentOrder;
                this.idOfClient = idOfClient;
                this.dateTime = dateTime;
                this.sum = sum;
                this.sumSubBalance1 = sumSubBalance1;
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

            public Long getSumSubBalance1() {
                return sumSubBalance1;
            }

            //public long getSum() {
            //    return sum;
            //}

            public Element toElement(Document document, DateFormat timeFormat) throws Exception {
                Element element = document.createElement("AI");
                element.setAttribute("IdOfPaymentOrder", Long.toString(idOfPaymentOrder));
                element.setAttribute("IdOfClient", Long.toString(idOfClient));
                element.setAttribute("Date", timeFormat.format(dateTime));
                element.setAttribute("Sum", Long.toString(sum));
                if(sumSubBalance1==null){
                    element.setAttribute("SumSubBalance1", "0");
                } else {
                    element.setAttribute("SumSubBalance1", Long.toString(sumSubBalance1));
                }
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

        public Iterator<Item> getItems() {
            return items.iterator();
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

            private final long orgOwner;
            private final long idOfClient;
            private final long version;
            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String idDocument;
            private final String address;
            private final String phone;
            private final String mobile;
            private final String middleGroup;
            private final String email;
            private final String fax;
            private final int contractState;
            private final long contractId;
            private final Integer freePayMaxCount;
            private final String categoriesDiscounts;
            private final ClientGroup clientGroup;
            private final boolean notifyViaEmail;
            private final boolean notifyViaSMS;
            private final boolean notifyViaPUSH;
            private final String remarks;
            private final boolean canConfirmGroupPayment;
            private final int discountMode;
            private final String guid;
            private boolean tempClient;
            private int clientType;
            private final boolean isUseLastEEModeForPlan;
            private final Integer gender;
            private final Date birthDate;
            private final String benefitOnAdmission;


            public Item(Client client, int clientType) {
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
                this.categoriesDiscounts = client.getCategoriesDiscounts();
                this.clientGroup=client.getClientGroup();
                this.notifyViaEmail=client.isNotifyViaEmail();
                this.notifyViaSMS=client.isNotifyViaSMS();
                this.notifyViaPUSH=client.isNotifyViaPUSH();
                this.remarks = client.getRemarks();
                this.canConfirmGroupPayment = client.getCanConfirmGroupPayment();
                this.discountMode = client.getDiscountMode();
                this.guid = client.getClientGUID();
                this.clientType = clientType;
                if (this.clientGroup!=null) this.clientGroup.getGroupName(); // lazy load
                this.isUseLastEEModeForPlan = client.isUseLastEEModeForPlan()==null ? false : client.isUseLastEEModeForPlan();
                this.gender = client.getGender();
                this.birthDate = client.getBirthDate();
                this.benefitOnAdmission = client.getBenefitOnAdmission();
            }

            public Item(Client client, int clientType, boolean tempClient) {
                this(client, clientType);
                this.tempClient = tempClient;
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

            public String getMiddleGroup() {
                return middleGroup;
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

            public boolean isTempClient() {
                return tempClient;
            }

            public String getBenefitOnAdmission() {
                return benefitOnAdmission;
            }

            public Date getBirthDate() {
                return birthDate;
            }

            public int getGender() {
                return gender;
            }

            public Element toElement(Document document) throws Exception {
                Element element = document.createElement("CC");
                element.setAttribute("OrgOwner", Long.toString(this.orgOwner));
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
                element.setAttribute("MiddleGroup", this.middleGroup);
                element.setAttribute("Fax", this.fax);
                element.setAttribute("NotifyViaEmail", this.notifyViaEmail?"1":"0");
                element.setAttribute("NotifyViaSMS", this.notifyViaSMS?"1":"0");
                element.setAttribute("NotifyViaPUSH", this.notifyViaPUSH?"1":"0");
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
                if (this.tempClient) {
                    element.setAttribute("IsTempClient", "1");
                }
                if (this.clientGroup != null) {
			        element.setAttribute("GroupName", this.clientGroup.getGroupName());
                }
                element.setAttribute("ClientType", Integer.toString(this.clientType));
                element.setAttribute("IsUseLastEEModeForPlan", this.isUseLastEEModeForPlan?"1":"0");
                if (this.gender != null) {
                    element.setAttribute("Gender", String.valueOf(this.gender));
                }
                if (this.birthDate != null) {
                    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
                    element.setAttribute("BirthDate", timeFormat.format(this.birthDate));
                }
                if (this.benefitOnAdmission != null) {
                    element.setAttribute("BenefitOnAdmission", this.benefitOnAdmission);
                }
                return element;
            }

            @Override
            public String toString() {
                return "Item{"+ "orgOwner="+ orgOwner + ", idOfClient=" + idOfClient + ", version=" + version + ", firstName='" + firstName
                        + '\'' + ", surname='" + surname + '\'' + ", secondName='" + secondName + '\''
                        + ", idDocument='" + idDocument + '\'' + ", address='" + address + '\'' + ", phone='" + phone
                        + '\'' + ", mobile='" + mobile + '\'' + ", email='" + email + '\'' + ", contractState="
                        + contractState + ", freePayMaxCount=" + freePayMaxCount + ", categoriesDiscounts='"
                        + categoriesDiscounts + '\''+", clientGroup="+ clientGroup+'}';

            }
        }

        private final List<Item> items = new ArrayList<Item>();
        private final List<Long> activeClientsId = new ArrayList<Long>();

        public void addActiveClientId(Long id) {
            activeClientsId.add(id);
        }

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
            if (activeClientsId.size() != 0) {
                Element activeClientsElem = document.createElement("ActiveClients");
                for (Long id : activeClientsId) {
                    Element elem = document.createElement("Client");
                    elem.setAttribute("IdOfClient", id.toString());
                    activeClientsElem.appendChild(elem);
                }
                element.appendChild(activeClientsElem);
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
        private  Long IdOfOutcomeMigrRequests;

        public CorrectingNumbersOrdersRegistry() {
            this.IdOfOrder = 0L;
            this.IdOfOrderDetail = 0L;
            this.IdOfEnterEvent = 0L;
            this.IdOfOutcomeMigrRequests = 0L;
        }

        public CorrectingNumbersOrdersRegistry(Long IdOfOrder, Long IdOfOrderDetail, Long IdOfEnterEvent, Long IdOfOutcomeMigrRequests) {
            this.IdOfOrder = IdOfOrder;
            this.IdOfOrderDetail = IdOfOrderDetail;
            this.IdOfEnterEvent = IdOfEnterEvent;
            this.IdOfOutcomeMigrRequests = IdOfOutcomeMigrRequests;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("CorrectingNumbersOrdersRegistry");
            element.setAttribute("IdOfOrder", Long.toString(this.IdOfOrder));
            element.setAttribute("IdOfOrderDetails", Long.toString(this.IdOfOrderDetail));
            element.setAttribute("IdOfEnterEvent", Long.toString(this.IdOfEnterEvent));
            element.setAttribute("IdOfOutcomeMigrRequests", Long.toString(this.IdOfOutcomeMigrRequests));
            return element;
        }

        @Override
        public String toString() {
            return "CorrectingNumbersOrdersRegistry{" + "IdOfOrder=" + IdOfOrder + ", IdOfOrderDetails=" + IdOfOrderDetail +", IdOfEnterEvent='"+IdOfEnterEvent +
                    ", IdOfOutcomeMigrRequests=" + IdOfOutcomeMigrRequests + '}';
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

        public Iterator<Item> getItems() {
            return items.iterator();
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
                private final Double vitB2;
                private final Double vitC;
                private final Double vitA;
                private final Double vitE;
                private final Double minCa;
                private final Double minP;
                private final Double minMg;
                private final Double minFe;
                private final Double vitPp;

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
                    this.vitB2 = menuDetail.getVitB2();
                    this.vitC = menuDetail.getVitC();
                    this.vitA = menuDetail.getVitA();
                    this.vitE = menuDetail.getVitE();
                    this.minCa = menuDetail.getMinCa();
                    this.minP = menuDetail.getMinP();
                    this.minMg = menuDetail.getMinMg();
                    this.minFe = menuDetail.getMinFe();
                    this.vitPp = menuDetail.getVitPp();

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

                public Double getVitB2() {
                    return vitB2;
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

                public Double getVitPp() {
                    return vitPp;
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
                    setMinorComponent(element, "VitB2", this.vitB2);
                    setMinorComponent(element, "VitC", this.vitC);
                    setMinorComponent(element, "VitA", this.vitA);
                    setMinorComponent(element, "VitE", this.vitE);
                    setMinorComponent(element, "MinCa", this.minCa);
                    setMinorComponent(element, "MinP", this.minP);
                    setMinorComponent(element, "MinMg", this.minMg);
                    setMinorComponent(element, "MinFe", this.minFe);
                    setMinorComponent(element, "VitPp", this.vitPp);
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
                            + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1=" + vitB1 + ", vitB2=" + vitB2
                            + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP="
                            + minP + ", minMg=" + minMg + ", minFe=" + minFe + ", vitPp=" + vitPp + '}';
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

            public Iterator<ResMenuDetail> getMenuDetails() {
                return resMenuDetails.iterator();
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

        public Iterator<Item> getItems() {
            return items.iterator();
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
            public static final int RC_OK = 0;
            public static final int RC_EVENT_EXISTS_WITH_DIFFERENT_ATTRIBUTES=2;
            public static final int RC_CLIENT_NOT_FOUND=3;

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

        public Iterator<Item> getItems() {
            return items.iterator();
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

    private final SyncType syncType;
    private final Long idOfOrg;
    private final String orgName;
    private final OrganizationType organizationType;
    private final String directorName;
    private final Long idOfPacket;
    private final Long protoVersion;
    private final Date time;
    private final String options;
    private final AccRegistry accRegistry;
    private final ResPaymentRegistry resPaymentRegistry;
    private final ResAccountOperationsRegistry resAccountOperationsRegistry;
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
    private final ResultClientGuardian resultClientGuardian;
    private final ClientGuardianData clientGuardians;
    private final AccRegistryUpdate accRegistryUpdate;
    private final ProhibitionsMenu prohibitionsMenu;
    private final ResCardsOperationsRegistry resCardsOperationsRegistry;
    private final AccountsRegistry accountsRegistry;
    private final OrganizationStructure organizationStructure;
    private final ResReestrTaloonApproval resReestrTaloonApproval;
    private final ReestrTaloonApprovalData reestrTaloonApprovalData;
    private final OrganizationComplexesStructure organizationComplexesStructure;
    private final ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportData interactiveReportData;
    private final ZeroTransactionData zeroTransactionData;
    private final ResZeroTransactions resZeroTransactions;
    private final SpecialDatesData specialDatesData;
    private final ResSpecialDates resSpecialDates;
    private final MigrantsData migrantsData;
    private final ResMigrants resMigrants;

    private List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();


    public SyncResponse(SyncType syncType, Long idOfOrg, String orgName, OrganizationType organizationType,
            String directorName, Long idOfPacket, Long protoVersion, Date time, String options, AccRegistry accRegistry,
            ResPaymentRegistry resPaymentRegistry, ResAccountOperationsRegistry resAccountOperationsRegistry,
            AccIncRegistry accIncRegistry, ClientRegistry clientRegistry, ResOrgStructure resOrgStructure,
            ResMenuExchangeData resMenuExchangeData, ResDiary resDiary, String message, ResEnterEvents resEnterEvents,
            ResTempCardsOperations resTempCardsOperations, TempCardOperationData tempCardOperationData,
            ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules, ComplexRoles complexRoles,
            CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry, Manager manager, OrgOwnerData orgOwnerData,
            QuestionaryData questionaryData, GoodsBasicBasketData goodsBasicBasketData, DirectiveElement directiveElement, ResultClientGuardian resultClientGuardian,
            ClientGuardianData clientGuardians, AccRegistryUpdate accRegistryUpdate, ProhibitionsMenu prohibitionsMenu,
            AccountsRegistry accountsRegistry,ResCardsOperationsRegistry resCardsOperationsRegistry, OrganizationStructure organizationStructure,
            ResReestrTaloonApproval resReestrTaloonApproval, ReestrTaloonApprovalData reestrTaloonApprovalData,
            OrganizationComplexesStructure organizationComplexesStructure, InteractiveReportData interactiveReportData,
            ZeroTransactionData zeroTransactionData, ResZeroTransactions resZeroTransactions, SpecialDatesData specialDatesData,
            ResSpecialDates resSpecialDates, MigrantsData migrantsData, ResMigrants resMigrants, List<AbstractToElement> responseSections) {
        this.syncType = syncType;
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.organizationType = organizationType;
        this.directorName = directorName;
        this.idOfPacket = idOfPacket;
        this.protoVersion = protoVersion;
        this.time = time;
        this.options = options;
        this.accRegistry = accRegistry;
        this.accIncRegistry = accIncRegistry;
        this.resPaymentRegistry = resPaymentRegistry;
        this.resAccountOperationsRegistry = resAccountOperationsRegistry;
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
        this.resultClientGuardian = resultClientGuardian;
        this.clientGuardians = clientGuardians;
        this.accRegistryUpdate = accRegistryUpdate;
        this.prohibitionsMenu = prohibitionsMenu;
        this.accountsRegistry = accountsRegistry;
        this.resCardsOperationsRegistry = resCardsOperationsRegistry;
        this.organizationStructure = organizationStructure;
        this.resReestrTaloonApproval = resReestrTaloonApproval;
        this.reestrTaloonApprovalData = reestrTaloonApprovalData;
        this.organizationComplexesStructure = organizationComplexesStructure;
        this.interactiveReportData = interactiveReportData;
        this.zeroTransactionData = zeroTransactionData;
        this.resZeroTransactions = resZeroTransactions;
        this.specialDatesData = specialDatesData;
        this.resSpecialDates = resSpecialDates;
        this.migrantsData = migrantsData;
        this.resMigrants = resMigrants;
        this.responseSections = responseSections;
    }

    public Document toDocument() throws Exception {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateOnlyFormat.setTimeZone(utcTimeZone);

        TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);//TimeZone.getTimeZone("Europe/Moscow");
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
        ecafeEnvelopeElement.setAttribute("Type",syncType.toString());
        ecafeEnvelopeElement.setAttribute("OrganizationType", String.valueOf(organizationType.ordinal()));
        ecafeEnvelopeElement.setAttribute("ReportService", (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.report.service"));

        if (directorName != null) {
            ecafeEnvelopeElement.setAttribute("DirectorName", this.directorName);
        }

        // ResPaymentRegistry
        if (null != resPaymentRegistry) {
            ecafeEnvelopeElement.appendChild(resPaymentRegistry.toElement(document));
        }

        // ResAccountOperationsRegistry
        if (null != resAccountOperationsRegistry) {
            ecafeEnvelopeElement.appendChild(resAccountOperationsRegistry.toElement(document));
        }

        // AccIncRegistry
        if (null != accIncRegistry) {
            ecafeEnvelopeElement.appendChild(accIncRegistry.toElement(document, dateFormat, timeFormat));
        }

        // AccRegistryUpdate
        if (null != accRegistryUpdate) {
            ecafeEnvelopeElement.appendChild(accRegistryUpdate.toElement(document, timeFormat));
        }

        // AccRegistry
        if (null != accRegistry) {
            ecafeEnvelopeElement.appendChild(accRegistry.toElement(document, dateFormat, timeFormat));
        }

        // ClientRegistry
        if (null != clientRegistry) {
            ecafeEnvelopeElement.appendChild(clientRegistry.toElement(document));
        }

        if(resultClientGuardian != null){
            ecafeEnvelopeElement.appendChild(resultClientGuardian.toElement(document));
        }

        if(clientGuardians != null){
            ecafeEnvelopeElement.appendChild(clientGuardians.toElement(document));
        }

        // ResOrgStructure
        if (null != resOrgStructure) {
            ecafeEnvelopeElement.appendChild(resOrgStructure.toElement(document));
        }

        // Menu
        if (null != resMenuExchangeData) {
            ecafeEnvelopeElement.appendChild(resMenuExchangeData.toElement(document));
        }

        // ProhibitionsMenu
        if (null != prohibitionsMenu) {
            ecafeEnvelopeElement.appendChild(prohibitionsMenu.toElement(document));
        }

        //OrganizationStructure
        if (null != organizationStructure) {
            ecafeEnvelopeElement.appendChild(organizationStructure.toElement(document));
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

        if (interactiveReportData != null) {
            ecafeEnvelopeElement.appendChild(interactiveReportData.toElement(document));
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

        if(accountsRegistry != null) {
            ecafeEnvelopeElement.appendChild(accountsRegistry.toElement(document));
        }

        if(resCardsOperationsRegistry != null) {
            ecafeEnvelopeElement.appendChild(resCardsOperationsRegistry.toElement(document));
        }

        if(directiveElement != null) {
            ecafeEnvelopeElement.appendChild(directiveElement.toElement(document));
        }

        if (resReestrTaloonApproval != null) {
            ecafeEnvelopeElement.appendChild(resReestrTaloonApproval.toElement(document));
        }

        if (reestrTaloonApprovalData != null) {
            ecafeEnvelopeElement.appendChild(reestrTaloonApprovalData.toElement(document));
        }

        if (organizationComplexesStructure != null) {
            ecafeEnvelopeElement.appendChild(organizationComplexesStructure.toElement(document));
        }

        if (resZeroTransactions != null) {
            ecafeEnvelopeElement.appendChild(resZeroTransactions.toElement(document));
        }

        if (zeroTransactionData != null) {
            ecafeEnvelopeElement.appendChild(zeroTransactionData.toElement(document));
        }

        if (resSpecialDates != null) {
            ecafeEnvelopeElement.appendChild(resSpecialDates.toElement(document));
        }

        if (specialDatesData != null) {
            ecafeEnvelopeElement.appendChild(specialDatesData.toElement(document));
        }

        if (resMigrants != null) {
            ecafeEnvelopeElement.appendChild(resMigrants.toElement(document));
        }

        if (migrantsData != null) {
            ecafeEnvelopeElement.appendChild(migrantsData.toElement(document));
        }

        if (this.responseSections != null) {
            for (AbstractToElement section : this.responseSections) {
                ecafeEnvelopeElement.appendChild(section.toElement(document));
            }
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

    public CorrectingNumbersOrdersRegistry getCorrectingNumbersOrdersRegistry() {
        return correctingNumbersOrdersRegistry;
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

    public AccRegistryUpdate getAccRegistryUpdate() {
        return accRegistryUpdate;
    }

    public ProhibitionsMenu getProhibitionsMenu() {
        return prohibitionsMenu;
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