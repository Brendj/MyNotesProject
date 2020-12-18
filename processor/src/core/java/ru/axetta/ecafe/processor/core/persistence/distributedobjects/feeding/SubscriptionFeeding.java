package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 02.09.13
 * Time: 13:35
 * Подписка на Абонементное питание
 */
public class SubscriptionFeeding extends DistributedObject{

    private Long idOfClient;
    private Client client;

    /* юридическая дата подписки, всегда присутствует */
    private Date dateCreateService;
    private Date dateDeactivateService;
    //дата
    private Date dateActivateSubscription;
    private Date lastDatePauseSubscription;

    private Boolean wasSuspended;

    private Staff staff;
    private String guidOfStaff;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;
    private SubscriptionFeedingType feedingType;
    private Long idOfOrgLastChange;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("client", "cl", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("staff", "s", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("cl.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("dateDeactivateService"), "dateDeactivateService");
        projectionList.add(Projections.property("lastDatePauseSubscription"), "lastDatePauseSubscription");
        projectionList.add(Projections.property("dateActivateSubscription"), "dateActivateSubscription");
        projectionList.add(Projections.property("wasSuspended"), "wasSuspended");
        projectionList.add(Projections.property("dateCreateService"), "dateCreateService");
        projectionList.add(Projections.property("s.guid"), "guidOfStaff");
        projectionList.add(Projections.property("feedingType"), "feedingType");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfStaff);
        //if (st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
        Boolean enableSubscriptionFeeding = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING);
        if (!enableSubscriptionFeeding) {
            throw new DistributedObjectException("Subscription Feeding is disable");
        }
        try {
            this.client = DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }

    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        if (informationContent!=null && informationContent == InformationContents.FRIENDLY_ORGS) {
            return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
        else {
            return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionFeeding.class);

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        DateFormat df = CalendarUtils.getDateFormatLocal();
        if (dateActivateSubscription != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateActivate", df.format(dateActivateSubscription));
        }
        if (lastDatePauseSubscription != null) {
            XMLUtils.setAttributeIfNotNull(element, "LastDatePause", df.format(lastDatePauseSubscription));
        }
        if (dateDeactivateService != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateDeactivate", df.format(dateDeactivateService));
        }
        XMLUtils.setAttributeIfNotNull(element, "WasSuspended", wasSuspended);
        if (dateCreateService != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateCreate", df.format(dateCreateService));
        }
        if (guidOfStaff != null) {
            XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfStaff);
        }
        XMLUtils.setAttributeIfNotNull(element, "Type", feedingType.ordinal());
    }

    @Override
    protected DistributedObject parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Long longIdOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        if (longIdOfClient != null){
            setIdOfClient(longIdOfClient);
        } else {
            throw new DistributedObjectException("Client is empty");
        }

        Date dateDateActivateService = XMLUtils.getDateAttributeValue(node, "DateActivate");
        if (dateDateActivateService != null){
            setDateActivateSubscription(dateDateActivateService);
        }

        Date dateLastDatePauseSubscription = XMLUtils.getDateAttributeValue(node, "LastDatePause");
        if (dateLastDatePauseSubscription != null){
            setLastDatePauseSubscription(dateLastDatePauseSubscription);
        }

        Date dateDateDeactivateService = XMLUtils.getDateAttributeValue(node, "DateDeactivate");
        if (dateDateDeactivateService != null){
            setDateDeactivateService(dateDateDeactivateService);
        }

        Boolean boolWasSuspended = XMLUtils.getBooleanAttributeValue(node, "WasSuspended");
        if (boolWasSuspended != null){
            setWasSuspended(boolWasSuspended);
        } else {
            throw new DistributedObjectException("WasSuspended is not null");
        }

        Date dateDateCreateService = XMLUtils.getDateAttributeValue(node, "DateCreate");
        if (dateDateCreateService != null) {
            setDateCreateService(dateDateCreateService);
        } else {
            LOG.warn("DateCreateService is not null: "+toString());
            if(dateDateActivateService!=null){
                setDateCreateService(dateDateActivateService);
            } else {
                throw new DistributedObjectException("DateActivateService is null, because DateCreateService is null");
            }
        }

        Integer intType = XMLUtils.getIntegerAttributeValue(node, "Type");
        if(intType != null){
            setFeedingType(SubscriptionFeedingType.values()[intType]);
        } else {
            setFeedingType(SubscriptionFeedingType.ABON_TYPE);
        }
        setIdOfOrgLastChange(getIdOfSyncOrg());

