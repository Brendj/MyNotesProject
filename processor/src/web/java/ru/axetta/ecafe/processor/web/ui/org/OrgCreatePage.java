/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
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
    private String shortNameInfoService;
    private String founder;
    private String subordination;
    private String officialName;
    private String address;
    private String shortAddress;
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
    private ContragentItem coSupplier = new ContragentItem();
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private String guid;
    private Long ekisId = null;
    private String egissoId;
    private String tag;
    private String city;
    private String district;
    private String municipalDistrict;
    private String location;
    private String latitude;
    private String longitude;
    private Integer refectoryType;
    private SelectItem[] refectoryTypeComboMenuItems = readRefectoryTypeComboMenuItems();
    // тип организации "Общеобразовательнок ОУ / Дошкольное ОУ / Поставщик питания / Профессиональное ОУ"
    private OrganizationType organizationType;
    private OrganizationType organizationTypeInitial;
    private final OrganizationTypeMenu organizationTypeMenu = new OrganizationTypeMenu();
    private OrganizationSecurityLevel securityLevel;

    private Long btiUnom;
    private Long btiUnad;
    private Long uniqueAddressId;
    private String introductionQueue;
    private Long additionalIdBuilding;
    private String statusDetailing;

    private Boolean payByCashier;
    private Boolean oneActiveCard;
    private Boolean photoRegistry;
    private Boolean changesDSZN;
    private Boolean isRecyclingEnabled;
    private Boolean autoCreateCards = false;
    private Boolean needVerifyCardSign = false;
    private Boolean denyPayPlanForTimeDifference = false;
    private Boolean allowRegistryChangeEmployee = false;
    private Boolean helpdeskEnabled = false;
    private Boolean preordersEnabled = false;
    private Boolean preorderlp = false;
    private Long orgIdFromNsi = null;
    private Boolean governmentContract = false;
    private Boolean useLongCardId = false;

    public static final String DEFAULT_SUPPLIER = "DefaultSupplier";
    public static final String CO_SUPPLIER = "CoSupplier";
    private String modeContragentSelect;

    public String getDefaultSupplierMode() {
        return DEFAULT_SUPPLIER;
    }

    public String getCoSupplierMode() {
        return CO_SUPPLIER;
    }

    private SelectItem[] statusDetails = readStatusDetailsComboMenuItems();
    private SelectItem[] securityLevels = readSecurityLevels();

    private SelectItem[] readStatusDetailsComboMenuItems() {
        SelectItem[] items = new SelectItem[5];
        items[0] = new SelectItem(0, "");
        items[1] = new SelectItem(1, "Запланировано подключение");
        items[2] = new SelectItem(2, "На ремонте");
        items[3] = new SelectItem(3, "Закрыто");
        items[4] = new SelectItem(4, "Другое");
        return items;
    }

    private SelectItem[] readSecurityLevels() {
        SelectItem[] items = new SelectItem[2];
        items[0] = new SelectItem(OrganizationSecurityLevel.STANDARD, OrganizationSecurityLevel.STANDARD.toString());
        items[1] = new SelectItem(OrganizationSecurityLevel.EXTENDED, OrganizationSecurityLevel.EXTENDED.toString());
        return items;
    }

    private List detailsItem = readDetailItems();

    private List<String> readDetailItems() {
        List<String> items = new ArrayList();
        items.add("");
        items.add("Запланировано подключение");
        items.add("На ремонте");
        items.add("Закрыто");
        items.add("Другое");
        return items;
    }

    private String statusTextArea;

    private String statusDetail;

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
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

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            if (modeContragentSelect.equals(DEFAULT_SUPPLIER)) {
                this.defaultSupplier = new ContragentItem(contragent);
            }
            if (modeContragentSelect.equals(CO_SUPPLIER)) {
                this.coSupplier = new ContragentItem(contragent);
            }
        } else {
            if (modeContragentSelect.equals(CO_SUPPLIER)) {
                this.coSupplier = null;
            }
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

    public OrganizationSecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(OrganizationSecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public ContragentItem getCoSupplier() {
        return coSupplier;
    }

    public void setCoSupplier(ContragentItem coSupplier) {
        this.coSupplier = coSupplier;
    }

    public String getModeContragentSelect() {
        return modeContragentSelect;
    }

    public void setModeContragentSelect(String modeContragentSelect) {
        this.modeContragentSelect = modeContragentSelect;
    }

    public OrganizationType getOrganizationTypeInitial() {
        return organizationTypeInitial;
    }

    public void setOrganizationTypeInitial(OrganizationType organizationTypeInitial) {
        this.organizationTypeInitial = organizationTypeInitial;
    }

    public Boolean getRecyclingEnabled() {
        return isRecyclingEnabled;
    }

    public void setRecyclingEnabled(Boolean recyclingEnabled) {
        isRecyclingEnabled = recyclingEnabled;
    }

    public Boolean getAutoCreateCards() {
        return autoCreateCards;
    }

    public void setAutoCreateCards(Boolean autoCreateCards) {
        this.autoCreateCards = autoCreateCards;
    }

    public Boolean getNeedVerifyCardSign() {
        return needVerifyCardSign;
    }

    public void setNeedVerifyCardSign(Boolean needVerifyCardSign) {
        this.needVerifyCardSign = needVerifyCardSign;
    }

    public Boolean getDenyPayPlanForTimeDifference() {
        return denyPayPlanForTimeDifference;
    }

    public void setDenyPayPlanForTimeDifference(Boolean denyPayPlanForTimeDifference) {
        this.denyPayPlanForTimeDifference = denyPayPlanForTimeDifference;
    }

    public Boolean getAllowRegistryChangeEmployee() {
        return allowRegistryChangeEmployee;
    }

    public void setAllowRegistryChangeEmployee(Boolean allowRegistryChangeEmployee) {
        this.allowRegistryChangeEmployee = allowRegistryChangeEmployee;
    }

    public Boolean getHelpdeskEnabled() {
        return helpdeskEnabled;
    }

    public void setHelpdeskEnabled(Boolean helpdeskEnabled) {
        this.helpdeskEnabled = helpdeskEnabled;
    }

    public Boolean getPreordersEnabled() {
        return preordersEnabled;
    }

    public void setPreordersEnabled(Boolean preordersEnabled) {
        this.preordersEnabled = preordersEnabled;
    }

    public Boolean getPreorderlp() {
        return preorderlp;
    }

    public void setPreorderlp(Boolean preorderlp) {
        this.preorderlp = preorderlp;
    }

    public Long getEkisId() {
        return ekisId;
    }

    public void setEkisId(Long ekisId) {
        this.ekisId = ekisId;
    }

    public String getEgissoId() {
        return egissoId;
    }

    public void setEgissoId(String egissoId) {
        this.egissoId = egissoId;
    }

    public String getMunicipalDistrict() {
        return municipalDistrict;
    }

    public void setMunicipalDistrict(String municipalDistrict) {
        this.municipalDistrict = municipalDistrict;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getSubordination() {
        return subordination;
    }

    public void setSubordination(String subordination) {
        this.subordination = subordination;
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            if (contragent != null) {
                this.idOfContragent = contragent.getIdOfContragent();
                this.contragentName = contragent.getContragentName();
            } else {
                this.idOfContragent = null;
                this.contragentName = null;
            }
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

    public String getPageTitle() {
        return "Организации / Регистрация";
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

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
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

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
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

        if(DAOUtils.findOrgByShortname(session, getShortName()) != null) {
            throw new Exception("\"Наименование ОО для поставщика\" уже существует");
        }
        if (this.defaultSupplier.getIdOfContragent()==null) {
            throw new Exception("Не указан поставщик по умолчанию");
        }
        if (StringUtils.isEmpty(this.shortName)) {
            throw new Exception("Не указано наименование ОО для поставщика");
        }
        Contragent defaultSupplier = (Contragent) session.load(Contragent.class,
                this.defaultSupplier.getIdOfContragent());

        if (this.statusTextArea != null) {
            if (this.statusDetail != null && this.statusDetail.length() > 0) {
                this.statusDetailing = this.detailsItem.get(Integer.parseInt(this.statusDetail)) + "/" + this.statusTextArea;
            } else {
                this.statusDetailing = "/" + this.statusTextArea;
            }
        } else {
            if (this.statusDetail != null && this.statusDetail.length() > 0) {
                this.statusDetailing = this.detailsItem.get(Integer.parseInt(this.statusDetail)).toString();
            }
        }

        long version = DAOUtils.nextVersionByOrgStucture(session);

        Org org = new Org(this.shortName, this.shortNameInfoService, this.officialName, this.address, this.shortAddress, officialPerson, this.officialPosition,
                this.contractId, this.contractTime, this.organizationType, this.state, this.cardLimit, this.publicKey, this.priceOfSms,
                this.subscriptionPrice, defaultSupplier, this.INN, this.OGRN, this.mailingListReportsOnNutrition,
                this.mailingListReportsOnVisits, this.mailingListReports1, this.mailingListReports2, this.btiUnom,  this.btiUnad, this.uniqueAddressId,
                this.introductionQueue, this.additionalIdBuilding, this.statusDetailing, version, changesDSZN);
        org.setCity(city);
        org.setDistrict(district);
        org.setMunicipalDistrict(municipalDistrict);
        org.setFounder(founder);
        org.setSubordination(subordination);
        org.setLocation(location);
        org.setLongitude(longitude);
        org.setLatitude(latitude);
        org.setGuid(this.guid);
        org.setEkisId(ekisId);
        org.setEgissoId(egissoId);
        org.setPhone(this.phone);
        org.setSmsSender(this.smsSender);
        if (StringUtils.isNotEmpty(plainSsoPassword)) {
            org.setSsoPassword(plainSsoPassword);
        }
        org.setRefectoryType(refectoryType);
        org.setPayByCashier(payByCashier);
        org.setOneActiveCard(oneActiveCard);
        PhotoRegistryDirective photoD = photoRegistry ? PhotoRegistryDirective.ALLOWED : PhotoRegistryDirective.DISALLOWED;
        org.setPhotoRegistryDirective(photoD);

        if (this.coSupplier != null && this.coSupplier.getIdOfContragent() != null) {
            Contragent coSupplier = (Contragent) session.load(Contragent.class, this.coSupplier.getIdOfContragent());
            org.setCoSupplier(coSupplier);
        }

        org.setSecurityLevel(securityLevel);
        org.setTypeInitial(getOrganizationTypeInitial());
        //org.setIsRecyclingEnabled(isRecyclingEnabled);
        org.setAutoCreateCards(autoCreateCards);
        org.setNeedVerifyCardSign(needVerifyCardSign);
        org.setDenyPayPlanForTimeDifference(denyPayPlanForTimeDifference);
        org.setAllowRegistryChangeEmployee(allowRegistryChangeEmployee);
        org.setHelpdeskEnabled(helpdeskEnabled);
        org.setPreordersEnabled(preordersEnabled);
        org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
        org.setPreorderlp(preorderlp);
        org.setOrgIdFromNsi(orgIdFromNsi);
        org.setGovernmentContract(governmentContract);
        org.setUseLongCardNo(useLongCardId);
        session.save(org);
        OrgSync orgSync = new OrgSync();
        orgSync.setIdOfPacket(0L);
        orgSync.setOrg(org);
        session.persist(orgSync);

        if (menuExchangeSourceOrg!=null) DAOUtils.updateMenuExchangeLink(session, menuExchangeSourceOrg, org.getIdOfOrg());

        if (!org.getType().equals(OrganizationType.SUPPLIER)) {
            createPredefinedClientGroupsForOrg(session, org.getIdOfOrg());
        }
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

    //public OrganizationType getType() {
    //    return type;
    //}
    //
    //public void setType(OrganizationType type) {
    //    this.type = type;
    //}

    public OrganizationTypeMenu getOrganizationTypeMenu() {
        return organizationTypeMenu;
    }

    public Long getBtiUnom() {
        return btiUnom;
    }

    public void setBtiUnom(Long btiUnom) {
        this.btiUnom = btiUnom;
    }

    public Long getBtiUnad() {
        return btiUnad;
    }

    public void setBtiUnad(Long btiUnad) {
        this.btiUnad = btiUnad;
    }

    public String getIntroductionQueue() {
        return introductionQueue;
    }

    public void setIntroductionQueue(String introductionQueue) {
        this.introductionQueue = introductionQueue;
    }

    public Long getAdditionalIdBuilding() {
        return additionalIdBuilding;
    }

    public void setAdditionalIdBuilding(Long additionalIdBuilding) {
        this.additionalIdBuilding = additionalIdBuilding;
    }

    public Long getUniqueAddressId() {
        return uniqueAddressId;
    }

    public void setUniqueAddressId(Long uniqueAddressId) {
        this.uniqueAddressId = uniqueAddressId;
    }

    public String getStatusDetailing() {
        return statusDetailing;
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
    }

    public SelectItem[] getStatusDetails() {
        return statusDetails;
    }

    public SelectItem[] getSecurityLevels() {
        return securityLevels;
    }

    public void setStatusDetails(SelectItem[] statusDetails) {
        this.statusDetails = statusDetails;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getStatusTextArea() {
        return statusTextArea;
    }

    public void setStatusTextArea(String statusTextArea) {
        this.statusTextArea = statusTextArea;
    }

    public Boolean getPayByCashier() {
        return payByCashier;
    }

    public void setPayByCashier(Boolean payByCashier) {
        this.payByCashier = payByCashier;
    }

    public Boolean getOneActiveCard() {
        return oneActiveCard;
    }

    public void setOneActiveCard(Boolean oneActiveCard) {
        this.oneActiveCard = oneActiveCard;
    }

    public Boolean getPhotoRegistry() {
        return photoRegistry;
    }

    public void setPhotoRegistry(Boolean photoRegistry) {
        this.photoRegistry = photoRegistry;
    }

    public Boolean getChangesDSZN() {
        return changesDSZN;
    }

    public void setChangesDSZN(Boolean changesDSZN) {
        this.changesDSZN = changesDSZN;
    }

    public void createPredefinedClientGroupsForOrg(Session persistenceSession, Long idOfOrg) {
        ClientGroup.Predefined[] predefineds = ClientGroup.Predefined.values();

        for (ClientGroup.Predefined predefined: predefineds) {
            if (!predefined.equals(ClientGroup.Predefined.CLIENT_EMPLOYEE) && !predefined.equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN)) {
                DAOUtils.createClientGroup(persistenceSession, idOfOrg, predefined);
            }
        }
    }

    public Long getOrgIdFromNsi() { return orgIdFromNsi; }

    public void setOrgIdFromNsi(Long orgIdFromNsi) { this.orgIdFromNsi = orgIdFromNsi; }

    public Boolean getGovernmentContract() {
        return governmentContract;
    }

    public void setGovernmentContract(Boolean governmentContract) {
        this.governmentContract = governmentContract;
    }

    public Boolean getUseLongCardId() {
        return useLongCardId;
    }

    public void setUseLongCardId(Boolean useLongCardId) {
        this.useLongCardId = useLongCardId;
    }
}