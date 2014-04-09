package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 02.09.13
 * Time: 13:35
 * Полписка на Абоненское питание
 */
public class SubscriptionFeeding extends DistributedObject{

    private Long idOfClient;
    private Client client;
    private Date dateActivateService;
    private Date lastDatePauseService;
    private Date dateDeactivateService;
    private Boolean wasSuspended;
    private Date dateCreateService;
    private String reasonWasSuspended;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("client", "cl", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("cl.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("dateActivateService"), "dateActivateService");
        projectionList.add(Projections.property("lastDatePauseService"), "lastDatePauseService");
        projectionList.add(Projections.property("dateDeactivateService"), "dateDeactivateService");
        projectionList.add(Projections.property("wasSuspended"), "wasSuspended");
        projectionList.add(Projections.property("dateCreateService"), "dateCreateService");
        projectionList.add(Projections.property("reasonWasSuspended"), "reasonWasSuspended");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
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
        SubscriptionFeedingService sfService = RuntimeContext.getAppContext().getBean(SubscriptionFeedingService.class);
        SubscriptionFeeding sf = sfService.findClientSubscriptionFeeding(client);
        // Если уже есть у клиента актуальная подписка и с АРМа приходит тоже актулаьная, то АРМовскую "разворачиваем".
        // Потому что не может быть у клиента двух актуальных подписок на АП !
        if (sf != null && isActual() && !sf.getGuid().equals(guid)) {
            DistributedObjectException doe = new DistributedObjectException("SubscriptionFeeding DATA_EXIST_VALUE");
            doe.setData(sf.getGuid());
            throw doe;
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        DateFormat df = CalendarUtils.getDateFormatLocal();
        if (dateActivateService != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateActivate", df.format(dateActivateService));
        }
        if (lastDatePauseService != null) {
            XMLUtils.setAttributeIfNotNull(element, "LastDatePause", df.format(lastDatePauseService));
        }
        if (dateDeactivateService != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateDeactivate", df.format(dateDeactivateService));
        }
        XMLUtils.setAttributeIfNotNull(element, "WasSuspended", wasSuspended);
        if (dateCreateService != null) {
            XMLUtils.setAttributeIfNotNull(element, "DateCreate", df.format(dateCreateService));
        }
        if (reasonWasSuspended != null) {
            XMLUtils.setAttributeIfNotNull(element, "ReasonWasSuspended", df.format(reasonWasSuspended));
        }
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

        Date longDateActivateService = XMLUtils.getDateAttributeValue(node, "DateActivate");
        if (longDateActivateService != null){
            setDateActivateService(longDateActivateService);
        } else {
            throw new DistributedObjectException("DateActivate is not null");
        }

        Date longLastDatePauseService = XMLUtils.getDateAttributeValue(node, "LastDatePause");
        if (longLastDatePauseService != null){
            setLastDatePauseService(longLastDatePauseService);
        }

        Date longDateDeactivateService = XMLUtils.getDateAttributeValue(node, "DateDeactivate");
        if (longDateDeactivateService != null){
            setDateDeactivateService(longDateDeactivateService);
        }

        Boolean boolWasSuspended = XMLUtils.getBooleanAttributeValue(node, "WasSuspended");
        if (boolWasSuspended != null){
            setWasSuspended(boolWasSuspended);
        } else {
            throw new DistributedObjectException("WasSuspended is not null");
        }

        Date longDateCreateService = XMLUtils.getDateAttributeValue(node, "DateCreate");
        if (longDateCreateService != null) {
            setDateCreateService(longDateCreateService);
        }

        String reasonWasSuspended = XMLUtils.getStringAttributeValue(node, "ReasonWasSuspended", 1024);
        if (reasonWasSuspended != null) {
            setReasonWasSuspended(reasonWasSuspended);
        }

        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((SubscriptionFeeding) distributedObject).getOrgOwner());
        setIdOfClient(((SubscriptionFeeding) distributedObject).getIdOfClient());
        setClient(((SubscriptionFeeding) distributedObject).getClient());
        setDateActivateService(((SubscriptionFeeding) distributedObject).getDateActivateService());
        setLastDatePauseService(((SubscriptionFeeding) distributedObject).getLastDatePauseService());
        setDateDeactivateService(((SubscriptionFeeding) distributedObject).getDateDeactivateService());
        setWasSuspended(((SubscriptionFeeding) distributedObject).getWasSuspended());
        setDateCreateService(((SubscriptionFeeding) distributedObject).getDateCreateService());
        setReasonWasSuspended(((SubscriptionFeeding) distributedObject).getReasonWasSuspended());
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

    public Date getDateActivateService() {
        return dateActivateService;
    }

    public void setDateActivateService(Date dateActivateService) {
        this.dateActivateService = dateActivateService;
    }

    public Date getLastDatePauseService() {
        return lastDatePauseService;
    }

    public void setLastDatePauseService(Date lastDatePauseService) {
        this.lastDatePauseService = lastDatePauseService;
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

    public String getReasonWasSuspended() {
        return reasonWasSuspended;
    }

    public void setReasonWasSuspended(String reasonWasSuspended) {
        this.reasonWasSuspended = reasonWasSuspended;
    }
}
