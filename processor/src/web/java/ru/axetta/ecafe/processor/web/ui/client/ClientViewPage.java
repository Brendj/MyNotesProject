/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.logging.Logger;

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
                    /*
    private List<String> categoryDiscountNames;

    public List<String> getCategoryDiscountNames() {
        return categoryDiscountNames;
    }

    public boolean isCategoryDiscountNamesEmpty() {
        return categoryDiscountNames.isEmpty();
    }
                      */
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
    private String email;
    private Boolean notifyViaEmail;
    private Boolean notifyViaSMS;
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
    private Long limit;
    private Long expenditureLimit;
    private String clientGroupName;

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

    private final ClientCardListViewPage clientCardListViewPage = new ClientCardListViewPage();

    public String getPageFilename() {
        return "client/view";
    }

    public Long getBalance() {
        return balance;
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

    public String getEmail() {
        return email;
    }

    public Boolean getNotifyViaEmail() {
        return notifyViaEmail;
    }

    public Boolean getNotifyViaSMS() {
        return notifyViaSMS;
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
        this.email = client.getEmail();
        this.notifyViaEmail = client.isNotifyViaEmail();
        this.notifyViaSMS = client.isNotifyViaSMS();
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
        this.balance = client.getBalance();
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();

        // опекуны
        // (Kadyrov D) 23.12.2011
        this.san= client.getSan();
        this.guardsan=client.getGuardSan();


        ClientGroup group = client.getClientGroup();
        this.clientGroupName = group==null?"":group.getGroupName();

        // Категории скидок
        this.categoriesDiscounts=new LinkedList<CategoryDiscount>();
        if(!client.getCategories().isEmpty()){
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                String name=categoryDiscount.getCategoryName();
                this.categoriesDiscounts.add(categoryDiscount);
            }
        }

        // Категории скидок старое не используется
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
    }

}