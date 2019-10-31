/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.12.14
 * Time: 13:25
 */
@Deprecated
public class DailyFormationOfRegistriesService {

    // метод выбора контрагентов ТСП
    public static List<Contragent> getContragentTSP(Session session) {
        Criteria criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", Contragent.TSP));
        List<Contragent> result = criteria.list();
        return result;
    }

    // Метод сбора данных для выгрузки в xml
    public static List<DailyFormationOfRegistriesModel> getGenerationReportResult(Session session,
            List<Contragent> contragentList, Date generationDate) {

        List<DailyFormationOfRegistriesModel> registriesModels = new ArrayList<DailyFormationOfRegistriesModel>();
        for (Contragent contragent : contragentList) {
            DailyFormationOfRegistriesModel dailyFormationOfRegistriesModel = new DailyFormationOfRegistriesModel();

            dailyFormationOfRegistriesModel.setGeneratedDate(generationDate);
            dailyFormationOfRegistriesModel.setContragentId(contragent.getIdOfContragent());
            dailyFormationOfRegistriesModel.setContragentName(contragent.getContragentName());

            Set<Org> contragentOrgs = contragent.getOrgs();

            List<Long> contragentOrgsList = new ArrayList<Long>();

            for (Org org : contragentOrgs) {
                contragentOrgsList.add(org.getIdOfOrg());
            }

            List<OrganizationSalesAmount> organizationSalesAmounts = orgSalesAmount(session, contragentOrgsList,
                    generationDate);

            List<OrganizationRechargeAmount> organizationRechargeAmounts = orgRechargeAmount(session,
                    contragentOrgsList, generationDate);

            List<OrgItem> orgItemList = new ArrayList<OrgItem>();
            for (Org org : contragentOrgs) {
                OrgItem orgItem = new OrgItem();

                orgItem.setOrgNum(org.getOrgNumberInName());
                orgItem.setIdOfOrg(org.getIdOfOrg());
                orgItem.setAddress(org.getAddress());
                orgItem.setOfficialName(org.getOfficialName());

                Long totalBalance = orgBalance(session, org.getIdOfOrg());


                orgItem.setTotalBalance(totalBalance);

                for (OrganizationRechargeAmount organizationRechargeAmount : organizationRechargeAmounts) {
                    if (orgItem.getIdOfOrg().equals(organizationRechargeAmount.getIdOfOrg())) {
                        orgItem.setRechargeAmount(organizationRechargeAmount.getRechargeAmount());
                    } else {
                        orgItem.setRechargeAmount(0L);
                    }
                    break;
                }

                if (orgItem.getRechargeAmount() == null) {
                    orgItem.setRechargeAmount(0L);
                }

                for (OrganizationSalesAmount organizationSalesAmount : organizationSalesAmounts) {
                    if (orgItem.getIdOfOrg().equals(organizationSalesAmount.getIdOfOrg())) {
                        orgItem.setSalesAmount(organizationSalesAmount.getSalesAmount());
                    } else {
                        orgItem.setSalesAmount(0L);
                    }
                    break;
                }

                if (orgItem.getSalesAmount() == null) {
                    orgItem.setSalesAmount(0L);
                }
                orgItemList.add(orgItem);
            }
            dailyFormationOfRegistriesModel.setOrgItemList(orgItemList);

            registriesModels.add(dailyFormationOfRegistriesModel);
        }

        return registriesModels;
    }

    public static Long orgBalance(Session session, Long orgId) {
        Query query = session.createSQLQuery(
                "SELECT sum(cfc.balance) FROM cf_orgs cfo LEFT JOIN cf_clients cfc ON cfc.idoforg = cfo.idoforg WHERE cfo.idoforg = :idOfOrg AND cfc.balance > 0");
        query.setParameter("idOfOrg", orgId);
        if (query.uniqueResult() != null) {
            return ((BigDecimal) query.uniqueResult()).longValue();
        }
        return 0L;
    }

