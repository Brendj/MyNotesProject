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
                List<Long> idOfOrgList, Boolean showOnlyUnpaidItems) throws Exception{
            List<ContragentPreordersReportItem> result = new LinkedList<ContragentPreordersReportItem>();
            String idOfOrgsCondition = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and o.idoforg in (:idOfOrgList) " ;
            String idOfContragentCondition = contragent == null ? "" : " and ctg.idofcontragent = :idOfContragent ";
            String ordersCondition = showOnlyUnpaidItems ? " and pl.idoforder is null " : "";

            Query query = session.createSQLQuery(" select ctg.idofcontragent, ctg.contragentname, o.idoforg, "
                    + " o.shortnameinfoservice, o.address, c.contractid, pc.preorderdate, "
                    + " pc.complexname, count(distinct pl.idoforder) as amount, '' as \"Блюдо\", cast(pc.complexprice as bigint), co.createddate, "
                    + " case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as \"Сторнировано\", "
                    + " ord.orderdate, pl.qty*pl.price as ordersum, pl.idoforder, case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as \"Оплачено\" "
                    + " from cf_preorder_complex pc "
                    + " join cf_clients c on pc.idofclient = c.idofclient "
                    + " join cf_orgs o on c.idoforg = o.idoforg "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idofcontragent "
                    + " join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join cf_orders ord on ord.idoforg = pl.idoforg and ord.idoforder = pl.idoforder "
                    + " left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction "
                    + " left join cf_canceledorders co on ord.idoforder = co.idoforder and ord.idoforg = co.idoforg "
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1 "
                    + " and pc.preorderdate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + ordersCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17"
                    + " union"
                    + " select ctg.idofcontragent, ctg.contragentname, o.idoforg, o.shortnameinfoservice, o.address, c.contractid, pc.preorderdate, "
                    + " pc.complexname, count(distinct pl.idoforder) as amount, array_to_string(array_agg(pmd.menudetailname), ', ') as \"Блюдо\", "
                    + " cast(sum(pmd.menudetailprice) as bigint), co.createddate, "
                    + " case coalesce(ord.state, 0) when 0 then 'Нет' else 'Да' end as \"Сторнировано\", "
                    + " ord.orderdate, pl.qty*pl.price as ordersum, pl.idoforder,  case coalesce(ord.state, 1) when 1 then 'Нет' else 'Да' end as \"Оплачено\" "
                    + " from cf_preorder_menudetail pmd "
                    + " join cf_clients c on pmd.idofclient = c.idofclient "
                    + " join cf_orgs o on c.idoforg = o.idoforg  "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idofcontragent "
                    + " join cf_preorder_complex pc on pmd.idofpreordercomplex = pc.idofpreordercomplex "
                    + " join cf_preorder_linkod pl on pl.preorderguid = pc.guid "
                    + " left join cf_orders ord on ord.idoforg = pl.idoforg and ord.idoforder = pl.idoforder"
                    + " left join cf_transactions tr on ord.idoftransaction = tr.idoftransaction"
                    + " left join cf_canceledorders co on ord.idoforder = co.idoforder and ord.idoforg = co.idoforg"
                    + " where pc.deletedstate = 0 and pmd.deletedstate = 0 and pmd.amount > 0 and o.PreordersEnabled = 1"
                    + " and pmd.preorderdate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + ordersCondition
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17"
                    + " UNION"
                    + " select ctg.idofcontragent, ctg.contragentname, o.idoforg, "
                    + " o.shortnameinfoservice, o.address, c.contractid, pc.preorderdate,"
                    + " pc.complexname, (pc.amount - coalesce(q.payed, 0)) as amount, '' as \"Блюдо\", cast(pc.complexprice as bigint),"
                    + " cast(null as bigint) as createddate, 'Нет'  as \"Сторнировано\","
                    + " cast(null as bigint), cast(null as bigint) as ordersum, pl.idoforder, 'Нет' as \"Оплачено\""
                    + " from cf_preorder_complex pc"
                    + " join cf_clients c on pc.idofclient = c.idofclient "
                    + " join cf_orgs o on c.idoforg = o.idoforg "
                    + " join cf_contragents ctg on o.defaultsupplier = ctg.idofcontragent"
                    + " left outer join cf_preorder_linkod pl on pl.preorderguid = pc.guid"
                    + " left outer join (select preorderguid, count(idofpreorderlinkod) as payed from cf_preorder_linkod group by  preorderguid) q on q.preorderguid = pc.guid"
                    + " where pc.deletedstate = 0 and pc.amount > 0 and o.PreordersEnabled = 1"
                    + " and (pc.amount - coalesce(q.payed, 0)) != 0 "
                    + " and pc.preorderdate BETWEEN :startDate and :endDate "
                    + idOfOrgsCondition
                    + idOfContragentCondition
                    + " and pl.idoforder is null "
                    + " group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17"
                    + " order by 1, 6 desc, 2, 3, 4 ");
            query.setParameter("startDate", startTime.getTime())
                 .setParameter("endDate", endTime.getTime());

            if(contragent != null) {
                query.setParameter("idOfContragent", contragent.getIdOfContragent());
            }
            if(!CollectionUtils.isEmpty(idOfOrgList)){
                query.setParameterList("idOfOrgList", idOfOrgList);
            }

            List<Object[]> dataFromDB = query.list();
            if(dataFromDB != null){
                for(Object[] row : dataFromDB) {
                    Long idOfContragent = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                    String contragentName = (String) row[1];
                    Long idOfOrg =DataBaseSafeConverterUtils. getLongFromBigIntegerOrNull(row[2]);
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
                    ContragentPreordersReportItem item = new ContragentPreordersReportItem(
                            idOfContragent, contragentName, idOfOrg, orgShortName, orgShortAddress, clientContractId,
                            preorderDate, complexName, amount, dish, complexPrice, cancelDate,
                            reversed, createdDate, orderSum, idOfOrder, isPaid
                            );
                    result.add(item);
                }
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
