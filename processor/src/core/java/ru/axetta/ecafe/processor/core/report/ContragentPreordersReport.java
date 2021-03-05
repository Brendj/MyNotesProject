/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;

import java.util.*;

@Component
@Scope("singleton")
public class ContragentPreordersReport extends BasicReportForContragentJob {
    private Logger logger = LoggerFactory.getLogger(ContragentPreordersReport.class);


    public static class Builder extends BasicReportForContragentJob.Builder {
        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance()
                    .getAutoReportGenerator().getReportsTemplateFilePath()
                    + ContragentPreordersReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.startOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            Long idOfContragent = (contragent == null ? null : contragent.getIdOfContragent());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            Boolean showOnlyUnpaidItems = Boolean.valueOf(getReportProperties().getProperty("showOnlyUnpaidItems"));

            JRDataSource dataSource = createDataSource(session, contragent, startTime, endTime,
                    idOfOrgList, showOnlyUnpaidItems);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent);
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime,
                List<Long> idOfOrgList, Boolean showOnlyUnpaidItems) throws Exception {
            List<ContragentPreordersReportItem> result = new LinkedList<ContragentPreordersReportItem>();
            String idOfOrgsCondition = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and o.idoforg in (:idOfOrgList) " ;
            String idOfContragentCondition = contragent == null ? "" : " and ctg.idofcontragent = :idOfContragent ";
            String simpleCondition = showOnlyUnpaidItems ? " where isPaid like 'Нет' " : "";
            Query query;

            String getDataQuery  = "with contragent_preorders_table AS ( "
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, "
                    + "o.shortnameinfoservice, o.address, c.contractId, pc.preorderDate, "
                    + "pc.complexName, cast(pl.qty as bigint) as amount, '' as dish, "
                    + "cast((pc.complexPrice * pl.qty) as bigint), co.createddate as cancelDate, "
                    + "case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as reversed, "
                    + "ord.orderDate, pl.qty*pl.price as orderSum, pl.idOfOrder, case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as isPaid, "
                    + "coalesce(pc.usedsum, 0) as usedsum, "
                    + "case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus, "
                    + " '' as usedamounts, "
                    + "case coalesce(gto.issixdaysworkweek, 0) when 0 then 'Нет' else 'Да' end as issixdaysworkweek,  '' as dishAmounts, c.idofclientgroup "
                    + "from cf_preorder_complex pc "
                    + "join cf_clients c on pc.idofclient = c.idofclient "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + "left join cf_orders ord on ord.idOfOrg = pl.idOfOrg and ord.idOfOrder = pl.idOfOrder "
                    + "left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction "
                    + "left join cf_canceledorders co on ord.idOfOrder = co.idOfOrder and ord.idOfOrg = co.idOfOrg "
                    + "left join cf_preorder_status ps on ps.guid = pc.guid "
                    + "left join cf_specialdates sd on sd.idoforg = pc.idoforgoncreate "
                    + " left join cf_clientgroups cg on cg.idoforg = pc.idoforgoncreate and cg.idofclientgroup =  c.idofclientgroup "
                    + " left join cf_groupnames_to_orgs gto on gto.groupname = cg.groupname and gto.idOfOrg = pc.idoforgoncreate "
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1 "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " union"
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, c.contractId, pc.preorderDate, "
                    + " pc.complexName, cast(1 as bigint) as amount, array_to_string(array_agg(pmd.shortname), '&&') as dish, "
                    + " cast(sum(pmd.menudetailprice * pmd.amount) as bigint), co.createddate as cancelDate, "
                    + " case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as reversed, "
                    + " ord.orderDate, pl.qty*pl.price as orderSum, pl.idOfOrder,  case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as isPaid, "
                    + "cast(sum(pmd.usedsum) as bigint) as usedsum, "
                    + "case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus, "
                    + " array_to_string(array_agg(pmd.usedamount), '&&')  as usedamounts, "
                    + " '' as issixdaysworkweek, array_to_string(array_agg(pmd.amount), '&&') as DishAounts, c.idofclientgroup "
                    + " from cf_preorder_menudetail pmd "
                    + " join cf_clients c on pmd.idofclient = c.idofclient "
                    + " join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + " join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + " join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join cf_orders ord on ord.idOfOrg = pl.idOfOrg and ord.idOfOrder = pl.idOfOrder"
                    + " left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction"
                    + " left join cf_canceledorders co on ord.idOfOrder = co.idOfOrder and ord.idOfOrg = co.idOfOrg"
                    + " left join cf_preorder_status ps on ps.guid = pc.guid "
                    + " where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.amount > 0 and o.PreordersEnabled = 1"
                    + " and pmd.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17, psStatus, issixdaysworkweek, c.idofclientgroup"
                    + " UNION "
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, "
                    + " o.shortNameInfoService, o.address, c.contractId, pc.preorderDate,"
                    + " pc.complexName, cast((pc.amount - coalesce(q.payed, 0)) as bigint) as amount, '' as dish, cast((pc.complexPrice * (pc.amount - coalesce(q.payed, 0))) as bigint) as complexPrice,"
                    + " cast(null as bigint) as createddate, 'Нет'  as reversed,"
                    + " cast(null as bigint) as idOfOrder, cast(null as bigint) as orderSum, cast(null as bigint), 'Нет' as isPaid, "
                    + "coalesce(pc.usedsum, 0) as usedsum, "
                    + "case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus, "
                    + "'' as usedamounts, "
                    + "case coalesce(gto.issixdaysworkweek, 0) when 0 then 'Нет' else 'Да' end as issixdaysworkweek, '' as dishAmounts, c.idofclientgroup "
                    + " from cf_preorder_complex pc "
                    + " join cf_clients c on pc.idofclient = c.idofclient "
                    + " join cf_orgs o on pc.idoforgoncreate = o.idOfOrg "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent"
                    + " left join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join (select preorderguid, sum(qty) as payed from cf_preorder_linkod group by  preorderguid) q on q.preorderguid = pc.guid "
                    + " left join cf_preorder_status ps on ps.guid = pc.guid "
                    + " left join cf_specialdates sd on sd.idoforg = pc.idoforgoncreate"
                    + " left join cf_clientgroups cg on cg.idoforg = pc.idoforgoncreate and cg.idofclientgroup =  c.idofclientgroup "
                    + " left join cf_groupnames_to_orgs gto on gto.groupname = cg.groupname and gto.idOfOrg = pc.idoforgoncreate "
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1"
                    + " and (pc.amount - coalesce(q.payed, 0)) > 0 "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, psStatus, usedamounts, issixdaysworkweek, dishAmounts, c.idofclientgroup "
                    + "union "
                    + "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg,"
                    + "o.shortNameInfoService, o.address, c.contractId, pc.preorderDate, "
                    + "pc.complexName, cast(1 as bigint) as amount, array_to_string(array_agg(pmd.shortname), '&&') as dish, "
                    + "cast(sum(pmd.menudetailprice * pmd.amount) as bigint) as complexPrice, cast(null as bigint) as cancelDate, 'Нет'  as reversed,"
                    + "cast(null as bigint) as orderDate, cast(null as bigint) as orderSum, cast(null as bigint) as idOfOrder, 'Нет' as isPaid, "
                    + "cast(sum(pmd.usedsum) as bigint) as usedsum, "
                    + "case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus, "
                    + " array_to_string(array_agg(pmd.usedamount), '&&')  as usedamounts, "
                    + " '' as issixdaysworkweek, array_to_string(array_agg(pmd.amount), '&&')  as dishAounts, c.idofclientgroup "
                    + "from cf_preorder_menudetail pmd "
                    + "join cf_clients c on pmd.idofclient = c.idofclient "
                    + "join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "left join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + "left join cf_canceledorders co on pl.idOfOrder = co.idOfOrder and pl.idOfOrg = co.idOfOrg  "
                    + "left join cf_preorder_status ps on ps.guid = pc.guid "
                    + "where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.amount > 0 and pc.usedamount = 0"
                    + " and o.PreordersEnabled = 1 and co.idOfOrder is null "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17, psStatus, issixdaysworkweek, c.idofclientgroup )"
                    + " select * from contragent_preorders_table "
                    + simpleCondition
                    + " order by 6 desc, 7, 1, 2, 3, 4 ";

            String getDishAmount = "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, "
                    + "pmd.shortname, pmd.menudetailprice, pmd.usedamount, case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus "
                    + "from cf_preorder_menudetail pmd "
                    + "join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "left join cf_preorder_status ps on ps.guid = pc.guid "
                    + "join cf_clients c on pc.idofclient = c.idofclient "
                    + "left join cf_specialdates sd on sd.idoforg = pc.idoforgoncreate and sd.idofclientgroup = c.idofclientgroup and sd.date = pc.preorderDate "
                    + "where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.usedamount > 0 "
                    + "and o.PreordersEnabled = 1 and pmd.amount > 0 and pc.usedamount > 0  "
                    + "and pmd.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " order by 1";

            String getDish = "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, pmd.shortname, pmd.menudetailprice "
                    + "from cf_preorder_menudetail pmd "
                    + "join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.usedamount > 0 "
                    + "and o.PreordersEnabled = 1 and pmd.amount > 0 and pc.usedamount > 0 "
                    + "and pmd.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, pmd.shortname, pmd.menudetailprice "
                    + " order by 1";

            String getComplexAmount = "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, pc.complexname, "
                    + "pc.complexprice,  pc.usedamount, case coalesce(ps.status, 0) when 0 then 'Нет' else 'Да' end as psStatus "
                    + "from  cf_preorder_complex pc "
                    + "left join cf_orgs o on o.idOfOrg = pc.idoforgoncreate "
                    + "left join cf_preorder_status ps on ps.guid = pc.guid "
                    + "left join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "left join cf_clients c on pc.idofclient = c.idofclient "
                    + "left join cf_specialdates sd on sd.idoforg = pc.idoforgoncreate and sd.idofclientgroup = c.idofclientgroup and sd.date = pc.preorderDate "
                    + "where pc.deletedstate = 0 "
                    + "and o.PreordersEnabled = 1 and pc.deletedstate = 0 and pc.usedamount > 0  and pc.modeofadd = 2 "
                    + "and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " order by 1";

            String getComplex = "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, pc.complexname, pc.complexprice "
                    + "from  cf_preorder_complex pc "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + "left join cf_preorder_status ps on ps.guid = pc.guid "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "where pc.deletedstate = 0 "
                    + "and o.PreordersEnabled = 1 and pc.deletedstate = 0 and pc.usedamount > 0 and pc.modeofadd = 2"
                    + "and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, pc.complexname, pc.complexprice "
                    + " order by 1";

            query = session.createSQLQuery(getDataQuery);
            query.setParameter("startDate", startTime.getTime())
                    .setParameter("endDate", endTime.getTime());
            if(contragent != null) {
                query.setParameter("idOfContragent", contragent.getIdOfContragent());
            }
            if(!CollectionUtils.isEmpty(idOfOrgList)){
                query.setParameterList("idOfOrgList", idOfOrgList);
            }
            List<Object[]> dataFromDB = query.list();
            if (CollectionUtils.isEmpty(dataFromDB)) {
                throw new Exception("Нет данных для построения отчета");
            }
            List<ContragentPreordersSubreportItem> subReportItem = new ArrayList<ContragentPreordersSubreportItem>();
            query = session.createSQLQuery(getComplex);
            List<Object[]> dataComplex = setParam(query, startTime, endTime, idOfOrgList);
            query = session.createSQLQuery(getComplexAmount);
            List<Object[]> dataComplexAmount = setParam(query, startTime, endTime, idOfOrgList);
            getSubReportItem(subReportItem, dataComplex, dataComplexAmount);

            query = session.createSQLQuery(getDishAmount);
            List<Object[]> dataDishAmount = setParam(query, startTime, endTime, idOfOrgList);
            query = session.createSQLQuery(getDish);
            List<Object[]> dataDish = setParam(query, startTime, endTime, idOfOrgList);
            getSubReportItem(subReportItem, dataDish, dataDishAmount);
            subReportItem = getFinal(getSort(subReportItem));

            for (Object[] row : dataFromDB) {
                Long idOfContragent = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                String contragentName = (String) row[1];
                Long idOfOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[2]);
                String orgShortName = (String) row[3];
                String orgShortAddress = (String) row[4];
                Long clientContractId = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[5]);
                Date preorderDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[6]);
                String complexName = (String) row[7];
                Integer amount = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[8]).intValue();
                Long complexPrice = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[10]);
                Date cancelDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[11]);
                String reversed = (String) row[12];
                Date createdDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[13]);
                Long orderSum = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[14]);
                Long idOfOrder = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[15]);
                String isPaid = (String) row[16];
                Long usedSum = isPaid.equals("Нет") ? 0L : DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[17]);
                String psStatus = (String) row[18];
                String isSixDaysWorkWeek = (String) row[20];
                Long idOfClientGroup = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[22]);
                String isWeekend = checkWeekend(session, idOfOrg, preorderDate, idOfClientGroup, isSixDaysWorkWeek);
                List<String> usedAmounts = new ArrayList<String>(Arrays.asList(row[19].toString().split("&&")));
                List<String> dish = new ArrayList<String>(Arrays.asList(row[9].toString().split("&&")));
                List<String> dishAmounts = new ArrayList<String>(Arrays.asList(row[21].toString().split("&&")));
                String paidDish = paidDish(dish, usedAmounts);
                String notPaidDish = notPaidDish(dish, usedAmounts, dishAmounts);
                ContragentPreordersReportItem item = new ContragentPreordersReportItem(idOfContragent, contragentName,
                        idOfOrg, orgShortName, orgShortAddress, clientContractId, preorderDate, complexName, amount,
                        paidDish, notPaidDish, complexPrice, cancelDate, reversed, createdDate, orderSum, idOfOrder, isPaid, usedSum, psStatus, isWeekend, subReportItem);
                result.add(item);
            }
            return new JRBeanCollectionDataSource(result);
        }

        private List<ContragentPreordersSubreportItem> getFinal(List<ContragentPreordersSubreportItem> subReportItem){
            List<ContragentPreordersSubreportItem> subReportItemNew = new ArrayList<>();
            int totalIndex = 0;
            for (int s = 0; s < subReportItem.size() ; s++){
                int finalCount = 0, finalPrice = 0;
                if(s + 1 == subReportItem.size() || !subReportItem.get(s).getIdOfContragent().equals(subReportItem.get(s + 1).getIdOfContragent())){
                    subReportItemNew.add(subReportItem.get(s));
                    for(int g = totalIndex; g <= s; g++){
                        finalCount += subReportItem.get(g).getAmount();
                        finalPrice += subReportItem.get(g).getAmount() * subReportItem.get(g).getComplexPrice();
                    }
                    totalIndex = s + 1;
                    ContragentPreordersSubreportItem item = new ContragentPreordersSubreportItem(subReportItem.get(s).getIdOfContragent(),
                            subReportItem.get(s).getContragentName(), null, null,
                            null, "Итого", finalCount, null,
                            (long)finalPrice, "0");
                    subReportItemNew.add(item);
                }
                else subReportItemNew.add(subReportItem.get(s));
            }
            return subReportItemNew;
        }
        private String checkWeekend(Session session, Long idOfOrg, Date preorderDate, Long idOfClientGroup, String isSixDaysWorkWeek ){
            String isWeekend = "Да";
            Criteria criteria = session.createCriteria(SpecialDate.class);
            criteria.add(Restrictions.eq("date", preorderDate));
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            criteria.add(Restrictions.eq("deleted", false));
            criteria.add(Restrictions.isNull("idOfClientGroup"));
            List<SpecialDate> specialDates = criteria.list();
            if(specialDates.size() > 0)
                for (SpecialDate sp : specialDates)
                    isWeekend = sp.getIsWeekend() ? "Нет" : "Да";

            criteria = session.createCriteria(SpecialDate.class);
            criteria.add(Restrictions.eq("date", preorderDate));
            criteria.add(Restrictions.eq("idOfClientGroup", idOfClientGroup));
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            criteria.add(Restrictions.eq("deleted", false));
            specialDates = criteria.list();
            if(specialDates.size() > 0)
                for (SpecialDate sp : specialDates)
                    isWeekend = sp.getIsWeekend() ? "Нет" : "Да";
            Calendar c = Calendar.getInstance();
            c.setTime(preorderDate);

            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                if (isSixDaysWorkWeek.equals("Нет"))
                    isWeekend = "Нет";
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                isWeekend = "Нет";
            return isWeekend;
        }

        private void getSubReportItem(List<ContragentPreordersSubreportItem> subReportItem, List<Object[]> dataComplex, List<Object[]> dataComplexAmount){
            for (Object[] row : dataComplex) {
                Long idOfContragent = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                String contragentName = (String) row[1];
                Long idOfOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[2]);
                String orgShortName = (String) row[3];
                String orgShortAddress = (String) row[4];
                String complexName = (String) row[5];
                Long complexPrice = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[6]);
                int dishCount = 0;
                for (Object[] amount : dataComplexAmount) {
                    if (complexName.equals(amount[5].toString()) && DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(amount[7]).intValue() > 0)
                        dishCount += DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(amount[7]).intValue();
                }
                if(dishCount > 0){
                    ContragentPreordersSubreportItem item = new ContragentPreordersSubreportItem(idOfContragent,
                            contragentName, idOfOrg, orgShortName, orgShortAddress, complexName, dishCount, complexPrice,
                            dishCount * complexPrice, "1");
                    subReportItem.add(item);
                }
            }
        }

        private List<ContragentPreordersSubreportItem> getSort(List<ContragentPreordersSubreportItem> subReportItem){
            for (ContragentPreordersSubreportItem row: subReportItem) {
                for (int s = 0; s < subReportItem.size() - 1; s++)
                    if (subReportItem.get(s).getIdOfContragent() > subReportItem.get(s + 1).getIdOfContragent())
                        Collections.swap(subReportItem, s, s + 1);
            }
            return subReportItem;
        }

        private List<Object[]> setParam(Query query, Date startTime, Date endTime, List<Long> idOfOrgList)
                throws Exception {
            query.setParameter("startDate", startTime.getTime())
                    .setParameter("endDate", endTime.getTime());
            if(contragent != null) {
                query.setParameter("idOfContragent", contragent.getIdOfContragent());
            }
            if(!CollectionUtils.isEmpty(idOfOrgList)){
                query.setParameterList("idOfOrgList", idOfOrgList);
            }
            return (List<Object[]>) query.list();
        }

        private String paidDish(List<String> dish, List<String> usedAmount){
            StringBuilder result = new StringBuilder ();
            for (int s = 0; s < dish.size(); s++)
                if (!usedAmount.get(s).equals("0") && dish.get(s).length() > 0)
                    result.append(dish.get(s)).append(" - ").append(usedAmount.get(s)).append(", ");
            if (result.length() > 1)
                result.setLength(result.length() - 2);
            return result.toString();
        }

        private String notPaidDish(List<String> dish, List<String> usedAmount, List<String> dishAmount){
            StringBuilder result = new StringBuilder ();
            try {
                for (int s = 0; s < dish.size(); s++)
                    if (0 < Integer.parseInt(dishAmount.get(s)) - Integer.parseInt(usedAmount.get(s)) && dish.get(s).length() > 0)
                        result.append(dish.get(s)).append(" - ").append(Integer.parseInt(dishAmount.get(s)) - Integer.parseInt(usedAmount.get(s))).append(", ");
                if (result.length() > 1)
                    result.setLength(result.length() - 2);
            } catch (NumberFormatException ignore){};
            return result.toString();
        }

    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ContragentPreordersReport();
    }

    @Override
    public ContragentPreordersReport.Builder createBuilder(String templateFilename) {
        return new ContragentPreordersReport.Builder(templateFilename);
    }
}