    // платежи на лс. не на субсчет. paytype=1
    public static List<OrganizationRechargeAmount> orgRechargeAmount(Session session, List<Long> contragentOrgs,
            Date rechargeDate) {
        Date beforeDate = CalendarUtils.subOneDay(CalendarUtils.truncateToDayOfMonth(rechargeDate));

        List<OrganizationRechargeAmount> organizationRechargeAmounts = new ArrayList<OrganizationRechargeAmount>();

        Query query = session.createSQLQuery(
                "SELECT cfo.idoforg, sum(cfcl.paysum) FROM cf_orgs cfo LEFT JOIN cf_clients cfc ON cfc.idoforg = cfo.idoforg LEFT JOIN cf_clientpayments cfcl "
                        + "ON cfc.idofclient = cfcl.idofclientpayment WHERE cfo.idoforg IN ( :contragentOrgs ) AND cfcl.paytype IN (1, 2) "
                        + "AND cfcl.createddate BETWEEN :beforeDate AND :rechargeDate " + "GROUP BY cfo.idoforg");
        query.setParameterList("contragentOrgs", contragentOrgs);
        query.setParameter("beforeDate", beforeDate.getTime());
        query.setParameter("rechargeDate", rechargeDate.getTime());

        List result = query.list();

        for (Object o : result) {
            Object[] resultOrganizationRechargeAmount = (Object[]) o;
            OrganizationRechargeAmount organizationSalesAmount = new OrganizationRechargeAmount(
                    ((BigInteger) resultOrganizationRechargeAmount[0]).longValue(),
                    ((BigDecimal) resultOrganizationRechargeAmount[1]).longValue());
            organizationRechargeAmounts.add(organizationSalesAmount);
        }
        return organizationRechargeAmounts;
    }

    // сумма продаж "Покупка" - sourcetype = 8
    public static List<OrganizationSalesAmount> orgSalesAmount(Session session, List<Long> contragentOrgs,
            Date salesDate) {
        Date beforeDate = CalendarUtils.subOneDay(CalendarUtils.truncateToDayOfMonth(salesDate));

        List<OrganizationSalesAmount> organizationSalesAmounts = new ArrayList<OrganizationSalesAmount>();

        Query query = session.createSQLQuery(
                "SELECT cfo.idoforg, sum(cford.rsum) FROM cf_orgs cfo LEFT JOIN cf_transactions cft ON cfo.idoforg = cft.idoforg "
                        + "LEFT JOIN cf_orders cford ON cford.idoftransaction = cft.idoftransaction WHERE cft.sourcetype = 8 AND "
                        + "cford.state = 0 AND cfo.idoforg  IN ( :contragentOrgs ) AND cft.transactiondate BETWEEN :beforeDate AND :salesDate "
                        + "GROUP BY cfo.idoforg");
        query.setParameterList("contragentOrgs", contragentOrgs);
        query.setParameter("beforeDate", beforeDate.getTime());
        query.setParameter("salesDate", salesDate.getTime());

        List result = query.list();

        for (Object o : result) {
            Object[] resultOrganizationSalesAmount = (Object[]) o;
            OrganizationSalesAmount organizationSalesAmount = new OrganizationSalesAmount(
                    ((BigInteger) resultOrganizationSalesAmount[0]).longValue(),
                    ((BigDecimal) resultOrganizationSalesAmount[1]).longValue());
            organizationSalesAmounts.add(organizationSalesAmount);
        }
        return organizationSalesAmounts;
    }

    public static List<StornedOrdersOrganizationSalesAmount> orgStornedOrdersSalesAmount(Session session,
            List<Long> contragentOrgs, Date salesDate) {
        Date beforeDate = CalendarUtils.subOneDay(CalendarUtils.truncateToDayOfMonth(salesDate));

        List<StornedOrdersOrganizationSalesAmount> stornedOrdersOrganizationSalesAmounts = new ArrayList<StornedOrdersOrganizationSalesAmount>();

        return stornedOrdersOrganizationSalesAmounts;
    }

    //Модель данных для выгрузки в xml - DailyFormationOfRegistriesModel
    public static class DailyFormationOfRegistriesModel {

