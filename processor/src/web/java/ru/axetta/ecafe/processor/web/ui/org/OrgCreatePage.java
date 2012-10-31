/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgCreatePage extends BasicWorkspacePage
    implements OrgSelectPage.CompleteHandler, ContragentSelectPage.CompleteHandler {
    private String shortName;
    private String officialName;
    private String address;
    private String phone;
    private String officialPersonFirstName;
    private String officialPersonSurname;
    private String officialPersonSecondName;
    private String officialPosition;
    private String contractId;
    private Date contractTime = new Date();
    private Integer state;
    private Long cardLimit;
    private Long priceOfSms;
    private Long subscriptionPrice;
    private Long menuExchangeSourceOrg;
    private String menuExchangeSourceOrgName;
    private String publicKey;
    private final OrgStateMenu orgStateMenu = new OrgStateMenu();
    private String plainSsoPassword;
    private String plainSsoPasswordConfirmation;
    private String smsSender;
    private ContragentItem defaultSupplier = new ContragentItem();
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private String guid;
    private String tag;
    private String city;
    private String district;
    private String location;
    private String latitude;
    private String longitude;
    private Integer refectoryType;
    private SelectItem[] refectoryTypeComboMenuItems = readRefectoryTypeComboMenuItems();

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

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.defaultSupplier = new ContragentItem(contragent);
        }
    }

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

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.menuExchangeSourceOrg = idOfOrg;
        reloadMenuExchangeSourceOrgName(session);
    }

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

    public Long getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Long subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public String getPageFilename() {
        return "org/create";
    }

    public OrgStateMenu getOrgStateMenu() {
        return orgStateMenu;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public Integer getRefectoryType() {
        return refectoryType;
    }

    public void setRefectoryType(Integer refectoryType) {
        if (refectoryType == -1) {
            this.refectoryType = null;
        } else {
            this.refectoryType = refectoryType;
        }
    }

    private static SelectItem[] readRefectoryTypeComboMenuItems() {
        SelectItem[] items = new SelectItem[Org.REFECTORY_TYPE_NAMES.length];
        for (int i = 0; i < Org.REFECTORY_TYPE_NAMES.length; i++) {
            items[i] = new SelectItem(i, Org.REFECTORY_TYPE_NAMES[i]);
        }
        return items;
    }

    public SelectItem[] getRefectoryTypeComboMenuItems() {
        return refectoryTypeComboMenuItems;
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

    public Long getPriceOfSms() {
        return priceOfSms;
    }

    public void setPriceOfSms(Long priceOfSms) {
        this.priceOfSms = priceOfSms;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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

    public ContragentItem getDefaultSupplier() {
        return defaultSupplier;
    }

    public void fill(Session session) throws Exception {
        this.state = 0;
    }

    public void createOrg(Session session) throws Exception {
        Person officialPerson = new Person(this.officialPersonFirstName, this.officialPersonSurname,
                this.officialPersonSecondName);
        session.save(officialPerson);

        if (this.defaultSupplier.getIdOfContragent()==null) {
            throw new Exception("Не указан поставщик по умолчанию");
        }
        Contragent defaultSupplier = (Contragent) session.load(Contragent.class,
                this.defaultSupplier.getIdOfContragent());

        Org org = new Org(this.shortName, this.officialName, this.address, officialPerson, this.officialPosition,
                this.contractId, this.contractTime, this.state, this.cardLimit, this.publicKey, this.priceOfSms,
                this.subscriptionPrice, defaultSupplier, this.INN, this.OGRN, this.mailingListReportsOnNutrition,
                this.mailingListReportsOnVisits, this.mailingListReports1, this.mailingListReports2);
        org.setCity(city);
        org.setDistrict(district);
        org.setLocation(location);
        org.setLongitude(longitude);
        org.setLatitude(latitude);
        org.setGuid(this.guid);
        org.setPhone(this.phone);
        org.setSmsSender(this.smsSender);
        if (StringUtils.isNotEmpty(plainSsoPassword)) {
            org.setSsoPassword(plainSsoPassword);
        }
        org.setRefectoryType(refectoryType);
        session.save(org);

        if (menuExchangeSourceOrg!=null) DAOUtils.updateMenuExchangeLink(session, menuExchangeSourceOrg, org.getIdOfOrg());
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}