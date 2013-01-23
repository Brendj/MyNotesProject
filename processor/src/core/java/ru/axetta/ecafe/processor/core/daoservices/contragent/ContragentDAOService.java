/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

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
                .add( Projections.groupProperty("c.idOfContragent"))
        );
        criteria.add(Restrictions.between("createTime",startDate, endDate));
        List list = criteria.list();
        contragentCompletionItem.setContragentPayItems(list);
        contragentCompletionItem.setEducationalInstitutionName(org.getShortName());
        return contragentCompletionItem;
    }

    //@SuppressWarnings("unchecked")
    //public List<OrgItem> findDistributionOrganizationBySource(Long idOfOrg){
    //    Query query = getSession().createQuery("select org.idOfOrg, org.shortName from Org org where org.idOfOrg in (select idOfSourceOrg from MenuExchangeRule where idOfDestOrg=:idOfOrg)");
    //    query.setParameter("idOfOrg",idOfOrg);
    //    List list = query.list();
    //    List<OrgItem> resultList = new ArrayList<OrgItem>(list.size());
    //    for (Object object: list){
    //        Object[] objects = (Object[]) object;
    //        resultList.add(new OrgItem((Long) objects[0],(String) objects[1]));
    //    }
    //    return resultList;
    //
    //}

    @SuppressWarnings("unchecked")
    public List<Org> findDistributionOrganizationByDefaultSupplier(Contragent defaultSupplier){
        Criteria criteria = getSession().createCriteria(Org.class);
        criteria.add(Restrictions.eq("defaultSupplier",defaultSupplier));
        return (List<Org>) criteria.list();
    }

}
