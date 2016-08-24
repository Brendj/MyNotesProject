/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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
public class ClientCreatePage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler, CategoryListSelectPage.CompleteHandlerList {

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

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

        public PersonItem() {
            this.firstName = null;
            this.surname = null;
            this.secondName = null;
            this.idDocument = null;
        }

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public Person buildPerson() {
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(idDocument);
            return person;
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

    private OrgItem org = null;
    private PersonItem person = null;
    private PersonItem contractPerson = null;
    private Integer flags = 0;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String fax;
    private Boolean notifyViaEmail = false;
    private Boolean notifyViaSMS = true;
    private Boolean notifyViaPUSH = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS);
    private String remarks;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private Long contractId;
    private Date contractTime = new Date();
    private Integer contractState = 0;
    private Integer payForSMS = 1;
    private Long limit;
    private String san;
    private String guardsan;
    private Long externalId;
    private String clientGUID;
    private Integer discountMode;
    private List<SelectItem> selectItemList = new LinkedList<SelectItem>();
    private Integer gender;
    private Date birthDate;
    private String benefitOnAdmission;


    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
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

    public String getBenefitOnAdmission() {
        return benefitOnAdmission;
    }

    public void setBenefitOnAdmission(String benefitOnAdmission) {
        this.benefitOnAdmission = benefitOnAdmission;
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

    private final ClientPayForSMSMenu clientPayForSMSMenu = new ClientPayForSMSMenu();
    private final ClientInitContractStateMenu clientInitContractStateMenu = new ClientInitContractStateMenu();
    private final ClientGenderMenu clientGenderMenu = new ClientGenderMenu();
    private boolean autoContractId = true;

    public String getPageFilename() {
        return "client/create";
    }

    public int getContractIdMaxLength() {
        return CONTRACT_ID_MAX_LENGTH;
    }

    public boolean isAutoContractId() {
        return autoContractId;
    }

    public void setAutoContractId(boolean autoContractId) {
        this.autoContractId = autoContractId;
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
    public Boolean getNotifyViaPUSH() {
        return notifyViaPUSH;
    }

    public void setNotifyViaPUSH(Boolean notifyViaPUSH) {
        this.notifyViaPUSH = notifyViaPUSH;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public ClientInitContractStateMenu getClientInitContractStateMenu() {
        return clientInitContractStateMenu;
    }

    public ClientGenderMenu getClientGenderMenu() {
        return clientGenderMenu;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
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

    public void fill(Session session) throws HibernateException {
        if (null == org) {
            org = new OrgItem();
        }
        if (null == person) {
            person = new PersonItem();
        }
        if (null == contractPerson) {
            contractPerson = new PersonItem();
        }
        payForSMS = 1;
        this.selectItemList = new LinkedList<SelectItem>();
        for (Integer i=0;i<Client.DISCOUNT_MODE_NAMES.length; i++){
            this.selectItemList.add(new SelectItem(i,Client.DISCOUNT_MODE_NAMES[i]));
        }
        this.discountMode = Client.DISCOUNT_MODE_NONE;
        this.limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);
        // Категории скидок
        /*
        Criteria categoryDiscountCriteria = session.createCriteria(CategoryDiscount.class);
        List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
        categoryDiscounts = new ArrayList<CategoryDiscountItem>();
        for (CategoryDiscount categoryDiscount : categoryDiscountList) {
            categoryDiscounts.add(new CategoryDiscountItem(
                    false,
                    categoryDiscount.getIdOfCategoryDiscount(),
                    categoryDiscount.getCategoryName()));
        } */
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
            if (org.getCardLimit()!=null && org.getCardLimit()!=0) {
                this.limit = org.getCardLimit();
            }
        }
    }

    public Object changeClientCategory() {
        if(this.discountMode != Client.DISCOUNT_MODE_BY_CATEGORY){
            this.categoryDiscountSet = new HashSet<CategoryDiscount>();
            filter = "Не выбрано";
        }
        return null;
    }


    public void createClient(Session persistenceSession) throws Exception {
        RuntimeContext runtimeContext  = RuntimeContext.getInstance();
        Org org = (Org) persistenceSession.load(Org.class, this.org.getIdOfOrg());
        if (autoContractId) {
            this.contractId = runtimeContext.getClientContractIdGenerator().generate(this.org.getIdOfOrg());
        }

        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);

        Person person = this.person.buildPerson();
        persistenceSession.save(person);
        Person contractPerson = this.contractPerson.buildPerson();
        persistenceSession.save(contractPerson);

        Client client = new Client(org, person, contractPerson, this.flags, this.notifyViaEmail, this.notifyViaSMS, this.notifyViaPUSH,
                this.contractId, this.contractTime, this.contractState, this.plainPassword, this.payForSMS,
                clientRegistryVersion, this.limit, RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT), "");

        client.setAddress(this.address);
        client.setPhone(this.phone);
        client.setMobile(this.mobile);
        client.setEmail(this.email);
        client.setFax(this.fax);
        client.setRemarks(this.remarks);
        client.setBirthDate(this.birthDate);
        client.setGender(this.gender);
        client.setBenefitOnAdmission(this.benefitOnAdmission);
        if (this.externalId==null || this.externalId==0) client.setExternalId(null);
        else client.setExternalId(this.externalId);
        if (this.clientGUID==null || this.clientGUID.isEmpty()) client.setClientGUID(null);
        else client.setClientGUID(this.clientGUID);

        if(null != discountMode) client.setDiscountMode(discountMode);
        if(discountMode == Client.DISCOUNT_MODE_BY_CATEGORY && idOfCategoryList.size()==0){
            throw new Exception("Выберите хотя бы одну категорию льгот");
        }
        // Категории скидок
          /*
        for (CategoryDiscountItem categoryDiscount : categoryDiscounts) {
            if (categoryDiscount.getSelected())
                clientCategories = clientCategories + categoryDiscount.getIdOfCategoryDiscount() + ",";
            }                            */

        StringBuilder clientCategories = new StringBuilder();
        if(!idOfCategoryList.isEmpty()){
            Criteria categoryCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
            categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", this.idOfCategoryList));
            for (Object object: categoryCriteria.list()){
                CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                clientCategories.append(categoryDiscount.getIdOfCategoryDiscount());
                clientCategories.append(",");
                client.getCategories().add(categoryDiscount);
            }
            client.setCategoriesDiscounts(clientCategories.substring(0, clientCategories.length()-1));
        }

        persistenceSession.save(client);

        ClientMigration clientMigration = new ClientMigration(client,org,this.contractTime);
        persistenceSession.save(clientMigration);
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