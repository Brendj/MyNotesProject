/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
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
    public ContragentCompletionItem generateReportItem(Long idOfOrg, List<Contragent> contragentList, Date startDate,
            Date endDate) {
        Org org = (Org) getSession().load(Org.class, idOfOrg);
        Criteria criteriaIsNotNull = getSession().createCriteria(ClientPayment.class);
        criteriaIsNotNull.createAlias("transaction", "tr")
                .add(Restrictions.isNotNull("tr.org"))
                .add(Restrictions.eq("tr.org", org))
                .createAlias("tr.client", "cl")
                .createAlias("contragent", "c")
                .add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));

        criteriaIsNotNull.setProjection(Projections.projectionList()
                .add(Projections.sum("paySum"))
                .add(Projections.groupProperty("c.idOfContragent"))
                .add(Projections.count("idOfClientPayment")));
        criteriaIsNotNull.add(Restrictions.between("createTime", startDate, endDate));
        List list = criteriaIsNotNull.list();

        ContragentCompletionItem contragentCompletionItem = new ContragentCompletionItem(contragentList);
        contragentCompletionItem.setContragentPayItems(list);
        contragentCompletionItem.setEducationalId(org.getIdOfOrg());
        contragentCompletionItem.setEducationalInstitutionName(org.getShortName());
        contragentCompletionItem.setEducationalCity(org.getCity());
        contragentCompletionItem.setEducationalLocation(org.getLocation());
        contragentCompletionItem.setEducationalTags(org.getTag());
        return contragentCompletionItem;
    }

    @SuppressWarnings("unchecked")
    public ContragentCompletionItem generateReportItemWithTransactionOrgIsNull(Long idOfOrg,
            List<Contragent> contragentList, Date startDate, Date endDate) {
        Org org = (Org) getSession().load(Org.class, idOfOrg);
        Criteria criteriaIsNull = getSession().createCriteria(ClientPayment.class);
        criteriaIsNull.createAlias("transaction", "tr")
                .add(Restrictions.isNull("tr.org"))
                .createAlias("tr.client", "cl")
                .add(Restrictions.eq("cl.org", org))
                .createAlias("contragent", "c")
                .add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));

        criteriaIsNull.setProjection(Projections.projectionList().add(Projections.sum("paySum"))
                .add(Projections.groupProperty("c.idOfContragent")).add(Projections.count("idOfClientPayment")));
        criteriaIsNull.add(Restrictions.between("createTime", startDate, endDate));
        List list = criteriaIsNull.list();

        ContragentCompletionItem contragentCompletionItem = new ContragentCompletionItem(contragentList);
        contragentCompletionItem.setContragentPayItems(list);
        contragentCompletionItem.setEducationalInstitutionName(org.getShortName());
        contragentCompletionItem.setEducationalCity(org.getCity());
        contragentCompletionItem.setEducationalLocation(org.getLocation());
        contragentCompletionItem.setEducationalTags(org.getTag());
        return contragentCompletionItem;
    }

    @SuppressWarnings("unchecked")
    public List<ContragentCompletionReportItem> generateReportItems(List<Long> idOfOrgList, Date startDate,
            Date endDate) {
        List<ContragentCompletionItem> contragentCompletionItems = new ArrayList<ContragentCompletionItem>();

        ContragentCompletionItem total = new ContragentCompletionItem(getPayAgentContragent());

        for (Long idOrg : idOfOrgList) {
            ContragentCompletionItem contragentCompletionItem = generateReportItem(idOrg, getPayAgentContragent(),
                    startDate, endDate);
            ContragentCompletionItem contragentCompletionItemWithTransactionOrgIsNull = generateReportItemWithTransactionOrgIsNull(
                    idOrg, getPayAgentContragent(), startDate, endDate);
            contragentCompletionItems.add(contragentCompletionItem);
            contragentCompletionItems.add(contragentCompletionItemWithTransactionOrgIsNull);
            total.addContragentPayItems(contragentCompletionItem.getContragentPayItems());
            total.addContragentPayItems(contragentCompletionItemWithTransactionOrgIsNull.getContragentPayItems());
        }
        contragentCompletionItems.add(total);

        List<ContragentCompletionReportItem> contragentCompletionReportItems = new ArrayList<ContragentCompletionReportItem>();
        for (ContragentCompletionItem contragentCompletionItem : contragentCompletionItems) {
            for (Contragent contragent : getPayAgentContragent()) {
                String educationalInstitutionName = contragentCompletionItem.getEducationalInstitutionName();
                String contragentName = contragent.getContragentName();
                Long paySum = contragentCompletionItem.getContragentPayValue(contragent.getIdOfContragent());
                if ((paySum > 0) && (contragentName != null) && (educationalInstitutionName != null)) {
                    ContragentCompletionReportItem contragentCompletionReportItem = new ContragentCompletionReportItem(
                            contragentName, educationalInstitutionName, paySum);
                    contragentCompletionReportItems.add(contragentCompletionReportItem);
                }
            }
        }

        return contragentCompletionReportItems;
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
