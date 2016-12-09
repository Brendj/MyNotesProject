/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
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
    public List<ContragentCompletionItem> generateReportItem(List<Long> idOfOrgList, List<Contragent> contragentList, Date startDate,
            Date endDate, Contragent defaultSupplier) {
        Criteria criteriaIsNotNull = getSession().createCriteria(ClientPayment.class);
        criteriaIsNotNull.createAlias("transaction", "tr")

                .createAlias("tr.client", "cl").createAlias("contragent", "c")
                .add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));

        if (!idOfOrgList.isEmpty()) {
            criteriaIsNotNull.add(Restrictions.isNotNull("tr.org")).add(Restrictions.in("tr.org.idOfOrg", idOfOrgList));
        }

        criteriaIsNotNull.setProjection(Projections.projectionList().add(Projections.sum("paySum"))
                .add(Projections.groupProperty("c.idOfContragent")).add(Projections.count("idOfClientPayment"))
        .add(Projections.groupProperty("tr.org")));
        criteriaIsNotNull.add(Restrictions.between("createTime", startDate, endDate));
        criteriaIsNotNull.add(Restrictions.eq("contragentReceiver", defaultSupplier));
        List list = criteriaIsNotNull.list();

        List<ContragentCompletionItem> contragentCompletionItemList = new ArrayList<ContragentCompletionItem>();

        for (Object obj : list) {
            Object[] vals = (Object[]) obj;
            ContragentCompletionItem contragentCompletionItem = new ContragentCompletionItem(contragentList);
            contragentCompletionItem.setContragentPayItems(vals);

            contragentCompletionItemList.add(contragentCompletionItem);
        }
        return contragentCompletionItemList;
    }

    @SuppressWarnings("unchecked")
    public List<ContragentCompletionItem> generateReportItemWithTransactionOrgIsNull(List<Long> idOfOrgList,
            List<Contragent> contragentList, Date startDate, Date endDate) {

        Criteria criteriaIsNull = getSession().createCriteria(ClientPayment.class);
        criteriaIsNull.createAlias("transaction", "tr").add(Restrictions.isNull("tr.org"))
                .createAlias("tr.client", "cl")
                .createAlias("contragent", "c").add(Restrictions.eq("c.classId", Contragent.PAY_AGENT));
        if (!idOfOrgList.isEmpty()) {
            criteriaIsNull.add(Restrictions.in("cl.org.idOfOrg", idOfOrgList));
        } criteriaIsNull.setProjection(Projections.projectionList().add(Projections.sum("paySum"))
                .add(Projections.groupProperty("c.idOfContragent")).add(Projections.count("idOfClientPayment"))
                .add(Projections.groupProperty("cl.org")));
        criteriaIsNull.add(Restrictions.between("createTime", startDate, endDate));
        List list = criteriaIsNull.list();

        List<ContragentCompletionItem> contragentCompletionItemList = new ArrayList<ContragentCompletionItem>();

        for (Object obj : list) {
            Object[] vals = (Object[]) obj;
            ContragentCompletionItem contragentCompletionItem = new ContragentCompletionItem(contragentList);
            contragentCompletionItem.setContragentPayItems(vals);

            contragentCompletionItemList.add(contragentCompletionItem);
        }
        return contragentCompletionItemList;
    }

     @SuppressWarnings("unchecked")
    public List<ContragentCompletionReportItem> generateReportItems(List<Long> idOfOrgList, Date startDate,
            Date endDate, Contragent defaultSupplier) {
        List<ContragentCompletionItem> contragentCompletionItems = new ArrayList<ContragentCompletionItem>();

        ContragentCompletionItem total = new ContragentCompletionItem(getPayAgentContragent());

            List<ContragentCompletionItem> completionItemList = generateReportItem(idOfOrgList, getPayAgentContragent(),
                    startDate, endDate, defaultSupplier);
            List<ContragentCompletionItem> contragentCompletionItemWithTransactionOrgIsNullList = generateReportItemWithTransactionOrgIsNull(
                    idOfOrgList, getPayAgentContragent(), startDate, endDate);

         for (ContragentCompletionItem completionItem: completionItemList) {
             contragentCompletionItems.add(completionItem);
             total.addContragentPayItems(completionItem.getContragentPayItems());
         }

         for (ContragentCompletionItem contragentCompletionItemWithTransactionOrgIsNull : contragentCompletionItemWithTransactionOrgIsNullList) {
             contragentCompletionItems.add(contragentCompletionItemWithTransactionOrgIsNull);
             total.addContragentPayItems(contragentCompletionItemWithTransactionOrgIsNull.getContragentPayItems());
         }

        contragentCompletionItems.add(total);

        List<ContragentCompletionReportItem> contragentCompletionReportItems = new ArrayList<ContragentCompletionReportItem>();
        for (ContragentCompletionItem contragentCompletionItem : contragentCompletionItems) {
            for (Contragent contragent : getPayAgentContragent()) {
                Long educationalId = contragentCompletionItem.getEducationalId();
                String educationalInstitutionName = contragentCompletionItem.getEducationalInstitutionName();
                String contragentName = contragent.getContragentName();
                String educationalCity = contragentCompletionItem.getEducationalCity();
                int paymentCount = contragentCompletionItem.getPaymentsCount();
                Long paySum = contragentCompletionItem.getContragentPayValue(contragent.getIdOfContragent());
                if ((paySum > 0) && (contragentName != null) && (educationalInstitutionName != null)) {
                    ContragentCompletionReportItem contragentCompletionReportItem = new ContragentCompletionReportItem(educationalId,
                            educationalInstitutionName, educationalCity, contragentName,  paySum, paymentCount);
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
