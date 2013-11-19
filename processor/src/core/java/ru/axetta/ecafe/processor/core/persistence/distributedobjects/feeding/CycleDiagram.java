package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
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
    private Long mondayPrice;
    private String tuesday;
    private Long tuesdayPrice;
    private String wednesday;
    private Long wednesdayPrice;
    private String thursday;
    private Long thursdayPrice;
    private String friday;
    private Long fridayPrice;
    private String saturday;
    private Long saturdayPrice;
    private String sunday;
    private Long sundayPrice;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        criteria.createAlias("client", "cl", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

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
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Boolean enableSubscriptionFeeding = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING);
        if(!enableSubscriptionFeeding) throw new DistributedObjectException("Subscription Feeding is disable");
        try {
            this.client = DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid);
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

        setMonday(XMLUtils.getStringAttributeValue(node, "Monday", 255));
        setMondayPrice(XMLUtils.getLongAttributeValue(node, "MondayPrice"));
        setTuesday(XMLUtils.getStringAttributeValue(node, "Tuesday", 255));
        setTuesdayPrice(XMLUtils.getLongAttributeValue(node, "TuesdayPrice"));
        setWednesday(XMLUtils.getStringAttributeValue(node, "Wednesday", 255));
        setWednesdayPrice(XMLUtils.getLongAttributeValue(node, "WednesdayPrice"));
        setThursday(XMLUtils.getStringAttributeValue(node, "Thursday", 255));
        setThursdayPrice(XMLUtils.getLongAttributeValue(node, "ThursdayPrice"));
        setFriday(XMLUtils.getStringAttributeValue(node, "Friday", 255));
        setFridayPrice(XMLUtils.getLongAttributeValue(node, "FridayPrice"));
        setSaturday(XMLUtils.getStringAttributeValue(node, "Saturday", 255));
        setSaturdayPrice(XMLUtils.getLongAttributeValue(node, "SaturdayPrice"));
        setSunday(XMLUtils.getStringAttributeValue(node, "Sunday", 255));
        setSundayPrice(XMLUtils.getLongAttributeValue(node, "SundayPrice"));
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

    public Long getMondayPrice() {
        return mondayPrice;
    }

    public void setMondayPrice(Long mondayPrice) {
        this.mondayPrice = mondayPrice;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public Long getTuesdayPrice() {
        return tuesdayPrice;
    }

    public void setTuesdayPrice(Long tuesdayPrice) {
        this.tuesdayPrice = tuesdayPrice;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public Long getWednesdayPrice() {
        return wednesdayPrice;
    }

    public void setWednesdayPrice(Long wednesdayPrice) {
        this.wednesdayPrice = wednesdayPrice;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public Long getThursdayPrice() {
        return thursdayPrice;
    }

    public void setThursdayPrice(Long thursdayPrice) {
        this.thursdayPrice = thursdayPrice;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public Long getFridayPrice() {
        return fridayPrice;
    }

    public void setFridayPrice(Long fridayPrice) {
        this.fridayPrice = fridayPrice;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public Long getSaturdayPrice() {
        return saturdayPrice;
    }

    public void setSaturdayPrice(Long saturdayPrice) {
        this.saturdayPrice = saturdayPrice;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public Long getSundayPrice() {
        return sundayPrice;
    }

    public void setSundayPrice(Long sundayPrice) {
        this.sundayPrice = sundayPrice;
    }

    public Long getWeekPrice(){
        return mondayPrice+tuesdayPrice+wednesdayPrice+thursdayPrice+fridayPrice+saturdayPrice+sundayPrice;
    }

    public Long getMonthPrice(){
        return getWeekPrice()*4;
    }
}
