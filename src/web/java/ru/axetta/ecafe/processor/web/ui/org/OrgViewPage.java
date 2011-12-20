/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.Date;

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
        ////  menu exchange source
        Long menuExchangeSourceOrgId = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (menuExchangeSourceOrgId == null) {
            menuExchangeSourceOrgName = "";
        } else {
            Org menuExchangeSourceOrg = (Org) session.load(Org.class, menuExchangeSourceOrgId);
            menuExchangeSourceOrgName = menuExchangeSourceOrg.getShortName();
        }
    }

}