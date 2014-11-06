/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 27.10.14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeBook extends LibraryDistributedObject {

    public static final int ORG_NAME_LENGTH = 127;
    public static final int GUID_LENGTH = 36;
    //public static final int NUMBER_BOOKS_FREE = 3;

    /*private Long globalVersionOnCreate;
    private Long globalVersion;
    private Long orgOwner;*/
    //private Boolean deletedState;
    /*private String guid;
    private Date lastUpdate;
    private Date deleteDate;
    private Date createdDate;*/

    private Integer exchangePrimaryKey;
    private Integer amount;
    private Integer orgId;
    private String orgName;
    private String guidPublication;
    public static Integer exchangeBookCondition;
    //private String guid;

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "Amount", amount);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrg", orgId);
        XMLUtils.setAttributeIfNotNull(element, "NameOfOrg", orgName);
        XMLUtils.setAttributeIfNotNull(element, "GuidPublication", guidPublication);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {

    }

    @Override
    public DistributedObject build(Node node) throws Exception {
        tagName = node.getNodeName();
        return parseAttributes(node);
    }

    @Override
    protected ExchangeBook parseAttributes(Node node) throws Exception {
        ExchangeBook.exchangeBookCondition = XMLUtils.getIntegerAttributeValue(node, "ExchangeBookCondition");
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setAmount(((ExchangeBook) distributedObject).getAmount());
        setOrgId(((ExchangeBook) distributedObject).getOrgId());
        setOrgName(((ExchangeBook) distributedObject).getOrgName());
        setGuidPublication(((ExchangeBook) distributedObject).getGuidPublication());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {

        String strquery = "select count(ins.IdOfInstance) as amount, row_number() OVER() as exchangePrimaryKey, pub.guid as guidPublication, org.IdOfOrg as orgId, org.ShortName as orgName " +
                "from cf_publications pub join cf_instances ins on pub.IdOfPublication = ins.IdOfPublication " +
                "join cf_issuable iss on iss.IdOfInstance = ins.IdOfInstance " +
                "join cf_orgs org on org.IdOfOrg = ins.OrgOwner " +
                "where not exists (select IdOfCirculation from cf_circulations cir where cir.IdOfIssuable = iss.IdOfIssuable and cir.RealRefundDate is null) " +
                "group by pub.IdOfPublication, org.idOfOrg having count(ins.idOfInstance) > " + String.valueOf(ExchangeBook.exchangeBookCondition);
        SQLQuery query = session.createSQLQuery(strquery);
        query.addEntity(ExchangeBook.class);
        return query.list();
    }

    @Override
    public DistributedObject getCurrentDistributedObject(Criteria criteria) {
        return null;
    }

    @Override
    public void createProjections(Criteria criteria) {

    }

    public Integer getExchangePrimaryKey() {
        return exchangePrimaryKey;
    }

    public void setExchangePrimaryKey(Integer exchangePrimaryKey) {
        this.exchangePrimaryKey = exchangePrimaryKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }

    @Override
    public Boolean getDeletedState() {
        return false;   //заглушка
    }

    @Override
    public Long getGlobalVersion() {
        return 0L; //заглушка
    }

    /*public String getGuid() {
        return UUID.randomUUID().toString();
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }*/
}
