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
import java.util.*;

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

    public static final String VALUE_GUID = "Guid";
    public static final String VALUE_NSI_ID = "Ид НСИ-3";
    public static final String VALUE_EKIS_ID = "ЕКИС Id";
    public static final String VALUE_EGISSO_ID = "ЕГИССО Id";
    public static final String VALUE_UNIQUE_ADDRESS_ID = "№ здания";
    public static final String VALUE_ADDRESS = "Адрес корпуса";
    public static final String VALUE_SHORT_ADDRESS = "Короткий адрес";
    public static final String VALUE_MUNICIPAL_DISTRICT = "Район";
    public static final String VALUE_SHORT_NAME = "Краткое наименование";
    public static final String VALUE_OFFICIAL_NAME = "Полное наименование";
    public static final String VALUE_UNOM = "УНОМ";
    public static final String VALUE_UNAD = "УНАД";
    public static final String VALUE_INN = "ИНН";
    public static final String VALUE_DIRECTOR = "Руководитель ОО";
    public static final String VALUE_FOUNDER = "Учредитель";
    public static final String VALUE_SUBORDINATION = "Подчиненность";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterOrgsService.class);

    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_AUTOSYNC_ON);
    }

    public void run() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        logger.info("Start import orgs from registry");
        StringBuffer logBuffer = new StringBuffer();
        try {
            RuntimeContext.getAppContext().getBean(ImportRegisterOrgsService.class).syncOrgsWithRegistry("", "", logBuffer);
        } catch (Exception e) {
            logger.error("Failed to refresh orgs from registry", e);
        }
        logger.info("Finish import orgs from registry");
    }

    @Transactional
    public boolean applyOrgRegistryChange(long idOfOrgRegistryChange, List<Long> buildingsList, Set<String> fieldFlags) throws Exception {

        Session session = (Session) em.unwrap(Session.class);
        OrgRegistryChange orgRegistryChange = DAOUtils.getOrgRegistryChange(session, idOfOrgRegistryChange);
        if ((orgRegistryChange.getOrgs() == null || orgRegistryChange.getOrgs().size() == 0) && (!orgRegistryChange.getOperationType().equals(OrgRegistryChange.DELETE_OPERATION))) {
            throw new Exception("У одной из выбранных организаций нет корпусов");
        }
        if(orgRegistryChange.getApplied()) {
            return true;
        }

        Contragent defaultSupplier = null;
        try {
            defaultSupplier = DAOService.getInstance().getContragentById(DEFAULT_SUPPLIER_ID);
        } catch (Exception e) { }

        switch(orgRegistryChange.getOperationType()) {
            case OrgRegistryChange.CREATE_OPERATION:
                try {
                    createOrg(orgRegistryChange, defaultSupplier, session, buildingsList);
                    modifyOrg(orgRegistryChange, session, buildingsList, fieldFlags);
                    break;
                } catch (Exception e) {
                    logger.error("Failed to create org", orgRegistryChange);
                    return false;
                }
            case OrgRegistryChange.MODIFY_OPERATION:
                try {
                    modifyOrg(orgRegistryChange, session, buildingsList, fieldFlags);
                    //createOrg(orgRegistryChange, defaultSupplier, session, buildingsList);
                    break;
                } catch (Exception e) {
                    logger.error("Failed to modify org", orgRegistryChange);
                    return false;
                }
            case OrgRegistryChange.DELETE_OPERATION:
                Org org = null;
                try {
                    if(orgRegistryChange.getIdOfOrg() != null) {
                        org = DAOService.getInstance().getOrg(orgRegistryChange.getIdOfOrg());
                    }
                    if (org == null && orgRegistryChange.getAdditionalId() != null && orgRegistryChange.getAdditionalId() != -1){
                        org = DAOUtils.findByAdditionalId(session,orgRegistryChange.getUniqueAddressId());
                    }
                    if (org == null && orgRegistryChange.getUnom() != -1){
                        org = DAOUtils.findByBtiUnom(session,orgRegistryChange.getUnom());
                    }
                    if (org != null) {
                        deleteOrg(org, session, buildingsList);
                    }
                    break;
                } catch (Exception e) {
                    logger.error("Failed to delete org", org);
                    return false;
                }
        }

        //orgRegistryChange.setApplied(true);
        Boolean allChildrenApplied = DAOUtils.allOrgRegistryChangeItemsApplied(session, idOfOrgRegistryChange);
        orgRegistryChange.setApplied(allChildrenApplied);
        session.persist(orgRegistryChange);
        return allChildrenApplied;
    }

    protected void createOrg(OrgRegistryChange orgRegistryChange, Contragent defaultSupplier, Session session,
            List<Long> buildingsList) throws Exception{
        Person officialPerson = new Person("", "", "");
        session.save(officialPerson);

        Date createDate = new Date();
        if (buildingsList.size() > 0){
            String shortName;
            int addToShortname = 0;
            List<Org> friendlyOrgs = new ArrayList<Org>();
            for (Long aLong : buildingsList) {
                OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
                if (orgRegistryChangeItem.getOperationType() != OrgRegistryChange.CREATE_OPERATION) {
                    continue;
                }
                if (orgRegistryChangeItem != null){
                    Org fakeOrg = DAOUtils.findOrgByShortname(session, orgRegistryChangeItem.getShortName());
                    int fakeCounter = 0;
                    while(fakeOrg != null) {
                        addToShortname++;
                        fakeOrg = DAOUtils.findOrgByShortname(session, orgRegistryChangeItem.getShortName() + " - " + addToShortname);
                        fakeCounter++;
                        if (fakeCounter > 10) break;
                    }

                    if (addToShortname > 0) {
                        shortName = orgRegistryChangeItem.getShortName() + " - " + addToShortname;
                    } else {
                        shortName = orgRegistryChangeItem.getShortName();
                    }
                    Org org = createOrg(orgRegistryChange, officialPerson, createDate, defaultSupplier, orgRegistryChangeItem, shortName);
                    if (org != null) {
                        orgRegistryChangeItem.setApplied(true);
                    }
                    if (org != null) {
                        org.setOrgStructureVersion(DAOUtils.nextVersionByOrgStucture(session));
                    }
                    addToShortname++;
                    friendlyOrgs.add(org); //все созданные организации загоняем в список, чтобы ниже связать их как дружественные

                    session.persist(org);
                    OrgSync orgSync = new OrgSync();
                    orgSync.setIdOfPacket(0L);
                    orgSync.setOrg(org);
                    session.persist(orgSync);


                    if (!org.getType().equals(OrganizationType.SUPPLIER)) {
                        createPredefinedClientGroupsForOrg(session, org.getIdOfOrg());
                    }
                }
            }
            //заполняем дружественные организации всем только что созданным организациям
            for (int i = 0; i < friendlyOrgs.size(); i++) {
                for (int j = 0; j < friendlyOrgs.size(); j++) {
                    if (friendlyOrgs.get(i).getFriendlyOrg() == null) {
                        friendlyOrgs.get(i).setFriendlyOrg(new HashSet<Org>());
                    }
                    friendlyOrgs.get(i).getFriendlyOrg().add(friendlyOrgs.get(j));
                }
                session.persist(friendlyOrgs.get(i));
            }
        }else{
            return;
        }

    }

    private Org createOrg(OrgRegistryChange orgRegistryChange, Person officialPerson, Date createDate,
            Contragent defaultSupplier, OrgRegistryChangeItem orgRegistryChangeItem, String orgShortName) throws Exception {
        String address = orgRegistryChangeItem.getAddress();
        String shortAddress = "";
        if (address == null){
            address = orgRegistryChange.getAddress();
        } else {
            String[] splitterAddress = address.split("/");
            int len = splitterAddress.length;
            try {
                shortAddress = splitterAddress[len - 2] + " /" + (splitterAddress[len - 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                shortAddress = address;
            }
        }
        Long additionalId = orgRegistryChangeItem.getAdditionalId();
        if (additionalId == null){
            additionalId = orgRegistryChange.getAdditionalId();
        }

        Org org = new Org(orgShortName, orgShortName, orgRegistryChange.getOfficialName(), address, shortAddress, officialPerson, "",
                "", createDate, orgRegistryChange.getOrganizationType(), 0, 0L, "", 0L,
                0L, defaultSupplier, orgRegistryChange.getInn(), "", "",
                "", "", "",
                orgRegistryChangeItem.getUnom(), orgRegistryChangeItem.getUnad(),
                orgRegistryChangeItem.getUniqueAddressId(), "", additionalId, "/", 0L, false);
        org.setCity(orgRegistryChange.getCity());
        org.setDistrict(orgRegistryChange.getRegion());
        org.setLocation("");
        org.setLongitude("");
        org.setLatitude("");
        org.setGuid(orgRegistryChange.getGuid());
        org.setEkisId(orgRegistryChange.getEkisId());
        org.setEgissoId(orgRegistryChange.getEgissoId());
        org.setOrgIdFromNsi(orgRegistryChange.getGlobalId());
        org.setPhone("");
        org.setSmsSender("");
        org.setTag("");
        org.setStatus(OrganizationStatus.PLANNED);
        org.setState(0);
        org.setSecurityLevel(OrganizationSecurityLevel.STANDARD);
        org.setPhotoRegistryDirective(PhotoRegistryDirective.DISALLOWED);

        return org;
    }

    public void createPredefinedClientGroupsForOrg(Session persistenceSession, Long idOfOrg) {
        ClientGroup.Predefined[] predefineds = ClientGroup.Predefined.values();

        for (ClientGroup.Predefined predefined: predefineds) {
            if (!predefined.equals(ClientGroup.Predefined.CLIENT_EMPLOYEE) && !predefined.equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN)) {
                DAOUtils.createClientGroup(persistenceSession, idOfOrg, predefined);
            }
        }
    }

    protected void modifyOrg(OrgRegistryChange orgRegistryChange, Session session, List<Long> buildingsList, Set<String> fieldFlags)
            throws Exception {
        for (Long aLong : buildingsList) {
            OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
            if (orgRegistryChangeItem.getOperationType() != OrgRegistryChange.MODIFY_OPERATION) {
                continue;
            }
            if (orgRegistryChangeItem != null) {
                Org org = DAOUtils.findOrgWithOfficialPerson(session, orgRegistryChangeItem.getIdOfOrg());
                if (org != null) {
                    //По новому алгоритму обновляем следующий набор полей оорганизации:
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_UNOM)))
                        org.setBtiUnom(orgRegistryChangeItem.getUnom());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_UNAD)))
                        org.setBtiUnad(orgRegistryChangeItem.getUnad());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_INN)))
                        org.setINN(orgRegistryChangeItem.getInn());
                    if (!RuntimeContext.getInstance().isNSI3()) {
                        if ((fieldFlags == null) || (fieldFlags.contains(VALUE_GUID)))
                            org.setGuid(orgRegistryChange.getGuid());
                    }
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_NSI_ID)))
                        org.setOrgIdFromNsi(orgRegistryChange.getGlobalId());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_EKIS_ID)))
                        org.setEkisId(orgRegistryChange.getEkisId());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_EGISSO_ID)))
                        org.setEgissoId(orgRegistryChange.getEgissoId());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_UNIQUE_ADDRESS_ID))) {
                        org.setUniqueAddressId(orgRegistryChangeItem.getUniqueAddressId());
                        org.setAdditionalIdBuilding(org.getUniqueAddressId()); // ??
                    }
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_ADDRESS))) {
                        org.setAddress(orgRegistryChangeItem.getAddress());
                        org.setCity(orgRegistryChange.getCity());
                    }
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_SHORT_ADDRESS)))
                        org.setShortAddress(orgRegistryChangeItem.getShortAddress());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_MUNICIPAL_DISTRICT)))
                        org.setMunicipalDistrict(orgRegistryChangeItem.getMunicipalDistrict());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_OFFICIAL_NAME)))
                        org.setOfficialName(orgRegistryChange.getOfficialName());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_SHORT_NAME)))
                        org.setShortNameInfoService(orgRegistryChange.getShortName());//Краткое наименование для инфосервиса
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_DIRECTOR))) {
                        org.setOfficialPerson(getPersonFromFullName(orgRegistryChangeItem.getDirector()));
                        session.persist(org.getOfficialPerson());
                    }
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_FOUNDER)))
                        org.setFounder(orgRegistryChange.getFounder());
                    if ((fieldFlags == null) || (fieldFlags.contains(VALUE_SUBORDINATION)))
                        org.setSubordination(orgRegistryChange.getSubordination());

                    orgRegistryChangeItem.setApplied(true);
                }
                session.persist(org);
            }
        }
    }

    private Person getPersonFromFullName(String fullName) {
        try {
            String[] str = fullName.split(" ");
            String surname = str[0];
            String firstName = str[1];
            String secondName = str.length > 2 ? str[2] : "";
            return new Person(firstName, surname, secondName);
        } catch (Exception e) {
            return null;
        }
    }

    protected void deleteOrg(Org org, Session session, List<Long> buildingsList)
            throws Exception {
        org.setState(0);
        for (Long aLong : buildingsList) {
            OrgRegistryChangeItem orgRegistryChangeItem = DAOUtils.getOrgRegistryChangeItem(session, aLong);
            if (orgRegistryChangeItem != null) {
                Org item = DAOUtils.findOrg(session, orgRegistryChangeItem.getIdOfOrg());
                if (item != null) {
                    item.setState(Org.INACTIVE_STATE);
                    session.persist(item);
                }
            }
        }
        session.persist(org);
    }

    @Transactional
    public StringBuffer syncOrgsWithRegistry(String orgName, String region, StringBuffer logBuffer) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Синхронизация с Реестрами от " + date + " по всем ОУ]: ";
        log(synchDate + "Производится синхронизация по всем организациям", logBuffer);

        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.NSI_ORGS, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;

        //  Итеративно загружаем организации, используя ограничения
        try {
            List<OrgInfo> orgs = nsiService.getOrgs(orgName, region);
            log(synchDate + "Получено " + orgs.size() + " записей", logBuffer);
            saveOrgs(synchDate, date, System.currentTimeMillis(), orgs, logBuffer);
        } catch (Exception e) {
            isSuccessEnd = false;
            logger.error("Failed to refresh orgs from registry", e);
        }
        SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                SecurityJournalProcess.EventType.NSI_ORGS, new Date());
        processEnd.saveWithSuccess(isSuccessEnd);
        return logBuffer;
    }

    @Transactional
    public void saveOrgs(String synchDate, String date, long ts, List<OrgInfo> orgs,
            StringBuffer logBuffer) throws Exception {
        log(synchDate + "Сохранение организаций", logBuffer);
        long createDate = System.currentTimeMillis();
        boolean addChange;
        for(OrgInfo oi : orgs) {
            OrgRegistryChange orgRegistryChange = fillOrgRegistryChange(oi, createDate);
            if (orgRegistryChange == null){
                continue;
            }
            addChange = false;
            for (OrgInfo orgInfo : oi.getOrgInfos()) {
                if (safeCompare(orgInfo.address, orgInfo.addressFrom) && safeCompare(orgInfo.shortName, orgInfo.shortNameFrom) &&
                        safeCompare(orgInfo.officialName, orgInfo.officialNameFrom) && safeCompare(orgInfo.unom, orgInfo.unomFrom) &&
                        safeCompare(orgInfo.unad, orgInfo.unadFrom) && safeCompare(orgInfo.inn, orgInfo.innFrom) &&
                        safeCompare(orgInfo.director, orgInfo.directorFrom) && safeCompare(orgInfo.egissoId, orgInfo.egissoIdFrom) &&
                        safeCompare(orgInfo.shortAddress, orgInfo.shortAddressFrom) && safeCompare(orgInfo.municipalDistrict, orgInfo.municipalDistrictFrom) &&
                        safeCompare(orgInfo.getFounder(), orgInfo.founderFrom) && safeCompare(orgInfo.getSubordination(), orgInfo.getSubordinationFrom()) &&
                        safeCompare(orgInfo.ekisId, orgInfo.ekisIdFrom)) {
                    //если полное совпадение по сверяемым полям, то запись не включаем в таблицу сверки
                } else {
                    if(orgRegistryChange.getOrgs() == null){
                        orgRegistryChange.setOrgs(new HashSet<OrgRegistryChangeItem>());
                    }
                    OrgRegistryChangeItem orgRegistryChangeItem = fillOrgRegistryChangeItem(orgRegistryChange, orgInfo, createDate);
                    orgRegistryChange.getOrgs().add(orgRegistryChangeItem);
                    addChange = true;
                }
            }
            if (addChange) {
                boolean onlyAdd = true;
                for (OrgRegistryChangeItem item : orgRegistryChange.getOrgs()) {
                    if (item.getOperationType().equals(OrgRegistryChange.MODIFY_OPERATION)) {
                        onlyAdd = false;
                        break;
                    }
                }
                if (onlyAdd) orgRegistryChange.setOperationType(OrgRegistryChange.CREATE_OPERATION);
                em.persist(orgRegistryChange);
            }
        }
    }

    private boolean safeCompare(String byOrg, String byReestrOrgInfo) {
        boolean result = false;
        String compareByOrg = (byOrg == null ? "" : byOrg);
        String compareByReestrOrgInfo = (byReestrOrgInfo == null ? "" : byReestrOrgInfo);
        if (compareByOrg.equals(compareByReestrOrgInfo)) {
            result = true;
        }
        return result;
    }

    private boolean safeCompare(Long byOrg, Long byRestrOrgInfo) {
        return safeCompare(byOrg == null ? "" : byOrg.toString(), byRestrOrgInfo == null ? "" : byRestrOrgInfo.toString());
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
                        oi.getEkisId(), oi.getEkisIdFrom(),
                        solveString(oi.getEgissoId()), solveString(oi.getEgissoIdFrom()),
                        solveString(oi.getShortAddress()), solveString(oi.getShortAddressFrom()),
                        solveString(oi.getMunicipalDistrict()), solveString(oi.getMunicipalDistrictFrom()),
                        solveString(oi.getFounder()), solveString(oi.getFounderFrom()),
                        solveString(oi.getSubordination()), solveString(oi.getSubordinationFrom()),
                        oi.getGlobalId(), oi.globalIdFrom
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
                        orgRegistryChange, oi.getShortNameSupplierFrom(),
                        oi.getOrgState(),
                        oi.getDirector(), oi.getDirectorFrom(),
                        oi.getEkisId(), oi.getEkisIdFrom(),
                        solveString(oi.getEgissoId()), solveString(oi.getEgissoIdFrom()),
                        solveString(oi.getShortAddress()), solveString(oi.getShortAddressFrom()),
                        solveString(oi.getMunicipalDistrict()), solveString(oi.getMunicipalDistrictFrom()),
                        solveString(oi.getFounder()), solveString(oi.getFounderFrom()),
                        solveString(oi.getSubordination()), solveString(oi.getSubordinationFrom()),
                        oi.getGlobalId(), oi.getGlobalIdFrom()
                );
    }

    protected String solveString(String v) {
        return v == null || StringUtils.isBlank(v) ? "" : v;
    }

    protected Boolean solveBoolean(Boolean v) {
        return (v == null) ? false : v;
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
        protected Integer orgState;
        protected Long createDate;
        protected Integer operationType;

        protected Boolean applied = false;

        protected OrganizationType organizationType;
        protected OrganizationType organizationTypeFrom;
        protected String shortName;
        protected String shortNameFrom;
        protected String shortNameSupplierFrom;
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

        private String directorFullName;
        private String OGRN;
        private String state;
        private String director;
        private String directorFrom;
        private Long ekisId;
        private Long ekisIdFrom;
        private String egissoId;
        private String egissoIdFrom;
        private String shortAddress;
        private String shortAddressFrom;
        private String municipalDistrict;
        private String municipalDistrictFrom;
        private String founder;
        private String founderFrom;
        private String subordination;
        private String subordinationFrom;
        private Long globalId;
        private Long globalIdFrom;

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
            this.shortName = shortName == null ? null : solveLength(shortName);
        }

        public String getShortNameFrom() {
            return shortNameFrom;
        }

        public void setShortNameFrom(String shortNameFrom) {
            this.shortNameFrom = shortNameFrom == null ? null : solveLength(shortNameFrom);
        }

        public String getShortNameSupplierFrom() {
            return shortNameSupplierFrom;
        }

        public void setShortNameSupplierFrom(String shortNameSupplierFrom) {
            this.shortNameSupplierFrom = shortNameSupplierFrom;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName == null ? null : solveLength(officialName.trim());
        }

        public String getOfficialNameFrom() {
            return officialNameFrom;
        }

        public void setOfficialNameFrom(String officialNameFrom) {
            this.officialNameFrom = officialNameFrom == null ? null : solveLength(officialNameFrom);
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address == null ? null : solveLength(address);
        }

        private String solveLength(String str) {
            if (str.length() > 255) {
                return str.substring(0, 255);
            } else {
                return str;
            }
        }

        public String getAddressFrom() {
            return addressFrom;
        }

        public void setAddressFrom(String addressFrom) {
            this.addressFrom = addressFrom == null ? null : solveLength(addressFrom);
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city == null ? null : solveLength(city);
        }

        public String getCityFrom() {
            return cityFrom;
        }

        public void setCityFrom(String cityFrom) {
            this.cityFrom = cityFrom == null ? null : solveLength(cityFrom);
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region == null ? null : solveLength(region);
        }

        public String getRegionFrom() {
            return regionFrom;
        }

        public void setRegionFrom(String regionFrom) {
            this.regionFrom = regionFrom == null ? null : solveLength(regionFrom);
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

        public Integer getOrgState() {
            return orgState;
        }

        public void setOrgState(Integer orgState) {
            this.orgState = orgState;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getDirectorFrom() {
            return directorFrom;
        }

        public void setDirectorFrom(String directorFrom) {
            this.directorFrom = directorFrom;
        }

        public Long getEkisId() {
            return ekisId;
        }

        public void setEkisId(Long ekisId) {
            this.ekisId = ekisId;
        }

        public Long getEkisIdFrom() {
            return ekisIdFrom;
        }

        public void setEkisIdFrom(Long ekisIdFrom) {
            this.ekisIdFrom = ekisIdFrom;
        }

        public String getEgissoId() {
            return egissoId;
        }

        public void setEgissoId(String egissoId) {
            this.egissoId = egissoId;
        }

        public String getEgissoIdFrom() {
            return egissoIdFrom;
        }

        public void setEgissoIdFrom(String egissoIdFrom) {
            this.egissoIdFrom = egissoIdFrom;
        }

        public String getShortAddress() {
            return shortAddress;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }

        public String getShortAddressFrom() {
            return shortAddressFrom;
        }

        public void setShortAddressFrom(String shortAddressFrom) {
            this.shortAddressFrom = shortAddressFrom;
        }

        public String getMunicipalDistrict() {
            return municipalDistrict;
        }

        public void setMunicipalDistrict(String municipalDistrict) {
            this.municipalDistrict = municipalDistrict;
        }

        public String getMunicipalDistrictFrom() {
            return municipalDistrictFrom;
        }

        public void setMunicipalDistrictFrom(String municipalDistrictFrom) {
            this.municipalDistrictFrom = municipalDistrictFrom;
        }

        public String getFounder() {
            return founder;
        }

        public void setFounder(String founder) {
            this.founder = founder;
        }

        public String getFounderFrom() {
            return founderFrom;
        }

        public void setFounderFrom(String founderFrom) {
            this.founderFrom = founderFrom;
        }

        public String getSubordination() {
            return subordination;
        }

        public void setSubordination(String subordination) {
            this.subordination = subordination;
        }

        public String getSubordinationFrom() {
            return subordinationFrom;
        }

        public void setSubordinationFrom(String subordinationFrom) {
            this.subordinationFrom = subordinationFrom;
        }

        public Long getGlobalId() {
            return globalId;
        }

        public void setGlobalId(Long globalId) {
            this.globalId = globalId;
        }

        public Long getGlobalIdFrom() {
            return globalIdFrom;
        }

        public void setGlobalIdFrom(Long globalIdFrom) {
            this.globalIdFrom = globalIdFrom;
        }
    }
}