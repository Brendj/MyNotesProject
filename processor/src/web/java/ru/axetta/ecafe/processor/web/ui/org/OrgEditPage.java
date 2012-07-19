/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgEditPage extends BasicWorkspacePage
        implements OrgSelectPage.CompleteHandler,// OrgListSelectPage.CompleteHandlerList,
        CategoryOrgListSelectPage.CompleteHandlerList, ContragentSelectPage.CompleteHandler,
        ConfigurationProviderSelect {

    private Long idOfOrg;
    private String shortName;
    private String officialName;
    private String address;
    private String phone;
    private Long officialPersonId;
    private String officialPersonFirstName;
    private String officialPersonSurname;
    private String officialPersonSecondName;
    private String officialPosition;
    private String contractId;
    private Date contractTime;
    private Integer state;
    private Long cardLimit;
    private String publicKey;
    private Long idOfPacket;
    private final OrgStateMenu orgStateMenu = new OrgStateMenu();
    private boolean changeSsoPassword = false;
    private String plainSsoPassword;
    private String plainSsoPasswordConfirmation;
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private Long menuExchangeSourceOrg;
    private String menuExchangeSourceOrgName;
    private ContragentItem defaultSupplier = new ContragentItem();
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private ConfigurationProvider configurationProvider;
    private String configurationProviderName;


    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }


    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        fill(org);
        ////
        menuExchangeSourceOrg = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        reloadMenuExchangeSourceOrgName(session);
    }

    public void updateOrg(Session session, Long idOfOrg) throws Exception {
        Contragent defaultSupplier = (Contragent) session.load(Contragent.class, this.defaultSupplier.getIdOfContragent());
        Org org = (Org) session.load(Org.class, idOfOrg);
        org.setShortName(shortName);
        org.setOfficialName(officialName);
        org.setAddress(address);
        org.setPhone(phone);
        Person officialPerson = org.getOfficialPerson();
        officialPerson.setFirstName(officialPersonFirstName);
        officialPerson.setSurname(officialPersonSurname);
        officialPerson.setSecondName(officialPersonSecondName);
        org.setOfficialPosition(officialPosition);
        org.setContractId(contractId);
        org.setContractTime(contractTime);
        org.setState(state);
        org.setCardLimit(cardLimit);
        org.setPublicKey(publicKey);
        org.setIdOfPacket(idOfPacket);
        if (changeSsoPassword) {
            org.setSsoPassword(plainSsoPassword);
        }
        org.setSmsSender(this.smsSender);
        org.setPriceOfSms(this.priceOfSms);
        org.setSubscriptionPrice(this.subscriptionPrice);

        org.setDefaultSupplier(defaultSupplier);
        org.setINN(INN);
        org.setOGRN(OGRN);
        org.setMailingListReportsOnNutrition(mailingListReportsOnNutrition);
        org.setMailingListReportsOnVisits(mailingListReportsOnVisits);
        org.setMailingListReports1(mailingListReports1);
        org.setMailingListReports2(mailingListReports2);
        org.setCategoriesInternal(new HashSet<CategoryOrg>());
        if (this.idOfCategoryOrgList.isEmpty()) org.setCategoriesInternal(null);
        else {
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(session, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                org.getCategories().add((CategoryOrg) object);
            }
        }

        if(this.configurationProvider!=null){
            ConfigurationProvider cp = (ConfigurationProvider) session.load(ConfigurationProvider.class, this.configurationProvider.getIdOfConfigurationProvider());
            if(cp!=null){
                org.setConfigurationProvider(cp);
            }
        }


        session.update(org);
        fill(org);
        /////
        DAOUtils.updateMenuExchangeLink(session, menuExchangeSourceOrg, idOfOrg);
        // save configuration provader
        //DAOService.getInstance().setConfigurationProviderInOrg(idOfOrg,CONFIGURATION_PROVIDER);
    }

    private void fill(Org org) throws Exception {
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.officialName = org.getOfficialName();
        this.address = org.getAddress();
        this.phone = org.getPhone();
        Person officialPerson = org.getOfficialPerson();
        this.officialPersonId = officialPerson.getIdOfPerson();
        this.officialPersonFirstName = officialPerson.getFirstName();
        this.officialPersonSurname = officialPerson.getSurname();
        this.officialPersonSecondName = officialPerson.getSecondName();
        this.officialPosition = org.getOfficialPosition();
        this.contractId = org.getContractId();
        this.contractTime = org.getContractTime();
        this.state = org.getState();
        this.cardLimit = org.getCardLimit();
        this.publicKey = org.getPublicKey();
        this.idOfPacket = org.getIdOfPacket();
        this.smsSender = org.getSmsSender();
        this.priceOfSms = org.getPriceOfSms();
        this.subscriptionPrice = org.getSubscriptionPrice();
        this.defaultSupplier = new ContragentItem(org.getDefaultSupplier());
        this.OGRN=org.getOGRN();
        this.INN=org.getINN();
        this.mailingListReportsOnNutrition = org.getMailingListReportsOnNutrition();
        this.mailingListReportsOnVisits = org.getMailingListReportsOnVisits();
        this.mailingListReports1 = org.getMailingListReports1();
        this.mailingListReports2 = org.getMailingListReports2();
        idOfCategoryOrgList.clear();
        this.filterOrg ="";
        if(org.getCategories()!=null && !org.getCategories().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryOrg categoryOrg: org.getCategories()){
                stringBuilder.append(categoryOrg.getCategoryName());
                stringBuilder.append("; ");
                idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
            }
            this.filterOrg = stringBuilder.substring(0,stringBuilder.length()-2);
        }

        if(org.getConfigurationProvider()!=null){
            this.configurationProviderName = org.getConfigurationProvider().getName();
        } else {
            this.configurationProviderName = "";
        }
    }


    /*public void setMenuExchangeSourceOrg(Long menuExchangeSourceOrg) {
       this.menuExchangeSourceOrg = menuExchangeSourceOrg;
   } */

    public Long getMenuExchangeSourceOrg() {
        return menuExchangeSourceOrg;
    }

    public String getMenuExchangeSourceOrgName() {
        return menuExchangeSourceOrgName;
    }

    private void reloadMenuExchangeSourceOrgName(Session session) {
        if (menuExchangeSourceOrg == null) {
            menuExchangeSourceOrgName = null;
        } else {
            Org org = (Org) session.load(Org.class, menuExchangeSourceOrg);
            menuExchangeSourceOrgName = org.getShortName();
        }
    }

    /* interface implements methods */
    private String filterOrg;
    private List<Long> idOfCategoryOrgList = new LinkedList<Long>();

    public String getIdOfCategoryOrgList() {
        return idOfCategoryOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getFilterOrg() {
        if (idOfCategoryOrgList.isEmpty()) return "Нет";
        return filterOrg;
    }

    @Override
    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
        if(null != categoryOrgMap) {
            idOfCategoryOrgList = new ArrayList<Long>();
            if(!categoryOrgMap.isEmpty()){
                filterOrg="";
                for(Long idOfCategoryOrg: categoryOrgMap.keySet()){
                    idOfCategoryOrgList.add(idOfCategoryOrg);
                    filterOrg=filterOrg.concat(categoryOrgMap.get(idOfCategoryOrg)+ "; ");
                }
                filterOrg = filterOrg.substring(0,filterOrg.length()-1);
            }

        }
    }

    public Object showConfigurationProviderSelection() throws Exception{
        RuntimeContext.getAppContext().getBean(ConfigurationProviderItemsPanel.class).reload();
        RuntimeContext.getAppContext().getBean(ConfigurationProviderItemsPanel.class).pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        if(null != configurationProvider){
            this.configurationProvider = configurationProvider;
        }
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.menuExchangeSourceOrg = idOfOrg;
        reloadMenuExchangeSourceOrgName(session);
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlags) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.defaultSupplier = new ContragentItem(contragent);
        }
    }

    public String getPageFilename() {
        return "org/edit";
    }


    /* Getters and Setters */
    public String getConfigurationProviderName() {
        return configurationProviderName;
    }

    public void setConfigurationProviderName(String configurationProviderName) {
        this.configurationProviderName = configurationProviderName;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

    public String getOGRN() {
        return OGRN;
    }

    public void setOGRN(String OGRN) {
        this.OGRN = OGRN;
    }

    public OrgStateMenu getOrgStateMenu() {
        return orgStateMenu;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
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

    public Long getOfficialPersonId() {
        return officialPersonId;
    }

    public void setOfficialPersonId(Long officialPersonId) {
        this.officialPersonId = officialPersonId;
    }

    public String getOfficialPersonFirstName() {
        return officialPersonFirstName;
    }

    public void setOfficialPersonFirstName(String officialPersonFirstName) {
        this.officialPersonFirstName = officialPersonFirstName;
    }

    public String getOfficialPersonSurname() {
        return officialPersonSurname;
    }

    public void setOfficialPersonSurname(String officialPersonSurname) {
        this.officialPersonSurname = officialPersonSurname;
    }

    public String getOfficialPersonSecondName() {
        return officialPersonSecondName;
    }

    public void setOfficialPersonSecondName(String officialPersonSecondName) {
        this.officialPersonSecondName = officialPersonSecondName;
    }

    public String getOfficialPosition() {
        return officialPosition;
    }

    public void setOfficialPosition(String officialPosition) {
        this.officialPosition = officialPosition;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(Long cardLimit) {
        this.cardLimit = cardLimit;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public void setIdOfPacket(Long idOfPacket) {
        this.idOfPacket = idOfPacket;
    }

    public boolean isChangeSsoPassword() {
        return changeSsoPassword;
    }

    public void setChangeSsoPassword(boolean changeSsoPassword) {
        this.changeSsoPassword = changeSsoPassword;
    }

    public String getPlainSsoPassword() {
        return plainSsoPassword;
    }

    public void setPlainSsoPassword(String plainSsoPassword) {
        this.plainSsoPassword = plainSsoPassword;
    }

    public String getPlainSsoPasswordConfirmation() {
        return plainSsoPasswordConfirmation;
    }

    public void setPlainSsoPasswordConfirmation(String plainSsoPasswordConfirmation) {
        this.plainSsoPasswordConfirmation = plainSsoPasswordConfirmation;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public void setSmsSender(String smsSender) {
        this.smsSender = smsSender;
    }

    public Long getPriceOfSms() {
        return priceOfSms;
    }

    public void setPriceOfSms(Long priceOfSms) {
        this.priceOfSms = priceOfSms;
    }

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Long subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public ContragentItem getDefaultSupplier() {
        return defaultSupplier;
    }

    public String getMailingListReportsOnNutrition() {
        return mailingListReportsOnNutrition;
    }

    public void setMailingListReportsOnNutrition(String mailingListReportsOnNutrition) {
        this.mailingListReportsOnNutrition = mailingListReportsOnNutrition;
    }

    public String getMailingListReportsOnVisits() {
        return mailingListReportsOnVisits;
    }

    public void setMailingListReportsOnVisits(String mailingListReportsOnVisits) {
        this.mailingListReportsOnVisits = mailingListReportsOnVisits;
    }

    public String getMailingListReports1() {
        return mailingListReports1;
    }

    public void setMailingListReports1(String mailingListReports1) {
        this.mailingListReports1 = mailingListReports1;
    }

    public String getMailingListReports2() {
        return mailingListReports2;
    }

    public void setMailingListReports2(String mailingListReports2) {
        this.mailingListReports2 = mailingListReports2;
    }
}