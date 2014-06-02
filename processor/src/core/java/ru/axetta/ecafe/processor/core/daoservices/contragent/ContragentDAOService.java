/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class ContragentDAOService extends AbstractDAOService {

    @SuppressWarnings("unchecked")
    public List<Contragent> getPayAgentContragent(){
        Criteria criteria = getSession().createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId",Contragent.PAY_AGENT));
        return criteria.list();

    }

    @SuppressWarnings("unchecked")
    public ContragentCompletionItem generateReportItems(Long idOfOrg, List<Contragent> contragentList, Date startDate, Date endDate){
        ContragentCompletionItem contragentCompletionItem = new ContragentCompletionItem(contragentList);
        Org org = (Org) getSession().load(Org.class, idOfOrg);
        Criteria criteria = getSession().createCriteria(ClientPayment.class);
        criteria.createAlias("transaction","tr").createAlias("tr.client","cl").add(Restrictions.eq("cl.org",org));
        criteria.createAlias("contragent","c").add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));
        criteria.setProjection( Projections.projectionList()
                .add(Projections.sum("paySum") )
                .add(Projections.groupProperty("c.idOfContragent"))
                .add(Projections.count("idOfClientPayment"))
        );
        criteria.add(Restrictions.between("createTime",startDate, endDate));
        List list = criteria.list();
        contragentCompletionItem.setContragentPayItems(list);
        contragentCompletionItem.setEducationalInstitutionName(org.getShortName());
        contragentCompletionItem.setEducationalCity(org.getCity());
        contragentCompletionItem.setEducationalLocation(org.getLocation());
        contragentCompletionItem.setEducationalTags(org.getTag());
        return contragentCompletionItem;
    }

    @SuppressWarnings("unchecked")
    public List<ContragentCompletionReportItem> generateContragentCompletionReportItems(List<Long> idOfOrgList, Long idOfContragent, Date startDate, Date endDate){
        Criteria criteria = getSession().createCriteria(ClientPayment.class);
        /* Условия запроса */
        criteria.createAlias("transaction", "tr")
                .createAlias("tr.client", "cl", JoinType.LEFT_OUTER_JOIN)
                .createAlias("cl.org", "o", JoinType.LEFT_OUTER_JOIN)
                .createAlias("o.defaultSupplier", "defaultSupplier", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
        criteria.createAlias("contragent", "c", JoinType.LEFT_OUTER_JOIN).add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));
        if (!CollectionUtils.isEmpty(idOfOrgList)) {
            criteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
        }
        /* отображение сгрупированных полей */
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("o.shortName"), "educationalInstitutionName")
                .add(Projections.property("c.contragentName"),"contragentName")
                .add(Projections.sum("paySum"),"paySum")
                //.add(Projections.sqlProjection("COALESCE(SUM({alias}.paySum), 0) as summaryPays", new String[]{"paySum"}, new Type[]{new LongType()}))
                .add(Projections.groupProperty("c.idOfContragent"))
                .add(Projections.groupProperty("o.idOfOrg"))
        );
        criteria.add(Restrictions.between("createTime", startDate, endDate));
        criteria.addOrder(Order.asc("o.idOfOrg"));
        criteria.addOrder(Order.asc("c.idOfContragent"));
        /* Трансформируем к нужному бину */
        criteria.setResultTransformer(Transformers.aliasToBean(ContragentCompletionReportItem.class));
        return  (List<ContragentCompletionReportItem>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Org> findDistributionOrganizationByDefaultSupplier(Contragent defaultSupplier){
        Criteria criteria = getSession().createCriteria(Org.class);
        criteria.add(Restrictions.eq("defaultSupplier",defaultSupplier));
        return (List<Org>) criteria.list();
    }


    @SuppressWarnings("unchecked")
    public List<Org> findAllDistributionOrganization(){
        Criteria criteria = getSession().createCriteria(Org.class);
        return (List<Org>) criteria.list();
    }

    public Org getOrdByOrgId(Long idOfOrg) {
        Org org = (Org) getSession().load(Org.class, idOfOrg);
        return org;
    }

}
