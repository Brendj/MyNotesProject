/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

            String getDataQuery  = "with contragent_preorders_table AS ( "
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, "
                    + "o.shortnameinfoservice, o.address, c.contractId, pc.preorderDate, "
                    + "pc.complexName, cast(pl.qty as bigint) as amount, '' as dish, "
                    + "cast((pc.complexPrice * pl.qty) as bigint), co.createddate as cancelDate, "
                    + "case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as reversed, "
                    + "ord.orderDate, pl.qty*pl.price as orderSum, pl.idOfOrder, case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as isPaid, "
                    + "coalesce(pc.usedsum, 0) as usedsum "
                    + "from cf_preorder_complex pc "
                    + "join cf_clients c on pc.idofclient = c.idofclient "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + "left join cf_orders ord on ord.idOfOrg = pl.idOfOrg and ord.idOfOrder = pl.idOfOrder "
                    + "left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction "
                    + "left join cf_canceledorders co on ord.idOfOrder = co.idOfOrder and ord.idOfOrg = co.idOfOrg "
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1 "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " union"
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, o.shortNameInfoService, o.address, c.contractId, pc.preorderDate, "
                    + " pc.complexName, cast(1 as bigint) as amount, array_to_string(array_agg(pmd.menudetailname), ', ') as dish, "
                    + " cast(sum(pmd.menudetailprice * pmd.amount) as bigint), co.createddate as cancelDate, "
                    + " case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as reversed, "
                    + " ord.orderDate, pl.qty*pl.price as orderSum, pl.idOfOrder,  case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as isPaid, "
                    + "cast(sum(pmd.usedsum) as bigint) as usedsum "
                    + " from cf_preorder_menudetail pmd "
                    + " join cf_clients c on pmd.idofclient = c.idofclient "
                    + " join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + " join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + " join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join cf_orders ord on ord.idOfOrg = pl.idOfOrg and ord.idOfOrder = pl.idOfOrder"
                    + " left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction"
                    + " left join cf_canceledorders co on ord.idOfOrder = co.idOfOrder and ord.idOfOrg = co.idOfOrg"
                    + " where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.amount > 0 and o.PreordersEnabled = 1"
                    + " and pmd.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17"
                    + " UNION "
                    + " select ctg.idOfContragent, ctg.contragentName, o.idOfOrg, "
                    + " o.shortNameInfoService, o.address, c.contractId, pc.preorderDate,"
                    + " pc.complexName, cast((pc.amount - coalesce(q.payed, 0)) as bigint) as amount, '' as dish, cast((pc.complexPrice * (pc.amount - coalesce(q.payed, 0))) as bigint) as complexPrice,"
                    + " cast(null as bigint) as createddate, 'Нет'  as reversed,"
                    + " cast(null as bigint) as idOfOrder, cast(null as bigint) as orderSum, cast(null as bigint), 'Нет' as isPaid, "
                    + "coalesce(pc.usedsum, 0) as usedsum "
                    + " from cf_preorder_complex pc "
                    + " join cf_clients c on pc.idofclient = c.idofclient "
                    + " join cf_orgs o on pc.idoforgoncreate = o.idOfOrg "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent"
                    + " left join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join (select preorderguid, sum(qty) as payed from cf_preorder_linkod group by  preorderguid) q on q.preorderguid = pc.guid "
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1"
                    + " and (pc.amount - coalesce(q.payed, 0)) > 0 "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18"
                    + "union "
                    + "select ctg.idOfContragent, ctg.contragentName, o.idOfOrg,"
                    + "o.shortNameInfoService, o.address, c.contractId, pc.preorderDate, "
                    + "pc.complexName, cast(1 as bigint) as amount, array_to_string(array_agg(pmd.menudetailname), ', ') as dish, "
                    + "cast(sum(pmd.menudetailprice * pmd.amount) as bigint) as complexPrice, cast(null as bigint) as cancelDate, 'Нет'  as reversed,"
                    + "cast(null as bigint) as orderDate, cast(null as bigint) as orderSum, cast(null as bigint) as idOfOrder, 'Нет' as isPaid, "
                    + "cast(sum(pmd.usedsum) as bigint) as usedsum "
                    + "from cf_preorder_menudetail pmd "
                    + "join cf_clients c on pmd.idofclient = c.idofclient "
                    + "join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + "join cf_orgs o on pc.idoforgoncreate = o.idOfOrg  "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idOfContragent "
                    + "left join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + "left join cf_canceledorders co on pl.idOfOrder = co.idOfOrder and pl.idOfOrg = co.idOfOrg  "
                    + "where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.amount > 0 and pc.usedamount = 0"
                    + "  and o.PreordersEnabled = 1 and co.idOfOrder is null "
                    + " and pc.preorderDate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17 )"
                    + " select * from contragent_preorders_table "
                    + simpleCondition
                    + " order by 1, 6 desc, 2, 3, 4 ";



            Query query = session.createSQLQuery(getDataQuery);
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
                String dish = (String) row[9];
                Long complexPrice = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[10]);
                Date cancelDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[11]);
                String reversed = (String) row[12];
                Date createdDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(row[13]);
                Long orderSum = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[14]);
                Long idOfOrder = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[15]);
                String isPaid = (String) row[16];
                Long usedSum = isPaid.equals("Нет") ? 0L : DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[17]);
                ContragentPreordersReportItem item = new ContragentPreordersReportItem(idOfContragent, contragentName,
                        idOfOrg, orgShortName, orgShortAddress, clientContractId, preorderDate, complexName, amount,
                        dish, complexPrice, cancelDate, reversed, createdDate, orderSum, idOfOrder, isPaid, usedSum);
                result.add(item);
            }
            return new JRBeanCollectionDataSource(result);
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
