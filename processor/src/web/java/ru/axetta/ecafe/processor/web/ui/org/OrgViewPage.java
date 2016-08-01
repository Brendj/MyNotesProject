/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.*;

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
    private String shortNameInfoService;
    private String officialName;
    private String tag;
    private String address;
    private String shortAddress;
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
    private String coSupplierName;
    private String INN;
    private String OGRN;
    private String mailingListReportsOnNutrition;
    private String mailingListReportsOnVisits;
    private String mailingListReports1;
    private String mailingListReports2;
    private String guid;
    private List<CategoryOrg> categoryOrg;
    private String configurationProviderName;
    private List<Long> idOfOrgList;
    private String filterOrgs = "Не выбрано";
    private String friendlyFilterOrgs = "Не выбрано";
    private boolean mainBuidling;
    private String city;
    private String district;
    private String location;
    private String latitude;
    private String longitude;
    private Integer refectoryType;
    private Boolean commodityAccountingParam;
    private Boolean usePlanOrders;
    // тип организации "ПОТРЕБИТЕЛЬ / ПОСТАВЩИК"
    private OrganizationType organizationType;
    private String refectoryTypeStringRepresentation;
    private Long btiUnom;
    private Long btiUnad;
    private String introductionQueue;
    private Long additionalIdBuilding;
    private Long uniqueAddressId;
    private String statusDetailing;
    private String securityLevel;

    private String interdistrictCouncil; //В каком межрайонном совете состоит ОО
    private String interdistrictCouncilChief ; //Председателем какого межрайонного совета является руководитель ОО

    private Boolean payByCashier;
    private Boolean oneActiveCard;

    private SelectItem[] statusDetails = readStatusDetailsComboMenuItems();

    private SelectItem[] readStatusDetailsComboMenuItems() {
        SelectItem[] items = new SelectItem[5];
        items[0] = new SelectItem(0, "");
        items[1] = new SelectItem(1, "Запланировано подключение");
        items[2] = new SelectItem(2, "На ремонте");
        items[3] = new SelectItem(3, "Закрыто");
        items[4] = new SelectItem(4, "Другое");
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

    public String getRefectoryTypeStringRepresentation() {
        if ((refectoryType == null) || (refectoryType >= Org.REFECTORY_TYPE_NAMES.length)) {
            refectoryType = -1;
            refectoryTypeStringRepresentation = "";
        } else if (refectoryType == -1) {
            refectoryTypeStringRepresentation = "";
        } else {
            refectoryTypeStringRepresentation = Org.REFECTORY_TYPE_NAMES[refectoryType];
        }
        return refectoryTypeStringRepresentation;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.shortNameInfoService = org.getShortNameInfoService();
        this.officialName = org.getOfficialName();
        this.tag = org.getTag();
        this.city = org.getCity();
        this.district = org.getDistrict();
        this.location = org.getLocation();
        this.longitude = org.getLongitude();
        this.latitude = org.getLatitude();
        this.address = org.getAddress();
        this.shortAddress = org.getShortAddress();
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
        this.idOfPacket = org.getOrgSync().getIdOfPacket();
        this.smsSender = org.getSmsSender();
        this.priceOfSms = org.getPriceOfSms();
        this.subscriptionPrice = org.getSubscriptionPrice();
        this.defaultSupplierName = org.getDefaultSupplier().getContragentName();
        if (org.getCoSupplier() != null) {
            this.coSupplierName = org.getCoSupplier().getContragentName();
        }
        this.INN=org.getINN();
        this.OGRN=org.getOGRN();
        this.guid = org.getGuid();
        this.commodityAccountingParam = org.getCommodityAccounting();
        this.usePlanOrders = org.getUsePlanOrders();
        this.categoryOrg= new LinkedList<CategoryOrg>();
        if(!org.getCategories().isEmpty()){
           for (CategoryOrg co: org.getCategories()){
               this.categoryOrg.add(co);
           }
        }

        this.refectoryType = org.getRefectoryType();
        getRefectoryTypeStringRepresentation();
        this.organizationType= org.getType();

        ////  menu exchange source
        Long menuExchangeSourceOrgId = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (menuExchangeSourceOrgId == null) {
            menuExchangeSourceOrgName = "";
        } else {
            Org menuExchangeSourceOrg = (Org) session.load(Org.class, menuExchangeSourceOrgId);
            menuExchangeSourceOrgName = menuExchangeSourceOrg.getShortName();
        }

        ConfigurationProvider configurationProvider = org.getConfigurationProvider();
        if (configurationProvider == null) {
            configurationProviderName = "";
        } else {
           configurationProviderName = configurationProvider.getName();
        }
        Set<Org> friendlyOrganisation = org.getFriendlyOrg();
        friendlyFilterOrgs = "Не выбрано";
        if(!(friendlyOrganisation==null || friendlyOrganisation.isEmpty())){
            StringBuilder stringBuilder = new StringBuilder();
            for (Org friendlyOrg: org.getFriendlyOrg()){
                stringBuilder.append(friendlyOrg.getShortName());
                stringBuilder.append("; ");
            }
            friendlyFilterOrgs = stringBuilder.toString();
        }
        this.mainBuidling = org.isMainBuilding();
        this.mailingListReportsOnNutrition = org.getMailingListReportsOnNutrition();
        this.mailingListReportsOnVisits = org.getMailingListReportsOnVisits();
        this.mailingListReports1 = org.getMailingListReports1();
        this.mailingListReports2 = org.getMailingListReports2();
        this.btiUnom = org.getBtiUnom();
        this.btiUnad = org.getBtiUnad();
        this.additionalIdBuilding = org.getAdditionalIdBuilding();
        this.uniqueAddressId = org.getUniqueAddressId();
        this.introductionQueue = org.getIntroductionQueue();
        this.statusDetailing = org.getStatusDetailing();

        String[] strings = this.statusDetailing.split("/");

        if (strings.length > 0) {
            if (strings[0].equals("")) {
                this.statusDetail = "0";
            } else if (strings[0].equals("Запланировано подключение")) {
                this.statusDetail = "1";
            } else if (strings[0].equals("На ремонте")) {
                this.statusDetail = "2";
            } else if (strings[0].equals("Закрыто")) {
                this.statusDetail = "3";
            } else if (strings[0].equals("Другое")) {
                this.statusDetail = "4";
            }
        } else {
            this.statusDetail = "";
        }

        if (strings.length == 2) {
            this.statusTextArea = strings[1];
        } else {
            this.statusTextArea = "";
        }

        this.payByCashier = org.getPayByCashier();
        this.oneActiveCard = org.getOneActiveCard();


        this.interdistrictCouncil = org.getInterdistrictCouncil();
        this.interdistrictCouncilChief = org.getInterdistrictCouncilChief();
        this.securityLevel = org.getSecurityLevel().toString();
    }

    public String getFriendlyFilterOrgs() {
        return friendlyFilterOrgs;
    }

    public void setFriendlyFilterOrgs(String friendlyFilterOrgs) {
        this.friendlyFilterOrgs = friendlyFilterOrgs;
    }

    public String getFilterOrgs() {
        return filterOrgs;
    }

    public String getConfigurationProviderName() {
        return configurationProviderName;
    }

    public void setConfigurationProviderName(String configurationProviderName) {
        this.configurationProviderName = configurationProviderName;
    }

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

    public String getTag() {
        return tag;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getShortAddress() {
        return shortAddress;
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

    public Integer getRefectoryType() {
        return refectoryType;
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

    public Boolean getUsePlanOrders() {
        return usePlanOrders;
    }

    public void setUsePlanOrders(Boolean usePlanOrders) {
        this.usePlanOrders = usePlanOrders;
    }

    public Boolean getCommodityAccountingParam() {
        return commodityAccountingParam;
    }

    public void setCommodityAccountingParam(Boolean commodityAccountingParam) {
        this.commodityAccountingParam = commodityAccountingParam;
    }

    public boolean isMainBuidling() {
        return mainBuidling;
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

    public String getIntroductionQueue() {
        return introductionQueue;
    }

    public void setIntroductionQueue(String introductionQueue) {
        this.introductionQueue = introductionQueue;
    }

    public Long getBtiUnad() {
        return btiUnad;
    }

    public void setBtiUnad(Long btiUnad) {
        this.btiUnad = btiUnad;
    }

    public Long getBtiUnom() {
        return btiUnom;
    }

    public void setBtiUnom(Long btiUnom) {
        this.btiUnom = btiUnom;
    }

    public String getStatusDetailing() {
        return statusDetailing;
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
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

    public List getDetailsItem() {
        return detailsItem;
    }

    public void setDetailsItem(List detailsItem) {
        this.detailsItem = detailsItem;
    }

    public SelectItem[] getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(SelectItem[] statusDetails) {
        this.statusDetails = statusDetails;
    }

    public Object updateBalance(){
        for (int i=0;i<10000000;i++);
        return null;
    }

    public Boolean getPayByCashier() {
        return payByCashier;
    }

    public Boolean getOneActiveCard() {
        return oneActiveCard;
    }

    public String getInterdistrictCouncil() {
        return interdistrictCouncil;
    }

    public String getInterdistrictCouncilChief() {
        return interdistrictCouncilChief;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public String getCoSupplierName() {
        return coSupplierName;
    }

    public void setCoSupplierName(String coSupplierName) {
        this.coSupplierName = coSupplierName;
    }
}