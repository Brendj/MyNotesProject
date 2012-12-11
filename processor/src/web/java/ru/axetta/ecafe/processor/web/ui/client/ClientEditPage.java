/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientEditPage extends BasicWorkspacePage
        implements OrgSelectPage.CompleteHandler, CategoryListSelectPage.CompleteHandlerList, ClientGroupSelectPage.CompleteHandler {


    private String fax;

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return fax;
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

    private List<CategoryDiscountItem> categoryDiscounts;

    public List<CategoryDiscountItem> getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public boolean isCategoriesEmpty() {
        return categoryDiscounts.isEmpty();
    }

    private List<CategoryDiscount> categoryDiscountList = new LinkedList<CategoryDiscount>();

    public List<CategoryDiscount> getCategoryDiscountList() {
        return categoryDiscountList;
    }

    public void setCategoryDiscountList(List<CategoryDiscount> categoryDiscountList) {
        this.categoryDiscountList = categoryDiscountList;
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
    private String remarks;
    private boolean changePassword;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private Long contractId;
    private Date contractTime;
    private Integer contractState;
    private Integer payForSMS;
    private Long balance;
    private Long limit;
    private Long expenditureLimit;
    private String clientGroupName;
    private Long idOfClientGroup;
    private Long externalId;
    private String clientGUID;
    private Integer discountMode;
    private List<SelectItem> selectItemList = new LinkedList<SelectItem>();
    private String san;
    private String guardsan;
    private final ClientPayForSMSMenu clientPayForSMSMenu = new ClientPayForSMSMenu();
    private final ClientContractStateMenu clientContractStateMenu = new ClientContractStateMenu();
    private Integer freePayMaxCount;

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
    }

    /* Является ли данный тип льгот как "Льгота по категорям" если да то TRUE, False в противном случает */
    public Boolean getDiscountModeIsCategory(){
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

    public String getGuardsan() {
        return guardsan;
    }

    public void setGuardsan(String guardsan) {
        this.guardsan = guardsan;
    }

    public int getContractIdMaxLength() {
        return CONTRACT_ID_MAX_LENGTH;
    }

    public Long getBalance() {
        return balance;
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

    public void fill(Session session, Long idOfClient) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);
        List clientCategories = Arrays.asList(client.getCategoriesDiscounts().split(","));
        Criteria categoryDiscountCriteria = session.createCriteria(CategoryDiscount.class);
        List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
        categoryDiscounts = new ArrayList<CategoryDiscountItem>();
        for (CategoryDiscount categoryDiscount : categoryDiscountList) {
            categoryDiscounts.add(new CategoryDiscountItem(
                    clientCategories.contains(categoryDiscount.getIdOfCategoryDiscount() + ""),
                    categoryDiscount.getIdOfCategoryDiscount(),
                    categoryDiscount.getCategoryName()));
        }

        idOfCategoryList.clear();
        categoryDiscountList.clear();
        if(!client.getCategories().isEmpty()){
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                this.categoryDiscountList.add(categoryDiscount);
            }
        }

        this.selectItemList = new LinkedList<SelectItem>();
        /* если у клиента уже выбрана льгота то она будет первой */
        if(null!=client.getDiscountMode() && client.getDiscountMode()>0){
            this.selectItemList.add(new SelectItem(client.getDiscountMode(),Client.DISCOUNT_MODE_NAMES[client.getDiscountMode()]));
        }
        this.selectItemList.add(new SelectItem(0,Client.DISCOUNT_MODE_NAMES[0]));
        for (Integer i=1;i<Client.DISCOUNT_MODE_NAMES.length; i++){
            SelectItem selectItem = new SelectItem(i,Client.DISCOUNT_MODE_NAMES[i]);
            if(!i.equals(client.getDiscountMode())) this.selectItemList.add(selectItem);
        }
        this.clientGroupName =client.getClientGroup() == null? "" : client.getClientGroup().getGroupName();
        fill(client);
    }

    public void completeClientGroupSelection(Session session, Long idOfClientGroup) throws Exception{
        if(null != idOfClientGroup){
             this.idOfClientGroup = idOfClientGroup;
             this.clientGroupName = DAOUtils.findClientGroup(session, new CompositeIdOfClientGroup(this.org.idOfOrg, idOfClientGroup)).getGroupName();
        }
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public Object changeClientCategory() {
        if(this.discountMode != Client.DISCOUNT_MODE_BY_CATEGORY){
            this.idOfCategoryList = new ArrayList<Long>(0);
            filter = "Не выбрано";
        }
        return null;
    }

    public void updateClient(Session persistenceSession, Long idOfClient) throws Exception {
        String mobile = Client.checkAndConvertMobile(this.mobile);
        if (mobile==null) throw new Exception("Неверный формат мобильного телефона");

        Client client = (Client) persistenceSession.load(Client.class, idOfClient);
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);

        Person person = client.getPerson();
        this.person.copyTo(person);
        persistenceSession.update(person);

        Person contractPerson = client.getContractPerson();
        this.contractPerson.copyTo(contractPerson);
        persistenceSession.update(contractPerson);

        Org org = (Org) persistenceSession.load(Org.class, this.org.getIdOfOrg());
        Boolean isReplaceOrg = !(client.getOrg().getIdOfOrg().equals(this.org.getIdOfOrg()));
        client.setOrg(org);
        client.setPerson(person);
        client.setContractPerson(contractPerson);
        client.setClientRegistryVersion(clientRegistryVersion);
        client.setFlags(this.flags);
        client.setAddress(this.address);
        client.setPhone(this.phone);
        client.setMobile(mobile);
        client.setFax(this.fax);
        client.setEmail(this.email);
        client.setNotifyViaEmail(this.notifyViaEmail);
        client.setNotifyViaSMS(this.notifyViaSMS);
        client.setRemarks(this.remarks);
        client.setUpdateTime(new Date());
        client.setContractTime(this.contractTime);
        client.setContractId(this.contractId);
        client.setContractState(this.contractState);
        client.setLimit(this.limit);
        client.setExpenditureLimit(this.expenditureLimit);
        client.setFreePayMaxCount(this.freePayMaxCount);
        client.setSan(this.san);
        client.setGuardSan(this.guardsan);
        if (this.externalId==null || this.externalId==0) client.setExternalId(null);
        else client.setExternalId(this.externalId);
        if (this.clientGUID==null || this.clientGUID.isEmpty()) client.setClientGUID(null);
        else client.setClientGUID(this.clientGUID);
        if (this.changePassword) {
            client.setPassword(this.plainPassword);
        }
        client.setPayForSMS(this.payForSMS);
        if(null != discountMode) client.setDiscountMode(discountMode);
        if(discountMode == Client.DISCOUNT_MODE_BY_CATEGORY && idOfCategoryList.size()==0){
            throw new Exception("Выберите хотя бы одну категорию льгот");
        }
        /* категори скидок */
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        StringBuilder clientCategories = new StringBuilder();
        if (this.idOfCategoryList.size()!=0) {
            Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
            for (Object object: categoryCriteria.list()){
                CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                clientCategories.append(categoryDiscount.getIdOfCategoryDiscount());
                clientCategories.append(",");
                this.categoryDiscountSet.add(categoryDiscount);
            }
        } else {
            /* очистить список если он не пуст */
            Set<CategoryDiscount> categories = client.getCategories();
            for (CategoryDiscount categoryDiscount: categories){
                categoryDiscount.getClients().remove(client);
                persistenceSession.update(categoryDiscount);
            }
            client.setCategories(new HashSet<CategoryDiscount>(0));
            //Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            //categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
        }
        client.setCategoriesDiscounts(clientCategories.length()==0?"":clientCategories.substring(0, clientCategories.length()-1));
        client.setCategories(categoryDiscountSet);
        if(isReplaceOrg){
            ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistenceSession, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_DISPLACED.getNameOfGroup());
            if(clientGroup==null) clientGroup = DAOUtils.createClientGroup(persistenceSession, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_DISPLACED);
            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            client.setIdOfClientGroup(this.idOfClientGroup);
        } else{
            if(this.idOfClientGroup != null){
                client.setIdOfClientGroup(this.idOfClientGroup);
            }
        }

        persistenceSession.update(client);
        fill(client);
    }

    public void removeClient(Session persistenceSession) throws Exception {
        Client client = (Client) persistenceSession.load(Client.class, idOfClient);
        if (!client.getOrders().isEmpty()) throw new Exception("Имеются зарегистрированные заказы");
        if (!client.getClientPaymentOrders().isEmpty()) throw new Exception("Имеются зарегистрированные пополнения счета");
        if (!client.getCards().isEmpty()) throw new Exception("Имеются зарегистрированные карты");
        if (!client.getCategories().isEmpty()){
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                client.getCategories().remove(categoryDiscount);
            }
        }
        persistenceSession.delete(client);
    }

    private void fill(Client client) throws Exception {
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
        this.remarks = client.getRemarks();
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
        this.payForSMS = client.getPayForSMS();
        this.balance = client.getBalance();
        this.limit = client.getLimit();
        this.expenditureLimit = client.getExpenditureLimit();
        this.freePayMaxCount = client.getFreePayMaxCount();
        this.san = client.getSan();
        this.guardsan = client.getGuardSan();
        this.externalId = client.getExternalId();
        this.clientGUID = client.getClientGUID();
        this.discountMode = client.getDiscountMode();
        /* filter fill*/
        StringBuilder categoriesFilter = new StringBuilder();
        if(!client.getCategories().isEmpty()){
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                categoriesFilter.append(categoryDiscount.getCategoryName());
                categoriesFilter.append("; ");
                this.categoryDiscountList.add(categoryDiscount);
            }
        }  else {
            categoriesFilter.append("Не выбрано");
        }
        this.filter=categoriesFilter.toString();
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]","");
    }

    private String filter = "Не выбрано";
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private Set<CategoryDiscount> categoryDiscountSet=new HashSet<CategoryDiscount>();

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

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
        //To change body of implemented methods use File | Settings | File Templates.
        if(null != categoryMap) {
            idOfCategoryList = new ArrayList<Long>();
            if(categoryMap.isEmpty()){
                filter = "Не выбрано";
            } else {
                filter="";
                for(Long idOfCategory: categoryMap.keySet()){
                    idOfCategoryList.add(idOfCategory);
                    filter=filter.concat(categoryMap.get(idOfCategory)+ "; ");
                }
                filter = filter.substring(0,filter.length()-1);
            }

        }
    }

}