        guidOfStaff = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);

        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((SubscriptionFeeding) distributedObject).getOrgOwner());
        setIdOfClient(((SubscriptionFeeding) distributedObject).getIdOfClient());
        setClient(((SubscriptionFeeding) distributedObject).getClient());
        setDateActivateSubscription(((SubscriptionFeeding) distributedObject).getDateActivateSubscription());
        setLastDatePauseSubscription(((SubscriptionFeeding) distributedObject).getLastDatePauseSubscription());
        setDateDeactivateService(((SubscriptionFeeding) distributedObject).getDateDeactivateService());
        setWasSuspended(((SubscriptionFeeding) distributedObject).getWasSuspended());
        setDateCreateService(((SubscriptionFeeding) distributedObject).getDateCreateService());
        setStaff(((SubscriptionFeeding) distributedObject).getStaff());
        setGuidOfStaff(((SubscriptionFeeding) distributedObject).getGuidOfStaff());
        setFeedingType(((SubscriptionFeeding) distributedObject).getFeedingType());
        setIdOfOrgLastChange(((SubscriptionFeeding) distributedObject).getIdOfOrgLastChange());
    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

    // Проверка подписки на актуальность.
    public boolean isActual() {
        return deletedState != null && !deletedState && (dateDeactivateService == null || dateDeactivateService
                .after(new Date()));
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Date getDateActivateSubscription() {
        return dateActivateSubscription;
    }

    public void setDateActivateSubscription(Date dateActivateSubscription) {
        this.dateActivateSubscription = dateActivateSubscription;
    }

    public Date getLastDatePauseSubscription() {
        return lastDatePauseSubscription;
    }

    public void setLastDatePauseSubscription(Date lastDatePauseSubscription) {
        this.lastDatePauseSubscription = lastDatePauseSubscription;
    }

    public Date getDateDeactivateService() {
        return dateDeactivateService;
    }

    public void setDateDeactivateService(Date dateDeactivateService) {
        this.dateDeactivateService = dateDeactivateService;
    }

    public Boolean getWasSuspended() {
        return wasSuspended;
    }

    public void setWasSuspended(Boolean wasSuspended) {
        this.wasSuspended = wasSuspended;
    }

    public Date getDateCreateService() {
        return dateCreateService;
    }

    public void setDateCreateService(Date dateCreateService) {
        this.dateCreateService = dateCreateService;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getGuidOfStaff() {
        return guidOfStaff;
    }

    public void setGuidOfStaff(String guidOfStaff) {
        this.guidOfStaff = guidOfStaff;
    }

    public Date getFirstDateCanChangeRegister(SubscriberFeedingSettingSettingValue parser) {
        return getFirstDateCanChangeRegister(parser, new Date(dateCreateService.getTime()));
    }

    static public Date getFirstDateCanChangeRegister(SubscriberFeedingSettingSettingValue parser, Date date) {
        int workDaysCount = 0;
        Date firstDayChange = date;
        while (workDaysCount < parser.getDaysForbidChange()) {
            firstDayChange = CalendarUtils.addOneDay(firstDayChange);
            if (CalendarUtils.isWorkDate(parser, firstDayChange)) {
                workDaysCount++;
            }
        }
        Calendar midday = Calendar.getInstance();
        midday.setTime(firstDayChange);
        CalendarUtils.truncateToDayOfMonth(midday);
        midday.add(Calendar.HOUR_OF_DAY, 12);
        if (firstDayChange.after(midday.getTime())) {
            firstDayChange = CalendarUtils.calculateNextWorkDate(parser, firstDayChange);
        }
        firstDayChange = CalendarUtils.truncateToDayOfMonth(firstDayChange);
        return firstDayChange;
    }

    @Override
    public String toString() {
        return "SubscriptionFeeding{" +
                "id=" + globalId +
                "idOfOrg=" + orgOwner +
                "guid=" + guid +
                "idOfClient=" + idOfClient +
                '}';
    }

    public SubscriptionFeedingType getFeedingType() {
        return feedingType;
    }

    public void setFeedingType(SubscriptionFeedingType subscriptionFeedingType) {
        this.feedingType = subscriptionFeedingType;
    }

    public Long getIdOfOrgLastChange() {
        return idOfOrgLastChange;
    }

    public void setIdOfOrgLastChange(Long idOfOrgLastChange) {
        this.idOfOrgLastChange = idOfOrgLastChange;
    }
}
