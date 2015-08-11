/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.01.15
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ImportRegisterOrgsService {

    @PersistenceContext(unitName = "processorPU")
    private javax.persistence.EntityManager em;

    @Autowired
    OrgMskNSIService nsiService;

    public static final long DEFAULT_SUPPLIER_ID = 28L;
    public static final int CREATE_OPERATION = 1;
    public static final int DELETE_OPERATION = 2;
    public static final int MODIFY_OPERATION = 3;
    public static final int MOVE_OPERATION = 4;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterOrgsService.class);

    @Transactional
    public boolean applyOrgRegistryChange(long idOfOrgRegistryChange, List<Long> buildingsList) {



        Session session = (Session) em.unwrap(Session.class);
        OrgRegistryChange orgRegistryChange = DAOUtils.getOrgRegistryChange(session, idOfOrgRegistryChange);
        if(orgRegistryChange.getApplied()) {
            return true;
        }
        Org org = null;
        Contragent defaultSupplier = null;
        try {
            defaultSupplier = DAOService.getInstance().getContragentById(DEFAULT_SUPPLIER_ID);
        } catch (Exception e) { }
        if(orgRegistryChange.getIdOfOrg() != null) {
            org = DAOService.getInstance().getOrg(orgRegistryChange.getIdOfOrg());
        }
        if (org == null && orgRegistryChange.getAdditionalId() != -1){
            org = DAOUtils.findByAdditionalId(session,orgRegistryChange.getAdditionalId());
        }
        if (org == null && orgRegistryChange.getUnom() != -1){
            org = DAOUtils.findByBtiUnom(session,orgRegistryChange.getUnom());
        }
        switch(orgRegistryChange.getOperationType()) {
            case OrgRegistryChange.CREATE_OPERATION:
                try {
                    createOrg(orgRegistryChange, defaultSupplier, session, buildingsList);
                    break;
                } catch (Exception e) {
                    logger.error("Failed to create org", org);
                    return false;
                }
            case OrgRegistryChange.MODIFY_OPERATION:
                try {
                    //modifyOrg(orgRegistryChange, org, session, buildingsList);
                    modifyOrg(orgRegistryChange, session, buildingsList);
                    break;
                } catch (Exception e) {
                    logger.error("Failed to modify org", org);
                    return false;
                }
            case OrgRegistryChange.DELETE_OPERATION:
                try {
                    deleteOrg(orgRegistryChange, org, session, buildingsList);
                    break;
                } catch (Exception e) {
                    logger.error("Failed to delete org", org);
                    return false;
                }
        }

        orgRegistryChange.setApplied(true);
        session.persist(orgRegistryChange);
        return true;
    }

    protected void createOrg(OrgRegistryChange orgRegistryChange, Contragent defaultSupplier, Session session,
            List<Long> buildingsList) throws Exception{
        Person officialPerson = new Person("", "", "");
        session.save(officialPerson);

        Date createDate = new Date();
        if (buildingsList.size() > 0){
            String shortName;
            int addToShortname = 0;
            for (Long aLong : buildingsList) {
                OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
                if (orgRegistryChangeItem != null){
                    /*Org org = createOrg(orgRegistryChange, officialPerson, createDate, defaultSupplier,orgRegistryChangeItem.getAddress(), orgRegistryChangeItem.getAdditionalId());
                    session.persist(org);*/
                    if (addToShortname > 0) {
                        shortName = orgRegistryChangeItem.getShortName() + " - " + addToShortname;
                    } else {
                        shortName = orgRegistryChangeItem.getShortName();
                    }
                    Org org = createOrg(orgRegistryChange, officialPerson, createDate, defaultSupplier, orgRegistryChangeItem, shortName);
                    addToShortname++;
                    session.persist(org);
                }
            }
        }else{
            return;
            //Org org = createOrg(orgRegistryChange, officialPerson, createDate, defaultSupplier, null, null);
            //session.persist(org);
        }

    }

    private Org createOrg(OrgRegistryChange orgRegistryChange, Person officialPerson, Date createDate,
            Contragent defaultSupplier, OrgRegistryChangeItem orgRegistryChangeItem, String orgShortName) throws Exception {
        String address = orgRegistryChangeItem.getAddress();
        if (address == null){
            address = orgRegistryChange.getAddress();
        }
        Long additionalId = orgRegistryChangeItem.getAdditionalId();
        if (additionalId == null){
            additionalId = orgRegistryChange.getAdditionalId();
        }
        /* Создание организациииии
        Org(String shortName, String officialName, String address, Person officialPerson, String officialPosition,
                String contractId, Date contractTime, OrganizationType type, int state, long cardLimit, String publicKey, Long priceOfSms,
                Long subscriptionPrice, Contragent defaultSupplier, String INN, String OGRN, String mailingListReportsOnNutrition,
                String mailingListReportsOnVisits, String mailingListReports1, String mailingListReports2,
                Long btiUnom, Long btiUnad, Long uniqueAddressId, String introductionQueue, Long additionalIdBuilding, String statusDetailing)*/
        Org org = new Org(orgShortName, orgRegistryChange.getOfficialName(), address, officialPerson, "",
                "", createDate, orgRegistryChange.getOrganizationType(), 0, 0L, "", 0L,
                0L, defaultSupplier, orgRegistryChange.getInn(), "", "",
                "", "", "",
                orgRegistryChangeItem.getUnom(), orgRegistryChangeItem.getUnad(),
                orgRegistryChangeItem.getUniqueAddressId(), "", additionalId, "/");
        org.setCity(orgRegistryChange.getCity());
        org.setDistrict(orgRegistryChange.getRegion());
        org.setLocation("");
        org.setLongitude("");
        org.setLatitude("");
        org.setGuid(orgRegistryChange.getGuid());
        org.setPhone("");
        org.setSmsSender("");
        org.setTag("");
        org.setStatus(OrganizationStatus.PLANNED);
        org.setState(0);

        return org;
    }

    /*private Org createOrg(OrgRegistryChange orgRegistryChange, Person officialPerson, Date createDate,
            Contragent defaultSupplier, String address, Long additionalId) throws Exception {
        if (address == null){
            address = orgRegistryChange.getAddress();
        }
        if (additionalId == null){
            additionalId = orgRegistryChange.getAdditionalId();
        }
        Org org = new Org(orgRegistryChange.getShortName(), orgRegistryChange.getOfficialName(),
                address, officialPerson, "",
                "", createDate, orgRegistryChange.getOrganizationType(), 0, 0L, "", 0L,
                0L, defaultSupplier, "", "", "",
                "", "", "", orgRegistryChange.getUnom(), orgRegistryChange.getUnad(),
                orgRegistryChange.getUniqueAddressId(), "", additionalId, "/");
        org.setCity(orgRegistryChange.getCity());
        org.setDistrict(orgRegistryChange.getRegion());
        org.setLocation("");
        org.setLongitude("");
        org.setLatitude("");
        org.setGuid(orgRegistryChange.getGuid());
        org.setPhone("");
        org.setSmsSender("");
        org.setTag("");
        org.setStatus(OrganizationStatus.PLANNED);
        org.setState(0);

        return org;
    }*/

    protected void modifyOrg(OrgRegistryChange orgRegistryChange, Session session, List<Long> buildingsList)
            throws Exception {
        for (Long aLong : buildingsList) {
            OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
            if (orgRegistryChangeItem != null) {
                Org org = DAOUtils.findOrg(session, orgRegistryChangeItem.getIdOfOrg());
                if (org != null) {
                    org.setAddress(orgRegistryChangeItem.getAddress());
                    org.setCity(orgRegistryChange.getCity());
                    org.setDistrict(orgRegistryChange.getRegion());

                    org.setBtiUnom(orgRegistryChangeItem.getUnom());
                    org.setBtiUnad(orgRegistryChangeItem.getUnad());
                    org.setUniqueAddressId(orgRegistryChangeItem.getUniqueAddressId());

                    org.setGuid(orgRegistryChange.getGuid());
                    org.setAdditionalIdBuilding(aLong);
                    org.setINN(orgRegistryChangeItem.getInn());
                }
                session.persist(org);
            }
        }
    }

    /*protected void modifyOrg(OrgRegistryChange orgRegistryChange, Org org, Session session, List<Long> buildingsList)
            throws Exception {
        org.setShortName(orgRegistryChange.getShortName());
        org.setOfficialName(orgRegistryChange.getOfficialName());

        org.setAddress(orgRegistryChange.getAddress());
        org.setCity(orgRegistryChange.getCity());
        org.setDistrict(orgRegistryChange.getRegion());

        org.setBtiUnom(orgRegistryChange.getUnom());
        org.setBtiUnad(orgRegistryChange.getUnad());
        org.setUniqueAddressId(orgRegistryChange.getUniqueAddressId());

        org.setGuid(orgRegistryChange.getGuid());
        org.setAdditionalIdBuilding(orgRegistryChange.getAdditionalId());

        for (Long aLong : buildingsList) {
            OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
            if (orgRegistryChangeItem != null){
                Org byAdditionalId = DAOUtils.findByAdditionalId(session, aLong);
                if (byAdditionalId == null){
                    Person officialPerson = new Person("", "", "");
                    session.save(officialPerson);

                    Date createDate = new Date();

                    Contragent defaultSupplier = null;
                    try {
                        defaultSupplier = DAOService.getInstance().getContragentById(DEFAULT_SUPPLIER_ID);
                    } catch (Exception e) { }
                    byAdditionalId = createOrg(orgRegistryChange, officialPerson, createDate, defaultSupplier,orgRegistryChangeItem);
                }else{
                    byAdditionalId.setShortName(orgRegistryChange.getShortName());
                    byAdditionalId.setOfficialName(orgRegistryChange.getOfficialName());

                    byAdditionalId.setAddress(orgRegistryChangeItem.getAddress());
                    byAdditionalId.setCity(orgRegistryChange.getCity());
                    byAdditionalId.setDistrict(orgRegistryChange.getRegion());

                    byAdditionalId.setBtiUnom(orgRegistryChangeItem.getUnom());
                    byAdditionalId.setBtiUnad(orgRegistryChange.getUnad());
                    byAdditionalId.setUniqueAddressId(orgRegistryChange.getUniqueAddressId());

                    byAdditionalId.setGuid(orgRegistryChange.getGuid());
                    byAdditionalId.setAdditionalIdBuilding(aLong);
                }
                session.persist(byAdditionalId);
            }


        }
        session.persist(org);
    }*/

    protected void deleteOrg(OrgRegistryChange orgRegistryChange, Org org, Session session, List<Long> buildingsList) {
        org.setState(OrganizationStatus.INACTIVE.ordinal());
        for (Long aLong : buildingsList) {
            Org byAdditionalId = DAOUtils.findByAdditionalId(session, aLong);
            byAdditionalId.setState(OrganizationStatus.INACTIVE.ordinal());
            session.persist(byAdditionalId);
        }
        session.persist(org);
    }

    @Transactional
    public StringBuffer syncOrgsWithRegistry(String orgName, StringBuffer logBuffer) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Синхронизация с Реестрами от " + date + " о всем ОУ]: ";
        log(synchDate + "Производится синхронизация по всем организациям", logBuffer);

        //  Итеративно загружаем организации, используя ограничения
        List<OrgInfo> orgs = nsiService.getOrgs(orgName);
        log(synchDate + "Получено " + orgs.size() + " записей", logBuffer);
        saveOrgs(synchDate, date, System.currentTimeMillis(), orgs, logBuffer);
        return logBuffer;
    }

    @Transactional
    public void saveOrgs(String synchDate, String date, long ts, List<OrgInfo> orgs,
            StringBuffer logBuffer) throws Exception {
        log(synchDate + "Сохранение организаций", logBuffer);
        long createDate = System.currentTimeMillis();
        OrgRegistryChangeItem orgRegistryChangeItem = null;
        for(OrgInfo oi : orgs) {
            OrgRegistryChange orgRegistryChange = fillOrgRegistryChange(oi, createDate);
            if (orgRegistryChange == null){
                continue;
            }
            for (OrgInfo orgInfo : oi.getOrgInfos()) {
                if(orgRegistryChange.getOrgs() == null){
                    orgRegistryChange.setOrgs(new HashSet<OrgRegistryChangeItem>());
                }
                orgRegistryChangeItem = fillOrgRegistryChangeItem(orgRegistryChange, orgInfo, createDate);
                orgRegistryChange.getOrgs().add(orgRegistryChangeItem);
            }
            em.persist(orgRegistryChange);
        }
    }

    private OrgRegistryChange fillOrgRegistryChange(OrgInfo oi, long createDate) {
        return new OrgRegistryChange
                (oi.getIdOfOrg(),

                        oi.getOrganizationType(), oi.getOrganizationTypeFrom(),
                        solveString(oi.getShortName()), oi.getShortNameFrom(),
                        solveString(oi.getOfficialName()), oi.getOfficialNameFrom(),
                        createDate, oi.getOperationType(),

                        false,

                        solveString(oi.getAddress()), oi.getAddressFrom(),
                        solveString(oi.getCity()), oi.getCityFrom(),
                        solveString(oi.getRegion()), oi.getRegionFrom(),

                        oi.getUnom(), oi.getUnomFrom(),
                        oi.getUnad(), oi.getUnadFrom(),
                        oi.getUniqueAddressId(), oi.getUniqueAddressIdFrom(),
                        solveString(oi.getInn()), oi.getInnFrom(),

                        solveString(oi.getGuid()), oi.getGuidFrom(),
                        oi.getAdditionalId() == null ? -1L : oi.getAdditionalId(),
                        oi.getInterdistrictCouncil(),
                        oi.getInterdistrictCouncilFrom(),
                        oi.getInterdistrictCouncilChief(),
                        oi.getInterdistrictCouncilChiefFrom()
                );
    }

    private OrgRegistryChangeItem fillOrgRegistryChangeItem(OrgRegistryChange orgRegistryChange, OrgInfo oi, long createDate) {
        return new OrgRegistryChangeItem
                (oi.getIdOfOrg(),

                        oi.getOrganizationType(), oi.getOrganizationTypeFrom(),
                        solveString(oi.getShortName()), oi.getShortNameFrom(),
                        solveString(oi.getOfficialName()), oi.getOfficialNameFrom(),
                        createDate, oi.getOperationType(),

                        false,

                        solveString(oi.getAddress()), oi.getAddressFrom(),
                        solveString(oi.getCity()), oi.getCityFrom(),
                        solveString(oi.getRegion()), oi.getRegionFrom(),

                        oi.getUnom(), oi.getUnomFrom(),
                        oi.getUnad(), oi.getUnadFrom(),
                        oi.getUniqueAddressId(), oi.getUniqueAddressIdFrom(),
                        solveString(oi.getInn()), oi.getInnFrom(),

                        solveString(oi.getGuid()), oi.getGuidFrom(),
                        oi.getAdditionalId() == null ? -1L : oi.getAdditionalId(),
                        oi.getInterdistrictCouncil(),
                        oi.getInterdistrictCouncilFrom(),
                        oi.getInterdistrictCouncilChief(),
                        oi.getInterdistrictCouncilChiefFrom(),
                        orgRegistryChange

                );
    }

    protected String solveString(String v) {
        return v == null || StringUtils.isBlank(v) ? "" : v;
    }

    public static void log(String str, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append('\n');
        }
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    public static class OrgInfo {
        /*
        Поля _From - данные из ИС ПП, поля без _From - данные из АИС Реестр.
         */
        protected Long idOfOrg;
        protected Long createDate;
        protected Integer operationType;

        protected Boolean applied = false;

        protected OrganizationType organizationType;
        protected OrganizationType organizationTypeFrom;
        protected String shortName;
        protected String shortNameFrom;
        protected String officialName;
        protected String officialNameFrom;

        protected String address;
        protected String addressFrom;
        protected String city;
        protected String cityFrom;
        protected String region;
        protected String regionFrom;

        protected Long unom;
        protected Long unomFrom;
        protected Long unad;
        protected Long unadFrom;
        protected Long uniqueAddressId;
        protected Long uniqueAddressIdFrom;
        protected String inn;
        protected String innFrom;

        protected String guid;
        protected String guidFrom;
        protected Long additionalId;
        protected Long registeryPrimaryId;

        protected String interdistrictCouncil;
        protected String interdistrictCouncilFrom;
        protected String interdistrictCouncilChief;
        protected String interdistrictCouncilChiefFrom;
        private String directorFullName;
        private String OGRN;
        private String state;

        private List<OrgInfo> orgInfos = new LinkedList<OrgInfo>();
        private Boolean mainBuilding;


        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Long createDate) {
            this.createDate = createDate;
        }

        public Integer getOperationType() {
            return operationType;
        }

        public void setOperationType(Integer operationType) {
            this.operationType = operationType;
        }

        public Boolean getApplied() {
            return applied;
        }

        public void setApplied(Boolean applied) {
            this.applied = applied;
        }

        public OrganizationType getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(OrganizationType organizationType) {
            this.organizationType = organizationType;
        }

        public OrganizationType getOrganizationTypeFrom() {
            return organizationTypeFrom;
        }

        public void setOrganizationTypeFrom(OrganizationType organizationTypeFrom) {
            this.organizationTypeFrom = organizationTypeFrom;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName == null ? null : shortName;
        }

        public String getShortNameFrom() {
            return shortNameFrom;
        }

        public void setShortNameFrom(String shortNameFrom) {
            this.shortNameFrom = shortNameFrom == null ? null : shortNameFrom;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName == null ? null : officialName.trim();
        }

        public String getOfficialNameFrom() {
            return officialNameFrom;
        }

        public void setOfficialNameFrom(String officialNameFrom) {
            this.officialNameFrom = officialNameFrom == null ? null : officialNameFrom;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address == null ? null : address;
        }

        public String getAddressFrom() {
            return addressFrom;
        }

        public void setAddressFrom(String addressFrom) {
            this.addressFrom = addressFrom == null ? null : addressFrom;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city == null ? null : city;
        }

        public String getCityFrom() {
            return cityFrom;
        }

        public void setCityFrom(String cityFrom) {
            this.cityFrom = cityFrom == null ? null : cityFrom;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region == null ? null : region;
        }

        public String getRegionFrom() {
            return regionFrom;
        }

        public void setRegionFrom(String regionFrom) {
            this.regionFrom = regionFrom == null ? null : regionFrom;
        }

        public Long getUnom() {
            return unom;
        }

        public void setUnom(Long unom) {
            this.unom = unom;
        }

        public Long getUnomFrom() {
            return unomFrom;
        }

        public void setUnomFrom(Long unomFrom) {
            this.unomFrom = unomFrom;
        }

        public Long getUnad() {
            return unad;
        }

        public void setUnad(Long unad) {
            this.unad = unad;
        }

        public Long getUnadFrom() {
            return unadFrom;
        }

        public void setUnadFrom(Long unadFrom) {
            this.unadFrom = unadFrom;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid == null ? null : guid;
        }

        public String getGuidFrom() {
            return guidFrom;
        }

        public void setGuidFrom(String guidFrom) {
            this.guidFrom = guidFrom == null ? null : guidFrom;
        }

        public Long getAdditionalId() {
            return additionalId;
        }

        public void setAdditionalId(Long additionalId) {
            this.additionalId = additionalId;
        }

        public Long getRegisteryPrimaryId() {
            return registeryPrimaryId;
        }

        public void setRegisteryPrimaryId(Long registeryPrimaryId) {
            this.registeryPrimaryId = registeryPrimaryId;
        }

        public String getInterdistrictCouncil() {
            return interdistrictCouncil;
        }

        public void setInterdistrictCouncil(String interdistrictCouncil) {
            this.interdistrictCouncil = interdistrictCouncil;
        }

        public String getInterdistrictCouncilFrom() {
            return interdistrictCouncilFrom;
        }

        public void setInterdistrictCouncilFrom(String interdistrictCouncilFrom) {
            this.interdistrictCouncilFrom = interdistrictCouncilFrom;
        }

        public String getInterdistrictCouncilChief() {
            return interdistrictCouncilChief;
        }

        public void setInterdistrictCouncilChief(String interdistrictCouncilChief) {
            this.interdistrictCouncilChief = interdistrictCouncilChief;
        }

        public String getInterdistrictCouncilChiefFrom() {
            return interdistrictCouncilChiefFrom;
        }

        public void setInterdistrictCouncilChiefFrom(String interdistrictCouncilChiefFrom) {
            this.interdistrictCouncilChiefFrom = interdistrictCouncilChiefFrom;
        }


        public void setDirectorFullName(String directorFullName) {
            this.directorFullName = directorFullName;
        }

        public String getDirectorFullName() {
            return directorFullName;
        }

        public void setOGRN(String OGRN) {
            this.OGRN = OGRN;
        }

        public String getOGRN() {
            return OGRN;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public List<OrgInfo> getOrgInfos() {
            return orgInfos;
        }

        public void setOrgInfos(List<OrgInfo> orgInfos) {
            this.orgInfos = orgInfos;
        }

        public void setMainBuilding(Boolean mainBuilding) {
            this.mainBuilding = mainBuilding;
        }

        public Boolean getMainBuilding() {
            return mainBuilding;
        }

        public Long getUniqueAddressId() {
            return uniqueAddressId;
        }

        public void setUniqueAddressId(Long uniqueAddressId) {
            this.uniqueAddressId = uniqueAddressId;
        }

        public Long getUniqueAddressIdFrom() {
            return uniqueAddressIdFrom;
        }

        public void setUniqueAddressIdFrom(Long uniqueAddressIdFrom) {
            this.uniqueAddressIdFrom = uniqueAddressIdFrom;
        }

        public String getInn() {
            return inn;
        }

        public void setInn(String inn) {
            this.inn = inn;
        }

        public String getInnFrom() {
            return innFrom;
        }

        public void setInnFrom(String innFrom) {
            this.innFrom = innFrom;
        }
    }
}