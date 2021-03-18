/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.logic.GoodDateForOrders;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.06.12
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */

public abstract class DistributedObject extends GoodDateForOrders{

    /* Идентификатор объекта */
    protected Long globalId;
    /* версия объекта */
    protected Long globalVersion;
    /* версия объекта на момент его создания */
    protected Long globalVersionOnCreate;
    /* дата создания объекта */
    protected Date createdDate;
    /* дата мзминения объекта */
    protected Date lastUpdate;
    /* дата удаления объекта */
    protected Date deleteDate;
    /* статус объекта (активен/удален) */
    protected Boolean deletedState;
    /* GUID объекта */
    protected String guid;
    /* Идентификатор организации */
    protected Long orgOwner;
    protected SendToAssociatedOrgs sendAll = SendToAssociatedOrgs.Send;
    /* имя узла элемента */
    protected String tagName;
    private DistributedObjectException distributedObjectException;



    /*
    * Флаги, ограничивающие объем информации по распр. объектам
    * */
    public enum InformationContents {
        ONLY_CURRENT_ORG(0),
        FRIENDLY_ORGS(1);

        private int code;

        InformationContents(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static InformationContents getByCode(int code) {
            for (InformationContents type : InformationContents.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return ONLY_CURRENT_ORG;
        }

        public boolean isDefault() {
            return this.code == ONLY_CURRENT_ORG.code;
        }
    }


    public DistributedObject() {
        this.guid = UUID.randomUUID().toString();
    }

    protected void buildVersionCriteria(Long currentMaxVersion, String currentLastGuid, Integer currentLimit,
            Criteria criteria) {
        if (currentLimit == null || currentLimit <= 0) {
            if (StringUtils.isNotEmpty(currentLastGuid)) {
                Disjunction mainRestriction = Restrictions.disjunction();
                mainRestriction.add(Restrictions.gt("globalVersion", currentMaxVersion));
                Conjunction andRestr = Restrictions.conjunction();
                andRestr.add(Restrictions.gt("guid", currentLastGuid));
                andRestr.add(Restrictions.ge("globalVersion", currentMaxVersion));
                mainRestriction.add(andRestr);
                criteria.add(mainRestriction);
            } else {
                criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
            }
        } else {
            criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
        }
    }

    protected void addDistributedObjectProjectionList(ProjectionList projectionList) {
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("globalVersionOnCreate"), "globalVersionOnCreate");
        projectionList.add(Projections.property("createdDate"), "createdDate");
        projectionList.add(Projections.property("lastUpdate"), "lastUpdate");
        projectionList.add(Projections.property("deleteDate"), "deleteDate");
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");
    }

    public DistributedObjectException getDistributedObjectException() {
        return distributedObjectException;
    }

    public void setDistributedObjectException(DistributedObjectException distributedObjectException) {
        this.distributedObjectException = distributedObjectException;
    }

    /* метод добавления атрибутов в узел в тег подтверждения*/
    public Element toConfirmElement(Element element){
        element.setAttribute("Guid", getGuid());
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        if(this.getGlobalVersion()!=null) element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        if(this.distributedObjectException != null){
            element.setAttribute("ErrorType", distributedObjectException.getMessage());
            if(distributedObjectException.getData()!=null){
                element.setAttribute("ErrorData", distributedObjectException.getData());
            }
        }
        return element;
    }

    /* метод добавления общих атрибутов в узел */
    public Element toElement(Element element){
        element.setAttribute("Guid", getGuid());
        element.setAttribute("V", String.valueOf(getGlobalVersion()));
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        appendAttributes(element);
        return element;
        /* Метод определения названия элемента */
    }

