package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 02.09.13
 * Time: 13:35
 * Клиентские циклограммы
 */
public class CycleDiagram extends DistributedObject{

    private Long idOfClient;
    private Client client;
    private Date dateActivationDiagram;
    private StateDiagram stateDiagram;
    private String monday;
    private String mondayPrice;
    private String tuesday;
    private String tuesdayPrice;
    private String wednesday;
    private String wednesdayPrice;
    private String thursday;
    private String thursdayPrice;
    private String friday;
    private String fridayPrice;
    private String saturday;
    private String saturdayPrice;
    private String sunday;
    private String sundayPrice;
    private Staff staff;
    private String guidOfStaff;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;
    private SubscriptionFeedingType feedingType;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("client", "cl", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("staff", "s", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("dateActivationDiagram"), "dateActivationDiagram");

        //projectionList.add(Projections.property("idOfClient"), "idOfClient");
        projectionList.add(Projections.property("cl.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("stateDiagram"), "stateDiagram");
        projectionList.add(Projections.property("monday"), "monday");
        projectionList.add(Projections.property("wednesday"), "wednesday");
        projectionList.add(Projections.property("tuesday"), "tuesday");
        projectionList.add(Projections.property("thursday"), "thursday");
        projectionList.add(Projections.property("friday"), "friday");
        projectionList.add(Projections.property("saturday"), "saturday");
        projectionList.add(Projections.property("sunday"), "sunday");
        projectionList.add(Projections.property("mondayPrice"), "mondayPrice");
        projectionList.add(Projections.property("wednesdayPrice"), "wednesdayPrice");
        projectionList.add(Projections.property("tuesdayPrice"), "tuesdayPrice");
        projectionList.add(Projections.property("thursdayPrice"), "thursdayPrice");
        projectionList.add(Projections.property("fridayPrice"), "fridayPrice");
        projectionList.add(Projections.property("saturdayPrice"), "saturdayPrice");
        projectionList.add(Projections.property("sundayPrice"), "sundayPrice");
        projectionList.add(Projections.property("s.guid"), "guidOfStaff");
        projectionList.add(Projections.property("feedingType"), "feedingType");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfStaff);
        /*if (st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");*/
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
/*
        // проверить бросить искл если idкл совпал и даты совпали гуид различается DATA_EXIST_VALUE
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", this.client));
        criteria.add(Restrictions.eq("dateActivationDiagram", this.dateActivationDiagram));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("stateDiagram", StateDiagram.ACTIVE));
        CycleDiagram cDiagram = (CycleDiagram) criteria.uniqueResult();
        session.clear();

        if (cDiagram != null && cDiagram.getClient().equals(client) && cDiagram.getDateActivationDiagram()
                .equals(dateActivationDiagram) && !cDiagram.getGuid().equals(guid)) {
            DistributedObjectException doe = new DistributedObjectException("CycleDiagram DATA_EXIST_VALUE");
            doe.setData(cDiagram.getGuid());
            throw doe;
        }*/

        /* При синхронизации пришла активная циклограмма */
        /*if(isActual()){
            *//* проверяем, есть ли на текущую дату активная циклограмма *//*
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            CycleDiagram cd = sfService.findActiveCycleDiagram(client, this.dateActivationDiagram);
            *//* записываем заблокированной текущую диаграмму *//*
            if(cd != null){
                stateDiagram = StateDiagram.BLOCK;
            }
        }*/
        // Проверка на случай, если циклограмма была активирована как на вебе, так и на клиенте.
        //if (cd != null && isActual() && !cd.getGuid().equals(guid)) {
        //    DistributedObjectException doe = new DistributedObjectException("CycleDiagram DATA_EXIST_VALUE");
        //    doe.setData(cd.getGuid());
        //    throw doe;
        //}
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        if (informationContent != null && informationContent == InformationContents.FRIENDLY_ORGS) {
            return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
        else {
            return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);

        XMLUtils.setAttributeIfNotNull(element, "DateActivation", CalendarUtils.dateToString(dateActivationDiagram));
        XMLUtils.setAttributeIfNotNull(element, "StateDiagram", stateDiagram.ordinal());

        XMLUtils.setAttributeIfNotNull(element, "Monday", monday);
        XMLUtils.setAttributeIfNotNull(element, "Wednesday", wednesday);
        XMLUtils.setAttributeIfNotNull(element, "Tuesday", tuesday);
        XMLUtils.setAttributeIfNotNull(element, "Thursday", thursday);
        XMLUtils.setAttributeIfNotNull(element, "Friday", friday);
        XMLUtils.setAttributeIfNotNull(element, "Saturday", saturday);
        XMLUtils.setAttributeIfNotNull(element, "Sunday", sunday);

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
        }
        Date longDateActivationDiagram = XMLUtils.getDateAttributeValue(node, "DateActivation");
        if (longDateActivationDiagram != null){
            setDateActivationDiagram(longDateActivationDiagram);
        } else {
            throw new DistributedObjectException("DateActivation is not null");
        }
        Integer intStateDiagram = XMLUtils.getIntegerAttributeValue(node, "StateDiagram");
        if (intStateDiagram != null){
            if(intStateDiagram>=0 && intStateDiagram<StateDiagram.values().length){
                setStateDiagram(StateDiagram.values()[intStateDiagram]);
            } else {
                throw new DistributedObjectException("Unknown StateDiagram value");
            }
        } else {
            throw new DistributedObjectException("StateDiagram is not null");
        }

        Integer intType = XMLUtils.getIntegerAttributeValue(node, "Type");
        if(intType != null){
            setFeedingType(SubscriptionFeedingType.values()[intType]);
        } else {
            setFeedingType(SubscriptionFeedingType.ABON_TYPE);
        }

        setMonday(XMLUtils.getStringAttributeValue(node, "Monday", 255));
        setMondayPrice(XMLUtils.getStringAttributeValue(node, "MondayPrice", 255));
        setTuesday(XMLUtils.getStringAttributeValue(node, "Tuesday", 255));
        setTuesdayPrice(XMLUtils.getStringAttributeValue(node, "TuesdayPrice", 255));
        setWednesday(XMLUtils.getStringAttributeValue(node, "Wednesday", 255));
        setWednesdayPrice(XMLUtils.getStringAttributeValue(node, "WednesdayPrice", 255));
        setThursday(XMLUtils.getStringAttributeValue(node, "Thursday", 255));
        setThursdayPrice(XMLUtils.getStringAttributeValue(node, "ThursdayPrice", 255));
        setFriday(XMLUtils.getStringAttributeValue(node, "Friday", 255));
        setFridayPrice(XMLUtils.getStringAttributeValue(node, "FridayPrice", 255));
        setSaturday(XMLUtils.getStringAttributeValue(node, "Saturday", 255));
        setSaturdayPrice(XMLUtils.getStringAttributeValue(node, "SaturdayPrice", 255));
        setSunday(XMLUtils.getStringAttributeValue(node, "Sunday", 255));
        setSundayPrice(XMLUtils.getStringAttributeValue(node, "SundayPrice", 255));
        guidOfStaff = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((CycleDiagram) distributedObject).getOrgOwner());
        setIdOfClient(((CycleDiagram) distributedObject).getIdOfClient());
        setClient(((CycleDiagram) distributedObject).getClient());
        setDateActivationDiagram(((CycleDiagram) distributedObject).getDateActivationDiagram());
        setStateDiagram(((CycleDiagram) distributedObject).getStateDiagram());
        setMonday(((CycleDiagram) distributedObject).getMonday());
        setTuesday(((CycleDiagram) distributedObject).getTuesday());
        setThursday(((CycleDiagram) distributedObject).getThursday());
        setFriday(((CycleDiagram) distributedObject).getFriday());
        setSaturday(((CycleDiagram) distributedObject).getSaturday());
        setSunday(((CycleDiagram) distributedObject).getSunday());
        setStaff(((CycleDiagram) distributedObject).getStaff());
        setGuidOfStaff(((CycleDiagram) distributedObject).getGuidOfStaff());
        setFeedingType(((CycleDiagram) distributedObject).getFeedingType());
    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

    public boolean isActual() {
        return deletedState != null && !deletedState && stateDiagram == StateDiagram.ACTIVE;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDateActivationDiagram() {
        return dateActivationDiagram;
    }

    public void setDateActivationDiagram(Date dateActivationDiagram) {
        this.dateActivationDiagram = dateActivationDiagram;
    }

    public StateDiagram getStateDiagram() {
        return stateDiagram;
    }

    public void setStateDiagram(StateDiagram stateDiagram) {
        this.stateDiagram = stateDiagram;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getMondayPrice() {
        return mondayPrice;
    }

    public void setMondayPrice(String mondayPrice) {
        this.mondayPrice = mondayPrice;
    }

    public String getTuesdayPrice() {
        return tuesdayPrice;
    }

    public void setTuesdayPrice(String tuesdayPrice) {
        this.tuesdayPrice = tuesdayPrice;
    }

    public String getWednesdayPrice() {
        return wednesdayPrice;
    }

    public void setWednesdayPrice(String wednesdayPrice) {
        this.wednesdayPrice = wednesdayPrice;
    }

    public String getThursdayPrice() {
        return thursdayPrice;
    }

    public void setThursdayPrice(String thursdayPrice) {
        this.thursdayPrice = thursdayPrice;
    }

    public String getFridayPrice() {
        return fridayPrice;
    }

    public void setFridayPrice(String fridayPrice) {
        this.fridayPrice = fridayPrice;
    }

    public String getSaturdayPrice() {
        return saturdayPrice;
    }

    public void setSaturdayPrice(String saturdayPrice) {
        this.saturdayPrice = saturdayPrice;
    }

    public String getSundayPrice() {
        return sundayPrice;
    }

    public void setSundayPrice(String sundayPrice) {
        this.sundayPrice = sundayPrice;
    }

    public Long getWeekPrice(){
        return 0L;// mondayPrice+tuesdayPrice+wednesdayPrice+thursdayPrice+fridayPrice+saturdayPrice+sundayPrice;
    }

    public Long getMonthPrice(){
        return getWeekPrice()*4;
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

    public SubscriptionFeedingType getFeedingType() {
        return feedingType;
    }

    public void setFeedingType(SubscriptionFeedingType feedingType) {
        this.feedingType = feedingType;
    }
}
