/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgViewPage extends BasicWorkspacePage {

    private Long idOfOrg;
    private String shortName;
    private String officialName;
    private String address;
    private String phone;
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
    private String smsSender;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private String menuExchangeSourceOrgName;
    private String defaultSupplierName;
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private List<CategoryOrg> categoryOrg;

    public List<CategoryOrg> getCategoryOrg() {
        return categoryOrg;
    }

    public void setCategoryOrg(List<CategoryOrg> categoryOrg) {
        this.categoryOrg = categoryOrg;
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

    public String getMenuExchangeSourceOrgName() {
        return menuExchangeSourceOrgName;
    }


    public String getPageFilename() {
        return "org/view";
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

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getOfficialPersonFirstName() {
        return officialPersonFirstName;
    }

    public String getOfficialPersonSurname() {
        return officialPersonSurname;
    }

    public String getOfficialPersonSecondName() {
        return officialPersonSecondName;
    }

    public String getOfficialPosition() {
        return officialPosition;
    }

    public String getContractId() {
        return contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public Integer getState() {
        return state;
    }

    public Long getCardLimit() {
        return cardLimit;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public Long getPriceOfSms() {
        return priceOfSms;
    }

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public String getDefaultSupplierName() {
        return defaultSupplierName;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.officialName = org.getOfficialName();
        this.address = org.getAddress();
        this.phone = org.getPhone();
        Person officialPerson = org.getOfficialPerson();
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
        this.defaultSupplierName = org.getDefaultSupplier().getContragentName();
        this.INN=org.getINN();
        this.OGRN=org.getOGRN();
        this.categoryOrg= new LinkedList<CategoryOrg>();
        if(!org.getCategories().isEmpty()){
           for (CategoryOrg co: org.getCategories()){
               this.categoryOrg.add(co);
           }
        }
        
        ////  menu exchange source
        Long menuExchangeSourceOrgId = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (menuExchangeSourceOrgId == null) {
            menuExchangeSourceOrgName = "";
        } else {
            Org menuExchangeSourceOrg = (Org) session.load(Org.class, menuExchangeSourceOrgId);
            menuExchangeSourceOrgName = menuExchangeSourceOrg.getShortName();
        }

        this.mailingListReportsOnNutrition = org.getMailingListReportsOnNutrition();
        this.mailingListReportsOnVisits = org.getMailingListReportsOnVisits();
        this.mailingListReports1 = org.getMailingListReports1();
        this.mailingListReports2 = org.getMailingListReports2();
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