    /* метод парсинга элемента */
    public DistributedObject build(Node node) throws Exception {
        tagName = node.getNodeName();
        /* Begin required params */
        String stringGUID = XMLUtils.getStringAttributeValue(node, "Guid", 36);
        if (StringUtils.isEmpty(stringGUID)) throw new Exception("Attribute GUID is Empty");
        setGuid(stringGUID);
        Long version = XMLUtils.getLongAttributeValue(node, "V");
        if (version != null)
            setGlobalVersion(version);
        Integer status = XMLUtils.getIntegerAttributeValue(node, "D");
        setDeletedState(status != null);
        /* End required params */
        if (getDeletedState()) {
            return this;
        }
        return parseAttributes(node);
    }

     /* меод добавления свойств наследника атрибутов в узел */
    protected abstract void appendAttributes(Element element);

    public abstract void preProcess(Session session, Long idOfOrg) throws DistributedObjectException;

    /* метод обновления версии родительского элемента */
    public void updateVersionFromParent(Session session){}

    /* Метод вызывается перед сохранением нового объекта в БД.
       Используется, если перед сохранением надо выполнить специфичную бизнес-логику.
     */
    //public void beforePersist(Session session, Long idOfOrg, String ignoreUuid) {}
    //
    //public void beforePersist(Session session, Long idOfOrg) {}

    protected abstract DistributedObject parseAttributes(Node node) throws Exception;

    public abstract void fill(DistributedObject distributedObject);

    public abstract List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception;

    public abstract void createProjections(Criteria criteria);

    /**
     * Метод для выборки объектов которые уходят от создателя к создателю без логики
     * правил распределения, например: натройки ECafeSettings
     *
     * @param session
     * @param idOfOrg
     * @param currentMaxVersion
     * @return
     * @throws DistributedObjectException
     */
    @SuppressWarnings("unchecked")
    protected List<DistributedObject> toSelfProcess(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid, Integer currentLimit) throws DistributedObjectException{
        Criteria criteria = session.createCriteria(getClass());
        criteria.add(Restrictions.eq("orgOwner",idOfOrg));
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        //criteria.add(Restrictions.gt("globalVersion",currentMaxVersion));
        createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    protected List<DistributedObject> toFriendlyOrgsProcess(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid, Integer currentLimit) throws
            DistributedObjectException {
        Org currentOrg = (Org) session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(getClass());
        Set<Long> friendlyOrgIds = OrgUtils.getFriendlyOrgIds(currentOrg);
        criteria.add(Restrictions.in("orgOwner", friendlyOrgIds));
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    public DistributedObject getCurrentDistributedObject(Criteria criteria) {
        criteria.add(Restrictions.eq("guid", this.getGuid()));
        this.createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(this.getClass()));
        criteria.setMaxResults(1);
        return (DistributedObject) criteria.uniqueResult();
    }

    ///* идентификатор синхронизируемой организации*/
    private Long idOfSyncOrg;

    public Long getIdOfSyncOrg() {
        return idOfSyncOrg;
    }

    public void setIdOfSyncOrg(Long idOfSyncOrg) {
        this.idOfSyncOrg = idOfSyncOrg;
    }

    /* Getters and Setters */
    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getGuid() {
        return guid;
    }

    protected void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(Long globalVersion) {
        this.globalVersion = globalVersion;
    }

    public Long getGlobalVersionOnCreate() {
        return globalVersionOnCreate;
    }

    public void setGlobalVersionOnCreate(Long globalVersionOnCreate) {
        this.globalVersionOnCreate = globalVersionOnCreate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public SendToAssociatedOrgs getSendAll() {
        return sendAll;
    }

    public void setSendAll(SendToAssociatedOrgs sendAll) {
        this.sendAll = sendAll;
    }

    public void setNewInformationContent(InformationContents informationContent) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DistributedObject))
            return false;
        DistributedObject that = (DistributedObject) o;
        if (guid != null ? !guid.equals(that.guid) : that.guid != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return guid != null ? guid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+ "{" +
                "globalId=" + globalId +
                ", globalVersion=" + globalVersion +
                ", guid='" + guid + '\'' +
                ", orgOwner=" + orgOwner +
                '}';
    }

}