        // тег Отчет
        public Date generatedDate;

        // тег Контрагент ТСП
        public Long contragentId;
        public String contragentName;

        List<OrgItem> orgItemList = new ArrayList<OrgItem>();

        public DailyFormationOfRegistriesModel() {
        }

        public DailyFormationOfRegistriesModel(Date generatedDate, Long contragentId, String contragentName,
                List<OrgItem> orgItemList) {
            this.generatedDate = generatedDate;
            this.contragentId = contragentId;
            this.contragentName = contragentName;
            this.orgItemList = orgItemList;
        }

        public Date getGeneratedDate() {
            return generatedDate;
        }

        public void setGeneratedDate(Date generatedDate) {
            this.generatedDate = generatedDate;
        }

        public Long getContragentId() {
            return contragentId;
        }

        public void setContragentId(Long contragentId) {
            this.contragentId = contragentId;
        }

        public String getContragentName() {
            return contragentName;
        }

        public void setContragentName(String contragentName) {
            this.contragentName = contragentName;
        }

        public List<OrgItem> getOrgItemList() {
            return orgItemList;
        }

        public void setOrgItemList(List<OrgItem> orgItemList) {
            this.orgItemList = orgItemList;
        }
    }

    // Данные об организации
    public static class OrgItem {

        // тег Организация
        public String orgNum;
        public Long idOfOrg;
        public String officialName;
        public String address;
        public Long totalBalance;
        public Long rechargeAmount;
        public Long salesAmount;

        public List<StornedOrdersOrganizationSalesAmount> stornedOrdersOrganizationSalesAmounts = new ArrayList<StornedOrdersOrganizationSalesAmount>();

        public OrgItem() {
        }

        public OrgItem(String orgNum, Long idOfOrg, String officialName, String address, Long totalBalance,
                Long rechargeAmount, Long salesAmount) {
            this.orgNum = orgNum;
            this.idOfOrg = idOfOrg;
            this.officialName = officialName;
            this.address = address;
            this.totalBalance = totalBalance;
            this.rechargeAmount = rechargeAmount;
            this.salesAmount = salesAmount;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Long getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(Long totalBalance) {
            this.totalBalance = totalBalance;
        }

        public Long getRechargeAmount() {
            return rechargeAmount;
        }

        public void setRechargeAmount(Long rechargeAmount) {
            this.rechargeAmount = rechargeAmount;
        }

        public Long getSalesAmount() {
            return salesAmount;
        }

        public void setSalesAmount(Long salesAmount) {
            this.salesAmount = salesAmount;
        }

        public List<StornedOrdersOrganizationSalesAmount> getStornedOrdersOrganizationSalesAmounts() {
            return stornedOrdersOrganizationSalesAmounts;
        }

        public void setStornedOrdersOrganizationSalesAmounts(
                List<StornedOrdersOrganizationSalesAmount> stornedOrdersOrganizationSalesAmounts) {
            this.stornedOrdersOrganizationSalesAmounts = stornedOrdersOrganizationSalesAmounts;
        }
    }

    public static class OrganizationSalesAmount {

        public Long idOfOrg;
        public Long salesAmount;

        public OrganizationSalesAmount(Long idOfOrg, Long salesAmount) {
            this.idOfOrg = idOfOrg;
            this.salesAmount = salesAmount;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Long getSalesAmount() {
            return salesAmount;
        }

        public void setSalesAmount(Long salesAmount) {
            this.salesAmount = salesAmount;
        }
    }

    public static class OrganizationRechargeAmount {

        public Long idOfOrg;
        public Long rechargeAmount;

        public OrganizationRechargeAmount(Long idOfOrg, Long rechargeAmount) {
            this.idOfOrg = idOfOrg;
            this.rechargeAmount = rechargeAmount;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Long getRechargeAmount() {
            return rechargeAmount;
        }

        public void setRechargeAmount(Long rechargeAmount) {
            this.rechargeAmount = rechargeAmount;
        }
    }
}
